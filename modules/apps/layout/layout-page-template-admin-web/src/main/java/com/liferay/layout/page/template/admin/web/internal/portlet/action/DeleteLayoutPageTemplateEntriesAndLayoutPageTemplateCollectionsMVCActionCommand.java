/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yurena Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/delete_layout_page_template_entries_and_layout_page_template_collections"
	},
	service = MVCActionCommand.class
)
public class
	DeleteLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand
		extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		List<Long> deleteLayoutPageTemplateCollectionIdsList =
			new ArrayList<>();

		long[] deleteLayoutPageTemplateCollectionIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsLayoutPageTemplateCollection");

		for (long deleteLayoutPageTemplateCollectionId :
				deleteLayoutPageTemplateCollectionIds) {

			try {
				_layoutPageTemplateCollectionService.
					deleteLayoutPageTemplateCollection(
						deleteLayoutPageTemplateCollectionId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				deleteLayoutPageTemplateCollectionIdsList.add(
					deleteLayoutPageTemplateCollectionId);
			}
		}

		List<Long> deleteLayoutPageTemplateEntryIdsList = new ArrayList<>();

		long[] deleteLayoutPageTemplateEntryIds = ParamUtil.getLongValues(
			actionRequest, "rowIds");

		for (long deleteLayoutPageTemplateEntryId :
				deleteLayoutPageTemplateEntryIds) {

			try {
				_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
					deleteLayoutPageTemplateEntryId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				deleteLayoutPageTemplateEntryIdsList.add(
					deleteLayoutPageTemplateEntryId);
			}
		}

		if ((deleteLayoutPageTemplateCollectionIds.length ==
				deleteLayoutPageTemplateCollectionIdsList.size()) &&
			(deleteLayoutPageTemplateEntryIds.length ==
				deleteLayoutPageTemplateEntryIdsList.size())) {

			SessionErrors.add(actionRequest, PortalException.class);

			sendRedirect(actionRequest, actionResponse);

			return;
		}

		hideDefaultSuccessMessage(actionRequest);

		MultiSessionMessages.add(
			actionRequest, "displayPageTemplateDeleted",
			_getMessage(
				deleteLayoutPageTemplateEntryIds.length -
					deleteLayoutPageTemplateEntryIdsList.size(),
				deleteLayoutPageTemplateCollectionIds.length -
					deleteLayoutPageTemplateCollectionIdsList.size(),
				_portal.getHttpServletRequest(actionRequest)));
	}

	private String _getMessage(
		int displayPageTemplates, int folders,
		HttpServletRequest httpServletRequest) {

		if ((displayPageTemplates == 1) && (folders == 1)) {
			return _language.format(
				httpServletRequest,
				"you-successfully-deleted-x-display-page-template-and-x-folder",
				new Object[] {displayPageTemplates, folders});
		}

		if ((displayPageTemplates == 1) && (folders > 1)) {
			return _language.format(
				httpServletRequest,
				"you-successfully-deleted-x-display-page-template-and-x-" +
					"folders",
				new Object[] {displayPageTemplates, folders});
		}

		if ((displayPageTemplates > 1) && (folders == 1)) {
			return _language.format(
				httpServletRequest,
				"you-successfully-deleted-x-display-page-templates-and-x-" +
					"folder",
				new Object[] {displayPageTemplates, folders});
		}

		return _language.format(
			httpServletRequest,
			"you-successfully-deleted-x-display-page-templates-and-x-folders",
			new Object[] {displayPageTemplates, folders});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}