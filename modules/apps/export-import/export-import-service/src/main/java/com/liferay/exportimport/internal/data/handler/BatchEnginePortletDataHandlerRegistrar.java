/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.staging.StagingGroupHelper;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = {})
public class BatchEnginePortletDataHandlerRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class,
			(companyId, featureFlagKey, enabled) -> {
				if (enabled) {
					_enabledCompanyIds.add(companyId);
				}
				else {
					_enabledCompanyIds.remove(companyId);
				}

				if (_enabledCompanyIds.isEmpty()) {
					_serviceTrackerListDCLSingleton.destroy(
						ServiceTrackerList::close);

					return;
				}

				AtomicBoolean newOpen = new AtomicBoolean();

				_serviceTrackerListDCLSingleton.getSingleton(
					() -> {
						newOpen.set(true);

						return ServiceTrackerListFactory.open(
							bundleContext, null,
							"(export.import.vulcan.batch.engine.task.item." +
								"delegate=true)",
							new VulcanBatchEngineTaskItemDelegateServiceTrackerCustomizer(
								bundleContext));
					});

				if (newOpen.get()) {
					return;
				}

				for (ServiceRegistration<PortletDataHandler>
						serviceRegistration : _serviceRegistrations.values()) {

					Dictionary<String, Object> properties = _toProperties(
						serviceRegistration.getReference());

					serviceRegistration.setProperties(
						_setEnabledCompanyIds(properties));
				}
			},
			MapUtil.singletonDictionary("feature.flag.key", "LPD-35914"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		_serviceTrackerListDCLSingleton.destroy(ServiceTrackerList::close);
	}

	private Dictionary<String, Object> _setEnabledCompanyIds(
		Dictionary<String, Object> properties) {

		return HashMapDictionaryBuilder.<String, Object>putAll(
			properties
		).put(
			"companyId", ArrayUtil.toStringArray(_enabledCompanyIds)
		).build();
	}

	private Dictionary<String, Object> _toProperties(
		ServiceReference<?> serviceReference) {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		for (String key : serviceReference.getPropertyKeys()) {
			Object value = serviceReference.getProperty(key);

			properties.put(key, value);
		}

		return properties;
	}

	@Reference
	private BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;

	@Reference
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Reference
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Reference
	private BatchEngineImportTaskService _batchEngineImportTaskService;

	private final Map<String, BatchEnginePortletDataHandler>
		_batchEnginePortletDataHandlers = new HashMap<>();
	private final List<Long> _enabledCompanyIds = new CopyOnWriteArrayList<>();

	@Reference
	private GroupLocalService _groupLocalService;

	private volatile ServiceRegistration<FeatureFlagListener>
		_serviceRegistration;
	private final Map<String, ServiceRegistration<PortletDataHandler>>
		_serviceRegistrations = new HashMap<>();
	private final DCLSingleton
		<ServiceTrackerList<ServiceRegistration<PortletDataHandler>>>
			_serviceTrackerListDCLSingleton = new DCLSingleton<>();

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	private class VulcanBatchEngineTaskItemDelegateServiceTrackerCustomizer
		implements EagerServiceTrackerCustomizer
			<VulcanBatchEngineTaskItemDelegate,
			 ServiceRegistration<PortletDataHandler>> {

		public VulcanBatchEngineTaskItemDelegateServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ServiceRegistration<PortletDataHandler> addingService(
			ServiceReference<VulcanBatchEngineTaskItemDelegate>
				serviceReference) {

			ExportImportVulcanBatchEngineTaskItemDelegate<?>
				exportImportVulcanBatchEngineTaskItemDelegate =
					(ExportImportVulcanBatchEngineTaskItemDelegate<?>)
						_bundleContext.getService(serviceReference);

			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					exportImportVulcanBatchEngineTaskItemDelegate.
						getExportImportDescriptor();

			String portletId = exportImportDescriptor.getPortletId();

			BatchEnginePortletDataHandler
				previousBatchEnginePortletDataHandler =
					_batchEnginePortletDataHandlers.get(portletId);

			BatchEnginePortletDataHandler batchEnginePortletDataHandler =
				previousBatchEnginePortletDataHandler;

			if (previousBatchEnginePortletDataHandler == null) {
				batchEnginePortletDataHandler =
					new BatchEnginePortletDataHandler(
						_batchEngineExportTaskExecutor,
						_batchEngineExportTaskLocalService,
						_batchEngineImportTaskExecutor,
						_batchEngineImportTaskService, _groupLocalService,
						_stagingGroupHelper);

				batchEnginePortletDataHandler.setPortletId(
					exportImportDescriptor.getPortletId());

				_batchEnginePortletDataHandlers.put(
					portletId, batchEnginePortletDataHandler);
			}

			batchEnginePortletDataHandler.
				registerExportImportVulcanBatchEngineTaskItemDelegate(
					GetterUtil.getObject(
						(String)serviceReference.getProperty(
							"batch.engine.task.item.delegate.class.name"),
						() -> (String)serviceReference.getProperty(
							"batch.engine.entity.class.name")),
					exportImportDescriptor,
					(String)serviceReference.getProperty(
						"batch.engine.task.item.delegate.name"));

			if (previousBatchEnginePortletDataHandler != null) {
				return _serviceRegistrations.get(portletId);
			}

			ServiceRegistration<PortletDataHandler> serviceRegistration =
				_bundleContext.registerService(
					PortletDataHandler.class, batchEnginePortletDataHandler,
					_setEnabledCompanyIds(
						HashMapDictionaryBuilder.<String, Object>put(
							"batch.engine.task.item.delegate.item.class.name",
							exportImportDescriptor.getItemClassName()
						).put(
							"jakarta.portlet.name", portletId
						).put(
							"service.ranking", Integer.MAX_VALUE
						).build()));

			_serviceRegistrations.put(portletId, serviceRegistration);

			return serviceRegistration;
		}

		@Override
		public void modifiedService(
			ServiceReference<VulcanBatchEngineTaskItemDelegate>
				serviceReference,
			ServiceRegistration<PortletDataHandler> serviceRegistration) {
		}

		@Override
		public void removedService(
			ServiceReference<VulcanBatchEngineTaskItemDelegate>
				serviceReference,
			ServiceRegistration<PortletDataHandler> serviceRegistration) {

			ExportImportVulcanBatchEngineTaskItemDelegate<?>
				exportImportVulcanBatchEngineTaskItemDelegate =
					(ExportImportVulcanBatchEngineTaskItemDelegate<?>)
						_bundleContext.getService(serviceReference);

			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					exportImportVulcanBatchEngineTaskItemDelegate.
						getExportImportDescriptor();

			String portletId = exportImportDescriptor.getPortletId();

			BatchEnginePortletDataHandler batchEnginePortletDataHandler =
				_batchEnginePortletDataHandlers.get(portletId);

			if (batchEnginePortletDataHandler == null) {
				return;
			}

			String className = GetterUtil.getObject(
				(String)serviceReference.getProperty(
					"batch.engine.task.item.delegate.class.name"),
				() -> (String)serviceReference.getProperty(
					"batch.engine.entity.class.name"));

			String taskItemDelegateName = (String)serviceReference.getProperty(
				"batch.engine.task.item.delegate.name");

			batchEnginePortletDataHandler.
				unregisterExportImportVulcanBatchEngineTaskItemDelegate(
					className, taskItemDelegateName);

			String[] classNames = batchEnginePortletDataHandler.getClassNames();

			if (classNames.length == 0) {
				serviceRegistration.unregister();

				_batchEnginePortletDataHandlers.remove(portletId);

				_serviceRegistrations.remove(portletId);
			}
		}

		private final BundleContext _bundleContext;

	}

}