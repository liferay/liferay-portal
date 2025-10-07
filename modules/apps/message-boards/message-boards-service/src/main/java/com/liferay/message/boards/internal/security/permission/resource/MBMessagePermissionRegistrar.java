/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.security.permission.resource;

import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.WorkflowedModelPermissionLogic;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = {})
public class MBMessagePermissionRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"model.class.name", MBMessage.class.getName()
			).build();

		_serviceRegistration = bundleContext.registerService(
			(Class<ModelResourcePermission<MBMessage>>)
				(Class<?>)ModelResourcePermission.class,
			ModelResourcePermissionFactory.create(
				MBMessage.class, MBMessage::getMessageId,
				(Long resourcePrimKey) -> {
					MBThread mbThread = _mbThreadLocalService.fetchThread(
						resourcePrimKey);

					if (mbThread == null) {
						return _mbMessageLocalService.getMessage(
							resourcePrimKey);
					}

					return _mbMessageLocalService.getMessage(
						mbThread.getRootMessageId());
				},
				_portletResourcePermission,
				(modelResourcePermission, consumer) -> {
					consumer.accept(
						(permissionChecker, name, message, actionId) -> {
							if (_mbBanLocalService.hasBan(
									message.getGroupId(),
									permissionChecker.getUserId())) {

								return false;
							}

							MBThread thread = message.getThread();

							if (!message.isRoot() &&
								!modelResourcePermission.contains(
									permissionChecker,
									thread.getRootMessageId(),
									ActionKeys.VIEW)) {

								return false;
							}

							return null;
						});
					consumer.accept(
						new StagedModelPermissionLogic<>(
							_stagingPermission, MBPortletKeys.MESSAGE_BOARDS,
							MBMessage::getMessageId));
					consumer.accept(
						new WorkflowedModelPermissionLogic<>(
							modelResourcePermission, _groupLocalService,
							MBMessage::getMessageId));

					if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
						consumer.accept(
							new DynamicInheritancePermissionLogic<>(
								_mbCategoryModelResourcePermission,
								_getFetchParentFunction(), false));
					}
				}),
			properties);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private UnsafeFunction<MBMessage, MBCategory, PortalException>
		_getFetchParentFunction() {

		return message -> {
			long categoryId = message.getCategoryId();

			if ((MBCategoryConstants.DISCUSSION_CATEGORY_ID == categoryId) ||
				(MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID ==
					categoryId)) {

				return null;
			}

			if (message.isInTrash()) {
				return _mbCategoryLocalService.fetchMBCategory(categoryId);
			}

			return _mbCategoryLocalService.getCategory(categoryId);
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_mbCategoryModelResourcePermission;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference(target = "(resource.name=" + MBConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	private ServiceRegistration<ModelResourcePermission<MBMessage>>
		_serviceRegistration;

	@Reference
	private StagingPermission _stagingPermission;

}