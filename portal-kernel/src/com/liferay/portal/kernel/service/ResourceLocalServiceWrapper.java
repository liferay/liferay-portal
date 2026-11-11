/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

/**
 * Provides a wrapper for {@link ResourceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ResourceLocalService
 * @generated
 */
public class ResourceLocalServiceWrapper
	implements ResourceLocalService, ServiceWrapper<ResourceLocalService> {

	public ResourceLocalServiceWrapper() {
		this(null);
	}

	public ResourceLocalServiceWrapper(
		ResourceLocalService resourceLocalService) {

		_resourceLocalService = resourceLocalService;
	}

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
	@Override
	public void addModelResources(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addModelResources(auditedModel, serviceContext);
	}

	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, java.lang.String name,
			long primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addModelResources(
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
	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, java.lang.String name,
			long primKey, java.lang.String[] groupPermissions,
			java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addModelResources(
			companyId, groupId, userId, name, primKey, groupPermissions,
			guestPermissions);
	}

	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, java.lang.String name,
			java.lang.String primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addModelResources(
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
	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, java.lang.String name,
			java.lang.String primKey, java.lang.String[] groupPermissions,
			java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addModelResources(
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
	@Override
	public void addResources(
			long companyId, long groupId, long userId, java.lang.String name,
			long primKey, boolean portletActions, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addResources(
			companyId, groupId, userId, name, primKey, portletActions,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addResources(
			long companyId, long groupId, long userId, java.lang.String name,
			long primKey, boolean portletActions,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addResources(
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
	@Override
	public void addResources(
			long companyId, long groupId, long userId, java.lang.String name,
			java.lang.String primKey, boolean portletActions,
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addResources(
			companyId, groupId, userId, name, primKey, portletActions,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addResources(
			long companyId, long groupId, long userId, java.lang.String name,
			java.lang.String[] primKeys, boolean portletActions,
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addResources(
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
	@Override
	public void addResources(
			long companyId, long groupId, java.lang.String name,
			boolean portletActions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.addResources(
			companyId, groupId, name, portletActions);
	}

	@Override
	public void copyModelResources(
		long companyId, java.lang.String name, long sourcePrimKey,
		long targetPrimKey) {

		_resourceLocalService.copyModelResources(
			companyId, name, sourcePrimKey, targetPrimKey);
	}

	/**
	 * Deletes the resource associated with the model at the scope.
	 *
	 * @param auditedModel the model associated with the resource
	 * @param scope the scope of the resource. For more information see {@link
	 ResourceConstants}.
	 */
	@Override
	public void deleteResource(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			int scope)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.deleteResource(auditedModel, scope);
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
	@Override
	public void deleteResource(
			long companyId, java.lang.String name, int scope, long primKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.deleteResource(companyId, name, scope, primKey);
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
	@Override
	public void deleteResource(
			long companyId, java.lang.String name, int scope,
			java.lang.String primKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.deleteResource(companyId, name, scope, primKey);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _resourceLocalService.getOSGiServiceIdentifier();
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
	@Override
	public com.liferay.portal.kernel.model.Resource getResource(
		long companyId, java.lang.String name, int scope,
		java.lang.String primKey) {

		return _resourceLocalService.getResource(
			companyId, name, scope, primKey);
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
	@Override
	public boolean hasUserPermissions(
			long userId, long resourceId,
			java.util.List<com.liferay.portal.kernel.model.Resource> resources,
			java.lang.String actionId, long[] roleIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _resourceLocalService.hasUserPermissions(
			userId, resourceId, resources, actionId, roleIds);
	}

	@Override
	public void removeResource(
			long companyId, java.lang.String name, int scope,
			java.lang.String primKey, long roleId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.removeResource(
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
	@Override
	public void updateModelResources(
			com.liferay.portal.kernel.model.AuditedModel auditedModel,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.updateModelResources(
			auditedModel, serviceContext);
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
	@Override
	public void updateResources(
			long companyId, long groupId, java.lang.String name, long primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.updateResources(
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
	@Override
	public void updateResources(
			long companyId, long groupId, java.lang.String name, long primKey,
			java.lang.String[] groupPermissions,
			java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.updateResources(
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
	@Override
	public void updateResources(
			long companyId, long groupId, java.lang.String name,
			java.lang.String primKey,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.updateResources(
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
	@Override
	public void updateResources(
			long companyId, long groupId, java.lang.String name,
			java.lang.String primKey, java.lang.String[] groupPermissions,
			java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_resourceLocalService.updateResources(
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
	@Override
	public void updateResources(
		long companyId, java.lang.String name, int scope,
		java.lang.String primKey, java.lang.String newPrimKey) {

		_resourceLocalService.updateResources(
			companyId, name, scope, primKey, newPrimKey);
	}

	@Override
	public ResourceLocalService getWrappedService() {
		return _resourceLocalService;
	}

	@Override
	public void setWrappedService(ResourceLocalService resourceLocalService) {
		_resourceLocalService = resourceLocalService;
	}

	private ResourceLocalService _resourceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1279640998