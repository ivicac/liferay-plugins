package com.liferay.lcs.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LCSClusterUserServiceUtil {

	public static long getUserId(String email)
		throws PortalException, SystemException {

		return _lcsClusterUserService.getUserId(email);
	}

	public void setLCSClusterUserService(
		LCSClusterUserService lcsClusterUserService) {

		_lcsClusterUserService = lcsClusterUserService;
	}

	private static LCSClusterUserService _lcsClusterUserService;

}