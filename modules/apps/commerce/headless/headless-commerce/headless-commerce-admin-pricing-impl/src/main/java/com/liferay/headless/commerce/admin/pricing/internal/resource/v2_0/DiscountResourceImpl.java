/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0;

import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountAccountRelService;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelService;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelService;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccountGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountCategory;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountChannel;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProductGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.internal.odata.entity.v2_0.DiscountEntityModel;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountAccountGroupUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountAccountUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountCategoryUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountChannelUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountOrderTypeUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountProductGroupUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountProductUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountRuleUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.math.BigDecimal;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/discount.properties",
	scope = ServiceScope.PROTOTYPE, service = DiscountResource.class
)
public class DiscountResourceImpl extends BaseDiscountResourceImpl {

	@Override
	public void deleteDiscount(Long id) throws Exception {
		_commerceDiscountService.deleteCommerceDiscount(id);
	}

	@Override
	public void deleteDiscountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		_commerceDiscountService.deleteCommerceDiscount(
			commerceDiscount.getCommerceDiscountId());
	}

	@Override
	public Discount getDiscount(Long id) throws Exception {
		return _toDiscount(GetterUtil.getLong(id));
	}

	@Override
	public Discount getDiscountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		return _toDiscount(commerceDiscount.getCommerceDiscountId());
	}

	@Override
	public Page<Discount> getDiscountsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceDiscount.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"skipCommerceAccountGroupValidation", Boolean.TRUE);
				searchContext.setAttribute(
					"status", WorkflowConstants.STATUS_ANY);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toDiscount(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Discount patchDiscount(Long id, Discount discount) throws Exception {
		return _toDiscount(
			_updateDiscount(
				_commerceDiscountService.getCommerceDiscount(id), discount));
	}

	@Override
	public Discount patchDiscountByExternalReferenceCode(
			String externalReferenceCode, Discount discount)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		return _toDiscount(_updateDiscount(commerceDiscount, discount));
	}

	@Override
	public Discount postDiscount(Discount discount) throws Exception {
		CommerceDiscount commerceDiscount = _addOrUpdateCommerceDiscount(
			discount.getExternalReferenceCode(), discount);

		return _toDiscount(commerceDiscount.getCommerceDiscountId());
	}

	@Override
	public Discount putDiscountByExternalReferenceCode(
			String externalReferenceCode, Discount discount)
		throws Exception {

		CommerceDiscount commerceDiscount = _addOrUpdateCommerceDiscount(
			externalReferenceCode, discount);

		return _toDiscount(commerceDiscount.getCommerceDiscountId());
	}

	private CommerceDiscount _addOrUpdateCommerceDiscount(
			String externalReferenceCode, Discount discount)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			discount.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			discount.getExpirationDate(), serviceContext.getTimeZone());

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.addOrUpdateCommerceDiscount(
				externalReferenceCode, GetterUtil.getLong(discount.getId()),
				discount.getTitle(), discount.getTarget(),
				GetterUtil.getBoolean(discount.getUseCouponCode()),
				discount.getCouponCode(),
				GetterUtil.getBoolean(discount.getUsePercentage()),
				discount.getMaximumDiscountAmount(), discount.getLevel(),
				discount.getPercentageLevel1(), discount.getPercentageLevel2(),
				discount.getPercentageLevel3(), discount.getPercentageLevel4(),
				discount.getLimitationType(),
				GetterUtil.getInteger(discount.getLimitationTimes()),
				GetterUtil.getInteger(discount.getLimitationTimesPerAccount()),
				GetterUtil.getBoolean(discount.getRulesConjunction()),
				GetterUtil.getBoolean(discount.getActive()),
				displayDateConfig.getMonth(), displayDateConfig.getDay(),
				displayDateConfig.getYear(), displayDateConfig.getHour(),
				displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
				expirationDateConfig.getDay(), expirationDateConfig.getYear(),
				expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(),
				GetterUtil.getBoolean(discount.getNeverExpire(), true),
				serviceContext);

		// Expando

		Map<String, ?> customFields = discount.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				serviceContext.getCompanyId(), CommerceDiscount.class,
				commerceDiscount.getPrimaryKey(), customFields);
		}

		// Update nested resources

		return _updateNestedResources(discount, commerceDiscount);
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceDiscount commerceDiscount)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"DELETE", commerceDiscount.getCommerceDiscountId(),
				"deleteDiscount", commerceDiscount.getUserId(),
				"com.liferay.commerce.discount.model.CommerceDiscount",
				serviceContext.getScopeGroupId())
		).put(
			"get",
			addAction(
				"VIEW", commerceDiscount.getCommerceDiscountId(), "getDiscount",
				commerceDiscount.getUserId(),
				"com.liferay.commerce.discount.model.CommerceDiscount",
				serviceContext.getScopeGroupId())
		).put(
			"permissions",
			addAction(
				"PERMISSIONS", commerceDiscount.getCommerceDiscountId(),
				"patchDiscount", commerceDiscount.getUserId(),
				"com.liferay.commerce.discount.model.CommerceDiscount",
				serviceContext.getScopeGroupId())
		).put(
			"update",
			addAction(
				"UPDATE", commerceDiscount.getCommerceDiscountId(),
				"patchDiscount", commerceDiscount.getUserId(),
				"com.liferay.commerce.discount.model.CommerceDiscount",
				serviceContext.getScopeGroupId())
		).build();
	}

	private Discount _toDiscount(CommerceDiscount commerceDiscount)
		throws Exception {

		return _toDiscount(commerceDiscount.getCommerceDiscountId());
	}

	private Discount _toDiscount(Long commerceDiscountId) throws Exception {
		CommerceDiscount commerceDiscount =
			_commerceDiscountService.getCommerceDiscount(commerceDiscountId);

		return _discountDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceDiscount), _dtoConverterRegistry,
				commerceDiscountId, contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser));
	}

	private CommerceDiscount _updateDiscount(
			CommerceDiscount commerceDiscount, Discount discount)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			discount.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			discount.getExpirationDate(), serviceContext.getTimeZone());

		commerceDiscount = _commerceDiscountService.updateCommerceDiscount(
			commerceDiscount.getCommerceDiscountId(), discount.getTitle(),
			discount.getTarget(),
			GetterUtil.get(
				discount.getUseCouponCode(),
				commerceDiscount.isUseCouponCode()),
			GetterUtil.get(
				discount.getCouponCode(), commerceDiscount.getCouponCode()),
			GetterUtil.get(
				discount.getUsePercentage(),
				commerceDiscount.isUsePercentage()),
			(BigDecimal)GetterUtil.get(
				discount.getMaximumDiscountAmount(),
				commerceDiscount.getMaximumDiscountAmount()),
			GetterUtil.get(discount.getLevel(), commerceDiscount.getLevel()),
			(BigDecimal)GetterUtil.get(
				discount.getPercentageLevel1(), commerceDiscount.getLevel1()),
			(BigDecimal)GetterUtil.get(
				discount.getPercentageLevel2(), commerceDiscount.getLevel2()),
			(BigDecimal)GetterUtil.get(
				discount.getPercentageLevel3(), commerceDiscount.getLevel3()),
			(BigDecimal)GetterUtil.get(
				discount.getPercentageLevel4(), commerceDiscount.getLevel4()),
			discount.getLimitationType(),
			GetterUtil.get(
				discount.getLimitationTimes(),
				commerceDiscount.getLimitationTimes()),
			GetterUtil.get(
				discount.getLimitationTimesPerAccount(),
				commerceDiscount.getLimitationTimesPerAccount()),
			GetterUtil.get(
				discount.getRulesConjunction(),
				commerceDiscount.isRulesConjunction()),
			GetterUtil.get(discount.getActive(), commerceDiscount.isActive()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(discount.getNeverExpire(), true),
			serviceContext);

		// Expando

		Map<String, ?> customFields = discount.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				serviceContext.getCompanyId(), CommerceDiscount.class,
				commerceDiscount.getPrimaryKey(), customFields);
		}

		// Update nested resources

		return _updateNestedResources(discount, commerceDiscount);
	}

	private CommerceDiscount _updateNestedResources(
			Discount discount, CommerceDiscount commerceDiscount)
		throws Exception {

		// Discount account groups

		DiscountAccountGroup[] discountAccountGroups =
			discount.getDiscountAccountGroups();

		if (discountAccountGroups != null) {
			for (DiscountAccountGroup discountAccountGroup :
					discountAccountGroups) {

				CommerceDiscountCommerceAccountGroupRel
					commerceDiscountCommerceAccountGroupRel =
						_commerceDiscountCommerceAccountGroupRelService.
							fetchCommerceDiscountCommerceAccountGroupRel(
								commerceDiscount.getCommerceDiscountId(),
								discountAccountGroup.getAccountGroupId());

				if (commerceDiscountCommerceAccountGroupRel != null) {
					continue;
				}

				DiscountAccountGroupUtil.addCommerceDiscountAccountGroupRel(
					_accountGroupService,
					_commerceDiscountCommerceAccountGroupRelService,
					discountAccountGroup, commerceDiscount,
					_serviceContextHelper);
			}
		}

		// Discount accounts

		DiscountAccount[] discountAccounts = discount.getDiscountAccounts();

		if (discountAccounts != null) {
			for (DiscountAccount discountAccount : discountAccounts) {
				CommerceDiscountAccountRel commerceDiscountAccountRel =
					_commerceDiscountAccountRelService.
						fetchCommerceDiscountAccountRel(
							discountAccount.getAccountId(),
							commerceDiscount.getCommerceDiscountId());

				if (commerceDiscountAccountRel != null) {
					continue;
				}

				DiscountAccountUtil.addCommerceDiscountAccountRel(
					_accountEntryService, _commerceDiscountAccountRelService,
					discountAccount, commerceDiscount, _serviceContextHelper);
			}
		}

		// Discount categories

		DiscountCategory[] discountCategories =
			discount.getDiscountCategories();

		if (discountCategories != null) {
			for (DiscountCategory discountCategory : discountCategories) {
				CommerceDiscountRel commerceDiscountRel =
					_commerceDiscountRelService.fetchCommerceDiscountRel(
						AssetCategory.class.getName(),
						discountCategory.getCategoryId());

				if (commerceDiscountRel != null) {
					continue;
				}

				DiscountCategoryUtil.addCommerceDiscountRel(
					contextCompany.getGroupId(), _assetCategoryLocalService,
					_commerceDiscountRelService, discountCategory,
					commerceDiscount, _serviceContextHelper);
			}
		}

		// Discount channels

		DiscountChannel[] discountChannels = discount.getDiscountChannels();

		if (discountChannels != null) {
			for (DiscountChannel discountChannel : discountChannels) {
				CommerceChannelRel commerceChannelRel =
					_commerceChannelRelService.fetchCommerceChannelRel(
						CommerceDiscount.class.getName(),
						commerceDiscount.getCommerceDiscountId(),
						discountChannel.getChannelId());

				if (commerceChannelRel != null) {
					continue;
				}

				DiscountChannelUtil.addCommerceDiscountChannelRel(
					_commerceChannelService, _commerceChannelRelService,
					discountChannel, commerceDiscount, _serviceContextHelper);
			}
		}

		// Discount order types

		DiscountOrderType[] discountOrderTypes =
			discount.getDiscountOrderTypes();

		if (discountOrderTypes != null) {
			for (DiscountOrderType discountOrderType : discountOrderTypes) {
				CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
					_commerceDiscountOrderTypeRelService.
						fetchCommerceDiscountOrderTypeRel(
							commerceDiscount.getCommerceDiscountId(),
							discountOrderType.getOrderTypeId());

				if (commerceDiscountOrderTypeRel != null) {
					continue;
				}

				DiscountOrderTypeUtil.addCommerceDiscountOrderTypeRel(
					commerceDiscount, _commerceDiscountOrderTypeRelService,
					_commerceOrderTypeService, discountOrderType,
					_serviceContextHelper);
			}
		}

		// Discount product groups

		DiscountProductGroup[] discountProductGroups =
			discount.getDiscountProductGroups();

		if (discountProductGroups != null) {
			for (DiscountProductGroup discountProductGroup :
					discountProductGroups) {

				CommerceDiscountRel commerceDiscountRel =
					_commerceDiscountRelService.fetchCommerceDiscountRel(
						DiscountProductGroup.class.getName(),
						discountProductGroup.getProductGroupId());

				if (commerceDiscountRel != null) {
					continue;
				}

				DiscountProductGroupUtil.addCommerceDiscountRel(
					_commercePricingClassService, _commerceDiscountRelService,
					discountProductGroup, commerceDiscount,
					_serviceContextHelper);
			}
		}

		// Discount products

		DiscountProduct[] discountProducts = discount.getDiscountProducts();

		if (discountProducts != null) {
			for (DiscountProduct discountProduct : discountProducts) {
				CProduct cProduct = _cProductLocalService.fetchCProduct(
					discountProduct.getProductId());

				if (cProduct == null) {
					cProduct =
						_cProductLocalService.
							fetchCProductByExternalReferenceCode(
								discountProduct.
									getProductExternalReferenceCode(),
								contextCompany.getCompanyId());
				}

				if (cProduct == null) {
					throw new NoSuchCProductException(
						"Unable to find product with external reference code " +
							discountProduct.getProductExternalReferenceCode());
				}

				CommerceDiscountRel commerceDiscountRel =
					_commerceDiscountRelService.fetchCommerceDiscountRel(
						CPDefinition.class.getName(),
						cProduct.getPublishedCPDefinitionId());

				if (commerceDiscountRel != null) {
					continue;
				}

				DiscountProductUtil.addCommerceDiscountRel(
					_cProductLocalService, _commerceDiscountRelService,
					discountProduct, commerceDiscount, _serviceContextHelper);
			}
		}

		// Discount rules

		DiscountRule[] discountRules = discount.getDiscountRules();

		if (discountRules != null) {
			for (DiscountRule discountRule : discountRules) {
				CommerceDiscountRule commerceDiscountRule =
					_commerceDiscountRuleService.fetchCommerceDiscountRule(
						discountRule.getId());

				if (commerceDiscountRule != null) {
					continue;
				}

				DiscountRuleUtil.addCommerceDiscountRule(
					_commerceDiscountRuleService, discountRule,
					commerceDiscount, _serviceContextHelper);
			}
		}

		return commerceDiscount;
	}

	private static final EntityModel _entityModel = new DiscountEntityModel();

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceDiscountAccountRelService
		_commerceDiscountAccountRelService;

	@Reference
	private CommerceDiscountCommerceAccountGroupRelService
		_commerceDiscountCommerceAccountGroupRelService;

	@Reference
	private CommerceDiscountOrderTypeRelService
		_commerceDiscountOrderTypeRelService;

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter.DiscountDTOConverter)"
	)
	private DTOConverter<CommerceDiscount, Discount> _discountDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}