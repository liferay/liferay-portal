/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.order.rule.exception.NoSuchCOREntryException;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRule;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccountGroup;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderRuleAccountGroupResource;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Marco Leo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/order-rule-account-group.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = OrderRuleAccountGroupResource.class
)
public class OrderRuleAccountGroupResourceImpl
	extends BaseOrderRuleAccountGroupResourceImpl {

	@Override
	public void deleteOrderRuleAccountGroup(Long id) throws Exception {
		_corEntryRelService.deleteCOREntryRel(id);
	}

	@Override
	public Page<OrderRuleAccountGroup>
			getOrderRuleByExternalReferenceCodeOrderRuleAccountGroupsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		COREntry corEntry =
			_corEntryService.fetchCOREntryByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		if (corEntry == null) {
			throw new NoSuchCOREntryException(
				"Unable to find order rule with external reference code " +
					externalReferenceCode);
		}

		return Page.of(
			transform(
				_corEntryRelService.getAccountGroupCOREntryRels(
					corEntry.getCOREntryId(), null,
					pagination.getStartPosition(), pagination.getEndPosition()),
				corEntryRel -> _toOrderRuleAccountGroup(corEntryRel)),
			pagination,
			_corEntryRelService.getAccountGroupCOREntryRelsCount(
				corEntry.getCOREntryId(), null));
	}

	@NestedField(
		parentClass = OrderRule.class, value = "orderRuleAccountGroups"
	)
	@Override
	public Page<OrderRuleAccountGroup> getOrderRuleIdOrderRuleAccountGroupsPage(
			Long id, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		COREntry corEntry = _corEntryService.fetchCOREntry(id);

		if (corEntry == null) {
			throw new NoSuchCOREntryException(
				"Unable to find order rule with ID " + id);
		}

		return Page.of(
			transform(
				_corEntryRelService.getAccountGroupCOREntryRels(
					id, search, pagination.getStartPosition(),
					pagination.getEndPosition()),
				corEntryRel -> _toOrderRuleAccountGroup(corEntryRel)),
			pagination,
			_corEntryRelService.getAccountGroupCOREntryRelsCount(id, search));
	}

	@Override
	public OrderRuleAccountGroup
			postOrderRuleByExternalReferenceCodeOrderRuleAccountGroup(
				String externalReferenceCode,
				OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		COREntry corEntry =
			_corEntryService.fetchCOREntryByExternalReferenceCode(
				contextCompany.getCompanyId(), externalReferenceCode);

		if (corEntry == null) {
			throw new NoSuchCOREntryException(
				"Unable to find order rule with external reference code " +
					externalReferenceCode);
		}

		AccountGroup accountGroup = _getAccountGroup(orderRuleAccountGroup);

		return _toOrderRuleAccountGroup(
			_corEntryRelService.addCOREntryRel(
				AccountGroup.class.getName(), accountGroup.getAccountGroupId(),
				corEntry.getCOREntryId()));
	}

	@Override
	public OrderRuleAccountGroup postOrderRuleIdOrderRuleAccountGroup(
			Long id, OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		AccountGroup accountGroup = _getAccountGroup(orderRuleAccountGroup);

		return _toOrderRuleAccountGroup(
			_corEntryRelService.addCOREntryRel(
				AccountGroup.class.getName(), accountGroup.getAccountGroupId(),
				id));
	}

	private AccountGroup _getAccountGroup(
			OrderRuleAccountGroup orderRuleAccountGroup)
		throws Exception {

		AccountGroup accountGroup = null;

		if (orderRuleAccountGroup.getAccountGroupId() > 0) {
			accountGroup = _accountGroupService.getAccountGroup(
				orderRuleAccountGroup.getAccountGroupId());
		}
		else {
			accountGroup =
				_accountGroupService.getAccountGroupByExternalReferenceCode(
					orderRuleAccountGroup.
						getAccountGroupExternalReferenceCode(),
					contextCompany.getCompanyId());
		}

		return accountGroup;
	}

	private Map<String, Map<String, String>> _getActions(
			COREntryRel corEntryRel)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE", corEntryRel.getCOREntryRelId(),
				"deleteOrderRuleAccountGroup",
				_corEntryRelModelResourcePermission)
		).build();
	}

	private OrderRuleAccountGroup _toOrderRuleAccountGroup(
			COREntryRel corEntryRel)
		throws Exception {

		return _orderRuleAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(corEntryRel), _dtoConverterRegistry,
				corEntryRel.getCOREntryRelId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.order.rule.model.COREntryRel)"
	)
	private ModelResourcePermission<COREntryRel>
		_corEntryRelModelResourcePermission;

	@Reference
	private COREntryRelService _corEntryRelService;

	@Reference
	private COREntryService _corEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.OrderRuleAccountGroupDTOConverter)"
	)
	private DTOConverter<COREntryRel, OrderRuleAccountGroup>
		_orderRuleAccountGroupDTOConverter;

}