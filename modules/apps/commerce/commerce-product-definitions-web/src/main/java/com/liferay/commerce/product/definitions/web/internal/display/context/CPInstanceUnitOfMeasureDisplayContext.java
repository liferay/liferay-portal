/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;

/**
 * @author Crescenzo Rega
 */
public class CPInstanceUnitOfMeasureDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPInstanceUnitOfMeasureDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceProductPriceCalculation commerceProductPriceCalculation,
		CPInstanceUnitOfMeasureService cpInstanceUnitOfMeasureService) {

		super(actionHelper, httpServletRequest);

		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_commerceProductPriceCalculation = commerceProductPriceCalculation;
		_cpInstanceUnitOfMeasureService = cpInstanceUnitOfMeasureService;
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createActionURL(
					cpRequestHelper.getRenderResponse()
				).setActionName(
					"/cp_definitions/edit_cp_instance_unit_of_measure"
				).setCMD(
					Constants.DELETE
				).setRedirect(
					cpRequestHelper.getCurrentURL()
				).buildString(),
				"trash", "delete", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				null));
	}

	@Override
	public String getCatalogDefaultLanguageId() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		if (cpInstance == null) {
			return null;
		}

		CommerceCatalog commerceCatalog = cpInstance.getCommerceCatalog();

		return commerceCatalog.getCatalogDefaultLanguageId();
	}

	public String getCommerceCurrencyCode() throws PortalException {
		if (_commerceCurrencyCode != null) {
			return _commerceCurrencyCode;
		}

		CommerceCurrency commerceCurrency = _getCommerceCurrency();

		if (commerceCurrency != null) {
			_commerceCurrencyCode = commerceCurrency.getCode();
		}
		else {
			_commerceCurrencyCode = StringPool.BLANK;
		}

		return _commerceCurrencyCode;
	}

	public CPInstance getCPInstance() throws PortalException {
		if (_cpInstance != null) {
			return _cpInstance;
		}

		_cpInstance = actionHelper.getCPInstance(
			cpRequestHelper.getRenderRequest());

		return _cpInstance;
	}

	public long getCPInstanceId() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		if (cpInstance == null) {
			return 0;
		}

		return cpInstance.getCPInstanceId();
	}

	public CPInstanceUnitOfMeasure getCPInstanceUnitOfMeasure()
		throws PortalException {

		if (_cpInstanceUnitOfMeasure != null) {
			return _cpInstanceUnitOfMeasure;
		}

		_cpInstanceUnitOfMeasure = actionHelper.getCPInstanceUnitOfMeasure(
			cpRequestHelper.getRenderRequest());

		return _cpInstanceUnitOfMeasure;
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/cp_definitions/add_cp_instance_unit_of_measure"
					).setBackURL(
						cpRequestHelper.getCurrentURL()
					).setParameter(
						"cpInstanceId", getCPInstanceId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-value"));
				dropdownItem.setTarget("modal-lg");
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					cpRequestHelper.getRenderResponse()
				).setMVCRenderCommandName(
					"/cp_definitions/edit_cp_instance_unit_of_measure"
				).setRedirect(
					cpRequestHelper.getCurrentURL()
				).setParameter(
					"cpInstanceId", getCPInstanceId()
				).setParameter(
					"cpInstanceUnitOfMeasureId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				null, "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				null, null, "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));
	}

	public BigDecimal getPrice() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		if (cpInstance == null) {
			return BigDecimal.ZERO;
		}

		CommerceMoney commerceMoney =
			_commerceProductPriceCalculation.getBasePrice(
				cpInstance.getCPInstanceId(), _getCommerceCurrency(),
				_getPrimaryUnitOfMeasureKey(cpInstance.getCPInstanceId()));

		return _round(commerceMoney.getPrice());
	}

	public String getPrimaryCPInstanceUnitOfMeasureName()
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureService.fetchPrimaryCPInstanceUnitOfMeasure(
				getCPInstanceId());

		if (cpInstanceUnitOfMeasure != null) {
			return cpInstanceUnitOfMeasure.getName(cpRequestHelper.getLocale());
		}

		return StringPool.BLANK;
	}

	public BigDecimal getPromoPrice() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		if (cpInstance == null) {
			return BigDecimal.ZERO;
		}

		CommerceMoney commerceMoney =
			_commerceProductPriceCalculation.getBasePromoPrice(
				cpInstance.getCPInstanceId(), _getCommerceCurrency(),
				_getPrimaryUnitOfMeasureKey(cpInstance.getCPInstanceId()));

		return _round(commerceMoney.getPrice());
	}

	public boolean hasCPInstanceUnitOfMeasure() throws PortalException {
		if (_hasCPInstanceUnitOfMeasure != null) {
			return _hasCPInstanceUnitOfMeasure;
		}

		int cpInstanceUnitOfMeasuresCount =
			_cpInstanceUnitOfMeasureService.getCPInstanceUnitOfMeasuresCount(
				getCPInstanceId());

		if (cpInstanceUnitOfMeasuresCount > 0) {
			_hasCPInstanceUnitOfMeasure = Boolean.TRUE;
		}
		else {
			_hasCPInstanceUnitOfMeasure = Boolean.FALSE;
		}

		return _hasCPInstanceUnitOfMeasure;
	}

	private CommerceCurrency _getCommerceCurrency() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		CommerceCatalog commerceCatalog = cpInstance.getCommerceCatalog();

		return _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCatalog.getCompanyId(),
			commerceCatalog.getCommerceCurrencyCode());
	}

	private String _getPrimaryUnitOfMeasureKey(long cpInstanceId)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureService.fetchPrimaryCPInstanceUnitOfMeasure(
				cpInstanceId);

		String unitOfMeasureKey = StringPool.BLANK;

		if (cpInstanceUnitOfMeasure != null) {
			unitOfMeasureKey = cpInstanceUnitOfMeasure.getKey();
		}

		return unitOfMeasureKey;
	}

	private BigDecimal _round(BigDecimal value) throws PortalException {
		CommerceCurrency commerceCurrency = _getCommerceCurrency();

		if (commerceCurrency == null) {
			return value;
		}

		return commerceCurrency.round(value);
	}

	private String _commerceCurrencyCode;
	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private final CommerceProductPriceCalculation
		_commerceProductPriceCalculation;
	private CPInstance _cpInstance;
	private CPInstanceUnitOfMeasure _cpInstanceUnitOfMeasure;
	private final CPInstanceUnitOfMeasureService
		_cpInstanceUnitOfMeasureService;
	private Boolean _hasCPInstanceUnitOfMeasure;

}