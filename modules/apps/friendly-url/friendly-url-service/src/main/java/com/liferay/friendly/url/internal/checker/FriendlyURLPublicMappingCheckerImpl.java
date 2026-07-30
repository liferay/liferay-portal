/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.internal.checker;

import com.liferay.friendly.url.checker.FriendlyURLPublicMappingChecker;
import com.liferay.friendly.url.checker.FriendlyURLPublicMappingConflict;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.FriendlyURLKeywordsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.model.SiteFriendlyURL;
import com.liferay.site.service.SiteFriendlyURLLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = FriendlyURLPublicMappingChecker.class)
public class FriendlyURLPublicMappingCheckerImpl
	implements FriendlyURLPublicMappingChecker {

	@Override
	public List<FriendlyURLPublicMappingConflict>
		getFriendlyURLPublicMappingConflicts(long companyId) {

		Group defaultGroup = _groupLocalService.fetchGroup(
			companyId, PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

		if (defaultGroup == null) {
			return Collections.emptyList();
		}

		Map<String, Group> groupsMap = new HashMap<>();

		for (Group group :
				_groupLocalService.getGroups(
					companyId, GroupConstants.ANY_PARENT_GROUP_ID, true)) {

			groupsMap.put(group.getFriendlyURL(), group);

			for (SiteFriendlyURL siteFriendlyURL :
					_siteFriendlyURLLocalService.getSiteFriendlyURLs(
						companyId, group.getGroupId())) {

				groupsMap.put(siteFriendlyURL.getFriendlyURL(), group);
			}
		}

		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts = new ArrayList<>();

		for (Map.Entry<String, Group> entry : groupsMap.entrySet()) {
			Group group = entry.getValue();

			_addReservedKeywordFriendlyURLPublicMappingConflict(
				Group.class.getName(), group.getGroupId(), entry.getKey(),
				friendlyURLPublicMappingConflicts,
				group.getName(LocaleUtil.getSiteDefault()));
		}

		long classNameId = _layoutFriendlyURLEntryHelper.getClassNameId(false);
		long defaultGroupId = defaultGroup.getGroupId();

		for (Layout layout :
				_layoutLocalService.getLayouts(defaultGroupId, false)) {

			long plid = layout.getPlid();
			String title = layout.getName(LocaleUtil.getSiteDefault());

			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
					classNameId, plid);

			if (friendlyURLEntry == null) {
				String friendlyURL = layout.getFriendlyURL();

				_addReservedKeywordFriendlyURLPublicMappingConflict(
					Layout.class.getName(), plid, friendlyURL,
					friendlyURLPublicMappingConflicts, title);
				_addSelfFriendlyURLPublicMappingConflict(
					defaultGroupId, friendlyURL,
					friendlyURLPublicMappingConflicts, groupsMap, plid, title);
				_addCrossSiteFriendlyURLPublicMappingConflict(
					defaultGroupId, friendlyURL,
					friendlyURLPublicMappingConflicts, groupsMap, plid, title);

				continue;
			}

			for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
					_friendlyURLEntryLocalService.
						getFriendlyURLEntryLocalizations(
							friendlyURLEntry.getFriendlyURLEntryId())) {

				String friendlyURL = friendlyURLEntryLocalization.getUrlTitle();

				_addReservedKeywordFriendlyURLPublicMappingConflict(
					Layout.class.getName(), plid, friendlyURL,
					friendlyURLPublicMappingConflicts, title);
				_addSelfFriendlyURLPublicMappingConflict(
					defaultGroupId, friendlyURL,
					friendlyURLPublicMappingConflicts, groupsMap, plid, title);
				_addCrossSiteFriendlyURLPublicMappingConflict(
					defaultGroupId, friendlyURL,
					friendlyURLPublicMappingConflicts, groupsMap, plid, title);
			}
		}

		friendlyURLPublicMappingConflicts.sort(_comparator);

		return friendlyURLPublicMappingConflicts;
	}

	private void _addCrossSiteFriendlyURLPublicMappingConflict(
		long defaultGroupId, String friendlyURL,
		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts,
		Map<String, Group> groupsMap, long plid, String title) {

		if (Validator.isNull(friendlyURL)) {
			return;
		}

		Group group = groupsMap.get(friendlyURL);

		if ((group == null) || (group.getGroupId() == defaultGroupId)) {
			return;
		}

		friendlyURLPublicMappingConflicts.add(
			new FriendlyURLPublicMappingConflict(
				Layout.class.getName(), plid, group.getGroupId(),
				group.getName(LocaleUtil.getSiteDefault()), friendlyURL, title,
				FriendlyURLPublicMappingConflict.TYPE_CROSS_SITE));
	}

	private void _addReservedKeywordFriendlyURLPublicMappingConflict(
		String className, long classPK, String friendlyURL,
		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts,
		String title) {

		if (Validator.isNull(friendlyURL) ||
			!FriendlyURLKeywordsUtil.hasSiteFriendlyURLKeyword(friendlyURL)) {

			return;
		}

		friendlyURLPublicMappingConflicts.add(
			new FriendlyURLPublicMappingConflict(
				className, classPK, null, null, friendlyURL, title,
				FriendlyURLPublicMappingConflict.TYPE_RESERVED_KEYWORD));
	}

	private void _addSelfFriendlyURLPublicMappingConflict(
		long defaultGroupId, String friendlyURL,
		List<FriendlyURLPublicMappingConflict>
			friendlyURLPublicMappingConflicts,
		Map<String, Group> groupsMap, long plid, String title) {

		if (Validator.isNull(friendlyURL)) {
			return;
		}

		Group group = groupsMap.get(friendlyURL);

		if ((group == null) || (group.getGroupId() != defaultGroupId)) {
			return;
		}

		friendlyURLPublicMappingConflicts.add(
			new FriendlyURLPublicMappingConflict(
				Layout.class.getName(), plid, null, null, friendlyURL, title,
				FriendlyURLPublicMappingConflict.TYPE_SELF));
	}

	private static final Comparator<FriendlyURLPublicMappingConflict>
		_comparator = Comparator.comparing(
			FriendlyURLPublicMappingConflict::getType
		).thenComparing(
			FriendlyURLPublicMappingConflict::getFriendlyURL
		);

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private SiteFriendlyURLLocalService _siteFriendlyURLLocalService;

}