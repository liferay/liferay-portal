/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.toolbar.contributor.locator.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Sergio González
 */
public abstract class BasePortletToolbarContributorLocator
	implements PortletToolbarContributorLocator {

	@Override
	public List<PortletToolbarContributor> getPortletToolbarContributors(
		String portletId, PortletRequest portletRequest) {

		String value = ParamUtil.getString(
			portletRequest, getParameterName(), "-");

		List<PortletToolbarContributor> portletToolbarContributors =
			_serviceTrackerMap.getService(_getKey(portletId, value));

		if (ListUtil.isEmpty(portletToolbarContributors)) {
			portletToolbarContributors = _serviceTrackerMap.getService(
				_getKey(portletId, StringPool.STAR));
		}

		return portletToolbarContributors;
	}

	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, PortletToolbarContributor.class,
			"(jakarta.portlet.name=*)",
			new ServiceReferenceMapper<String, PortletToolbarContributor>() {

				@Override
				public void map(
					ServiceReference<PortletToolbarContributor>
						serviceReference,
					Emitter<String> emitter) {

					List<String> portletNames = StringPlus.asList(
						serviceReference.getProperty("jakarta.portlet.name"));
					List<String> values = StringPlus.asList(
						serviceReference.getProperty(getPropertyName()));

					for (String portletName : portletNames) {
						for (String value : values) {
							emitter.emit(_getKey(portletName, value));
						}
					}
				}

			});
	}

	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected abstract String getParameterName();

	protected abstract String getPropertyName();

	private String _getKey(String portletId, String value) {
		return StringBundler.concat(
			portletId, StringPool.PERIOD, getPropertyName(), StringPool.PERIOD,
			value);
	}

	private ServiceTrackerMap<String, List<PortletToolbarContributor>>
		_serviceTrackerMap;

}