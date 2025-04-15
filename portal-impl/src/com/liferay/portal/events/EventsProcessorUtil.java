/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.InstancePool;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Young
 * @author Raymond Augé
 */
public class EventsProcessorUtil {

	public static void process(String key, String[] classes)
		throws ActionException {

		process(key, classes, new LifecycleEvent());
	}

	public static void process(
			String key, String[] classes, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		process(
			key, classes,
			new LifecycleEvent(httpServletRequest, httpServletResponse));
	}

	public static void process(
			String key, String[] classes, HttpSession httpSession)
		throws ActionException {

		process(key, classes, new LifecycleEvent(httpSession));
	}

	public static void process(
			String key, String[] classes, LifecycleEvent lifecycleEvent)
		throws ActionException {

		for (String className : classes) {
			if (Validator.isNull(className)) {
				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Process event " + className);
			}

			LifecycleAction lifecycleAction = (LifecycleAction)InstancePool.get(
				className);

			lifecycleAction.processLifecycleEvent(lifecycleEvent);
		}

		if (Validator.isNull(key)) {
			return;
		}

		List<LifecycleAction> lifecycleActions = _lifecycleActions.getService(
			key);

		if (lifecycleActions != null) {
			for (LifecycleAction lifecycleAction : lifecycleActions) {
				lifecycleAction.processLifecycleEvent(lifecycleEvent);
			}
		}
	}

	public static void process(String key, String[] classes, String[] ids)
		throws ActionException {

		process(key, classes, new LifecycleEvent(ids));
	}

	public static void processEvent(
			LifecycleAction lifecycleAction, LifecycleEvent lifecycleEvent)
		throws ActionException {

		lifecycleAction.processLifecycleEvent(lifecycleEvent);
	}

	protected EventsProcessorUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EventsProcessorUtil.class);

	private static final ServiceTrackerMap<String, List<LifecycleAction>>
		_lifecycleActions = ServiceTrackerMapFactory.openMultiValueMap(
			SystemBundleUtil.getBundleContext(), LifecycleAction.class, "key");

}