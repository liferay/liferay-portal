/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.constants.CommerceDiscountActionKeys;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleType;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleTypeRegistry;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.discount.target.CommerceDiscountTarget;
import com.liferay.commerce.discount.target.CommerceDiscountTargetRegistry;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.web.internal.constants.CommerceDiscountScreenNavigationConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceDiscountDisplayContext extends BasePricingDisplayContext {

	public CommerceDiscountDisplayContext(
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		ModelResourcePermission<CommerceDiscount>
			commerceDiscountModelResourcePermission,
		CommerceDiscountService commerceDiscountService,
		CommerceDiscountRuleService commerceDiscountRuleService,
		CommerceDiscountRuleTypeRegistry commerceDiscountRuleTypeRegistry,
		CommerceDiscountTargetRegistry commerceDiscountTargetRegistry,
		CommercePriceFormatter commercePriceFormatter,
		PercentageFormatter percentageFormatter,
		HttpServletRequest httpServletRequest, Portal portal) {

		super(httpServletRequest);

		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_commerceDiscountModelResourcePermission =
			commerceDiscountModelResourcePermission;
		_commerceDiscountService = commerceDiscountService;
		_commerceDiscountRuleService = commerceDiscountRuleService;
		_commerceDiscountRuleTypeRegistry = commerceDiscountRuleTypeRegistry;
		_commerceDiscountTargetRegistry = commerceDiscountTargetRegistry;
		_commercePriceFormatter = commercePriceFormatter;
		_percentageFormatter = percentageFormatter;
		this.portal = portal;
	}

	public String getAddCommerceDiscountRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			commercePricingRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_discount/add_commerce_discount"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getAddCommerceDiscountRuleRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			commercePricingRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_discount/add_commerce_discount_rule"
		).setParameter(
			"commerceDiscountId", getCommerceDiscountId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceDiscount getCommerceDiscount() throws PortalException {
		if (_commerceDiscount != null) {
			return _commerceDiscount;
		}

		long commerceDiscountId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(), "commerceDiscountId");

		if (commerceDiscountId > 0) {
			_commerceDiscount = _commerceDiscountService.getCommerceDiscount(
				commerceDiscountId);
		}

		return _commerceDiscount;
	}

	public String getCommerceDiscountAmount(Locale locale)
		throws PortalException {

		CommerceDiscount commerceDiscount = getCommerceDiscount();

		if (commerceDiscount == null) {
			return null;
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L1)) {

			return _getCommerceDiscountAmount(commerceDiscount.getLevel1());
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L2)) {

			return _getCommerceDiscountAmount(commerceDiscount.getLevel2());
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L3)) {

			return _getCommerceDiscountAmount(commerceDiscount.getLevel3());
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L4)) {

			return _getCommerceDiscountAmount(commerceDiscount.getLevel4());
		}

		return _getCommerceDiscountAmount(BigDecimal.ZERO);
	}

	public long getCommerceDiscountId() throws PortalException {
		CommerceDiscount commerceDiscount = getCommerceDiscount();

		if (commerceDiscount == null) {
			return 0;
		}

		return commerceDiscount.getCommerceDiscountId();
	}

	public CommerceDiscountRule getCommerceDiscountRule()
		throws PortalException {

		if (_commerceDiscountRule != null) {
			return _commerceDiscountRule;
		}

		long commerceDiscountRuleId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(),
			"commerceDiscountRuleId");

		if (commerceDiscountRuleId > 0) {
			_commerceDiscountRule =
				_commerceDiscountRuleService.getCommerceDiscountRule(
					commerceDiscountRuleId);
		}

		return _commerceDiscountRule;
	}

	public List<CommerceDiscountRuleType> getCommerceDiscountRuleTypes() {
		return _commerceDiscountRuleTypeRegistry.getCommerceDiscountRuleTypes();
	}

	public List<CommerceDiscountTarget> getCommerceDiscountTargets() {
		return _commerceDiscountTargetRegistry.getCommerceDiscountTargets();
	}

	public String getDefaultCommerceCurrencyCode() {
		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				commercePricingRequestHelper.getCompanyId());

		if (commerceCurrency == null) {
			return StringPool.BLANK;
		}

		return commerceCurrency.getCode();
	}

	public String getDiscountCategoriesApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() +
				"/discount-categories?nestedFields=category";
	}

	public List<FDSActionDropdownItem>
			getDiscountCategoryFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));
	}

	public String getDiscountCPDefinitionApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() + "/discount-products?nestedFields=product";
	}

	public List<FDSActionDropdownItem>
			getDiscountCPDefinitionFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CPDefinition.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/cp_definitions/edit_cp_definition"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"cpDefinitionId", "{product.id}"
			).setParameter(
				"screenNavigationCategoryKey", "details"
			).buildString(),
			false);
	}

	public String getDiscountCPInstanceAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() + "/discount-skus?nestedFields=sku";
	}

	public List<FDSActionDropdownItem>
			getDiscountCPInstanceFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CPDefinition.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/cp_definitions/edit_cp_definition"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"cpDefinitionId", "{productId}"
			).setParameter(
				"cpInstanceId", "{sku.id}"
			).setParameter(
				"screenNavigationCategoryKey", "skus"
			).buildString(),
			false);
	}

	public CreationMenu getDiscountCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommerceDiscountRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commercePricingRequestHelper.getRequest(),
							"add-discount"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public List<FDSActionDropdownItem> getDiscountFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CommerceDiscount.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_discount/edit_commerce_discount"
				).setRedirect(
					commercePricingRequestHelper.getCurrentURL()
				).setParameter(
					"commerceDiscountId", "{id}"
				).setParameter(
					"screenNavigationCategoryKey",
					CommerceDiscountScreenNavigationConstants.
						CATEGORY_KEY_DETAILS
				).setParameter(
					"usePercentage", "{usePercentage}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"),
			new FDSActionDropdownItem(
				_getManageDiscountPermissionsURL(), null, "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"));
	}

	public String getDiscountPricingClassesApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() +
				"/discount-product-groups?nestedFields=productGroup";
	}

	public List<FDSActionDropdownItem>
			getDiscountPricingClassFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommercePricingClass.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_pricing_classes/edit_commerce_pricing_class"
			).setRedirect(
				commercePricingRequestHelper.getCurrentURL()
			).setParameter(
				"commercePricingClassId", "{productGroupId}"
			).setParameter(
				"screenNavigationCategoryKey", "details"
			).buildString(),
			false);
	}

	public CreationMenu getDiscountRuleCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasPermission(ActionKeys.UPDATE)) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommerceDiscountRuleRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commercePricingRequestHelper.getRequest(),
							"add-discount-rule"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public String getDiscountRulesApiURL() throws PortalException {
		return "/o/headless-commerce-admin-pricing/v2.0/discounts/" +
			getCommerceDiscountId() + "/discount-rules";
	}

	public List<FDSActionDropdownItem> getDiscountRulesFDSActionDropdownItem()
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceDiscount.class.getName(),
				PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/commerce_discount/edit_commerce_discount_rule"
		).setRedirect(
			commercePricingRequestHelper.getCurrentURL()
		).setParameter(
			"commerceDiscountRuleId", "{id}"
		).setParameter(
			"screenNavigationCategoryKey",
			CommerceDiscountScreenNavigationConstants.CATEGORY_KEY_DETAILS
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return getFDSActionTemplates(portletURL.toString(), true);
	}

	public String getEditCommerceDiscountActionURL() throws Exception {
		CommerceDiscount commerceDiscount = getCommerceDiscount();

		if (commerceDiscount == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				commercePricingRequestHelper.getRequest(),
				CommercePricingPortletKeys.COMMERCE_DISCOUNT,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_discount/edit_commerce_discount"
		).setCMD(
			Constants.UPDATE
		).setParameter(
			"commerceDiscountId", commerceDiscount.getCommerceDiscountId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public PortletURL getEditCommerceDiscountRenderURL() {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				commercePricingRequestHelper.getRequest(),
				CommercePricingPortletKeys.COMMERCE_DISCOUNT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_discount/edit_commerce_discount"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		HeaderActionModel cancelHeaderActionModel = new HeaderActionModel(
			null,
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).buildString(),
			null, "cancel");

		headerActionModels.add(cancelHeaderActionModel);

		String saveButtonLabel = "save";

		CommerceDiscount commerceDiscount = getCommerceDiscount();

		if ((commerceDiscount == null) || commerceDiscount.isDraft() ||
			commerceDiscount.isApproved() || commerceDiscount.isExpired() ||
			commerceDiscount.isScheduled()) {

			saveButtonLabel = "save-as-draft";
		}

		HeaderActionModel saveAsDraftHeaderActionModel = new HeaderActionModel(
			null, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_discount/edit_commerce_discount"
			).buildString(),
			null, saveButtonLabel);

		headerActionModels.add(saveAsDraftHeaderActionModel);

		String publishButtonLabel = "publish";

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				commercePricingRequestHelper.getCompanyId(),
				commercePricingRequestHelper.getScopeGroupId(),
				CommerceDiscount.class.getName())) {

			publishButtonLabel = "submit-for-workflow";
		}

		String additionalClasses = "btn-primary";

		if ((commerceDiscount != null) && commerceDiscount.isPending()) {
			additionalClasses = additionalClasses + " disabled";
		}

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			additionalClasses, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_discount/edit_commerce_discount"
			).buildString(),
			liferayPortletResponse.getNamespace() + "publishButton",
			publishButtonLabel);

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public String getLocalizedPercentage(BigDecimal percentage, Locale locale)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				commercePricingRequestHelper.getCompanyId());

		return StringUtil.removeSubstring(
			_percentageFormatter.getLocalizedPercentage(
				locale, commerceCurrency.getMaxFractionDigits(),
				commerceCurrency.getMinFractionDigits(), percentage),
			StringPool.PERCENT);
	}

	public String getMaximumDiscountAmount() throws PortalException {
		CommerceDiscount commerceDiscount = getCommerceDiscount();

		return _getCommerceDiscountAmount(
			commerceDiscount.getMaximumDiscountAmount());
	}

	public PortletURL getPortletDiscountRuleURL() {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long commerceDiscountRuleId = ParamUtil.getLong(
			httpServletRequest, "commerceDiscountRuleId");

		if (commerceDiscountRuleId > 0) {
			portletURL.setParameter(
				"commerceDiscountRuleId",
				String.valueOf(commerceDiscountRuleId));
		}

		return portletURL;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long commerceDiscountId = ParamUtil.getLong(
			httpServletRequest, "commerceDiscountId");

		if (commerceDiscountId > 0) {
			portletURL.setParameter(
				"commerceDiscountId", String.valueOf(commerceDiscountId));
		}

		return portletURL;
	}

	public boolean getUsePercentage(String commerceDiscountType) {
		return Objects.equals(
			commerceDiscountType, CommerceDiscountConstants.TYPE_PERCENTAGE);
	}

	public boolean hasAddPermission() throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commerceDiscountModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(), null,
			CommerceDiscountActionKeys.ADD_COMMERCE_DISCOUNT);
	}

	public boolean hasCustomAttributesAvailable() throws Exception {
		return CustomAttributesUtil.hasCustomAttributes(
			commercePricingRequestHelper.getCompanyId(),
			CommerceDiscount.class.getName(), getCommerceDiscountId(), null);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _commerceDiscountModelResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(),
			getCommerceDiscountId(), actionId);
	}

	public BigDecimal round(BigDecimal value) {
		if (value == null) {
			value = BigDecimal.ZERO;
		}

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				commercePricingRequestHelper.getCompanyId());

		if (commerceCurrency == null) {
			return value;
		}

		return commerceCurrency.round(value);
	}

	protected List<FDSActionDropdownItem> getFDSActionTemplates(
		String portletURL, boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			portletURL, "pencil", "edit",
			LanguageUtil.get(httpServletRequest, "edit"), "get", null, null);

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "trash", "remove",
				LanguageUtil.get(httpServletRequest, "remove"), "delete",
				"delete", "headless"));

		return fdsActionDropdownItems;
	}

	protected final Portal portal;

	private String _getCommerceDiscountAmount(BigDecimal commerceDiscountAmount)
		throws PortalException {

		if (commerceDiscountAmount == null) {
			commerceDiscountAmount = BigDecimal.ZERO;
		}

		return _commercePriceFormatter.format(
			round(commerceDiscountAmount),
			commercePricingRequestHelper.getLocale());
	}

	private String _getManageDiscountPermissionsURL() throws PortalException {
		PortletURL portletURL = PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			commercePricingRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", CommerceDiscount.class.getName()
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return portletURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountDisplayContext.class);

	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private CommerceDiscount _commerceDiscount;
	private final ModelResourcePermission<CommerceDiscount>
		_commerceDiscountModelResourcePermission;
	private CommerceDiscountRule _commerceDiscountRule;
	private final CommerceDiscountRuleService _commerceDiscountRuleService;
	private final CommerceDiscountRuleTypeRegistry
		_commerceDiscountRuleTypeRegistry;
	private final CommerceDiscountService _commerceDiscountService;
	private final CommerceDiscountTargetRegistry
		_commerceDiscountTargetRegistry;
	private final CommercePriceFormatter _commercePriceFormatter;
	private final PercentageFormatter _percentageFormatter;

}