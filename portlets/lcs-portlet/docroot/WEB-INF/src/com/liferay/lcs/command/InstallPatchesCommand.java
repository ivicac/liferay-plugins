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
import com.liferay.lcs.util.LCSConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.patcher.Patcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.util.Map;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class InstallPatchesCommand extends AbstractCommand {

	public void execute(RequestCommandMessage requestCommandMessage)
		throws PortalException, SystemException {

		sendResponse(
			LCSConstants.PATCHER_DOWNLOADING_PATCHES, requestCommandMessage);

		Map<String, String> patches =
			(Map<String, String>)requestCommandMessage.getPayload();

		try {
			for (String patchName : patches.keySet()) {
				String urlString = patches.get(patchName);

				URL url = new URL(urlString);

				InputStream reader = new BufferedInputStream(url.openStream());

				File file = new File(
					_patcher.getPatchDirectory().getAbsolutePath() +
						File.separator + patchName);

				OutputStream writer;

				writer = new BufferedOutputStream(new FileOutputStream(file));

				int read;

				while ((read = reader.read()) != -1) {
					writer.write(read);
				}

				writer.flush();
				writer.close();
				reader.close();
			}

			_log.info("The following patches are downloaded: " +
					StringUtil.merge(patches.keySet()));

			sendResponse(
				LCSConstants.PATCHER_PATCHES_DOWNLOADED, requestCommandMessage);
		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	public void setPatcher(Patcher patcher) {
		this._patcher = patcher;
	}

	private static Log _log = LogFactoryUtil.getLog(
		InstallPatchesCommand.class);

	private Patcher _patcher;

}