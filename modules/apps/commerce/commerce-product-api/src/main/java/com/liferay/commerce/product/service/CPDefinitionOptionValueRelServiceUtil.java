/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CPDefinitionOptionValueRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionValueRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRelService
 * @generated
 */
public class CPDefinitionOptionValueRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionValueRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId, String key,
			Map<java.util.Locale, String> nameMap, boolean preselected,
			java.math.BigDecimal deltaPrice, double priority,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, cpInstanceId, key, nameMap, preselected,
			deltaPrice, priority, quantity, unitOfMeasureKey, serviceContext);
	}

	public static CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key,
			Map<java.util.Locale, String> nameMap, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, key, nameMap, priority, serviceContext);
	}

	public static CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		return getService().deleteCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId);
	}

	public static CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		return getService().fetchCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId);
	}

	public static CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key)
		throws PortalException {

		return getService().fetchCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, key);
	}

	public static CPDefinitionOptionValueRel
			fetchCPDefinitionOptionValueRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			fetchCPDefinitionOptionValueRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	public static CPDefinitionOptionValueRel getCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId);
	}

	public static List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(
				long cpDefinitionOptionRelId, int start, int end)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId, start, end);
	}

	public static List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(
				long cpDefinitionOptionRelId, int start, int end,
				OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId, start, end, orderByComparator);
	}

	public static List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(
				long groupId, String key, int start, int end)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRels(
			groupId, key, start, end);
	}

	public static int getCPDefinitionOptionValueRelsCount(
			long cpDefinitionOptionRelId)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRelsCount(
			cpDefinitionOptionRelId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CPDefinitionOptionValueRel
			resetCPInstanceCPDefinitionOptionValueRel(
				long cpDefinitionOptionValueRelId)
		throws PortalException {

		return getService().resetCPInstanceCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionValueRel> searchCPDefinitionOptionValueRels(
				long companyId, long groupId, long cpDefinitionOptionRelId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws PortalException {

		return getService().searchCPDefinitionOptionValueRels(
			companyId, groupId, cpDefinitionOptionRelId, keywords, start, end,
			sorts);
	}

	public static int searchCPDefinitionOptionValueRelsCount(
			long companyId, long groupId, long cpDefinitionOptionRelId,
			String keywords)
		throws PortalException {

		return getService().searchCPDefinitionOptionValueRelsCount(
			companyId, groupId, cpDefinitionOptionRelId, keywords);
	}

	public static CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId, long cpInstanceId, String key,
			Map<java.util.Locale, String> nameMap, boolean preselected,
			java.math.BigDecimal price, double priority,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId, cpInstanceId, key, nameMap,
			preselected, price, priority, quantity, unitOfMeasureKey,
			serviceContext);
	}

	public static CPDefinitionOptionValueRel
			updateCPDefinitionOptionValueRelPreselected(
				long cpDefinitionOptionValueRelId, boolean preselected)
		throws PortalException {

		return getService().updateCPDefinitionOptionValueRelPreselected(
			cpDefinitionOptionValueRelId, preselected);
	}

	public static CPDefinitionOptionValueRel updateExternalReferenceCode(
			long cpDefinitionOptionValueRelId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			cpDefinitionOptionValueRelId, externalReferenceCode);
	}

	public static CPDefinitionOptionValueRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionOptionValueRelService>
		_serviceSnapshot = new Snapshot<>(
			CPDefinitionOptionValueRelServiceUtil.class,
			CPDefinitionOptionValueRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:372764533