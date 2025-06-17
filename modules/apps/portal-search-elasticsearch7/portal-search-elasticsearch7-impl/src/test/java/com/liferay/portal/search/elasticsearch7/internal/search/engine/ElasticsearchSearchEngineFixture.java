/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchSearchEngine;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.index.CompanyIndexHelper;
import com.liferay.portal.search.elasticsearch7.internal.index.IndexFactory;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ElasticsearchEngineAdapterFixture;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.test.util.search.engine.SearchEngineFixture;

import java.util.Map;
import java.util.Objects;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Adam Brandizzi
 */
public class ElasticsearchSearchEngineFixture implements SearchEngineFixture {

	public ElasticsearchSearchEngineFixture(
		ElasticsearchConnectionFixture elasticsearchConnectionFixture) {

		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;
	}

	public ElasticsearchConnectionManager getElasticsearchConnectionManager() {
		return _elasticsearchConnectionManager;
	}

	public ElasticsearchSearchEngine getElasticsearchSearchEngine() {
		return _elasticsearchSearchEngine;
	}

	@Override
	public IndexNameBuilder getIndexNameBuilder() {
		return _indexNameBuilder;
	}

	@Override
	public SearchEngine getSearchEngine() {
		return getElasticsearchSearchEngine();
	}

	@Override
	public void setUp() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			Objects.requireNonNull(_elasticsearchConnectionFixture);

		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper =
			_createElasticsearchConfigurationWrapper(
				elasticsearchConnectionFixture.
					getElasticsearchConfigurationProperties());

		ElasticsearchConnectionManager elasticsearchConnectionManager =
			_createElasticsearchConnectionManager(
				elasticsearchConfigurationWrapper,
				elasticsearchConnectionFixture);

		IndexNameBuilder indexNameBuilder = _createIndexNameBuilder(
			elasticsearchConnectionFixture.
				getElasticsearchConfigurationProperties());

