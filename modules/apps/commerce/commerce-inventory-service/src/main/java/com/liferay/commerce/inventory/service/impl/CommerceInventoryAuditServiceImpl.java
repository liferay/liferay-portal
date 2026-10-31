/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.model.CommerceInventoryAudit;
import com.liferay.commerce.inventory.service.base.CommerceInventoryAuditServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceInventoryAudit"
	},
	service = AopService.class
)
public class CommerceInventoryAuditServiceImpl
	extends CommerceInventoryAuditServiceBaseImpl {

	@Override
	public List<CommerceInventoryAudit> getCommerceInventoryAudits(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end)
		throws PortalException {

		if (_portletResourcePermission.contains(
				getPermissionChecker(), null,
				CommerceInventoryActionKeys.VIEW_INVENTORIES)) {

			return commerceInventoryAuditPersistence.findByC_S_U(
				companyId, sku, unitOfMeasureKey, start, end);
		}

		return Collections.emptyList();
	}

	@Override
	public int getCommerceInventoryAuditsCount(
			long companyId, String sku, String unitOfMeasureKey)
		throws PortalException {

		if (_portletResourcePermission.contains(
				getPermissionChecker(), null,
				CommerceInventoryActionKeys.VIEW_INVENTORIES)) {

			return commerceInventoryAuditPersistence.countByC_S_U(
				companyId, sku, unitOfMeasureKey);
		}

		return 0;
	}

	@Reference(
		target = "(resource.name=" + CommerceInventoryConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}