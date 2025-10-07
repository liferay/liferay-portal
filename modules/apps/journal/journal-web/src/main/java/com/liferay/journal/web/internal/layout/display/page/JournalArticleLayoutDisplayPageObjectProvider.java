/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.layout.display.page;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.util.AssetHelper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;

/**
 * @author Jürgen Kappler
 */
public class JournalArticleLayoutDisplayPageObjectProvider
	implements LayoutDisplayPageObjectProvider<JournalArticle> {

	public JournalArticleLayoutDisplayPageObjectProvider(
			JournalArticle article, AssetHelper assetHelper)
		throws PortalException {

		_article = article;
		_assetHelper = assetHelper;

		_assetEntry = _getAssetEntry(article);
	}

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public long getClassNameId() {
		return _assetEntry.getClassNameId();
	}

	@Override
	public long getClassPK() {
		return _article.getResourcePrimKey();
	}

	@Override
	public long getClassTypeId() {
		return _assetEntry.getClassTypeId();
	}

	@Override
	public String getDescription(Locale locale) {
		return _assetEntry.getDescription(locale);
	}

	@Override
	public JournalArticle getDisplayObject() {
		return _article;
	}

	@Override
	public String getExternalReferenceCode() {
		return _article.getExternalReferenceCode();
	}

	@Override
	public long getGroupId() {
		return _article.getGroupId();
	}

	@Override
	public String getKeywords(Locale locale) {
		return _assetHelper.getAssetKeywords(
			_assetEntry.getClassName(), _assetEntry.getClassPK(), locale);
	}

	@Override
	public String getTitle(Locale locale) {
		return _assetEntry.getTitle(locale);
	}

	@Override
	public String getURLTitle(Locale locale) {
		AssetRenderer<?> assetRenderer = _assetEntry.getAssetRenderer();

		return assetRenderer.getUrlTitle(locale);
	}

	private AssetEntry _getAssetEntry(JournalArticle journalArticle)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		return assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	private final JournalArticle _article;
	private final AssetEntry _assetEntry;
	private final AssetHelper _assetHelper;

}