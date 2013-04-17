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

import com.liferay.lcs.messaging.MetricsMessage;
import com.liferay.lcs.service.LCSGatewayService;
import com.liferay.lcs.util.HandshakeManager;
import com.liferay.lcs.util.KeyGenerator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.yammer.metrics.core.VirtualMachineMetrics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Ivica Cardic
 */
public class JVMMetricsTask implements Runnable {

	public void run() {
		try {
			if (!_handshakeManager.isReady()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Waiting for handshakeManager to complete. " +
						"Reading messages delayed!");
				}

				return;
			}

			MetricsMessage metricsMessage = new MetricsMessage();

			// TODO change this to value assigned by LACS ieg. have a factory or
			// build pattern

			metricsMessage.setKey(_keyGenerator.getKey());
			metricsMessage.setMetricsType(MetricsMessage.METRICS_TYPE_JVM);
			metricsMessage.setPayload(getJVMMetrics());
			metricsMessage.setCreateTime(System.currentTimeMillis());

			if (_log.isDebugEnabled()) {
				_log.debug("Sending "
					.concat(MetricsMessage.METRICS_TYPE_JVM)
					.concat(" metrics message: ")
					.concat(metricsMessage.toString()));
			}

			_lcsGatewayService.sendMessage(metricsMessage);
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

	public void setVirtualMachineMetrics(
		VirtualMachineMetrics _virtualMachineMetrics) {

		this._virtualMachineMetrics = _virtualMachineMetrics;
	}

	private Map<String, Object> getJVMMetrics() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(
			"daemonThreadCount", _virtualMachineMetrics.daemonThreadCount());
		map.put(
			"deadlockedThreads", new HashSet<String>(
			_virtualMachineMetrics.deadlockedThreads()));

		double fileDescriptorUsage =
			_virtualMachineMetrics.fileDescriptorUsage();

		if(fileDescriptorUsage == Double.NaN){
			map.put("fileDescriptorUsage", null);
		}else{
			map.put("fileDescriptorUsage", fileDescriptorUsage);
		}

		Map<String, Object> garbageCollectorStatMap =
			new HashMap<String, Object>();
		for (String key : _virtualMachineMetrics.garbageCollectors().keySet()) {
			VirtualMachineMetrics.GarbageCollectorStats garbageCollectorStats =
				_virtualMachineMetrics.garbageCollectors().get(key);

			garbageCollectorStatMap.put("name", key);
			garbageCollectorStatMap.put(
				"runs", garbageCollectorStats.getRuns());
			garbageCollectorStatMap.put(
				"time", garbageCollectorStats.getTime(TimeUnit.SECONDS));
		}

		map.put("garbageCollectorStats", garbageCollectorStatMap);

		Map<String, Object> bufferPoolStatMap = new HashMap<String, Object>();
		for (String key : _virtualMachineMetrics.getBufferPoolStats()
			.keySet()) {

			VirtualMachineMetrics.BufferPoolStats bufferPoolStats =
				_virtualMachineMetrics.getBufferPoolStats().get(key);

			garbageCollectorStatMap.put("name", key);
			garbageCollectorStatMap.put("count", bufferPoolStats.getCount());
			garbageCollectorStatMap.put("", bufferPoolStats.getMemoryUsed());
			garbageCollectorStatMap.put("", bufferPoolStats.getTotalCapacity());
		}

		map.put("bufferPoolStats", bufferPoolStatMap);

		map.put("heapCommitted", _virtualMachineMetrics.heapCommitted());
		map.put("heapInit", _virtualMachineMetrics.heapInit());
		map.put("heapMax", _virtualMachineMetrics.heapMax());
		map.put("heapUsage", _virtualMachineMetrics.heapUsage());
		map.put("heapUsed", _virtualMachineMetrics.heapUsed());
		map.put(
			"memoryPoolUsage",
			new HashMap<String, Double>(
				_virtualMachineMetrics.memoryPoolUsage()));
		map.put("name", _virtualMachineMetrics.name());
		map.put("nonHeapUsage", _virtualMachineMetrics.nonHeapUsage());
		map.put("threadCount", _virtualMachineMetrics.threadCount());

		Map threadStatePercentages = new HashMap();

		for (Thread.State key : _virtualMachineMetrics.threadStatePercentages()
			.keySet()) {
			threadStatePercentages.put(
				key, _virtualMachineMetrics.threadStatePercentages().get(key));
		}

		map.put("threadStatePercentages", threadStatePercentages);
		map.put("totalCommitted", _virtualMachineMetrics.totalCommitted());
		map.put("totalInit", _virtualMachineMetrics.totalInit());
		map.put("totalMax", _virtualMachineMetrics.totalMax());
		map.put("totalUsed", _virtualMachineMetrics.totalUsed());
		map.put("uptime", _virtualMachineMetrics.uptime());
		map.put("version", _virtualMachineMetrics.version());

		return map;
	}

	private static Log _log = LogFactoryUtil.getLog(JVMMetricsTask.class);

	private HandshakeManager _handshakeManager;

	private KeyGenerator _keyGenerator;
	private LCSGatewayService _lcsGatewayService;

	private VirtualMachineMetrics _virtualMachineMetrics;

}