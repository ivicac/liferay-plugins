package com.liferay.lcs.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public interface LCSClusterUserService {
	public long getUserId(String email) throws PortalException, SystemException;
}