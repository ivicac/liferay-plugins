package com.liferay.lcs.util;

import com.liferay.lcs.json.JSONWebServiceClient;
import com.liferay.portal.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

import javax.portlet.PortletPreferences;

/**
 * @author Igor Beslic
 * @author Ivica Cardic
 */
public class OsbLcsPortletHelper {

	public static void setupCredentials()
		throws PortalException, SystemException {

		com.liferay.portal.model.PortletPreferences portletPreferences =
			PortletPreferencesLocalServiceUtil.getPortletPreferences(
				CompanyConstants.SYSTEM,
				PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0, _portletId);

		PortletPreferences pp = PortletPreferencesFactoryUtil
			.fromDefaultXML(portletPreferences.getPreferences());

		String lcsUserName = pp.getValue("lcsUserName", null);

		String lcsPassword = pp.getValue("lcsPassword", null);

		if (Validator.isNull(lcsUserName) ||
			Validator.isNull(lcsPassword)) {

			throw new SystemException("Failed to setup LCS Credentials...");
		}

		_jsonWebServiceClient.setPassword(lcsPassword);

		_jsonWebServiceClient.setUserName(lcsUserName);
	}

	public void setJsonWebServiceClient(
		JSONWebServiceClient jsonWebServiceClient) {

		_jsonWebServiceClient = jsonWebServiceClient;
	}

	private static final String _portletId =
		"RegistrationPortlet_WAR_lcsportlet";

	private static JSONWebServiceClient _jsonWebServiceClient;

}