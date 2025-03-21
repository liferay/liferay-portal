/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.order.rule.constants.COREntryActionKeys;
import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.commerce.order.rule.constants.COREntryPortletKeys;
import com.liferay.commerce.order.rule.entry.type.COREntryType;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeJSPContributor;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeJSPContributorRegistry;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeRegistry;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.commerce.order.rule.web.internal.display.context.helper.COREntryRequestHelper;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class COREntryDisplayContext {

	public COREntryDisplayContext(
		CommerceCurrencyService commerceCurrencyService,
		ModelResourcePermission<COREntry> corEntryModelResourcePermission,
		COREntryService corEntryService,
		COREntryTypeJSPContributorRegistry corEntryTypeJSPContributorRegistry,
		COREntryTypeRegistry corEntryTypeRegistry,
		HttpServletRequest httpServletRequest, Portal portal) {

		_commerceCurrencyService = commerceCurrencyService;
		_corEntryModelResourcePermission = corEntryModelResourcePermission;
		_corEntryService = corEntryService;
		_corEntryTypeJSPContributorRegistry =
			corEntryTypeJSPContributorRegistry;
		_corEntryTypeRegistry = corEntryTypeRegistry;
		this.httpServletRequest = httpServletRequest;
		this.portal = portal;

		corEntryRequestHelper = new COREntryRequestHelper(httpServletRequest);
	}

	public String getAddCOREntryRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			corEntryRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/cor_entry/add_cor_entry"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getApplyTo() throws Exception {
		COREntry corEntry = getCOREntry();

		if (corEntry == null) {
			return StringPool.BLANK;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		return GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty(
				COREntryConstants.TYPE_MINIMUM_ORDER_AMOUNT_FIELD_APPLY_TO));
	}

	public List<CommerceCurrency> getCommerceCurrencies()
		throws PortalException {

		return _commerceCurrencyService.getCommerceCurrencies(
			corEntryRequestHelper.getCompanyId(), true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public String getCommerceCurrencyCode() throws PortalException {
		CommerceCurrency commerceCurrency = _getCommerceCurrency();

		if (commerceCurrency == null) {
			return StringPool.BLANK;
		}

		return commerceCurrency.getCode();
	}

	public COREntry getCOREntry() throws PortalException {
		long corEntryId = ParamUtil.getLong(
			corEntryRequestHelper.getRequest(), "corEntryId");

		if (corEntryId == 0) {
			return null;
		}

		return _corEntryService.fetchCOREntry(corEntryId);
	}

	public List<FDSActionDropdownItem> getCOREntryFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, COREntry.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/cor_entry/edit_cor_entry"
				).setRedirect(
					corEntryRequestHelper.getCurrentURL()
				).setParameter(
					"corEntryId", "{id}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"),
			new FDSActionDropdownItem(
				_getManagePermissionsURL(), null, "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"));
	}

	public long getCOREntryId() throws PortalException {
		COREntry corEntry = getCOREntry();

		if (corEntry == null) {
			return 0;
		}

		return corEntry.getCOREntryId();
	}

	public COREntryTypeJSPContributor getCOREntryTypeJSPContributor(
		String key) {

		return _corEntryTypeJSPContributorRegistry.
			getCOREntryTypeJSPContributor(key);
	}

	public List<COREntryType> getCOREntryTypes() {
		return _corEntryTypeRegistry.getCOREntryTypes();
	}

	public String getCProductIds() throws Exception {
		COREntry corEntry = getCOREntry();

		if (corEntry == null) {
			return StringPool.BLANK;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		return typeSettingsUnicodeProperties.getProperty(
			COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS);
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCOREntryRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							corEntryRequestHelper.getRequest(),
							"add-order-rule"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public PortletURL getEditCOREntryRenderURL() {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				corEntryRequestHelper.getRequest(),
				COREntryPortletKeys.COR_ENTRY, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/cor_entry/edit_cor_entry"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		LiferayPortletResponse liferayPortletResponse =
			corEntryRequestHelper.getLiferayPortletResponse();

		String saveButtonLabel = "save";

		COREntry corEntry = getCOREntry();

		if ((corEntry == null) || corEntry.isDraft() || corEntry.isApproved() ||
			corEntry.isExpired() || corEntry.isScheduled()) {

			saveButtonLabel = "save-as-draft";
		}

		HeaderActionModel saveAsDraftHeaderActionModel = new HeaderActionModel(
			null, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/cor_entry/edit_cor_entry"
			).buildString(),
			null, saveButtonLabel);

		headerActionModels.add(saveAsDraftHeaderActionModel);

		String publishButtonLabel = "publish";

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				corEntryRequestHelper.getCompanyId(),
				corEntryRequestHelper.getScopeGroupId(),
				COREntry.class.getName())) {

			publishButtonLabel = "submit-for-workflow";
		}

		String additionalClasses = "btn-primary";

		if ((corEntry != null) && corEntry.isPending()) {
			additionalClasses = additionalClasses + " disabled";
		}

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			additionalClasses, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/cor_entry/edit_cor_entry"
			).buildString(),
			liferayPortletResponse.getNamespace() + "publishButton",
			publishButtonLabel);

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public String getMinimumAmount() throws Exception {
		COREntry corEntry = getCOREntry();

		if (corEntry == null) {
			return StringPool.BLANK;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		BigDecimal minimumAmount = BigDecimal.valueOf(
			GetterUtil.getDouble(
				typeSettingsUnicodeProperties.getProperty(
					COREntryConstants.TYPE_MINIMUM_ORDER_AMOUNT_FIELD_AMOUNT)));

		CommerceCurrency commerceCurrency = _getCommerceCurrency();

		if (commerceCurrency == null) {
			return minimumAmount.toPlainString();
		}

		minimumAmount = commerceCurrency.round(minimumAmount);

		return minimumAmount.toPlainString();
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			corEntryRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long corEntryId = ParamUtil.getLong(httpServletRequest, "corEntryId");

		if (corEntryId > 0) {
			portletURL.setParameter("corEntryId", String.valueOf(corEntryId));
		}

		return portletURL;
	}

	public String getQuantity() throws Exception {
		COREntry corEntry = getCOREntry();

		if (corEntry == null) {
			return StringPool.BLANK;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		return typeSettingsUnicodeProperties.getProperty(
			COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY);
	}

	public boolean hasAddPermission() throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_corEntryModelResourcePermission.getPortletResourcePermission();

		return portletResourcePermission.contains(
			corEntryRequestHelper.getPermissionChecker(), null,
			COREntryActionKeys.ADD_COR_ENTRY);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _corEntryModelResourcePermission.contains(
			corEntryRequestHelper.getPermissionChecker(), getCOREntryId(),
			actionId);
	}

	protected final COREntryRequestHelper corEntryRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final Portal portal;

	private CommerceCurrency _getCommerceCurrency() throws PortalException {
		COREntry corEntry = getCOREntry();

		if (corEntry == null) {
			return null;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		String commerceCurrencyCode = GetterUtil.getString(
			typeSettingsUnicodeProperties.getProperty(
				COREntryConstants.
					TYPE_MINIMUM_ORDER_AMOUNT_FIELD_CURRENCY_CODE));

		if (Validator.isNull(commerceCurrencyCode)) {
			return _commerceCurrencyService.fetchPrimaryCommerceCurrency(
				corEntryRequestHelper.getCompanyId());
		}

		return _commerceCurrencyService.getCommerceCurrency(
			corEntryRequestHelper.getCompanyId(), commerceCurrencyCode);
	}

	private String _getManagePermissionsURL() throws PortalException {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			corEntryRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", COREntry.class.getName()
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final CommerceCurrencyService _commerceCurrencyService;
	private final ModelResourcePermission<COREntry>
		_corEntryModelResourcePermission;
	private final COREntryService _corEntryService;
	private final COREntryTypeJSPContributorRegistry
		_corEntryTypeJSPContributorRegistry;
	private final COREntryTypeRegistry _corEntryTypeRegistry;

}