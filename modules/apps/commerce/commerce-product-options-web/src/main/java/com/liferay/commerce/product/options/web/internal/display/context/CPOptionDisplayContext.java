/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.product.configuration.CPOptionConfiguration;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.servlet.taglib.ui.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.product.util.CommerceOptionTypeUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPOptionDisplayContext {

	public CPOptionDisplayContext(
		CommerceOptionTypeRegistry commerceOptionTypeRegistry,
		ConfigurationProvider configurationProvider, CPOption cpOption,
		HttpServletRequest httpServletRequest) {

		_commerceOptionTypeRegistry = commerceOptionTypeRegistry;
		_configurationProvider = configurationProvider;
		_cpOption = cpOption;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public List<CommerceOptionType> getCommerceOptionTypes()
		throws PortalException {

		List<CommerceOptionType> commerceOptionTypes =
			_commerceOptionTypeRegistry.getCommerceOptionTypes();

		CPOptionConfiguration cpOptionConfiguration =
			_configurationProvider.getConfiguration(
				CPOptionConfiguration.class,
				new SystemSettingsLocator(CPConstants.SERVICE_NAME_CP_OPTION));

		String[] allowedCommerceOptionTypes =
			cpOptionConfiguration.allowedCommerceOptionTypes();

		return CommerceOptionTypeUtil.getAllowedCommerceOptionTypes(
			commerceOptionTypes, allowedCommerceOptionTypes);
	}

	public CPOption getCPOption() {
		return _cpOption;
	}

	public long getCPOptionId() {
		if (_cpOption == null) {
			return 0;
		}

		return _cpOption.getCPOptionId();
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						cpRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/cp_options/add_cp_option"
					).setBackURL(
						cpRequestHelper.getCurrentURL()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						cpRequestHelper.getRequest(), "add-option"));
				dropdownItem.setTarget("modal");
			}
		).build();
	}

	public List<HeaderActionModel> getHeaderActionModels() {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		RenderResponse renderResponse = cpRequestHelper.getRenderResponse();

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			"btn-primary", renderResponse.getNamespace() + "fm", null, null,
			"save");

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public List<FDSActionDropdownItem> getOptionFDSActionDropdownItems() {
		return _getFDSActionDropdownItems(
			PortletURLBuilder.createRenderURL(
				cpRequestHelper.getRenderResponse()
			).setMVCRenderCommandName(
				"/cp_options/edit_cp_option"
			).setRedirect(
				cpRequestHelper.getCurrentURL()
			).setParameter(
				"cpOptionId", "{id}"
			).setParameter(
				"screenNavigationCategoryKey",
				CPDefinitionScreenNavigationConstants.CATEGORY_KEY_DETAILS
			).buildString(),
			false);
	}

	public CreationMenu getOptionValueCreationMenu(long cpOptionId) {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						cpRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/cp_options/add_cp_option_value"
					).setBackURL(
						cpRequestHelper.getCurrentURL()
					).setParameter(
						"cpOptionId", cpOptionId
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						cpRequestHelper.getRequest(),
						"add-option-value-template"));
				dropdownItem.setTarget("modal");
			}
		).build();
	}

	public List<FDSActionDropdownItem> getOptionValueFDSActionDropdownItems()
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			cpRequestHelper.getRenderResponse()
		).setMVCRenderCommandName(
			"/cp_options/edit_cp_option_value"
		).setRedirect(
			cpRequestHelper.getCurrentURL()
		).setParameter(
			"cpOptionValueId", "{id}"
		).setParameter(
			"screenNavigationCategoryKey",
			CPDefinitionScreenNavigationConstants.CATEGORY_KEY_DETAILS
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return _getFDSActionDropdownItems(portletURL.toString(), true);
	}

	public boolean hasValues(CPOption cpOption) {
		CommerceOptionType commerceOptionType =
			_commerceOptionTypeRegistry.getCommerceOptionType(
				cpOption.getCommerceOptionTypeKey());

		if (commerceOptionType == null) {
			return false;
		}

		return commerceOptionType.hasValues();
	}

	public boolean isCPOptionSelectDate() {
		return Objects.equals(
			CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
			_cpOption.getCommerceOptionTypeKey());
	}

	protected final CPRequestHelper cpRequestHelper;

	private List<FDSActionDropdownItem> _getFDSActionDropdownItems(
		String portletURL, boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			portletURL, "pencil", "edit",
			LanguageUtil.get(cpRequestHelper.getRequest(), "edit"), "get", null,
			null);

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(cpRequestHelper.getRequest(), "delete"), null,
				"delete", null));

		return fdsActionDropdownItems;
	}

	private final CommerceOptionTypeRegistry _commerceOptionTypeRegistry;
	private final ConfigurationProvider _configurationProvider;
	private CPOption _cpOption;

}