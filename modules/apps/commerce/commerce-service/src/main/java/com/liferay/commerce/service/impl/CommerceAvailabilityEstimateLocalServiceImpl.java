/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CPDAvailabilityEstimateLocalService;
import com.liferay.commerce.service.base.CommerceAvailabilityEstimateLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceAvailabilityEstimate",
	service = AopService.class
)
public class CommerceAvailabilityEstimateLocalServiceImpl
	extends CommerceAvailabilityEstimateLocalServiceBaseImpl {

	@Override
	public CommerceAvailabilityEstimate addCommerceAvailabilityEstimate(
			Map<Locale, String> titleMap, double priority,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce availability estimate

		User user = _userLocalService.getUser(serviceContext.getUserId());

		long commerceAvailabilityEstimateId = counterLocalService.increment();

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			commerceAvailabilityEstimatePersistence.create(
				commerceAvailabilityEstimateId);

		commerceAvailabilityEstimate.setCompanyId(user.getCompanyId());
		commerceAvailabilityEstimate.setUserId(user.getUserId());
		commerceAvailabilityEstimate.setUserName(user.getFullName());
		commerceAvailabilityEstimate.setTitleMap(titleMap);
		commerceAvailabilityEstimate.setPriority(priority);
		commerceAvailabilityEstimate.setStatus(
			WorkflowConstants.STATUS_APPROVED);

		commerceAvailabilityEstimate =
			commerceAvailabilityEstimatePersistence.update(
				commerceAvailabilityEstimate);

		// Resources

		_resourceLocalService.addModelResources(
			commerceAvailabilityEstimate, serviceContext);

		return commerceAvailabilityEstimate;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceAvailabilityEstimate deleteCommerceAvailabilityEstimate(
			CommerceAvailabilityEstimate commerceAvailabilityEstimate)
		throws PortalException {

		// Commerce availability estimate

		commerceAvailabilityEstimatePersistence.remove(
			commerceAvailabilityEstimate);

		// Resources

		_resourceLocalService.deleteResource(
			commerceAvailabilityEstimate, ResourceConstants.SCOPE_INDIVIDUAL);

		// Commerce product definition availability estimates

		_cpdAvailabilityEstimateLocalService.deleteCPDAvailabilityEstimates(
			commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId());

		return commerceAvailabilityEstimate;
	}

	@Override
	public CommerceAvailabilityEstimate deleteCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			commerceAvailabilityEstimatePersistence.findByPrimaryKey(
				commerceAvailabilityEstimateId);

		return commerceAvailabilityEstimateLocalService.
			deleteCommerceAvailabilityEstimate(commerceAvailabilityEstimate);
	}

	@Override
	public void deleteCommerceAvailabilityEstimates(long companyId)
		throws PortalException {

		List<CommerceAvailabilityEstimate> commerceAvailabilityEstimates =
			commerceAvailabilityEstimatePersistence.findByCompanyId(companyId);

		for (CommerceAvailabilityEstimate commerceAvailabilityEstimate :
				commerceAvailabilityEstimates) {

			commerceAvailabilityEstimateLocalService.
				deleteCommerceAvailabilityEstimate(
					commerceAvailabilityEstimate);
		}
	}

	@Override
	public List<CommerceAvailabilityEstimate> getCommerceAvailabilityEstimates(
		long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return commerceAvailabilityEstimatePersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAvailabilityEstimatesCount(long companyId) {
		return commerceAvailabilityEstimatePersistence.countByCompanyId(
			companyId);
	}

	@Override
	public CommerceAvailabilityEstimate updateCommerceAvailabilityEstimate(
			long commerceAvailabilityId, Map<Locale, String> titleMap,
			double priority, ServiceContext serviceContext)
		throws PortalException {

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			commerceAvailabilityEstimatePersistence.findByPrimaryKey(
				commerceAvailabilityId);

		commerceAvailabilityEstimate.setTitleMap(titleMap);
		commerceAvailabilityEstimate.setPriority(priority);

		return commerceAvailabilityEstimatePersistence.update(
			commerceAvailabilityEstimate);
	}

	@Reference
	private CPDAvailabilityEstimateLocalService
		_cpdAvailabilityEstimateLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}