/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/import"
	},
	service = MVCResourceCommand.class
)
public class ImportMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long layoutPageTemplateCollectionId = ParamUtil.getLong(
			resourceRequest, "layoutPageTemplateCollectionId");

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(resourceRequest);

		File file = uploadPortletRequest.getFile("file");

		String importType = ParamUtil.getString(resourceRequest, "importType");

		boolean validFile = true;

		if (Validator.isNull(importType)) {
			validFile = _layoutsImporter.validateFile(
				themeDisplay.getScopeGroupId(), layoutPageTemplateCollectionId,
				file);
		}

		if (validFile) {
			LayoutsImportStrategy layoutsImportStrategy =
				LayoutsImportStrategy.create(importType);

			if (layoutsImportStrategy == null) {
				layoutsImportStrategy = LayoutsImportStrategy.DO_NOT_OVERWRITE;
			}

			jsonObject = _importFile(
				file, themeDisplay.getScopeGroupId(),
				layoutPageTemplateCollectionId, layoutsImportStrategy,
				themeDisplay.getLocale(), themeDisplay.getUserId());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		else {
			jsonObject.put("invalid", true);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	private String _getKey(
		LayoutsImporterResultEntry.Status status, boolean hasWarningMessages) {

		if (status == LayoutsImporterResultEntry.Status.IGNORED) {
			return "warning";
		}

		if (status == LayoutsImporterResultEntry.Status.IMPORTED) {
			if (hasWarningMessages) {
				return "warning";
			}

			return "success";
		}

		if (status == LayoutsImporterResultEntry.Status.INVALID) {
			return "error";
		}

		return StringPool.BLANK;
	}

	private JSONObject _importFile(
		File file, long groupId, long layoutPageTemplateCollectionId,
		LayoutsImportStrategy layoutsImportStrategy, Locale locale,
		long userId) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
				_layoutsImporter.importFile(
					userId, groupId, layoutPageTemplateCollectionId, file,
					layoutsImportStrategy, true);

			JSONObject importResultsJSONObject =
				_jsonFactory.createJSONObject();

			for (LayoutsImporterResultEntry layoutsImporterResultEntry :
					layoutsImporterResultEntries) {

				String key = _getKey(
					layoutsImporterResultEntry.getStatus(),
					ArrayUtil.isNotEmpty(
						layoutsImporterResultEntry.getWarningMessages()));

				JSONArray jsonArray = importResultsJSONObject.getJSONArray(key);

				if (jsonArray == null) {
					jsonArray = _jsonFactory.createJSONArray();
				}

				jsonArray.put(
					JSONUtil.put(
						"messages",
						() -> {
							if (ArrayUtil.isNotEmpty(
									layoutsImporterResultEntry.
										getWarningMessages())) {

								return layoutsImporterResultEntry.
									getWarningMessages();
							}

							if (Validator.isNotNull(
									layoutsImporterResultEntry.getErrorMessage(
										locale))) {

								return Collections.singletonList(
									layoutsImporterResultEntry.getErrorMessage(
										locale));
							}

							return Collections.emptyList();
						}
					).put(
						"name", layoutsImporterResultEntry.getName()
					).put(
						"type", layoutsImporterResultEntry.getType()
					));

				importResultsJSONObject.put(key, jsonArray);
			}

			jsonObject.put("importResults", importResultsJSONObject);
		}
		catch (Exception exception) {
			_log.error(exception);

			jsonObject.put(
				"error", _language.get(locale, "an-unexpected-error-occurred"));
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportMVCResourceCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutsImporter _layoutsImporter;

	@Reference
	private Portal _portal;

}