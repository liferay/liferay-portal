/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.web.internal.servlet.taglib.util.BasicFragmentCompositionActionDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseBaseClayCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

/**
 * @author Pavel Savinov
 */
public class BasicFragmentCompositionVerticalCard
	extends BaseBaseClayCard implements VerticalCard {

	public BasicFragmentCompositionVerticalCard(
		FragmentComposition fragmentComposition, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(fragmentComposition, rowChecker);

		_fragmentComposition = fragmentComposition;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		BasicFragmentCompositionActionDropdownItemsProvider
			basicFragmentCompositionActionDropdownItemsProvider =
				new BasicFragmentCompositionActionDropdownItemsProvider(
					_fragmentComposition, _renderRequest, _renderResponse);

		try {
			return basicFragmentCompositionActionDropdownItemsProvider.
				getActionDropdownItems();
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
		if (_fragmentComposition.isMarketplace()) {
			return "marketplace";
		}

		return "edit-layout";
	}

	@Override
	public String getImageSrc() {
		return _fragmentComposition.getImagePreviewURL(_themeDisplay);
	}

	@Override
	public String getInputName() {
		return rowChecker.getRowIds() +
			FragmentComposition.class.getSimpleName();
	}

	@Override
	public String getInputValue() {
		return String.valueOf(_fragmentComposition.getFragmentCompositionId());
	}

	@Override
	public List<LabelItem> getLabels() {
		if (_fragmentComposition.isMarketplace()) {
			return null;
		}

		return LabelItemListBuilder.add(
			labelItem -> labelItem.setStatus(_fragmentComposition.getStatus())
		).build();
	}

	@Override
	public String getStickerCssClass() {
		if (_fragmentComposition.isMarketplace()) {
			return "fragment-marketplace-sticker";
		}

		return "fragment-composition-sticker";
	}

	@Override
	public String getStickerIcon() {
		if (_fragmentComposition.isMarketplace()) {
			return "marketplace";
		}

		return getIcon();
	}

	@Override
	public String getSubtitle() {
		if (_fragmentComposition.isMarketplace()) {
			return null;
		}

		Date modifiedDate = _fragmentComposition.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - modifiedDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "modified-x-ago", modifiedDateDescription);
	}

	@Override
	public String getTitle() {
		return HtmlUtil.escape(_fragmentComposition.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasicFragmentCompositionVerticalCard.class);

	private final FragmentComposition _fragmentComposition;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}