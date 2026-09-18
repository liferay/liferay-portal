/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CommerceOrderType. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderTypeServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderTypeService
 * @generated
 */
public class CommerceOrderTypeServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderTypeServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceOrderType addCommerceOrderType(
			String externalReferenceCode, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int displayOrder,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceOrderType(
			externalReferenceCode, nameMap, descriptionMap, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, displayOrder, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static CommerceOrderType deleteCommerceOrderType(
			long commerceOrderTypeId)
		throws PortalException {

		return getService().deleteCommerceOrderType(commerceOrderTypeId);
	}

	public static CommerceOrderType fetchCommerceOrderType(
			long commerceOrderTypeId)
		throws PortalException {

		return getService().fetchCommerceOrderType(commerceOrderTypeId);
	}

	public static CommerceOrderType
			fetchCommerceOrderTypeByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommerceOrderTypeByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceOrderType getCommerceOrderType(
			long commerceOrderTypeId)
		throws PortalException {

		return getService().getCommerceOrderType(commerceOrderTypeId);
	}

	public static List<CommerceOrderType> getCommerceOrderTypes(
			String className, long classPK, boolean active, int start, int end)
		throws PortalException {

		return getService().getCommerceOrderTypes(
			className, classPK, active, start, end);
	}

	public static int getCommerceOrderTypesCount(
			String className, long classPK, boolean active)
		throws PortalException {

		return getService().getCommerceOrderTypesCount(
			className, classPK, active);
	}

	public static CommerceOrderType getOrAddEmptyCommerceOrderType(
			String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCommerceOrderType(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CommerceOrderType updateCommerceOrderType(
			String externalReferenceCode, long commerceOrderTypeId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int displayOrder,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderType(
			externalReferenceCode, commerceOrderTypeId, nameMap, descriptionMap,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, displayOrder,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceOrderType
			updateCommerceOrderTypeExternalReferenceCode(
				String externalReferenceCode, long commerceOrderTypeId)
		throws PortalException {

		return getService().updateCommerceOrderTypeExternalReferenceCode(
			externalReferenceCode, commerceOrderTypeId);
	}

	public static CommerceOrderTypeService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderTypeService> _serviceSnapshot =
		new Snapshot<>(
			CommerceOrderTypeServiceUtil.class, CommerceOrderTypeService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:822006380