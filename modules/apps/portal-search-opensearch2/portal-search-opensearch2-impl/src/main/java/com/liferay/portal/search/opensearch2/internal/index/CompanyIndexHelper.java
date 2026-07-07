/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionNotInitializedException;
import com.liferay.portal.search.opensearch2.internal.index.constants.IndexSettingsConstants;
import com.liferay.portal.search.opensearch2.internal.index.util.IndexFactoryCompanyIdRegistryUtil;
import com.liferay.portal.search.opensearch2.internal.settings.SettingsHelperImpl;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;
import com.liferay.portal.search.opensearch2.internal.util.ResourceUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexConfigurationContributor;
import com.liferay.portal.search.spi.index.listener.CompanyIndexListener;

import jakarta.json.spi.JsonProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CloseIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.OpenRequest;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.transport.endpoints.BooleanResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 * @author Petteri Karttunen
 */
@Component(service = CompanyIndexHelper.class)
public class CompanyIndexHelper {

	public void createIndex(
		long companyId, String indexName,
		OpenSearchIndicesClient openSearchIndicesClient) {

		CreateIndexRequest.Builder builder = new CreateIndexRequest.Builder(
		).index(
			indexName
		);

		_setSettings(companyId, builder);

		MappingsHelperImpl mappingsHelperImpl = new MappingsHelperImpl(
			indexName, _jsonFactory, openSearchIndicesClient,
			_openSearchConfigurationWrapper.overrideTypeMappings(),
			_searchEngineInformation);

		mappingsHelperImpl.setDefaultOrOverrideMappings(
			builder, _openSearchConnectionManager.getJsonpMapper(null));

		try {
			CreateIndexResponse createIndexResponse =
				openSearchIndicesClient.create(builder.build());

			JsonpUtil.logInfoResponse(createIndexResponse, _log);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		_putNondefaultMappings(companyId, mappingsHelperImpl);

		_executeCompanyIndexListenersAfterCreate(indexName);
	}

	public void deleteIndex(
		long companyId, String indexName,
		OpenSearchIndicesClient openSearchIndicesClient,
		boolean resetBothIndexNames) {

		_executeCompanyIndexListenersBeforeDelete(indexName);

		try {
			JsonpUtil.logInfoResponse(
				openSearchIndicesClient.delete(
					DeleteIndexRequest.of(
						deleteIndexRequest -> deleteIndexRequest.index(
							indexName))),
				_log);

			if (companyId != CompanyConstants.SYSTEM) {
				if (resetBothIndexNames) {
					_companyLocalService.updateIndexNames(
						companyId, null, null);
				}
				else {
					_companyLocalService.updateIndexNameNext(companyId, null);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to update company index names", portalException);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public String getIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId);
	}

	public boolean hasIndex(
		String indexName, OpenSearchIndicesClient openSearchIndicesClient) {

		try {
			BooleanResponse booleanResponse = openSearchIndicesClient.exists(
				ExistsRequest.of(
					existRequest -> existRequest.index(indexName)));

			return booleanResponse.value();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public void updateIndex(
		long companyId, String indexName,
		OpenSearchIndicesClient openSearchIndicesClient) {

		_updateSettings(companyId, indexName, openSearchIndicesClient);

		MappingsHelperImpl mappingsHelperImpl = new MappingsHelperImpl(
			indexName, _jsonFactory, openSearchIndicesClient,
			_openSearchConfigurationWrapper.overrideTypeMappings(),
			_searchEngineInformation);

		mappingsHelperImpl.putDefaultOrOverrideMappings();

		_putNondefaultMappings(companyId, mappingsHelperImpl);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_companyIndexListenerServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, CompanyIndexListener.class);

		_companyIndexConfigurationContributorServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, CompanyIndexConfigurationContributor.class, null,
				new EagerServiceTrackerCustomizer
					<CompanyIndexConfigurationContributor,
					 CompanyIndexConfigurationContributor>() {

					@Override
					public CompanyIndexConfigurationContributor addingService(
						ServiceReference<CompanyIndexConfigurationContributor>
							serviceReference) {

						CompanyIndexConfigurationContributor
							companyIndexConfigurationContributor =
								bundleContext.getService(serviceReference);

						_processCompanyIndexConfigurationContributor(
							companyIndexConfigurationContributor);

						return companyIndexConfigurationContributor;
					}

					@Override
					public void modifiedService(
						ServiceReference<CompanyIndexConfigurationContributor>
							serviceReference,
						CompanyIndexConfigurationContributor
							companyIndexConfigurationContributor) {
					}

					@Override
					public void removedService(
						ServiceReference<CompanyIndexConfigurationContributor>
							serviceReference,
						CompanyIndexConfigurationContributor
							companyIndexConfigurationContributor) {

						bundleContext.ungetService(serviceReference);
					}

				});
	}

	@Deactivate
	protected void deactivate() {
		if (_companyIndexListenerServiceTrackerList != null) {
			_companyIndexListenerServiceTrackerList.close();
		}

		if (_companyIndexConfigurationContributorServiceTrackerList != null) {
			_companyIndexConfigurationContributorServiceTrackerList.close();
		}
	}

	private PutIndicesSettingsRequest _buildPutIndicesSettingsRequest(
		String indexName, String settings) {

		PutIndicesSettingsRequest.Builder builder =
			new PutIndicesSettingsRequest.Builder(
			).index(
				indexName
			);

		JsonpMapper jsonpMapper = _openSearchConnectionManager.getJsonpMapper(
			null);

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		try (InputStream inputStream = new ByteArrayInputStream(
				settings.getBytes(StandardCharsets.UTF_8))) {

			builder.settings(
				IndexSettings._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return builder.build();
	}

	private void _executeCompanyIndexListenerAfterCreate(
		CompanyIndexListener companyIndexListener, String indexName) {

		try {
			companyIndexListener.onAfterCreate(indexName);
		}
		catch (Throwable throwable) {
			_log.error(
				StringBundler.concat(
					"Unable to apply listener ", companyIndexListener,
					" after creating index ", indexName),
				throwable);
		}
	}

	private void _executeCompanyIndexListenerBeforeDelete(
		CompanyIndexListener companyIndexListener, String indexName) {

		try {
			companyIndexListener.onBeforeDelete(indexName);
		}
		catch (Throwable throwable) {
			_log.error(
				StringBundler.concat(
					"Unable to apply listener ", companyIndexListener,
					" before deleting index ", indexName),
				throwable);
		}
	}

	private void _executeCompanyIndexListenersAfterCreate(String indexName) {
		for (CompanyIndexListener companyIndexListener :
				_companyIndexListenerServiceTrackerList) {

			_executeCompanyIndexListenerAfterCreate(
				companyIndexListener, indexName);
		}
	}

	private void _executeCompanyIndexListenersBeforeDelete(String indexName) {
		for (CompanyIndexListener companyIndexListener :
				_companyIndexListenerServiceTrackerList) {

			_executeCompanyIndexListenerBeforeDelete(
				companyIndexListener, indexName);
		}
	}

	private void _loadAdditionalSettings(
		long companyId, SettingsHelperImpl settingsHelperImpl,
		boolean includeStaticProperties) {

		_loadContributedSettings(companyId, settingsHelperImpl);

		_loadConfigurationSettings(settingsHelperImpl, includeStaticProperties);

		if (Validator.isNotNull(
				settingsHelperImpl.get("index.number_of_replicas"))) {

			settingsHelperImpl.put("index.auto_expand_replicas", false);
		}
	}

	private void _loadConfigurationSettings(
		SettingsHelperImpl settingsHelperImpl,
		boolean includeStaticProperties) {

		settingsHelperImpl.put(
			"index.max_result_window",
			String.valueOf(
				_openSearchConfigurationWrapper.indexMaxResultWindow()));
		settingsHelperImpl.put(
			"index.number_of_replicas",
			_openSearchConfigurationWrapper.indexNumberOfReplicas());

		if (includeStaticProperties) {
			settingsHelperImpl.put(
				"index.number_of_shards",
				_openSearchConfigurationWrapper.indexNumberOfShards());
		}

		settingsHelperImpl.loadFromSource(
			_openSearchConfigurationWrapper.additionalIndexConfigurations());
	}

	private void _loadContributedSettings(
		long companyId, SettingsHelperImpl settingsHelperImpl) {

		for (CompanyIndexConfigurationContributor
				companyIndexConfigurationContributor :
					_companyIndexConfigurationContributorServiceTrackerList) {

			companyIndexConfigurationContributor.contributeSettings(
				companyId, settingsHelperImpl);
		}
	}

	private void _loadDefaultSettings(SettingsHelperImpl settingsHelperImpl) {
		settingsHelperImpl.loadFromSource(
			ResourceUtil.getResourceAsString(
				getClass(), IndexSettingsConstants.INDEX_SETTINGS_FILE_NAME));
	}

	private void _loadTestModeSettings(SettingsHelperImpl settingsHelperImpl) {
		if (!PortalRunMode.isTestMode()) {
			return;
		}

		settingsHelperImpl.put("index.refresh_interval", "1ms");
		settingsHelperImpl.put(
			"index.search.slowlog.threshold.fetch.warn", "-1");
		settingsHelperImpl.put(
			"index.search.slowlog.threshold.query.warn", "-1");
		settingsHelperImpl.put("index.translog.sync_interval", "100ms");
	}

	private void _processCompanyIndexConfigurationContributor(
		CompanyIndexConfigurationContributor
			companyIndexConfigurationContributor) {

		OpenSearchClient openSearchClient = null;

		try {
			openSearchClient =
				_openSearchConnectionManager.getOpenSearchClient();
		}
		catch (OpenSearchConnectionNotInitializedException
					openSearchConnectionNotInitializedException) {

			_log.error(openSearchConnectionNotInitializedException);

			return;
		}

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			String indexName = getIndexName(companyId);

			SettingsHelperImpl settingsHelperImpl = new SettingsHelperImpl();

			companyIndexConfigurationContributor.contributeSettings(
				companyId, settingsHelperImpl);

			JSONObject settingsJSONObject =
				settingsHelperImpl.getSettingsJSONObject();

			if (SetUtil.isNotEmpty(settingsJSONObject.keySet())) {
				try {
					openSearchIndicesClient.close(
						CloseIndexRequest.of(
							closeIndexRequest -> closeIndexRequest.index(
								indexName)));

					openSearchIndicesClient.putSettings(
						_buildPutIndicesSettingsRequest(
							indexName, settingsJSONObject.toString()));

					openSearchIndicesClient.open(
						OpenRequest.of(
							openRequest -> openRequest.index(indexName)));
				}
				catch (Exception exception) {
					_log.error(
						StringBundler.concat(
							"Unable to put settings for index ", indexName,
							" with contributor ",
							companyIndexConfigurationContributor),
						exception);
				}
			}

			companyIndexConfigurationContributor.contributeMappings(
				companyId,
				new MappingsHelperImpl(
					indexName, _jsonFactory, openSearchIndicesClient,
					_openSearchConfigurationWrapper.overrideTypeMappings(),
					_searchEngineInformation));
		}
	}

	private void _putAdditionalMappings(MappingsHelperImpl mappingsHelperImpl) {
		if (Validator.isNull(
				_openSearchConfigurationWrapper.additionalTypeMappings())) {

			return;
		}

		mappingsHelperImpl.putMappings(
			_openSearchConfigurationWrapper.additionalTypeMappings());
	}

	private void _putContributedMappings(
		long companyId, MappingsHelperImpl mappingsHelperImpl) {

		for (CompanyIndexConfigurationContributor
				companyIndexConfigurationContributor :
					_companyIndexConfigurationContributorServiceTrackerList) {

			companyIndexConfigurationContributor.contributeMappings(
				companyId, mappingsHelperImpl);
		}
	}

	private void _putNondefaultMappings(
		long companyId, MappingsHelperImpl mappingsHelperImpl) {

		if (Validator.isNotNull(
				_openSearchConfigurationWrapper.overrideTypeMappings())) {

			return;
		}

		_putContributedMappings(companyId, mappingsHelperImpl);

		_putAdditionalMappings(mappingsHelperImpl);
	}

	private void _setSettings(
		CreateIndexRequest.Builder builder, String settings) {

		JsonpMapper jsonpMapper = _openSearchConnectionManager.getJsonpMapper(
			null);

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		try (InputStream inputStream = new ByteArrayInputStream(
				settings.getBytes(StandardCharsets.UTF_8))) {

			builder.settings(
				IndexSettings._DESERIALIZER.deserialize(
					jsonProvider.createParser(inputStream), jsonpMapper));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _setSettings(
		long companyId, CreateIndexRequest.Builder builder) {

		SettingsHelperImpl settingsHelperImpl = new SettingsHelperImpl();

		_loadDefaultSettings(settingsHelperImpl);

		_loadTestModeSettings(settingsHelperImpl);

		_loadAdditionalSettings(companyId, settingsHelperImpl, true);

		_setSettings(
			builder,
			String.valueOf(settingsHelperImpl.getSettingsJSONObject()));
	}

	private void _updateSettings(
		long companyId, String indexName,
		OpenSearchIndicesClient openSearchIndicesClient) {

		SettingsHelperImpl settingsHelperImpl = new SettingsHelperImpl();

		_loadDefaultSettings(settingsHelperImpl);

		_loadAdditionalSettings(companyId, settingsHelperImpl, false);

		JSONObject settingsJSONObject =
			settingsHelperImpl.getSettingsJSONObject();

		try {
			openSearchIndicesClient.close(
				CloseIndexRequest.of(
					closeIndexRequest -> closeIndexRequest.index(indexName)));

			openSearchIndicesClient.putSettings(
				_buildPutIndicesSettingsRequest(
					indexName, settingsJSONObject.toString()));

			openSearchIndicesClient.open(
				OpenRequest.of(openRequest -> openRequest.index(indexName)));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to put settings for index " + indexName, exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyIndexHelper.class);

	private ServiceTrackerList<CompanyIndexConfigurationContributor>
		_companyIndexConfigurationContributorServiceTrackerList;
	private ServiceTrackerList<CompanyIndexListener>
		_companyIndexListenerServiceTrackerList;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OpenSearchConfigurationWrapper _openSearchConfigurationWrapper;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

	@Reference
	private SearchEngineInformation _searchEngineInformation;

}