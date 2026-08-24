/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.diff.DiffHtml;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;

import java.util.HashMap;
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

			_objectEntryService.getObjectEntry(objectEntryId);

			String languageId = jsonObject.getString("languageId");

			Map<String, String> sourceFieldValues = _getFieldValues(
				languageId, objectEntryId, jsonObject.getInt("sourceVersion"));
			Map<String, String> targetFieldValues = _getFieldValues(
				languageId, objectEntryId, jsonObject.getInt("targetVersion"));

			JSONObject sourceDiffsJSONObject = _jsonFactory.createJSONObject();
			JSONObject targetDiffsJSONObject = _jsonFactory.createJSONObject();

			Set<String> fieldNames = new TreeSet<>(sourceFieldValues.keySet());

			fieldNames.addAll(targetFieldValues.keySet());

			for (String fieldName : fieldNames) {
				String source = sourceFieldValues.getOrDefault(
					fieldName, StringPool.BLANK);
				String target = targetFieldValues.getOrDefault(
					fieldName, StringPool.BLANK);

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

	private Map<String, String> _getFieldValues(
			String languageId, long objectEntryId, int version)
		throws Exception {

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

		Map<String, String> fieldValues = new HashMap<>();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String name = entry.getKey();

			if (name.endsWith("_i18n") || name.endsWith("RawText")) {
				continue;
			}

			Object value = entry.getValue();

			Object localizedValues = properties.get(name + "_i18n");

			if (localizedValues instanceof Map) {
				value = ((Map<String, Object>)localizedValues).get(languageId);
			}

			if (value == null) {
				value = StringPool.BLANK;
			}

			fieldValues.put(name, String.valueOf(value));
		}

		return fieldValues;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompareObjectEntryVersionsCMSServlet.class);

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

}
