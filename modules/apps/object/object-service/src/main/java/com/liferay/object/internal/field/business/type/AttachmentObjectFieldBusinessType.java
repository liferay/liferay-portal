/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.Folder;
import com.liferay.object.rest.dto.v1_0.util.LinkUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.extension.PropertyDefinition;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.scope.Scope;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
	service = ObjectFieldBusinessType.class
)
public class AttachmentObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
			ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP,
			ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.ATTACHMENT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale, "upload-files-or-select-from-documents-and-media");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (objectField.isLocalized()) {
			return getLocalizedValues(objectField, userId, values);
		}

		return super.getDisplayContextValue(objectField, userId, values);
	}

	@Override
	public Serializable getDTOValue(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectField objectField, Serializable serializable)
		throws Exception {

		if (serializable instanceof FileEntry) {
			return serializable;
		}

		long fileEntryId = 0;

		if (serializable instanceof Long) {
			fileEntryId = GetterUtil.getLong(serializable);
		}
		else if (serializable instanceof Map) {
			fileEntryId = MapUtil.getLong(
				(Map<String, Serializable>)serializable, "id");
		}

		if (fileEntryId == 0) {
			return null;
		}

		DLFileEntry dlFileEntry = _dLFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry == null) {
			return new FileEntry();
		}

		LiferayFileEntry liferayFileEntry = new LiferayFileEntry(dlFileEntry);

		return new FileEntry() {
			{
				setAlternativeText(
					() -> {
						FileVersion fileVersion =
							liferayFileEntry.getFileVersion();

						return fileVersion.getDescription();
					});
				setExtension(dlFileEntry::getExtension);
				setExternalReferenceCode(dlFileEntry::getExternalReferenceCode);
				setFileBase64(() -> _getFileBase64(dlFileEntry, objectField));
				setFileURL(() -> _getFileURL(dlFileEntry, objectField));
				setFolder(() -> _getFolder(dlFileEntry, objectField));
				setId(dlFileEntry::getFileEntryId);
				setLink(
					() -> LinkUtil.toLink(
						_dlAppService, dlFileEntry, _dlURLHelper,
						objectEntry.getGroupId(),
						objectDefinition.getExternalReferenceCode(),
						objectEntry, _objectEntryService, objectField,
						GuestOrUserUtil.getPermissionChecker(), _portal));
				setMetadata(
					() -> _getMetadata(
						liferayFileEntry.getFileVersion(),
						dtoConverterContext.getLocale(), objectField));
				setMimeType(dlFileEntry::getMimeType);
				setName(dlFileEntry::getFileName);
				setPreviewURL(
					() -> _getPreviewURL(liferayFileEntry, objectField));
				setScope(
					() -> _getScope(
						dlFileEntry, objectDefinition, objectEntry));
				setSize(
					() -> LanguageUtil.formatStorageSize(
						dlFileEntry.getSize(),
						dtoConverterContext.getLocale()));
				setThumbnailURL(
					() -> _getThumbnailURL(dlFileEntry, objectField));
			}
		};
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "attachment");
	}

	@Override
	public Map<String, Object> getLocalizedValues(
			ObjectField objectField, Long userId, Map<String, Object> values)
		throws PortalException {

		Map<String, Object> localizedValues = super.getLocalizedValues(
			objectField, userId, values);

		if (localizedValues == null) {
			return null;
		}

		for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
			localizedValues.put(
				entry.getKey(), _getFileEntryId(entry.getValue()));
		}

		return localizedValues;
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		Map<String, Object> properties = super.getProperties(
			objectField, objectFieldRenderingContext);

		properties.remove(
			ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY);
		properties.remove(
			ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH);

		return HashMapBuilder.<String, Object>put(
			"groupAware",
			() -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.getObjectDefinition(
						objectField.getObjectDefinitionId());

				ObjectScopeProvider objectScopeProvider =
					_objectScopeProviderRegistry.getObjectScopeProvider(
						objectDefinition.getScope());

				return objectScopeProvider.isGroupAware();
			}
		).put(
			"objectFieldId", objectField.getObjectFieldId()
		).put(
			"portletId", objectFieldRenderingContext.getPortletId()
		).putAll(
			properties
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.LONG;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS,
			ObjectFieldSettingConstants.NAME_FILE_SOURCE,
			ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE);
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		return _getFileEntryId(
			super.getValue(groupId, objectField, userId, values));
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return objectDefinition.isDefaultStorageType();
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		Map<String, String> objectFieldSettingsValues =
			getObjectFieldSettingsValues(objectFieldSettings);

		String fileSource = objectFieldSettingsValues.get(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE);

		if (Objects.equals(
				fileSource,
				ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT) ||
			Objects.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA)) {

			validateNotAllowedObjectFieldSettingNames(
				SetUtil.fromArray(
					ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
					ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP,
					ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH),
				objectField.getName(), objectFieldSettingsValues);
		}
		else if (Objects.equals(
					fileSource,
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT)) {

			validateRelatedObjectFieldSettings(
				objectField,
				ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
				ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP,
				objectFieldSettingsValues);
			validateRelatedObjectFieldSettings(
				objectField,
				ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
				ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
				objectFieldSettingsValues);
		}
		else if (Objects.equals(
					fileSource,
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA)) {

			validateNotAllowedObjectFieldSettingNames(
				SetUtil.fromArray(
					ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP),
				objectField.getName(), objectFieldSettingsValues);
			validateRelatedObjectFieldSettings(
				objectField,
				ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
				ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
				objectFieldSettingsValues);
		}
		else {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_FILE_SOURCE, fileSource);
		}

		BigDecimal bigDecimal = null;

		try {
			bigDecimal = new BigDecimal(
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}
		}

		if ((bigDecimal == null) || (bigDecimal.signum() == -1)) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE,
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE));
		}
	}

	@Override
	protected void validateObjectFieldSettingValue(
			ObjectField objectField, String objectFieldSettingName,
			Map<String, String> objectFieldSettingsValues)
		throws PortalException {

		super.validateObjectFieldSettingValue(
			objectField, objectFieldSettingName, objectFieldSettingsValues);

		String objectFieldSettingValue = objectFieldSettingsValues.get(
			objectFieldSettingName);

		if (Objects.equals(
				objectFieldSettingName,
				ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP)) {

			Group group = _groupLocalService.fetchGroup(
				GetterUtil.getLong(objectFieldSettingValue));

			if ((group == null) || !group.isDepot()) {
				throw new ObjectFieldSettingValueException.InvalidValue(
					objectField.getName(),
					ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP,
					objectFieldSettingValue);
			}
		}
	}

	private String _getAspectRatio(
		long imageLength, long imageWidth, Locale locale) {

		if ((imageLength <= 0) || (imageWidth <= 0)) {
			return null;
		}

		if (imageLength > imageWidth) {
			return _language.get(locale, "tall");
		}
		else if (imageLength < imageWidth) {
			return _language.get(locale, "wide");
		}

		return _language.get(locale, "square");
	}

	private long _getDDMFormFieldsValueValue(
		List<DLFileEntryMetadata> dlFileEntryMetadatas, String fieldName) {

		if (ListUtil.isEmpty(dlFileEntryMetadatas)) {
			return 0;
		}

		for (DLFileEntryMetadata dlFileEntryMetadata : dlFileEntryMetadatas) {
			Long ddmFormFieldsValueValue = _getDDMFormFieldsValueValue(
				dlFileEntryMetadata.getDDMStorageId(), fieldName);

			if (ddmFormFieldsValueValue != null) {
				return ddmFormFieldsValueValue;
			}
		}

		return 0;
	}

	private Long _getDDMFormFieldsValueValue(
		long ddmStorageId, String fieldName) {

		List<DDMField> ddmFields = _ddmFieldLocalService.getDDMFields(
			ddmStorageId, fieldName);

		if (ListUtil.isEmpty(ddmFields)) {
			return null;
		}

		DDMField ddmField = ddmFields.get(0);

		DDMFieldAttribute ddmFieldAttribute =
			_ddmFieldLocalService.fetchDDMFieldAttribute(
				ddmField.getFieldId(), StringPool.BLANK, StringPool.BLANK);

		if ((ddmFieldAttribute == null) ||
			(ddmFieldAttribute.getAttributeValue() == null) ||
			!Validator.isNumber(ddmFieldAttribute.getAttributeValue())) {

			return null;
		}

		return Long.valueOf(ddmFieldAttribute.getAttributeValue());
	}

	private String _getFileBase64(
			DLFileEntry dlFileEntry, ObjectField objectField)
		throws Exception {

		return NestedFieldsSupplier.supply(
			objectField.getName() + ".fileBase64",
			fieldName -> Base64.encode(
				_file.getBytes(dlFileEntry.getContentStream())));
	}

	private Object _getFileEntryId(Object value) throws PortalException {
		long fileEntryId = GetterUtil.getLong(value);

		if (fileEntryId > 0) {
			return fileEntryId;
		}

		if (value instanceof Map) {
			fileEntryId = MapUtil.getLong((Map<String, Object>)value, "id");
		}
		else {
			String valueString = String.valueOf(value);

			if (JSONUtil.isJSONObject(valueString)) {
				JSONObject jsonObject = jsonFactory.createJSONObject(
					valueString);

				fileEntryId = GetterUtil.getLong(jsonObject.get("id"));
			}
		}

		if (fileEntryId > 0) {
			return fileEntryId;
		}

		return value;
	}

	private String _getFileURL(DLFileEntry dlFileEntry, ObjectField objectField)
		throws Exception {

		String fileSource = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE, objectField);

		if (Objects.equals(
				fileSource,
				ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT) ||
			Objects.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA) ||
			GetterUtil.getBoolean(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY,
					objectField.getObjectFieldSettings()))) {

			return null;
		}

		return _exportImportAttachmentManager.getFileURL(dlFileEntry);
	}

	private Folder _getFolder(DLFileEntry dlFileEntry, ObjectField objectField)
		throws Exception {

		return NestedFieldsSupplier.supply(
			objectField.getName() + ".folder",
			fieldName -> {
				if (!Objects.equals(
						ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA,
						ObjectFieldSettingUtil.getValue(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE,
							objectField))) {

					return null;
				}

				return new Folder() {
					{
						setExternalReferenceCode(
							() -> {
								if (dlFileEntry.getFolderId() == 0) {
									return null;
								}

								DLFolder dlFolder = dlFileEntry.getFolder();

								return dlFolder.getExternalReferenceCode();
							});
						setSiteId(dlFileEntry::getGroupId);
					}
				};
			});
	}

	private Map<String, Object> _getMetadata(
			FileVersion fileVersion, Locale locale, ObjectField objectField)
		throws Exception {

		List<DLFileEntryMetadata> dlFileEntryMetadatas =
			_dlFileEntryMetadataLocalService.getFileVersionFileEntryMetadatas(
				fileVersion.getFileVersionId());

		long tiffImageLength = _getDDMFormFieldsValueValue(
			dlFileEntryMetadatas, "TIFF_IMAGE_LENGTH");
		long tiffImageWidth = _getDDMFormFieldsValueValue(
			dlFileEntryMetadatas, "TIFF_IMAGE_WIDTH");

		return NestedFieldsSupplier.supply(
			objectField.getName() + ".metadata",
			fieldName -> {
				if (fileVersion.getSize() == 0) {
					return null;
				}

				return HashMapBuilder.<String, Object>put(
					"aspectRatio",
					_getAspectRatio(tiffImageLength, tiffImageWidth, locale)
				).put(
					"numberOfPages",
					() -> {
						if (!PDFProcessorUtil.hasImages(fileVersion) &&
							!PDFProcessorUtil.isDocumentSupported(
								fileVersion.getMimeType())) {

							return null;
						}

						return PDFProcessorUtil.getPreviewFileCount(
							fileVersion);
					}
				).put(
					"resolution",
					_getResolution(tiffImageLength, tiffImageWidth, locale)
				).build();
			});
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

	private String _getPreviewURL(
			LiferayFileEntry liferayFileEntry, ObjectField objectField)
		throws Exception {

		return NestedFieldsSupplier.supply(
			objectField.getName() + ".previewURL",
			fieldName -> {
				String previewURL = _getPreviewURL(liferayFileEntry);

				if (Validator.isNull(previewURL)) {
					return null;
				}

				return previewURL;
			});
	}

	private String _getResolution(
		long imageLength, long imageWidth, Locale locale) {

		if ((imageLength <= 0) || (imageWidth <= 0)) {
			return null;
		}

		long max = Math.max(imageLength, imageWidth);
		long min = Math.min(imageLength, imageWidth);

		if ((min <= _RESOLUTION_SMALL_MIN) && (max <= _RESOLUTION_SMALL_MAX)) {
			return _language.format(
				locale, "resolution-small-up-to-x-x",
				new Object[] {_RESOLUTION_SMALL_MAX, _RESOLUTION_SMALL_MIN});
		}

		if ((min <= _RESOLUTION_MEDIUM_MIN) &&
			(max <= _RESOLUTION_MEDIUM_MAX)) {

			return _language.format(
				locale, "resolution-medium-up-to-x-x",
				new Object[] {_RESOLUTION_MEDIUM_MAX, _RESOLUTION_MEDIUM_MIN});
		}

		return _language.format(
			locale, "resolution-large-bigger-than-x", _RESOLUTION_MEDIUM_MAX);
	}

	private Scope _getScope(
			DLFileEntry dlFileEntry, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		if ((objectEntry.getGroupId() == dlFileEntry.getGroupId()) &&
			!Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return null;
		}

		Group group = _groupLocalService.getGroup(dlFileEntry.getGroupId());

		if (group.getType() == GroupConstants.TYPE_DEPOT) {
			return Scope.ofReference(
				group.getExternalReferenceCode(), Scope.Type.ASSET_LIBRARY);
		}

		return Scope.ofReference(
			group.getExternalReferenceCode(), Scope.Type.SITE);
	}

	private String _getThumbnailURL(
			DLFileEntry dlFileEntry, ObjectField objectField)
		throws Exception {

		return NestedFieldsSupplier.supply(
			objectField.getName() + ".thumbnailURL",
			fieldName -> {
				String thumbnailURL = _dlURLHelper.getThumbnailSrc(
					new LiferayFileEntry(dlFileEntry), null);

				if (Validator.isNull(thumbnailURL)) {
					return null;
				}

				return thumbnailURL;
			});
	}

	private static final long _RESOLUTION_MEDIUM_MAX = 1024;

	private static final long _RESOLUTION_MEDIUM_MIN = 768;

	private static final long _RESOLUTION_SMALL_MAX = 400;

	private static final long _RESOLUTION_SMALL_MIN = 300;

	private static final Log _log = LogFactoryUtil.getLog(
		AttachmentObjectFieldBusinessType.class);

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryLocalService _dLFileEntryLocalService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ExportImportAttachmentManager _exportImportAttachmentManager;

	@Reference
	private File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private Portal _portal;

}