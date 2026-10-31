/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.impl;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.constants.CommerceVirtualOrderActionKeys;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.order.service.base.CommerceVirtualOrderItemServiceBaseImpl;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDVirtualSettingFileEntryPersistence;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.io.File;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceVirtualOrderItem"
	},
	service = AopService.class
)
public class CommerceVirtualOrderItemServiceImpl
	extends CommerceVirtualOrderItemServiceBaseImpl {

	@Override
	public CommerceVirtualOrderItem fetchCommerceVirtualOrderItem(
			long commerceVirtualOrderItemId)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.fetchByPrimaryKey(
				commerceVirtualOrderItemId);

		if (commerceVirtualOrderItem != null) {
			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);
		}

		return commerceVirtualOrderItem;
	}

	@Override
	public CommerceVirtualOrderItem
			fetchCommerceVirtualOrderItemByCommerceOrderItemId(
				long commerceOrderItemId)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.fetchByCommerceOrderItemId(
				commerceOrderItemId);

		if (commerceVirtualOrderItem != null) {
			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
				ActionKeys.VIEW);
		}

		return commerceVirtualOrderItem;
	}

	@Override
	public File getFile(
			long commerceVirtualOrderItemId,
			long commerceVirtualOrderItemFileEntryId)
		throws Exception {

		PermissionChecker permissionChecker = getPermissionChecker();

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		_commerceVirtualOrderItemFileEntryModelResourcePermission.check(
			permissionChecker, commerceVirtualOrderItemFileEntryId,
			CommerceVirtualOrderActionKeys.
				DOWNLOAD_COMMERCE_VIRTUAL_ORDER_ITEM);

		File file = commerceVirtualOrderItemLocalService.getFile(
			commerceVirtualOrderItemId, commerceVirtualOrderItemFileEntryId);

		if (!permissionChecker.isCompanyAdmin() ||
			!permissionChecker.isGroupAdmin(
				commerceVirtualOrderItem.getGroupId())) {

			_commerceVirtualOrderItemFileEntryLocalService.incrementUsages(
				commerceVirtualOrderItemFileEntryId);
		}

		return file;
	}

	@Override
	public void propagateCPDVirtualSettingFileEntry(
			long cpdVirtualSettingFileEntryId)
		throws PortalException {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryPersistence.findByPrimaryKey(
				cpdVirtualSettingFileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		long cpDefinitionId = cpDefinitionVirtualSetting.getClassPK();

		if (Objects.equals(
				cpDefinitionVirtualSetting.getClassName(),
				CPInstance.class.getName())) {

			CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
				cpDefinitionVirtualSetting.getClassPK());

			cpDefinitionId = cpInstance.getCPDefinitionId();
		}

		PermissionChecker permissionChecker = getPermissionChecker();

		_cpDefinitionModelResourcePermission.check(
			permissionChecker, cpDefinitionId, ActionKeys.UPDATE);

		if (!_cpDefinitionLocalService.isPublishedCPDefinition(
				cpDefinitionId)) {

			throw new CommerceVirtualOrderItemException(
				StringBundler.concat(
					"Unable to propagate commerce product definition virtual ",
					"setting file entry ", cpdVirtualSettingFileEntryId,
					" because commerce product definition ", cpDefinitionId,
					" is not published"));
		}

		for (CommerceVirtualOrderItem commerceVirtualOrderItem :
				commerceVirtualOrderItemLocalService.
					getNoCPDVirtualSettingFileEntryCommerceVirtualOrderItems(
						cpdVirtualSettingFileEntryId)) {

			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			if (!_commerceOrderModelResourcePermission.contains(
					permissionChecker, commerceOrderItem.getCommerceOrderId(),
					ActionKeys.UPDATE)) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Skipping commerce order ",
							commerceOrderItem.getCommerceOrderId(),
							" because user ", getUserId(),
							" does not have permission to update it"));
				}

				continue;
			}

			_commerceVirtualOrderItemFileEntryLocalService.
				addCommerceVirtualOrderItemFileEntry(
					getUserId(), commerceVirtualOrderItem.getGroupId(),
					commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
					cpdVirtualSettingFileEntry.getFileEntryId(),
					cpdVirtualSettingFileEntry.getUrl(), 0,
					cpdVirtualSettingFileEntry.getVersion());
		}
	}

	@Override
	public CommerceVirtualOrderItem updateCommerceVirtualOrderItem(
			long commerceVirtualOrderItemId, int activationStatus,
			long duration, int maxUsages, boolean active)
		throws PortalException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItemPersistence.findByPrimaryKey(
				commerceVirtualOrderItemId);

		CommerceOrderItem commerceOrderItem =
			commerceVirtualOrderItem.getCommerceOrderItem();

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.UPDATE);

		return commerceVirtualOrderItemLocalService.
			updateCommerceVirtualOrderItem(
				commerceVirtualOrderItemId, activationStatus, duration,
				maxUsages, active);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceVirtualOrderItemServiceImpl.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry)"
	)
	private ModelResourcePermission<CommerceVirtualOrderItemFileEntry>
		_commerceVirtualOrderItemFileEntryModelResourcePermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPDVirtualSettingFileEntryPersistence
		_cpdVirtualSettingFileEntryPersistence;

}