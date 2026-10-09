/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.convert;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.BundleContext;

/**
 * @author Iván Zaera
 */
public class ConvertProcessUtil {

	public static ConvertProcess getConvertProcess(String className) {
		return _convertProcesses.getService(className);
	}

	public static Collection<ConvertProcess> getConvertProcesses() {
		return _convertProcesses.values();
	}

	public static Collection<ConvertProcess> getEnabledConvertProcesses() {
		Collection<ConvertProcess> convertProcesses = new ArrayList<>(
			getConvertProcesses());

		Iterator<ConvertProcess> iterator = convertProcesses.iterator();

		while (iterator.hasNext()) {
			ConvertProcess convertProcess = iterator.next();

			if (convertProcess.isEnabled()) {
				continue;
			}

			iterator.remove();
		}

		return convertProcesses;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, ConvertProcess>
		_convertProcesses = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, ConvertProcess.class, null,
			(serviceReference, emitter) -> {
				ConvertProcess convertProcess = _bundleContext.getService(
					serviceReference);

				Class<?> clazz = convertProcess.getClass();

				emitter.emit(clazz.getName());

				_bundleContext.ungetService(serviceReference);
			});

}