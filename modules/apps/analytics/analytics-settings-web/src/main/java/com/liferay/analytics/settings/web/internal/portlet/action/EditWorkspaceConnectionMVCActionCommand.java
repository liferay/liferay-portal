/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.portlet.action;

import com.liferay.analytics.settings.web.internal.util.AnalyticsSettingsUtil;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/analytics_settings/edit_workspace_connection"
	},
	service = MVCActionCommand.class
)
public class EditWorkspaceConnectionMVCActionCommand
	extends BaseAnalyticsMVCActionCommand {

	@Override
	protected void updateConfigurationProperties(
			ActionRequest actionRequest,
			Dictionary<String, Object> configurationProperties)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Objects.equals(cmd, "disconnect")) {
			_disconnect(actionRequest, configurationProperties);

			return;
		}

		boolean upgrade = false;

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		if (AnalyticsSettingsUtil.isAnalyticsEnabled(companyId) &&
			Validator.isBlank(
				AnalyticsSettingsUtil.getConnectionType(companyId))) {

			upgrade = true;
		}

		_connect(actionRequest, configurationProperties, upgrade);
	}

	private void _connect(
			ActionRequest actionRequest,
			Dictionary<String, Object> configurationProperties, boolean upgrade)
		throws Exception {

		String dataSourceConnectionJSON = _connectDataSource(
			actionRequest, _createTokenJSONObject(actionRequest));

		_updateCompanyPreferences(actionRequest, dataSourceConnectionJSON);
		_updateConfigurationProperties(
			actionRequest, configurationProperties, dataSourceConnectionJSON,
			upgrade);
	}

	private String _connectDataSource(
			ActionRequest actionRequest, JSONObject tokenJSONObject)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		httpClientBuilder.useSystemProperties();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build()) {

			HttpPost httpPost = new HttpPost(tokenJSONObject.getString("url"));

			Company company = themeDisplay.getCompany();

			httpPost.setEntity(
				new UrlEncodedFormEntity(
					Arrays.asList(
						new BasicNameValuePair("name", company.getName()),
						new BasicNameValuePair(
							"portalURL", _portal.getPortalURL(themeDisplay)),
						new BasicNameValuePair(
							"token", tokenJSONObject.getString("token")))));

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpPost);

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				_log.error(
					"Unable to connect to Analytics Cloud at " +
						tokenJSONObject.getString("url"));

				throw new PortalException("Invalid token");
			}

			return EntityUtils.toString(closeableHttpResponse.getEntity());
		}
	}

	private JSONObject _createTokenJSONObject(ActionRequest actionRequest)
		throws Exception {

		String token = ParamUtil.getString(actionRequest, "token");

		try {
			if (Validator.isBlank(token)) {
				throw new IllegalArgumentException();
			}

			return _jsonFactory.createJSONObject(
				new String(Base64.decode(token)));
		}
		catch (Exception exception) {
			_log.error("Invalid token", exception);

			throw new PortalException("Invalid token", exception);
		}
	}

	private void _disconnect(
			ActionRequest actionRequest,
			Dictionary<String, Object> configurationProperties)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		String dataSourceId = null;
		String faroBackendURL = null;
		String projectId = null;

		if (!AnalyticsSettingsUtil.isAnalyticsEnabled(companyId)) {
			if (Validator.isNotNull(
					GetterUtil.getString(
						configurationProperties.get("token"), null))) {

				dataSourceId = GetterUtil.getString(
					configurationProperties.get("osbAsahDataSourceId"), null);
				faroBackendURL = GetterUtil.getString(
					configurationProperties.get(
						"liferayAnalyticsFaroBackendURL"),
					null);
				projectId = GetterUtil.getString(
					configurationProperties.get("liferayAnalyticsProjectId"),
					null);
			}
		}
		else {
			dataSourceId = AnalyticsSettingsUtil.getDataSourceId(companyId);
			projectId = AnalyticsSettingsUtil.getProjectId(companyId);
		}

		try {
			HttpResponse httpResponse = AnalyticsSettingsUtil.doPost(
				null, companyId, faroBackendURL,
				String.format(
					"api/1.0/data-sources/%s/disconnect", dataSourceId),
				projectId);

			StatusLine statusLine = httpResponse.getStatusLine();

			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				SessionErrors.add(
					actionRequest, "unableToNotifyAnalyticsCloud");
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			SessionErrors.add(actionRequest, "unableToNotifyAnalyticsCloud");
		}

		configurationProperties.remove("token");

		clearConfiguration(companyId);
	}

	private void _updateCompanyPreferences(
			ActionRequest actionRequest, String dataSourceConnectionJSON)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			dataSourceConnectionJSON);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			unicodeProperties.setProperty(key, jsonObject.getString(key));
		}

		unicodeProperties.setProperty(
			"liferayAnalyticsConnectionType", "token");

		companyService.updatePreferences(
			themeDisplay.getCompanyId(), unicodeProperties);
	}

	private void _updateConfigurationProperties(
			ActionRequest actionRequest,
			Dictionary<String, Object> configurationProperties,
			String dataSourceConnectionJSON, boolean upgrade)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		configurationProperties.put("companyId", themeDisplay.getCompanyId());

		configurationProperties.put(
			"token", ParamUtil.getString(actionRequest, "token"));

		if (upgrade) {
			configurationProperties.put(
				"syncedContactFieldNames",
				new String[] {
					"birthday", "classNameId", "classPK", "companyId",
					"contactId", "createDate", "emailAddress", "employeeNumber",
					"employeeStatusId", "facebookSn", "firstName",
					"hoursOfOperation", "jabberSn", "jobClass", "jobTitle",
					"lastName", "male", "middleName", "modifiedDate",
					"parentContactId", "prefixListTypeId", "skypeSn", "smsSn",
					"suffixListTypeId", "twitterSn", "userId", "userName"
				});
			configurationProperties.put(
				"syncedUserFieldNames",
				new String[] {
					"agreedToTermsOfUse", "comments", "companyId", "contactId",
					"createDate", "emailAddress", "emailAddressVerified",
					"externalReferenceCode", "facebookId", "firstName",
					"googleUserId", "greeting", "jobTitle", "languageId",
					"lastName", "ldapServerId", "middleName", "modifiedDate",
					"openId", "portraitId", "screenName", "status",
					"timeZoneId", "userId", "uuid"
				});
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			dataSourceConnectionJSON);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			configurationProperties.put(key, jsonObject.getString(key));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditWorkspaceConnectionMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}