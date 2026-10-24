/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.exporter;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Tardín
 */
@Component(service = TranslationInfoItemFieldValuesExporterRegistry.class)
public class TranslationInfoItemFieldValuesExporterRegistryImpl
	implements TranslationInfoItemFieldValuesExporterRegistry {

	@Override
	public TranslationInfoItemFieldValuesExporter
		getTranslationInfoItemFieldValuesExporter(String mimeType) {

		return _serviceTrackerMap.getService(mimeType);
	}

	@Override
	public Collection<TranslationInfoItemFieldValuesExporter>
		getTranslationInfoItemFieldValuesExporters() {

		return _serviceTrackerMap.values();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, TranslationInfoItemFieldValuesExporter.class,
			"content.type");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private volatile ServiceTrackerMap
		<String, TranslationInfoItemFieldValuesExporter> _serviceTrackerMap;

}