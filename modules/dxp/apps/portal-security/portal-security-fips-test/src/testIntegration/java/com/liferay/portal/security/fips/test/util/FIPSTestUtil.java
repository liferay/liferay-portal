/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.test.util;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jorge García Jiménez
 * @author Lucas Miranda
 */
public class FIPSTestUtil {

	public static SafeCloseable enableFIPSModeWithSafeCloseable()
		throws Exception {

		SafeCloseable safeCloseable = PropsValuesTestUtil.swapWithSafeCloseable(
			"FIPS_ENABLED", true);

		_setGatedComponentsEnabled(true);

		return () -> {
			try {
				_setGatedComponentsEnabled(false);
			}
			catch (Exception exception) {
				ReflectionUtil.throwException(exception);
			}
			finally {
				safeCloseable.close();
			}
		};
	}

	public static List<JSONObject> getAuditLogJSONObjects() throws Exception {
		return TransformUtil.unsafeTransform(
			Files.readAllLines(getAuditLogPath()),
			JSONFactoryUtil::createJSONObject);
	}

	public static Path getAuditLogPath() {
		LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

		return Paths.get(
			PropsValues.LIFERAY_HOME, "logs",
			StringBundler.concat("fips-audit.", localDate, ".ndjson"));
	}

	public static void portalInstanceRegistered(Company company)
		throws Exception {

		String filterString = StringBundler.concat(
			"(&(component.name=com.liferay.portal.security.fips.internal.",
			"instance.lifecycle.FIPSPortalInstanceLifecycleListener)",
			"(objectClass=", PortalInstanceLifecycleListener.class.getName(),
			"))");

		ServiceTracker
			<PortalInstanceLifecycleListener, PortalInstanceLifecycleListener>
				serviceTracker = ServiceTrackerFactory.open(
					BundleContextTestUtil.getBundleContext(FIPSTestUtil.class),
					filterString);

		try {
			int timeout = 10000;

			PortalInstanceLifecycleListener portalInstanceLifecycleListener =
				serviceTracker.waitForService(timeout);

			if (portalInstanceLifecycleListener == null) {
				throw new TimeoutException(
					StringBundler.concat(
						"Timeout on waiting for ", filterString, " after ",
						timeout, "ms"));
			}

			portalInstanceLifecycleListener.portalInstanceRegistered(company);
		}
		finally {
			serviceTracker.close();
		}
	}

	private static void _setGatedComponentsEnabled(boolean enabled)
		throws Exception {

		SystemBundleUtil.callService(
			ServiceComponentRuntime.class,
			serviceComponentRuntime -> {
				_setGatedComponentsEnabled(enabled, serviceComponentRuntime);

				return null;
			});
	}

	private static void _setGatedComponentsEnabled(
			boolean enabled, ServiceComponentRuntime serviceComponentRuntime)
		throws Exception {

		BundleContext bundleContext = BundleContextTestUtil.getBundleContext(
			FIPSTestUtil.class);

		for (Bundle bundle : bundleContext.getBundles()) {
			if (!ArrayUtil.contains(
					_BUNDLE_SYMBOLIC_NAMES, bundle.getSymbolicName())) {

				continue;
			}

			for (ComponentDescriptionDTO componentDescriptionDTO :
					serviceComponentRuntime.getComponentDescriptionDTOs(
						bundle)) {

				if (componentDescriptionDTO.defaultEnabled) {
					continue;
				}

				Promise<Void> promise = null;

				if (enabled) {
					promise = serviceComponentRuntime.enableComponent(
						componentDescriptionDTO);
				}
				else {
					promise = serviceComponentRuntime.disableComponent(
						componentDescriptionDTO);
				}

				promise.getValue();
			}
		}
	}

	private static final String[] _BUNDLE_SYMBOLIC_NAMES = {
		"com.liferay.portal.security.fips.impl",
		"com.liferay.portal.security.fips.web"
	};

}