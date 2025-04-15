/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.exception.RequiredLayoutPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/delete_master_layout"
	},
	service = MVCActionCommand.class
)
public class DeleteMasterLayoutMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteLayoutPageTemplateEntryIds = null;

		long layoutPageTemplateEntryId = ParamUtil.getLong(
			actionRequest, "layoutPageTemplateEntryId");

		if (layoutPageTemplateEntryId > 0) {
			deleteLayoutPageTemplateEntryIds = new long[] {
				layoutPageTemplateEntryId
			};
		}
		else {
			deleteLayoutPageTemplateEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		List<Long> deleteLayoutPageTemplateIdsList = new ArrayList<>();

		for (long deleteLayoutPageTemplateEntryId :
				deleteLayoutPageTemplateEntryIds) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
					deleteLayoutPageTemplateEntryId);

			if (layoutPageTemplateEntry == null) {
				SessionErrors.add(actionRequest, PortalException.class);

				deleteLayoutPageTemplateIdsList.add(
					deleteLayoutPageTemplateEntryId);

				continue;
			}

			int count = _layoutLocalService.getMasterLayoutsCount(
				layoutPageTemplateEntry.getGroupId(),
				layoutPageTemplateEntry.getPlid());

			if (count > 0) {
				SessionErrors.add(
					actionRequest,
					RequiredLayoutPageTemplateEntryException.class);

				deleteLayoutPageTemplateIdsList.add(
					deleteLayoutPageTemplateEntryId);

				continue;
			}

			try {
				_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
					deleteLayoutPageTemplateEntryId);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				SessionErrors.add(actionRequest, PortalException.class);

				deleteLayoutPageTemplateIdsList.add(
					deleteLayoutPageTemplateEntryId);
			}
		}

		if (!SessionErrors.isEmpty(actionRequest)) {
			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteMasterLayoutMVCActionCommand.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

}