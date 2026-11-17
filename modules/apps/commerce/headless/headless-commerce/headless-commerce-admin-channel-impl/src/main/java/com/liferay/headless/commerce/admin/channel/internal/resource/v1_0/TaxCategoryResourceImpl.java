/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.resource.v1_0;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.TaxCategoryResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/tax-category.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxCategoryResource.class
)
public class TaxCategoryResourceImpl extends BaseTaxCategoryResourceImpl {

	@Override
	public TaxCategory patchTaxCategory(Long id, TaxCategory taxCategory)
		throws Exception {

		return _updateTaxCategory(
			_cpTaxCategoryService.getCPTaxCategory(id), taxCategory);
	}

	@Override
	public TaxCategory patchTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, TaxCategory taxCategory)
		throws Exception {

		return _updateTaxCategory(
			_cpTaxCategoryService.getCPTaxCategoryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId()),
			taxCategory);
	}

	@Override
	protected Page<TaxCategory> doGetTaxCategoriesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		_portletResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_PRODUCT_TAX_CATEGORIES);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CPTaxCategory.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toTaxCategory(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	protected TaxCategory doGetTaxCategory(Long id) throws Exception {
		CPTaxCategory cpTaxCategory = _cpTaxCategoryService.getCPTaxCategory(
			id);

		return _toTaxCategory(cpTaxCategory.getCPTaxCategoryId());
	}

	@Override
	protected TaxCategory doGetTaxCategoryByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryService.getCPTaxCategoryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return _toTaxCategory(cpTaxCategory.getCPTaxCategoryId());
	}

	@Override
	protected TaxCategory doPostTaxCategory(TaxCategory taxCategory)
		throws Exception {

		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryService.fetchCPTaxCategoryByExternalReferenceCode(
				taxCategory.getExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (cpTaxCategory != null) {
			return _updateTaxCategory(cpTaxCategory, taxCategory);
		}

		cpTaxCategory = _cpTaxCategoryService.addCPTaxCategory(
			taxCategory.getExternalReferenceCode(),
			LanguageUtils.getLocalizedMap(taxCategory.getName()),
			LanguageUtils.getLocalizedMap(taxCategory.getDescription()),
			_serviceContextHelper.getServiceContext());

		return _toTaxCategory(cpTaxCategory.getCPTaxCategoryId());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) {
		return 0L;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return CPTaxCategory.class.getName();
	}

	private TaxCategory _toTaxCategory(Long taxCategoryId) throws Exception {
		return _taxCategoryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				taxCategoryId, contextAcceptLanguage.getPreferredLocale()));
	}

	private TaxCategory _updateTaxCategory(
			CPTaxCategory cpTaxCategory, TaxCategory taxCategory)
		throws Exception {

		Map<String, String> nameMap = taxCategory.getName();

		if (nameMap == null) {
			nameMap = LanguageUtils.getLanguageIdMap(
				cpTaxCategory.getNameMap());
		}

		Map<String, String> descriptionMap = taxCategory.getDescription();

		if (descriptionMap == null) {
			descriptionMap = LanguageUtils.getLanguageIdMap(
				cpTaxCategory.getDescriptionMap());
		}

		cpTaxCategory = _cpTaxCategoryService.updateCPTaxCategory(
			GetterUtil.get(
				taxCategory.getExternalReferenceCode(),
				cpTaxCategory.getExternalReferenceCode()),
			cpTaxCategory.getCPTaxCategoryId(),
			LanguageUtils.getLocalizedMap(nameMap),
			LanguageUtils.getLocalizedMap(descriptionMap));

		return _toTaxCategory(cpTaxCategory.getCPTaxCategoryId());
	}

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

	@Reference(target = "(resource.name=" + CPConstants.RESOURCE_NAME_TAX + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter.TaxCategoryDTOConverter)"
	)
	private DTOConverter<CPTaxCategory, TaxCategory> _taxCategoryDTOConverter;

}