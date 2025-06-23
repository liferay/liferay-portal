/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.ERCAssetLibraryTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ERCAssetLibraryTestEntitySerDes;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public abstract class BaseERCAssetLibraryTestEntityResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_ercAssetLibraryTestEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		ercAssetLibraryTestEntityResource =
			ERCAssetLibraryTestEntityResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
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

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			randomERCAssetLibraryTestEntity();

		String json = objectMapper.writeValueAsString(
			ercAssetLibraryTestEntity1);

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
			ERCAssetLibraryTestEntitySerDes.toDTO(json);

		Assert.assertTrue(
			equals(ercAssetLibraryTestEntity1, ercAssetLibraryTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		String json1 = objectMapper.writeValueAsString(
			ercAssetLibraryTestEntity);
		String json2 = ERCAssetLibraryTestEntitySerDes.toJSON(
			ercAssetLibraryTestEntity);

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

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ercAssetLibraryTestEntity.setAssetLibraryExternalReferenceCode(regex);
		ercAssetLibraryTestEntity.setDescription(regex);
		ercAssetLibraryTestEntity.setExternalReferenceCode(regex);

		String json = ERCAssetLibraryTestEntitySerDes.toJSON(
			ercAssetLibraryTestEntity);

		Assert.assertFalse(json.contains(regex));

		ercAssetLibraryTestEntity = ERCAssetLibraryTestEntitySerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			ercAssetLibraryTestEntity.getAssetLibraryExternalReferenceCode());
		Assert.assertEquals(regex, ercAssetLibraryTestEntity.getDescription());
		Assert.assertEquals(
			regex, ercAssetLibraryTestEntity.getExternalReferenceCode());
	}

	@Test
	public void testDeleteAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity =
			testDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		assertHttpResponseStatusCode(
			204,
			ercAssetLibraryTestEntityResource.
				deleteAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					ercAssetLibraryTestEntity.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					ercAssetLibraryTestEntity.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					"-"));
	}

	protected ERCAssetLibraryTestEntity
			testDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testGetAssetLibraryERCAssetLibraryTestEntitiesPage()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getAssetLibraryExternalReferenceCode();
		String irrelevantAssetLibraryExternalReferenceCode =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getIrrelevantAssetLibraryExternalReferenceCode();

		Page<ERCAssetLibraryTestEntity> page =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntitiesPage(
					assetLibraryExternalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryExternalReferenceCode != null) {
			ERCAssetLibraryTestEntity irrelevantERCAssetLibraryTestEntity =
				testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
					irrelevantAssetLibraryExternalReferenceCode,
					randomIrrelevantERCAssetLibraryTestEntity());

			page =
				ercAssetLibraryTestEntityResource.
					getAssetLibraryERCAssetLibraryTestEntitiesPage(
						irrelevantAssetLibraryExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantERCAssetLibraryTestEntity,
				(List<ERCAssetLibraryTestEntity>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getExpectedActions(
					irrelevantAssetLibraryExternalReferenceCode));
		}

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode,
				randomERCAssetLibraryTestEntity());

		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode,
				randomERCAssetLibraryTestEntity());

		page =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntitiesPage(
					assetLibraryExternalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			ercAssetLibraryTestEntity1,
			(List<ERCAssetLibraryTestEntity>)page.getItems());
		assertContains(
			ercAssetLibraryTestEntity2,
			(List<ERCAssetLibraryTestEntity>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getExpectedActions(
				assetLibraryExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/test/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/erc-asset-library-test-entities/batch".
				replace(
					"{assetLibraryExternalReferenceCode}",
					String.valueOf(assetLibraryExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected ERCAssetLibraryTestEntity
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_addERCAssetLibraryTestEntity(
				String assetLibraryExternalReferenceCode,
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				assetLibraryExternalReferenceCode, ercAssetLibraryTestEntity);
	}

	protected String
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryERCAssetLibraryTestEntitiesPage_getIrrelevantAssetLibraryExternalReferenceCode()
		throws Exception {

		return irrelevantDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testGetAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity getERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode());

		assertEquals(
			postERCAssetLibraryTestEntity, getERCAssetLibraryTestEntity);
		assertValid(getERCAssetLibraryTestEntity);
	}

	protected ERCAssetLibraryTestEntity
			testGetAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testPostAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testPostAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				randomERCAssetLibraryTestEntity);

		assertEquals(
			randomERCAssetLibraryTestEntity, postERCAssetLibraryTestEntity);
		assertValid(postERCAssetLibraryTestEntity);
	}

	protected ERCAssetLibraryTestEntity
			testPostAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity(
				ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity postERCAssetLibraryTestEntity =
			testPutAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		ERCAssetLibraryTestEntity putERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				putAssetLibraryERCAssetLibraryTestEntity(
					postERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					postERCAssetLibraryTestEntity.getExternalReferenceCode(),
					randomERCAssetLibraryTestEntity);

		assertEquals(
			randomERCAssetLibraryTestEntity, putERCAssetLibraryTestEntity);
		assertValid(putERCAssetLibraryTestEntity);

		ERCAssetLibraryTestEntity getERCAssetLibraryTestEntity =
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntity(
					putERCAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode(),
					putERCAssetLibraryTestEntity.getExternalReferenceCode());

		assertEquals(
			randomERCAssetLibraryTestEntity, getERCAssetLibraryTestEntity);
		assertValid(getERCAssetLibraryTestEntity);
	}

	protected ERCAssetLibraryTestEntity
			testPutAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity()
		throws Exception {

		return ercAssetLibraryTestEntityResource.
			postAssetLibraryERCAssetLibraryTestEntity(
				testDepotEntryGroup.getExternalReferenceCode(),
				randomERCAssetLibraryTestEntity());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
			testBatchEngineDeleteImportTask_addAssetLibraryERCAssetLibraryTestEntity();

		testBatchEngineDeleteImportTask_deleteERCAssetLibraryTestEntity(
			200, ercAssetLibraryTestEntity1.getExternalReferenceCode(),
			"assetLibraryExternalReferenceCode",
			testDepotEntryGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			ercAssetLibraryTestEntityResource.
				getAssetLibraryERCAssetLibraryTestEntityHttpResponse(
					ercAssetLibraryTestEntity1.
						getAssetLibraryExternalReferenceCode(),
					ercAssetLibraryTestEntity1.getExternalReferenceCode()));
	}

	protected ERCAssetLibraryTestEntity
			testBatchEngineDeleteImportTask_addAssetLibraryERCAssetLibraryTestEntity()
		throws Exception {

		return testDeleteAssetLibraryERCAssetLibraryTestEntity_addERCAssetLibraryTestEntity();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteERCAssetLibraryTestEntity(
				int expectedStatusCode, String externalReferenceCode,
				String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCAssetLibraryTestEntity",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected void assertContains(
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity,
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities) {

		boolean contains = false;

		for (ERCAssetLibraryTestEntity item : ercAssetLibraryTestEntities) {
			if (equals(ercAssetLibraryTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			ercAssetLibraryTestEntities + " does not contain " +
				ercAssetLibraryTestEntity,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1,
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2) {

		Assert.assertTrue(
			ercAssetLibraryTestEntity1 + " does not equal " +
				ercAssetLibraryTestEntity2,
			equals(ercAssetLibraryTestEntity1, ercAssetLibraryTestEntity2));
	}

	protected void assertEquals(
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities1,
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities2) {

		Assert.assertEquals(
			ercAssetLibraryTestEntities1.size(),
			ercAssetLibraryTestEntities2.size());

		for (int i = 0; i < ercAssetLibraryTestEntities1.size(); i++) {
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 =
				ercAssetLibraryTestEntities1.get(i);
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 =
				ercAssetLibraryTestEntities2.get(i);

			assertEquals(
				ercAssetLibraryTestEntity1, ercAssetLibraryTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities1,
		List<ERCAssetLibraryTestEntity> ercAssetLibraryTestEntities2) {

		Assert.assertEquals(
			ercAssetLibraryTestEntities1.size(),
			ercAssetLibraryTestEntities2.size());

		for (ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1 :
				ercAssetLibraryTestEntities1) {

			boolean contains = false;

			for (ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2 :
					ercAssetLibraryTestEntities2) {

				if (equals(
						ercAssetLibraryTestEntity1,
						ercAssetLibraryTestEntity2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				ercAssetLibraryTestEntities2 + " does not contain " +
					ercAssetLibraryTestEntity1,
				contains);
		}
	}

	protected void assertValid(
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		boolean valid = true;

		if (ercAssetLibraryTestEntity.getDateCreated() == null) {
			valid = false;
		}

		if (ercAssetLibraryTestEntity.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"assetLibraryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (ercAssetLibraryTestEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (ercAssetLibraryTestEntity.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (ercAssetLibraryTestEntity.getPermissions() == null) {
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

	protected void assertValid(Page<ERCAssetLibraryTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ERCAssetLibraryTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ERCAssetLibraryTestEntity>
			ercAssetLibraryTestEntities = page.getItems();

		int size = ercAssetLibraryTestEntities.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						ERCAssetLibraryTestEntity.class)) {

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
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity1,
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity2) {

		if (ercAssetLibraryTestEntity1 == ercAssetLibraryTestEntity2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"assetLibraryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.
							getAssetLibraryExternalReferenceCode(),
						ercAssetLibraryTestEntity2.
							getAssetLibraryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getDateCreated(),
						ercAssetLibraryTestEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getDateModified(),
						ercAssetLibraryTestEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getDescription(),
						ercAssetLibraryTestEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getExternalReferenceCode(),
						ercAssetLibraryTestEntity2.
							getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ercAssetLibraryTestEntity1.getPermissions(),
						ercAssetLibraryTestEntity2.getPermissions())) {

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

		if (!(_ercAssetLibraryTestEntityResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_ercAssetLibraryTestEntityResource;

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
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetLibraryExternalReferenceCode")) {
			Object object =
				ercAssetLibraryTestEntity.
					getAssetLibraryExternalReferenceCode();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = ercAssetLibraryTestEntity.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_format.format(ercAssetLibraryTestEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = ercAssetLibraryTestEntity.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_format.format(
						ercAssetLibraryTestEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = ercAssetLibraryTestEntity.getDescription();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object =
				ercAssetLibraryTestEntity.getExternalReferenceCode();

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

		if (entityFieldName.equals("permissions")) {
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

	protected ERCAssetLibraryTestEntity randomERCAssetLibraryTestEntity()
		throws Exception {

		return new ERCAssetLibraryTestEntity() {
			{
				assetLibraryExternalReferenceCode =
					testDepotEntryGroup.getExternalReferenceCode();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ERCAssetLibraryTestEntity
			randomIrrelevantERCAssetLibraryTestEntity()
		throws Exception {

		ERCAssetLibraryTestEntity randomIrrelevantERCAssetLibraryTestEntity =
			randomERCAssetLibraryTestEntity();

		randomIrrelevantERCAssetLibraryTestEntity.
			setAssetLibraryExternalReferenceCode(
				irrelevantDepotEntryGroup.getExternalReferenceCode());

		return randomIrrelevantERCAssetLibraryTestEntity;
	}

	protected ERCAssetLibraryTestEntity randomPatchERCAssetLibraryTestEntity()
		throws Exception {

		return randomERCAssetLibraryTestEntity();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected ERCAssetLibraryTestEntityResource
		ercAssetLibraryTestEntityResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
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
		LogFactoryUtil.getLog(
			BaseERCAssetLibraryTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		ERCAssetLibraryTestEntityResource _ercAssetLibraryTestEntityResource;

}