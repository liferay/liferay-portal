/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.object.client.dto.v1_0.Collaborator;
import com.liferay.headless.object.client.http.HttpInvoker;
import com.liferay.headless.object.client.pagination.Page;
import com.liferay.headless.object.client.pagination.Pagination;
import com.liferay.headless.object.client.resource.v1_0.CollaboratorResource;
import com.liferay.headless.object.client.serdes.v1_0.CollaboratorSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
import com.liferay.portal.kernel.util.GetterUtil;
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
 * @author Alicia García
 * @generated
 */
@Generated("")
public abstract class BaseCollaboratorResourceTestCase {

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

		_collaboratorResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		collaboratorResource = CollaboratorResource.builder(
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

		Collaborator collaborator1 = randomCollaborator();

		String json = objectMapper.writeValueAsString(collaborator1);

		Collaborator collaborator2 = CollaboratorSerDes.toDTO(json);

		Assert.assertTrue(equals(collaborator1, collaborator2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Collaborator collaborator = randomCollaborator();

		String json1 = objectMapper.writeValueAsString(collaborator);
		String json2 = CollaboratorSerDes.toJSON(collaborator);

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

		Collaborator collaborator = randomCollaborator();

		collaborator.setExternalReferenceCode(regex);
		collaborator.setName(regex);
		collaborator.setPortrait(regex);
		collaborator.setType(regex);

		String json = CollaboratorSerDes.toJSON(collaborator);

		Assert.assertFalse(json.contains(regex));

		collaborator = CollaboratorSerDes.toDTO(json);

		Assert.assertEquals(regex, collaborator.getExternalReferenceCode());
		Assert.assertEquals(regex, collaborator.getName());
		Assert.assertEquals(regex, collaborator.getPortrait());
		Assert.assertEquals(regex, collaborator.getType());
	}

	@Test
	public void testDeleteObjectEntryFolderCollaboratorByTypeCollaborator()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Collaborator collaborator =
			testDeleteObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator();

		assertHttpResponseStatusCode(
			204,
			collaboratorResource.
				deleteObjectEntryFolderCollaboratorByTypeCollaboratorHttpResponse(
					testDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId(),
					collaborator.getType(), collaborator.getId()));

		assertHttpResponseStatusCode(
			404,
			collaboratorResource.
				getObjectEntryFolderCollaboratorByTypeCollaboratorHttpResponse(
					testDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId(),
					collaborator.getType(), collaborator.getId()));
		assertHttpResponseStatusCode(
			404,
			collaboratorResource.
				getObjectEntryFolderCollaboratorByTypeCollaboratorHttpResponse(
					testDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId(),
					collaborator.getType(), 0L));
	}

	protected Collaborator
			testDeleteObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator()
		throws Exception {

		// No namespace

		Collaborator collaborator1 =
			testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectEntryFolderCollaboratorByTypeCollaborator",
						new HashMap<String, Object>() {
							{
								put(
									"objectEntryFolderId",
									testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId());
								put(
									"type",
									"\"" + collaborator1.getType() + "\"");
								put("collaboratorId", collaborator1.getId());
							}
						})),
				"JSONObject/data",
				"Object/deleteObjectEntryFolderCollaboratorByTypeCollaborator"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectEntryFolderCollaboratorByTypeCollaborator",
					new HashMap<String, Object>() {
						{
							put(
								"objectEntryFolderId",
								testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId());
							put("type", "\"" + collaborator1.getType() + "\"");
							put("collaboratorId", collaborator1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessObject_v1_0

		Collaborator collaborator2 =
			testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"deleteObjectEntryFolderCollaboratorByTypeCollaborator",
							new HashMap<String, Object>() {
								{
									put(
										"objectEntryFolderId",
										testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId());
									put(
										"type",
										"\"" + collaborator2.getType() + "\"");
									put(
										"collaboratorId",
										collaborator2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessObject_v1_0",
				"Object/deleteObjectEntryFolderCollaboratorByTypeCollaborator"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessObject_v1_0",
					new GraphQLField(
						"objectEntryFolderCollaboratorByTypeCollaborator",
						new HashMap<String, Object>() {
							{
								put(
									"objectEntryFolderId",
									testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId());
								put(
									"type",
									"\"" + collaborator2.getType() + "\"");
								put("collaboratorId", collaborator2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Collaborator
			testGraphQLDeleteObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		return testGraphQLCollaborator_addCollaborator();
	}

	@Test
	public void testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Collaborator collaborator =
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator();

		assertHttpResponseStatusCode(
			204,
			collaboratorResource.
				deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaboratorHttpResponse(
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey(),
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
						collaborator),
					collaborator.getType(), collaborator.getId()));

		assertHttpResponseStatusCode(
			404,
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaboratorHttpResponse(
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey(),
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
						collaborator),
					collaborator.getType(), collaborator.getId()));
		assertHttpResponseStatusCode(
			404,
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaboratorHttpResponse(
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey(),
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
						collaborator),
					collaborator.getType(), 0L));
	}

	protected Collaborator
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
				Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		// No namespace

		Collaborator collaborator1 =
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
						new HashMap<String, Object>() {
							{
								put(
									"scopeKey",
									"\"" +
										testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey() +
											"\"");

								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
											collaborator1) + "\"");
								put(
									"type",
									"\"" + collaborator1.getType() + "\"");
								put("collaboratorId", collaborator1.getId());
							}
						})),
				"JSONObject/data",
				"Object/deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
					new HashMap<String, Object>() {
						{
							put(
								"scopeKey",
								"\"" +
									testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey() +
										"\"");

							put(
								"externalReferenceCode",
								"\"" +
									testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
										collaborator1) + "\"");
							put("type", "\"" + collaborator1.getType() + "\"");
							put("collaboratorId", collaborator1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessObject_v1_0

		Collaborator collaborator2 =
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
							new HashMap<String, Object>() {
								{
									put(
										"scopeKey",
										"\"" +
											testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey() +
												"\"");

									put(
										"externalReferenceCode",
										"\"" +
											testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
												collaborator2) + "\"");
									put(
										"type",
										"\"" + collaborator2.getType() + "\"");
									put(
										"collaboratorId",
										collaborator2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessObject_v1_0",
				"Object/deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessObject_v1_0",
					new GraphQLField(
						"scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
						new HashMap<String, Object>() {
							{
								put(
									"scopeKey",
									"\"" +
										testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey() +
											"\"");

								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
											collaborator2) + "\"");
								put(
									"type",
									"\"" + collaborator2.getType() + "\"");
								put("collaboratorId", collaborator2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected String
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
				Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Collaborator
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		return testGraphQLCollaborator_addCollaborator();
	}

	@Test
	public void testGetObjectEntryFolderCollaboratorByTypeCollaborator()
		throws Exception {

		Collaborator postCollaborator =
			testGetObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator();

		Collaborator getCollaborator =
			collaboratorResource.
				getObjectEntryFolderCollaboratorByTypeCollaborator(
					testGetObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId(),
					postCollaborator.getType(), postCollaborator.getId());

		assertEquals(postCollaborator, getCollaborator);
		assertValid(getCollaborator);
	}

	protected Collaborator
			testGetObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaborator()
		throws Exception {

		Collaborator collaborator =
			testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator();

		// No namespace

		Assert.assertTrue(
			equals(
				collaborator,
				CollaboratorSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectEntryFolderCollaboratorByTypeCollaborator",
								new HashMap<String, Object>() {
									{
										put(
											"objectEntryFolderId",
											testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId());
										put(
											"type",
											"\"" + collaborator.getType() +
												"\"");
										put(
											"collaboratorId",
											collaborator.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/objectEntryFolderCollaboratorByTypeCollaborator"))));

		// Using the namespace headlessObject_v1_0

		Assert.assertTrue(
			equals(
				collaborator,
				CollaboratorSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessObject_v1_0",
								new GraphQLField(
									"objectEntryFolderCollaboratorByTypeCollaborator",
									new HashMap<String, Object>() {
										{
											put(
												"objectEntryFolderId",
												testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId());
											put(
												"type",
												"\"" + collaborator.getType() +
													"\"");
											put(
												"collaboratorId",
												collaborator.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessObject_v1_0",
						"Object/objectEntryFolderCollaboratorByTypeCollaborator"))));
	}

	protected Long
			testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaboratorNotFound()
		throws Exception {

		Long irrelevantObjectEntryFolderId = RandomTestUtil.randomLong();
		String irrelevantType = "\"" + RandomTestUtil.randomString() + "\"";
		Long irrelevantCollaboratorId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectEntryFolderCollaboratorByTypeCollaborator",
						new HashMap<String, Object>() {
							{
								put(
									"objectEntryFolderId",
									irrelevantObjectEntryFolderId);
								put("type", irrelevantType);
								put("collaboratorId", irrelevantCollaboratorId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessObject_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"objectEntryFolderCollaboratorByTypeCollaborator",
							new HashMap<String, Object>() {
								{
									put(
										"objectEntryFolderId",
										irrelevantObjectEntryFolderId);
									put("type", irrelevantType);
									put(
										"collaboratorId",
										irrelevantCollaboratorId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Collaborator
			testGraphQLGetObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		return testGraphQLCollaborator_addCollaborator();
	}

	@Test
	public void testGetObjectEntryFolderCollaboratorsPage() throws Exception {
		Long objectEntryFolderId =
			testGetObjectEntryFolderCollaboratorsPage_getObjectEntryFolderId();
		Long irrelevantObjectEntryFolderId =
			testGetObjectEntryFolderCollaboratorsPage_getIrrelevantObjectEntryFolderId();

		Page<Collaborator> page =
			collaboratorResource.getObjectEntryFolderCollaboratorsPage(
				objectEntryFolderId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantObjectEntryFolderId != null) {
			Collaborator irrelevantCollaborator =
				testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
					irrelevantObjectEntryFolderId,
					randomIrrelevantCollaborator());

			page = collaboratorResource.getObjectEntryFolderCollaboratorsPage(
				irrelevantObjectEntryFolderId,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantCollaborator, (List<Collaborator>)page.getItems());
			assertValid(
				page,
				testGetObjectEntryFolderCollaboratorsPage_getExpectedActions(
					irrelevantObjectEntryFolderId));
		}

		Collaborator collaborator1 =
			testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
				objectEntryFolderId, randomCollaborator());

		Collaborator collaborator2 =
			testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
				objectEntryFolderId, randomCollaborator());

		page = collaboratorResource.getObjectEntryFolderCollaboratorsPage(
			objectEntryFolderId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(collaborator1, (List<Collaborator>)page.getItems());
		assertContains(collaborator2, (List<Collaborator>)page.getItems());
		assertValid(
			page,
			testGetObjectEntryFolderCollaboratorsPage_getExpectedActions(
				objectEntryFolderId));
	}

	protected Map<String, Map<String, String>>
			testGetObjectEntryFolderCollaboratorsPage_getExpectedActions(
				Long objectEntryFolderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectEntryFolderCollaboratorsPageWithPagination()
		throws Exception {

		Long objectEntryFolderId =
			testGetObjectEntryFolderCollaboratorsPage_getObjectEntryFolderId();

		Page<Collaborator> collaboratorsPage =
			collaboratorResource.getObjectEntryFolderCollaboratorsPage(
				objectEntryFolderId, null);

		int totalCount = GetterUtil.getInteger(
			collaboratorsPage.getTotalCount());

		Collaborator collaborator1 =
			testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
				objectEntryFolderId, randomCollaborator());

		Collaborator collaborator2 =
			testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
				objectEntryFolderId, randomCollaborator());

		Collaborator collaborator3 =
			testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
				objectEntryFolderId, randomCollaborator());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Collaborator> page1 =
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(collaborator1, (List<Collaborator>)page1.getItems());

			Page<Collaborator> page2 =
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(collaborator2, (List<Collaborator>)page2.getItems());

			Page<Collaborator> page3 =
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(collaborator3, (List<Collaborator>)page3.getItems());
		}
		else {
			Page<Collaborator> page1 =
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId, Pagination.of(1, totalCount + 2));

			List<Collaborator> collaborators1 =
				(List<Collaborator>)page1.getItems();

			Assert.assertEquals(
				collaborators1.toString(), totalCount + 2,
				collaborators1.size());

			Page<Collaborator> page2 =
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Collaborator> collaborators2 =
				(List<Collaborator>)page2.getItems();

			Assert.assertEquals(
				collaborators2.toString(), 1, collaborators2.size());

			Page<Collaborator> page3 =
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId, Pagination.of(1, (int)totalCount + 3));

			assertContains(collaborator1, (List<Collaborator>)page3.getItems());
			assertContains(collaborator2, (List<Collaborator>)page3.getItems());
			assertContains(collaborator3, (List<Collaborator>)page3.getItems());
		}
	}

	protected Collaborator
			testGetObjectEntryFolderCollaboratorsPage_addCollaborator(
				Long objectEntryFolderId, Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectEntryFolderCollaboratorsPage_getObjectEntryFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectEntryFolderCollaboratorsPage_getIrrelevantObjectEntryFolderId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		Collaborator postCollaborator =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator();

		Collaborator getCollaborator =
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
					testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey(),
					testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
						postCollaborator),
					postCollaborator.getType(), postCollaborator.getId());

		assertEquals(postCollaborator, getCollaborator);
		assertValid(getCollaborator);
	}

	protected Collaborator
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
				Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		Collaborator collaborator =
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator();

		// No namespace

		Assert.assertTrue(
			equals(
				collaborator,
				CollaboratorSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
								new HashMap<String, Object>() {
									{
										put(
											"scopeKey",
											"\"" +
												testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey() +
													"\"");

										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
													collaborator) + "\"");
										put(
											"type",
											"\"" + collaborator.getType() +
												"\"");
										put(
											"collaboratorId",
											collaborator.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator"))));

		// Using the namespace headlessObject_v1_0

		Assert.assertTrue(
			equals(
				collaborator,
				CollaboratorSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessObject_v1_0",
								new GraphQLField(
									"scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
									new HashMap<String, Object>() {
										{
											put(
												"scopeKey",
												"\"" +
													testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey() +
														"\"");

											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
														collaborator) + "\"");
											put(
												"type",
												"\"" + collaborator.getType() +
													"\"");
											put(
												"collaboratorId",
												collaborator.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessObject_v1_0",
						"Object/scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator"))));
	}

	protected String
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
				Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaboratorNotFound()
		throws Exception {

		String irrelevantScopeKey = "\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantType = "\"" + RandomTestUtil.randomString() + "\"";
		Long irrelevantCollaboratorId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
						new HashMap<String, Object>() {
							{
								put("scopeKey", irrelevantScopeKey);
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
								put("type", irrelevantType);
								put("collaboratorId", irrelevantCollaboratorId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessObject_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator",
							new HashMap<String, Object>() {
								{
									put("scopeKey", irrelevantScopeKey);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
									put("type", irrelevantType);
									put(
										"collaboratorId",
										irrelevantCollaboratorId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Collaborator
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		return testGraphQLCollaborator_addCollaborator();
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage()
		throws Exception {

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getScopeKey();
		String irrelevantScopeKey =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getIrrelevantScopeKey();
		String externalReferenceCode =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getIrrelevantExternalReferenceCode();

		Page<Collaborator> page =
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
					scopeKey, externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantScopeKey != null) &&
			(irrelevantExternalReferenceCode != null)) {

			Collaborator irrelevantCollaborator =
				testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
					irrelevantScopeKey, irrelevantExternalReferenceCode,
					randomIrrelevantCollaborator());

			page =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						irrelevantScopeKey, irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantCollaborator, (List<Collaborator>)page.getItems());
			assertValid(
				page,
				testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getExpectedActions(
					irrelevantScopeKey, irrelevantExternalReferenceCode));
		}

		Collaborator collaborator1 =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
				scopeKey, externalReferenceCode, randomCollaborator());

		Collaborator collaborator2 =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
				scopeKey, externalReferenceCode, randomCollaborator());

		page =
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
					scopeKey, externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(collaborator1, (List<Collaborator>)page.getItems());
		assertContains(collaborator2, (List<Collaborator>)page.getItems());
		assertValid(
			page,
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getExpectedActions(
				scopeKey, externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getExpectedActions(
				String scopeKey, String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPageWithPagination()
		throws Exception {

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getScopeKey();
		String externalReferenceCode =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getExternalReferenceCode();

		Page<Collaborator> collaboratorsPage =
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
					scopeKey, externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			collaboratorsPage.getTotalCount());

		Collaborator collaborator1 =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
				scopeKey, externalReferenceCode, randomCollaborator());

		Collaborator collaborator2 =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
				scopeKey, externalReferenceCode, randomCollaborator());

		Collaborator collaborator3 =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
				scopeKey, externalReferenceCode, randomCollaborator());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Collaborator> page1 =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(collaborator1, (List<Collaborator>)page1.getItems());

			Page<Collaborator> page2 =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(collaborator2, (List<Collaborator>)page2.getItems());

			Page<Collaborator> page3 =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(collaborator3, (List<Collaborator>)page3.getItems());
		}
		else {
			Page<Collaborator> page1 =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<Collaborator> collaborators1 =
				(List<Collaborator>)page1.getItems();

			Assert.assertEquals(
				collaborators1.toString(), totalCount + 2,
				collaborators1.size());

			Page<Collaborator> page2 =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Collaborator> collaborators2 =
				(List<Collaborator>)page2.getItems();

			Assert.assertEquals(
				collaborators2.toString(), 1, collaborators2.size());

			Page<Collaborator> page3 =
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(collaborator1, (List<Collaborator>)page3.getItems());
			assertContains(collaborator2, (List<Collaborator>)page3.getItems());
			assertContains(collaborator3, (List<Collaborator>)page3.getItems());
		}
	}

	protected Collaborator
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_addCollaborator(
				String scopeKey, String externalReferenceCode,
				Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getIrrelevantScopeKey()
		throws Exception {

		return null;
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testPostObjectEntryFolderCollaboratorsPage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPutObjectEntryFolderCollaboratorByTypeCollaborator()
		throws Exception {

		Collaborator postCollaborator =
			testPutObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator();

		Collaborator randomCollaborator = randomCollaborator();

		Collaborator putCollaborator =
			collaboratorResource.
				putObjectEntryFolderCollaboratorByTypeCollaborator(
					testPutObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId(),
					postCollaborator.getType(), postCollaborator.getId(),
					randomCollaborator);

		assertEquals(randomCollaborator, putCollaborator);
		assertValid(putCollaborator);

		Collaborator getCollaborator =
			collaboratorResource.
				getObjectEntryFolderCollaboratorByTypeCollaborator(
					testPutObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId(),
					putCollaborator.getType(), putCollaborator.getId());

		assertEquals(randomCollaborator, getCollaborator);
		assertValid(getCollaborator);
	}

	protected Collaborator
			testPutObjectEntryFolderCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPutObjectEntryFolderCollaboratorByTypeCollaborator_getObjectEntryFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		Collaborator postCollaborator =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator();

		Collaborator randomCollaborator = randomCollaborator();

		Collaborator putCollaborator =
			collaboratorResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey(),
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
						postCollaborator),
					postCollaborator.getType(), postCollaborator.getId(),
					randomCollaborator);

		assertEquals(randomCollaborator, putCollaborator);
		assertValid(putCollaborator);

		Collaborator getCollaborator =
			collaboratorResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey(),
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
						putCollaborator),
					putCollaborator.getType(), putCollaborator.getId());

		assertEquals(randomCollaborator, getCollaborator);
		assertValid(getCollaborator);
	}

	protected Collaborator
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator_getExternalReferenceCode(
				Collaborator collaborator)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected Collaborator testGraphQLCollaborator_addCollaborator()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		Collaborator collaborator, List<Collaborator> collaborators) {

		boolean contains = false;

		for (Collaborator item : collaborators) {
			if (equals(collaborator, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			collaborators + " does not contain " + collaborator, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		Collaborator collaborator1, Collaborator collaborator2) {

		Assert.assertTrue(
			collaborator1 + " does not equal " + collaborator2,
			equals(collaborator1, collaborator2));
	}

	protected void assertEquals(
		List<Collaborator> collaborators1, List<Collaborator> collaborators2) {

		Assert.assertEquals(collaborators1.size(), collaborators2.size());

		for (int i = 0; i < collaborators1.size(); i++) {
			Collaborator collaborator1 = collaborators1.get(i);
			Collaborator collaborator2 = collaborators2.get(i);

			assertEquals(collaborator1, collaborator2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Collaborator> collaborators1, List<Collaborator> collaborators2) {

		Assert.assertEquals(collaborators1.size(), collaborators2.size());

		for (Collaborator collaborator1 : collaborators1) {
			boolean contains = false;

			for (Collaborator collaborator2 : collaborators2) {
				if (equals(collaborator1, collaborator2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				collaborators2 + " does not contain " + collaborator1,
				contains);
		}
	}

	protected void assertValid(Collaborator collaborator) throws Exception {
		boolean valid = true;

		if (collaborator.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actionIds", additionalAssertFieldName)) {
				if (collaborator.getActionIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (collaborator.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (collaborator.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateExpired", additionalAssertFieldName)) {
				if (collaborator.getDateExpired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (collaborator.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (collaborator.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("portrait", additionalAssertFieldName)) {
				if (collaborator.getPortrait() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("share", additionalAssertFieldName)) {
				if (collaborator.getShare() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (collaborator.getType() == null) {
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

	protected void assertValid(Page<Collaborator> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Collaborator> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Collaborator> collaborators = page.getItems();

		int size = collaborators.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.object.dto.v1_0.Collaborator.class)) {

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
		Collaborator collaborator1, Collaborator collaborator2) {

		if (collaborator1 == collaborator2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actionIds", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getActionIds(),
						collaborator2.getActionIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)collaborator1.getActions(),
						(Map)collaborator2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getCreator(),
						collaborator2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateExpired", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getDateExpired(),
						collaborator2.getDateExpired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						collaborator1.getExternalReferenceCode(),
						collaborator2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getId(), collaborator2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getName(), collaborator2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("portrait", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getPortrait(),
						collaborator2.getPortrait())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("share", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getShare(), collaborator2.getShare())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						collaborator1.getType(), collaborator2.getType())) {

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

		if (!(_collaboratorResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_collaboratorResource;

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
		EntityField entityField, String operator, Collaborator collaborator) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actionIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateExpired")) {
			if (operator.equals("between")) {
				Date date = collaborator.getDateExpired();

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

				sb.append(_format.format(collaborator.getDateExpired()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = collaborator.getExternalReferenceCode();

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
			Object object = collaborator.getName();

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

		if (entityFieldName.equals("portrait")) {
			Object object = collaborator.getPortrait();

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

		if (entityFieldName.equals("share")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			Object object = collaborator.getType();

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

	protected Collaborator randomCollaborator() throws Exception {
		return new Collaborator() {
			{
				dateExpired = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				portrait = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				share = RandomTestUtil.randomBoolean();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Collaborator randomIrrelevantCollaborator() throws Exception {
		Collaborator randomIrrelevantCollaborator = randomCollaborator();

		return randomIrrelevantCollaborator;
	}

	protected Collaborator randomPatchCollaborator() throws Exception {
		return randomCollaborator();
	}

	protected CollaboratorResource collaboratorResource;
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
		LogFactoryUtil.getLog(BaseCollaboratorResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.object.resource.v1_0.CollaboratorResource
		_collaboratorResource;

}