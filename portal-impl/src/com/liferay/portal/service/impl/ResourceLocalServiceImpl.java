/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.model.impl.ResourceImpl;
import com.liferay.portal.service.base.ResourceLocalServiceBaseImpl;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;

/**
 * Provides the local service for accessing, adding, and updating resources.
 *
 * <p>
 * Permissions in Liferay are defined for resource/action pairs. Some resources,
 * known as portlet resources, define actions that the end-user can perform with
 * respect to a portlet window. Other resources, known as model resources,
 * define actions that the end-user can perform with respect to the
 * service/persistence layer.
 * </p>
 *
 * <p>
 * On creating an entity instance, you should create resources for it. The
 * following example demonstrates adding resources for an instance of a model
 * entity named <code>SomeWidget</code>. The IDs of the actions permitted for
 * the group and guests are passed in from the service context.
 * </p>
 *
 * <p>
 * <pre>
 * <code>
 * resourceLocalService.addModelResources(
 * 		SomeWidget.getCompanyId(), SomeWidget.getGroupId(), userId,
 * 		SomeWidget.class.getName(), SomeWidget.getPrimaryKey(),
 * 		serviceContext.getGroupPermissions, serviceContext.getGuestPermissions);
 * </code>
 * </pre></p>
 *
 * <p>
 * Just prior to deleting an entity instance, you should delete its resource at
 * the individual scope. The following example demonstrates deleting a resource
 * associated with the <code>SomeWidget</code> model entity at the scope
 * individual scope.
 * </p>
 *
 * <p>
 * <pre>
 * <code>
 * resourceLocalService.deleteResource(
 * 		SomeWidget.getCompanyId(), SomeWidget.class.getName(),
 * 		ResourceConstants.SCOPE_INDIVIDUAL, SomeWidget.getPrimaryKey());
 * </code>
 * </pre></p>
 *
 * @author Brian Wing Shun Chan
 * @author Wilson S. Man
 * @author Raymond Augé
 * @author Julio Camarero
 * @author Connor McKay
 */
