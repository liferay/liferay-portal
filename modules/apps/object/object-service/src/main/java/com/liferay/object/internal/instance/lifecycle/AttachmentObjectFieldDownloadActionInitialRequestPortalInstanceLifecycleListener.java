/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.instance.lifecycle;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.definition.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class
	AttachmentObjectFieldDownloadActionInitialRequestPortalInstanceLifecycleListener
		extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				companyId, WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			List<ObjectField> objectFields =
				_objectFieldLocalService.getObjectFieldsByBusinessType(
					objectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

			List<ResourcePermission> resourcePermissions = null;

			for (ObjectField objectField : objectFields) {
				String attachmentDownloadActionKey =
					objectField.getAttachmentDownloadActionKey();

				ResourceAction resourceAction =
					_resourceActionLocalService.fetchResourceAction(
						objectDefinition.getClassName(),
						attachmentDownloadActionKey);

				if (resourceAction != null) {
					continue;
				}

				try {
					ObjectDefinitionResourcePermissionUtil.
						populateResourceActions(
							null, null, objectDefinition,
							_objectFieldLocalService,
							Collections.singletonList(objectField),
							_portletLocalService, _resourceActions);

					_objectFieldLocalService.addOrUpdateObjectFieldPLOEntries(
						objectField);

					if (resourcePermissions == null) {
						resourcePermissions =
							_resourcePermissionLocalService.
								getResourcePermissions(
									objectDefinition.getCompanyId(),
									objectDefinition.getClassName(),
									ResourceConstants.SCOPE_INDIVIDUAL);
					}

					for (ResourcePermission resourcePermission :
							resourcePermissions) {

						if (!resourcePermission.hasActionId(
								attachmentDownloadActionKey) &&
							resourcePermission.isViewActionId()) {

							resourcePermission.addResourceAction(
								attachmentDownloadActionKey);

							_resourcePermissionLocalService.
								updateResourcePermission(resourcePermission);
						}
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AttachmentObjectFieldDownloadActionInitialRequestPortalInstanceLifecycleListener.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}