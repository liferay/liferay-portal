/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.rule.exception.NoSuchCOREntryException;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRule;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccount;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccountGroup;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleChannel;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleOrderType;
import com.liferay.headless.commerce.admin.order.internal.odata.entity.v1_0.OrderRuleEntityModel;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.OrderRuleAccountGroupUtil;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.OrderRuleAccountUtil;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.OrderRuleChannelUtil;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.OrderRuleOrderTypeUtil;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Marco Leo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/order-rule.properties",
	scope = ServiceScope.PROTOTYPE, service = OrderRuleResource.class
)
public class OrderRuleResourceImpl extends BaseOrderRuleResourceImpl {

	@Override
	public void deleteOrderRule(Long id) throws Exception {
		_corEntryService.deleteCOREntry(id);
	}

	@Override
	public void deleteOrderRuleByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		COREntry corEntry =
			_corEntryService.fetchCOREntryByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		if (corEntry == null) {
			throw new NoSuchCOREntryException(
				"Unable to find order rule with external reference code " +
					externalReferenceCode);
		}

		_corEntryService.deleteCOREntry(corEntry.getCOREntryId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public OrderRule getOrderRule(Long id) throws Exception {
		return _toOrderRule(GetterUtil.getLong(id));
	}

	@Override
	public OrderRule getOrderRuleByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		COREntry corEntry =
			_corEntryService.fetchCOREntryByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		if (corEntry == null) {
			throw new NoSuchCOREntryException(
				"Unable to find order rule with external reference code " +
					externalReferenceCode);
		}

		return _toOrderRule(corEntry.getCOREntryId());
	}

