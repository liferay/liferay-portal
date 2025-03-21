/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.portlet.action;

import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.redirect.configuration.RedirectPatternConfigurationProvider;
import com.liferay.redirect.model.RedirectPatternEntry;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + RedirectPortletKeys.REDIRECT,
		"mvc.command.name=/redirect/edit_redirect_patterns"
	},
	service = MVCActionCommand.class
)
public class EditRedirectPatternsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_redirectPatternConfigurationProvider.updatePatternStrings(
				themeDisplay.getScopeGroupId(),
				_getRedirectPatternEntries(actionRequest));
		}
		catch (ConfigurationModelListenerException | PatternSyntaxException
					exception) {

			_log.error(exception);

			SessionErrors.add(actionRequest, "redirectPatternInvalid");

			hideDefaultErrorMessage(actionRequest);

			actionResponse.sendRedirect(
				ParamUtil.getString(actionRequest, "redirect"));
		}
	}

	private List<RedirectPatternEntry> _getRedirectPatternEntries(
		ActionRequest actionRequest) {

		List<RedirectPatternEntry> redirectPatternEntries = new ArrayList<>();

		Map<String, String[]> parameterMap = actionRequest.getParameterMap();

		for (int i = 0; parameterMap.containsKey("pattern_" + i); i++) {
			String patternString = null;

			String[] patterStrings = parameterMap.get("pattern_" + i);

			if ((patterStrings.length != 0) &&
				Validator.isNotNull(patterStrings[0])) {

				patternString = patterStrings[0];
			}

			String destinationURL = null;

			String[] destinationURLs = parameterMap.get("destinationURL_" + i);

			if ((destinationURLs.length != 0) &&
				Validator.isNotNull(destinationURLs[0])) {

				destinationURL = destinationURLs[0];
			}

			String userAgent = _getUserAgent(parameterMap, i);

			if ((patternString != null) && (destinationURL != null) &&
				(userAgent != null)) {

				redirectPatternEntries.add(
					new RedirectPatternEntry(
						Pattern.compile(patternString), destinationURL,
						userAgent));
			}
		}

		return redirectPatternEntries;
	}

	private String _getUserAgent(Map<String, String[]> parameterMap, int i) {
		String[] userAgents = parameterMap.get("userAgent_" + i);

		if ((userAgents.length != 0) && Validator.isNotNull(userAgents[0])) {
			return userAgents[0];
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditRedirectPatternsMVCActionCommand.class);

	@Reference
	private RedirectPatternConfigurationProvider
		_redirectPatternConfigurationProvider;

}