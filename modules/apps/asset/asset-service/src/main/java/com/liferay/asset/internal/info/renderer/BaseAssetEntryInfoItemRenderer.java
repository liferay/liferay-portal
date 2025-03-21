/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.info.renderer;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Jorge Ferrer
 */
public abstract class BaseAssetEntryInfoItemRenderer
	implements InfoItemRenderer<AssetEntry> {

	@Override
	public void render(
		AssetEntry assetEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						assetEntry.getClassName());

			httpServletRequest.setAttribute(
				WebKeys.ASSET_RENDERER_FACTORY, assetRendererFactory);

			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());

			httpServletRequest.setAttribute(
				WebKeys.ASSET_RENDERER, assetRenderer);

			assetRenderer.include(
				httpServletRequest, httpServletResponse, getTemplate());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected abstract String getTemplate();

}