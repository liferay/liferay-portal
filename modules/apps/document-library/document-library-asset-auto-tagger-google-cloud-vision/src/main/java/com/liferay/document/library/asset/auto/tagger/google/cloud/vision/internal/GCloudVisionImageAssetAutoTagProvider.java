/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal.configuration.GCloudVisionAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal.util.GCloudVisionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.net.HttpURLConnection;

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
public class GCloudVisionImageAssetAutoTagProvider
	implements AssetAutoTagProvider<FileEntry> {

	@Override
	public Collection<String> getTagNames(FileEntry fileEntry) {
		try {
			GCloudVisionAssetAutoTagProviderCompanyConfiguration
				gCloudVisionAssetAutoTagProviderCompanyConfiguration =
					_getGCloudVisionAssetAutoTagProviderCompanyConfiguration(
						fileEntry);

			if (!gCloudVisionAssetAutoTagProviderCompanyConfiguration.
					enabled() ||
				_isTemporary(fileEntry) ||
				!_isSupportedMimeType(fileEntry.getMimeType())) {

				return Collections.emptyList();
			}

			JSONObject responseJSONObject = _queryGCloudVisionJSONObject(
				_secretResolver.resolve(
					fileEntry.getCompanyId(),
					gCloudVisionAssetAutoTagProviderCompanyConfiguration.
						apiKey()),
				GCloudVisionUtil.getAnnotateImagePayload(fileEntry));

			JSONArray responsesJSONArray = responseJSONObject.getJSONArray(
				"responses");

			if ((responsesJSONArray != null) &&
				(responsesJSONArray.length() > 0)) {

				JSONObject firstResponseJSONObject =
					responsesJSONArray.getJSONObject(0);

				JSONArray labelAnnotationsJSONArray =
					firstResponseJSONObject.getJSONArray("labelAnnotations");

				return JSONUtil.toStringList(
					labelAnnotationsJSONArray, "description");
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return Collections.emptyList();
	}

	private GCloudVisionAssetAutoTagProviderCompanyConfiguration
			_getGCloudVisionAssetAutoTagProviderCompanyConfiguration(
				FileEntry fileEntry)
		throws ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			GCloudVisionAssetAutoTagProviderCompanyConfiguration.class,
			fileEntry.getCompanyId());
	}

	private boolean _isSupportedMimeType(String mimeType) {
		return _supportedMimeTypes.contains(mimeType);
	}

	private boolean _isTemporary(FileEntry fileEntry) {
		return fileEntry.isRepositoryCapabilityProvided(
			TemporaryFileEntriesCapability.class);
	}

	private JSONObject _queryGCloudVisionJSONObject(
			String apiKey, String payloadJSON)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(
			payloadJSON, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(
			"https://vision.googleapis.com/v1/images:annotate?key=" + apiKey);
		options.setPost(true);

		String responseJSON = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new PortalException(
				StringBundler.concat(
					"Response code ", response.getResponseCode(), ": ",
					responseJSON));
		}

		return _jsonFactory.createJSONObject(responseJSON);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GCloudVisionImageAssetAutoTagProvider.class);

	private static final List<String> _supportedMimeTypes = Arrays.asList(
		ContentTypes.IMAGE_BMP, ContentTypes.IMAGE_GIF, ContentTypes.IMAGE_JPEG,
		ContentTypes.IMAGE_PNG, "image/tiff", "image/webp", "image/x-icon");

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SecretResolver _secretResolver;

}