/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.initializer;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = SiteInitializerRegistry.class)
public class SiteInitializerRegistryImpl implements SiteInitializerRegistry {

	@Override
	public SiteInitializer getSiteInitializer(String key) {
		if (Validator.isNull(key)) {
			return null;
		}

		SiteInitializer siteInitializer = _serviceTrackerMap.getService(key);

		if (siteInitializer == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No site initializer registered with key " + key);
			}

			return null;
		}

		return siteInitializer;
	}

	@Override
	public List<SiteInitializer> getSiteInitializers(long companyId) {
		return getSiteInitializers(companyId, false);
	}

	@Override
	public List<SiteInitializer> getSiteInitializers(
		long companyId, boolean activeOnly) {

		Predicate<SiteInitializer> predicate =
			siteInitializer -> !Objects.equals(
				siteInitializer.getKey(), "com.liferay.site.initializer.cms");

		if (activeOnly) {
			predicate = predicate.and(
				siteInitializer -> siteInitializer.isActive(companyId));
		}

		return ListUtil.filter(
			new ArrayList<>(_serviceTrackerMap.values()), predicate);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SiteInitializer.class, "site.initializer.key");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteInitializerRegistryImpl.class);

	private ServiceTrackerMap<String, SiteInitializer> _serviceTrackerMap;

}