/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0;

import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountRuleUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountRuleResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/discount-rule.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = DiscountRuleResource.class
)
public class DiscountRuleResourceImpl extends BaseDiscountRuleResourceImpl {

	@Override
	public void deleteDiscountRule(Long id) throws Exception {
		_commerceDiscountRuleService.deleteCommerceDiscountRule(id);
	}

	@Override
	public Page<DiscountRule>
			getDiscountByExternalReferenceCodeDiscountRulesPage(
				String externalReferenceCode, Pagination pagination)
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

		List<CommerceDiscountRule> commerceDiscountRules =
			_commerceDiscountRuleService.getCommerceDiscountRules(
				commerceDiscount.getCommerceDiscountId(),
				pagination.getStartPosition(), pagination.getEndPosition(),
				null);

		int totalCount =
			_commerceDiscountRuleService.getCommerceDiscountRulesCount(
				commerceDiscount.getCommerceDiscountId());

		return Page.of(
			_toDiscountRules(commerceDiscountRules), pagination, totalCount);
	}

	@NestedField(parentClass = Discount.class, value = "discountRules")
	@Override
	public Page<DiscountRule> getDiscountIdDiscountRulesPage(
			Long id, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		List<CommerceDiscountRule> commerceDiscountRules =
			_commerceDiscountRuleService.getCommerceDiscountRules(
				id, search, pagination.getStartPosition(),
				pagination.getEndPosition());

		int totalCount =
			_commerceDiscountRuleService.getCommerceDiscountRulesCount(
				id, search);

		return Page.of(
			_toDiscountRules(commerceDiscountRules), pagination, totalCount);
	}

	@Override
	public DiscountRule getDiscountRule(Long id) throws Exception {
		return _toDiscountRule(GetterUtil.getLong(id));
	}

	@Override
	public DiscountRule patchDiscountRule(Long id, DiscountRule discountRule)
		throws Exception {

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleService.getCommerceDiscountRule(id);

		return _toDiscountRule(
			_commerceDiscountRuleService.updateCommerceDiscountRule(
				commerceDiscountRule.getCommerceDiscountRuleId(),
				GetterUtil.get(
					discountRule.getName(), commerceDiscountRule.getName()),
				discountRule.getType(),
				GetterUtil.get(
					discountRule.getTypeSettingsValue(),
					GetterUtil.get(
						discountRule.getTypeSettings(),
						commerceDiscountRule.getTypeSettings()))));
	}

	@Override
	public DiscountRule postDiscountByExternalReferenceCodeDiscountRule(
			String externalReferenceCode, DiscountRule discountRule)
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

		CommerceDiscountRule commerceDiscountRule =
			DiscountRuleUtil.addOrUpdateCommerceDiscountRule(
				_commerceDiscountRuleService, discountRule, commerceDiscount,
				_serviceContextHelper);

		return _toDiscountRule(
			commerceDiscountRule.getCommerceDiscountRuleId());
	}

	@Override
	public DiscountRule postDiscountIdDiscountRule(
			Long id, DiscountRule discountRule)
		throws Exception {

		CommerceDiscountRule commerceDiscountRule =
			DiscountRuleUtil.addOrUpdateCommerceDiscountRule(
				_commerceDiscountRuleService, discountRule,
				_commerceDiscountService.getCommerceDiscount(id),
				_serviceContextHelper);

		return _toDiscountRule(
			commerceDiscountRule.getCommerceDiscountRuleId());
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceDiscountRule commerceDiscountRule)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE", commerceDiscountRule.getCommerceDiscountRuleId(),
				"deleteDiscountRule",
				_commerceDiscountRuleModelResourcePermission)
		).put(
			"get",
			addAction(
				"VIEW", commerceDiscountRule.getCommerceDiscountRuleId(),
				"getDiscountRule", _commerceDiscountRuleModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE", commerceDiscountRule.getCommerceDiscountRuleId(),
				"patchDiscountRule",
				_commerceDiscountRuleModelResourcePermission)
		).build();
	}

	private DiscountRule _toDiscountRule(
			CommerceDiscountRule commerceDiscountRule)
		throws Exception {

		return _toDiscountRule(
			commerceDiscountRule.getCommerceDiscountRuleId());
	}

	private DiscountRule _toDiscountRule(Long commerceDiscountRuleId)
		throws Exception {

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleService.getCommerceDiscountRule(
				commerceDiscountRuleId);

		return _discountRuleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceDiscountRule), _dtoConverterRegistry,
				commerceDiscountRuleId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private List<DiscountRule> _toDiscountRules(
			List<CommerceDiscountRule> commerceDiscountRules)
		throws Exception {

		return transform(
			commerceDiscountRules,
			commerceDiscountRule -> _toDiscountRule(
				commerceDiscountRule.getCommerceDiscountRuleId()));
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscountRule)"
	)
	private ModelResourcePermission<CommerceDiscountRule>
		_commerceDiscountRuleModelResourcePermission;

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter.DiscountRuleDTOConverter)"
	)
	private DTOConverter<CommerceDiscountRule, DiscountRule>
		_discountRuleDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}