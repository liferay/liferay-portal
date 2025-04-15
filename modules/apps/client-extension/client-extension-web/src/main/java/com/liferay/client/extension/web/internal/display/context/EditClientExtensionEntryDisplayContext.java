/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.display.context;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.web.internal.display.context.util.CETLabelUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.WebAppPool;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * @author Iván Zaera Avellón
 */
public class EditClientExtensionEntryDisplayContext<T extends CET> {

	public EditClientExtensionEntryDisplayContext(
		boolean adding, T cet, PortletRequest portletRequest) {

		_adding = adding;
		_cet = cet;
		_portletRequest = portletRequest;

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Set<Locale> getAvailableLocales() {
		return LanguageUtil.getCompanyAvailableLocales(
			_themeDisplay.getCompanyId());
	}

	public T getCET() {
		return _cet;
	}

	public String getCmd() {
		if (_adding) {
			return Constants.ADD;
		}

		return Constants.UPDATE;
	}

	public String getDefaultLanguageId() throws PortalException {
		Company company = _themeDisplay.getCompany();

		return LanguageUtil.getLanguageId(company.getLocale());
	}

	public String getDescription() {
		return BeanParamUtil.getString(_cet, _portletRequest, "description");
	}

	public String getEditJSP() {
		return _cet.getEditJSP();
	}

	public String getExternalReferenceCode() {
		return BeanParamUtil.getString(
			_cet, _portletRequest, "externalReferenceCode");
	}

	public String getHelpLabel() {
		return CETLabelUtil.getHelpLabel(_themeDisplay.getLocale(), getType());
	}

	public String getLearnResourceKey() {
		return CETLabelUtil.getLearnResourceKey(getType());
	}

	public String getName() {
		return BeanParamUtil.getString(_cet, _portletRequest, "name");
	}

	public List<SelectOption> getPortletCategoryNameSelectOptions(
		String selectedPortletCategoryName) {

		List<SelectOption> selectOptions = new ArrayList<>();

		boolean found = false;

		if (Validator.isBlank(selectedPortletCategoryName)) {
			selectedPortletCategoryName = "category.client-extensions";
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletCategory rootPortletCategory = (PortletCategory)WebAppPool.get(
			themeDisplay.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		for (PortletCategory portletCategory :
				rootPortletCategory.getCategories()) {

			selectOptions.add(
				new SelectOption(
					LanguageUtil.get(
						themeDisplay.getLocale(), portletCategory.getName()),
					portletCategory.getName(),
					selectedPortletCategoryName.equals(
						portletCategory.getName())));

			if (Objects.equals(
					portletCategory.getName(), "category.client-extensions")) {

				found = true;
			}
		}

		if (!found) {
			selectOptions.add(
				new SelectOption(
					LanguageUtil.get(
						themeDisplay.getLocale(), "category.client-extensions"),
					"category.client-extensions",
					Objects.equals(
						selectedPortletCategoryName,
						"category.client-extensions")));
		}

		return ListUtil.sort(
			selectOptions,
			new Comparator<SelectOption>() {

				@Override
				public int compare(
					SelectOption selectOption1, SelectOption selectOption2) {

					String label1 = selectOption1.getLabel();
					String label2 = selectOption2.getLabel();

					return label1.compareTo(label2);
				}

			});
	}

	public String getProperties() {
		return ParamUtil.getString(
			_portletRequest, "properties",
			PropertiesUtil.toString(_cet.getProperties()));
	}

	public String getRedirect() {
		return ParamUtil.getString(_portletRequest, "redirect");
	}

	public String getSourceCodeURL() {
		return BeanParamUtil.getString(_cet, _portletRequest, "sourceCodeURL");
	}

	public String[] getStrings(String urls) {
		String[] strings = StringUtil.split(urls, CharPool.NEW_LINE);

		if (strings.length == 0) {
			return _EMPTY_STRINGS;
		}

		return strings;
	}

	public String getTitle() {
		if (_adding) {
			return CETLabelUtil.getNewLabel(
				_themeDisplay.getLocale(), _cet.getType());
		}

		return _cet.getName(_themeDisplay.getLocale());
	}

	public String getType() {
		return BeanParamUtil.getString(_cet, _portletRequest, "type");
	}

	public String getTypeLabel() {
		return CETLabelUtil.getTypeLabel(_themeDisplay.getLocale(), getType());
	}

	public boolean isAdding() {
		return _adding;
	}

	public boolean isPropertiesVisible() {
		return _cet.hasProperties();
	}

	private static final String[] _EMPTY_STRINGS = {StringPool.BLANK};

	private final boolean _adding;
	private final T _cet;
	private final PortletRequest _portletRequest;
	private final ThemeDisplay _themeDisplay;

}