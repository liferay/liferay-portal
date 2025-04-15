/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.security.permission.contributor;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.security.permission.contributor.RoleCollection;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsEntryRole;
import com.liferay.segments.service.SegmentsEntryRoleLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	configurationPid = "com.liferay.segments.configuration.SegmentsConfiguration",
	service = RoleContributor.class
)
public class SegmentsEntryRoleContributor implements RoleContributor {

	@Override
	public void contribute(RoleCollection roleCollection) {
		try {
			if (!_segmentsConfigurationProvider.isRoleSegmentationEnabled(
					roleCollection.getCompanyId())) {

				return;
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);

			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			if (permissionChecker != null) {
				PermissionThreadLocal.setPermissionChecker(
					new LiberalPermissionChecker(permissionChecker.getUser()));
			}

			for (long segmentsEntryId : _getSegmentsEntryIds(roleCollection)) {
				List<SegmentsEntryRole> segmentsEntryRoles =
					_segmentsEntryRoleLocalService.getSegmentsEntryRoles(
						segmentsEntryId);

				for (SegmentsEntryRole segmentsEntryRole : segmentsEntryRoles) {
					roleCollection.addRoleId(segmentsEntryRole.getRoleId());
				}
			}
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private long[] _getSegmentsEntryIds(RoleCollection roleCollection) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return new long[0];
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return new long[0];
		}

		long[] cachedSegmentsEntryIds = (long[])httpServletRequest.getAttribute(
			SegmentsWebKeys.SEGMENTS_ENTRY_IDS);

		if (cachedSegmentsEntryIds != null) {
			return cachedSegmentsEntryIds;
		}

		User user = roleCollection.getUser();

		long[] segmentsEntryIds = _segmentsEntryRetriever.getSegmentsEntryIds(
			roleCollection.getGroupId(), user.getUserId(),
			_requestContextMapper.map(httpServletRequest), new long[0]);

		if ((segmentsEntryIds.length > 0) && _log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Found segments ", segmentsEntryIds, " for user ",
					user.getUserId(), " in group ",
					roleCollection.getGroupId()));
		}

		httpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_ENTRY_IDS, segmentsEntryIds);

		return segmentsEntryIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntryRoleContributor.class);

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

	@Reference
	private SegmentsEntryRoleLocalService _segmentsEntryRoleLocalService;

	private class LiberalPermissionChecker implements PermissionChecker {

		@Override
		public PermissionChecker clone() {
			return this;
		}

		@Override
		public long getCompanyId() {
			return _user.getCompanyId();
		}

		@Override
		public long[] getGuestUserRoleIds() {
			return PermissionChecker.DEFAULT_ROLE_IDS;
		}

		@Override
		public long getOwnerRoleId() {
			return _ownerRole.getRoleId();
		}

		@Override
		public Map<Object, Object> getPermissionChecksMap() {
			return new HashMap<>();
		}

		@Override
		public long[] getRoleIds(long userId, long groupId) {
			return PermissionChecker.DEFAULT_ROLE_IDS;
		}

		@Override
		public User getUser() {
			return _user;
		}

		@Override
		public UserBag getUserBag() {
			return null;
		}

		@Override
		public long getUserId() {
			return _user.getUserId();
		}

		@Override
		public boolean hasOwnerPermission(
			long companyId, String name, long primKey, long ownerId,
			String actionId) {

			return true;
		}

		@Override
		public boolean hasOwnerPermission(
			long companyId, String name, String primKey, long ownerId,
			String actionId) {

			return true;
		}

		@Override
		public boolean hasPermission(
			Group group, String name, long primKey, String actionId) {

			return true;
		}

		@Override
		public boolean hasPermission(
			Group group, String name, String primKey, String actionId) {

			return true;
		}

		@Override
		public boolean hasPermission(
			long groupId, String name, long primKey, String actionId) {

			return true;
		}

		@Override
		public boolean hasPermission(
			long groupId, String name, String primKey, String actionId) {

			return true;
		}

		@Override
		public void init(User user) {
			_user = user;

			try {
				_ownerRole = _roleLocalService.getRole(
					user.getCompanyId(), RoleConstants.OWNER);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		@Override
		public boolean isCheckGuest() {
			return PropsValues.PERMISSIONS_CHECK_GUEST_ENABLED;
		}

		@Override
		public boolean isCompanyAdmin() {
			return true;
		}

		@Override
		public boolean isCompanyAdmin(long companyId) {
			return true;
		}

		@Override
		public boolean isContentReviewer(long companyId, long groupId) {
			return true;
		}

		@Override
		public boolean isGroupAdmin(long groupId) {
			return true;
		}

		@Override
		public boolean isGroupMember(long groupId) {
			return true;
		}

		@Override
		public boolean isGroupOwner(long groupId) {
			return true;
		}

		@Override
		public boolean isOmniadmin() {
			return true;
		}

		@Override
		public boolean isOrganizationAdmin(long organizationId) {
			return true;
		}

		@Override
		public boolean isOrganizationOwner(long organizationId) {
			return true;
		}

		@Override
		public boolean isSignedIn() {
			return true;
		}

		private LiberalPermissionChecker(User user) {
			init(user);
		}

		private Role _ownerRole;
		private User _user;

	}

}