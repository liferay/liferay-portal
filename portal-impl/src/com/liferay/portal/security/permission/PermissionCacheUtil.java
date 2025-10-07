/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.index.IndexEncoder;
import com.liferay.portal.kernel.cache.index.PortalCacheIndexer;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.io.Serializable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Charles May
 * @author Michael Young
 * @author Shuyang Zhou
 * @author Connor McKay
 * @author László Csontos
 */
public class PermissionCacheUtil {

	public static final String PERMISSION_CACHE_NAME =
		PermissionCacheUtil.class.getName() + "_PERMISSION";

	public static final String PERMISSION_CHECKER_BAG_CACHE_NAME =
		PermissionCacheUtil.class.getName() + "_PERMISSION_CHECKER_BAG";

	public static final String USER_BAG_CACHE_NAME =
		PermissionCacheUtil.class.getName() + "_USER_BAG";

	public static final String USER_PRIMARY_KEY_ROLE_CACHE_NAME =
		PermissionCacheUtil.class.getName() + "_USER_PRIMARY_KEY_ROLE";

	public static final String USER_ROLE_CACHE_NAME =
		PermissionCacheUtil.class.getName() + "_USER_ROLE";

	public static void clearCache() {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		_clearPermissionChecksMap();

		_userRolePortalCache.removeAll();
		_userGroupRoleIdsPortalCache.removeAll();
		_permissionPortalCache.removeAll();
		_userBagPortalCache.removeAll();
		_userPrimaryKeyRolePortalCache.removeAll();
	}

	public static void clearCache(long... userIds) {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		_clearPermissionChecksMap();

		for (long userId : userIds) {
			_userBagPortalCache.remove(userId);
			_userGroupRoleIdsPortalCache.remove(userId);

			_userPrimaryKeyRolePortalCacheUserIdIndexer.removeKeys(userId);
			_userRolePortalCacheIndexer.removeKeys(userId);
		}

		_permissionPortalCache.removeAll();

		_sendClearCacheClusterMessage(_clearCacheMethodKey, userIds);
	}

	public static void clearPrimaryKeyRoleCache() {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		_clearPermissionChecksMap();

		_permissionPortalCache.removeAll();
		_userPrimaryKeyRolePortalCache.removeAll();
	}

	public static void clearResourceCache() {
		if (!ExportImportThreadLocal.isImportInProcess()) {
			_permissionPortalCache.removeAll();
		}

		_clearPermissionChecksMap();
	}

	public static void clearResourcePermissionCache(
		int scope, String name, String primKey) {

		if (ExportImportThreadLocal.isImportInProcess() ||
			!PermissionThreadLocal.isFlushResourcePermissionEnabled(
				name, primKey)) {

			return;
		}

		_clearPermissionChecksMap();

		if (scope == ResourceConstants.SCOPE_INDIVIDUAL) {
			_permissionPortalCacheNamePrimKeyIndexer.removeKeys(
				PermissionKeyNamePrimKeyIndexEncoder.encode(name, primKey));

			_sendClearCacheClusterMessage(
				_clearResourcePermissionCacheMethodKey, scope, name, primKey);
		}
		else if (scope == ResourceConstants.SCOPE_GROUP) {
			_permissionPortalCacheGroupIdIndexer.removeKeys(
				Long.valueOf(primKey));

			_sendClearCacheClusterMessage(
				_clearResourcePermissionCacheMethodKey, scope, name, primKey);
		}
		else {
			_permissionPortalCache.removeAll();
		}
	}

	public static Boolean getPermission(
		long groupId, String name, String primKey, long[] roleIds,
		String actionId) {

		if (!CTCollectionThreadLocal.isProductionMode()) {
			return null;
		}

		PermissionKey permissionKey = new PermissionKey(
			groupId, name, primKey, roleIds, actionId);

		return _permissionPortalCache.get(permissionKey);
	}

	public static UserBag getUserBag(long userId) {
		if (!CTCollectionThreadLocal.isProductionMode()) {
			return null;
		}

		return _userBagPortalCache.get(userId);
	}

	public static Map<Long, long[]> getUserGroupRoleIds(long userId) {
		Map<Long, long[]> groupRoleIds = _userGroupRoleIdsPortalCache.get(
			userId);

		if (groupRoleIds == null) {
			groupRoleIds = new ConcurrentHashMap<>();

			PortalCacheHelperUtil.putWithoutReplicator(
				_userGroupRoleIdsPortalCache, userId, groupRoleIds);
		}

		return groupRoleIds;
	}

