/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.item.renderer;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "service.ranking:Integer=200", service = InfoItemRenderer.class
)
public class BlogsEntryAbstractInfoItemRenderer
	implements InfoItemRenderer<BlogsEntry> {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "abstract");
	}

	@Override
	public void render(
		BlogsEntry entry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				BlogsEntry.class);

		try {
			httpServletRequest.setAttribute(
				WebKeys.ASSET_RENDERER,
				assetRendererFactory.getAssetRenderer(entry.getEntryId()));
			httpServletRequest.setAttribute(WebKeys.BLOGS_ENTRY, entry);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/blogs/info/item/renderer/abstract.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.blogs.web)")
	private ServletContext _servletContext;

}