/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.exception.CommercePriceEntryPriceException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureIncrementalOrderQuantityException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasurePriceException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureQuantityException;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureRateException;
import com.liferay.commerce.product.exception.DuplicateCPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_instance_unit_of_measure"
	},
	service = MVCActionCommand.class
)
public class EditCPInstanceUnitOfMeasureMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {
				_addOrUpdateCPInstanceUnitOfMeasure(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					_addOrUpdateCPInstanceUnitOfMeasure(actionRequest);

				String redirect = getSaveAndContinueRedirect(
					actionRequest, cpInstanceUnitOfMeasure);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPInstanceUnitOfMeasures(actionRequest);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof CommercePriceEntryPriceException ||
				throwable instanceof
					CPInstanceUnitOfMeasureIncrementalOrderQuantityException ||
				throwable instanceof CPInstanceUnitOfMeasurePriceException ||
				throwable instanceof CPInstanceUnitOfMeasureQuantityException ||
				throwable instanceof CPInstanceUnitOfMeasureRateException ||
				throwable instanceof
					DuplicateCPInstanceUnitOfMeasureKeyException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				hideDefaultErrorMessage(actionRequest);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				_log.error(throwable, throwable);

				throw new PortletException(throwable);
			}
		}
	}

	protected String getSaveAndContinueRedirect(
			ActionRequest actionRequest,
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure)
		throws Exception {

		if (cpInstanceUnitOfMeasure == null) {
			return ParamUtil.getString(actionRequest, "redirect");
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				actionRequest, themeDisplay.getScopeGroup(),
				CPDefinition.class.getName(), PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance_unit_of_measure"
		).setParameter(
			"cpInstanceId", cpInstanceUnitOfMeasure.getCPInstanceId()
		).setParameter(
			"cpInstanceUnitOfMeasureId",
			cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private CPInstanceUnitOfMeasure _addOrUpdateCPInstanceUnitOfMeasure(
			ActionRequest actionRequest)
		throws Exception {

		long cpInstanceUnitOfMeasureId = ParamUtil.getLong(
			actionRequest, "cpInstanceUnitOfMeasureId");
		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		BigDecimal incrementalOrderQuantity =
			_commerceOrderItemQuantityFormatter.parse(
				actionRequest, CPInstanceUnitOfMeasure.class.getName(),
				"incrementalOrderQuantity");
		String key = ParamUtil.getString(actionRequest, "key");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		int precision = ParamUtil.getInteger(actionRequest, "precision");
		BigDecimal pricingQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPInstanceUnitOfMeasure.class.getName(),
			"pricingQuantity");
		boolean primary = ParamUtil.getBoolean(actionRequest, "primary");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		BigDecimal rate = _commercePriceFormatter.parse(
			actionRequest, false, CPInstanceUnitOfMeasure.class.getName(),
			"rate");
		String sku = ParamUtil.getString(actionRequest, "sku");

		if (cpInstanceUnitOfMeasureId > 0) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureService.fetchCPInstanceUnitOfMeasure(
					cpInstanceUnitOfMeasureId);

			if (cpInstanceUnitOfMeasure.isPrimary()) {
				primary = true;
			}

			return _cpInstanceUnitOfMeasureService.
				updateCPInstanceUnitOfMeasure(
					cpInstanceUnitOfMeasureId, cpInstanceId, active,
					incrementalOrderQuantity, key, nameMap, precision,
					pricingQuantity, primary, priority, rate, sku);
		}

		_cpInstanceUnitOfMeasureService.addCPInstanceUnitOfMeasure(
			cpInstanceId, active, incrementalOrderQuantity, key, nameMap,
			precision, pricingQuantity, primary, priority, rate, sku);

		_updateCommercePriceEntries(actionRequest, cpInstanceId, key);

		return null;
	}

	private void _deleteCPInstanceUnitOfMeasures(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPInstanceUnitOfMeasures = StringUtil.split(
			ParamUtil.getString(actionRequest, "id"), 0L);

		for (long deleteCPInstanceUnitOfMeasureId :
				deleteCPInstanceUnitOfMeasures) {

			_cpInstanceUnitOfMeasureService.deleteCPInstanceUnitOfMeasure(
				deleteCPInstanceUnitOfMeasureId);
		}
	}

	private void _updateCommercePriceEntries(
			ActionRequest actionRequest, long cpInstanceId, String key)
		throws Exception {

		CPInstance cpInstance = _cpInstanceService.fetchCPInstance(
			cpInstanceId);

		BigDecimal basePrice = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(),
			"basePrice");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPInstanceUnitOfMeasure.class.getName(), actionRequest);

		if (basePrice != null) {
			_updateCommercePriceEntry(
				cpInstance.getCPInstanceUuid(), key, basePrice,
				CommercePriceListConstants.TYPE_PRICE_LIST, serviceContext);
		}

		BigDecimal promoPrice = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(),
			"promoPrice");

		if (promoPrice != null) {
			_updateCommercePriceEntry(
				cpInstance.getCPInstanceUuid(), key, promoPrice,
				CommercePriceListConstants.TYPE_PROMOTION, serviceContext);
		}
	}

	private void _updateCommercePriceEntry(
			String cpInstanceUuid, String unitOfMeasureKey, BigDecimal price,
			String type, ServiceContext serviceContext)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getInstanceBaseCommercePriceEntry(
				cpInstanceUuid, type, unitOfMeasureKey);

		if (commercePriceEntry != null) {
			_commercePriceEntryService.updatePricingInfo(
				commercePriceEntry.getCommercePriceEntryId(),
				commercePriceEntry.isBulkPricing(), price,
				commercePriceEntry.isPriceOnApplication(),
				commercePriceEntry.getPromoPrice(), unitOfMeasureKey,
				serviceContext);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPInstanceUnitOfMeasureMVCActionCommand.class);

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private CPInstanceUnitOfMeasureService _cpInstanceUnitOfMeasureService;

	@Reference
	private Localization _localization;

}