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

import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.util.HandshakeManager;
import com.liferay.lcs.util.KeyGenerator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.monitoring.statistics.DataSample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivica Cardic
 */
public class MonitoringMessageListener implements MessageListener {

	public void receive(Message message) throws MessageListenerException {
		if (!_handshakeManager.isReady()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Waiting for handshakeManager to complete. " +
						"Reading messages delayed!");
			}

			return;
		}

		DataSample dataSample = (DataSample)message.getPayload();

		String target;
		if (dataSample.getNamespace().contains("Portal")) {
			target = MetricsMessage.METRICS_TYPE_PORTAL;
		}else if (dataSample.getNamespace().contains("Portlet")) {
			target = MetricsMessage.METRICS_TYPE_PORTLET;
		}else {
			target = MetricsMessage.METRICS_TYPE_SERVICE;
		}

		MetricsMessage metricsMessage = new MetricsMessage();

		// TODO change this to value assigned by CSEP ieg. have a factory or
		// build pattern

		metricsMessage.setKey(_keyGenerator.getKey());
		metricsMessage.setMetricsType(target);
		metricsMessage.setCreateTime(System.currentTimeMillis());

		Method[] methods = dataSample.getClass().getMethods();

		Map<String, Object> params = new HashMap<String, Object>();

		for (Method method : methods) {
			if (method.getName().startsWith("get") &&
					 !method.getName().equals("getClass")) {
				String field =
					method.getName().substring(3, method.getName().length());
				field = field.substring(0, 1).toLowerCase()
						.concat(field.substring(1, field.length()));

				Object value;

				try {
					value = method.invoke(dataSample);
				} catch (IllegalAccessException e) {
					throw new MessageListenerException(e);
				} catch (InvocationTargetException e) {
					throw new MessageListenerException(e);
				}

				if ((value != null) && !(value instanceof Number) &&
						 !(value instanceof String)) {
					value = value.toString();
				}

				if (value != null)
					params.put(field, value);
			}
		}

		metricsMessage.setPayload(params);

		try {
			_lcsGatewayService.sendMessage(metricsMessage);
		}catch (Exception e) {
			_log.error(e);
		}
	}

	public void setHandshakeService(HandshakeManager handshakeManager) {
		this._handshakeManager = handshakeManager;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this._keyGenerator = keyGenerator;
	}

	public void setLcsGatewayService(LCSGatewayService lcsGatewayService) {
		this._lcsGatewayService = lcsGatewayService;
	}

	private static Log _log = LogFactoryUtil.getLog(
		MonitoringMessageListener.class);

	private HandshakeManager _handshakeManager;

	private KeyGenerator _keyGenerator;
	private LCSGatewayService _lcsGatewayService;

}