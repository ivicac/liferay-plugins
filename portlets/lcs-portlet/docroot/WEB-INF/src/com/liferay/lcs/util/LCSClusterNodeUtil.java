package com.liferay.lcs.util;


import com.liferay.lcs.service.LCSClusterNodeServiceUtil;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterNodeResponses;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.util.ClassLoaderPool;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Ivica Cardic
 */
public class LCSClusterNodeUtil {

	public static Map<String, Object> getServerInfo() {
		Map<String, Object> serverInfo = new HashMap<String, Object>();

		try {
			serverInfo.put(
				"registered", LCSClusterNodeServiceUtil.isRegistered());
			serverInfo.put("key", KeyGeneratorUtil.getKey());
		} catch (Exception e) {
			_log.error(e);
		}

		return serverInfo;
	}

	public static boolean registerLCSClusterNode() throws Exception {
		if (ClusterExecutorUtil.isEnabled()) {
			String siblingKey = null;

			List<ClusterNode> clusterNodes = ClusterExecutorUtil
				.getClusterNodes();

			String localClusterNodeId = ClusterExecutorUtil
				.getLocalClusterNode().getClusterNodeId();

			for (int i = 0; i < clusterNodes.size(); i++) {
				String clusterNodeId = clusterNodes.get(i).getClusterNodeId();

				if (!localClusterNodeId.equals(clusterNodeId)) {
					Map<String, Object> result = _getServerInfo(clusterNodeId);

					boolean registered = (Boolean) result.get("registered");

					if (registered) {
						siblingKey = (String) result.get("key");

						break;
					}
				}
			}

			if (siblingKey != null) {
				LCSClusterNodeServiceUtil.addLCSClusterNode(siblingKey);

				return true;
			}
		}

		return false;
	}

	public static Map<String, Object> registerLCSClusterNode(
		String siblingKey) {

		Map<String, Object> attributes = new HashMap<String, Object>();

		String key = null;
		try {
			key = KeyGeneratorUtil.getKey();
		} catch (Exception e) {
		}

		try {
			//TODO init web services

			LCSClusterNodeServiceUtil.addLCSClusterNode(siblingKey);

			HandshakeManagerUtil.start();

			attributes.put(
				"SUCCESS_MESSAGE",
				"Cluster node has been successfully registered: " + key);
		} catch (Exception e) {
			_log.error(e, e);

			attributes.put(
				"ERROR_MESSAGE",
				"There was an error registering cluster node " + key);
		}

		return attributes;
	}

	public static Map<String, Object> registerLCSClusterNode(
			String description, String location, long corpEntryId,
			long lcsClusterEntryId, String lcsClusterEntryName)
		throws Exception {

		Map<String, Object> results = new HashMap<String, Object>();

		if (ClusterExecutorUtil.isEnabled()) {
			try {
				List<ClusterNode> clusterNodes = ClusterExecutorUtil
					.getClusterNodes();

				String localClusterNodeId = ClusterExecutorUtil
					.getLocalClusterNode().getClusterNodeId();

				String siblingKey = null;
				List<String> notRegistered = new ArrayList<String>();

				for (int i = 0; i < clusterNodes.size(); i++) {
					ClusterNode clusterNode = clusterNodes.get(i);

					String clusterNodeId = clusterNode.getClusterNodeId();

					if (!localClusterNodeId.equals(clusterNodeId)) {
						Map<String, Object> result =
							_getServerInfo(clusterNodeId);

						boolean registered = (Boolean) result.get("registered");

						if (registered) {
							siblingKey = (String) result.get("key");
						} else {
							notRegistered.add(clusterNodeId);
						}
					}
				}

				String key = KeyGeneratorUtil.getKey();

				if (siblingKey == null) {
					LCSClusterNodeServiceUtil.addLCSClusterNode(
						description, location, corpEntryId, lcsClusterEntryId,
						lcsClusterEntryName);

					siblingKey = key;
				} else {
					LCSClusterNodeServiceUtil.addLCSClusterNode(siblingKey);
				}

				MethodHandler methodHandler = new MethodHandler(
					_registerLCSClusterNodeMethodKey, siblingKey);

				MethodHandlerWrapper methodHandlerWrapper =
					new MethodHandlerWrapper(methodHandler);

				for (String clusterNodeId : notRegistered) {
					ClusterRequest clusterRequest =
						ClusterRequest.createUnicastRequest(
							methodHandlerWrapper, clusterNodeId);

					FutureClusterResponses futureClusterResponses =
						ClusterExecutorUtil.execute(clusterRequest);

					ClusterNodeResponses clusterNodeResponses =
						futureClusterResponses.get(
							20000, TimeUnit.MILLISECONDS);

					ClusterNodeResponse clusterNodeResponse =
						clusterNodeResponses.getClusterResponse(clusterNodeId);

					Map<String, Object> attributes =
						(Map<String, Object>) clusterNodeResponse.getResult();

					for (Map.Entry<String, Object> entry :
						attributes.entrySet()) {

						results.put(
							clusterNodeId + StringPool.UNDERLINE +
								entry.getKey(), entry.getValue());
					}
				}
			} catch (Exception e) {
				_log.error(e, e);

				throw e;
			}
		} else {
			LCSClusterNodeServiceUtil.addLCSClusterNode(
				description, location, corpEntryId, lcsClusterEntryId,
				lcsClusterEntryName);
		}

		HandshakeManagerUtil.start();

		return results;
	}

	private static Map<String, Object> _getServerInfo(
		String clusterNodeId) throws Exception {

		MethodHandlerWrapper methodHandlerWrapper = new MethodHandlerWrapper(
			_getServerInfoMethodHandler);

		ClusterRequest clusterRequest = ClusterRequest.createUnicastRequest(
			methodHandlerWrapper, clusterNodeId);

		FutureClusterResponses futureClusterResponses =
			ClusterExecutorUtil.execute(clusterRequest);

		ClusterNodeResponses clusterNodeResponses =
			futureClusterResponses.get(20000, TimeUnit.MILLISECONDS);

		ClusterNodeResponse clusterNodeResponse =
			clusterNodeResponses.getClusterResponse(clusterNodeId);

		return (Map<String, Object>) clusterNodeResponse.getResult();
	}

	private static class MethodHandlerWrapper extends MethodHandler{

		public MethodHandlerWrapper(
			MethodHandler methodHandler) {

			super(methodHandler.getMethodKey(), methodHandler.getArguments());

			_methodHandler = methodHandler;
		}

		public Object invoke(boolean newInstance) throws Exception {
			Thread currentThread = Thread.currentThread();

			ClassLoader contextClassLoader =
				currentThread.getContextClassLoader();

			try {
				ClassLoader classLoader =PortletClassLoaderUtil.getClassLoader(
					"RegistrationPortlet_WAR_lcsportlet");

				currentThread.setContextClassLoader(classLoader);

				return _methodHandler.invoke(newInstance);
			}
			finally {
				currentThread.setContextClassLoader(contextClassLoader);
			}
		}

		private MethodHandler _methodHandler;

	}

	private static Log _log = LogFactoryUtil.getLog(LCSClusterNodeUtil.class);

	private static MethodHandler _getServerInfoMethodHandler =
		new MethodHandler(
			new MethodKey(LCSClusterNodeUtil.class, "getServerInfo"));

	private static MethodKey _registerLCSClusterNodeMethodKey = new MethodKey(
		LCSClusterNodeUtil.class, "registerLCSClusterNode",
		String.class);

}
