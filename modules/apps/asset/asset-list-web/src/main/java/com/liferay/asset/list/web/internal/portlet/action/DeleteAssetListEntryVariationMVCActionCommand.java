/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/delete_asset_list_entry_variation"
	},
	service = MVCActionCommand.class
)
public class DeleteAssetListEntryVariationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long assetListEntryId = ParamUtil.getLong(
			actionRequest, "assetListEntryId");

		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		_assetListEntryService.deleteAssetListEntry(
			assetListEntryId, segmentsEntryId);

		sendRedirect(
			actionRequest, actionResponse,
			getRedirectURL(actionRequest, actionResponse, assetListEntryId));
	}

	protected String getRedirectURL(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long assetListEntryId) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCPath(
			"/edit_asset_list_entry.jsp"
		).setBackURL(
			ParamUtil.getString(actionRequest, "backURL")
		).setParameter(
			"assetListEntryId", assetListEntryId
		).setParameter(
			"backURLTitle",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _language.get(themeDisplay.getLocale(), "collections");
			}
		).buildString();
	}

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}