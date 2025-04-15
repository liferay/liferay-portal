/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.sort.display.context.builder;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.sort.configuration.SortPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.sort.display.context.SortDisplayContext;
import com.liferay.portal.search.web.internal.sort.display.context.SortTermDisplayContext;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferences;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;

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
			_buildTermDisplayContexts();

		sortDisplayContext.setAnySelected(
			isAnySelected(sortTermDisplayContexts));

		sortDisplayContext.setDisplayStyleGroupId(getDisplayStyleGroupId());
		sortDisplayContext.setParameterName(_parameterName);
		sortDisplayContext.setParameterValue(getParameterValue());
		sortDisplayContext.setRenderNothing(isRenderNothing());
		sortDisplayContext.setSortPortletInstanceConfiguration(
			_sortPortletInstanceConfiguration);
		sortDisplayContext.setSortTermDisplayContexts(sortTermDisplayContexts);

		return sortDisplayContext;
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
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
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

	protected boolean isAnySelected(
		List<SortTermDisplayContext> sortTermDisplayContexts) {

		for (SortTermDisplayContext sortTermDisplayContext :
				sortTermDisplayContexts) {

			if (sortTermDisplayContext.isSelected()) {
				return true;
			}
		}

		if (!sortTermDisplayContexts.isEmpty()) {
			SortTermDisplayContext sortTermDisplayContext =
				sortTermDisplayContexts.get(0);

			sortTermDisplayContext.setSelected(true);

			return true;
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

	private SortTermDisplayContext _buildTermDisplayContext(
		String label, String field) {

		SortTermDisplayContext sortTermDisplayContext =
			new SortTermDisplayContext();

		sortTermDisplayContext.setLabel(label);
		sortTermDisplayContext.setLanguageLabel(
			_language.get(
				_portal.getHttpServletRequest(_renderRequest), label));
		sortTermDisplayContext.setField(field);
		sortTermDisplayContext.setSelected(_selectedFields.contains(field));

		return sortTermDisplayContext;
	}

	private List<SortTermDisplayContext> _buildTermDisplayContexts() {
		List<SortTermDisplayContext> sortTermDisplayContexts =
			new ArrayList<>();

		JSONArray fieldsJSONArray =
			_sortPortletPreferences.getFieldsJSONArray();

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			sortTermDisplayContexts.add(
				_buildTermDisplayContext(
					jsonObject.getString("label"),
					jsonObject.getString("field")));
		}

		return sortTermDisplayContexts;
	}

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