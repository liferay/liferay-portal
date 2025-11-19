/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.commerce.exception.CPDefinitionInventoryAllowedOrderQuantitiesException;
import com.liferay.commerce.exception.CPDefinitionInventoryMaxOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryMinOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryMultipleOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryQuantityException;
import com.liferay.commerce.exception.NoSuchCPDefinitionInventoryException;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPConfigurationEntryAllowedOrderQuantitiesException;
import com.liferay.commerce.product.exception.CPConfigurationEntryQuantityException;
import com.liferay.commerce.product.exception.CPDefinitionExpirationDateException;
import com.liferay.commerce.product.exception.CPDefinitionMetaDescriptionException;
import com.liferay.commerce.product.exception.CPDefinitionMetaKeywordsException;
import com.liferay.commerce.product.exception.CPDefinitionMetaTitleException;
import com.liferay.commerce.product.exception.CPDefinitionNameDefaultLanguageException;
import com.liferay.commerce.product.exception.CPDefinitionSubscriptionLengthException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.service.CPDAvailabilityEstimateService;
import com.liferay.commerce.service.CPDefinitionInventoryService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.friendly.url.exception.FriendlyURLLengthException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import java.net.URL;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			CPDefinition cpDefinition = _getCPDefinition(actionRequest);

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Callable<CPDefinition> cpDefinitionCallable =
					new CPDefinitionCallable(actionRequest, cpDefinition);

				cpDefinition = TransactionInvokerUtil.invoke(
					_transactionConfig, cpDefinitionCallable);

				String redirect = getSaveAndContinueRedirect(
					actionRequest, cpDefinition.getCPDefinitionId(),
					CPDefinitionScreenNavigationConstants.CATEGORY_KEY_DETAILS);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPDefinitions(actionRequest);
			}
			else if (cmd.equals("deleteAccountGroup")) {
				_deleteAccountGroup(
					actionRequest, cpDefinition.getCPDefinitionId());

				String redirect = getSaveAndContinueRedirect(
					actionRequest, cpDefinition.getCPDefinitionId(),
					CPDefinitionScreenNavigationConstants.
						CATEGORY_KEY_VISIBILITY);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (cmd.equals("deleteChannel")) {
				_deleteChannel(actionRequest, cpDefinition.getCPDefinitionId());

				String redirect = getSaveAndContinueRedirect(
					actionRequest, cpDefinition.getCPDefinitionId(),
					CPDefinitionScreenNavigationConstants.
						CATEGORY_KEY_VISIBILITY);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (cmd.equals("updateAttachmentFileEntries") ||
					 cmd.equals("updateDefinitionLinks") ||
					 cmd.equals("updateDefinitionOptionRels") ||
					 cmd.equals("updateDefinitionPricingClasses") ||
					 cmd.equals("updateInstances")) {

				Callable<CPDefinition> cpDefinitionUpdateCallable =
					new CPDefinitionUpdateCallable(actionRequest, cpDefinition);

				cpDefinition = TransactionInvokerUtil.invoke(
					_transactionConfig, cpDefinitionUpdateCallable);

				_sendRedirect(actionRequest, actionResponse, cpDefinition);
			}
			else if (cmd.equals("updateConfiguration")) {
				Callable<Object> cpDefinitionConfigurationCallable =
					new CPDefinitionConfigurationCallable(
						actionRequest, cpDefinition);

				TransactionInvokerUtil.invoke(
					_transactionConfig, cpDefinitionConfigurationCallable);

				sendRedirect(
					actionRequest, actionResponse,
					getSaveAndContinueRedirect(
						actionRequest, cpDefinition.getCPDefinitionId(),
						CPDefinitionScreenNavigationConstants.
							CATEGORY_KEY_CONFIGURATION));
			}
			else if (cmd.equals("updateSubscriptionInfo")) {
				Callable<Object> cpDefinitionSubscriptionInfoCallable =
					new CPDefinitionSubscriptionInfoCallable(
						actionRequest, cpDefinition);

				TransactionInvokerUtil.invoke(
					_transactionConfig, cpDefinitionSubscriptionInfoCallable);

				sendRedirect(
					actionRequest, actionResponse,
					getSaveAndContinueRedirect(
						actionRequest, cpDefinition.getCPDefinitionId(),
						CPDefinitionScreenNavigationConstants.
							CATEGORY_KEY_SUBSCRIPTION));
			}
			else if (cmd.equals("updateVisibility")) {
				Callable<Object> cpDefinitionVisibilityCallable =
					new CPDefinitionVisibilityCallable(
						actionRequest, cpDefinition);

				TransactionInvokerUtil.invoke(
					_transactionConfig, cpDefinitionVisibilityCallable);

				sendRedirect(
					actionRequest, actionResponse,
					getSaveAndContinueRedirect(
						actionRequest, cpDefinition.getCPDefinitionId(),
						CPDefinitionScreenNavigationConstants.
							CATEGORY_KEY_VISIBILITY));
			}
			else {
				_sendRedirect(actionRequest, actionResponse, cpDefinition);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchCPDefinitionException ||
				throwable instanceof PrincipalException) {

				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (throwable instanceof AssetCategoryException ||
					 throwable instanceof AssetTagException ||
					 throwable instanceof
						 CPConfigurationEntryAllowedOrderQuantitiesException ||
					 throwable instanceof
						 CPConfigurationEntryQuantityException ||
					 throwable instanceof CPDefinitionExpirationDateException ||
					 throwable instanceof
						 CPDefinitionInventoryAllowedOrderQuantitiesException ||
					 throwable instanceof
						 CPDefinitionInventoryMaxOrderQuantityException ||
					 throwable instanceof
						 CPDefinitionInventoryMinOrderQuantityException ||
					 throwable instanceof
						 CPDefinitionInventoryMultipleOrderQuantityException ||
					 throwable instanceof
						 CPDefinitionInventoryQuantityException ||
					 throwable instanceof
						 CPDefinitionMetaDescriptionException ||
					 throwable instanceof CPDefinitionMetaKeywordsException ||
					 throwable instanceof CPDefinitionMetaTitleException ||
					 throwable instanceof
						 CPDefinitionNameDefaultLanguageException ||
					 throwable instanceof
						 CPDefinitionSubscriptionLengthException ||
					 throwable instanceof FriendlyURLLengthException ||
					 throwable instanceof NoSuchCatalogException ||
					 throwable instanceof
						 NoSuchCPDefinitionInventoryException ||
					 throwable instanceof NumberFormatException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				if (throwable instanceof RuntimeException) {
					Throwable currentThrowable = throwable;

					while (currentThrowable != null) {
						currentThrowable = currentThrowable.getCause();

						if (currentThrowable instanceof
								SanitizerException sanitizerException) {

							SessionErrors.add(
								actionRequest, SanitizerException.class,
								sanitizerException);

							String redirect = ParamUtil.getString(
								actionRequest, "redirect");

							sendRedirect(
								actionRequest, actionResponse, redirect);

							return;
						}
					}
				}

				_log.error(throwable, throwable);

				throw new Exception(throwable);
			}
		}
	}

	protected String getSaveAndContinueRedirect(
			ActionRequest actionRequest, long cpDefinitionId,
			String screenNavigationCategoryKey)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				actionRequest, themeDisplay.getScopeGroup(),
				CPDefinition.class.getName(), PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"cpDefinitionId", cpDefinitionId
		).setParameter(
			"screenNavigationCategoryKey", screenNavigationCategoryKey
		).buildString();
	}

	private void _deleteAccountGroup(
			ActionRequest actionRequest, long cpDefinitionId)
		throws PortalException {

		long accountGroupRelId = ParamUtil.getLong(
			actionRequest, "commerceAccountGroupRelId");

		_accountGroupRelLocalService.deleteAccountGroupRel(accountGroupRelId);

		_reindexCPDefinition(cpDefinitionId);
	}

	private void _deleteChannel(
			ActionRequest actionRequest, long cpDefinitionId)
		throws PortalException {

		long commerceChannelRelId = ParamUtil.getLong(
			actionRequest, "commerceChannelRelId");

		_commerceChannelRelService.deleteCommerceChannelRel(
			commerceChannelRelId);

		_reindexCPDefinition(cpDefinitionId);
	}

	private void _deleteCPDefinitions(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCPDefinitionIds = null;

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		if (cpDefinitionId > 0) {
			deleteCPDefinitionIds = new long[] {cpDefinitionId};
		}
		else {
			deleteCPDefinitionIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "id"), 0L);
		}

		for (long deleteCPDefinitionId : deleteCPDefinitionIds) {
			_cpDefinitionService.deleteCPDefinition(deleteCPDefinitionId);
		}
	}

	private CPDefinition _getCPDefinition(ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		if (cpDefinitionId <= 0) {
			return null;
		}

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpDefinitionId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinition.class.getName(), actionRequest);

		if (!cpDefinition.isDraft() &&
			(serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_SAVE_DRAFT)) {

			CProductVersionConfiguration cProductVersionConfiguration =
				_configurationProvider.getConfiguration(
					CProductVersionConfiguration.class,
					new CompanyServiceSettingsLocator(
						cpDefinition.getCompanyId(),
						CProductVersionConfiguration.class.getName()));

			if (cProductVersionConfiguration.enabled()) {
				List<CPDefinition> cProductCPDefinitions =
					_cpDefinitionService.getCProductCPDefinitions(
						cpDefinition.getCProductId(),
						WorkflowConstants.STATUS_DRAFT, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS);

				for (CPDefinition cProductCPDefinition :
						cProductCPDefinitions) {

					_cpDefinitionService.updateStatus(
						cProductCPDefinition.getCPDefinitionId(),
						WorkflowConstants.STATUS_INCOMPLETE, serviceContext,
						Collections.emptyMap());
				}

				boolean saveAsDraft = ParamUtil.getBoolean(
					actionRequest, "saveAsDraft");

				if (saveAsDraft) {
					cpDefinition = _cpDefinitionService.copyCPDefinition(
						cpDefinitionId, cpDefinition.getGroupId(),
						WorkflowConstants.STATUS_DRAFT);
				}
			}
		}

		return cpDefinition;
	}

	private Map<String, String> _getQueryMap(
		long cpDefinitionId, String query) {

		String[] params = query.split(StringPool.AMPERSAND);

		Map<String, String> map = new HashMap<>();

		for (String param : params) {
			String name = param.split(StringPool.EQUAL)[0];

			name = name.substring(name.lastIndexOf(StringPool.UNDERLINE) + 1);

			if (name.equals("cpDefinitionId")) {
				map.put(name, String.valueOf(cpDefinitionId));
			}
			else {
				map.put(name, param.split(StringPool.EQUAL)[1]);
			}
		}

		return map;
	}

	private ServiceContext _getServiceContext(
			ActionRequest actionRequest, CPDefinition cpDefinition)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinition.class.getName(), actionRequest);

		serviceContext.setAssetCategoryIds(
			_assetCategoryLocalService.getCategoryIds(
				CPDefinition.class.getName(),
				cpDefinition.getCPDefinitionId()));
		serviceContext.setAssetTagNames(
			_assetTagLocalService.getTagNames(
				CPDefinition.class.getName(),
				cpDefinition.getCPDefinitionId()));

		return serviceContext;
	}

	private void _reindexCPDefinition(long cpDefinitionId)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpDefinitionId);

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(cpDefinition);
	}

	private void _sendRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse,
			CPDefinition cpDefinition)
		throws Exception {

		URL redirectURL = new URL(
			ParamUtil.getString(actionRequest, "redirect"));

		Map<String, String> queryMap = _getQueryMap(
			cpDefinition.getCPDefinitionId(), redirectURL.getQuery());

		sendRedirect(
			actionRequest, actionResponse,
			getSaveAndContinueRedirect(
				actionRequest, Long.valueOf(queryMap.get("cpDefinitionId")),
				queryMap.get("screenNavigationCategoryKey")));
	}

	private CPDefinition _updateCPDefinition(
			ActionRequest actionRequest, CPDefinition cpDefinition)
		throws Exception {

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "nameMapAsXML");
		Map<Locale, String> shortDescriptionMap =
			_localization.getLocalizationMap(
				actionRequest, "shortDescriptionMapAsXML");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "descriptionMapAsXML");
		Map<Locale, String> urlTitleMap = _localization.getLocalizationMap(
			actionRequest, "urlTitleMapAsXML");
		Map<Locale, String> metaTitleMap = _localization.getLocalizationMap(
			actionRequest, "metaTitleMapAsXML");
		Map<Locale, String> metaDescriptionMap =
			_localization.getLocalizationMap(
				actionRequest, "metaDescriptionMapAsXML");
		Map<Locale, String> metaKeywordsMap = _localization.getLocalizationMap(
			actionRequest, "metaKeywordsMapAsXML");
		boolean published = ParamUtil.getBoolean(actionRequest, "published");

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

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinition.class.getName(), actionRequest);

		if (cpDefinition == null) {
			long commerceCatalogGroupId = ParamUtil.getLong(
				actionRequest, "commerceCatalogGroupId");

			CommerceCatalog commerceCatalog =
				_commerceCatalogService.fetchCommerceCatalogByGroupId(
					commerceCatalogGroupId);

			if (commerceCatalog == null) {
				throw new NoSuchCatalogException();
			}

			Locale defaultLocale = LocaleUtil.fromLanguageId(
				commerceCatalog.getCatalogDefaultLanguageId());

			if (Validator.isNull(nameMap.get(defaultLocale))) {
				throw new CPDefinitionNameDefaultLanguageException();
			}

			String productTypeName = ParamUtil.getString(
				actionRequest, "productTypeName");

			cpDefinition = _cpDefinitionService.addCPDefinition(
				null, commerceCatalogGroupId, nameMap, shortDescriptionMap,
				descriptionMap, urlTitleMap, metaTitleMap, metaDescriptionMap,
				metaKeywordsMap, productTypeName, true, true, false, false, 0D,
				0D, 0D, 0D, 0D, 0L, false, false, null, published,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire,
				CPInstanceConstants.DEFAULT_SKU, false, 1, null, null, 0L,
				false, 1, null, null, 0, false, false,
				WorkflowConstants.STATUS_DRAFT, serviceContext);
		}
		else {
			cpDefinition = _cpDefinitionService.updateCPDefinition(
				cpDefinition.getCPDefinitionId(), nameMap, shortDescriptionMap,
				descriptionMap, urlTitleMap, metaTitleMap, metaDescriptionMap,
				metaKeywordsMap, cpDefinition.isIgnoreSKUCombinations(),
				cpDefinition.isShippable(), cpDefinition.isFreeShipping(),
				cpDefinition.isShipSeparately(),
				cpDefinition.getShippingExtraPrice(), cpDefinition.getWidth(),
				cpDefinition.getHeight(), cpDefinition.getDepth(),
				cpDefinition.getWeight(), cpDefinition.getCPTaxCategoryId(),
				cpDefinition.isTaxExempt(), cpDefinition.isTelcoOrElectronics(),
				null, published, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute,
				cpDefinition.isAccountGroupFilterEnabled(),
				cpDefinition.isChannelFilterEnabled(), neverExpire,
				serviceContext);
		}

		return cpDefinition;
	}

	private CPDefinition _updateCPDefinition(
			CPDefinition cpDefinition, ServiceContext serviceContext)
		throws Exception {

		Date displayDate = cpDefinition.getDisplayDate();

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			displayDate.getTime());

		int displayDateHour = displayCalendar.get(Calendar.HOUR);
		int displayDateAmPm = displayCalendar.get(Calendar.AM_PM);

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		int expirationDateMonth = 0;
		int expirationDateDay = 0;
		int expirationDateYear = 0;
		int expirationDateHour = 0;
		int expirationDateMinute = 0;
		boolean neverExpire = true;

		if (cpDefinition.getExpirationDate() != null) {
			Date expirationDate = cpDefinition.getExpirationDate();

			Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
				expirationDate.getTime());

			expirationDateMonth = expirationCalendar.get(Calendar.MONTH);

			expirationDateDay = expirationCalendar.get(Calendar.DAY_OF_MONTH);

			expirationDateYear = expirationCalendar.get(Calendar.YEAR);

			expirationDateHour = expirationCalendar.get(Calendar.HOUR);

			expirationDateMinute = expirationCalendar.get(Calendar.MINUTE);

			int expirationDateAmPm = expirationCalendar.get(Calendar.AM_PM);

			if (expirationDateAmPm == Calendar.PM) {
				expirationDateHour += 12;
			}

			neverExpire = false;
		}

		return _cpDefinitionService.updateCPDefinition(
			cpDefinition.getCPDefinitionId(), cpDefinition.getNameMap(),
			cpDefinition.getShortDescriptionMap(),
			cpDefinition.getDescriptionMap(), cpDefinition.getUrlTitleMap(),
			cpDefinition.getMetaTitleMap(),
			cpDefinition.getMetaDescriptionMap(),
			cpDefinition.getMetaKeywordsMap(),
			cpDefinition.isIgnoreSKUCombinations(), cpDefinition.isShippable(),
			cpDefinition.isFreeShipping(), cpDefinition.isShipSeparately(),
			cpDefinition.getShippingExtraPrice(), cpDefinition.getWidth(),
			cpDefinition.getHeight(), cpDefinition.getDepth(),
			cpDefinition.getWeight(), cpDefinition.getCPTaxCategoryId(),
			cpDefinition.isTaxExempt(), cpDefinition.isTelcoOrElectronics(),
			cpDefinition.getDDMStructureKey(), cpDefinition.isPublished(),
			displayCalendar.get(Calendar.MONTH),
			displayCalendar.get(Calendar.DAY_OF_MONTH),
			displayCalendar.get(Calendar.YEAR), displayDateHour,
			displayCalendar.get(Calendar.MINUTE), expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, cpDefinition.isAccountGroupFilterEnabled(),
			cpDefinition.isChannelFilterEnabled(), neverExpire, serviceContext);
	}

	private void _updateCPDefinitionInventory(
			ActionRequest actionRequest, long cpDefinitionId)
		throws Exception {

		long cpdAvailabilityEstimateEntryId = ParamUtil.getLong(
			actionRequest, "cpdAvailabilityEstimateEntryId");

		String cpDefinitionInventoryEngine = ParamUtil.getString(
			actionRequest, "CPDefinitionInventoryEngine");
		String lowStockActivity = ParamUtil.getString(
			actionRequest, "lowStockActivity");
		long commerceAvailabilityEstimateId = ParamUtil.getLong(
			actionRequest, "commerceAvailabilityEstimateId");
		boolean displayAvailability = ParamUtil.getBoolean(
			actionRequest, "displayAvailability");
		boolean displayStockQuantity = ParamUtil.getBoolean(
			actionRequest, "displayStockQuantity");
		boolean backOrders = ParamUtil.getBoolean(actionRequest, "backOrders");
		BigDecimal minStockQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPDefinitionInventory.class.getName(),
			"minStockQuantity");
		BigDecimal minOrderQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPDefinitionInventory.class.getName(),
			"minOrderQuantity");
		BigDecimal maxOrderQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPDefinitionInventory.class.getName(),
			"maxOrderQuantity");
		BigDecimal multipleOrderQuantity =
			_commerceOrderItemQuantityFormatter.parse(
				actionRequest, CPDefinitionInventory.class.getName(),
				"multipleOrderQuantity");

		String allowedOrderQuantities = ParamUtil.getString(
			actionRequest, "allowedOrderQuantities");

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryService.
				fetchCPDefinitionInventoryByCPDefinitionId(cpDefinitionId);

		if (cpDefinitionInventory == null) {
			_cpDefinitionInventoryService.addCPDefinitionInventory(
				cpDefinitionId, cpDefinitionInventoryEngine, lowStockActivity,
				displayAvailability, displayStockQuantity, minStockQuantity,
				backOrders, minOrderQuantity, maxOrderQuantity,
				allowedOrderQuantities, multipleOrderQuantity);
		}
		else {
			_cpDefinitionInventoryService.updateCPDefinitionInventory(
				cpDefinitionInventory.getCPDefinitionInventoryId(),
				cpDefinitionInventoryEngine, lowStockActivity,
				displayAvailability, displayStockQuantity, minStockQuantity,
				backOrders, minOrderQuantity, maxOrderQuantity,
				allowedOrderQuantities, multipleOrderQuantity);
		}

		_cpdAvailabilityEstimateService.updateCPDAvailabilityEstimate(
			cpdAvailabilityEstimateEntryId, cpDefinitionId,
			commerceAvailabilityEstimateId);
	}

	private void _updateMasterConfiguration(
			ActionRequest actionRequest, long cpDefinitionId)
		throws Exception {

		long cpTaxCategoryId = ParamUtil.getLong(
			actionRequest, "cpTaxCategoryId");
		String allowedOrderQuantities = ParamUtil.getString(
			actionRequest, "allowedOrderQuantities");
		boolean backOrders = ParamUtil.getBoolean(actionRequest, "backOrders");
		long commerceAvailabilityEstimateId = ParamUtil.getLong(
			actionRequest, "commerceAvailabilityEstimateId");
		String cpDefinitionInventoryEngine = ParamUtil.getString(
			actionRequest, "CPDefinitionInventoryEngine");
		double depth = ParamUtil.getDouble(actionRequest, "depth");
		boolean displayAvailability = ParamUtil.getBoolean(
			actionRequest, "displayAvailability");
		boolean displayStockQuantity = ParamUtil.getBoolean(
			actionRequest, "displayStockQuantity");
		boolean freeShipping = ParamUtil.getBoolean(
			actionRequest, "freeShipping");
		double height = ParamUtil.getDouble(actionRequest, "height");
		String lowStockActivity = ParamUtil.getString(
			actionRequest, "lowStockActivity");
		BigDecimal maxOrderQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPConfigurationEntry.class.getName(),
			"maxOrderQuantity");
		BigDecimal minOrderQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPConfigurationEntry.class.getName(),
			"minOrderQuantity");
		BigDecimal minStockQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CPConfigurationEntry.class.getName(),
			"minStockQuantity");
		BigDecimal multipleOrderQuantity =
			_commerceOrderItemQuantityFormatter.parse(
				actionRequest, CPConfigurationEntry.class.getName(),
				"multipleOrderQuantity");
		boolean purchasable = ParamUtil.getBoolean(
			actionRequest, "purchasable", true);
		boolean shippable = ParamUtil.getBoolean(actionRequest, "shippable");
		double shippingExtraPrice = ParamUtil.getDouble(
			actionRequest, "shippingExtraPrice");
		boolean shipSeparately = ParamUtil.getBoolean(
			actionRequest, "shipSeparately");
		boolean taxExempt = ParamUtil.getBoolean(actionRequest, "taxExempt");
		double weight = ParamUtil.getDouble(actionRequest, "weight");
		double width = ParamUtil.getDouble(actionRequest, "width");

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpDefinitionId);

		CPConfigurationEntry cpConfigurationEntry =
			cpDefinition.fetchMasterCPConfigurationEntry();

		if (cpConfigurationEntry == null) {
			CPConfigurationList masterCPConfigurationList =
				cpDefinition.getMasterCPConfigurationList();

			_cpConfigurationEntryService.addCPConfigurationEntry(
				null, cpDefinition.getGroupId(),
				_portal.getClassNameId(CPDefinition.class), cpDefinitionId,
				masterCPConfigurationList.getCPConfigurationListId(),
				cpTaxCategoryId, allowedOrderQuantities, backOrders,
				commerceAvailabilityEstimateId, cpDefinitionInventoryEngine,
				depth, displayAvailability, displayStockQuantity, freeShipping,
				height, lowStockActivity, maxOrderQuantity, minOrderQuantity,
				minStockQuantity, multipleOrderQuantity, purchasable, shippable,
				shippingExtraPrice, shipSeparately, taxExempt, weight, width);
		}
		else {
			_cpConfigurationEntryService.updateCPConfigurationEntry(
				cpConfigurationEntry.getExternalReferenceCode(),
				cpConfigurationEntry.getCPConfigurationEntryId(),
				cpTaxCategoryId, allowedOrderQuantities, backOrders,
				commerceAvailabilityEstimateId, cpDefinitionInventoryEngine,
				depth, displayAvailability, displayStockQuantity, freeShipping,
				height, lowStockActivity, maxOrderQuantity, minOrderQuantity,
				minStockQuantity, multipleOrderQuantity, purchasable, shippable,
				shippingExtraPrice, shipSeparately, taxExempt, weight, width);
		}

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionId, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			cpInstance.setPurchasable(purchasable);

			_cpInstanceLocalService.updateCPInstance(cpInstance);
		}
	}

	private void _updateShippingInfo(
			ActionRequest actionRequest, long cpDefinitionId)
		throws Exception {

		boolean shippable = ParamUtil.getBoolean(actionRequest, "shippable");
		boolean freeShipping = ParamUtil.getBoolean(
			actionRequest, "freeShipping");
		boolean shipSeparately = ParamUtil.getBoolean(
			actionRequest, "shipSeparately");
		double shippingExtraPrice = ParamUtil.getDouble(
			actionRequest, "shippingExtraPrice");
		double width = ParamUtil.getDouble(actionRequest, "width");
		double height = ParamUtil.getDouble(actionRequest, "height");
		double depth = ParamUtil.getDouble(actionRequest, "depth");
		double weight = ParamUtil.getDouble(actionRequest, "weight");

		_cpDefinitionService.updateShippingInfo(
			cpDefinitionId, shippable, freeShipping, shipSeparately,
			shippingExtraPrice, width, height, depth, weight,
			ServiceContextFactory.getInstance(
				CPDefinition.class.getName(), actionRequest));
	}

	private void _updateSubscriptionInfo(
			ActionRequest actionRequest, CPDefinition cpDefinition)
		throws Exception {

		if (cpDefinition == null) {
			return;
		}

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

		_cpDefinitionService.updateSubscriptionInfo(
			cpDefinition.getCPDefinitionId(), subscriptionEnabled,
			subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	private void _updateTaxCategoryInfo(
			ActionRequest actionRequest, long cpDefinitionId)
		throws Exception {

		long cpTaxCategoryId = ParamUtil.getLong(
			actionRequest, "cpTaxCategoryId");
		boolean taxExempt = ParamUtil.getBoolean(actionRequest, "taxExempt");
		boolean telcoOrElectronics = ParamUtil.getBoolean(
			actionRequest, "telcoOrElectronics");

		_cpDefinitionService.updateTaxCategoryInfo(
			cpDefinitionId, cpTaxCategoryId, taxExempt, telcoOrElectronics);
	}

	private void _updateVisibility(
			ActionRequest actionRequest, long cpDefinitionId)
		throws Exception {

		// Commerce account group rels

		long[] accountGroupIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "commerceAccountGroupIds"), 0L);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AccountGroupRel.class.getName(), actionRequest);

		for (long accountGroupId : accountGroupIds) {
			if (accountGroupId == 0) {
				continue;
			}

			_accountGroupRelLocalService.addAccountGroupRel(
				accountGroupId, CPDefinition.class.getName(), cpDefinitionId);
		}

		// Commerce channel rels

		long[] commerceChannelIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "commerceChannelIds"), 0L);

		for (long commerceChannelId : commerceChannelIds) {
			if (commerceChannelId == 0) {
				continue;
			}

			_commerceChannelRelService.addCommerceChannelRel(
				CPDefinition.class.getName(), cpDefinitionId, commerceChannelId,
				serviceContext);
		}

		// Filters

		boolean accountGroupFilterEnabled = ParamUtil.getBoolean(
			actionRequest, "accountGroupFilterEnabled");
		boolean channelFilterEnabled = ParamUtil.getBoolean(
			actionRequest, "channelFilterEnabled");

		_cpDefinitionService.updateCPDefinitionAccountGroupFilter(
			cpDefinitionId, accountGroupFilterEnabled);
		_cpDefinitionService.updateCPDefinitionChannelFilter(
			cpDefinitionId, channelFilterEnabled);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPDefinitionMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPConfigurationEntryService _cpConfigurationEntryService;

	@Reference
	private CPDAvailabilityEstimateService _cpdAvailabilityEstimateService;

	@Reference
	private CPDefinitionInventoryService _cpDefinitionInventoryService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	private class CPDefinitionCallable implements Callable<CPDefinition> {

		@Override
		public CPDefinition call() throws Exception {
			return _updateCPDefinition(_actionRequest, _cpDefinition);
		}

		private CPDefinitionCallable(
			ActionRequest actionRequest, CPDefinition cpDefinition) {

			_actionRequest = actionRequest;
			_cpDefinition = cpDefinition;
		}

		private final ActionRequest _actionRequest;
		private final CPDefinition _cpDefinition;

	}

	private class CPDefinitionConfigurationCallable
		implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			if (_cpDefinition == null) {
				return null;
			}

			long cpDefinitionId = _cpDefinition.getCPDefinitionId();

			_updateCPDefinitionInventory(_actionRequest, cpDefinitionId);
			_updateMasterConfiguration(_actionRequest, cpDefinitionId);
			_updateShippingInfo(_actionRequest, cpDefinitionId);
			_updateTaxCategoryInfo(_actionRequest, cpDefinitionId);

			_updateCPDefinition(
				_cpDefinition,
				_getServiceContext(_actionRequest, _cpDefinition));

			return null;
		}

		private CPDefinitionConfigurationCallable(
			ActionRequest actionRequest, CPDefinition cpDefinition) {

			_actionRequest = actionRequest;
			_cpDefinition = cpDefinition;
		}

		private final ActionRequest _actionRequest;
		private final CPDefinition _cpDefinition;

	}

	private class CPDefinitionSubscriptionInfoCallable
		implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			if (_cpDefinition == null) {
				return null;
			}

			_updateSubscriptionInfo(_actionRequest, _cpDefinition);

			_updateCPDefinition(
				_cpDefinition,
				_getServiceContext(_actionRequest, _cpDefinition));

			return null;
		}

		private CPDefinitionSubscriptionInfoCallable(
			ActionRequest actionRequest, CPDefinition cpDefinition) {

			_actionRequest = actionRequest;
			_cpDefinition = cpDefinition;
		}

		private final ActionRequest _actionRequest;
		private final CPDefinition _cpDefinition;

	}

	private class CPDefinitionUpdateCallable implements Callable<CPDefinition> {

		@Override
		public CPDefinition call() throws Exception {
			return _updateCPDefinition(
				_cpDefinition,
				_getServiceContext(_actionRequest, _cpDefinition));
		}

		private CPDefinitionUpdateCallable(
			ActionRequest actionRequest, CPDefinition cpDefinition) {

			_actionRequest = actionRequest;
			_cpDefinition = cpDefinition;
		}

		private final ActionRequest _actionRequest;
		private final CPDefinition _cpDefinition;

	}

	private class CPDefinitionVisibilityCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			if (_cpDefinition == null) {
				return null;
			}

			_updateVisibility(
				_actionRequest, _cpDefinition.getCPDefinitionId());

			_updateCPDefinition(
				_cpDefinition,
				_getServiceContext(_actionRequest, _cpDefinition));

			return null;
		}

		private CPDefinitionVisibilityCallable(
			ActionRequest actionRequest, CPDefinition cpDefinition) {

			_actionRequest = actionRequest;
			_cpDefinition = cpDefinition;
		}

		private final ActionRequest _actionRequest;
		private final CPDefinition _cpDefinition;

	}

}