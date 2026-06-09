/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.web.internal.util.DisplayContextUtil;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Pedro Leite
 */
public class EditConfigurationDisplayContext {

	public EditConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService) {

		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			_themeDisplay.getUserId());

		return HashMapBuilder.<String, Object>put(
			"accountEntryId",
			() -> {
				if (accountEntry == null) {
					return null;
				}

				return accountEntry.getAccountEntryId();
			}
		).put(
			"backURL", DisplayContextUtil.getAIHubURL(_themeDisplay)
		).put(
			"clientId",
			() -> {
				if (accountEntry == null) {
					return null;
				}

				OAuth2Application oAuth2Application =
					_oAuth2ApplicationLocalService.
						fetchOAuth2ApplicationByExternalReferenceCode(
							accountEntry.getAccountEntryId() +
								"-ai-hub-oauth2-application",
							_themeDisplay.getCompanyId());

				if (oAuth2Application == null) {
					return null;
				}

				return oAuth2Application.getClientId();
			}
		).put(
			"externalReferenceCode",
			() -> {
				if (accountEntry == null) {
					return null;
				}

				return accountEntry.getAccountEntryId() +
					"-ai-hub-configuration";
			}
		).build();
	}

	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;
	private final ThemeDisplay _themeDisplay;

}