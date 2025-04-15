/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.commerce.account.item.selector.CommerceAccountGroupItemSelectorCriterion;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.item.selector.criterion.CommerceChannelItemSelectorCriterion;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 * @author Marco Leo
 */
public class CPDefinitionsDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionsDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		AccountGroupRelLocalService accountGroupRelLocalService,
		CommerceCatalogService commerceCatalogService,
		CommerceChannelRelService commerceChannelRelService,
		ConfigurationProvider configurationProvider,
		CPDefinitionService cpDefinitionService, CPFriendlyURL cpFriendlyURL,
		ItemSelector itemSelector,
		PortletResourcePermission portletResourcePermission) {

		super(actionHelper, httpServletRequest);

		_accountGroupRelLocalService = accountGroupRelLocalService;
		_commerceCatalogService = commerceCatalogService;
		_commerceChannelRelService = commerceChannelRelService;
		_configurationProvider = configurationProvider;
		_cpDefinitionService = cpDefinitionService;
		_cpFriendlyURL = cpFriendlyURL;
		_itemSelector = itemSelector;
		_portletResourcePermission = portletResourcePermission;
	}

	public String getAccountGroupItemSelectorUrl() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		CommerceAccountGroupItemSelectorCriterion
			commerceAccountGroupItemSelectorCriterion =
				new CommerceAccountGroupItemSelectorCriterion();

		commerceAccountGroupItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				Collections.<ItemSelectorReturnType>singletonList(
					new UUIDItemSelectorReturnType()));

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "accountGroupSelectItem",
				commerceAccountGroupItemSelectorCriterion)
		).setParameter(
			"checkedCommerceAccountGroupIds",
			getCheckedCommerceAccountGroupIds()
		).setParameter(
			"permissionUserId",
			() -> {
				PermissionChecker permissionChecker =
					cpRequestHelper.getPermissionChecker();

				return permissionChecker.getUserId();
			}
		).buildString();
	}

	public CreationMenu getAccountGroupsCreationMenu() throws PortalException {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				CPDefinition cpDefinition = getCPDefinition();

				String cpDefinitionName = StringPool.BLANK;

				if (cpDefinition != null) {
					cpDefinitionName = cpDefinition.getName(
						LocaleUtil.toLanguageId(cpRequestHelper.getLocale()));
				}

				dropdownItem.setHref(
					liferayPortletResponse.getNamespace() +
						"selectCommerceAccountGroup");
				dropdownItem.setLabel(
					LanguageUtil.format(
						httpServletRequest, "add-account-group-relation-to-x",
						cpDefinitionName));
				dropdownItem.setTarget("event");
			}
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createActionURL(
					cpRequestHelper.getRenderResponse()
				).setActionName(
					"/cp_definitions/edit_cp_definition"
				).setCMD(
					Constants.DELETE
				).buildString(),
				"trash", "delete", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				null));
	}

	public String getChannelItemSelectorUrl() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		CommerceChannelItemSelectorCriterion
			commerceChannelItemSelectorCriterion =
				new CommerceChannelItemSelectorCriterion();

		commerceChannelItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new UUIDItemSelectorReturnType()));

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "channelSelectItem",
				commerceChannelItemSelectorCriterion)
		).setParameter(
			"checkedCommerceChannelIds", getCheckedCommerceChannelIds()
		).buildString();
	}

	public CreationMenu getChannelsCreationMenu() throws PortalException {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				String cpDefinitionName = StringPool.BLANK;

				CPDefinition cpDefinition = getCPDefinition();

				if (cpDefinition != null) {
					cpDefinitionName = cpDefinition.getName(
						LocaleUtil.toLanguageId(cpRequestHelper.getLocale()));
				}

				dropdownItem.setHref(
					liferayPortletResponse.getNamespace() +
						"selectCommerceChannel");
				dropdownItem.setLabel(
					LanguageUtil.format(
						httpServletRequest, "add-channel-relation-to-x",
						cpDefinitionName));
				dropdownItem.setTarget("event");
			}
		).build();
	}

	public String getCheckedCommerceAccountGroupIds() throws PortalException {
		return StringUtil.merge(
			TransformUtil.transformToLongArray(
				_accountGroupRelLocalService.getAccountGroupRels(
					CPDefinition.class.getName(), getCPDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				AccountGroupRel::getAccountGroupId));
	}

	public String getCheckedCommerceChannelIds() throws PortalException {
		return StringUtil.merge(
			TransformUtil.transformToLongArray(
				_commerceChannelRelService.getCommerceChannelRels(
					CPDefinition.class.getName(), getCPDefinitionId(), null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				CommerceChannelRel::getCommerceChannelId));
	}

	public List<CommerceCatalog> getCommerceCatalogs() throws PortalException {
		return _commerceCatalogService.search(
			cpRequestHelper.getCompanyId(), null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public CreationMenu getCPDefinitionSpecificationOptionValueCreationMenu()
		throws Exception {

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/cp_definitions" +
							"/add_cp_definition_specification_option_value"
					).setBackURL(
						cpRequestHelper.getCurrentURL()
					).setParameter(
						"cpDefinitionId", getCPDefinitionId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildPortletURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "add-an-existing-specification"));
				dropdownItem.setTarget("modal");
			}
		).addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/cp_definitions" +
							"/add_cp_definition_specification_option_value"
					).setBackURL(
						cpRequestHelper.getCurrentURL()
					).setParameter(
						"cpDefinitionId", getCPDefinitionId()
					).setParameter(
						"createNewSpecification", true
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildPortletURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "create-new-specification"));
				dropdownItem.setTarget("modal");
			}
		).build();
	}

	public String getCPDefinitionThumbnailURL() throws Exception {
		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition == null) {
			return StringPool.BLANK;
		}

		return cpDefinition.getDefaultImageThumbnailSrc(
			AccountConstants.ACCOUNT_ENTRY_ID_ADMIN);
	}

	public CProduct getCProduct() throws PortalException {
		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition == null) {
			return null;
		}

		return cpDefinition.getCProduct();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/cp_definitions/add_cp_definition"
		).setBackURL(
			cpRequestHelper.getCurrentURL()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();

		for (CPType cpType : getCPTypes()) {
			portletURL.setParameter("productTypeName", cpType.getName());

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(portletURL.toString());
					dropdownItem.setLabel(
						cpType.getLabel(cpRequestHelper.getLocale()));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public List<DropdownItem> getDropdownItems() throws Exception {
		List<DropdownItem> dropdownItems = new ArrayList<>();

		CPDefinition cpDefinition = getCPDefinition();

		if ((cpDefinition != null) && !cpDefinition.isDraft()) {
			DropdownItem convertToDraftDropdownItem =
				DropdownItemBuilder.setData(
					HashMapBuilder.<String, Object>put(
						"confirmationMessage",
						LanguageUtil.get(
							httpServletRequest,
							"converting-the-product-status-to-draft-will-" +
								"remove-the-product-from-the-product-" +
									"catalog.-do-you-wish-to-proceed")
					).put(
						"formId", liferayPortletResponse.getNamespace() + "fm"
					).build()
				).setHref(
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/cp_definitions/edit_cp_definition"
					).buildString()
				).setLabel(
					LanguageUtil.get(httpServletRequest, "convert-to-draft")
				).setTarget(
					"submitWithConfirmation"
				).build();

			dropdownItems.add(convertToDraftDropdownItem);
		}

		DropdownItem duplicateDropdownItem = DropdownItemBuilder.setHref(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					cpRequestHelper.getRenderRequest(),
					cpRequestHelper.getPortletId(), PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/cp_definitions/duplicate_cp_definition"
			).setParameter(
				"cpDefinitionId",
				ParamUtil.getString(httpServletRequest, "cpDefinitionId")
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).setLabel(
			LanguageUtil.get(httpServletRequest, "duplicate")
		).setTarget(
			"modal"
		).build();

		dropdownItems.add(duplicateDropdownItem);

		return dropdownItems;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		StringBundler sb = new StringBundler(
			"/o/headless-commerce-admin-catalog/v1.0/products/{productId}");

		if (_isVersioningEnabled()) {
			sb.append("/by-version/{version}");
		}

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CPDefinition.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/cp_definitions/edit_cp_definition"
				).setParameter(
					"cpDefinitionId", "{id}"
				).setParameter(
					"screenNavigationCategoryKey",
					CPDefinitionScreenNavigationConstants.CATEGORY_KEY_DETAILS
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				sb.toString(), "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						cpRequestHelper.getRenderRequest(),
						cpRequestHelper.getPortletId(),
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/cp_definitions/duplicate_cp_definition"
				).setParameter(
					"cpDefinitionId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"paste", "duplicate",
				LanguageUtil.get(httpServletRequest, "duplicate"), "post",
				"update", "modal"));
	}

	public List<HeaderActionModel> getHeaderActionModels()
		throws PortalException {

		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		CPDefinition cpDefinition = getCPDefinition();
		CProductVersionConfiguration cProductVersionConfiguration =
			_configurationProvider.getConfiguration(
				CProductVersionConfiguration.class,
				new SystemSettingsLocator(
					CProductVersionConfiguration.class.getName()));

		if (((cpDefinition != null) && cpDefinition.isDraft()) ||
			cProductVersionConfiguration.enabled()) {

			HeaderActionModel saveAsDraftHeaderActionModel =
				new HeaderActionModel(
					null, liferayPortletResponse.getNamespace() + "fm",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/cp_definitions/edit_cp_definition"
					).buildString(),
					liferayPortletResponse.getNamespace() + "saveAsDraftButton",
					"save-as-draft");

			headerActionModels.add(saveAsDraftHeaderActionModel);
		}

		String publishButtonLabel = "publish";

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				cpRequestHelper.getCompanyId(),
				cpRequestHelper.getScopeGroupId(),
				CPDefinition.class.getName())) {

			publishButtonLabel = "submit-for-workflow";
		}

		String additionalClasses = "btn-primary";

		if ((cpDefinition != null) && cpDefinition.isPending()) {
			additionalClasses = additionalClasses + " disabled";
		}

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			additionalClasses, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/cp_definitions/edit_cp_definition"
			).buildString(),
			liferayPortletResponse.getNamespace() + "publishButton",
			publishButtonLabel);

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public String getProductURLSeparator() {
		return _cpFriendlyURL.getProductURLSeparator(
			cpRequestHelper.getCompanyId());
	}

	public String getUrlTitleMapAsXML() throws PortalException {
		long cpDefinitionId = getCPDefinitionId();

		if (cpDefinitionId <= 0) {
			return StringPool.BLANK;
		}

		return _cpDefinitionService.getUrlTitleMapAsXML(cpDefinitionId);
	}

	public boolean hasCustomAttributesAvailable() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return CustomAttributesUtil.hasCustomAttributes(
			themeDisplay.getCompanyId(), CPDefinition.class.getName(),
			getCPDefinitionId(), null);
	}

	public boolean hasManageCommerceProductChannelVisibility()
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_CHANNEL_VISIBILITY);
	}

	public boolean isSelectedCatalog(CommerceCatalog commerceCatalog)
		throws PortalException {

		CPDefinition cpDefinition = getCPDefinition();

		if (commerceCatalog.getGroupId() == cpDefinition.getGroupId()) {
			return true;
		}

		return false;
	}

	public boolean showConfirmationMessage(CPDefinition cpDefinition)
		throws PortalException {

		if (cpDefinition == null) {
			return false;
		}

		CProduct cProduct = cpDefinition.getCProduct();

		if (cProduct.getPublishedCPDefinitionId() <= 0) {
			return false;
		}

		List<CPDefinition> cProductCPDefinitions =
			_cpDefinitionService.getCProductCPDefinitions(
				cProduct.getCProductId(), WorkflowConstants.STATUS_DRAFT,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (((cProductCPDefinitions.size() == 1) &&
			 !cpDefinition.equals(cProductCPDefinitions.get(0))) ||
			(cProductCPDefinitions.size() > 1)) {

			return true;
		}

		return false;
	}

	private boolean _isVersioningEnabled() throws PortalException {
		CProductVersionConfiguration cProductVersionConfiguration =
			ConfigurationProviderUtil.getConfiguration(
				CProductVersionConfiguration.class,
				new SystemSettingsLocator(
					CProductVersionConfiguration.class.getName()));

		return cProductVersionConfiguration.enabled();
	}

	private final AccountGroupRelLocalService _accountGroupRelLocalService;
	private final CommerceCatalogService _commerceCatalogService;
	private final CommerceChannelRelService _commerceChannelRelService;
	private final ConfigurationProvider _configurationProvider;
	private final CPDefinitionService _cpDefinitionService;
	private final CPFriendlyURL _cpFriendlyURL;
	private final ItemSelector _itemSelector;
	private final PortletResourcePermission _portletResourcePermission;

}