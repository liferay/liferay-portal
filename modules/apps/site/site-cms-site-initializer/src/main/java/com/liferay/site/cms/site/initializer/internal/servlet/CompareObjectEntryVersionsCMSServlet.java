/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.diff.DiffHtml;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectEntryVersionService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.site.cms.site.initializer.internal.comparison.ObjectEntryVersionFieldValueResolver;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Activate;
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

	@Activate
	protected void activate() {
		_objectEntryVersionFieldValueResolver =
			new ObjectEntryVersionFieldValueResolver(
				_dlAppLocalService, _dlFileEntryLocalService, _dlURLHelper,
				_language, _listTypeEntryLocalService,
				_objectEntryVersionService);
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (!FeatureFlagManagerUtil.isEnabled(
				portal.getCompanyId(httpServletRequest), "LPD-56634")) {

			throw new UnsupportedOperationException();
		}

		JSONObject requestJSONObject = null;

		try {
			requestJSONObject = _jsonFactory.createJSONObject(
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
			long objectEntryId = requestJSONObject.getLong("objectEntryId");

			ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
				objectEntryId);

			Map<String, ObjectField> objectFields = new HashMap<>();

			for (ObjectField objectField :
					_objectFieldLocalService.getObjectFields(
						objectEntry.getObjectDefinitionId())) {

				objectFields.put(objectField.getName(), objectField);
			}

			String languageId = requestJSONObject.getString("languageId");

			User user = portal.getUser(httpServletRequest);

			Map<String, Object> sourceFieldValues =
				_objectEntryVersionFieldValueResolver.getFieldValues(
					languageId, objectEntryId,
					requestJSONObject.getInt("sourceVersion"));
			Map<String, Object> targetFieldValues =
				_objectEntryVersionFieldValueResolver.getFieldValues(
					languageId, objectEntryId,
					requestJSONObject.getInt("targetVersion"));

			JSONObject sourceDiffsJSONObject = _jsonFactory.createJSONObject();
			JSONObject targetDiffsJSONObject = _jsonFactory.createJSONObject();

			Set<String> fieldNames = new TreeSet<>(sourceFieldValues.keySet());

			fieldNames.addAll(targetFieldValues.keySet());

			for (String fieldName : fieldNames) {
				ObjectField objectField = objectFields.get(fieldName);

				String sourceDisplayValue =
					_objectEntryVersionFieldValueResolver.toDisplayValue(
						languageId, objectField, user,
						sourceFieldValues.get(fieldName));
				String targetDisplayValue =
					_objectEntryVersionFieldValueResolver.toDisplayValue(
						languageId, objectField, user,
						targetFieldValues.get(fieldName));

				if (sourceDisplayValue.equals(targetDisplayValue)) {
					continue;
				}

				if (_objectEntryVersionFieldValueResolver.isDateBusinessType(
						objectField)) {

					sourceDiffsJSONObject.put(
						fieldName,
						_toDateDiffHtml(
							sourceDisplayValue, targetDisplayValue));
					targetDiffsJSONObject.put(
						fieldName,
						_toDateDiffHtml(
							targetDisplayValue, sourceDisplayValue));

					continue;
				}

				sourceDiffsJSONObject.put(
					fieldName,
					_diffHtml.diff(
						new StringReader(targetDisplayValue),
						new StringReader(sourceDisplayValue)));
				targetDiffsJSONObject.put(
					fieldName,
					_diffHtml.diff(
						new StringReader(sourceDisplayValue),
						new StringReader(targetDisplayValue)));
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

	private String _toDateDiffHtml(String added, String removed) {
		StringBundler sb = new StringBundler(6);

		if (!removed.isEmpty()) {
			sb.append("<span class=\"diff-html-removed\">");
			sb.append(HtmlUtil.escape(removed));
			sb.append("</span>");
		}

		if (!added.isEmpty()) {
			sb.append("<span class=\"diff-html-added\">");
			sb.append(HtmlUtil.escape(added));
			sb.append("</span>");
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompareObjectEntryVersionsCMSServlet.class);

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	private ObjectEntryVersionFieldValueResolver
		_objectEntryVersionFieldValueResolver;

	@Reference
	private ObjectEntryVersionService _objectEntryVersionService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}