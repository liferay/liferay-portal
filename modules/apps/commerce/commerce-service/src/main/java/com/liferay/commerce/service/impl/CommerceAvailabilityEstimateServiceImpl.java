/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.base.CommerceAvailabilityEstimateServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceAvailabilityEstimate"
	},
	service = AopService.class
)
public class CommerceAvailabilityEstimateServiceImpl
	extends CommerceAvailabilityEstimateServiceBaseImpl {

	@Override
	public CommerceAvailabilityEstimate addCommerceAvailabilityEstimate(
			String externalReferenceCode, Map<Locale, String> titleMap,
			double priority, ServiceContext serviceContext)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.ADD_COMMERCE_AVAILABILITY_ESTIMATE);

		return commerceAvailabilityEstimateLocalService.
			addCommerceAvailabilityEstimate(
				externalReferenceCode, titleMap, priority, serviceContext);
	}

	@Override
	public void deleteCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException {

		_commerceAvailabilityEstimateModelResourcePermission.check(
			getPermissionChecker(), commerceAvailabilityEstimateId,
			ActionKeys.DELETE);

		commerceAvailabilityEstimateLocalService.
			deleteCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	@Override
	public CommerceAvailabilityEstimate
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			commerceAvailabilityEstimateLocalService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceAvailabilityEstimate != null) {
			_commerceAvailabilityEstimateModelResourcePermission.check(
				getPermissionChecker(), commerceAvailabilityEstimate,
				ActionKeys.VIEW);
		}

		return commerceAvailabilityEstimate;
	}

	@Override
	public CommerceAvailabilityEstimate getCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException {

		_commerceAvailabilityEstimateModelResourcePermission.check(
			getPermissionChecker(), commerceAvailabilityEstimateId,
			ActionKeys.VIEW);

		return commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	@Override
	public List<CommerceAvailabilityEstimate> getCommerceAvailabilityEstimates(
			long companyId, int start, int end,
			OrderByComparator<CommerceAvailabilityEstimate> orderByComparator)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_AVAILABILITY_ESTIMATES);

		return commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimates(
				companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAvailabilityEstimatesCount(long companyId)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_AVAILABILITY_ESTIMATES);

		return commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimatesCount(companyId);
	}

	@Override
	public CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				String externalReferenceCode)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			commerceAvailabilityEstimateService.
				fetchCommerceAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, permissionChecker.getCompanyId());

		if (commerceAvailabilityEstimate != null) {
			return commerceAvailabilityEstimate;
		}

		_checkPortletResourcePermission(
			CommerceActionKeys.ADD_COMMERCE_AVAILABILITY_ESTIMATE);

		return commerceAvailabilityEstimateLocalService.
			getOrAddEmptyCommerceAvailabilityEstimate(
				externalReferenceCode, permissionChecker.getCompanyId(),
				permissionChecker.getUserId());
	}

	@Override
	public CommerceAvailabilityEstimate updateCommerceAvailabilityEstimate(
			String externalReferenceCode, long commerceAvailabilityEstimateId,
			Map<Locale, String> titleMap, double priority,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceAvailabilityEstimateModelResourcePermission.check(
			getPermissionChecker(), commerceAvailabilityEstimateId,
			ActionKeys.UPDATE);

		return commerceAvailabilityEstimateLocalService.
			updateCommerceAvailabilityEstimate(
				externalReferenceCode, commerceAvailabilityEstimateId, titleMap,
				priority, serviceContext);
	}

	private void _checkPortletResourcePermission(String actionId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceAvailabilityEstimateModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(getPermissionChecker(), null, actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceAvailabilityEstimate)"
	)
	private ModelResourcePermission<CommerceAvailabilityEstimate>
		_commerceAvailabilityEstimateModelResourcePermission;

}