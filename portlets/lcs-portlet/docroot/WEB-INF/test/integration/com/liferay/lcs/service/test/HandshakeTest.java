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
import com.liferay.lcs.messaging.ResponseCommandMessage;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.util.HandshakeManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.sender.SingleDestinationMessageSender;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * @author Igor Beslic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {
	"classpath:META-INF/portlet-spring.xml",
	"classpath:META-INF/portlet-spring-test.xml"
})
public class HandshakeTest {
	public void before() throws PortalException, SystemException {
		ResponseCommandMessage responseCommandMessage =
			new ResponseCommandMessage();
		responseCommandMessage.setCommandType(CommandMessage
			.COMMAND_TYPE_INITIATE_HANDSHAKE);

		_messages.add(responseCommandMessage);

		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();

				return "called with arguments: " + args;
			}
		}).when(_messageSender).send(any());

		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();

				return "called with arguments: " + args;
			}
		}).when(_lcsGatewayService).sendMessage(any(Message.class));

		when(
			_lcsGatewayService.getMessages(
				SERVER_ID_DEFAULT)).thenReturn(_messages);
	}

	@Test
	public void testHandshake() throws PortalException {
		_handshakeManager.start();
	}

	@Test
	public void testHandshakeError() throws PortalException {
		_handshakeManager.start();

		// here we expect error

		try {
			_handshakeManager.start();

			fail("Gateway shouldn't be able to handshake twice in a row!");
		}
		catch (RuntimeException re) {
			System.out.println(re.getMessage());
			assertTrue(re.getMessage().startsWith("0001"));
		}
	}

	private static final String SERVER_ID_DEFAULT = "LCS-SRV-01";

	@Autowired
	private HandshakeManager _handshakeManager;

	@Autowired
	private LCSGatewayService _lcsGatewayService;

	private List<Message> _messages = new ArrayList<Message>();

	@Autowired
	@Qualifier("messageSender.commands")
	private SingleDestinationMessageSender _messageSender;

}