/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.lcs.command;

import com.liferay.lcs.messaging.RequestCommandMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncPrintWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scripting.Scripting;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivica Cardic
 */
public class ExecuteScriptCommand extends AbstractCommand {
	public void execute(RequestCommandMessage requestCommandMessage)
		throws PortalException, SystemException {

		Map<String, Object> portletObjects = new HashMap<String, Object>();

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		UnsyncPrintWriter unsyncPrintWriter = UnsyncPrintWriterPool.borrow(
			unsyncByteArrayOutputStream);

		portletObjects.put("out", unsyncPrintWriter);

		String script = (String)requestCommandMessage.getPayload();

		if (_log.isDebugEnabled()) {
			_log.debug("Executing script: " + script);
		}

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("command", script);

		Exception exception = null;
		try {
			_scripting.exec(null, portletObjects, "groovy", script);

			unsyncPrintWriter.flush();

			map.put("result", unsyncByteArrayOutputStream.toString());
		}catch (ScriptingException e) {
			exception = e;
		}

		sendResponse(map, exception, requestCommandMessage);
	}

	public void setScripting(Scripting scripting) {
		this._scripting = scripting;
	}

	private static Log _log = LogFactoryUtil.getLog(ExecuteScriptCommand.class);


	private Scripting _scripting;
}