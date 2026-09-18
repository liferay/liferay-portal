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
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Akhash Ramprakash
 * @author Shrilakshmi Reddy
 */
public class CategoryFacetConfigurationDisplayContext {

	public CategoryFacetConfigurationDisplayContext(
		AssetVocabularyService assetVocabularyService,
		GroupLocalService groupLocalService, GroupService groupService,
		JSONFactory jsonFactory, Locale locale,
		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider) {

		_assetVocabularyService = assetVocabularyService;
		_groupLocalService = groupLocalService;
		_groupService = groupService;
		_jsonFactory = jsonFactory;
		_locale = locale;
		_siteConnectedGroupGroupProvider = siteConnectedGroupGroupProvider;
	}

	public JSONArray getGroupsJSONArray() {
		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray();

			List<Group> groups = _getSiteAndConnectedDepotGroups();

			Map<Long, List<AssetVocabulary>> assetVocabulariesMap =
				_getAssetVocabulariesMap(groups);

			for (Group group : groups) {
				jsonArray.put(
					_toJSONObject(
						group,
						assetVocabulariesMap.getOrDefault(
							group.getGroupId(), Collections.emptyList())));
			}

			return jsonArray;
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return _jsonFactory.createJSONArray();
		}
	}

	private Map<Long, List<AssetVocabulary>> _getAssetVocabulariesMap(
		List<Group> groups) {

		Map<Long, List<AssetVocabulary>> assetVocabulariesMap = new HashMap<>();

		long[] groupIds = ListUtil.toLongArray(groups, Group::getGroupId);

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(groupIds);

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			List<AssetVocabulary> groupAssetVocabularies =
				assetVocabulariesMap.computeIfAbsent(
					assetVocabulary.getGroupId(), key -> new ArrayList<>());

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

		Map<Long, Group> groupsMap = new LinkedHashMap<>();

		for (Group siteGroup : siteGroups) {
			groupsMap.put(siteGroup.getGroupId(), siteGroup);
		}

		long[] siteGroupIds = ListUtil.toLongArray(
			siteGroups, Group::getGroupId);

		long[] groupIds =
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(siteGroupIds);

		for (long groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group != null) && group.isDepot() &&
				!groupsMap.containsKey(group.getGroupId())) {

				depotGroups.add(group);
				groupsMap.put(group.getGroupId(), group);
			}
		}

		depotGroups.sort(
			Comparator.comparing(group -> group.getName(_locale, true)));

		groups.addAll(depotGroups);

		return groups;
	}

	private JSONObject _toJSONObject(
			Group group, List<AssetVocabulary> assetVocabularies)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		JSONArray childrenJSONArray = _jsonFactory.createJSONArray();

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			childrenJSONArray.put(
				_jsonFactory.createJSONObject(
				).put(
					"externalReferenceCode",
					StringBundler.concat(
						group.getExternalReferenceCode(), "&&",
						assetVocabulary.getExternalReferenceCode())
				).put(
					"id", assetVocabulary.getVocabularyId()
				).put(
					"name", assetVocabulary.getTitle(_locale)
				));
		}

		if (group.isDepot()) {
			jsonObject.put("assetLibraryKey", group.getGroupKey());
		}

		return jsonObject.put(
			"children", childrenJSONArray
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
	private final JSONFactory _jsonFactory;
	private final Locale _locale;
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider;

}