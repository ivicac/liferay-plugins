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

package com.liferay.lcs.task;

import com.liferay.lcs.messaging.Message;
import com.liferay.lcs.messaging.RequestCommandMessage;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.util.HandshakeManager;
import com.liferay.lcs.util.KeyGenerator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.sender.SingleDestinationMessageSender;

import java.util.List;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class CommandMessageTask implements Runnable {

	public void run() {
		try {
			if (!_handshakeManager.isReady()) {
				if (_log.isInfoEnabled()) {
					_log.info("Waiting for handshakeManager to complete. " +
						"Reading messages delayed!");
				}

				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(_keyGenerator.getKey().concat(
					" checking messages..."));
			}

			List<Message> messages = _lcsGatewayService.getMessages(
				_keyGenerator.getKey());

			for (Message message : messages) {
				if (message instanceof RequestCommandMessage) {
					_messageSenderCommands.send(message);
				} else {
					_log.error("Unknown message: " + message);
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public void setHandshakeManager(HandshakeManager handshakeManager) {
		this._handshakeManager = handshakeManager;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this._keyGenerator = keyGenerator;
	}

	public void setLcsGatewayService(LCSGatewayService lcsGatewayService) {
		this._lcsGatewayService = lcsGatewayService;
	}

	public void setMessageSenderCommands(
		SingleDestinationMessageSender _messageSenderCommands) {

		this._messageSenderCommands = _messageSenderCommands;
	}

	private static Log _log = LogFactoryUtil.getLog(CommandMessageTask.class);

	private HandshakeManager _handshakeManager;

	private KeyGenerator _keyGenerator;
	private LCSGatewayService _lcsGatewayService;

	private SingleDestinationMessageSender _messageSenderCommands;

}