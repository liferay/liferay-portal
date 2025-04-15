/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.browser.web.internal.item.selector;

import com.liferay.asset.browser.web.internal.display.context.AssetBrowserDisplayContext;
import com.liferay.asset.browser.web.internal.frontend.taglib.clay.servlet.taglib.AssetEntryVerticalCard;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Barbara Cabrera
 */
public class AssetEntryItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public AssetEntryItemDescriptor(
		AssetBrowserDisplayContext assetBrowserDisplayContext,
		AssetEntry assetEntry, HttpServletRequest httpServletRequest) {

		_assetBrowserDisplayContext = assetBrowserDisplayContext;
		_assetEntry = assetEntry;
		_httpServletRequest = httpServletRequest;

		_assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntry.getClassName());
	}

	@Override
	public String getIcon() {
		return _assetRendererFactory.getIconCssClass();
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return _assetEntry.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"assetEntryId", String.valueOf(_assetEntry.getEntryId())
		).put(
			"assetType",
			() -> {
				if (!_assetRendererFactory.isSupportsClassTypes()) {
					return _assetRendererFactory.getTypeName(
						themeDisplay.getLocale(), _assetEntry.getClassTypeId());
				}

				ClassTypeReader classTypeReader =
					_assetRendererFactory.getClassTypeReader();

				ClassType classType = classTypeReader.getClassType(
					_assetEntry.getClassTypeId(), themeDisplay.getLocale());

				return classType.getName();
			}
		).put(
			"className", _assetEntry.getClassName()
		).put(
			"classNameId", _assetEntry.getClassNameId()
		).put(
			"classPK", String.valueOf(_assetEntry.getClassPK())
		).put(
			"groupDescriptiveName",
			() -> {
				Group group = GroupLocalServiceUtil.fetchGroup(
					_assetEntry.getGroupId());

				return group.getDescriptiveName(themeDisplay.getLocale());
			}
		).put(
			"title", _assetEntry.getTitle(themeDisplay.getLocale())
		).toString();
	}

	@Override
	public Integer getStatus() {
		AssetRenderer<?> assetRenderer = _assetEntry.getAssetRenderer();

		return assetRenderer.getStatus();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _assetEntry.getTitle(locale);
	}

	@Override
	public String getUserName() {
		return _assetEntry.getUserName();
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new AssetEntryVerticalCard(
			_assetEntry, renderRequest, _assetBrowserDisplayContext);
	}

	private final AssetBrowserDisplayContext _assetBrowserDisplayContext;
	private final AssetEntry _assetEntry;
	private final AssetRendererFactory<?> _assetRendererFactory;
	private final HttpServletRequest _httpServletRequest;

}