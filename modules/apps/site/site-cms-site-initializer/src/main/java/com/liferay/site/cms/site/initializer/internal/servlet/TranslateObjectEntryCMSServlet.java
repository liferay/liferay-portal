/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.io.AutoDeleteFileInputStream;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.translation.manager.TranslationManager;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.TranslateObjectEntryCMSServlet",
		"osgi.http.whiteboard.servlet.pattern=/cms/translations/*",
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class TranslateObjectEntryCMSServlet extends BaseCMSServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_exportObjectEntryTranslation(
				httpServletRequest, httpServletResponse);
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_exportObjectEntryTranslation(
				httpServletRequest, httpServletResponse);
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	private void _exportObjectEntryTranslation(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

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

		if (!StringUtil.equalsIgnoreCase(
				_EXPORT_TRANSLATION_BULK_ACTION,
				jsonObject.getString("type"))) {

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Type is not \"" + _EXPORT_TRANSLATION_BULK_ACTION + "\"");
			}

			return;
		}

		String sourceLanguageId = jsonObject.getString("sourceLanguageId");
		String[] targetLanguageIds = JSONUtil.toStringArray(
			jsonObject.getJSONArray("targetLanguageIds"));
		String xliffMimeType = jsonObject.getString("xliffMimeType");

		Map<String, List<Long>> objectEntryIds = new HashMap<>();

		JSONArray jsonArray = null;

		JSONObject selectionScopeJSONObject = jsonObject.getJSONObject(
			"selectionScope");

		if ((selectionScopeJSONObject != null) &&
			selectionScopeJSONObject.getBoolean("selectAll")) {

			jsonArray = _getSelectAllJSONArray(httpServletRequest);
		}
		else {
			jsonArray = jsonObject.getJSONArray("bulkActionItems");
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Set<String> allowedClassNames = _getAllowedClassNames(
			themeDisplay.getCompanyId());

		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObject = jsonArray.getJSONObject(i);

			String className = jsonObject.getString("className");

			if (!allowedClassNames.contains(className) ||
				StringUtil.equalsIgnoreCase(
					className, ObjectEntryFolder.class.getName())) {

				continue;
			}

			Long classPK = jsonObject.getLong("classPK");

			List<Long> classPKs = objectEntryIds.computeIfAbsent(
				className, key -> new LinkedList<>());

			classPKs.add(classPK);
		}

		if (objectEntryIds.isEmpty()) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn("No allowed object definitions selected");
			}

			return;
		}

		File file = null;
		Locale locale = portal.getLocale(httpServletRequest);
		String zipFileName = StringPool.BLANK;
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		for (Map.Entry<String, List<Long>> entry : objectEntryIds.entrySet()) {
			file = _translationManager.getXLIFFZipFile(
				entry.getKey(), ArrayUtil.toLongArray(entry.getValue()),
				xliffMimeType, locale, sourceLanguageId, targetLanguageIds);

			zipFileName = file.getName();

			if (objectEntryIds.size() <= 1) {
				continue;
			}

			zipWriter.addEntry(
				file.getName(), new AutoDeleteFileInputStream(file));
		}

		if (objectEntryIds.size() > 1) {
			file = zipWriter.getFile();
			zipFileName = "translations-" + Time.getTimestamp() + ".zip";
		}

		InputStream inputStream = new AutoDeleteFileInputStream(file);

		ServletResponseUtil.sendFile(
			httpServletRequest, httpServletResponse, zipFileName, inputStream,
			0, ContentTypes.APPLICATION_ZIP,
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
	}

	private Set<String> _getAllowedClassNames(long companyId) {
		Set<String> set = new HashSet<>();

		for (String externalReferenceCode : _ALLOWED_OBJECT_ENTRY_ERCS) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, companyId);

			if (objectDefinition == null) {
				continue;
			}

			set.add(objectDefinition.getClassName());
		}

		return set;
	}

	private JSONArray _getSelectAllJSONArray(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		JSONArray jsonArray = _jsonFactory.createJSONArray();
		SearchHits searchHits = getSelectAllSearchHits(httpServletRequest);

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			JSONObject jsonObject = _jsonFactory.createJSONObject();

			jsonObject.put(
				"className", document.getString("entryClassName")
			).put(
				"classPK", document.getLong("entryClassPK")
			);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static final String[] _ALLOWED_OBJECT_ENTRY_ERCS = {
		"L_CMS_BASIC_WEB_CONTENT", "L_CMS_BLOG"
	};

	private static final String _EXPORT_TRANSLATION_BULK_ACTION =
		"ExportTranslationBulkAction";

	private static final Log _log = LogFactoryUtil.getLog(
		TranslateObjectEntryCMSServlet.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private TranslationManager _translationManager;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}