@Transactional(enabled = false)
public class ResourceLocalServiceImpl extends ResourceLocalServiceBaseImpl {

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
	 *        add the model's default group and guest permissions, set whether
	 *        to derive default group and guest permissions from the model, set
	 *        group permissions to apply, and set guest permissions to apply.
	 */
	@Override
	public void addModelResources(
			AuditedModel auditedModel, ServiceContext serviceContext)
		throws PortalException {

		_resourcePermissionLocalService.addModelResourcePermissions(
			auditedModel, serviceContext);
	}

	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, String name,
			long primKey, ModelPermissions modelPermissions)
		throws PortalException {

		_resourcePermissionLocalService.addModelResourcePermissions(
			companyId, groupId, userId, name, String.valueOf(primKey),
			modelPermissions);
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
	 *        <code>0</code> if no instance exists
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, String name,
			long primKey, String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		_resourcePermissionLocalService.addModelResourcePermissions(
			companyId, groupId, userId, name, String.valueOf(primKey),
			groupPermissions, guestPermissions);
	}

	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, String name,
			String primKey, ModelPermissions modelPermissions)
		throws PortalException {

		_resourcePermissionLocalService.addModelResourcePermissions(
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
	 *        an empty string if no instance exists
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	@Override
	public void addModelResources(
			long companyId, long groupId, long userId, String name,
			String primKey, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		_resourcePermissionLocalService.addModelResourcePermissions(
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
	 *        resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key of the resource instance, optionally
	 *        <code>0</code> if no instance exists
	 * @param portletActions whether to associate portlet actions with the
	 *        resource
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 */
	@Override
	public void addResources(
			long companyId, long groupId, long userId, String name,
			long primKey, boolean portletActions, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(addGroupPermissions);
		serviceContext.setAddGuestPermissions(addGuestPermissions);

		addResources(
			companyId, groupId, userId, name, primKey, portletActions,
			serviceContext);
	}

	@Override
	public void addResources(
			long companyId, long groupId, long userId, String name,
			long primKey, boolean portletActions, ServiceContext serviceContext)
		throws PortalException {

		_resourcePermissionLocalService.addResourcePermissions(
			companyId, groupId, userId, name, String.valueOf(primKey),
			portletActions, serviceContext);
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
	 *        resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key string of the resource instance,
	 *        optionally an empty string if no instance exists
	 * @param portletActions whether to associate portlet actions with the
	 *        resource
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 */
	@Override
	public void addResources(
			long companyId, long groupId, long userId, String name,
			String primKey, boolean portletActions, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(addGroupPermissions);
		serviceContext.setAddGuestPermissions(addGuestPermissions);

		_resourcePermissionLocalService.addResourcePermissions(
			companyId, groupId, userId, name, primKey, portletActions,
			serviceContext);
	}

	@Override
	public void addResources(
			long companyId, long groupId, long userId, String name,
			String[] primKeys, boolean portletActions,
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(addGroupPermissions);
		serviceContext.setAddGuestPermissions(addGuestPermissions);

		_resourcePermissionLocalService.addResourcePermissions(
			companyId, groupId, userId, name, primKeys, portletActions,
			serviceContext);
	}

	/**
	 * Adds resources for the entity with the name. Use this method if the user
	 * is unknown or irrelevant and there is no current entity instance.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name a name for the resource, which should be a portlet ID if the
	 *        resource is a portlet or the resource's class name otherwise
	 * @param portletActions whether to associate portlet actions with the
	 *        resource
	 */
	@Override
	public void addResources(
			long companyId, long groupId, String name, boolean portletActions)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		_resourcePermissionLocalService.addResourcePermissions(
			companyId, groupId, 0, name, (String)null, portletActions,
			serviceContext);
	}

	@Override
	public void copyModelResources(
		long companyId, String name, long sourcePrimKey, long targetPrimKey) {

		_resourcePermissionLocalService.copyModelResourcePermissions(
			companyId, name, sourcePrimKey, targetPrimKey);
	}

	/**
	 * Deletes the resource associated with the model at the scope.
	 *
	 * @param auditedModel the model associated with the resource
	 * @param scope the scope of the resource. For more information see {@link
	 *        ResourceConstants}.
	 */
	@Override
	public void deleteResource(AuditedModel auditedModel, int scope)
		throws PortalException {

		_resourcePermissionLocalService.deleteResourcePermissions(
			auditedModel.getCompanyId(), auditedModel.getModelClassName(),
			scope, String.valueOf(auditedModel.getPrimaryKeyObj()));
	}

	/**
	 * Deletes the resource matching the primary key at the scope.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param name the resource's name, which should be a portlet ID if the
	 *        resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 *        ResourceConstants}.
	 * @param primKey the primary key of the resource instance
	 */
	@Override
	public void deleteResource(
			long companyId, String name, int scope, long primKey)
		throws PortalException {

		_resourcePermissionLocalService.deleteResourcePermissions(
			companyId, name, scope, primKey);
	}

	/**
	 * Deletes the resource matching the primary key at the scope.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param name the resource's name, which should be a portlet ID if the
	 *        resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 *        ResourceConstants}.
	 * @param primKey the primary key string of the resource instance
	 */
	@Override
	public void deleteResource(
			long companyId, String name, int scope, String primKey)
		throws PortalException {

		_resourcePermissionLocalService.deleteResourcePermissions(
			companyId, name, scope, primKey);
	}

	/**
	 * Returns a new resource with the name and primary key at the scope.
	 *
	 * @param  companyId the primary key of the portal instance
	 * @param  name a name for the resource, which should be a portlet ID if the
	 *         resource is a portlet or the resource's class name otherwise
	 * @param  scope the scope of the resource. For more information see {@link
	 *         ResourceConstants}.
	 * @param  primKey the primary key string of the resource
	 * @return the new resource
	 */
	@Override
	public Resource getResource(
		long companyId, String name, int scope, String primKey) {

		Resource resource = new ResourceImpl();

		resource.setCompanyId(companyId);
		resource.setName(name);
		resource.setScope(scope);
		resource.setPrimKey(primKey);

		return resource;
	}

	/**
	 * Returns <code>true</code> if the roles have permission to perform the
	 * action on the resources.
	 *
	 * @param  userId the primary key of the user performing the permission
	 *         check
	 * @param  resourceId the primary key of the resource, typically the scope
	 *         group ID representing the scope in which the permission check is
	 *         being performed
	 * @param  resources the resources for which permissions are to be checked
	 * @param  actionId the primary key of the action to be performed on the
	 *         resources
	 * @param  roleIds the primary keys of the roles
	 * @return <code>true</code> if the roles have permission to perform the
	 *         action on the resources;<code>false</code> otherwise
	 */
	@Override
	public boolean hasUserPermissions(
			long userId, long resourceId, List<Resource> resources,
			String actionId, long[] roleIds)
		throws PortalException {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		boolean hasUserPermissions =
			_resourcePermissionLocalService.hasResourcePermission(
				resources, roleIds, actionId);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Checking user permissions for ", userId, " ", resourceId,
					" ", actionId, " takes ", stopWatch.getTime(), " ms"));
		}

		return hasUserPermissions;
	}

	@Override
	public void removeResource(
			long companyId, String name, int scope, String primKey, long roleId,
			String actionId)
		throws PortalException {

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, name, scope, primKey, roleId, actionId);
	}

	/**
	 * Updates the resources for the model, replacing their group and guest
	 * permissions with new ones from the service context.
	 *
	 * @param auditedModel the model associated with the resources
	 * @param serviceContext the service context to be applied. Can set group
	 *        and guest permissions.
	 */
	@Override
	public void updateModelResources(
			AuditedModel auditedModel, ServiceContext serviceContext)
		throws PortalException {

		_resourcePermissionLocalService.updateModelResourcePermissions(
			auditedModel, serviceContext);
	}

	/**
	 * Updates resources matching the group, name, and primary key at the
	 * individual scope, setting new permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 *        resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key of the resource instance
	 * @param modelPermissions the model permissions to be applied
	 */
	@Override
	public void updateResources(
			long companyId, long groupId, String name, long primKey,
			ModelPermissions modelPermissions)
		throws PortalException {

		_resourcePermissionLocalService.updateResourcePermissions(
			companyId, groupId, name, String.valueOf(primKey),
			modelPermissions);
	}

	/**
	 * Updates resources matching the group, name, and primary key at the
	 * individual scope, setting new group and guest permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 *        resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key of the resource instance
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	@Override
	public void updateResources(
			long companyId, long groupId, String name, long primKey,
			String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		_resourcePermissionLocalService.updateResourcePermissions(
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
	 *        resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key string of the resource instance
	 * @param modelPermissions the model permissions to be applied
	 */
	@Override
	public void updateResources(
			long companyId, long groupId, String name, String primKey,
			ModelPermissions modelPermissions)
		throws PortalException {

		_resourcePermissionLocalService.updateResourcePermissions(
			companyId, groupId, name, primKey, modelPermissions);
	}

	/**
	 * Updates resources matching the group, name, and primary key string at the
	 * individual scope, setting new group and guest permissions.
	 *
	 * @param companyId the primary key of the portal instance
	 * @param groupId the primary key of the group
	 * @param name the resource's name, which should be a portlet ID if the
	 *        resource is a portlet or the resource's class name otherwise
	 * @param primKey the primary key string of the resource instance
	 * @param groupPermissions the group permissions to be applied
	 * @param guestPermissions the guest permissions to be applied
	 */
	@Override
	public void updateResources(
			long companyId, long groupId, String name, String primKey,
			String[] groupPermissions, String[] guestPermissions)
		throws PortalException {

		_resourcePermissionLocalService.updateResourcePermissions(
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
	 *        resource is a portlet or the resource's class name otherwise
	 * @param scope the scope of the resource. For more information see {@link
	 *        ResourceConstants}.
	 * @param primKey the primary key string of the resource instance
	 * @param newPrimKey the new primary key string of the resource
	 */
	@Override
	public void updateResources(
		long companyId, String name, int scope, String primKey,
		String newPrimKey) {

		_resourcePermissionLocalService.updateResourcePermissions(
			companyId, name, scope, primKey, newPrimKey);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceLocalServiceImpl.class);

	@BeanReference(type = ResourcePermissionLocalService.class)
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}