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

package com.liferay.lcs.service.impl;

import com.liferay.lcs.json.JSONWebServiceClient;
import com.liferay.lcs.messaging.Message;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.util.CompressionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.auth.PrincipalException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.CredentialException;

import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.UnmarshallException;

/**
 * @author Ivica Cardic
 */
public class LCSGatewayServiceImpl implements LCSGatewayService {

	public LCSGatewayServiceImpl() {
		try {
			_jsonSerializer.registerDefaultSerializers();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Message> getMessages(String key)
		throws PortalException, SystemException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("key", key);

		String json;
		try {
			json = _jsonWebServiceClient.doGet(WS_GET_MESSAGES_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		if (json == null || "{}".equals(json)) {
			_log.warn("Messages not received from osb-lcs-gateway.");

			return Collections.emptyList();
		}

		JSONDeserializer<List<String>> jsonDeserializer =
			_jsonFactory.createJSONDeserializer();

		List<String> jsonList;

		try {
			jsonList = jsonDeserializer.use(
				"values", String.class).deserialize(json);
		}catch (java.lang.ClassCastException e) {
			throw new JSONException(e);
		}

		List<Message> messages = new ArrayList<Message>();

		for (String jsonString : jsonList) {
			try {
				messages.add((Message) _jsonSerializer.fromJSON(jsonString));
			} catch (UnmarshallException e) {
				throw new JSONException(e);
			}
		}

		return messages;
	}

	public void sendMessage(Message message)
		throws PortalException, SystemException {

		Map<String, String> params = new HashMap<String, String>();

		String json;

		try {
			json = _jsonSerializer.toJSON(message);
		} catch (MarshallException e) {
			throw new JSONException(e);
		}

		try {
			params.put("json", CompressionUtil.compress(json));

			_jsonWebServiceClient.doPost(WS_SEND_MESSAGE_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}
	}

	public void setJsonFactory(JSONFactory jsonFactory) {
		this._jsonFactory = jsonFactory;
	}

	public void setJsonWebServiceClient(
		JSONWebServiceClient jsonWebServiceClient) {

		this._jsonWebServiceClient = jsonWebServiceClient;
	}

	private static final String LCS_GATEWAY_URL =
		"/osb-lcs-gateway-web/api/jsonws/lcsgateway";

	private static final String WS_GET_MESSAGES_URL =
		LCS_GATEWAY_URL + "/get-messages";

	private static final String WS_SEND_MESSAGE_URL =
		LCS_GATEWAY_URL + "/send-message";

	private static Log _log = LogFactoryUtil.getLog(LCSGatewayService.class);

	private JSONFactory _jsonFactory;

	private org.jabsorb.JSONSerializer _jsonSerializer =
		new org.jabsorb.JSONSerializer();
	private JSONWebServiceClient _jsonWebServiceClient;

}