package com.liferay.lcs.registration.portlet;

import com.liferay.lcs.model.LCSClusterEntry;
import com.liferay.lcs.service.LCSClusterEntryServiceUtil;
import com.liferay.lcs.service.LCSClusterNodeServiceUtil;
import com.liferay.lcs.service.LCSClusterUserServiceUtil;
import com.liferay.lcs.util.Constants;
import com.liferay.lcs.util.HandshakeManagerUtil;
import com.liferay.lcs.util.LCSClusterNodeUtil;
import com.liferay.lcs.util.OsbLcsPortletHelper;
import com.liferay.lcs.util.WebKeys;
import com.liferay.portal.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class RegistrationPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			WebKeys.PENDING, HandshakeManagerUtil.isPending());

		try {
			renderRequest.setAttribute(
				WebKeys.REGISTERED, LCSClusterNodeServiceUtil.isRegistered());
		}
		catch (Exception e) {
			if (e instanceof PrincipalException) {
				renderRequest.setAttribute(WebKeys.REGISTERED, Boolean.FALSE);
			}
			else {
				throw new PortletException(e);
			}
		}

		super.doView(renderRequest, renderResponse);
	}

	public void loginToLCS(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		String command = resourceRequest.getParameter(Constants.CMD);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		try {
			long remoteUserId = 0;

			long corpEntryId = ParamUtil.getLong(resourceRequest, "account", 0);

			if (command.equals("login")) {
				String userName = resourceRequest.getParameter("userName");

				String password = resourceRequest.getParameter("password");

				if (Validator.isNotNull(userName) &&
						 Validator.isNotNull(password)) {

					String portletId = (String)resourceRequest
						.getAttribute(WebKeys.PORTLET_ID);

					ServiceContext serviceContext =
						ServiceContextFactory.getInstance(resourceRequest);

					doConfigureJSONWSCredentials(userName, password, portletId);

					remoteUserId = LCSClusterUserServiceUtil.getUserId(
						userName);
				}
			} else if (command.equals("add")) {
				String description = ParamUtil.getString(
					resourceRequest, "description");

				String location = ParamUtil.getString(
					resourceRequest, "location");

				long lcsClusterEntryId = ParamUtil.getLong(
					resourceRequest, "lcsClusterEntry", 0);

				String lcsClusterEntryName = ParamUtil.getString(
					resourceRequest, "lcsClusterEntryName", null);

				LCSClusterNodeUtil.registerLCSClusterNode(
					description, location, corpEntryId,
					lcsClusterEntryId, lcsClusterEntryName);
			}

			if (remoteUserId != 0) {
				Map<String, String> corpEntries = LCSClusterEntryServiceUtil
					.getUserCorpEntries();

				JSONArray corpEntriesJSON = JSONFactoryUtil.createJSONArray();

				for (Map.Entry<String, String> corpEntry
						: corpEntries.entrySet()) {

					JSONObject corpEntryJSON =
						JSONFactoryUtil.createJSONObject();

					corpEntryJSON.put("corpEntryId", corpEntry.getKey());
					corpEntryJSON.put("name", corpEntry.getValue());

					corpEntriesJSON.put(corpEntryJSON);

					if (corpEntryId == 0) {
						corpEntryId = Long.parseLong(corpEntry.getKey());
					}
				}

				jsonObject.put("corpEntries", corpEntriesJSON);
			}

			if (corpEntryId != 0) {
				List<LCSClusterEntry> lcsClusterEntries =
					LCSClusterEntryServiceUtil
						.getCorpEntryLCSClusterEntries(corpEntryId);

				JSONArray lcsClusterEntriesArray =
					JSONFactoryUtil.createJSONArray();

				JSONObject lcsClusterEntryJSON =
					JSONFactoryUtil.createJSONObject();

				lcsClusterEntryJSON.put("", "");

				lcsClusterEntriesArray.put(lcsClusterEntryJSON);

				for (LCSClusterEntry lcsClusterEntry : lcsClusterEntries) {
					lcsClusterEntryJSON = JSONFactoryUtil.createJSONObject();

					if (lcsClusterEntry.getType() !=
						LCSClusterEntry.LCS_CLUSTER_ENTRY_TYPE_CLUSTER) {

						lcsClusterEntryJSON.put(
							"lcsClusterEntryId", lcsClusterEntry
							.getLcsClusterEntryId());
						lcsClusterEntryJSON.put(
							"name", lcsClusterEntry.getName());

						lcsClusterEntriesArray.put(lcsClusterEntryJSON);
					}
				}

				jsonObject.put("lcsClusterEntries", lcsClusterEntriesArray);
			}

			jsonObject.put("result", "success");
		} catch (Exception e) {
			_log.error(e);

			jsonObject.put("result", "failure");
		}

		writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	protected void doConfigureJSONWSCredentials(
			String userName, String password, String portletId)
		throws Exception {

		PortletPreferences systemPortletPreferences = null;

		try {
			systemPortletPreferences = PortletPreferencesLocalServiceUtil
				.getPortletPreferences(
					CompanyConstants.SYSTEM,
					PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0, portletId);
		}
		catch (NoSuchPortletPreferencesException e) {
			systemPortletPreferences = PortletPreferencesLocalServiceUtil
				.addPortletPreferences(
					CompanyConstants.SYSTEM, CompanyConstants.SYSTEM,
					PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0L, portletId, null,
					null);
		}

		javax.portlet.PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				CompanyConstants.SYSTEM, CompanyConstants.SYSTEM,
				PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0L, portletId,
				systemPortletPreferences.getPreferences());

		portletPreferences.setValue("lcsUserName", userName);
		portletPreferences.setValue("lcsPassword", password);

		portletPreferences.store();

		OsbLcsPortletHelper.setupCredentials();
	}

	private static Log _log = LogFactoryUtil.getLog(RegistrationPortlet.class);

}
