/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.item.action;

import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionActionProviderRegistry;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.GenericUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stefan Tanasie
 */
@Component(service = ContentDashboardItemVersionActionProviderRegistry.class)
public class ContentDashboardItemVersionActionProviderRegistryImpl
	implements ContentDashboardItemVersionActionProviderRegistry {

	@Override
	public List<ContentDashboardItemVersionActionProvider>
		getContentDashboardItemVersionActionProviders(String className) {

		List<ContentDashboardItemVersionActionProvider>
			contentDashboardItemVersionActionProviders =
				_serviceTrackerMap.getService(className);

		if (ListUtil.isEmpty(contentDashboardItemVersionActionProviders)) {
			return Collections.emptyList();
		}

		return contentDashboardItemVersionActionProviders;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, ContentDashboardItemVersionActionProvider.class,
			null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(contentDashboardItemVersionAction, emitter) -> emitter.emit(
					GenericUtil.getGenericClassName(
						contentDashboardItemVersionAction))));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap
		<String, List<ContentDashboardItemVersionActionProvider>>
			_serviceTrackerMap;

}