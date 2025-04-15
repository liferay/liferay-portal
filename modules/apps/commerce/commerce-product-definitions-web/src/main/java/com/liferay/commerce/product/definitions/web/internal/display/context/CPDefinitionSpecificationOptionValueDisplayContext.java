/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.commerce.product.constants.CPOptionCategoryConstants;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.item.selector.criterion.CPSpecificationOptionItemSelectorCriterion;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionSpecificationOptionValueDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionSpecificationOptionValueDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CPOptionCategoryService cpOptionCategoryService,
		ItemSelector itemSelector, ListTypeEntryService listTypeEntryService) {

		super(actionHelper, httpServletRequest);

		_cpOptionCategoryService = cpOptionCategoryService;
		_itemSelector = itemSelector;
		_listTypeEntryService = listTypeEntryService;
	}

	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValue()
		throws PortalException {

		if (_cpDefinitionSpecificationOptionValue != null) {
			return _cpDefinitionSpecificationOptionValue;
		}

		_cpDefinitionSpecificationOptionValue =
			actionHelper.getCPDefinitionSpecificationOptionValue(
				cpRequestHelper.getRenderRequest());

		return _cpDefinitionSpecificationOptionValue;
	}

	public List<CPOptionCategory> getCPOptionCategories()
		throws PortalException {

		BaseModelSearchResult<CPOptionCategory>
			cpOptionCategoryBaseModelSearchResult =
				_cpOptionCategoryService.searchCPOptionCategories(
					cpRequestHelper.getCompanyId(), null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		return cpOptionCategoryBaseModelSearchResult.getBaseModels();
	}

	public String getCPOptionCategoryTitle(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue)
		throws PortalException {

		long cpOptionCategoryId =
			cpDefinitionSpecificationOptionValue.getCPOptionCategoryId();

		if (cpOptionCategoryId ==
				CPOptionCategoryConstants.DEFAULT_CP_OPTION_CATEGORY_ID) {

			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			CPOptionCategory cpOptionCategory =
				_cpOptionCategoryService.getCPOptionCategory(
					cpOptionCategoryId);

			return cpOptionCategory.getTitle(themeDisplay.getLocale());
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return StringPool.BLANK;
	}

	public String getItemSelectorUrl() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		CPSpecificationOptionItemSelectorCriterion
			cpSpecificationOptionItemSelectorCriterion =
				new CPSpecificationOptionItemSelectorCriterion();

		cpSpecificationOptionItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				Collections.<ItemSelectorReturnType>singletonList(
					new UUIDItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				"productSpecificationOptionsSelectItem",
				cpSpecificationOptionItemSelectorCriterion));
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	public Map<String, List<SelectOption>> getSelectOptionsMap()
		throws Exception {

		Map<String, List<SelectOption>> selectOptionsMap = new TreeMap<>();

		CPSpecificationOption cpSpecificationOption =
			_cpDefinitionSpecificationOptionValue.getCPSpecificationOption();

		Locale locale = LocaleUtil.fromLanguageId(
			LanguageUtil.getLanguageId(httpServletRequest));

		for (ListTypeDefinition listTypeDefinition :
				cpSpecificationOption.getListTypeDefinitions()) {

			List<SelectOption> selectOptions = new ArrayList<>();

			for (ListTypeEntry listTypeEntry :
					_listTypeEntryService.getListTypeEntries(
						listTypeDefinition.getListTypeDefinitionId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				selectOptions.add(
					new SelectOption(
						listTypeEntry.getName(locale), listTypeEntry.getKey(),
						Objects.equals(
							_cpDefinitionSpecificationOptionValue.getValue(
								locale),
							listTypeEntry.getName(locale))));
			}

			selectOptionsMap.put(
				listTypeDefinition.getName(locale), selectOptions);
		}

		return selectOptionsMap;
	}

	public boolean hasCustomAttributesAvailable() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long cpDefinitionSpecificationOptionValueId = 0;

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				getCPDefinitionSpecificationOptionValue();

		if (cpDefinitionSpecificationOptionValue != null) {
			cpDefinitionSpecificationOptionValueId =
				cpDefinitionSpecificationOptionValue.
					getCPDefinitionSpecificationOptionValueId();
		}

		return CustomAttributesUtil.hasCustomAttributes(
			themeDisplay.getCompanyId(),
			CPDefinitionSpecificationOptionValue.class.getName(),
			cpDefinitionSpecificationOptionValueId, null);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionSpecificationOptionValueDisplayContext.class);

	private CPDefinitionSpecificationOptionValue
		_cpDefinitionSpecificationOptionValue;
	private final CPOptionCategoryService _cpOptionCategoryService;
	private final ItemSelector _itemSelector;
	private final ListTypeEntryService _listTypeEntryService;

}