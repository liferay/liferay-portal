/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.io.unsync.UnsyncStringReader;
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
import com.liferay.portal.search.opensearch2.internal.settings.SettingsHelper;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;
import com.liferay.portal.search.opensearch2.internal.util.ResourceUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexMappingsContributor;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexSettingsContributor;
import com.liferay.portal.search.spi.index.listener.CompanyIndexListener;

import jakarta.json.spi.JsonProvider;

import java.io.IOException;

import java.util.function.Consumer;

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

		MappingsHelper mappingsHelper = new MappingsHelper(
			indexName, _jsonFactory, openSearchIndicesClient,
			_openSearchConfigurationWrapper.overrideTypeMappings(),
			_searchEngineInformation);

		mappingsHelper.setDefaultOrOverrideMappings(
			builder, _openSearchConnectionManager.getJsonpMapper(null));

		try {
			CreateIndexResponse createIndexResponse =
				openSearchIndicesClient.create(builder.build());

			JsonpUtil.logInfoResponse(createIndexResponse, _log);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		_putNondefaultMappings(mappingsHelper);

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

		MappingsHelper mappingsHelper = new MappingsHelper(
			indexName, _jsonFactory, openSearchIndicesClient,
			_openSearchConfigurationWrapper.overrideTypeMappings(),
			_searchEngineInformation);

		mappingsHelper.putDefaultOrOverrideMappings();

		_putNondefaultMappings(mappingsHelper);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_companyIndexListenerServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, CompanyIndexListener.class);

		_companyIndexMappingsContributorServiceTrackerList =
			_openServiceTrackerList(
				bundleContext, CompanyIndexMappingsContributor.class,
				this::_processCompanyIndexMappingsContributor);
		_companyIndexSettingsContributorServiceTrackerList =
			_openServiceTrackerList(
				bundleContext, CompanyIndexSettingsContributor.class,
				this::_processCompanyIndexSettingsContributor);
	}

	@Deactivate
	protected void deactivate() {
		if (_companyIndexListenerServiceTrackerList != null) {
			_companyIndexListenerServiceTrackerList.close();
		}

		if (_companyIndexMappingsContributorServiceTrackerList != null) {
			_companyIndexMappingsContributorServiceTrackerList.close();
		}

		if (_companyIndexSettingsContributorServiceTrackerList != null) {
			_companyIndexSettingsContributorServiceTrackerList.close();
		}
	}

	private PutIndicesSettingsRequest _buildPutIndicesSettingsRequest(
		String indexName, String settings) {

		PutIndicesSettingsRequest.Builder builder =
			new PutIndicesSettingsRequest.Builder(
			).index(
				indexName
			);

		builder.settings(_getIndexSettings(settings));

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

	private IndexSettings _getIndexSettings(String settingsJSON) {
		JsonpMapper jsonpMapper = _openSearchConnectionManager.getJsonpMapper(
			null);

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		return IndexSettings._DESERIALIZER.deserialize(
			jsonProvider.createParser(new UnsyncStringReader(settingsJSON)),
			jsonpMapper);
	}

	private OpenSearchIndicesClient _getOpenSearchIndicesClient() {
		try {
			OpenSearchClient openSearchClient =
				_openSearchConnectionManager.getOpenSearchClient();

			return openSearchClient.indices();
		}
		catch (OpenSearchConnectionNotInitializedException
					openSearchConnectionNotInitializedException) {

			_log.error(openSearchConnectionNotInitializedException);
		}

		return null;
	}

	private void _loadAdditionalSettings(
		long companyId, SettingsHelper settingsHelper,
		boolean includeStaticProperties) {

		_loadContributedSettings(companyId, settingsHelper);

		_loadConfigurationSettings(settingsHelper, includeStaticProperties);

		if (Validator.isNotNull(
				settingsHelper.get("index.number_of_replicas"))) {

			settingsHelper.put("index.auto_expand_replicas", false);
		}
	}

	private void _loadConfigurationSettings(
		SettingsHelper settingsHelper, boolean includeStaticProperties) {

		settingsHelper.put(
			"index.max_result_window",
			String.valueOf(
				_openSearchConfigurationWrapper.indexMaxResultWindow()));
		settingsHelper.put(
			"index.number_of_replicas",
			_openSearchConfigurationWrapper.indexNumberOfReplicas());

		if (includeStaticProperties) {
			settingsHelper.put(
				"index.number_of_shards",
				_openSearchConfigurationWrapper.indexNumberOfShards());
		}

		settingsHelper.loadFromSource(
			_openSearchConfigurationWrapper.additionalIndexConfigurations());
	}

	private void _loadContributedSettings(
		long companyId, SettingsHelper settingsHelper) {

		for (CompanyIndexSettingsContributor companyIndexSettingsContributor :
				_companyIndexSettingsContributorServiceTrackerList) {

			settingsHelper.loadFromSource(
				companyIndexSettingsContributor.contributeSettings(companyId));
		}
	}

	private void _loadDefaultSettings(SettingsHelper settingsHelper) {
		settingsHelper.loadFromSource(
			ResourceUtil.getResourceAsString(
				getClass(), IndexSettingsConstants.INDEX_SETTINGS_FILE_NAME));
	}

	private void _loadTestModeSettings(SettingsHelper settingsHelper) {
		if (!PortalRunMode.isTestMode()) {
			return;
		}

		settingsHelper.put("index.refresh_interval", "1ms");
		settingsHelper.put("index.search.slowlog.threshold.fetch.warn", "-1");
		settingsHelper.put("index.search.slowlog.threshold.query.warn", "-1");
		settingsHelper.put("index.translog.sync_interval", "100ms");
	}

	private <T> ServiceTrackerList<T> _openServiceTrackerList(
		BundleContext bundleContext, Class<T> clazz, Consumer<T> consumer) {

		return ServiceTrackerListFactory.open(
			bundleContext, clazz, null,
			new EagerServiceTrackerCustomizer<T, T>() {

				@Override
				public T addingService(ServiceReference<T> serviceReference) {
					T service = bundleContext.getService(serviceReference);

					consumer.accept(service);

					return service;
				}

				@Override
				public void modifiedService(
					ServiceReference<T> serviceReference, T service) {
				}

				@Override
				public void removedService(
					ServiceReference<T> serviceReference, T service) {

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	private void _processCompanyIndexMappingsContributor(
		CompanyIndexMappingsContributor companyIndexMappingsContributor) {

		OpenSearchIndicesClient openSearchIndicesClient =
			_getOpenSearchIndicesClient();

		if (openSearchIndicesClient == null) {
			return;
		}

		String mappings = companyIndexMappingsContributor.contributeMappings();

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			String indexName = getIndexName(companyId);

			MappingsHelper mappingsHelper = new MappingsHelper(
				indexName, _jsonFactory, openSearchIndicesClient,
				_openSearchConfigurationWrapper.overrideTypeMappings(),
				_searchEngineInformation);

			try {
				mappingsHelper.putMappings(mappings);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to put mappings for index ", indexName,
						" with contributor ", companyIndexMappingsContributor),
					exception);
			}
		}
	}

	private void _processCompanyIndexSettingsContributor(
		CompanyIndexSettingsContributor companyIndexSettingsContributor) {

		OpenSearchIndicesClient openSearchIndicesClient =
			_getOpenSearchIndicesClient();

		if (openSearchIndicesClient == null) {
			return;
		}

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			SettingsHelper settingsHelper = new SettingsHelper();

			settingsHelper.loadFromSource(
				companyIndexSettingsContributor.contributeSettings(companyId));

			JSONObject settingsJSONObject =
				settingsHelper.getSettingsJSONObject();

			if (SetUtil.isEmpty(settingsJSONObject.keySet())) {
				continue;
			}

			String indexName = getIndexName(companyId);

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
						" with contributor ", companyIndexSettingsContributor),
					exception);
			}
		}
	}

	private void _putAdditionalMappings(MappingsHelper mappingsHelper) {
		if (Validator.isNull(
				_openSearchConfigurationWrapper.additionalTypeMappings())) {

			return;
		}

		mappingsHelper.putMappings(
			_openSearchConfigurationWrapper.additionalTypeMappings());
	}

	private void _putContributedMappings(MappingsHelper mappingsHelper) {
		for (CompanyIndexMappingsContributor companyIndexMappingsContributor :
				_companyIndexMappingsContributorServiceTrackerList) {

			mappingsHelper.putMappings(
				companyIndexMappingsContributor.contributeMappings());
		}
	}

	private void _putNondefaultMappings(MappingsHelper mappingsHelper) {
		if (Validator.isNotNull(
				_openSearchConfigurationWrapper.overrideTypeMappings())) {

			return;
		}

		_putContributedMappings(mappingsHelper);

		_putAdditionalMappings(mappingsHelper);
	}

	private void _setSettings(
		long companyId, CreateIndexRequest.Builder builder) {

		SettingsHelper settingsHelper = new SettingsHelper();

		_loadDefaultSettings(settingsHelper);

		_loadTestModeSettings(settingsHelper);

		_loadAdditionalSettings(companyId, settingsHelper, true);

		builder.settings(
			_getIndexSettings(
				String.valueOf(settingsHelper.getSettingsJSONObject())));
	}

	private void _updateSettings(
		long companyId, String indexName,
		OpenSearchIndicesClient openSearchIndicesClient) {

		SettingsHelper settingsHelper = new SettingsHelper();

		_loadDefaultSettings(settingsHelper);

		_loadAdditionalSettings(companyId, settingsHelper, false);

		JSONObject settingsJSONObject = settingsHelper.getSettingsJSONObject();

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

	private ServiceTrackerList<CompanyIndexListener>
		_companyIndexListenerServiceTrackerList;
	private ServiceTrackerList<CompanyIndexMappingsContributor>
		_companyIndexMappingsContributorServiceTrackerList;
	private ServiceTrackerList<CompanyIndexSettingsContributor>
		_companyIndexSettingsContributorServiceTrackerList;

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