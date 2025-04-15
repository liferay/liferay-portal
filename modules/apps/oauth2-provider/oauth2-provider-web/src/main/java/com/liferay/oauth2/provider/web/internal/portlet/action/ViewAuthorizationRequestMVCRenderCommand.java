/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationException;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.web.internal.AssignableScopes;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderWebKeys;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2AuthorizePortletDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_AUTHORIZE,
		"mvc.command.name=/",
		"mvc.command.name=/oauth2_provider/view_authorization_request"
	},
	service = MVCRenderCommand.class
)
public class ViewAuthorizationRequestMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(renderRequest));

		Map<String, String> oAuth2Parameters = _getOAuth2Parameters(
			httpServletRequest);

		String error = oAuth2Parameters.get("error");

		if (StringUtil.equals(error, "invalid_client")) {
			SessionErrors.add(renderRequest, "clientIdInvalid");

			return "/authorize/error.jsp";
		}

		String redirectURI = oAuth2Parameters.get("redirect_uri");

		if (Validator.isBlank(redirectURI)) {
			SessionErrors.add(renderRequest, "redirectURIMissing");

			return "/authorize/error.jsp";
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String clientId = oAuth2Parameters.get("client_id");

		try {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationService.getOAuth2Application(
					themeDisplay.getCompanyId(), clientId);
			OAuth2AuthorizePortletDisplayContext
				oAuth2AuthorizePortletDisplayContext =
					new OAuth2AuthorizePortletDisplayContext(
						_dlURLHelper, _oAuth2ApplicationService, renderRequest,
						themeDisplay);

			oAuth2AuthorizePortletDisplayContext.setOAuth2Application(
				oAuth2Application);
			oAuth2AuthorizePortletDisplayContext.setOAuth2Parameters(
				oAuth2Parameters);

			AssignableScopes assignableScopes = new AssignableScopes(
				_applicationDescriptorLocator, themeDisplay.getLocale(),
				_scopeDescriptorLocator);

			if (oAuth2Application.getOAuth2ApplicationScopeAliasesId() > 0) {
				OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
					_oAuth2ApplicationScopeAliasesLocalService.
						getOAuth2ApplicationScopeAliases(
							oAuth2Application.
								getOAuth2ApplicationScopeAliasesId());

				String[] requestedScopeAliases = StringUtil.split(
					oAuth2Parameters.get("scope"), StringPool.SPACE);

				_populateAssignableScopes(
					assignableScopes, oAuth2ApplicationScopeAliases,
					requestedScopeAliases);
			}

			oAuth2AuthorizePortletDisplayContext.setAssignableScopes(
				assignableScopes);

			renderRequest.setAttribute(
				OAuth2ProviderWebKeys.OAUTH2_AUTHORIZE_PORTLET_DISPLAY_CONTEXT,
				oAuth2AuthorizePortletDisplayContext);
		}
		catch (NoSuchOAuth2ApplicationException
					noSuchOAuth2ApplicationException) {

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchOAuth2ApplicationException);
			}

			SessionErrors.add(renderRequest, "clientIdInvalid");

			return "/authorize/error.jsp";
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			SessionErrors.add(renderRequest, principalException.getClass());

			return "/authorize/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return "/authorize/authorize.jsp";
	}

	private Map<String, String> _getOAuth2Parameters(
		HttpServletRequest httpServletRequest) {

		Map<String, String> oAuth2Parameters = new HashMap<>();

		Enumeration<String> enumeration =
			httpServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith("oauth2_")) {
				oAuth2Parameters.put(
					name.substring("oauth2_".length()),
					ParamUtil.getString(httpServletRequest, name));
			}
		}

		return oAuth2Parameters;
	}

	private void _populateAssignableScopes(
		AssignableScopes assignableScopes,
		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases,
		String[] requestedScopeAliases) {

		Set<String> requestedScopeAliasesSet = new HashSet<>(
			Arrays.asList(requestedScopeAliases));

		Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
			_scopeLocator.getLiferayOAuth2Scopes(
				oAuth2ApplicationScopeAliases.getCompanyId());

		for (OAuth2ScopeGrant oAuth2ScopeGrant :
				_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
					oAuth2ApplicationScopeAliases.
						getOAuth2ApplicationScopeAliasesId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			if (Collections.disjoint(
					oAuth2ScopeGrant.getScopeAliasesList(),
					requestedScopeAliasesSet)) {

				continue;
			}

			LiferayOAuth2Scope liferayOAuth2Scope =
				_scopeLocator.getLiferayOAuth2Scope(
					oAuth2ScopeGrant.getCompanyId(),
					oAuth2ScopeGrant.getApplicationName(),
					oAuth2ScopeGrant.getScope());

			if (liferayOAuth2Scopes.contains(liferayOAuth2Scope)) {
				assignableScopes.addLiferayOAuth2Scope(liferayOAuth2Scope);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAuthorizationRequestMVCRenderCommand.class);

	@Reference
	private ApplicationDescriptorLocator _applicationDescriptorLocator;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ScopeDescriptorLocator _scopeDescriptorLocator;

	@Reference
	private ScopeLocator _scopeLocator;

}