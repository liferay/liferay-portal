/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.configuration.OpenSearchConfiguration;
import com.liferay.portal.search.opensearch2.internal.OpenSearchSearchEngine;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapperImpl;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.connection.TestOpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.index.CompanyIndexHelper;
import com.liferay.portal.search.opensearch2.internal.index.IndexFactory;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.OpenSearchEngineAdapterFixture;
import com.liferay.portal.search.test.util.search.engine.SearchEngineFixture;

import java.util.Collections;
import java.util.Map;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Adam Brandizzi
 * @author Petteri Karttunen
 */
public class OpenSearchSearchEngineFixture implements SearchEngineFixture {

	public OpenSearchSearchEngineFixture(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	@Override
	public IndexNameBuilder getIndexNameBuilder() {
		return _indexNameBuilder;
	}

	public OpenSearchConnectionManager getOpenSearchConnectionManager() {
		return _openSearchConnectionManager;
	}

	public OpenSearchSearchEngine getOpenSearchSearchEngine() {
		return _openSearchSearchEngine;
	}

	@Override
	public SearchEngine getSearchEngine() {
		return getOpenSearchSearchEngine();
	}

	@Override
	public void setUp() throws Exception {
		TestOpenSearchConnectionManager testOpenSearchConnectionManager =
			(TestOpenSearchConnectionManager)_openSearchConnectionManager;

		OpenSearchConfigurationWrapper openSearchConfigurationWrapper =
			_createOpenSearchConfigurationWrapper(
				testOpenSearchConnectionManager.
					getOpenSearchConfigurationProperties());

		IndexNameBuilder indexNameBuilder = _createIndexNameBuilder(
			testOpenSearchConnectionManager.
				getOpenSearchConfigurationProperties());

		_frameworkUtilMockedStatic = _createFrameworkUtil();
		_indexNameBuilder = indexNameBuilder;
		_openSearchSearchEngine = _createOpenSearchSearchEngine(
			indexNameBuilder, openSearchConfigurationWrapper);
	}

	@Override
	public void tearDown() throws Exception {
		_openSearchEngineAdapterFixture.tearDown();

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

	private CompanyIndexHelper _createCompanyIndexHelper(
		IndexNameBuilder indexNameBuilder,
		OpenSearchConfigurationWrapper openSearchConfigurationWrapper) {

		CompanyIndexHelper companyIndexHelper = new CompanyIndexHelper();

		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_companyLocalService",
			Mockito.mock(CompanyLocalService.class));
		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_openSearchConfigurationWrapper",
			openSearchConfigurationWrapper);
		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_openSearchConnectionManager",
			_openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_searchEngineInformation",
			_createSearchEngineInformation());

		ReflectionTestUtil.invoke(
			companyIndexHelper, "activate",
			new Class<?>[] {BundleContext.class},
			SystemBundleUtil.getBundleContext());

		return companyIndexHelper;
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
		CompanyIndexHelper companyIndexHelper,
		OpenSearchConfigurationWrapper openSearchConfigurationWrapper) {

		return new IndexFactory(
			companyIndexHelper, Mockito.mock(CompanyLocalService.class),
			openSearchConfigurationWrapper, _openSearchConnectionManager);
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

	private OpenSearchConfigurationWrapper
		_createOpenSearchConfigurationWrapper(
			Map<String, Object> configurationProperties) {

		return new OpenSearchConfigurationWrapperImpl() {
			{
				if (configurationProperties == null) {
					setOpenSearchConfiguration(
						ConfigurableUtil.createConfigurable(
							OpenSearchConfiguration.class,
							Collections.emptyMap()));
				}
				else {
					setOpenSearchConfiguration(
						ConfigurableUtil.createConfigurable(
							OpenSearchConfiguration.class,
							configurationProperties));
				}
			}
		};
	}

	private OpenSearchSearchEngine _createOpenSearchSearchEngine(
		IndexNameBuilder indexNameBuilder,
		OpenSearchConfigurationWrapper openSearchConfigurationWrapper) {

		OpenSearchSearchEngine openSearchSearchEngine =
			new OpenSearchSearchEngine();

		_companyIndexHelper = _createCompanyIndexHelper(
			indexNameBuilder, openSearchConfigurationWrapper);

		_indexFactory = _createIndexFactory(
			_companyIndexHelper, openSearchConfigurationWrapper);

		ReflectionTestUtil.setFieldValue(
			openSearchSearchEngine, "_indexFactory", _indexFactory);

		ReflectionTestUtil.setFieldValue(
			openSearchSearchEngine, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchSearchEngine, "_openSearchConnectionManager",
			_openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			openSearchSearchEngine, "_searchEngineAdapter",
			_createSearchEngineAdapter());

		return openSearchSearchEngine;
	}

	private SearchEngineAdapter _createSearchEngineAdapter() {
		_openSearchEngineAdapterFixture = new OpenSearchEngineAdapterFixture() {
			{
				setOpenSearchConnectionManager(_openSearchConnectionManager);
			}
		};

		_openSearchEngineAdapterFixture.setUp();

		return _openSearchEngineAdapterFixture.getSearchEngineAdapter();
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
	private MockedStatic<FrameworkUtil> _frameworkUtilMockedStatic;
	private IndexFactory _indexFactory;
	private IndexNameBuilder _indexNameBuilder;
	private final OpenSearchConnectionManager _openSearchConnectionManager;
	private OpenSearchEngineAdapterFixture _openSearchEngineAdapterFixture;
	private OpenSearchSearchEngine _openSearchSearchEngine;

}