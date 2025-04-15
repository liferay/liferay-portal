/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.display.context;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.comparator.CPTaxCategoryCreateDateComparator;
import com.liferay.commerce.tax.engine.internal.display.context.helper.CommerceTaxCategoryMappingsRequestHelper;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Ivica Cardic
 */
public class CommerceTaxCategoryMappingsDisplayContext {

	public CommerceTaxCategoryMappingsDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceTaxCategoryMappingService commerceTaxCategoryMappingService,
		CommerceTaxMethodService commerceTaxMethodService,
		CPTaxCategoryService cpTaxCategoryService,
		RenderRequest renderRequest) {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceTaxCategoryMappingService = commerceTaxCategoryMappingService;
		_commerceTaxMethodService = commerceTaxMethodService;
		_cpTaxCategoryService = cpTaxCategoryService;

		_commerceTaxCategoryMappingsRequestHelper =
			new CommerceTaxCategoryMappingsRequestHelper(renderRequest);
		_modelResourcePermission = commerceChannelModelResourcePermission;
	}

	public String getAddTaxCategoryMappingURL() throws Exception {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_commerceTaxCategoryMappingsRequestHelper.getRequest(),
				CommercePortletKeys.COMMERCE_TAX_METHODS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_tax_methods/edit_commerce_tax_category_mapping"
		).setParameter(
			"commerceTaxMethodId", getCommerceTaxMethodId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<CPTaxCategory> getAvailableCPTaxCategories()
		throws PortalException {

		return _cpTaxCategoryService.getCPTaxCategories(
			_commerceTaxCategoryMappingsRequestHelper.getCompanyId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			CPTaxCategoryCreateDateComparator.getInstance(false));
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

		if (commerceTaxMethod != null) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByGroupId(
					commerceTaxMethod.getGroupId());

			return commerceChannel.getCommerceChannelId();
		}

		return ParamUtil.getLong(
			_commerceTaxCategoryMappingsRequestHelper.getRequest(),
			"commerceChannelId");
	}

	public CommerceTaxCategoryMapping getCommerceTaxCategoryMapping()
		throws PortalException {

		long commerceTaxCategoryMappingId = ParamUtil.getLong(
			_commerceTaxCategoryMappingsRequestHelper.getRequest(),
			"commerceTaxCategoryMappingId");

		return _commerceTaxCategoryMappingService.
			fetchCommerceTaxCategoryMapping(commerceTaxCategoryMappingId);
	}

	public CommerceTaxMethod getCommerceTaxMethod() throws PortalException {
		if (_commerceTaxMethod != null) {
			return _commerceTaxMethod;
		}

		long commerceTaxMethodId = getCommerceTaxMethodId();

		if (commerceTaxMethodId > 0) {
			_commerceTaxMethod = _commerceTaxMethodService.getCommerceTaxMethod(
				commerceTaxMethodId);
		}

		return _commerceTaxMethod;
	}

	public long getCommerceTaxMethodId() throws PortalException {
		return ParamUtil.getLong(
			_commerceTaxCategoryMappingsRequestHelper.getRequest(),
			"commerceTaxMethodId");
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasUpdateCommerceChannelPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddTaxCategoryMappingURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							_commerceTaxCategoryMappingsRequestHelper.
								getRequest(),
							"add-tax-category-mapping"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public boolean hasUpdateCommerceChannelPermission() throws PortalException {
		return _modelResourcePermission.contains(
			_commerceTaxCategoryMappingsRequestHelper.getPermissionChecker(),
			_commerceChannelLocalService.getCommerceChannel(
				getCommerceChannelId()),
			ActionKeys.UPDATE);
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceTaxCategoryMappingService
		_commerceTaxCategoryMappingService;
	private final CommerceTaxCategoryMappingsRequestHelper
		_commerceTaxCategoryMappingsRequestHelper;
	private CommerceTaxMethod _commerceTaxMethod;
	private final CommerceTaxMethodService _commerceTaxMethodService;
	private final CPTaxCategoryService _cpTaxCategoryService;
	private final ModelResourcePermission<CommerceChannel>
		_modelResourcePermission;

}