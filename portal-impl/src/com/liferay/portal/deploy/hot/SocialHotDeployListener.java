/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.deploy.hot.BaseHotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.social.kernel.model.SocialAchievement;
import com.liferay.social.kernel.model.SocialActivityCounterDefinition;
import com.liferay.social.kernel.model.SocialActivityDefinition;
import com.liferay.social.kernel.util.SocialConfigurationUtil;

import jakarta.servlet.ServletContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zsolt Berentey
 */
public class SocialHotDeployListener extends BaseHotDeployListener {

	@Override
	public void invokeDeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeDeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error registering social for ", throwable);
		}
	}

	@Override
	public void invokeUndeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeUndeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error unregistering social for ", throwable);
		}
	}

	protected void doInvokeDeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking deploy for " + servletContextName);
		}

		String[] xmls = {
			StreamUtil.toString(
				servletContext.getResourceAsStream(
					"/WEB-INF/liferay-social.xml"))
		};

		if (xmls[0] == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Registering social for " + servletContextName);
		}

		List<Object> objects = SocialConfigurationUtil.read(
			hotDeployEvent.getContextClassLoader(), xmls);

		_objects.put(servletContextName, objects);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Social for " + servletContextName + " is available for use");
		}
	}

	protected void doInvokeUndeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking undeploy for " + servletContextName);
		}

		List<Object> objects = _objects.remove(servletContextName);

		if (objects == null) {
			return;
		}

		for (Object object : objects) {
			if (object instanceof SocialActivityDefinition) {
				SocialActivityDefinition activityDefinition =
					(SocialActivityDefinition)object;

				SocialConfigurationUtil.removeActivityDefinition(
					activityDefinition);

				continue;
			}

			Tuple tuple = (Tuple)object;

			SocialActivityDefinition activityDefinition =
				(SocialActivityDefinition)tuple.getObject(0);

			Object tupleObject1 = tuple.getObject(1);

			if (tupleObject1 instanceof SocialAchievement) {
				List<SocialAchievement> achievements =
					activityDefinition.getAchievements();

				achievements.remove(tupleObject1);
			}
			else if (tupleObject1 instanceof SocialActivityCounterDefinition) {
				Collection<SocialActivityCounterDefinition>
					activityCounterDefinitions =
						activityDefinition.getActivityCounterDefinitions();

				activityCounterDefinitions.remove(tupleObject1);
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("Social for " + servletContextName + " was unregistered");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SocialHotDeployListener.class);

	private static final Map<String, List<Object>> _objects = new HashMap<>();

}