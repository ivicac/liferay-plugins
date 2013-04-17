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

package com.liferay.lcs.util;

import com.liferay.lcs.messaging.CommandMessage;
import com.liferay.lcs.messaging.HandshakeMessage;
import com.liferay.lcs.messaging.Message;
import com.liferay.lcs.messaging.ResponseCommandMessage;
import com.liferay.lcs.service.LCSClusterNodeService;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.task.CommandMessageTask;
import com.liferay.lcs.task.HeartbeatTask;
import com.liferay.lcs.task.JVMMetricsTask;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.sender.SingleDestinationMessageSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor Beslic
 * @author Ivica Cardic
 */
public class HandshakeManagerImpl implements HandshakeManager {

	public void destroy(){
		Future<?> futureStop = stop();

		try {
			futureStop.get();
		}
		catch (InterruptedException e) {}
		catch (ExecutionException e) {}

		_executorService.shutdown();

		try {
			if (!_executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				_executorService.shutdownNow();
			}
		} catch (final InterruptedException ie) {
			_executorService.shutdownNow();
		}
	}

	public synchronized boolean isPending() {
		return _pending;
	}

	synchronized public boolean isReady() {
		return _ready;
	}

	public void setHandshakeReplyReads(int handshakeReplyReads) {
		this._handshakeReplyReads = handshakeReplyReads;
	}

	public void setHandshakeWaitTime(long handshakeWaitTime) {
		this._handshakeWaitTime = handshakeWaitTime;
	}

	public void setHeartbeatInterval(long heartbeatInterval) {
		this._heartbeatInterval = heartbeatInterval;
	}

	public void setHeartbeatTask(HeartbeatTask heartbeatTask) {
		this._heartbeatTask = heartbeatTask;
	}

	public void setJvmMetricsTask(JVMMetricsTask jvmMetricsTask) {
		this._jvmMetricsTask = jvmMetricsTask;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this._keyGenerator = keyGenerator;
	}

	public void setLcsClusterNodeService(
		LCSClusterNodeService lcsClusterNodeService) {
		this._lcsClusterNodeService = lcsClusterNodeService;
	}

	public void setLcsGatewayService(LCSGatewayService lcsGatewayService) {
		this._lcsGatewayService = lcsGatewayService;
	}

	public void setMessageSenderCommands(
		SingleDestinationMessageSender _messageSenderCommands) {

		this._messageSenderCommands = _messageSenderCommands;
	}

	public void setMessageTask(CommandMessageTask messageTask) {
		this._messageTask = messageTask;
	}

	public void setStopReplyReads(int stopReplyReads) {
		_stopReplyReads = stopReplyReads;
	}

	public void setStopWaitTime(long stopWaitTime) {
		_stopWaitTime = stopWaitTime;
	}

	synchronized private void setPending(boolean pending) {
		this._pending = pending;
	}

	synchronized private void setReady(boolean ready) {
		this._ready = ready;
	}

	public Future<?> start() throws PortalException {
		if (!isReady() && !isPending()) {
			setPending(true);

			HandshakeRunnable handshakeRunnable = new HandshakeRunnable();

			Future<?> future = _executorService.submit(handshakeRunnable);

			return future;
		}

		return null;
	}

	public Future<?> stop() {
		if (isReady()) {
			setReady(false);
			setPending(true);

			SignoffRunnable signoffRunnable = new SignoffRunnable();

			Future<?> future = _executorService.submit(signoffRunnable);

			return future;
		}

		return null;
	}

	private boolean processResponse(
		List<Message> messages, List<Message> others){

		boolean handshakeRespondPresent = false;

		for (Message m : messages) {
			if (m instanceof ResponseCommandMessage) {
				ResponseCommandMessage rm = (ResponseCommandMessage)m;

				if (CommandMessage
					.COMMAND_TYPE_INITIATE_HANDSHAKE.equals(
						rm.getCommandType())) {

					if ((System.currentTimeMillis() - rm.getCreateTime()) >
						_handshakeWaitTime) {

						_log.warn("Handshake received too late, " +
							"but it was accepted as valid...");
					}

					if (rm.contains("error")) {
						throw new RuntimeException((String) rm.get("error"));
					}

					handshakeRespondPresent = true;
				}
			} else {
				others.add(m);
			}
		}

		return handshakeRespondPresent;
	}

	private static Log _log = LogFactoryUtil.getLog(HandshakeManagerImpl.class);

	private int _handshakeReplyReads;
	private long _handshakeWaitTime;
	private long _heartbeatInterval;
	private HeartbeatTask _heartbeatTask;

	private JVMMetricsTask _jvmMetricsTask;

	private KeyGenerator _keyGenerator;

	private LCSClusterNodeService _lcsClusterNodeService;

	private LCSGatewayService _lcsGatewayService;

	private SingleDestinationMessageSender _messageSenderCommands;

	private CommandMessageTask _messageTask;

	private boolean _pending;

	private boolean _ready;

	private ScheduledExecutorService _executorService =
		Executors.newScheduledThreadPool(6);

	private List<ScheduledFuture> _scheduledFutures =
		new ArrayList<ScheduledFuture>();

	private int _stopReplyReads;
	private long _stopWaitTime;

