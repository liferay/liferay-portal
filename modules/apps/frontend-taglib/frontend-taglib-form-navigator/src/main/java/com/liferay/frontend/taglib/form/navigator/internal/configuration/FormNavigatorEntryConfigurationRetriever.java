/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.form.navigator.internal.configuration;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Tardín
 */
@Component(service = FormNavigatorEntryConfigurationRetriever.class)
public class FormNavigatorEntryConfigurationRetriever {

	public List<String> getFormNavigatorEntryKeys(
		String formNavigatorId, String categoryKey, String context) {

		List<String> formNavigatorEntryKeys = null;

		List<FormNavigatorEntryConfigurationParser>
			formNavigatorEntryConfigurationParsers = ListUtil.fromCollection(
				_serviceTrackerMap.getService(formNavigatorId));

		for (FormNavigatorEntryConfigurationParser
				formNavigatorEntryConfigurationParser :
					formNavigatorEntryConfigurationParsers) {

			List<String> currentFormNavigatorEntryKeys =
				formNavigatorEntryConfigurationParser.getFormNavigatorEntryKeys(
					categoryKey, context);

			if (currentFormNavigatorEntryKeys == null) {
				continue;
			}

			formNavigatorEntryKeys = currentFormNavigatorEntryKeys;
		}

		return formNavigatorEntryKeys;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, FormNavigatorEntryConfigurationParser.class, null,
			(serviceReference, emitter) -> {
				FormNavigatorEntryConfigurationParser
					formNavigatorEntryConfigurationParser =
						bundleContext.getService(serviceReference);

				emitter.emit(
					formNavigatorEntryConfigurationParser.getFormNavigatorId());

				bundleContext.ungetService(serviceReference);
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected final void setServiceTrackerMap(
		ServiceTrackerMap<String, List<FormNavigatorEntryConfigurationParser>>
			serviceTrackerMap) {

		_serviceTrackerMap = serviceTrackerMap;
	}

	private ServiceTrackerMap
		<String, List<FormNavigatorEntryConfigurationParser>>
			_serviceTrackerMap;

}