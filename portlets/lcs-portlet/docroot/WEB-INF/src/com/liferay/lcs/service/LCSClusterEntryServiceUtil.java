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
public class LCSClusterEntryServiceUtil {

	public static List<LCSClusterEntry> getCorpEntryLCSClusterEntries(
			long corpEntryId)
		throws PortalException, SystemException {

		return _lcsClusterEntryService.getCorpEntryLCSClusterEntries(
			corpEntryId);
	}

	public static Map<String, String> getUserCorpEntries()
		throws PortalException, SystemException {

		return _lcsClusterEntryService.getUserCorpEntries();
	}

	public static List<LCSClusterEntry> getUserLCSClusterEntries(long userId)
		throws PortalException, SystemException {

		return _lcsClusterEntryService.getUserLCSClusterEntries(userId);
	}

	public void setLCSClusterEntryService(
			LCSClusterEntryService lcsClusterEntryService) {

		_lcsClusterEntryService = lcsClusterEntryService;
	}

	private static LCSClusterEntryService _lcsClusterEntryService;
}