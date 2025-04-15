/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.result;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.SummaryFactory;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Adolfo Pérez
 * @author André de Oliveira
 */
@Component(service = SummaryFactory.class)
public class SummaryFactoryImpl implements SummaryFactory {

	public static final int SUMMARY_MAX_CONTENT_LENGTH = 200;

	@Override
	public Summary getSummary(
			Document document, String className, long classPK, Locale locale,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		Indexer<?> indexer = _indexerRegistry.getIndexer(className);

		if (indexer != null) {
			String snippet = document.get(Field.SNIPPET);

			return indexer.getSummary(
				document, snippet, portletRequest, portletResponse);
		}

		return getSummary(className, classPK, locale);
	}

	@Override
	public Summary getSummary(String className, long classPK, Locale locale)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory == null) {
			return null;
		}

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			classPK);

		if (assetRenderer == null) {
			return null;
		}

		Summary summary = new Summary(
			assetRenderer.getTitle(locale),
			assetRenderer.getSearchSummary(locale));

		summary.setMaxContentLength(SUMMARY_MAX_CONTENT_LENGTH);

		return summary;
	}

	@Reference
	private IndexerRegistry _indexerRegistry;

}