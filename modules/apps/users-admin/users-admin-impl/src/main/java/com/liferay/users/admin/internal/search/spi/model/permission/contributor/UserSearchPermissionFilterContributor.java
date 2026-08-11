/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.permission.contributor;

import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactTable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.TeamPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jesse Yeh
 * @author Drew Brokke
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = SearchPermissionFilterContributor.class
)
public class UserSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(User.class.getName())) {
			return;
		}

		_addManagedOrganizationUsersFilter(
			booleanFilter, companyId, permissionChecker);
		_addManagedTeamGroupUsersFilter(booleanFilter, permissionChecker);
		_addOwnedUsersFilter(booleanFilter, permissionChecker, userId);
	}

	private void _addManagedOrganizationUsersFilter(
		BooleanFilter booleanFilter, long companyId,
		PermissionChecker permissionChecker) {

		try {
			TermsFilter termsFilter = new TermsFilter("organizationIds");

			Set<Long> organizationIds = new HashSet<>();

			UserBag userBag = permissionChecker.getUserBag();

			long[] userOrgIds = userBag.getUserOrgIds();

			for (long userOrgId : userOrgIds) {
				if (OrganizationPermissionUtil.contains(
						permissionChecker, userOrgId,
						ActionKeys.MANAGE_USERS)) {

					organizationIds.add(userOrgId);
				}

				if (OrganizationPermissionUtil.contains(
						permissionChecker, userOrgId,
						ActionKeys.MANAGE_SUBORGANIZATIONS_USERS)) {

					Organization organization =
						_organizationLocalService.getOrganization(userOrgId);

					for (Organization suborganization :
							_organizationLocalService.getOrganizations(
								companyId, organization.getTreePath() + "%/")) {

						organizationIds.add(
							suborganization.getOrganizationId());
					}
				}
			}

			if (!organizationIds.isEmpty()) {
				termsFilter.addValues(ArrayUtil.toStringArray(organizationIds));
			}

			if (!termsFilter.isEmpty()) {
				booleanFilter.add(termsFilter);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private void _addManagedTeamGroupUsersFilter(
		BooleanFilter booleanFilter, PermissionChecker permissionChecker) {

		try {
			TermsFilter termsFilter = new TermsFilter(Field.GROUP_ID);

			UserBag userBag = permissionChecker.getUserBag();

			for (Group group : userBag.getGroups()) {
				long groupId = group.getGroupId();

				for (Team team : _teamLocalService.getGroupTeams(groupId)) {
					if (TeamPermissionUtil.contains(
							permissionChecker, team,
							ActionKeys.ASSIGN_MEMBERS)) {

						termsFilter.addValue(String.valueOf(groupId));

						break;
					}
				}
			}

			if (!termsFilter.isEmpty()) {
				booleanFilter.add(termsFilter);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private void _addOwnedUsersFilter(
		BooleanFilter booleanFilter, PermissionChecker permissionChecker,
		long userId) {

		TermsFilter termsFilter = new TermsFilter(Field.ENTRY_CLASS_PK);

		User user = permissionChecker.getUser();

		if ((userId == 0) || user.isGuestUser()) {
			termsFilter.addValue(String.valueOf(userId));
		}
		else {
			List<Contact> contacts = _contactLocalService.dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					ContactTable.INSTANCE
				).from(
					ContactTable.INSTANCE
				).where(
					ContactTable.INSTANCE.classNameId.eq(
						_portal.getClassNameId(User.class)
					).and(
						ContactTable.INSTANCE.userId.eq(userId)
					)
				));

			for (Contact contact : contacts) {
				termsFilter.addValue(String.valueOf(contact.getClassPK()));
			}
		}

		if (!termsFilter.isEmpty()) {
			booleanFilter.add(termsFilter);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserSearchPermissionFilterContributor.class);

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private TeamLocalService _teamLocalService;

}