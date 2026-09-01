/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryMenuDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationScopeDisplay;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
public abstract class BaseSettingsPanelApp extends BasePanelApp {

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(
				getPortletId(), themeDisplay);

		Map<String, ConfigurationCategoryMenuDisplay>
			configurationCategoryMenuDisplays =
				configurationEntryRetriever.
					getConfigurationCategoryMenuDisplays(
						themeDisplay.getLanguageId(),
						configurationScopeDisplayContext.getScope(),
						configurationScopeDisplayContext.getScopePK());

		for (ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay :
				configurationCategoryMenuDisplays.values()) {

			if (configurationCategoryMenuDisplay.isEmpty()) {
				continue;
			}

			ConfigurationCategoryDisplay configurationCategoryDisplay =
				configurationCategoryMenuDisplay.
					getConfigurationCategoryDisplay();

			panelAppNavigationItems.add(
				new PanelAppNavigationItem(
					configurationCategoryDisplay.getCategoryLabel(
						LocaleUtil.ENGLISH),
					_getEditURL(
						configurationCategoryMenuDisplay.
							getFirstConfigurationEntry(),
						httpServletRequest),
					configurationCategoryDisplay.getCategoryLabel(
						themeDisplay.getLocale())));

			_addConfigurationEntryPanelAppNavigationItems(
				configurationCategoryDisplay, configurationCategoryMenuDisplay,
				httpServletRequest, panelAppNavigationItems, themeDisplay);
		}

		return panelAppNavigationItems;
	}

	@Reference
	protected ConfigurationEntryRetriever configurationEntryRetriever;

	private void _addConfigurationEntryPanelAppNavigationItems(
			ConfigurationCategoryDisplay configurationCategoryDisplay,
			ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay,
			HttpServletRequest httpServletRequest,
			List<PanelAppNavigationItem> panelAppNavigationItems,
			ThemeDisplay themeDisplay)
		throws PortalException {

		for (ConfigurationScopeDisplay configurationScopeDisplay :
				configurationCategoryMenuDisplay.
					getConfigurationScopeDisplays()) {

			List<ConfigurationEntry> configurationEntries =
				configurationScopeDisplay.getConfigurationEntries();

			if (configurationEntries.size() < 2) {
				continue;
			}

			panelAppNavigationItems.addAll(
				TransformUtil.unsafeTransform(
					configurationEntries,
					configurationEntry -> new PanelAppNavigationItem(
						configurationEntry.getName(LocaleUtil.ENGLISH),
						_getEditURL(configurationEntry, httpServletRequest),
						configurationEntry.getName(themeDisplay.getLocale()),
						configurationCategoryDisplay.getCategoryLabel(
							themeDisplay.getLocale()))));
		}
	}

	private String _getEditURL(
			ConfigurationEntry configurationEntry,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		Map<String, String> editURLParameters =
			configurationEntry.getEditURLParameters();

		PortletURL portletURL = getPortletURL(httpServletRequest);

		for (Map.Entry<String, String> entry : editURLParameters.entrySet()) {
			portletURL.setParameter(entry.getKey(), entry.getValue());
		}

		return portletURL.toString();
	}

}