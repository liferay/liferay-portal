/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.entry.util.ObjectEntryValuesUtil;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryModel;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionModel;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.object.rest.dto.v1_0.AuditEvent;
import com.liferay.object.rest.dto.v1_0.AuditFieldChange;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.Folder;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectDefinitionBrief;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Scope;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.SystemProperties;
import com.liferay.object.rest.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.object.rest.dto.v1_0.Version;
import com.liferay.object.rest.dto.v1_0.util.CreatorUtil;
import com.liferay.object.rest.dto.v1_0.util.LinkUtil;
import com.liferay.object.rest.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.util.ExtensionUtil;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.jaxrs.extension.ExtendedEntity;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier de Arcos
 */
@Component(
	property = {
		"dto.class.name=com.liferay.object.model.ObjectEntry",
		"service.ranking:Integer=100"
	},
	service = DTOConverter.class
)
public class ObjectEntryDTOConverter
	implements DTOConverter<com.liferay.object.model.ObjectEntry, ObjectEntry> {

	public ObjectEntryDTOConverter() {
	}

	public ObjectEntryDTOConverter(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	@Override
	public String getContentType() {
		return ObjectEntry.class.getSimpleName();
	}

	@Override
	public String getDTOClassName() {
		if (_objectDefinition != null) {
			return _objectDefinition.getClassName();
		}

		return DTOConverter.super.getDTOClassName();
	}

	@Override
	public String getExternalDTOClassName() {
		if (_objectDefinition != null) {
			return StringUtil.replace(
				_objectDefinition.getClassName(),
				ObjectDefinition.class.getName(),
				com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition.class.
					getName());
		}

		return DTOConverter.super.getExternalDTOClassName();
	}

	@Override
	public ObjectEntry toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			dtoConverterContext);

		ObjectEntry objectEntry = ObjectEntry.unsafeToDTO(
			(String)dtoConverterContext.getAttribute("payload"));

		objectEntry.setActions(dtoConverterContext::getActions);

		if (objectEntry.getStatus() == null) {
			objectEntry.setStatus(
				() -> {
					User user = dtoConverterContext.getUser();

					return _toStatus(
						user.getLocale(), WorkflowConstants.STATUS_APPROVED);
				});
		}

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		for (ObjectField objectField : objectFields) {
			if (!Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				continue;
			}

			Map<String, Object> properties = objectEntry.getProperties();

			Map<String, String> map = (Map<String, String>)properties.get(
				objectField.getName());

			properties.put(
				objectField.getName(),
				_getListEntry(
					dtoConverterContext, map.get("key"),
					objectField.getListTypeDefinitionId()));

			objectEntry.setProperties(() -> properties);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = _getObjectDefinition(
			dtoConverterContext, serviceBuilderObjectEntry);

		ObjectEntryVersion objectEntryVersion =
			(ObjectEntryVersion)dtoConverterContext.getAttribute(
				"objectEntryVersion");

		ObjectEntry contentObjectEntry = (objectEntryVersion == null) ? null :
			ObjectEntry.unsafeToDTO(objectEntryVersion.getContent());

		ObjectEntry objectEntry = _toSimplifiedObjectEntry(
			contentObjectEntry, objectDefinition, objectEntryVersion,
			serviceBuilderObjectEntry);

		if (GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("simplifiedObjectEntry"))) {

			return objectEntry;
		}

		TrashEntry trashEntry = null;

		if (serviceBuilderObjectEntry.getStatus() ==
				WorkflowConstants.STATUS_IN_TRASH) {

			trashEntry = _trashEntryLocalService.fetchEntry(
				objectDefinition.getClassName(),
				serviceBuilderObjectEntry.getObjectEntryId());
		}

		TrashEntry finalTrashEntry = trashEntry;

		objectEntry.setActions(dtoConverterContext::getActions);
		objectEntry.setAuditEvents(
			() -> _toAuditEvents(
				dtoConverterContext, objectDefinition,
				serviceBuilderObjectEntry));
		objectEntry.setCreator(
			() -> {
				long userId = _getAttribute(
					objectEntryVersion, ObjectEntryVersionModel::getUserId,
					serviceBuilderObjectEntry, ObjectEntryModel::getUserId);

				return CreatorUtil.toCreator(
					_portal, dtoConverterContext.getUriInfo(),
					_userLocalService.fetchUser(userId));
			});
		objectEntry.setDateCreated(
			() -> _getAttribute(
				objectEntryVersion, ObjectEntryVersionModel::getCreateDate,
				serviceBuilderObjectEntry, ObjectEntryModel::getCreateDate));
		objectEntry.setDateModified(
			() -> _getAttribute(
				objectEntryVersion, ObjectEntryVersionModel::getModifiedDate,
				serviceBuilderObjectEntry, ObjectEntryModel::getModifiedDate));
		objectEntry.setDefaultLanguageId(
			() -> {
				if (FeatureFlagManagerUtil.isEnabled(
						objectDefinition.getCompanyId(), "LPD-32050")) {

					return serviceBuilderObjectEntry.getDefaultLanguageId();
				}

				return null;
			});
		objectEntry.setDisplayDate(
			() -> _getAttribute(
				objectEntryVersion, ObjectEntryVersionModel::getDisplayDate,
				serviceBuilderObjectEntry, ObjectEntryModel::getDisplayDate));
		objectEntry.setExpirationDate(
			() -> _getAttribute(
				objectEntryVersion, ObjectEntryVersionModel::getExpirationDate,
				serviceBuilderObjectEntry,
				ObjectEntryModel::getExpirationDate));
		objectEntry.setFriendlyUrlPath(
			() -> serviceBuilderObjectEntry.getURLTitle(
				dtoConverterContext.getLocale()));
		objectEntry.setFriendlyUrlPath_i18n(
			serviceBuilderObjectEntry::getURLTitleMap);
		objectEntry.setId(serviceBuilderObjectEntry::getObjectEntryId);
		objectEntry.setKeywords(
			() -> {
				if (objectEntryVersion != null) {
					return contentObjectEntry.getKeywords();
				}
				else if (!objectDefinition.isEnableCategorization()) {
					return null;
				}

				return ListUtil.toArray(
					_assetTagLocalService.getTags(
						objectDefinition.getClassName(),
						serviceBuilderObjectEntry.getObjectEntryId()),
					AssetTag.NAME_ACCESSOR);
			});
		objectEntry.setObjectEntryFolderExternalReferenceCode(
			() -> {
				ObjectEntryFolder objectEntryFolder =
					_objectEntryFolderLocalService.fetchObjectEntryFolder(
						serviceBuilderObjectEntry.getObjectEntryFolderId());

				if (objectEntryFolder != null) {
					return objectEntryFolder.getExternalReferenceCode();
				}

				return StringPool.BLANK;
			});
		objectEntry.setObjectEntryFolderId(
			serviceBuilderObjectEntry::getObjectEntryFolderId);
		objectEntry.setPermissions(
			() -> _toPermissions(objectDefinition, serviceBuilderObjectEntry));
		objectEntry.setProperties(
			() -> {
				if (objectEntryVersion == null) {
					return _toProperties(
						dtoConverterContext, objectDefinition,
						serviceBuilderObjectEntry);
				}

				Map<String, Object> properties =
					contentObjectEntry.getProperties();

				com.liferay.object.model.ObjectEntry
					clonedServiceBuilderObjectEntry =
						(com.liferay.object.model.ObjectEntry)
							serviceBuilderObjectEntry.clone();

				clonedServiceBuilderObjectEntry.setValues(
					(Map<String, Serializable>)properties.get("properties"));

				return _toProperties(
					dtoConverterContext,
					_objectDefinitionLocalService.getObjectDefinition(
						clonedServiceBuilderObjectEntry.
							getObjectDefinitionId()),
					clonedServiceBuilderObjectEntry);
			});
		objectEntry.setRemovedBy(
			() -> {
				if (finalTrashEntry != null) {
					return CreatorUtil.toCreator(
						_portal, dtoConverterContext.getUriInfo(),
						_userLocalService.fetchUser(
							finalTrashEntry.getUserId()));
				}

				return null;
			});
		objectEntry.setRemovedDate(
			() -> {
				if (finalTrashEntry != null) {
					return finalTrashEntry.getCreateDate();
				}

				return null;
			});
		objectEntry.setReviewDate(
			() -> _getAttribute(
				objectEntryVersion, ObjectEntryVersionModel::getReviewDate,
				serviceBuilderObjectEntry, ObjectEntryModel::getReviewDate));
		objectEntry.setStatus(
			() -> {
				int status = _getAttribute(
					objectEntryVersion, ObjectEntryVersionModel::getStatus,
					serviceBuilderObjectEntry, ObjectEntryModel::getStatus);

				return _toStatus(dtoConverterContext.getLocale(), status);
			});
		objectEntry.setSystemProperties(
			() -> {
				if (objectEntryVersion != null) {
					return _toSystemProperties(
						dtoConverterContext.getLocale(), objectDefinition,
						objectEntryVersion.getVersion());
				}

				return _toSystemProperties(
					dtoConverterContext.getLocale(), objectDefinition,
					serviceBuilderObjectEntry.getVersion());
			});
		objectEntry.setTaxonomyCategoryBriefs(
			() -> {
				if (objectEntryVersion != null) {
					return contentObjectEntry.getTaxonomyCategoryBriefs();
				}
				else if (!objectDefinition.isEnableCategorization()) {
					return null;
				}

				return TransformUtil.transformToArray(
					_assetCategoryLocalService.getCategories(
						objectDefinition.getClassName(),
						serviceBuilderObjectEntry.getObjectEntryId()),
					assetCategory ->
						TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
							assetCategory, dtoConverterContext),
					TaxonomyCategoryBrief.class);
			});

		return objectEntry;
	}

	private void _addManyToOneObjectRelationshipNames(
		ObjectField objectField, String objectFieldName,
		ObjectRelationship objectRelationship, long primaryKey,
		Map<String, UnsafeSupplier<Object, Exception>> unsafeSuppliers,
		Map<String, Serializable> values) {

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectField);

		String relatedObjectEntryERC = GetterUtil.getString(
			values.get(objectRelationshipERCObjectFieldName));

		if (unsafeSuppliers.get(objectRelationship.getName()) == null) {
			unsafeSuppliers.put(
				objectRelationship.getName() + "ERC",
				() -> relatedObjectEntryERC);
		}

		unsafeSuppliers.put(objectFieldName, () -> primaryKey);
		unsafeSuppliers.put(
			objectRelationshipERCObjectFieldName, () -> relatedObjectEntryERC);
	}

	private void _addManyToOneRelatedObjectEntries(
			DTOConverterContext dtoConverterContext, String objectFieldName,
			ObjectRelationship objectRelationship, long primaryKey,
			Map<String, UnsafeSupplier<Object, Exception>> unsafeSuppliers)
		throws Exception {

		String relatedObjectDefinitionName = StringUtil.replaceLast(
			objectFieldName.substring(
				objectFieldName.lastIndexOf(StringPool.UNDERLINE) + 1),
			"Id", "");

		String manyToOneRelationshipName = StringUtil.removeLast(
			objectFieldName, "Id");

		AtomicReference<Serializable> relatedObjectEntryAtomicReference =
			new AtomicReference<>();

		Map<String, Serializable> nestedFieldValues =
			NestedFieldsSupplier.supply(
				nestedFieldName -> {
					if (!nestedFieldName.contains(
							relatedObjectDefinitionName) &&
						!StringUtil.equals(
							nestedFieldName, objectRelationship.getName())) {

						return null;
					}

					if (!StringUtil.equals(
							nestedFieldName, manyToOneRelationshipName) &&
						!StringUtil.equals(
							nestedFieldName, objectRelationship.getName()) &&
						!StringUtil.equals(
							nestedFieldName, relatedObjectDefinitionName) &&
						_log.isWarnEnabled()) {

						_log.warn(
							StringBundler.concat(
								"Replace the deprecated nested field \"",
								nestedFieldName, "\" with \"",
								objectRelationship.getName()));
					}

					if (relatedObjectEntryAtomicReference.get() != null) {
						return relatedObjectEntryAtomicReference.get();
					}

					ObjectDefinition objectDefinition =
						_objectDefinitionLocalService.getObjectDefinition(
							objectRelationship.getObjectDefinitionId1());

					if (objectDefinition.isUnmodifiableSystemObject()) {
						SystemObjectDefinitionManager
							systemObjectDefinitionManager =
								_systemObjectDefinitionManagerRegistry.
									getSystemObjectDefinitionManager(
										objectDefinition.getName());

						BaseModel<?> baseModel =
							systemObjectDefinitionManager.
								getBaseModelByExternalReferenceCode(
									systemObjectDefinitionManager.
										getBaseModelExternalReferenceCode(
											primaryKey),
									objectDefinition.getCompanyId());

						Map<String, Object> values =
							ObjectEntryDTOConverterUtil.toValues(
								baseModel,
								dtoConverterContext.getDTOConverterRegistry(),
								objectDefinition.getName(),
								_systemObjectDefinitionManagerRegistry,
								dtoConverterContext.getUser());

						if (MapUtil.isNotEmpty(values)) {
							ObjectField objectField =
								_objectFieldLocalService.fetchObjectField(
									objectDefinition.getTitleObjectFieldId());

							if (objectField == null) {
								objectField =
									_objectFieldLocalService.getObjectField(
										objectDefinition.
											getObjectDefinitionId(),
										"id");
							}

							values.put(
								objectField.getName(),
								ObjectEntryValuesUtil.getTitleFieldValue(
									objectField.getBusinessType(),
									baseModel.getModelAttributes(), objectField,
									dtoConverterContext.getUser(), values));

							values.putAll(
								_objectEntryLocalService.
									getExtensionDynamicObjectDefinitionTableValues(
										objectDefinition, primaryKey));
						}

						relatedObjectEntryAtomicReference.set(
							(Serializable)values);
					}
					else {
						com.liferay.object.model.ObjectEntry
							serviceBuilderObjectEntry =
								_objectEntryLocalService.getObjectEntry(
									primaryKey);

						if (GetterUtil.getBoolean(
								dtoConverterContext.getAttribute(
									"preferApproved")) &&
							!serviceBuilderObjectEntry.isApproved()) {

							serviceBuilderObjectEntry =
								_objectEntryLocalService.
									fetchObjectEntryByHeadObjectEntryId(
										primaryKey);
						}

						relatedObjectEntryAtomicReference.set(
							toDTO(
								_getDTOConverterContext(
									dtoConverterContext,
									serviceBuilderObjectEntry.
										getObjectEntryId()),
								serviceBuilderObjectEntry));
					}

					return relatedObjectEntryAtomicReference.get();
				});

		if (nestedFieldValues == null) {
			return;
		}

		for (Map.Entry<String, Serializable> entry :
				nestedFieldValues.entrySet()) {

			String nestedFieldName = entry.getKey();

			if (StringUtil.equals(
					nestedFieldName, objectRelationship.getName())) {

				unsafeSuppliers.put(
					objectRelationship.getName(), entry::getValue);
			}

			if (nestedFieldName.contains(relatedObjectDefinitionName) ||
				StringUtil.equals(
					nestedFieldName, objectRelationship.getName())) {

				unsafeSuppliers.put(manyToOneRelationshipName, entry::getValue);
			}
		}
	}

	private Assignee _getAssignee(Map<String, Serializable> assigneeMap) {
		if (assigneeMap.containsKey("externalReferenceCode")) {
			return Assignee.toDTO(_jsonFactory.looseSerializeDeep(assigneeMap));
		}

		String className = _portal.fetchClassName(
			MapUtil.getLong(assigneeMap, "classNameId"));

		if (StringUtil.equals(className, Role.class.getName())) {
			Role role = _roleLocalService.fetchRole(
				MapUtil.getLong(assigneeMap, "classPK"));

			if (role == null) {
				return new Assignee();
			}

			return new Assignee() {
				{
					setExternalReferenceCode(role::getExternalReferenceCode);
					setName(role::getName);
					setType(() -> Type.ROLE);
				}
			};
		}
		else if (StringUtil.equals(className, User.class.getName())) {
			User user = _userLocalService.fetchUser(
				MapUtil.getLong(assigneeMap, "classPK"));

			if (user == null) {
				return new Assignee();
			}

			return new Assignee() {
				{
					setExternalReferenceCode(user::getExternalReferenceCode);
					setName(user::getFullName);
					setType(() -> Type.USER);
				}
			};
		}

		return new Assignee();
	}

	private <T> T _getAttribute(
		ObjectEntryVersion objectEntryVersion,
		Function<ObjectEntryVersion, T> objectEntryVersionGetterFunction,
		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
		Function<com.liferay.object.model.ObjectEntry, T>
			serviceBuilderObjectEntryGetterFunction) {

		if (objectEntryVersion != null) {
			return objectEntryVersionGetterFunction.apply(objectEntryVersion);
		}

		return serviceBuilderObjectEntryGetterFunction.apply(
			serviceBuilderObjectEntry);
	}

	private String _getDateString(
		ObjectField objectField, Timestamp timestamp) {

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_DATE) ||
			StringUtil.equals(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_TIME_STORAGE, objectField),
				ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC)) {

			pattern += "'Z'";
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		return simpleDateFormat.format(timestamp);
	}

	private DTOConverterContext _getDTOConverterContext(
		DTOConverterContext dtoConverterContext, long objectEntryId) {

		UriInfo uriInfo = dtoConverterContext.getUriInfo();

		DTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				dtoConverterContext.isAcceptAllLanguages(), null,
				dtoConverterContext.getDTOConverterRegistry(),
				dtoConverterContext.getHttpServletRequest(), objectEntryId,
				dtoConverterContext.getLocale(), uriInfo,
				dtoConverterContext.getUser());

		defaultDTOConverterContext.setAttribute(
			"preferApproved",
			GetterUtil.getBoolean(
				dtoConverterContext.getAttribute("preferApproved")));

		if (ExportImportThreadLocal.isExportInProcess()) {
			defaultDTOConverterContext.setAttribute(
				"simplifiedObjectEntry", Boolean.TRUE);
		}

		return defaultDTOConverterContext;
	}

	private FileEntry _getFileEntry(
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry,
			ObjectField objectField, long fileEntryId, String objectFieldName)
		throws Exception {

		FileEntry fileEntry = new FileEntry();

		DLFileEntry dlFileEntry = _dLFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry == null) {
			return fileEntry;
		}

		LiferayFileEntry liferayFileEntry = new LiferayFileEntry(dlFileEntry);

		FileVersion fileVersion = liferayFileEntry.getFileVersion();

		fileEntry.setAlternativeText(fileVersion::getDescription);

		fileEntry.setExternalReferenceCode(
			dlFileEntry::getExternalReferenceCode);

		fileEntry.setFileBase64(
			() -> (String)NestedFieldsSupplier.supply(
				objectFieldName + ".fileBase64",
				fieldName -> Base64.encode(
					_file.getBytes(dlFileEntry.getContentStream()))));
		fileEntry.setFileURL(
			() -> {
				if (!Objects.equals(
						ObjectFieldSettingConstants.VALUE_USER_COMPUTER,
						ObjectFieldSettingUtil.getValue(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE,
							objectField)) ||
					GetterUtil.getBoolean(
						ObjectFieldSettingUtil.getValue(
							ObjectFieldSettingConstants.
								NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
							objectField.getObjectFieldSettings()))) {

					return null;
				}

				return _exportImportAttachmentManager.getFileURL(dlFileEntry);
			});
		fileEntry.setFolder(
			() -> (Folder)NestedFieldsSupplier.supply(
				objectFieldName + ".folder",
				fieldName -> {
					if (!Objects.equals(
							ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA,
							ObjectFieldSettingUtil.getValue(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE,
								objectField))) {

						return null;
					}

					Folder folder = new Folder();

					folder.setExternalReferenceCode(
						() -> {
							if (dlFileEntry.getFolderId() == 0) {
								return null;
							}

							DLFolder dlFolder = dlFileEntry.getFolder();

							return dlFolder.getExternalReferenceCode();
						});
					folder.setSiteId(dlFileEntry::getGroupId);

					return folder;
				}));

		fileEntry.setId(dlFileEntry::getFileEntryId);
		fileEntry.setLink(
			() -> LinkUtil.toLink(
				_dlAppService, dlFileEntry, _dlURLHelper,
				objectEntry.getGroupId(),
				objectDefinition.getExternalReferenceCode(),
				objectEntry.getExternalReferenceCode(), _portal));
		fileEntry.setMetadata(
			() -> NestedFieldsSupplier.supply(
				objectFieldName + ".metadata",
				fieldName -> {
					if ((fileVersion == null) || (fileVersion.getSize() == 0) ||
						(!PDFProcessorUtil.hasImages(fileVersion) &&
						 !PDFProcessorUtil.isDocumentSupported(
							 fileVersion.getMimeType()))) {

						return null;
					}

					return HashMapBuilder.<String, Object>put(
						"numberOfPages",
						PDFProcessorUtil.getPreviewFileCount(fileVersion)
					).build();
				}));
		fileEntry.setMimeType(dlFileEntry::getMimeType);
		fileEntry.setName(dlFileEntry::getFileName);
		fileEntry.setPreviewURL(
			() -> NestedFieldsSupplier.supply(
				objectFieldName + ".previewURL",
				fieldName -> {
					String previewURL = _getPreviewURL(liferayFileEntry);

					if (Validator.isNull(previewURL)) {
						return null;
					}

					return previewURL;
				}));
		fileEntry.setScope(
			() -> {
				if ((objectEntry.getGroupId() == dlFileEntry.getGroupId()) &&
					!Objects.equals(
						objectDefinition.getScope(),
						ObjectDefinitionConstants.SCOPE_COMPANY)) {

					return null;
				}

				Scope scope = new Scope();

				Group group = _groupLocalService.getGroup(
					dlFileEntry.getGroupId());

				scope.setExternalReferenceCode(group::getExternalReferenceCode);
				scope.setType(
					() -> {
						if (group.getType() == GroupConstants.TYPE_DEPOT) {
							return Scope.Type.ASSET_LIBRARY;
						}

						return Scope.Type.SITE;
					});

				return scope;
			});
		fileEntry.setThumbnailURL(
			() -> NestedFieldsSupplier.supply(
				objectFieldName + ".thumbnailURL",
				fieldName -> {
					String thumbnailURL = _dlURLHelper.getThumbnailSrc(
						new LiferayFileEntry(dlFileEntry), null);

					if (Validator.isNull(thumbnailURL)) {
						return null;
					}

					return thumbnailURL;
				}));

		return fileEntry;
	}

	private ListEntry _getListEntry(
		DTOConverterContext dtoConverterContext, String key,
		long listTypeDefinitionId) {

		if (StringUtil.equals(key, StringPool.BLANK)) {
			return new ListEntry() {
				{
					setKey(() -> StringPool.BLANK);
				}
			};
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinitionId, key);

		if (listTypeEntry == null) {
			return null;
		}

		return new ListEntry() {
			{
				setKey(listTypeEntry::getKey);
				setName(
					() -> listTypeEntry.getName(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						listTypeEntry.getNameMap()));
			}
		};
	}

	private Serializable _getLocalizedValue(
			DTOConverterContext dtoConverterContext, Long groupId,
			Map<String, Serializable> objectField_i18n)
		throws Exception {

		Serializable serializable = objectField_i18n.get(
			String.valueOf(dtoConverterContext.getLocale()));

		if (Validator.isNotNull(serializable)) {
			return serializable;
		}

		User user = dtoConverterContext.getUser();

		if (user != null) {
			serializable = objectField_i18n.get(
				String.valueOf(user.getLocale()));

			if (Validator.isNotNull(serializable)) {
				return serializable;
			}
		}

		return objectField_i18n.get(
			String.valueOf(_portal.getSiteDefaultLocale(groupId)));
	}

	private Map<String, UnsafeSupplier<Object, Exception>>
			_getNestedFieldsRelatedProperties(
				DTOConverterContext dtoConverterContext, long groupId,
				ObjectDefinition objectDefinition, long primaryKey)
		throws Exception {

		return NestedFieldsSupplier.supplyUnsafeSupplier(
			nestedFieldName -> {
				ObjectRelationship objectRelationship =
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectDefinitionId1(
							objectDefinition.getObjectDefinitionId(),
							nestedFieldName);

				if ((objectRelationship == null) ||
					!objectRelationship.isAllowedObjectRelationshipType(
						objectRelationship.getType())) {

					return null;
				}

				ObjectDefinition relatedObjectDefinition =
					_objectDefinitionLocalService.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2());

				if (!relatedObjectDefinition.isActive()) {
					return null;
				}

				ObjectRelatedModelsProvider objectRelatedModelsProvider =
					_objectRelatedModelsProviderRegistry.
						getObjectRelatedModelsProvider(
							relatedObjectDefinition.getClassName(),
							relatedObjectDefinition.getCompanyId(),
							objectRelationship.getType());

				long relatedObjectDefinitionGroupId = groupId;

				if (Objects.equals(
						relatedObjectDefinition.getScope(),
						ObjectDefinitionConstants.SCOPE_COMPANY)) {

					relatedObjectDefinitionGroupId = 0;
				}

				List<?> relatedModels =
					objectRelatedModelsProvider.getRelatedModels(
						relatedObjectDefinitionGroupId,
						objectRelationship.getObjectRelationshipId(), null,
						GetterUtil.getBoolean(
							dtoConverterContext.getAttribute("preferApproved")),
						primaryKey, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						null);

				if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
					SystemObjectDefinitionManager
						systemObjectDefinitionManager =
							_systemObjectDefinitionManagerRegistry.
								getSystemObjectDefinitionManager(
									relatedObjectDefinition.getName());

					return () -> TransformUtil.transformToArray(
						relatedModels,
						relatedModel -> _toExtendedEntity(
							(BaseModel<?>)relatedModel, dtoConverterContext,
							relatedObjectDefinition,
							systemObjectDefinitionManager),
						Object.class);
				}

				return () -> TransformUtil.transformToArray(
					relatedModels,
					relatedModel -> {
						com.liferay.object.model.ObjectEntry objectEntry =
							(com.liferay.object.model.ObjectEntry)relatedModel;

						return toDTO(
							_getDTOConverterContext(
								dtoConverterContext,
								objectEntry.getObjectEntryId()),
							objectEntry);
					},
					ObjectEntry.class);
			});
	}

	private ObjectDefinition _getObjectDefinition(
		DTOConverterContext dtoConverterContext) {

		if (_objectDefinition != null) {
			return _objectDefinition;
		}

		return (ObjectDefinition)dtoConverterContext.getAttribute(
			"objectDefinition");
	}

	private ObjectDefinition _getObjectDefinition(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectEntry objectEntry)
		throws Exception {

		if (_objectDefinition != null) {
			return _objectDefinition;
		}

		ObjectDefinition objectDefinition =
			(ObjectDefinition)dtoConverterContext.getAttribute(
				"objectDefinition");

		if (objectDefinition == null) {
			objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId());
		}

		return objectDefinition;
	}

	private String _getPreviewURL(LiferayFileEntry liferayFileEntry)
		throws PortalException {

		if (StringUtil.startsWith(liferayFileEntry.getMimeType(), "video/")) {
			return _dlURLHelper.getPreviewURL(
				liferayFileEntry, liferayFileEntry.getFileVersion(), null,
				"&videoEmbed=true", true, true);
		}

		return _dlURLHelper.getPreviewURL(
			liferayFileEntry, liferayFileEntry.getFileVersion(), null,
			StringPool.BLANK, false, false);
	}

	private String _getScopeKey(
		ObjectDefinition objectDefinition,
		com.liferay.object.model.ObjectEntry objectEntry) {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (!objectScopeProvider.isGroupAware()) {
			return null;
		}

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		if (group == null) {
			return null;
		}

		return group.getGroupKey();
	}

	private Serializable _getValue(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry,
			ObjectField objectField, Serializable serializable)
		throws Exception {

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {

			if (serializable instanceof Assignee) {
				return serializable;
			}
			else if (serializable instanceof Map) {
				return _getAssignee((Map<String, Serializable>)serializable);
			}

			return null;
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			if (serializable instanceof FileEntry) {
				return serializable;
			}

			long fileEntryId = 0;

			if (serializable instanceof Long) {
				fileEntryId = GetterUtil.getLong(serializable);
			}
			else if (serializable instanceof Map) {
				Map<String, Serializable> map =
					(Map<String, Serializable>)serializable;

				fileEntryId = GetterUtil.getLong(map.get("id"));
			}

			if (fileEntryId == 0) {
				return null;
			}

			return _getFileEntry(
				objectDefinition, objectEntry, objectField, fileEntryId,
				objectField.getName());
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE) ||
				 objectField.compareBusinessType(
					 ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			if (Validator.isNull(serializable)) {
				return null;
			}

			if (serializable instanceof String) {
				Date date = DateUtil.parseDate(
					"yyyy-MM-dd", (String)serializable,
					LocaleUtil.getSiteDefault());

				serializable = new Timestamp(date.getTime());
			}

			Timestamp timestamp = (Timestamp)serializable;

			if (timestamp == null) {
				return null;
			}

			return _getDateString(objectField, timestamp);
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			if (objectField.getListTypeDefinitionId() == 0) {
				return null;
			}

			if (serializable instanceof List) {
				return serializable;
			}

			return (Serializable)TransformUtil.transformToList(
				StringUtil.split(
					(String)serializable, StringPool.COMMA_AND_SPACE),
				key -> _getListEntry(
					dtoConverterContext, key,
					objectField.getListTypeDefinitionId()));
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			if (objectField.getListTypeDefinitionId() == 0) {
				return null;
			}

			if (serializable instanceof ListEntry) {
				return serializable;
			}

			String key = null;

			if (serializable instanceof Map) {
				key = MapUtil.getString(
					(Map<String, String>)serializable, "key");
			}
			else if (serializable instanceof String) {
				key = (String)serializable;
			}

			return _getListEntry(
				dtoConverterContext, key,
				objectField.getListTypeDefinitionId());
		}

		return serializable;
	}

	private boolean _hasRootModelHierarchyNestedField() {
		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if ((nestedFieldsContext != null) &&
			ListUtil.exists(
				nestedFieldsContext.getNestedFields(),
				nestedFieldName -> StringUtil.equals(
					nestedFieldName, "rootModelHierarchy"))) {

			return true;
		}

		return false;
	}

	private AuditEvent[] _toAuditEvents(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry)
		throws Exception {

		return NestedFieldsSupplier.supply(
			"auditEvents",
			nestedFieldNames -> {
				if (!objectDefinition.isEnableObjectEntryHistory() ||
					!_objectEntryService.hasModelResourcePermission(
						objectDefinition.getObjectDefinitionId(),
						objectEntry.getObjectEntryId(),
						ObjectActionKeys.OBJECT_ENTRY_HISTORY)) {

					return null;
				}

				return TransformUtil.transformToArray(
					_auditEventLocalService.getAuditEvents(
						0, 0, 0, null, null, null, null, null,
						String.valueOf(objectEntry.getObjectEntryId()), null,
						null, null, 0, null, false, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS),
					auditEvent -> {
						AuditEvent newAuditEvent = new AuditEvent();

						newAuditEvent.setAuditFieldChanges(
							() -> _toAuditFieldChanges(
								auditEvent.getAdditionalInfo(),
								auditEvent.getEventType()));
						newAuditEvent.setCreator(
							() -> CreatorUtil.toCreator(
								_portal, dtoConverterContext.getUriInfo(),
								_userLocalService.fetchUser(
									auditEvent.getUserId())));
						newAuditEvent.setDateCreated(auditEvent::getCreateDate);
						newAuditEvent.setEventType(auditEvent::getEventType);

						return newAuditEvent;
					},
					AuditEvent.class);
			});
	}

	private AuditFieldChange[] _toAuditFieldChanges(
			String additionalInfo, String eventType)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(additionalInfo);

		if (StringUtil.equals(eventType, EventTypes.ADD)) {
			Map<String, Object> map = jsonObject.toMap();

			return TransformUtil.transformToArray(
				map.keySet(),
				key -> new AuditFieldChange() {
					{
						setName(() -> key);
						setNewValue(() -> map.get(key));
					}
				},
				AuditFieldChange.class);
		}

		return JSONUtil.toArray(
			jsonObject.getJSONArray("attributes"),
			attributeJSONObject -> new AuditFieldChange() {
				{
					setName(() -> attributeJSONObject.getString("name"));
					setNewValue(() -> attributeJSONObject.get("newValue"));
					setOldValue(() -> attributeJSONObject.get("oldValue"));
				}
			},
			AuditFieldChange.class);
	}

	private ExtendedEntity _toExtendedEntity(
			BaseModel<?> baseModel, DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws Exception {

		DTOConverter<BaseModel<?>, ?> dtoConverter =
			ObjectEntryDTOConverterUtil.getDTOConverter(
				dtoConverterContext.getDTOConverterRegistry(),
				systemObjectDefinitionManager);

		Object dto = ObjectEntryDTOConverterUtil.toDTO(
			baseModel, dtoConverterContext.getDTOConverterRegistry(),
			systemObjectDefinitionManager, dtoConverterContext.getUser());

		Map<String, Serializable> nestedFieldsRelatedProperties = null;

		EntityExtensionHandler entityExtensionHandler =
			ExtensionUtil.getEntityExtensionHandler(
				dtoConverter.getExternalDTOClassName(),
				objectDefinition.getCompanyId(), _extensionProviderRegistry);

		if (entityExtensionHandler != null) {
			nestedFieldsRelatedProperties =
				entityExtensionHandler.getExtendedProperties(
					objectDefinition.getCompanyId(),
					dtoConverterContext.getUserId(), dto);
		}

		return ExtendedEntity.extend(dto, nestedFieldsRelatedProperties, null);
	}

	private ObjectDefinitionBrief _toObjectDefinitionBrief(
		Locale locale, ObjectDefinition objectDefinition) {

		return new ObjectDefinitionBrief() {
			{
				setExternalReferenceCode(
					objectDefinition::getExternalReferenceCode);
				setLabel(() -> objectDefinition.getLabel(locale));
				setObjectFolderExternalReferenceCode(
					objectDefinition::getObjectFolderExternalReferenceCode);
			}
		};
	}

	private Permission[] _toPermissions(
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry)
		throws Exception {

		return NestedFieldsSupplier.supply(
			"permissions",
			nestedFieldNames -> {
				_permissionService.checkPermission(
					objectEntry.getGroupId(), objectDefinition.getClassName(),
					objectEntry.getObjectEntryId());

				Collection<Permission> permissions =
					PermissionUtil.getPermissions(
						objectDefinition.getCompanyId(),
						_resourceActionLocalService.getResourceActions(
							objectDefinition.getClassName()),
						objectEntry.getObjectEntryId(),
						objectDefinition.getClassName(), null);

				return permissions.toArray(new Permission[0]);
			});
	}

	private Map<String, Object> _toProperties(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectEntry objectEntry)
		throws Exception {

		Map<String, UnsafeSupplier<Object, Exception>> unsafeSuppliers =
			new HashMap<>();

		Map<String, Serializable> values = objectEntry.getValues();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		for (ObjectField objectField : objectFields) {
			if (objectField.isMetadata()) {
				continue;
			}

			String objectFieldName = objectField.getName();

			Serializable serializable = values.get(objectFieldName);

			if (objectField.isLocalized()) {
				String i18nObjectFieldName =
					objectField.getI18nObjectFieldName();

				Map<String, Serializable> objectField_i18n =
					(Map<String, Serializable>)values.get(i18nObjectFieldName);

				if (objectField_i18n != null) {
					serializable = _getLocalizedValue(
						dtoConverterContext, objectEntry.getGroupId(),
						objectField_i18n);

					if (Objects.equals(
							objectField.getDBType(),
							ObjectFieldConstants.DB_TYPE_BLOB) ||
						Objects.equals(
							objectField.getDBType(),
							ObjectFieldConstants.DB_TYPE_CLOB) ||
						Objects.equals(
							objectField.getDBType(),
							ObjectFieldConstants.DB_TYPE_STRING)) {

						serializable = GetterUtil.getString(serializable);
					}

					for (Map.Entry<String, Serializable> entry :
							objectField_i18n.entrySet()) {

						objectField_i18n.put(
							entry.getKey(),
							_getValue(
								dtoConverterContext, objectDefinition,
								objectEntry, objectField, entry.getValue()));
					}
				}

				unsafeSuppliers.put(
					i18nObjectFieldName, () -> objectField_i18n);
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

				Serializable finalSerializable = serializable;

				unsafeSuppliers.put(objectFieldName, () -> finalSerializable);
				unsafeSuppliers.put(
					objectFieldName + "RawText",
					() -> HtmlParserUtil.extractText(
						GetterUtil.getString(finalSerializable)));
			}
			else if (Objects.equals(
						objectField.getRelationshipType(),
						ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				long primaryKey = GetterUtil.getLong(serializable);

				ObjectRelationship objectRelationship =
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectFieldId2(
							objectField.getObjectFieldId());

				if ((primaryKey == 0) && objectRelationship.isEdge()) {
					continue;
				}

				if ((primaryKey > 0) &&
					(!_hasRootModelHierarchyNestedField() ||
					 !objectRelationship.isEdge())) {

					_addManyToOneRelatedObjectEntries(
						dtoConverterContext, objectFieldName,
						objectRelationship, primaryKey, unsafeSuppliers);
				}

				_addManyToOneObjectRelationshipNames(
					objectField, objectFieldName, objectRelationship,
					primaryKey, unsafeSuppliers, values);
			}
			else {
				Serializable finalSerializable = serializable;

				unsafeSuppliers.put(
					objectFieldName,
					() -> _getValue(
						dtoConverterContext, objectDefinition, objectEntry,
						objectField, finalSerializable));
			}
		}

		values.remove(objectDefinition.getPKObjectFieldName());

		Map<String, UnsafeSupplier<Object, Exception>>
			nestedFieldsRelatedProperties = _getNestedFieldsRelatedProperties(
				dtoConverterContext, objectEntry.getGroupId(), objectDefinition,
				objectEntry.getHeadObjectEntryId());

		if (nestedFieldsRelatedProperties != null) {
			unsafeSuppliers.putAll(nestedFieldsRelatedProperties);
		}

		return (Map<String, Object>)(Map)unsafeSuppliers;
	}

	/**
	 * @see com.liferay.exportimport.internal.data.handler.test.BatchEnginePortletDataHandlerTest#_toSimplifiedObjectEntryJSONObject(
	 *      Group, com.liferay.object.model.ObjectEntry, String)
	 */
	private ObjectEntry _toSimplifiedObjectEntry(
		ObjectEntry contentObjectEntry, ObjectDefinition objectDefinition,
		ObjectEntryVersion objectEntryVersion,
		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry) {

		return new ObjectEntry() {
			{
				setExternalReferenceCode(
					() -> {
						if (objectEntryVersion != null) {
							return contentObjectEntry.
								getExternalReferenceCode();
						}

						return serviceBuilderObjectEntry.
							getExternalReferenceCode();
					});
				setScopeId(serviceBuilderObjectEntry::getGroupId);
				setScopeKey(
					() -> _getScopeKey(
						objectDefinition, serviceBuilderObjectEntry));
			}
		};
	}

	private Status _toStatus(Locale locale, int status) {
		return new Status() {
			{
				setCode(() -> status);
				setLabel(() -> WorkflowConstants.getStatusLabel(status));
				setLabel_i18n(
					() -> _language.get(
						LanguageResources.getResourceBundle(locale),
						WorkflowConstants.getStatusLabel(status)));
			}
		};
	}

	private SystemProperties _toSystemProperties(
			Locale locale, ObjectDefinition objectDefinition, int versionInt)
		throws Exception {

		boolean enableObjectEntryVersioning =
			objectDefinition.isEnableObjectEntryVersioning();
		ObjectDefinitionBrief objectDefinitionBrief =
			NestedFieldsSupplier.supply(
				"systemProperties.objectDefinitionBrief",
				nestedField -> _toObjectDefinitionBrief(
					locale, objectDefinition));

		if (!enableObjectEntryVersioning && (objectDefinitionBrief == null)) {
			return null;
		}

		SystemProperties systemProperties = new SystemProperties();

		if (objectDefinitionBrief != null) {
			systemProperties.setObjectDefinitionBrief(
				() -> objectDefinitionBrief);
		}

		if (enableObjectEntryVersioning) {
			systemProperties.setVersion(
				() -> new Version() {
					{
						setNumber(() -> versionInt);
					}
				});
		}

		return systemProperties;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryDTOConverter.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private AuditEventLocalService _auditEventLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryLocalService _dLFileEntryLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ExportImportAttachmentManager _exportImportAttachmentManager;

	@Reference
	private ExtensionProviderRegistry _extensionProviderRegistry;

	@Reference
	private File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private ObjectDefinition _objectDefinition;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}