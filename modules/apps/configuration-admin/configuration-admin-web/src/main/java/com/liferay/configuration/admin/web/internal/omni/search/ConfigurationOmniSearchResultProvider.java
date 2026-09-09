/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.omni.search;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.display.ConfigurationModelConfigurationEntry;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.product.navigation.omni.search.OmniSearchResult;
import com.liferay.product.navigation.omni.search.OmniSearchResultProvider;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
@Component(
	property = "omni.search.result.provider.order:Integer=200",
	service = OmniSearchResultProvider.class
)
public class ConfigurationOmniSearchResultProvider
	implements OmniSearchResultProvider {

	@Override
	public List<OmniSearchResult> getOmniSearchResults(
			String keywords, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			ThemeDisplay themeDisplay)
		throws PortalException {

		List<OmniSearchResult> omniSearchResults = new ArrayList<>();

		String lowerCaseKeywords = StringUtil.toLowerCase(keywords.trim());

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isOmniadmin()) {
			_addOmniSearchResult(
				liferayPortletRequest, lowerCaseKeywords, omniSearchResults,
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				ExtendedObjectClassDefinition.Scope.SYSTEM, null,
				"system-settings", themeDisplay);
		}

		if (permissionChecker.isCompanyAdmin()) {
			_addOmniSearchResult(
				liferayPortletRequest, lowerCaseKeywords, omniSearchResults,
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				ExtendedObjectClassDefinition.Scope.COMPANY,
				themeDisplay.getCompanyId(), "instance-settings", themeDisplay);
		}

		return omniSearchResults;
	}

	private void _addConfigurationModelOmniSearchResults(
			LiferayPortletRequest liferayPortletRequest, Locale locale,
			String lowerCaseKeywords, List<OmniSearchResult> omniSearchResults,
			String portletId, ExtendedObjectClassDefinition.Scope scope,
			Serializable scopePK, ThemeDisplay themeDisplay)
		throws PortalException {

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				LocaleUtil.toLanguageId(locale), scope, scopePK);

		for (ConfigurationModel configurationModel :
				configurationModels.values()) {

			if (!configurationModel.isGenerateUI()) {
				continue;
			}

			ConfigurationModelConfigurationEntry
				configurationModelConfigurationEntry =
					new ConfigurationModelConfigurationEntry(
						configurationModel);

			String name = configurationModelConfigurationEntry.getName(locale);

			if (!_matchesName(lowerCaseKeywords, name)) {
				continue;
			}

			omniSearchResults.add(
				new OmniSearchResult(
					_getCategoryLabel(configurationModel.getCategory(), locale),
					"cog", name,
					_getConfigurationModelEditURL(
						configurationModel, liferayPortletRequest, portletId,
						themeDisplay)));
		}
	}

	private void _addConfigurationScreenOmniSearchResults(
			LiferayPortletRequest liferayPortletRequest, Locale locale,
			String lowerCaseKeywords, List<OmniSearchResult> omniSearchResults,
			String portletId, ExtendedObjectClassDefinition.Scope scope,
			ThemeDisplay themeDisplay)
		throws PortalException {

		for (ConfigurationScreen configurationScreen :
				_configurationEntryRetriever.getAllConfigurationScreens()) {

			if (!scope.equals(configurationScreen.getScope()) ||
				!configurationScreen.isVisible()) {

				continue;
			}

			String name = configurationScreen.getName(locale);

			if (!_matchesName(lowerCaseKeywords, name)) {
				continue;
			}

			omniSearchResults.add(
				new OmniSearchResult(
					_getCategoryLabel(
						configurationScreen.getCategoryKey(), locale),
					"cog", name,
					_getConfigurationScreenEditURL(
						configurationScreen, liferayPortletRequest, portletId,
						themeDisplay)));
		}
	}

	private void _addOmniSearchResult(
			LiferayPortletRequest liferayPortletRequest,
			String lowerCaseKeywords, List<OmniSearchResult> omniSearchResults,
			String portletId, ExtendedObjectClassDefinition.Scope scope,
			Serializable scopePK, String scopeLanguageKey,
			ThemeDisplay themeDisplay)
		throws PortalException {

		List<OmniSearchResult> entryOmniSearchResults = new ArrayList<>();

		Locale locale = themeDisplay.getLocale();

		_addConfigurationModelOmniSearchResults(
			liferayPortletRequest, locale, lowerCaseKeywords,
			entryOmniSearchResults, portletId, scope, scopePK, themeDisplay);

		_addConfigurationScreenOmniSearchResults(
			liferayPortletRequest, locale, lowerCaseKeywords,
			entryOmniSearchResults, portletId, scope, themeDisplay);

		if (entryOmniSearchResults.isEmpty()) {
			return;
		}

		entryOmniSearchResults.sort(
			Comparator.comparing(
				OmniSearchResult::getTitle, String.CASE_INSENSITIVE_ORDER));

		omniSearchResults.add(
			new OmniSearchResult(
				"cog", entryOmniSearchResults,
				LanguageUtil.get(locale, scopeLanguageKey)));
	}

	private LiferayPortletURL _createEditLiferayPortletURL(
			LiferayPortletRequest liferayPortletRequest, String portletId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return PortletURLFactoryUtil.create(
			liferayPortletRequest, portletId,
			_portal.getControlPanelPlid(themeDisplay.getCompanyId()),
			PortletRequest.RENDER_PHASE);
	}

	private String _getCategoryLabel(String category, Locale locale) {
		if (Validator.isBlank(category)) {
			return StringPool.BLANK;
		}

		return LanguageUtil.get(locale, "category." + category, category);
	}

	private String _getConfigurationModelEditURL(
			ConfigurationModel configurationModel,
			LiferayPortletRequest liferayPortletRequest, String portletId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return PortletURLBuilder.create(
			_createEditLiferayPortletURL(
				liferayPortletRequest, portletId, themeDisplay)
		).setMVCRenderCommandName(
			() -> {
				if (configurationModel.isFactory()) {
					return "/configuration_admin/view_factory_instances";
				}

				return "/configuration_admin/edit_configuration";
			}
		).setParameter(
			"factoryPid", configurationModel.getFactoryPid()
		).setParameter(
			"pid",
			() -> {
				if (configurationModel.isFactory()) {
					return null;
				}

				return configurationModel.getID();
			}
		).buildString();
	}

	private String _getConfigurationScreenEditURL(
			ConfigurationScreen configurationScreen,
			LiferayPortletRequest liferayPortletRequest, String portletId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return PortletURLBuilder.create(
			_createEditLiferayPortletURL(
				liferayPortletRequest, portletId, themeDisplay)
		).setMVCRenderCommandName(
			"/configuration_admin/view_configuration_screen"
		).setParameter(
			"configurationScreenKey", configurationScreen.getKey()
		).buildString();
	}

	private boolean _matchesName(String keywords, String name) {
		if (Validator.isBlank(name)) {
			return false;
		}

		String lowerCaseName = StringUtil.toLowerCase(name);

		return lowerCaseName.contains(keywords);
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private Portal _portal;

}