/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.messaging;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.resources.importer.internal.constants.ResourcesImporterDestinationNames;
import com.liferay.exportimport.resources.importer.internal.util.Importer;
import com.liferay.exportimport.resources.importer.internal.util.ImporterException;
import com.liferay.exportimport.resources.importer.internal.util.ImporterFactory;
import com.liferay.exportimport.resources.importer.internal.util.PluginPackageProperties;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.HotDeployMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Raymond Augé
 */
@Component(
	property = "destination.name=" + DestinationNames.HOT_DEPLOY,
	service = MessageListener.class
)
public class ResourcesImporterHotDeployMessageListener
	extends HotDeployMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ServletContext.class, null,
			(serviceReference, emitter) -> {
				try {
					ServletContext servletContext = bundleContext.getService(
						serviceReference);

					String servletContextName = GetterUtil.getString(
						servletContext.getServletContextName());

					emitter.emit(servletContextName);
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});

		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SERIAL,
				ResourcesImporterDestinationNames.RESOURCES_IMPORTER);

		_destination = _destinationFactory.createDestination(
			destinationConfiguration);

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", _destination.getName()
			).build();

		_serviceRegistration = _bundleContext.registerService(
			Destination.class, _destination, dictionary);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		_serviceTrackerMap.close();

		_bundleContext = null;
	}

	@Override
	protected void onDeploy(Message message) throws Exception {
		_initialize(message);
	}

	private void _importResources(
			Company company, ServletContext servletContext,
			PluginPackageProperties pluginPackageProperties,
			String messageResponseId)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			Importer importer = _importerFactory.createImporter(
				company.getCompanyId(), servletContext,
				pluginPackageProperties);

			if (!importer.isDeveloperModeEnabled() && importer.isExisting() &&
				!importer.isCompanyGroup()) {

				if (_log.isInfoEnabled()) {
					_log.info(
						"Group or layout set prototype already exists for " +
							"company " + company.getWebId());
				}

				return;
			}

			long startTime = 0;

			if (_log.isInfoEnabled()) {
				startTime = System.currentTimeMillis();
			}

			importer.importResources();

			if (_log.isInfoEnabled()) {
				StringBundler sb = new StringBundler(7);

				sb.append("Importing resources from ");
				sb.append(servletContext.getServletContextName());
				sb.append(" to group ");
				sb.append(importer.getGroupId());
				sb.append(" takes ");

				long endTime = System.currentTimeMillis() - startTime;

				sb.append(endTime);

				sb.append(" ms");

				_log.info(sb.toString());
			}

			Message message = new Message();

			message.put("companyId", company.getCompanyId());
			message.put(
				"servletContextName", servletContext.getServletContextName());
			message.put("targetClassName", importer.getTargetClassName());
			message.put("targetClassPK", importer.getTargetClassPK());

			if (Validator.isNotNull(messageResponseId)) {
				message.setPayload(
					HashMapBuilder.<String, Object>put(
						"groupId", importer.getTargetClassPK()
					).build());

				message.setResponseId(messageResponseId);
			}

			_messageBus.sendMessage(
				ResourcesImporterDestinationNames.RESOURCES_IMPORTER, message);
		}
		catch (ImporterException importerException) {
			Message message = new Message();

			message.put("companyId", company.getCompanyId());
			message.put("error", importerException.getMessage());
			message.put(
				"servletContextName", servletContext.getServletContextName());
			message.put(
				"targetClassName",
				pluginPackageProperties.getTargetClassName());
			message.put("targetClassPK", 0);

			_messageBus.sendMessage(
				ResourcesImporterDestinationNames.RESOURCES_IMPORTER, message);
		}
	}

	private void _initialize(Message message) throws Exception {
		String servletContextName = message.getString("servletContextName");

		ServletContext servletContext = _serviceTrackerMap.getService(
			servletContextName);

		if (servletContext == null) {
			return;
		}

		PluginPackageProperties pluginPackageProperties =
			new PluginPackageProperties(servletContext);

		if ((servletContext.getResource(ImporterFactory.RESOURCES_DIR) ==
				null) &&
			(servletContext.getResource(ImporterFactory.TEMPLATES_DIR) ==
				null) &&
			Validator.isNull(pluginPackageProperties.getResourcesDir())) {

			return;
		}

		try {
			ExportImportThreadLocal.setLayoutImportInProcess(true);
			ExportImportThreadLocal.setPortletImportInProcess(true);

			_companyLocalService.forEachCompany(
				company -> _importResources(
					company, servletContext, pluginPackageProperties,
					message.getResponseId()));
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourcesImporterHotDeployMessageListener.class);

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	private Destination _destination;

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private ImporterFactory _importerFactory;

	@Reference
	private MessageBus _messageBus;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.exportimport.service)(release.schema.version=1.0.2))"
	)
	private Release _release;

	private ServiceRegistration<Destination> _serviceRegistration;
	private ServiceTrackerMap<String, ServletContext> _serviceTrackerMap;

}