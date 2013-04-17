package com.liferay.lcs.messaging.config;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.config.PluginMessagingConfigurator;

/**
 * @author Ivica Cardic
 */
public class LCSPluginMessagingConfigurator extends PluginMessagingConfigurator {

	public void afterPropertiesSet() {
		try {
			super.afterPropertiesSet();
		}catch (IllegalStateException e) {
			_log.warn(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LCSPluginMessagingConfigurator.class);

}