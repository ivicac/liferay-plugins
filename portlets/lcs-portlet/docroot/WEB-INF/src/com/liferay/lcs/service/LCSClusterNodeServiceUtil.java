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

package com.liferay.lcs.service;

import com.liferay.lcs.model.LCSClusterNode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Ivica Cardic
 */
public class LCSClusterNodeServiceUtil {

	public static LCSClusterNode addLCSClusterNode(String siblingKey)
		throws SystemException, PortalException {

		return _lcsClusterNodeService.addLCSClusterNode(siblingKey);
	}
	
	public static LCSClusterNode addLCSClusterNode(
		String description, String location, long corpEntryId,
		Long lcsClusterEntryId, String lcsClusterEntryName) throws PortalException, SystemException {

		return _lcsClusterNodeService.addLCSClusterNode(
			description, location, corpEntryId, lcsClusterEntryId,
			lcsClusterEntryName);
	}

	public static boolean isRegistered() throws PortalException, SystemException {
		return _lcsClusterNodeService.isRegistered();
	}

	public void setLCSClusterNodeService(
		LCSClusterNodeService lcsClusterNodeService) {

		_lcsClusterNodeService = lcsClusterNodeService;
	}

	private static LCSClusterNodeService _lcsClusterNodeService;

}