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

package com.liferay.lcs.command;

import com.liferay.lcs.messaging.Message;
import com.liferay.lcs.messaging.RequestCommandMessage;
import com.liferay.lcs.messaging.ResponseCommandMessage;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author IvicaCardic
 */
public abstract class AbstractCommand implements Command {
	public void sendResponse(
			Object payload, Exception exception,
			RequestCommandMessage requestCommandMessage)
		throws PortalException, SystemException {

		ResponseCommandMessage responseMessage = new ResponseCommandMessage();

		responseMessage.setCorrelationId(
			requestCommandMessage.getCorrelationId());
		responseMessage.setCreateTime(System.currentTimeMillis());
		responseMessage.setKey(requestCommandMessage.getKey());
		responseMessage.setCommandType(requestCommandMessage.getCommandType());

		if (null != payload) {
			responseMessage.setPayload(payload);
		}

		if (null != exception) {
			responseMessage.put(Message.KEY_EXCEPTION, exception.getMessage());
		}

		_lcsGatewayService.sendMessage(responseMessage);
	}

	public void sendResponse(
			Object payload, RequestCommandMessage requestCommandMessage)
		throws PortalException, SystemException {

		sendResponse(payload, null, requestCommandMessage);
	}

	public void setLcsGatewayService(LCSGatewayService lcsGatewayService) {
		this._lcsGatewayService = lcsGatewayService;
	}

	private LCSGatewayService _lcsGatewayService;

}