/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.util.RankingResultUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;

/**
 * @author Wade Cao
 */
public class RankingResultContentDisplayBuilder {

	@SuppressWarnings("deprecation")
	public RankingResultContentDisplayContext build() throws Exception {
		RankingResultContentDisplayContext rankingResultContentDisplayContext =
			new RankingResultContentDisplayContext();

		AssetRenderer<?> assetRenderer = RankingResultUtil.getAssetRenderer(
			_entryClassName, _entryClassPK);

		if (assetRenderer == null) {
			return rankingResultContentDisplayContext;
		}

		AssetRendererFactory<?> assetRendererFactory =
			assetRenderer.getAssetRendererFactory();

		rankingResultContentDisplayContext.setAssetRendererFactory(
			assetRendererFactory);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			_assetEntryId);

		rankingResultContentDisplayContext.setAssetEntry(assetEntry);

		rankingResultContentDisplayContext.setAssetRenderer(assetRenderer);

		boolean visible;

		if ((assetEntry != null) && (assetRenderer != null) &&
			assetEntry.isVisible() &&
			assetRenderer.hasViewPermission(_permissionChecker)) {

			visible = true;
		}
		else {
			visible = false;
		}

		rankingResultContentDisplayContext.setVisible(visible);

		if (visible) {
			String title = assetRenderer.getTitle(_locale);

			rankingResultContentDisplayContext.setHeaderTitle(title);

			boolean hasEditPermission = assetRenderer.hasEditPermission(
				_permissionChecker);

			rankingResultContentDisplayContext.setHasEditPermission(
				hasEditPermission);

			if (hasEditPermission) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				rankingResultContentDisplayContext.setIconEditTarget(title);
				rankingResultContentDisplayContext.setIconURLString(
					PortletURLBuilder.create(
						assetRenderer.getURLEdit(
							_portal.getLiferayPortletRequest(_renderRequest),
							_portal.getLiferayPortletResponse(_renderResponse))
					).setRedirect(
						themeDisplay.getURLCurrent()
					).buildString());
			}
		}

		return rankingResultContentDisplayContext;
	}

	public void setAssetEntryId(long assetEntryId) {
		_assetEntryId = assetEntryId;
	}

	public void setEntryClassName(String entryClassName) {
		_entryClassName = entryClassName;
	}

	public void setEntryClassPK(long entryClassPK) {
		_entryClassPK = entryClassPK;
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

	private long _assetEntryId;
	private String _entryClassName;
	private long _entryClassPK;
	private Locale _locale;
	private PermissionChecker _permissionChecker;
	private Portal _portal;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;

}