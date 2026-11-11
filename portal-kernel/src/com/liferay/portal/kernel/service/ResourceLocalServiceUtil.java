/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

/**
 * Provides the local service utility for Resource. This utility wraps
 * <code>com.liferay.portal.service.impl.ResourceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ResourceLocalService
 * @generated
 */
public class ResourceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.ResourceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds resources for the model, always creating a resource at the
	 * individual scope and only creating resources at the group, group
	 * template, and company scope if such resources don't already exist.
	 *
	 * <ol>
	 * <li>
	 * If the service context specifies that default group or default guest
	 * permissions are to be added, then only default permissions are added. See
	 * {@link ServiceContext#setAddGroupPermissions(
	 * boolean)} and {@link
	 * ServiceContext#setAddGuestPermissions(
	 * boolean)}.
	 * </li>
	 * <li>
	 * Else ...
	 * <ol>
	 * <li>
	 * If the service context specifies to derive default permissions, then
	 * default group and guest permissions are derived from the model and
	 * added. See {@link
	 * ServiceContext#setDeriveDefaultPermissions(
	 * boolean)}.
	 * </li>
	 * <li>
	 * Lastly group and guest permissions from the service
	 * context are applied. See {@link
	 * ServiceContext#setGroupPermissions(String[])}
	 * and {@link
	 * ServiceContext#setGuestPermissions(String[])}.
	 * </li>
	 * </ol>
	 *
	 * </li>
	 * </ol>
	 *
	 * @param auditedModel the model to associate with the resources
	 * @param serviceContext the service context to apply. Can set whether to
	 add the model's default group and guest permissions, set whether
	 to derive default group and guest permissions from the model, set
	 group permissions to apply, and set guest permissions to apply.
	 */
	public static void addModelResources(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			ServiceContext serviceContext)
		throws PortalException {

		getService().addModelResources(auditedModel, serviceContext);
	}

	public static void addModelResources(
			long companyId, long groupId, long userId, String name,
			long primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addModelResources(
			companyId, groupId, userId, name, primKey, modelPermissions);
	}

	/**
	 * Adds resources for the model with the name and primary key, always
	 * creating a resource at the individual scope and only creating resources
	 * at the group, group template, and company scope if such resources don't
	 * already exist.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user adding the resources
	 * @param name a name for the resource, typically the model's class name
	 * @param primKey the primary key of the model instance, optionally
	 <code>0</code> if no instance exists
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	public static void addModelResources(
			long companyId, long groupId, long userId, String name,
			long primKey, String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		getService().addModelResources(
			companyId, groupId, userId, name, primKey, groupPermissions,
			guestPermissions);
	}

	public static void addModelResources(
			long companyId, long groupId, long userId, String name,
			String primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addModelResources(
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
	public static void addModelResources(
			long companyId, long groupId, long userId, String name,
			String primKey, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		getService().addModelResources(
			companyId, groupId, userId, name, primKey, groupPermissions,
			guestPermissions);
	}

	/**
	 * Adds resources for the entity with the name and primary key, always
	 * creating a resource at the individual scope and only creating resources
	 * at the group, group template, and company scope if such resources don't
	 * already exist.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user adding the resources
	 * @param name a name for the resource, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key of the resource instance, optionally
	 <code>0</code> if no instance exists
	 * @param portletActions whether to associate portlet actions with the
	 resource
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 */
	public static void addResources(
			long companyId, long groupId, long userId, String name,
			long primKey, boolean portletActions, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addResources(
			companyId, groupId, userId, name, primKey, portletActions,
			addGroupPermissions, addGuestPermissions);
	}

	public static void addResources(
			long companyId, long groupId, long userId, String name,
			long primKey, boolean portletActions, ServiceContext serviceContext)
		throws PortalException {

		getService().addResources(
			companyId, groupId, userId, name, primKey, portletActions,
			serviceContext);
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
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 */
	public static void addResources(
			long companyId, long groupId, long userId, String name,
			String primKey, boolean portletActions, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addResources(
			companyId, groupId, userId, name, primKey, portletActions,
			addGroupPermissions, addGuestPermissions);
	}

	public static void addResources(
			long companyId, long groupId, long userId, String name,
			String[] primKeys, boolean portletActions,
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws PortalException {

		getService().addResources(
			companyId, groupId, userId, name, primKeys, portletActions,
			addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds resources for the entity with the name. Use this method if the user
	 * is unknown or irrelevant and there is no current entity instance.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name a name for the resource, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param portletActions whether to associate portlet actions with the
	 resource
	 */
	public static void addResources(
			long companyId, long groupId, String name, boolean portletActions)
		throws PortalException {

		getService().addResources(companyId, groupId, name, portletActions);
	}

	public static void copyModelResources(
		long companyId, String name, long sourcePrimKey, long targetPrimKey) {

		getService().copyModelResources(
			companyId, name, sourcePrimKey, targetPrimKey);
	}

	/**
	 * Deletes the resource associated with the model at the scope.
	 *
	 * @param auditedModel the model associated with the resource
	 * @param scope the scope of the resource. For more information see {@link
	 ResourceConstants}.
	 */
	public static void deleteResource(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			int scope)
		throws PortalException {

		getService().deleteResource(auditedModel, scope);
	}

	/**
	 * Deletes the resource matching the primary key at the scope.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 ResourceConstants}.
	 * @param primKey the primary key of the resource instance
	 */
	public static void deleteResource(
			long companyId, String name, int scope, long primKey)
		throws PortalException {

		getService().deleteResource(companyId, name, scope, primKey);
	}

	/**
	 * Deletes the resource matching the primary key at the scope.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 ResourceConstants}.
	 * @param primKey the primary key string of the resource instance
	 */
	public static void deleteResource(
			long companyId, String name, int scope, String primKey)
		throws PortalException {

		getService().deleteResource(companyId, name, scope, primKey);
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
	 * Returns a new resource with the name and primary key at the scope.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param name a name for the resource, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 ResourceConstants}.
	 * @param primKey the primary key string of the resource
	 * @return the new resource
	 */
	public static com.liferay.portal.kernel.model.Resource getResource(
		long companyId, String name, int scope, String primKey) {

		return getService().getResource(companyId, name, scope, primKey);
	}

	/**
	 * Returns <code>true</code> if the roles have permission to perform the
	 * action on the resources.
	 *
	 * @param userId the primary key of the user performing the permission
	 check
	 * @param resourceId the primary key of the resource, typically the scope
	 group ID representing the scope in which the permission check is
	 being performed
	 * @param resources the resources for which permissions are to be checked
	 * @param actionId the primary key of the action to be performed on the
	 resources
	 * @param roleIds the primary keys of the roles
	 * @return <code>true</code> if the roles have permission to perform the
	 action on the resources;<code>false</code> otherwise
	 */
	public static boolean hasUserPermissions(
			long userId, long resourceId,
			List<com.liferay.portal.kernel.model.Resource> resources,
			String actionId, long[] roleIds)
		throws PortalException {

		return getService().hasUserPermissions(
			userId, resourceId, resources, actionId, roleIds);
	}

	public static void removeResource(
			long companyId, String name, int scope, String primKey, long roleId,
			String actionId)
		throws PortalException {

		getService().removeResource(
			companyId, name, scope, primKey, roleId, actionId);
	}

	/**
	 * Updates the resources for the model, replacing their group and guest
	 * permissions with new ones from the service context.
	 *
	 * @param auditedModel the model associated with the resources
	 * @param serviceContext the service context to be applied. Can set group
	 and guest permissions.
	 */
	public static void updateModelResources(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			ServiceContext serviceContext)
		throws PortalException {

		getService().updateModelResources(auditedModel, serviceContext);
	}

	/**
	 * Updates resources matching the group, name, and primary key at the
	 * individual scope, setting new permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key of the resource instance
	 * @param modelPermissions the model permissions to be applied
	 */
	public static void updateResources(
			long companyId, long groupId, String name, long primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().updateResources(
			companyId, groupId, name, primKey, modelPermissions);
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
	public static void updateResources(
			long companyId, long groupId, String name, long primKey,
			String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		getService().updateResources(
			companyId, groupId, name, primKey, groupPermissions,
			guestPermissions);
	}

	/**
	 * Updates resources matching the group, name, and primary key string at the
	 * individual scope, setting new permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key string of the resource instance
	 * @param modelPermissions the model permissions to be applied
	 */
	public static void updateResources(
			long companyId, long groupId, String name, String primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().updateResources(
			companyId, groupId, name, primKey, modelPermissions);
	}

	/**
	 * Updates resources matching the group, name, and primary key string at the
	 * individual scope, setting new group and guest permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key string of the resource instance
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	public static void updateResources(
			long companyId, long groupId, String name, String primKey,
			String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		getService().updateResources(
			companyId, groupId, name, primKey, groupPermissions,
			guestPermissions);
	}

	/**
	 * Updates resources matching the name, primary key string and scope,
	 * replacing the primary key of their resource permissions with the new
	 * primary key.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param name the resource's name, which should be a portlet ID if the
	 resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 ResourceConstants}.
	 * @param primKey the primary key string of the resource instance
	 * @param newPrimKey the new primary key string of the resource
	 */
	public static void updateResources(
		long companyId, String name, int scope, String primKey,
		String newPrimKey) {

		getService().updateResources(
			companyId, name, scope, primKey, newPrimKey);
	}

	public static ResourceLocalService getService() {
		return _service;
	}

	public static void setService(ResourceLocalService service) {
		_service = service;
	}

	private static volatile ResourceLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1636999757