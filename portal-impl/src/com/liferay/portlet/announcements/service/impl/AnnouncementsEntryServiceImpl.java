/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.impl;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;
import com.liferay.portlet.announcements.service.base.AnnouncementsEntryServiceBaseImpl;
import com.liferay.portlet.announcements.service.permission.AnnouncementsEntryPermission;

import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 * @author Roberto Díaz
 */
public class AnnouncementsEntryServiceImpl
	extends AnnouncementsEntryServiceBaseImpl {

	@Override
	public AnnouncementsEntry addEntry(
			long classNameId, long classPK, String title, String content,
			String url, String type, Date displayDate, Date expirationDate,
			int priority, boolean alert)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (classNameId == 0) {
			if (!PortalPermissionUtil.contains(
					permissionChecker, ActionKeys.ADD_GENERAL_ANNOUNCEMENTS)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, PortletKeys.PORTAL, PortletKeys.PORTAL,
					ActionKeys.ADD_GENERAL_ANNOUNCEMENTS);
			}
		}
		else {
			String className = PortalUtil.getClassName(classNameId);

			if (className.equals(Group.class.getName()) &&
				!GroupPermissionUtil.contains(
					permissionChecker, classPK,
					ActionKeys.MANAGE_ANNOUNCEMENTS)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, className, classPK,
					ActionKeys.MANAGE_ANNOUNCEMENTS);
			}

			if (className.equals(Organization.class.getName()) &&
				!OrganizationPermissionUtil.contains(
					permissionChecker, classPK,
					ActionKeys.MANAGE_ANNOUNCEMENTS)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, className, classPK,
					ActionKeys.MANAGE_ANNOUNCEMENTS);
			}

			if (className.equals(Role.class.getName())) {
				Role role = _rolePersistence.findByPrimaryKey(classPK);

				if (role.isTeam()) {
					Team team = _teamPersistence.findByPrimaryKey(
						role.getClassPK());

					if (!GroupPermissionUtil.contains(
							permissionChecker, team.getGroupId(),
							ActionKeys.MANAGE_ANNOUNCEMENTS) ||
						!RolePermissionUtil.contains(
							permissionChecker, team.getGroupId(), classPK,
							ActionKeys.MANAGE_ANNOUNCEMENTS)) {

						throw new PrincipalException.MustHavePermission(
							permissionChecker, Team.class.getName(), classPK,
							ActionKeys.MANAGE_ANNOUNCEMENTS);
					}
				}
				else if (!RolePermissionUtil.contains(
							permissionChecker, classPK,
							ActionKeys.MANAGE_ANNOUNCEMENTS)) {

					throw new PrincipalException.MustHavePermission(
						permissionChecker, className, classPK,
						ActionKeys.MANAGE_ANNOUNCEMENTS);
				}
			}

			if (className.equals(UserGroup.class.getName()) &&
				!UserGroupPermissionUtil.contains(
					permissionChecker, classPK,
					ActionKeys.MANAGE_ANNOUNCEMENTS)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, className, classPK,
					ActionKeys.MANAGE_ANNOUNCEMENTS);
			}
		}

		return announcementsEntryLocalService.addEntry(
			getUserId(), classNameId, classPK, title, content, url, type,
			displayDate, expirationDate, priority, alert);
	}

	@Override
	public void deleteEntry(long entryId) throws PortalException {
		AnnouncementsEntryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		announcementsEntryLocalService.deleteEntry(entryId);
	}

	@Override
	public AnnouncementsEntry getEntry(long entryId) throws PortalException {
		AnnouncementsEntry entry =
			announcementsEntryPersistence.findByPrimaryKey(entryId);

		AnnouncementsEntryPermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		return entry;
	}

	@Override
	public AnnouncementsEntry updateEntry(
			long entryId, String title, String content, String url, String type,
			Date displayDate, Date expirationDate, int priority)
		throws PortalException {

		AnnouncementsEntryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.UPDATE);

		return announcementsEntryLocalService.updateEntry(
			entryId, title, content, url, type, displayDate, expirationDate,
			priority);
	}

	@BeanReference(type = RolePersistence.class)
	private RolePersistence _rolePersistence;

	@BeanReference(type = TeamPersistence.class)
	private TeamPersistence _teamPersistence;

}