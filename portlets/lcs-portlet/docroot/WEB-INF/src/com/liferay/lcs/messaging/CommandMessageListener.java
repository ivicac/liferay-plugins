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

package com.liferay.lcs.messaging;

import com.liferay.lcs.command.Command;
import com.liferay.lcs.security.DigitalSignature;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;

import java.util.Map;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class CommandMessageListener implements MessageListener {
	public void receive(Message message) throws MessageListenerException {
		RequestCommandMessage commandMessage = (RequestCommandMessage)message.getPayload();

		String exception = null;

		if (_digitalSignature.verifyMessage(commandMessage)) {
			try {
				_commands.get(
					commandMessage.getCommandType()).execute(commandMessage);
			} catch (Exception e) {
				exception = e.getMessage();
			}
		} else {
			_log.warn("Received ".concat(commandMessage.getCommandType())
				.concat(" command has invalid digital signature. ")
				.concat("Please check previous ERRORS for details."));

			exception = "Failed to verify digital signature.";
		}

		if (exception != null) {
			ResponseCommandMessage responseMessage =
				new ResponseCommandMessage();

			responseMessage.setCorrelationId(commandMessage.getCorrelationId());
			responseMessage.setCreateTime(System.currentTimeMillis());
			responseMessage.setKey(commandMessage.getKey());
			responseMessage.setCommandType(commandMessage.getCommandType());

			responseMessage.put(
				com.liferay.lcs.messaging.Message.KEY_EXCEPTION, exception);

			try {
				_lcsGatewayService.sendMessage(responseMessage);
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}

	public void setCommands(Map<String, Command> commands) {
		this._commands = commands;
	}

	public void setDigitalSignature(DigitalSignature digitalSignatureSrv) {
		this._digitalSignature = digitalSignatureSrv;
	}

	public void setLcsGatewayService(LCSGatewayService lcsGatewayService) {
		this._lcsGatewayService = lcsGatewayService;
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommandMessageListener.class);

	private Map<String, Command> _commands;

	private DigitalSignature _digitalSignature;

	private LCSGatewayService _lcsGatewayService;

}