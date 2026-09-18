/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommerceDiscountOrderTypeRel. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountOrderTypeRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CommerceDiscountOrderTypeRelService
 * @generated
 */
public class CommerceDiscountOrderTypeRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountOrderTypeRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId, int priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountOrderTypeRel(
			commerceDiscountId, commerceOrderTypeId, priority, serviceContext);
	}

	public static void deleteCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		getService().deleteCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
	}

	public static CommerceDiscountOrderTypeRel
			fetchCommerceDiscountOrderTypeRel(
				long commerceDiscountId, long commerceOrderTypeId)
		throws PortalException {

		return getService().fetchCommerceDiscountOrderTypeRel(
			commerceDiscountId, commerceOrderTypeId);
	}

	public static CommerceDiscountOrderTypeRel getCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
	}

	public static List<CommerceDiscountOrderTypeRel>
			getCommerceDiscountOrderTypeRels(
				long commerceDiscountId, String name, int start, int end,
				OrderByComparator<CommerceDiscountOrderTypeRel>
					orderByComparator)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRels(
			commerceDiscountId, name, start, end, orderByComparator);
	}

	public static int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRelsCount(
			commerceDiscountId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CommerceDiscountOrderTypeRel updatePriority(
			long commerceDiscountOrderTypeRelId, int priority)
		throws PortalException {

		return getService().updatePriority(
			commerceDiscountOrderTypeRelId, priority);
	}

	public static CommerceDiscountOrderTypeRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountOrderTypeRelService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountOrderTypeRelServiceUtil.class,
			CommerceDiscountOrderTypeRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:815412641