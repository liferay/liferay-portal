/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.commerce.payment.service.base.CommercePaymentEntryAuditServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePaymentEntryAudit"
	},
	service = AopService.class
)
public class CommercePaymentEntryAuditServiceImpl
	extends CommercePaymentEntryAuditServiceBaseImpl {

	@Override
	public List<CommercePaymentEntryAudit> getCommercePaymentEntries(
			long companyId, long commercePaymentEntryId, int start, int end,
			OrderByComparator<CommercePaymentEntryAudit> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, CommercePaymentEntryAudit.class.getName(), companyId,
				ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntryAudit.class.getName(), 0,
				ActionKeys.VIEW);
		}

		return commercePaymentEntryAuditPersistence.
			findByCommercePaymentEntryId(
				commercePaymentEntryId, start, end, orderByComparator);
	}

	@Override
	public CommercePaymentEntryAudit getCommercePaymentEntryAudit(
			long commercePaymentEntryAuditId)
		throws PortalException {

		_commercePaymentEntryAuditModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryAuditId,
			ActionKeys.VIEW);

		return commercePaymentEntryAuditPersistence.findByPrimaryKey(
			commercePaymentEntryAuditId);
	}

	@Override
	public List<CommercePaymentEntryAudit> search(
			long companyId, long[] commercePaymentEntryIds, String[] logTypes,
			String keywords, int start, int end, String orderByField,
			boolean reverse)
		throws PortalException {

		BaseModelSearchResult<CommercePaymentEntryAudit> baseModelSearchResult =
			commercePaymentEntryAuditLocalService.
				searchCommercePaymentEntryAudits(
					companyId, keywords,
					LinkedHashMapBuilder.<String, Object>put(
						"commercePaymentEntryIds", commercePaymentEntryIds
					).put(
						"logTypes", logTypes
					).put(
						"permissionUserId", getPermissionChecker().getUserId()
					).build(),
					start, end, orderByField, reverse);

		return baseModelSearchResult.getBaseModels();
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntryAudit)"
	)
	private ModelResourcePermission<CommercePaymentEntryAudit>
		_commercePaymentEntryAuditModelResourcePermission;

}