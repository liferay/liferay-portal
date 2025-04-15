/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2AdminPortletDisplayContext
	extends BaseOAuth2PortletDisplayContext {

	public OAuth2AdminPortletDisplayContext(
		DLURLHelper dlURLHelper,
		OAuth2ApplicationScopeAliasesLocalService
			oAuth2ApplicationScopeAliasesLocalService,
		OAuth2ApplicationService oAuth2ApplicationService,
		OAuth2ProviderConfiguration oAuth2ProviderConfiguration,
		PortletRequest portletRequest, ThemeDisplay themeDisplay) {

		super(
			dlURLHelper, oAuth2ApplicationService, portletRequest,
			themeDisplay);

		this.oAuth2ApplicationScopeAliasesLocalService =
			oAuth2ApplicationScopeAliasesLocalService;
		_oAuth2ProviderConfiguration = oAuth2ProviderConfiguration;

		_resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());
	}

	public String getExtraPropertiesContent(
		OAuth2Application oAuth2Application) {

		if (hasRememberDevicePermission() &&
			oAuth2Application.isRememberDevice()) {

			return LanguageUtil.get(_resourceBundle, "remember-device-is-on");
		}

		if (hasAddTrustedApplicationPermission() &&
			oAuth2Application.isTrustedApplication()) {

			return LanguageUtil.get(_resourceBundle, "trusted");
		}

		return StringPool.BLANK;
	}

	public List<GrantType> getGrantTypes(
		PortletPreferences portletPreferences) {

		List<GrantType> grantTypes = new ArrayList<>();

		String[] oAuth2Grants = StringUtil.split(
			portletPreferences.getValue("oAuth2Grants", StringPool.BLANK));

		for (String oAuth2Grant : oAuth2Grants) {
			grantTypes.add(GrantType.valueOf(oAuth2Grant));
		}

		if (grantTypes.isEmpty()) {
			Collections.addAll(grantTypes, GrantType.values());
		}

		if (!_oAuth2ProviderConfiguration.allowAuthorizationCodeGrant()) {
			grantTypes.remove(GrantType.AUTHORIZATION_CODE);
		}

		if (!_oAuth2ProviderConfiguration.allowAuthorizationCodePKCEGrant()) {
			grantTypes.remove(GrantType.AUTHORIZATION_CODE_PKCE);
		}

		if (!_oAuth2ProviderConfiguration.allowClientCredentialsGrant()) {
			grantTypes.remove(GrantType.CLIENT_CREDENTIALS);
		}

		if (!_oAuth2ProviderConfiguration.allowRefreshTokenGrant()) {
			grantTypes.remove(GrantType.REFRESH_TOKEN);
		}

		if (!_oAuth2ProviderConfiguration.
				allowResourceOwnerPasswordCredentialsGrant()) {

			grantTypes.remove(GrantType.RESOURCE_OWNER_PASSWORD);
		}

		return grantTypes;
	}

	public int getOAuth2AuthorizationsCount(OAuth2Application oAuth2Application)
		throws PortalException {

		return OAuth2AuthorizationServiceUtil.
			getApplicationOAuth2AuthorizationsCount(
				oAuth2Application.getOAuth2ApplicationId());
	}

	public String[] getOAuth2Features(PortletPreferences portletPreferences) {
		return StringUtil.split(
			portletPreferences.getValue("oAuth2Features", StringPool.BLANK));
	}

	public ClientProfile[] getSortedClientProfiles() {
		ClientProfile[] clientProfiles = ClientProfile.values();

		Arrays.sort(clientProfiles, Comparator.comparingInt(ClientProfile::id));

		return clientProfiles;
	}

	protected final OAuth2ApplicationScopeAliasesLocalService
		oAuth2ApplicationScopeAliasesLocalService;

	private final OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;
	private final ResourceBundle _resourceBundle;

}