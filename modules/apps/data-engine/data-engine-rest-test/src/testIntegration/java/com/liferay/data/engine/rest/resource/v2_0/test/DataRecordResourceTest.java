/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.dto.v2_0.DataListView;
import com.liferay.data.engine.rest.client.dto.v2_0.DataRecord;
import com.liferay.data.engine.rest.client.dto.v2_0.DataRecordCollection;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.resource.v2_0.DataListViewResource;
import com.liferay.data.engine.rest.client.resource.v2_0.DataRecordCollectionResource;
import com.liferay.data.engine.rest.resource.v2_0.test.util.DataDefinitionTestUtil;
import com.liferay.data.engine.rest.resource.v2_0.test.util.DataRecordCollectionTestUtil;
import com.liferay.data.engine.rest.resource.v2_0.test.util.content.type.test.util.ModelResourceActionTestUtil;
import com.liferay.data.engine.rest.strategy.util.DataRecordValueKeyUtil;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jeyvison Nascimento
 * @author Leonardo Barros
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataRecordResourceTest extends BaseDataRecordResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDataRecordResourceTestCase.setUpClass();

		ModelResourceActionTestUtil.populateModelResourceAction(
			_resourceActions);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ModelResourceActionTestUtil.deleteModelResourceAction(_resourceActions);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		DDMStructure ddmStructure = DataDefinitionTestUtil.addDDMStructure(
			testGroup);

		_dataDefinitionId = ddmStructure.getStructureId();

		DDLRecordSet ddlRecordSet = DataRecordCollectionTestUtil.addRecordSet(
			ddmStructure, testGroup, _resourceLocalService);

		_dataRecordCollectionId = ddlRecordSet.getRecordSetId();

		_irrelevantDDLRecordSet = DataRecordCollectionTestUtil.addRecordSet(
			ddmStructure, irrelevantGroup, _resourceLocalService);
	}

	@Override
	@Test
	public void testGetDataDefinitionDataRecordsPageWithSortInteger()
		throws Exception {

		super.testGetDataDefinitionDataRecordsPageWithSortInteger();

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				DataDefinition.toDTO(
					DataDefinitionTestUtil.read(
						"localized-data-definition.json")),
				testGroup.getGroupId());

		DataRecordCollectionResource.Builder builder =
			DataRecordCollectionResource.builder();

		DataRecordCollectionResource dataRecordCollectionResource =
			builder.authentication(
				"test@liferay.com", TestPropsValues.USER_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		DataRecordCollection dataDefinitionDataRecordCollection =
			dataRecordCollectionResource.getDataDefinitionDataRecordCollection(
				dataDefinition.getId());

		_dataRecordCollectionId = dataDefinitionDataRecordCollection.getId();

		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();

		DataRecord dataRecord1 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId,
				new DataRecord() {
					{
						dataRecordCollectionId = _dataRecordCollectionId;
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"Numeric", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.put(
								"en_US", "10"
							).build()
						).build();
					}
				});

		DataRecord dataRecord2 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId,
				new DataRecord() {
					{
						dataRecordCollectionId = _dataRecordCollectionId;
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"Numeric", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.put(
								"en_US", "20"
							).build()
						).build();
					}
				});

		// Sort by numeric

		Page<DataRecord> sortByNumericAscPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/Numeric:asc");

		assertEquals(
			Arrays.asList(dataRecord1, dataRecord2),
			(List<DataRecord>)sortByNumericAscPage.getItems());

		Page<DataRecord> sortByNumericDescPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/Numeric:desc");

		assertEquals(
			Arrays.asList(dataRecord2, dataRecord1),
			(List<DataRecord>)sortByNumericDescPage.getItems());
	}

	@Override
	@Test
	public void testGetDataRecordCollectionDataRecordExport() throws Exception {
		DataRecord dataRecord = testGetDataRecord_addDataRecord();

		assertHttpResponseStatusCode(
			200,
			dataRecordResource.
				getDataRecordCollectionDataRecordExportHttpResponse(
					dataRecord.getDataRecordCollectionId(),
					Pagination.of(1, 2)));
	}

	@Override
	@Test
	public void testGetDataRecordCollectionDataRecordsPage() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				DataDefinition.toDTO(
					DataDefinitionTestUtil.read(
						"localized-data-definition.json")),
				testGroup.getGroupId());

		_dataDefinitionId = dataDefinition.getId();

		DataRecordCollectionResource.Builder
			dataRecordCollectionResourceBuilder =
				DataRecordCollectionResource.builder();

		DataRecordCollectionResource dataRecordCollectionResource =
			dataRecordCollectionResourceBuilder.authentication(
				"test@liferay.com", TestPropsValues.USER_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		DataRecordCollection dataDefinitionDataRecordCollection =
			dataRecordCollectionResource.getDataDefinitionDataRecordCollection(
				_dataDefinitionId);

		_dataRecordCollectionId = dataDefinitionDataRecordCollection.getId();

		super.testGetDataRecordCollectionDataRecordsPage();

		// Retrieve data records according to fixed filters

		DataListViewResource.Builder dataListViewResourceBuilder =
			DataListViewResource.builder();

		DataListViewResource dataListViewResource =
			dataListViewResourceBuilder.authentication(
				"test@liferay.com", TestPropsValues.USER_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();

		DataRecord dataRecord1 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId,
				new DataRecord() {
					{
						dataRecordCollectionId = _dataRecordCollectionId;
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"SingleSelection",
								RandomTestUtil.randomString(), StringPool.BLANK,
								0),
							HashMapBuilder.put(
								"en_US", "Car"
							).build()
						).build();
					}
				});

		DataRecord dataRecord2 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId,
				new DataRecord() {
					{
						dataRecordCollectionId = _dataRecordCollectionId;
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"SingleSelection",
								RandomTestUtil.randomString(), StringPool.BLANK,
								0),
							HashMapBuilder.put(
								"en_US", "Boat"
							).build()
						).build();
					}
				});

		DataListView dataListView =
			dataListViewResource.postDataDefinitionDataListView(
				_dataDefinitionId,
				new DataListView() {
					{
						appliedFilters =
							LinkedHashMapBuilder.<String, Object>put(
								"SingleSelection", new String[] {"Car"}
							).build();
						dataDefinitionId = _dataDefinitionId;
						fieldNames = new String[] {"SingleSelection"};
					}
				});

		Page<DataRecord> singleSelectionFixedFilterPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId(),
				dataListView.getId(), null, Pagination.of(1, 2), null);

		assertEqualsIgnoringOrder(
			Arrays.asList(dataRecord1),
			(List<DataRecord>)singleSelectionFixedFilterPage.getItems());

		// Retrieve data records according to full term

		Page<DataRecord> searchFullTermPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId(),
				null, "Boat", Pagination.of(1, 2), null);

		assertEqualsIgnoringOrder(
			Arrays.asList(dataRecord2),
			(List<DataRecord>)searchFullTermPage.getItems());

		// Retrieve data records according to partial term

		Page<DataRecord> searchPartialTermPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId(),
				null, "Bo", Pagination.of(1, 2), null);

		assertEqualsIgnoringOrder(
			Arrays.asList(dataRecord2),
			(List<DataRecord>)searchPartialTermPage.getItems());
	}

	@Override
	@Test
	public void testGetDataRecordCollectionDataRecordsPageWithSortString()
		throws Exception {

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				DataDefinition.toDTO(
					DataDefinitionTestUtil.read(
						"localized-data-definition.json")),
				testGroup.getGroupId());

		_dataDefinitionId = dataDefinition.getId();

		DataRecordCollectionResource.Builder builder =
			DataRecordCollectionResource.builder();

		DataRecordCollectionResource dataRecordCollectionResource =
			builder.authentication(
				"test@liferay.com", TestPropsValues.USER_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		DataRecordCollection dataDefinitionDataRecordCollection =
			dataRecordCollectionResource.getDataDefinitionDataRecordCollection(
				_dataDefinitionId);

		_dataRecordCollectionId = dataDefinitionDataRecordCollection.getId();

		super.testGetDataRecordCollectionDataRecordsPageWithSortString();

		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();

		DataRecord dataRecord1 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId,
				new DataRecord() {
					{
						dataRecordCollectionId = _dataRecordCollectionId;
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"MultipleSelection",
								RandomTestUtil.randomString(), StringPool.BLANK,
								0),
							HashMapBuilder.put(
								"en_US", new String[] {"Apartment"}
							).build()
						).put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"SelectFromList", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.put(
								"en_US", new String[] {"Magazine"}
							).build()
						).put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"SingleSelection",
								RandomTestUtil.randomString(), StringPool.BLANK,
								0),
							HashMapBuilder.put(
								"en_US", "Car"
							).build()
						).put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"Text", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.put(
								"en_US", "aaa"
							).build()
						).build();
					}
				});
		DataRecord dataRecord2 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId,
				new DataRecord() {
					{
						dataRecordCollectionId = _dataRecordCollectionId;
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"MultipleSelection",
								RandomTestUtil.randomString(), StringPool.BLANK,
								0),
							HashMapBuilder.put(
								"en_US", new String[] {"House"}
							).build()
						).put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"SelectFromList", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.put(
								"en_US", new String[] {"Book"}
							).build()
						).put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"SingleSelection",
								RandomTestUtil.randomString(), StringPool.BLANK,
								0),
							HashMapBuilder.put(
								"en_US", "Boat"
							).build()
						).put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"Text", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.put(
								"en_US", "bbb"
							).build()
						).build();
					}
				});

		// Sort by multiple selection field

		Page<DataRecord> multipleSelectionAscPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/MultipleSelection:asc");

		assertEquals(
			Arrays.asList(dataRecord1, dataRecord2),
			(List<DataRecord>)multipleSelectionAscPage.getItems());

		Page<DataRecord> multipleSelectionDescPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/MultipleSelection:desc");

		assertEquals(
			Arrays.asList(dataRecord2, dataRecord1),
			(List<DataRecord>)multipleSelectionDescPage.getItems());

		// Sort by select from list

		Page<DataRecord> sortBySelectFromListAscPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/SelectFromList:asc");

		assertEquals(
			Arrays.asList(dataRecord2, dataRecord1),
			(List<DataRecord>)sortBySelectFromListAscPage.getItems());

		Page<DataRecord> sortBySelectFromListDescPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/SelectFromList:desc");

		assertEquals(
			Arrays.asList(dataRecord1, dataRecord2),
			(List<DataRecord>)sortBySelectFromListDescPage.getItems());

		// Sort by single selection

		Page<DataRecord> sortBySingleSelectionAscPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/SingleSelection:asc");

		assertEquals(
			Arrays.asList(dataRecord2, dataRecord1),
			(List<DataRecord>)sortBySingleSelectionAscPage.getItems());

		Page<DataRecord> sortBySingleSelectionDescPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/SingleSelection:desc");

		assertEquals(
			Arrays.asList(dataRecord1, dataRecord2),
			(List<DataRecord>)sortBySingleSelectionDescPage.getItems());

		// Sort by text

		Page<DataRecord> sortByTextAscPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/Text:asc");

		assertEquals(
			Arrays.asList(dataRecord1, dataRecord2),
			(List<DataRecord>)sortByTextAscPage.getItems());

		Page<DataRecord> sortByTextDescPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 2),
				"dataRecordValues/Text:desc");

		assertEquals(
			Arrays.asList(dataRecord2, dataRecord1),
			(List<DataRecord>)sortByTextDescPage.getItems());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDataDefinitionDataRecordsPage() throws Exception {
		super.testGraphQLGetDataDefinitionDataRecordsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetDataRecordCollectionDataRecordsPage()
		throws Exception {

		super.testGraphQLGetDataRecordCollectionDataRecordsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostDataDefinitionDataRecord() throws Exception {
		super.testGraphQLPostDataDefinitionDataRecord();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostDataRecordCollectionDataRecord()
		throws Exception {

		super.testGraphQLPostDataRecordCollectionDataRecord();
	}

	@Override
	@Test
	public void testPostDataDefinitionDataRecord() throws Exception {
		super.testPostDataDefinitionDataRecord();

		// Persist data for text in a fieldset

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinitionWithFieldset(
				testGroup.getGroupId());

		String fieldsetKey = StringBundler.concat(
			"FieldsGroup4Test$", RandomTestUtil.randomString(), "$0");

		DataRecord dataRecord = new DataRecord() {
			{
				dataRecordValues = HashMapBuilder.<String, Object>put(
					fieldsetKey, ""
				).put(
					StringBundler.concat(
						fieldsetKey, "#Text4Test$",
						RandomTestUtil.randomString(), "$0"),
					HashMapBuilder.<String, Object>put(
						"en_US", "Text Value"
					).build()
				).build();
			}
		};

		DataRecord postDataRecord =
			dataRecordResource.postDataDefinitionDataRecord(
				dataDefinition.getId(), dataRecord);

		Assert.assertEquals(
			dataRecord.getDataRecordValues(),
			postDataRecord.getDataRecordValues());
		assertValid(postDataRecord);
	}

	@Override
	@Test
	public void testPostDataRecordCollectionDataRecord() throws Exception {
		super.testPostDataRecordCollectionDataRecord();

		assertHttpResponseStatusCode(
			404,
			dataRecordResource.postDataRecordCollectionDataRecordHttpResponse(
				RandomTestUtil.randomLong(), randomDataRecord()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"dataRecordCollectionId", "dataRecordValues"};
	}

	@Override
	protected DataRecord randomDataRecord() {
		String fieldsGroupNameKey1 =
			DataRecordValueKeyUtil.createDataRecordValueKey(
				"MyFieldsGroup", "NKhHPIoI", StringPool.BLANK, 0);
		String fieldsGroupNameKey2 =
			DataRecordValueKeyUtil.createDataRecordValueKey(
				"MyFieldsGroup", "7xuXGHHn", StringPool.BLANK, 1);

		return new DataRecord() {
			{
				dataRecordCollectionId = _dataRecordCollectionId;
				dataRecordValues = HashMapBuilder.<String, Object>put(
					fieldsGroupNameKey1, StringPool.BLANK
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MyText", "y6vP0SgC", StringPool.BLANK, 0),
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MyNestedText1", "j6XHPwTW", fieldsGroupNameKey1, 0),
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MySeparator", "A8EDLwCA", fieldsGroupNameKey1, 0),
					StringPool.BLANK
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MySeparator", "DPC3rctI", fieldsGroupNameKey1, 1),
					StringPool.BLANK
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MyNestedText2", "429DxRpx", fieldsGroupNameKey1, 0),
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MyNestedText2", "Xbd2f4Eu", fieldsGroupNameKey1, 1),
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					fieldsGroupNameKey2, StringPool.BLANK
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MyNestedText1", "gzTlPtbc", fieldsGroupNameKey2, 0),
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MySeparator", "A2UlsuUk", fieldsGroupNameKey2, 0),
					StringPool.BLANK
				).put(
					DataRecordValueKeyUtil.createDataRecordValueKey(
						"MyNestedText2", "cW3tzejt", fieldsGroupNameKey2, 0),
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).build();
			}
		};
	}

	@Override
	protected DataRecord randomIrrelevantDataRecord() throws Exception {
		DataRecord randomIrrelevantDataRecord =
			super.randomIrrelevantDataRecord();

		randomIrrelevantDataRecord.setDataRecordCollectionId(
			_irrelevantDDLRecordSet.getRecordSetId());

		return randomIrrelevantDataRecord;
	}

	@Override
	protected DataRecord testDeleteDataRecord_addDataRecord() throws Exception {
		return dataRecordResource.postDataRecordCollectionDataRecord(
			_dataRecordCollectionId, randomDataRecord());
	}

	@Override
	protected Long testGetDataDefinitionDataRecordsPage_getDataDefinitionId()
		throws Exception {

		return _dataDefinitionId;
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataRecordsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected DataRecord testGetDataRecord_addDataRecord() throws Exception {
		return dataRecordResource.postDataRecordCollectionDataRecord(
			_dataRecordCollectionId, randomDataRecord());
	}

	@Override
	protected DataRecord
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				Long dataLayoutId, DataRecord dataRecord)
		throws Exception {

		return dataRecordResource.postDataRecordCollectionDataRecord(
			dataRecord.getDataRecordCollectionId(), dataRecord);
	}

	@Override
	protected Long
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId()
		throws Exception {

		return _dataRecordCollectionId;
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetDataRecordCollectionDataRecordsPage_getExpectedActions(
				Long dataRecordCollectionId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected DataRecord testGraphQLDataRecord_addDataRecord()
		throws Exception {

		return dataRecordResource.postDataRecordCollectionDataRecord(
			_dataRecordCollectionId, randomDataRecord());
	}

	@Override
	protected DataRecord testPatchDataRecord_addDataRecord() throws Exception {
		return dataRecordResource.postDataRecordCollectionDataRecord(
			_dataRecordCollectionId, randomDataRecord());
	}

	@Override
	protected DataRecord testPostDataRecordCollectionDataRecord_addDataRecord(
			DataRecord dataRecord)
		throws Exception {

		return dataRecordResource.postDataRecordCollectionDataRecord(
			dataRecord.getDataRecordCollectionId(), dataRecord);
	}

	@Override
	protected DataRecord testPutDataRecord_addDataRecord() throws Exception {
		return dataRecordResource.postDataRecordCollectionDataRecord(
			_dataRecordCollectionId, randomDataRecord());
	}

	@Inject
	private static ResourceActions _resourceActions;

	private long _dataDefinitionId;
	private long _dataRecordCollectionId;
	private DDLRecordSet _irrelevantDDLRecordSet;

	@Inject
	private ResourceLocalService _resourceLocalService;

}