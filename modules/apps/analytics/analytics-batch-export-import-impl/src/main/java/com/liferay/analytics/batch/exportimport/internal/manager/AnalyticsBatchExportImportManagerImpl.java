/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.manager;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.analytics.batch.exportimport.manager.AnalyticsBatchExportImportManager;
import com.liferay.analytics.message.storage.service.AnalyticsMessageLocalService;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsDescriptor;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipReaderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(service = AnalyticsBatchExportImportManager.class)
public class AnalyticsBatchExportImportManagerImpl
	implements AnalyticsBatchExportImportManager {

	@Override
	public void exportToAnalyticsCloud(
			List<String> batchEngineExportTaskItemDelegateNames, long companyId,
			UnsafeConsumer<String, Exception> notificationUnsafeConsumer,
			Date resourceLastModifiedDate, String resourceName, long userId)
		throws Exception {

		_notify(
			"Exporting resource " + resourceName, notificationUnsafeConsumer);

		File tempFile = FileUtil.createTempFile();

		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
			new FileOutputStream(tempFile));

		List<BatchEngineExportTask> batchEngineExportTasks = new ArrayList<>();
		boolean skipUpload = true;

		for (String batchEngineExportTaskItemDelegateName :
				batchEngineExportTaskItemDelegateNames) {

			BatchEngineExportTask batchEngineExportTask =
				_batchEngineExportTaskLocalService.addBatchEngineExportTask(
					null, companyId, userId, null, resourceName,
					BatchEngineTaskContentType.JSONL.name(),
					BatchEngineTaskExecuteStatus.INITIAL.name(), null,
					HashMapBuilder.<String, Serializable>put(
						"resourceLastModifiedDate", resourceLastModifiedDate
					).build(),
					batchEngineExportTaskItemDelegateName);

			_batchEngineExportTaskExecutor.execute(batchEngineExportTask);

			batchEngineExportTask =
				_batchEngineExportTaskLocalService.fetchBatchEngineExportTask(
					batchEngineExportTask.getBatchEngineExportTaskId());

			batchEngineExportTasks.add(batchEngineExportTask);

			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
				BatchEngineTaskExecuteStatus.valueOf(
					batchEngineExportTask.getExecuteStatus());

			if (batchEngineTaskExecuteStatus.equals(
					BatchEngineTaskExecuteStatus.COMPLETED)) {

				_notify(
					StringBundler.concat(
						"Exported ", batchEngineExportTask.getTotalItemsCount(),
						" items from task ",
						batchEngineExportTaskItemDelegateName),
					notificationUnsafeConsumer);

				if (batchEngineExportTask.getTotalItemsCount() == 0) {
					_notify(
						"There are no items from task " +
							batchEngineExportTaskItemDelegateName,
						notificationUnsafeConsumer);

					continue;
				}

				skipUpload = false;

				try (ZipInputStream zipInputStream = new ZipInputStream(
						_batchEngineExportTaskLocalService.
							openContentInputStream(
								batchEngineExportTask.
									getBatchEngineExportTaskId()))) {

					zipInputStream.getNextEntry();

					StreamUtil.transfer(
						zipInputStream, gzipOutputStream, false);
				}
			}
			else {
				throw new PortalException(
					"Unable to export resource " +
						batchEngineExportTaskItemDelegateName);
			}
		}

		StreamUtil.cleanUp(gzipOutputStream);

		try {
			if (!skipUpload) {
				_notify(
					"Uploading resource " + resourceName,
					notificationUnsafeConsumer);

				_upload(
					companyId, "gzip", tempFile, resourceLastModifiedDate,
					resourceName);

				_notify(
					"Completed uploading resource " + resourceName,
					notificationUnsafeConsumer);
			}
			else {
				_notify(
					"Skip uploading resource " + resourceName,
					notificationUnsafeConsumer);
			}

			for (BatchEngineExportTask batchEngineExportTask :
					batchEngineExportTasks) {

				_batchEngineExportTaskLocalService.deleteBatchEngineExportTask(
					batchEngineExportTask);
			}
		}
		finally {
			boolean deleted = tempFile.delete();

			if (_log.isDebugEnabled()) {
				if (deleted) {
					_log.debug("Deleted temp file " + tempFile.getName());
				}
				else {
					_log.debug(
						"Unable to delete temp file " + tempFile.getName());
				}
			}
		}
	}

	@Override
	public void exportToAnalyticsCloud(
			String batchEngineExportTaskItemDelegateName, long companyId,
			List<String> fieldNames, String filterString,
			UnsafeConsumer<String, Exception> notificationUnsafeConsumer,
			Date resourceLastModifiedDate, String resourceName, long userId)
		throws Exception {

		_notify(
			"Exporting resource " + resourceName, notificationUnsafeConsumer);

		Map<String, Serializable> parameters = new HashMap<>();

		if (resourceLastModifiedDate != null) {
			parameters.put(
				"filter",
				StringBundler.concat(
					Field.getSortableFieldName(Field.MODIFIED_DATE), " ge ",
					resourceLastModifiedDate.getTime()));
		}

		if (Validator.isNotNull(filterString)) {
			if (resourceLastModifiedDate != null) {
				parameters.put(
					"filter",
					StringBundler.concat(
						"(", parameters.get("filter"), ") and (", filterString,
						")"));
			}
			else {
				parameters.put("filter", filterString);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Filtering by: " + parameters.get("filter"));
		}

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.addBatchEngineExportTask(
				null, companyId, userId, null, resourceName,
				BatchEngineTaskContentType.JSONL.name(),
				BatchEngineTaskExecuteStatus.INITIAL.name(), fieldNames,
				parameters, batchEngineExportTaskItemDelegateName);

		_batchEngineExportTaskExecutor.execute(batchEngineExportTask);

		batchEngineExportTask =
			_batchEngineExportTaskLocalService.fetchBatchEngineExportTask(
				batchEngineExportTask.getBatchEngineExportTaskId());

		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
			BatchEngineTaskExecuteStatus.valueOf(
				batchEngineExportTask.getExecuteStatus());

		if (batchEngineTaskExecuteStatus.equals(
				BatchEngineTaskExecuteStatus.COMPLETED)) {

			_notify(
				StringBundler.concat(
					"Exported ", batchEngineExportTask.getTotalItemsCount(),
					" items for resource ", resourceName),
				notificationUnsafeConsumer);

			if (batchEngineExportTask.getTotalItemsCount() == 0) {
				_notify(
					"There are no items to upload", notificationUnsafeConsumer);

				return;
			}

			_notify(
				"Uploading resource " + resourceName,
				notificationUnsafeConsumer);

			File tempFile = FileUtil.createTempFile();

			try {
				try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
						new FileOutputStream(tempFile));
					ZipInputStream zipInputStream = new ZipInputStream(
						_batchEngineExportTaskLocalService.
							openContentInputStream(
								batchEngineExportTask.
									getBatchEngineExportTaskId()))) {

					zipInputStream.getNextEntry();

					StreamUtil.transfer(
						zipInputStream, gzipOutputStream, false);
				}

				_upload(
					companyId, "gzip", tempFile, resourceLastModifiedDate,
					resourceName);

				_batchEngineExportTaskLocalService.deleteBatchEngineExportTask(
					batchEngineExportTask);
			}
			finally {
				boolean deleted = tempFile.delete();

				if (_log.isDebugEnabled()) {
					if (deleted) {
						_log.debug("Deleted temp file " + tempFile.getName());
					}
					else {
						_log.debug(
							"Unable to delete temp file " + tempFile.getName());
					}
				}
			}

			_notify(
				"Completed uploading resource " + resourceName,
				notificationUnsafeConsumer);
		}
		else {
			throw new PortalException(
				"Unable to export resource " + resourceName);
		}
	}

	@Override
	public void importFromAnalyticsCloud(
			String batchEngineImportTaskItemDelegateName, long companyId,
			Map<String, String> fieldMapping,
			UnsafeConsumer<String, Exception> notificationUnsafeConsumer,
			Date resourceLastModifiedDate, String resourceName, long userId)
		throws Exception {

		_notify(
			"Checking modifications for resource " + resourceName,
			notificationUnsafeConsumer);

		File resourceFile = _download(
			companyId, resourceLastModifiedDate, resourceName);

		if (resourceFile == null) {
			_notify(
				"There are no modifications for resource " + resourceName,
				notificationUnsafeConsumer);

			return;
		}

		_notify(
			"Importing resource " + resourceName, notificationUnsafeConsumer);

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, companyId, userId, 50, null, resourceName,
				Files.readAllBytes(resourceFile.toPath()),
				BatchEngineTaskContentType.JSONL.name(),
				BatchEngineTaskExecuteStatus.INITIAL.name(), fieldMapping,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), null,
				batchEngineImportTaskItemDelegateName);

		_batchEngineImportTaskExecutor.execute(batchEngineImportTask);

		batchEngineImportTask =
			_batchEngineImportTaskLocalService.fetchBatchEngineImportTask(
				batchEngineImportTask.getBatchEngineImportTaskId());

		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
			BatchEngineTaskExecuteStatus.valueOf(
				batchEngineImportTask.getExecuteStatus());

		if (batchEngineTaskExecuteStatus.equals(
				BatchEngineTaskExecuteStatus.COMPLETED)) {

			_notify(
				StringBundler.concat(
					"Imported ", batchEngineImportTask.getTotalItemsCount(),
					" items for resource ", resourceName),
				notificationUnsafeConsumer);

			_batchEngineImportTaskLocalService.deleteBatchEngineImportTask(
				batchEngineImportTask);
		}
		else {
			throw new PortalException(
				"Unable to import resource " + resourceName);
		}
	}

	@Override
	public void validateConnection(long companyId) throws Exception {
		if (!_isEnabled(companyId)) {
			return;
		}

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		_checkEndpoints(analyticsConfiguration, companyId);
		_checkOAuth2Application(analyticsConfiguration, companyId);

		HttpUriRequest httpUriRequest = _buildHttpUriRequest(
			null, analyticsConfiguration.liferayAnalyticsDataSourceId(),
			analyticsConfiguration.
				liferayAnalyticsFaroBackendSecuritySignature(),
			HttpMethods.GET, analyticsConfiguration.liferayAnalyticsProjectId(),
			analyticsConfiguration.liferayAnalyticsFaroBackendURL() +
				"/api/1.0/data-sources/" +
					analyticsConfiguration.liferayAnalyticsDataSourceId());

		_execute(analyticsConfiguration, companyId, httpUriRequest);
	}

	@Reference
	protected BatchEngineExportTaskExecutor batchEngineExportTaskExecutor;

	@Reference
	protected ZipReaderFactory zipReaderFactory;

	private HttpUriRequest _buildHttpUriRequest(
		String body, String dataSourceId, String faroBackendSecuritySignature,
		String method, String projectId, String url) {

		HttpUriRequest httpUriRequest = null;

		if (method.equals(HttpMethods.GET)) {
			httpUriRequest = new HttpGet(url);
		}
		else if (method.equals(HttpMethods.POST)) {
			HttpPost httpPost = new HttpPost(url);

			if (Validator.isNotNull(body)) {
				httpPost.setEntity(
					new StringEntity(body, StandardCharsets.UTF_8));
			}

			httpUriRequest = httpPost;
		}

		if (httpUriRequest != null) {
			httpUriRequest.setHeader("Content-Type", "application/json");
			httpUriRequest.setHeader("OSB-Asah-Data-Source-ID", dataSourceId);
			httpUriRequest.setHeader(
				"OSB-Asah-Faro-Backend-Security-Signature",
				faroBackendSecuritySignature);
			httpUriRequest.setHeader("OSB-Asah-Project-ID", projectId);
		}

		return httpUriRequest;
	}

	private void _checkCompany(long companyId) {
		if (_analyticsConfigurationRegistry.isActive()) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Analytics configuration tracker is inactive");
		}

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		if (Validator.isNotNull(
				analyticsConfiguration.liferayAnalyticsEndpointURL())) {

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Analytics configuration endpoint URL is null");
		}

		throw new IllegalStateException(
			"Analytics batch export/import is disabled");
	}

	private void _checkEndpoints(
			AnalyticsConfiguration analyticsConfiguration, long companyId)
		throws Exception {

		HttpGet httpGet = new HttpGet(
			analyticsConfiguration.liferayAnalyticsURL() + "/endpoints/" +
				analyticsConfiguration.liferayAnalyticsProjectId());

		try (CloseableHttpClient closeableHttpClient = _getCloseableHttpClient(
				false)) {

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpGet);

			JSONObject responseJSONObject = null;

			try {
				responseJSONObject = _jsonFactory.createJSONObject(
					EntityUtils.toString(
						closeableHttpResponse.getEntity(),
						Charset.defaultCharset()));
			}
			catch (Exception exception) {
				_log.error(
					"Unable to check Analytics Cloud endpoints", exception);

				return;
			}

			String liferayAnalyticsEndpointURL = responseJSONObject.getString(
				"liferayAnalyticsEndpointURL");
			String liferayAnalyticsFaroBackendURL =
				responseJSONObject.getString("liferayAnalyticsFaroBackendURL");

			if (liferayAnalyticsEndpointURL.equals(
					analyticsConfiguration.liferayAnalyticsEndpointURL()) &&
				liferayAnalyticsFaroBackendURL.equals(
					analyticsConfiguration.liferayAnalyticsFaroBackendURL())) {

				return;
			}

			_companyLocalService.updatePreferences(
				companyId,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"liferayAnalyticsEndpointURL", liferayAnalyticsEndpointURL
				).put(
					"liferayAnalyticsFaroBackendURL",
					liferayAnalyticsFaroBackendURL
				).build());

			Dictionary<String, Object> configurationProperties =
				_getConfigurationProperties(companyId);

			configurationProperties.put(
				"liferayAnalyticsEndpointURL", liferayAnalyticsEndpointURL);

			_configurationProvider.saveCompanyConfiguration(
				AnalyticsConfiguration.class, companyId,
				configurationProperties);
		}
	}

	private void _checkOAuth2Application(
			AnalyticsConfiguration analyticsConfiguration, long companyId)
		throws Exception {

		if (StringUtil.equals(
				analyticsConfiguration.liferayAnalyticsCredentialType(),
				"OAuth 2 Authentication")) {

			return;
		}

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", companyId);

		if (oAuth2Application == null) {
			throw new Exception(
				"No OAuth 2 application found for ANALYTICS-CLOUD");
		}

		Http.Options options = new Http.Options();

		options.addPart("oAuthClientId", oAuth2Application.getClientId());
		options.addPart(
			"oAuthClientSecret",
			_oAuth2ApplicationLocalService.resolveClientSecret(
				oAuth2Application));

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			new String(Base64.decode(analyticsConfiguration.token())));

		options.setLocation(
			StringUtil.replace(
				jsonObject.getString("url"), "data_source/connect",
				"data_source/" +
					analyticsConfiguration.liferayAnalyticsDataSourceId()));

		options.setPost(true);

		_http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
			_analyticsSettingsManager.updateCompanyConfiguration(
				companyId,
				Collections.singletonMap(
					"liferayAnalyticsCredentialType",
					"OAuth 2 Authentication"));
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to update analytics data source");
			}
		}
	}

	private File _download(
		long companyId, Date resourceLastModifiedDate, String resourceName) {

		_checkCompany(companyId);

		Http.Options options = _getOptions(companyId);

		if (resourceLastModifiedDate != null) {
			options.addHeader(
				"If-Modified-Since", _format.format(resourceLastModifiedDate));
		}

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		options.setLocation(
			HttpComponentsUtil.addParameter(
				analyticsConfiguration.liferayAnalyticsEndpointURL() +
					"/dxp-batch-entities",
				"resourceName", resourceName));

		try (InputStream inputStream = _http.URLtoInputStream(options)) {
			Http.Response response = options.getResponse();

			if (response.getResponseCode() ==
					HttpURLConnection.HTTP_FORBIDDEN) {

				JSONObject responseJSONObject = _jsonFactory.createJSONObject(
					StringUtil.read(inputStream));

				boolean disconnected = StringUtil.equals(
					GetterUtil.getString(responseJSONObject.getString("state")),
					"DISCONNECTED");

				_processInvalidTokenMessage(
					analyticsConfiguration, companyId, disconnected,
					responseJSONObject.getString("message"));
			}
			else if (response.getResponseCode() >=
						HttpURLConnection.HTTP_BAD_REQUEST) {

				throw new RuntimeException(
					"Server response code: " + response.getResponseCode());
			}

			if (inputStream != null) {
				return _file.createTempFile(inputStream);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return null;
	}

	private CloseableHttpResponse _execute(
			AnalyticsConfiguration analyticsConfiguration, long companyId,
			HttpUriRequest httpUriRequest)
		throws Exception {

		try (CloseableHttpClient closeableHttpClient = _getCloseableHttpClient(
				false)) {

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpUriRequest);

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			JSONObject responseJSONObject = _jsonFactory.createJSONObject(
				EntityUtils.toString(
					closeableHttpResponse.getEntity(),
					Charset.defaultCharset()));

			boolean disconnected = StringUtil.equals(
				GetterUtil.getString(responseJSONObject.getString("state")),
				"DISCONNECTED");

			if ((statusLine.getStatusCode() != HttpStatus.SC_FORBIDDEN) &&
				!disconnected) {

				return closeableHttpResponse;
			}

			_processInvalidTokenMessage(
				analyticsConfiguration, companyId, disconnected,
				responseJSONObject.getString("message"));

			return closeableHttpResponse;
		}
		catch (UnknownHostException unknownHostException) {
			_checkEndpoints(analyticsConfiguration, companyId);

			throw unknownHostException;
		}
	}

	private CloseableHttpClient _getCloseableHttpClient(
		boolean disableAutomaticRetries) {

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		if (disableAutomaticRetries) {
			httpClientBuilder.disableAutomaticRetries();

			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

			requestConfigBuilder.setConnectionRequestTimeout(60000);
			requestConfigBuilder.setConnectTimeout(30000);
			requestConfigBuilder.setSocketTimeout(600000);

			httpClientBuilder.setDefaultRequestConfig(
				requestConfigBuilder.build());
		}

		httpClientBuilder.useSystemProperties();

		return httpClientBuilder.build();
	}

	private Dictionary<String, Object> _getConfigurationProperties(
			long companyId)
		throws Exception {

		Dictionary<String, Object> configurationProperties = new Hashtable<>();

		Class<?> clazz = AnalyticsConfiguration.class;

		Meta.OCD ocd = clazz.getAnnotation(Meta.OCD.class);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new CompanyServiceSettingsLocator(companyId, ocd.id()));

		SettingsDescriptor settingsDescriptor =
			_settingsLocatorHelper.getSettingsDescriptor(ocd.id());

		if (settingsDescriptor == null) {
			return configurationProperties;
		}

		Set<String> multiValuedKeys = settingsDescriptor.getMultiValuedKeys();

		for (String multiValuedKey : multiValuedKeys) {
			configurationProperties.put(
				multiValuedKey,
				settings.getValues(multiValuedKey, new String[0]));
		}

		Set<String> keys = settingsDescriptor.getAllKeys();

		keys.removeAll(multiValuedKeys);

		for (String key : keys) {
			configurationProperties.put(
				key, settings.getValue(key, StringPool.BLANK));
		}

		return configurationProperties;
	}

	private Http.Options _getOptions(long companyId) {
		Http.Options options = new Http.Options();

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		options.addHeader(
			"OSB-Asah-Data-Source-ID",
			analyticsConfiguration.liferayAnalyticsDataSourceId());
		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			analyticsConfiguration.
				liferayAnalyticsFaroBackendSecuritySignature());
		options.addHeader(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId());

		return options;
	}

	private boolean _isEnabled(long companyId) {
		if (!_analyticsConfigurationRegistry.isActive()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Analytics configuration tracker not active");
			}

			return false;
		}

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		if (analyticsConfiguration.liferayAnalyticsEndpointURL() == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Analytics endpoint URL null");
			}

			return false;
		}

		return true;
	}

	private void _notify(
			String message,
			UnsafeConsumer<String, Exception> notificationUnsafeConsumer)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(message);
		}

		if (notificationUnsafeConsumer == null) {
			return;
		}

		notificationUnsafeConsumer.accept(message);
	}

	private void _processInvalidTokenMessage(
		AnalyticsConfiguration analyticsConfiguration, long companyId,
		boolean disconnected, String message) {

		if (!Objects.equals(message, "INVALID_TOKEN") && !disconnected) {
			return;
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Disconnecting data source for company ", companyId, ": ",
					message));
		}

		try {
			String[] groupIds = analyticsConfiguration.syncedGroupIds();

			if (ArrayUtil.isNotEmpty(groupIds)) {
				for (String groupId : groupIds) {
					Group group = _groupLocalService.fetchGroup(
						GetterUtil.getLong(groupId));

					if (group == null) {
						continue;
					}

					UnicodeProperties typeSettingsUnicodeProperties =
						group.getTypeSettingsProperties();

					typeSettingsUnicodeProperties.remove("analyticsChannelId");

					group.setTypeSettingsProperties(
						typeSettingsUnicodeProperties);

					_groupLocalService.updateGroup(group);
				}
			}

			_companyLocalService.updatePreferences(
				companyId,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"liferayAnalyticsConnectionType", ""
				).put(
					"liferayAnalyticsDataSourceId", ""
				).put(
					"liferayAnalyticsEndpointURL", ""
				).put(
					"liferayAnalyticsFaroBackendSecuritySignature", ""
				).put(
					"liferayAnalyticsFaroBackendURL", ""
				).put(
					"liferayAnalyticsGroupIds", ""
				).put(
					"liferayAnalyticsProjectId", ""
				).put(
					"liferayAnalyticsURL", ""
				).build());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to remove analytics preferences for company " +
						companyId,
					exception);
			}
		}

		try {
			_configurationProvider.deleteCompanyConfiguration(
				AnalyticsConfiguration.class, companyId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to remove analytics configuration for company " +
						companyId,
					exception);
			}
		}

		_analyticsMessageLocalService.deleteAnalyticsMessages(companyId);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Deleted all analytics messages for company " + companyId);
		}
	}

	private int _upload(
			AnalyticsConfiguration analyticsConfiguration, int attempt,
			String boundary, long companyId, String contentEncoding,
			File multipartFile, String resourceName)
		throws Exception {

		HttpPost httpPost = new HttpPost(
			analyticsConfiguration.liferayAnalyticsEndpointURL() +
				"/dxp-batch-entities");

		httpPost.setEntity(new FileEntity(multipartFile));
		httpPost.setHeader(HttpHeaders.CONTENT_ENCODING, contentEncoding);
		httpPost.setHeader(
			HttpHeaders.CONTENT_TYPE,
			ContentTypes.MULTIPART_FORM_DATA + "; boundary=" + boundary);
		httpPost.setHeader(
			"OSB-Asah-Data-Source-ID",
			analyticsConfiguration.liferayAnalyticsDataSourceId());
		httpPost.setHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			analyticsConfiguration.
				liferayAnalyticsFaroBackendSecuritySignature());
		httpPost.setHeader(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId());

		try (CloseableHttpClient closeableHttpClient = _getCloseableHttpClient(
				true);

			CloseableHttpResponse closeableHttpResponse =
				closeableHttpClient.execute(httpPost)) {

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			String responseBody = StringPool.BLANK;

			try {
				responseBody = EntityUtils.toString(
					closeableHttpResponse.getEntity(),
					Charset.defaultCharset());
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to read upload response body for ",
						resourceName, " (HTTP ", statusCode, ")"),
					exception);
			}

			if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
				JSONObject responseJSONObject = _jsonFactory.createJSONObject(
					responseBody);

				boolean disconnected = StringUtil.equals(
					GetterUtil.getString(responseJSONObject.getString("state")),
					"DISCONNECTED");

				_processInvalidTokenMessage(
					analyticsConfiguration, companyId, disconnected,
					responseJSONObject.getString("message"));
			}

			if ((statusCode >= 200) && (statusCode < 300)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Upload completed successfully on attempt " +
							(attempt + 1));
				}

				return statusCode;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Upload of ", resourceName, " returned HTTP ",
						statusCode, " on attempt ", attempt + 1, ": ",
						responseBody));
			}

			if ((statusCode != 400) && (statusCode != 408) &&
				(statusCode != 429) && (statusCode < 500)) {

				throw new RuntimeException(
					"Upload failed with HTTP response code: " + statusCode);
			}

			return statusCode;
		}
	}

	private void _upload(
		long companyId, String contentEncoding, File file,
		Date resourceLastModifiedDate, String resourceName) {

		_checkCompany(companyId);

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsConfigurationRegistry.getAnalyticsConfiguration(
				companyId);

		int lastStatusCode = -1;
		int retryCount = 3;
		long[] retryDelays = {5000, 15000};

		for (int attempt = 0; attempt < retryCount; attempt++) {
			if (attempt > 0) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Retrying upload of ", resourceName, " (attempt ",
							attempt + 1, "/", retryCount, ")"));
				}

				int retryDelayIndex = Math.min(
					attempt - 1, retryDelays.length - 1);

				try {
					Thread.sleep(retryDelays[retryDelayIndex]);
				}
				catch (InterruptedException interruptedException) {
					Thread thread = Thread.currentThread();

					thread.interrupt();

					throw new RuntimeException(
						"Upload retry interrupted", interruptedException);
				}
			}

			File multipartFile = null;

			try {
				String boundary = StringUtil.removeSubstring(
					String.valueOf(UUID.randomUUID()), "-");

				multipartFile = _writeMultipartFile(
					boundary, file, resourceName,
					(resourceLastModifiedDate != null) ? "INCREMENTAL" :
						"FULL");

				int statusCode = _upload(
					analyticsConfiguration, attempt, boundary, companyId,
					contentEncoding, multipartFile, resourceName);

				if ((statusCode >= 200) && (statusCode < 300)) {
					return;
				}

				lastStatusCode = statusCode;
			}
			catch (IOException ioException) {
				if (attempt == (retryCount - 1)) {
					throw new RuntimeException(
						"Upload failed after " + retryCount + " attempts",
						ioException);
				}

				_log.error(
					StringBundler.concat(
						"Transport failure on upload attempt ", attempt + 1,
						": ", ioException.getMessage()));
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
			finally {
				if (multipartFile != null) {
					multipartFile.delete();
				}
			}
		}

		throw new RuntimeException(
			StringBundler.concat(
				"Upload failed after ", retryCount,
				" attempts with HTTP response code: ", lastStatusCode));
	}

	private File _writeMultipartFile(
			String boundary, File file, String resourceName, String uploadType)
		throws Exception {

		File tempFile = FileUtil.createTempFile();

		try (OutputStream outputStream = new FileOutputStream(tempFile)) {
			String filePartHeader = StringBundler.concat(
				"--", boundary, "\r\n",
				"Content-Disposition: form-data; name=\"file\"; filename=\"",
				resourceName, "\"\r\n", "Content-Type: ",
				ContentTypes.MULTIPART_FORM_DATA, "\r\n\r\n");

			outputStream.write(
				filePartHeader.getBytes(StandardCharsets.US_ASCII));

			Files.copy(file.toPath(), outputStream);

			String uploadTypePart = StringBundler.concat(
				"\r\n--", boundary, "\r\n", "Content-Disposition: form-data; ",
				"name=\"uploadType\"\r\n\r\n", uploadType);

			outputStream.write(
				uploadTypePart.getBytes(StandardCharsets.US_ASCII));

			String closingBoundary = "\r\n--" + boundary + "--\r\n";

			outputStream.write(
				closingBoundary.getBytes(StandardCharsets.US_ASCII));
		}
		catch (Exception exception) {
			tempFile.delete();

			throw exception;
		}

		return tempFile;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsBatchExportImportManagerImpl.class);

	private static final Format _format =
		FastDateFormatFactoryUtil.getSimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz");

	@Reference
	private AnalyticsConfigurationRegistry _analyticsConfigurationRegistry;

	@Reference
	private AnalyticsMessageLocalService _analyticsMessageLocalService;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;

	@Reference
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Reference
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Reference
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}