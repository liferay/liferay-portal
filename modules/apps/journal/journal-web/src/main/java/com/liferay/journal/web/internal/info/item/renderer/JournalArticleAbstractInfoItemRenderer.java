/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.renderer;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.journal.model.JournalArticle;
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
public class JournalArticleAbstractInfoItemRenderer
	implements InfoItemRenderer<JournalArticle> {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "default-template-shorten");
	}

	@Override
	public void render(
		JournalArticle article, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!JournalArticleRendererUtil.isShowArticle(
				httpServletRequest, article)) {

			return;
		}

		try {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
					JournalArticle.class);

			httpServletRequest.setAttribute(
				WebKeys.ASSET_RENDERER,
				assetRendererFactory.getAssetRenderer(
					article.getResourcePrimKey()));

			httpServletRequest.setAttribute(WebKeys.JOURNAL_ARTICLE, article);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer/abstract.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.journal.web)")
	private ServletContext _servletContext;

}