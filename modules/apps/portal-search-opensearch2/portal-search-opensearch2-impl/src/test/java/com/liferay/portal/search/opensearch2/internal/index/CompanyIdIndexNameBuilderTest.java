/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.configuration.OpenSearchConfiguration;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapperImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.GetIndexResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class CompanyIdIndexNameBuilderTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@After
	public void tearDown() throws Exception {
		if (_indexFactory != null) {
			_indexFactory.close();

			_indexFactory = null;
		}

		if (_companyIndexHelper != null) {
			ReflectionTestUtil.invoke(
				_companyIndexHelper, "deactivate", new Class<?>[0]);

			_companyIndexHelper = null;
		}
	}

	@Test
	public void testActivate() throws Exception {
		OpenSearchConfigurationWrapper openSearchConfigurationWrapperMock =
			Mockito.mock(OpenSearchConfigurationWrapperImpl.class);

		Mockito.when(
			openSearchConfigurationWrapperMock.indexNamePrefix()
		).thenReturn(
			"UPPERCASE"
		);

		CompanyIdIndexNameBuilder companyIdIndexNameBuilder =
			new CompanyIdIndexNameBuilder() {
				{
					openSearchConfigurationWrapper =
						openSearchConfigurationWrapperMock;
				}
			};

		companyIdIndexNameBuilder.activate();

		Assert.assertEquals(
			"uppercase0", companyIdIndexNameBuilder.getIndexName(0));
	}

	@Test
	public void testIndexNamePrefixBlank() throws Exception {
		_assertIndexNamePrefix(StringPool.BLANK, StringPool.BLANK);
	}

	@Test(expected = OpenSearchException.class)
	public void testIndexNamePrefixInvalidIndexName() throws Exception {
		createIndices(0, StringPool.SLASH);
	}

	@Test
	public void testIndexNamePrefixNull() throws Exception {
		_assertIndexNamePrefix(StringPool.BLANK, null);
	}

	@Test
	public void testIndexNamePrefixTrim() throws Exception {
		String string = RandomTestUtil.randomString();

		_assertIndexNamePrefix(
			StringUtil.toLowerCase(string),
			StringPool.TAB + string + StringPool.SPACE);
	}

	@Test
	public void testIndexNamePrefixUppercase() throws Exception {
		_assertIndexNamePrefix("uppercase", "UPPERCASE");
	}

	protected void createIndices(long companyId, String indexNamePrefix)
		throws Exception {

		CompanyIdIndexNameBuilder companyIdIndexNameBuilder =
			new CompanyIdIndexNameBuilder();

		companyIdIndexNameBuilder.setIndexNamePrefix(indexNamePrefix);

		OpenSearchConfigurationWrapper openSearchConfigurationWrapper =
			_createOpenSearchConfigurationWrapper();

		_companyIndexHelper = new CompanyIndexHelper();

		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_companyLocalService",
			Mockito.mock(CompanyLocalService.class));
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_indexNameBuilder",
			_createIndexNameBuilder(indexNamePrefix));
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_openSearchConfigurationWrapper",
			openSearchConfigurationWrapper);
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_openSearchConnectionManager",
			openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			_companyIndexHelper, "_searchEngineInformation",
			_createSearchEngineInformation());

		ReflectionTestUtil.invoke(
			_companyIndexHelper, "activate",
			new Class<?>[] {BundleContext.class},
			SystemBundleUtil.getBundleContext());

		_indexFactory = new IndexFactory(
			_companyIndexHelper, Mockito.mock(CompanyLocalService.class),
			openSearchConfigurationWrapper, openSearchConnectionManager);

		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		_indexFactory.initializeIndex(companyId, openSearchClient.indices());
	}

	private void _assertIndexNamePrefix(
			String expectedIndexNamePrefix, String indexNamePrefix)
		throws Exception {

		long companyId = RandomTestUtil.randomLong();

		createIndices(companyId, indexNamePrefix);

		String expectedIndexName = expectedIndexNamePrefix + companyId;

		GetIndexResponse getIndexResponse = getIndex(expectedIndexName);

		Assert.assertTrue(getIndexResponse.get(expectedIndexName) != null);

		_deleteIndices(companyId, indexNamePrefix);
	}

	private IndexNameBuilder _createIndexNameBuilder(String indexNamePrefix) {
		return new CompanyIdIndexNameBuilder() {
			{
				setIndexNamePrefix(indexNamePrefix);
			}
		};
	}

	private OpenSearchConfigurationWrapper
		_createOpenSearchConfigurationWrapper() {

		return new OpenSearchConfigurationWrapperImpl() {
			{
				setOpenSearchConfiguration(
					ConfigurableUtil.createConfigurable(
						OpenSearchConfiguration.class, Collections.emptyMap()));
			}
		};
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

	private void _deleteIndices(long companyId, String indexNamePrefix) {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			openSearchIndicesClient.delete(
				DeleteIndexRequest.of(
					deleteIndexRequest -> deleteIndexRequest.index(
						_getDeleteIndexName(companyId, indexNamePrefix))));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getDeleteIndexName(long companyId, String indexNamePrefix) {
		if (!Validator.isBlank(indexNamePrefix)) {
			indexNamePrefix = StringUtil.toLowerCase(indexNamePrefix);
			indexNamePrefix = indexNamePrefix.trim();

			return indexNamePrefix + companyId;
		}

		return String.valueOf(companyId);
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private CompanyIndexHelper _companyIndexHelper;
	private IndexFactory _indexFactory;

}