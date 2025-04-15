/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseBaseClayCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseFragmentEntryVerticalCard
	extends BaseBaseClayCard implements VerticalCard {

	public BaseFragmentEntryVerticalCard(
		FragmentEntry fragmentEntry, RenderRequest renderRequest,
		RowChecker rowChecker) {

		super(fragmentEntry, rowChecker);

		this.fragmentEntry = fragmentEntry;
		themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getIcon() {
		return fragmentEntry.getIcon();
	}

	@Override
	public String getImageSrc() {
		return fragmentEntry.getImagePreviewURL(themeDisplay);
	}

	@Override
	public String getInputName() {
		return rowChecker.getRowIds() + FragmentEntry.class.getSimpleName();
	}

	@Override
	public String getInputValue() {
		return String.valueOf(fragmentEntry.getFragmentEntryId());
	}

	@Override
	public String getStickerCssClass() {
		if (fragmentEntry.isTypeInput()) {
			return "fragment-entry-input-sticker";
		}

		return "fragment-entry-basic-sticker";
	}

	@Override
	public String getStickerIcon() {
		return getIcon();
	}

	@Override
	public String getTitle() {
		return HtmlUtil.escape(fragmentEntry.getName());
	}

	protected final FragmentEntry fragmentEntry;
	protected final ThemeDisplay themeDisplay;

}