	public static Boolean getUserPrimaryKeyRole(
		long userId, long primaryKey, String roleName) {

		UserPrimaryKeyRoleKey userPrimaryKeyRoleKey = new UserPrimaryKeyRoleKey(
			userId, primaryKey, roleName);

		return _userPrimaryKeyRolePortalCache.get(userPrimaryKeyRoleKey);
	}

	public static Boolean getUserRole(long userId, Role role) {
		UserRoleKey userRoleKey = new UserRoleKey(userId, role.getRoleId());

		Boolean userRole = _userRolePortalCache.get(userRoleKey);

		if (userRole != null) {
			return userRole;
		}

		UserBag userBag = getUserBag(userId);

		if (userBag == null) {
			return null;
		}

		userRole = userBag.hasRole(role);

		PortalCacheHelperUtil.putWithoutReplicator(
			_userRolePortalCache, userRoleKey, userRole);

		return userRole;
	}

	public static void putPermission(
		long groupId, String name, String primKey, long[] roleIds,
		String actionId, Boolean value) {

		if (!CTCollectionThreadLocal.isProductionMode()) {
			return;
		}

		PermissionKey permissionKey = new PermissionKey(
			groupId, name, primKey, roleIds, actionId);

		PortalCacheHelperUtil.putWithoutReplicator(
			_permissionPortalCache, permissionKey, value);
	}

	public static void putUserBag(long userId, UserBag userBag) {
		if (CTCollectionThreadLocal.isProductionMode()) {
			PortalCacheHelperUtil.putWithoutReplicator(
				_userBagPortalCache, userId, userBag);
		}
	}

	public static void putUserPrimaryKeyRole(
		long userId, long primaryKey, String roleName, Boolean value) {

		if (value == null) {
			return;
		}

		UserPrimaryKeyRoleKey userPrimaryKeyRoleKey = new UserPrimaryKeyRoleKey(
			userId, primaryKey, roleName);

		PortalCacheHelperUtil.putWithoutReplicator(
			_userPrimaryKeyRolePortalCache, userPrimaryKeyRoleKey, value);
	}

	public static void putUserRole(long userId, Role role, Boolean value) {
		if (value == null) {
			return;
		}

		UserRoleKey userRoleKey = new UserRoleKey(userId, role.getRoleId());

		PortalCacheHelperUtil.putWithoutReplicator(
			_userRolePortalCache, userRoleKey, value);
	}

