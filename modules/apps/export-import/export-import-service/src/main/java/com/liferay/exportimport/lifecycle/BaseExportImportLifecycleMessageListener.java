/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.lifecycle;

import com.liferay.exportimport.kernel.lifecycle.EventAwareExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.ProcessAwareExportImportLifecycleListener;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Daniel Kocsis
 */
public abstract class BaseExportImportLifecycleMessageListener
	extends BaseMessageListener {

	protected void activate(BundleContext bundleContext, boolean parallel) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ExportImportLifecycleListener.class, null,
			new ExportImportLifecycleListenerServiceTrackerCustomizer(
				bundleContext, parallel));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		ExportImportLifecycleEvent exportImportLifecycleEvent =
			(ExportImportLifecycleEvent)message.get(
				"exportImportLifecycleEvent");

		for (ExportImportLifecycleListener exportImportLifecycleListener :
				_serviceTrackerList) {

			try {
				exportImportLifecycleListener.onExportImportLifecycleEvent(
					exportImportLifecycleEvent);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to call " +
							exportImportLifecycleListener.getClass(),
						exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseExportImportLifecycleMessageListener.class);

	private ServiceTrackerList<ExportImportLifecycleListener>
		_serviceTrackerList;

	private static class ExportImportLifecycleListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ExportImportLifecycleListener, ExportImportLifecycleListener> {

		@Override
		public ExportImportLifecycleListener addingService(
			ServiceReference<ExportImportLifecycleListener> serviceReference) {

			ExportImportLifecycleListener exportImportLifecycleListener =
				_bundleContext.getService(serviceReference);

			if (exportImportLifecycleListener instanceof
					EventAwareExportImportLifecycleListener) {

				exportImportLifecycleListener =
					new DefaultEventAwareExportImportLifecycleListener(
						(EventAwareExportImportLifecycleListener)
							exportImportLifecycleListener);
			}
			else if (exportImportLifecycleListener instanceof
					ProcessAwareExportImportLifecycleListener) {

				exportImportLifecycleListener =
					new DefaultProcessAwareExportImportLifecycleListener(
						(ProcessAwareExportImportLifecycleListener)
							exportImportLifecycleListener);
			}

			if (exportImportLifecycleListener.isParallel() != _parallel) {
				_bundleContext.ungetService(serviceReference);

				return null;
			}

			return exportImportLifecycleListener;
		}

		@Override
		public void modifiedService(
			ServiceReference<ExportImportLifecycleListener> serviceReference,
			ExportImportLifecycleListener exportImportLifecycleListener) {
		}

		@Override
		public void removedService(
			ServiceReference<ExportImportLifecycleListener> serviceReference,
			ExportImportLifecycleListener exportImportLifecycleListener) {

			_bundleContext.ungetService(serviceReference);
		}

		private ExportImportLifecycleListenerServiceTrackerCustomizer(
			BundleContext bundleContext, boolean parallel) {

			_bundleContext = bundleContext;
			_parallel = parallel;
		}

		private final BundleContext _bundleContext;
		private final boolean _parallel;

	}

}