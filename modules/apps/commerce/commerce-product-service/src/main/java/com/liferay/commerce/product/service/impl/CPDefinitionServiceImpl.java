/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.service.base.CPDefinitionServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPDefinition"
	},
	service = AopService.class
)
public class CPDefinitionServiceImpl extends CPDefinitionServiceBaseImpl {

	@Override
	public CPDefinition addCPDefinition(
			String externalReferenceCode, long groupId, long cpTaxCategoryId,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			String ddmStructureKey, String defaultSku,
			long deliveryMaxSubscriptionCycles,
			boolean deliverySubscriptionEnabled, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			double depth, Map<Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			long maxSubscriptionCycles, Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap,
			Map<Locale, String> metaTitleMap, Map<Locale, String> nameMap,
			boolean neverExpire, String productTypeName, boolean published,
			boolean shipSeparately, boolean shippable,
			double shippingExtraPrice, Map<Locale, String> shortDescriptionMap,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			boolean taxExempt, boolean telcoOrElectronics,
			Map<Locale, String> urlTitleMap, double weight, double width,
			int status, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.addCPDefinition(
			externalReferenceCode, getUserId(), groupId, cpTaxCategoryId,
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
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			double depth, Map<Locale, String> descriptionMap,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear,
			boolean freeShipping, double height, boolean ignoreSKUCombinations,
			long maxSubscriptionCycles, Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap,
			Map<Locale, String> metaTitleMap, Map<Locale, String> nameMap,
			boolean neverExpire, String productTypeName, boolean published,
			boolean shipSeparately, boolean shippable,
			double shippingExtraPrice, Map<Locale, String> shortDescriptionMap,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			boolean taxExempt, boolean telcoOrElectronics,
			Map<Locale, String> urlTitleMap, double weight, double width,
			int status, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.addOrUpdateCPDefinition(
			externalReferenceCode, getUserId(), groupId, cpDefinitionId,
			cpTaxCategoryId, accountGroupFilterEnabled, channelFilterEnabled,
			ddmStructureKey, defaultSku, deliveryMaxSubscriptionCycles,
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
			long cpDefinitionId, long groupId, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.cloneCPDefinition(
			getUserId(), cpDefinitionId, groupId, serviceContext);
	}

	@Override
	public CPDefinition copyCPDefinition(
			long sourceCPDefinitionId, long groupId, int status)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.copyCPDefinition(
			sourceCPDefinitionId, groupId, status);
	}

	@Override
	public void deleteAssetCategoryCPDefinition(
			long cpDefinitionId, long categoryId, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		cpDefinitionLocalService.deleteAssetCategoryCPDefinition(
			cpDefinitionId, categoryId, serviceContext);
	}

	@Override
	public void deleteCPDefinition(long cpDefinitionId) throws PortalException {
		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		cpDefinitionLocalService.deleteCPDefinition(cpDefinitionId);
	}

	@Override
	public CPDefinition fetchCPDefinition(long cpDefinitionId)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition != null) {
			_checkCommerceCatalogByCPDefinitionId(
				cpDefinitionId, ActionKeys.VIEW);
		}

