/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.layout.admin.web.internal.servlet.taglib.util.LayoutUtilityPageEntryActionDropdownItemsProvider;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRenderer;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRendererRegistryUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutUtilityPageEntryVerticalCard extends BaseVerticalCard {

	public LayoutUtilityPageEntryVerticalCard(
		LayoutUtilityPageEntry layoutUtilityPageEntry,
		RenderRequest renderRequest, RenderResponse renderResponse,
		RowChecker rowChecker) {

		super(layoutUtilityPageEntry, renderRequest, rowChecker);

		_layoutUtilityPageEntry = layoutUtilityPageEntry;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layoutUtilityPageEntry.getPlid());
		_layout = LayoutLocalServiceUtil.fetchLayout(
			layoutUtilityPageEntry.getPlid());
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		LayoutUtilityPageEntryActionDropdownItemsProvider
			layoutUtilityPageEntryActionDropdownItemsProvider =
				new LayoutUtilityPageEntryActionDropdownItemsProvider(
					_layoutUtilityPageEntry, _renderRequest, _renderResponse);

		return layoutUtilityPageEntryActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	@Override
	public String getHref() {
		try {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(_draftLayout, themeDisplay),
				"p_l_back_url", themeDisplay.getURLCurrent(),
				"p_l_back_url_title", portletDisplay.getPortletDisplayName(),
				"p_l_mode", Constants.EDIT);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getImageSrc() {
		return _layoutUtilityPageEntry.getImagePreviewURL(themeDisplay);
	}

	@Override
	public List<LabelItem> getLabels() {
		if (StringUtil.startsWith(
				_layoutUtilityPageEntry.getExternalReferenceCode(), "LFR-")) {

			return LabelItemListBuilder.add(
				labelItem -> {
					labelItem.setDisplayType("info");
					labelItem.setLabel(
						LanguageUtil.get(
							themeDisplay.getLocale(), "provided-by-liferay"));
				}
			).build();
		}

		return LabelItemListBuilder.add(
			() -> _draftLayout != null,
			labelItem -> {
				if (_layout.isPublished()) {
					labelItem.setStatus(_draftLayout.getStatus());
				}
				else {
					labelItem.setStatus(_layout.getStatus());
				}
			}
		).build();
	}

	@Override
	public String getStickerCssClass() {
		return "sticker-primary";
	}

	@Override
	public String getStickerIcon() {
		if (_layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {
			return "check-circle";
		}

		return null;
	}

	@Override
	public String getStickerImageSrc() {
		return null;
	}

	@Override
	public String getStickerShape() {
		return null;
	}

	@Override
	public String getStickerStyle() {
		return "primary";
	}

	@Override
	public String getSubtitle() {
		LayoutUtilityPageEntryViewRenderer layoutUtilityPageEntryViewRenderer =
			LayoutUtilityPageEntryViewRendererRegistryUtil.
				getLayoutUtilityPageEntryViewRenderer(
					_layoutUtilityPageEntry.getType());

		if (layoutUtilityPageEntryViewRenderer == null) {
			_log.error("Invalid type" + _layoutUtilityPageEntry.getType());

			return null;
		}

		return layoutUtilityPageEntryViewRenderer.getLabel(
			themeDisplay.getLocale());
	}

	@Override
	public String getTitle() {
		return HtmlUtil.escape(_layoutUtilityPageEntry.getName());
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryVerticalCard.class);

	private final Layout _draftLayout;
	private final Layout _layout;
	private final LayoutUtilityPageEntry _layoutUtilityPageEntry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}