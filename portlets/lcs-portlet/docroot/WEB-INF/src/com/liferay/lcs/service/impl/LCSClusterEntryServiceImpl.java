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
import com.liferay.lcs.model.LCSClusterEntry;
import com.liferay.lcs.service.LCSClusterEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.security.auth.PrincipalException;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.CredentialException;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LCSClusterEntryServiceImpl implements LCSClusterEntryService {

	public List<LCSClusterEntry> getCorpEntryLCSClusterEntries(long corpEntryId)
		throws PortalException, SystemException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("corpEntryId", String.valueOf(corpEntryId));

		String json;
		try {
			json = _jsonWebServiceClient.doGet(
				GET_CORP_ENTRY_LCS_CLUSTER_ENTRIES_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		JSONDeserializer<List<LCSClusterEntry>> jsonDeserializer =
			_jsonFactory.createJSONDeserializer();

		return jsonDeserializer
			.use("values", LCSClusterEntry.class).deserialize(json);
	}

	public Map<String, String> getUserCorpEntries()
		throws PortalException, SystemException {

		String json;

		try {
			json = _jsonWebServiceClient.doGet(
				GET_USER_CORP_ENTRIES_URL,
				Collections.<String, String>emptyMap());
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		JSONDeserializer<Map<String, String>> jsonDeserializer =
			_jsonFactory.createJSONDeserializer();

		return jsonDeserializer.deserialize(json);
	}

	public List<LCSClusterEntry> getUserLCSClusterEntries(long userId)
		throws PortalException, SystemException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(userId));

		String json;
		try {
			json = _jsonWebServiceClient.doGet(
				GET_USER_LCS_CLUSTER_ENTRIES_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		JSONDeserializer<List<LCSClusterEntry>> jsonDeserializer =
			_jsonFactory.createJSONDeserializer();

		return jsonDeserializer
			.use("values", LCSClusterEntry.class).deserialize(json);
	}

	public void setJsonFactory(JSONFactory jsonFactory) {
		this._jsonFactory = jsonFactory;
	}

	public void setJsonWebServiceClient(
		JSONWebServiceClient jsonWebServiceClient) {

		this._jsonWebServiceClient = jsonWebServiceClient;
	}

	private static final String GET_CORP_ENTRY_LCS_CLUSTER_ENTRIES_URL =
		"/api/secure/jsonws/osb-lcs-portlet.lcsclusterentry/" +
			"get-corp-entry-lcs-cluster-entries";

	private static final String GET_USER_CORP_ENTRIES_URL =
		"/api/secure/jsonws/osb-lcs-portlet.lcsclusterentry/" +
			"get-user-corp-entries";

	private static final String GET_USER_LCS_CLUSTER_ENTRIES_URL =
		"/api/secure/jsonws/osb-lcs-portlet.lcsclusterentry/" +
			"get-user-lcs-cluster-entries";

	private JSONFactory _jsonFactory;
	private JSONWebServiceClient _jsonWebServiceClient;

}