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
			String externalReferenceCode, long groupId, long cpTaxCategoryId,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			String ddmStructureKey, String defaultSku,
			long deliveryMaxSubscriptionCycles,
			boolean deliverySubscriptionEnabled, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			double depth,
			java.util.Map<java.util.Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			long maxSubscriptionCycles,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean neverExpire, String productTypeName, boolean published,
			boolean shipSeparately, boolean shippable,
			double shippingExtraPrice,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			boolean taxExempt, boolean telcoOrElectronics,
			java.util.Map<java.util.Locale, String> urlTitleMap, double weight,
			double width, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.addCPDefinition(
			externalReferenceCode, groupId, cpTaxCategoryId,
			accountGroupFilterEnabled, channelFilterEnabled, ddmStructureKey,
			defaultSku, deliveryMaxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties, depth,
			descriptionMap, displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, freeShipping, height, ignoreSKUCombinations,
			maxSubscriptionCycles, metaDescriptionMap, metaKeywordsMap,
			metaTitleMap, nameMap, neverExpire, productTypeName, published,
			shipSeparately, shippable, shippingExtraPrice, shortDescriptionMap,
			subscriptionEnabled, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, taxExempt,
			telcoOrElectronics, urlTitleMap, weight, width, status,
			serviceContext);
	}

	@Override
	public CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long groupId, long cpDefinitionId,
			long cpTaxCategoryId, boolean accountGroupFilterEnabled,
			boolean channelFilterEnabled, String ddmStructureKey,
			String defaultSku, long deliveryMaxSubscriptionCycles,
			boolean deliverySubscriptionEnabled, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			double depth,
			java.util.Map<java.util.Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			long maxSubscriptionCycles,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean neverExpire, String productTypeName, boolean published,
			boolean shipSeparately, boolean shippable,
			double shippingExtraPrice,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			boolean taxExempt, boolean telcoOrElectronics,
			java.util.Map<java.util.Locale, String> urlTitleMap, double weight,
			double width, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.addOrUpdateCPDefinition(
			externalReferenceCode, groupId, cpDefinitionId, cpTaxCategoryId,
			accountGroupFilterEnabled, channelFilterEnabled, ddmStructureKey,
			defaultSku, deliveryMaxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties, depth,
			descriptionMap, displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, freeShipping, height, ignoreSKUCombinations,
			maxSubscriptionCycles, metaDescriptionMap, metaKeywordsMap,
			metaTitleMap, nameMap, neverExpire, productTypeName, published,
			shipSeparately, shippable, shippingExtraPrice, shortDescriptionMap,
			subscriptionEnabled, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, taxExempt,
			telcoOrElectronics, urlTitleMap, weight, width, status,
			serviceContext);
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
			String externalReferenceCode, long companyId, boolean excludeDraft)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.
			fetchCPDefinitionByCProductExternalReferenceCode(
				externalReferenceCode, companyId, excludeDraft);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
			String externalReferenceCode, long companyId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.
			fetchCPDefinitionByCProductExternalReferenceCode(
				externalReferenceCode, companyId, status);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(
			long cProductId, boolean excludeDraft)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.fetchCPDefinitionByCProductId(
			cProductId, excludeDraft);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(
			long cProductId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.fetchCPDefinitionByCProductId(
			cProductId, status);
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
	public boolean isVersionable(CPDefinition cpDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.isVersionable(cpDefinition);
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
			long cpDefinitionId, long cpTaxCategoryId,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			String ddmStructureKey, double depth,
			java.util.Map<java.util.Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			java.util.Map<java.util.Locale, String> metaDescriptionMap,
			java.util.Map<java.util.Locale, String> metaKeywordsMap,
			java.util.Map<java.util.Locale, String> metaTitleMap,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean neverExpire, boolean published, boolean shipSeparately,
			boolean shippable, double shippingExtraPrice,
			java.util.Map<java.util.Locale, String> shortDescriptionMap,
			boolean taxExempt, boolean telcoOrElectronics,
			java.util.Map<java.util.Locale, String> urlTitleMap, double weight,
			double width,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionService.updateCPDefinition(
			cpDefinitionId, cpTaxCategoryId, accountGroupFilterEnabled,
			channelFilterEnabled, ddmStructureKey, depth, descriptionMap,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, freeShipping, height, ignoreSKUCombinations,
			metaDescriptionMap, metaKeywordsMap, metaTitleMap, nameMap,
			neverExpire, published, shipSeparately, shippable,
			shippingExtraPrice, shortDescriptionMap, taxExempt,
			telcoOrElectronics, urlTitleMap, weight, width, serviceContext);
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
// LIFERAY-SERVICE-BUILDER-HASH:-1427499691