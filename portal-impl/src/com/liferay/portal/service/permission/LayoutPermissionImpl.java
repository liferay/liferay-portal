/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.permission;

import com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.permission.WorkflowPermissionUtil;
import com.liferay.portal.util.LayoutTypeControllerTracker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
@OSGiBeanProperties(
	property = "model.class.name=com.liferay.portal.kernel.model.Layout"
)
public class LayoutPermissionImpl implements LayoutPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkViewableGroup, String actionId)
		throws PortalException {

		if (!contains(
				permissionChecker, layout, checkViewableGroup, actionId)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Layout.class.getName(), layout.getLayoutId(),
				actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, Layout layout, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, layout, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, Layout.class.getName(), layout.getLayoutId(),
				actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long groupId,
			boolean privateLayout, long layoutId, String actionId)
		throws PortalException {

		if (!contains(
				permissionChecker, groupId, privateLayout, layoutId,
				actionId)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Layout.class.getName(), layoutId, actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long plid, String actionId)
		throws PortalException {

		check(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid),
			actionId);
	}

	@Override
	public void checkLayoutRestrictedUpdatePermission(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		if (!containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Layout.class.getName(), layout.getPlid(),
				ActionKeys.UPDATE);
		}
	}

	@Override
	public void checkLayoutRestrictedUpdatePermission(
			PermissionChecker permissionChecker, long plid)
		throws PortalException {

		checkLayoutRestrictedUpdatePermission(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid));
	}

	@Override
	public void checkLayoutUpdatePermission(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		if (!containsLayoutUpdatePermission(permissionChecker, layout)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, Layout.class.getName(), layout.getPlid(),
				ActionKeys.UPDATE);
		}
	}

	@Override
	public void checkLayoutUpdatePermission(
			PermissionChecker permissionChecker, long plid)
		throws PortalException {

		checkLayoutUpdatePermission(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid));
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkViewableGroup, String actionId)
		throws PortalException {

		Map<Object, Object> permissionChecksMap =
			permissionChecker.getPermissionChecksMap();

		CacheKey cacheKey = new CacheKey(
			layout.getPlid(), layout.getMvccVersion(), checkViewableGroup,
			actionId);

		Boolean contains = (Boolean)permissionChecksMap.get(cacheKey);

		if (contains == null) {
			contains = _contains(
				permissionChecker, layout, checkViewableGroup, actionId);

			permissionChecksMap.put(cacheKey, contains);
		}

		return contains;
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, Layout layout, String actionId)
		throws PortalException {

		return contains(permissionChecker, layout, false, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long groupId,
			boolean privateLayout, long layoutId, String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			LayoutLocalServiceUtil.getLayout(groupId, privateLayout, layoutId),
			actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long plid, String actionId)
		throws PortalException {

		return contains(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid),
			actionId);
	}

	@Override
	public boolean containsLayoutPreviewDraftPermission(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return false;
		}

		if (containsLayoutUpdatePermission(permissionChecker, layout) ||
			contains(permissionChecker, layout, ActionKeys.PREVIEW_DRAFT)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean containsLayoutPreviewDraftPermission(
			PermissionChecker permissionChecker, long plid)
		throws PortalException {

		return containsLayoutPreviewDraftPermission(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid));
	}

	@Override
	public boolean containsLayoutRestrictedUpdatePermission(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		if (contains(permissionChecker, layout, ActionKeys.UPDATE) ||
			contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_BASIC) ||
			contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_LIMITED)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean containsLayoutRestrictedUpdatePermission(
			PermissionChecker permissionChecker, long plid)
		throws PortalException {

		return containsLayoutRestrictedUpdatePermission(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid));
	}

	@Override
	public boolean containsLayoutUpdatePermission(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		if (contains(permissionChecker, layout, ActionKeys.UPDATE) ||
			contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_BASIC) ||
			contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_CONTENT) ||
			contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_LIMITED)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean containsLayoutUpdatePermission(
			PermissionChecker permissionChecker, long plid)
		throws PortalException {

		return containsLayoutUpdatePermission(
			permissionChecker, LayoutLocalServiceUtil.getLayout(plid));
	}

	@Override
	public boolean containsWithoutViewableGroup(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkLayoutUpdateable, String actionId)
		throws PortalException {

		if (layout.isTypeControlPanel()) {
			return false;
		}

		if (checkLayoutUpdateable && !actionId.equals(ActionKeys.CUSTOMIZE) &&
			!actionId.equals(ActionKeys.VIEW) &&
			(layout instanceof VirtualLayout)) {

			return false;
		}

		if (actionId.equals(ActionKeys.CUSTOMIZE) &&
			(layout instanceof VirtualLayout)) {

			VirtualLayout virtualLayout = (VirtualLayout)layout;

			layout = virtualLayout.getWrappedModel();
		}

		if (actionId.equals(ActionKeys.ADD_LAYOUT)) {
			if ((layout instanceof VirtualLayout) ||
				!layout.isLayoutSortable()) {

				return false;
			}

			LayoutType layoutType = layout.getLayoutType();

			if (!layoutType.isParentable()) {
				return false;
			}
		}

		if (actionId.equals(ActionKeys.DELETE) &&
			((layout instanceof VirtualLayout) ||
			 !layout.isLayoutDeleteable())) {

			return false;
		}

		if (layout.isPending() &&
			(!actionId.equals(ActionKeys.VIEW) || !layout.isPublished())) {

			Boolean hasPermission = WorkflowPermissionUtil.hasPermission(
				permissionChecker, layout.getGroupId(), Layout.class.getName(),
				layout.getPlid(), actionId);

			if (hasPermission != null) {
				return hasPermission;
			}
		}

		Group group = layout.getGroup();

		if (checkLayoutUpdateable && !group.isLayoutSetPrototype() &&
			isAttemptToModifyLockedLayout(layout, actionId)) {

			return false;
		}

		if (actionId.equals(ActionKeys.ADD_LAYOUT) &&
			GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.ADD_LAYOUT)) {

			return true;
		}

		if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE &&
			actionId.equals(ActionKeys.VIEW)) {

			// Check upward recursively to see if any pages above forbid the
			// action

			long layoutGroupId = layout.getGroupId();

			if (layout instanceof VirtualLayout) {
				VirtualLayout virtualLayout = (VirtualLayout)layout;

				layoutGroupId = virtualLayout.getSourceGroupId();
			}

			long parentLayoutId = layout.getParentLayoutId();

			while (parentLayoutId != LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
				Layout parentLayout = LayoutLocalServiceUtil.getLayout(
					layoutGroupId, layout.isPrivateLayout(), parentLayoutId);

				if (!contains(permissionChecker, parentLayout, actionId)) {
					return false;
				}

				parentLayoutId = parentLayout.getParentLayoutId();
			}
		}

		if ((layout.isDraftLayout() &&
			 permissionChecker.hasPermission(
				 group, Layout.class.getName(), layout.getClassPK(),
				 actionId)) ||
			permissionChecker.hasPermission(
				group, Layout.class.getName(), layout.getPlid(), actionId)) {

			return true;
		}

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.MANAGE_LAYOUTS)) {

			return true;
		}

		User user = permissionChecker.getUser();

		if (!user.isGuestUser() && !group.isUser()) {

			// This is new way of doing an ownership check without having to
			// have a userId field on the model. When the instance model was
			// first created, we set the user's userId as the ownerId of the
			// individual scope ResourcePermission of the Owner Role. Therefore,
			// ownership can be determined by obtaining the Owner role
			// ResourcePermission for the current instance model and testing it
			// with the hasOwnerPermission call.

			ResourcePermission resourcePermission =
				ResourcePermissionLocalServiceUtil.getResourcePermission(
					layout.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()),
					permissionChecker.getOwnerRoleId());

			if (permissionChecker.hasOwnerPermission(
					layout.getCompanyId(), Layout.class.getName(),
					String.valueOf(layout.getPlid()),
					resourcePermission.getOwnerId(), actionId)) {

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsWithoutViewableGroup(
			PermissionChecker permissionChecker, Layout layout, String actionId)
		throws PortalException {

		return containsWithoutViewableGroup(
			permissionChecker, layout, true, actionId);
	}

	protected boolean containsWithViewableGroup(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkViewableGroup, String actionId)
		throws PortalException {

		if (actionId.equals(ActionKeys.VIEW) && checkViewableGroup) {
			return isViewableGroup(
				permissionChecker, layout, checkViewableGroup);
		}

		return containsWithoutViewableGroup(
			permissionChecker, layout, actionId);
	}

	protected boolean isAttemptToModifyLockedLayout(
		Layout layout, String actionId) {

		if ((ActionKeys.CUSTOMIZE.equals(actionId) ||
			 ActionKeys.UPDATE.equals(actionId)) &&
			((layout instanceof VirtualLayout) ||
			 !layout.isLayoutUpdateable())) {

			return true;
		}

		return false;
	}

	protected boolean isViewableGroup(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkResourcePermission)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(layout.getGroupId());

		if (group.isControlPanel() && layout.isTypeControlPanel()) {
			return permissionChecker.isSignedIn();
		}

		// Inactive sites are not viewable

		if (!GroupLocalServiceUtil.isLiveGroupActive(group)) {
			return false;
		}

		// User private layouts are only viewable by the user and anyone who can
		// update the user. The user must also be active.

		if (group.isUser()) {
			long groupUserId = group.getClassPK();

			if (groupUserId == permissionChecker.getUserId()) {
				return true;
			}

			User groupUser = UserLocalServiceUtil.getUserById(groupUserId);

			if (!groupUser.isActive()) {
				return false;
			}

			if (layout.isPrivateLayout()) {
				if (GroupPermissionUtil.contains(
						permissionChecker, group, ActionKeys.MANAGE_LAYOUTS) ||
					UserPermissionUtil.contains(
						permissionChecker, groupUserId,
						groupUser.getOrganizationIds(), ActionKeys.UPDATE)) {

					return true;
				}

				return false;
			}
		}

		// If the current group is staging, only users with editorial rights can
		// access it

		if (group.isStagingGroup()) {
			return GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.VIEW_STAGING);
		}

		// Site layouts are only viewable by users who are members of the site
		// or by users who can update the site

		if (group.isSite()) {
			if (GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.MANAGE_LAYOUTS) ||
				GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.UPDATE)) {

				return true;
			}

			if (layout.isPrivateLayout() &&
				!permissionChecker.isGroupMember(group.getGroupId())) {

				return false;
			}
		}

		// Organization site layouts are also viewable by users who belong to
		// the organization or by users who can update organization

		if (group.isCompany()) {
			return false;
		}
		else if (group.isLayoutPrototype()) {
			return LayoutPrototypePermissionUtil.contains(
				permissionChecker, group.getClassPK(), ActionKeys.VIEW);
		}
		else if (group.isLayoutSetPrototype()) {
			return LayoutSetPrototypePermissionUtil.contains(
				permissionChecker, group.getClassPK(), ActionKeys.VIEW);
		}
		else if (group.isOrganization()) {
			long organizationId = group.getOrganizationId();

			if (OrganizationLocalServiceUtil.hasUserOrganization(
					permissionChecker.getUserId(), organizationId, false,
					false)) {

				return true;
			}
			else if (OrganizationPermissionUtil.contains(
						permissionChecker, organizationId, ActionKeys.UPDATE)) {

				return true;
			}

			if (!PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
				List<Organization> userOrgs =
					OrganizationLocalServiceUtil.getUserOrganizations(
						permissionChecker.getUserId());

				for (Organization organization : userOrgs) {
					for (Organization ancestorOrganization :
							organization.getAncestors()) {

						if (organizationId ==
								ancestorOrganization.getOrganizationId()) {

							return true;
						}
					}
				}
			}
		}
		else if (group.isUserGroup()) {
			if (UserGroupPermissionUtil.contains(
					permissionChecker, group.getClassPK(), ActionKeys.UPDATE)) {

				return true;
			}
		}

		// Only check the actual Layout if all of the above failed

		if (containsWithoutViewableGroup(
				permissionChecker, layout, ActionKeys.VIEW)) {

			return true;
		}

		// As a last resort, check if any top level pages are viewable by the
		// user

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			layout.getGroupId(), layout.isPrivateLayout(),
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		for (Layout curLayout : layouts) {
			if (containsWithoutViewableGroup(
					permissionChecker, curLayout, ActionKeys.VIEW) &&
				!curLayout.isHidden()) {

				return true;
			}
		}

		return false;
	}

	private boolean _contains(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkViewableGroup, String actionId)
		throws PortalException {

		if (actionId.equals(ActionKeys.VIEW)) {
			LayoutTypeController layoutTypeController =
				LayoutTypeControllerTracker.getLayoutTypeController(
					layout.getType());

			if (!layoutTypeController.isCheckLayoutViewPermission()) {
				return true;
			}
		}

		if (actionId.equals(ActionKeys.CUSTOMIZE) &&
			(layout instanceof VirtualLayout)) {

			VirtualLayout virtualLayout = (VirtualLayout)layout;

			layout = virtualLayout.getWrappedModel();
		}

		if (isAttemptToModifyLockedLayout(layout, actionId)) {
			return false;
		}

		Group group = layout.getGroup();

		if (group.hasLocalOrRemoteStagingGroup()) {
			Boolean hasPermission = StagingPermissionUtil.hasPermission(
				permissionChecker, group, Layout.class.getName(),
				layout.getGroupId(), null, actionId);

			if (hasPermission != null) {
				return hasPermission.booleanValue();
			}
		}
		else if (!checkViewableGroup && group.isUserGroup() &&
				 actionId.equals(ActionKeys.VIEW)) {

			if (permissionChecker.isGroupAdmin(group.getGroupId())) {
				return true;
			}

			try {
				UserBag userBag = permissionChecker.getUserBag();

				if (userBag == null) {
					return UserGroupLocalServiceUtil.hasUserUserGroup(
						permissionChecker.getUserId(), group.getClassPK());
				}

				int count = Arrays.binarySearch(
					userBag.getUserUserGroupsIds(), group.getClassPK());

				if (count >= 0) {
					return true;
				}
			}
			catch (PortalException | RuntimeException exception) {
				throw exception;
			}
			catch (Exception exception) {
				throw new PortalException(exception);
			}
		}

		return containsWithViewableGroup(
			permissionChecker, layout, checkViewableGroup, actionId);
	}

	private static class CacheKey {

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof CacheKey)) {
				return false;
			}

			CacheKey cacheKey = (CacheKey)object;

			if ((_plid == cacheKey._plid) &&
				(_mvccVersion == cacheKey._mvccVersion) &&
				(_checkViewableGroup == cacheKey._checkViewableGroup) &&
				Objects.equals(_actionId, cacheKey._actionId)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, _plid);

			hash = HashUtil.hash(hash, _mvccVersion);
			hash = HashUtil.hash(hash, _checkViewableGroup);

			return HashUtil.hash(hash, _actionId);
		}

		private CacheKey(
			long plid, long mvccVersion, boolean checkViewableGroup,
			String actionId) {

			_plid = plid;
			_mvccVersion = mvccVersion;
			_checkViewableGroup = checkViewableGroup;
			_actionId = actionId;
		}

		private final String _actionId;
		private final boolean _checkViewableGroup;
		private final long _mvccVersion;
		private final long _plid;

	}

}