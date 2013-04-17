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

package com.liferay.lcs.service.test;

import com.liferay.lcs.messaging.CommandMessage;
import com.liferay.lcs.messaging.Message;
import com.liferay.lcs.messaging.MetricsMessage;
import com.liferay.lcs.service.LCSGatewayService;
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
 * @author Igor Beslic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {
	"classpath:META-INF/portlet-spring-test.xml"
})
public class LCSGatewayServiceTest {

	@Test
	public void testGetMessages() throws PortalException, SystemException {
		List<Message> commandMessages = _lcsGatewayService.getMessages(_key);

		System.out.println(
			"Received: " + commandMessages.size() + " messages.");

		if (commandMessages.size() > 0) {
			for (Message m : commandMessages) {
				System.out.println("Received ".concat(m.toString()));
			}
		}

		assertTrue(commandMessages.size() >= 0);
	}

	@Test
	public void testSendMetricsMessage() throws PortalException, SystemException {
		_lcsGatewayService.sendMessage(new MetricsMessage());
	}

	@Test
	public void testWaitMessages() throws PortalException, SystemException {
		for (int idx = 0; idx < 2; idx++) {
			List<Message> commandMessages = _lcsGatewayService.getMessages(
				_key);

			System.out.println(
				"Received: " + commandMessages.size() + " messages.");

			if (commandMessages.size() > 0) {
				for (Message m : commandMessages) {
					if (m instanceof CommandMessage) {
						System.out.println(
							"Received command ".concat(m.toString()));
					}
					else {
						System.out.println("Received ".concat(m.toString()));
					}
				}
			}

			assertTrue(commandMessages.size() >= 0);

			try {
				Thread.sleep(63000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private String _key = "j4owe444irnm664";

	@Autowired
	private LCSGatewayService _lcsGatewayService;

}