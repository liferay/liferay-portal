/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.customer.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.customer.exception.FileServerUnavailableException;
import com.liferay.petra.string.StringBundler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URI;
import java.net.URL;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Amos Fong
 */
@Component
public class GoogleCloudStorageService extends BaseService {

	public void deleteObject(String bucketName, String objectName)
		throws Exception {

		try {
			delete(
				"Bearer " + _getAccessToken(), "",
				UriComponentsBuilder.fromUriString(
					getBaseURL() + "/storage/v1/b/{bucketName}/o/{objectName}"
				).build(
					bucketName, objectName
				));
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			throw new FileServerUnavailableException(exception);
		}
	}

	public String getDownloadURL(String bucketName, String objectName)
		throws Exception {

		try (InputStream inputStream = new ByteArrayInputStream(
				_gcsServiceAccountKey.getBytes())) {

			ServiceAccountCredentials serviceAccountCredentials =
				ServiceAccountCredentials.fromStream(inputStream);

			StorageOptions storageOptions = StorageOptions.newBuilder(
			).setCredentials(
				serviceAccountCredentials
			).setProjectId(
				serviceAccountCredentials.getProjectId()
			).build();

			Storage storage = storageOptions.getService();

			URL url = storage.signUrl(
				BlobInfo.newBuilder(
					BlobId.of(bucketName, objectName)
				).build(),
				15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

			return url.toString();
		}
		catch (StorageException storageException) {
			if ((storageException.getCode() == 500) ||
				(storageException.getCode() == 503)) {

				throw new FileServerUnavailableException(storageException);
			}

			throw storageException;
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			throw new FileServerUnavailableException(exception);
		}
	}

	public String getUploadSessionURL(
			String origin, String bucketName, String objectName,
			String fileSize)
		throws Exception {

		try {
			ResponseEntity<String> responseEntity = WebClient.create(
			).post(
			).uri(
				StringBundler.concat(
					getBaseURL(), "/upload/storage/v1/b/", bucketName,
					"/o?uploadType=resumable&name=", objectName)
			).accept(
				MediaType.APPLICATION_JSON
			).header(
				HttpHeaders.AUTHORIZATION, "Bearer " + _getAccessToken()
			).header(
				HttpHeaders.CONTENT_LENGTH, fileSize
			).header(
				HttpHeaders.ORIGIN, origin
			).retrieve(
			).toEntity(
				String.class
			).block();

			if (responseEntity == null) {
				throw new FileServerUnavailableException();
			}

			HttpStatusCode statusCode = responseEntity.getStatusCode();

			if (statusCode.is5xxServerError()) {
				throw new FileServerUnavailableException(
					"GCP server returned error status: " + statusCode, null);
			}

			HttpHeaders httpHeaders = responseEntity.getHeaders();

			URI location = httpHeaders.getLocation();

			if (location == null) {
				throw new FileServerUnavailableException();
			}

			return location.toString();
		}
		catch (WebClientException webClientException) {
			_log.error(webClientException, webClientException);

			throw new FileServerUnavailableException(webClientException);
		}
	}

	protected String getBaseURL() {
		return "https://storage.googleapis.com";
	}

	private String _getAccessToken() throws Exception {
		try (InputStream inputStream = new ByteArrayInputStream(
				_gcsServiceAccountKey.getBytes())) {

			ServiceAccountCredentials serviceAccountCredentials =
				ServiceAccountCredentials.fromStream(inputStream);

			Set<String> scopes = new HashSet<>();

			scopes.add("https://www.googleapis.com/auth/cloud-platform");

			GoogleCredentials googleCredentials =
				serviceAccountCredentials.createScoped(scopes);

			AccessToken accessToken = googleCredentials.refreshAccessToken();

			return accessToken.getTokenValue();
		}
	}

	private static final Log _log = LogFactory.getLog(
		GoogleCloudStorageService.class);

	@Value("${liferay.customer.gcs.service.account.key}")
	private String _gcsServiceAccountKey;

}