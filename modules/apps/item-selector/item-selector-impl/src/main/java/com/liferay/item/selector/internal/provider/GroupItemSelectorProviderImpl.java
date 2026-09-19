/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.internal.provider;

import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.search.GroupSearch;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = GroupItemSelectorProvider.class)
public class GroupItemSelectorProviderImpl
	implements GroupItemSelectorProvider {

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public String getEmptyResultsMessage() {
		return GroupSearch.EMPTY_RESULTS_MESSAGE;
	}

	@Override
	public String getEmptyResultsMessage(Locale locale) {
		return _language.get(locale, GroupSearch.EMPTY_RESULTS_MESSAGE);
	}

	@Override
	public String getGroupType() {
		return "site";
	}

	@Override
	public List<Group> getGroups(
		long companyId, long groupId, String keywords, int start, int end) {

		LinkedHashMap<String, Object> groupParams =
			LinkedHashMapBuilder.<String, Object>put(
				"actionId", ActionKeys.VIEW
			).put(
				"active", Boolean.TRUE
			).put(
				"site", Boolean.TRUE
			).build();

		try {
			long[] classNameIds = _getClassNameIds();

			List<Group> groups = _groupLocalService.search(
				companyId, classNameIds, keywords, groupParams, start, end,
				null);

			return ListUtil.filter(
				groups,
				(startIndex, endIndex) -> _groupLocalService.search(
					companyId, classNameIds, keywords, groupParams, startIndex,
					endIndex, null),
				() -> getGroupsCount(companyId, groupId, keywords),
				group -> _hasViewPermission(group), start, end);
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyList();
		}
	}

	@Override
	public int getGroupsCount(long companyId, long groupId, String keywords) {
		return _groupService.searchCount(
			companyId, _getClassNameIds(), keywords,
			LinkedHashMapBuilder.<String, Object>put(
				"actionId", ActionKeys.VIEW
			).put(
				"active", Boolean.TRUE
			).put(
				"site", Boolean.TRUE
			).build());
	}

	@Override
	public String getIcon() {
		return "sites";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "site");
	}

	private long[] _getClassNameIds() {
		return new long[] {
			_classNameLocalService.getClassNameId(Company.class),
			_classNameLocalService.getClassNameId(Group.class),
			_classNameLocalService.getClassNameId(Organization.class)
		};
	}

	private boolean _hasViewPermission(Group group) {
		try {
			PermissionChecker permissionChecker =
				GuestOrUserUtil.getPermissionChecker();

			if (group.isCompany() ||
				permissionChecker.isGroupAdmin(group.getGroupId()) ||
				GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.VIEW)) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupItemSelectorProviderImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

}