package com.liferay.lcs.service.test;

import com.liferay.lcs.model.LCSClusterNode;
import com.liferay.lcs.service.LCSClusterNodeService;
import com.liferay.lcs.util.KeyGenerator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ivica Cardic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {
	"classpath:META-INF/portlet-spring-test.xml"
})
public class LCSClusterNodeServiceTest {

	@Before
	public void before() {
		Mockito.when(_keyGenerator.getKey()).thenReturn("j4owe444irnm664");
	}

	@Test
	public void testAddLCSClusterNode() throws PortalException, SystemException {
		LCSClusterNode lcsClusterNode = _lcsClusterNodeService
			.addLCSClusterNode("test", "test", 1, 0, null);

		assertTrue(lcsClusterNode.getLcsClusterNodeId() != 0);
	}

	@Test
	public void testGetLCSClusterNode() throws PortalException, SystemException {
		LCSClusterNode lcsClusterNode =
			_lcsClusterNodeService.getLCSClusterNode();

		assertNotNull(lcsClusterNode);
	}

	@Autowired
	private KeyGenerator _keyGenerator; @Autowired
	private LCSClusterNodeService _lcsClusterNodeService;

}