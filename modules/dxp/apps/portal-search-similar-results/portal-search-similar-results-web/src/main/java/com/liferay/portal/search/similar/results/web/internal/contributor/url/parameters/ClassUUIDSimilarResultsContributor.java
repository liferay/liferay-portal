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

import java.util.Objects;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class ClassUUIDSimilarResultsContributor
	implements SimilarResultsContributor {

	public static final String CLASS_UUID = "classUuid";

	public ClassUUIDSimilarResultsContributor(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	public void detectRoute(
		RouteBuilder routeBuilder, RouteHelper routeHelper) {

		routeBuilder.addAttribute(
			CLASS_UUID,
			Objects.requireNonNull(
				URLParameterUtil.getPortletIdParameter(
					HttpComponentsUtil.decodePath(routeHelper.getURLString()),
					CLASS_UUID)));
	}

	@Override
	public void resolveCriteria(
		CriteriaBuilder criteriaBuilder, CriteriaHelper criteriaHelper) {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			criteriaHelper.getGroupId(),
			(String)criteriaHelper.getRouteParameter(CLASS_UUID));

		if (assetEntry == null) {
			return;
		}

		criteriaBuilder.uid(
			Field.getUID(
				assetEntry.getClassName(),
				String.valueOf(assetEntry.getClassPK())));
	}

	@Override
	public void writeDestination(
		DestinationBuilder destinationBuilder,
		DestinationHelper destinationHelper) {

		AssetEntry assetEntry = destinationHelper.getAssetEntry();

		destinationBuilder.replaceParameter(
			CLASS_UUID, assetEntry.getClassUuid());
	}

	private final AssetEntryLocalService _assetEntryLocalService;

}