		return cpDefinition;
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
			String externalReferenceCode, long companyId, boolean excludeDraft)
		throws PortalException {

		CPDefinition cpDefinition =
			cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, companyId, excludeDraft);

		if (cpDefinition != null) {
			_checkCommerceCatalogByCPDefinitionId(
				cpDefinition.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinition;
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
			String externalReferenceCode, long companyId, int status)
		throws PortalException {

		CPDefinition cpDefinition =
			cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, companyId, status);

		if (cpDefinition != null) {
			_checkCommerceCatalogByCPDefinitionId(
				cpDefinition.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinition;
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(
			long cProductId, boolean excludeDraft)
		throws PortalException {

		CPDefinition cpDefinition =
			cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				cProductId, excludeDraft);

		if (cpDefinition != null) {
			_checkCommerceCatalogByCPDefinitionId(
				cpDefinition.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinition;
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(
			long cProductId, int status)
		throws PortalException {

		CPDefinition cpDefinition =
			cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				cProductId, status);

		if (cpDefinition != null) {
			_checkCommerceCatalogByCPDefinitionId(
				cpDefinition.getCPDefinitionId(), ActionKeys.VIEW);
		}

		return cpDefinition;
	}

	@Override
	public CPDefinition getCPDefinition(long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionLocalService.getCPDefinition(cpDefinitionId);
	}

	@Override
	public List<CPDefinition> getCPDefinitions(
			long groupId, int status, int start, int end,
			OrderByComparator<CPDefinition> orderByComparator)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.VIEW);

		return cpDefinitionLocalService.getCPDefinitions(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionsCount(long groupId, int status)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.VIEW);

		return cpDefinitionLocalService.getCPDefinitionsCount(groupId, status);
	}

	@Override
	public CPDefinition getCProductCPDefinition(long cProductId, int version)
		throws PortalException {

		CProduct cProduct = _cProductLocalService.getCProduct(cProductId);

		_checkCommerceCatalog(cProduct.getGroupId(), ActionKeys.VIEW);

		return cpDefinitionLocalService.getCProductCPDefinition(
			cProductId, version);
	}

	@Override
	public List<CPDefinition> getCProductCPDefinitions(
			long cProductId, int status, int start, int end)
		throws PortalException {

		CProduct cProduct = _cProductLocalService.getCProduct(cProductId);

		_checkCommerceCatalog(cProduct.getGroupId(), ActionKeys.VIEW);

		return cpDefinitionLocalService.getCProductCPDefinitions(
			cProduct.getCProductId(), status, start, end);
	}

	@Override
	public CPAttachmentFileEntry getDefaultImageCPAttachmentFileEntry(
			long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionLocalService.getDefaultImageCPAttachmentFileEntry(
			cpDefinitionId);
	}

	@Override
	public Map<Locale, String> getUrlTitleMap(long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionLocalService.getUrlTitleMap(cpDefinitionId);
	}

	@Override
	public String getUrlTitleMapAsXML(long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(cpDefinitionId, ActionKeys.VIEW);

		return cpDefinitionLocalService.getUrlTitleMapAsXML(cpDefinitionId);
	}

	@Override
	public boolean isVersionable(CPDefinition cpDefinition)
		throws PortalException {

		if (cpDefinition == null) {
			return false;
		}

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinition.getCPDefinitionId(), ActionKeys.VIEW);

		return cpDefinitionLocalService.isVersionable(cpDefinition);
	}

	@Override
	public BaseModelSearchResult<CPDefinition> searchCPDefinitions(
			long companyId, String keywords, int status,
			boolean ignoreCommerceAccountGroup, int start, int end, Sort sort)
		throws PortalException {

		return cpDefinitionLocalService.searchCPDefinitions(
			companyId,
			TransformUtil.transformToLongArray(
				_commerceCatalogService.search(
					companyId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null),
				CommerceCatalog::getGroupId),
			keywords, status, ignoreCommerceAccountGroup, start, end, sort);
	}

	@Override
	public BaseModelSearchResult<CPDefinition> searchCPDefinitions(
			long companyId, String keywords, String filterFields,
			String filterValues, int start, int end, Sort sort)
		throws PortalException {

		return cpDefinitionLocalService.searchCPDefinitions(
			companyId,
			TransformUtil.transformToLongArray(
				_commerceCatalogService.search(
					companyId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null),
				CommerceCatalog::getGroupId),
			keywords, filterFields, filterValues, start, end, sort);
	}

	@Override
	public BaseModelSearchResult<CPDefinition>
			searchCPDefinitionsByChannelGroupId(
				long companyId, long commerceChannelGroupId, String keywords,
				int status, boolean ignoreCommerceAccountGroup, int start,
				int end, Sort sort)
		throws PortalException {

		return cpDefinitionLocalService.searchCPDefinitionsByChannelGroupId(
			companyId,
			TransformUtil.transformToLongArray(
				_commerceCatalogService.getCommerceCatalogs(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				CommerceCatalog::getGroupId),
			commerceChannelGroupId, keywords, status,
			ignoreCommerceAccountGroup, start, end, sort);
	}

	@Override
	public CPDefinition updateCPDefinition(
			long cpDefinitionId, long cpTaxCategoryId,
			boolean accountGroupFilterEnabled, boolean channelFilterEnabled,
			String ddmStructureKey, double depth,
			Map<Locale, String> descriptionMap, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, boolean freeShipping, double height,
			boolean ignoreSKUCombinations,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap,
			Map<Locale, String> metaTitleMap, Map<Locale, String> nameMap,
			boolean neverExpire, boolean published, boolean shipSeparately,
			boolean shippable, double shippingExtraPrice,
			Map<Locale, String> shortDescriptionMap, boolean taxExempt,
			boolean telcoOrElectronics, Map<Locale, String> urlTitleMap,
			double weight, double width, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateCPDefinition(
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
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateCPDefinitionAccountGroupFilter(
			cpDefinitionId, enable);
	}

	@Override
	public CPDefinition updateCPDefinitionCategorization(
			long cpDefinitionId, ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateCPDefinitionCategorization(
			cpDefinitionId, serviceContext);
	}

	@Override
	public CPDefinition updateCPDefinitionChannelFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateCPDefinitionChannelFilter(
			cpDefinitionId, enable);
	}

	@Override
	public CPDefinition updateExternalReferenceCode(
			String externalReferenceCode, long cpDefinitionId)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateExternalReferenceCode(
			externalReferenceCode, cpDefinitionId);
	}

	@Override
	public CPDefinition updateShippingInfo(
			long cpDefinitionId, boolean shippable, boolean freeShipping,
			boolean shipSeparately, double shippingExtraPrice, double width,
			double height, double depth, double weight,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateShippingInfo(
			cpDefinitionId, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight, serviceContext);
	}

	@Override
	public CPDefinition updateStatus(
			long cpDefinitionId, int status, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateStatus(
			getUserId(), cpDefinitionId, status, serviceContext,
			workflowContext);
	}

	@Override
	public CPDefinition updateSubscriptionInfo(
			long cpDefinitionId, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateSubscriptionInfo(
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
		throws PortalException {

		_checkCommerceCatalogByCPDefinitionId(
			cpDefinitionId, ActionKeys.UPDATE);

		return cpDefinitionLocalService.updateTaxCategoryInfo(
			cpDefinitionId, cpTaxCategoryId, taxExempt, telcoOrElectronics);
	}

	private void _checkCommerceCatalog(long groupId, String actionId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	private void _checkCommerceCatalogByCPDefinitionId(
			long cpDefinitionId, String actionId)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException();
		}

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpDefinition.getGroupId());

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CProductLocalService _cProductLocalService;

}