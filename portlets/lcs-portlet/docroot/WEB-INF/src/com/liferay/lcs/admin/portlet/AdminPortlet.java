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

package com.liferay.lcs.admin.portlet;

import com.liferay.lcs.util.HandshakeManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * @author Ivica Cardic
 */
public class AdminPortlet extends MVCPortlet {

	public void start(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		try {
			HandshakeManagerUtil.start();
		} catch (PortalException e) {
			throw new PortletException(e);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	public void stop(ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		HandshakeManagerUtil.stop();

		sendRedirect(actionRequest, actionResponse);
	}

}