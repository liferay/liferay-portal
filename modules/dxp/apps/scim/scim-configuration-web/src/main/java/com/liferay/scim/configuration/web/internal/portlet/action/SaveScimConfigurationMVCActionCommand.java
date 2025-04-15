/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.configuration.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.scim.configuration.web.internal.constants.ScimWebKeys;
import com.liferay.scim.rest.util.ScimClientUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alvaro Saugar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/scim_configuration/save_scim_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveScimConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			SessionErrors.add(actionRequest, PrincipalException.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String scimClientId = ScimClientUtil.generateScimClientId(
			ParamUtil.getString(actionRequest, "oAuth2ApplicationName"));

		if (Objects.equals(cmd, "generate")) {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.getOAuth2Application(
					themeDisplay.getCompanyId(), scimClientId);

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				_localOAuthClient.requestTokens(
					oAuth2Application,
					oAuth2Application.getClientCredentialUserId()));

			String accessToken = jsonObject.getString("access_token");

			List<OAuth2Authorization> oAuth2Authorizations =
				_oAuth2AuthorizationLocalService.getOAuth2Authorizations(
					oAuth2Application.getOAuth2ApplicationId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			for (OAuth2Authorization oAuth2Authorization :
					oAuth2Authorizations) {

				if (Objects.equals(
						accessToken,
						oAuth2Authorization.getAccessTokenContent())) {

					continue;
				}

				Date accessTokenExpirationDate =
					oAuth2Authorization.getAccessTokenExpirationDate();

				int daysBetween = DateUtil.getDaysBetween(
					accessTokenExpirationDate, new Date());

				Date refreshTokenExpirationDate =
					oAuth2Authorization.getRefreshTokenExpirationDate();

				if ((daysBetween > 10) ||
					(refreshTokenExpirationDate != null)) {

					oAuth2Authorization.setAccessTokenExpirationDate(
						new Date(
							Math.min(
								accessTokenExpirationDate.getTime(),
								System.currentTimeMillis() + (Time.DAY * 10))));
					oAuth2Authorization.setRefreshTokenContent(null);
					oAuth2Authorization.setRefreshTokenCreateDate(null);
					oAuth2Authorization.setRefreshTokenExpirationDate(null);

					_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
						oAuth2Authorization);
				}
			}

			actionRequest.setAttribute(
				ScimWebKeys.SCIM_OAUTH2_ACCESS_TOKEN, accessToken);
		}
		else if (Objects.equals(cmd, "reset")) {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.getOAuth2Application(
					themeDisplay.getCompanyId(), scimClientId);

			_oAuth2AuthorizationService.revokeAllOAuth2Authorizations(
				oAuth2Application.getOAuth2ApplicationId());

			_oAuth2ApplicationLocalService.deleteOAuth2Application(
				oAuth2Application);

			Indexer<UserGroup> userGroupIndexer =
				IndexerRegistryUtil.nullSafeGetIndexer(UserGroup.class);
			Indexer<User> userIndexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			ActionableDynamicQuery actionableDynamicQuery =
				_expandoValueLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					List<Long> columnIds = new ArrayList<>();

					ExpandoColumn expandoColumn =
						_expandoColumnLocalService.getColumn(
							themeDisplay.getCompanyId(), User.class.getName(),
							"CUSTOM_FIELDS", "scimClientId");

					if (expandoColumn != null) {
						columnIds.add(expandoColumn.getColumnId());
					}

					expandoColumn = _expandoColumnLocalService.getColumn(
						themeDisplay.getCompanyId(), UserGroup.class.getName(),
						"CUSTOM_FIELDS", "scimClientId");

					if (expandoColumn != null) {
						columnIds.add(expandoColumn.getColumnId());
					}

					if (!columnIds.isEmpty()) {
						Property columnProperty = PropertyFactoryUtil.forName(
							"columnId");

						dynamicQuery.add(columnProperty.in(columnIds));
					}

					Property dataProperty = PropertyFactoryUtil.forName("data");

					dynamicQuery.add(dataProperty.eq(scimClientId));
				});
			actionableDynamicQuery.setPerformActionMethod(
				(ExpandoValue expandoValue) -> {
					_expandoRowLocalService.deleteRow(
						expandoValue.getTableId(), expandoValue.getClassPK());

					String className = _portal.getClassName(
						expandoValue.getClassNameId());

					if (className.equals(User.class.getName())) {
						userIndexer.reindex(
							_userLocalService.getUser(
								expandoValue.getClassPK()));
					}
					else {
						userGroupIndexer.reindex(
							_userGroupLocalService.getUserGroup(
								expandoValue.getClassPK()));
					}
				});

			actionableDynamicQuery.performActions();
		}
		else if (Objects.equals(cmd, "revoke")) {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.getOAuth2Application(
					themeDisplay.getCompanyId(), scimClientId);

			_oAuth2AuthorizationService.revokeAllOAuth2Authorizations(
				oAuth2Application.getOAuth2ApplicationId());
		}
		else {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(&(", ConfigurationAdmin.SERVICE_FACTORYPID,
						"=com.liferay.scim.rest.internal.configuration.",
						"ScimClientOAuth2ApplicationConfiguration)(companyId=",
						themeDisplay.getCompanyId(), "))"));

			if (configurations != null) {
				Configuration configuration = configurations[0];

				Dictionary<String, Object> properties =
					configuration.getProperties();

				properties.put(
					"matcherField",
					ParamUtil.getString(actionRequest, "matcherField"));
				properties.put(
					"oAuth2ApplicationName",
					ParamUtil.getString(
						actionRequest, "oAuth2ApplicationName"));
				properties.put("userId", themeDisplay.getUserId());

				configuration.update(properties);
			}
			else {
				Configuration configuration =
					_configurationAdmin.createFactoryConfiguration(
						"com.liferay.scim.rest.internal.configuration." +
							"ScimClientOAuth2ApplicationConfiguration",
						StringPool.QUESTION);

				configuration.update(
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId", themeDisplay.getCompanyId()
					).put(
						"matcherField",
						ParamUtil.getString(actionRequest, "matcherField")
					).put(
						"oAuth2ApplicationName",
						ParamUtil.getString(
							actionRequest, "oAuth2ApplicationName")
					).put(
						"userId", themeDisplay.getUserId()
					).build());
			}
		}
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

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

	@Reference
	private Portal _portal;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}