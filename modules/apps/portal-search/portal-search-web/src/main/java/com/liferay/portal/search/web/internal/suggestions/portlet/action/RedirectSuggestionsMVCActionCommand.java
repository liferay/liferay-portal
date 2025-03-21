/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.suggestions.portlet.action;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferences;
import com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.suggestions.constants.SuggestionsPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SuggestionsPortletKeys.SUGGESTIONS,
		"mvc.command.name=/portal_search/redirect_suggestions"
	},
	service = MVCActionCommand.class
)
public class RedirectSuggestionsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		SearchBarPortletPreferences searchBarPortletPreferences =
			new SearchBarPortletPreferencesImpl(actionRequest.getPreferences());

		String redirectURL = _getRedirectURL(
			actionRequest, searchBarPortletPreferences);

		redirectURL = _addParameters(
			redirectURL, actionRequest,
			searchBarPortletPreferences.getKeywordsParameterName(),
			searchBarPortletPreferences.getScopeParameterName());

		actionResponse.sendRedirect(portal.escapeRedirect(redirectURL));
	}

	@Reference
	protected Portal portal;

	private String _addParameter(
		String url, PortletRequest portletRequest, String parameterName) {

		String parameterValue = StringUtil.trim(
			portletRequest.getParameter(parameterName));

		if (Validator.isBlank(parameterValue)) {
			return url;
		}

		return HttpComponentsUtil.addParameter(
			url, parameterName, parameterValue);
	}

	private String _addParameters(
		String url, PortletRequest portletRequest, String... parameterNames) {

		for (String parameterName : parameterNames) {
			url = _addParameter(url, portletRequest, parameterName);
		}

		return url;
	}

	private String _getFriendlyURL(ThemeDisplay themeDisplay) {
		Layout layout = themeDisplay.getLayout();

		return layout.getFriendlyURL(themeDisplay.getLocale());
	}

	private String _getPath(String path, String destination) {
		if (destination.charAt(0) == CharPool.SLASH) {
			return path.concat(destination);
		}

		return path + CharPool.SLASH + destination;
	}

	private String _getRedirectURL(
		ActionRequest actionRequest,
		SearchBarPortletPreferences searchBarPortletPreferences) {

		ThemeDisplay themeDisplay = _getThemeDisplay(actionRequest);

		String url = themeDisplay.getURLCurrent();

		String friendlyURL = _getFriendlyURL(themeDisplay);

		String path = url.substring(0, url.indexOf(friendlyURL));

		String destination = searchBarPortletPreferences.getDestination();

		if (Validator.isNull(destination)) {
			destination = friendlyURL;
		}

		return _getPath(path, destination);
	}

	private ThemeDisplay _getThemeDisplay(ActionRequest actionRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(actionRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

}