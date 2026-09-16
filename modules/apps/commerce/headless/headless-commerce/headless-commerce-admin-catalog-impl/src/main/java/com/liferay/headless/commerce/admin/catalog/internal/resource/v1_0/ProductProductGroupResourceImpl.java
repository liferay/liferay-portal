/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductProductGroup;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductProductGroupResource;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-product-group.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductProductGroupResource.class
)
public class ProductProductGroupResourceImpl
	extends BaseProductProductGroupResourceImpl {

	@NestedField(parentClass = Product.class, value = "productGroups")
	@Override
	public Page<ProductProductGroup> getProductIdProductGroupsPage(
			@NestedFieldId(value = "productId") Long id, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id, false);

		if (cpDefinition == null) {
			return Page.of(Collections.emptyList());
		}

		List<CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_commercePricingClassCPDefinitionRelLocalService.
					getCommercePricingClassByCPDefinitionId(
						cpDefinition.getCPDefinitionId());

		return Page.of(
			transform(
				ListUtil.subList(
					commercePricingClassCPDefinitionRels,
					pagination.getStartPosition(), pagination.getEndPosition()),
				this::_toProductProductGroup),
			pagination, commercePricingClassCPDefinitionRels.size());
	}

	private ProductProductGroup _toProductProductGroup(
			CommercePricingClassCPDefinitionRel
				commercePricingClassCPDefinitionRel)
		throws Exception {

		CommercePricingClass commercePricingClass =
			_commercePricingClassLocalService.getCommercePricingClass(
				commercePricingClassCPDefinitionRel.
					getCommercePricingClassId());

		return new ProductProductGroup() {
			{
				setExternalReferenceCode(
					commercePricingClass::getExternalReferenceCode);
				setId(
					commercePricingClassCPDefinitionRel::
						getCommercePricingClassCPDefinitionRelId);
				setProductGroupId(
					commercePricingClass::getCommercePricingClassId);
				setTitle(
					() -> commercePricingClass.getTitle(
						contextAcceptLanguage.getPreferredLocale()));
			}
		};
	}

	@Reference
	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

	@Reference
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

}