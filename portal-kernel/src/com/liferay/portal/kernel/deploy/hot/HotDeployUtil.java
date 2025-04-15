/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.deploy.hot;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Ivica Cardic
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class HotDeployUtil {

	public static void fireDeployEvent(HotDeployEvent hotDeployEvent) {
		ServletContext servletContext = hotDeployEvent.getServletContext();

		ServletContextPool.put(
			servletContext.getServletContextName(), servletContext);

		if (_capturePrematureEvents) {

			// Capture events that are fired before the portal initialized

			DependencyManagerSyncUtil.registerSyncCallable(
				() -> {
					fireDeployEvent(hotDeployEvent);

					return null;
				});
		}
		else {

			// Fire event

			_fireDeployEvent(hotDeployEvent);
		}
	}

	public static void fireUndeployEvent(HotDeployEvent hotDeployEvent) {
		for (int i = _hotDeployListeners.size() - 1; i >= 0; i--) {
			HotDeployListener hotDeployListener = _hotDeployListeners.get(i);

			PortletClassLoaderUtil.setServletContextName(
				hotDeployEvent.getServletContextName());

			try {
				hotDeployListener.invokeUndeploy(hotDeployEvent);
			}
			catch (HotDeployException hotDeployException) {
				_log.error(hotDeployException);
			}
			finally {
				PortletClassLoaderUtil.setServletContextName(null);
			}
		}

		_deployedServletContextNames.remove(
			hotDeployEvent.getServletContextName());
	}

	public static void registerListener(HotDeployListener hotDeployListener) {
		_hotDeployListeners.add(hotDeployListener);
	}

	public static void reset() {
		_capturePrematureEvents = true;
		_dependentHotDeployEvents.clear();
		_deployedServletContextNames.clear();
		_hotDeployListeners.clear();
	}

	public static void setCapturePrematureEvents(
		boolean capturePrematureEvents) {

		_capturePrematureEvents = capturePrematureEvents;
	}

	public static void unregisterListener(HotDeployListener hotDeployListener) {
		_hotDeployListeners.remove(hotDeployListener);
	}

	public static void unregisterListeners() {
		_hotDeployListeners.clear();
	}

	private static void _fireDeployEvent(HotDeployEvent hotDeployEvent) {
		String servletContextName = hotDeployEvent.getServletContextName();

		if (_deployedServletContextNames.contains(servletContextName)) {
			return;
		}

		boolean hasDependencies = true;

		for (String dependentServletContextName :
				hotDeployEvent.getDependentServletContextNames()) {

			if (!_deployedServletContextNames.contains(
					dependentServletContextName)) {

				hasDependencies = false;

				break;
			}
		}

		if (hasDependencies) {
			if (_log.isDebugEnabled()) {
				_log.debug("Deploying " + servletContextName + " from queue");
			}

			for (HotDeployListener hotDeployListener : _hotDeployListeners) {
				PortletClassLoaderUtil.setServletContextName(
					hotDeployEvent.getServletContextName());

				try {
					hotDeployListener.invokeDeploy(hotDeployEvent);
				}
				catch (HotDeployException hotDeployException) {
					_log.error(hotDeployException);
				}
				finally {
					PortletClassLoaderUtil.setServletContextName(null);
				}
			}

			_deployedServletContextNames.add(servletContextName);

			_dependentHotDeployEvents.remove(hotDeployEvent);

			try (SafeCloseable safeCloseable1 =
					ThreadContextClassLoaderUtil.swap(
						PortalClassLoaderUtil.getClassLoader())) {

				List<HotDeployEvent> dependentEvents = new ArrayList<>(
					_dependentHotDeployEvents);

				for (HotDeployEvent dependentEvent : dependentEvents) {
					try (SafeCloseable safeCloseable2 =
							ThreadContextClassLoaderUtil.swap(
								dependentEvent.getContextClassLoader())) {

						_fireDeployEvent(dependentEvent);
					}
				}
			}
		}
		else {
			if (!_dependentHotDeployEvents.contains(hotDeployEvent)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Queueing ", servletContextName,
							" for deploy because it is missing ",
							_getRequiredServletContextNames(hotDeployEvent)));
				}

				_dependentHotDeployEvents.add(hotDeployEvent);
			}
			else {
				if (_log.isInfoEnabled()) {
					for (HotDeployEvent dependentHotDeployEvent :
							_dependentHotDeployEvents) {

						_log.info(
							StringBundler.concat(
								servletContextName,
								" is still in queue because it is missing ",
								_getRequiredServletContextNames(
									dependentHotDeployEvent)));
					}
				}
			}
		}
	}

	private static String _getRequiredServletContextNames(
		HotDeployEvent hotDeployEvent) {

		List<String> requiredServletContextNames = new ArrayList<>();

		for (String dependentServletContextName :
				hotDeployEvent.getDependentServletContextNames()) {

			if (!_deployedServletContextNames.contains(
					dependentServletContextName)) {

				requiredServletContextNames.add(dependentServletContextName);
			}
		}

		Collections.sort(requiredServletContextNames);

		return StringUtil.merge(requiredServletContextNames, ", ");
	}

	private static final Log _log = LogFactoryUtil.getLog(HotDeployUtil.class);

	private static volatile boolean _capturePrematureEvents = true;
	private static final Queue<HotDeployEvent> _dependentHotDeployEvents =
		new ConcurrentLinkedQueue<>();
	private static final Set<String> _deployedServletContextNames =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private static final List<HotDeployListener> _hotDeployListeners =
		new CopyOnWriteArrayList<>();

}