		_elasticsearchConnectionManager = elasticsearchConnectionManager;
		_elasticsearchSearchEngine = _createElasticsearchSearchEngine(
			elasticsearchConnectionFixture, elasticsearchConfigurationWrapper,
			elasticsearchConnectionManager, indexNameBuilder);
		_frameworkUtilMockedStatic = _createFrameworkUtil();
		_indexNameBuilder = indexNameBuilder;
	}

	@Override
	public void tearDown() throws Exception {
		_elasticsearchConnectionFixture.destroyNode();

		_elasticsearchEngineAdapterFixture.tearDown();

		if (_indexFactory != null) {
			_indexFactory.close();

			_indexFactory = null;
		}

		if (_companyIndexHelper != null) {
			ReflectionTestUtil.invoke(
				_companyIndexHelper, "deactivate", new Class<?>[0]);

			_companyIndexHelper = null;
		}

		if (_frameworkUtilMockedStatic != null) {
			_frameworkUtilMockedStatic.close();

			_frameworkUtilMockedStatic = null;
		}
	}

	private ElasticsearchConfigurationWrapper
		_createElasticsearchConfigurationWrapper(
			Map<String, Object> configurationProperties) {

		return new ElasticsearchConfigurationWrapper() {
			{
				setElasticsearchConfiguration(
					ConfigurableUtil.createConfigurable(
						ElasticsearchConfiguration.class,
						configurationProperties));
			}
		};
	}

	private ElasticsearchConnectionManager
		_createElasticsearchConnectionManager(
			ElasticsearchConfigurationWrapper
				elasticsearchConfigurationWrapper1,
			ElasticsearchConnectionFixture elasticsearchConnectionFixture) {

		return new ElasticsearchConnectionManager() {
			{
				elasticsearchConfigurationWrapper =
					elasticsearchConfigurationWrapper1;

				ElasticsearchConnection elasticsearchConnection =
					elasticsearchConnectionFixture.
						createElasticsearchConnection();

				addElasticsearchConnection(elasticsearchConnection);

				getElasticsearchConnection(
					elasticsearchConnection.getConnectionId());
			}
		};
	}

	private ElasticsearchSearchEngine _createElasticsearchSearchEngine(
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		ElasticsearchConnectionManager elasticsearchConnectionManager,
		IndexNameBuilder indexNameBuilder) {

		ElasticsearchSearchEngine elasticsearchSearchEngine =
			new ElasticsearchSearchEngine();

		ReflectionTestUtil.setFieldValue(
			elasticsearchSearchEngine, "_elasticsearchConnectionManager",
			elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			elasticsearchSearchEngine, "_indexFactory",
			_createIndexFactory(
				elasticsearchConfigurationWrapper, indexNameBuilder));
		ReflectionTestUtil.setFieldValue(
			elasticsearchSearchEngine, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			elasticsearchSearchEngine, "_searchEngineAdapter",
			_createSearchEngineAdapter(elasticsearchClientResolver));

		return elasticsearchSearchEngine;
	}

	private MockedStatic<FrameworkUtil> _createFrameworkUtil() {
		MockedStatic<FrameworkUtil> frameworkUtilMockedStatic =
			Mockito.mockStatic(FrameworkUtil.class);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		return frameworkUtilMockedStatic;
	}

	private IndexFactory _createIndexFactory(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		IndexNameBuilder indexNameBuilder) {

		_companyIndexHelper = new CompanyIndexHelper();

		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_elasticsearchConfigurationWrapper",
			elasticsearchConfigurationWrapper);
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_searchEngineInformation",
			_createSearchEngineInformation());

		ReflectionTestUtil.invoke(
			_companyIndexHelper, "activate",
			new Class<?>[] {BundleContext.class},
			SystemBundleUtil.getBundleContext());

		_indexFactory = new IndexFactory(
			_companyIndexHelper, Mockito.mock(CompanyLocalService.class),
			elasticsearchConfigurationWrapper,
			Mockito.mock(ElasticsearchConnectionManager.class));

		return _indexFactory;
	}

	private IndexNameBuilder _createIndexNameBuilder(
		Map<String, Object> configurationProperties) {

		String indexNamePrefix = null;

		if (MapUtil.isNotEmpty(configurationProperties)) {
			indexNamePrefix = MapUtil.getString(
				configurationProperties, "indexNamePrefix");
		}

		IndexNameBuilder indexNameBuilder = Mockito.mock(
			IndexNameBuilder.class);

		Mockito.when(
			indexNameBuilder.getIndexName(Mockito.anyLong())
		).then(
			invocation -> String.valueOf(invocation.getArgument(0, Long.class))
		);

		Mockito.when(
			indexNameBuilder.getIndexNamePrefix()
		).thenReturn(
			indexNamePrefix
		);

		return indexNameBuilder;
	}

	private SearchEngineAdapter _createSearchEngineAdapter(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchEngineAdapterFixture =
			new ElasticsearchEngineAdapterFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
				}
			};

		_elasticsearchEngineAdapterFixture.setUp();

		return _elasticsearchEngineAdapterFixture.getSearchEngineAdapter();
	}

	private SearchEngineInformation _createSearchEngineInformation() {
		SearchEngineInformation searchEngineInformation = Mockito.mock(
			SearchEngineInformation.class);

		Mockito.when(
			searchEngineInformation.getEmbeddingVectorDimensions()
		).thenReturn(
			new int[] {256}
		);

		return searchEngineInformation;
	}

	private CompanyIndexHelper _companyIndexHelper;
	private final ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;
	private ElasticsearchConnectionManager _elasticsearchConnectionManager;
	private ElasticsearchEngineAdapterFixture
		_elasticsearchEngineAdapterFixture;
	private ElasticsearchSearchEngine _elasticsearchSearchEngine;
	private MockedStatic<FrameworkUtil> _frameworkUtilMockedStatic;
	private IndexFactory _indexFactory;
	private IndexNameBuilder _indexNameBuilder;

}