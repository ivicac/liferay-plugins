package com.liferay.lcs.service.test;

import com.liferay.lcs.model.LCSClusterEntry;
import com.liferay.lcs.service.LCSClusterEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author Ivica Cardic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {
	"classpath:META-INF/portlet-spring-test.xml"
})
public class LCSClusterEntryServiceTest {

	@Test
	public void testGetCorpEntryLCSClusterEntries() throws PortalException, SystemException {
		List<LCSClusterEntry> lcsClusterEntries =
			_lcsClusterEntryService.getCorpEntryLCSClusterEntries(1);

		assertTrue(lcsClusterEntries.size() >= 0);
	}

	@Autowired
	private LCSClusterEntryService _lcsClusterEntryService;
}