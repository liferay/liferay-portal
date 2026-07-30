/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.object.deployer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cms.site.initializer.internal.security.permission.resource.CMSDefaultPermissionObjectEntryModelResourcePermission;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ObjectDefinitionDeployer.class)
public class ObjectDefinitionDeployerImpl implements ObjectDefinitionDeployer {

	@Override
	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition) {

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_CMS_DEFAULT_PERMISSION")) {

			return Collections.emptyList();
		}

		String className = objectDefinition.getClassName();

		try {
			Role role = _roleLocalService.fetchRole(
				objectDefinition.getCompanyId(), RoleConstants.USER);

			if (role != null) {
				ResourcePermission resourcePermission =
					_resourcePermissionLocalService.fetchResourcePermission(
						objectDefinition.getCompanyId(), className,
						ResourceConstants.SCOPE_COMPANY,
						String.valueOf(objectDefinition.getCompanyId()),
						role.getRoleId());

				if (resourcePermission == null) {
					_resourcePermissionLocalService.addResourcePermission(
						objectDefinition.getCompanyId(), className,
						ResourceConstants.SCOPE_COMPANY,
						String.valueOf(objectDefinition.getCompanyId()),
						role.getRoleId(), ActionKeys.VIEW);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		Snapshot<ModelResourcePermission<ObjectEntry>>
			modelResourcePermissionSnapshot = new Snapshot<>(
				CMSDefaultPermissionObjectEntryModelResourcePermission.class,
				Snapshot.cast(ModelResourcePermission.class),
				StringBundler.concat(
					"(&(com.liferay.object=true)(model.class.name=", className,
					"))"));

		return ListUtil.fromArray(
			_bundleContext.registerService(
				ModelResourcePermission.class,
				new CMSDefaultPermissionObjectEntryModelResourcePermission(
					className, _depotEntryLocalService, _groupLocalService,
					modelResourcePermissionSnapshot,
					_objectEntryFolderLocalService, _objectEntryLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", className
				).put(
					"service.ranking", Integer.valueOf(200)
				).build()));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionDeployerImpl.class);

	private BundleContext _bundleContext;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}