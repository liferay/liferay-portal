/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.vulcan.extension.v1_0;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.internal.util.ServiceContextUtil;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectRelationshipElementsParser;
import com.liferay.object.rest.manager.v1_0.ObjectRelationshipElementsParserRegistry;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.PropertyDefinition;
import com.liferay.portal.vulcan.extension.validation.DefaultPropertyValidator;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = ExtensionProvider.class)
public class ObjectRelationshipExtensionProvider
	extends BaseObjectExtensionProvider {

	@Override
	public Map<String, Serializable> getExtendedProperties(
			long companyId, long userId, String className, Object entity)
		throws Exception {

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			companyId, className);

		return NestedFieldsSupplier.supply(
			nestedFieldName -> {
				ObjectRelationship objectRelationship =
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectDefinitionId(
							objectDefinition.getObjectDefinitionId(),
							nestedFieldName);

				if ((objectRelationship == null) ||
					!objectRelationship.isAllowedObjectRelationshipType(
						objectRelationship.getType())) {

					return null;
				}

				ObjectDefinition relatedObjectDefinition =
					ObjectRelationshipUtil.getRelatedObjectDefinition(
						objectDefinition, objectRelationship);

				if (!relatedObjectDefinition.isActive() ||
					relatedObjectDefinition.isUnmodifiableSystemObject()) {

					return null;
				}

				long primaryKey = getPrimaryKey(entity);

				if (_isManyToOneObjectRelationship(
						objectDefinition, objectRelationship,
						relatedObjectDefinition)) {

					DefaultObjectEntryManager defaultObjectEntryManager =
						DefaultObjectEntryManagerProvider.provide(
							_objectEntryManagerRegistry.getObjectEntryManager(
								objectDefinition.getStorageType()));

					return defaultObjectEntryManager.
						fetchRelatedManyToOneObjectEntry(
							_getDefaultDTOConverterContext(
								objectDefinition, primaryKey, null, userId),
							objectDefinition, primaryKey,
							objectRelationship.getName());
				}

				DefaultObjectEntryManager defaultObjectEntryManager =
					DefaultObjectEntryManagerProvider.provide(
						_objectEntryManagerRegistry.getObjectEntryManager(
							objectDefinition.getStorageType()));

				Page<ObjectEntry> relatedObjectEntriesPage =
					defaultObjectEntryManager.
						getObjectEntryRelatedObjectEntries(
							_getDefaultDTOConverterContext(
								objectDefinition, primaryKey, null, userId),
							objectDefinition, primaryKey,
							objectRelationship.getName(),
							Pagination.of(
								QueryUtil.ALL_POS, QueryUtil.ALL_POS));

				return (Serializable)relatedObjectEntriesPage.getItems();
			});
	}

	@Override
	public Map<String, PropertyDefinition> getExtendedPropertyDefinitions(
			long companyId, String className)
		throws Exception {

		Map<String, PropertyDefinition> extendedPropertyDefinitions =
			new HashMap<>();

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			companyId, className);

		for (ObjectRelationship objectRelationship :
				_objectRelationshipLocalService.getAllObjectRelationships(
					objectDefinition.getObjectDefinitionId())) {

			if (!objectRelationship.isAllowedObjectRelationshipType(
					objectRelationship.getType())) {

				continue;
			}

			ObjectDefinition relatedObjectDefinition =
				ObjectRelationshipUtil.getRelatedObjectDefinition(
					objectDefinition, objectRelationship);

			if (!relatedObjectDefinition.isActive() ||
				relatedObjectDefinition.isUnmodifiableSystemObject()) {

				continue;
			}

			extendedPropertyDefinitions.put(
				objectRelationship.getName(),
				new PropertyDefinition(
					Collections.singleton(ObjectEntry.class), null,
					StringUtil.removeFirst(
						relatedObjectDefinition.getName(), "C_"),
					StringBundler.concat(
						"Information about the object relationship ",
						objectRelationship.getName(),
						" can be embedded with \"nestedFields\"."),
					objectRelationship.getName(),
					_getPropertyType(objectDefinition, objectRelationship),
					new DefaultPropertyValidator(), false));
		}

		return extendedPropertyDefinitions;
	}

	@Override
	public void setExtendedProperties(
			long companyId, long userId, String className, Object entity,
			Map<String, Serializable> extendedProperties)
		throws Exception {

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			companyId, className);

		if (objectDefinition == null) {
			throw new IllegalStateException(
				"No object definition exists with class name " + className);
		}

		long primaryKey = getPrimaryKey(entity);

		for (Map.Entry<String, Serializable> entry :
				extendedProperties.entrySet()) {

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					getObjectRelationshipByObjectDefinitionId(
						objectDefinition.getObjectDefinitionId(),
						entry.getKey());

			ObjectDefinition relatedObjectDefinition =
				ObjectRelationshipUtil.getRelatedObjectDefinition(
					objectDefinition, objectRelationship);

			ObjectEntryManager objectEntryManager =
				_objectEntryManagerRegistry.getObjectEntryManager(
					relatedObjectDefinition.getStorageType());

			ObjectRelationshipElementsParser objectRelationshipElementsParser =
				_objectRelationshipElementsParserRegistry.
					getObjectRelationshipElementsParser(
						relatedObjectDefinition.getClassName(),
						relatedObjectDefinition.getCompanyId(),
						objectRelationship.getType());

			List<ObjectEntry> nestedObjectEntries =
				objectRelationshipElementsParser.parse(
					objectRelationship, entry.getValue());

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getStorageType()));

			defaultObjectEntryManager.disassociateRelatedModels(
				_getDefaultDTOConverterContext(
					objectDefinition, primaryKey, null, userId),
				objectDefinition, objectRelationship, primaryKey,
				relatedObjectDefinition, userId);

			for (ObjectEntry nestedObjectEntry : nestedObjectEntries) {
				nestedObjectEntry = objectEntryManager.updateObjectEntry(
					objectDefinition.getCompanyId(),
					_getDefaultDTOConverterContext(
						objectDefinition, primaryKey, null, userId),
					nestedObjectEntry.getExternalReferenceCode(),
					relatedObjectDefinition, nestedObjectEntry,
					relatedObjectDefinition.getScope());

				_relateNestedObjectEntry(
					objectDefinition, objectRelationship, primaryKey,
					nestedObjectEntry.getId(),
					ServiceContextUtil.createServiceContext(
						nestedObjectEntry, userId));
			}

			NestedFieldsSupplier.addNestedField(entry.getKey());
		}
	}

	private DefaultDTOConverterContext _getDefaultDTOConverterContext(
			ObjectDefinition objectDefinition, Long objectEntryId,
			UriInfo uriInfo, Long userId)
		throws Exception {

		User user = null;

		if (Validator.isNotNull(userId)) {
			user = _userLocalService.getUser(userId);
		}

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, null, _dtoConverterRegistry, objectEntryId,
				LocaleUtil.fromLanguageId(
					objectDefinition.getDefaultLanguageId(), true, false),
				uriInfo, user);

		defaultDTOConverterContext.setAttribute("addActions", Boolean.FALSE);

		return defaultDTOConverterContext;
	}

	private PropertyDefinition.PropertyType _getPropertyType(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY) &&
			(objectDefinition.getObjectDefinitionId() ==
				objectRelationship.getObjectDefinitionId2())) {

			return PropertyDefinition.PropertyType.SINGLE_ELEMENT;
		}

		return PropertyDefinition.PropertyType.MULTIPLE_ELEMENT;
	}

	private boolean _isManyToOneObjectRelationship(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship,
		ObjectDefinition relatedObjectDefinition) {

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY) &&
			(objectRelationship.getObjectDefinitionId1() ==
				relatedObjectDefinition.getObjectDefinitionId()) &&
			(objectRelationship.getObjectDefinitionId2() ==
				objectDefinition.getObjectDefinitionId())) {

			return true;
		}

		return false;
	}

	private void _relateNestedObjectEntry(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long primaryKey,
			long relatedPrimaryKey, ServiceContext serviceContext)
		throws Exception {

		long primaryKey1 = relatedPrimaryKey;
		long primaryKey2 = primaryKey;

		if (objectDefinition.getObjectDefinitionId() ==
				objectRelationship.getObjectDefinitionId1()) {

			primaryKey1 = primaryKey;
			primaryKey2 = relatedPrimaryKey;
		}

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationship.getObjectRelationshipId(), primaryKey1,
			primaryKey2, serviceContext);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipElementsParserRegistry
		_objectRelationshipElementsParserRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipService _objectRelationshipService;

	@Reference
	private UserLocalService _userLocalService;

}