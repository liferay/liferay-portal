/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.SearchPermissionCheckerConfiguration;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFieldContributor;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Allen Chiang
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Amos Fong
 * @author Preston Crary
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.SearchPermissionCheckerConfiguration",
	service = SearchPermissionChecker.class
)
public class SearchPermissionCheckerImpl implements SearchPermissionChecker {

	@Override
	public void addPermissionFields(long companyId, Document document) {
		try {
			long groupId = GetterUtil.getLong(document.get(Field.GROUP_ID));

			String className = document.get(Field.ENTRY_CLASS_NAME);
			String classPK = document.get(Field.ENTRY_CLASS_PK);

			if (Validator.isNull(className) && Validator.isNull(classPK)) {
				className = document.get(Field.ROOT_ENTRY_CLASS_NAME);
				classPK = document.get(Field.ROOT_ENTRY_CLASS_PK);
			}

			boolean relatedEntry = GetterUtil.getBoolean(
				document.get(Field.RELATED_ENTRY));

			if (relatedEntry) {
				long classNameId = GetterUtil.getLong(
					document.get(Field.CLASS_NAME_ID));

				if (classNameId == 0) {
					return;
				}

				className = _portal.getClassName(classNameId);

				classPK = document.get(Field.CLASS_PK);
			}

			if (Validator.isNull(className) || Validator.isNull(classPK)) {
				return;
			}

			Indexer<?> indexer = _indexerRegistry.nullSafeGetIndexer(className);

			if (!indexer.isPermissionAware()) {
				return;
			}

			String viewActionId = document.get(Field.VIEW_ACTION_ID);

			if (Validator.isNull(viewActionId)) {
				viewActionId = ActionKeys.VIEW;
			}

			_addPermissionFields(
				companyId, groupId, className, GetterUtil.getLong(classPK),
				viewActionId, document);
		}
		catch (NoSuchResourceException noSuchResourceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchResourceException);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public BooleanFilter getPermissionBooleanFilter(
		long companyId, long[] groupIds, long userId, String className,
		BooleanFilter booleanFilter, SearchContext searchContext) {

		try {
			return _getPermissionBooleanFilter(
				companyId, groupIds, userId, className, booleanFilter,
				searchContext);
		}
		catch (Exception exception) {
			_log.error(exception);

			return booleanFilter;
		}
	}

	@Override
	public void updatePermissionFields(
		String resourceName, String resourceClassPK) {

		try {
			Indexer<?> indexer = _indexerRegistry.nullSafeGetIndexer(
				resourceName);

			indexer.reindex(resourceName, GetterUtil.getLong(resourceClassPK));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, SearchPermissionFilterContributor.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_searchPermissionCheckerConfiguration =
			ConfigurableUtil.createConfigurable(
				SearchPermissionCheckerConfiguration.class, properties);
	}

	private void _add(BooleanFilter booleanFilter, TermsFilter termsFilter) {
		if (!termsFilter.isEmpty()) {
			booleanFilter.add(termsFilter, BooleanClauseOccur.SHOULD);
		}
	}

	private void _addGroup(
		Group group, List<Role> groupRoles,
		List<UsersGroupIdRoles> usersGroupIdsRoles) {

		if (group != null) {
			usersGroupIdsRoles.add(
				new UsersGroupIdRoles(group.getGroupId(), groupRoles));
		}
	}

	private void _addPermissionFields(
			long companyId, long groupId, String className, long classPK,
			String viewActionId, Document document)
		throws Exception {

		for (SearchPermissionFieldContributor searchPermissionFieldContributor :
				SearchPermissionFieldContributorRegistryUtil.
					getSearchPermissionFieldContributors()) {

			searchPermissionFieldContributor.contribute(
				document, className, classPK);
		}

		List<Role> roles = _resourcePermissionLocalService.getRoles(
			companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(classPK), viewActionId);

		if (roles.isEmpty()) {
			return;
		}

		List<Long> roleIds = new ArrayList<>();
		List<String> groupRoleIds = new ArrayList<>();

		for (Role role : roles) {
			if ((role.getType() == RoleConstants.TYPE_ORGANIZATION) ||
				(role.getType() == RoleConstants.TYPE_SITE)) {

				groupRoleIds.add(groupId + StringPool.DASH + role.getRoleId());
			}
			else {
				roleIds.add(role.getRoleId());
			}
		}

		document.addKeyword(Field.ROLE_ID, roleIds.toArray(new Long[0]));
		document.addKeyword(
			Field.GROUP_ROLE_ID, groupRoleIds.toArray(new String[0]));
	}

	private SearchPermissionContext _createSearchPermissionContext(
			long companyId, long[] groupIds, long userId,
			PermissionChecker permissionChecker)
		throws Exception {

		UserBag userBag = permissionChecker.getUserBag();

		if (userBag == null) {
			return null;
		}

		Set<Role> roles = new HashSet<>();

		if (permissionChecker.isSignedIn() && ArrayUtil.isNotEmpty(groupIds)) {
			for (long groupId : groupIds) {
				for (Role role :
						_roleLocalService.getRoles(
							permissionChecker.getRoleIds(userId, groupId))) {

					if ((role.getType() == RoleConstants.TYPE_DEPOT) ||
						(role.getType() == RoleConstants.TYPE_REGULAR)) {

						roles.add(role);
					}
				}
			}
		}
		else {
			roles.addAll(
				_roleLocalService.getRoles(
					permissionChecker.getRoleIds(userId, 0)));
		}

		int termsCount = roles.size();

		int permissionTermsLimit =
			_searchPermissionCheckerConfiguration.permissionTermsLimit();

		if (termsCount > permissionTermsLimit) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping presearch permission checking due to too ",
						"many roles: ", termsCount, " > ",
						permissionTermsLimit));
			}

