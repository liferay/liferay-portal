/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorConfigurationUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMURLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 */
public class EditPIMConnectorDisplayContext {

	public EditPIMConnectorDisplayContext(
		HttpServletRequest httpServletRequest,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		List<PIMConnector> pimConnectors) {

		_httpServletRequest = httpServletRequest;
		_listTypeEntryLocalService = listTypeEntryLocalService;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_pimConnectors = pimConnectors;

		_objectEntryId = ParamUtil.getLong(httpServletRequest, "objectEntryId");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_objectEntryId);

		return HashMapBuilder.<String, Object>put(
			"apiURL", _getAPIURL()
		).put(
			"backURL",
			PIMURLUtil.getBackURL(
				_themeDisplay.getURLCurrent(), _httpServletRequest)
		).put(
			"configuration",
			_getPIMConnectorConfigurationValuesJSONObject(objectEntry)
		).put(
			"objectEntryId", _objectEntryId
		).put(
			"pimConnector", _getPIMConnectorJSONObject(objectEntry)
		).put(
			"pimConnectors",
			JSONUtil.toJSONArray(
				_pimConnectors,
				pimConnector -> {
					String name = GetterUtil.getString(
						pimConnector.getName(_themeDisplay.getLocale()),
						pimConnector.getKey());

					return JSONUtil.put(
						"configuration",
						_getPIMConnectorConfigurationJSONObject(
							name, pimConnector)
					).put(
						"key", pimConnector.getKey()
					).put(
						"name", name
					);
				})
		).put(
			"title", _getTitle(objectEntry)
		).build();
	}

	private String _getAPIURL() {
		if (_objectDefinition == null) {
			return StringPool.BLANK;
		}

		return "/o" + _objectDefinition.getRESTContextPath();
	}

	private JSONArray _getOptionsJSONArray(ObjectField objectField)
		throws Exception {

		if (!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) &&
			!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return null;
		}

		return JSONUtil.toJSONArray(
			_listTypeEntryLocalService.getListTypeEntries(
				objectField.getListTypeDefinitionId()),
			listTypeEntry -> JSONUtil.put(
				"label", listTypeEntry.getName(_themeDisplay.getLocale())
			).put(
				"value", listTypeEntry.getKey()
			));
	}

	private JSONObject _getPIMConnectorConfigurationJSONObject(
			String name, PIMConnector pimConnector)
		throws Exception {

		ObjectDefinition objectDefinition =
			PIMConnectorConfigurationUtil.
				fetchPIMConnectorConfigurationObjectDefinition(
					_themeDisplay.getCompanyId(), pimConnector);

		if (objectDefinition == null) {
			return null;
		}

		return JSONUtil.put(
			"apiURL", "/o" + objectDefinition.getRESTContextPath()
		).put(
			"fields",
			JSONUtil.toJSONArray(
				ListUtil.filter(
					_objectFieldLocalService.getObjectFields(
						objectDefinition.getObjectDefinitionId()),
					objectField -> !objectField.isMetadata()),
				objectField -> JSONUtil.put(
					"businessType", objectField.getBusinessType()
				).put(
					"label", objectField.getLabel(_themeDisplay.getLocale())
				).put(
					"name", objectField.getName()
				).put(
					"options", _getOptionsJSONArray(objectField)
				).put(
					"required", objectField.isRequired()
				))
		).put(
			"title",
			LanguageUtil.format(_httpServletRequest, "x-configuration", name)
		);
	}

	private Object _getPIMConnectorConfigurationValue(
		ObjectField objectField, Object value) {

		if (value == null) {
			return null;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED)) {

			if (Validator.isNull(String.valueOf(value))) {
				return StringPool.BLANK;
			}

			return Portal.TEMP_OBFUSCATION_VALUE;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");

			return simpleDateFormat.format((Date)value);
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm");

			return simpleDateFormat.format((Date)value);
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			return JSONUtil.putAll(
				(Object[])StringUtil.split(
					String.valueOf(value), StringPool.COMMA_AND_SPACE));
		}

		if (value instanceof Boolean || value instanceof Number) {
			return value;
		}

		return String.valueOf(value);
	}

	private JSONObject _getPIMConnectorConfigurationValuesJSONObject(
		ObjectEntry objectEntry) {

		if (objectEntry == null) {
			return null;
		}

		String key = MapUtil.getString(objectEntry.getValues(), "key");
		PIMConnector pimConnector = null;

		for (PIMConnector curPIMConnector : _pimConnectors) {
			if (Objects.equals(curPIMConnector.getKey(), key)) {
				pimConnector = curPIMConnector;

				break;
			}
		}

		ObjectEntry pimConnectorConfigurationObjectEntry =
			PIMConnectorConfigurationUtil.
				fetchPIMConnectorConfigurationObjectEntry(
					pimConnector, objectEntry);

		if (pimConnectorConfigurationObjectEntry == null) {
			return null;
		}

		JSONObject jsonObject = JSONUtil.put(
			"externalReferenceCode",
			pimConnectorConfigurationObjectEntry.getExternalReferenceCode());

		JSONObject valuesJSONObject = JSONFactoryUtil.createJSONObject();

		List<ObjectField> objectFields = ListUtil.filter(
			_objectFieldLocalService.getObjectFields(
				pimConnectorConfigurationObjectEntry.getObjectDefinitionId()),
			objectField -> !objectField.isMetadata());
		Map<String, Serializable> values =
			pimConnectorConfigurationObjectEntry.getValues();

		for (ObjectField objectField : objectFields) {
			valuesJSONObject.put(
				objectField.getName(),
				_getPIMConnectorConfigurationValue(
					objectField, values.get(objectField.getName())));
		}

		return jsonObject.put("values", valuesJSONObject);
	}

	private JSONObject _getPIMConnectorJSONObject(ObjectEntry objectEntry) {
		if (objectEntry == null) {
			return null;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		return JSONUtil.put(
			"active", MapUtil.getBoolean(values, "active")
		).put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).put(
			"key", MapUtil.getString(values, "key")
		).put(
			"name", MapUtil.getString(values, "name")
		);
	}

	private String _getTitle(ObjectEntry objectEntry) {
		if (objectEntry == null) {
			return LanguageUtil.get(_httpServletRequest, "new-connector");
		}

		String name = MapUtil.getString(objectEntry.getValues(), "name");

		if (Validator.isNotNull(name)) {
			return LanguageUtil.format(
				_httpServletRequest, "edit-x", name, false);
		}

		return LanguageUtil.get(_httpServletRequest, "edit-connector");
	}

	private final HttpServletRequest _httpServletRequest;
	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ObjectDefinition _objectDefinition;
	private final long _objectEntryId;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final List<PIMConnector> _pimConnectors;
	private final ThemeDisplay _themeDisplay;

}