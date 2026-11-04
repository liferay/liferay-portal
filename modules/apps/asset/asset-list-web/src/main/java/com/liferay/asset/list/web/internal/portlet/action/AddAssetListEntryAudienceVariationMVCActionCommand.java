/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.asset.list.web.internal.util.AudienceVariationUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/add_asset_list_entry_audience_variation"
	},
	service = MVCActionCommand.class
)
public class AddAssetListEntryAudienceVariationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String audienceEntryERC = AudienceVariationUtil.getAudienceEntryERC(
			actionRequest);

		if (Validator.isNull(audienceEntryERC)) {
			return;
		}

		long assetListEntryId = ParamUtil.getLong(
			actionRequest, "assetListEntryId");

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					assetListEntryId, audienceEntryERC);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (assetListEntryAudienceEntryRel == null) {
			_assetListEntryService.updateAssetListEntry(
				assetListEntryId, audienceEntryERC,
				_getTypeSettings(assetListEntryId, themeDisplay),
				ServiceContextFactory.getInstance(actionRequest));
		}

		sendRedirect(
			actionRequest, actionResponse,
			PortletURLBuilder.createRenderURL(
				_portal.getLiferayPortletResponse(actionResponse)
			).setMVCPath(
				"/edit_asset_list_entry.jsp"
			).setBackURL(
				ParamUtil.getString(actionRequest, "backURL")
			).setParameter(
				"assetListEntryId", assetListEntryId
			).setParameter(
				"audienceEntryERC", audienceEntryERC
			).setParameter(
				"backURLTitle",
				_language.get(themeDisplay.getLocale(), "collections")
			).buildString());
	}

	private String _getTypeSettings(
			long assetListEntryId, ThemeDisplay themeDisplay)
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.getAssetListEntry(assetListEntryId);

		if (assetListEntry.getType() !=
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			return StringPool.BLANK;
		}

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"groupIds", themeDisplay.getScopeGroupId()
		).buildString();
	}

	@Reference
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}