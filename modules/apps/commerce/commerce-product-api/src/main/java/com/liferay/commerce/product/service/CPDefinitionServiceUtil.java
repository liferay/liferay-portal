/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CPDefinition. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPDefinitionService
 * @generated
 */
public class CPDefinitionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPDefinition addCPDefinition(
			String externalReferenceCode, long groupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
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
		throws PortalException {

		return getService().addCPDefinition(
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

	public static CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
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
		throws PortalException {

		return getService().addOrUpdateCPDefinition(
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

	public static CPDefinition cloneCPDefinition(
			long cpDefinitionId, long groupId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().cloneCPDefinition(
			cpDefinitionId, groupId, serviceContext);
	}

	public static CPDefinition copyCPDefinition(
			long sourceCPDefinitionId, long groupId, int status)
		throws PortalException {

		return getService().copyCPDefinition(
			sourceCPDefinitionId, groupId, status);
	}

	public static void deleteAssetCategoryCPDefinition(
			long cpDefinitionId, long categoryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().deleteAssetCategoryCPDefinition(
			cpDefinitionId, categoryId, serviceContext);
	}

	public static void deleteCPDefinition(long cpDefinitionId)
		throws PortalException {

		getService().deleteCPDefinition(cpDefinitionId);
	}

	public static CPDefinition fetchCPDefinition(long cpDefinitionId)
		throws PortalException {

		return getService().fetchCPDefinition(cpDefinitionId);
	}

	public static CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPDefinitionByCProductExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPDefinition fetchCPDefinitionByCProductId(long cProductId)
		throws PortalException {

		return getService().fetchCPDefinitionByCProductId(cProductId);
	}

	public static CPDefinition getCPDefinition(long cpDefinitionId)
		throws PortalException {

		return getService().getCPDefinition(cpDefinitionId);
	}

	public static List<CPDefinition> getCPDefinitions(
			long groupId, int status, int start, int end,
			OrderByComparator<CPDefinition> orderByComparator)
		throws PortalException {

		return getService().getCPDefinitions(
			groupId, status, start, end, orderByComparator);
	}

	public static int getCPDefinitionsCount(long groupId, int status)
		throws PortalException {

		return getService().getCPDefinitionsCount(groupId, status);
	}

	public static CPDefinition getCProductCPDefinition(
			long cProductId, int version)
		throws PortalException {

		return getService().getCProductCPDefinition(cProductId, version);
	}

	public static List<CPDefinition> getCProductCPDefinitions(
			long cProductId, int status, int start, int end)
		throws PortalException {

		return getService().getCProductCPDefinitions(
			cProductId, status, start, end);
	}

	public static com.liferay.commerce.product.model.CPAttachmentFileEntry
			getDefaultImageCPAttachmentFileEntry(long cpDefinitionId)
		throws PortalException {

		return getService().getDefaultImageCPAttachmentFileEntry(
			cpDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static Map<java.util.Locale, String> getUrlTitleMap(
			long cpDefinitionId)
		throws PortalException {

		return getService().getUrlTitleMap(cpDefinitionId);
	}

	public static String getUrlTitleMapAsXML(long cpDefinitionId)
		throws PortalException {

		return getService().getUrlTitleMapAsXML(cpDefinitionId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinition> searchCPDefinitions(
				long companyId, String keywords, int status,
				boolean ignoreCommerceAccountGroup, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitions(
			companyId, keywords, status, ignoreCommerceAccountGroup, start, end,
			sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinition> searchCPDefinitions(
				long companyId, String keywords, String filterFields,
				String filterValues, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitions(
			companyId, keywords, filterFields, filterValues, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinition> searchCPDefinitionsByChannelGroupId(
				long companyId, long commerceChannelGroupId, String keywords,
				int status, boolean ignoreCommerceAccountGroup, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitionsByChannelGroupId(
			companyId, commerceChannelGroupId, keywords, status,
			ignoreCommerceAccountGroup, start, end, sort);
	}

	public static CPDefinition updateCPDefinition(
			long cpDefinitionId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> shortDescriptionMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> urlTitleMap,
			Map<java.util.Locale, String> metaTitleMap,
			Map<java.util.Locale, String> metaDescriptionMap,
			Map<java.util.Locale, String> metaKeywordsMap,
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
		throws PortalException {

		return getService().updateCPDefinition(
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

	public static CPDefinition updateCPDefinitionAccountGroupFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		return getService().updateCPDefinitionAccountGroupFilter(
			cpDefinitionId, enable);
	}

	public static CPDefinition updateCPDefinitionCategorization(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionCategorization(
			cpDefinitionId, serviceContext);
	}

	public static CPDefinition updateCPDefinitionChannelFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		return getService().updateCPDefinitionChannelFilter(
			cpDefinitionId, enable);
	}

	public static CPDefinition updateExternalReferenceCode(
			String externalReferenceCode, long cpDefinitionId)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			externalReferenceCode, cpDefinitionId);
	}

	public static CPDefinition updateShippingInfo(
			long cpDefinitionId, boolean shippable, boolean freeShipping,
			boolean shipSeparately, double shippingExtraPrice, double width,
			double height, double depth, double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateShippingInfo(
			cpDefinitionId, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, serviceContext);
	}

	public static CPDefinition updateStatus(
			long cpDefinitionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			cpDefinitionId, status, serviceContext, workflowContext);
	}

	public static CPDefinition updateSubscriptionInfo(
			long cpDefinitionId, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws PortalException {

		return getService().updateSubscriptionInfo(
			cpDefinitionId, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	public static CPDefinition updateTaxCategoryInfo(
			long cpDefinitionId, long cpTaxCategoryId, boolean taxExempt,
			boolean telcoOrElectronics)
		throws PortalException {

		return getService().updateTaxCategoryInfo(
			cpDefinitionId, cpTaxCategoryId, taxExempt, telcoOrElectronics);
	}

	public static CPDefinitionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionService> _serviceSnapshot =
		new Snapshot<>(
			CPDefinitionServiceUtil.class, CPDefinitionService.class);

}