	@Override
	public Page<OrderRule> getOrderRulesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			COREntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"status", WorkflowConstants.STATUS_ANY);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toOrderRule(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public OrderRule patchOrderRule(Long id, OrderRule orderRule)
		throws Exception {

		return _toOrderRule(
			_updateOrderRule(_corEntryService.getCOREntry(id), orderRule));
	}

	@Override
	public OrderRule patchOrderRuleByExternalReferenceCode(
			String externalReferenceCode, OrderRule orderRule)
		throws Exception {

		COREntry corEntry =
			_corEntryService.fetchCOREntryByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		if (corEntry == null) {
			throw new NoSuchCOREntryException(
				"Unable to find order rule with external reference code " +
					externalReferenceCode);
		}

		return _toOrderRule(_updateOrderRule(corEntry, orderRule));
	}

	@Override
	public OrderRule postOrderRule(OrderRule orderRule) throws Exception {
		COREntry corEntry = _addCOREntry(
			orderRule.getExternalReferenceCode(), orderRule);

		return _toOrderRule(corEntry.getCOREntryId());
	}

	@Override
	public OrderRule putOrderRuleByExternalReferenceCode(
			String externalReferenceCode, OrderRule orderRule)
		throws Exception {

		COREntry corEntry =
			_corEntryService.fetchCOREntryByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		if (corEntry == null) {
			corEntry = _addCOREntry(externalReferenceCode, orderRule);

			return _toOrderRule(corEntry.getCOREntryId());
		}

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderRule.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderRule.getExpirationDate(), serviceContext.getTimeZone());

		corEntry = _corEntryService.updateCOREntry(
			corEntry.getCOREntryId(),
			GetterUtil.getBoolean(orderRule.getActive()),
			GetterUtil.getString(orderRule.getDescription()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(orderRule.getNeverExpire()),
			GetterUtil.getString(orderRule.getName()),
			GetterUtil.getInteger(orderRule.getPriority()),
			GetterUtil.getString(orderRule.getTypeSettings()), serviceContext);

		return _toOrderRule(_updateNestedResources(corEntry, orderRule));
	}

	private COREntry _addCOREntry(
			String externalReferenceCode, OrderRule orderRule)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderRule.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderRule.getExpirationDate(), serviceContext.getTimeZone());

		COREntry corEntry = _corEntryService.addCOREntry(
			externalReferenceCode, GetterUtil.getBoolean(orderRule.getActive()),
			orderRule.getDescription(), displayDateConfig.getMonth(),
			displayDateConfig.getDay(), displayDateConfig.getYear(),
			displayDateConfig.getHour(), displayDateConfig.getMinute(),
			expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
			expirationDateConfig.getYear(), expirationDateConfig.getHour(),
			expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(orderRule.getNeverExpire(), true),
			orderRule.getName(), GetterUtil.getInteger(orderRule.getPriority()),
			orderRule.getType(), orderRule.getTypeSettings(), serviceContext);

		return _updateNestedResources(corEntry, orderRule);
	}

	private Map<String, Map<String, String>> _getActions(COREntry corEntry)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"DELETE", corEntry.getCOREntryId(), "deleteOrderRule",
				_corEntryModelResourcePermission)
		).put(
			"get",
			addAction(
				"VIEW", corEntry.getCOREntryId(), "getOrderRule",
				_corEntryModelResourcePermission)
		).put(
			"permissions",
			addAction(
				"PERMISSIONS", corEntry.getCOREntryId(), "patchOrderRule",
				_corEntryModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE", corEntry.getCOREntryId(), "patchOrderRule",
				_corEntryModelResourcePermission)
		).build();
	}

	private OrderRule _toOrderRule(COREntry corEntry) throws Exception {
		return _toOrderRule(corEntry.getCOREntryId());
	}

	private OrderRule _toOrderRule(Long corEntryId) throws Exception {
		COREntry corEntry = _corEntryService.getCOREntry(corEntryId);

		return _orderRuleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(corEntry), _dtoConverterRegistry, corEntryId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private COREntry _updateNestedResources(
			COREntry corEntry, OrderRule orderRule)
		throws Exception {

		// Order rule account groups

		OrderRuleAccountGroup[] orderRuleAccountGroups =
			orderRule.getOrderRuleAccountGroup();

		if (orderRuleAccountGroups != null) {
			for (OrderRuleAccountGroup orderRuleAccountGroup :
					orderRuleAccountGroups) {

				COREntryRel corEntryRel = _corEntryRelService.fetchCOREntryRel(
					AccountGroup.class.getName(),
					orderRuleAccountGroup.getAccountGroupId(),
					corEntry.getCOREntryId());

				if (corEntryRel != null) {
					continue;
				}

				OrderRuleAccountGroupUtil.addCOREntryAccountGroupRel(
					_accountGroupService, _corEntryRelService, corEntry,
					orderRuleAccountGroup);
			}
		}

		// Order rule accounts

		OrderRuleAccount[] orderRuleAccounts = orderRule.getOrderRuleAccount();

		if (orderRuleAccounts != null) {
			for (OrderRuleAccount orderRuleAccount : orderRuleAccounts) {
				COREntryRel corEntryRel = _corEntryRelService.fetchCOREntryRel(
					AccountEntry.class.getName(),
					orderRuleAccount.getAccountId(), corEntry.getCOREntryId());

				if (corEntryRel != null) {
					continue;
				}

				OrderRuleAccountUtil.addCOREntryAccountRel(
					_accountEntryService, _corEntryRelService, corEntry,
					orderRuleAccount);
			}
		}

		// Order rule channels

		OrderRuleChannel[] orderRuleChannels = orderRule.getOrderRuleChannel();

		if (orderRuleChannels != null) {
			for (OrderRuleChannel orderRuleChannel : orderRuleChannels) {
				COREntryRel corEntryRel = _corEntryRelService.fetchCOREntryRel(
					CommerceChannel.class.getName(),
					orderRuleChannel.getChannelId(), corEntry.getCOREntryId());

				if (corEntryRel != null) {
					continue;
				}

				OrderRuleChannelUtil.addCOREntryCommerceChannelRel(
					_commerceChannelService, _corEntryRelService, corEntry,
					orderRuleChannel);
			}
		}

		// Order rule order types

		OrderRuleOrderType[] orderRuleOrderTypes =
			orderRule.getOrderRuleOrderType();

		if (orderRuleOrderTypes != null) {
			for (OrderRuleOrderType orderRuleOrderType : orderRuleOrderTypes) {
				COREntryRel corEntryRel = _corEntryRelService.fetchCOREntryRel(
					CommerceOrderType.class.getName(),
					orderRuleOrderType.getOrderRuleId(),
					corEntry.getCOREntryId());

				if (corEntryRel != null) {
					continue;
				}

				OrderRuleOrderTypeUtil.addCOREntryCommerceOrderTypeRel(
					_corEntryRelService, corEntry, _commerceOrderTypeService,
					orderRuleOrderType);
			}
		}

		return corEntry;
	}

	private COREntry _updateOrderRule(COREntry corEntry, OrderRule orderRule)
		throws Exception {

		ServiceContext serviceContext =
			_serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			orderRule.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			orderRule.getExpirationDate(), serviceContext.getTimeZone());

		corEntry = _corEntryService.updateCOREntry(
			corEntry.getCOREntryId(),
			GetterUtil.getBoolean(orderRule.getActive(), corEntry.isActive()),
			GetterUtil.getString(
				orderRule.getDescription(), corEntry.getDescription()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(orderRule.getNeverExpire(), true),
			GetterUtil.getString(orderRule.getName(), corEntry.getName()),
			GetterUtil.getInteger(
				orderRule.getPriority(), corEntry.getPriority()),
			GetterUtil.get(
				orderRule.getTypeSettings(), corEntry.getTypeSettings()),
			serviceContext);

		return _updateNestedResources(corEntry, orderRule);
	}

	private static final EntityModel _entityModel = new OrderRuleEntityModel();

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.order.rule.model.COREntry)"
	)
	private ModelResourcePermission<COREntry> _corEntryModelResourcePermission;

	@Reference
	private COREntryRelService _corEntryRelService;

	@Reference
	private COREntryService _corEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.OrderRuleDTOConverter)"
	)
	private DTOConverter<COREntry, OrderRule> _orderRuleDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}