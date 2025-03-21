/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/import_layout_utility_page_entries"
	},
	service = MVCActionCommand.class
)
public class ImportLayoutUtilityPageEntriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String successMessage = _language.get(
			_portal.getHttpServletRequest(actionRequest),
			"the-file-was-processed-correctly");

		SessionMessages.add(actionRequest, "requestProcessed", successMessage);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		File file = uploadPortletRequest.getFile("file");

		boolean overwrite = ParamUtil.getBoolean(
			actionRequest, "overwrite", true);

		LayoutsImportStrategy layoutsImportStrategy =
			LayoutsImportStrategy.OVERWRITE;

		if (!overwrite) {
			layoutsImportStrategy = LayoutsImportStrategy.DO_NOT_OVERWRITE;
		}

		try {
			List<LayoutsImporterResultEntry>
				layoutUtilityPageImporterResultEntries =
					_layoutsImporter.importFile(
						themeDisplay.getUserId(),
						themeDisplay.getScopeGroupId(), 0L, file,
						layoutsImportStrategy, true);

			if (ListUtil.isEmpty(layoutUtilityPageImporterResultEntries)) {
				return;
			}

			SessionMessages.add(
				actionRequest, "layoutUtilityPageImporterResultEntries",
				layoutUtilityPageImporterResultEntries);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportLayoutUtilityPageEntriesMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutsImporter _layoutsImporter;

	@Reference
	private Portal _portal;

}