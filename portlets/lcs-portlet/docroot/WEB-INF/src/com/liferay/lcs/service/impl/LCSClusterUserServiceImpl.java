package com.liferay.lcs.service.impl;

import com.liferay.lcs.json.JSONWebServiceClient;
import com.liferay.lcs.service.LCSClusterUserService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.auth.PrincipalException;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.CredentialException;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LCSClusterUserServiceImpl implements LCSClusterUserService {

	public long getUserId(String email)
		throws PortalException, SystemException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);

		String json;
		try {
			json = _jsonWebServiceClient.doGet(GET_USER_ID_URL, params);
		}
		catch (CredentialException e) {
			throw new PrincipalException(e);
		}
		catch (IOException e) {
			throw new SystemException(e);
		}

		if ("{}".equals(json)) {
			return 0;
		}

		return Long.parseLong(json);
	}

	public void setJsonWebServiceClient(
		JSONWebServiceClient jsonWebServiceClient) {

		_jsonWebServiceClient = jsonWebServiceClient;
	}

	private static final String GET_USER_ID_URL =
		"/api/secure/jsonws/osb-lcs-portlet.lcsclusteruser/" +
			"get-user-id";

	private JSONWebServiceClient _jsonWebServiceClient;
}