/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cmp.site.initializer.internal.util.CMPDepotEntryGroupUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Pedro Leite
 */
public class ViewProjectMembersSummarySectionDisplayContext {

	public ViewProjectMembersSummarySectionDisplayContext(
			GroupLocalService groupLocalService,
			ModelResourcePermission<Group> groupModelResourcePermission,
			HttpServletRequest httpServletRequest, Language language,
			ObjectEntry objectEntry,
			UserGroupLocalService userGroupLocalService,
			UserLocalService userLocalService)
		throws PortalException {

		_groupModelResourcePermission = groupModelResourcePermission;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_objectEntry = objectEntry;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;

		_group = groupLocalService.getGroup(objectEntry.getGroupId());

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL(String type) {
		StringBundler sb = new StringBundler(9);

		sb.append("/o/headless-asset-library/v1.0/asset-libraries/");
		sb.append(_group.getExternalReferenceCode());
		sb.append("/");
		sb.append(type);
		sb.append("?page=");
		sb.append(_PAGE);
		sb.append("&pageSize=");
		sb.append(_PAGE_SIZE);

		if (type.equals("user-accounts")) {
			sb.append("&nestedFields=roles");
		}
		else {
			sb.append("&nestedFields=numberOfUserAccounts");
		}

		return sb.toString();
	}

	public CreationMenu getCreationMenu() throws Exception {
		if (!_hasAssignMembersPermission()) {
			return new CreationMenu();
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addMembers");
				dropdownItem.putData(
					"assetLibraryCreatorUserId",
					_getAssetLibraryCreatorUserId());
				dropdownItem.putData(
					"cmpProjectObjectEntryId",
					String.valueOf(_objectEntry.getObjectEntryId()));
				dropdownItem.putData(
					"externalReferenceCode", _group.getExternalReferenceCode());
				dropdownItem.putData(
					"filter", CMPDepotEntryGroupUtil.getFilterString());
				dropdownItem.putData(
					"hasAssignMembersPermission",
					_hasAssignMembersPermission());
				dropdownItem.putData("title", _getHeaderTitle());
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "add-members"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			_language.get(_httpServletRequest, "add-members-to-this-project")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", _language.get(_httpServletRequest, "no-members-yet")
		).build();
	}

	public Map<String, Object> getHeaderProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"apiURL", getAPIURL("user-accounts")
		).put(
			"label", _language.get(_httpServletRequest, "view-all-members")
		).put(
			"permissions",
			HashMapBuilder.<String, Object>put(
				"hasAssignMembersPermission", _hasAssignMembersPermission()
			).build()
		).put(
			"spaceModalProps",
			HashMapBuilder.<String, Object>put(
				"action", "open-members-modal"
			).put(
				"assetLibraryCreatorUserId", _getAssetLibraryCreatorUserId()
			).put(
				"cmpProjectObjectEntryId", _objectEntry.getObjectEntryId()
			).put(
				"externalReferenceCode", _group.getExternalReferenceCode()
			).put(
				"filter", CMPDepotEntryGroupUtil.getFilterString()
			).build()
		).put(
			"title", _getHeaderTitle()
		).put(
			"url", StringPool.BLANK
		).build();
	}

	public List<TabsItem> getTabsItems() {
		return TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(_language.get(_httpServletRequest, "users"));
			}
		).add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					_language.get(_httpServletRequest, "user-groups"));
			}
		).build();
	}

	private String _getAssetLibraryCreatorUserId() {
		return String.valueOf(_group.getCreatorUserId());
	}

	private String _getHeaderTitle() {
		long groupId = _group.getGroupId();

		return StringBundler.concat(
			_language.get(_httpServletRequest, "members"), StringPool.SPACE,
			StringPool.OPEN_PARENTHESIS,
			_userGroupLocalService.getGroupUserGroupsCount(groupId) +
				_userLocalService.getGroupUsersCount(groupId),
			StringPool.CLOSE_PARENTHESIS);
	}

	private boolean _hasAssignMembersPermission() throws Exception {
		return _groupModelResourcePermission.contains(
			_themeDisplay.getPermissionChecker(), _group.getGroupId(),
			ActionKeys.ASSIGN_MEMBERS);
	}

	private static final int _PAGE = 1;

	private static final int _PAGE_SIZE = 8;

	private final Group _group;
	private final ModelResourcePermission<Group> _groupModelResourcePermission;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final ObjectEntry _objectEntry;
	private final ThemeDisplay _themeDisplay;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}