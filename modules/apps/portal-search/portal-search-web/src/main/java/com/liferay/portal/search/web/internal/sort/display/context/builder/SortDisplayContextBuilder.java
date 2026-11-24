/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.sort.display.context.builder;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.sort.configuration.SortPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.sort.display.context.SortDisplayContext;
import com.liferay.portal.search.web.internal.sort.display.context.SortTermDisplayContext;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferences;
import com.liferay.portal.search.web.internal.util.DisplayContextUtil;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SortDisplayContextBuilder {

	public SortDisplayContextBuilder(
			Language language, Portal portal, RenderRequest renderRequest,
			SortPortletPreferences sortPortletPreferences)
		throws ConfigurationException {

		_language = language;
		_portal = portal;
		_renderRequest = renderRequest;
		_sortPortletPreferences = sortPortletPreferences;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_sortPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SortPortletInstanceConfiguration.class, _themeDisplay);
	}

	public SortDisplayContext build() {
		SortDisplayContext sortDisplayContext = new SortDisplayContext();

		List<SortTermDisplayContext> sortTermDisplayContexts =
			_buildSortTermDisplayContexts();

		sortDisplayContext.setActionDropdownItems(
			_getActionDropdownItems(sortTermDisplayContexts));
		sortDisplayContext.setAnySelected(
			isAnySelected(sortTermDisplayContexts));

		sortDisplayContext.setDisplayStyleGroupId(getDisplayStyleGroupId());
		sortDisplayContext.setParameterName(_parameterName);
		sortDisplayContext.setParameterValue(getParameterValue());
		sortDisplayContext.setRenderNothing(isRenderNothing());
		sortDisplayContext.setSelectedSortTermDisplayContext(
			getSelectedSortTermDisplayContext(sortTermDisplayContexts));
		sortDisplayContext.setSortPortletInstanceConfiguration(
			_sortPortletInstanceConfiguration);
		sortDisplayContext.setSortTermDisplayContexts(sortTermDisplayContexts);

		return sortDisplayContext;
	}

	public SortDisplayContextBuilder currentURL(String currentURL) {
		_currentURL = currentURL;

		return this;
	}

	public SortDisplayContextBuilder parameterName(String parameterName) {
		_parameterName = parameterName;

		return this;
	}

	public SortDisplayContextBuilder parameterValues(
		String... parameterValues) {

		if (parameterValues == null) {
			_selectedFields = Collections.emptyList();

			return this;
		}

		_selectedFields = Arrays.asList(parameterValues);

		return this;
	}

	public SortDisplayContextBuilder renderNothing(boolean renderNothing) {
		_renderNothing = renderNothing;

		return this;
	}

	protected long getDisplayStyleGroupId() {
		return DisplayContextUtil.getDisplayStyleGroupId(
			_sortPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	protected String getParameterValue() {
		if (!_selectedFields.isEmpty()) {
			return _selectedFields.get(_selectedFields.size() - 1);
		}

		return null;
	}

	protected SortTermDisplayContext getSelectedSortTermDisplayContext(
		List<SortTermDisplayContext> sortTermDisplayContexts) {

		for (SortTermDisplayContext sortTermDisplayContext :
				sortTermDisplayContexts) {

			if (sortTermDisplayContext.isSelected()) {
				return sortTermDisplayContext;
			}
		}

		return null;
	}

	protected boolean isAnySelected(
		List<SortTermDisplayContext> sortTermDisplayContexts) {

		for (SortTermDisplayContext sortTermDisplayContext :
				sortTermDisplayContexts) {

			if (sortTermDisplayContext.isSelected()) {
				return true;
			}
		}

		return false;
	}

	protected boolean isRenderNothing() {
		JSONArray jsonArray = _sortPortletPreferences.getFieldsJSONArray();

		if ((jsonArray == null) || (jsonArray.length() == 0) ||
			_renderNothing) {

			return true;
		}

		return false;
	}

	private SortTermDisplayContext _buildSortTermDisplayContext(
		String label, String field, int index) {

		SortTermDisplayContext sortTermDisplayContext =
			new SortTermDisplayContext();

		sortTermDisplayContext.setLabel(label);
		sortTermDisplayContext.setLanguageLabel(
			_language.get(
				_portal.getHttpServletRequest(_renderRequest), label));
		sortTermDisplayContext.setField(field);

		if (_selectedFields.isEmpty() && (index == 0)) {
			sortTermDisplayContext.setSelected(true);
		}
		else {
			sortTermDisplayContext.setSelected(_selectedFields.contains(field));
		}

		return sortTermDisplayContext;
	}

	private List<SortTermDisplayContext> _buildSortTermDisplayContexts() {
		List<SortTermDisplayContext> sortTermDisplayContexts =
			new ArrayList<>();

		JSONArray fieldsJSONArray =
			_sortPortletPreferences.getFieldsJSONArray();

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			sortTermDisplayContexts.add(
				_buildSortTermDisplayContext(
					jsonObject.getString("label"),
					jsonObject.getString("field"), i));
		}

		return sortTermDisplayContexts;
	}

	private List<DropdownItem> _getActionDropdownItems(
		List<SortTermDisplayContext> sortTermDisplayContexts) {

		DropdownItemListBuilder.DropdownItemListWrapper
			dropdownItemListWrapper =
				new DropdownItemListBuilder.DropdownItemListWrapper();

		for (SortTermDisplayContext sortTermDisplayContext :
				sortTermDisplayContexts) {

			dropdownItemListWrapper.add(
				dropdownItem -> {
					dropdownItem.setHref(
						_getSortURL(sortTermDisplayContext.getField()));
					dropdownItem.setLabel(
						_language.get(
							_portal.getHttpServletRequest(_renderRequest),
							sortTermDisplayContext.getLabel()));

					if (sortTermDisplayContext.isSelected()) {
						dropdownItem.setActive(true);
						dropdownItem.setIcon("check-small");
					}
				});
		}

		return dropdownItemListWrapper.build();
	}

	private String _getSortURL(String field) {
		String sortURL = HttpComponentsUtil.removeParameter(
			_currentURL, _parameterName);

		sortURL = HttpComponentsUtil.setParameter(
			sortURL, _parameterName, field);

		return HttpComponentsUtil.sortParameters(sortURL);
	}

	private String _currentURL;
	private final Language _language;
	private String _parameterName;
	private final Portal _portal;
	private boolean _renderNothing;
	private final RenderRequest _renderRequest;
	private List<String> _selectedFields = Collections.emptyList();
	private final SortPortletInstanceConfiguration
		_sortPortletInstanceConfiguration;
	private final SortPortletPreferences _sortPortletPreferences;
	private final ThemeDisplay _themeDisplay;

}