/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommercePriceModifierRel. This utility wraps
 * <code>com.liferay.commerce.pricing.service.impl.CommercePriceModifierRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Riccardo Alberti
 * @see CommercePriceModifierRelService
 * @generated
 */
public class CommercePriceModifierRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.pricing.service.impl.CommercePriceModifierRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommercePriceModifierRel addCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceModifierRel(
			commercePriceModifierId, className, classPK, serviceContext);
	}

	public static void deleteCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws PortalException {

		getService().deleteCommercePriceModifierRel(commercePriceModifierRelId);
	}

	public static CommercePriceModifierRel fetchCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK)
		throws PortalException {

		return getService().fetchCommercePriceModifierRel(
			commercePriceModifierId, className, classPK);
	}

	public static List<CommercePriceModifierRel>
		getCategoriesCommercePriceModifierRels(
			long commercePriceModifierId, String name, int start, int end) {

		return getService().getCategoriesCommercePriceModifierRels(
			commercePriceModifierId, name, start, end);
	}

	public static int getCategoriesCommercePriceModifierRelsCount(
		long commercePriceModifierId, String name) {

		return getService().getCategoriesCommercePriceModifierRelsCount(
			commercePriceModifierId, name);
	}

	public static long[] getClassPKs(
			long commercePriceModifierId, String className)
		throws PortalException {

		return getService().getClassPKs(commercePriceModifierId, className);
	}

	public static CommercePriceModifierRel getCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws PortalException {

		return getService().getCommercePriceModifierRel(
			commercePriceModifierRelId);
	}

	public static List<CommercePriceModifierRel> getCommercePriceModifierRels(
			long commercePriceModifierId, String className)
		throws PortalException {

		return getService().getCommercePriceModifierRels(
			commercePriceModifierId, className);
	}

	public static List<CommercePriceModifierRel> getCommercePriceModifierRels(
			long commercePriceModifierId, String className, int start, int end,
			OrderByComparator<CommercePriceModifierRel> orderByComparator)
		throws PortalException {

		return getService().getCommercePriceModifierRels(
			commercePriceModifierId, className, start, end, orderByComparator);
	}

	public static int getCommercePriceModifierRelsCount(
			long commercePriceModifierId, String className)
		throws PortalException {

		return getService().getCommercePriceModifierRelsCount(
			commercePriceModifierId, className);
	}

	public static List<CommercePriceModifierRel> getCommercePriceModifiersRels(
		String className, long classPK) {

		return getService().getCommercePriceModifiersRels(className, classPK);
	}

	public static List<CommercePriceModifierRel>
		getCommercePricingClassesCommercePriceModifierRels(
			long commercePriceModifierId, String title, int start, int end) {

		return getService().getCommercePricingClassesCommercePriceModifierRels(
			commercePriceModifierId, title, start, end);
	}

	public static int getCommercePricingClassesCommercePriceModifierRelsCount(
		long commercePriceModifierId, String title) {

		return getService().
			getCommercePricingClassesCommercePriceModifierRelsCount(
				commercePriceModifierId, title);
	}

	public static List<CommercePriceModifierRel>
		getCPDefinitionsCommercePriceModifierRels(
			long commercePriceModifierId, String name, String languageId,
			int start, int end) {

		return getService().getCPDefinitionsCommercePriceModifierRels(
			commercePriceModifierId, name, languageId, start, end);
	}

	public static int getCPDefinitionsCommercePriceModifierRelsCount(
		long commercePriceModifierId, String name, String languageId) {

		return getService().getCPDefinitionsCommercePriceModifierRelsCount(
			commercePriceModifierId, name, languageId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CommercePriceModifierRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceModifierRelService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceModifierRelServiceUtil.class,
			CommercePriceModifierRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1465969273