/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Mario Leandro
 */
public class DesignLibraryMembersDisplayContext
	extends BaseDesignLibraryDisplayContext {

	public DesignLibraryMembersDisplayContext(
		DepotEntry depotEntry, HttpServletRequest httpServletRequest) {

		super(depotEntry, httpServletRequest);
	}

	public Map<String, Object> getMembersEmptyState() {
		return buildEmptyState(
			"add-members-to-this-design-library", "no-members-yet");
	}

	public Map<String, Object> getMembersFDSAdditionalProps()
		throws PortalException {

		Group group = getGroup();

		return HashMapBuilder.<String, Object>put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"hasAssignMembersPermission", hasAssignMembersPermission(group)
		).put(
			"ownerId", String.valueOf(group.getCreatorUserId())
		).build();
	}

	public Map<String, Object> getMembersSectionHeaderProps()
		throws PortalException {

		return HashMapBuilder.<String, Object>putAll(
			getMembersFDSAdditionalProps()
		).put(
			"count", _getMembersCount()
		).build();
	}

	public List<TabsItem> getMembersTabsItems() {
		return TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(httpServletRequest, "users"));
			}
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(httpServletRequest, "user-groups"))
		).build();
	}

	public String getMembersUserGroupsAPIURL() throws PortalException {
		return _getMembersAPIURL("user-groups", "numberOfUserAccounts");
	}

	public String getMembersUsersAPIURL() throws PortalException {
		return _getMembersAPIURL("user-accounts", "roles");
	}

	private String _getMembersAPIURL(String type, String nestedFields)
		throws PortalException {

		return getAssetLibraryURL(
			getGroup(),
			StringBundler.concat(
				"/", type, "?page=1&pageSize=10&nestedFields=", nestedFields));
	}

	private int _getMembersCount() throws PortalException {
		UserGroupLocalService userGroupLocalService =
			_userGroupLocalServiceSnapshot.get();
		UserLocalService userLocalService = _userLocalServiceSnapshot.get();

		if ((userGroupLocalService == null) || (userLocalService == null)) {
			return 0;
		}

		Group group = getGroup();

		long groupId = group.getGroupId();

		return userLocalService.getGroupUsersCount(groupId) +
			userGroupLocalService.getGroupUserGroupsCount(groupId);
	}

	private static final Snapshot<UserGroupLocalService>
		_userGroupLocalServiceSnapshot = new Snapshot<>(
			DesignLibraryMembersDisplayContext.class,
			UserGroupLocalService.class);
	private static final Snapshot<UserLocalService> _userLocalServiceSnapshot =
		new Snapshot<>(
			DesignLibraryMembersDisplayContext.class, UserLocalService.class);

}