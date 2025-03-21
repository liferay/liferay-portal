/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.web.internal.servlet.taglib.util.ContributedFragmentCompositionActionDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseBaseClayCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ContributedFragmentCompositionVerticalCard
	extends BaseBaseClayCard implements VerticalCard {

	public ContributedFragmentCompositionVerticalCard(
		FragmentComposition fragmentComposition, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(fragmentComposition, rowChecker);

		_fragmentComposition = fragmentComposition;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ContributedFragmentCompositionActionDropdownItemsProvider
			contributedFragmentEntryActionDropdownItemsProvider =
				new ContributedFragmentCompositionActionDropdownItemsProvider(
					_fragmentComposition, _renderRequest, _renderResponse);

		try {
			return contributedFragmentEntryActionDropdownItemsProvider.
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
	public String getImageSrc() {
		return _fragmentComposition.getImagePreviewURL(_themeDisplay);
	}

	@Override
	public String getInputValue() {
		return _fragmentComposition.getFragmentCompositionKey();
	}

	@Override
	public String getStickerCssClass() {
		return "fragment-composition-sticker";
	}

	@Override
	public String getStickerIcon() {
		return "edit-layout";
	}

	@Override
	public String getTitle() {
		return _fragmentComposition.getName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContributedFragmentCompositionVerticalCard.class);

	private final FragmentComposition _fragmentComposition;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}