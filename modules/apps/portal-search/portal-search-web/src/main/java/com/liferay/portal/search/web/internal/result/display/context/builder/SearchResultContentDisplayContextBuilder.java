/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.result.display.context.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SearchResultContentDisplayContextBuilder {

	public SearchResultContentDisplayContext build() throws Exception {
		SearchResultContentDisplayContext searchResultContentDisplayContext =
			new SearchResultContentDisplayContext();

		searchResultContentDisplayContext.setAssetRendererFactory(
			getAssetRendererFactoryByType(_type));

		AssetEntry assetEntry = getAssetEntry();

		searchResultContentDisplayContext.setAssetEntry(assetEntry);

		AssetRenderer<?> assetRenderer = null;

		if (assetEntry != null) {
			assetRenderer = assetEntry.getAssetRenderer();
		}

		searchResultContentDisplayContext.setAssetRenderer(assetRenderer);

		boolean visible = false;

		if ((assetEntry != null) && (assetRenderer != null) &&
			assetEntry.isVisible() &&
			assetRenderer.hasViewPermission(_permissionChecker)) {

			visible = true;
		}

		searchResultContentDisplayContext.setVisible(visible);

		if (visible) {
			String title = assetRenderer.getTitle(_locale);

			searchResultContentDisplayContext.setHeaderTitle(title);

			boolean hasEditPermission = assetRenderer.hasEditPermission(
				_permissionChecker);

			searchResultContentDisplayContext.setHasEditPermission(
				hasEditPermission);

			if (hasEditPermission) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				searchResultContentDisplayContext.setIconEditTarget(title);
				searchResultContentDisplayContext.setIconURLString(
					PortletURLBuilder.create(
						assetRenderer.getURLEdit(
							_portal.getLiferayPortletRequest(_renderRequest),
							_portal.getLiferayPortletResponse(_renderResponse))
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setPortletResource(
						() -> {
							PortletDisplay portletDisplay =
								themeDisplay.getPortletDisplay();

							return portletDisplay.getId();
						}
					).buildString());
			}

			searchResultContentDisplayContext.setShowExtraInfo(
				_type.equals("document"));
		}

		return searchResultContentDisplayContext;
	}

	public void setAssetEntryId(long assetEntryId) {
		_assetEntryId = assetEntryId;
	}

	public void setAssetRendererFactoryLookup(
		AssetRendererFactoryLookup assetRendererFactoryLookup) {

		_assetRendererFactoryLookup = assetRendererFactoryLookup;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setPermissionChecker(PermissionChecker permissionChecker) {
		_permissionChecker = permissionChecker;
	}

	public void setPortal(Portal portal) {
		_portal = portal;
	}

	public void setRenderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public void setRenderResponse(RenderResponse renderResponse) {
		_renderResponse = renderResponse;
	}

	public void setType(String type) {
		_type = type;
	}

	protected AssetEntry getAssetEntry() throws PortalException {
		return AssetEntryLocalServiceUtil.getAssetEntry(_assetEntryId);
	}

	protected AssetRendererFactory<?> getAssetRendererFactoryByType(
		String type) {

		if (_assetRendererFactoryLookup != null) {
			return _assetRendererFactoryLookup.getAssetRendererFactoryByType(
				type);
		}

		return AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
			type);
	}

	private long _assetEntryId;
	private AssetRendererFactoryLookup _assetRendererFactoryLookup;
	private Locale _locale;
	private PermissionChecker _permissionChecker;
	private Portal _portal;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private String _type;

}