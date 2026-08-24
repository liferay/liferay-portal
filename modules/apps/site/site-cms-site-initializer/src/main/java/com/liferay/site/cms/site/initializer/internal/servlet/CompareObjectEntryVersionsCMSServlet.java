/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.diff.DiffHtml;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Verónica González
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.CompareObjectEntryVersionsCMSServlet",
		"osgi.http.whiteboard.servlet.pattern=/cms/compare-versions",
		"servlet.init.httpMethods=POST"
	},
	service = Servlet.class
)
public class CompareObjectEntryVersionsCMSServlet extends BaseCMSServlet {

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		JSONObject jsonObject = null;

		try {
			jsonObject = _jsonFactory.createJSONObject(
				StreamUtil.toString(
					httpServletRequest.getInputStream(), StringPool.UTF8));
		}
		catch (JSONException jsonException) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn(jsonException);
			}

			return;
		}

		try {
			long objectEntryId = jsonObject.getLong("objectEntryId");

			com.liferay.object.model.ObjectEntry objectEntry =
				_objectEntryService.getObjectEntry(objectEntryId);

			Map<String, ObjectField> objectFields = new HashMap<>();

			for (ObjectField objectField :
					_objectFieldLocalService.getObjectFields(
						objectEntry.getObjectDefinitionId())) {

				objectFields.put(objectField.getName(), objectField);
			}

			String languageId = jsonObject.getString("languageId");

			Map<String, Object> sourceFieldValues = _getFieldValues(
				languageId, objectEntryId, jsonObject.getInt("sourceVersion"));
			Map<String, Object> targetFieldValues = _getFieldValues(
				languageId, objectEntryId, jsonObject.getInt("targetVersion"));

			JSONObject sourceDiffsJSONObject = _jsonFactory.createJSONObject();
			JSONObject targetDiffsJSONObject = _jsonFactory.createJSONObject();

			Set<String> fieldNames = new TreeSet<>(sourceFieldValues.keySet());

			fieldNames.addAll(targetFieldValues.keySet());

			for (String fieldName : fieldNames) {
				ObjectField objectField = objectFields.get(fieldName);

				String source = _toDisplayValue(
					sourceFieldValues.get(fieldName), objectField, languageId);
				String target = _toDisplayValue(
					targetFieldValues.get(fieldName), objectField, languageId);

				if (source.equals(target)) {
					continue;
				}

				sourceDiffsJSONObject.put(
					fieldName,
					_diffHtml.diff(
						new StringReader(target), new StringReader(source)));
				targetDiffsJSONObject.put(
					fieldName,
					_diffHtml.diff(
						new StringReader(source), new StringReader(target)));
			}

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse,
				JSONUtil.put(
					"diffs",
					JSONUtil.put(
						"source", sourceDiffsJSONObject
					).put(
						"target", targetDiffsJSONObject
					)
				).toString());
		}
		catch (Exception exception) {
			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private Map<String, Object> _getFieldValues(
			String languageId, long objectEntryId, int version)
		throws Exception {

		Map<String, Object> fieldValues = new HashMap<>();

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.getObjectEntryVersion(
				objectEntryId, version);

		ObjectEntry objectEntry = ObjectEntry.unsafeToDTO(
			objectEntryVersion.getContent());

		Map<String, Object> properties = objectEntry.getProperties();

		Object nestedProperties = properties.get("properties");

		if (nestedProperties instanceof Map) {
			properties = (Map<String, Object>)nestedProperties;
		}

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String name = entry.getKey();

			if (name.endsWith("_i18n") || name.endsWith("RawText")) {
				continue;
			}

			Object value = entry.getValue();

			Object localizedValues = properties.get(name + "_i18n");

			if (localizedValues instanceof Map) {
				Map<String, Object> localizedValuesMap =
					(Map<String, Object>)localizedValues;

				value = localizedValuesMap.get(languageId);
			}

			fieldValues.put(name, value);
		}

		Map<String, String> friendlyUrlPathI18n =
			objectEntry.getFriendlyUrlPath_i18n();

		if ((friendlyUrlPathI18n != null) &&
			friendlyUrlPathI18n.containsKey(languageId)) {

			fieldValues.put(
				"objectEntryFriendlyURL", friendlyUrlPathI18n.get(languageId));
		}
		else {
			fieldValues.put(
				"objectEntryFriendlyURL", objectEntry.getFriendlyUrlPath());
		}

		return fieldValues;
	}

	private String _toAttachmentFileName(Object value) {
		Object idObject = value;

		if (value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>)value;

			idObject = valueMap.get("id");
		}

		long fileEntryId = GetterUtil.getLong(idObject);

		if (fileEntryId == 0) {
			return String.valueOf(value);
		}

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry == null) {
			return String.valueOf(value);
		}

		return dlFileEntry.getFileName();
	}

	private String _toDisplayValue(
		Object value, ObjectField objectField, String languageId) {

		String businessType =
			(objectField == null) ? null : objectField.getBusinessType();

		if (ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN.equals(businessType)) {
			Locale locale = LocaleUtil.fromLanguageId(languageId);

			if (GetterUtil.getBoolean(value)) {
				return _language.get(locale, "yes");
			}

			return _language.get(locale, "no");
		}

		if (value == null) {
			return StringPool.BLANK;
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST.equals(
				businessType)) {

			return _toMultiselectPicklistLabels(objectField, languageId, value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_PICKLIST.equals(businessType)) {
			return _toPicklistLabel(objectField, languageId, value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT.equals(
				businessType)) {

			return _toAttachmentFileName(value);
		}

		return String.valueOf(value);
	}

	private String _toMultiselectPicklistLabels(
		ObjectField objectField, String languageId, Object value) {

		Object[] values = null;

		if (value instanceof Object[]) {
			values = (Object[])value;
		}
		else if (value instanceof List) {
			List<?> list = (List<?>)value;

			values = list.toArray();
		}
		else {
			return _toPicklistLabel(objectField, languageId, value);
		}

		StringBundler sb = new StringBundler((values.length * 2) - 1);

		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(_toPicklistLabel(objectField, languageId, values[i]));
		}

		return sb.toString();
	}

	private String _toPicklistLabel(
		ObjectField objectField, String languageId, Object value) {

		Object keyObject = value;

		if (value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>)value;

			keyObject = valueMap.get("key");
		}

		if (keyObject == null) {
			return StringPool.BLANK;
		}

		String key = String.valueOf(keyObject);

		if (key.isEmpty()) {
			return StringPool.BLANK;
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), key);

		if (listTypeEntry == null) {
			return key;
		}

		return listTypeEntry.getName(languageId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompareObjectEntryVersionsCMSServlet.class);

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}