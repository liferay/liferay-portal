/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.browser.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.asset.browser.web.internal.display.context.AssetBrowserDisplayContext;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AssetEntryVerticalCard implements VerticalCard {

	public AssetEntryVerticalCard(
		AssetEntry assetEntry, RenderRequest renderRequest,
		AssetBrowserDisplayContext assetBrowserDisplayContext) {

		_assetEntry = assetEntry;
		_renderRequest = renderRequest;
		_assetBrowserDisplayContext = assetBrowserDisplayContext;

		_assetRenderer = assetEntry.getAssetRenderer();
		_assetRendererFactory =
			assetBrowserDisplayContext.getAssetRendererFactory();
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getCssClass() {
		if (_assetEntry.getEntryId() !=
				_assetBrowserDisplayContext.getRefererAssetEntryId()) {

			return "card-interactive card-interactive-secondary " +
				"selector-button";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getIcon() {
		return _assetRendererFactory.getIconCssClass();
	}

	@Override
	public String getImageSrc() {
		try {
			return _assetRenderer.getThumbnailPath(_renderRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getInputValue() {
		return String.valueOf(_assetEntry.getEntryId());
	}

	@Override
	public List<LabelItem> getLabels() {
		if (!_assetBrowserDisplayContext.isShowAssetEntryStatus()) {
			return Collections.emptyList();
		}

		return LabelItemListBuilder.add(
			labelItem -> labelItem.setStatus(_assetRenderer.getStatus())
		).build();
	}

	@Override
	public String getSubtitle() {
		if (Validator.isNull(_assetBrowserDisplayContext.getTypeSelection())) {
			return HtmlUtil.escape(
				_assetRendererFactory.getTypeName(
					_themeDisplay.getLocale(),
					_assetBrowserDisplayContext.getSubtypeSelectionId()));
		}

		if (!_assetBrowserDisplayContext.isSearchEverywhere()) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			_assetEntry.getGroupId());

		try {
			return HtmlUtil.escape(
				group.getDescriptiveName(_themeDisplay.getLocale()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getTitle() {
		return _assetRenderer.getTitle(_themeDisplay.getLocale());
	}

	@Override
	public boolean isDisabled() {
		if (_assetEntry.getEntryId() ==
				_assetBrowserDisplayContext.getRefererAssetEntryId()) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isSelectable() {
		return _assetBrowserDisplayContext.isMultipleSelection();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntryVerticalCard.class);

	private final AssetBrowserDisplayContext _assetBrowserDisplayContext;
	private final AssetEntry _assetEntry;
	private final AssetRenderer<?> _assetRenderer;
	private final AssetRendererFactory<?> _assetRendererFactory;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;

}