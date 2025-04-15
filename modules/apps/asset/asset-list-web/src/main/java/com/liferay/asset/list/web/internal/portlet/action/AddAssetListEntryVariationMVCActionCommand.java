/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;

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
		"mvc.command.name=/asset_list/add_asset_list_entry_variation"
	},
	service = MVCActionCommand.class
)
public class AddAssetListEntryVariationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long assetListEntryId = ParamUtil.getLong(
			actionRequest, "assetListEntryId");

		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		int type = ParamUtil.getInteger(actionRequest, "type");

		if (type == AssetListEntryTypeConstants.TYPE_DYNAMIC) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			unicodeProperties.setProperty(
				"groupIds", String.valueOf(themeDisplay.getScopeGroupId()));
		}
		else if (type == AssetListEntryTypeConstants.TYPE_MANUAL) {
			AssetListEntry assetListEntry =
				_assetListEntryLocalService.fetchAssetListEntry(
					assetListEntryId);

			unicodeProperties.load(
				assetListEntry.getTypeSettings(
					SegmentsEntryConstants.ID_DEFAULT));
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_assetListEntryService.updateAssetListEntry(
			assetListEntryId, segmentsEntryId, unicodeProperties.toString(),
			serviceContext);

		sendRedirect(
			actionRequest, actionResponse,
			getRedirectURL(
				actionRequest, actionResponse, assetListEntryId,
				segmentsEntryId));
	}

	protected String getRedirectURL(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long assetListEntryId, long segmentsEntryId) {

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
		).setParameter(
			"segmentsEntryId", segmentsEntryId
		).buildString();
	}

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}