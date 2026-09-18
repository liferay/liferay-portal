/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CPInstance. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPInstanceServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPInstanceService
 * @generated
 */
public class CPInstanceServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPInstanceServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPInstance addCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable,
			Map<Long, List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds,
			double width, double height, double depth, double weight,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal cost, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean overrideSubscriptionInfo,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPInstance(
			externalReferenceCode, cpDefinitionId, groupId, sku, gtin,
			manufacturerPartNumber, purchasable,
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds, width, height,
			depth, weight, price, promoPrice, cost, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			overrideSubscriptionInfo, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, unspsc, discontinued,
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static CPInstance addOrUpdateCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable, String json, double width, double height,
			double depth, double weight, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal cost,
			boolean published, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			boolean overrideSubscriptionInfo, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPInstance(
			externalReferenceCode, cpDefinitionId, groupId, sku, gtin,
			manufacturerPartNumber, purchasable, json, width, height, depth,
			weight, price, promoPrice, cost, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			overrideSubscriptionInfo, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, unspsc, discontinued,
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static List<CPInstance> buildCPInstances(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().buildCPInstances(cpDefinitionId, serviceContext);
	}

	public static void deleteCPInstance(long cpInstanceId)
		throws PortalException {

		getService().deleteCPInstance(cpInstanceId);
	}

	public static CPInstance fetchCPInstance(long cpInstanceId)
		throws PortalException {

		return getService().fetchCPInstance(cpInstanceId);
	}

	public static CPInstance fetchCPInstanceByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPInstanceByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPInstance fetchCProductInstance(
			long cProductId, String cpInstanceUuid)
		throws PortalException {

		return getService().fetchCProductInstance(cProductId, cpInstanceUuid);
	}

	public static List<CPInstance> getCPDefinitionInstances(
			long cpDefinitionId, int status, int start, int end,
			OrderByComparator<CPInstance> orderByComparator)
		throws PortalException {

		return getService().getCPDefinitionInstances(
			cpDefinitionId, status, start, end, orderByComparator);
	}

	public static int getCPDefinitionInstancesCount(
			long cpDefinitionId, int status)
		throws PortalException {

		return getService().getCPDefinitionInstancesCount(
			cpDefinitionId, status);
	}

	public static CPInstance getCPInstance(long cpInstanceId)
		throws PortalException {

		return getService().getCPInstance(cpInstanceId);
	}

	public static List<CPInstance> getCPInstances(
			long groupId, int status, int start, int end,
			OrderByComparator<CPInstance> orderByComparator)
		throws PortalException {

		return getService().getCPInstances(
			groupId, status, start, end, orderByComparator);
	}

	public static int getCPInstancesCount(long groupId, int status)
		throws PortalException {

		return getService().getCPInstancesCount(groupId, status);
	}

	public static CPInstance getCProductInstance(
			long cProductId, String cpInstanceUuid)
		throws PortalException {

		return getService().getCProductInstance(cProductId, cpInstanceUuid);
	}

	public static CPInstance getOrAddEmptyCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId)
		throws PortalException {

		return getService().getOrAddEmptyCPInstance(
			externalReferenceCode, cpDefinitionId, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPInstances(
				long companyId, long groupId, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPInstances(
			companyId, groupId, keywords, status, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPInstances(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPInstances(
			companyId, keywords, status, start, end, sort);
	}

	public static CPInstance updateCPInstance(
			String externalReferenceCode, long cpInstanceId, String sku,
			String gtin, String manufacturerPartNumber, boolean purchasable,
			double width, double height, double depth, double weight,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal cost, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean overrideSubscriptionInfo,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPInstance(
			externalReferenceCode, cpInstanceId, sku, gtin,
			manufacturerPartNumber, purchasable, width, height, depth, weight,
			price, promoPrice, cost, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			overrideSubscriptionInfo, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, unspsc, discontinued,
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static CPInstance updateExternalReferenceCode(
			long cpInstanceId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			cpInstanceId, externalReferenceCode);
	}

	public static CPInstance updatePricingInfo(
			long cpInstanceId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal cost,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePricingInfo(
			cpInstanceId, price, promoPrice, cost, serviceContext);
	}

	public static CPInstance updateShippingInfo(
			long cpInstanceId, double width, double height, double depth,
			double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateShippingInfo(
			cpInstanceId, width, height, depth, weight, serviceContext);
	}

	public static CPInstance updateSubscriptionInfo(
			long cpInstanceId, boolean overrideSubscriptionInfo,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws PortalException {

		return getService().updateSubscriptionInfo(
			cpInstanceId, overrideSubscriptionInfo, subscriptionEnabled,
			subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	public static CPInstanceService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPInstanceService> _serviceSnapshot =
		new Snapshot<>(CPInstanceServiceUtil.class, CPInstanceService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-594295841