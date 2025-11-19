/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPDefinitionService}.
 *
 * @author Marco Leo
 * @see CPDefinitionService
 * @generated
 */
public class CPDefinitionServiceWrapper
	implements CPDefinitionService, ServiceWrapper<CPDefinitionService> {

	public CPDefinitionServiceWrapper() {
		this(null);
	}

	public CPDefinitionServiceWrapper(CPDefinitionService cpDefinitionService) {
		_cpDefinitionService = cpDefinitionService;
	}

	@Override
	public CPDefinition addCPDefinition(
			String externalReferenceCode, long groupId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> urlTitleMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			String productTypeName, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.addCPDefinition(
			externalReferenceCode, groupId, nameMap, shortDescriptionMap,
			descriptionMap, urlTitleMap, metaTitleMap, metaDescriptionMap,
			metaKeywordsMap, productTypeName, ignoreSKUCombinations, shippable,
			freeShipping, shipSeparately, shippingExtraPrice, width, height,
			depth, weight, cpTaxCategoryId, taxExempt, telcoOrElectronics,
			ddmStructureKey, published, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, defaultSku,
			subscriptionEnabled, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, accountGroupFilterEnabled,
			channelFilterEnabled, status, serviceContext);
	}

	@Override
	public CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> urlTitleMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			String productTypeName, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.addOrUpdateCPDefinition(
			externalReferenceCode, cpDefinitionId, groupId, nameMap,
			shortDescriptionMap, descriptionMap, urlTitleMap, metaTitleMap,
			metaDescriptionMap, metaKeywordsMap, productTypeName,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, defaultSku, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, accountGroupFilterEnabled,
			channelFilterEnabled, status, serviceContext);
	}

	@Override
	public CPDefinition cloneCPDefinition(
			long cpDefinitionId, long groupId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.cloneCPDefinition(
			cpDefinitionId, groupId, serviceContext);
	}

	@Override
	public CPDefinition copyCPDefinition(
			long sourceCPDefinitionId, long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.copyCPDefinition(
			sourceCPDefinitionId, groupId, status);
	}

	@Override
	public void deleteAssetCategoryCPDefinition(
			long cpDefinitionId, long categoryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionService.deleteAssetCategoryCPDefinition(
			cpDefinitionId, categoryId, serviceContext);
	}

	@Override
	public void deleteCPDefinition(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionService.deleteCPDefinition(cpDefinitionId);
	}

	@Override
	public CPDefinition fetchCPDefinition(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.fetchCPDefinition(cpDefinitionId);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.
			fetchCPDefinitionByCProductExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.fetchCPDefinitionByCProductId(cProductId);
	}

	@Override
	public CPDefinition getCPDefinition(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getCPDefinition(cpDefinitionId);
	}

	@Override
	public java.util.List<CPDefinition> getCPDefinitions(
			long groupId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPDefinition>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getCPDefinitions(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionsCount(long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getCPDefinitionsCount(groupId, status);
	}

	@Override
	public CPDefinition getCProductCPDefinition(long cProductId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getCProductCPDefinition(
			cProductId, version);
	}

	@Override
	public java.util.List<CPDefinition> getCProductCPDefinitions(
			long cProductId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getCProductCPDefinitions(
			cProductId, status, start, end);
	}

	@Override
	public com.liferay.commerce.product.model.CPAttachmentFileEntry
			getDefaultImageCPAttachmentFileEntry(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getDefaultImageCPAttachmentFileEntry(
			cpDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.Map<java.util.Locale, String> getUrlTitleMap(
			long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getUrlTitleMap(cpDefinitionId);
	}

	@Override
	public String getUrlTitleMapAsXML(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.getUrlTitleMapAsXML(cpDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPDefinition>
			searchCPDefinitions(
				long companyId, String keywords, int status,
				boolean ignoreCommerceAccountGroup, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.searchCPDefinitions(
			companyId, keywords, status, ignoreCommerceAccountGroup, start, end,
			sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPDefinition>
			searchCPDefinitions(
				long companyId, String keywords, String filterFields,
				String filterValues, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.searchCPDefinitions(
			companyId, keywords, filterFields, filterValues, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPDefinition>
			searchCPDefinitionsByChannelGroupId(
				long companyId, long commerceChannelGroupId, String keywords,
				int status, boolean ignoreCommerceAccountGroup, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.searchCPDefinitionsByChannelGroupId(
			companyId, commerceChannelGroupId, keywords, status,
			ignoreCommerceAccountGroup, start, end, sort);
	}

	@Override
	public CPDefinition updateCPDefinition(
			long cpDefinitionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> urlTitleMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			boolean ignoreSKUCombinations, boolean shippable,
			boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateCPDefinition(
			cpDefinitionId, nameMap, shortDescriptionMap, descriptionMap,
			urlTitleMap, metaTitleMap, metaDescriptionMap, metaKeywordsMap,
			ignoreSKUCombinations, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, cpTaxCategoryId,
			taxExempt, telcoOrElectronics, ddmStructureKey, published,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			accountGroupFilterEnabled, channelFilterEnabled, neverExpire,
			serviceContext);
	}

	@Override
	public CPDefinition updateCPDefinitionAccountGroupFilter(
			long cpDefinitionId, boolean enable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateCPDefinitionAccountGroupFilter(
			cpDefinitionId, enable);
	}

	@Override
	public CPDefinition updateCPDefinitionCategorization(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateCPDefinitionCategorization(
			cpDefinitionId, serviceContext);
	}

	@Override
	public CPDefinition updateCPDefinitionChannelFilter(
			long cpDefinitionId, boolean enable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateCPDefinitionChannelFilter(
			cpDefinitionId, enable);
	}

	@Override
	public CPDefinition updateExternalReferenceCode(
			String externalReferenceCode, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateExternalReferenceCode(
			externalReferenceCode, cpDefinitionId);
	}

	@Override
	public CPDefinition updateShippingInfo(
			long cpDefinitionId, boolean shippable, boolean freeShipping,
			boolean shipSeparately, double shippingExtraPrice, double width,
			double height, double depth, double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateShippingInfo(
			cpDefinitionId, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, serviceContext);
	}

	@Override
	public CPDefinition updateStatus(
			long cpDefinitionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateStatus(
			cpDefinitionId, status, serviceContext, workflowContext);
	}

	@Override
	public CPDefinition updateSubscriptionInfo(
			long cpDefinitionId, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateSubscriptionInfo(
			cpDefinitionId, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	@Override
	public CPDefinition updateTaxCategoryInfo(
			long cpDefinitionId, long cpTaxCategoryId, boolean taxExempt,
			boolean telcoOrElectronics)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateTaxCategoryInfo(
			cpDefinitionId, cpTaxCategoryId, taxExempt, telcoOrElectronics);
	}

	@Override
	public CPDefinitionService getWrappedService() {
		return _cpDefinitionService;
	}

	@Override
	public void setWrappedService(CPDefinitionService cpDefinitionService) {
		_cpDefinitionService = cpDefinitionService;
	}

	private CPDefinitionService _cpDefinitionService;

}