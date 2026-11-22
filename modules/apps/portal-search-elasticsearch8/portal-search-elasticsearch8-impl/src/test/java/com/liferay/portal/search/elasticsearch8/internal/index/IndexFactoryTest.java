/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DynamicTemplate;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.util.NamedValue;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch8.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch8.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch8.internal.document.SingleFieldFixture;
import com.liferay.portal.search.elasticsearch8.internal.index.util.IndexFactoryCompanyIdRegistryUtil;
import com.liferay.portal.search.elasticsearch8.internal.query.QueryFactories;
import com.liferay.portal.search.elasticsearch8.internal.util.ResourceUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexMappingsContributor;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexSettingsContributor;
import com.liferay.portal.search.spi.index.listener.CompanyIndexListener;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);
		}

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
			_elasticsearchFixture.getElasticsearchClient(),
			new IndexName(_indexFactoryFixture.getIndexName()));

		_singleFieldFixture.setQueryFactory(QueryFactories.MATCH);
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
	public void testAddMultipleCompanyIndexSettingsContributors()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexSettingsContributor.class, companyId -> null,
				null));

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexSettingsContributor.class, companyId -> null,
				null));
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
				CompanyIndexMappingsContributor.class,
				new CompanyIndexMappingsContributor() {

					@Override
					public String contributeMappings() {
						return JSONUtil.put(
							"properties",
							JSONUtil.put(
								"additionalKeyword",
								JSONUtil.put(
									"store", true
								).put(
									"type", "text"
								))
						).toString();
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
				CompanyIndexSettingsContributor.class,
				new CompanyIndexSettingsContributor() {

					@Override
					public String contributeSettings(long companyId) {
						return JSONUtil.put(
							"index",
							JSONUtil.put(
								"number_of_replicas", "3"
							).put(
								"number_of_shards", "4"
							)
						).toString();
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
				CompanyIndexMappingsContributor.class,
				new CompanyIndexMappingsContributor() {

					@Override
					public String contributeMappings() {
						return _getAdditionalTypeMappings();
					}

				},
				null));

		initializeIndex();

		_assertAdditionalTypeMappings();
	}

	@Test
	public void testContributeMappingsAfterFailedLatePut() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		IndexFactoryCompanyIdRegistryUtil.registerCompanyId(companyId);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CompanyIndexHelper.class.getName(), LoggerTestUtil.ERROR)) {

			_serviceRegistrations.add(
				_bundleContext.registerService(
					CompanyIndexMappingsContributor.class,
					new CompanyIndexMappingsContributor() {

						@Override
						public String contributeMappings() {
							return _getAdditionalTypeMappings();
						}

						@Override
						public String toString() {
							return "TestCompanyIndexMappingsContributor";
						}

					},
					null));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

			CompanyIndexHelper companyIndexHelper =
				_indexFactoryFixture.getCompanyIndexHelper();

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to put mappings for index ",
					companyIndexHelper.getIndexName(companyId),
					" with contributor TestCompanyIndexMappingsContributor"),
				logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			String message = throwable.getMessage();

			Assert.assertTrue(message.contains("index_not_found_exception"));
		}

		IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);

		initializeIndex();

		_assertAdditionalTypeMappings();
	}

	@Test
	public void testContributeMappingsCannotOverrideDefaultMappings()
		throws Exception {

		_serviceRegistrations.add(
			_bundleContext.registerService(
				CompanyIndexMappingsContributor.class,
				new CompanyIndexMappingsContributor() {

					@Override
					public String contributeMappings() {
						return _getOverrideTypeMappings();
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
				CompanyIndexSettingsContributor.class,
				new CompanyIndexSettingsContributor() {

					@Override
					public String contributeSettings(long companyId) {
						return JSONUtil.put(
							"index",
							JSONUtil.put(
								"number_of_replicas", "2"
							).put(
								"number_of_shards", "3"
							)
						).toString();
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
				CompanyIndexMappingsContributor.class,
				new CompanyIndexMappingsContributor() {

					@Override
					public String contributeMappings() {
						return JSONUtil.put(
							"contributedKeyword",
							JSONUtil.put(
								"store", true
							).put(
								"type", "keyword"
							)
						).toString();
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
	public void testRemoveCompanyIndexSettingsContributor() {
		ServiceRegistration<CompanyIndexSettingsContributor>
			serviceRegistration = _bundleContext.registerService(
				CompanyIndexSettingsContributor.class, companyId -> null, null);

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

		ElasticsearchClient elasticsearchClient =
			_elasticsearchFixture.getElasticsearchClient();

		FieldMappingAssert.assertAnalyzer(
			elasticsearchClient.indices(), analyzer, field,
			_indexFactoryFixture.getIndexName());
	}

	protected void assertNoAnalyzer(String field) throws Exception {
		assertAnalyzer(null, field);
	}

	protected void assertNoMapping(String field) throws Exception {
		assertType(field, null);
	}

	protected void assertType(String field, String type) throws Exception {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchFixture.getElasticsearchClient();

		FieldMappingAssert.assertType(
			elasticsearchClient.indices(), type, field,
			_indexFactoryFixture.getIndexName());
	}

	protected void deleteIndex() {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchFixture.getElasticsearchClient();

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		_indexFactory.deleteIndex(
			RandomTestUtil.randomLong(), elasticsearchIndicesClient);
	}

	protected void initializeIndex() throws Exception {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchFixture.getElasticsearchClient();

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		_indexFactory.initializeIndex(
			RandomTestUtil.randomLong(), elasticsearchIndicesClient);
	}

	private void _assertAdditionalTypeMappings() throws Exception {
		ElasticsearchClient elasticsearchClient =
			_elasticsearchFixture.getElasticsearchClient();

		ElasticsearchIndicesClient elasticsearchIndicesClient =
			elasticsearchClient.indices();

		GetMappingResponse getMappingResponse =
			elasticsearchIndicesClient.getMapping(
				GetMappingRequest.of(
					getMappingRequest -> getMappingRequest.index(
						_indexFactoryFixture.getIndexName())));

		Map<String, IndexMappingRecord> result = getMappingResponse.result();

		IndexMappingRecord indexMappingRecord = result.get(
			_indexFactoryFixture.getIndexName());

		TypeMapping typeMapping = indexMappingRecord.mappings();

		List<NamedValue<DynamicTemplate>> dynamicTemplatesList =
			typeMapping.dynamicTemplates();

		NamedValue<DynamicTemplate> dynamicTemplateNamedValue =
			dynamicTemplatesList.get(0);

		DynamicTemplate dynamicTemplate = dynamicTemplateNamedValue.value();

		List<String> match = dynamicTemplate.match();

		Assert.assertEquals("*_additional_mapping", match.get(0));

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

		GetIndexResponse getIndexResponse = _elasticsearchFixture.getIndex(
			indexName);

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
		String name = _indexFactoryFixture.getIndexName();

		GetIndexResponse getIndexResponse = _elasticsearchFixture.getIndex(
			name);

		IndexState indexState = getIndexResponse.get(name);

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