/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor.wiki;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.similar.results.web.internal.util.URLParameterUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteHelper;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class WikiSimilarResultsContributor
	extends BaseWikiSimilarResultsContributor {

	public WikiSimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService, UIDFactory uidFactory,
		WikiNodeLocalService wikiNodeLocalService,
		WikiPageLocalService wikiPageLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_uidFactory = uidFactory;
		_wikiNodeLocalService = wikiNodeLocalService;
		_wikiPageLocalService = wikiPageLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		String[] parameters = URLParameterUtil.getFriendlyURLParameters(
			routeHelper.getURLString());

		SearchStringUtil.requireEquals(
			"wiki", URLCodec.decodeURL(parameters[0]));

		routeBuilder.addAttribute(
			"nodeName", URLCodec.decodeURL(parameters[1])
		).addAttribute(
			"title", URLCodec.decodeURL(parameters[2])
		);
	}

	@Override
	protected AssetEntryLocalService getAssetEntryLocalService() {
		return _assetEntryLocalService;
	}

	@Override
	protected UIDFactory getUidFactory() {
		return _uidFactory;
	}

	@Override
	protected WikiNodeLocalService getWikiNodeLocalService() {
		return _wikiNodeLocalService;
	}

	@Override
	protected WikiPageLocalService getWikiPageLocalService() {
		return _wikiPageLocalService;
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final UIDFactory _uidFactory;
	private final WikiNodeLocalService _wikiNodeLocalService;
	private final WikiPageLocalService _wikiPageLocalService;

}