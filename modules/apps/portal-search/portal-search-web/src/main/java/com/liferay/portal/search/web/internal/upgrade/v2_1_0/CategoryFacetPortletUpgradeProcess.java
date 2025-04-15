/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.v2_1_0;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferences;

import jakarta.portlet.PortletPreferences;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joshua Cords
 */
public class CategoryFacetPortletUpgradeProcess
	extends BasePortletPreferencesUpgradeProcess {

	public CategoryFacetPortletUpgradeProcess(
		AssetVocabularyLocalService assetVocabularyLocalService,
		GroupLocalService groupLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
		_groupLocalService = groupLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {"%CategoryFacetPortlet%"};
	}

	@Override
	protected String getUpdatePortletPreferencesWhereClause() {
		return "portletId like '%CategoryFacetPortlet%'";
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String portletPreference = portletPreferences.getValue(
			CategoryFacetPortletPreferences.
				PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
			null);

		if (portletPreference != null) {
			return PortletPreferencesFactoryUtil.toXML(portletPreferences);
		}

		portletPreferences.setValues(
			CategoryFacetPortletPreferences.
				PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES,
			_getGroupVocabularyExternalReferenceCodes(
				portletId, portletPreferences.getValue("vocabularyIds", null)));

		portletPreferences.reset("vocabularyIds");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private String _getGroupVocabularyExternalReferenceCodes(
			String portletId, String vocabularyIdsPreference)
		throws Exception {

		if (vocabularyIdsPreference == null) {
			return new String();
		}

		List<String> groupVocabularyExternalReferenceCodes = new LinkedList<>();

		List<String> vocabularyIds = StringUtil.split(
			vocabularyIdsPreference, ',');

		for (String vocabularyId : vocabularyIds) {
			try {
				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.getAssetVocabulary(
						Long.parseLong(vocabularyId));

				Group group = _groupLocalService.getGroup(
					assetVocabulary.getGroupId());

				groupVocabularyExternalReferenceCodes.add(
					group.getExternalReferenceCode() + "&&" +
						assetVocabulary.getExternalReferenceCode());
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to upgrade portlet ", portletId,
						" referencing vocabulary ID ", vocabularyId));

				throw exception;
			}
		}

		return StringUtil.merge(groupVocabularyExternalReferenceCodes, ",");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CategoryFacetPortletUpgradeProcess.class);

	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final GroupLocalService _groupLocalService;

}