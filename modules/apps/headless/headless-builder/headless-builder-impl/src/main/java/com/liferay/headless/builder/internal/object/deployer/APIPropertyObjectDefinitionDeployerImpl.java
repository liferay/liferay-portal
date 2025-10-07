/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.object.deployer;

import com.liferay.headless.builder.internal.object.related.models.DeleteOnDisassociateObjectRelatedModelsProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistryUtil;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Correa
 */
@Component(service = ObjectDefinitionDeployer.class)
public class APIPropertyObjectDefinitionDeployerImpl
	implements ObjectDefinitionDeployer {

	@Override
	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition) {

		if (!_isAPIPropertyObjectDefinition(objectDefinition)) {
			return Collections.emptyList();
		}

		_serviceTracker = ServiceTrackerFactory.open(
			_bundleContext,
			StringBundler.concat(
				"(&(objectClass=", ObjectRelatedModelsProvider.class.getName(),
				")(",
				ObjectRelatedModelsProviderRegistryUtil.
					KEY_OBJECT_DEFINITION_ERC,
				"=L_API_PROPERTY)(",
				ObjectRelatedModelsProviderRegistryUtil.KEY_RELATIONSHIP_TYPE,
				"=", ObjectRelationshipConstants.TYPE_ONE_TO_MANY, "))"),
			new ObjectRelatedModelsProviderServiceTrackerCustomizer(
				objectDefinition.getCompanyId()));

		try {
			_updateExistingAPIProperties(objectDefinition);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public void undeploy(ObjectDefinition objectDefinition) {
		if (!_isAPIPropertyObjectDefinition(objectDefinition)) {
			return;
		}

		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private boolean _isAPIPropertyObjectDefinition(
		ObjectDefinition objectDefinition) {

		return Objects.equals(
			objectDefinition.getExternalReferenceCode(), "L_API_PROPERTY");
	}

	private void _updateExistingAPIProperties(ObjectDefinition objectDefinition)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "type");

		if (objectField == null) {
			return;
		}

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				GroupThreadLocal.getGroupId(), objectDefinition.getCompanyId(),
				objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create("type eq null", objectDefinition), null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (Map<String, Serializable> values : valuesList) {
			Collection<Serializable> collection = values.values();

			collection.removeAll(Collections.singleton(null));

			values.put("type", "field");

			_objectEntryLocalService.addOrUpdateObjectEntry(
				(String)values.get("externalReferenceCode"),
				GroupThreadLocal.getGroupId(), objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				values, new ServiceContext());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		APIPropertyObjectDefinitionDeployerImpl.class);

	private BundleContext _bundleContext;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ServiceTracker
		<ObjectRelatedModelsProvider, ObjectRelatedModelsProvider>
			_serviceTracker;

	private class ObjectRelatedModelsProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ObjectRelatedModelsProvider, ObjectRelatedModelsProvider> {

		public ObjectRelatedModelsProviderServiceTrackerCustomizer(
			long companyId) {

			_companyId = companyId;
		}

		@Override
		public ObjectRelatedModelsProvider addingService(
			ServiceReference<ObjectRelatedModelsProvider> serviceReference) {

			ObjectRelatedModelsProvider<ObjectEntry>
				objectRelatedModelsProvider = _bundleContext.getService(
					serviceReference);

			if ((_companyId != objectRelatedModelsProvider.getCompanyId()) ||
				(objectRelatedModelsProvider instanceof
					DeleteOnDisassociateObjectRelatedModelsProvider)) {

				return objectRelatedModelsProvider;
			}

			int serviceRanking = GetterUtil.getInteger(
				serviceReference.getProperty(Constants.SERVICE_RANKING));

			ObjectRelatedModelsProvider<ObjectEntry>
				deleteOnDisassociateObjectRelatedModelsProvider =
					new DeleteOnDisassociateObjectRelatedModelsProvider(
						_objectEntryLocalService, objectRelatedModelsProvider,
						_objectRelationshipLocalService);

			ServiceRegistration<ObjectRelatedModelsProvider<?>>
				serviceRegistration =
					(ServiceRegistration<ObjectRelatedModelsProvider<?>>)
						ObjectRelatedModelsProviderRegistryUtil.register(
							_bundleContext,
							_objectDefinitionLocalService.
								fetchObjectDefinitionByExternalReferenceCode(
									"L_API_PROPERTY",
									objectRelatedModelsProvider.getCompanyId()),
							deleteOnDisassociateObjectRelatedModelsProvider,
							Math.min(Integer.MAX_VALUE, serviceRanking + 100));

			_serviceRegistrations.put(serviceReference, serviceRegistration);

			return deleteOnDisassociateObjectRelatedModelsProvider;
		}

		@Override
		public void modifiedService(
			ServiceReference<ObjectRelatedModelsProvider> serviceReference,
			ObjectRelatedModelsProvider objectRelatedModelsProvider) {

			removedService(serviceReference, objectRelatedModelsProvider);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<ObjectRelatedModelsProvider> serviceReference,
			ObjectRelatedModelsProvider objectRelatedModelsProvider) {

			ServiceRegistration<ObjectRelatedModelsProvider<?>>
				serviceRegistration = _serviceRegistrations.remove(
					serviceReference);

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}

		private final long _companyId;
		private final Map
			<ServiceReference<ObjectRelatedModelsProvider>,
			 ServiceRegistration<ObjectRelatedModelsProvider<?>>>
				_serviceRegistrations = new ConcurrentHashMap<>();

	}

}