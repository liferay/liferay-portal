/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.portlet;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lino Alves
 */
public class CategoryFacetPortletPreferencesImpl
	extends BasePortletPreferences implements CategoryFacetPortletPreferences {

	public CategoryFacetPortletPreferencesImpl(
		AssetVocabularyLocalService assetVocabularyLocalService,
		GroupLocalService groupLocalService,
		PortletPreferences portletPreferences) {

		super(portletPreferences);

		_assetVocabularyLocalService = assetVocabularyLocalService;
		_groupLocalService = groupLocalService;
	}

	@Override
	public String getDisplayStyle() {
		return getString(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_DISPLAY_STYLE,
			"cloud");
	}

	@Override
	public int getFrequencyThreshold() {
		return getInteger(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD,
			1);
	}

	@Override
	public String[] getGroupVocabularyExternalReferenceCodes() {
		String groupVocabularyExternalReferenceCodes = getString(
			CategoryFacetPortletPreferences.
				PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
			null);

		return StringUtil.split(groupVocabularyExternalReferenceCodes);
	}

	@Override
	public int getMaxTerms() {
		return getInteger(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS, 10);
	}

	@Override
	public String getOrder() {
		return getString(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_ORDER, "count:desc");
	}

	@Override
	public String getParameterName() {
		return getString(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
			"category");
	}

	@Override
	public String[] getVocabularyIds() {
		List<String> vocabularyIds = new LinkedList<>();

		for (String externalReferenceCode :
				getGroupVocabularyExternalReferenceCodes()) {

			String[] externalReferenceCodeParts = StringUtil.split(
				externalReferenceCode, "&&");

			try {
				Group group =
					_groupLocalService.getGroupByExternalReferenceCode(
						externalReferenceCodeParts[0],
						CompanyThreadLocal.getCompanyId());

				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.
						getAssetVocabularyByExternalReferenceCode(
							externalReferenceCodeParts[1], group.getGroupId());

				vocabularyIds.add(
					String.valueOf(assetVocabulary.getVocabularyId()));
			}
			catch (PortalException portalException) {
				if (_log.isInfoEnabled()) {
					_log.info(portalException);
				}
			}
		}

		return ArrayUtil.toStringArray(vocabularyIds);
	}

	@Override
	public boolean isFrequenciesVisible() {
		return getBoolean(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
			true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CategoryFacetPortletPreferencesImpl.class);

	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final GroupLocalService _groupLocalService;

}