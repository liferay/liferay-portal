/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.internal.util.ServiceContextUtil;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 */
@Component(
	factory = "com.liferay.object.rest.internal.resource.v1_0.RelatedObjectEntryResourceImpl",
	property = {"api.version=v1.0", "osgi.jaxrs.resource=true"},
	service = RelatedObjectEntryResourceImpl.class
)
public class RelatedObjectEntryResourceImpl
	extends BaseRelatedObjectEntryResourceImpl {

	@Override
	public void deleteObjectRelationshipMappingTableValues(
			String previousPath, Long objectEntryId,
			String objectRelationshipName, Long relatedObjectEntryId)
		throws Exception {

		ObjectDefinition systemObjectDefinition = _getSystemObjectDefinition(
			previousPath);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				getObjectRelationshipByObjectDefinitionId(
					systemObjectDefinition.getObjectDefinitionId(),
					objectRelationshipName);

		_checkRelatedObjectEntry(
			objectRelationship, relatedObjectEntryId, systemObjectDefinition);

		_checkSystemObjectEntry(objectEntryId, systemObjectDefinition);

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				systemObjectDefinition.getClassName(),
				systemObjectDefinition.getCompanyId(),
				objectRelationship.getType());

		objectRelatedModelsProvider.disassociateRelatedModels(
			GuestOrUserUtil.getUserId(),
			objectRelationship.getObjectRelationshipId(),
			_getPrimaryKey1(
				objectRelationship.getObjectDefinitionId1(), objectEntryId,
				relatedObjectEntryId, systemObjectDefinition),
			_getPrimaryKey2(
				objectRelationship.getObjectDefinitionId1(), objectEntryId,
				relatedObjectEntryId, systemObjectDefinition));
	}

	@Override
	public Page<Object> getRelatedObjectEntriesPage(
			String previousPath, Long objectEntryId,
			String objectRelationshipName, Pagination pagination)
		throws Exception {

		ObjectDefinition systemObjectDefinition = _getSystemObjectDefinition(
			previousPath);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				getObjectRelationshipByObjectDefinitionId(
					systemObjectDefinition.getObjectDefinitionId(),
					objectRelationshipName);

		ObjectDefinition relatedObjectDefinition = _getRelatedObjectDefinition(
			systemObjectDefinition, objectRelationship);

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					systemObjectDefinition.getStorageType()));

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			return defaultObjectEntryManager.getRelatedSystemObjectEntries(
				systemObjectDefinition, objectEntryId, objectRelationshipName,
				pagination);
		}

		return (Page)
			defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
				_getDefaultDTOConverterContext(
					systemObjectDefinition, objectEntryId, _uriInfo),
				systemObjectDefinition, objectEntryId, objectRelationshipName,
				pagination);
	}

	@Override
	public ObjectEntry putObjectRelationshipMappingTableValues(
			String previousPath, Long objectEntryId,
			String objectRelationshipName, Long relatedObjectEntryId,
			Pagination pagination)
		throws Exception {

		ObjectDefinition systemObjectDefinition = _getSystemObjectDefinition(
			previousPath);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				getObjectRelationshipByObjectDefinitionId(
					systemObjectDefinition.getObjectDefinitionId(),
					objectRelationshipName);

		long primaryKey2 = _getPrimaryKey2(
			objectRelationship.getObjectDefinitionId1(), objectEntryId,
			relatedObjectEntryId, systemObjectDefinition);

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationship.getObjectRelationshipId(),
			_getPrimaryKey1(
				objectRelationship.getObjectDefinitionId1(), objectEntryId,
				relatedObjectEntryId, systemObjectDefinition),
			primaryKey2, ServiceContextUtil.createServiceContext(primaryKey2));

		return _getRelatedObjectEntry(
			objectRelationship, relatedObjectEntryId, systemObjectDefinition);
	}

	private void _checkRelatedObjectEntry(
			ObjectRelationship objectRelationship, long relatedObjectEntryId,
			ObjectDefinition systemObjectDefinition)
		throws Exception {

		_getRelatedObjectEntry(
			objectRelationship, relatedObjectEntryId, systemObjectDefinition);
	}

	private void _checkSystemObjectEntry(
			long objectEntryId, ObjectDefinition systemObjectDefinition)
		throws Exception {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinition.getClassName());

		persistedModelLocalService.getPersistedModel(objectEntryId);
	}

	private DefaultDTOConverterContext _getDefaultDTOConverterContext(
		ObjectDefinition objectDefinition, Long objectEntryId,
		UriInfo uriInfo) {

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, objectEntryId,
				LocaleUtil.fromLanguageId(
					objectDefinition.getDefaultLanguageId(), true, false),
				uriInfo, null);

		defaultDTOConverterContext.setAttribute("addActions", Boolean.FALSE);

		return defaultDTOConverterContext;
	}

	private long _getPrimaryKey1(
		long objectDefinitionId, long objectEntryId, long relatedObjectEntryId,
		ObjectDefinition systemObjectDefinition) {

		if (objectDefinitionId ==
				systemObjectDefinition.getObjectDefinitionId()) {

			return objectEntryId;
		}

		return relatedObjectEntryId;
	}

	private long _getPrimaryKey2(
		long objectDefinitionId, long objectEntryId, long relatedObjectEntryId,
		ObjectDefinition systemObjectDefinition) {

		if (objectDefinitionId ==
				systemObjectDefinition.getObjectDefinitionId()) {

			return relatedObjectEntryId;
		}

		return objectEntryId;
	}

	private ObjectDefinition _getRelatedObjectDefinition(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectDefinition relatedObjectDefinition =
			ObjectRelationshipUtil.getRelatedObjectDefinition(
				objectDefinition, objectRelationship);

		if (!relatedObjectDefinition.isActive()) {
			throw new NoSuchObjectDefinitionException(
				"No active object definition found for relationship " +
					objectRelationship.getName());
		}

		return relatedObjectDefinition;
	}

	private ObjectEntry _getRelatedObjectEntry(
			ObjectRelationship objectRelationship, long relatedObjectEntryId,
			ObjectDefinition systemObjectDefinition)
		throws Exception {

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					systemObjectDefinition.getStorageType()));

		ObjectDefinition relatedObjectDefinition = _getRelatedObjectDefinition(
			systemObjectDefinition, objectRelationship);

		return defaultObjectEntryManager.getObjectEntry(
			_getDefaultDTOConverterContext(
				relatedObjectDefinition, relatedObjectEntryId, _uriInfo),
			relatedObjectDefinition, relatedObjectEntryId);
	}

	private ObjectDefinition _getSystemObjectDefinition(String previousPath) {
		long companyId = CompanyThreadLocal.getCompanyId();

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_getSystemObjectDefinitionManager(companyId, previousPath);

		ObjectDefinition systemObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, systemObjectDefinitionManager.getName());

		if (systemObjectDefinition != null) {
			return systemObjectDefinition;
		}

		throw new NotFoundException(
			"No system object definition metadata for name \"" +
				systemObjectDefinitionManager.getName() + "\"");
	}

	private SystemObjectDefinitionManager _getSystemObjectDefinitionManager(
		long companyId, String previousPath) {

		URI uri = _uriInfo.getBaseUri();

		String path = uri.getPath();

		String restContextPath = path.split("/")[2] + "/v1.0/" + previousPath;

		for (ObjectDefinition systemObjectDefinition :
				_objectDefinitionLocalService.
					getUnmodifiableSystemObjectDefinitions(companyId)) {

			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						systemObjectDefinition.getName());

			JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
				systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

			if (StringUtil.equals(
					jaxRsApplicationDescriptor.getRESTContextPath(),
					restContextPath)) {

				return systemObjectDefinitionManager;
			}
		}

		throw new NotFoundException(
			"No system object definition metadata for REST context path \"" +
				restContextPath + "\"");
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipService _objectRelationshipService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Context
	private UriInfo _uriInfo;

}