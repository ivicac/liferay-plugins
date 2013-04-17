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

import com.liferay.lcs.messaging.RequestCommandMessage;
import com.liferay.lcs.task.HeartbeatTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ivica Cardic
 */
public class CheckHeartbeatCommand extends AbstractCommand {
	public void execute(RequestCommandMessage requestCommandMessage) {
		_executorService.execute(_heartbeatTask);
	}

	public void setHeartbeatTask(HeartbeatTask heartbeatTask) {
		this._heartbeatTask = heartbeatTask;
	}

	private ExecutorService _executorService = Executors.newCachedThreadPool();

	private HeartbeatTask _heartbeatTask;
}