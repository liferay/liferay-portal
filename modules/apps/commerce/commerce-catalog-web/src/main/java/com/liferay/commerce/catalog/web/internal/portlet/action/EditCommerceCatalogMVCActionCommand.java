/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.portlet.action;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.AccountEntryStatusException;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.media.constants.CommerceMediaConstants;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CommerceCatalogProductsException;
import com.liferay.commerce.product.exception.CommerceCatalogSystemException;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CATALOGS,
		"mvc.command.name=/commerce_catalogs/edit_commerce_catalog"
	},
	service = MVCActionCommand.class
)
public class EditCommerceCatalogMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceCatalog(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				Callable<Object> commerceCatalogCallable =
					new CommerceCatalogCallable(actionRequest);

				TransactionInvokerUtil.invoke(
					_transactionConfig, commerceCatalogCallable);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof AccountEntryStatusException ||
				throwable instanceof AccountEntryTypeException ||
				throwable instanceof CommerceCatalogProductsException ||
				throwable instanceof CommerceCatalogSystemException ||
				throwable instanceof NoSuchPriceListException) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, throwable.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (throwable instanceof NoSuchCatalogException ||
					 throwable instanceof PrincipalException) {

				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(throwable, throwable);
			}
		}
	}

	private void _deleteCommerceCatalog(ActionRequest actionRequest)
		throws Exception {

		long[] commerceCatalogIds = null;

		long commerceCatalogId = ParamUtil.getLong(
			actionRequest, "commerceCatalogId");

		if (commerceCatalogId > 0) {
			commerceCatalogIds = new long[] {commerceCatalogId};
		}
		else {
			commerceCatalogIds = ParamUtil.getLongValues(
				actionRequest, "commerceCatalogIds");
		}

		for (long deleteCommerceCatalogId : commerceCatalogIds) {
			_commerceCatalogService.deleteCommerceCatalog(
				deleteCommerceCatalogId);
		}
	}

	private void _updateBasePriceListAndPromotion(
			ActionRequest actionRequest, CommerceCatalog commerceCatalog)
		throws Exception {

		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getConfiguration(
				CommercePricingConfiguration.class,
				new SystemSettingsLocator(
					CommercePricingConstants.SERVICE_NAME));

		if (!Objects.equals(
				commercePricingConfiguration.commercePricingCalculationKey(),
				CommercePricingConstants.VERSION_2_0)) {

			return;
		}

		// Base price list

		long baseCommercePriceListId = ParamUtil.getLong(
			actionRequest, "baseCommercePriceListId");

		_commercePriceListService.setCatalogBasePriceList(
			commerceCatalog.getGroupId(), baseCommercePriceListId,
			CommercePriceListConstants.TYPE_PRICE_LIST);

		// Base promotion

		long basePromotionCommercePriceListId = ParamUtil.getLong(
			actionRequest, "basePromotionCommercePriceListId");

		_commercePriceListService.setCatalogBasePriceList(
			commerceCatalog.getGroupId(), basePromotionCommercePriceListId,
			CommercePriceListConstants.TYPE_PROMOTION);
	}

	private CommerceCatalog _updateCommerceCatalog(ActionRequest actionRequest)
		throws Exception {

		long commerceCatalogId = ParamUtil.getLong(
			actionRequest, "commerceCatalogId");

		String name = ParamUtil.getString(actionRequest, "name");
		String commerceCurrencyCode = ParamUtil.getString(
			actionRequest, "commerceCurrencyCode");
		String catalogDefaultLanguageId = ParamUtil.getString(
			actionRequest, "catalogDefaultLanguageId");

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalog(commerceCatalogId);

		if (commerceCatalog == null) {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceCatalog.class.getName(), actionRequest);

			commerceCatalog = _commerceCatalogService.addCommerceCatalog(
				null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, name,
				commerceCurrencyCode, catalogDefaultLanguageId, serviceContext);
		}
		else {
			commerceCatalog = _commerceCatalogService.updateCommerceCatalog(
				commerceCatalog.getCommerceCatalogId(),
				ParamUtil.getLong(
					actionRequest, "accountEntryId",
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
				name, commerceCurrencyCode, catalogDefaultLanguageId);
		}

		return commerceCatalog;
	}

	private void _updateDefaultImage(
			ActionRequest actionRequest, CommerceCatalog commerceCatalog)
		throws Exception {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceCatalog.getGroupId(),
				CommerceMediaConstants.SERVICE_NAME));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"defaultFileEntryId", String.valueOf(fileEntryId));

		modifiableSettings.store();
	}

	private void _updateInventoryMethodKey(
			ActionRequest actionRequest, CommerceCatalog commerceCatalog)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceCatalog.getGroupId(),
				CommerceInventoryConstants.SERVICE_NAME));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		Map<String, String> parameterMap = PropertiesParamUtil.getProperties(
			actionRequest, "inventorySettings--");

		for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
			modifiableSettings.setValue(entry.getKey(), entry.getValue());
		}

		modifiableSettings.store();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceCatalogMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private class CommerceCatalogCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			CommerceCatalog commerceCatalog = _updateCommerceCatalog(
				_actionRequest);

			_updateBasePriceListAndPromotion(_actionRequest, commerceCatalog);
			_updateDefaultImage(_actionRequest, commerceCatalog);
			_updateInventoryMethodKey(_actionRequest, commerceCatalog);

			return null;
		}

		private CommerceCatalogCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}