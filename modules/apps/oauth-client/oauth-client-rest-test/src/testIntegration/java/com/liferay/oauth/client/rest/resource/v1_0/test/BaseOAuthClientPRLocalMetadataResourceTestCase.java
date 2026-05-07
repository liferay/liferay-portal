/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.rest.client.http.HttpInvoker;
import com.liferay.oauth.client.rest.client.pagination.Page;
import com.liferay.oauth.client.rest.client.resource.v1_0.OAuthClientPRLocalMetadataResource;
import com.liferay.oauth.client.rest.client.serdes.v1_0.OAuthClientPRLocalMetadataSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
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
 * @author Manuele Castro
 * @generated
 */
@Generated("")
public abstract class BaseOAuthClientPRLocalMetadataResourceTestCase {

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

		_oAuthClientPRLocalMetadataResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		oAuthClientPRLocalMetadataResource =
			OAuthClientPRLocalMetadataResource.builder(
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

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1 =
			randomOAuthClientPRLocalMetadata();

		String json = objectMapper.writeValueAsString(
			oAuthClientPRLocalMetadata1);

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata2 =
			OAuthClientPRLocalMetadataSerDes.toDTO(json);

		Assert.assertTrue(
			equals(oAuthClientPRLocalMetadata1, oAuthClientPRLocalMetadata2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			randomOAuthClientPRLocalMetadata();

		String json1 = objectMapper.writeValueAsString(
			oAuthClientPRLocalMetadata);
		String json2 = OAuthClientPRLocalMetadataSerDes.toJSON(
			oAuthClientPRLocalMetadata);

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

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			randomOAuthClientPRLocalMetadata();

		oAuthClientPRLocalMetadata.setExternalReferenceCode(regex);
		oAuthClientPRLocalMetadata.setLocalWellKnownURI(regex);
		oAuthClientPRLocalMetadata.setMetadataJSON(regex);
		oAuthClientPRLocalMetadata.setResource(regex);

		String json = OAuthClientPRLocalMetadataSerDes.toJSON(
			oAuthClientPRLocalMetadata);

		Assert.assertFalse(json.contains(regex));

		oAuthClientPRLocalMetadata = OAuthClientPRLocalMetadataSerDes.toDTO(
			json);

		Assert.assertEquals(
			regex, oAuthClientPRLocalMetadata.getExternalReferenceCode());
		Assert.assertEquals(
			regex, oAuthClientPRLocalMetadata.getLocalWellKnownURI());
		Assert.assertEquals(
			regex, oAuthClientPRLocalMetadata.getMetadataJSON());
		Assert.assertEquals(regex, oAuthClientPRLocalMetadata.getResource());
	}

	@Test
	public void testDeleteOAuthClientPRLocalMetadataByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			testDeleteOAuthClientPRLocalMetadataByExternalReferenceCode_addOAuthClientPRLocalMetadata();

		assertHttpResponseStatusCode(
			204,
			oAuthClientPRLocalMetadataResource.
				deleteOAuthClientPRLocalMetadataByExternalReferenceCodeHttpResponse(
					oAuthClientPRLocalMetadata.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadataByExternalReferenceCodeHttpResponse(
					oAuthClientPRLocalMetadata.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadataByExternalReferenceCodeHttpResponse(
					"-"));
	}

	protected OAuthClientPRLocalMetadata
			testDeleteOAuthClientPRLocalMetadataByExternalReferenceCode_addOAuthClientPRLocalMetadata()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetOAuthClientPRLocalMetadataByExternalReferenceCode()
		throws Exception {

		OAuthClientPRLocalMetadata postOAuthClientPRLocalMetadata =
			testGetOAuthClientPRLocalMetadataByExternalReferenceCode_addOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadataByExternalReferenceCode(
					postOAuthClientPRLocalMetadata.getExternalReferenceCode());

		assertEquals(
			postOAuthClientPRLocalMetadata, getOAuthClientPRLocalMetadata);
		assertValid(getOAuthClientPRLocalMetadata);
	}

	protected OAuthClientPRLocalMetadata
			testGetOAuthClientPRLocalMetadataByExternalReferenceCode_addOAuthClientPRLocalMetadata()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetOAuthClientPRLocalMetadatasPage() throws Exception {
		Page<OAuthClientPRLocalMetadata> page =
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadatasPage();

		long totalCount = page.getTotalCount();

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1 =
			testGetOAuthClientPRLocalMetadatasPage_addOAuthClientPRLocalMetadata(
				randomOAuthClientPRLocalMetadata());

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata2 =
			testGetOAuthClientPRLocalMetadatasPage_addOAuthClientPRLocalMetadata(
				randomOAuthClientPRLocalMetadata());

		page =
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadatasPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			oAuthClientPRLocalMetadata1,
			(List<OAuthClientPRLocalMetadata>)page.getItems());
		assertContains(
			oAuthClientPRLocalMetadata2,
			(List<OAuthClientPRLocalMetadata>)page.getItems());
		assertValid(
			page, testGetOAuthClientPRLocalMetadatasPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetOAuthClientPRLocalMetadatasPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected OAuthClientPRLocalMetadata
			testGetOAuthClientPRLocalMetadatasPage_addOAuthClientPRLocalMetadata(
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOAuthClientPRLocalMetadata() throws Exception {
		OAuthClientPRLocalMetadata randomOAuthClientPRLocalMetadata =
			randomOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata postOAuthClientPRLocalMetadata =
			testPostOAuthClientPRLocalMetadata_addOAuthClientPRLocalMetadata(
				randomOAuthClientPRLocalMetadata);

		assertEquals(
			randomOAuthClientPRLocalMetadata, postOAuthClientPRLocalMetadata);
		assertValid(postOAuthClientPRLocalMetadata);
	}

	protected OAuthClientPRLocalMetadata
			testPostOAuthClientPRLocalMetadata_addOAuthClientPRLocalMetadata(
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutOAuthClientPRLocalMetadataByExternalReferenceCode()
		throws Exception {

		OAuthClientPRLocalMetadata postOAuthClientPRLocalMetadata =
			testPutOAuthClientPRLocalMetadataByExternalReferenceCode_addOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata randomOAuthClientPRLocalMetadata =
			randomOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata putOAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataResource.
				putOAuthClientPRLocalMetadataByExternalReferenceCode(
					postOAuthClientPRLocalMetadata.getExternalReferenceCode(),
					randomOAuthClientPRLocalMetadata);

		assertEquals(
			randomOAuthClientPRLocalMetadata, putOAuthClientPRLocalMetadata);
		assertValid(putOAuthClientPRLocalMetadata);

		OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadataByExternalReferenceCode(
					putOAuthClientPRLocalMetadata.getExternalReferenceCode());

		assertEquals(
			randomOAuthClientPRLocalMetadata, getOAuthClientPRLocalMetadata);
		assertValid(getOAuthClientPRLocalMetadata);

		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			testPutOAuthClientPRLocalMetadataByExternalReferenceCode_createOAuthClientPRLocalMetadata();

		putOAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataResource.
				putOAuthClientPRLocalMetadataByExternalReferenceCode(
					newOAuthClientPRLocalMetadata.getExternalReferenceCode(),
					newOAuthClientPRLocalMetadata);

		assertEquals(
			newOAuthClientPRLocalMetadata, putOAuthClientPRLocalMetadata);
		assertValid(putOAuthClientPRLocalMetadata);

		getOAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataResource.
				getOAuthClientPRLocalMetadataByExternalReferenceCode(
					putOAuthClientPRLocalMetadata.getExternalReferenceCode());

		assertEquals(
			newOAuthClientPRLocalMetadata, getOAuthClientPRLocalMetadata);

		Assert.assertEquals(
			newOAuthClientPRLocalMetadata.getExternalReferenceCode(),
			putOAuthClientPRLocalMetadata.getExternalReferenceCode());
	}

	protected OAuthClientPRLocalMetadata
			testPutOAuthClientPRLocalMetadataByExternalReferenceCode_addOAuthClientPRLocalMetadata()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected OAuthClientPRLocalMetadata
			testPutOAuthClientPRLocalMetadataByExternalReferenceCode_createOAuthClientPRLocalMetadata()
		throws Exception {

		return randomOAuthClientPRLocalMetadata();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1 =
			testBatchEngineDeleteImportTask_addOAuthClientPRLocalMetadata();

		testBatchEngineDeleteImportTask_deleteOAuthClientPRLocalMetadata(
			200, oAuthClientPRLocalMetadata1.getExternalReferenceCode());
	}

	protected OAuthClientPRLocalMetadata
			testBatchEngineDeleteImportTask_addOAuthClientPRLocalMetadata()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void
			testBatchEngineDeleteImportTask_deleteOAuthClientPRLocalMetadata(
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
				"com.liferay.oauth.client.rest.dto.v1_0.OAuthClientPRLocalMetadata",
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
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata,
		List<OAuthClientPRLocalMetadata> oAuthClientPRLocalMetadatas) {

		boolean contains = false;

		for (OAuthClientPRLocalMetadata item : oAuthClientPRLocalMetadatas) {
			if (equals(oAuthClientPRLocalMetadata, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			oAuthClientPRLocalMetadatas + " does not contain " +
				oAuthClientPRLocalMetadata,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1,
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata2) {

		Assert.assertTrue(
			oAuthClientPRLocalMetadata1 + " does not equal " +
				oAuthClientPRLocalMetadata2,
			equals(oAuthClientPRLocalMetadata1, oAuthClientPRLocalMetadata2));
	}

	protected void assertEquals(
		List<OAuthClientPRLocalMetadata> oAuthClientPRLocalMetadatas1,
		List<OAuthClientPRLocalMetadata> oAuthClientPRLocalMetadatas2) {

		Assert.assertEquals(
			oAuthClientPRLocalMetadatas1.size(),
			oAuthClientPRLocalMetadatas2.size());

		for (int i = 0; i < oAuthClientPRLocalMetadatas1.size(); i++) {
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1 =
				oAuthClientPRLocalMetadatas1.get(i);
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata2 =
				oAuthClientPRLocalMetadatas2.get(i);

			assertEquals(
				oAuthClientPRLocalMetadata1, oAuthClientPRLocalMetadata2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OAuthClientPRLocalMetadata> oAuthClientPRLocalMetadatas1,
		List<OAuthClientPRLocalMetadata> oAuthClientPRLocalMetadatas2) {

		Assert.assertEquals(
			oAuthClientPRLocalMetadatas1.size(),
			oAuthClientPRLocalMetadatas2.size());

		for (OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1 :
				oAuthClientPRLocalMetadatas1) {

			boolean contains = false;

			for (OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata2 :
					oAuthClientPRLocalMetadatas2) {

				if (equals(
						oAuthClientPRLocalMetadata1,
						oAuthClientPRLocalMetadata2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				oAuthClientPRLocalMetadatas2 + " does not contain " +
					oAuthClientPRLocalMetadata1,
				contains);
		}
	}

	protected void assertValid(
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws Exception {

		boolean valid = true;

		if (oAuthClientPRLocalMetadata.getDateCreated() == null) {
			valid = false;
		}

		if (oAuthClientPRLocalMetadata.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (oAuthClientPRLocalMetadata.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (oAuthClientPRLocalMetadata.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"localWellKnownEnabled", additionalAssertFieldName)) {

				if (oAuthClientPRLocalMetadata.getLocalWellKnownEnabled() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"localWellKnownURI", additionalAssertFieldName)) {

				if (oAuthClientPRLocalMetadata.getLocalWellKnownURI() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metadataJSON", additionalAssertFieldName)) {
				if (oAuthClientPRLocalMetadata.getMetadataJSON() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("resource", additionalAssertFieldName)) {
				if (oAuthClientPRLocalMetadata.getResource() == null) {
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

	protected void assertValid(Page<OAuthClientPRLocalMetadata> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OAuthClientPRLocalMetadata> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OAuthClientPRLocalMetadata>
			oAuthClientPRLocalMetadatas = page.getItems();

		int size = oAuthClientPRLocalMetadatas.size();

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

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.oauth.client.rest.dto.v1_0.
						OAuthClientPRLocalMetadata.class)) {

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
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata1,
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata2) {

		if (oAuthClientPRLocalMetadata1 == oAuthClientPRLocalMetadata2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getCreator(),
						oAuthClientPRLocalMetadata2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getDateCreated(),
						oAuthClientPRLocalMetadata2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getDateModified(),
						oAuthClientPRLocalMetadata2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getExternalReferenceCode(),
						oAuthClientPRLocalMetadata2.
							getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"localWellKnownEnabled", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getLocalWellKnownEnabled(),
						oAuthClientPRLocalMetadata2.
							getLocalWellKnownEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"localWellKnownURI", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getLocalWellKnownURI(),
						oAuthClientPRLocalMetadata2.getLocalWellKnownURI())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metadataJSON", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getMetadataJSON(),
						oAuthClientPRLocalMetadata2.getMetadataJSON())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("resource", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientPRLocalMetadata1.getResource(),
						oAuthClientPRLocalMetadata2.getResource())) {

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

		if (!(_oAuthClientPRLocalMetadataResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_oAuthClientPRLocalMetadataResource;

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
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = oAuthClientPRLocalMetadata.getDateCreated();

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
						oAuthClientPRLocalMetadata.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = oAuthClientPRLocalMetadata.getDateModified();

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
						oAuthClientPRLocalMetadata.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object =
				oAuthClientPRLocalMetadata.getExternalReferenceCode();

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

		if (entityFieldName.equals("localWellKnownEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("localWellKnownURI")) {
			Object object = oAuthClientPRLocalMetadata.getLocalWellKnownURI();

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

		if (entityFieldName.equals("metadataJSON")) {
			Object object = oAuthClientPRLocalMetadata.getMetadataJSON();

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

		if (entityFieldName.equals("resource")) {
			Object object = oAuthClientPRLocalMetadata.getResource();

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

	protected OAuthClientPRLocalMetadata randomOAuthClientPRLocalMetadata()
		throws Exception {

		return new OAuthClientPRLocalMetadata() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				localWellKnownEnabled = RandomTestUtil.randomBoolean();
				localWellKnownURI = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				metadataJSON = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				resource = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected OAuthClientPRLocalMetadata
			randomIrrelevantOAuthClientPRLocalMetadata()
		throws Exception {

		OAuthClientPRLocalMetadata randomIrrelevantOAuthClientPRLocalMetadata =
			randomOAuthClientPRLocalMetadata();

		return randomIrrelevantOAuthClientPRLocalMetadata;
	}

	protected OAuthClientPRLocalMetadata randomPatchOAuthClientPRLocalMetadata()
		throws Exception {

		return randomOAuthClientPRLocalMetadata();
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

	protected OAuthClientPRLocalMetadataResource
		oAuthClientPRLocalMetadataResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
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
			BaseOAuthClientPRLocalMetadataResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.oauth.client.rest.resource.v1_0.
		OAuthClientPRLocalMetadataResource _oAuthClientPRLocalMetadataResource;

}
// LIFERAY-REST-BUILDER-HASH:1483715530