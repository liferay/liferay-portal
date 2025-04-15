/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.upgrade.v1_0_0;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;

import jakarta.portlet.PortletPreferences;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sam Ziemer
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	public UpgradePortletPreferences(
		DDMStructureLocalService ddmStructureLocalService,
		DDMStructureLinkLocalService ddmStructureLinkLocalService,
		SAXReader saxReader) {

		_ddmStructureLocalService = ddmStructureLocalService;
		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
		_saxReader = saxReader;

		_newDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");
		_oldDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			AssetPublisherPortletKeys.ASSET_PUBLISHER + "_INSTANCE_%"
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		portletPreferences = upgradePreferences(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected PortletPreferences upgradePreferences(
			PortletPreferences portletPreferences)
		throws Exception {

		String[] assetEntryXmls = portletPreferences.getValues(
			"asset-entry-xml", new String[0]);

		if (ArrayUtil.isNotEmpty(assetEntryXmls)) {
			_upgradeUuids(assetEntryXmls);

			portletPreferences.setValues("assetEntryXml", assetEntryXmls);
		}

		boolean subtypeFieldsFilterEnabled = GetterUtil.getBoolean(
			portletPreferences.getValue(
				"subtypeFieldsFilterEnabled", Boolean.FALSE.toString()));

		if (subtypeFieldsFilterEnabled) {
			boolean dlFilterByFieldEnable = _isFilterByFieldEnable(
				portletPreferences, _DL_FILTER_BY_FIELD_ENABLED_KEY);
			boolean journalFilterByFieldEnable = _isFilterByFieldEnable(
				portletPreferences, _JOURNAL_FILTER_BY_FIELD_ENABLED_KEY);

			if (dlFilterByFieldEnable) {
				_upgradeDLDateFieldsValues(portletPreferences);
			}
			else if (journalFilterByFieldEnable) {
				_upgradeJournalDateFieldValue(portletPreferences);
			}
		}

		_upgradeOrderByColumns(portletPreferences);

		return portletPreferences;
	}

	private DDMForm _getDDMForm(long structureId) throws Exception {
		DDMForm ddmForm = _ddmSructureDDMForms.get(structureId);

		if (ddmForm != null) {
			return ddmForm;
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			structureId);

		ddmForm = ddmStructure.getDDMForm();

		_ddmSructureDDMForms.put(structureId, ddmForm);

		return ddmForm;
	}

	private DDMFormField _getDDMFormField(DDMForm ddmForm, String fieldName) {
		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(false);

		return ddmFormFieldsMap.get(fieldName);
	}

	private String _getJournalArticleResourceUuid(String journalArticleUuid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select JournalArticleResource.uuid_ from ",
					"JournalArticleResource inner join JournalArticle on ",
					"JournalArticle.resourcePrimKey = ",
					"JournalArticleResource.resourcePrimKey where ",
					"JournalArticle.uuid_ = ?"))) {

			preparedStatement.setString(1, journalArticleUuid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("uuid_");
				}

				return null;
			}
		}
	}

	private boolean _isDateField(DDMForm ddmForm, String fieldName) {
		DDMFormField ddmFormField = _getDDMFormField(ddmForm, fieldName);

		if (ddmFormField == null) {
			return false;
		}

		if (Objects.equals(ddmFormField.getType(), "date") ||
			Objects.equals(ddmFormField.getType(), "ddm-date")) {

			return true;
		}

		return false;
	}

	private boolean _isFilterByFieldEnable(
		PortletPreferences portletPreferences, String key) {

		return GetterUtil.getBoolean(
			portletPreferences.getValue(key, Boolean.FALSE.toString()));
	}

	private boolean _isOldDDMPreferenceValueFormat(String value) {
		return value.startsWith(_DDM_FIELD_OLD_PREFIX);
	}

	private void _transformDateFieldValue(PortletPreferences portletPreferences)
		throws Exception {

		String value = GetterUtil.getString(
			portletPreferences.getValue(_DDM_STRUCTURE_FIELD_VALUE, null));

		if (Validator.isNotNull(value)) {
			Date date = _oldDateFormat.parse(value);

			portletPreferences.setValue(
				_DDM_STRUCTURE_FIELD_VALUE, _newDateFormat.format(date));
		}
	}

	private void _upgradeDLDateFieldsValues(
			PortletPreferences portletPreferences)
		throws Exception {

		long fileEntryTypeId = GetterUtil.getLong(
			portletPreferences.getValue(_DL_CLASS_TYPE, "0"));

		if (fileEntryTypeId > 0) {
			long fileEntryTypeClassNameId = PortalUtil.getClassNameId(
				DLFileEntryType.class);

			List<DDMStructureLink> ddmStructureLinks =
				_ddmStructureLinkLocalService.getStructureLinks(
					fileEntryTypeClassNameId, fileEntryTypeId);

			String selectedFieldName = GetterUtil.getString(
				portletPreferences.getValue(_DDM_STRUCTURE_FIELD_NAME, null));

			for (DDMStructureLink ddmStructureLink : ddmStructureLinks) {
				if (_isDateField(
						_getDDMForm(ddmStructureLink.getStructureId()),
						selectedFieldName)) {

					_transformDateFieldValue(portletPreferences);

					break;
				}
			}
		}
	}

	private void _upgradeJournalDateFieldValue(
			PortletPreferences portletPreferences)
		throws Exception {

		long structureId = GetterUtil.getLong(
			portletPreferences.getValue(_JOURNAL_CLASS_TYPE, "0"));

		if (structureId > 0) {
			String selectedFieldName = GetterUtil.getString(
				portletPreferences.getValue(_DDM_STRUCTURE_FIELD_NAME, null));

			if (_isDateField(_getDDMForm(structureId), selectedFieldName)) {
				_transformDateFieldValue(portletPreferences);
			}
		}
	}

	private void _upgradeOrderByColumn(
			PortletPreferences portletPreferences, String column)
		throws Exception {

		String value = GetterUtil.getString(
			portletPreferences.getValue(column, null));

		if (Validator.isNull(value)) {
			return;
		}

		if (value.startsWith(_DDM_FIELD_OLD_PREFIX) ||
			value.startsWith(_DDM_FIELD_PREFIX)) {

			String[] values = new String[0];

			boolean oldDDMPreferenceValueFormat =
				_isOldDDMPreferenceValueFormat(value);

			if (oldDDMPreferenceValueFormat) {
				values = StringUtil.split(value, _DDM_FIELD_OLD_SEPARATOR);
			}
			else {
				values = StringUtil.split(value, _DDM_FIELD_SEPARATOR);
			}

			if (values.length == 3) {
				long structureId = GetterUtil.getLong(values[1]);

				String fieldName = values[2];

				Matcher matcher = _invalidFieldNameCharsPattern.matcher(
					fieldName);

				if (matcher.find()) {
					fieldName = fieldName.replaceAll(
						_INVALID_FIELD_NAME_CHARS_REGEX, StringPool.BLANK);
				}

				DDMFormField ddmFormField = _getDDMFormField(
					_getDDMForm(structureId), fieldName);

				if ((ddmFormField != null) &&
					Validator.isNotNull(ddmFormField.getIndexType())) {

					value = StringBundler.concat(
						values[0], _DDM_FIELD_SEPARATOR,
						ddmFormField.getIndexType(), _DDM_FIELD_SEPARATOR,
						values[1], _DDM_FIELD_SEPARATOR, fieldName);
				}
			}
			else if ((values.length == 4) && oldDDMPreferenceValueFormat) {
				value = StringUtil.replace(
					value, _DDM_FIELD_OLD_SEPARATOR, _DDM_FIELD_SEPARATOR);
			}

			portletPreferences.setValue(column, value);
		}
	}

	private void _upgradeOrderByColumns(PortletPreferences portletPreferences)
		throws Exception {

		_upgradeOrderByColumn(portletPreferences, _ORDER_BY_COLUMN_1);
		_upgradeOrderByColumn(portletPreferences, _ORDER_BY_COLUMN_2);
	}

	private void _upgradeUuids(String[] assetEntryXmls) throws Exception {
		for (int i = 0; i < assetEntryXmls.length; i++) {
			String assetEntry = assetEntryXmls[i];

			Document document = _saxReader.read(assetEntry);

			Element rootElement = document.getRootElement();

			Element assetTypeElementUuid = rootElement.element(
				"asset-entry-uuid");

			if (assetTypeElementUuid == null) {
				continue;
			}

			String journalArticleResourceUuid = _getJournalArticleResourceUuid(
				assetTypeElementUuid.getStringValue());

			if (journalArticleResourceUuid == null) {
				continue;
			}

			rootElement.remove(assetTypeElementUuid);

			assetTypeElementUuid.setText(journalArticleResourceUuid);

			rootElement.add(assetTypeElementUuid);

			document.setRootElement(rootElement);

			assetEntryXmls[i] = document.formattedString(StringPool.BLANK);
		}
	}

	private static final String _DDM_FIELD_NAMESPACE = "ddm";

	private static final String _DDM_FIELD_OLD_PREFIX =
		_DDM_FIELD_NAMESPACE + StringPool.FORWARD_SLASH;

	private static final String _DDM_FIELD_OLD_SEPARATOR =
		StringPool.FORWARD_SLASH;

	private static final String _DDM_FIELD_PREFIX =
		_DDM_FIELD_NAMESPACE + StringPool.DOUBLE_UNDERLINE;

	private static final String _DDM_FIELD_SEPARATOR =
		StringPool.DOUBLE_UNDERLINE;

	private static final String _DDM_STRUCTURE_FIELD_NAME =
		"ddmStructureFieldName";

	private static final String _DDM_STRUCTURE_FIELD_VALUE =
		"ddmStructureFieldValue";

	private static final String _DL_CLASS_TYPE =
		"anyClassTypeDLFileEntryAssetRendererFactory";

	private static final String _DL_FILTER_BY_FIELD_ENABLED_KEY =
		"subtypeFieldsFilterEnabledDLFileEntryAssetRendererFactory";

	private static final String _INVALID_FIELD_NAME_CHARS_REGEX =
		"([\\p{Punct}&&[^_]]|\\p{Space})+";

	private static final String _JOURNAL_CLASS_TYPE =
		"anyClassTypeJournalArticleAssetRendererFactory";

	private static final String _JOURNAL_FILTER_BY_FIELD_ENABLED_KEY =
		"subtypeFieldsFilterEnabledJournalArticleAssetRendererFactory";

	private static final String _ORDER_BY_COLUMN_1 = "orderByColumn1";

	private static final String _ORDER_BY_COLUMN_2 = "orderByColumn2";

	private static final Map<Long, DDMForm> _ddmSructureDDMForms =
		new HashMap<>();
	private static final Pattern _invalidFieldNameCharsPattern =
		Pattern.compile(_INVALID_FIELD_NAME_CHARS_REGEX);

	private final DDMStructureLinkLocalService _ddmStructureLinkLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final DateFormat _newDateFormat;
	private final DateFormat _oldDateFormat;
	private final SAXReader _saxReader;

}