			return null;
		}

		Collection<Group> groups = userBag.getGroups();

		List<UsersGroupIdRoles> usersGroupIdsRoles = new ArrayList<>(
			groups.size());

		termsCount += groups.size();

		if (termsCount > permissionTermsLimit) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping presearch permission checking due to too ",
						"many roles and groups: ", termsCount, " > ",
						permissionTermsLimit));
			}

			return null;
		}

		Role assetLibraryMemberRole = _roleLocalService.getRole(
			companyId, DepotRolesConstants.ASSET_LIBRARY_MEMBER);
		Role organizationUserRole = _roleLocalService.getRole(
			companyId, RoleConstants.ORGANIZATION_USER);
		Role siteMemberRole = _roleLocalService.getRole(
			companyId, RoleConstants.SITE_MEMBER);

		for (Group group : groups) {
			List<Role> groupRoles = _roleLocalService.getRoles(
				permissionChecker.getRoleIds(userId, group.getGroupId()));

			roles.addAll(groupRoles);

			Iterator<Role> iterator = groupRoles.iterator();

			while (iterator.hasNext()) {
				Role groupRole = iterator.next();

				if ((groupRole.getType() != RoleConstants.TYPE_DEPOT) &&
					(groupRole.getType() != RoleConstants.TYPE_ORGANIZATION) &&
					(groupRole.getType() != RoleConstants.TYPE_SITE)) {

					iterator.remove();
				}
			}

			if (group.isDepot() &&
				!groupRoles.contains(assetLibraryMemberRole)) {

				groupRoles.add(assetLibraryMemberRole);
			}

			if (group.isOrganization() &&
				!groupRoles.contains(organizationUserRole)) {

				groupRoles.add(organizationUserRole);
			}

			if (group.isSite() && !groupRoles.contains(siteMemberRole)) {
				groupRoles.add(siteMemberRole);
			}

			_addGroup(group, groupRoles, usersGroupIdsRoles);

			_addGroup(group.getStagingGroup(), groupRoles, usersGroupIdsRoles);

			termsCount += groupRoles.size();

			if (termsCount > permissionTermsLimit) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Skipping presearch permission checking due to ",
							"too many roles, groups, and group roles: ",
							termsCount, " > ", permissionTermsLimit));
				}

				return null;
			}
		}

		return new SearchPermissionContext(roles, usersGroupIdsRoles);
	}

	private BooleanFilter _getPermissionBooleanFilter(
			long companyId, long[] groupIds, long userId, String className,
			BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {

		BooleanFilter permissionBooleanFilter = _getPermissionBooleanFilter(
			companyId, groupIds, userId, className, searchContext);

		if (booleanFilter == null) {
			return permissionBooleanFilter;
		}

		if (permissionBooleanFilter != null) {
			booleanFilter.add(permissionBooleanFilter, BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private BooleanFilter _getPermissionBooleanFilter(
			long companyId, long[] groupIds, long userId, String className,
			SearchContext searchContext)
		throws Exception {

		Indexer<?> indexer = _indexerRegistry.getIndexer(className);

		if (!indexer.isPermissionAware()) {
			return null;
		}

		PermissionChecker permissionChecker = _getPermissionChecker();

		User user = permissionChecker.getUser();

		if ((user == null) || (user.getUserId() != userId)) {
			user = _userLocalService.fetchUser(userId);

			if (user == null) {
				return null;
			}

			permissionChecker = _permissionCheckerFactory.create(user);
		}

		Object searchPermissionContextObject = searchContext.getAttribute(
			"searchPermissionContext");

		SearchPermissionContext searchPermissionContext = null;

		if (searchPermissionContextObject != null) {
			if (searchPermissionContextObject ==
					_NULL_SEARCH_PERMISSION_CONTEXT) {

				return null;
			}

			searchPermissionContext =
				(SearchPermissionContext)searchPermissionContextObject;
		}
		else if (!permissionChecker.isCompanyAdmin(companyId)) {
			searchPermissionContext = _createSearchPermissionContext(
				companyId, groupIds, userId, permissionChecker);
		}

		if (searchPermissionContext == null) {
			searchContext.setAttribute(
				"searchPermissionContext", _NULL_SEARCH_PERMISSION_CONTEXT);

			return null;
		}

		searchContext.setAttribute(
			"searchPermissionContext", searchPermissionContext);

		return _getPermissionFilter(
			companyId, groupIds, userId, permissionChecker,
			_getPermissionName(searchContext, className),
			searchPermissionContext);
	}

	private PermissionChecker _getPermissionChecker() {
		if (_permissionChecker != null) {
			return _permissionChecker;
		}

		return PermissionThreadLocal.getPermissionChecker();
	}

	private BooleanFilter _getPermissionFilter(
			long companyId, long[] groupIds, long userId,
			PermissionChecker permissionChecker, String className,
			SearchPermissionContext searchPermissionContext)
		throws Exception {

		List<UsersGroupIdRoles> usersGroupIdsRoles =
			searchPermissionContext._usersGroupIdsRoles;

		BooleanFilter booleanFilter = new BooleanFilter();

		if (userId > 0) {
			booleanFilter.add(
				new TermFilter(Field.USER_ID, String.valueOf(userId)),
				BooleanClauseOccur.SHOULD);
		}

		TermsFilter groupRolesTermsFilter =
			searchPermissionContext._groupRolesTermsFilter;
		TermsFilter rolesTermsFilter =
			searchPermissionContext._rolesTermsFilter;

		long[] roleIds = searchPermissionContext._roleIds;

		if (_resourcePermissionLocalService.hasResourcePermission(
				companyId, className, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), roleIds, ActionKeys.VIEW)) {

			return null;
		}

		long[] regularRoleIds = searchPermissionContext._regularRoleIds;

		if (_resourcePermissionLocalService.hasResourcePermission(
				companyId, className, ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				regularRoleIds, ActionKeys.VIEW)) {

			return null;
		}

		TermsFilter groupsTermsFilter = new TermsFilter(Field.GROUP_ID);

		for (UsersGroupIdRoles usersGroupIdRoles : usersGroupIdsRoles) {
			long groupId = usersGroupIdRoles._groupId;
			List<Role> groupRoles = usersGroupIdRoles._groupRoles;

			if (permissionChecker.isGroupAdmin(groupId) ||
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, className, ResourceConstants.SCOPE_GROUP,
					String.valueOf(groupId), roleIds, ActionKeys.VIEW) ||
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, className,
					ResourceConstants.SCOPE_GROUP_TEMPLATE,
					String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
					ListUtil.toLongArray(groupRoles, Role.ROLE_ID_ACCESSOR),
					ActionKeys.VIEW)) {

				groupsTermsFilter.addValue(String.valueOf(groupId));
			}
		}

		if (ArrayUtil.isEmpty(groupIds)) {
			List<ResourcePermission> resourcePermissions = new ArrayList<>();

			for (long roleId : roleIds) {
				resourcePermissions.addAll(
					_resourcePermissionLocalService.getResourcePermissions(
						companyId, className, ResourceConstants.SCOPE_GROUP,
						roleId, true));
			}

			groupsTermsFilter.addValues(
				TransformUtil.transformToArray(
					resourcePermissions,
					resourcePermission -> resourcePermission.getPrimKey(),
					String.class));
		}
		else {
			for (long searchGroupId : groupIds) {
				if (!searchPermissionContext.containsGroupId(searchGroupId) &&
					_resourcePermissionLocalService.hasResourcePermission(
						companyId, className, ResourceConstants.SCOPE_GROUP,
						String.valueOf(searchGroupId), roleIds,
						ActionKeys.VIEW)) {

					groupsTermsFilter.addValue(String.valueOf(searchGroupId));
				}
			}
		}

		_add(booleanFilter, groupRolesTermsFilter);
		_add(booleanFilter, groupsTermsFilter);
		_add(booleanFilter, rolesTermsFilter);

		for (SearchPermissionFilterContributor
				searchPermissionFilterContributor : _serviceTrackerList) {

			searchPermissionFilterContributor.contribute(
				booleanFilter, companyId, groupIds, userId, permissionChecker,
				className);
		}

		if (!booleanFilter.hasClauses()) {
			return null;
		}

		return booleanFilter;
	}

	private String _getPermissionName(
		SearchContext searchContext, String defaultValue) {

		return GetterUtil.getString(
			searchContext.getAttribute("resourcePermissionName"), defaultValue);
	}

	private static final String _NULL_SEARCH_PERMISSION_CONTEXT =
		StringPool.BLANK;

	private static final Log _log = LogFactoryUtil.getLog(
		SearchPermissionCheckerImpl.class);

	@Reference
	private IndexerRegistry _indexerRegistry;

	private PermissionChecker _permissionChecker;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	private volatile SearchPermissionCheckerConfiguration
		_searchPermissionCheckerConfiguration;
	private ServiceTrackerList<SearchPermissionFilterContributor>
		_serviceTrackerList;

	@Reference
	private UserLocalService _userLocalService;

	private static class SearchPermissionContext implements Serializable {

		public boolean containsGroupId(long groupId) {
			for (UsersGroupIdRoles usersGroupIdRoles : _usersGroupIdsRoles) {
				if (groupId == usersGroupIdRoles._groupId) {
					return true;
				}
			}

			return false;
		}

		private SearchPermissionContext(
			Set<Role> roles, List<UsersGroupIdRoles> usersGroupIdsRoles) {

			_usersGroupIdsRoles = usersGroupIdsRoles;

			List<Long> roleIds = new ArrayList<>(roles.size());
			List<Long> regularRoleIds = new ArrayList<>();

			for (Role role : roles) {
				roleIds.add(role.getRoleId());

				if (role.getType() == RoleConstants.TYPE_REGULAR) {
					regularRoleIds.add(role.getRoleId());
				}

				_rolesTermsFilter.addValue(String.valueOf(role.getRoleId()));
			}

			_roleIds = ArrayUtil.toLongArray(roleIds);
			_regularRoleIds = ArrayUtil.toLongArray(regularRoleIds);

			for (UsersGroupIdRoles usersGroupIdRoles : _usersGroupIdsRoles) {
				long groupId = usersGroupIdRoles._groupId;
				List<Role> groupRoles = usersGroupIdRoles._groupRoles;

				for (Role groupRole : groupRoles) {
					_groupRolesTermsFilter.addValue(
						groupId + StringPool.DASH + groupRole.getRoleId());
				}
			}
		}

		private static final long serialVersionUID = 1L;

		private final TermsFilter _groupRolesTermsFilter = new TermsFilter(
			Field.GROUP_ROLE_ID);
		private final long[] _regularRoleIds;
		private final long[] _roleIds;
		private final TermsFilter _rolesTermsFilter = new TermsFilter(
			Field.ROLE_ID);
		private final List<UsersGroupIdRoles> _usersGroupIdsRoles;

	}

	private static class UsersGroupIdRoles implements Serializable {

		private UsersGroupIdRoles(long groupId, List<Role> groupRoles) {
			_groupId = groupId;
			_groupRoles = groupRoles;
		}

		private static final long serialVersionUID = 1L;

		private final long _groupId;
		private final List<Role> _groupRoles;

	}

}