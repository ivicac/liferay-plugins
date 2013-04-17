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
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author Ivica Cardic
 */
public class SendPortalPropertiesCommand extends AbstractCommand {

	public void execute(RequestCommandMessage requestCommandMessage)
		throws PortalException, SystemException {

		Properties portalProperties = _props.getProperties();

		String hashCode = null;

		if (requestCommandMessage.getPayload() != null) {
			hashCode = (String)requestCommandMessage.getPayload();
		}

		TreeMap<String, String> treeMap = new TreeMap(portalProperties);

		StringBundler sb = new StringBundler(treeMap.size());

		for (String key : treeMap.keySet()) {
			sb.append(_digester.digestHex(Digester.MD5, treeMap.get(key)));
		}

		String installedHashCode = _digester.digestHex(
			Digester.MD5, sb.toString());

		if (!installedHashCode.equals(hashCode)) {
			Map<String, String> properties = new HashMap<String, String>();

			int i = 0;
			for (Object key : portalProperties.keySet()) {
				properties.put((String)key, portalProperties.getProperty(
					(String)key));

				if ((i % 50 == 0) || (i == portalProperties.size() - 1)) {

					Map<String, Object> payload = new HashMap<String, Object>();

					payload.put("hashCode", installedHashCode);
					payload.put("properties", properties);

					sendResponse(payload, requestCommandMessage);

					properties = new HashMap<String, String>();
				}

				i++;
			}
		}
	}

	public void setDigester(Digester digester) {
		_digester = digester;
	}

	public void setProps(Props _props) {
		this._props = _props;
	}

	private Digester _digester;
	private Props _props;
}