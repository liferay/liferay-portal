/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/move_layout_page_template_entries_and_layout_page_template_collections"
	},
	service = MVCActionCommand.class
)
public class
	MoveLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand
		extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] layoutPageTemplateCollectionsIds = ParamUtil.getLongValues(
			actionRequest, "layoutPageTemplateCollectionsIds");
		long[] layoutPageTemplateEntriesIds = ParamUtil.getLongValues(
			actionRequest, "layoutPageTemplateEntriesIds");
		long layoutParentPageTemplateCollectionId = ParamUtil.getLong(
			actionRequest, "layoutParentPageTemplateCollectionId");

		try {
			for (long selectedLayoutPageTemplateCollectionId :
					layoutPageTemplateCollectionsIds) {

				_layoutPageTemplateCollectionService.
					moveLayoutPageTemplateCollection(
						selectedLayoutPageTemplateCollectionId,
						layoutParentPageTemplateCollectionId);
			}

			for (long selectedLayoutPageTemplateEntryId :
					layoutPageTemplateEntriesIds) {

				_layoutPageTemplateEntryService.moveLayoutPageTemplateEntry(
					selectedLayoutPageTemplateEntryId,
					layoutParentPageTemplateCollectionId);
			}

			sendRedirect(actionRequest, actionResponse);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MoveLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand.class);

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

}