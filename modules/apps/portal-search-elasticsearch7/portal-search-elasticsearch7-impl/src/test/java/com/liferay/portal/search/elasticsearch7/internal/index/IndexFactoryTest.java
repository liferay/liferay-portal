/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch7.internal.document.SingleFieldFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.QueryBuilderFactories;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexConfigurationContributor;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.MappingsHelper;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.SettingsHelper;
import com.liferay.portal.search.spi.index.listener.CompanyIndexListener;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author André de Oliveira
 */
public class IndexFactoryTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			IndexFactoryTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() throws Exception {
		_indexFactoryFixture = new IndexFactoryFixture(
			_elasticsearchFixture, testName.getMethodName());

		_indexFactory = _indexFactoryFixture.getIndexFactory();

		CompanyIndexHelper companyIndexHelper =
			_indexFactoryFixture.getCompanyIndexHelper();

		Mockito.reset(_elasticsearchConfigurationWrapper);

		ReflectionTestUtil.setFieldValue(
			companyIndexHelper, "_elasticsearchConfigurationWrapper",
			_elasticsearchConfigurationWrapper);

		ReflectionTestUtil.setFieldValue(
			_indexFactory, "_companyIndexHelper", companyIndexHelper);
		ReflectionTestUtil.setFieldValue(
			_indexFactory, "_elasticsearchConfigurationWrapper",
			_elasticsearchConfigurationWrapper);

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexMaxResultWindow()
		).thenReturn(
			10000
		);

		_singleFieldFixture = new SingleFieldFixture(
			_elasticsearchFixture.getRestHighLevelClient(),
			new IndexName(_indexFactoryFixture.getIndexName()));

		_singleFieldFixture.setQueryBuilderFactory(QueryBuilderFactories.MATCH);
	}

	@After
	public void tearDown() {
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
			_elasticsearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			"index.number_of_replicas: 1\nindex.number_of_shards: 2"
		);

		initializeIndex();

		_assertIndexSettings(1, 2);
	}

	@Test
	public void testAdditionalTypeMappings() throws Exception {
		Mockito.when(
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
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
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
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
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
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
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
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
			_elasticsearchConfigurationWrapper.additionalIndexConfigurations()
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
			_elasticsearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			StringPool.SPACE
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexNumberOfReplicas()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexNumberOfShards()
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
			_elasticsearchConfigurationWrapper.indexNumberOfReplicas()
		).thenReturn(
			"1"
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexNumberOfShards()
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
			_elasticsearchConfigurationWrapper.indexNumberOfReplicas()
		).thenReturn(
			"1"
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
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
			_elasticsearchConfigurationWrapper.overrideTypeMappings()
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
			_elasticsearchConfigurationWrapper.overrideTypeMappings()
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
			_elasticsearchConfigurationWrapper.additionalIndexConfigurations()
		).thenReturn(
			"index.number_of_replicas: 1\nindex.number_of_shards: 2"
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.overrideTypeMappings()
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
			_elasticsearchConfigurationWrapper.additionalTypeMappings()
		).thenReturn(
			_getAdditionalTypeMappings()
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.overrideTypeMappings()
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
			_elasticsearchConfigurationWrapper.overrideTypeMappings()
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

		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		FieldMappingAssert.assertAnalyzer(
			analyzer, field, _indexFactoryFixture.getIndexName(),
			restHighLevelClient.indices());
	}

	protected void assertNoAnalyzer(String field) throws Exception {
		assertAnalyzer(null, field);
	}

	protected void assertNoMapping(String field) throws Exception {
		assertType(field, null);
	}

	protected void assertType(String field, String type) throws Exception {
		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		FieldMappingAssert.assertType(
			type, field, _indexFactoryFixture.getIndexName(),
			restHighLevelClient.indices());
	}

	protected void deleteIndex() {
		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		IndicesClient indicesClient = restHighLevelClient.indices();

		_indexFactory.deleteIndex(RandomTestUtil.randomLong(), indicesClient);
	}

	protected void initializeIndex() throws Exception {
		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		IndicesClient indicesClient = restHighLevelClient.indices();

		_indexFactory.initializeIndex(
			RandomTestUtil.randomLong(), indicesClient);
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
		GetMappingsRequest getMappingsRequest = new GetMappingsRequest();

		getMappingsRequest.indices(_indexFactoryFixture.getIndexName());

		GetMappingsResponse getMappingsResponse = _getGetMappingsResponse(
			getMappingsRequest);

		ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetadata>>
			mappingsResponseMappings = getMappingsResponse.getMappings();

		ImmutableOpenMap<String, MappingMetadata> mappings =
			mappingsResponseMappings.get(_indexFactoryFixture.getIndexName());

		MappingMetadata mappingMetadata = mappings.get("_doc");

		Map<String, Object> sourceMap = mappingMetadata.getSourceAsMap();

		ArrayList<Object> dynamicTemplates = (ArrayList<Object>)sourceMap.get(
			"dynamic_templates");

		Map<String, Object> dynamicTemplate =
			(Map<String, Object>)dynamicTemplates.get(0);

		Map<String, Object> dynamicTemplateProperties =
			(Map<String, Object>)dynamicTemplate.get(
				"template_additional_mapping");

		Assert.assertEquals(
			"*_additional_mapping", dynamicTemplateProperties.get("match"));

		Map<String, Object> dynamicTemplateMappingProperties =
			(Map<String, Object>)dynamicTemplateProperties.get("mapping");

		Assert.assertEquals(
			"keyword", dynamicTemplateMappingProperties.get("type"));

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

		Settings settings = _getIndexSettings();

		Assert.assertEquals(
			String.valueOf(numberOfReplicas),
			settings.get("index.number_of_replicas"));
		Assert.assertEquals(
			String.valueOf(numberOfShards),
			settings.get("index.number_of_shards"));
	}

	private void _assertMappings(String... fieldNames) {
		String indexName = _indexFactoryFixture.getIndexName();

		GetIndexResponse getIndexResponse = _elasticsearchFixture.getIndex(
			indexName);

		Map<String, MappingMetadata> mappings = getIndexResponse.getMappings();

		MappingMetadata mappingMetadata = mappings.get(indexName);

		Map<String, Object> map = _getPropertiesMap(mappingMetadata);

		Set<String> set = map.keySet();

		Assert.assertThat(set, CoreMatchers.hasItems(fieldNames));
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

	private GetMappingsResponse _getGetMappingsResponse(
		GetMappingsRequest getMappingsRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.getMapping(
				getMappingsRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private Settings _getIndexSettings() {
		String name = _indexFactoryFixture.getIndexName();

		GetIndexResponse getIndexResponse = _elasticsearchFixture.getIndex(
			name);

		Map<String, Settings> map = getIndexResponse.getSettings();

		return map.get(name);
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

	private Map<String, Object> _getPropertiesMap(
		MappingMetadata mappingMetadata) {

		Map<String, Object> map = mappingMetadata.getSourceAsMap();

		return (Map<String, Object>)map.get("properties");
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
	private static ElasticsearchFixture _elasticsearchFixture;

	private final ElasticsearchConfigurationWrapper
		_elasticsearchConfigurationWrapper = Mockito.mock(
			ElasticsearchConfigurationWrapper.class);
	private IndexFactory _indexFactory;
	private IndexFactoryFixture _indexFactoryFixture;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private SingleFieldFixture _singleFieldFixture;

}