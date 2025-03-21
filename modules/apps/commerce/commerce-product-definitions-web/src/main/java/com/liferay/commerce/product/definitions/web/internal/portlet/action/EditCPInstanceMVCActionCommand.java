/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.pricing.exception.CommerceUndefinedBasePriceListException;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPDefinitionIgnoreSKUCombinationsException;
import com.liferay.commerce.product.exception.CPInstanceDeliverySubscriptionLengthException;
import com.liferay.commerce.product.exception.CPInstanceJsonException;
import com.liferay.commerce.product.exception.CPInstanceMaxPriceValueException;
import com.liferay.commerce.product.exception.CPInstancePriceException;
import com.liferay.commerce.product.exception.CPInstanceReplacementCPInstanceUuidException;
import com.liferay.commerce.product.exception.CPInstanceSkuException;
import com.liferay.commerce.product.exception.DuplicateCPInstanceException;
import com.liferay.commerce.product.exception.DuplicateCPInstanceExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchSkuContributorCPDefinitionOptionRelException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_instance"
	},
	service = MVCActionCommand.class
)
public class EditCPInstanceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Callable<CPInstance> cpInstanceCallable =
					new CPInstanceCallable(actionRequest);

				CPInstance cpInstance = TransactionInvokerUtil.invoke(
					_transactionConfig, cpInstanceCallable);

				String redirect = getSaveAndContinueRedirect(
					actionRequest, cpInstance);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (cmd.equals(Constants.ADD_MULTIPLE)) {
				Callable<CPInstance> cpInstanceCallable =
					new CPInstanceCallable(actionRequest);

				TransactionInvokerUtil.invoke(
					_transactionConfig, cpInstanceCallable);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPInstances(actionRequest);
			}
			else if (cmd.equals("updateSubscriptionInfo")) {
				updateSubscriptionInfo(actionRequest);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof CommerceUndefinedBasePriceListException ||
				throwable instanceof
					CPDefinitionIgnoreSKUCombinationsException ||
				throwable instanceof
					CPInstanceDeliverySubscriptionLengthException ||
				throwable instanceof CPInstanceJsonException ||
				throwable instanceof CPInstanceMaxPriceValueException ||
				throwable instanceof CPInstancePriceException ||
				throwable instanceof
					CPInstanceReplacementCPInstanceUuidException ||
				throwable instanceof CPInstanceSkuException ||
				throwable instanceof DuplicateCPInstanceException ||
				throwable instanceof
					NoSuchSkuContributorCPDefinitionOptionRelException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (throwable instanceof
						DuplicateCPInstanceExternalReferenceCodeException ||
					 throwable instanceof PrincipalException) {

				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw new PortletException(throwable);
			}
		}
	}

	protected String getSaveAndContinueRedirect(
			ActionRequest actionRequest, CPInstance cpInstance)
		throws Exception {

		if (cpInstance == null) {
			return ParamUtil.getString(actionRequest, "redirect");
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				actionRequest, themeDisplay.getScopeGroup(),
				CPDefinition.class.getName(), PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance"
		).setParameter(
			"cpDefinitionId", cpInstance.getCPDefinitionId()
		).setParameter(
			"cpInstanceId", cpInstance.getCPInstanceId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	protected void updateSubscriptionInfo(ActionRequest actionRequest)
		throws PortalException {

		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");

		boolean overrideSubscriptionInfo = ParamUtil.getBoolean(
			actionRequest, "overrideSubscriptionInfo");
		boolean subscriptionEnabled = ParamUtil.getBoolean(
			actionRequest, "subscriptionEnabled");
		int subscriptionLength = ParamUtil.getInteger(
			actionRequest, "subscriptionLength");

		String subscriptionType = ParamUtil.getString(
			actionRequest, "subscriptionType");

		UnicodeProperties subscriptionTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest,
				"subscriptionTypeSettings--" + subscriptionType + "--");

		long maxSubscriptionCycles = ParamUtil.getLong(
			actionRequest, "maxSubscriptionCycles");
		boolean deliverySubscriptionEnabled = ParamUtil.getBoolean(
			actionRequest, "deliverySubscriptionEnabled");
		int deliverySubscriptionLength = ParamUtil.getInteger(
			actionRequest, "deliverySubscriptionLength");

		String deliverySubscriptionType = ParamUtil.getString(
			actionRequest, "deliverySubscriptionType");

		UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest,
				"deliverySubscriptionTypeSettings--" +
					deliverySubscriptionType + "--");

		long deliveryMaxSubscriptionCycles = ParamUtil.getLong(
			actionRequest, "deliveryMaxSubscriptionCycles");

		_cpInstanceService.updateSubscriptionInfo(
			cpInstanceId, overrideSubscriptionInfo, subscriptionEnabled,
			subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	private CPInstance _addOrUpdateCPInstance(ActionRequest actionRequest)
		throws Exception {

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");
		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");

		String sku = ParamUtil.getString(actionRequest, "sku");
		String gtin = ParamUtil.getString(actionRequest, "gtin");
		String manufacturerPartNumber = ParamUtil.getString(
			actionRequest, "manufacturerPartNumber");
		boolean purchasable = ParamUtil.getBoolean(
			actionRequest, "purchasable");
		boolean published = ParamUtil.getBoolean(actionRequest, "published");
		double width = ParamUtil.getDouble(actionRequest, "width");
		double height = ParamUtil.getDouble(actionRequest, "height");
		double depth = ParamUtil.getDouble(actionRequest, "depth");
		double weight = ParamUtil.getDouble(actionRequest, "weight");
		BigDecimal price = _commercePriceFormatter.parse(
			actionRequest, false, CPInstance.class.getName(), "price");
		BigDecimal promoPrice = _commercePriceFormatter.parse(
			actionRequest, false, CPInstance.class.getName(), "promoPrice");
		BigDecimal cost = _commercePriceFormatter.parse(
			actionRequest, false, CPInstance.class.getName(), "cost");
		int displayDateMonth = ParamUtil.getInteger(
			actionRequest, "displayDateMonth");
		int displayDateDay = ParamUtil.getInteger(
			actionRequest, "displayDateDay");
		int displayDateYear = ParamUtil.getInteger(
			actionRequest, "displayDateYear");
		int displayDateHour = ParamUtil.getInteger(
			actionRequest, "displayDateHour");
		int displayDateMinute = ParamUtil.getInteger(
			actionRequest, "displayDateMinute");
		int displayDateAmPm = ParamUtil.getInteger(
			actionRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		int expirationDateMonth = ParamUtil.getInteger(
			actionRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			actionRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			actionRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			actionRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			actionRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			actionRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		boolean neverExpire = ParamUtil.getBoolean(
			actionRequest, "neverExpire");
		String unspsc = ParamUtil.getString(actionRequest, "unspsc");
		boolean discontinued = ParamUtil.getBoolean(
			actionRequest, "discontinued");

		CPInstance cpInstance = null;

		String replacementCPInstanceUuid = null;
		long replacementCProductId = 0;
		int discontinuedDateMonth = 0;
		int discontinuedDateDay = 0;
		int discontinuedDateYear = 0;

		if (discontinued) {
			long replacementCPInstanceId = ParamUtil.getLong(
				actionRequest, "replacementCPInstanceId");

			if (replacementCPInstanceId > 0) {
				CPInstance replacementCPInstance =
					_cpInstanceService.fetchCPInstance(replacementCPInstanceId);

				if (replacementCPInstance != null) {
					replacementCPInstanceUuid =
						replacementCPInstance.getCPInstanceUuid();

					CPDefinition replacementCPDefinition =
						replacementCPInstance.getCPDefinition();

					replacementCProductId =
						replacementCPDefinition.getCProductId();
				}
			}

			Date discontinuedDate = ParamUtil.getDate(
				actionRequest, "discontinuedDate",
				DateFormatFactoryUtil.getSimpleDateFormat("MM/dd/yyyy"), null);

			if (discontinuedDate != null) {
				Calendar calendar = CalendarFactoryUtil.getCalendar(
					discontinuedDate.getTime());

				discontinuedDateDay = calendar.get(Calendar.DAY_OF_MONTH);
				discontinuedDateMonth = calendar.get(Calendar.MONTH);
				discontinuedDateYear = calendar.get(Calendar.YEAR);
			}
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPInstance.class.getName(), actionRequest);

		if (cpInstanceId > 0) {
			cpInstance = _cpInstanceService.getCPInstance(cpInstanceId);

			price = (BigDecimal)ParamUtil.getNumber(
				actionRequest, "price", cpInstance.getPrice());
			promoPrice = (BigDecimal)ParamUtil.getNumber(
				actionRequest, "promoPrice", cpInstance.getPromoPrice());
			cost = (BigDecimal)ParamUtil.getNumber(
				actionRequest, "cost", cpInstance.getCost());

			cpInstance = _cpInstanceService.updateCPInstance(
				externalReferenceCode, cpInstanceId, sku, gtin,
				manufacturerPartNumber, purchasable, width, height, depth,
				weight, price, promoPrice, cost, published, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, cpInstance.isOverrideSubscriptionInfo(),
				cpInstance.isSubscriptionEnabled(),
				cpInstance.getSubscriptionLength(),
				cpInstance.getSubscriptionType(),
				cpInstance.getSubscriptionTypeSettingsUnicodeProperties(),
				cpInstance.getMaxSubscriptionCycles(),
				cpInstance.isDeliverySubscriptionEnabled(),
				cpInstance.getDeliverySubscriptionLength(),
				cpInstance.getDeliverySubscriptionType(),
				cpInstance.
					getDeliverySubscriptionTypeSettingsUnicodeProperties(),
				cpInstance.getDeliveryMaxSubscriptionCycles(), unspsc,
				discontinued, replacementCPInstanceUuid, replacementCProductId,
				discontinuedDateMonth, discontinuedDateDay,
				discontinuedDateYear, serviceContext);
		}
		else {
			long cpDefinitionId = ParamUtil.getLong(
				actionRequest, "cpDefinitionId");

			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(cpDefinitionId);

			cpInstance = _cpInstanceService.addCPInstance(
				externalReferenceCode, cpDefinitionId,
				cpDefinition.getGroupId(), sku, gtin, manufacturerPartNumber,
				purchasable,
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
						cpDefinitionId,
						ParamUtil.getString(
							actionRequest, "cpInstanceOptions")),
				width, height, depth, weight, price, promoPrice, cost,
				published, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, false, false, 1,
				StringPool.BLANK, null, 0, false, 1, StringPool.BLANK, null, 0,
				unspsc, discontinued, replacementCPInstanceUuid,
				replacementCProductId, discontinuedDateMonth,
				discontinuedDateDay, discontinuedDateYear, serviceContext);
		}

		cpInstance = _cpInstanceService.updatePricingInfo(
			cpInstance.getCPInstanceId(), price, promoPrice, cost,
			serviceContext);

		if (Objects.equals(
				_getCommercePricingConfigurationKey(),
				CommercePricingConstants.VERSION_2_0)) {

			_updateCommercePriceEntries(
				cpInstance, price,
				ParamUtil.getBoolean(actionRequest, "priceOnApplication"),
				promoPrice, ServiceContextFactory.getInstance(actionRequest));
		}

		return cpInstance;
	}

	private void _buildCPInstances(ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPInstance.class.getName(), actionRequest);

		List<CPInstance> cpInstances = _cpInstanceService.buildCPInstances(
			cpDefinitionId, serviceContext);

		for (CPInstance cpInstance : cpInstances) {
			cpInstance = _cpInstanceService.updatePricingInfo(
				cpInstance.getCPInstanceId(), cpInstance.getPrice(),
				cpInstance.getPromoPrice(), cpInstance.getCost(),
				serviceContext);

			if (Objects.equals(
					_getCommercePricingConfigurationKey(),
					CommercePricingConstants.VERSION_2_0)) {

				_updateCommercePriceEntries(
					cpInstance, cpInstance.getPrice(), false,
					cpInstance.getPromoPrice(),
					ServiceContextFactory.getInstance(actionRequest));
			}
		}
	}

	private void _deleteCPInstances(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPInstanceIds = null;

		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");

		if (cpInstanceId > 0) {
			deleteCPInstanceIds = new long[] {cpInstanceId};
		}
		else {
			deleteCPInstanceIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteCPInstanceIds"), 0L);
		}

		for (long deleteCPInstanceId : deleteCPInstanceIds) {
			_cpInstanceService.deleteCPInstance(deleteCPInstanceId);
		}
	}

	private String _getCommercePricingConfigurationKey() throws Exception {
		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getConfiguration(
				CommercePricingConfiguration.class,
				new SystemSettingsLocator(
					CommercePricingConstants.SERVICE_NAME));

		return commercePricingConfiguration.commercePricingCalculationKey();
	}

	private void _updateCommercePriceEntries(
			CPInstance cpInstance, BigDecimal price, boolean priceOnApplication,
			BigDecimal promoPrice, ServiceContext serviceContext)
		throws Exception {

		_updateCommercePriceEntry(
			cpInstance, CommercePriceListConstants.TYPE_PRICE_LIST, price,
			priceOnApplication, serviceContext);
		_updateCommercePriceEntry(
			cpInstance, CommercePriceListConstants.TYPE_PROMOTION, promoPrice,
			priceOnApplication, serviceContext);
	}

	private void _updateCommercePriceEntry(
			CPInstance cpInstance, String type, BigDecimal price,
			boolean priceOnApplication, ServiceContext serviceContext)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				getCatalogBaseCommercePriceListByType(
					cpInstance.getGroupId(), type);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid(), StringPool.BLANK);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		if (commercePriceEntry == null) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			_commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price,
				priceOnApplication, null, null, serviceContext);
		}
		else {
			_commercePriceEntryLocalService.updatePricingInfo(
				commercePriceEntry.getCommercePriceEntryId(),
				commercePriceEntry.isBulkPricing(), price, priceOnApplication,
				null, null, serviceContext);
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPInstanceService _cpInstanceService;

	private class CPInstanceCallable implements Callable<CPInstance> {

		@Override
		public CPInstance call() throws Exception {
			String cmd = ParamUtil.getString(_actionRequest, Constants.CMD);

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				return _addOrUpdateCPInstance(_actionRequest);
			}

			if (cmd.equals(Constants.ADD_MULTIPLE)) {
				_buildCPInstances(_actionRequest);
			}

			return null;
		}

		private CPInstanceCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}