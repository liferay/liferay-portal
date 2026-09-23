/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.microsoft.cognitive.services.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.document.library.asset.auto.tagger.microsoft.cognitive.services.internal.configuration.MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = AssetAutoTagProvider.class
)
public class MSCognitiveServicesImageAssetAutoTagProvider
	implements AssetAutoTagProvider<FileEntry> {

	@Override
	public Collection<String> getTagNames(FileEntry fileEntry) {
		try {
			MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration
				msCognitiveServicesAssetAutoTagProviderCompanyConfiguration =
					_getMSCognitiveServicesAssetAutoTagProviderCompanyConfiguration(
						fileEntry);

			if (!msCognitiveServicesAssetAutoTagProviderCompanyConfiguration.
					enabled() ||
				_isTemporary(fileEntry) || (fileEntry.getSize() > _MAX_SIZE) ||
				!_isSupportedMimeType(fileEntry.getMimeType())) {

				return Collections.emptyList();
			}

			_checkAPIEndpoint(
				msCognitiveServicesAssetAutoTagProviderCompanyConfiguration.
					apiEndpoint());

			JSONObject responseJSONObject = _queryComputerVisionJSONObject(
				msCognitiveServicesAssetAutoTagProviderCompanyConfiguration.
					apiEndpoint(),
				_secretResolver.resolve(
					fileEntry.getCompanyId(),
					msCognitiveServicesAssetAutoTagProviderCompanyConfiguration.
						apiKey()),
				fileEntry.getFileVersion());

			JSONArray tagsJSONArray = responseJSONObject.getJSONArray("tags");

			return JSONUtil.toStringList(tagsJSONArray, "name");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return Collections.emptyList();
		}
	}

	private void _checkAPIEndpoint(String apiEndpoint)
		throws MalformedURLException, UnknownHostException {

		URL url = new URL(apiEndpoint);

		if (InetAddressUtil.isLocalInetAddress(
				InetAddressUtil.getInetAddressByName(url.getHost()))) {

			throw new SecurityException(
				"Microsoft Cognitive Services Image Auto Tagging API " +
					"endpoint is a local address");
		}
	}

	private MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration
			_getMSCognitiveServicesAssetAutoTagProviderCompanyConfiguration(
				FileEntry fileEntry)
		throws ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration.class,
			fileEntry.getCompanyId());
	}

	private boolean _isSupportedMimeType(String mimeType) {
		return _supportedMimeTypes.contains(mimeType);
	}

	private boolean _isTemporary(FileEntry fileEntry) {
		return fileEntry.isRepositoryCapabilityProvided(
			TemporaryFileEntriesCapability.class);
	}

	private JSONObject _queryComputerVisionJSONObject(
			String apiEnpoint, String apiKey, FileVersion fileVersion)
		throws Exception {

		URL url = new URL(apiEnpoint + "/tag");

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty(
			"Content-Type", "application/octet-stream");
		httpURLConnection.setRequestProperty(
			"Ocp-Apim-Subscription-Key", apiKey);

		try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
			outputStream.write(
				FileUtil.getBytes(fileVersion.getContentStream(false)));
		}

		httpURLConnection.getResponseMessage();

		try (InputStream inputStream = httpURLConnection.getInputStream()) {
			return _jsonFactory.createJSONObject(StringUtil.read(inputStream));
		}
		catch (Exception exception) {
			try (InputStream inputStream = httpURLConnection.getErrorStream()) {
				throw new PortalException(
					StringBundler.concat(
						"Response code ", httpURLConnection.getResponseCode(),
						":", StringUtil.read(inputStream)),
					exception);
			}
		}
	}

	private static final int _MAX_SIZE = 4 * 1024 * 1024;

	private static final Log _log = LogFactoryUtil.getLog(
		MSCognitiveServicesImageAssetAutoTagProvider.class);

	private static final List<String> _supportedMimeTypes = Arrays.asList(
		ContentTypes.IMAGE_BMP, ContentTypes.IMAGE_GIF, ContentTypes.IMAGE_JPEG,
		ContentTypes.IMAGE_PNG);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SecretResolver _secretResolver;

}