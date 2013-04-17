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

import com.liferay.lcs.messaging.HeartbeatMessage;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.util.HandshakeManager;
import com.liferay.lcs.util.KeyGenerator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Igor Beslic
 */
public class HeartbeatTask implements Runnable {

	public void run() {
		try {
			if (!_handshakeManager.isReady()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Waiting for handshakeManager to complete. " +
						"Heart beat delayed!");
				}

				return;
			}

			HeartbeatMessage heartbeatMessage = new HeartbeatMessage();
			heartbeatMessage.setCreateTime(System.currentTimeMillis());

			// TODO change this to value assigned by LACS ieg. have a factory or
			// build pattern

			heartbeatMessage.setKey(_keyGenerator.getKey());

			if (_log.isDebugEnabled()) {
				_log.debug("Sending ".concat(heartbeatMessage.toString()));
			}

			_lcsGatewayService.sendMessage(heartbeatMessage);
		}catch (Exception e) {
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

	private HandshakeManager _handshakeManager;

	private KeyGenerator _keyGenerator;
	private LCSGatewayService _lcsGatewayService;

	private Log _log = LogFactoryUtil.getLog(HeartbeatTask.class);

}