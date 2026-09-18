/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.pricing.exception.NoSuchPriceModifierException;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierCategory;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.PriceModifierCategoryUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceModifierCategoryResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
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

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Alberti
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/price-modifier-category.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = PriceModifierCategoryResource.class
)
public class PriceModifierCategoryResourceImpl
	extends BasePriceModifierCategoryResourceImpl {

	@Override
	public void deletePriceModifierCategory(Long id) throws Exception {
		_commercePriceModifierRelService.deleteCommercePriceModifierRel(id);
	}

	@Override
	public Page<PriceModifierCategory>
			getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.
				fetchCommercePriceModifierByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceModifier == null) {
			throw new NoSuchPriceModifierException(
				"Unable to find price modifier with external reference code " +
					externalReferenceCode);
		}

		List<CommercePriceModifierRel> commercePriceModifierRels =
			_commercePriceModifierRelService.getCommercePriceModifierRels(
				commercePriceModifier.getCommercePriceModifierId(),
				AssetCategory.class.getName(), pagination.getStartPosition(),
				pagination.getEndPosition(), null);

		int totalCount =
			_commercePriceModifierRelService.getCommercePriceModifierRelsCount(
				commercePriceModifier.getCommercePriceModifierId(),
				AssetCategory.class.getName());

		return Page.of(
			_toPriceModifierCategories(commercePriceModifierRels), pagination,
			totalCount);
	}

	@NestedField(
		parentClass = PriceModifier.class, value = "priceModifierCategories"
	)
	@Override
	public Page<PriceModifierCategory>
			getPriceModifierIdPriceModifierCategoriesPage(
				Long id, String search, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		List<CommercePriceModifierRel> commercePriceModifierRels =
			_commercePriceModifierRelService.
				getCategoriesCommercePriceModifierRels(
					id, search, pagination.getStartPosition(),
					pagination.getEndPosition());

		int totalCount =
			_commercePriceModifierRelService.
				getCategoriesCommercePriceModifierRelsCount(id, search);

		return Page.of(
			_toPriceModifierCategories(commercePriceModifierRels), pagination,
			totalCount);
	}

	@Override
	public PriceModifierCategory
			postPriceModifierByExternalReferenceCodePriceModifierCategory(
				String externalReferenceCode,
				PriceModifierCategory priceModifierCategory)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.
				fetchCommercePriceModifierByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePriceModifier == null) {
			throw new NoSuchPriceModifierException(
				"Unable to find price modifier with external reference code " +
					externalReferenceCode);
		}

		CommercePriceModifierRel commercePriceModifierRel =
			PriceModifierCategoryUtil.addCommercePriceModifierRel(
				contextCompany.getGroupId(), _assetCategoryLocalService,
				_assetCategoryService, _commercePriceModifierRelService,
				priceModifierCategory, commercePriceModifier,
				_serviceContextHelper);

		return _toPriceModifierCategory(
			commercePriceModifierRel.getCommercePriceModifierRelId());
	}

	@Override
	public PriceModifierCategory postPriceModifierIdPriceModifierCategory(
			Long id, PriceModifierCategory priceModifierCategory)
		throws Exception {

		CommercePriceModifierRel commercePriceModifierRel =
			PriceModifierCategoryUtil.addCommercePriceModifierRel(
				contextCompany.getGroupId(), _assetCategoryLocalService,
				_assetCategoryService, _commercePriceModifierRelService,
				priceModifierCategory,
				_commercePriceModifierService.getCommercePriceModifier(id),
				_serviceContextHelper);

		return _toPriceModifierCategory(
			commercePriceModifierRel.getCommercePriceModifierRelId());
	}

	private Map<String, Map<String, String>> _getActions(
			CommercePriceModifierRel commercePriceModifierRel)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE",
				commercePriceModifierRel.getCommercePriceModifierRelId(),
				"deletePriceModifierCategory",
				_commercePriceModifierRelModelResourcePermission)
		).build();
	}

	private List<PriceModifierCategory> _toPriceModifierCategories(
			List<CommercePriceModifierRel> commercePriceModifierRels)
		throws Exception {

		return transform(
			commercePriceModifierRels,
			commercePriceModifierRel -> _toPriceModifierCategory(
				commercePriceModifierRel.getCommercePriceModifierRelId()));
	}

	private PriceModifierCategory _toPriceModifierCategory(
			Long commercePriceModifierRelId)
		throws Exception {

		CommercePriceModifierRel commercePriceModifierRel =
			_commercePriceModifierRelService.getCommercePriceModifierRel(
				commercePriceModifierRelId);

		return _priceModifierCategoryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commercePriceModifierRel), _dtoConverterRegistry,
				commercePriceModifierRelId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.pricing.model.CommercePriceModifierRel)"
	)
	private ModelResourcePermission<CommercePriceModifierRel>
		_commercePriceModifierRelModelResourcePermission;

	@Reference
	private CommercePriceModifierRelService _commercePriceModifierRelService;

	@Reference
	private CommercePriceModifierService _commercePriceModifierService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter.PriceModifierCategoryDTOConverter)"
	)
	private DTOConverter<CommercePriceModifierRel, PriceModifierCategory>
		_priceModifierCategoryDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}