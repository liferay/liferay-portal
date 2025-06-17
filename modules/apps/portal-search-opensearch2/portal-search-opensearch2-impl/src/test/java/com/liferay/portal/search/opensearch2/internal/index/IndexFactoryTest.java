/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapperImpl;
import com.liferay.portal.search.opensearch2.internal.connection.IndexName;
import com.liferay.portal.search.opensearch2.internal.document.SingleFieldFixture;
import com.liferay.portal.search.opensearch2.internal.query.QueryFactories;
import com.liferay.portal.search.opensearch2.internal.util.ResourceUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexConfigurationContributor;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.MappingsHelper;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.SettingsHelper;
import com.liferay.portal.search.spi.index.listener.CompanyIndexListener;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mockito.Mockito;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.DynamicTemplate;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.GetIndexResponse;
import org.opensearch.client.opensearch.indices.GetMappingRequest;
import org.opensearch.client.opensearch.indices.GetMappingResponse;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.IndexState;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.get_mapping.IndexMappingRecord;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class IndexFactoryTest extends BaseOpenSearchTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_indexFactoryFixture = new IndexFactoryFixture(
			testName.getMethodName(), openSearchConnectionManager);

		_indexFactory = _indexFactoryFixture.getIndexFactory();

		CompanyIndexHelper companyIndexHelper =
			_indexFactoryFixture.getCompanyIndexHelper();

		Mockito.reset(_openSearchConfigurationWrapper);

		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_openSearchConfigurationWrapper",
			_openSearchConfigurationWrapper);

		ReflectionTestUtil.setFieldValue(
			_indexFactory, "_openSearchConfigurationWrapper",
			_openSearchConfigurationWrapper);

		Mockito.when(
			_openSearchConfigurationWrapper.indexMaxResultWindow()
		).thenReturn(
			10000
		);

		_singleFieldFixture = new SingleFieldFixture(
			openSearchConnectionManager.getOpenSearchClient(),
			new IndexName(_indexFactoryFixture.getIndexName()));

		_singleFieldFixture.setQueryBuilderFactory(QueryFactories.MATCH);
	}

	@After
	public void tearDown() {
		deleteIndex();

		_indexFactoryFixture.tearDown();

		if (_serviceRegistrations.isEmpty()) {
			return;
		}

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testAdditionalIndexConfigurations() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			"index.number_of_replicas: 1\nindex.number_of_shards: 2"
		);

		initializeIndex();

		_assertIndexSettings(1, 2);
	}

	@Test
	public void testAdditionalTypeMappings() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getAdditionalTypeMappings()
		);

		initializeIndex();

		_assertAdditionalTypeMappings();
	}

	@Test
	public void testAdditionalTypeMappingsCannotOverrideContributedMappings()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new CompanyIndexConfigurationContributor() {

					@Override
					public void contributeMappings(
						long companyId, MappingsHelper mappingsHelper) {

						mappingsHelper.putMappings(
							JSONUtil.put(
								"properties",
								JSONUtil.put(
									"additionalKeyword",
									JSONUtil.put(
										"store", true
									).put(
										"type", "text"
									))
							).toString());
					}

					@Override
					public void contributeSettings(
						long companyId, SettingsHelper settingsHelper) {
					}

				},
				null));

		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getAdditionalTypeMappings()
		);

		initializeIndex();

		assertType("additionalKeyword", "text");
	}

	@Test
	public void testAdditionalTypeMappingsWithLegacyRootType()
		throws Exception {

		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getLegacyAdditionalTypeMappings()
		);

		initializeIndex();

		_assertAdditionalTypeMappings();
	}

	@Test
	public void testAddMultipleCompanyIndexConfigurationContributors()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new TestCompanyIndexConfigurationContributor(), null));

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new TestCompanyIndexConfigurationContributor(), null));
	}

	@Test
	public void testCatchAllTemplateIsAlwaysLast() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getAdditionalTypeMappings()
		);

		initializeIndex();

		_indexOneDocument("match_additional_mapping");
		_indexOneDocument("match_catch_all");

		assertType("match_additional_mapping", "keyword");
		assertType("match_catch_all", "text");
	}

	@Test
	public void testCompanyIndexListener() throws Exception {
		CompanyIndexListener companyIndexListener = Mockito.mock(
			CompanyIndexListener.class);

		addCompanyIndexListener(companyIndexListener);

		initializeIndex();

		Mockito.verify(
			companyIndexListener, Mockito.times(1)
		).onAfterCreate(
			Mockito.anyString()
		);

		deleteIndex();

		Mockito.verify(
			companyIndexListener, Mockito.times(1)
		).onBeforeDelete(
			Mockito.anyString()
		);
	}

	@Test
	public void testCompanyIndexListenersThrowsException() throws Exception {
		addCompanyIndexListener(
			new CompanyIndexListener() {

				@Override
				public void onAfterCreate(String indexName) {
					throw new RuntimeException();
				}

				@Override
				public void onBeforeDelete(String indexName) {
					throw new RuntimeException();
				}

			});

		initializeIndex();
	}

	@Test
	public void testConfigurationSettingsOverrideContributedSettings()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new CompanyIndexConfigurationContributor() {

					@Override
					public void contributeMappings(
						long companyId, MappingsHelper mappingsHelper) {
					}

					@Override
					public void contributeSettings(
						long companyId, SettingsHelper settingsHelper) {

						settingsHelper.put("index.number_of_replicas", "3");
						settingsHelper.put("index.number_of_shards", "4");
					}

				},
				null));

		Mockito.when(
			_openSearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			"index.number_of_replicas: 1\nindex.number_of_shards: 2"
		);

		initializeIndex();

		_assertIndexSettings(1, 2);
	}

	@Test
	public void testContributeMappings() throws Exception {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new CompanyIndexConfigurationContributor() {

					@Override
					public void contributeMappings(
						long companyId, MappingsHelper mappingsHelper) {

						mappingsHelper.putMappings(
							_getAdditionalTypeMappings());
					}

					@Override
					public void contributeSettings(
						long companyId, SettingsHelper settingsHelper) {
					}

				},
				null));

		initializeIndex();

		_assertAdditionalTypeMappings();
	}

	@Test
	public void testContributeMappingsCannotOverrideDefaultMappings()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new CompanyIndexConfigurationContributor() {

					@Override
					public void contributeMappings(
						long companyId, MappingsHelper mappingsHelper) {

						mappingsHelper.putMappings(_getOverrideTypeMappings());
					}

					@Override
					public void contributeSettings(
						long companyId, SettingsHelper settingsHelper) {
					}

				},
				null));

		initializeIndex();

		_assertDefaultLiferayFields();
	}

	@Test
	public void testContributeSettings() throws Exception {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new CompanyIndexConfigurationContributor() {

					@Override
					public void contributeMappings(
						long companyId, MappingsHelper mappingsHelper) {
					}

					@Override
					public void contributeSettings(
						long companyId, SettingsHelper settingsHelper) {

						settingsHelper.put("index.number_of_replicas", "2");
						settingsHelper.put("index.number_of_shards", "3");
					}

				},
				null));

		initializeIndex();

		_assertIndexSettings(2, 3);
	}

	@Test
	public void testCreateIndicesWithBlankStrings() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			StringPool.SPACE
		);

		Mockito.when(
			_openSearchConfigurationWrapper.indexNumberOfReplicas()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_openSearchConfigurationWrapper.indexNumberOfShards()
		).thenReturn(
			StringPool.SPACE
		);

		initializeIndex();
	}

	@Test
	public void testCreateIndicesWithEmptyConfiguration() throws Exception {
		initializeIndex();
	}

	@Test
	public void testDefaultIndexSettings() throws Exception {
		initializeIndex();

		_assertIndexSettings(0, 1);
	}

	@Test
	public void testDefaultIndices() throws Exception {
		initializeIndex();

		_assertMappings(Field.COMPANY_ID, Field.ENTRY_CLASS_NAME);
	}

	@Test
	public void testIndexConfigurations() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.indexNumberOfReplicas()
		).thenReturn(
			"1"
		);

		Mockito.when(
			_openSearchConfigurationWrapper.indexNumberOfShards()
		).thenReturn(
			"2"
		);

		initializeIndex();

		_assertIndexSettings(1, 2);
	}

	@FeatureFlag("LPD-7822")
	@Test
	public void testInitializeIndexAfterIndexExists() throws Exception {
		initializeIndex();

		_assertIndexSettings(0, 1);

		assertNoMapping("additionalKeyword");

		Mockito.when(
			_openSearchConfigurationWrapper.indexNumberOfReplicas()
		).thenReturn(
			"1"
		);

		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getAdditionalTypeMappings()
		);

		initializeIndex();

		_assertIndexSettings(1, 1);

		assertType("additionalKeyword", "keyword");
	}

	@Test
	public void testLegacyOverrideTypeMappings() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.overrideTypeMappings()
		).thenReturn(
			_getLegacyOverrideTypeMappings()
		);

		initializeIndex();

		String field1 = RandomTestUtil.randomString() + "_double";

		_indexOneDocument(field1, RandomTestUtil.randomInt());

		assertType(field1, "integer");

		assertType(Field.SUBTITLE, "keyword");

		String field2 = "title_en";

		_indexOneDocument(field2);

		assertNoAnalyzer(field2);
	}

	@Test
	public void testOverrideTypeMappings() throws Exception {
		Mockito.when(
			_openSearchConfigurationWrapper.overrideTypeMappings()
		).thenReturn(
			_getOverrideTypeMappings()
		);

		initializeIndex();

		String field1 = RandomTestUtil.randomString() + "_double";

		_indexOneDocument(field1, RandomTestUtil.randomInt());

		assertType(field1, "integer");

		assertType(Field.SUBTITLE, "keyword");

		String field2 = "title_en";

		_indexOneDocument(field2);

		assertNoAnalyzer(field2);
	}

	@Test
	public void testOverrideTypeMappingsDoesNotInterfereWithIndexSettings()
		throws Exception {

		Mockito.when(
			_openSearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			"index.number_of_replicas: 1\nindex.number_of_shards: 2"
		);

		Mockito.when(
			_openSearchConfigurationWrapper.overrideTypeMappings()
		).thenReturn(
			_getOverrideTypeMappings()
		);

		initializeIndex();

		_assertIndexSettings(1, 2);
		_assertMappings(Field.SUBTITLE);
	}

	@Test
	public void testOverrideTypeMappingsPreventsAdditionalTypeMapings()
		throws Exception {

		Mockito.when(
			_openSearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getAdditionalTypeMappings()
		);

		Mockito.when(
			_openSearchConfigurationWrapper.overrideTypeMappings()
		).thenReturn(
			_getOverrideTypeMappings()
		);

		initializeIndex();

		assertNoMapping("additionalKeyword");
		assertType(Field.SUBTITLE, "keyword");
	}

	@Test
	public void testOverrideTypeMappingsPreventsContributedMapings()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new CompanyIndexConfigurationContributor() {

					@Override
					public void contributeMappings(
						long companyId, MappingsHelper mappingsHelper) {

						mappingsHelper.putMappings(
							JSONUtil.put(
								"contributedKeyword",
								JSONUtil.put(
									"store", true
								).put(
									"type", "keyword"
								)
							).toString());
					}

					@Override
					public void contributeSettings(
						long companyId, SettingsHelper settingsHelper) {
					}

				},
				null));

		Mockito.when(
			_openSearchConfigurationWrapper.overrideTypeMappings()
		).thenReturn(
			_getOverrideTypeMappings()
		);

		initializeIndex();

		assertNoMapping("contributedKeyword");
		assertType(Field.SUBTITLE, "keyword");
	}

	@Test
	public void testRemoveCompanyIndexConfigurationContributor() {
		ServiceRegistration<CompanyIndexConfigurationContributor>
			serviceRegistration = _bundleContext.registerService(
				CompanyIndexConfigurationContributor.class,
				new TestCompanyIndexConfigurationContributor(), null);

		serviceRegistration.unregister();
	}

	@Rule
	public TestName testName = new TestName();

	protected void addCompanyIndexListener(
		CompanyIndexListener companyIndexListener) {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexListener.class, companyIndexListener, null));
	}

	protected void assertAnalyzer(String analyzer, String field)
		throws Exception {

		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		FieldMappingAssert.assertAnalyzer(
			analyzer, field, _indexFactoryFixture.getIndexName(),
			openSearchClient.indices());
	}

	protected void assertNoAnalyzer(String field) throws Exception {
		assertAnalyzer(null, field);
	}

	protected void assertNoMapping(String field) throws Exception {
		assertType(field, null);
	}

	protected void assertType(String field, String type) throws Exception {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		FieldMappingAssert.assertType(
			type, field, _indexFactoryFixture.getIndexName(),
			openSearchClient.indices());
	}

	protected void deleteIndex() {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		_indexFactory.deleteIndex(
			RandomTestUtil.randomLong(), openSearchIndicesClient);
	}

	protected void initializeIndex() throws Exception {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		_indexFactory.initializeIndex(
			RandomTestUtil.randomLong(), openSearchIndicesClient);
	}

	protected static class TestCompanyIndexConfigurationContributor
		implements CompanyIndexConfigurationContributor {

		@Override
		public void contributeMappings(
			long companyId, MappingsHelper mappingsHelper) {
		}

		@Override
		public void contributeSettings(
			long companyId, SettingsHelper settingsHelper) {
		}

	}

	private void _assertAdditionalTypeMappings() throws Exception {
		OpenSearchClient openSearchClient =
			openSearchConnectionManager.getOpenSearchClient();

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		GetMappingResponse getMappingResponse =
			openSearchIndicesClient.getMapping(
				GetMappingRequest.of(
					getMappingRequest -> getMappingRequest.index(
						_indexFactoryFixture.getIndexName())));

		Map<String, IndexMappingRecord> result = getMappingResponse.result();

		IndexMappingRecord indexMappingRecord = result.get(
			_indexFactoryFixture.getIndexName());

		TypeMapping typeMapping = indexMappingRecord.mappings();

		List<Map<String, DynamicTemplate>> dynamicTemplatesList =
			typeMapping.dynamicTemplates();

		Map<String, DynamicTemplate> dynamicTemplateMap =
			dynamicTemplatesList.get(0);

		DynamicTemplate dynamicTemplate = dynamicTemplateMap.get(
			"template_additional_mapping");

		Assert.assertEquals("*_additional_mapping", dynamicTemplate.match());

		Property property = dynamicTemplate.mapping();

		Assert.assertTrue(property.isKeyword());

		assertType("additionalKeyword", "keyword");
		assertType("additionalText", "text");

		_assertDefaultLiferayFields();
	}

	private void _assertDefaultLiferayFields() throws Exception {
		assertType(Field.STATUS, "keyword");
		assertType(Field.SUBTITLE, "text");

		String field = RandomTestUtil.randomString() + "_double";

		_indexOneDocument(field, RandomTestUtil.randomDouble());

		assertType(field, "double");
	}

	private void _assertIndexSettings(
		int numberOfReplicas, int numberOfShards) {

		IndexSettings indexSettings1 = _getIndexSettings();

		IndexSettings indexSettings2 = indexSettings1.index();

		Assert.assertEquals(
			String.valueOf(numberOfReplicas),
			indexSettings2.numberOfReplicas());
		Assert.assertEquals(
			String.valueOf(numberOfShards), indexSettings2.numberOfShards());
	}

	private void _assertMappings(String... fieldNames) {
		String indexName = _indexFactoryFixture.getIndexName();

		GetIndexResponse getIndexResponse = getIndex(indexName);

		IndexState indexState = getIndexResponse.get(indexName);

		TypeMapping typeMapping = indexState.mappings();

		Map<String, Property> properties = typeMapping.properties();

		Set<String> keySet = properties.keySet();

		Assert.assertThat(keySet, CoreMatchers.hasItems(fieldNames));
	}

	private String _getAdditionalTypeMappings() {
		try {
			return ResourceUtil.getResourceAsString(
				getClass(), "IndexFactoryTest-additionalTypeMappings.json");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private IndexSettings _getIndexSettings() {
		String indexName = _indexFactoryFixture.getIndexName();

		GetIndexResponse getIndexResponse = getIndex(indexName);

		IndexState indexState = getIndexResponse.get(indexName);

		return indexState.settings();
	}

	private String _getLegacyAdditionalTypeMappings() {
		try {
			return ResourceUtil.getResourceAsString(
				getClass(),
				"IndexFactoryTest-legacyAdditionalTypeMappings.json");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getLegacyOverrideTypeMappings() {
		try {
			return ResourceUtil.getResourceAsString(
				getClass(), "IndexFactoryTest-legacyOverrideTypeMappings.json");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getOverrideTypeMappings() {
		try {
			return ResourceUtil.getResourceAsString(
				getClass(), "IndexFactoryTest-overrideTypeMappings.json");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void _indexOneDocument(String field) {
		_indexOneDocument(field, RandomTestUtil.randomString());
	}

	private void _indexOneDocument(String field, Object value) {
		_singleFieldFixture.setField(field);

		_singleFieldFixture.indexDocument(value);
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private IndexFactory _indexFactory;
	private IndexFactoryFixture _indexFactoryFixture;
	private final OpenSearchConfigurationWrapper
		_openSearchConfigurationWrapper = Mockito.mock(
			OpenSearchConfigurationWrapperImpl.class);
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private SingleFieldFixture _singleFieldFixture;

}