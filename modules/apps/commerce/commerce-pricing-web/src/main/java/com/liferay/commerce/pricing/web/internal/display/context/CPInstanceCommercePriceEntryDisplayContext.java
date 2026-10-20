/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.portlet.action.CommercePriceListActionHelper;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.util.comparator.CPInstanceUnitOfMeasurePriorityComparator;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CPInstanceCommercePriceEntryDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPInstanceCommercePriceEntryDisplayContext(
		ActionHelper actionHelper,
		CommercePriceEntryService commercePriceEntryService,
		CommercePriceFormatter commercePriceFormatter,
		CommercePriceListActionHelper commercePriceListActionHelper,
		CommercePriceListService commercePriceListService,
		HttpServletRequest httpServletRequest) {

		super(actionHelper, httpServletRequest);

		_commercePriceEntryService = commercePriceEntryService;
		_commercePriceFormatter = commercePriceFormatter;
		_commercePriceListActionHelper = commercePriceListActionHelper;
		_commercePriceListService = commercePriceListService;
	}

	public String getBasePrice() throws PortalException {
		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		CommercePriceList commercePriceList =
			commercePriceEntry.getCommercePriceList();

		CommercePriceEntry instanceBaseCommercePriceEntry =
			_commercePriceEntryService.getInstanceBaseCommercePriceEntry(
				commercePriceEntry.getCPInstanceUuid(),
				commercePriceList.getType(),
				commercePriceEntry.getUnitOfMeasureKey());

		if (instanceBaseCommercePriceEntry == null) {
			return StringPool.DASH;
		}

		CommerceCurrency commerceCurrency =
			commercePriceList.getCommerceCurrency();

		CommerceMoney priceCommerceMoney =
			instanceBaseCommercePriceEntry.getPriceCommerceMoney(
				commerceCurrency.getCommerceCurrencyId());

		return priceCommerceMoney.format(cpRequestHelper.getLocale());
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
		long cpInstanceId = 0;

		CPInstance cpInstance = getCPInstance();

		if (cpInstance != null) {
			cpInstanceId = cpInstance.getCPInstanceId();
		}

		return cpInstanceId;
	}

	public List<CPInstanceUnitOfMeasure> getCPInstanceUnitOfMeasures()
		throws PortalException {

		CPInstance cpInstance = getCPInstance();

		return cpInstance.getCPInstanceUnitOfMeasures(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			CPInstanceUnitOfMeasurePriorityComparator.getInstance(true));
	}

	public CommercePriceEntry getCommercePriceEntry() throws PortalException {
		return _commercePriceListActionHelper.getCommercePriceEntry(
			cpRequestHelper.getRenderRequest());
	}

	public long getCommercePriceEntryId() throws PortalException {
		long commercePriceEntryId = 0;

		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		if (commercePriceEntry != null) {
			commercePriceEntryId = commercePriceEntry.getCommercePriceEntryId();
		}

		return commercePriceEntryId;
	}

	public CreationMenu getCreationMenu() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.getNamespace() +
						"addCommercePriceEntry");
				dropdownItem.setLabel(
					LanguageUtil.format(
						httpServletRequest, "add-x-to-price-list",
						HtmlUtil.escape(cpInstance.getSku()), false));
				dropdownItem.setTarget("event");
			}
		).build();
	}

	public String getFormattedDiscount(
			CommerceCurrency commerceCurrency, BigDecimal discount)
		throws PortalException {

		if (discount == null) {
			return StringPool.BLANK;
		}

		return _commercePriceFormatter.format(
			commerceCurrency, false, cpRequestHelper.getLocale(), discount);
	}

	public String getFormattedPrice(CommerceCurrency commerceCurrency)
		throws PortalException {

		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		return _commercePriceFormatter.format(
			commerceCurrency, false, cpRequestHelper.getLocale(),
			commercePriceEntry.getPrice());
	}

	public HashMap<String, Object> getJSContext() throws PortalException {
		CPInstance cpInstance = getCPInstance();

		return HashMapBuilder.<String, Object>put(
			"url",
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/cp_definitions/add_cp_instance_commerce_price_entry"
			).setRedirect(
				cpRequestHelper.getCurrentURL()
			).setParameter(
				"cpInstanceId", cpInstance.getCPInstanceId()
			).buildString()
		).build();
	}

	public HashMap<String, Object> getModalJSContext() throws PortalException {
		String cpInstanceUnitOfMeasureKey = StringPool.BLANK;

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			getCPInstanceUnitOfMeasures();

		if (!cpInstanceUnitOfMeasures.isEmpty()) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				cpInstanceUnitOfMeasures.get(0);

			cpInstanceUnitOfMeasureKey = cpInstanceUnitOfMeasure.getKey();
		}

		CPInstance cpInstance = getCPInstance();

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getInstanceBaseCommercePriceEntry(
				cpInstance.getCPInstanceUuid(),
				CommercePriceListConstants.TYPE_PRICE_LIST,
				cpInstanceUnitOfMeasureKey);

		BigDecimal basePrice = BigDecimal.ZERO;
		String currencyCode = StringPool.BLANK;
		int maxFractionDigits = 2;

		if (commercePriceEntry != null) {
			CommercePriceList commercePriceList =
				commercePriceEntry.getCommercePriceList();

			CommerceCurrency commerceCurrency =
				commercePriceList.getCommerceCurrency();

			basePrice = commercePriceEntry.getPrice();
			currencyCode = commerceCurrency.getCode();
			maxFractionDigits = commerceCurrency.getMaxFractionDigits();
		}

		BaseModelSearchResult<CommercePriceList>
			commercePriceListBaseModelSearchResult =
				_commercePriceListService.searchCommercePriceLists(
					cpInstance.getCompanyId(), StringPool.BLANK,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS,
					new Sort(Field.PRIORITY, Sort.DOUBLE_TYPE, false));

		return HashMapBuilder.<String, Object>put(
			"basePrice", basePrice
		).put(
			"cpInstanceId", cpInstance.getCPInstanceId()
		).put(
			"currency", currencyCode
		).put(
			"dataSetId", CommercePricingFDSNames.INSTANCE_PRICE_ENTRIES
		).put(
			"maxFractionDigits", maxFractionDigits
		).put(
			"namespace", liferayPortletResponse.getNamespace()
		).put(
			"priceLists",
			TransformUtil.transform(
				commercePriceListBaseModelSearchResult.getBaseModels(),
				commercePriceList -> HashMapBuilder.<String, Object>put(
					"currency",
					() -> {
						CommerceCurrency commerceCurrency =
							commercePriceList.getCommerceCurrency();

						return commerceCurrency.getCode();
					}
				).put(
					"label", commercePriceList.getName()
				).put(
					"value", commercePriceList.getCommercePriceListId()
				).build())
		).put(
			"unitOfMeasures",
			TransformUtil.transform(
				cpInstanceUnitOfMeasures,
				cpInstanceUnitOfMeasure -> HashMapBuilder.<String, Object>put(
					"label",
					cpInstanceUnitOfMeasure.getName(cpRequestHelper.getLocale())
				).put(
					"value", cpInstanceUnitOfMeasure.getKey()
				).build())
		).build();
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance"
		).setParameter(
			"cpInstanceId", getCPInstanceId()
		).setParameter(
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey()
		).setParameter(
			"screenNavigationEntryKey", "price-lists"
		).buildPortletURL();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return "price-lists";
	}

	public CreationMenu getTierPriceEntryCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getAddCommerceTierPriceEntryURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-new-price-tier"));
				dropdownItem.setTarget("modal-lg");
			}
		).build();
	}

	private String _getAddCommerceTierPriceEntryURL() throws Exception {
		CPInstance cpInstance = getCPInstance();

		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance_commerce_tier_price_entry"
		).setRedirect(
			cpRequestHelper.getCurrentURL()
		).setParameter(
			"commercePriceEntryId", getCommercePriceEntryId()
		).setParameter(
			"cpDefinitionId", cpInstance.getCPDefinitionId()
		).setParameter(
			"cpInstanceId", cpInstance.getCPInstanceId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final CommercePriceEntryService _commercePriceEntryService;
	private final CommercePriceFormatter _commercePriceFormatter;
	private final CommercePriceListActionHelper _commercePriceListActionHelper;
	private final CommercePriceListService _commercePriceListService;
	private CPInstance _cpInstance;

}