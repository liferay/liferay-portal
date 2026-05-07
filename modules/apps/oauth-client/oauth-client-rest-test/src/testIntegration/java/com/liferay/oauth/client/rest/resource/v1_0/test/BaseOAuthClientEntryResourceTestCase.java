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
import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientEntry;
import com.liferay.oauth.client.rest.client.http.HttpInvoker;
import com.liferay.oauth.client.rest.client.pagination.Page;
import com.liferay.oauth.client.rest.client.resource.v1_0.OAuthClientEntryResource;
import com.liferay.oauth.client.rest.client.serdes.v1_0.OAuthClientEntrySerDes;
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
public abstract class BaseOAuthClientEntryResourceTestCase {

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

		_oAuthClientEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		oAuthClientEntryResource = OAuthClientEntryResource.builder(
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

		OAuthClientEntry oAuthClientEntry1 = randomOAuthClientEntry();

		String json = objectMapper.writeValueAsString(oAuthClientEntry1);

		OAuthClientEntry oAuthClientEntry2 = OAuthClientEntrySerDes.toDTO(json);

		Assert.assertTrue(equals(oAuthClientEntry1, oAuthClientEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OAuthClientEntry oAuthClientEntry = randomOAuthClientEntry();

		String json1 = objectMapper.writeValueAsString(oAuthClientEntry);
		String json2 = OAuthClientEntrySerDes.toJSON(oAuthClientEntry);

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

		OAuthClientEntry oAuthClientEntry = randomOAuthClientEntry();

		oAuthClientEntry.setAuthRequestParametersJSON(regex);
		oAuthClientEntry.setAuthServerWellKnownURI(regex);
		oAuthClientEntry.setClientId(regex);
		oAuthClientEntry.setCustomClaims(regex);
		oAuthClientEntry.setExternalReferenceCode(regex);
		oAuthClientEntry.setInfoJSON(regex);
		oAuthClientEntry.setMatcherField(regex);
		oAuthClientEntry.setOidcUserInfoMapperJSON(regex);
		oAuthClientEntry.setTokenRequestParametersJSON(regex);

		String json = OAuthClientEntrySerDes.toJSON(oAuthClientEntry);

		Assert.assertFalse(json.contains(regex));

		oAuthClientEntry = OAuthClientEntrySerDes.toDTO(json);

		Assert.assertEquals(
			regex, oAuthClientEntry.getAuthRequestParametersJSON());
		Assert.assertEquals(
			regex, oAuthClientEntry.getAuthServerWellKnownURI());
		Assert.assertEquals(regex, oAuthClientEntry.getClientId());
		Assert.assertEquals(regex, oAuthClientEntry.getCustomClaims());
		Assert.assertEquals(regex, oAuthClientEntry.getExternalReferenceCode());
		Assert.assertEquals(regex, oAuthClientEntry.getInfoJSON());
		Assert.assertEquals(regex, oAuthClientEntry.getMatcherField());
		Assert.assertEquals(
			regex, oAuthClientEntry.getOidcUserInfoMapperJSON());
		Assert.assertEquals(
			regex, oAuthClientEntry.getTokenRequestParametersJSON());
	}

	@Test
	public void testDeleteOAuthClientEntryByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OAuthClientEntry oAuthClientEntry =
			testDeleteOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry();

		assertHttpResponseStatusCode(
			204,
			oAuthClientEntryResource.
				deleteOAuthClientEntryByExternalReferenceCodeHttpResponse(
					oAuthClientEntry.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			oAuthClientEntryResource.
				getOAuthClientEntryByExternalReferenceCodeHttpResponse(
					oAuthClientEntry.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			oAuthClientEntryResource.
				getOAuthClientEntryByExternalReferenceCodeHttpResponse("-"));
	}

	protected OAuthClientEntry
			testDeleteOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetOAuthClientEntriesPage() throws Exception {
		Page<OAuthClientEntry> page =
			oAuthClientEntryResource.getOAuthClientEntriesPage();

		long totalCount = page.getTotalCount();

		OAuthClientEntry oAuthClientEntry1 =
			testGetOAuthClientEntriesPage_addOAuthClientEntry(
				randomOAuthClientEntry());

		OAuthClientEntry oAuthClientEntry2 =
			testGetOAuthClientEntriesPage_addOAuthClientEntry(
				randomOAuthClientEntry());

		page = oAuthClientEntryResource.getOAuthClientEntriesPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			oAuthClientEntry1, (List<OAuthClientEntry>)page.getItems());
		assertContains(
			oAuthClientEntry2, (List<OAuthClientEntry>)page.getItems());
		assertValid(page, testGetOAuthClientEntriesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetOAuthClientEntriesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected OAuthClientEntry
			testGetOAuthClientEntriesPage_addOAuthClientEntry(
				OAuthClientEntry oAuthClientEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetOAuthClientEntryByExternalReferenceCode()
		throws Exception {

		OAuthClientEntry postOAuthClientEntry =
			testGetOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry();

		OAuthClientEntry getOAuthClientEntry =
			oAuthClientEntryResource.getOAuthClientEntryByExternalReferenceCode(
				postOAuthClientEntry.getExternalReferenceCode());

		assertEquals(postOAuthClientEntry, getOAuthClientEntry);
		assertValid(getOAuthClientEntry);
	}

	protected OAuthClientEntry
			testGetOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOAuthClientEntry() throws Exception {
		OAuthClientEntry randomOAuthClientEntry = randomOAuthClientEntry();

		OAuthClientEntry postOAuthClientEntry =
			testPostOAuthClientEntry_addOAuthClientEntry(
				randomOAuthClientEntry);

		assertEquals(randomOAuthClientEntry, postOAuthClientEntry);
		assertValid(postOAuthClientEntry);
	}

	protected OAuthClientEntry testPostOAuthClientEntry_addOAuthClientEntry(
			OAuthClientEntry oAuthClientEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutOAuthClientEntryByExternalReferenceCode()
		throws Exception {

		OAuthClientEntry postOAuthClientEntry =
			testPutOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry();

		OAuthClientEntry randomOAuthClientEntry = randomOAuthClientEntry();

		OAuthClientEntry putOAuthClientEntry =
			oAuthClientEntryResource.putOAuthClientEntryByExternalReferenceCode(
				postOAuthClientEntry.getExternalReferenceCode(),
				randomOAuthClientEntry);

		assertEquals(randomOAuthClientEntry, putOAuthClientEntry);
		assertValid(putOAuthClientEntry);

		OAuthClientEntry getOAuthClientEntry =
			oAuthClientEntryResource.getOAuthClientEntryByExternalReferenceCode(
				putOAuthClientEntry.getExternalReferenceCode());

		assertEquals(randomOAuthClientEntry, getOAuthClientEntry);
		assertValid(getOAuthClientEntry);

		OAuthClientEntry newOAuthClientEntry =
			testPutOAuthClientEntryByExternalReferenceCode_createOAuthClientEntry();

		putOAuthClientEntry =
			oAuthClientEntryResource.putOAuthClientEntryByExternalReferenceCode(
				newOAuthClientEntry.getExternalReferenceCode(),
				newOAuthClientEntry);

		assertEquals(newOAuthClientEntry, putOAuthClientEntry);
		assertValid(putOAuthClientEntry);

		getOAuthClientEntry =
			oAuthClientEntryResource.getOAuthClientEntryByExternalReferenceCode(
				putOAuthClientEntry.getExternalReferenceCode());

		assertEquals(newOAuthClientEntry, getOAuthClientEntry);

		Assert.assertEquals(
			newOAuthClientEntry.getExternalReferenceCode(),
			putOAuthClientEntry.getExternalReferenceCode());
	}

	protected OAuthClientEntry
			testPutOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected OAuthClientEntry
			testPutOAuthClientEntryByExternalReferenceCode_createOAuthClientEntry()
		throws Exception {

		return randomOAuthClientEntry();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OAuthClientEntry oAuthClientEntry1 =
			testBatchEngineDeleteImportTask_addOAuthClientEntry();

		testBatchEngineDeleteImportTask_deleteOAuthClientEntry(
			200, oAuthClientEntry1.getExternalReferenceCode());
	}

	protected OAuthClientEntry
			testBatchEngineDeleteImportTask_addOAuthClientEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void testBatchEngineDeleteImportTask_deleteOAuthClientEntry(
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
				"com.liferay.oauth.client.rest.dto.v1_0.OAuthClientEntry", null,
				null, null, null,
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
		OAuthClientEntry oAuthClientEntry,
		List<OAuthClientEntry> oAuthClientEntries) {

		boolean contains = false;

		for (OAuthClientEntry item : oAuthClientEntries) {
			if (equals(oAuthClientEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			oAuthClientEntries + " does not contain " + oAuthClientEntry,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OAuthClientEntry oAuthClientEntry1,
		OAuthClientEntry oAuthClientEntry2) {

		Assert.assertTrue(
			oAuthClientEntry1 + " does not equal " + oAuthClientEntry2,
			equals(oAuthClientEntry1, oAuthClientEntry2));
	}

	protected void assertEquals(
		List<OAuthClientEntry> oAuthClientEntries1,
		List<OAuthClientEntry> oAuthClientEntries2) {

		Assert.assertEquals(
			oAuthClientEntries1.size(), oAuthClientEntries2.size());

		for (int i = 0; i < oAuthClientEntries1.size(); i++) {
			OAuthClientEntry oAuthClientEntry1 = oAuthClientEntries1.get(i);
			OAuthClientEntry oAuthClientEntry2 = oAuthClientEntries2.get(i);

			assertEquals(oAuthClientEntry1, oAuthClientEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OAuthClientEntry> oAuthClientEntries1,
		List<OAuthClientEntry> oAuthClientEntries2) {

		Assert.assertEquals(
			oAuthClientEntries1.size(), oAuthClientEntries2.size());

		for (OAuthClientEntry oAuthClientEntry1 : oAuthClientEntries1) {
			boolean contains = false;

			for (OAuthClientEntry oAuthClientEntry2 : oAuthClientEntries2) {
				if (equals(oAuthClientEntry1, oAuthClientEntry2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				oAuthClientEntries2 + " does not contain " + oAuthClientEntry1,
				contains);
		}
	}

	protected void assertValid(OAuthClientEntry oAuthClientEntry)
		throws Exception {

		boolean valid = true;

		if (oAuthClientEntry.getDateCreated() == null) {
			valid = false;
		}

		if (oAuthClientEntry.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"authRequestParametersJSON", additionalAssertFieldName)) {

				if (oAuthClientEntry.getAuthRequestParametersJSON() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"authServerWellKnownURI", additionalAssertFieldName)) {

				if (oAuthClientEntry.getAuthServerWellKnownURI() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("clientId", additionalAssertFieldName)) {
				if (oAuthClientEntry.getClientId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (oAuthClientEntry.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customClaims", additionalAssertFieldName)) {
				if (oAuthClientEntry.getCustomClaims() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (oAuthClientEntry.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("infoJSON", additionalAssertFieldName)) {
				if (oAuthClientEntry.getInfoJSON() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("matcherField", additionalAssertFieldName)) {
				if (oAuthClientEntry.getMatcherField() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"metadataCacheTime", additionalAssertFieldName)) {

				if (oAuthClientEntry.getMetadataCacheTime() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"oAuthClientASLocalMetadata", additionalAssertFieldName)) {

				if (oAuthClientEntry.getOAuthClientASLocalMetadata() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"oidcUserInfoMapperJSON", additionalAssertFieldName)) {

				if (oAuthClientEntry.getOidcUserInfoMapperJSON() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"tokenRequestParametersJSON", additionalAssertFieldName)) {

				if (oAuthClientEntry.getTokenRequestParametersJSON() == null) {
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

	protected void assertValid(Page<OAuthClientEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OAuthClientEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OAuthClientEntry> oAuthClientEntries =
			page.getItems();

		int size = oAuthClientEntries.size();

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
					com.liferay.oauth.client.rest.dto.v1_0.OAuthClientEntry.
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
		OAuthClientEntry oAuthClientEntry1,
		OAuthClientEntry oAuthClientEntry2) {

		if (oAuthClientEntry1 == oAuthClientEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"authRequestParametersJSON", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getAuthRequestParametersJSON(),
						oAuthClientEntry2.getAuthRequestParametersJSON())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"authServerWellKnownURI", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getAuthServerWellKnownURI(),
						oAuthClientEntry2.getAuthServerWellKnownURI())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clientId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getClientId(),
						oAuthClientEntry2.getClientId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getCreator(),
						oAuthClientEntry2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customClaims", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getCustomClaims(),
						oAuthClientEntry2.getCustomClaims())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getDateCreated(),
						oAuthClientEntry2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getDateModified(),
						oAuthClientEntry2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getExternalReferenceCode(),
						oAuthClientEntry2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("infoJSON", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getInfoJSON(),
						oAuthClientEntry2.getInfoJSON())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("matcherField", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						oAuthClientEntry1.getMatcherField(),
						oAuthClientEntry2.getMatcherField())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"metadataCacheTime", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getMetadataCacheTime(),
						oAuthClientEntry2.getMetadataCacheTime())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"oAuthClientASLocalMetadata", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getOAuthClientASLocalMetadata(),
						oAuthClientEntry2.getOAuthClientASLocalMetadata())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"oidcUserInfoMapperJSON", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getOidcUserInfoMapperJSON(),
						oAuthClientEntry2.getOidcUserInfoMapperJSON())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"tokenRequestParametersJSON", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						oAuthClientEntry1.getTokenRequestParametersJSON(),
						oAuthClientEntry2.getTokenRequestParametersJSON())) {

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

		if (!(_oAuthClientEntryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_oAuthClientEntryResource;

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
		OAuthClientEntry oAuthClientEntry) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("authRequestParametersJSON")) {
			Object object = oAuthClientEntry.getAuthRequestParametersJSON();

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

		if (entityFieldName.equals("authServerWellKnownURI")) {
			Object object = oAuthClientEntry.getAuthServerWellKnownURI();

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

		if (entityFieldName.equals("clientId")) {
			Object object = oAuthClientEntry.getClientId();

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customClaims")) {
			Object object = oAuthClientEntry.getCustomClaims();

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
				Date date = oAuthClientEntry.getDateCreated();

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

				sb.append(_format.format(oAuthClientEntry.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = oAuthClientEntry.getDateModified();

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

				sb.append(_format.format(oAuthClientEntry.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = oAuthClientEntry.getExternalReferenceCode();

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

		if (entityFieldName.equals("infoJSON")) {
			Object object = oAuthClientEntry.getInfoJSON();

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

		if (entityFieldName.equals("matcherField")) {
			Object object = oAuthClientEntry.getMatcherField();

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

		if (entityFieldName.equals("metadataCacheTime")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("oAuthClientASLocalMetadata")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("oidcUserInfoMapperJSON")) {
			Object object = oAuthClientEntry.getOidcUserInfoMapperJSON();

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

		if (entityFieldName.equals("tokenRequestParametersJSON")) {
			Object object = oAuthClientEntry.getTokenRequestParametersJSON();

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

	protected OAuthClientEntry randomOAuthClientEntry() throws Exception {
		return new OAuthClientEntry() {
			{
				authRequestParametersJSON = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				authServerWellKnownURI = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				clientId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				customClaims = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				infoJSON = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				matcherField = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				metadataCacheTime = RandomTestUtil.randomLong();
				oidcUserInfoMapperJSON = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				tokenRequestParametersJSON = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected OAuthClientEntry randomIrrelevantOAuthClientEntry()
		throws Exception {

		OAuthClientEntry randomIrrelevantOAuthClientEntry =
			randomOAuthClientEntry();

		return randomIrrelevantOAuthClientEntry;
	}

	protected OAuthClientEntry randomPatchOAuthClientEntry() throws Exception {
		return randomOAuthClientEntry();
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

	protected OAuthClientEntryResource oAuthClientEntryResource;
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
		LogFactoryUtil.getLog(BaseOAuthClientEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.oauth.client.rest.resource.v1_0.OAuthClientEntryResource
		_oAuthClientEntryResource;

}
// LIFERAY-REST-BUILDER-HASH:-1021091226