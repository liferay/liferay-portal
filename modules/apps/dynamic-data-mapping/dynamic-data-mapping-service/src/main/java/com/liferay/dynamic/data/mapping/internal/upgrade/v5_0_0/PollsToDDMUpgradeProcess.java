/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_0_0;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceConstants;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesSerializeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.ConcurrentHashMapBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class PollsToDDMUpgradeProcess extends UpgradeProcess {

	public PollsToDDMUpgradeProcess(
		DDMFormLayoutSerializer ddmFormLayoutSerializer,
		DDMFormSerializer ddmFormSerializer,
		DDMFormValuesSerializer ddmFormValuesSerializer,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_ddmFormLayoutSerializer = ddmFormLayoutSerializer;
		_ddmFormSerializer = ddmFormSerializer;
		_ddmFormValuesSerializer = ddmFormValuesSerializer;
		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	/**
	 * @see com.liferay.polls.internal.upgrade.v1_0_0.UpgradeLastPublishDate
	 */
	public class UpgradeLastPublishDate
		extends com.liferay.portal.upgrade.v7_0_0.UpgradeLastPublishDate {

		protected void addLastPublishDateColumns() throws Exception {
			try (LoggingTimer loggingTimer = new LoggingTimer()) {
				addLastPublishDateColumn("PollsChoice");
				addLastPublishDateColumn("PollsQuestion");
				addLastPublishDateColumn("PollsVote");
			}
		}

		@Override
		protected void doUpgrade() throws Exception {
			addLastPublishDateColumns();

			//updateLastPublishDates(PollsPortletKeys.POLLS, "PollsChoice");
			//updateLastPublishDates(PollsPortletKeys.POLLS, "PollsQuestion");
			//updateLastPublishDates(PollsPortletKeys.POLLS, "PollsVote");
		}

	}

	protected void createDDDMFormFieldOptions(
		DDMFormFieldOptions ddmFormFieldOptions, String description,
		String name, String optionValue) {

		for (String availableLanguageId :
				LocalizationUtil.getAvailableLanguageIds(description)) {

			Locale availableLocale = LocaleUtil.fromLanguageId(
				availableLanguageId);

			ddmFormFieldOptions.addOptionLabel(
				optionValue, availableLocale,
				_getOptionLabel(availableLocale, description, name));
		}

		ddmFormFieldOptions.addOptionReference(optionValue, optionValue);
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeDDMFormInstance();

		if (!hasTable("PollsQuestion")) {
			return;
		}

		if (!hasColumn("PollsQuestion", "lastPublishDate")) {
			UpgradeLastPublishDate upgradeLastPublishDate =
				new UpgradeLastPublishDate();

			upgradeLastPublishDate.upgrade();
		}

		_upgradePollsQuestions();

		runSQL("delete from PollsVote");

		runSQL("delete from PollsChoice");

		runSQL("delete from PollsQuestion");
	}

	protected DDMForm getDDMForm(DDMFormField ddmFormField) {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(_availableLocales);
		ddmForm.setDDMFormFields(ListUtil.fromArray(ddmFormField));
		ddmForm.setDefaultLocale(_defaultLocale);

		return ddmForm;
	}

	protected DDMFormField getDDMFormField(
		DDMFormFieldOptions ddmFormFieldOptions, String description) {

		String ddmFormFieldLabel = LanguageUtil.get(
			_getResourceBundle(), "radio-field-type-label");

		DDMFormField ddmFormField = new DDMFormField(
			DDMFormFieldUtil.getDDMFormFieldName(ddmFormFieldLabel),
			DDMFormFieldTypeConstants.RADIO);

		ddmFormField.setDataType("string");
		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		LocalizedValue localizedValue = new LocalizedValue(_defaultLocale);

		for (String availableLanguageId :
				LocalizationUtil.getAvailableLanguageIds(description)) {

			Locale availableLocale = LocaleUtil.fromLanguageId(
				availableLanguageId);

			localizedValue.addString(
				availableLocale,
				LocalizationUtil.getLocalization(
					description, LocaleUtil.toLanguageId(availableLocale)));
		}

		ddmFormField.setLabel(localizedValue);
		ddmFormField.setLocalizable(true);
		ddmFormField.setProperty("inline", false);
		ddmFormField.setProperty("instanceId", StringUtil.randomString(8));
		ddmFormField.setProperty("visible", true);
		ddmFormField.setRequired(true);
		ddmFormField.setShowLabel(true);

		return ddmFormField;
	}

	protected String getDDMFormLayoutDefinition(DDMFormField ddmFormField) {
		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		ddmFormLayout.addDDMFormLayoutPage(_getDDMFormLayoutPage(ddmFormField));
		ddmFormLayout.setDefaultLocale(_defaultLocale);
		ddmFormLayout.setPaginationMode(DDMFormLayout.MULTI_PAGES);

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				_ddmFormLayoutSerializer.serialize(
					DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
						ddmFormLayout
					).build());

		return ddmFormLayoutSerializerSerializeResponse.getContent();
	}

	protected JSONObject getDataJSONObject(String ddmFormFieldName) {
		return JSONUtil.put(
			ddmFormFieldName,
			JSONUtil.put(
				"type", DDMFormFieldTypeConstants.RADIO
			).put(
				"values", JSONFactoryUtil.createJSONObject()
			)
		).put(
			"totalItems", 0
		);
	}

	protected String getSerializedSettingsDDMFormValues() {
		DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceSettings.class);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(ddmForm.getAvailableLocales());
		ddmFormValues.setDefaultLocale(ddmForm.getDefaultLocale());

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setFieldReference(
				ddmFormField.getFieldReference());
			ddmFormFieldValue.setName(ddmFormField.getName());

			if (StringUtil.equals(
					ddmFormField.getName(), "convertedFromPolls")) {

				ddmFormFieldValue.setValue(
					new UnlocalizedValue(Boolean.TRUE.toString()));
			}
			else {
				ddmFormFieldValue.setValue(
					_getSettingDefaultValue(ddmForm, ddmFormField));
			}

			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return DDMFormValuesSerializeUtil.serialize(
			ddmFormValues, _ddmFormValuesSerializer);
	}

	private long _addDDMField(
			long companyId, long storageId, long structureVersionId,
			String fieldName, String fieldType, String instanceId,
			boolean localizable, int priority)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMField (mvccVersion, ctCollectionId, ",
					"fieldId, companyId, parentFieldId, storageId, ",
					"structureVersionId, fieldName, fieldType, instanceId, ",
					"localizable, priority) values (0, 0, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?)"))) {

			long fieldId = increment(DDMField.class.getName());

			preparedStatement.setLong(1, fieldId);

			preparedStatement.setLong(2, companyId);
			preparedStatement.setLong(
				3, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID);
			preparedStatement.setLong(4, storageId);
			preparedStatement.setLong(5, structureVersionId);
			preparedStatement.setString(6, fieldName);
			preparedStatement.setString(7, fieldType);
			preparedStatement.setString(8, instanceId);
			preparedStatement.setBoolean(9, localizable);
			preparedStatement.setInt(10, priority);

			preparedStatement.executeUpdate();

			return fieldId;
		}
	}

	private void _addDDMFieldAttribute(
			long companyId, long fieldId, long storageId, String attributeName,
			String languageId, String attributeValue)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMFieldAttribute (mvccVersion, ",
					"ctCollectionId, fieldAttributeId, companyId, fieldId, ",
					"storageId, attributeName, languageId, ",
					"largeAttributeValue, smallAttributeValue) values (0, 0, ",
					"?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(
				1, increment(DDMFieldAttribute.class.getName()));
			preparedStatement.setLong(2, companyId);
			preparedStatement.setLong(3, fieldId);
			preparedStatement.setLong(4, storageId);
			preparedStatement.setString(5, attributeName);
			preparedStatement.setString(6, languageId);

			String largeAttributeValue = null;
			String smallAttributeValue = null;

			if ((attributeValue != null) && (attributeValue.length() > 255)) {
				largeAttributeValue = attributeValue;
			}
			else {
				smallAttributeValue = attributeValue;
			}

			preparedStatement.setString(7, largeAttributeValue);
			preparedStatement.setString(8, smallAttributeValue);

			preparedStatement.executeUpdate();
		}
	}

	private void _addDDMFormInstance(
			long formInstanceId, long groupId, long companyId, long userId,
			String userName, Timestamp createDate, Timestamp modifiedDate,
			long structureId, String name, String settings,
			Timestamp expirationDate, Timestamp lastPublishDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMFormInstance (mvccVersion, ",
					"ctCollectionId, uuid_, formInstanceId, groupId, ",
					"companyId, userId, userName, versionUserId, ",
					"versionUserName, createDate, modifiedDate, structureId, ",
					"version, name, description, settings_, expirationDate, ",
					"lastPublishDate) values (0, 0, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, formInstanceId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setLong(7, userId);
			preparedStatement.setString(8, userName);
			preparedStatement.setTimestamp(9, createDate);
			preparedStatement.setTimestamp(10, modifiedDate);
			preparedStatement.setLong(11, structureId);
			preparedStatement.setString(
				12, DDMFormInstanceConstants.VERSION_DEFAULT);
			preparedStatement.setString(13, name);
			preparedStatement.setString(14, StringPool.BLANK);
			preparedStatement.setString(15, settings);
			preparedStatement.setTimestamp(16, expirationDate);
			preparedStatement.setTimestamp(17, lastPublishDate);

			preparedStatement.executeUpdate();
		}
	}

	private long _addDDMFormInstanceRecord(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, Timestamp modifiedDate, long formInstanceId,
			long storageId, Timestamp lastPublishDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMFormInstanceRecord (mvccVersion, ",
					"ctCollectionId, uuid_, formInstanceRecordId, groupId, ",
					"companyId, userId, userName, versionUserId, ",
					"versionUserName, createDate, modifiedDate, ",
					"formInstanceId, formInstanceVersion, storageId, version, ",
					"ipAddress, lastPublishDate) values (0, 0, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, null, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());

			long formInstanceRecordId = increment();

			preparedStatement.setLong(2, formInstanceRecordId);

			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setLong(7, userId);
			preparedStatement.setString(8, userName);
			preparedStatement.setTimestamp(9, createDate);
			preparedStatement.setTimestamp(10, modifiedDate);
			preparedStatement.setLong(11, formInstanceId);
			preparedStatement.setString(
				12, DDMFormInstanceConstants.VERSION_DEFAULT);
			preparedStatement.setLong(13, storageId);
			preparedStatement.setString(
				14, DDMFormInstanceConstants.VERSION_DEFAULT);
			preparedStatement.setTimestamp(15, lastPublishDate);

			preparedStatement.executeUpdate();

			return formInstanceRecordId;
		}
	}

	private void _addDDMFormInstanceRecordVersion(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long formInstanceId,
			long formInstanceRecordId, long storageId, Timestamp statusDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMFormInstanceRecordVersion (mvccVersion, ",
					"ctCollectionId, formInstanceRecordVersionId, groupId, ",
					"companyId, userId, userName, createDate, formInstanceId, ",
					"formInstanceVersion, formInstanceRecordId, version, ",
					"storageId, status, statusByUserId, statusByUserName, ",
					"statusDate) values (0, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, increment());
			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(3, companyId);
			preparedStatement.setLong(4, userId);
			preparedStatement.setString(5, userName);
			preparedStatement.setTimestamp(6, createDate);
			preparedStatement.setLong(7, formInstanceId);
			preparedStatement.setString(
				8, DDMFormInstanceConstants.VERSION_DEFAULT);
			preparedStatement.setLong(9, formInstanceRecordId);
			preparedStatement.setString(
				10, DDMFormInstanceConstants.VERSION_DEFAULT);
			preparedStatement.setLong(11, storageId);
			preparedStatement.setInt(12, WorkflowConstants.STATUS_APPROVED);
			preparedStatement.setLong(13, userId);
			preparedStatement.setString(14, userName);
			preparedStatement.setTimestamp(15, statusDate);

			preparedStatement.executeUpdate();
		}
	}

	private void _addDDMFormInstanceReport(
			long groupId, long companyId, Timestamp createDate,
			Timestamp modifiedDate, long formInstanceId,
			JSONObject dataJSONObject)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMFormInstanceReport (formInstanceReportId, ",
					"groupId, companyId, createDate, modifiedDate, ",
					"formInstanceId, data_) values (?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, increment());
			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(3, companyId);
			preparedStatement.setTimestamp(4, createDate);
			preparedStatement.setTimestamp(5, modifiedDate);
			preparedStatement.setLong(6, formInstanceId);
			preparedStatement.setString(7, dataJSONObject.toString());

			preparedStatement.executeUpdate();
		}
	}

	private void _addDDMFormInstanceVersion(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long formInstanceId, long structureVersionId,
			String name, String settings, Timestamp statusDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMFormInstanceVersion (mvccVersion, ",
					"ctCollectionId, formInstanceVersionId, groupId, ",
					"companyId, userId, userName, createDate, formInstanceId, ",
					"structureVersionId, name, description, settings_, ",
					"version, status, statusByUserId, statusByUserName, ",
					"statusDate) values (0, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, increment());
			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(3, companyId);
			preparedStatement.setLong(4, userId);
			preparedStatement.setString(5, userName);
			preparedStatement.setTimestamp(6, createDate);
			preparedStatement.setLong(7, formInstanceId);
			preparedStatement.setLong(8, structureVersionId);
			preparedStatement.setString(9, name);
			preparedStatement.setString(10, StringPool.BLANK);
			preparedStatement.setString(11, settings);
			preparedStatement.setString(
				12, DDMFormInstanceConstants.VERSION_DEFAULT);
			preparedStatement.setInt(13, WorkflowConstants.STATUS_APPROVED);
			preparedStatement.setLong(14, userId);
			preparedStatement.setString(15, userName);
			preparedStatement.setTimestamp(16, statusDate);

			preparedStatement.executeUpdate();
		}
	}

	private long _addDDMStorageLink(
			long companyId, long structureId, long structureVersionId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMStorageLink (mvccVersion, ctCollectionId, ",
					"uuid_, storageLinkId, companyId, classNameId, classPK, ",
					"structureId, structureVersionId) values (0, 0, ?, ?, ?, ",
					"?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, increment());
			preparedStatement.setLong(3, companyId);
			preparedStatement.setLong(
				4, PortalUtil.getClassNameId(DDMContent.class));

			long storageId = increment();

			preparedStatement.setLong(5, storageId);

			preparedStatement.setLong(6, structureId);
			preparedStatement.setLong(7, structureVersionId);

			preparedStatement.executeUpdate();

			return storageId;
		}
	}

	private long _addDDMStructure(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, Timestamp modifiedDate, String name,
			String definition, Timestamp lastPublishDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMStructure (mvccVersion, ctCollectionId, ",
					"uuid_, structureId, groupId, companyId, userId, ",
					"userName, versionUserId, versionUserName, createDate, ",
					"modifiedDate, parentStructureId, classNameId, ",
					"structureKey, version, name, description, definition, ",
					"storageType, type_, lastPublishDate) values (0, 0, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());

			long structureId = increment();

			preparedStatement.setLong(2, structureId);

			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setLong(7, userId);
			preparedStatement.setString(8, userName);
			preparedStatement.setTimestamp(9, createDate);
			preparedStatement.setTimestamp(10, modifiedDate);
			preparedStatement.setLong(
				11, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID);
			preparedStatement.setLong(
				12, PortalUtil.getClassNameId(DDMFormInstance.class));
			preparedStatement.setString(13, String.valueOf(increment()));
			preparedStatement.setString(
				14, DDMStructureConstants.VERSION_DEFAULT);
			preparedStatement.setString(15, name);
			preparedStatement.setString(16, StringPool.BLANK);
			preparedStatement.setString(17, definition);
			preparedStatement.setString(18, StorageType.DEFAULT.toString());
			preparedStatement.setInt(19, DDMStructureConstants.TYPE_AUTO);
			preparedStatement.setTimestamp(20, lastPublishDate);

			preparedStatement.executeUpdate();

			return structureId;
		}
	}

	private void _addDDMStructureLayout(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, Timestamp modifiedDate,
			long structureVersionId, String name, String definition)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMStructureLayout (mvccVersion, ",
					"ctCollectionId, uuid_, structureLayoutId, groupId, ",
					"companyId, userId, userName, createDate, modifiedDate, ",
					"classNameId, structureLayoutKey, structureVersionId, ",
					"name, description, definition) values (0, 0, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, increment());
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, modifiedDate);
			preparedStatement.setLong(
				9, PortalUtil.getClassNameId(DDMFormInstance.class));
			preparedStatement.setString(10, String.valueOf(increment()));
			preparedStatement.setLong(11, structureVersionId);
			preparedStatement.setString(12, name);
			preparedStatement.setString(13, StringPool.BLANK);
			preparedStatement.setString(14, definition);

			preparedStatement.executeUpdate();
		}
	}

	private long _addDDMStructureVersion(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long structureId, String name,
			String definition, Timestamp statusDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DDMStructureVersion (mvccVersion, ",
					"ctCollectionId, structureVersionId, groupId, companyId, ",
					"userId, userName, createDate, structureId, version, ",
					"parentStructureId, name, description, definition, ",
					"storageType, type_, status, statusByUserId, ",
					"statusByUserName, statusDate) values (0, 0, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			long structureVersionId = increment();

			preparedStatement.setLong(1, structureVersionId);

			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(3, companyId);
			preparedStatement.setLong(4, userId);
			preparedStatement.setString(5, userName);
			preparedStatement.setTimestamp(6, createDate);
			preparedStatement.setLong(7, structureId);
			preparedStatement.setString(
				8, DDMStructureConstants.VERSION_DEFAULT);
			preparedStatement.setLong(
				9, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID);
			preparedStatement.setString(10, name);
			preparedStatement.setString(11, StringPool.BLANK);
			preparedStatement.setString(12, definition);
			preparedStatement.setString(13, StorageType.DEFAULT.toString());
			preparedStatement.setInt(14, DDMStructureConstants.TYPE_AUTO);
			preparedStatement.setInt(15, WorkflowConstants.STATUS_APPROVED);
			preparedStatement.setLong(16, userId);
			preparedStatement.setString(17, userName);
			preparedStatement.setTimestamp(18, statusDate);

			preparedStatement.executeUpdate();

			return structureVersionId;
		}
	}

	private void _addLanguageDDMField(
			long companyId, long storageId, long structureVersionId)
		throws Exception {

		long fieldId = _addDDMField(
			companyId, storageId, structureVersionId, null, null, null, false,
			0);

		_addDDMFieldAttribute(
			companyId, fieldId, storageId, "availableLanguageIds", null,
			com.liferay.petra.string.StringUtil.merge(
				_availableLocales, LocaleUtil::toLanguageId, StringPool.COMMA));
		_addDDMFieldAttribute(
			companyId, fieldId, storageId, "defaultLanguageId", null,
			LocaleUtil.toLanguageId(_defaultLocale));
	}

	private void _addRadioDDMField(
			long companyId, long storageId, long structureVersionId,
			String fieldName, String instanceId, String optionValue)
		throws Exception {

		long fieldId = _addDDMField(
			companyId, storageId, structureVersionId, fieldName,
			DDMFormFieldTypeConstants.RADIO, instanceId, true, 1);

		for (Locale availableLocale : _availableLocales) {
			String value = StringPool.BLANK;

			if (availableLocale == _defaultLocale) {
				value = optionValue;
			}

			_addDDMFieldAttribute(
				companyId, fieldId, storageId, null,
				LocaleUtil.toLanguageId(availableLocale), value);
		}
	}

	private long _getActionIds(long oldActionIds) {
		long sum = 0;

		Set<String> actionsIds = new HashSet<>();

		for (ResourceAction resourceAction :
				_resourceActionLocalService.getResourceActions(
					_CLASS_NAME_POLLS)) {

			long bitwiseValue = resourceAction.getBitwiseValue();

			if ((oldActionIds & bitwiseValue) != bitwiseValue) {
				continue;
			}

			actionsIds.add(
				MapUtil.getString(
					_resourceActionIds, resourceAction.getActionId()));
		}

		for (String actionId : actionsIds) {
			sum += MapUtil.getLong(
				_getDDMFormInstanceResourceActions(), actionId);
		}

		return sum;
	}

	private Map<Long, String> _getDDMFormFieldOptionsValues(
			DDMFormFieldOptions ddmFormFieldOptions, long questionId)
		throws Exception {

		Map<Long, String> ddmFormFieldOptionsValues = new LinkedHashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select choiceId, description, name from PollsChoice where " +
					"questionId = ? order by name")) {

			preparedStatement.setLong(1, questionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String optionValue = DDMFormFieldUtil.getDDMFormFieldName(
						LanguageUtil.get(_getResourceBundle(), "option"));

					ddmFormFieldOptionsValues.put(
						resultSet.getLong("choiceId"), optionValue);

					createDDDMFormFieldOptions(
						ddmFormFieldOptions, resultSet.getString("description"),
						resultSet.getString("name"), optionValue);
				}
			}
		}

		return ddmFormFieldOptionsValues;
	}

	private Map<String, Long> _getDDMFormInstanceResourceActions() {
		Map<String, Long> ddmFormInstanceResourceActions = new HashMap<>();

		for (ResourceAction resourceAction :
				_resourceActionLocalService.getResourceActions(
					_CLASS_NAME_DDM_FORM_INSTANCE)) {

			ddmFormInstanceResourceActions.put(
				resourceAction.getActionId(), resourceAction.getBitwiseValue());
		}

		return ddmFormInstanceResourceActions;
	}

	private DDMFormLayoutPage _getDDMFormLayoutPage(DDMFormField ddmFormField) {
		DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

		ddmFormLayoutPage.addDDMFormLayoutRow(
			_getDDMFormLayoutRow(ddmFormField));
		ddmFormLayoutPage.setTitle(
			_getLocalizedValue(_defaultLocale, StringPool.BLANK));

		return ddmFormLayoutPage;
	}

	private DDMFormLayoutRow _getDDMFormLayoutRow(DDMFormField ddmFormField) {
		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		ddmFormLayoutRow.addDDMFormLayoutColumn(
			new DDMFormLayoutColumn(
				DDMFormLayoutColumn.FULL, ddmFormField.getName()));

		return ddmFormLayoutRow;
	}

	private LocalizedValue _getLocalizedValue(Locale locale, String value) {
		LocalizedValue localizedValue = new LocalizedValue(locale);

		localizedValue.addString(locale, value);

		return localizedValue;
	}

	private String _getName(String title) {
		Map<Locale, String> localizations = new HashMap<>();

		for (Locale availableLocale : _availableLocales) {
			localizations.put(
				availableLocale,
				LocalizationUtil.getLocalization(
					title, LocaleUtil.toLanguageId(availableLocale)));
		}

		return LocalizationUtil.updateLocalization(
			localizations, StringPool.BLANK, "Name",
			LocaleUtil.toLanguageId(_defaultLocale));
	}

	private String _getOptionLabel(
		Locale availableLocale, String description, String name) {

		return StringBundler.concat(
			name, StringPool.PERIOD, StringPool.SPACE,
			LocalizationUtil.getLocalization(
				description, LocaleUtil.toLanguageId(availableLocale)));
	}

	private ResourceBundle _getResourceBundle() {
		return new AggregateResourceBundle(
			ResourceBundleUtil.getBundle(
				"content.Language", _defaultLocale, getClass()),
			PortalUtil.getResourceBundle(_defaultLocale));
	}

	private Value _getSettingDefaultValue(
		DDMForm ddmForm, DDMFormField ddmFormField) {

		LocalizedValue localizedValue = ddmFormField.getPredefinedValue();

		if ((localizedValue == null) ||
			MapUtil.isEmpty(localizedValue.getValues())) {

			localizedValue = _getLocalizedValue(
				ddmForm.getDefaultLocale(), StringPool.BLANK);
		}

		if (ddmFormField.isLocalizable()) {
			return localizedValue;
		}

		return new UnlocalizedValue(
			GetterUtil.getString(
				localizedValue.getString(ddmForm.getDefaultLocale())));
	}

	private void _upgradeDDMFormInstance() throws Exception {
		alterTableAddColumn("DDMFormInstance", "expirationDate", "DATE null");
	}

	private void _upgradePollsQuestion(
			long questionId, long groupId, long companyId, long userId,
			String userName, Timestamp createDate, Timestamp modifiedDate,
			String name, String description, Timestamp expirationDate,
			Timestamp lastPublishDate, Timestamp lastVoteDate)
		throws Exception {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions(
			_defaultLocale);

		Map<Long, String> ddmFormFieldOptionsValues =
			_getDDMFormFieldOptionsValues(ddmFormFieldOptions, questionId);

		DDMFormField ddmFormField = getDDMFormField(
			ddmFormFieldOptions, description);

		String definition = DDMFormSerializeUtil.serialize(
			getDDMForm(ddmFormField), _ddmFormSerializer);

		long structureId = _addDDMStructure(
			groupId, companyId, userId, userName, createDate, modifiedDate,
			name, definition, lastPublishDate);

		long structureVersionId = _addDDMStructureVersion(
			groupId, companyId, userId, userName, createDate, structureId, name,
			definition, lastPublishDate);

		_addDDMStructureLayout(
			groupId, companyId, userId, userName, createDate, modifiedDate,
			structureVersionId, name, getDDMFormLayoutDefinition(ddmFormField));

		String settings = getSerializedSettingsDDMFormValues();

		_addDDMFormInstance(
			questionId, groupId, companyId, userId, userName, createDate,
			modifiedDate, structureId, name, settings, expirationDate,
			lastPublishDate);

		_upgradeResourcePermission(questionId);

		_addDDMFormInstanceVersion(
			groupId, companyId, userId, userName, createDate, questionId,
			structureVersionId, name, settings, lastPublishDate);

		JSONObject dataJSONObject = getDataJSONObject(ddmFormField.getName());

		_upgradePollsVotes(
			dataJSONObject, ddmFormFieldOptionsValues, ddmFormField.getName(),
			String.valueOf(ddmFormField.getProperty("instanceId")), questionId,
			structureId, structureVersionId);

		_addDDMFormInstanceReport(
			groupId, companyId, createDate, lastVoteDate, questionId,
			dataJSONObject);
	}

	private void _upgradePollsQuestions() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from PollsQuestion");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long questionId = resultSet.getLong("questionId");

				String title = resultSet.getString("title");

				_availableLocales = SetUtil.fromArray(
					LocaleUtil.fromLanguageIds(
						LocalizationUtil.getAvailableLanguageIds(title)));
				_defaultLocale = LocaleUtil.fromLanguageId(
					LocalizationUtil.getDefaultLanguageId(title));

				_upgradePollsQuestion(
					questionId, resultSet.getLong("groupId"),
					resultSet.getLong("companyId"), resultSet.getLong("userId"),
					resultSet.getString("userName"),
					resultSet.getTimestamp("createDate"),
					resultSet.getTimestamp("modifiedDate"),
					_getName(resultSet.getString("title")),
					resultSet.getString("description"),
					resultSet.getTimestamp("expirationDate"),
					resultSet.getTimestamp("lastPublishDate"),
					resultSet.getTimestamp("lastVoteDate"));
			}
		}
	}

	private void _upgradePollsVote(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, Timestamp modifiedDate, long formInstanceId,
			long structureId, long structureVersionId,
			Timestamp lastPublishDate, String fieldName, String instanceId,
			String optionValue)
		throws Exception {

		long storageId = _addDDMStorageLink(
			companyId, structureId, structureVersionId);

		_addLanguageDDMField(companyId, storageId, structureVersionId);

		_addRadioDDMField(
			companyId, storageId, structureVersionId, fieldName, instanceId,
			optionValue);

		long formInstanceRecordId = _addDDMFormInstanceRecord(
			groupId, companyId, userId, userName, createDate, modifiedDate,
			formInstanceId, storageId, lastPublishDate);

		_addDDMFormInstanceRecordVersion(
			groupId, companyId, userId, userName, createDate, formInstanceId,
			formInstanceRecordId, storageId, lastPublishDate);
	}

	private void _upgradePollsVotes(
			JSONObject dataJSONObject,
			Map<Long, String> ddmFormFieldOptionsValues, String fieldName,
			String instanceId, long questionId, long structureId,
			long structureVersionId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from PollsVote where questionId = ?")) {

			preparedStatement.setLong(1, questionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String optionValue = ddmFormFieldOptionsValues.get(
						resultSet.getLong("choiceId"));

					_upgradePollsVote(
						resultSet.getLong("groupId"),
						resultSet.getLong("companyId"),
						resultSet.getLong("userId"),
						resultSet.getString("userName"),
						resultSet.getTimestamp("createDate"),
						resultSet.getTimestamp("modifiedDate"), questionId,
						structureId, structureVersionId,
						resultSet.getTimestamp("lastPublishDate"), fieldName,
						instanceId, optionValue);

					JSONObject fieldJSONObject = dataJSONObject.getJSONObject(
						fieldName);

					JSONObject valuesJSONObject = fieldJSONObject.getJSONObject(
						"values");

					valuesJSONObject.put(
						optionValue, valuesJSONObject.getInt(optionValue) + 1);

					dataJSONObject.put(
						"totalItems", dataJSONObject.getInt("totalItems") + 1);
				}
			}
		}
	}

	private void _upgradeResourcePermission(long primKeyId) throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_resourcePermissionLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property nameProperty = PropertyFactoryUtil.forName("name");

				dynamicQuery.add(nameProperty.eq(_CLASS_NAME_POLLS));

				Property primKeyProperty = PropertyFactoryUtil.forName(
					"primKey");

				dynamicQuery.add(primKeyProperty.eq(String.valueOf(primKeyId)));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<ResourcePermission>)
				resourcePermission -> {
					resourcePermission.setName(_CLASS_NAME_DDM_FORM_INSTANCE);
					resourcePermission.setActionIds(
						_getActionIds(resourcePermission.getActionIds()));

					_resourcePermissionLocalService.updateResourcePermission(
						resourcePermission);
				});

		actionableDynamicQuery.performActions();
	}

	private static final String _CLASS_NAME_DDM_FORM_INSTANCE =
		"com.liferay.dynamic.data.mapping.model.DDMFormInstance";

	private static final String _CLASS_NAME_POLLS =
		"com.liferay.polls.model.PollsQuestion";

	private static final Map<String, String> _resourceActionIds =
		ConcurrentHashMapBuilder.put(
			"ADD_VOTE", "ADD_FORM_INSTANCE_RECORD"
		).put(
			"DELETE", "DELETE"
		).put(
			"PERMISSIONS", "PERMISSIONS"
		).put(
			"UPDATE", "UPDATE"
		).put(
			"VIEW", "VIEW"
		).build();

	private Set<Locale> _availableLocales;
	private final DDMFormLayoutSerializer _ddmFormLayoutSerializer;
	private final DDMFormSerializer _ddmFormSerializer;
	private final DDMFormValuesSerializer _ddmFormValuesSerializer;
	private Locale _defaultLocale;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}