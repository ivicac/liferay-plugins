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
import com.liferay.lcs.model.LCSClusterNode;
import com.liferay.lcs.service.LCSClusterNodeService;
import com.liferay.lcs.util.KeyGenerator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.PrincipalException;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.CredentialException;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LCSClusterNodeServiceImpl implements LCSClusterNodeService {

	public LCSClusterNode addLCSClusterNode(String siblingKey)
		throws PortalException, SystemException {

		String key = _keyGenerator.getKey();

		Map<String, String> params = new HashMap<String, String>();

		params.put("key", key);
		params.put("siblingKey", siblingKey);

		String json;
		try {
			json = _jsonWebServiceClient.doPost(
				WS_ADD_LCS_CLUSTER_NODE_URL, params);
		}
		catch (CredentialException e) {
			throw  new PrincipalException(e);
		}
		catch (IOException e) {
			throw  new SystemException(e);
		}

		return _jsonFactory.looseDeserialize(json, LCSClusterNode.class);
	}
	
	public LCSClusterNode addLCSClusterNode(
			String description, String location, long corpEntryId,
			long lcsClusterEntryId, String lcsClusterEntryName)
		throws PortalException, SystemException {

		String key = _keyGenerator.getKey();

		if (Validator.isNull(description)) {
			description = "null";
		}

		if (Validator.isNull(location)) {
			location = "null";
		}

		Map<String, String> params = new HashMap<String, String>();

		params.put("corpEntryId", String.valueOf(corpEntryId));
		params.put("buildNumber", String.valueOf(ReleaseInfo.getBuildNumber()));
		params.put("description", description);
		params.put("key", key);
		params.put("location", location);

		if (lcsClusterEntryId != 0) {
			params.put("lcsClusterEntryId", String.valueOf(lcsClusterEntryId));
		}
		else if (Validator.isNotNull(lcsClusterEntryName)) {
			params.put("lcsClusterEntryName", lcsClusterEntryName);
		}
		else {
			params.put("lcsClusterEntryName", null);
		}

		String json;
		try {
			json = _jsonWebServiceClient.doPost(
				WS_ADD_LCS_CLUSTER_NODE_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		return _jsonFactory.looseDeserialize(json, LCSClusterNode.class);
	}

	public LCSClusterNode getLCSClusterNode()
		throws PortalException, SystemException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("key", _keyGenerator.getKey());

		String json;

		try {
			json = _jsonWebServiceClient.doGet(WS_GET_SERVER_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		LCSClusterNode lcsClusterNode = _jsonFactory.looseDeserialize(
			json, LCSClusterNode.class);

		if (lcsClusterNode.getLcsClusterNodeId() == 0) {
			return null;
		}

		return lcsClusterNode;

	}

	public boolean isRegistered() throws PortalException, SystemException {
		return getLCSClusterNode() != null;
	}

	public void setJsonFactory(JSONFactory jsonFactory) {
		this._jsonFactory = jsonFactory;
	}

	public void setJsonWebServiceClient(
		JSONWebServiceClient jsonWebServiceClient) {

		this._jsonWebServiceClient = jsonWebServiceClient;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this._keyGenerator = keyGenerator;
	}

	private static final String LCS_CLUSTER_NODE_URL =
		"/api/secure/jsonws/osb-lcs-portlet.lcsclusternode";

	private static final String WS_ADD_LCS_CLUSTER_NODE_URL =
		LCS_CLUSTER_NODE_URL + "/add-lcs-cluster-node";

	private static final String WS_GET_SERVER_URL =
		LCS_CLUSTER_NODE_URL + "/get-lcs-cluster-node";

	private JSONFactory _jsonFactory;

	private JSONWebServiceClient _jsonWebServiceClient;

	private KeyGenerator _keyGenerator;

}