	private class HandshakeRunnable implements Runnable {
		public void run() {
			try {
				if (!_lcsClusterNodeService.isRegistered()) {
					boolean registered = LCSClusterNodeUtil
						.registerLCSClusterNode();

					if (!registered){
						_log.warn("This node is not registered with LCS.");

						setPending(false);

						return;
					}
				}

				if (_log.isInfoEnabled()) {
					_log.info("LCS handshake started.");
				}

				HandshakeMessage handshakeMessage = new HandshakeMessage();
				handshakeMessage.setKey(_keyGenerator.getKey());

				handshakeMessage.put(
					Message.KEY_HEARTBEAT_INTERVAL,
					String.valueOf(_heartbeatInterval));

				_lcsGatewayService.sendMessage(handshakeMessage);

				long waitTime = _handshakeWaitTime
					/ _handshakeReplyReads;
				int attempt = 0;

				List<Message> receivedMessages;

				List<Message> delayedMessages = new ArrayList<Message>();

				while (true) {
					if (attempt++ > _handshakeReplyReads) {
						_log.error(
							"Didn't receive response to handshake within "
								.concat(String.valueOf(
									_handshakeReplyReads))
								.concat(" attempts."));

						_log.error("LCS Portlet "
							.concat(" couldn't establish connection with LCS.")
							.concat(" Please contact person")
							.concat(" responsible for maintenance."));

						setPending(false);

						return;
					}

					receivedMessages = _lcsGatewayService.getMessages(
						_keyGenerator.getKey());

					/**
					 * If there is no response then check inbox couple more
					 * times since handshake confirmation can be late.
					 */
					if (receivedMessages.size() == 0) {
						try {
							TimeUnit.MILLISECONDS.sleep(waitTime);
						} catch (InterruptedException e) {}
					}
					else {
						if (processResponse(
							receivedMessages, delayedMessages)) {

							break;
						}
					}
				}

				Collections.sort(
					delayedMessages, new MessagePriorityComparator());

				//check for command messages and send them to the message bus

				for (Message message : delayedMessages) {
					if (message instanceof CommandMessage) {
						_messageSenderCommands.send(message);
					} else {

						// TODO do something with this response (this can beold unread message from inbox)

						if (_log.isWarnEnabled()) {
							_log.error(
								"There is no handlers for the message: "
									.concat(message.toString()));
						}
					}
				}

				_scheduledFutures.add(
					_executorService.scheduleAtFixedRate(
						_jvmMetricsTask, 60, 60, TimeUnit.SECONDS));

				_scheduledFutures.add(
					_executorService.scheduleAtFixedRate(
						_messageTask, 10, 10, TimeUnit.SECONDS));

				_scheduledFutures.add(
					_executorService.scheduleAtFixedRate(
						_heartbeatTask, _heartbeatInterval, _heartbeatInterval,
						TimeUnit.MILLISECONDS));

				setReady(true);
				setPending(false);

				if (_log.isInfoEnabled()) {
					_log.info("Connection with LCS established.");
				}
			} catch (Exception e) {
				setPending(false);
				setReady(false);

				_log.error(e);
			}
		}
	}

	private class SignoffRunnable implements Runnable {
		public void run() {
			for (ScheduledFuture scheduledFuture : _scheduledFutures) {
				while (!scheduledFuture.isCancelled()) {
					scheduledFuture.cancel(true);
				}
			}

			_scheduledFutures.clear();

			try {
				if (_log.isInfoEnabled()) {
					_log.info("LCS Sign off started.");
				}

				HandshakeMessage handshakeMessage = new HandshakeMessage();
				handshakeMessage.setKey(_keyGenerator.getKey());

				handshakeMessage.put(
					Message.KEY_SIGN_OFF, String.valueOf(_heartbeatInterval));

				_lcsGatewayService.sendMessage(handshakeMessage);

				long waitTime = _stopWaitTime / _stopReplyReads;
				int attempt = 0;

				List<Message> receivedMessages;

				List<Message> delayedMessages = new ArrayList<Message>();

				while (true) {
					if (attempt++ > _handshakeReplyReads) {
						setPending(false);

						if (_log.isInfoEnabled()) {
							_log.info("Connection with LCS ended.");
						}

						return;
					}

					receivedMessages = _lcsGatewayService.getMessages(
						_keyGenerator.getKey());

					/**
					 * If there is no response then check inbox couple more
					 * times since handshake confirmation can be late.
					 */
					if (receivedMessages.size() == 0) {
						try {

							TimeUnit.MILLISECONDS.sleep(waitTime);
						} catch (InterruptedException e) {}
					}
					else {
						if (processResponse(
							receivedMessages, delayedMessages)) {

							break;
						}
					}
				}

				Collections.sort(
					delayedMessages, new MessagePriorityComparator());

				//check for command messages and send them to the message bus

				for (Message message : delayedMessages) {
					if (message instanceof CommandMessage) {
						_messageSenderCommands.send(message);
					} else {

						// TODO do something with this response (this can be old unread message from inbox)

						if (_log.isWarnEnabled()) {
							_log.error(
								"There is no handlers for the message: "
									.concat(message.toString()));
						}
					}
				}

				setPending(false);

				if (_log.isInfoEnabled()) {
					_log.info("Connection with LCS ended.");
				}
			} catch (Exception e) {
				setPending(false);

				_log.error(e);
			}
		}
	}

}