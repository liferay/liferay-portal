/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.portlet.preferences.processor;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Máté Thurzó
 */
public class ExportImportPortletPreferencesProcessorRegistryUtil {

	public static ExportImportPortletPreferencesProcessor
		getExportImportPortletPreferencesProcessor(String portletName) {

		return _exportImportPortletPreferencesProcessorRegistryUtil.
			_getExportImportPortletPreferencesProcessor(portletName);
	}

	public static List<ExportImportPortletPreferencesProcessor>
		getExportImportPortletPreferencesProcessors() {

		return _exportImportPortletPreferencesProcessorRegistryUtil.
			_getExportImportPortletPreferencesProcessors();
	}

	private ExportImportPortletPreferencesProcessorRegistryUtil() {
		Bundle bundle = FrameworkUtil.getBundle(
			ExportImportPortletPreferencesProcessorRegistryUtil.class);

		_bundleContext = bundle.getBundleContext();

		_serviceTracker = ServiceTrackerFactory.open(
			_bundleContext, ExportImportPortletPreferencesProcessor.class,
			new ExportImportPortletPreferencesProcessorServiceTrackerCustomizer());
	}

	private ExportImportPortletPreferencesProcessor
		_getExportImportPortletPreferencesProcessor(String portletName) {

		return _exportImportPortletPreferencesProcessors.get(portletName);
	}

	private List<ExportImportPortletPreferencesProcessor>
		_getExportImportPortletPreferencesProcessors() {

		Collection<ExportImportPortletPreferencesProcessor> values =
			_exportImportPortletPreferencesProcessors.values();

		return ListUtil.fromCollection(values);
	}

	private static final ExportImportPortletPreferencesProcessorRegistryUtil
		_exportImportPortletPreferencesProcessorRegistryUtil =
			new ExportImportPortletPreferencesProcessorRegistryUtil();

	private final BundleContext _bundleContext;
	private final Map<String, ExportImportPortletPreferencesProcessor>
		_exportImportPortletPreferencesProcessors = new ConcurrentHashMap<>();
	private final ServiceTracker
		<ExportImportPortletPreferencesProcessor,
		 ExportImportPortletPreferencesProcessor> _serviceTracker;

	private class
		ExportImportPortletPreferencesProcessorServiceTrackerCustomizer
			implements ServiceTrackerCustomizer
				<ExportImportPortletPreferencesProcessor,
				 ExportImportPortletPreferencesProcessor> {

		@Override
		public ExportImportPortletPreferencesProcessor addingService(
			ServiceReference<ExportImportPortletPreferencesProcessor>
				serviceReference) {

			ExportImportPortletPreferencesProcessor
				exportImportPortletPreferencesProcessor =
					_bundleContext.getService(serviceReference);

			String portletName = GetterUtil.getString(
				serviceReference.getProperty("jakarta.portlet.name"));

			_exportImportPortletPreferencesProcessors.put(
				portletName, exportImportPortletPreferencesProcessor);

			return exportImportPortletPreferencesProcessor;
		}

		@Override
		public void modifiedService(
			ServiceReference<ExportImportPortletPreferencesProcessor>
				serviceReference,
			ExportImportPortletPreferencesProcessor
				exportImportPortletPreferencesProcessor) {

			removedService(
				serviceReference, exportImportPortletPreferencesProcessor);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<ExportImportPortletPreferencesProcessor>
				serviceReference,
			ExportImportPortletPreferencesProcessor
				exportImportPortletPreferencesProcessor) {

			_bundleContext.ungetService(serviceReference);

			String portletName = GetterUtil.getString(
				serviceReference.getProperty("jakarta.portlet.name"));

			_exportImportPortletPreferencesProcessors.remove(portletName);
		}

	}

}