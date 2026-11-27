/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.util;

import com.liferay.exportimport.internal.constants.RemoteInstanceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class RemoteImportHttpUtil {

	public static JSONObject fetchImportProcessJSONObject(
			long companyId, long importProcessId,
			Map<String, Serializable> remoteInstanceValues)
		throws Exception {

		Http.Options options = new Http.Options();

		options.setTimeout(_TIMEOUT);

		try {
			return _invoke(
				companyId, options, "/import-processes/" + importProcessId,
				remoteInstanceValues);
		}
		catch (NoSuchModelException noSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchModelException);
			}

			return null;
		}
	}

	public static String getImportURL(
		Map<String, Serializable> remoteInstanceValues) {

		return _getBaseURL(remoteInstanceValues) +
			"/group/control_panel/manage?p_p_id=" + PortletKeys.COMPANY_IMPORT;
	}

	public static JSONObject postImportPreview(
			long companyId, FileEntry fileEntry,
			Map<String, Serializable> remoteInstanceValues)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE,
			ContentTypes.MULTIPART_FORM_DATA + "; boundary=" +
				StringUtil.randomString(30));

		try (InputStream inputStream = fileEntry.getContentStream()) {
			options.addInputStreamPart(
				"file", fileEntry.getFileName(), inputStream,
				ContentTypes.APPLICATION_OCTET_STREAM);
			options.setPost(true);
			options.setTimeout(_TIMEOUT);

			return _invoke(
				companyId, options, "/import-preview", remoteInstanceValues);
		}
	}

	public static JSONObject postImportProcess(
			long companyId, JSONObject importPreviewJSONObject, String name,
			Map<String, Serializable> remoteInstanceValues)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.setBody(
			JSONUtil.put(
				"assetLibraries",
				_getGroupReferencesJSONArray(
					importPreviewJSONObject.getJSONArray("assetLibraries"))
			).put(
				"name", name
			).put(
				"portletDataHandlers",
				importPreviewJSONObject.getJSONArray("portletDataHandlers")
			).put(
				"sites",
				_getGroupReferencesJSONArray(
					importPreviewJSONObject.getJSONArray("sites"))
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setPost(true);
		options.setTimeout(_TIMEOUT);

		return _invoke(
			companyId, options, "/import-processes", remoteInstanceValues);
	}

	private static String _getAccessToken(
			String baseURL, long companyId,
			Map<String, Serializable> remoteInstanceValues)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addPart(
			"client_id", MapUtil.getString(remoteInstanceValues, "clientId"));
		options.addPart(
			"client_secret",
			_getClientSecret(
				companyId,
				MapUtil.getString(remoteInstanceValues, "clientSecret")));
		options.addPart("grant_type", "client_credentials");
		options.setLocation(baseURL + "/o/oauth2/token");
		options.setPost(true);
		options.setTimeout(_TIMEOUT);

		String responseString = null;

		try {
			responseString = HttpUtil.URLtoString(options);
		}
		catch (IOException ioException) {
			throw new PortalException(
				"Unable to connect to " + options.getLocation(), ioException);
		}

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpServletResponse.SC_OK) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				responseString);

			return jsonObject.getString("access_token");
		}

		if (responseString.contains("invalid_grant")) {
			throw new PortalException(
				"The client credentials user of the OAuth 2 application is " +
					"not allowed to create tokens");
		}

		throw new PortalException(
			"The client ID or the client secret is not valid");
	}

	private static String _getBaseURL(
		Map<String, Serializable> remoteInstanceValues) {

		StringBundler sb = new StringBundler(4);

		if (MapUtil.getBoolean(remoteInstanceValues, "secureConnection")) {
			sb.append(Http.HTTPS_WITH_SLASH);
		}
		else {
			sb.append(Http.HTTP_WITH_SLASH);
		}

		sb.append(MapUtil.getString(remoteInstanceValues, "hostname"));

		int port = MapUtil.getInteger(remoteInstanceValues, "port");

		if (port > 0) {
			sb.append(StringPool.COLON);
			sb.append(port);
		}

		return sb.toString();
	}

	private static String _getClientSecret(long companyId, String clientSecret)
		throws Exception {

		if (!StringUtil.startsWith(
				clientSecret, RemoteInstanceConstants.CLIENT_SECRET_PREFIX)) {

			return SecretResolverUtil.resolve(companyId, clientSecret);
		}

		Company company = CompanyLocalServiceUtil.getCompany(companyId);

		return EncryptorUtil.decryptAuthenticated(
			company.getKeyObj(),
			clientSecret.substring(
				RemoteInstanceConstants.CLIENT_SECRET_PREFIX.length()));
	}

	private static JSONArray _getGroupReferencesJSONArray(JSONArray jsonArray) {
		JSONArray groupReferencesJSONArray = JSONFactoryUtil.createJSONArray();

		if (jsonArray == null) {
			return groupReferencesJSONArray;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			groupReferencesJSONArray.put(
				JSONUtil.put(
					"externalReferenceCode",
					jsonObject.getString("externalReferenceCode")));
		}

		return groupReferencesJSONArray;
	}

	private static JSONObject _invoke(
			long companyId, Http.Options options, String path,
			Map<String, Serializable> remoteInstanceValues)
		throws Exception {

		String baseURL = _getBaseURL(remoteInstanceValues);

		options.addHeader(
			HttpHeaders.AUTHORIZATION,
			"Bearer " +
				_getAccessToken(baseURL, companyId, remoteInstanceValues));
		options.setLocation(baseURL + "/o/export-import/v1.0" + path);

		String responseString = null;

		try {
			responseString = HttpUtil.URLtoString(options);
		}
		catch (IOException ioException) {
			throw new PortalException(
				"Unable to connect to " + options.getLocation(), ioException);
		}

		Http.Response response = options.getResponse();

		int responseCode = response.getResponseCode();

		if (responseCode == HttpServletResponse.SC_UNAUTHORIZED) {
			throw new PortalException(
				"Unable to authenticate with the remote instance");
		}

		if (responseCode == HttpServletResponse.SC_FORBIDDEN) {
			throw new PortalException(
				"The OAuth 2 application is not allowed to import into " +
					options.getLocation());
		}

		if (responseCode == HttpServletResponse.SC_NOT_FOUND) {
			throw new NoSuchModelException(
				"Unable to find " + options.getLocation());
		}

		if ((responseCode < HttpServletResponse.SC_OK) ||
			(responseCode >= HttpServletResponse.SC_MULTIPLE_CHOICES)) {

			throw new PortalException(
				StringBundler.concat(
					"Request to ", options.getLocation(),
					" returned response code ", responseCode, " with body ",
					StringUtil.shorten(responseString, 500)));
		}

		return JSONFactoryUtil.createJSONObject(responseString);
	}

	private static final int _TIMEOUT = 10 * 60 * 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		RemoteImportHttpUtil.class);

}