/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.portlet.shared.search;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.facet.category.CategoryFacetSearchContributor;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferences;
import com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + CategoryFacetPortletKeys.CATEGORY_FACET,
	service = PortletSharedSearchContributor.class
)
public class CategoryFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CategoryFacetPortletPreferences categoryFacetPortletPreferences =
			new CategoryFacetPortletPreferencesImpl(
				_assetVocabularyLocalService, _groupLocalService,
				portletSharedSearchSettings.getPortletPreferences());

		categoryFacetSearchContributor.contribute(
			portletSharedSearchSettings.getSearchRequestBuilder(),
			categoryFacetBuilder -> categoryFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).frequencyThreshold(
				categoryFacetPortletPreferences.getFrequencyThreshold()
			).maxTerms(
				categoryFacetPortletPreferences.getMaxTerms()
			).selectedCategoryIds(
				_toLongArray(
					portletSharedSearchSettings.getParameterValues(
						categoryFacetPortletPreferences.getParameterName()))
			).vocabularyIds(
				categoryFacetPortletPreferences.getVocabularyIds()
			));
	}

	@Reference
	protected CategoryFacetSearchContributor categoryFacetSearchContributor;

	private long[] _toLongArray(String[] parameterValues) {
		if (ArrayUtil.isNotEmpty(parameterValues)) {
			return ListUtil.toLongArray(
				Arrays.asList(parameterValues), GetterUtil::getLong);
		}

		return new long[0];
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}