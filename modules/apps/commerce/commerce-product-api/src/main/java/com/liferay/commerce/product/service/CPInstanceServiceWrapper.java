/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPInstanceService}.
 *
 * @author Marco Leo
 * @see CPInstanceService
 * @generated
 */
public class CPInstanceServiceWrapper
	implements CPInstanceService, ServiceWrapper<CPInstanceService> {

	public CPInstanceServiceWrapper() {
		this(null);
	}

	public CPInstanceServiceWrapper(CPInstanceService cpInstanceService) {
		_cpInstanceService = cpInstanceService;
	}

	@Override
	public CPInstance addCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable,
			java.util.Map<Long, java.util.List<Long>>
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.addCPInstance(
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

	@Override
	public CPInstance addOrUpdateCPInstance(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.addOrUpdateCPInstance(
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

	@Override
	public java.util.List<CPInstance> buildCPInstances(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.buildCPInstances(
			cpDefinitionId, serviceContext);
	}

	@Override
	public void deleteCPInstance(long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceService.deleteCPInstance(cpInstanceId);
	}

	@Override
	public CPInstance fetchCPInstance(long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.fetchCPInstance(cpInstanceId);
	}

	@Override
	public CPInstance fetchCPInstanceByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.fetchCPInstanceByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public CPInstance fetchCProductInstance(
			long cProductId, String cpInstanceUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.fetchCProductInstance(
			cProductId, cpInstanceUuid);
	}

	@Override
	public java.util.List<CPInstance> getCPDefinitionInstances(
			long cpDefinitionId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPInstance>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getCPDefinitionInstances(
			cpDefinitionId, status, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionInstancesCount(long cpDefinitionId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getCPDefinitionInstancesCount(
			cpDefinitionId, status);
	}

	@Override
	public CPInstance getCPInstance(long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getCPInstance(cpInstanceId);
	}

	@Override
	public java.util.List<CPInstance> getCPInstances(
			long groupId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPInstance>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getCPInstances(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getCPInstancesCount(long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getCPInstancesCount(groupId, status);
	}

	@Override
	public CPInstance getCProductInstance(
			long cProductId, String cpInstanceUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getCProductInstance(
			cProductId, cpInstanceUuid);
	}

	@Override
	public CPInstance getOrAddEmptyCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.getOrAddEmptyCPInstance(
			externalReferenceCode, cpDefinitionId, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpInstanceService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPInstances(
				long companyId, long groupId, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.searchCPInstances(
			companyId, groupId, keywords, status, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPInstances(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.searchCPInstances(
			companyId, keywords, status, start, end, sort);
	}

	@Override
	public CPInstance updateCPInstance(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.updateCPInstance(
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

	@Override
	public CPInstance updateExternalReferenceCode(
			long cpInstanceId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.updateExternalReferenceCode(
			cpInstanceId, externalReferenceCode);
	}

	@Override
	public CPInstance updatePricingInfo(
			long cpInstanceId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal cost,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.updatePricingInfo(
			cpInstanceId, price, promoPrice, cost, serviceContext);
	}

	@Override
	public CPInstance updateShippingInfo(
			long cpInstanceId, double width, double height, double depth,
			double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.updateShippingInfo(
			cpInstanceId, width, height, depth, weight, serviceContext);
	}

	@Override
	public CPInstance updateSubscriptionInfo(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceService.updateSubscriptionInfo(
			cpInstanceId, overrideSubscriptionInfo, subscriptionEnabled,
			subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	@Override
	public CPInstanceService getWrappedService() {
		return _cpInstanceService;
	}

	@Override
	public void setWrappedService(CPInstanceService cpInstanceService) {
		_cpInstanceService = cpInstanceService;
	}

	private CPInstanceService _cpInstanceService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1652927503