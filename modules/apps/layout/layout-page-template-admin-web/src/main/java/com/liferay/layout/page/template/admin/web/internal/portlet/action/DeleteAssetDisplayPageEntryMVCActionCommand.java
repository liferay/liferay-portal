/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/delete_asset_display_page_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteAssetDisplayPageEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteAssetDisplayPageEntryIds = null;

		long assetDisplayPageEntryId = ParamUtil.getLong(
			actionRequest, "assetDisplayPageEntryId");

		if (assetDisplayPageEntryId > 0) {
			deleteAssetDisplayPageEntryIds = new long[] {
				assetDisplayPageEntryId
			};
		}
		else {
			deleteAssetDisplayPageEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		try {
			for (long deleteAssetDisplayPageEntryId :
					deleteAssetDisplayPageEntryIds) {

				_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
					deleteAssetDisplayPageEntryId);
			}

			hideDefaultSuccessMessage(actionRequest);

			MultiSessionMessages.add(actionRequest, "displayPageAssigned");
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, portalException.getClass());

			hideDefaultErrorMessage(actionRequest);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

}