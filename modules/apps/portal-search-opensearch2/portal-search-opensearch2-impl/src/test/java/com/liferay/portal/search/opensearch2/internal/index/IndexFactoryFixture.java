/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapperImpl;
import com.liferay.portal.search.opensearch2.internal.connection.IndexName;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.util.HashMap;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.opensearch.client.opensearch.OpenSearchClient;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Adam Brandizzi
 */
public class IndexFactoryFixture {

	public IndexFactoryFixture(
		String indexName,
		OpenSearchConnectionManager openSearchConnectionManager) {

		_indexName = indexName;

		_frameworkUtilMockedStatic = _createFrameworkUtil();

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public void createIndices() {
		IndexFactory indexFactory = getIndexFactory();

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		indexFactory.initializeIndex(
			RandomTestUtil.randomLong(), openSearchClient.indices());
	}

	public void deleteIndices() {
		IndexFactory indexFactory = getIndexFactory();

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient();

		indexFactory.deleteIndex(
			RandomTestUtil.randomLong(), openSearchClient.indices());
	}

	public CompanyIndexHelper getCompanyIndexHelper() {
		if (_companyIndexHelper != null) {
			return _companyIndexHelper;
		}

		_companyIndexHelper = new CompanyIndexHelper();

		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_companyLocalService",
			Mockito.mock(CompanyLocalService.class));
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_indexNameBuilder",
			new TestIndexNameBuilder());
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_openSearchConfigurationWrapper",
			createOpenSearchConfigurationWrapper());
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_openSearchConnectionManager",
			_openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_searchEngineInformation",
			_createSearchEngineInformation());

		ReflectionTestUtil.invoke(
			_companyIndexHelper, "activate",
			new Class<?>[] {BundleContext.class},
			SystemBundleUtil.getBundleContext());

		return _companyIndexHelper;
	}

	public IndexFactory getIndexFactory() {
		if (_indexFactory != null) {
			return _indexFactory;
		}

		_indexFactory = new IndexFactory(
			getCompanyIndexHelper(), Mockito.mock(CompanyLocalService.class),
			createOpenSearchConfigurationWrapper(),
			_openSearchConnectionManager);

		return _indexFactory;
	}

	public String getIndexName() {
		IndexName indexName = new IndexName(_indexName);

		return indexName.getName();
	}

	public void tearDown() {
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

	protected OpenSearchConfigurationWrapper
		createOpenSearchConfigurationWrapper() {

		return new OpenSearchConfigurationWrapperImpl() {
			{
				activate(new HashMap<>());
			}
		};
	}

	protected class TestIndexNameBuilder implements IndexNameBuilder {

		@Override
		public String getIndexName(long companyId) {
			return IndexFactoryFixture.this.getIndexName();
		}

		@Override
		public String getIndexNamePrefix() {
			return null;
		}

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
	private final String _indexName;
	private final OpenSearchConnectionManager _openSearchConnectionManager;

}