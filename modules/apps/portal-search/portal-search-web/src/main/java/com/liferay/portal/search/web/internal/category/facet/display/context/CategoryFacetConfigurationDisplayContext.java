/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.category.facet.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Akhash Ramprakash
 * @author Shrilakshmi Reddy
 */
public class CategoryFacetConfigurationDisplayContext {

	public CategoryFacetConfigurationDisplayContext(
		AssetVocabularyService assetVocabularyService,
		GroupLocalService groupLocalService, GroupService groupService,
		Locale locale,
		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider) {

		_assetVocabularyService = assetVocabularyService;
		_groupLocalService = groupLocalService;
		_groupService = groupService;
		_locale = locale;
		_siteConnectedGroupGroupProvider = siteConnectedGroupGroupProvider;
	}

	public JSONArray getGroupsJSONArray() {
		try {
			List<Group> groups = _getSiteAndConnectedDepotGroups();

			Map<Long, List<AssetVocabulary>> assetVocabulariesMap =
				_getAssetVocabulariesMap(groups);

			return JSONUtil.toJSONArray(
				groups,
				group -> _toJSONObject(
					assetVocabulariesMap.getOrDefault(
						group.getGroupId(), Collections.emptyList()),
					group));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return JSONFactoryUtil.createJSONArray();
		}
	}

	private Map<Long, List<AssetVocabulary>> _getAssetVocabulariesMap(
		List<Group> groups) {

		Map<Long, List<AssetVocabulary>> assetVocabulariesMap = new HashMap<>();

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				ListUtil.toLongArray(groups, Group::getGroupId));

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			List<AssetVocabulary> groupAssetVocabularies =
				assetVocabulariesMap.computeIfAbsent(
					assetVocabulary.getGroupId(), groupId -> new ArrayList<>());

			groupAssetVocabularies.add(assetVocabulary);
		}

		return assetVocabulariesMap;
	}

	private List<Group> _getSiteAndConnectedDepotGroups()
		throws PortalException {

		List<Group> siteGroups = ListUtil.filter(
			_groupService.getUserSitesGroups(), Group::isSite);

		List<Group> groups = new ArrayList<>(siteGroups);

		List<Group> depotGroups = new ArrayList<>();

		long[] siteGroupIds = ListUtil.toLongArray(
			siteGroups, Group::getGroupId);

		long[] connectedGroupIds =
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(siteGroupIds);

		Set<Long> groupIds = SetUtil.fromArray(siteGroupIds);

		for (long connectedGroupId : connectedGroupIds) {
			Group group = _groupLocalService.fetchGroup(connectedGroupId);

			if ((group != null) && group.isDepot() &&
				!groupIds.contains(group.getGroupId())) {

				depotGroups.add(group);
				groupIds.add(group.getGroupId());
			}
		}

		depotGroups.sort(
			Comparator.comparing(group -> group.getName(_locale, true)));

		groups.addAll(depotGroups);

		return groups;
	}

	private JSONObject _toJSONObject(
			List<AssetVocabulary> assetVocabularies, Group group)
		throws Exception {

		return JSONUtil.put(
			"assetLibraryKey",
			() -> {
				if (!group.isDepot()) {
					return null;
				}

				return group.getGroupKey();
			}
		).put(
			"children",
			JSONUtil.toJSONArray(
				assetVocabularies,
				assetVocabulary -> JSONUtil.put(
					"externalReferenceCode",
					StringBundler.concat(
						group.getExternalReferenceCode(), "&&",
						assetVocabulary.getExternalReferenceCode())
				).put(
					"id", assetVocabulary.getVocabularyId()
				).put(
					"name", assetVocabulary.getTitle(_locale)
				))
		).put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"name", group.getDescriptiveName(_locale)
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CategoryFacetConfigurationDisplayContext.class);

	private final AssetVocabularyService _assetVocabularyService;
	private final GroupLocalService _groupLocalService;
	private final GroupService _groupService;
	private final Locale _locale;
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider;

}