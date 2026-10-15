/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilder;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;
import com.liferay.site.pim.site.initializer.internal.link.VariantPIMLinkType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class BasePIMConnectorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_complexQueryPartBuilder.build()
		).thenReturn(
			Mockito.mock(ComplexQueryPart.class)
		);

		Mockito.when(
			_complexQueryPartBuilderFactory.builder()
		).thenReturn(
			_complexQueryPartBuilder
		);

		Mockito.when(
			_filterFactory.create(Mockito.anyString(), Mockito.any())
		).thenReturn(
			Mockito.mock(Predicate.class)
		);

		Mockito.when(
			_language.get(LocaleUtil.US, _KEY)
		).thenReturn(
			"Test PIM Connector"
		);

		Mockito.when(
			_objectRelationship.getObjectRelationshipId()
		).thenReturn(
			_OBJECT_RELATIONSHIP_ID
		);

		_objectRelationshipLocalServiceUtilMockedStatic.when(
			() ->
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByExternalReferenceCode(
						Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			_objectRelationship
		);

		ReflectionTestUtil.setFieldValue(
			_pimConnector, "complexQueryPartBuilderFactory",
			_complexQueryPartBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_pimConnector, "filterFactory", _filterFactory);
		ReflectionTestUtil.setFieldValue(_pimConnector, "language", _language);
		ReflectionTestUtil.setFieldValue(
			_pimConnector, "objectDefinitionLocalService",
			_objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnector, "objectEntryLocalService", _objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnector, "searchRequestBuilderFactory",
			_searchRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(_pimConnector, "searcher", _searcher);
		ReflectionTestUtil.setFieldValue(_pimConnector, "sorts", _sorts);

		Mockito.when(
			_pimConnectorObjectEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_pimConnectorObjectEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_searchRequestBuilder.build()
		).thenReturn(
			_searchRequest
		);

		Mockito.when(
			_searchRequestBuilderFactory.builder()
		).thenReturn(
			_searchRequestBuilder
		);

		Mockito.when(
			_searcher.search(_searchRequest)
		).thenReturn(
			_searchResponse
		);

		Mockito.when(
			_sorts.field(Field.ENTRY_CLASS_PK, SortOrder.ASC)
		).thenReturn(
			Mockito.mock(FieldSort.class)
		);
	}

	@After
	public void tearDown() {
		_objectEntryLocalServiceUtilMockedStatic.close();
		_objectRelationshipLocalServiceUtilMockedStatic.close();
		_queriesUtilMockedStatic.close();
	}

	@Test
	public void testGetName() {
		Assert.assertEquals(
			"Test PIM Connector", _pimConnector.getName(LocaleUtil.US));
	}

	@Test
	public void testGetPIMFieldMappingObjectEntriesMap() throws Exception {
		_testGetPIMFieldMappingObjectEntriesMap();
		_testGetPIMFieldMappingObjectEntriesMapWithBlankRequiredMapping();
		_testGetPIMFieldMappingObjectEntriesMapWithFixedValueMapping();
		_testGetPIMFieldMappingObjectEntriesMapWithoutRequiredMapping();
	}

	@Test
	public void testGetPIMProductObjectEntriesMap() throws Exception {
		_testGetPIMProductObjectEntriesMap();
		_testGetPIMProductObjectEntriesMapWithFullSearchPage();
		_testGetPIMProductObjectEntriesMapWithVariantPIMLinks();
	}

	private void _assertPIMConnectorException() throws Exception {
		try {
			_pimConnector.getPIMFieldMappingObjectEntriesMap(
				_pimConnectorObjectEntry);

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				"a-required-channel-field-is-not-mapped",
				pimConnectorException.getMessage());
		}
	}

	private void _assertPIMProductObjectEntriesMap(
		String key, Map<String, List<ObjectEntry>> pimProductObjectEntriesMap,
		String... externalReferenceCodes) {

		Assert.assertEquals(
			Arrays.asList(externalReferenceCodes),
			TransformUtil.transform(
				pimProductObjectEntriesMap.get(key),
				ObjectEntry::getExternalReferenceCode));
	}

	private Document _mockDocument(long entryClassPK) {
		Document document = Mockito.mock(Document.class);

		Mockito.when(
			document.getLong(Field.ENTRY_CLASS_PK)
		).thenReturn(
			entryClassPK
		);

		return document;
	}

	private ObjectEntry _mockFieldMapping(
		String channelFieldName, int priority, String sourceFieldName,
		String value) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"channelFieldName", channelFieldName
			).put(
				"priority", priority
			).put(
				"sourceFieldName", sourceFieldName
			).put(
				"value", value
			).build()
		);

		return objectEntry;
	}

	private void _mockPIMFieldMappingObjectEntries(
			List<ObjectEntry> objectEntries)
		throws Exception {

		_objectEntryLocalServiceUtilMockedStatic.when(
			() -> ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(
				Mockito.anyLong(), Mockito.anyLong(),
				Mockito.nullable(Predicate.class), Mockito.anyBoolean(),
				Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.nullable(String.class), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.nullable(Sort[].class))
		).thenReturn(
			objectEntries
		);
	}

	private ObjectEntry _mockProductObjectEntry(
		long entryClassPK, String externalReferenceCode, long groupId) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			objectEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(entryClassPK)
		).thenReturn(
			objectEntry
		);

		return objectEntry;
	}

	private void _mockSearchResponse(List<Document> documents) {
		Mockito.when(
			_searchResponse.getDocuments()
		).thenReturn(
			documents, Collections.<Document>emptyList()
		);
	}

	private void _mockVariantPIMLinks(
			Map<String, String> clusterKeysByExternalReferenceCode,
			String primaryExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					_COMPANY_ID)
		).thenReturn(
			objectDefinition
		);

		Mockito.when(
			_objectEntryLocalService.getValuesList(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.nullable(Predicate.class),
				Mockito.nullable(String.class), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.nullable(Sort[].class))
		).thenReturn(
			TransformUtil.transform(
				clusterKeysByExternalReferenceCode.entrySet(),
				entry -> HashMapBuilder.<String, Serializable>put(
					"clusterKey", entry.getValue()
				).put(
					"primary",
					Objects.equals(entry.getKey(), primaryExternalReferenceCode)
				).put(
					"sourceClassExternalReferenceCode", entry.getKey()
				).build())
		);
	}

	private void _testGetPIMFieldMappingObjectEntriesMap() throws Exception {
		ObjectEntry nameObjectEntry = _mockFieldMapping(
			"name", 1, "name", StringPool.BLANK);
		ObjectEntry tag1ObjectEntry = _mockFieldMapping(
			"tags", 2, "tag1", StringPool.BLANK);
		ObjectEntry tag2ObjectEntry = _mockFieldMapping(
			"tags", 3, "tag2", StringPool.BLANK);

		_mockPIMFieldMappingObjectEntries(
			ListUtil.fromArray(
				nameObjectEntry, tag1ObjectEntry, tag2ObjectEntry));

		Assert.assertEquals(
			HashMapBuilder.put(
				"name", Collections.singletonList(nameObjectEntry)
			).put(
				"tags", Arrays.asList(tag1ObjectEntry, tag2ObjectEntry)
			).build(),
			_pimConnector.getPIMFieldMappingObjectEntriesMap(
				_pimConnectorObjectEntry));
	}

	private void _testGetPIMFieldMappingObjectEntriesMapWithBlankRequiredMapping()
		throws Exception {

		_mockPIMFieldMappingObjectEntries(
			ListUtil.fromArray(
				_mockFieldMapping(
					"name", 1, StringPool.BLANK, StringPool.BLANK)));

		_assertPIMConnectorException();
	}

	private void _testGetPIMFieldMappingObjectEntriesMapWithFixedValueMapping()
		throws Exception {

		ObjectEntry objectEntry = _mockFieldMapping(
			"name", 1, StringPool.BLANK, "A shirt");

		_mockPIMFieldMappingObjectEntries(ListUtil.fromArray(objectEntry));

		Assert.assertEquals(
			HashMapBuilder.put(
				"name", Collections.singletonList(objectEntry)
			).build(),
			_pimConnector.getPIMFieldMappingObjectEntriesMap(
				_pimConnectorObjectEntry));
	}

	private void _testGetPIMFieldMappingObjectEntriesMapWithoutRequiredMapping()
		throws Exception {

		_mockPIMFieldMappingObjectEntries(
			ListUtil.fromArray(
				_mockFieldMapping("tags", 1, "tag", StringPool.BLANK)));

		_assertPIMConnectorException();
	}

	private void _testGetPIMProductObjectEntriesMap() throws Exception {
		_mockProductObjectEntry(1, "SKU-1", _GROUP_ID);
		_mockProductObjectEntry(2, "SKU-2", _GROUP_ID);
		_mockSearchResponse(
			ListUtil.fromArray(_mockDocument(1), _mockDocument(2)));

		Map<String, List<ObjectEntry>> pimProductObjectEntriesMap =
			_pimConnector.getPIMProductObjectEntriesMap(_COMPANY_ID);

		Assert.assertEquals(
			Arrays.asList("SKU-1", "SKU-2"),
			new ArrayList<>(pimProductObjectEntriesMap.keySet()));

		_assertPIMProductObjectEntriesMap(
			"SKU-1", pimProductObjectEntriesMap, "SKU-1");
		_assertPIMProductObjectEntriesMap(
			"SKU-2", pimProductObjectEntriesMap, "SKU-2");

		_queriesUtilMockedStatic.verify(
			() -> QueriesUtil.term(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED));
	}

	private void _testGetPIMProductObjectEntriesMapWithFullSearchPage()
		throws Exception {

		_mockProductObjectEntry(1, "SKU-1", _GROUP_ID);
		_mockProductObjectEntry(2, "SKU-2", _GROUP_ID);

		List<Document> documents = new ArrayList<>(
			Collections.nCopies(_SEARCH_SIZE - 1, _mockDocument(1)));

		documents.add(_mockDocument(2));

		_mockSearchResponse(documents);

		Mockito.clearInvocations(_searcher);

		Map<String, List<ObjectEntry>> pimProductObjectEntriesMap =
			_pimConnector.getPIMProductObjectEntriesMap(_COMPANY_ID);

		Mockito.verify(
			_searcher, Mockito.times(2)
		).search(
			_searchRequest
		);

		Assert.assertEquals(
			pimProductObjectEntriesMap.toString(), 2,
			pimProductObjectEntriesMap.size());

		List<ObjectEntry> objectEntries = pimProductObjectEntriesMap.get(
			"SKU-1");

		Assert.assertEquals(
			objectEntries.toString(), _SEARCH_SIZE - 1, objectEntries.size());

		_assertPIMProductObjectEntriesMap(
			"SKU-2", pimProductObjectEntriesMap, "SKU-2");
	}

	private void _testGetPIMProductObjectEntriesMapWithVariantPIMLinks()
		throws Exception {

		_mockProductObjectEntry(1, "SKU-1", _GROUP_ID);
		_mockProductObjectEntry(2, "SKU-2", _GROUP_ID);
		_mockProductObjectEntry(3, "SKU-3", _GROUP_ID);
		_mockProductObjectEntry(4, "SKU-4", _GROUP_ID + 1);
		_mockSearchResponse(
			ListUtil.fromArray(
				_mockDocument(1), _mockDocument(2), _mockDocument(3),
				_mockDocument(4)));
		_mockVariantPIMLinks(
			HashMapBuilder.put(
				"SKU-1", "CLUSTER-1"
			).put(
				"SKU-2", "CLUSTER-1"
			).build(),
			"SKU-2");

		Map<String, List<ObjectEntry>> pimProductObjectEntriesMap =
			_pimConnector.getPIMProductObjectEntriesMap(_COMPANY_ID);

		Assert.assertEquals(
			Arrays.asList("CLUSTER-1", "SKU-3", "SKU-4"),
			new ArrayList<>(pimProductObjectEntriesMap.keySet()));

		_assertPIMProductObjectEntriesMap(
			"CLUSTER-1", pimProductObjectEntriesMap, "SKU-2", "SKU-1");
		_assertPIMProductObjectEntriesMap(
			"SKU-3", pimProductObjectEntriesMap, "SKU-3");
		_assertPIMProductObjectEntriesMap(
			"SKU-4", pimProductObjectEntriesMap, "SKU-4");

		Mockito.verify(
			_filterFactory, Mockito.times(2)
		).create(
			Mockito.eq("type eq '" + VariantPIMLinkType.TYPE + "'"),
			Mockito.any()
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					_COMPANY_ID)
		).thenReturn(
			null
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _KEY = RandomTestUtil.randomString();

	private static final long _OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_NAME = new PIMConnectorChannelField(
			"name", false, "name", true,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_TAGS = new PIMConnectorChannelField(
			"tags", true, "tags", false,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final int _SEARCH_SIZE = 1000;

	private final ComplexQueryPartBuilder _complexQueryPartBuilder =
		Mockito.mock(ComplexQueryPartBuilder.class, Mockito.RETURNS_SELF);
	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory = Mockito.mock(
			ComplexQueryPartBuilderFactory.class);
	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final Language _language = Mockito.mock(Language.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final MockedStatic<ObjectEntryLocalServiceUtil>
		_objectEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectEntryLocalServiceUtil.class);
	private final ObjectRelationship _objectRelationship = Mockito.mock(
		ObjectRelationship.class);
	private final MockedStatic<ObjectRelationshipLocalServiceUtil>
		_objectRelationshipLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectRelationshipLocalServiceUtil.class);
	private final BasePIMConnector _pimConnector = new TestPIMConnector();
	private final ObjectEntry _pimConnectorObjectEntry = Mockito.mock(
		ObjectEntry.class);
	private final MockedStatic<QueriesUtil> _queriesUtilMockedStatic =
		Mockito.mockStatic(QueriesUtil.class);
	private final SearchRequest _searchRequest = Mockito.mock(
		SearchRequest.class);
	private final SearchRequestBuilder _searchRequestBuilder = Mockito.mock(
		SearchRequestBuilder.class, Mockito.RETURNS_SELF);
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory =
		Mockito.mock(SearchRequestBuilderFactory.class);
	private final SearchResponse _searchResponse = Mockito.mock(
		SearchResponse.class);
	private final Searcher _searcher = Mockito.mock(Searcher.class);
	private final Sorts _sorts = Mockito.mock(Sorts.class);

	private class TestPIMConnector extends BasePIMConnector {

		@Override
		public String execute(ObjectEntry pimConnectorObjectEntry) {
			return StringPool.BLANK;
		}

		@Override
		public String getKey() {
			return _KEY;
		}

		@Override
		public List<PIMConnectorChannelField> getPIMConnectorChannelFields() {
			return ListUtil.fromArray(
				_PIM_CONNECTOR_CHANNEL_FIELD_NAME,
				_PIM_CONNECTOR_CHANNEL_FIELD_TAGS);
		}

		@Override
		public boolean isActive(long companyId) {
			return true;
		}

	}

}