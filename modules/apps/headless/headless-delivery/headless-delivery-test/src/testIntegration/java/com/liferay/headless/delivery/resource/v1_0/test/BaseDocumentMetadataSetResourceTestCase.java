/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentMetadataSet;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentMetadataSetResource;
import com.liferay.headless.delivery.client.serdes.v1_0.DocumentMetadataSetSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import java.lang.reflect.Method;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseDocumentMetadataSetResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		_documentMetadataSetResource.setContextCompany(testCompany);

		com.liferay.portal.kernel.model.User testCompanyAdminUser =
			UserTestUtil.getAdminUser(testCompany.getCompanyId());

		DocumentMetadataSetResource.Builder builder =
			DocumentMetadataSetResource.builder();

		documentMetadataSetResource = builder.authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DocumentMetadataSet documentMetadataSet1 = randomDocumentMetadataSet();

		String json = objectMapper.writeValueAsString(documentMetadataSet1);

		DocumentMetadataSet documentMetadataSet2 =
			DocumentMetadataSetSerDes.toDTO(json);

		Assert.assertTrue(equals(documentMetadataSet1, documentMetadataSet2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DocumentMetadataSet documentMetadataSet = randomDocumentMetadataSet();

		String json1 = objectMapper.writeValueAsString(documentMetadataSet);
		String json2 = DocumentMetadataSetSerDes.toJSON(documentMetadataSet);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		DocumentMetadataSet documentMetadataSet = randomDocumentMetadataSet();

		documentMetadataSet.setAssetLibraryKey(regex);
		documentMetadataSet.setDescription(regex);
		documentMetadataSet.setExternalReferenceCode(regex);
		documentMetadataSet.setName(regex);

		String json = DocumentMetadataSetSerDes.toJSON(documentMetadataSet);

		Assert.assertFalse(json.contains(regex));

		documentMetadataSet = DocumentMetadataSetSerDes.toDTO(json);

		Assert.assertEquals(regex, documentMetadataSet.getAssetLibraryKey());
		Assert.assertEquals(regex, documentMetadataSet.getDescription());
		Assert.assertEquals(
			regex, documentMetadataSet.getExternalReferenceCode());
		Assert.assertEquals(regex, documentMetadataSet.getName());
	}

	@Test
	public void testGetAssetLibraryDocumentMetadataSetsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryDocumentMetadataSetsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentMetadataSetsPage_getIrrelevantAssetLibraryId();

		Page<DocumentMetadataSet> page =
			documentMetadataSetResource.getAssetLibraryDocumentMetadataSetsPage(
				assetLibraryId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			DocumentMetadataSet irrelevantDocumentMetadataSet =
				testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
					irrelevantAssetLibraryId,
					randomIrrelevantDocumentMetadataSet());

			page =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						irrelevantAssetLibraryId,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentMetadataSet,
				(List<DocumentMetadataSet>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentMetadataSetsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		DocumentMetadataSet documentMetadataSet1 =
			testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
				assetLibraryId, randomDocumentMetadataSet());

		DocumentMetadataSet documentMetadataSet2 =
			testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
				assetLibraryId, randomDocumentMetadataSet());

		page =
			documentMetadataSetResource.getAssetLibraryDocumentMetadataSetsPage(
				assetLibraryId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			documentMetadataSet1, (List<DocumentMetadataSet>)page.getItems());
		assertContains(
			documentMetadataSet2, (List<DocumentMetadataSet>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentMetadataSetsPage_getExpectedActions(
				assetLibraryId));

		documentMetadataSetResource.deleteDocumentMetadataSet(
			documentMetadataSet1.getId());

		documentMetadataSetResource.deleteDocumentMetadataSet(
			documentMetadataSet2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentMetadataSetsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/asset-libraries/{assetLibraryId}/document-metadata-sets/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentMetadataSetsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentMetadataSetsPage_getAssetLibraryId();

		Page<DocumentMetadataSet> documentMetadataSetPage =
			documentMetadataSetResource.getAssetLibraryDocumentMetadataSetsPage(
				assetLibraryId, null);

		int totalCount = GetterUtil.getInteger(
			documentMetadataSetPage.getTotalCount());

		DocumentMetadataSet documentMetadataSet1 =
			testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
				assetLibraryId, randomDocumentMetadataSet());

		DocumentMetadataSet documentMetadataSet2 =
			testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
				assetLibraryId, randomDocumentMetadataSet());

		DocumentMetadataSet documentMetadataSet3 =
			testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
				assetLibraryId, randomDocumentMetadataSet());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentMetadataSet> page1 =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						assetLibraryId,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentMetadataSet1,
				(List<DocumentMetadataSet>)page1.getItems());

			Page<DocumentMetadataSet> page2 =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						assetLibraryId,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				documentMetadataSet2,
				(List<DocumentMetadataSet>)page2.getItems());

			Page<DocumentMetadataSet> page3 =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						assetLibraryId,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				documentMetadataSet3,
				(List<DocumentMetadataSet>)page3.getItems());
		}
		else {
			Page<DocumentMetadataSet> page1 =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						assetLibraryId, Pagination.of(1, totalCount + 2));

			List<DocumentMetadataSet> documentMetadataSets1 =
				(List<DocumentMetadataSet>)page1.getItems();

			Assert.assertEquals(
				documentMetadataSets1.toString(), totalCount + 2,
				documentMetadataSets1.size());

			Page<DocumentMetadataSet> page2 =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						assetLibraryId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentMetadataSet> documentMetadataSets2 =
				(List<DocumentMetadataSet>)page2.getItems();

			Assert.assertEquals(
				documentMetadataSets2.toString(), 1,
				documentMetadataSets2.size());

			Page<DocumentMetadataSet> page3 =
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						assetLibraryId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				documentMetadataSet1,
				(List<DocumentMetadataSet>)page3.getItems());
			assertContains(
				documentMetadataSet2,
				(List<DocumentMetadataSet>)page3.getItems());
			assertContains(
				documentMetadataSet3,
				(List<DocumentMetadataSet>)page3.getItems());
		}
	}

	protected DocumentMetadataSet
			testGetAssetLibraryDocumentMetadataSetsPage_addDocumentMetadataSet(
				Long assetLibraryId, DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSetResource.postAssetLibraryDocumentMetadataSet(
			assetLibraryId, documentMetadataSet);
	}

	protected Long
			testGetAssetLibraryDocumentMetadataSetsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentMetadataSetsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostAssetLibraryDocumentMetadataSet() throws Exception {
		DocumentMetadataSet randomDocumentMetadataSet =
			randomDocumentMetadataSet();

		DocumentMetadataSet postDocumentMetadataSet =
			testPostAssetLibraryDocumentMetadataSet_addDocumentMetadataSet(
				randomDocumentMetadataSet);

		assertEquals(randomDocumentMetadataSet, postDocumentMetadataSet);
		assertValid(postDocumentMetadataSet);
	}

	protected DocumentMetadataSet
			testPostAssetLibraryDocumentMetadataSet_addDocumentMetadataSet(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSetResource.postAssetLibraryDocumentMetadataSet(
			testGetAssetLibraryDocumentMetadataSetsPage_getAssetLibraryId(),
			documentMetadataSet);
	}

	@Test
	public void testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentMetadataSet documentMetadataSet =
			testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		assertHttpResponseStatusCode(
			204,
			documentMetadataSetResource.
				deleteAssetLibraryDocumentMetadataSetByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					documentMetadataSet.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentMetadataSetResource.
				getAssetLibraryDocumentMetadataSetByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					documentMetadataSet.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentMetadataSetResource.
				getAssetLibraryDocumentMetadataSetByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					documentMetadataSet.getExternalReferenceCode()));
	}

	protected Long
			testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DocumentMetadataSet
			testDeleteAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		DocumentMetadataSet postDocumentMetadataSet =
			testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		DocumentMetadataSet getDocumentMetadataSet =
			documentMetadataSetResource.
				getAssetLibraryDocumentMetadataSetByExternalReferenceCode(
					testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					postDocumentMetadataSet.getExternalReferenceCode());

		assertEquals(postDocumentMetadataSet, getDocumentMetadataSet);
		assertValid(getDocumentMetadataSet);
	}

	protected Long
			testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DocumentMetadataSet
			testGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		DocumentMetadataSet documentMetadataSet =
			testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		// No namespace

		Assert.assertTrue(
			equals(
				documentMetadataSet,
				DocumentMetadataSetSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryDocumentMetadataSetByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryId",
											"\"" +
												testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId() +
													"\"");

										put(
											"externalReferenceCode",
											"\"" +
												documentMetadataSet.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryDocumentMetadataSetByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentMetadataSet,
				DocumentMetadataSetSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"assetLibraryDocumentMetadataSetByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryId",
												"\"" +
													testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId() +
														"\"");

											put(
												"externalReferenceCode",
												"\"" +
													documentMetadataSet.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/assetLibraryDocumentMetadataSetByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryDocumentMetadataSetByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"assetLibraryDocumentMetadataSetByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId() +
												"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentMetadataSet
			testGraphQLGetAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return testGraphQLDocumentMetadataSet_addDocumentMetadataSet();
	}

	@Test
	public void testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		DocumentMetadataSet postDocumentMetadataSet =
			testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		DocumentMetadataSet randomDocumentMetadataSet =
			randomDocumentMetadataSet();

		DocumentMetadataSet putDocumentMetadataSet =
			documentMetadataSetResource.
				putAssetLibraryDocumentMetadataSetByExternalReferenceCode(
					testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					postDocumentMetadataSet.getExternalReferenceCode(),
					randomDocumentMetadataSet);

		assertEquals(randomDocumentMetadataSet, putDocumentMetadataSet);
		assertValid(putDocumentMetadataSet);

		DocumentMetadataSet getDocumentMetadataSet =
			documentMetadataSetResource.
				getAssetLibraryDocumentMetadataSetByExternalReferenceCode(
					testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					putDocumentMetadataSet.getExternalReferenceCode());

		assertEquals(randomDocumentMetadataSet, getDocumentMetadataSet);
		assertValid(getDocumentMetadataSet);

		DocumentMetadataSet newDocumentMetadataSet =
			testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_createDocumentMetadataSet();

		putDocumentMetadataSet =
			documentMetadataSetResource.
				putAssetLibraryDocumentMetadataSetByExternalReferenceCode(
					testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					newDocumentMetadataSet.getExternalReferenceCode(),
					newDocumentMetadataSet);

		assertEquals(newDocumentMetadataSet, putDocumentMetadataSet);
		assertValid(putDocumentMetadataSet);

		getDocumentMetadataSet =
			documentMetadataSetResource.
				getAssetLibraryDocumentMetadataSetByExternalReferenceCode(
					testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId(),
					putDocumentMetadataSet.getExternalReferenceCode());

		assertEquals(newDocumentMetadataSet, getDocumentMetadataSet);

		Assert.assertEquals(
			newDocumentMetadataSet.getExternalReferenceCode(),
			putDocumentMetadataSet.getExternalReferenceCode());
	}

	protected Long
			testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DocumentMetadataSet
			testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_createDocumentMetadataSet()
		throws Exception {

		return randomDocumentMetadataSet();
	}

	protected DocumentMetadataSet
			testPutAssetLibraryDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testDeleteDocumentMetadataSet() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentMetadataSet documentMetadataSet =
			testDeleteDocumentMetadataSet_addDocumentMetadataSet();

		assertHttpResponseStatusCode(
			204,
			documentMetadataSetResource.deleteDocumentMetadataSetHttpResponse(
				documentMetadataSet.getId()));

		assertHttpResponseStatusCode(
			404,
			documentMetadataSetResource.getDocumentMetadataSetHttpResponse(
				documentMetadataSet.getId()));

		assertHttpResponseStatusCode(
			404,
			documentMetadataSetResource.getDocumentMetadataSetHttpResponse(0L));
	}

	protected DocumentMetadataSet
			testDeleteDocumentMetadataSet_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testGraphQLDeleteDocumentMetadataSet() throws Exception {

		// No namespace

		DocumentMetadataSet documentMetadataSet1 =
			testGraphQLDeleteDocumentMetadataSet_addDocumentMetadataSet();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocumentMetadataSet",
						new HashMap<String, Object>() {
							{
								put(
									"documentMetadataSetId",
									documentMetadataSet1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocumentMetadataSet"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentMetadataSet",
					new HashMap<String, Object>() {
						{
							put(
								"documentMetadataSetId",
								documentMetadataSet1.getId());
						}
					},
					new GraphQLField("id"))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentMetadataSet documentMetadataSet2 =
			testGraphQLDeleteDocumentMetadataSet_addDocumentMetadataSet();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocumentMetadataSet",
							new HashMap<String, Object>() {
								{
									put(
										"documentMetadataSetId",
										documentMetadataSet2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocumentMetadataSet"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentMetadataSet",
						new HashMap<String, Object>() {
							{
								put(
									"documentMetadataSetId",
									documentMetadataSet2.getId());
							}
						},
						new GraphQLField("id")))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentMetadataSet
			testGraphQLDeleteDocumentMetadataSet_addDocumentMetadataSet()
		throws Exception {

		return testGraphQLDocumentMetadataSet_addDocumentMetadataSet();
	}

	@Test
	public void testGetDocumentMetadataSet() throws Exception {
		DocumentMetadataSet postDocumentMetadataSet =
			testGetDocumentMetadataSet_addDocumentMetadataSet();

		DocumentMetadataSet getDocumentMetadataSet =
			documentMetadataSetResource.getDocumentMetadataSet(
				postDocumentMetadataSet.getId());

		assertEquals(postDocumentMetadataSet, getDocumentMetadataSet);
		assertValid(getDocumentMetadataSet);
	}

	protected DocumentMetadataSet
			testGetDocumentMetadataSet_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testGraphQLGetDocumentMetadataSet() throws Exception {
		DocumentMetadataSet documentMetadataSet =
			testGraphQLGetDocumentMetadataSet_addDocumentMetadataSet();

		// No namespace

		Assert.assertTrue(
			equals(
				documentMetadataSet,
				DocumentMetadataSetSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentMetadataSet",
								new HashMap<String, Object>() {
									{
										put(
											"documentMetadataSetId",
											documentMetadataSet.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/documentMetadataSet"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentMetadataSet,
				DocumentMetadataSetSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentMetadataSet",
									new HashMap<String, Object>() {
										{
											put(
												"documentMetadataSetId",
												documentMetadataSet.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentMetadataSet"))));
	}

	@Test
	public void testGraphQLGetDocumentMetadataSetNotFound() throws Exception {
		Long irrelevantDocumentMetadataSetId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentMetadataSet",
						new HashMap<String, Object>() {
							{
								put(
									"documentMetadataSetId",
									irrelevantDocumentMetadataSetId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"documentMetadataSet",
							new HashMap<String, Object>() {
								{
									put(
										"documentMetadataSetId",
										irrelevantDocumentMetadataSetId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentMetadataSet
			testGraphQLGetDocumentMetadataSet_addDocumentMetadataSet()
		throws Exception {

		return testGraphQLDocumentMetadataSet_addDocumentMetadataSet();
	}

	@Test
	public void testGetSiteDocumentMetadataSetsPage() throws Exception {
		Long siteId = testGetSiteDocumentMetadataSetsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDocumentMetadataSetsPage_getIrrelevantSiteId();

		Page<DocumentMetadataSet> page =
			documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
				siteId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			DocumentMetadataSet irrelevantDocumentMetadataSet =
				testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
					irrelevantSiteId, randomIrrelevantDocumentMetadataSet());

			page = documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
				irrelevantSiteId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentMetadataSet,
				(List<DocumentMetadataSet>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentMetadataSetsPage_getExpectedActions(
					irrelevantSiteId));
		}

		DocumentMetadataSet documentMetadataSet1 =
			testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
				siteId, randomDocumentMetadataSet());

		DocumentMetadataSet documentMetadataSet2 =
			testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
				siteId, randomDocumentMetadataSet());

		page = documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
			siteId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			documentMetadataSet1, (List<DocumentMetadataSet>)page.getItems());
		assertContains(
			documentMetadataSet2, (List<DocumentMetadataSet>)page.getItems());
		assertValid(
			page,
			testGetSiteDocumentMetadataSetsPage_getExpectedActions(siteId));

		documentMetadataSetResource.deleteDocumentMetadataSet(
			documentMetadataSet1.getId());

		documentMetadataSetResource.deleteDocumentMetadataSet(
			documentMetadataSet2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentMetadataSetsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/document-metadata-sets/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentMetadataSetsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteDocumentMetadataSetsPage_getSiteId();

		Page<DocumentMetadataSet> documentMetadataSetPage =
			documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
				siteId, null);

		int totalCount = GetterUtil.getInteger(
			documentMetadataSetPage.getTotalCount());

		DocumentMetadataSet documentMetadataSet1 =
			testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
				siteId, randomDocumentMetadataSet());

		DocumentMetadataSet documentMetadataSet2 =
			testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
				siteId, randomDocumentMetadataSet());

		DocumentMetadataSet documentMetadataSet3 =
			testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
				siteId, randomDocumentMetadataSet());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentMetadataSet> page1 =
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentMetadataSet1,
				(List<DocumentMetadataSet>)page1.getItems());

			Page<DocumentMetadataSet> page2 =
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentMetadataSet2,
				(List<DocumentMetadataSet>)page2.getItems());

			Page<DocumentMetadataSet> page3 =
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentMetadataSet3,
				(List<DocumentMetadataSet>)page3.getItems());
		}
		else {
			Page<DocumentMetadataSet> page1 =
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					siteId, Pagination.of(1, totalCount + 2));

			List<DocumentMetadataSet> documentMetadataSets1 =
				(List<DocumentMetadataSet>)page1.getItems();

			Assert.assertEquals(
				documentMetadataSets1.toString(), totalCount + 2,
				documentMetadataSets1.size());

			Page<DocumentMetadataSet> page2 =
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					siteId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentMetadataSet> documentMetadataSets2 =
				(List<DocumentMetadataSet>)page2.getItems();

			Assert.assertEquals(
				documentMetadataSets2.toString(), 1,
				documentMetadataSets2.size());

			Page<DocumentMetadataSet> page3 =
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					siteId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				documentMetadataSet1,
				(List<DocumentMetadataSet>)page3.getItems());
			assertContains(
				documentMetadataSet2,
				(List<DocumentMetadataSet>)page3.getItems());
			assertContains(
				documentMetadataSet3,
				(List<DocumentMetadataSet>)page3.getItems());
		}
	}

	protected DocumentMetadataSet
			testGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet(
				Long siteId, DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			siteId, documentMetadataSet);
	}

	protected Long testGetSiteDocumentMetadataSetsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteDocumentMetadataSetsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentMetadataSetsPage() throws Exception {
		Long siteId = testGetSiteDocumentMetadataSetsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documentMetadataSets",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);

					put("siteKey", "\"" + siteId + "\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentMetadataSetsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentMetadataSets");

		long totalCount = documentMetadataSetsJSONObject.getLong("totalCount");

		DocumentMetadataSet documentMetadataSet1 =
			testGraphQLGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet();
		DocumentMetadataSet documentMetadataSet2 =
			testGraphQLGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet();

		documentMetadataSetsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentMetadataSets");

		Assert.assertEquals(
			totalCount + 2,
			documentMetadataSetsJSONObject.getLong("totalCount"));

		assertContains(
			documentMetadataSet1,
			Arrays.asList(
				DocumentMetadataSetSerDes.toDTOs(
					documentMetadataSetsJSONObject.getString("items"))));
		assertContains(
			documentMetadataSet2,
			Arrays.asList(
				DocumentMetadataSetSerDes.toDTOs(
					documentMetadataSetsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentMetadataSetsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentMetadataSets");

		Assert.assertEquals(
			totalCount + 2,
			documentMetadataSetsJSONObject.getLong("totalCount"));

		assertContains(
			documentMetadataSet1,
			Arrays.asList(
				DocumentMetadataSetSerDes.toDTOs(
					documentMetadataSetsJSONObject.getString("items"))));
		assertContains(
			documentMetadataSet2,
			Arrays.asList(
				DocumentMetadataSetSerDes.toDTOs(
					documentMetadataSetsJSONObject.getString("items"))));
	}

	protected DocumentMetadataSet
			testGraphQLGetSiteDocumentMetadataSetsPage_addDocumentMetadataSet()
		throws Exception {

		return testGraphQLDocumentMetadataSet_addDocumentMetadataSet();
	}

	@Test
	public void testPostSiteDocumentMetadataSet() throws Exception {
		DocumentMetadataSet randomDocumentMetadataSet =
			randomDocumentMetadataSet();

		DocumentMetadataSet postDocumentMetadataSet =
			testPostSiteDocumentMetadataSet_addDocumentMetadataSet(
				randomDocumentMetadataSet);

		assertEquals(randomDocumentMetadataSet, postDocumentMetadataSet);
		assertValid(postDocumentMetadataSet);
	}

	protected DocumentMetadataSet
			testPostSiteDocumentMetadataSet_addDocumentMetadataSet(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGetSiteDocumentMetadataSetsPage_getSiteId(),
			documentMetadataSet);
	}

	@Test
	public void testGraphQLPostSiteDocumentMetadataSet() throws Exception {
		DocumentMetadataSet randomDocumentMetadataSet =
			randomDocumentMetadataSet();

		DocumentMetadataSet documentMetadataSet =
			testGraphQLDocumentMetadataSet_addDocumentMetadataSet(
				randomDocumentMetadataSet);

		Assert.assertTrue(
			equals(randomDocumentMetadataSet, documentMetadataSet));
	}

	@Test
	public void testDeleteSiteDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentMetadataSet documentMetadataSet =
			testDeleteSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		assertHttpResponseStatusCode(
			204,
			documentMetadataSetResource.
				deleteSiteDocumentMetadataSetByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						documentMetadataSet),
					documentMetadataSet.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentMetadataSetResource.
				getSiteDocumentMetadataSetByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						documentMetadataSet),
					documentMetadataSet.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentMetadataSetResource.
				getSiteDocumentMetadataSetByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						documentMetadataSet),
					documentMetadataSet.getExternalReferenceCode()));
	}

	protected Long
			testDeleteSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSet.getSiteId();
	}

	protected DocumentMetadataSet
			testDeleteSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testGetSiteDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		DocumentMetadataSet postDocumentMetadataSet =
			testGetSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		DocumentMetadataSet getDocumentMetadataSet =
			documentMetadataSetResource.
				getSiteDocumentMetadataSetByExternalReferenceCode(
					testGetSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						postDocumentMetadataSet),
					postDocumentMetadataSet.getExternalReferenceCode());

		assertEquals(postDocumentMetadataSet, getDocumentMetadataSet);
		assertValid(getDocumentMetadataSet);
	}

	protected Long
			testGetSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSet.getSiteId();
	}

	protected DocumentMetadataSet
			testGetSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	@Test
	public void testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		DocumentMetadataSet documentMetadataSet =
			testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		// No namespace

		Assert.assertTrue(
			equals(
				documentMetadataSet,
				DocumentMetadataSetSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentMetadataSetByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
													documentMetadataSet) +
														"\"");

										put(
											"externalReferenceCode",
											"\"" +
												documentMetadataSet.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/documentMetadataSetByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentMetadataSet,
				DocumentMetadataSetSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentMetadataSetByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
														documentMetadataSet) +
															"\"");

											put(
												"externalReferenceCode",
												"\"" +
													documentMetadataSet.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentMetadataSetByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSet.getSiteId();
	}

	@Test
	public void testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentMetadataSetByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"documentMetadataSetByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentMetadataSet
			testGraphQLGetSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return testGraphQLDocumentMetadataSet_addDocumentMetadataSet();
	}

	@Test
	public void testPutSiteDocumentMetadataSetByExternalReferenceCode()
		throws Exception {

		DocumentMetadataSet postDocumentMetadataSet =
			testPutSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet();

		DocumentMetadataSet randomDocumentMetadataSet =
			randomDocumentMetadataSet();

		DocumentMetadataSet putDocumentMetadataSet =
			documentMetadataSetResource.
				putSiteDocumentMetadataSetByExternalReferenceCode(
					testPutSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						postDocumentMetadataSet),
					postDocumentMetadataSet.getExternalReferenceCode(),
					randomDocumentMetadataSet);

		assertEquals(randomDocumentMetadataSet, putDocumentMetadataSet);
		assertValid(putDocumentMetadataSet);

		DocumentMetadataSet getDocumentMetadataSet =
			documentMetadataSetResource.
				getSiteDocumentMetadataSetByExternalReferenceCode(
					testPutSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						putDocumentMetadataSet),
					putDocumentMetadataSet.getExternalReferenceCode());

		assertEquals(randomDocumentMetadataSet, getDocumentMetadataSet);
		assertValid(getDocumentMetadataSet);

		DocumentMetadataSet newDocumentMetadataSet =
			testPutSiteDocumentMetadataSetByExternalReferenceCode_createDocumentMetadataSet();

		putDocumentMetadataSet =
			documentMetadataSetResource.
				putSiteDocumentMetadataSetByExternalReferenceCode(
					testPutSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						newDocumentMetadataSet),
					newDocumentMetadataSet.getExternalReferenceCode(),
					newDocumentMetadataSet);

		assertEquals(newDocumentMetadataSet, putDocumentMetadataSet);
		assertValid(putDocumentMetadataSet);

		getDocumentMetadataSet =
			documentMetadataSetResource.
				getSiteDocumentMetadataSetByExternalReferenceCode(
					testPutSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
						putDocumentMetadataSet),
					putDocumentMetadataSet.getExternalReferenceCode());

		assertEquals(newDocumentMetadataSet, getDocumentMetadataSet);

		Assert.assertEquals(
			newDocumentMetadataSet.getExternalReferenceCode(),
			putDocumentMetadataSet.getExternalReferenceCode());
	}

	protected Long
			testPutSiteDocumentMetadataSetByExternalReferenceCode_getSiteId(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		return documentMetadataSet.getSiteId();
	}

	protected DocumentMetadataSet
			testPutSiteDocumentMetadataSetByExternalReferenceCode_createDocumentMetadataSet()
		throws Exception {

		return randomDocumentMetadataSet();
	}

	protected DocumentMetadataSet
			testPutSiteDocumentMetadataSetByExternalReferenceCode_addDocumentMetadataSet()
		throws Exception {

		return documentMetadataSetResource.postSiteDocumentMetadataSet(
			testGroup.getGroupId(), randomDocumentMetadataSet());
	}

	protected void appendGraphQLFieldValue(StringBuilder sb, Object value)
		throws Exception {

		if (value instanceof Object[]) {
			StringBuilder arraySB = new StringBuilder("[");

			for (Object object : (Object[])value) {
				if (arraySB.length() > 1) {
					arraySB.append(", ");
				}

				arraySB.append("{");

				Class<?> clazz = object.getClass();

				for (java.lang.reflect.Field field :
						getDeclaredFields(clazz.getSuperclass())) {

					arraySB.append(field.getName());
					arraySB.append(": ");

					appendGraphQLFieldValue(arraySB, field.get(object));

					arraySB.append(", ");
				}

				arraySB.setLength(arraySB.length() - 2);

				arraySB.append("}");
			}

			arraySB.append("]");

			sb.append(arraySB.toString());
		}
		else if (value instanceof String) {
			sb.append("\"");
			sb.append(value);
			sb.append("\"");
		}
		else {
			sb.append(value);
		}
	}

	protected DocumentMetadataSet
			testGraphQLDocumentMetadataSet_addDocumentMetadataSet()
		throws Exception {

		return testGraphQLDocumentMetadataSet_addDocumentMetadataSet(
			randomDocumentMetadataSet());
	}

	protected DocumentMetadataSet
			testGraphQLDocumentMetadataSet_addDocumentMetadataSet(
				DocumentMetadataSet documentMetadataSet)
		throws Exception {

		JSONDeserializer<DocumentMetadataSet> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentMetadataSet.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(field.getName());
			sb.append(": ");

			appendGraphQLFieldValue(sb, field.get(documentMetadataSet));
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentMetadataSet",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + testGroup.getGroupId() + "\"");
								put("documentMetadataSet", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocumentMetadataSet"),
			DocumentMetadataSet.class);
	}

	protected void assertContains(
		DocumentMetadataSet documentMetadataSet,
		List<DocumentMetadataSet> documentMetadataSets) {

		boolean contains = false;

		for (DocumentMetadataSet item : documentMetadataSets) {
			if (equals(documentMetadataSet, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			documentMetadataSets + " does not contain " + documentMetadataSet,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DocumentMetadataSet documentMetadataSet1,
		DocumentMetadataSet documentMetadataSet2) {

		Assert.assertTrue(
			documentMetadataSet1 + " does not equal " + documentMetadataSet2,
			equals(documentMetadataSet1, documentMetadataSet2));
	}

	protected void assertEquals(
		List<DocumentMetadataSet> documentMetadataSets1,
		List<DocumentMetadataSet> documentMetadataSets2) {

		Assert.assertEquals(
			documentMetadataSets1.size(), documentMetadataSets2.size());

		for (int i = 0; i < documentMetadataSets1.size(); i++) {
			DocumentMetadataSet documentMetadataSet1 =
				documentMetadataSets1.get(i);
			DocumentMetadataSet documentMetadataSet2 =
				documentMetadataSets2.get(i);

			assertEquals(documentMetadataSet1, documentMetadataSet2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DocumentMetadataSet> documentMetadataSets1,
		List<DocumentMetadataSet> documentMetadataSets2) {

		Assert.assertEquals(
			documentMetadataSets1.size(), documentMetadataSets2.size());

		for (DocumentMetadataSet documentMetadataSet1 : documentMetadataSets1) {
			boolean contains = false;

			for (DocumentMetadataSet documentMetadataSet2 :
					documentMetadataSets2) {

				if (equals(documentMetadataSet1, documentMetadataSet2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				documentMetadataSets2 + " does not contain " +
					documentMetadataSet1,
				contains);
		}
	}

	protected void assertValid(DocumentMetadataSet documentMetadataSet)
		throws Exception {

		boolean valid = true;

		if (documentMetadataSet.getDateCreated() == null) {
			valid = false;
		}

		if (documentMetadataSet.getDateModified() == null) {
			valid = false;
		}

		if (documentMetadataSet.getId() == null) {
			valid = false;
		}

		com.liferay.portal.kernel.model.Group group = testDepotEntry.getGroup();

		if (!Objects.equals(
				documentMetadataSet.getAssetLibraryKey(),
				group.getGroupKey()) &&
			!Objects.equals(
				documentMetadataSet.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (documentMetadataSet.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (documentMetadataSet.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (documentMetadataSet.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionFields", additionalAssertFieldName)) {

				if (documentMetadataSet.getDataDefinitionFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataLayout", additionalAssertFieldName)) {
				if (documentMetadataSet.getDataLayout() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (documentMetadataSet.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (documentMetadataSet.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (documentMetadataSet.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (documentMetadataSet.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (documentMetadataSet.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<DocumentMetadataSet> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DocumentMetadataSet> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DocumentMetadataSet> documentMetadataSets =
			page.getItems();

		int size = documentMetadataSets.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.DocumentMetadataSet.
						class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		DocumentMetadataSet documentMetadataSet1,
		DocumentMetadataSet documentMetadataSet2) {

		if (documentMetadataSet1 == documentMetadataSet2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentMetadataSet1.getActions(),
						(Map)documentMetadataSet2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentMetadataSet1.getAvailableLanguages(),
						documentMetadataSet2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionFields", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentMetadataSet1.getDataDefinitionFields(),
						documentMetadataSet2.getDataDefinitionFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataLayout", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetadataSet1.getDataLayout(),
						documentMetadataSet2.getDataLayout())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetadataSet1.getDateCreated(),
						documentMetadataSet2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetadataSet1.getDateModified(),
						documentMetadataSet2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetadataSet1.getDescription(),
						documentMetadataSet2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentMetadataSet1.getDescription_i18n(),
						(Map)documentMetadataSet2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentMetadataSet1.getExternalReferenceCode(),
						documentMetadataSet2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetadataSet1.getId(),
						documentMetadataSet2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetadataSet1.getName(),
						documentMetadataSet2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentMetadataSet1.getName_i18n(),
						(Map)documentMetadataSet2.getName_i18n())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_documentMetadataSetResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_documentMetadataSetResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		DocumentMetadataSet documentMetadataSet) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = documentMetadataSet.getAssetLibraryKey();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataDefinitionFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataLayout")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = documentMetadataSet.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(documentMetadataSet.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = documentMetadataSet.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(documentMetadataSet.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = documentMetadataSet.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = documentMetadataSet.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = documentMetadataSet.getName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected DocumentMetadataSet randomDocumentMetadataSet() throws Exception {
		return new DocumentMetadataSet() {
			{
				assetLibraryKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected DocumentMetadataSet randomIrrelevantDocumentMetadataSet()
		throws Exception {

		DocumentMetadataSet randomIrrelevantDocumentMetadataSet =
			randomDocumentMetadataSet();

		randomIrrelevantDocumentMetadataSet.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantDocumentMetadataSet;
	}

	protected DocumentMetadataSet randomPatchDocumentMetadataSet()
		throws Exception {

		return randomDocumentMetadataSet();
	}

	protected DocumentMetadataSetResource documentMetadataSetResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseDocumentMetadataSetResourceTestCase.class);

	private static DateFormat _dateFormat;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.DocumentMetadataSetResource
			_documentMetadataSetResource;

}