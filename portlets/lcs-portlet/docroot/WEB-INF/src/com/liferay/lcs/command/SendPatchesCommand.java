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
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.patcher.Patcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivica Cardic
 */
public class SendPatchesCommand extends AbstractCommand {
	public void execute(RequestCommandMessage requestCommandMessage) throws PortalException, SystemException {

		String[] installedPatches = _patcher.getInstalledPatches();

		String hashCode = null;

		if (requestCommandMessage.getPayload() != null) {
			hashCode = (String)requestCommandMessage.getPayload();
		}

		Map<String, Object> payload = new HashMap<String, Object>();

		if (_patcher.isConfigured()) {
			String installedHashCode = "0";

			if (installedPatches.length > 0) {
				Arrays.sort(installedPatches);

				StringBundler sb = new StringBundler(installedPatches.length);

				for (String patch : installedPatches) {
					sb.append(_digester.digestHex(Digester.MD5, patch));
				}

				installedHashCode = _digester.digestHex(
					Digester.MD5, sb.toString());
			}

			if (!installedHashCode.equals(hashCode)) {
				payload.put("hashCode", installedHashCode);

				List<String> list = new ArrayList<String>(
					Arrays.asList(installedPatches));
				payload.put("patches", list);

				sendResponse(payload, requestCommandMessage);
			}
		}else if (hashCode != null) {
			sendResponse(payload, requestCommandMessage);
		}
	}

	public void setDigester(Digester digester) {
		_digester = digester;
	}

	public void setPatcher(Patcher patcher) {
		_patcher = patcher;
	}

	private Digester _digester;
	private Patcher _patcher;
}