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
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
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
 * @author Sergio González
 */
@Component(service = {})
public class MBCategoryPermissionRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"model.class.name", MBCategory.class.getName()
			).build();

		_serviceRegistration = bundleContext.registerService(
			(Class<ModelResourcePermission<MBCategory>>)
				(Class<?>)ModelResourcePermission.class,
			ModelResourcePermissionFactory.create(
				MBCategory.class, MBCategory::getCategoryId,
				_mbCategoryLocalService::getCategory,
				_portletResourcePermission,
				(modelResourcePermission, consumer) -> {
					consumer.accept(
						(permissionChecker, name, message, actionId) -> {
							if (_mbBanLocalService.hasBan(
									message.getGroupId(),
									permissionChecker.getUserId())) {

								return false;
							}

							return null;
						});
					consumer.accept(
						new StagedModelPermissionLogic<>(
							_stagingPermission, MBPortletKeys.MESSAGE_BOARDS,
							MBCategory::getCategoryId));

					if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
						consumer.accept(
							new DynamicInheritancePermissionLogic<>(
								modelResourcePermission,
								_getFetchParentFunction(), false));
					}
				},
				actionId -> {
					if (ActionKeys.ADD_CATEGORY.equals(actionId)) {
						return ActionKeys.ADD_SUBCATEGORY;
					}

					return actionId;
				}),
			properties);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private UnsafeFunction<MBCategory, MBCategory, PortalException>
		_getFetchParentFunction() {

		return category -> {
			long categoryId = category.getParentCategoryId();

			if (MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID == categoryId) {
				return null;
			}

			if (category.isInTrash()) {
				return _mbCategoryLocalService.fetchMBCategory(categoryId);
			}

			return _mbCategoryLocalService.getCategory(categoryId);
		};
	}

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference(target = "(resource.name=" + MBConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	private ServiceRegistration<ModelResourcePermission<MBCategory>>
		_serviceRegistration;

	@Reference
	private StagingPermission _stagingPermission;

}