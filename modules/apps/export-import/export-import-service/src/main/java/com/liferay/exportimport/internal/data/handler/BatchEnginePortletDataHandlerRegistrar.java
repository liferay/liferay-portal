/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.InitialRequestSyncUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.staging.StagingGroupHelper;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
		InitialRequestSyncUtil.registerSyncCallable(
			() -> _serviceTrackerListDCLSingleton.getSingleton(
				() -> ServiceTrackerListFactory.open(
					bundleContext, null,
					"(export.import.vulcan.batch.engine.task.item.delegate=" +
						"true)",
					new VulcanBatchEngineTaskItemDelegateServiceTrackerCustomizer(
						bundleContext))));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerListDCLSingleton.destroy(ServiceTrackerList::close);
	}

	private Dictionary<String, Object> _setEnabledCompanyId(
		long companyId, Dictionary<String, Object> properties) {

		return HashMapDictionaryBuilder.<String, Object>putAll(
			properties
		).put(
			"companyId",
			() -> {
				if (companyId == 0) {
					return null;
				}

				return String.valueOf(companyId);
			}
		).build();
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

	@Reference
	private ChangesetEntryLocalService _changesetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	private final Map<String, ServiceRegistration<PortletDataHandler>>
		_portletIdServiceRegistrations = new ConcurrentHashMap<>();
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

			long companyId = GetterUtil.getLong(
				serviceReference.getProperty("companyId"));

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
				BatchEnginePortletDataHandlerRegistryUtil.getByPortletId(
					companyId, portletId);

			if (batchEnginePortletDataHandler == null) {
				batchEnginePortletDataHandler =
					new BatchEnginePortletDataHandler(
						_batchEngineExportTaskExecutor,
						_batchEngineExportTaskLocalService,
						_batchEngineImportTaskExecutor,
						_batchEngineImportTaskService,
						_changesetEntryLocalService, _classNameLocalService,
						_exportImportHelper, _groupLocalService,
						_stagingGroupHelper);

				batchEnginePortletDataHandler.setPortletId(
					exportImportDescriptor.getPortletId());
			}

			batchEnginePortletDataHandler.
				registerExportImportVulcanBatchEngineTaskItemDelegate(
					GetterUtil.getObject(
						(String)serviceReference.getProperty(
							"batch.engine.task.item.delegate.class.name"),
						() -> (String)serviceReference.getProperty(
							"batch.engine.entity.class.name")),
					_bundleContext, companyId, exportImportDescriptor,
					(String)serviceReference.getProperty(
						"batch.engine.task.item.delegate.name"));

			BatchEnginePortletDataHandlerRegistryUtil.
				registerBatchEnginePortletDataHandler(
					batchEnginePortletDataHandler, companyId, portletId);
			BatchEnginePortletDataHandlerRegistryUtil.registerKey(
				companyId, exportImportDescriptor.getKey(), portletId);

			BatchEnginePortletDataHandler finalBatchEnginePortletDataHandler =
				batchEnginePortletDataHandler;

			return _portletIdServiceRegistrations.computeIfAbsent(
				portletId,
				key -> _bundleContext.registerService(
					PortletDataHandler.class,
					finalBatchEnginePortletDataHandler,
					_setEnabledCompanyId(
						companyId,
						HashMapDictionaryBuilder.<String, Object>put(
							"jakarta.portlet.name", portletId
						).put(
							"service.ranking", Integer.MAX_VALUE
						).build())));
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

			long companyId = GetterUtil.getLong(
				serviceReference.getProperty("companyId"));

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
				BatchEnginePortletDataHandlerRegistryUtil.getByPortletId(
					companyId, portletId);

			if (batchEnginePortletDataHandler == null) {
				return;
			}

			exportImportDescriptor =
				batchEnginePortletDataHandler.
					unregisterExportImportVulcanBatchEngineTaskItemDelegate(
						_bundleContext, companyId,
						exportImportDescriptor.getKey());

			if (exportImportDescriptor != null) {
				BatchEnginePortletDataHandlerRegistryUtil.unregisterKey(
					companyId, exportImportDescriptor.getKey(), portletId);
			}

			String[] classNames = batchEnginePortletDataHandler.getClassNames();

			if (classNames.length == 0) {
				serviceRegistration.unregister();

				BatchEnginePortletDataHandlerRegistryUtil.unregister(
					companyId, portletId);

				_portletIdServiceRegistrations.remove(portletId);
			}
		}

		private final BundleContext _bundleContext;

	}

}