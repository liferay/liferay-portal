/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides the local service utility for ResourcePermission. This utility wraps
 * <code>com.liferay.portal.service.impl.ResourcePermissionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ResourcePermissionLocalService
 * @generated
 */
public class ResourcePermissionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.ResourcePermissionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addModelResourcePermissions(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			ServiceContext serviceContext)
		throws PortalException {

		getService().addModelResourcePermissions(auditedModel, serviceContext);
	}

	public static void addModelResourcePermissions(
			long companyId, long groupId, long userId, String name,
			String primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addModelResourcePermissions(
			companyId, groupId, userId, name, primKey, modelPermissions);
	}

	/**
	 * Adds resources for the model with the name and primary key string, always
	 * creating a resource at the individual scope and only creating resources
	 * at the group, group template, and company scope if such resources don't
	 * already exist.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user adding the resources
	 * @param name a name for the resource, typically the model's class name
	 * @param primKey the primary key string of the model instance, optionally
	 an empty string if no instance exists
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	public static void addModelResourcePermissions(
			long companyId, long groupId, long userId, String name,
			String primKey, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		getService().addModelResourcePermissions(
			companyId, groupId, userId, name, primKey, groupPermissions,
			guestPermissions);
	}

	/**
	 * Grants the role permission at the scope to perform the action on
	 * resources of the type. Existing actions are retained.
	 *
	 * <p>
	 * This method cannot be used to grant individual scope permissions, but is
	 * only intended for adding permissions at the company, group, and
	 * group-template scopes. For example, this method could be used to grant a
	 * company scope permission to edit message board posts.
	 * </p>
	 *
	 * <p>
	 * If a company scope permission is granted to resources that the role
	 * already had group scope permissions to, the group scope permissions are
	 * deleted. Likewise, if a group scope permission is granted to resources
	 * that the role already had company scope permissions to, the company scope
	 * permissions are deleted. Be aware that this latter behavior can result in
	 * an overall reduction in permissions for the role.
	 * </p>
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope. This method only supports company, group, and
	 group-template scope.
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @param actionId the action ID
	 */
	public static void addResourcePermission(
			long companyId, String name, int scope, String primKey, long roleId,
			String actionId)
		throws PortalException {

		getService().addResourcePermission(
			companyId, name, scope, primKey, roleId, actionId);
	}

	/**
	 * Adds the resource permission to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ResourcePermissionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePermission the resource permission
	 * @return the resource permission that was added
	 */
	public static ResourcePermission addResourcePermission(
		ResourcePermission resourcePermission) {

		return getService().addResourcePermission(resourcePermission);
	}

	/**
	 * Adds resources for the entity with the name and primary key string,
	 * always creating a resource at the individual scope and only creating
	 * resources at the group, group template, and company scope if such
	 * resources don't already exist.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user adding the resources
	 * @param name a name for the resource, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key string of the resource instance,
	 optionally an empty string if no instance exists
	 * @param portletActions whether to associate portlet actions with the
	 resource
	 */
	public static void addResourcePermissions(
			long companyId, long groupId, long userId, String name,
			String primKey, boolean portletActions,
			ServiceContext serviceContext)
		throws PortalException {

		getService().addResourcePermissions(
			companyId, groupId, userId, name, primKey, portletActions,
			serviceContext);
	}

	public static void addResourcePermissions(
			long companyId, long groupId, long userId, String name,
			String[] primKeys, boolean portletActions,
			ServiceContext serviceContext)
		throws PortalException {

		getService().addResourcePermissions(
			companyId, groupId, userId, name, primKeys, portletActions,
			serviceContext);
	}

	/**
	 * Grants the role permissions at the scope to perform the actions on all
	 * resources of the type. Existing actions are retained.
	 *
	 * <p>
	 * This method should only be used to add default permissions to existing
	 * resources en masse during upgrades or while verifying permissions. For
	 * example, this method could be used to grant site members individual scope
	 * permissions to view all blog posts.
	 * </p>
	 *
	 * @param resourceName the resource's name, which can be either a class name
	 or a portlet ID
	 * @param roleName the role's name
	 * @param scope the scope
	 * @param resourceActionBitwiseValue the bitwise IDs of the actions
	 */
	public static void addResourcePermissions(
		String resourceName, String roleName, int scope,
		long resourceActionBitwiseValue) {

		getService().addResourcePermissions(
			resourceName, roleName, scope, resourceActionBitwiseValue);
	}

	public static void copyModelResourcePermissions(
			long companyId, String name, long sourcePrimKey, long targetPrimKey)
		throws PortalException {

		getService().copyModelResourcePermissions(
			companyId, name, sourcePrimKey, targetPrimKey);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new resource permission with the primary key. Does not add the resource permission to the database.
	 *
	 * @param resourcePermissionId the primary key for the new resource permission
	 * @return the new resource permission
	 */
	public static ResourcePermission createResourcePermission(
		long resourcePermissionId) {

		return getService().createResourcePermission(resourcePermissionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the resource permission with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ResourcePermissionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePermissionId the primary key of the resource permission
	 * @return the resource permission that was removed
	 * @throws PortalException if a resource permission with the primary key could not be found
	 */
	public static ResourcePermission deleteResourcePermission(
			long resourcePermissionId)
		throws PortalException {

		return getService().deleteResourcePermission(resourcePermissionId);
	}

	/**
	 * Deletes the resource permission from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ResourcePermissionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePermission the resource permission
	 * @return the resource permission that was removed
	 */
	public static ResourcePermission deleteResourcePermission(
		ResourcePermission resourcePermission) {

		return getService().deleteResourcePermission(resourcePermission);
	}

	public static void deleteResourcePermissions(
			long companyId, String name, int scope)
		throws PortalException {

		getService().deleteResourcePermissions(companyId, name, scope);
	}

	/**
	 * Deletes all resource permissions at the scope to resources of the type.
	 * This method should not be confused with any of the
	 * <code>removeResourcePermission</code> methods, as its purpose is very
	 * different. This method should only be used for deleting resource
	 * permissions that refer to a resource when that resource is deleted. For
	 * example this method could be used to delete all individual scope
	 * permissions to a blog post when it is deleted.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 */
	public static void deleteResourcePermissions(
			long companyId, String name, int scope, long primKey)
		throws PortalException {

		getService().deleteResourcePermissions(companyId, name, scope, primKey);
	}

	/**
	 * Deletes all resource permissions at the scope to resources of the type.
	 * This method should not be confused with any of the
	 * <code>removeResourcePermission</code> methods, as its purpose is very
	 * different. This method should only be used for deleting resource
	 * permissions that refer to a resource when that resource is deleted. For
	 * example this method could be used to delete all individual scope
	 * permissions to a blog post when it is deleted.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 */
	public static void deleteResourcePermissions(
			long companyId, String name, int scope, String primKey)
		throws PortalException {

		getService().deleteResourcePermissions(companyId, name, scope, primKey);
	}

	public static void deleteResourcePermissions(String name) {
		getService().deleteResourcePermissions(name);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static ResourcePermission fetchResourcePermission(
		long resourcePermissionId) {

		return getService().fetchResourcePermission(resourcePermissionId);
	}

	public static ResourcePermission fetchResourcePermission(
		long companyId, String name, int scope, String primKey, long roleId) {

		return getService().fetchResourcePermission(
			companyId, name, scope, primKey, roleId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static Map<Long, Set<String>>
		getAvailableResourcePermissionActionIds(
			long companyId, String name, int scope, String primKey,
			java.util.Collection<String> actionIds) {

		return getService().getAvailableResourcePermissionActionIds(
			companyId, name, scope, primKey, actionIds);
	}

	/**
	 * Returns the intersection of action IDs the role has permission at the
	 * scope to perform on resources of the type.
	 *
	 * @param companyId he primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @param actionIds the action IDs
	 * @return the intersection of action IDs the role has permission at the
	 scope to perform on resources of the type
	 */
	public static List<String> getAvailableResourcePermissionActionIds(
			long companyId, String name, int scope, String primKey, long roleId,
			java.util.Collection<String> actionIds)
		throws PortalException {

		return getService().getAvailableResourcePermissionActionIds(
			companyId, name, scope, primKey, roleId, actionIds);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the resource permission with the primary key.
	 *
	 * @param resourcePermissionId the primary key of the resource permission
	 * @return the resource permission
	 * @throws PortalException if a resource permission with the primary key could not be found
	 */
	public static ResourcePermission getResourcePermission(
			long resourcePermissionId)
		throws PortalException {

		return getService().getResourcePermission(resourcePermissionId);
	}

	/**
	 * Returns the resource permission for the role at the scope to perform the
	 * actions on resources of the type.
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @return the resource permission for the role at the scope to perform the
	 actions on resources of the type
	 */
	public static ResourcePermission getResourcePermission(
			long companyId, String name, int scope, String primKey, long roleId)
		throws PortalException {

		return getService().getResourcePermission(
			companyId, name, scope, primKey, roleId);
	}

	/**
	 * Returns a range of all the resource permissions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of resource permissions
	 */
	public static List<ResourcePermission> getResourcePermissions(
		int start, int end) {

		return getService().getResourcePermissions(start, end);
	}

	public static List<ResourcePermission> getResourcePermissions(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId) {

		return getService().getResourcePermissions(
			companyId, name, scope, roleId, viewActionId);
	}

	/**
	 * Returns all the resource permissions at the scope of the type.
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @return the resource permissions at the scope of the type
	 */
	public static List<ResourcePermission> getResourcePermissions(
		long companyId, String name, int scope, String primKey) {

		return getService().getResourcePermissions(
			companyId, name, scope, primKey);
	}

	public static List<ResourcePermission> getResourcePermissions(String name) {
		return getService().getResourcePermissions(name);
	}

	/**
	 * Returns the number of resource permissions.
	 *
	 * @return the number of resource permissions
	 */
	public static int getResourcePermissionsCount() {
		return getService().getResourcePermissionsCount();
	}

	/**
	 * Returns the number of resource permissions at the scope of the type.
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @return the number of resource permissions at the scope of the type
	 */
	public static int getResourcePermissionsCount(
		long companyId, String name, int scope, String primKey) {

		return getService().getResourcePermissionsCount(
			companyId, name, scope, primKey);
	}

	/**
	 * Returns the resource permissions that apply to the resource.
	 *
	 * @param companyId the primary key of the resource's company
	 * @param groupId the primary key of the resource's group
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param primKey the primary key of the resource
	 * @return the resource permissions associated with the resource
	 */
	public static List<ResourcePermission> getResourceResourcePermissions(
		long companyId, long groupId, String name, String primKey) {

		return getService().getResourceResourcePermissions(
			companyId, groupId, name, primKey);
	}

	/**
	 * Returns all the resource permissions for the role.
	 *
	 * @param roleId the primary key of the role
	 * @return the resource permissions for the role
	 */
	public static List<ResourcePermission> getRoleResourcePermissions(
		long roleId) {

		return getService().getRoleResourcePermissions(roleId);
	}

	/**
	 * Returns a range of all the resource permissions for the role at the
	 * scopes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param roleId the primary key of the role
	 * @param scopes the scopes
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of resource permissions for the role at the scopes
	 */
	public static List<ResourcePermission> getRoleResourcePermissions(
		long roleId, int[] scopes, int start, int end) {

		return getService().getRoleResourcePermissions(
			roleId, scopes, start, end);
	}

	public static List<com.liferay.portal.kernel.model.Role> getRoles(
			long companyId, String name, int scope, String primKey,
			String actionId)
		throws PortalException {

		return getService().getRoles(companyId, name, scope, primKey, actionId);
	}

	/**
	 * Returns all the resource permissions where scope = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param scopes the scopes
	 * @return the resource permissions where scope = any &#63;
	 */
	public static List<ResourcePermission> getScopeResourcePermissions(
		int[] scopes) {

		return getService().getScopeResourcePermissions(scopes);
	}

	/**
	 * Returns <code>true</code> if the resource permission grants permission to
	 * perform the resource action. Note that this method does not ensure that
	 * the resource permission refers to the same type of resource as the
	 * resource action.
	 *
	 * @param resourcePermission the resource permission
	 * @param resourceAction the resource action
	 * @return <code>true</code> if the resource permission grants permission to
	 perform the resource action
	 */
	public static boolean hasActionId(
		ResourcePermission resourcePermission,
		com.liferay.portal.kernel.model.ResourceAction resourceAction) {

		return getService().hasActionId(resourcePermission, resourceAction);
	}

	/**
	 * Returns <code>true</code> if the roles have permission at the scope to
	 * perform the action on the resources.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param resources the resources
	 * @param roleIds the primary keys of the roles
	 * @param actionId the action ID
	 * @return <code>true</code> if any one of the roles has permission to
	 perform the action on any one of the resources;
	 <code>false</code> otherwise
	 */
	public static boolean hasResourcePermission(
			List<com.liferay.portal.kernel.model.Resource> resources,
			long[] roleIds, String actionId)
		throws PortalException {

		return getService().hasResourcePermission(resources, roleIds, actionId);
	}

	/**
	 * Returns <code>true</code> if the role has permission at the scope to
	 * perform the action on resources of the type.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @param actionId the action ID
	 * @return <code>true</code> if the role has permission to perform the
	 action on the resource; <code>false</code> otherwise
	 */
	public static boolean hasResourcePermission(
			long companyId, String name, int scope, String primKey, long roleId,
			String actionId)
		throws PortalException {

		return getService().hasResourcePermission(
			companyId, name, scope, primKey, roleId, actionId);
	}

	/**
	 * Returns <code>true</code> if the roles have permission at the scope to
	 * perform the action on resources of the type.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleIds the primary keys of the roles
	 * @param actionId the action ID
	 * @return <code>true</code> if any one of the roles has permission to
	 perform the action on the resource; <code>false</code> otherwise
	 */
	public static boolean hasResourcePermission(
			long companyId, String name, int scope, String primKey,
			long[] roleIds, String actionId)
		throws PortalException {

		return getService().hasResourcePermission(
			companyId, name, scope, primKey, roleIds, actionId);
	}

	/**
	 * Returns <code>true</code> if the role has permission at the scope to
	 * perform the action on the resource.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param roleId the primary key of the role
	 * @param actionId the action ID
	 * @return <code>true</code> if the role has permission to perform the
	 action on the resource; <code>false</code> otherwise
	 */
	public static boolean hasScopeResourcePermission(
			long companyId, String name, int scope, long roleId,
			String actionId)
		throws PortalException {

		return getService().hasScopeResourcePermission(
			companyId, name, scope, roleId, actionId);
	}

	public static void initDefaultModelResourcePermissions(
			long companyId, java.util.Collection<String> modelResources)
		throws PortalException {

		getService().initDefaultModelResourcePermissions(
			companyId, modelResources);
	}

	public static void initPortletDefaultPermissions(
			com.liferay.portal.kernel.model.Portlet portlet)
		throws PortalException {

		getService().initPortletDefaultPermissions(portlet);
	}

	/**
	 * Reassigns all the resource permissions from the source role to the
	 * destination role, and deletes the source role.
	 *
	 * @param fromRoleId the primary key of the source role
	 * @param toRoleId the primary key of the destination role
	 */
	public static void mergePermissions(long fromRoleId, long toRoleId)
		throws PortalException {

		getService().mergePermissions(fromRoleId, toRoleId);
	}

	/**
	 * Grants the role default permissions to all the resources of the type and
	 * at the scope stored in the resource permission, deletes the resource
	 * permission, and deletes the resource permission's role if it has no
	 * permissions remaining.
	 *
	 * @param resourcePermissionId the primary key of the resource permission
	 * @param toRoleId the primary key of the role
	 */
	public static void reassignPermissions(
			long resourcePermissionId, long toRoleId)
		throws PortalException {

		getService().reassignPermissions(resourcePermissionId, toRoleId);
	}

	/**
	 * Revokes permission at the scope from the role to perform the action on
	 * resources of the type. For example, this method could be used to revoke a
	 * group scope permission to edit blog posts.
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @param actionId the action ID
	 */
	public static void removeResourcePermission(
			long companyId, String name, int scope, String primKey, long roleId,
			String actionId)
		throws PortalException {

		getService().removeResourcePermission(
			companyId, name, scope, primKey, roleId, actionId);
	}

	/**
	 * Revokes all permissions at the scope from the role to perform the action
	 * on resources of the type. For example, this method could be used to
	 * revoke all individual scope permissions to edit blog posts from site
	 * members.
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param roleId the primary key of the role
	 * @param actionId the action ID
	 */
	public static void removeResourcePermissions(
			long companyId, String name, int scope, long roleId,
			String actionId)
		throws PortalException {

		getService().removeResourcePermissions(
			companyId, name, scope, roleId, actionId);
	}

	/**
	 * Updates the role's permissions at the scope, setting the actions that can
	 * be performed on resources of the type, also setting the owner of any
	 * newly created resource permissions. Existing actions are replaced.
	 *
	 * <p>
	 * This method can be used to set permissions at any scope, but it is
	 * generally only used at the individual scope. For example, it could be
	 * used to set the guest permissions on a blog post.
	 * </p>
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @param ownerId the primary key of the owner (generally the user that
	 created the resource)
	 * @param actionIds the action IDs of the actions
	 */
	public static void setOwnerResourcePermissions(
			long companyId, String name, int scope, String primKey, long roleId,
			long ownerId, String[] actionIds)
		throws PortalException {

		getService().setOwnerResourcePermissions(
			companyId, name, scope, primKey, roleId, ownerId, actionIds);
	}

	/**
	 * Updates the role's permissions at the scope, setting the actions that can
	 * be performed on resources of the type. Existing actions are replaced.
	 *
	 * <p>
	 * This method can be used to set permissions at any scope, but it is
	 * generally only used at the individual scope. For example, it could be
	 * used to set the guest permissions on a blog post.
	 * </p>
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleId the primary key of the role
	 * @param actionIds the action IDs of the actions
	 */
	public static void setResourcePermissions(
			long companyId, String name, int scope, String primKey, long roleId,
			String[] actionIds)
		throws PortalException {

		getService().setResourcePermissions(
			companyId, name, scope, primKey, roleId, actionIds);
	}

	/**
	 * Updates the role's permissions at the scope, setting the actions that can
	 * be performed on resources of the type. Existing actions are replaced.
	 *
	 * <p>
	 * This method can be used to set permissions at any scope, but it is
	 * generally only used at the individual scope. For example, it could be
	 * used to set the guest permissions on a blog post.
	 * </p>
	 *
	 * <p>
	 * Depending on the scope, the value of <code>primKey</code> will have
	 * different meanings. For more information, see {@link
	 * com.liferay.portal.model.impl.ResourcePermissionImpl}.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param name the resource's name, which can be either a class name or a
	 portlet ID
	 * @param scope the scope
	 * @param primKey the primary key
	 * @param roleIdsToActionIds a map of role IDs to action IDs of the actions
	 */
	public static void setResourcePermissions(
			long companyId, String name, int scope, String primKey,
			Map<Long, String[]> roleIdsToActionIds)
		throws PortalException {

		getService().setResourcePermissions(
			companyId, name, scope, primKey, roleIdsToActionIds);
	}

	/**
	 * Updates the resources for the model, replacing their group and guest
	 * permissions with new ones from the service context.
	 *
	 * @param auditedModel the model associated with the resources
	 * @param serviceContext the service context to be applied. Can set group
	 and guest permissions.
	 */
	public static void updateModelResourcePermissions(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			ServiceContext serviceContext)
		throws PortalException {

		getService().updateModelResourcePermissions(
			auditedModel, serviceContext);
	}

	/**
	 * Updates the resource permission in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ResourcePermissionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePermission the resource permission
	 * @return the resource permission that was updated
	 */
	public static ResourcePermission updateResourcePermission(
		ResourcePermission resourcePermission) {

		return getService().updateResourcePermission(resourcePermission);
	}

	/**
	 * Updates resources matching the group, name, and primary key at the
	 * individual scope, setting new group and guest permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key of the resource instance
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	public static void updateResourcePermissions(
			long companyId, long groupId, String name, long primKey,
			String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		getService().updateResourcePermissions(
			companyId, groupId, name, primKey, groupPermissions,
			guestPermissions);
	}

	public static void updateResourcePermissions(
			long companyId, long groupId, String name, String primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().updateResourcePermissions(
			companyId, groupId, name, primKey, modelPermissions);
	}

	public static void updateResourcePermissions(
			long companyId, long groupId, String name, String primKey,
			String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		getService().updateResourcePermissions(
			companyId, groupId, name, primKey, groupPermissions,
			guestPermissions);
	}

	public static void updateResourcePermissions(
		long companyId, String name, int scope, String primKey,
		String newPrimKey) {

		getService().updateResourcePermissions(
			companyId, name, scope, primKey, newPrimKey);
	}

	public static ResourcePermissionLocalService getService() {
		return _service;
	}

	public static void setService(ResourcePermissionLocalService service) {
		_service = service;
	}

	private static volatile ResourcePermissionLocalService _service;

}