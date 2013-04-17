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

import com.liferay.lcs.model.LCSClusterEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.List;
import java.util.Map;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public interface LCSClusterEntryService {
	public List<LCSClusterEntry> getCorpEntryLCSClusterEntries(long corpEntryId)
		throws PortalException, SystemException;

	public Map<String, String> getUserCorpEntries()
		throws PortalException, SystemException;

	public List<LCSClusterEntry> getUserLCSClusterEntries(long userId)
		throws PortalException, SystemException;
}