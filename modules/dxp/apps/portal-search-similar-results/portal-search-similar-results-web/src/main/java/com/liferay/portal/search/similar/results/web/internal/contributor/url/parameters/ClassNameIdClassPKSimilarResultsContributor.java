/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.contributor.url.parameters;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.util.URLParameterUtil;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteBuilder;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.RouteHelper;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class ClassNameIdClassPKSimilarResultsContributor
	implements SimilarResultsContributor {

	public static final String CLASS_NAME_ID = "classNameId";

	public static final String CLASS_PK = "classPK";

	public ClassNameIdClassPKSimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		String urlString = HttpComponentsUtil.decodePath(
			routeHelper.getURLString());

		routeBuilder.addAttribute(
			CLASS_NAME_ID,
			Long.valueOf(
				URLParameterUtil.getPortletIdParameter(
					urlString, CLASS_NAME_ID))
		).addAttribute(
			CLASS_PK,
			Long.valueOf(
				URLParameterUtil.getPortletIdParameter(urlString, CLASS_PK))
		);
	}

	@Override
	public void resolveCriteria(
		CriteriaBuilder criteriaBuilder, CriteriaHelper criteriaHelper) {

		Long classNameId = (Long)criteriaHelper.getRouteParameter(
			CLASS_NAME_ID);
		Long classPK = (Long)criteriaHelper.getRouteParameter(CLASS_PK);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			classNameId, classPK);

		if (assetEntry == null) {
			return;
		}

		criteriaBuilder.uid(
			Field.getUID(assetEntry.getClassName(), String.valueOf(classPK)));
	}

	@Override
	public void writeDestination(
		DestinationBuilder destinationBuilder,
		DestinationHelper destinationHelper) {

		AssetEntry assetEntry = destinationHelper.getAssetEntry();

		destinationBuilder.replaceParameter(
			CLASS_NAME_ID, String.valueOf(assetEntry.getClassNameId())
		).replaceParameter(
			CLASS_PK, String.valueOf(assetEntry.getClassPK())
		);
	}

	private final AssetEntryLocalService _assetEntryLocalService;

}