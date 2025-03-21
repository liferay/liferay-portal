/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.exception.RequiredLayoutPageTemplateEntryException;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/delete_layout_utility_page_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteLayoutUtilityPageEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteLayoutUtilityPageEntryIds = null;

		long layoutUtilityPageEntryId = ParamUtil.getLong(
			actionRequest, "layoutUtilityPageEntryId");

		if (layoutUtilityPageEntryId > 0) {
			deleteLayoutUtilityPageEntryIds = new long[] {
				layoutUtilityPageEntryId
			};
		}
		else {
			deleteLayoutUtilityPageEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		List<Long> deleteLayoutUtilityPageIdsList = new ArrayList<>();

		for (long deleteLayoutUtilityPageEntryId :
				deleteLayoutUtilityPageEntryIds) {

			int layoutUtilityEntriesCount =
				_layoutUtilityPageEntryService.getLayoutUtilityPageEntriesCount(
					deleteLayoutUtilityPageEntryId);

			try {
				if (layoutUtilityEntriesCount > 0) {
					deleteLayoutUtilityPageIdsList.add(
						deleteLayoutUtilityPageEntryId);

					SessionErrors.add(
						actionRequest,
						RequiredLayoutPageTemplateEntryException.class);
				}
				else {
					_layoutUtilityPageEntryService.deleteLayoutUtilityPageEntry(
						deleteLayoutUtilityPageEntryId);
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				deleteLayoutUtilityPageIdsList.add(
					deleteLayoutUtilityPageEntryId);
			}
		}

		if (!deleteLayoutUtilityPageIdsList.isEmpty()) {
			SessionErrors.add(actionRequest, PortalException.class);

			hideDefaultErrorMessage(actionRequest);
		}
		else {
			int total =
				deleteLayoutUtilityPageEntryIds.length -
					deleteLayoutUtilityPageIdsList.size();

			if (total > 0) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, "layoutUtilityPageDeleted",
					_language.format(
						_portal.getHttpServletRequest(actionRequest),
						"you-successfully-deleted-x-utility-page",
						new Object[] {total}));
			}
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteLayoutUtilityPageEntryMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Reference
	private Portal _portal;

}