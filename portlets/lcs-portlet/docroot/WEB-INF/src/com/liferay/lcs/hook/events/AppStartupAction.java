package com.liferay.lcs.hook.events;

import com.liferay.lcs.util.HandshakeManagerUtil;
import com.liferay.lcs.util.OsbLcsPortletHelper;
import com.liferay.portal.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class AppStartupAction extends SimpleAction {

	@Override
	public void run(String[] ids) throws ActionException {
		try {
			if(!HandshakeManagerUtil.isPending()){
				OsbLcsPortletHelper.setupCredentials();

				HandshakeManagerUtil.start();
			}
		} catch (Exception e) {
			if(!(e instanceof NoSuchPortletPreferencesException)){
				throw new ActionException(e);
			}
		}
	}

}