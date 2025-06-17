/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.CPDefinitionDeliveryMaxSubscriptionCyclesException;
import com.liferay.commerce.product.exception.CPDefinitionDisplayDateException;
import com.liferay.commerce.product.exception.CPDefinitionExpirationDateException;
import com.liferay.commerce.product.exception.CPDefinitionIgnoreSKUCombinationsException;
import com.liferay.commerce.product.exception.CPDefinitionMaxSubscriptionCyclesException;
import com.liferay.commerce.product.exception.CPDefinitionMetaDescriptionException;
import com.liferay.commerce.product.exception.CPDefinitionMetaKeywordsException;
import com.liferay.commerce.product.exception.CPDefinitionMetaTitleException;
import com.liferay.commerce.product.exception.CPDefinitionProductTypeNameException;
import com.liferay.commerce.product.exception.CPDefinitionSubscriptionLengthException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.model.impl.CPDefinitionImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionModelImpl;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.service.base.CPDefinitionLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionLinkPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionSpecificationOptionValuePersistence;
import com.liferay.commerce.product.service.persistence.CPDisplayLayoutPersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.commerce.product.service.persistence.CProductPersistence;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.util.CPSubscriptionType;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.commerce.product.util.CPVersionContributor;
import com.liferay.commerce.product.util.CPVersionContributorRegistryUtil;
import com.liferay.commerce.product.util.comparator.CPDefinitionVersionComparator;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPDefinition",
	service = AopService.class
)
public class CPDefinitionLocalServiceImpl
	extends CPDefinitionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition addCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> urlTitleMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, String productTypeName,
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
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, int status,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce product definition

		User user = _userLocalService.getUser(userId);

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPDefinitionDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPDefinitionExpirationDateException.class);
		}

		_validate(
			groupId, ddmStructureKey, metaTitleMap, metaDescriptionMap,
			metaKeywordsMap, displayDate, expirationDate, productTypeName);
		_validateSubscriptionLength(subscriptionLength, "length");
		_validateSubscriptionCycles(
			maxSubscriptionCycles, "subscriptionCycles");
		_validateSubscriptionTypeSettingsUnicodeProperties(
			subscriptionType, subscriptionTypeSettingsUnicodeProperties);
		_validateSubscriptionLength(
			deliverySubscriptionLength, "deliverySubscriptionLength");
		_validateSubscriptionCycles(
			deliveryMaxSubscriptionCycles, "deliverySubscriptionCycles");
		_validateDeliverySubscriptionTypeSettingsUnicodeProperties(
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties);

		long cpDefinitionId = counterLocalService.increment();

		CPDefinition cpDefinition = cpDefinitionPersistence.create(
			cpDefinitionId);

		CProduct cProduct = _cProductLocalService.addCProduct(
			externalReferenceCode, userId, groupId, new ServiceContext());

		cpDefinition.setGroupId(groupId);
		cpDefinition.setCompanyId(user.getCompanyId());
		cpDefinition.setUserId(user.getUserId());
		cpDefinition.setUserName(user.getFullName());
		cpDefinition.setCProductId(cProduct.getCProductId());
		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setProductTypeName(productTypeName);
		cpDefinition.setIgnoreSKUCombinations(ignoreSKUCombinations);
		cpDefinition.setFreeShipping(freeShipping);
		cpDefinition.setShipSeparately(shipSeparately);

		if (StringUtil.equalsIgnoreCase(
				productTypeName, VirtualCPTypeConstants.NAME)) {

			cpDefinition.setShippable(false);
		}
		else {
			cpDefinition.setShippable(shippable);
		}

		cpDefinition.setShippingExtraPrice(shippingExtraPrice);
		cpDefinition.setWidth(width);
		cpDefinition.setHeight(height);
		cpDefinition.setDepth(depth);
		cpDefinition.setWeight(weight);
		cpDefinition.setTaxExempt(taxExempt);
		cpDefinition.setTelcoOrElectronics(telcoOrElectronics);
		cpDefinition.setDDMStructureKey(ddmStructureKey);
		cpDefinition.setPublished(published);
		cpDefinition.setDisplayDate(displayDate);
		cpDefinition.setExpirationDate(expirationDate);
		cpDefinition.setSubscriptionEnabled(subscriptionEnabled);
		cpDefinition.setSubscriptionLength(subscriptionLength);
		cpDefinition.setSubscriptionType(subscriptionType);
		cpDefinition.setSubscriptionTypeSettingsUnicodeProperties(
			subscriptionTypeSettingsUnicodeProperties);
		cpDefinition.setMaxSubscriptionCycles(maxSubscriptionCycles);
		cpDefinition.setDeliverySubscriptionEnabled(
			deliverySubscriptionEnabled);
		cpDefinition.setDeliverySubscriptionLength(deliverySubscriptionLength);
		cpDefinition.setDeliverySubscriptionType(deliverySubscriptionType);
		cpDefinition.setDeliverySubscriptionTypeSettingsUnicodeProperties(
			deliverySubscriptionTypeSettingsUnicodeProperties);
		cpDefinition.setDeliveryMaxSubscriptionCycles(
			deliveryMaxSubscriptionCycles);
		cpDefinition.setAccountGroupFilterEnabled(false);
		cpDefinition.setChannelFilterEnabled(false);
		cpDefinition.setVersion(1);

		if ((expirationDate == null) || expirationDate.after(date)) {
			cpDefinition.setStatus(status);
		}
		else {
			cpDefinition.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpDefinition.setStatusByUserId(user.getUserId());
		cpDefinition.setStatusDate(serviceContext.getModifiedDate(date));
		cpDefinition.setExpandoBridgeAttributes(serviceContext);

		cpDefinition = cpDefinitionPersistence.update(cpDefinition);

		// Commerce product definition localization

		_addCPDefinitionLocalizedFields(
			user.getCompanyId(), cpDefinitionId, nameMap, shortDescriptionMap,
			descriptionMap, metaTitleMap, metaDescriptionMap, metaKeywordsMap);

		// Commerce product instance

		if (Validator.isNotNull(defaultSku)) {
			ServiceContext cpInstanceServiceContext = new ServiceContext();

			cpInstanceServiceContext.setScopeGroupId(groupId);
			cpInstanceServiceContext.setUserId(userId);

			_cpInstanceLocalService.addCPInstance(
				externalReferenceCode, cpDefinitionId, groupId, defaultSku,
				null, null, published, null, cpDefinition.getWidth(),
				cpDefinition.getHeight(), cpDefinition.getDepth(),
				cpDefinition.getWeight(), BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, published, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, neverExpire, false,
				false, 1, StringPool.BLANK, null, 0, false, 1, null, null, 0,
				null, false, null, 0, 0, 0, 0, cpInstanceServiceContext);
		}

		// Friendly URL

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpDefinition.getCompanyId());

		Map<Locale, String> newURLTitleMap = new HashMap<>();

		if (MapUtil.isEmpty(urlTitleMap)) {
			newURLTitleMap = _getUniqueUrlTitles(cpDefinition, nameMap);
		}
		else {
			newURLTitleMap = _getUniqueUrlTitles(cpDefinition, urlTitleMap);
		}

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			companyGroup.getGroupId(),
			_classNameLocalService.getClassNameId(CProduct.class),
			cProduct.getCProductId(), _toLanguageIdMap(newURLTitleMap),
			serviceContext);

		// Asset

		updateAsset(
			user.getUserId(), cpDefinition,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Workflow

		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
				CPDefinition.class.getName(), 0)) {

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		return _startWorkflowInstance(
			user.getUserId(), cpDefinition, serviceContext);
	}

	@Override
	public CPDefinition addCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> urlTitleMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, String productTypeName,
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
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int status,
			ServiceContext serviceContext)
		throws PortalException {

		return cpDefinitionLocalService.addCPDefinition(
			externalReferenceCode, userId, groupId, nameMap,
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
			maxSubscriptionCycles, false, 1, null, null, 0, status,
			serviceContext);
	}

	@Override
	public CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> urlTitleMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, String productTypeName,
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
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, int status,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			CProduct cProduct =
				_cProductLocalService.fetchCProductByExternalReferenceCode(
					externalReferenceCode, serviceContext.getCompanyId());

			if (cProduct != null) {
				CPDefinition cpDefinition =
					cpDefinitionLocalService.updateCPDefinition(
						cProduct.getPublishedCPDefinitionId(), nameMap,
						shortDescriptionMap, descriptionMap, urlTitleMap,
						metaTitleMap, metaDescriptionMap, metaKeywordsMap,
						ignoreSKUCombinations, shippable, freeShipping,
						shipSeparately, shippingExtraPrice, width, height,
						depth, weight, cpTaxCategoryId, taxExempt,
						telcoOrElectronics, ddmStructureKey, published,
						displayDateMonth, displayDateDay, displayDateYear,
						displayDateHour, displayDateMinute, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						serviceContext);

				return cpDefinitionLocalService.updateSubscriptionInfo(
					cpDefinition.getCPDefinitionId(), subscriptionEnabled,
					subscriptionLength, subscriptionType,
					subscriptionTypeSettingsUnicodeProperties,
					maxSubscriptionCycles, deliverySubscriptionEnabled,
					deliverySubscriptionLength, deliverySubscriptionType,
					deliverySubscriptionTypeSettingsUnicodeProperties,
					deliveryMaxSubscriptionCycles);
			}
		}

		return cpDefinitionLocalService.addCPDefinition(
			externalReferenceCode, userId, groupId, nameMap,
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
			deliveryMaxSubscriptionCycles, status, serviceContext);
	}

	@Override
	public CPDefinition addOrUpdateCPDefinition(
			String externalReferenceCode, long userId, long groupId,
			Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> urlTitleMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, String productTypeName,
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
			boolean neverExpire, String defaultSku, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int status,
			ServiceContext serviceContext)
		throws PortalException {

		return cpDefinitionLocalService.addOrUpdateCPDefinition(
			externalReferenceCode, userId, groupId, nameMap,
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
			maxSubscriptionCycles, false, 1, null, null, 0, status,
			serviceContext);
	}

	@Override
	public void checkCPDefinitions() throws PortalException {
		_checkCPDefinitionsByDisplayDate();
		_checkCPDefinitionsByExpirationDate();
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition cloneCPDefinition(
			long userId, long cpDefinitionId, long groupId,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CPDefinition originalCPDefinition =
			cpDefinitionLocalService.getCPDefinition(cpDefinitionId);

		CPDefinition newCPDefinition =
			(CPDefinition)originalCPDefinition.clone();

		newCPDefinition.setUuid(PortalUUIDUtil.generate());

		long newCPDefinitionId = counterLocalService.increment();

		newCPDefinition.setCPDefinitionId(newCPDefinitionId);

		newCPDefinition.setGroupId(groupId);
		newCPDefinition.setUserId(user.getUserId());
		newCPDefinition.setUserName(user.getFullName());

		CProduct originalCProduct = originalCPDefinition.getCProduct();

		CProduct newCProduct = (CProduct)originalCProduct.clone();

		newCProduct.setUuid(PortalUUIDUtil.generate());

		long cProductId = counterLocalService.increment();

		newCProduct.setExternalReferenceCode(String.valueOf(cProductId));
		newCProduct.setCProductId(cProductId);

		newCProduct.setUserId(user.getUserId());
		newCProduct.setUserName(user.getFullName());
		newCProduct.setPublishedCPDefinitionId(newCPDefinitionId);

		newCPDefinition.setCProductId(newCProduct.getCProductId());

		newCProduct = _cProductPersistence.update(newCProduct);

		newCPDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);

		newCPDefinition = cpDefinitionPersistence.update(newCPDefinition);

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			cpDefinitionClassNameId, cpDefinitionId);

		if (assetEntry != null) {
			AssetEntry newAssetEntry = (AssetEntry)assetEntry.clone();

			newAssetEntry.setEntryId(counterLocalService.increment());
			newAssetEntry.setClassPK(newCPDefinitionId);

			_assetEntryLocalService.addAssetEntry(newAssetEntry);
		}

		List<CPDefinitionLocalization> cpDefinitionLocalizations =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		Map<Locale, String> newNameMap = new HashMap<>();

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizations) {

			CPDefinitionLocalization newCPDefinitionLocalization =
				(CPDefinitionLocalization)cpDefinitionLocalization.clone();

			newCPDefinitionLocalization.setCpDefinitionLocalizationId(
				counterLocalService.increment());
			newCPDefinitionLocalization.setCPDefinitionId(newCPDefinitionId);

			if (originalCPDefinition.getCProductId() !=
					newCPDefinition.getCProductId()) {

				newCPDefinitionLocalization.setName(
					_language.format(
						LocaleUtil.fromLanguageId(
							newCPDefinitionLocalization.getLanguageId()),
						"copy-of-x", newCPDefinitionLocalization.getName()));
			}

			newCPDefinitionLocalization =
				cpDefinitionLocalizationPersistence.update(
					newCPDefinitionLocalization);

			newNameMap.put(
				LocaleUtil.fromLanguageId(
					newCPDefinitionLocalization.getLanguageId()),
				newCPDefinitionLocalization.getName());
		}

		Group companyGroup = _groupLocalService.getCompanyGroup(
			newCPDefinition.getCompanyId());

		Map<Locale, String> newURLTitleMap = _getUniqueUrlTitles(
			newCPDefinition, newNameMap);

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			companyGroup.getGroupId(),
			_classNameLocalService.getClassNameId(CProduct.class),
			newCProduct.getCProductId(), _toLanguageIdMap(newURLTitleMap),
			serviceContext);

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryPersistence.findByC_C(
				cpDefinitionClassNameId, cpDefinitionId);

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			CPAttachmentFileEntry newCPAttachmentFileEntry =
				(CPAttachmentFileEntry)cpAttachmentFileEntry.clone();

			newCPAttachmentFileEntry.setUuid(PortalUUIDUtil.generate());

			long cpAttachmentFileEntryId = counterLocalService.increment();

			newCPAttachmentFileEntry.setExternalReferenceCode(
				String.valueOf(cpAttachmentFileEntryId));
			newCPAttachmentFileEntry.setCPAttachmentFileEntryId(
				cpAttachmentFileEntryId);

			newCPAttachmentFileEntry.setClassPK(newCPDefinitionId);

			_cpAttachmentFileEntryPersistence.update(newCPAttachmentFileEntry);
		}

		List<CPDefinitionLink> cpDefinitionLinks =
			_cpDefinitionLinkPersistence.findByCPDefinitionId(cpDefinitionId);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			CPDefinitionLink newCPDefinitionLink =
				(CPDefinitionLink)cpDefinitionLink.clone();

			newCPDefinitionLink.setUuid(PortalUUIDUtil.generate());
			newCPDefinitionLink.setCPDefinitionLinkId(
				counterLocalService.increment());
			newCPDefinitionLink.setCPDefinitionId(newCPDefinitionId);

			_cpDefinitionLinkPersistence.update(newCPDefinitionLink);
		}

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelPersistence.findByCPDefinitionId(
				cpDefinitionId);

		List<CPDefinitionOptionRel> newCPDefinitionOptionRels = new ArrayList<>(
			cpDefinitionOptionRels.size());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			CPDefinitionOptionRel newCPDefinitionOptionRel =
				(CPDefinitionOptionRel)cpDefinitionOptionRel.clone();

			newCPDefinitionOptionRel.setUuid(PortalUUIDUtil.generate());

			long newCPDefinitionOptionRelId = counterLocalService.increment();

			newCPDefinitionOptionRel.setCPDefinitionOptionRelId(
				newCPDefinitionOptionRelId);

			newCPDefinitionOptionRel.setCPDefinitionId(newCPDefinitionId);

			newCPDefinitionOptionRel = _cpDefinitionOptionRelPersistence.update(
				newCPDefinitionOptionRel);

			newCPDefinitionOptionRels.add(newCPDefinitionOptionRel);

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_cpDefinitionOptionValueRelPersistence.
					findByCPDefinitionOptionRelId(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
					cpDefinitionOptionValueRels) {

				CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
					(CPDefinitionOptionValueRel)
						cpDefinitionOptionValueRel.clone();

				newCPDefinitionOptionValueRel.setUuid(
					PortalUUIDUtil.generate());
				newCPDefinitionOptionValueRel.setCPDefinitionOptionValueRelId(
					counterLocalService.increment());
				newCPDefinitionOptionValueRel.setCPDefinitionOptionRelId(
					newCPDefinitionOptionRelId);

				_cpDefinitionOptionValueRelPersistence.update(
					newCPDefinitionOptionValueRel);
			}

			_reindexCPDefinitionOptionValueRels(newCPDefinitionOptionRel);
		}

		_reindexCPDefinitionOptionRels(newCPDefinition);

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValuePersistence.
					findByCPDefinitionId(cpDefinitionId);

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			CPDefinitionSpecificationOptionValue
				newCPDefinitionSpecificationOptionValue =
					(CPDefinitionSpecificationOptionValue)
						cpDefinitionSpecificationOptionValue.clone();

			newCPDefinitionSpecificationOptionValue.setUuid(
				PortalUUIDUtil.generate());
			newCPDefinitionSpecificationOptionValue.setExternalReferenceCode(
				null);
			newCPDefinitionSpecificationOptionValue.
				setCPDefinitionSpecificationOptionValueId(
					counterLocalService.increment());
			newCPDefinitionSpecificationOptionValue.setCPDefinitionId(
				newCPDefinitionId);

			_cpDefinitionSpecificationOptionValuePersistence.update(
				newCPDefinitionSpecificationOptionValue);
		}

		List<CPDisplayLayout> cpDisplayLayouts =
			_cpDisplayLayoutPersistence.findByC_C(
				cpDefinitionClassNameId, cpDefinitionId);

		for (CPDisplayLayout cpDisplayLayout : cpDisplayLayouts) {
			CPDisplayLayout newCPDisplayLayout =
				(CPDisplayLayout)cpDisplayLayout.clone();

			newCPDisplayLayout.setUuid(PortalUUIDUtil.generate());
			newCPDisplayLayout.setCPDisplayLayoutId(
				counterLocalService.increment());
			newCPDisplayLayout.setClassPK(newCPDefinitionId);

			_cpDisplayLayoutPersistence.update(newCPDisplayLayout);
		}

		List<CPInstance> cpInstances =
			_cpInstancePersistence.findByCPDefinitionId(cpDefinitionId);

		for (CPInstance cpInstance : cpInstances) {
			CPInstance newCPInstance = (CPInstance)cpInstance.clone();

			newCPInstance.setUuid(PortalUUIDUtil.generate());

			long cpInstanceId = counterLocalService.increment();

			newCPInstance.setExternalReferenceCode(
				String.valueOf(cpInstanceId));
			newCPInstance.setCPInstanceId(cpInstanceId);

			newCPInstance.setCPDefinitionId(newCPDefinitionId);
			newCPInstance.setCPInstanceUuid(PortalUUIDUtil.generate());

			for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
					_cpInstanceOptionValueRelPersistence.findByCPInstanceId(
						cpInstance.getCPInstanceId())) {

				CPInstanceOptionValueRel newCPInstanceOptionValueRel =
					(CPInstanceOptionValueRel)cpInstanceOptionValueRel.clone();

				newCPInstanceOptionValueRel.setUuid(PortalUUIDUtil.generate());
				newCPInstanceOptionValueRel.setCPInstanceOptionValueRelId(
					counterLocalService.increment());
				newCPInstanceOptionValueRel.setCPInstanceId(
					newCPInstance.getCPInstanceId());

				CPDefinitionOptionRel cpDefinitionOptionRel =
					_cpDefinitionOptionRelPersistence.findByPrimaryKey(
						cpInstanceOptionValueRel.getCPDefinitionOptionRelId());

				for (CPDefinitionOptionRel newCPDefinitionOptionRel :
						newCPDefinitionOptionRels) {

					if (cpDefinitionOptionRel.getCPOptionId() !=
							newCPDefinitionOptionRel.getCPOptionId()) {

						continue;
					}

					long cpDefinitionOptionRelId =
						newCPDefinitionOptionRel.getCPDefinitionOptionRelId();

					newCPInstanceOptionValueRel.setCPDefinitionOptionRelId(
						cpDefinitionOptionRelId);

					for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
							cpDefinitionOptionRel.
								getCPDefinitionOptionValueRels()) {

						if (cpDefinitionOptionRelId !=
								cpDefinitionOptionValueRel.
									getCPDefinitionOptionRelId()) {

							continue;
						}

						newCPInstanceOptionValueRel.
							setCPInstanceOptionValueRelId(
								cpDefinitionOptionValueRel.
									getCPDefinitionOptionValueRelId());

						break;
					}

					break;
				}

				_cpInstanceOptionValueRelLocalService.
					updateCPInstanceOptionValueRel(newCPInstanceOptionValueRel);
			}

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			newCPInstance = _cpInstancePersistence.update(newCPInstance);

			_addCommercePriceEntry(
				newCPInstance, cpInstance.getCPInstanceUuid(),
				CommercePriceListConstants.TYPE_PRICE_LIST, serviceContext);
			_addCommercePriceEntry(
				newCPInstance, cpInstance.getCPInstanceUuid(),
				CommercePriceListConstants.TYPE_PROMOTION, serviceContext);
		}

		for (CommerceChannelRel commerceChannelRel :
				_commerceChannelRelLocalService.getCommerceChannelRels(
					originalCPDefinition.getModelClassName(),
					originalCPDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			_commerceChannelRelLocalService.addCommerceChannelRel(
				newCPDefinition.getModelClassName(), newCPDefinitionId,
				commerceChannelRel.getCommerceChannelId(), serviceContext);
		}

		for (AccountGroupRel accountGroupRel :
				_accountGroupRelLocalService.getAccountGroupRels(
					originalCPDefinition.getModelClassName(),
					originalCPDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			_accountGroupRelLocalService.addAccountGroupRel(
				accountGroupRel.getAccountGroupId(),
				newCPDefinition.getModelClassName(), newCPDefinitionId);
		}

		List<CPVersionContributor> cpVersionContributors =
			CPVersionContributorRegistryUtil.getCPVersionContributors();

		for (CPVersionContributor cpVersionContributor :
				cpVersionContributors) {

			cpVersionContributor.onUpdate(cpDefinitionId, newCPDefinitionId);
		}

		return newCPDefinition;
	}

	@Override
	public CPDefinition copyCPDefinition(long sourceCPDefinitionId)
		throws PortalException {

		CPDefinition sourceCPDefinition =
			cpDefinitionLocalService.getCPDefinition(sourceCPDefinitionId);

		return cpDefinitionLocalService.copyCPDefinition(
			sourceCPDefinitionId, sourceCPDefinition.getGroupId(),
			WorkflowConstants.STATUS_DRAFT);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition copyCPDefinition(
			long sourceCPDefinitionId, long groupId, int status)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CPDefinition sourceCPDefinition =
			cpDefinitionLocalService.getCPDefinition(sourceCPDefinitionId);

		CPDefinition targetCPDefinition =
			(CPDefinition)sourceCPDefinition.clone();

		targetCPDefinition.setUuid(PortalUUIDUtil.generate());

		long newCPDefinitionId = counterLocalService.increment();

		targetCPDefinition.setCPDefinitionId(newCPDefinitionId);

		targetCPDefinition.setGroupId(groupId);
		targetCPDefinition.setUserId(user.getUserId());
		targetCPDefinition.setUserName(user.getFullName());

		CProduct sourceCProduct = sourceCPDefinition.getCProduct();

		if (cpDefinitionLocalService.isVersionable(
				sourceCProduct.getPublishedCPDefinitionId())) {

			targetCPDefinition.setVersion(
				_cProductLocalService.increment(
					sourceCPDefinition.getCProductId()));

			if (status == WorkflowConstants.STATUS_APPROVED) {
				CPDefinition publishedCPDefinition =
					cpDefinitionLocalService.getCPDefinition(
						sourceCProduct.getPublishedCPDefinitionId());

				publishedCPDefinition.setPublished(false);

				publishedCPDefinition = cpDefinitionPersistence.update(
					publishedCPDefinition);

				_cProductLocalService.updatePublishedCPDefinitionId(
					publishedCPDefinition.getCProductId(),
					targetCPDefinition.getCPDefinitionId());

				long companyId = publishedCPDefinition.getCompanyId();
				long cProductId = publishedCPDefinition.getCProductId();

				TransactionCommitCallbackUtil.registerCallback(
					() -> {
						cpDefinitionLocalService.maintainVersionThreshold(
							companyId, cProductId);

						return null;
					});
			}
		}

		targetCPDefinition.setStatus(status);

		targetCPDefinition = cpDefinitionPersistence.update(targetCPDefinition);

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			cpDefinitionClassNameId, sourceCPDefinitionId);

		if (assetEntry != null) {
			AssetEntry newAssetEntry = (AssetEntry)assetEntry.clone();

			newAssetEntry.setEntryId(counterLocalService.increment());
			newAssetEntry.setClassPK(newCPDefinitionId);

			_assetEntryLocalService.addAssetEntry(newAssetEntry);
		}

		List<CPDefinitionLocalization> cpDefinitionLocalizations =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				sourceCPDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizations) {

			CPDefinitionLocalization newCPDefinitionLocalization =
				(CPDefinitionLocalization)cpDefinitionLocalization.clone();

			newCPDefinitionLocalization.setCpDefinitionLocalizationId(
				counterLocalService.increment());
			newCPDefinitionLocalization.setCPDefinitionId(newCPDefinitionId);

			if (sourceCPDefinition.getCProductId() !=
					targetCPDefinition.getCProductId()) {

				newCPDefinitionLocalization.setName(
					_language.format(
						LocaleUtil.fromLanguageId(
							newCPDefinitionLocalization.getLanguageId()),
						"copy-of-x", newCPDefinitionLocalization.getName()));
			}

			cpDefinitionLocalizationPersistence.update(
				newCPDefinitionLocalization);
		}

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryPersistence.findByC_C(
				cpDefinitionClassNameId, sourceCPDefinitionId);

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			CPAttachmentFileEntry newCPAttachmentFileEntry =
				(CPAttachmentFileEntry)cpAttachmentFileEntry.clone();

			newCPAttachmentFileEntry.setUuid(PortalUUIDUtil.generate());

			long cpAttachmentFileEntryId = counterLocalService.increment();

			newCPAttachmentFileEntry.setExternalReferenceCode(
				String.valueOf(cpAttachmentFileEntryId));
			newCPAttachmentFileEntry.setCPAttachmentFileEntryId(
				cpAttachmentFileEntryId);

			newCPAttachmentFileEntry.setClassPK(newCPDefinitionId);

			_cpAttachmentFileEntryPersistence.update(newCPAttachmentFileEntry);
		}

		List<CPDefinitionLink> cpDefinitionLinks =
			_cpDefinitionLinkPersistence.findByCPDefinitionId(
				sourceCPDefinitionId);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			CPDefinitionLink newCPDefinitionLink =
				(CPDefinitionLink)cpDefinitionLink.clone();

			newCPDefinitionLink.setUuid(PortalUUIDUtil.generate());
			newCPDefinitionLink.setCPDefinitionLinkId(
				counterLocalService.increment());
			newCPDefinitionLink.setCPDefinitionId(newCPDefinitionId);

			_cpDefinitionLinkPersistence.update(newCPDefinitionLink);
		}

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelPersistence.findByCPDefinitionId(
				sourceCPDefinitionId);

		List<CPDefinitionOptionRel> newCPDefinitionOptionRels = new ArrayList<>(
			cpDefinitionOptionRels.size());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			CPDefinitionOptionRel newCPDefinitionOptionRel =
				(CPDefinitionOptionRel)cpDefinitionOptionRel.clone();

			newCPDefinitionOptionRel.setUuid(PortalUUIDUtil.generate());

			long newCPDefinitionOptionRelId = counterLocalService.increment();

			newCPDefinitionOptionRel.setCPDefinitionOptionRelId(
				newCPDefinitionOptionRelId);

			newCPDefinitionOptionRel.setCPDefinitionId(newCPDefinitionId);

			newCPDefinitionOptionRel = _cpDefinitionOptionRelPersistence.update(
				newCPDefinitionOptionRel);

			newCPDefinitionOptionRels.add(newCPDefinitionOptionRel);

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_cpDefinitionOptionValueRelPersistence.
					findByCPDefinitionOptionRelId(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
					cpDefinitionOptionValueRels) {

				CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
					(CPDefinitionOptionValueRel)
						cpDefinitionOptionValueRel.clone();

				newCPDefinitionOptionValueRel.setUuid(
					PortalUUIDUtil.generate());
				newCPDefinitionOptionValueRel.setCPDefinitionOptionValueRelId(
					counterLocalService.increment());
				newCPDefinitionOptionValueRel.setCPDefinitionOptionRelId(
					newCPDefinitionOptionRelId);

				_cpDefinitionOptionValueRelPersistence.update(
					newCPDefinitionOptionValueRel);
			}

			_reindexCPDefinitionOptionValueRels(newCPDefinitionOptionRel);
		}

		_reindexCPDefinitionOptionRels(targetCPDefinition);

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValuePersistence.
					findByCPDefinitionId(sourceCPDefinitionId);

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			CPDefinitionSpecificationOptionValue
				newCPDefinitionSpecificationOptionValue =
					(CPDefinitionSpecificationOptionValue)
						cpDefinitionSpecificationOptionValue.clone();

			newCPDefinitionSpecificationOptionValue.setUuid(
				PortalUUIDUtil.generate());

			long cpDefinitionSpecificationOptionValueId =
				counterLocalService.increment();

			newCPDefinitionSpecificationOptionValue.setExternalReferenceCode(
				String.valueOf(cpDefinitionSpecificationOptionValueId));
			newCPDefinitionSpecificationOptionValue.
				setCPDefinitionSpecificationOptionValueId(
					cpDefinitionSpecificationOptionValueId);

			newCPDefinitionSpecificationOptionValue.setCPDefinitionId(
				newCPDefinitionId);

			_cpDefinitionSpecificationOptionValuePersistence.update(
				newCPDefinitionSpecificationOptionValue);
		}

		List<CPDisplayLayout> cpDisplayLayouts =
			_cpDisplayLayoutPersistence.findByC_C(
				cpDefinitionClassNameId, sourceCPDefinitionId);

		for (CPDisplayLayout cpDisplayLayout : cpDisplayLayouts) {
			CPDisplayLayout newCPDisplayLayout =
				(CPDisplayLayout)cpDisplayLayout.clone();

			newCPDisplayLayout.setUuid(PortalUUIDUtil.generate());
			newCPDisplayLayout.setCPDisplayLayoutId(
				counterLocalService.increment());
			newCPDisplayLayout.setClassPK(newCPDefinitionId);

			_cpDisplayLayoutPersistence.update(newCPDisplayLayout);
		}

		List<CPInstance> cpInstances =
			_cpInstancePersistence.findByCPDefinitionId(sourceCPDefinitionId);

		for (CPInstance cpInstance : cpInstances) {
			CPInstance newCPInstance = (CPInstance)cpInstance.clone();

			newCPInstance.setUuid(PortalUUIDUtil.generate());

			long cpInstanceId = counterLocalService.increment();

			newCPInstance.setExternalReferenceCode(
				String.valueOf(cpInstanceId));
			newCPInstance.setCPInstanceId(cpInstanceId);

			newCPInstance.setCPDefinitionId(newCPDefinitionId);
			newCPInstance.setCPInstanceUuid(cpInstance.getCPInstanceUuid());

			for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
					_cpInstanceOptionValueRelPersistence.findByCPInstanceId(
						cpInstance.getCPInstanceId())) {

				CPInstanceOptionValueRel newCPInstanceOptionValueRel =
					(CPInstanceOptionValueRel)cpInstanceOptionValueRel.clone();

				newCPInstanceOptionValueRel.setUuid(PortalUUIDUtil.generate());
				newCPInstanceOptionValueRel.setCPInstanceOptionValueRelId(
					counterLocalService.increment());
				newCPInstanceOptionValueRel.setCPInstanceId(
					newCPInstance.getCPInstanceId());

				CPDefinitionOptionRel cpDefinitionOptionRel =
					_cpDefinitionOptionRelPersistence.findByPrimaryKey(
						cpInstanceOptionValueRel.getCPDefinitionOptionRelId());

				for (CPDefinitionOptionRel newCPDefinitionOptionRel :
						newCPDefinitionOptionRels) {

					if (cpDefinitionOptionRel.getCPOptionId() !=
							newCPDefinitionOptionRel.getCPOptionId()) {

						continue;
					}

					long cpDefinitionOptionRelId =
						newCPDefinitionOptionRel.getCPDefinitionOptionRelId();

					newCPInstanceOptionValueRel.setCPDefinitionOptionRelId(
						cpDefinitionOptionRelId);

					for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
							cpDefinitionOptionRel.
								getCPDefinitionOptionValueRels()) {

						if (cpDefinitionOptionRelId !=
								cpDefinitionOptionValueRel.
									getCPDefinitionOptionRelId()) {

							continue;
						}

						newCPInstanceOptionValueRel.
							setCPInstanceOptionValueRelId(
								cpDefinitionOptionValueRel.
									getCPDefinitionOptionValueRelId());

						break;
					}

					break;
				}

				_cpInstanceOptionValueRelLocalService.
					updateCPInstanceOptionValueRel(newCPInstanceOptionValueRel);
			}

			_cpInstancePersistence.update(newCPInstance);
		}

		for (CommerceChannelRel commerceChannelRel :
				_commerceChannelRelLocalService.getCommerceChannelRels(
					sourceCPDefinition.getModelClassName(),
					sourceCPDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			_commerceChannelRelLocalService.addCommerceChannelRel(
				targetCPDefinition.getModelClassName(), newCPDefinitionId,
				commerceChannelRel.getCommerceChannelId(), serviceContext);
		}

		for (AccountGroupRel accountGroupRel :
				_accountGroupRelLocalService.getAccountGroupRels(
					sourceCPDefinition.getModelClassName(),
					sourceCPDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			_accountGroupRelLocalService.addAccountGroupRel(
				accountGroupRel.getAccountGroupId(),
				targetCPDefinition.getModelClassName(), newCPDefinitionId);
		}

		List<CPVersionContributor> cpVersionContributors =
			CPVersionContributorRegistryUtil.getCPVersionContributors();

		for (CPVersionContributor cpVersionContributor :
				cpVersionContributors) {

			cpVersionContributor.onUpdate(
				sourceCPDefinitionId, newCPDefinitionId);
		}

		return targetCPDefinition;
	}

	@Override
	public void deleteAssetCategoryCPDefinition(
			long cpDefinitionId, long categoryId, ServiceContext serviceContext)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			CPDefinition.class.getName(), cpDefinitionId);

		long[] categoryIds = ArrayUtil.remove(
			assetEntry.getCategoryIds(), categoryId);

		serviceContext.setAssetCategoryIds(categoryIds);

		serviceContext.setAssetTagNames(assetEntry.getTagNames());

		cpDefinitionLocalService.updateCPDefinitionCategorization(
			cpDefinitionId, serviceContext);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinition deleteCPDefinition(CPDefinition cpDefinition)
		throws PortalException {

		int cpDefinitionsCount = cpDefinitionPersistence.countByCProductId(
			cpDefinition.getCProductId());

		if (cpDefinitionsCount == 1) {
			_cProductLocalService.deleteCProduct(cpDefinition.getCProductId());
		}

		if (cpDefinitionsCount > 1) {
			CProduct cProduct = _cProductLocalService.getCProduct(
				cpDefinition.getCProductId());

			long publishedCPDefinitionId =
				cProduct.getPublishedCPDefinitionId();

			if (publishedCPDefinitionId == cpDefinition.getCPDefinitionId()) {
				List<CPDefinition> cpDefinitions =
					cpDefinitionLocalService.getCProductCPDefinitions(
						cProduct.getCProductId(),
						WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS,
						CPDefinitionVersionComparator.getInstance(false));

				if (ListUtil.isEmpty(cpDefinitions)) {
					_cProductLocalService.updatePublishedCPDefinitionId(
						cProduct.getCProductId(), 0);
				}
				else {
					List<CPDefinition> lastApprovedCPDefinitions =
						ListUtil.filter(
							cpDefinitions,
							curCPDefinition ->
								curCPDefinition.getCPDefinitionId() !=
									cpDefinition.getCPDefinitionId());

					if (ListUtil.isEmpty(lastApprovedCPDefinitions)) {
						_cProductLocalService.updatePublishedCPDefinitionId(
							cProduct.getCProductId(), 0);
					}
					else {
						CPDefinition lastApprovedCPDefinition =
							lastApprovedCPDefinitions.get(0);

						_cProductLocalService.updatePublishedCPDefinitionId(
							cProduct.getCProductId(),
							lastApprovedCPDefinition.getCPDefinitionId());
					}
				}
			}
		}

		_cpDefinitionSpecificationOptionValueLocalService.
			deleteCPDefinitionSpecificationOptionValues(
				cpDefinition.getCPDefinitionId(), false);

		// Commerce product instances

		_cpInstanceLocalService.deleteCPInstances(
			cpDefinition.getCPDefinitionId());

		// Commerce product definition option rels

		_cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRels(
			cpDefinition.getCPDefinitionId());

		// Commerce product definition attachment file entries

		_cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntries(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		// Commerce product definition links

		_cpDefinitionLinkLocalService.deleteCPDefinitionLinksByCPDefinitionId(
			cpDefinition.getCPDefinitionId());

		// Commerce product type

		CPType cpType = _cpTypeRegistry.getCPType(
			cpDefinition.getProductTypeName());

		if (cpType != null) {
			cpType.deleteCPDefinition(cpDefinition.getCPDefinitionId());
		}

		// Commerce product friendly URL entries

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpDefinition.getCompanyId());

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			companyGroup.getGroupId(), CProduct.class,
			cpDefinition.getCProductId());

		// Commerce product configuration entries

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntries(
			_portal.getClassNameId(CPDefinition.class),
			cpDefinition.getCPDefinitionId(), true);

		// Commerce product display layouts

		_cpDisplayLayoutLocalService.deleteCPDisplayLayouts(
			CPDefinition.class, cpDefinition.getCPDefinitionId());

		// Commerce product version contributors

		List<CPVersionContributor> cpVersionContributors =
			CPVersionContributorRegistryUtil.getCPVersionContributors();

		for (CPVersionContributor cpVersionContributor :
				cpVersionContributors) {

			cpVersionContributor.onDelete(cpDefinition.getCPDefinitionId());
		}

		// Commerce product definition

		cpDefinitionPersistence.remove(cpDefinition);

		// Asset

		_assetEntryLocalService.deleteEntry(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		// Expando

		_expandoRowLocalService.deleteRows(cpDefinition.getCPDefinitionId());

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		return cpDefinition;
	}

	@Override
	public CPDefinition deleteCPDefinition(long cpDefinitionId)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		return cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
	}

	@Override
	public void deleteCPDefinitions(long companyId) throws PortalException {
		List<CPDefinition> cpDefinitions =
			cpDefinitionPersistence.findByCompanyId(companyId);

		for (CPDefinition cpDefinition : cpDefinitions) {
			cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
		}
	}

	@Override
	public void deleteCPDefinitions(long cProductId, int status) {
		cpDefinitionPersistence.removeByC_S(cProductId, status);
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		CProduct cProduct =
			_cProductLocalService.fetchCProductByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (cProduct == null) {
			return null;
		}

		CPDefinition cpDefinition = cpDefinitionPersistence.fetchByPrimaryKey(
			cProduct.getPublishedCPDefinitionId());

		if (cpDefinition != null) {
			return cpDefinition;
		}

		return cpDefinitionPersistence.fetchByC_V(
			cProduct.getCProductId(), cProduct.getLatestVersion());
	}

	@Override
	public CPDefinition fetchCPDefinitionByCProductId(long cProductId) {
		CProduct cProduct = _cProductLocalService.fetchCProduct(cProductId);

		if (cProduct == null) {
			return null;
		}

		CPDefinition cpDefinition = cpDefinitionPersistence.fetchByPrimaryKey(
			cProduct.getPublishedCPDefinitionId());

		if (cpDefinition != null) {
			return cpDefinition;
		}

		return cpDefinitionPersistence.fetchByC_V(
			cProduct.getCProductId(), cProduct.getLatestVersion());
	}

	@Override
	public CPDefinition fetchCPDefinitionByFriendlyURL(
		long groupId, String friendlyURL) {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, _classNameLocalService.getClassNameId(CProduct.class),
				friendlyURL);

		if (friendlyURLEntry == null) {
			return null;
		}

		return cpDefinitionLocalService.fetchCPDefinitionByCProductId(
			friendlyURLEntry.getClassPK());
	}

	@Override
	public Map<Locale, String> getCPDefinitionDescriptionMap(
		long cpDefinitionId) {

		Map<Locale, String> cpDefinitionLocalizationDescriptionMap =
			new HashMap<>();

		List<CPDefinitionLocalization> cpDefinitionLocalizationList =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizationList) {

			cpDefinitionLocalizationDescriptionMap.put(
				LocaleUtil.fromLanguageId(
					cpDefinitionLocalization.getLanguageId()),
				cpDefinitionLocalization.getDescription());
		}

		return cpDefinitionLocalizationDescriptionMap;
	}

	@Override
	public List<String> getCPDefinitionLocalizationLanguageIds(
		long cpDefinitionId) {

		return TransformUtil.transform(
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId),
			cpDefinitionLocalization ->
				cpDefinitionLocalization.getLanguageId());
	}

	@Override
	public Map<Locale, String> getCPDefinitionMetaDescriptionMap(
		long cpDefinitionId) {

		Map<Locale, String> cpDefinitionLocalizationMetaDescriptionMap =
			new HashMap<>();

		List<CPDefinitionLocalization> cpDefinitionLocalizationList =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizationList) {

			cpDefinitionLocalizationMetaDescriptionMap.put(
				LocaleUtil.fromLanguageId(
					cpDefinitionLocalization.getLanguageId()),
				cpDefinitionLocalization.getMetaDescription());
		}

		return cpDefinitionLocalizationMetaDescriptionMap;
	}

	@Override
	public Map<Locale, String> getCPDefinitionMetaKeywordsMap(
		long cpDefinitionId) {

		Map<Locale, String> cpDefinitionLocalizationMetaKeywordsMap =
			new HashMap<>();

		List<CPDefinitionLocalization> cpDefinitionLocalizationList =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizationList) {

			cpDefinitionLocalizationMetaKeywordsMap.put(
				LocaleUtil.fromLanguageId(
					cpDefinitionLocalization.getLanguageId()),
				cpDefinitionLocalization.getMetaKeywords());
		}

		return cpDefinitionLocalizationMetaKeywordsMap;
	}

	@Override
	public Map<Locale, String> getCPDefinitionMetaTitleMap(
		long cpDefinitionId) {

		Map<Locale, String> cpDefinitionLocalizationMetaTitleMap =
			new HashMap<>();

		List<CPDefinitionLocalization> cpDefinitionLocalizationList =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizationList) {

			cpDefinitionLocalizationMetaTitleMap.put(
				LocaleUtil.fromLanguageId(
					cpDefinitionLocalization.getLanguageId()),
				cpDefinitionLocalization.getMetaTitle());
		}

		return cpDefinitionLocalizationMetaTitleMap;
	}

	@Override
	public Map<Locale, String> getCPDefinitionNameMap(long cpDefinitionId) {
		Map<Locale, String> cpDefinitionLocalizationNameMap = new HashMap<>();

		List<CPDefinitionLocalization> cpDefinitionLocalizationList =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizationList) {

			cpDefinitionLocalizationNameMap.put(
				LocaleUtil.fromLanguageId(
					cpDefinitionLocalization.getLanguageId()),
				cpDefinitionLocalization.getName());
		}

		return cpDefinitionLocalizationNameMap;
	}

	@Override
	public List<CPDefinition> getCPDefinitions(
		long groupId, boolean subscriptionEnabled) {

		return cpDefinitionPersistence.findByG_SE(groupId, subscriptionEnabled);
	}

	@Override
	public List<CPDefinition> getCPDefinitions(
		long groupId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return cpDefinitionPersistence.findByGroupId(groupId, start, end);
		}

		return cpDefinitionPersistence.findByG_S(groupId, status, start, end);
	}

	@Override
	public List<CPDefinition> getCPDefinitions(
		long groupId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return cpDefinitionPersistence.findByGroupId(
				groupId, start, end, orderByComparator);
		}

		return cpDefinitionPersistence.findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public List<CPDefinition> getCPDefinitions(
		long groupId, String productTypeName, String languageId, int status,
		int start, int end, OrderByComparator<CPDefinition> orderByComparator) {

		QueryDefinition<CPDefinition> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return cpDefinitionFinder.findByG_P_S(
			groupId, productTypeName, languageId, queryDefinition);
	}

	@Override
	public int getCPDefinitionsCount(
		long groupId, boolean subscriptionEnabled) {

		return cpDefinitionPersistence.countByG_SE(
			groupId, subscriptionEnabled);
	}

	@Override
	public int getCPDefinitionsCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return cpDefinitionPersistence.countByGroupId(groupId);
		}

		return cpDefinitionPersistence.countByG_S(groupId, status);
	}

	@Override
	public int getCPDefinitionsCount(
		long groupId, String productTypeName, String languageId, int status) {

		QueryDefinition<CPDefinition> queryDefinition = new QueryDefinition<>(
			status);

		return cpDefinitionFinder.countByG_P_S(
			groupId, productTypeName, languageId, queryDefinition);
	}

	@Override
	public Map<Locale, String> getCPDefinitionShortDescriptionMap(
		long cpDefinitionId) {

		Map<Locale, String> cpDefinitionLocalizationShortDescriptionMap =
			new HashMap<>();

		List<CPDefinitionLocalization> cpDefinitionLocalizationList =
			cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizationList) {

			cpDefinitionLocalizationShortDescriptionMap.put(
				LocaleUtil.fromLanguageId(
					cpDefinitionLocalization.getLanguageId()),
				cpDefinitionLocalization.getShortDescription());
		}

		return cpDefinitionLocalizationShortDescriptionMap;
	}

	@Override
	public CPDefinition getCProductCPDefinition(long cProductId, int version)
		throws PortalException {

		return cpDefinitionPersistence.findByC_V(cProductId, version);
	}

	@Override
	public List<CPDefinition> getCProductCPDefinitions(
		long cProductId, int status, int start, int end) {

		return cpDefinitionPersistence.findByC_S(
			cProductId, status, start, end);
	}

	@Override
	public List<CPDefinition> getCProductCPDefinitions(
		long cProductId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return cpDefinitionPersistence.findByC_S(
			cProductId, status, start, end, orderByComparator);
	}

	@Override
	public CPAttachmentFileEntry getDefaultImageCPAttachmentFileEntry(
			long cpDefinitionId)
		throws PortalException {

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpDefinitionId, CPAttachmentFileEntryConstants.TYPE_IMAGE,
				WorkflowConstants.STATUS_APPROVED, 0, 1);

		if (cpAttachmentFileEntries.isEmpty()) {
			return null;
		}

		return cpAttachmentFileEntries.get(0);
	}

	@Override
	public List<Facet> getFacets(
		String filterFields, String filterValues, SearchContext searchContext) {

		List<Facet> facets = new ArrayList<>();

		if (Validator.isNotNull(filterFields) &&
			Validator.isNotNull(filterValues)) {

			Map<String, List<String>> facetMap = new HashMap<>();

			String[] filterFieldsArray = StringUtil.split(filterFields);
			String[] filterValuesArray = StringUtil.split(filterValues);

			for (int i = 0; i < filterFieldsArray.length; i++) {
				String key = filterFieldsArray[i];
				String value = filterValuesArray[i];

				if (key.startsWith("OPTION_")) {
					key = StringUtil.removeSubstring(key, "OPTION_");

					key = _getIndexFieldName(
						key, searchContext.getLanguageId());
				}

				List<String> facetValues = null;

				if (facetMap.containsKey(key)) {
					facetValues = facetMap.get(key);
				}

				if (facetValues == null) {
					facetValues = new ArrayList<>();
				}

				facetValues.add(value);

				facetMap.put(key, facetValues);
			}

			for (Map.Entry<String, List<String>> entry : facetMap.entrySet()) {
				String fieldName = entry.getKey();

				String[] values = ArrayUtil.toStringArray(entry.getValue());

				if (fieldName.equals("assetCategoryIds")) {
					searchContext.setAssetCategoryIds(
						TransformUtil.transformToLongArray(
							Arrays.asList(values), GetterUtil::getLong));
				}

				searchContext.setAttribute(fieldName, StringUtil.merge(values));

				MultiValueFacet multiValueFacet = new MultiValueFacet(
					searchContext);

				multiValueFacet.setFieldName(fieldName);
				multiValueFacet.setValues(values);

				facets.add(multiValueFacet);
			}
		}

		return facets;
	}

	@Override
	public String getLayoutPageTemplateEntryUuid(
		long groupId, long cpDefinitionId) {

		CPDisplayLayout cpDisplayLayout =
			_cpDisplayLayoutLocalService.fetchCPDisplayLayout(
				groupId, CPDefinition.class, cpDefinitionId);

		if (cpDisplayLayout == null) {
			return null;
		}

		return cpDisplayLayout.getLayoutPageTemplateEntryUuid();
	}

	@Override
	public String getLayoutUuid(long groupId, long cpDefinitionId) {
		CPDisplayLayout cpDisplayLayout =
			_cpDisplayLayoutLocalService.fetchCPDisplayLayout(
				groupId, CPDefinition.class, cpDefinitionId);

		if (cpDisplayLayout == null) {
			return null;
		}

		return cpDisplayLayout.getLayoutUuid();
	}

	@Override
	public Map<Locale, String> getUrlTitleMap(long cpDefinitionId) {
		CPDefinition cpDefinition = cpDefinitionPersistence.fetchByPrimaryKey(
			cpDefinitionId);

		if (cpDefinition == null) {
			return Collections.emptyMap();
		}

		Map<Locale, String> urlTitleMap = new HashMap<>();

		try {
			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					_classNameLocalService.getClassNameId(CProduct.class),
					cpDefinition.getCProductId());

			List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations =
				_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
					friendlyURLEntry.getFriendlyURLEntryId());

			for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
					friendlyURLEntryLocalizations) {

				urlTitleMap.put(
					LocaleUtil.fromLanguageId(
						friendlyURLEntryLocalization.getLanguageId()),
					friendlyURLEntryLocalization.getUrlTitle());
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return Collections.emptyMap();
		}

		return urlTitleMap;
	}

	@Override
	public String getUrlTitleMapAsXML(long cpDefinitionId)
		throws PortalException {

		try {
			CPDefinition cpDefinition =
				cpDefinitionPersistence.findByPrimaryKey(cpDefinitionId);

			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					_classNameLocalService.getClassNameId(CProduct.class),
					cpDefinition.getCProductId());

			return friendlyURLEntry.getUrlTitleMapAsXML();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public boolean hasChildCPDefinitions(long cpDefinitionId) {
		int count =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinitionId);

		if ((count <= 0) ||
			!_cpDefinitionOptionRelLocalService.
				hasLinkedCPInstanceCPDefinitionOptionRels(cpDefinitionId)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isPublishedCPDefinition(CPDefinition cpDefinition) {
		CProduct cProduct = _cProductLocalService.fetchCProduct(
			cpDefinition.getCProductId());

		if ((cProduct != null) &&
			(cProduct.getPublishedCPDefinitionId() ==
				cpDefinition.getCPDefinitionId())) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isPublishedCPDefinition(long cpDefinitionId) {
		CPDefinition cpDefinition = cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition == null) {
			return false;
		}

		return isPublishedCPDefinition(cpDefinition);
	}

	@Override
	public boolean isVersionable(CPDefinition cpDefinition) {
		if ((cpDefinition == null) ||
			!_isVersioningEnabled(cpDefinition.getCompanyId())) {

			return false;
		}

		return isPublishedCPDefinition(cpDefinition);
	}

	@Override
	public boolean isVersionable(long cpDefinitionId) {
		return cpDefinitionLocalService.isVersionable(
			cpDefinitionLocalService.fetchCPDefinition(cpDefinitionId));
	}

	@Override
	public boolean isVersionable(
		long cpDefinitionId, HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return isVersionable(cpDefinitionId);
		}

		boolean versionable = GetterUtil.getBoolean(
			httpServletRequest.getAttribute("versionable#" + cpDefinitionId),
			true);

		if (versionable) {
			return isVersionable(cpDefinitionId);
		}

		return false;
	}

	@Override
	public void maintainVersionThreshold(long companyId, long cProductId)
		throws PortalException {

		int threshold = 0;

		try {
			CProductVersionConfiguration cProductVersionConfiguration =
				_configurationProvider.getConfiguration(
					CProductVersionConfiguration.class,
					new CompanyServiceSettingsLocator(
						companyId,
						CProductVersionConfiguration.class.getName()));

			threshold = cProductVersionConfiguration.versionThreshold();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return;
		}

		OrderByComparator<CPDefinition> orderByComparator =
			OrderByComparatorFactoryUtil.create(
				CPDefinitionModelImpl.TABLE_NAME, Field.VERSION, false);

		List<CPDefinition> deletableCPDefinitions =
			cpDefinitionPersistence.findByC_S(
				cProductId, WorkflowConstants.STATUS_APPROVED, threshold,
				threshold + Short.MAX_VALUE, orderByComparator);

		for (CPDefinition cpDefinition : deletableCPDefinitions) {
			cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
		}
	}

	@Override
	public BaseModelSearchResult<CPDefinition> searchCPDefinitions(
			long companyId, long[] groupIds, String keywords, int status,
			boolean ignoreCommerceAccountGroup, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, keywords, status, start, end, sort);

		searchContext.setAttribute(
			"ignoreCommerceAccountGroup", ignoreCommerceAccountGroup);

		return _searchCPDefinitions(searchContext);
	}

	@Override
	public BaseModelSearchResult<CPDefinition> searchCPDefinitions(
			long companyId, long[] groupIds, String keywords,
			String filterFields, String filterValues, int start, int end,
			Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, keywords, WorkflowConstants.STATUS_ANY, start,
			end, sort);

		List<Facet> facets = getFacets(
			filterFields, filterValues, searchContext);

		searchContext.setFacets(facets);

		return _searchCPDefinitions(searchContext);
	}

	@Override
	public BaseModelSearchResult<CPDefinition>
			searchCPDefinitionsByChannelGroupId(
				long companyId, long[] groupIds, long commerceChannelGroupId,
				String keywords, int status, boolean ignoreCommerceAccountGroup,
				int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, groupIds, keywords, status, start, end, sort);

		searchContext.setAttribute(
			CPField.COMMERCE_CHANNEL_GROUP_ID, commerceChannelGroupId);
		searchContext.setAttribute(
			"ignoreCommerceAccountGroup", ignoreCommerceAccountGroup);
		searchContext.setAttribute("secure", Boolean.TRUE);

		return _searchCPDefinitions(searchContext);
	}

	@Override
	public void updateAsset(
			long userId, CPDefinition cpDefinition, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpDefinition.getCompanyId());

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, companyGroup.getGroupId(), cpDefinition.getCreateDate(),
			cpDefinition.getModifiedDate(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId(), cpDefinition.getUuid(), 0,
			assetCategoryIds, assetTagNames, true, true, null, null,
			cpDefinition.getCreateDate(), null, ContentTypes.TEXT_PLAIN,
			cpDefinition.getNameMapAsXML(),
			cpDefinition.getDescriptionMapAsXML(), null, null, null, 0, 0,
			priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateCPDefinition(
			long cpDefinitionId, Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> urlTitleMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, boolean ignoreSKUCombinations,
			boolean shippable, boolean freeShipping, boolean shipSeparately,
			double shippingExtraPrice, double width, double height,
			double depth, double weight, long cpTaxCategoryId,
			boolean taxExempt, boolean telcoOrElectronics,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Commerce product definition

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		long groupId = cpDefinition.getGroupId();

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPDefinitionDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPDefinitionExpirationDateException.class);
		}

		String productTypeName = cpDefinition.getProductTypeName();

		_validate(
			groupId, ddmStructureKey, metaTitleMap, metaDescriptionMap,
			metaKeywordsMap, displayDate, expirationDate, productTypeName);

		CProduct cProduct = _cProductLocalService.getCProduct(
			cpDefinition.getCProductId());

		if (cpDefinitionLocalService.isVersionable(
				cProduct.getPublishedCPDefinitionId()) &&
			(serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_PUBLISH)) {

			if (!cpDefinition.isDraft()) {
				cpDefinition = cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionId, groupId, WorkflowConstants.STATUS_APPROVED);
			}
			else if (cpDefinition.getCPDefinitionId() !=
						cProduct.getPublishedCPDefinitionId()) {

				CPDefinition publishedCPDefinition =
					cpDefinitionLocalService.getCPDefinition(
						cProduct.getPublishedCPDefinitionId());

				publishedCPDefinition.setPublished(false);

				publishedCPDefinition = cpDefinitionPersistence.update(
					publishedCPDefinition);

				_cProductLocalService.updatePublishedCPDefinitionId(
					publishedCPDefinition.getCProductId(),
					cpDefinition.getCPDefinitionId());
			}
		}

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setIgnoreSKUCombinations(ignoreSKUCombinations);
		cpDefinition.setFreeShipping(freeShipping);
		cpDefinition.setShipSeparately(shipSeparately);

		if (StringUtil.equalsIgnoreCase(
				productTypeName, VirtualCPTypeConstants.NAME)) {

			cpDefinition.setShippable(false);
		}
		else {
			cpDefinition.setShippable(shippable);
		}

		cpDefinition.setShippingExtraPrice(shippingExtraPrice);
		cpDefinition.setWidth(width);
		cpDefinition.setHeight(height);
		cpDefinition.setDepth(depth);
		cpDefinition.setWeight(weight);
		cpDefinition.setTaxExempt(taxExempt);
		cpDefinition.setTelcoOrElectronics(telcoOrElectronics);
		cpDefinition.setDDMStructureKey(ddmStructureKey);
		cpDefinition.setPublished(published);
		cpDefinition.setDisplayDate(displayDate);
		cpDefinition.setExpirationDate(expirationDate);

		if ((expirationDate == null) || expirationDate.after(date)) {
			cpDefinition.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			cpDefinition.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpDefinition.setStatusByUserId(user.getUserId());
		cpDefinition.setStatusDate(serviceContext.getModifiedDate(date));
		cpDefinition.setExpandoBridgeAttributes(serviceContext);

		cpDefinition = cpDefinitionPersistence.update(cpDefinition);

		// Commerce product definition localization

		_updateCPDefinitionLocalizedFields(
			cpDefinition.getCompanyId(), cpDefinition.getCPDefinitionId(),
			nameMap, shortDescriptionMap, descriptionMap, metaTitleMap,
			metaDescriptionMap, metaKeywordsMap);

		// Commerce product friendly URL entries

		_addFriendlyURLEntries(
			cpDefinition, nameMap, urlTitleMap, serviceContext);

		// Asset

		updateAsset(
			user.getUserId(), cpDefinition,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Workflow

		return _startWorkflowInstance(
			user.getUserId(), cpDefinition, serviceContext);
	}

	@Override
	public CPDefinition updateCPDefinition(
			long cpDefinitionId, Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> urlTitleMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, boolean ignoreSKUCombinations,
			String ddmStructureKey, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		return cpDefinitionLocalService.updateCPDefinition(
			cpDefinitionId, nameMap, shortDescriptionMap, descriptionMap,
			urlTitleMap, metaTitleMap, metaDescriptionMap, metaKeywordsMap,
			ignoreSKUCombinations, cpDefinition.isShippable(),
			cpDefinition.isFreeShipping(), cpDefinition.isShipSeparately(),
			cpDefinition.getShippingExtraPrice(), cpDefinition.getWidth(),
			cpDefinition.getHeight(), cpDefinition.getDepth(),
			cpDefinition.getWeight(), cpDefinition.getCPTaxCategoryId(),
			cpDefinition.isTaxExempt(), cpDefinition.isTelcoOrElectronics(),
			ddmStructureKey, published, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateCPDefinitionAccountGroupFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		cpDefinition.setAccountGroupFilterEnabled(enable);

		return cpDefinitionPersistence.update(cpDefinition);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateCPDefinitionCategorization(
			long cpDefinitionId, ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if (cpDefinitionLocalService.isVersionable(cpDefinition)) {
			cpDefinition = cpDefinitionLocalService.copyCPDefinition(
				cpDefinitionId);

			cpDefinitionId = cpDefinition.getCPDefinitionId();
		}

		updateStatus(
			serviceContext.getUserId(), cpDefinitionId,
			cpDefinition.getStatus(), serviceContext,
			new HashMap<String, Serializable>());

		// Asset

		Group companyGroup = _groupLocalService.getCompanyGroup(
			serviceContext.getCompanyId());

		_assetEntryLocalService.updateEntry(
			serviceContext.getUserId(), companyGroup.getGroupId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return cpDefinitionPersistence.update(cpDefinition);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateCPDefinitionChannelFilter(
			long cpDefinitionId, boolean enable)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		cpDefinition.setChannelFilterEnabled(enable);

		return cpDefinitionPersistence.update(cpDefinition);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateCPDefinitionIgnoreSKUCombinations(
			long cpDefinitionId, boolean ignoreSKUCombinations,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCPInstances(
			serviceContext.getUserId(), cpDefinitionId, ignoreSKUCombinations);

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		cpDefinition.setIgnoreSKUCombinations(ignoreSKUCombinations);

		return cpDefinitionPersistence.update(cpDefinition);
	}

	@Override
	public void updateCPDefinitionsByCPTaxCategoryId(long cpTaxCategoryId)
		throws PortalException {

		List<CPDefinition> cpDefinitions =
			cpDefinitionPersistence.findByCPTaxCategoryId(cpTaxCategoryId);

		for (CPDefinition cpDefinition : cpDefinitions) {
			updateTaxCategoryInfo(
				cpDefinition.getCPDefinitionId(), 0, cpDefinition.isTaxExempt(),
				cpDefinition.isTelcoOrElectronics());
		}
	}

	@Override
	public CPDefinition updateExternalReferenceCode(
			String externalReferenceCode, long cpDefinitionId)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		_cProductLocalService.updateCProductExternalReferenceCode(
			externalReferenceCode, cpDefinition.getCProductId());

		return cpDefinitionLocalService.getCPDefinition(cpDefinitionId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateShippingInfo(
			long cpDefinitionId, boolean shippable, boolean freeShipping,
			boolean shipSeparately, double shippingExtraPrice, double width,
			double height, double depth, double weight,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if (cpDefinitionLocalService.isVersionable(cpDefinition)) {
			cpDefinition = cpDefinitionLocalService.copyCPDefinition(
				cpDefinitionId);
		}

		cpDefinition.setShippable(shippable);
		cpDefinition.setFreeShipping(freeShipping);
		cpDefinition.setShipSeparately(shipSeparately);
		cpDefinition.setShippingExtraPrice(shippingExtraPrice);
		cpDefinition.setWidth(width);
		cpDefinition.setHeight(height);
		cpDefinition.setDepth(depth);
		cpDefinition.setWeight(weight);

		return cpDefinitionPersistence.update(cpDefinition);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPDefinition updateStatus(
			long userId, long cpDefinitionId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		// Commerce product definition

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(cpDefinition.getDisplayDate() != null) &&
			date.before(cpDefinition.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		Date expirationDate = cpDefinition.getExpirationDate();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(expirationDate != null) && expirationDate.before(date)) {

			cpDefinition.setStatus(WorkflowConstants.STATUS_EXPIRED);

			status = WorkflowConstants.STATUS_EXPIRED;
		}

		if ((status == WorkflowConstants.STATUS_EXPIRED) &&
			((expirationDate == null) || expirationDate.after(date))) {

			cpDefinition.setExpirationDate(date);
		}

		cpDefinition.setStatus(status);
		cpDefinition.setStatusByUserId(user.getUserId());
		cpDefinition.setStatusByUserName(user.getFullName());
		cpDefinition.setStatusDate(serviceContext.getModifiedDate(date));

		cpDefinition = cpDefinitionPersistence.update(cpDefinition);

		if (status == WorkflowConstants.STATUS_APPROVED) {

			// Asset

			_assetEntryLocalService.updateEntry(
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				cpDefinition.getDisplayDate(), cpDefinition.getExpirationDate(),
				true, true);

			// CProduct

			_cProductLocalService.updatePublishedCPDefinitionId(
				cpDefinition.getCProductId(), cpDefinition.getCPDefinitionId());
		}
		else {

			// Asset

			_assetEntryLocalService.updateVisible(
				CPDefinition.class.getName(), cpDefinitionId, false);
		}

		// Commerce product instances

		_reindexCPInstances(cpDefinition);

		return cpDefinition;
	}

	@Indexable(type = IndexableType.REINDEX)
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

		if (!subscriptionEnabled) {
			subscriptionLength = 1;
			subscriptionType = null;
			subscriptionTypeSettingsUnicodeProperties = null;
			maxSubscriptionCycles = 0L;
		}
		else {
			_validateSubscriptionLength(subscriptionLength, "length");
			_validateSubscriptionCycles(
				maxSubscriptionCycles, "subscriptionCycles");
			_validateSubscriptionTypeSettingsUnicodeProperties(
				subscriptionType, subscriptionTypeSettingsUnicodeProperties);
		}

		if (!deliverySubscriptionEnabled) {
			deliverySubscriptionLength = 1;
			deliverySubscriptionType = null;
			deliverySubscriptionTypeSettingsUnicodeProperties = null;
			deliveryMaxSubscriptionCycles = 0L;
		}
		else {
			_validateSubscriptionLength(
				deliverySubscriptionLength, "deliverySubscriptionLength");
			_validateSubscriptionCycles(
				deliveryMaxSubscriptionCycles, "deliverySubscriptionCycles");
			_validateDeliverySubscriptionTypeSettingsUnicodeProperties(
				deliverySubscriptionType,
				deliverySubscriptionTypeSettingsUnicodeProperties);
		}

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if (cpDefinitionLocalService.isVersionable(cpDefinition)) {
			cpDefinition = cpDefinitionLocalService.copyCPDefinition(
				cpDefinitionId);
		}

		cpDefinition.setSubscriptionEnabled(subscriptionEnabled);
		cpDefinition.setSubscriptionLength(subscriptionLength);
		cpDefinition.setSubscriptionType(subscriptionType);
		cpDefinition.setSubscriptionTypeSettingsUnicodeProperties(
			subscriptionTypeSettingsUnicodeProperties);
		cpDefinition.setMaxSubscriptionCycles(maxSubscriptionCycles);
		cpDefinition.setDeliverySubscriptionEnabled(
			deliverySubscriptionEnabled);
		cpDefinition.setDeliverySubscriptionLength(deliverySubscriptionLength);
		cpDefinition.setDeliverySubscriptionType(deliverySubscriptionType);
		cpDefinition.setDeliverySubscriptionTypeSettingsUnicodeProperties(
			deliverySubscriptionTypeSettingsUnicodeProperties);
		cpDefinition.setDeliveryMaxSubscriptionCycles(
			deliveryMaxSubscriptionCycles);

		return cpDefinitionPersistence.update(cpDefinition);
	}

	@Override
	public CPDefinition updateTaxCategoryInfo(
			long cpDefinitionId, long cpTaxCategoryId, boolean taxExempt,
			boolean telcoOrElectronics)
		throws PortalException {

		CPDefinition cpDefinition = cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if (cpDefinitionLocalService.isVersionable(cpDefinition)) {
			cpDefinition = cpDefinitionLocalService.copyCPDefinition(
				cpDefinitionId);
		}

		cpDefinition.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinition.setTaxExempt(taxExempt);
		cpDefinition.setTelcoOrElectronics(telcoOrElectronics);

		return cpDefinitionPersistence.update(cpDefinition);
	}

	private void _addCommercePriceEntry(
			CPInstance cpInstance, String cpInstanceUuid, String type,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceEntryLocalService commercePriceEntryLocalService =
			_commercePriceEntryLocalServiceSnapshot.get();

		CommercePriceListLocalService commercePriceListLocalService =
			_commercePriceListLocalServiceSnapshot.get();

		CommercePriceList commercePriceList =
			commercePriceListLocalService.getCatalogBaseCommercePriceListByType(
				cpInstance.getGroupId(), type);

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(), cpInstanceUuid,
				StringPool.BLANK);

		if (commercePriceEntry == null) {
			return;
		}

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		commercePriceEntryLocalService.addCommercePriceEntry(
			null, cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(),
			commercePriceEntry.getPrice(),
			commercePriceEntry.isPriceOnApplication(), null, StringPool.BLANK,
			serviceContext);
	}

	private List<CPDefinitionLocalization> _addCPDefinitionLocalizedFields(
			long companyId, long cpDefinitionId, Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap)
		throws PortalException {

		Set<Locale> locales = new HashSet<>();

		locales.addAll(nameMap.keySet());

		if (shortDescriptionMap != null) {
			locales.addAll(shortDescriptionMap.keySet());
		}

		if (descriptionMap != null) {
			locales.addAll(descriptionMap.keySet());
		}

		if (metaTitleMap != null) {
			locales.addAll(metaTitleMap.keySet());
		}

		if (metaDescriptionMap != null) {
			locales.addAll(metaDescriptionMap.keySet());
		}

		if (metaKeywordsMap != null) {
			locales.addAll(metaKeywordsMap.keySet());
		}

		return TransformUtil.transform(
			locales,
			locale -> {
				String name = nameMap.get(locale);
				String shortDescription = null;
				String description = null;
				String metaTitle = null;
				String metaDescription = null;
				String metaKeywords = null;

				if (shortDescriptionMap != null) {
					shortDescription = shortDescriptionMap.get(locale);
				}

				if (descriptionMap != null) {
					description = descriptionMap.get(locale);
				}

				if (metaTitleMap != null) {
					metaTitle = metaTitleMap.get(locale);
				}

				if (metaDescriptionMap != null) {
					metaDescription = metaDescriptionMap.get(locale);
				}

				if (metaKeywordsMap != null) {
					metaKeywords = metaKeywordsMap.get(locale);
				}

				if (Validator.isNull(name) &&
					Validator.isNull(shortDescription) &&
					Validator.isNull(description) &&
					Validator.isNull(metaTitle) &&
					Validator.isNull(metaDescription) &&
					Validator.isNull(metaKeywords)) {

					return null;
				}

				return _addCPDefinitionLocalizedFields(
					companyId, cpDefinitionId, name, shortDescription,
					description, metaTitle, metaDescription, metaKeywords,
					LocaleUtil.toLanguageId(locale));
			});
	}

	private CPDefinitionLocalization _addCPDefinitionLocalizedFields(
			long companyId, long cpDefinitionId, String name,
			String shortDescription, String description, String metaTitle,
			String metaDescription, String metaKeywords, String languageId)
		throws PortalException {

		CPDefinitionLocalization cpDefinitionLocalization =
			cpDefinitionLocalizationPersistence.
				fetchByCPDefinitionId_LanguageId(cpDefinitionId, languageId);

		if (cpDefinitionLocalization == null) {
			long cpDefinitionLocalizationId = counterLocalService.increment();

			cpDefinitionLocalization =
				cpDefinitionLocalizationPersistence.create(
					cpDefinitionLocalizationId);

			cpDefinitionLocalization.setCompanyId(companyId);
			cpDefinitionLocalization.setCPDefinitionId(cpDefinitionId);
			cpDefinitionLocalization.setLanguageId(languageId);
			cpDefinitionLocalization.setName(name);
			cpDefinitionLocalization.setShortDescription(shortDescription);
			cpDefinitionLocalization.setDescription(description);
			cpDefinitionLocalization.setMetaTitle(metaTitle);
			cpDefinitionLocalization.setMetaDescription(metaDescription);
			cpDefinitionLocalization.setMetaKeywords(metaKeywords);
		}
		else {
			cpDefinitionLocalization.setName(name);
			cpDefinitionLocalization.setShortDescription(shortDescription);
			cpDefinitionLocalization.setDescription(description);
			cpDefinitionLocalization.setMetaTitle(metaTitle);
			cpDefinitionLocalization.setMetaDescription(metaDescription);
			cpDefinitionLocalization.setMetaKeywords(metaKeywords);
		}

		return cpDefinitionLocalizationPersistence.update(
			cpDefinitionLocalization);
	}

	private void _addFriendlyURLEntries(
			CPDefinition cpDefinition, Map<Locale, String> nameMap,
			Map<Locale, String> urlTitleMap, ServiceContext serviceContext)
		throws PortalException {

		if ((cpDefinition != null) &&
			Objects.equals(urlTitleMap, cpDefinition.getUrlTitleMap())) {

			return;
		}

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpDefinition.getCompanyId());

		Map<Locale, String> newUrlTitleMap = new HashMap<>();

		if (MapUtil.isEmpty(urlTitleMap)) {
			newUrlTitleMap = _getUniqueUrlTitles(cpDefinition, nameMap);
		}
		else {
			newUrlTitleMap = _getUniqueUrlTitles(cpDefinition, urlTitleMap);
		}

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			companyGroup.getGroupId(),
			_classNameLocalService.getClassNameId(CProduct.class),
			cpDefinition.getCProductId(), _toLanguageIdMap(newUrlTitleMap),
			serviceContext);
	}

	private SearchContext _buildSearchContext(
		long companyId, long[] groupIds, String keywords, int status, int start,
		int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.CONTENT, keywords
			).put(
				Field.DESCRIPTION, keywords
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.NAME, keywords
			).put(
				Field.STATUS, status
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (groupIds.length > 0) {
			searchContext.setGroupIds(groupIds);
		}

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private void _checkCPDefinitionsByDisplayDate() throws PortalException {
		List<CPDefinition> cpDefinitions = cpDefinitionPersistence.findByLtD_S(
			new Date(), WorkflowConstants.STATUS_SCHEDULED);

		for (CPDefinition cpDefinition : cpDefinitions) {
			long userId = _portal.getValidUserId(
				cpDefinition.getCompanyId(), cpDefinition.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(cpDefinition.getGroupId());

			cpDefinitionLocalService.updateStatus(
				userId, cpDefinition.getCPDefinitionId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<String, Serializable>());

			if (cpDefinition.isApproved()) {
				_cpAttachmentFileEntryLocalService.
					checkCPAttachmentFileEntriesByDisplayDate(
						_classNameLocalService.getClassNameId(
							cpDefinition.getModelClassName()),
						cpDefinition.getCPDefinitionId());

				_cpInstanceLocalService.checkCPInstancesByDisplayDate(
					cpDefinition.getCPDefinitionId());
			}
		}
	}

	private void _checkCPDefinitionsByExpirationDate() throws PortalException {
		List<CPDefinition> cpDefinitions =
			cpDefinitionFinder.findByExpirationDate(
				new Date(),
				new QueryDefinition<>(WorkflowConstants.STATUS_APPROVED));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring " + cpDefinitions.size() +
					" commerce product definitions");
		}

		if ((cpDefinitions != null) && !cpDefinitions.isEmpty()) {
			for (CPDefinition cpDefinition : cpDefinitions) {
				long userId = _portal.getValidUserId(
					cpDefinition.getCompanyId(), cpDefinition.getUserId());

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCommand(Constants.UPDATE);
				serviceContext.setScopeGroupId(cpDefinition.getGroupId());

				cpDefinitionLocalService.updateStatus(
					userId, cpDefinition.getCPDefinitionId(),
					WorkflowConstants.STATUS_EXPIRED, serviceContext,
					new HashMap<String, Serializable>());
			}
		}
	}

	private void _checkCPInstances(
			long userId, long cpDefinitionId, boolean ignoreSKUCombinations)
		throws PortalException {

		if (ignoreSKUCombinations) {
			int cpInstancesCount =
				_cpInstanceLocalService.getCPDefinitionInstancesCount(
					cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

			if (cpInstancesCount <= 1) {
				return;
			}

			throw new CPDefinitionIgnoreSKUCombinationsException();
		}

		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinitionId, true);

		if (cpDefinitionOptionRelsCount == 0) {
			return;
		}

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionId, WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			if (!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceOptionValueRel(cpInstance.getCPInstanceId())) {

				_cpInstanceLocalService.updateStatus(
					userId, cpInstance.getCPInstanceId(),
					WorkflowConstants.STATUS_INACTIVE);
			}
		}
	}

	private List<CPDefinition> _getCPDefinitions(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CPDefinition> cpDefinitions = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long cpDefinitionId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CPDefinition cpDefinition = fetchCPDefinition(cpDefinitionId);

			if (cpDefinition == null) {
				cpDefinitions = null;

				Indexer<CPDefinition> indexer = IndexerRegistryUtil.getIndexer(
					CPDefinition.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (cpDefinitions != null) {
				cpDefinitions.add(cpDefinition);
			}
		}

		return cpDefinitions;
	}

	private String _getIndexFieldName(String optionKey, String languageId) {
		return StringBundler.concat(
			languageId, "_ATTRIBUTE_", optionKey, "_VALUES_NAMES");
	}

	private Map<Locale, String> _getUniqueUrlTitles(
			CPDefinition cpDefinition, Map<Locale, String> urlTitleMap)
		throws PortalException {

		Map<Locale, String> newURLTitleMap = new HashMap<>();

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpDefinition.getCompanyId());

		for (Map.Entry<Locale, String> titleEntry : urlTitleMap.entrySet()) {
			String urlTitle = urlTitleMap.get(titleEntry.getKey());

			if (Validator.isNotNull(urlTitle) ||
				((urlTitle != null) && urlTitle.equals(StringPool.BLANK))) {

				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					companyGroup.getGroupId(),
					_classNameLocalService.getClassNameId(CProduct.class),
					cpDefinition.getCProductId(), titleEntry.getValue(), null);

				newURLTitleMap.put(titleEntry.getKey(), urlTitle);
			}
		}

		return newURLTitleMap;
	}

	private boolean _isVersioningEnabled(long companyId) {
		try {
			CProductVersionConfiguration cProductVersionConfiguration =
				_configurationProvider.getConfiguration(
					CProductVersionConfiguration.class,
					new CompanyServiceSettingsLocator(
						companyId,
						CProductVersionConfiguration.class.getName()));

			if (cProductVersionConfiguration.enabled()) {
				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private void _reindexCPDefinitionOptionRels(CPDefinition cpDefinition)
		throws PortalException {

		Indexer<CPDefinitionOptionRel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPDefinitionOptionRel.class);

		indexer.reindex(cpDefinition.getCPDefinitionOptionRels());
	}

	private void _reindexCPDefinitionOptionValueRels(
			CPDefinitionOptionRel cpDefinitionOptionRel)
		throws PortalException {

		Indexer<CPDefinitionOptionValueRel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CPDefinitionOptionValueRel.class);

		indexer.reindex(cpDefinitionOptionRel.getCPDefinitionOptionValueRels());
	}

	private void _reindexCPInstances(CPDefinition cpDefinition)
		throws PortalException {

		Indexer<CPInstance> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPInstance.class);

		indexer.reindex(cpDefinition.getCPInstances());
	}

	private BaseModelSearchResult<CPDefinition> _searchCPDefinitions(
			SearchContext searchContext)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CPDefinition> cpDefinitions = _getCPDefinitions(hits);

			if (cpDefinitions != null) {
				return new BaseModelSearchResult<>(
					cpDefinitions, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private CPDefinition _startWorkflowInstance(
			long userId, CPDefinition cpDefinition,
			ServiceContext serviceContext)
		throws PortalException {

		boolean skipObjectActionExecution =
			ObjectActionThreadLocal.isSkipObjectActionExecution();

		try {
			ObjectActionThreadLocal.setSkipObjectActionExecution(true);

			Map<String, Serializable> workflowContext = new HashMap<>();

			cpDefinition = WorkflowHandlerRegistryUtil.startWorkflowInstance(
				cpDefinition.getCompanyId(), cpDefinition.getGroupId(), userId,
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				cpDefinition, serviceContext, workflowContext);
		}
		finally {
			ObjectActionThreadLocal.setSkipObjectActionExecution(
				skipObjectActionExecution);
		}

		return cpDefinition;
	}

	private Map<String, String> _toLanguageIdMap(Map<Locale, String> map) {
		Map<String, String> languageIdMap = new HashMap<>();

		map.forEach(
			(locale, value) -> languageIdMap.put(
				LocaleUtil.toLanguageId(locale), value));

		return Collections.unmodifiableMap(languageIdMap);
	}

	private List<CPDefinitionLocalization> _updateCPDefinitionLocalizedFields(
			long companyId, long cpDefinitionId, Map<Locale, String> nameMap,
			Map<Locale, String> shortDescriptionMap,
			Map<Locale, String> descriptionMap,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap)
		throws PortalException {

		List<CPDefinitionLocalization> oldCPDefinitionLocalizations =
			new ArrayList<>(
				cpDefinitionLocalizationPersistence.findByCPDefinitionId(
					cpDefinitionId));

		List<CPDefinitionLocalization> newCPDefinitionLocalizations =
			_addCPDefinitionLocalizedFields(
				companyId, cpDefinitionId, nameMap, shortDescriptionMap,
				descriptionMap, metaTitleMap, metaDescriptionMap,
				metaKeywordsMap);

		oldCPDefinitionLocalizations.removeAll(newCPDefinitionLocalizations);

		for (CPDefinitionLocalization oldCPDefinitionLocalization :
				oldCPDefinitionLocalizations) {

			cpDefinitionLocalizationPersistence.remove(
				oldCPDefinitionLocalization);
		}

		return newCPDefinitionLocalizations;
	}

	private void _validate(
			long groupId, String ddmStructureKey,
			Map<Locale, String> metaTitleMap,
			Map<Locale, String> metaDescriptionMap,
			Map<Locale, String> metaKeywordsMap, Date displayDate,
			Date expirationDate, String productTypeName)
		throws PortalException {

		if (Validator.isNotNull(ddmStructureKey)) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					groupId,
					_classNameLocalService.getClassNameId(
						CPDefinition.class.getName()),
					ddmStructureKey, true);

			if (ddmStructure == null) {
				throw new NoSuchStructureException();
			}
		}

		if (metaTitleMap != null) {
			for (Map.Entry<Locale, String> entry : metaTitleMap.entrySet()) {
				CPDefinitionMetaTitleException cpDefinitionMetaTitleException =
					CPDefinitionImpl.validateMetaTitle(entry.getValue());

				if (cpDefinitionMetaTitleException != null) {
					throw cpDefinitionMetaTitleException;
				}
			}
		}

		if (metaDescriptionMap != null) {
			for (Map.Entry<Locale, String> entry :
					metaDescriptionMap.entrySet()) {

				CPDefinitionMetaDescriptionException
					cpDefinitionMetaDescriptionException =
						CPDefinitionImpl.validateMetaDescription(
							entry.getValue());

				if (cpDefinitionMetaDescriptionException != null) {
					throw cpDefinitionMetaDescriptionException;
				}
			}
		}

		if (metaKeywordsMap != null) {
			for (Map.Entry<Locale, String> entry : metaKeywordsMap.entrySet()) {
				CPDefinitionMetaKeywordsException
					cpDefinitionMetaKeywordsException =
						CPDefinitionImpl.validateMetaKeyword(entry.getValue());

				if (cpDefinitionMetaKeywordsException != null) {
					throw cpDefinitionMetaKeywordsException;
				}
			}
		}

		if ((expirationDate != null) && (displayDate != null) &&
			expirationDate.before(displayDate)) {

			throw new CPDefinitionExpirationDateException(
				"Expiration date " + expirationDate +
					" is before display date");
		}

		CPType cpType = _cpTypeRegistry.getCPType(productTypeName);

		if (cpType == null) {
			throw new CPDefinitionProductTypeNameException();
		}
	}

	private UnicodeProperties
			_validateDeliverySubscriptionTypeSettingsUnicodeProperties(
				String deliverySubscriptionType,
				UnicodeProperties
					deliverySubscriptionTypeSettingsUnicodeProperties)
		throws PortalException {

		CPSubscriptionType deliveryCPSubscriptionType =
			_cpSubscriptionTypeRegistry.getCPSubscriptionType(
				deliverySubscriptionType);

		if (deliveryCPSubscriptionType == null) {
			return null;
		}

		return deliveryCPSubscriptionType.
			getDeliverySubscriptionTypeSettingsUnicodeProperties(
				deliverySubscriptionTypeSettingsUnicodeProperties);
	}

	private void _validateSubscriptionCycles(
			long subscriptionCycles, String subscriptionCyclesKey)
		throws PortalException {

		if ((subscriptionCycles < 0) &&
			subscriptionCyclesKey.equals("subscriptionCycles")) {

			throw new CPDefinitionMaxSubscriptionCyclesException(
				StringBundler.concat(
					"Invalid ", subscriptionCyclesKey, " ",
					subscriptionCycles));
		}
		else if ((subscriptionCycles < 0) &&
				 subscriptionCyclesKey.equals("deliverySubscriptionCycles")) {

			throw new CPDefinitionDeliveryMaxSubscriptionCyclesException(
				StringBundler.concat(
					"Invalid ", subscriptionCyclesKey, " ",
					subscriptionCycles));
		}
	}

	private void _validateSubscriptionLength(
			int subscriptionLength, String subscriptionLengthKey)
		throws PortalException {

		if (subscriptionLength < 1) {
			throw new CPDefinitionSubscriptionLengthException(
				StringBundler.concat(
					"Invalid ", subscriptionLengthKey, " ",
					subscriptionLength));
		}
	}

	private UnicodeProperties
			_validateSubscriptionTypeSettingsUnicodeProperties(
				String subscriptionType,
				UnicodeProperties subscriptionTypeSettingsUnicodeProperties)
		throws PortalException {

		CPSubscriptionType cpSubscriptionType =
			_cpSubscriptionTypeRegistry.getCPSubscriptionType(subscriptionType);

		if (cpSubscriptionType == null) {
			return null;
		}

		return cpSubscriptionType.getSubscriptionTypeSettingsUnicodeProperties(
			subscriptionTypeSettingsUnicodeProperties);
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.GROUP_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionLocalServiceImpl.class);

	private static final Snapshot<CommercePriceEntryLocalService>
		_commercePriceEntryLocalServiceSnapshot = new Snapshot<>(
			CPDefinitionLocalServiceImpl.class,
			CommercePriceEntryLocalService.class);
	private static final Snapshot<CommercePriceListLocalService>
		_commercePriceListLocalServiceSnapshot = new Snapshot<>(
			CPDefinitionLocalServiceImpl.class,
			CommercePriceListLocalService.class);

	@Reference
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPAttachmentFileEntryPersistence _cpAttachmentFileEntryPersistence;

	@Reference
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Reference
	private CPDefinitionLinkPersistence _cpDefinitionLinkPersistence;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private CPDefinitionOptionValueRelPersistence
		_cpDefinitionOptionValueRelPersistence;

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference
	private CPDefinitionSpecificationOptionValuePersistence
		_cpDefinitionSpecificationOptionValuePersistence;

	@Reference
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@Reference
	private CPDisplayLayoutPersistence _cpDisplayLayoutPersistence;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstanceOptionValueRelPersistence
		_cpInstanceOptionValueRelPersistence;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CProductPersistence _cProductPersistence;

	@Reference
	private CPSubscriptionTypeRegistry _cpSubscriptionTypeRegistry;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}