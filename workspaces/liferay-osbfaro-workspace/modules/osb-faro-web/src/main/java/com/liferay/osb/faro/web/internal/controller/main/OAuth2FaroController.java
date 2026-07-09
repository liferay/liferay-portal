/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.osb.faro.web.internal.application.ApiApplication;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.main.TokenDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.BigEndianCodec;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = {FaroController.class, OAuth2FaroController.class})
@Path("/{groupId}/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class OAuth2FaroController extends BaseFaroController {

	@GET
	@Path("/tokens")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public List<TokenDisplay> getTokenDisplays(
			@PathParam("groupId") long groupId)
		throws Exception {

		return TransformUtil.transform(
			_getUserOAuth2AuthorizationsByGroupId(groupId),
			userOAuth2Authorization -> _mapTokenDisplay(
				userOAuth2Authorization));
	}

	@Path("/tokens/new")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public TokenDisplay newToken(
			@PathParam("groupId") long groupId,
			@QueryParam("expiresIn") Long expiresIn,
			@QueryParam("type") String type,
			@Context HttpServletRequest httpServletRequest)
		throws Exception {

		OAuth2Application oAuth2Application = _getOrCreateOAuth2Application(
			httpServletRequest, type);

		if (Validator.isNotNull(type)) {
			List<OAuth2Authorization> oAuth2Authorizations =
				_getApplicationOAuth2Authorizations(
					groupId, oAuth2Application.getOAuth2ApplicationId(), type);

			if (ListUtil.isNotEmpty(oAuth2Authorizations)) {
				return _mapTokenDisplay(oAuth2Authorizations.get(0));
			}
		}

		if (expiresIn == null) {
			expiresIn = 3153600000L;
		}

		String tokensJSON = _localOAuthClient.requestTokens(
			oAuth2Application, oAuth2Application.getClientCredentialUserId());

		if (tokensJSON == null) {
			throw new PortalException(
				"Unable to create access token for OAuth2 application " +
					oAuth2Application.getOAuth2ApplicationId());
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(tokensJSON);

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					jsonObject.getString("access_token"));

		if (oAuth2Authorization == null) {
			throw new PortalException(
				"Unable to get OAuth2 authorization for the created access " +
					"token");
		}

		Date accessTokenCreateDate =
			oAuth2Authorization.getAccessTokenCreateDate();

		oAuth2Authorization.setAccessTokenExpirationDate(
			new Date(accessTokenCreateDate.getTime() + (expiresIn * 1000)));

		Date refreshTokenCreateDate =
			oAuth2Authorization.getRefreshTokenCreateDate();

		if (refreshTokenCreateDate != null) {
			oAuth2Authorization.setRefreshTokenExpirationDate(
				new Date(
					refreshTokenCreateDate.getTime() + (expiresIn * 1000)));
		}

		oAuth2Authorization =
			_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
				oAuth2Authorization);

		_setOAuth2AuthorizationGroupId(groupId, oAuth2Authorization);

		if (Validator.isNotNull(type)) {
			_setOAuth2AuthorizationType(oAuth2Authorization, type);
		}

		return _mapTokenDisplay(oAuth2Authorization);
	}

	@Path("/tokens/{token}/revoke")
	@POST
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void revokeToken(
			@PathParam("groupId") long groupId,
			@PathParam("token") String token)
		throws Exception {

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(token);

		if (oAuth2Authorization == null) {
			throw new IllegalArgumentException(
				"Unable to revoke OAuth2 authorization with token " + token);
		}

		_oAuth2AuthorizationLocalService.deleteOAuth2Authorization(
			oAuth2Authorization);
	}

	private boolean _filterOAuth2Authorization(
		long groupId, OAuth2Authorization oAuth2Authorization, String type) {

		ExpandoBridge expandoBridge = oAuth2Authorization.getExpandoBridge();

		if (Validator.isNotNull(type)) {
			if (Objects.equals(
					groupId, expandoBridge.getAttribute("groupId", false)) &&
				Objects.equals(
					type, expandoBridge.getAttribute("type", false))) {

				return true;
			}

			return false;
		}

		if (Objects.equals(
				groupId, expandoBridge.getAttribute("groupId", false)) &&
			Objects.equals(expandoBridge.getAttribute("type", false), "")) {

			return true;
		}

		return false;
	}

	private String _generateApplicationName() {
		Matcher matcher = _baseIdPattern.matcher(_generateSecureRandomString());

		return matcher.replaceFirst("app-$1-$2-$3-$4-$5");
	}

	private String _generateClientSecret() {
		Matcher matcher = _baseIdPattern.matcher(_generateSecureRandomString());

		return matcher.replaceFirst("secret-$1-$2-$3-$4-$5");
	}

	private String _generateSecureRandomString() {
		int size = 16;

		StringBundler sb = new StringBundler(size);

		int count = (int)Math.ceil((double)size / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(buffer, i * 8, SecureRandomUtil.nextLong());
		}

		for (int i = 0; i < size; i++) {
			sb.append(Integer.toHexString(0xFF & buffer[i]));
		}

		return sb.toString();
	}

	private List<OAuth2Authorization> _getApplicationOAuth2Authorizations(
			long groupId, long oAuth2ApplicationId, String type)
		throws Exception {

		return TransformUtil.transform(
			_oAuth2AuthorizationLocalService.getOAuth2Authorizations(
				oAuth2ApplicationId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null),
			oAuth2Authorization -> {
				if (!_filterOAuth2Authorization(
						groupId, oAuth2Authorization, type)) {

					return null;
				}

				return oAuth2Authorization;
			});
	}

	private OAuth2Application _getOrCreateOAuth2Application(
			HttpServletRequest httpServletRequest, String type)
		throws Exception {

		User user = getUser();

		String clientId = user.getEmailAddress();

		if (Validator.isNotNull(type)) {
			clientId = StringUtil.upperCase(type);
		}

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				getCompanyId(), clientId);

		if (oAuth2Application != null) {
			return oAuth2Application;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			OAuth2Application.class.getName(), httpServletRequest);

		ClientProfile clientProfile = ClientProfile.HEADLESS_SERVER;

		List<String> scopes = _oAuth2ScopesMap.getOrDefault(
			type,
			Arrays.asList(
				ApiApplication.OAuth2ScopeAliases.RECOMMENDATIONS_EVERYTHING,
				ApiApplication.OAuth2ScopeAliases.REPORTS_EVERYTHING));

		return _oAuth2ApplicationLocalService.addOAuth2Application(
			user.getCompanyId(), user.getUserId(), user.getFullName(),
			Arrays.asList(GrantType.CLIENT_CREDENTIALS), StringPool.BLANK,
			user.getUserId(), clientId, clientProfile.id(),
			_generateClientSecret(), StringPool.BLANK, Collections.emptyList(),
			StringPool.BLANK, 0, StringPool.BLANK, _generateApplicationName(),
			StringPool.BLANK, Collections.emptyList(), false, scopes, false,
			serviceContext);
	}

	private List<OAuth2Authorization> _getUserOAuth2AuthorizationsByGroupId(
			long groupId)
		throws Exception {

		return TransformUtil.transform(
			_oAuth2AuthorizationService.getUserOAuth2Authorizations(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			oAuth2Authorization -> {
				if (!_filterOAuth2Authorization(
						groupId, oAuth2Authorization, null)) {

					return null;
				}

				return oAuth2Authorization;
			});
	}

	private TokenDisplay _mapTokenDisplay(
		OAuth2Authorization oAuth2Authorization) {

		Date expirationDate =
			oAuth2Authorization.getRefreshTokenExpirationDate();

		if (expirationDate == null) {
			expirationDate = oAuth2Authorization.getAccessTokenExpirationDate();
		}

		return new TokenDisplay(
			oAuth2Authorization.getCreateDate(), expirationDate,
			oAuth2Authorization.getAccessTokenCreateDate(),
			oAuth2Authorization.getAccessTokenContent());
	}

	private void _setOAuth2AuthorizationGroupId(
		long groupId, OAuth2Authorization oAuth2Authorization) {

		ExpandoBridge expandoBridge = oAuth2Authorization.getExpandoBridge();

		expandoBridge.setAttribute("groupId", groupId, false);
	}

	private void _setOAuth2AuthorizationType(
		OAuth2Authorization oAuth2Authorization, String type) {

		ExpandoBridge expandoBridge = oAuth2Authorization.getExpandoBridge();

		expandoBridge.setAttribute("type", type, false);
	}

	private static final Pattern _baseIdPattern = Pattern.compile(
		"(.{8})(.{4})(.{4})(.{4})(.*)");

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LocalOAuthClient _localOAuthClient;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Reference
	private OAuth2AuthorizationService _oAuth2AuthorizationService;

	private final Map<String, List<String>> _oAuth2ScopesMap =
		HashMapBuilder.<String, List<String>>put(
			"demandbase",
			Arrays.asList(ApiApplication.OAuth2ScopeAliases.ACCOUNTS_WRITE)
		).put(
			"hubspot",
			Arrays.asList(ApiApplication.OAuth2ScopeAliases.HUBSPOT_WRITE)
		).put(
			"marketo",
			Arrays.asList(ApiApplication.OAuth2ScopeAliases.MARKETO_WRITE)
		).build();

}