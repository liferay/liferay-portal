/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2AdminPortletDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN,
		"mvc.command.name=/oauth2_provider/update_oauth2_application"
	},
	service = MVCActionCommand.class
)
public class UpdateOAuth2ApplicationMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest request, ActionResponse response) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		long oAuth2ApplicationId = ParamUtil.getLong(
			request, "oAuth2ApplicationId");

		int clientProfileId = ParamUtil.getInteger(request, "clientProfile");

		ClientProfile clientProfile = _getClientProfile(clientProfileId);

		PortletPreferences portletPreferences = request.getPreferences();

		OAuth2AdminPortletDisplayContext oAuth2AdminPortletDisplayContext =
			new OAuth2AdminPortletDisplayContext(
				_dlurlHelper, _oAuth2ApplicationScopeAliasesLocalService,
				_oAuth2ApplicationService, _oAuth2ProviderConfiguration,
				request, themeDisplay);

		String[] oAuth2Features =
			oAuth2AdminPortletDisplayContext.getOAuth2Features(
				portletPreferences);

		List<String> featuresList = new ArrayList<>();

		for (String feature : oAuth2Features) {
			if (ParamUtil.getBoolean(request, "feature-" + feature)) {
				featuresList.add(feature);
			}
		}

		List<GrantType> allowedGrantTypesList = new ArrayList<>();
		List<GrantType> grantTypes =
			oAuth2AdminPortletDisplayContext.getGrantTypes(portletPreferences);

		for (GrantType grantType : clientProfile.grantTypes()) {
			if (!grantTypes.contains(grantType)) {
				continue;
			}

			if (ParamUtil.getBoolean(request, "grant-" + grantType.name())) {
				allowedGrantTypesList.add(grantType);
			}
		}

		String clientAuthenticationMethod = ParamUtil.get(
			request, "clientAuthenticationMethod", StringPool.BLANK);
		long clientCredentialUserId = ParamUtil.get(
			request, "clientCredentialUserId", themeDisplay.getUserId());
		String clientId = ParamUtil.get(request, "clientId", StringPool.BLANK);
		String clientSecret = ParamUtil.get(
			request, "clientSecret", StringPool.BLANK);
		String description = ParamUtil.get(
			request, "description", StringPool.BLANK);
		String homePageURL = ParamUtil.get(
			request, "homePageURL", StringPool.BLANK);
		String jwks = ParamUtil.get(request, "jwks", StringPool.BLANK);
		String name = ParamUtil.get(request, "name", StringPool.BLANK);
		String privacyPolicyURL = ParamUtil.get(
			request, "privacyPolicyURL", StringPool.BLANK);
		List<String> redirectURIsList = Arrays.asList(
			StringUtil.splitLines(
				ParamUtil.get(request, "redirectURIs", StringPool.BLANK)));
		List<String> scopeAliasesList = Collections.emptyList();

		boolean rememberDevice = false;
		boolean trustedApplication = false;

		if (allowedGrantTypesList.contains(GrantType.AUTHORIZATION_CODE) ||
			allowedGrantTypesList.contains(GrantType.AUTHORIZATION_CODE_PKCE)) {

			trustedApplication = ParamUtil.getBoolean(
				request, "trustedApplication");

			if (!trustedApplication) {
				rememberDevice = ParamUtil.getBoolean(
					request, "rememberDevice");
			}
		}

		try {
			if (oAuth2ApplicationId == 0) {
				if (Validator.isBlank(clientId)) {
					clientId = OAuth2SecureRandomGenerator.generateClientId();
				}

				for (GrantType grantType : allowedGrantTypesList) {
					if (!grantType.isSupportsPublicClients()) {
						clientSecret =
							OAuth2SecureRandomGenerator.generateClientSecret();
					}
				}

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(
						OAuth2Application.class.getName(), request);

				OAuth2Application oAuth2Application =
					_oAuth2ApplicationService.addOAuth2Application(
						allowedGrantTypesList, clientAuthenticationMethod,
						clientCredentialUserId, clientId, clientProfile.id(),
						clientSecret, description, featuresList, homePageURL, 0,
						jwks, name, privacyPolicyURL, redirectURIsList,
						rememberDevice, scopeAliasesList, trustedApplication,
						serviceContext);

				response.setRenderParameter(
					"oAuth2ApplicationId",
					String.valueOf(oAuth2Application.getOAuth2ApplicationId()));
			}
			else {
				OAuth2Application oAuth2Application =
					_oAuth2ApplicationService.getOAuth2Application(
						oAuth2ApplicationId);

				_oAuth2ApplicationService.updateOAuth2Application(
					oAuth2ApplicationId,
					oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
					allowedGrantTypesList, clientAuthenticationMethod,
					clientCredentialUserId, clientId, clientProfile.id(),
					clientSecret, description, featuresList, homePageURL,
					oAuth2Application.getIconFileEntryId(), jwks, name,
					privacyPolicyURL, redirectURIsList, rememberDevice,
					trustedApplication);

				long fileEntryId = ParamUtil.getLong(request, "fileEntryId");

				if (ParamUtil.getBoolean(request, "deleteLogo")) {
					_oAuth2ApplicationService.updateIcon(
						oAuth2ApplicationId, null);
				}
				else if (fileEntryId > 0) {
					FileEntry fileEntry = _dlAppService.getFileEntry(
						fileEntryId);

					InputStream inputStream = fileEntry.getContentStream();

					_oAuth2ApplicationService.updateIcon(
						oAuth2ApplicationId, inputStream);

					_dlAppService.deleteFileEntry(fileEntryId);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			Class<?> peClass = portalException.getClass();

			SessionErrors.add(request, peClass.getName(), portalException);
		}

		String backURL = ParamUtil.get(request, "backURL", StringPool.BLANK);

		response.setRenderParameter("redirect", backURL);

		return true;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_oAuth2ProviderConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2ProviderConfiguration.class, properties);
	}

	private ClientProfile _getClientProfile(int clientProfileId) {
		for (ClientProfile clientProfile : ClientProfile.values()) {
			if (clientProfile.id() == clientProfileId) {
				return clientProfile;
			}
		}

		throw new IllegalArgumentException(
			"No client profile found for " + clientProfileId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateOAuth2ApplicationMVCActionCommand.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

	private OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;

}