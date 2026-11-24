/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor.asset.publisher;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.similar.results.web.internal.builder.AssetTypeUtil;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.similar.results.web.internal.util.URLParameterUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteHelper;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.Objects;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class AssetPublisherSimilarResultsContributor
	implements SimilarResultsContributor {

	public AssetPublisherSimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService,
		BlogsEntryLocalService blogsEntryLocalService, UIDFactory uidFactory,
		WikiPageLocalService wikiPageLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_blogsEntryLocalService = blogsEntryLocalService;
		_uidFactory = uidFactory;
		_wikiPageLocalService = wikiPageLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		String urlString = HttpComponentsUtil.decodePath(
			routeHelper.getURLString());

		String[] parameters = URLParameterUtil.getFriendlyURLParameters(
			urlString);

		SearchStringUtil.requireEquals("asset_publisher", parameters[0]);

		_putAttribute(parameters[2], "type", routeBuilder);

		String assetEntryId = URLParameterUtil.getPortletIdParameter(
			urlString,
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "assetEntryId");

		_putAttribute(Long.valueOf(assetEntryId), "entryId", routeBuilder);
	}

	@Override
	public void resolveCriteria(
		CriteriaBuilder criteriaBuilder, CriteriaHelper criteriaHelper) {

		Long entryId = (Long)criteriaHelper.getRouteParameter("entryId");

		AssetEntry assetEntry = _assetEntryLocalService.fetchAssetEntry(
			entryId);

		if (assetEntry == null) {
			return;
		}

		criteriaBuilder.type(
			assetEntry.getClassName()
		).uid(
			_getUID(assetEntry)
		);
	}

	@Override
	public void writeDestination(
		DestinationBuilder destinationBuilder,
		DestinationHelper destinationHelper) {

		Long entryId = (Long)destinationHelper.getRouteParameter("entryId");

		String type = (String)destinationHelper.getRouteParameter("type");

		AssetEntry assetEntry = destinationHelper.getAssetEntry();

		destinationBuilder.replace(
			type,
			AssetTypeUtil.getAssetTypeByClassName(
				destinationHelper.getClassName())
		).replace(
			String.valueOf(entryId), String.valueOf(assetEntry.getEntryId())
		);
	}

	private ClassedModel _getClassedModel(AssetEntry assetEntry) {
		if (Objects.equals(
				BlogsEntry.class.getName(), assetEntry.getClassName())) {

			return _blogsEntryLocalService.fetchBlogsEntryByUuidAndGroupId(
				assetEntry.getClassUuid(), assetEntry.getGroupId());
		}
		else if (Objects.equals(
					JournalArticle.class.getName(),
					assetEntry.getClassName())) {

			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			return (JournalArticle)assetRenderer.getAssetObject();
		}
		else if (Objects.equals(
					WikiPage.class.getName(), assetEntry.getClassName())) {

			return _wikiPageLocalService.fetchWikiPageByUuidAndGroupId(
				assetEntry.getClassUuid(), assetEntry.getGroupId());
		}

		return null;
	}

	private String _getUID(AssetEntry assetEntry) {
		ClassedModel classedModel = _getClassedModel(assetEntry);

		if (classedModel != null) {
			return _uidFactory.getUID(classedModel);
		}

		return Field.getUID(
			assetEntry.getClassName(), String.valueOf(assetEntry.getClassPK()));
	}

	private void _putAttribute(
		Object value, String name, RouteBuilder routeBuilder) {

		routeBuilder.addAttribute(name, value);
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final BlogsEntryLocalService _blogsEntryLocalService;
	private final UIDFactory _uidFactory;
	private final WikiPageLocalService _wikiPageLocalService;

}