	private static void _clearPermissionChecksMap() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker != null) {
			Map<Object, Object> permissionChecksMap =
				permissionChecker.getPermissionChecksMap();

			if (permissionChecksMap != null) {
				permissionChecksMap.clear();
			}
		}
	}

	private static void _sendClearCacheClusterMessage(
		MethodKey methodKey, Object... arguments) {

		if (!ClusterInvokeThreadLocal.isEnabled()) {
			return;
		}

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			new MethodHandler(methodKey, arguments), true);

		clusterRequest.setFireAndForget(true);

		ClusterExecutorUtil.execute(clusterRequest);
	}

	private static final MethodKey _clearCacheMethodKey = new MethodKey(
		PermissionCacheUtil.class, "clearCache", long[].class);
	private static final MethodKey _clearResourcePermissionCacheMethodKey =
		new MethodKey(
			PermissionCacheUtil.class, "clearResourcePermissionCache",
			int.class, String.class, String.class);
	private static final PortalCache<PermissionKey, Boolean>
		_permissionPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, PERMISSION_CACHE_NAME);
	private static final PortalCacheIndexer<Long, PermissionKey, Boolean>
		_permissionPortalCacheGroupIdIndexer = new PortalCacheIndexer<>(
			new PermissionKeyGroupIdIndexEncoder(), _permissionPortalCache);
	private static final PortalCacheIndexer
		<Map.Entry<?, ?>, PermissionKey, Boolean>
			_permissionPortalCacheNamePrimKeyIndexer = new PortalCacheIndexer<>(
				new PermissionKeyNamePrimKeyIndexEncoder(),
				_permissionPortalCache);
	private static final PortalCache<Long, UserBag> _userBagPortalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, USER_BAG_CACHE_NAME);
	private static final PortalCache<Long, Map<Long, long[]>>
		_userGroupRoleIdsPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			PERMISSION_CHECKER_BAG_CACHE_NAME);
	private static final PortalCache<UserPrimaryKeyRoleKey, Boolean>
		_userPrimaryKeyRolePortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, USER_PRIMARY_KEY_ROLE_CACHE_NAME);
	private static final PortalCacheIndexer
		<Long, UserPrimaryKeyRoleKey, Boolean>
			_userPrimaryKeyRolePortalCacheUserIdIndexer =
				new PortalCacheIndexer<>(
					new UserGroupRoleKeyUserIdEncoder(),
					_userPrimaryKeyRolePortalCache);
	private static final PortalCache<UserRoleKey, Boolean>
		_userRolePortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM, USER_ROLE_CACHE_NAME);
	private static final PortalCacheIndexer<Long, UserRoleKey, Boolean>
		_userRolePortalCacheIndexer = new PortalCacheIndexer<>(
			new UserRoleKeyIndexEncoder(), _userRolePortalCache);

	private static class PermissionKey implements Serializable {

		@Override
		public boolean equals(Object object) {
			PermissionKey permissionKey = (PermissionKey)object;

			if ((permissionKey._groupId == _groupId) &&
				Objects.equals(permissionKey._name, _name) &&
				Objects.equals(permissionKey._primKey, _primKey) &&
				Arrays.equals(permissionKey._roleIds, _roleIds) &&
				Objects.equals(permissionKey._actionId, _actionId)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _groupId);

			hashCode = HashUtil.hash(hashCode, _name);
			hashCode = HashUtil.hash(hashCode, _primKey);
			hashCode = HashUtil.hash(hashCode, _roleIds.length);

			for (long roleId : _roleIds) {
				hashCode = HashUtil.hash(hashCode, roleId);
			}

			return HashUtil.hash(hashCode, _actionId);
		}

		private PermissionKey(
			long groupId, String name, String primKey, long[] roleIds,
			String actionId) {

			_groupId = groupId;
			_name = name;
			_primKey = primKey;
			_roleIds = roleIds;
			_actionId = actionId;
		}

		private static final long serialVersionUID = 1L;

		private final String _actionId;
		private final long _groupId;
		private final String _name;
		private final String _primKey;
		private final long[] _roleIds;

	}

	private static class PermissionKeyGroupIdIndexEncoder
		implements IndexEncoder<Long, PermissionKey> {

		@Override
		public Long encode(PermissionKey permissionKey) {
			return permissionKey._groupId;
		}

	}

	private static class PermissionKeyNamePrimKeyIndexEncoder
		implements IndexEncoder<Map.Entry<?, ?>, PermissionKey> {

		public static Map.Entry<?, ?> encode(String name, String primKey) {
			return new AbstractMap.SimpleImmutableEntry<>(name, primKey);
		}

		@Override
		public Map.Entry<?, ?> encode(PermissionKey permissionKey) {
			return encode(permissionKey._name, permissionKey._primKey);
		}

	}

	private static class UserGroupRoleKeyUserIdEncoder
		implements IndexEncoder<Long, UserPrimaryKeyRoleKey> {

		@Override
		public Long encode(UserPrimaryKeyRoleKey key) {
			return key._userId;
		}

	}

	private static class UserPrimaryKeyRoleKey implements Serializable {

		@Override
		public boolean equals(Object object) {
			UserPrimaryKeyRoleKey userPrimaryKeyRoleKey =
				(UserPrimaryKeyRoleKey)object;

			if ((userPrimaryKeyRoleKey._userId == _userId) &&
				(userPrimaryKeyRoleKey._primaryKey == _primaryKey) &&
				Objects.equals(userPrimaryKeyRoleKey._name, _name)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _userId);

			hashCode = HashUtil.hash(hashCode, _primaryKey);
			hashCode = HashUtil.hash(hashCode, _name);

			return hashCode;
		}

		private UserPrimaryKeyRoleKey(
			long userId, long primaryKey, String name) {

			_userId = userId;
			_primaryKey = primaryKey;
			_name = name;
		}

		private static final long serialVersionUID = 1L;

		private final String _name;
		private final long _primaryKey;
		private final long _userId;

	}

	private static class UserRoleKey implements Serializable {

		@Override
		public boolean equals(Object object) {
			UserRoleKey userRoleKey = (UserRoleKey)object;

			if ((userRoleKey._userId == _userId) &&
				(userRoleKey._roleId == _roleId)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _userId);

			return HashUtil.hash(hashCode, _roleId);
		}

		private UserRoleKey(long userId, long roleId) {
			_userId = userId;
			_roleId = roleId;
		}

		private static final long serialVersionUID = 1L;

		private final long _roleId;
		private final long _userId;

	}

	private static class UserRoleKeyIndexEncoder
		implements IndexEncoder<Long, UserRoleKey> {

		@Override
		public Long encode(UserRoleKey userRoleKey) {
			return userRoleKey._userId;
		}

	}

}