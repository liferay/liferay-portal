/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
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
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/delete_layout_utility_page_entry_preview"
	},
	service = MVCActionCommand.class
)
public class DeleteLayoutUtilityPageEntryPreviewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutUtilityPageEntryId = ParamUtil.getLong(
			actionRequest, "layoutUtilityPageEntryId");

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.fetchLayoutUtilityPageEntry(
				layoutUtilityPageEntryId);

		if ((layoutUtilityPageEntry != null) &&
			(layoutUtilityPageEntry.getPreviewFileEntryId() > 0)) {

			_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
				layoutUtilityPageEntryId, 0);

			_portletFileRepository.deletePortletFileEntry(
				layoutUtilityPageEntry.getPreviewFileEntryId());
		}
	}

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}