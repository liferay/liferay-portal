/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.servlet.taglib.util;

import com.liferay.asset.list.web.internal.display.context.EditAssetListDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Diego Hu
 */
public class AssetListEntryVariationActionDropdownItemsProvider {

	public AssetListEntryVariationActionDropdownItemsProvider(
		EditAssetListDisplayContext editAssetListDisplayContext,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_editAssetListDisplayContext = editAssetListDisplayContext;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			() -> Validator.isNull(
				_editAssetListDisplayContext.getAudienceEntryERC()),
			dropdownItem -> {
				dropdownItem.putData(
					"action", "viewAssetListEntryVariationItems");
				dropdownItem.putData(
					"viewAssetListEntryVariationItemsURL",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/view_asset_list_items.jsp"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"assetListEntryId",
						_editAssetListDisplayContext.getAssetListEntryId()
					).setParameter(
						"segmentsEntryId",
						_editAssetListDisplayContext.getSegmentsEntryId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("view");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "view-items"));
			}
		).add(
			() ->
				(Validator.isNotNull(
					_editAssetListDisplayContext.getAudienceEntryERC()) ||
				 (_editAssetListDisplayContext.getSegmentsEntryId() !=
					 SegmentsEntryConstants.ID_DEFAULT)) &&
				!_editAssetListDisplayContext.isLiveGroup(),
			dropdownItem -> {
				dropdownItem.putData("action", "deleteAssetListEntryVariation");
				dropdownItem.putData(
					"deleteAssetListEntryVariationURL",
					_getDeleteAssetListEntryVariationURL());
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	private String _getDeleteAssetListEntryVariationURL() {
		String audienceEntryERC =
			_editAssetListDisplayContext.getAudienceEntryERC();

		if (Validator.isNotNull(audienceEntryERC)) {
			return PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/asset_list/delete_asset_list_entry_audience_variation"
			).setBackURL(
				_editAssetListDisplayContext.getBackURL()
			).setParameter(
				"assetListEntryId",
				_editAssetListDisplayContext.getAssetListEntryId()
			).setParameter(
				"audienceEntryERC", audienceEntryERC
			).buildString();
		}

		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/asset_list/delete_asset_list_entry_variation"
		).setParameter(
			"assetListEntryId",
			_editAssetListDisplayContext.getAssetListEntryId()
		).setParameter(
			"segmentsEntryId", _editAssetListDisplayContext.getSegmentsEntryId()
		).buildString();
	}

	private final EditAssetListDisplayContext _editAssetListDisplayContext;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}