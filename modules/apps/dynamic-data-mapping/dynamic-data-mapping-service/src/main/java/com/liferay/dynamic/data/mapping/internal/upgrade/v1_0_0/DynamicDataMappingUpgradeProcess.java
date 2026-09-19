/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.internal.util.DDMImpl;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFieldsCounter;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueTransformer;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesTransformer;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustNotDuplicateFieldName;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;
import com.liferay.view.count.service.ViewCountEntryLocalService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public class DynamicDataMappingUpgradeProcess extends UpgradeProcess {

	public DynamicDataMappingUpgradeProcess(
		AssetEntryLocalService assetEntryLocalService,
		ClassNameLocalService classNameLocalService, DDM ddm,
		DDMFormDeserializer ddmFormJSONDeserializer,
		DDMFormDeserializer ddmFormXSDDeserializer,
		DDMFormLayoutSerializer ddmFormLayoutSerializer,
		DDMFormSerializer ddmFormSerializer,
		DDMFormValuesDeserializer ddmFormValuesDeserializer,
		DDMFormValuesSerializer ddmFormValuesSerializer,
		DLFileEntryLocalService dlFileEntryLocalService,
		DLFileVersionLocalService dlFileVersionLocalService,
		DLFolderLocalService dlFolderLocalService,
		ExpandoRowLocalService expandoRowLocalService,
		ExpandoTableLocalService expandoTableLocalService,
		ExpandoValueLocalService expandoValueLocalService,
		ResourceActions resourceActions,
		ResourceLocalService resourceLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		Store store, ViewCountEntryLocalService viewCountEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_classNameLocalService = classNameLocalService;
		_ddm = ddm;
		_ddmFormJSONDeserializer = ddmFormJSONDeserializer;
		_ddmFormXSDDeserializer = ddmFormXSDDeserializer;
		_ddmFormLayoutSerializer = ddmFormLayoutSerializer;
		_ddmFormSerializer = ddmFormSerializer;
		_ddmFormValuesDeserializer = ddmFormValuesDeserializer;
		_ddmFormValuesSerializer = ddmFormValuesSerializer;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_dlFileVersionLocalService = dlFileVersionLocalService;
		_dlFolderLocalService = dlFolderLocalService;
		_expandoRowLocalService = expandoRowLocalService;
		_expandoTableLocalService = expandoTableLocalService;
		_expandoValueLocalService = expandoValueLocalService;
		_resourceLocalService = resourceLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_store = store;
		_viewCountEntryLocalService = viewCountEntryLocalService;

		_dlFolderModelPermissions = ModelPermissionsFactory.create(
			_DLFOLDER_GROUP_PERMISSIONS, _DLFOLDER_GUEST_PERMISSIONS,
			DLFolder.class.getName());

		_dlFolderModelPermissions.addRolePermissions(
			RoleConstants.OWNER, _DLFOLDER_OWNER_PERMISSIONS);

		_initModelResourceNames(resourceActions);
	}

	protected void addDynamicContentElements(
		Element dynamicElementElement, String name, String data) {

		Map<Locale, String> localizationMap =
			LocalizationUtil.getLocalizationMap(data);

		for (Map.Entry<Locale, String> entry : localizationMap.entrySet()) {
			String[] values = StringUtil.split(entry.getValue());

			if (name.startsWith(StringPool.UNDERLINE)) {
				values = new String[] {entry.getValue()};
			}

			for (String value : values) {
				Element dynamicContentElement =
					dynamicElementElement.addElement("dynamic-content");

				dynamicContentElement.addAttribute(
					"language-id", LanguageUtil.getLanguageId(entry.getKey()));
				dynamicContentElement.addCDATA(value.trim());
			}
		}
	}

	protected String createNewDDMFormFieldName(
			String fieldName, Set<String> existingFieldNames)
		throws Exception {

		String newFieldName = fieldName.replaceAll(
			_INVALID_FIELD_NAME_CHARS_REGEX, StringPool.BLANK);

		if (Validator.isNotNull(newFieldName)) {
			String updatedFieldName = newFieldName;

			for (int i = 1; existingFieldNames.contains(updatedFieldName);
				 i++) {

				updatedFieldName = newFieldName + i;
			}

			return updatedFieldName;
		}

		throw new UpgradeException(
			String.format(
				"Unable to automatically update field name \"%s\" because it " +
					"only contains invalid characters",
				fieldName, newFieldName));
	}

	protected DDMForm deserialize(String content, String type)
		throws Exception {

		DDMFormDeserializer ddmFormDeserializer = null;

		if (StringUtil.equalsIgnoreCase(type, "json")) {
			ddmFormDeserializer = _ddmFormJSONDeserializer;
		}
		else if (StringUtil.equalsIgnoreCase(type, "xsd")) {
			ddmFormDeserializer = _ddmFormXSDDeserializer;
		}

		return DDMFormDeserializeUtil.deserialize(ddmFormDeserializer, content);
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeExpandoStorageAdapter();

		_upgradeStructuresAndAddStructureVersionsAndLayouts();
		_upgradeTemplatesAndAddTemplateVersions();
		_upgradeXMLStorageAdapter();

		_upgradeFieldTypeReferences();

		_upgradeStructuresPermissions();
		_upgradeTemplatesPermissions();
	}

	protected DDMFormValues getDDMFormValues(
			long companyId, DDMForm ddmForm, String xml)
		throws Exception {

		DDMFormValuesXSDDeserializer ddmFormValuesXSDDeserializer =
			new DDMFormValuesXSDDeserializer(companyId);

		return ddmFormValuesXSDDeserializer.deserialize(ddmForm, xml);
	}

	protected boolean isInvalidFieldName(String fieldName) {
		Matcher matcher = _invalidFieldNameCharsPattern.matcher(fieldName);

		return matcher.find();
	}

	protected void populateStructureInvalidDDMFormFieldNamesMap(
		long structureId, DDMForm ddmForm) {

		Map<String, String> ddmFormFieldNamesMap = new HashMap<>();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			String oldName = (String)ddmFormField.getProperty("oldName");

			if (oldName == null) {
				continue;
			}

			ddmFormFieldNamesMap.put(oldName, ddmFormField.getName());
		}

		_structureInvalidDDMFormFieldNamesMap.put(
			structureId, ddmFormFieldNamesMap);
	}

	protected String renameInvalidDDMFormFieldNames(
		long structureId, String string) {

		Map<String, String> ddmFormFieldNamesMap =
			_structureInvalidDDMFormFieldNamesMap.get(structureId);

		if ((ddmFormFieldNamesMap == null) || ddmFormFieldNamesMap.isEmpty()) {
			return string;
		}

		Set<String> oldFieldNames = ddmFormFieldNamesMap.keySet();

		String[] oldSub = oldFieldNames.toArray(new String[0]);

		String[] newSub = new String[oldFieldNames.size()];

		for (int i = 0; i < oldSub.length; i++) {
			newSub[i] = ddmFormFieldNamesMap.get(oldSub[i]);
		}

		return StringUtil.replace(string, oldSub, newSub);
	}

	protected String toJSON(DDMFormValues ddmFormValues) {
		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				_ddmFormValuesSerializer.serialize(builder.build());

		return ddmFormValuesSerializerSerializeResponse.getContent();
	}

	protected String toXML(Map<String, String> expandoValuesMap) {
		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		for (Map.Entry<String, String> entry : expandoValuesMap.entrySet()) {
			Element dynamicElementElement = rootElement.addElement(
				"dynamic-element");

			String name = entry.getKey();
			String data = entry.getValue();

			dynamicElementElement.addAttribute("name", name);
			dynamicElementElement.addAttribute(
				"default-language-id",
				LocalizationUtil.getDefaultLanguageId(data));

			addDynamicContentElements(dynamicElementElement, name, data);
		}

		return document.asXML();
	}

	private void _deleteExpandoData(Set<Long> expandoRowIds)
		throws PortalException {

		Set<Long> expandoTableIds = new HashSet<>();

		for (long expandoRowId : expandoRowIds) {
			ExpandoRow expandoRow = _expandoRowLocalService.fetchExpandoRow(
				expandoRowId);

			if (expandoRow != null) {
				expandoTableIds.add(expandoRow.getTableId());
			}
		}

		for (long expandoTableId : expandoTableIds) {
			try {
				_expandoTableLocalService.deleteTable(expandoTableId);
			}
			catch (PortalException portalException) {
				_log.error("Unable delete expando table", portalException);

				throw portalException;
			}
		}
	}

	private List<String> _getDDMDateFieldNames(DDMForm ddmForm)
		throws Exception {

		return TransformUtil.transform(
			ddmForm.getDDMFormFields(),
			ddmFormField -> {
				String dataType = ddmFormField.getType();

				if (!dataType.equals("ddm-date")) {
					return null;
				}

				return ddmFormField.getName();
			});
	}

	private DDMForm _getDDMForm(long structureId) throws Exception {
		DDMForm ddmForm = _ddmForms.get(structureId);

		if (ddmForm != null) {
			return ddmForm;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select parentStructureId, definition, storageType from " +
					"DDMStructure where structureId = ?")) {

			preparedStatement.setLong(1, structureId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String definition = resultSet.getString("definition");
					String storageType = resultSet.getString("storageType");

					if (storageType.equals("expando") ||
						storageType.equals("xml")) {

						ddmForm = deserialize(definition, "xsd");
					}
					else {
						ddmForm = deserialize(definition, "json");
					}

					try {
						_validateDDMFormFieldNames(ddmForm);
					}
					catch (MustNotDuplicateFieldName mndfn) {
						throw new UpgradeException(
							String.format(
								"The field name '%s' from structure ID %d is " +
									"defined more than once",
								mndfn.getFieldName(), structureId));
					}

					long parentStructureId = resultSet.getLong(
						"parentStructureId");

					if (parentStructureId > 0) {
						DDMForm parentDDMForm = _getDDMForm(parentStructureId);

						Set<String> commonDDMFormFieldNames = SetUtil.intersect(
							_getDDMFormFieldsNames(parentDDMForm),
							_getDDMFormFieldsNames(ddmForm));

						String commonDDMFormFieldNamesString = StringUtil.merge(
							commonDDMFormFieldNames);

						if (!commonDDMFormFieldNames.isEmpty()) {
							throw new UpgradeException(
								"Duplicate dynamic data mapping form field " +
									"names: " + commonDDMFormFieldNamesString);
						}
					}

					DDMForm updatedDDMForm = _updateDDMFormFields(ddmForm);

					_ddmForms.put(structureId, updatedDDMForm);

					return updatedDDMForm;
				}
			}

			throw new UpgradeException(
				"Unable to find dynamic data mapping structure with ID " +
					structureId);
		}
	}

	private Set<String> _getDDMFormFieldsNames(DDMForm ddmForm) {
		Set<String> ddmFormFieldsNames = new HashSet<>();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (String ddmFormFieldName : ddmFormFieldsMap.keySet()) {
			ddmFormFieldsNames.add(StringUtil.toLowerCase(ddmFormFieldName));
		}

		return ddmFormFieldsNames;
	}

	private Map<String, String> _getDDMTemplateScriptMap(long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from DDMTemplate where classPK = ? and type_ = ?")) {

			preparedStatement.setLong(1, structureId);
			preparedStatement.setString(
				2, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Map<String, String> ddmTemplateScriptMap = new HashMap<>();

				while (resultSet.next()) {
					long templateId = resultSet.getLong("templateId");
					String language = resultSet.getString("language");
					String script = resultSet.getString("script");

					String key = templateId + StringPool.DOLLAR + language;

					ddmTemplateScriptMap.put(key, script);
				}

				return ddmTemplateScriptMap;
			}
		}
	}

	private String _getDefaultDDMFormLayoutDefinition(DDMForm ddmForm) {
		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		DDMFormLayoutSerializerSerializeRequest.Builder builder =
			DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
				ddmFormLayout);

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				_ddmFormLayoutSerializer.serialize(builder.build());

		return ddmFormLayoutSerializerSerializeResponse.getContent();
	}

	private Map<String, String> _getExpandoValuesMap(long expandoRowId)
		throws PortalException {

		Map<String, String> fieldsMap = new HashMap<>();

		List<ExpandoValue> expandoValues =
			_expandoValueLocalService.getRowValues(expandoRowId);

		for (ExpandoValue expandoValue : expandoValues) {
			ExpandoColumn expandoColumn = expandoValue.getColumn();

			fieldsMap.put(expandoColumn.getName(), expandoValue.getData());
		}

		return fieldsMap;
	}

	private DDMForm _getFullHierarchyDDMForm(long structureId)
		throws Exception {

		DDMForm fullHierarchyDDMForm = _fullHierarchyDDMForms.get(structureId);

		if (fullHierarchyDDMForm != null) {
			return fullHierarchyDDMForm;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select parentStructureId from DDMStructure where " +
					"structureId = ?")) {

			preparedStatement.setLong(1, structureId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					long parentStructureId = resultSet.getLong(
						"parentStructureId");

					fullHierarchyDDMForm = _getDDMForm(structureId);

					if (parentStructureId > 0) {
						DDMForm parentDDMForm = _getFullHierarchyDDMForm(
							parentStructureId);

						List<DDMFormField> ddmFormFields =
							fullHierarchyDDMForm.getDDMFormFields();

						ddmFormFields.addAll(parentDDMForm.getDDMFormFields());
					}

					_fullHierarchyDDMForms.put(
						structureId, fullHierarchyDDMForm);

					return fullHierarchyDDMForm;
				}
			}

			throw new UpgradeException(
				"Unable to find dynamic data mapping structure with ID " +
					structureId);
		}
	}

	private boolean _hasStructureVersion(long structureId, String version)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from DDMStructureVersion where structureId = ? and " +
					"version = ?")) {

			preparedStatement.setLong(1, structureId);
			preparedStatement.setString(2, version);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private boolean _hasTemplateVersion(long templateId, String version)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from DDMTemplateVersion where templateId = ? and " +
					"version = ?")) {

			preparedStatement.setLong(1, templateId);
			preparedStatement.setString(2, version);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private void _initModelResourceNames(ResourceActions resourceActions) {
		_structureModelResourceNames = HashMapBuilder.put(
			"com.liferay.document.library.kernel.model.DLFileEntry",
			resourceActions.getCompositeModelName(
				"com.liferay.document.library.kernel.model.DLFileEntry",
				_CLASS_NAME_DDM_STRUCTURE)
		).put(
			"com.liferay.document.library.kernel.model.DLFileEntryMetadata",
			resourceActions.getCompositeModelName(
				"com.liferay.document.library.kernel.model.DLFileEntryMetadata",
				_CLASS_NAME_DDM_STRUCTURE)
		).put(
			"com.liferay.document.library.kernel.processor." +
				"RawMetadataProcessor",
			_CLASS_NAME_DDM_STRUCTURE
		).put(
			"com.liferay.dynamic.data.lists.model.DDLRecordSet",
			resourceActions.getCompositeModelName(
				"com.liferay.dynamic.data.lists.model.DDLRecordSet",
				_CLASS_NAME_DDM_STRUCTURE)
		).put(
			"com.liferay.journal.model.JournalArticle",
			resourceActions.getCompositeModelName(
				"com.liferay.journal.model.JournalArticle",
				_CLASS_NAME_DDM_STRUCTURE)
		).put(
			"com.liferay.portlet.dynamicdatalists.model.DDLRecordSet",
			resourceActions.getCompositeModelName(
				"com.liferay.dynamic.data.lists.model.DDLRecordSet",
				_CLASS_NAME_DDM_STRUCTURE)
		).build();

		_templateModelResourceNames = HashMapBuilder.put(
			"com.liferay.dynamic.data.lists.model.DDLRecordSet",
			resourceActions.getCompositeModelName(
				"com.liferay.dynamic.data.lists.model.DDLRecordSet",
				_CLASS_NAME_DDM_TEMPLATE)
		).put(
			"com.liferay.journal.model.JournalArticle",
			resourceActions.getCompositeModelName(
				"com.liferay.journal.model.JournalArticle",
				_CLASS_NAME_DDM_TEMPLATE)
		).put(
			"com.liferay.portlet.display.template.PortletDisplayTemplate",
			_CLASS_NAME_DDM_TEMPLATE
		).put(
			"com.liferay.portlet.dynamicdatalists.model.DDLRecordSet",
			resourceActions.getCompositeModelName(
				"com.liferay.dynamic.data.lists.model.DDLRecordSet",
				_CLASS_NAME_DDM_TEMPLATE)
		).build();
	}

	private void _transformFieldTypeDDMFormFields(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long entryId, String entryVersion,
			String entryModelName, DDMFormValues ddmFormValues)
		throws Exception {

		DDMFormValuesTransformer ddmFormValuesTransformer =
			new DDMFormValuesTransformer(ddmFormValues);

		ddmFormValuesTransformer.addTransformer(
			new FileUploadDDMFormFieldValueTransformer(
				groupId, companyId, userId, userName, createDate, entryId,
				entryVersion, entryModelName));

		ddmFormValuesTransformer.addTransformer(
			new DateDDMFormFieldValueTransformer());

		ddmFormValuesTransformer.transform();
	}

	private DDMForm _updateDDMFormFields(DDMForm ddmForm) throws Exception {
		DDMForm copyDDMForm = new DDMForm(ddmForm);

		Map<String, DDMFormField> ddmFormFieldsMap =
			copyDDMForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			String fieldName = ddmFormField.getName();

			if (isInvalidFieldName(fieldName)) {
				String newFieldName = createNewDDMFormFieldName(
					fieldName, ddmFormFieldsMap.keySet());

				ddmFormField.setName(newFieldName);

				ddmFormField.setProperty("oldName", fieldName);
			}

			String dataType = ddmFormField.getDataType();

			if (Objects.equals(dataType, "file-upload")) {
				ddmFormField.setDataType("document-library");
				ddmFormField.setType("ddm-documentlibrary");
			}
			else if (Objects.equals(dataType, "image")) {
				ddmFormField.setFieldNamespace("ddm");
				ddmFormField.setType("ddm-image");
			}
		}

		return copyDDMForm;
	}

	private void _updateDDMStructureStorageType() throws Exception {
		runSQL(
			"update DDMStructure set storageType = 'xml' where storageType = " +
				"'expando'");
	}

	private void _updateStructureStorageType() throws Exception {
		runSQL(
			"update DDMStructure set storageType='json' where storageType = " +
				"'xml'");
	}

	private void _updateStructureVersionStorageType() throws Exception {
		runSQL(
			"update DDMStructureVersion set storageType='json' where " +
				"storageType = 'xml'");
	}

	private void _updateTemplateScript(long templateId, String script)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMTemplate set script = ? where templateId = ?")) {

			preparedStatement.setString(1, script);
			preparedStatement.setLong(2, templateId);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update dynamic data mapping template with " +
					"template ID " + templateId);

			throw exception;
		}
	}

	private String _updateTemplateScriptDateAssignStatement(
		String dateFieldName, String language, String script) {

		StringBundler oldTemplateScriptSB = new StringBundler(7);
		StringBundler newTemplateScriptSB = new StringBundler(5);

		if (language.equals("ftl")) {
			oldTemplateScriptSB.append("<#assign\\s+");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append("_Data\\s*=\\s*getterUtil\\s*.");
			oldTemplateScriptSB.append("\\s*getLong\\s*\\(\\s*");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append(".\\s*getData\\s*\\(\\s*\\)");
			oldTemplateScriptSB.append("\\s*\\)\\s*/?>");

			newTemplateScriptSB.append("<#assign ");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append("_Data = getterUtil.getString(");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append(".getData()) />");
		}
		else if (language.equals("vm")) {
			dateFieldName =
				StringPool.BACK_SLASH + StringPool.DOLLAR + dateFieldName;

			oldTemplateScriptSB.append("#set\\s+\\(\\s*");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append("_Data\\s*=\\s*\\$getterUtil.");
			oldTemplateScriptSB.append("getLong\\(\\s*");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append(".getData\\(\\)\\s*\\)\\s*\\)");

			newTemplateScriptSB.append("#set (");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append("_Data = \\$getterUtil.getString(");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append(".getData()))");
		}

		return script.replaceAll(
			oldTemplateScriptSB.toString(), newTemplateScriptSB.toString());
	}

	private void _updateTemplateScriptDateFields(
			long structureId, DDMForm ddmForm)
		throws Exception {

		List<String> ddmDateFieldNames = _getDDMDateFieldNames(ddmForm);

		if (ddmDateFieldNames.isEmpty()) {
			return;
		}

		Map<String, String> ddmTemplateScriptMap = _getDDMTemplateScriptMap(
			structureId);

		for (Map.Entry<String, String> entrySet :
				ddmTemplateScriptMap.entrySet()) {

			String[] templateIdAndLanguage = StringUtil.split(
				entrySet.getKey(), StringPool.DOLLAR);

			long ddmTemplateId = GetterUtil.getLong(templateIdAndLanguage[0]);
			String language = templateIdAndLanguage[1];

			String script = entrySet.getValue();

			for (String ddmDateFieldName : ddmDateFieldNames) {
				script = _updateTemplateScriptDateAssignStatement(
					ddmDateFieldName, language, script);

				script = _updateTemplateScriptDateIfStatement(
					ddmDateFieldName, language, script);

				script = _updateTemplateScriptDateParseStatement(
					ddmDateFieldName, language, script);

				script = _updateTemplateScriptDateGetDateStatement(
					language, script);
			}

			_updateTemplateScript(ddmTemplateId, script);
		}
	}

	private String _updateTemplateScriptDateGetDateStatement(
		String language, String script) {

		StringBundler oldTemplateScriptSB = new StringBundler(3);
		String newTemplateScript = null;

		if (language.equals("ftl")) {
			oldTemplateScriptSB.append("dateUtil.getDate\\((.*)");
			oldTemplateScriptSB.append("locale[,\\s]*timeZoneUtil.");
			oldTemplateScriptSB.append("getTimeZone\\(\"UTC\"\\)\\s*\\)");

			newTemplateScript = "dateUtil.getDate($1locale)";
		}
		else if (language.equals("vm")) {
			oldTemplateScriptSB.append("dateUtil.getDate\\((.*)");
			oldTemplateScriptSB.append("\\$locale[,\\s]*\\$timeZoneUtil.");
			oldTemplateScriptSB.append("getTimeZone\\(\"UTC\"\\)\\s*\\)");

			newTemplateScript = "dateUtil.getDate($1\\$locale)";
		}

		return script.replaceAll(
			oldTemplateScriptSB.toString(), newTemplateScript);
	}

	private String _updateTemplateScriptDateIfStatement(
		String dateFieldName, String language, String script) {

		String oldTemplateScript = StringPool.BLANK;
		String newTemplateScript = StringPool.BLANK;

		if (language.equals("ftl")) {
			oldTemplateScript = StringBundler.concat(
				"<#if\\s*\\(?\\s*", dateFieldName, "_Data\\s*>\\s*0\\s*\\)?",
				"\\s*>");

			newTemplateScript =
				"<#if validator.isNotNull(" + dateFieldName + "_Data)>";
		}
		else if (language.equals("vm")) {
			dateFieldName =
				StringPool.BACK_SLASH + StringPool.DOLLAR + dateFieldName;

			oldTemplateScript =
				"#if\\s*\\(\\s*" + dateFieldName + "_Data\\s*>\\s*0\\s*\\)";

			newTemplateScript =
				"#if (\\$validator.isNotNull(" + dateFieldName + "_Data))";
		}

		return script.replaceAll(oldTemplateScript, newTemplateScript);
	}

	private String _updateTemplateScriptDateParseStatement(
		String dateFieldName, String language, String script) {

		StringBundler oldTemplateScriptSB = new StringBundler(6);
		StringBundler newTemplateScriptSB = new StringBundler(5);

		if (language.equals("ftl")) {
			oldTemplateScriptSB.append("<#assign\\s+");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append("_DateObj\\s*=\\s*dateUtil\\s*.");
			oldTemplateScriptSB.append("\\s*newDate\\(\\s*");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append("_Data\\s*\\)\\s*/?>");

			newTemplateScriptSB.append("<#assign ");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append(
				"_DateObj = dateUtil.parseDate(\"yyyy-MM-dd\", ");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append("_Data, locale) />");
		}
		else if (language.equals("vm")) {
			dateFieldName =
				StringPool.BACK_SLASH + StringPool.DOLLAR + dateFieldName;

			oldTemplateScriptSB.append("#set\\s*\\(");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append("_DateObj\\s*=\\s*\\$dateUtil.");
			oldTemplateScriptSB.append("newDate\\(\\s*");
			oldTemplateScriptSB.append(dateFieldName);
			oldTemplateScriptSB.append("_Data\\s*\\)\\s*\\)");

			newTemplateScriptSB.append("#set (");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append(
				"_DateObj = \\$dateUtil.parseDate(\"yyyy-MM-dd\", ");
			newTemplateScriptSB.append(dateFieldName);
			newTemplateScriptSB.append("_Data, \\$locale))");
		}

		return script.replaceAll(
			oldTemplateScriptSB.toString(), newTemplateScriptSB.toString());
	}

	private void _upgradeDDLFieldTypeReferences() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DDLRecordVersion.*, DDMContent.data_, ",
					"DDMStructure.structureId from DDLRecordVersion inner ",
					"join DDLRecordSet on DDLRecordVersion.recordSetId = ",
					"DDLRecordSet.recordSetId inner join DDMContent on  ",
					"DDLRecordVersion.DDMStorageId = DDMContent.contentId ",
					"inner join DDMStructure on DDLRecordSet.DDMStructureId = ",
					"DDMStructure.structureId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMContent set data_= ? where contentId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Timestamp createDate = resultSet.getTimestamp("createDate");
				long entryId = resultSet.getLong("recordId");
				String entryVersion = resultSet.getString("version");
				long contentId = resultSet.getLong("ddmStorageId");
				String data_ = resultSet.getString("data_");

				long ddmStructureId = resultSet.getLong("structureId");

				DDMForm ddmForm = _getFullHierarchyDDMForm(ddmStructureId);

				DDMFormValues ddmFormValues =
					DDMFormValuesDeserializeUtil.deserialize(
						data_, ddmForm, _ddmFormValuesDeserializer);

				_transformFieldTypeDDMFormFields(
					groupId, companyId, userId, userName, createDate, entryId,
					entryVersion, "DDLRecord", ddmFormValues);

				preparedStatement2.setString(1, toJSON(ddmFormValues));

				preparedStatement2.setLong(2, contentId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _upgradeDLFieldTypeReferences() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DLFileVersion.*, DDMContent.contentId, ",
					"DDMContent.data_, DDMStructure.structureId from ",
					"DLFileEntryMetadata inner join DDMContent on ",
					"DLFileEntryMetadata.DDMStorageId = DDMContent.contentId ",
					"inner join DDMStructure on ",
					"DLFileEntryMetadata.DDMStructureId = DDMStructure.",
					"structureId inner join DLFileVersion on ",
					"DLFileEntryMetadata.fileVersionId = DLFileVersion.",
					"fileVersionId and DLFileEntryMetadata.fileEntryId = ",
					"DLFileVersion.fileEntryId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMContent set data_= ? where contentId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Timestamp createDate = resultSet.getTimestamp("createDate");
				long entryId = resultSet.getLong("fileEntryId");
				String entryVersion = resultSet.getString("version");
				long contentId = resultSet.getLong("contentId");
				String data_ = resultSet.getString("data_");

				long ddmStructureId = resultSet.getLong("structureId");

				DDMForm ddmForm = _getFullHierarchyDDMForm(ddmStructureId);

				DDMFormValues ddmFormValues =
					DDMFormValuesDeserializeUtil.deserialize(
						data_, ddmForm, _ddmFormValuesDeserializer);

				_transformFieldTypeDDMFormFields(
					groupId, companyId, userId, userName, createDate, entryId,
					entryVersion, "DLFileEntry", ddmFormValues);

				preparedStatement2.setString(1, toJSON(ddmFormValues));

				preparedStatement2.setLong(2, contentId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _upgradeExpandoStorageAdapter() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						StringBundler.concat(
							"select DDMStructure.*, DDMStorageLink.* from ",
							"DDMStorageLink inner join DDMStructure on ",
							"DDMStorageLink.structureId = ",
							"DDMStructure.structureId where ",
							"DDMStructure.storageType = 'expando'"));
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into DDMContent (uuid_, contentId, ",
							"groupId, companyId, userId, userName, ",
							"createDate, modifiedDate, name, description, ",
							"data_) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"));
				PreparedStatement preparedStatement3 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update DDMStorageLink set classNameId = ? where " +
							"classNameId = ? and classPK = ?");
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				Set<Long> expandoRowIds = new HashSet<>();

				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");
					long companyId = resultSet.getLong("companyId");
					long userId = resultSet.getLong("userId");
					String userName = resultSet.getString("userName");
					Timestamp createDate = resultSet.getTimestamp("createDate");

					long expandoRowId = resultSet.getLong("classPK");

					String xml = toXML(_getExpandoValuesMap(expandoRowId));

					preparedStatement2.setString(1, PortalUUIDUtil.generate());
					preparedStatement2.setLong(2, expandoRowId);
					preparedStatement2.setLong(3, groupId);
					preparedStatement2.setLong(4, companyId);
					preparedStatement2.setLong(5, userId);
					preparedStatement2.setString(6, userName);
					preparedStatement2.setTimestamp(7, createDate);
					preparedStatement2.setTimestamp(8, createDate);
					preparedStatement2.setString(
						9, DDMStorageLink.class.getName());
					preparedStatement2.setString(10, null);
					preparedStatement2.setString(11, xml);

					preparedStatement2.addBatch();

					preparedStatement3.setLong(
						1, PortalUtil.getClassNameId(DDMContent.class));
					preparedStatement3.setLong(
						2,
						PortalUtil.getClassNameId(
							"com.liferay.portlet.dynamicdatamapping.storage." +
								"ExpandoStorageAdapter"));
					preparedStatement3.setLong(3, expandoRowId);

					preparedStatement3.addBatch();

					expandoRowIds.add(expandoRowId);
				}

				if (expandoRowIds.isEmpty()) {
					return;
				}

				preparedStatement2.executeBatch();

				preparedStatement3.executeBatch();

				_updateDDMStructureStorageType();

				_deleteExpandoData(expandoRowIds);
			}
		}
	}

	private void _upgradeFieldTypeReferences() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_upgradeDDLFieldTypeReferences();
			_upgradeDLFieldTypeReferences();
		}
	}

	private void _upgradeStructurePermissions(long companyId, long structureId)
		throws Exception {

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					companyId, DDMStructure.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(structureId))) {

			String className = _structureClassNames.get(
				Long.valueOf(resourcePermission.getPrimKey()));

			if (className == null) {
				continue;
			}

			String resourceName = _structureModelResourceNames.get(className);

			if (resourceName == null) {
				throw new UpgradeException(
					StringBundler.concat(
						"Model ", className, " does not support DDM structure ",
						"permission checking"));
			}

			// A permission with the correct resource name may already exist.
			// This means that Documents and Media has already migrated the
			// structures for all its file entry types. In this case, we simply
			// need to remove the old permission and continue with the upgrade
			// process for the remaining resource permissions.

			ResourcePermission existingResourcePermission =
				_resourcePermissionLocalService.fetchResourcePermission(
					companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(structureId),
					resourcePermission.getRoleId());

			if (existingResourcePermission != null) {
				_resourcePermissionLocalService.deleteResourcePermission(
					resourcePermission.getResourcePermissionId());
			}
			else {
				resourcePermission.setName(resourceName);

				_resourcePermissionLocalService.updateResourcePermission(
					resourcePermission);
			}
		}
	}

	private void _upgradeStructuresAndAddStructureVersionsAndLayouts()
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select * from DDMStructure");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMStructureVersion (structureVersionId, ",
						"groupId, companyId, userId, userName, createDate, ",
						"structureId, version, parentStructureId, name, ",
						"description, definition, storageType, type_, status, ",
						"statusByUserId, statusByUserName, statusDate) values ",
						"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						"?)"));
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMStructureLayout (uuid_, ",
						"structureLayoutId, groupId, companyId, userId, ",
						"userName, createDate, modifiedDate, ",
						"structureVersionId, definition) values (?, ?, ?, ?, ",
						"?, ?, ?, ?, ?, ?)"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long structureId = resultSet.getLong("structureId");

				_structureClassNames.put(
					structureId,
					PortalUtil.getClassName(resultSet.getLong("classNameId")));

				// Structure content

				DDMForm ddmForm = _getDDMForm(structureId);

				populateStructureInvalidDDMFormFieldNamesMap(
					structureId, ddmForm);

				String definition = DDMFormSerializeUtil.serialize(
					ddmForm, _ddmFormSerializer);

				preparedStatement2.setString(1, definition);

				preparedStatement2.setLong(2, structureId);

				preparedStatement2.addBatch();

				_updateTemplateScriptDateFields(structureId, ddmForm);

				// Structure version

				if (_hasStructureVersion(
						structureId, resultSet.getString("version"))) {

					continue;
				}

				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Timestamp modifiedDate = resultSet.getTimestamp("modifiedDate");
				long parentStructureId = resultSet.getLong("parentStructureId");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				String storageType = resultSet.getString("storageType");
				int type = resultSet.getInt("type_");

				long structureVersionId = increment();

				preparedStatement3.setLong(1, structureVersionId);

				preparedStatement3.setLong(2, groupId);
				preparedStatement3.setLong(3, companyId);
				preparedStatement3.setLong(4, userId);
				preparedStatement3.setString(5, userName);
				preparedStatement3.setTimestamp(6, modifiedDate);
				preparedStatement3.setLong(7, structureId);
				preparedStatement3.setString(
					8, DDMStructureConstants.VERSION_DEFAULT);
				preparedStatement3.setLong(9, parentStructureId);
				preparedStatement3.setString(10, name);
				preparedStatement3.setString(11, description);
				preparedStatement3.setString(12, definition);
				preparedStatement3.setString(13, storageType);
				preparedStatement3.setInt(14, type);
				preparedStatement3.setInt(
					15, WorkflowConstants.STATUS_APPROVED);
				preparedStatement3.setLong(16, userId);
				preparedStatement3.setString(17, userName);
				preparedStatement3.setTimestamp(18, modifiedDate);

				preparedStatement3.addBatch();

				// Structure layout

				String ddmFormLayoutDefinition =
					_getDefaultDDMFormLayoutDefinition(ddmForm);

				preparedStatement4.setString(1, PortalUUIDUtil.generate());
				preparedStatement4.setLong(2, increment());
				preparedStatement4.setLong(3, groupId);
				preparedStatement4.setLong(4, companyId);
				preparedStatement4.setLong(5, userId);
				preparedStatement4.setString(6, userName);
				preparedStatement4.setTimestamp(7, modifiedDate);
				preparedStatement4.setTimestamp(8, modifiedDate);
				preparedStatement4.setLong(9, structureVersionId);
				preparedStatement4.setString(10, ddmFormLayoutDefinition);

				preparedStatement4.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();

			preparedStatement4.executeBatch();
		}
	}

	private void _upgradeStructuresPermissions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from DDMStructure");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");
				long structureId = resultSet.getLong("structureId");

				_upgradeStructurePermissions(companyId, structureId);
			}
		}
	}

	private void _upgradeTemplatesAndAddTemplateVersions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select * from DDMTemplate");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMTemplate set resourceClassNameId = ? where " +
						"templateId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMTemplate set language = ?, script = ? where " +
						"templateId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMTemplateVersion (templateVersionId, ",
						"groupId, companyId, userId, userName, createDate, ",
						"classNameId, classPK, templateId, version, name, ",
						"description, language, script, status, ",
						"statusByUserId, statusByUserName, statusDate) values ",
						"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						"?)"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long classNameId = resultSet.getLong("classNameId");
				long classPK = resultSet.getLong("classPK");
				long templateId = resultSet.getLong("templateId");

				// Template resource class name

				String className = null;

				if (classNameId != PortalUtil.getClassNameId(
						DDMStructure.class)) {

					className =
						"com.liferay.portlet.display.template." +
							"PortletDisplayTemplate";
				}
				else if (classPK == 0) {
					className = "com.liferay.journal.model.JournalArticle";
				}
				else {
					className = _structureClassNames.get(classPK);
				}

				if (className == null) {
					if (_log.isWarnEnabled()) {
						_log.warn("Orphaned DDM template " + templateId);
					}

					continue;
				}

				String version = resultSet.getString("version");
				String language = resultSet.getString("language");
				String script = resultSet.getString("script");

				preparedStatement2.setLong(
					1, PortalUtil.getClassNameId(className));
				preparedStatement2.setLong(2, templateId);

				preparedStatement2.addBatch();

				_templateResourceClassNames.put(templateId, className);

				// Template content

				String updatedScript = renameInvalidDDMFormFieldNames(
					classPK, script);

				if (language.equals("xsd")) {
					DDMForm ddmForm = deserialize(updatedScript, "xsd");

					ddmForm = _updateDDMFormFields(ddmForm);

					updatedScript = DDMFormSerializeUtil.serialize(
						ddmForm, _ddmFormSerializer);

					language = "json";
				}

				if (!script.equals(updatedScript)) {
					preparedStatement3.setString(1, language);
					preparedStatement3.setString(2, updatedScript);
					preparedStatement3.setLong(3, templateId);

					preparedStatement3.addBatch();
				}

				// Template version

				if (_hasTemplateVersion(templateId, version)) {
					continue;
				}

				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Timestamp modifiedDate = resultSet.getTimestamp("modifiedDate");

				preparedStatement4.setLong(1, increment());
				preparedStatement4.setLong(2, resultSet.getLong("groupId"));
				preparedStatement4.setLong(3, resultSet.getLong("companyId"));
				preparedStatement4.setLong(4, userId);
				preparedStatement4.setString(5, userName);
				preparedStatement4.setTimestamp(6, modifiedDate);
				preparedStatement4.setLong(7, classNameId);
				preparedStatement4.setLong(8, classPK);
				preparedStatement4.setLong(9, templateId);
				preparedStatement4.setString(
					10, DDMStructureConstants.VERSION_DEFAULT);
				preparedStatement4.setString(11, resultSet.getString("name"));
				preparedStatement4.setString(
					12, resultSet.getString("description"));
				preparedStatement4.setString(13, language);
				preparedStatement4.setString(14, updatedScript);
				preparedStatement4.setInt(
					15, WorkflowConstants.STATUS_APPROVED);
				preparedStatement4.setLong(16, userId);
				preparedStatement4.setString(17, userName);
				preparedStatement4.setTimestamp(18, modifiedDate);

				preparedStatement4.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();

			preparedStatement4.executeBatch();
		}
	}

	private void _upgradeTemplatesPermissions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from DDMTemplate");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				for (ResourcePermission resourcePermission :
						_resourcePermissionLocalService.getResourcePermissions(
							resultSet.getLong("companyId"),
							DDMTemplate.class.getName(),
							ResourceConstants.SCOPE_INDIVIDUAL,
							String.valueOf(resultSet.getLong("templateId")))) {

					String className = _templateResourceClassNames.get(
						Long.valueOf(resourcePermission.getPrimKey()));

					if (className == null) {
						continue;
					}

					String resourceName = _templateModelResourceNames.get(
						className);

					if (resourceName == null) {
						throw new UpgradeException(
							StringBundler.concat(
								"Model ", className,
								" does not support DDM template permission ",
								"checking"));
					}

					resourcePermission.setName(resourceName);

					_resourcePermissionLocalService.updateResourcePermission(
						resourcePermission);
				}
			}
		}
	}

	private void _upgradeXMLStorageAdapter() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						StringBundler.concat(
							"select DDMStorageLink.classPK, DDMStorageLink.",
							"structureId from DDMStorageLink inner join ",
							"DDMStructure on (DDMStorageLink.structureId = ",
							"DDMStructure.structureId) where DDMStorageLink.",
							"classNameId = ? and DDMStructure.storageType = ",
							"?"));
				PreparedStatement preparedStatement2 =
					connection.prepareStatement(
						"select companyId, data_ from DDMContent where " +
							"contentId = ? and data_ like '<%'");
				PreparedStatement preparedStatement3 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update DDMContent set data_= ? where contentId = ?")) {

				preparedStatement1.setLong(
					1, PortalUtil.getClassNameId(DDMContent.class));
				preparedStatement1.setString(2, "xml");

				try (ResultSet resultSet = preparedStatement1.executeQuery()) {
					while (resultSet.next()) {
						long structureId = resultSet.getLong("structureId");
						long classPK = resultSet.getLong("classPK");

						DDMForm ddmForm = _getFullHierarchyDDMForm(structureId);

						preparedStatement2.setLong(1, classPK);

						try (ResultSet resultSet2 =
								preparedStatement2.executeQuery()) {

							if (resultSet2.next()) {
								long companyId = resultSet2.getLong(
									"companyId");

								String xml = renameInvalidDDMFormFieldNames(
									structureId, resultSet2.getString("data_"));

								String content = toJSON(
									getDDMFormValues(companyId, ddmForm, xml));

								preparedStatement3.setString(1, content);

								preparedStatement3.setLong(2, classPK);

								preparedStatement3.addBatch();
							}
						}
					}

					preparedStatement3.executeBatch();
				}
			}

			_updateStructureStorageType();
			_updateStructureVersionStorageType();
		}
	}

	private void _validateDDMFormFieldName(
			DDMFormField ddmFormField, Set<String> ddmFormFieldNames)
		throws MustNotDuplicateFieldName {

		if (ddmFormFieldNames.contains(
				StringUtil.toLowerCase(ddmFormField.getName()))) {

			throw new MustNotDuplicateFieldName(ddmFormField.getName());
		}

		ddmFormFieldNames.add(StringUtil.toLowerCase(ddmFormField.getName()));

		for (DDMFormField nestedDDMFormField :
				ddmFormField.getNestedDDMFormFields()) {

			_validateDDMFormFieldName(nestedDDMFormField, ddmFormFieldNames);
		}
	}

	private void _validateDDMFormFieldNames(DDMForm ddmForm)
		throws MustNotDuplicateFieldName {

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		Set<String> ddmFormFieldNames = new HashSet<>();

		for (DDMFormField ddmFormField : ddmFormFields) {
			_validateDDMFormFieldName(ddmFormField, ddmFormFieldNames);
		}
	}

	private static final String _CLASS_NAME_DDM_STRUCTURE =
		"com.liferay.dynamic.data.mapping.model.DDMStructure";

	private static final String _CLASS_NAME_DDM_TEMPLATE =
		"com.liferay.dynamic.data.mapping.model.DDMTemplate";

	private static final String[] _DLFOLDER_GROUP_PERMISSIONS = {
		"ADD_DOCUMENT", "ADD_SHORTCUT", "ADD_SUBFOLDER", "SUBSCRIBE", "VIEW"
	};

	private static final String[] _DLFOLDER_GUEST_PERMISSIONS = {"VIEW"};

	private static final String[] _DLFOLDER_OWNER_PERMISSIONS = {
		"ACCESS", "ADD_DOCUMENT", "ADD_SHORTCUT", "ADD_SUBFOLDER", "DELETE",
		"PERMISSIONS", "SUBSCRIBE", "UPDATE", "VIEW"
	};

	private static final String _INVALID_FIELD_NAME_CHARS_REGEX =
		"([\\p{Punct}&&[^_]]|\\p{Space})+";

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicDataMappingUpgradeProcess.class);

	private static final Pattern _invalidFieldNameCharsPattern =
		Pattern.compile(_INVALID_FIELD_NAME_CHARS_REGEX);

	private final AssetEntryLocalService _assetEntryLocalService;
	private final ClassNameLocalService _classNameLocalService;
	private final DDM _ddm;
	private final DDMFormDeserializer _ddmFormJSONDeserializer;
	private final DDMFormLayoutSerializer _ddmFormLayoutSerializer;
	private final DDMFormSerializer _ddmFormSerializer;
	private final DDMFormValuesDeserializer _ddmFormValuesDeserializer;
	private final DDMFormValuesSerializer _ddmFormValuesSerializer;
	private final DDMFormDeserializer _ddmFormXSDDeserializer;
	private final Map<Long, DDMForm> _ddmForms = new HashMap<>();
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final DLFileVersionLocalService _dlFileVersionLocalService;
	private final DLFolderLocalService _dlFolderLocalService;
	private final ModelPermissions _dlFolderModelPermissions;
	private final ExpandoRowLocalService _expandoRowLocalService;
	private final ExpandoTableLocalService _expandoTableLocalService;
	private final ExpandoValueLocalService _expandoValueLocalService;
	private final Map<Long, DDMForm> _fullHierarchyDDMForms = new HashMap<>();
	private final ResourceLocalService _resourceLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final Store _store;
	private final Map<Long, String> _structureClassNames = new HashMap<>();
	private final Map<Long, Map<String, String>>
		_structureInvalidDDMFormFieldNamesMap = new HashMap<>();
	private Map<String, String> _structureModelResourceNames;
	private Map<String, String> _templateModelResourceNames;
	private final Map<Long, String> _templateResourceClassNames =
		new HashMap<>();
	private final ViewCountEntryLocalService _viewCountEntryLocalService;

	private static class DDMFormValuesXSDDeserializer {

		public DDMFormValuesXSDDeserializer(long companyId) {
			_companyId = companyId;
		}

		public DDMFormValues deserialize(DDMForm ddmForm, String xml)
			throws PortalException {

			try {
				DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

				Document document = SAXReaderUtil.read(xml);

				Element rootElement = document.getRootElement();

				setDDMFormValuesAvailableLocales(ddmFormValues, rootElement);
				setDDMFormValuesDefaultLocale(ddmFormValues, rootElement);

				DDMFieldsCounter ddmFieldsCounter = new DDMFieldsCounter();

				for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
					String fieldName = ddmFormField.getName();

					int repetitions = countDDMFieldRepetitions(
						rootElement, fieldName, null, -1);

					for (int i = 0; i < repetitions; i++) {
						DDMFormFieldValue ddmFormFieldValue =
							createDDMFormFieldValue(
								ddmFormField, ddmFieldsCounter, fieldName,
								rootElement);

						if (ddmFormFieldValue != null) {
							ddmFormValues.addDDMFormFieldValue(
								ddmFormFieldValue);
						}
					}
				}

				return ddmFormValues;
			}
			catch (DocumentException documentException) {
				throw new UpgradeException(documentException);
			}
		}

		protected int countDDMFieldRepetitions(
			Element rootElement, String fieldName, String parentFieldName,
			int parentOffset) {

			String[] ddmFieldsDisplayValues = getDDMFieldsDisplayValues(
				rootElement, true);

			if (ddmFieldsDisplayValues.length != 0) {
				return countDDMFieldRepetitions(
					ddmFieldsDisplayValues, fieldName, parentFieldName,
					parentOffset);
			}

			Element dynamicElementElement = getDynamicElementElementByName(
				rootElement, fieldName);

			if (dynamicElementElement != null) {
				return 1;
			}

			return 0;
		}

		protected int countDDMFieldRepetitions(
			String[] fieldsDisplayValues, String fieldName,
			String parentFieldName, int parentOffset) {

			int offset = -1;

			int repetitions = 0;

			for (String fieldDisplayName : fieldsDisplayValues) {
				if (offset > parentOffset) {
					break;
				}

				if (fieldDisplayName.equals(parentFieldName)) {
					offset++;
				}

				if (fieldDisplayName.equals(fieldName) &&
					(offset == parentOffset)) {

					repetitions++;
				}
			}

			return repetitions;
		}

		protected DDMFormFieldValue createDDMFormFieldValue(
				DDMFormField ddmFormField, DDMFieldsCounter ddmFieldsCounter,
				String fieldName, Element rootElement)
			throws PortalException {

			Value value = extractDDMFormFieldValue(
				ddmFormField, ddmFieldsCounter, fieldName, rootElement);

			if ((value == null) && !ddmFormField.isTransient()) {
				return null;
			}

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue(
				getDDMFieldInstanceId(
					rootElement, fieldName, ddmFieldsCounter.get(fieldName))) {

				{
					setName(fieldName);
					setValue(value);
				}
			};

			setNestedDDMFormFieldValues(
				ddmFormFieldValue, ddmFormField, rootElement, ddmFieldsCounter);

			ddmFieldsCounter.incrementKey(fieldName);

			return ddmFormFieldValue;
		}

		protected Value extractDDMFormFieldLocalizedValue(
			Element element, int index) {

			Value value = new LocalizedValue(getDefaultLocale(element));

			Map<String, Integer> dynamicContentValues = new HashMap<>();

			for (Element dynamicContentElement :
					element.elements("dynamic-content")) {

				String languageId = dynamicContentElement.attributeValue(
					"language-id");

				int localizedContentIndex = dynamicContentValues.getOrDefault(
					languageId, 0);

				if (localizedContentIndex == index) {
					value.addString(
						LocaleUtil.fromLanguageId(languageId),
						dynamicContentElement.getText());
				}

				dynamicContentValues.put(languageId, localizedContentIndex + 1);
			}

			return value;
		}

		protected Value extractDDMFormFieldValue(
			DDMFormField ddmFormField, DDMFieldsCounter ddmFieldsCounter,
			String fieldName, Element rootElement) {

			Value value = null;

			Element element = getDynamicElementElementByName(
				rootElement, fieldName);

			if (Validator.isNotNull(ddmFormField.getDataType()) &&
				(element != null)) {

				if (ddmFormField.isLocalizable()) {
					value = extractDDMFormFieldLocalizedValue(
						element, ddmFieldsCounter.get(fieldName));
				}
				else {
					value = new UnlocalizedValue(
						getDDMFormFieldValueValueString(
							element, getDefaultLocale(element),
							ddmFieldsCounter.get(fieldName)));
				}
			}

			return value;
		}

		protected Set<Locale> getAvailableLocales(
			Element dynamicElementElement) {

			Set<Locale> availableLocales = new LinkedHashSet<>();

			List<Element> dynamicContentElements =
				dynamicElementElement.elements("dynamic-content");

			for (Element dynamicContentElement : dynamicContentElements) {
				String languageId = dynamicContentElement.attributeValue(
					"language-id");

				availableLocales.add(LocaleUtil.fromLanguageId(languageId));
			}

			return availableLocales;
		}

		protected Set<Locale> getAvailableLocales(
			List<Element> dynamicElementElements) {

			Set<Locale> availableLocales = new LinkedHashSet<>();

			for (Element dynamicElementElement : dynamicElementElements) {
				availableLocales.addAll(
					getAvailableLocales(dynamicElementElement));
			}

			return availableLocales;
		}

		protected String getDDMFieldInstanceId(
			Element rootElement, String fieldName, int index) {

			String[] ddmFieldsDisplayValues = getDDMFieldsDisplayValues(
				rootElement, false);

			if (ddmFieldsDisplayValues.length == 0) {
				return StringUtil.randomString();
			}

			String prefix = fieldName.concat(DDMImpl.INSTANCE_SEPARATOR);

			for (String ddmFieldsDisplayValue : ddmFieldsDisplayValues) {
				if (ddmFieldsDisplayValue.startsWith(prefix)) {
					index--;

					if (index < 0) {
						return StringUtil.extractLast(
							ddmFieldsDisplayValue, DDMImpl.INSTANCE_SEPARATOR);
					}
				}
			}

			return null;
		}

		protected String[] getDDMFieldsDisplayValues(
			Element rootElement, boolean extractFieldName) {

			Element fieldsDisplayElement = getDynamicElementElementByName(
				rootElement, "_fieldsDisplay");

			if (fieldsDisplayElement == null) {
				return new String[0];
			}

			Element fieldsDisplayDynamicContent = fieldsDisplayElement.element(
				"dynamic-content");

			if (fieldsDisplayDynamicContent == null) {
				return new String[0];
			}

			String fieldsDisplayText = fieldsDisplayDynamicContent.getText();

			return TransformUtil.transform(
				StringUtil.split(fieldsDisplayText),
				fieldDisplayValue -> {
					if (!extractFieldName) {
						return fieldDisplayValue;
					}

					return StringUtil.extractFirst(
						fieldDisplayValue, DDMImpl.INSTANCE_SEPARATOR);
				},
				String.class);
		}

		protected DDMFormFieldValue getDDMFormFieldValue(
			Element dynamicElementElement) {

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setName(
				dynamicElementElement.attributeValue("name"));

			List<Element> dynamicContentElements =
				dynamicElementElement.elements("dynamic-content");

			ddmFormFieldValue.setValue(getValue(dynamicContentElements));

			ddmFormFieldValue.setNestedDDMFormFields(
				getDDMFormFieldValues(
					dynamicElementElement.elements("dynamic-element")));

			return ddmFormFieldValue;
		}

		protected String getDDMFormFieldValueValueString(
			Element dynamicElementElement, Locale locale, int index) {

			Element dynamicContentElement = getDynamicContentElement(
				dynamicElementElement, locale, index);

			return dynamicContentElement.getTextTrim();
		}

		protected List<DDMFormFieldValue> getDDMFormFieldValues(
			List<Element> dynamicElementElements) {

			if (dynamicElementElements == null) {
				return null;
			}

			return TransformUtil.transform(
				dynamicElementElements,
				dynamicElement -> getDDMFormFieldValue(dynamicElement));
		}

		protected Locale getDefaultLocale(Element dynamicElementElement) {
			if (dynamicElementElement == null) {
				String locale = null;

				try {
					locale = UpgradeProcessUtil.getDefaultLanguageId(
						_companyId);
				}
				catch (SQLException sqlException) {
					_log.error(
						"Unable to get default locale for company " +
							_companyId,
						sqlException);

					throw new RuntimeException(sqlException);
				}

				return LocaleUtil.fromLanguageId(locale);
			}

			String defaultLanguageId = dynamicElementElement.attributeValue(
				"default-language-id");

			return LocaleUtil.fromLanguageId(defaultLanguageId);
		}

		protected Locale getDefaultLocale(
			List<Element> dynamicElementElements) {

			for (Element dynamicElement : dynamicElementElements) {
				String defaultLanguageId = dynamicElement.attributeValue(
					"default-language-id");

				if (defaultLanguageId != null) {
					return LocaleUtil.fromLanguageId(defaultLanguageId);
				}
			}

			return null;
		}

		protected Element getDynamicContentElement(
			Element dynamicElementElement, Locale locale, int index) {

			String languageId = LocaleUtil.toLanguageId(locale);

			XPath dynamicContentXPath = SAXReaderUtil.createXPath(
				"dynamic-content[(@language-id='" + languageId + "')]");

			List<Node> nodes = dynamicContentXPath.selectNodes(
				dynamicElementElement);

			if (nodes.isEmpty()) {
				dynamicContentXPath = SAXReaderUtil.createXPath(
					"dynamic-content");

				nodes = dynamicContentXPath.selectNodes(dynamicElementElement);

				Element element = null;

				if (nodes.isEmpty()) {
					element = dynamicElementElement.addElement(
						"dynamic-content");
				}
				else {
					element = (Element)nodes.get(index);
				}

				element.addAttribute("language-id", languageId);

				return element;
			}

			return (Element)nodes.get(index);
		}

		protected Element getDynamicElementElementByName(
			Element rootElement, String fieldName) {

			XPath dynamicElementXPath = SAXReaderUtil.createXPath(
				"//dynamic-element[(@name=\"" + fieldName + "\")]");

			if (dynamicElementXPath.booleanValueOf(rootElement)) {
				return (Element)dynamicElementXPath.evaluate(rootElement);
			}

			return null;
		}

		protected Value getValue(List<Element> dynamicContentElements) {
			Value value = new LocalizedValue();

			for (Element dynamicContentElement : dynamicContentElements) {
				String fieldValue = dynamicContentElement.getText();

				String languageId = dynamicContentElement.attributeValue(
					"language-id");

				Locale locale = LocaleUtil.fromLanguageId(languageId);

				value.addString(locale, fieldValue);
			}

			return value;
		}

		protected void setDDMFormValuesAvailableLocales(
			DDMFormValues ddmFormValues, Element rootElement) {

			ddmFormValues.setAvailableLocales(
				getAvailableLocales(rootElement.elements("dynamic-element")));
		}

		protected void setDDMFormValuesDefaultLocale(
			DDMFormValues ddmFormValues, Element rootElement) {

			ddmFormValues.setDefaultLocale(
				getDefaultLocale(rootElement.elements("dynamic-element")));
		}

		protected void setNestedDDMFormFieldValues(
				DDMFormFieldValue ddmFormFieldValue, DDMFormField ddmFormField,
				Element rootElement, DDMFieldsCounter ddmFieldsCounter)
			throws PortalException {

			String fieldName = ddmFormFieldValue.getName();

			int parentOffset = ddmFieldsCounter.get(fieldName);

			String[] ddmFieldsDisplayValues = getDDMFieldsDisplayValues(
				rootElement, true);

			List<DDMFormField> nestedDDMFormFields =
				ddmFormField.getNestedDDMFormFields();

			for (DDMFormField nestedDDMFormField : nestedDDMFormFields) {
				String nestedDDMFormFieldName = nestedDDMFormField.getName();

				int repetitions = countDDMFieldRepetitions(
					ddmFieldsDisplayValues, nestedDDMFormFieldName, fieldName,
					parentOffset);

				for (int i = 0; i < repetitions; i++) {
					DDMFormFieldValue nestedDDMFormFieldValue =
						createDDMFormFieldValue(
							nestedDDMFormField, ddmFieldsCounter,
							nestedDDMFormFieldName, rootElement);

					if (nestedDDMFormFieldValue != null) {
						ddmFormFieldValue.addNestedDDMFormFieldValue(
							nestedDDMFormFieldValue);
					}
				}
			}
		}

		private long _companyId;

	}

	private static class DateDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		@Override
		public String getFieldType() {
			return DDMFormFieldType.DATE;
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			if (value != null) {
				for (Locale locale : value.getAvailableLocales()) {
					String valueString = value.getString(locale);

					if (Validator.isNull(valueString) ||
						!Validator.isNumber(valueString)) {

						continue;
					}

					Date dateValue = new Date(GetterUtil.getLong(valueString));

					value.addString(locale, _dateFormat.format(dateValue));
				}
			}
		}

		private final DateFormat _dateFormat =
			DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyy-MM-dd", TimeZoneUtil.getTimeZone("UTC"));

	}

	private class FileUploadDDMFormFieldValueTransformer
		implements DDMFormFieldValueTransformer {

		public FileUploadDDMFormFieldValueTransformer(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long entryId, String entryVersion,
			String entryModelName) {

			_groupId = groupId;
			_companyId = companyId;
			_userId = userId;
			_userName = userName;
			_createDate = createDate;
			_entryId = entryId;
			_entryVersion = entryVersion;
			_entryModelName = entryModelName;

			_dlFileEntryModelPermissions = ModelPermissionsFactory.create(
				_groupPermissions, _guestPermissions,
				DLFileEntry.class.getName());

			_dlFileEntryModelPermissions.addRolePermissions(
				RoleConstants.OWNER, _ownerPermissions);
		}

		@Override
		public String getFieldType() {
			return "ddm-fileupload";
		}

		@Override
		public void transform(DDMFormFieldValue ddmFormFieldValue)
			throws PortalException {

			Value value = ddmFormFieldValue.getValue();

			if (value != null) {
				for (Locale locale : value.getAvailableLocales()) {
					String valueString = value.getString(locale);

					if (Validator.isNull(valueString)) {
						continue;
					}

					String fileEntryUuid = PortalUUIDUtil.generate();

					upgradeFileUploadReference(
						fileEntryUuid, ddmFormFieldValue.getName(),
						valueString);

					value.addString(locale, toJSON(_groupId, fileEntryUuid));
				}
			}
		}

		protected void addAssetEntry(
				long entryId, long groupId, long companyId, long userId,
				String userName, Timestamp createDate, Timestamp modifiedDate,
				long classNameId, long classPK, String classUuid,
				long classTypeId, boolean visible, Timestamp startDate,
				Timestamp endDate, Timestamp publishDate,
				Timestamp expirationDate, String mimeType, String title,
				String description, String summary, String url,
				String layoutUuid, int height, int width, double priority,
				int viewCount)
			throws Exception {

			AssetEntry assetEntry = _assetEntryLocalService.createAssetEntry(
				entryId);

			assetEntry.setGroupId(groupId);
			assetEntry.setCompanyId(companyId);
			assetEntry.setUserId(userId);
			assetEntry.setUserName(userName);
			assetEntry.setCreateDate(createDate);
			assetEntry.setModifiedDate(modifiedDate);
			assetEntry.setClassNameId(classNameId);
			assetEntry.setClassPK(classPK);
			assetEntry.setClassUuid(classUuid);
			assetEntry.setClassTypeId(classTypeId);
			assetEntry.setVisible(visible);
			assetEntry.setStartDate(startDate);
			assetEntry.setEndDate(endDate);
			assetEntry.setPublishDate(publishDate);
			assetEntry.setExpirationDate(expirationDate);
			assetEntry.setMimeType(mimeType);
			assetEntry.setTitle(title);
			assetEntry.setDescription(description);
			assetEntry.setSummary(summary);
			assetEntry.setUrl(url);
			assetEntry.setLayoutUuid(layoutUuid);
			assetEntry.setHeight(height);
			assetEntry.setWidth(width);
			assetEntry.setPriority(priority);

			_assetEntryLocalService.updateAssetEntry(assetEntry);

			_viewCountEntryLocalService.incrementViewCount(
				companyId,
				_classNameLocalService.getClassNameId(AssetEntry.class),
				entryId, viewCount);
		}

		protected long addDDMDLFolder() throws Exception {
			long ddmFolderId = getDLFolderId(
				_groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "DDM");

			if (ddmFolderId > 0) {
				return ddmFolderId;
			}

			ddmFolderId = increment();

			addDLFolder(
				PortalUUIDUtil.generate(), ddmFolderId, _groupId, _companyId,
				_userId, _userName, _now, _now, _groupId,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "DDM",
				StringPool.BLANK, _now);

			return ddmFolderId;
		}

		protected DLFileEntry addDLFileEntry(
				String uuid, long fileEntryId, long groupId, long companyId,
				long userId, String userName, Timestamp createDate,
				Timestamp modifiedDate, long classNameId, long classPK,
				long repositoryId, long folderId, String treePath, String name,
				String fileName, String extension, String mimeType,
				String title, String description, String extraSettings,
				long fileEntryTypeId, String version, long size, int readCount,
				long smallImageId, long largeImageId, long custom1ImageId,
				long custom2ImageId, boolean manualCheckInRequired)
			throws Exception {

			DLFileEntry dlFileEntry =
				_dlFileEntryLocalService.createDLFileEntry(fileEntryId);

			dlFileEntry.setUuid(uuid);
			dlFileEntry.setGroupId(groupId);
			dlFileEntry.setCompanyId(companyId);
			dlFileEntry.setUserId(userId);
			dlFileEntry.setUserName(userName);
			dlFileEntry.setCreateDate(createDate);
			dlFileEntry.setModifiedDate(modifiedDate);
			dlFileEntry.setClassNameId(classNameId);
			dlFileEntry.setClassPK(classPK);
			dlFileEntry.setRepositoryId(repositoryId);
			dlFileEntry.setFolderId(folderId);
			dlFileEntry.setTreePath(treePath);
			dlFileEntry.setName(name);
			dlFileEntry.setFileName(fileName);
			dlFileEntry.setExtension(extension);
			dlFileEntry.setMimeType(mimeType);
			dlFileEntry.setTitle(title);
			dlFileEntry.setDescription(description);
			dlFileEntry.setExtraSettings(extraSettings);
			dlFileEntry.setFileEntryTypeId(fileEntryTypeId);
			dlFileEntry.setVersion(version);
			dlFileEntry.setSize(size);
			dlFileEntry.setSmallImageId(smallImageId);
			dlFileEntry.setLargeImageId(largeImageId);
			dlFileEntry.setCustom1ImageId(custom1ImageId);
			dlFileEntry.setCustom2ImageId(custom2ImageId);
			dlFileEntry.setManualCheckInRequired(manualCheckInRequired);

			_viewCountEntryLocalService.incrementViewCount(
				dlFileEntry.getCompanyId(),
				_classNameLocalService.getClassNameId(DLFileEntry.class),
				dlFileEntry.getFileEntryId(), readCount);

			return dlFileEntry;
		}

		protected void addDLFileVersion(
				String uuid, long fileVersionId, long groupId, long companyId,
				long userId, String userName, Timestamp createDate,
				Timestamp modifiedDate, long repositoryId, long folderId,
				long fileEntryId, String treePath, String fileName,
				String extension, String mimeType, String title,
				String description, String changeLog, String extraSettings,
				long fileEntryTypeId, String version, long size,
				String checksum, int status, long statusByUserId,
				String statusByUserName, Timestamp statusDate)
			throws Exception {

			DLFileVersion dlFileVersion =
				_dlFileVersionLocalService.createDLFileVersion(fileVersionId);

			dlFileVersion.setUuid(uuid);
			dlFileVersion.setGroupId(groupId);
			dlFileVersion.setCompanyId(companyId);
			dlFileVersion.setUserId(userId);
			dlFileVersion.setUserName(userName);
			dlFileVersion.setCreateDate(createDate);
			dlFileVersion.setModifiedDate(modifiedDate);
			dlFileVersion.setRepositoryId(repositoryId);
			dlFileVersion.setFolderId(folderId);
			dlFileVersion.setFileEntryId(fileEntryId);
			dlFileVersion.setTreePath(treePath);
			dlFileVersion.setFileName(fileName);
			dlFileVersion.setExtension(extension);
			dlFileVersion.setMimeType(mimeType);
			dlFileVersion.setTitle(title);
			dlFileVersion.setDescription(description);
			dlFileVersion.setChangeLog(changeLog);
			dlFileVersion.setExtraSettings(extraSettings);
			dlFileVersion.setFileEntryTypeId(fileEntryTypeId);
			dlFileVersion.setVersion(version);
			dlFileVersion.setSize(size);
			dlFileVersion.setChecksum(checksum);
			dlFileVersion.setStatus(status);
			dlFileVersion.setStatusByUserId(statusByUserId);
			dlFileVersion.setStatusByUserName(statusByUserName);
			dlFileVersion.setStatusDate(statusDate);

			_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);
		}

		protected void addDLFolder(
				String uuid, long folderId, long groupId, long companyId,
				long userId, String userName, Timestamp createDate,
				Timestamp modifiedDate, long repositoryId, long parentFolderId,
				String name, String description, Timestamp lastPostDate)
			throws Exception {

			DLFolder dlFolder = _dlFolderLocalService.createDLFolder(folderId);

			dlFolder.setUuid(uuid);
			dlFolder.setGroupId(groupId);
			dlFolder.setCompanyId(companyId);
			dlFolder.setUserId(userId);
			dlFolder.setUserName(userName);
			dlFolder.setCreateDate(createDate);
			dlFolder.setModifiedDate(modifiedDate);
			dlFolder.setRepositoryId(repositoryId);
			dlFolder.setMountPoint(false);
			dlFolder.setParentFolderId(parentFolderId);
			dlFolder.setName(name);
			dlFolder.setDescription(description);
			dlFolder.setLastPostDate(lastPostDate);
			dlFolder.setDefaultFileEntryTypeId(0);
			dlFolder.setHidden(false);
			dlFolder.setRestrictionType(0);
			dlFolder.setStatus(WorkflowConstants.STATUS_APPROVED);
			dlFolder.setStatusByUserId(0);
			dlFolder.setStatusByUserName(StringPool.BLANK);

			dlFolder = _dlFolderLocalService.updateDLFolder(dlFolder);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setModelPermissions(_dlFolderModelPermissions);

			_resourceLocalService.addModelResources(dlFolder, serviceContext);
		}

		protected long addDLFolderTree(String ddmFormFieldName)
			throws Exception {

			long ddmFolderId = addDDMDLFolder();

			long entryIdFolderId = addEntryIdDLFolder(ddmFolderId);

			long entryVersionFolderId = addEntryVersionDLFolder(
				entryIdFolderId);

			long fieldNameFolderId = increment();

			addDLFolder(
				PortalUUIDUtil.generate(), fieldNameFolderId, _groupId,
				_companyId, _userId, _userName, _now, _now, _groupId,
				entryVersionFolderId, ddmFormFieldName, StringPool.BLANK, _now);

			return fieldNameFolderId;
		}

		protected long addEntryIdDLFolder(long ddmFolderId) throws Exception {
			long entryIdFolderId = getDLFolderId(
				_groupId, ddmFolderId, String.valueOf(_entryId));

			if (entryIdFolderId > 0) {
				return entryIdFolderId;
			}

			entryIdFolderId = increment();

			addDLFolder(
				PortalUUIDUtil.generate(), entryIdFolderId, _groupId,
				_companyId, _userId, _userName, _now, _now, _groupId,
				ddmFolderId, String.valueOf(_entryId), StringPool.BLANK, _now);

			return entryIdFolderId;
		}

		protected long addEntryVersionDLFolder(long entryIdFolderId)
			throws Exception {

			long entryVersionFolderId = getDLFolderId(
				_groupId, entryIdFolderId, _entryVersion);

			if (entryVersionFolderId > 0) {
				return entryVersionFolderId;
			}

			entryVersionFolderId = increment();

			addDLFolder(
				PortalUUIDUtil.generate(), entryVersionFolderId, _groupId,
				_companyId, _userId, _userName, _now, _now, _groupId,
				entryIdFolderId, _entryVersion, StringPool.BLANK, _now);

			return entryVersionFolderId;
		}

		protected File fetchFile(String filePath) throws Exception {
			try {
				return FileUtil.createTempFile(
					_store.getFileAsStream(
						_companyId, CompanyConstants.SYSTEM, filePath,
						StringPool.BLANK));
			}
			catch (PortalException portalException) {
				_log.error(
					String.format(
						"Unable to find the binary file with path \"%s\" " +
							"referenced by %s",
						filePath, getModelInfo()));

				throw portalException;
			}
		}

		protected long getDLFolderId(
			long groupId, long parentFolderId, String name) {

			DLFolder dlFolder = _dlFolderLocalService.fetchFolder(
				groupId, parentFolderId, name);

			if (dlFolder == null) {
				return 0;
			}

			return dlFolder.getFolderId();
		}

		protected String getExtension(String fileName) {
			String extension = StringPool.BLANK;

			int pos = fileName.lastIndexOf(CharPool.PERIOD);

			if (pos > 0) {
				extension = fileName.substring(pos + 1);
			}

			return StringUtil.toLowerCase(extension);
		}

		protected String getModelInfo() {
			return String.format(
				"%s {primaryKey: %d, version: %s}", _entryModelName, _entryId,
				_entryVersion);
		}

		protected String toJSON(long groupId, String fileEntryUuid) {
			return JSONUtil.put(
				"groupId", groupId
			).put(
				"uuid", fileEntryUuid
			).toString();
		}

		protected String upgradeFileUploadReference(
				String fileEntryUuid, String ddmFormFieldName,
				String valueString)
			throws PortalException {

			try {
				long dlFolderId = addDLFolderTree(ddmFormFieldName);

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					valueString);

				String name = String.valueOf(
					increment(DLFileEntry.class.getName()));

				String fileName = jsonObject.getString("name");
				String filePath = jsonObject.getString("path");

				// File entry

				long fileEntryId = increment();

				String extension = getExtension(fileName);

				File file = fetchFile(filePath);

				DLFileEntry dlFileEntry = addDLFileEntry(
					fileEntryUuid, fileEntryId, _groupId, _companyId, _userId,
					_userName, _createDate, _createDate, 0, 0, _groupId,
					dlFolderId, StringPool.BLANK, name, fileName, extension,
					MimeTypesUtil.getContentType(fileName), fileName,
					StringPool.BLANK, StringPool.BLANK,
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
					DLFileEntryConstants.VERSION_DEFAULT, file.length(),
					DLFileEntryConstants.DEFAULT_READ_COUNT, 0, 0, 0, 0, false);

				// File version

				addDLFileVersion(
					fileEntryUuid, increment(), _groupId, _companyId, _userId,
					_userName, _createDate, _createDate, _groupId, dlFolderId,
					fileEntryId, StringPool.BLANK, fileName, extension,
					MimeTypesUtil.getContentType(fileName), fileName,
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
					DLFileEntryConstants.VERSION_DEFAULT, file.length(),
					StringPool.BLANK, WorkflowConstants.STATUS_APPROVED,
					_userId, _userName, _createDate);

				dlFileEntry = _dlFileEntryLocalService.updateDLFileEntry(
					dlFileEntry);

				// Resources

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setModelPermissions(
					_dlFileEntryModelPermissions);

				_resourceLocalService.addModelResources(
					dlFileEntry, serviceContext);

				// Asset entry

				addAssetEntry(
					increment(), _groupId, _companyId, _userId, _userName,
					_createDate, _createDate,
					PortalUtil.getClassNameId(DLFileEntry.class), fileEntryId,
					fileEntryUuid,
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
					false, null, null, null, null,
					MimeTypesUtil.getContentType(fileName), fileName,
					StringPool.BLANK, StringPool.BLANK, null, null, 0, 0, 0, 0);

				// File

				try (InputStream inputStream = new FileInputStream(file)) {
					_store.addFile(
						_companyId, dlFolderId, name, Store.VERSION_DEFAULT,
						inputStream);
				}

				file.delete();

				return fileEntryUuid;
			}
			catch (Exception exception) {
				throw new UpgradeException(exception);
			}
		}

		private final long _companyId;
		private final Timestamp _createDate;
		private final ModelPermissions _dlFileEntryModelPermissions;
		private final long _entryId;
		private final String _entryModelName;
		private final String _entryVersion;
		private final long _groupId;
		private final String[] _groupPermissions = {"ADD_DISCUSSION", "VIEW"};
		private final String[] _guestPermissions = {"ADD_DISCUSSION", "VIEW"};
		private final Timestamp _now = new Timestamp(
			System.currentTimeMillis());
		private final String[] _ownerPermissions = {
			"ADD_DISCUSSION", "DELETE", "DELETE_DISCUSSION",
			"OVERRIDE_CHECKOUT", "PERMISSIONS", "UPDATE", "UPDATE_DISCUSSION",
			"VIEW"
		};
		private final long _userId;
		private final String _userName;

	}

}