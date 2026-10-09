/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRole;
import com.liferay.segments.service.base.SegmentsEntryRoleLocalServiceBaseImpl;
import com.liferay.segments.service.persistence.SegmentsEntryPersistence;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.segments.model.SegmentsEntryRole",
	service = AopService.class
)
public class SegmentsEntryRoleLocalServiceImpl
	extends SegmentsEntryRoleLocalServiceBaseImpl {

	@Override
	public SegmentsEntryRole addSegmentsEntryRole(
			long segmentsEntryId, long roleId, ServiceContext serviceContext)
		throws PortalException {

		// Segments entry role

		_roleLocalService.getRole(roleId);
		_segmentsEntryPersistence.findByPrimaryKey(segmentsEntryId);

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long segmentsEntryRoleId = counterLocalService.increment();

		SegmentsEntryRole segmentsEntryRole =
			segmentsEntryRolePersistence.create(segmentsEntryRoleId);

		segmentsEntryRole.setCompanyId(user.getCompanyId());
		segmentsEntryRole.setUserId(user.getUserId());
		segmentsEntryRole.setUserName(user.getFullName());
		segmentsEntryRole.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		segmentsEntryRole.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		segmentsEntryRole.setSegmentsEntryId(segmentsEntryId);
		segmentsEntryRole.setRoleId(roleId);

		segmentsEntryRole = segmentsEntryRolePersistence.update(
			segmentsEntryRole);

		// Indexer

		_reindex(segmentsEntryId);

		return segmentsEntryRole;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SegmentsEntryRole deleteSegmentsEntryRole(
			long segmentsEntryId, long roleId)
		throws PortalException {

		// Segments entry role

		SegmentsEntryRole segmentsEntryRole =
			segmentsEntryRolePersistence.removeByS_R(segmentsEntryId, roleId);

		// Indexer

		_reindex(segmentsEntryId);

		return segmentsEntryRole;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteSegmentsEntryRoles(long segmentsEntryId)
		throws PortalException {

		// Segments entry role

		segmentsEntryRolePersistence.removeBySegmentsEntryId(segmentsEntryId);

		// Indexer

		_reindex(segmentsEntryId);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteSegmentsEntryRolesByRoleId(long roleId)
		throws PortalException {

		// Segments entry role

		List<SegmentsEntryRole> segmentsEntryRoles =
			segmentsEntryRolePersistence.findByRoleId(roleId);

		segmentsEntryRolePersistence.removeByRoleId(roleId);

		// Indexer

		for (SegmentsEntryRole segmentsEntryRole : segmentsEntryRoles) {
			_reindex(segmentsEntryRole.getSegmentsEntryId());
		}
	}

	@Override
	public List<SegmentsEntryRole> getSegmentsEntryRoles(long segmentsEntryId) {
		return segmentsEntryRolePersistence.findBySegmentsEntryId(
			segmentsEntryId);
	}

	@Override
	public List<SegmentsEntryRole> getSegmentsEntryRolesByRoleId(long roleId) {
		return segmentsEntryRolePersistence.findByRoleId(roleId);
	}

	@Override
	public int getSegmentsEntryRolesCount(long segmentsEntryId) {
		return segmentsEntryRolePersistence.countBySegmentsEntryId(
			segmentsEntryId);
	}

	@Override
	public int getSegmentsEntryRolesCountByRoleId(long roleId) {
		return segmentsEntryRolePersistence.countByRoleId(roleId);
	}

	@Override
	public boolean hasSegmentEntryRole(long segmentsEntryId, long roleId) {
		if (segmentsEntryRolePersistence.fetchByS_R(segmentsEntryId, roleId) !=
				null) {

			return true;
		}

		return false;
	}

	@Override
	public void setSegmentsEntrySiteRoles(
			long segmentsEntryId, long[] siteRoleIds,
			ServiceContext serviceContext)
		throws PortalException {

		_checkSiteRoleIds(segmentsEntryId, siteRoleIds);

		Set<Long> newSiteRoleIdsSet = SetUtil.fromArray(siteRoleIds);

		Set<Long> oldSiteRoleIdsSet = _getSiteRoleIdsSet(segmentsEntryId);

		Set<Long> removeSiteRoleIdsSet = new HashSet<>(oldSiteRoleIdsSet);

		removeSiteRoleIdsSet.removeAll(newSiteRoleIdsSet);

		_removeSiteRoles(segmentsEntryId, removeSiteRoleIdsSet);

		newSiteRoleIdsSet.removeAll(oldSiteRoleIdsSet);

		_addSiteRoles(segmentsEntryId, newSiteRoleIdsSet, serviceContext);
	}

	private void _addSiteRoles(
			long segmentsEntryId, Set<Long> siteRoleIdsSet,
			ServiceContext serviceContext)
		throws PortalException {

		for (long siteRoleId : siteRoleIdsSet) {
			segmentsEntryRoleLocalService.addSegmentsEntryRole(
				segmentsEntryId, siteRoleId, serviceContext);
		}
	}

	private void _checkSiteRoleIds(long segmentsEntryId, long[] siteRoleIds)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			_segmentsEntryPersistence.findByPrimaryKey(segmentsEntryId);

		String[] excludedRoleNames = _getExcludedRoleNames();

		for (long siteRoleId : siteRoleIds) {
			Role role = _roleLocalService.getRole(siteRoleId);

			if ((role.getCompanyId() != segmentsEntry.getCompanyId()) ||
				(role.getType() != RoleConstants.TYPE_SITE) ||
				ArrayUtil.contains(excludedRoleNames, role.getName())) {

				throw new RoleAssignmentException(
					StringBundler.concat(
						"Unable to assign site role ID ", siteRoleId,
						" to segments entry ID ", segmentsEntryId));
			}
		}
	}

	private String[] _getExcludedRoleNames() {
		RoleTypeContributor roleTypeContributor =
			_roleTypeContributorProvider.getRoleTypeContributor(
				RoleConstants.TYPE_SITE);

		if (roleTypeContributor != null) {
			return roleTypeContributor.getExcludedRoleNames();
		}

		return new String[0];
	}

	private Set<Long> _getSiteRoleIdsSet(long segmentsEntryId) {
		Set<Long> roleIds = new HashSet<>();

		List<SegmentsEntryRole> segmentsEntryRoles = getSegmentsEntryRoles(
			segmentsEntryId);

		for (SegmentsEntryRole segmentsEntryRole : segmentsEntryRoles) {
			Role role = _roleLocalService.fetchRole(
				segmentsEntryRole.getRoleId());

			if (!Objects.equals(role.getType(), RoleConstants.TYPE_SITE)) {
				continue;
			}

			roleIds.add(role.getRoleId());
		}

		return roleIds;
	}

	private void _reindex(long segmentsEntryId) throws PortalException {
		SegmentsEntry segmentsEntry =
			_segmentsEntryPersistence.fetchByPrimaryKey(segmentsEntryId);

		if (segmentsEntry == null) {
			return;
		}

		Indexer<SegmentsEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			SegmentsEntry.class);

		indexer.reindex(segmentsEntry);
	}

	private void _removeSiteRoles(
			long segmentsEntryId, Set<Long> siteRoleIdsSet)
		throws PortalException {

		for (long siteRoleId : siteRoleIdsSet) {
			segmentsEntryRoleLocalService.deleteSegmentsEntryRole(
				segmentsEntryId, siteRoleId);
		}
	}

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference
	private SegmentsEntryPersistence _segmentsEntryPersistence;

	@Reference
	private UserLocalService _userLocalService;

}