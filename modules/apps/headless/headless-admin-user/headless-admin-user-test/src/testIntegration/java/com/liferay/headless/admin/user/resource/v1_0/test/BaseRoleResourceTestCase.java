/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.user.client.dto.v1_0.Role;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.RoleResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.RoleSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseRoleResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		_roleResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		roleResource = RoleResource.builder(
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

		Role role1 = randomRole();

		String json = objectMapper.writeValueAsString(role1);

		Role role2 = RoleSerDes.toDTO(json);

		Assert.assertTrue(equals(role1, role2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Role role = randomRole();

		String json1 = objectMapper.writeValueAsString(role);
		String json2 = RoleSerDes.toJSON(role);

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

		Role role = randomRole();

		role.setDescription(regex);
		role.setExternalReferenceCode(regex);
		role.setName(regex);
		role.setRoleType(regex);

		String json = RoleSerDes.toJSON(role);

		Assert.assertFalse(json.contains(regex));

		role = RoleSerDes.toDTO(json);

		Assert.assertEquals(regex, role.getDescription());
		Assert.assertEquals(regex, role.getExternalReferenceCode());
		Assert.assertEquals(regex, role.getName());
		Assert.assertEquals(regex, role.getRoleType());
	}

	@Test
	public void testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role =
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(),
					testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId(),
					testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()));
	}

	protected Role
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		// No namespace

		Role role1 =
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + role1.getExternalReferenceCode() +
										"\"");

								put(
									"userAccountId",
									testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId());

								put(
									"organizationId",
									testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId());
							}
						})),
				"JSONObject/data",
				"Object/deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation"));

		// Using the namespace headlessAdminUser_v1_0

		Role role2 =
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											role2.getExternalReferenceCode() +
												"\"");

									put(
										"userAccountId",
										testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId());

									put(
										"organizationId",
										testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation"));
	}

	protected Long
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteOrganizationRoleUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testDeleteOrganizationRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				deleteOrganizationRoleUserAccountAssociationHttpResponse(
					role.getId(),
					testDeleteOrganizationRoleUserAccountAssociation_getUserAccountId(),
					testDeleteOrganizationRoleUserAccountAssociation_getOrganizationId()));
	}

	protected Role testDeleteOrganizationRoleUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteOrganizationRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteOrganizationRoleUserAccountAssociation_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrganizationRoleUserAccountAssociation()
		throws Exception {

		// No namespace

		Role role1 =
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrganizationRoleUserAccountAssociation",
						new HashMap<String, Object>() {
							{
								put("roleId", role1.getId());

								put(
									"userAccountId",
									testGraphQLDeleteOrganizationRoleUserAccountAssociation_getUserAccountId());

								put(
									"organizationId",
									testGraphQLDeleteOrganizationRoleUserAccountAssociation_getOrganizationId());
							}
						})),
				"JSONObject/data",
				"Object/deleteOrganizationRoleUserAccountAssociation"));

		// Using the namespace headlessAdminUser_v1_0

		Role role2 =
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteOrganizationRoleUserAccountAssociation",
							new HashMap<String, Object>() {
								{
									put("roleId", role2.getId());

									put(
										"userAccountId",
										testGraphQLDeleteOrganizationRoleUserAccountAssociation_getUserAccountId());

									put(
										"organizationId",
										testGraphQLDeleteOrganizationRoleUserAccountAssociation_getOrganizationId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteOrganizationRoleUserAccountAssociation"));
	}

	protected Long
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteRole() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testDeleteRole_addRole();

		assertHttpResponseStatusCode(
			204, roleResource.deleteRoleHttpResponse(role.getId()));

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role.getId()));
		assertHttpResponseStatusCode(404, roleResource.getRoleHttpResponse(0L));
	}

	protected Role testDeleteRole_addRole() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteRole() throws Exception {

		// No namespace

		Role role1 = testGraphQLDeleteRole_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteRole",
						new HashMap<String, Object>() {
							{
								put("roleId", role1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteRole"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"role",
					new HashMap<String, Object>() {
						{
							put("roleId", role1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		Role role2 = testGraphQLDeleteRole_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteRole",
							new HashMap<String, Object>() {
								{
									put("roleId", role2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteRole"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"role",
						new HashMap<String, Object>() {
							{
								put("roleId", role2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Role testGraphQLDeleteRole_addRole() throws Exception {
		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteRoleBatch() throws Exception {
		Role role1 = testDeleteRoleBatch_addRole();

		testDeleteRoleBatch_deleteRole(
			202, role1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role1.getId()));

		role1 = testDeleteRoleBatch_addRole();

		testDeleteRoleBatch_deleteRole(202, null, role1.getId());

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role1.getId()));

		role1 = testDeleteRoleBatch_addRole();
		Role role2 = testDeleteRoleBatch_addRole();

		testDeleteRoleBatch_deleteRole(
			202, role2.getExternalReferenceCode(), role1.getId());

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role1.getId()));
		assertHttpResponseStatusCode(
			200, roleResource.getRoleHttpResponse(role2.getId()));

		testDeleteRoleBatch_deleteRole(
			202, role2.getExternalReferenceCode(), role1.getId());

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role2.getId()));
	}

	protected Role testDeleteRoleBatch_addRole() throws Exception {
		return testDeleteRole_addRole();
	}

	protected void testDeleteRoleBatch_deleteRole(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			roleResource.deleteRoleBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testDeleteRoleByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testDeleteRoleByExternalReferenceCode_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.deleteRoleByExternalReferenceCodeHttpResponse(
				role.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			roleResource.getRoleByExternalReferenceCodeHttpResponse(
				role.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404, roleResource.getRoleByExternalReferenceCodeHttpResponse("-"));
	}

	protected Role testDeleteRoleByExternalReferenceCode_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteRoleByExternalReferenceCode()
		throws Exception {

		// No namespace

		Role role1 = testGraphQLDeleteRoleByExternalReferenceCode_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteRoleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + role1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data", "Object/deleteRoleByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"roleByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + role1.getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		Role role2 = testGraphQLDeleteRoleByExternalReferenceCode_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteRoleByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											role2.getExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteRoleByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"roleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + role2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Role testGraphQLDeleteRoleByExternalReferenceCode_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role =
			testDeleteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				deleteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(),
					testDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()));
	}

	protected Role
			testDeleteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		// No namespace

		Role role1 =
			testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteRoleByExternalReferenceCodeUserAccountAssociation",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + role1.getExternalReferenceCode() +
										"\"");

								put(
									"userAccountId",
									testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId());
							}
						})),
				"JSONObject/data",
				"Object/deleteRoleByExternalReferenceCodeUserAccountAssociation"));

		// Using the namespace headlessAdminUser_v1_0

		Role role2 =
			testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteRoleByExternalReferenceCodeUserAccountAssociation",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											role2.getExternalReferenceCode() +
												"\"");

									put(
										"userAccountId",
										testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteRoleByExternalReferenceCodeUserAccountAssociation"));
	}

	protected Long
			testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role
			testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteRoleUserAccountAssociation() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testDeleteRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.deleteRoleUserAccountAssociationHttpResponse(
				role.getId(),
				testDeleteRoleUserAccountAssociation_getUserAccountId()));
	}

	protected Role testDeleteRoleUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteRoleUserAccountAssociation() throws Exception {

		// No namespace

		Role role1 = testGraphQLDeleteRoleUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteRoleUserAccountAssociation",
						new HashMap<String, Object>() {
							{
								put("roleId", role1.getId());

								put(
									"userAccountId",
									testGraphQLDeleteRoleUserAccountAssociation_getUserAccountId());
							}
						})),
				"JSONObject/data", "Object/deleteRoleUserAccountAssociation"));

		// Using the namespace headlessAdminUser_v1_0

		Role role2 = testGraphQLDeleteRoleUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteRoleUserAccountAssociation",
							new HashMap<String, Object>() {
								{
									put("roleId", role2.getId());

									put(
										"userAccountId",
										testGraphQLDeleteRoleUserAccountAssociation_getUserAccountId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteRoleUserAccountAssociation"));
	}

	protected Long
			testGraphQLDeleteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role testGraphQLDeleteRoleUserAccountAssociation_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role =
			testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				deleteSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(),
					testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId(),
					testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId()));
	}

	protected Role
			testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Test
	public void testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		// No namespace

		Role role1 =
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteRoleByExternalReferenceCodeUserAccountAssociation",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + role1.getExternalReferenceCode() +
										"\"");

								put(
									"userAccountId",
									testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId());

								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteRoleByExternalReferenceCodeUserAccountAssociation"));

		// Using the namespace headlessAdminUser_v1_0

		Role role2 =
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteSiteRoleByExternalReferenceCodeUserAccountAssociation",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											role2.getExternalReferenceCode() +
												"\"");

									put(
										"userAccountId",
										testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId());

									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteSiteRoleByExternalReferenceCodeUserAccountAssociation"));
	}

	protected Long
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Role
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testDeleteSiteRoleUserAccountAssociation() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testDeleteSiteRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.deleteSiteRoleUserAccountAssociationHttpResponse(
				role.getId(),
				testDeleteSiteRoleUserAccountAssociation_getUserAccountId(),
				testDeleteSiteRoleUserAccountAssociation_getSiteId()));
	}

	protected Role testDeleteSiteRoleUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteSiteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteSiteRoleUserAccountAssociation_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Test
	public void testGraphQLDeleteSiteRoleUserAccountAssociation()
		throws Exception {

		// No namespace

		Role role1 = testGraphQLDeleteSiteRoleUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteRoleUserAccountAssociation",
						new HashMap<String, Object>() {
							{
								put("roleId", role1.getId());

								put(
									"userAccountId",
									testGraphQLDeleteSiteRoleUserAccountAssociation_getUserAccountId());

								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteRoleUserAccountAssociation_getSiteId() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteRoleUserAccountAssociation"));

		// Using the namespace headlessAdminUser_v1_0

		Role role2 = testGraphQLDeleteSiteRoleUserAccountAssociation_addRole();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteSiteRoleUserAccountAssociation",
							new HashMap<String, Object>() {
								{
									put("roleId", role2.getId());

									put(
										"userAccountId",
										testGraphQLDeleteSiteRoleUserAccountAssociation_getUserAccountId());

									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteRoleUserAccountAssociation_getSiteId() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteSiteRoleUserAccountAssociation"));
	}

	protected Long
			testGraphQLDeleteSiteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGraphQLDeleteSiteRoleUserAccountAssociation_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Role testGraphQLDeleteSiteRoleUserAccountAssociation_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testGetRole() throws Exception {
		Role postRole = testGetRole_addRole();

		Role getRole = roleResource.getRole(postRole.getId());

		assertEquals(postRole, getRole);
		assertValid(getRole);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Role postRole = testGetRole_addRole();

		Role getRole = roleResource.getRole(postRole.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.admin.user.dto.v1_0.Role"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(postRole.getId());

		assertEquals(getRole, RoleSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected Role testGetRole_addRole() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetRole() throws Exception {
		Role role = testGraphQLGetRole_addRole();

		// No namespace

		Assert.assertTrue(
			equals(
				role,
				RoleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"role",
								new HashMap<String, Object>() {
									{
										put("roleId", role.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/role"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				role,
				RoleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"role",
									new HashMap<String, Object>() {
										{
											put("roleId", role.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/role"))));
	}

	@Test
	public void testGraphQLGetRoleNotFound() throws Exception {
		Long irrelevantRoleId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"role",
						new HashMap<String, Object>() {
							{
								put("roleId", irrelevantRoleId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"role",
							new HashMap<String, Object>() {
								{
									put("roleId", irrelevantRoleId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Role testGraphQLGetRole_addRole() throws Exception {
		return testGraphQLRole_addRole();
	}

	@Test
	public void testGetRoleByExternalReferenceCode() throws Exception {
		Role postRole = testGetRoleByExternalReferenceCode_addRole();

		Role getRole = roleResource.getRoleByExternalReferenceCode(
			postRole.getExternalReferenceCode());

		assertEquals(postRole, getRole);
		assertValid(getRole);
	}

	protected Role testGetRoleByExternalReferenceCode_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetRoleByExternalReferenceCode() throws Exception {
		Role role = testGraphQLGetRoleByExternalReferenceCode_addRole();

		// No namespace

		Assert.assertTrue(
			equals(
				role,
				RoleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"roleByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												role.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/roleByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				role,
				RoleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"roleByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													role.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/roleByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetRoleByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"roleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"roleByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Role testGraphQLGetRoleByExternalReferenceCode_addRole()
		throws Exception {

		return testGraphQLRole_addRole();
	}

	@Test
	public void testGetRolesPage() throws Exception {
		Page<Role> page = roleResource.getRolesPage(
			null, null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		Role role1 = testGetRolesPage_addRole(randomRole());

		Role role2 = testGetRolesPage_addRole(randomRole());

		page = roleResource.getRolesPage(
			null, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(role1, (List<Role>)page.getItems());
		assertContains(role2, (List<Role>)page.getItems());
		assertValid(page, testGetRolesPage_getExpectedActions());

		roleResource.deleteRole(role1.getId());

		roleResource.deleteRole(role2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetRolesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetRolesPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Role role1 = randomRole();

		role1 = testGetRolesPage_addRole(role1);

		for (EntityField entityField : entityFields) {
			Page<Role> page = roleResource.getRolesPage(
				null, null, getFilterString(entityField, "between", role1),
				Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(role1), (List<Role>)page.getItems());
		}
	}

	@Test
	public void testGetRolesPageWithFilterDoubleEquals() throws Exception {
		testGetRolesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetRolesPageWithFilterStringContains() throws Exception {
		testGetRolesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetRolesPageWithFilterStringEquals() throws Exception {
		testGetRolesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetRolesPageWithFilterStringStartsWith() throws Exception {
		testGetRolesPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetRolesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Role role1 = testGetRolesPage_addRole(randomRole());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role2 = testGetRolesPage_addRole(randomRole());

		for (EntityField entityField : entityFields) {
			Page<Role> page = roleResource.getRolesPage(
				null, null, getFilterString(entityField, operator, role1),
				Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(role1), (List<Role>)page.getItems());
		}
	}

	@Test
	public void testGetRolesPageWithPagination() throws Exception {
		Page<Role> rolesPage = roleResource.getRolesPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(rolesPage.getTotalCount());

		Role role1 = testGetRolesPage_addRole(randomRole());

		Role role2 = testGetRolesPage_addRole(randomRole());

		Role role3 = testGetRolesPage_addRole(randomRole());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Role> page1 = roleResource.getRolesPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(role1, (List<Role>)page1.getItems());

			Page<Role> page2 = roleResource.getRolesPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(role2, (List<Role>)page2.getItems());

			Page<Role> page3 = roleResource.getRolesPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(role3, (List<Role>)page3.getItems());
		}
		else {
			Page<Role> page1 = roleResource.getRolesPage(
				null, null, null, Pagination.of(1, totalCount + 2));

			List<Role> roles1 = (List<Role>)page1.getItems();

			Assert.assertEquals(
				roles1.toString(), totalCount + 2, roles1.size());

			Page<Role> page2 = roleResource.getRolesPage(
				null, null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Role> roles2 = (List<Role>)page2.getItems();

			Assert.assertEquals(roles2.toString(), 1, roles2.size());

			Page<Role> page3 = roleResource.getRolesPage(
				null, null, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(role1, (List<Role>)page3.getItems());
			assertContains(role2, (List<Role>)page3.getItems());
			assertContains(role3, (List<Role>)page3.getItems());
		}
	}

	protected Role testGetRolesPage_addRole(Role role) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetRolesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"roles",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject rolesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/roles");

		long totalCount = rolesJSONObject.getLong("totalCount");

		Role role1 = testGraphQLRole_addRole(randomRole());

		Role role2 = testGraphQLRole_addRole(randomRole());

		rolesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/roles");

		Assert.assertEquals(
			totalCount + 2, rolesJSONObject.getLong("totalCount"));

		assertContains(
			role1,
			Arrays.asList(
				RoleSerDes.toDTOs(rolesJSONObject.getString("items"))));
		assertContains(
			role2,
			Arrays.asList(
				RoleSerDes.toDTOs(rolesJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		rolesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/roles");

		Assert.assertEquals(
			totalCount + 2, rolesJSONObject.getLong("totalCount"));

		assertContains(
			role1,
			Arrays.asList(
				RoleSerDes.toDTOs(rolesJSONObject.getString("items"))));
		assertContains(
			role2,
			Arrays.asList(
				RoleSerDes.toDTOs(rolesJSONObject.getString("items"))));
	}

	@Test
	public void testPatchRole() throws Exception {
		Role postRole = testPatchRole_addRole();

		Role randomPatchRole = randomPatchRole();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role patchRole = roleResource.patchRole(
			postRole.getId(), randomPatchRole);

		Role expectedPatchRole = postRole.clone();

		BeanTestUtil.copyProperties(randomPatchRole, expectedPatchRole);

		Role getRole = roleResource.getRole(patchRole.getId());

		assertEquals(expectedPatchRole, getRole);
		assertValid(getRole);
	}

	protected Role testPatchRole_addRole() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchRoleByExternalReferenceCode() throws Exception {
		Role postRole = testPatchRoleByExternalReferenceCode_addRole();

		Role randomPatchRole = randomPatchRole();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role patchRole = roleResource.patchRoleByExternalReferenceCode(
			postRole.getExternalReferenceCode(), randomPatchRole);

		Role expectedPatchRole = postRole.clone();

		BeanTestUtil.copyProperties(randomPatchRole, expectedPatchRole);

		Role getRole = roleResource.getRoleByExternalReferenceCode(
			patchRole.getExternalReferenceCode());

		assertEquals(expectedPatchRole, getRole);
		assertValid(getRole);
	}

	protected Role testPatchRoleByExternalReferenceCode_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role =
			testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				postOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(),
					testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId(),
					testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.
				postOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					"-",
					testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId(),
					testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()));
	}

	protected Long
			testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role
			testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrganizationRoleUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testPostOrganizationRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.postOrganizationRoleUserAccountAssociationHttpResponse(
				role.getId(),
				testPostOrganizationRoleUserAccountAssociation_getUserAccountId(),
				testPostOrganizationRoleUserAccountAssociation_getOrganizationId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.postOrganizationRoleUserAccountAssociationHttpResponse(
				0L,
				testPostOrganizationRoleUserAccountAssociation_getUserAccountId(),
				testPostOrganizationRoleUserAccountAssociation_getOrganizationId()));
	}

	protected Long
			testPostOrganizationRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPostOrganizationRoleUserAccountAssociation_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role testPostOrganizationRoleUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostRole() throws Exception {
		Role randomRole = randomRole();

		Role postRole = testPostRole_addRole(randomRole);

		assertEquals(randomRole, postRole);
		assertValid(postRole);
	}

	protected Role testPostRole_addRole(Role role) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostRole() throws Exception {
		Role randomRole = randomRole();

		Role role = testGraphQLRole_addRole(randomRole);

		Assert.assertTrue(equals(randomRole, role));
	}

	@Test
	public void testPostRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role =
			testPostRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				postRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(),
					testPostRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.
				postRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					"-",
					testPostRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()));
	}

	protected Long
			testPostRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role
			testPostRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostRoleUserAccountAssociation() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testPostRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.postRoleUserAccountAssociationHttpResponse(
				role.getId(),
				testPostRoleUserAccountAssociation_getUserAccountId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.postRoleUserAccountAssociationHttpResponse(
				0L, testPostRoleUserAccountAssociation_getUserAccountId()));
	}

	protected Long testPostRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role testPostRoleUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role =
			testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				postSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(),
					testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId(),
					testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.
				postSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					"-",
					testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId(),
					testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId()));
	}

	protected Long
			testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Role
			testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteRoleUserAccountAssociation() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Role role = testPostSiteRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.postSiteRoleUserAccountAssociationHttpResponse(
				role.getId(),
				testPostSiteRoleUserAccountAssociation_getUserAccountId(),
				testPostSiteRoleUserAccountAssociation_getSiteId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.postSiteRoleUserAccountAssociationHttpResponse(
				0L, testPostSiteRoleUserAccountAssociation_getUserAccountId(),
				testPostSiteRoleUserAccountAssociation_getSiteId()));
	}

	protected Long testPostSiteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPostSiteRoleUserAccountAssociation_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Role testPostSiteRoleUserAccountAssociation_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutRole() throws Exception {
		Role postRole = testPutRole_addRole();

		Role randomRole = randomRole();

		Role putRole = roleResource.putRole(postRole.getId(), randomRole);

		assertEquals(randomRole, putRole);
		assertValid(putRole);

		Role getRole = roleResource.getRole(putRole.getId());

		assertEquals(randomRole, getRole);
		assertValid(getRole);
	}

	protected Role testPutRole_addRole() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutRoleByExternalReferenceCode() throws Exception {
		Role postRole = testPutRoleByExternalReferenceCode_addRole();

		Role randomRole = randomRole();

		Role putRole = roleResource.putRoleByExternalReferenceCode(
			postRole.getExternalReferenceCode(), randomRole);

		assertEquals(randomRole, putRole);
		assertValid(putRole);

		Role getRole = roleResource.getRoleByExternalReferenceCode(
			putRole.getExternalReferenceCode());

		assertEquals(randomRole, getRole);
		assertValid(getRole);

		Role newRole = testPutRoleByExternalReferenceCode_createRole();

		putRole = roleResource.putRoleByExternalReferenceCode(
			newRole.getExternalReferenceCode(), newRole);

		assertEquals(newRole, putRole);
		assertValid(putRole);

		getRole = roleResource.getRoleByExternalReferenceCode(
			putRole.getExternalReferenceCode());

		assertEquals(newRole, getRole);

		Assert.assertEquals(
			newRole.getExternalReferenceCode(),
			putRole.getExternalReferenceCode());
	}

	protected Role testPutRoleByExternalReferenceCode_addRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Role testPutRoleByExternalReferenceCode_createRole()
		throws Exception {

		return randomRole();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Role role1 = testBatchEngineDeleteImportTask_addRole();

		testBatchEngineDeleteImportTask_deleteRole(
			200, role1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role1.getId()));

		role1 = testBatchEngineDeleteImportTask_addRole();

		testBatchEngineDeleteImportTask_deleteRole(200, null, role1.getId());

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role1.getId()));

		role1 = testBatchEngineDeleteImportTask_addRole();
		Role role2 = testBatchEngineDeleteImportTask_addRole();

		testBatchEngineDeleteImportTask_deleteRole(
			200, role2.getExternalReferenceCode(), role1.getId());

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role1.getId()));
		assertHttpResponseStatusCode(
			200, roleResource.getRoleHttpResponse(role2.getId()));

		testBatchEngineDeleteImportTask_deleteRole(
			200, role2.getExternalReferenceCode(), role1.getId());

		assertHttpResponseStatusCode(
			404, roleResource.getRoleHttpResponse(role2.getId()));
	}

	protected Role testBatchEngineDeleteImportTask_addRole() throws Exception {
		return testDeleteRole_addRole();
	}

	protected void testBatchEngineDeleteImportTask_deleteRole(
			int expectedStatusCode, String externalReferenceCode, Long id,
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
				"com.liferay.headless.admin.user.dto.v1_0.Role", null, null,
				null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Role testGraphQLRole_addRole() throws Exception {
		return testGraphQLRole_addRole(randomRole());
	}

	protected Role testGraphQLRole_addRole(Role role) throws Exception {
		JSONDeserializer<Role> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Role.class)) {
			if (getGraphQLValue(field.get(role)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(role)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createRole",
						new HashMap<String, Object>() {
							{
								put("role", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createRole"),
			Role.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(Role role, List<Role> roles) {
		boolean contains = false;

		for (Role item : roles) {
			if (equals(role, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(roles + " does not contain " + role, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Role role1, Role role2) {
		Assert.assertTrue(
			role1 + " does not equal " + role2, equals(role1, role2));
	}

	protected void assertEquals(List<Role> roles1, List<Role> roles2) {
		Assert.assertEquals(roles1.size(), roles2.size());

		for (int i = 0; i < roles1.size(); i++) {
			Role role1 = roles1.get(i);
			Role role2 = roles2.get(i);

			assertEquals(role1, role2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Role> roles1, List<Role> roles2) {

		Assert.assertEquals(roles1.size(), roles2.size());

		for (Role role1 : roles1) {
			boolean contains = false;

			for (Role role2 : roles2) {
				if (equals(role1, role2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(roles2 + " does not contain " + role1, contains);
		}
	}

	protected void assertValid(Role role) throws Exception {
		boolean valid = true;

		if (role.getDateCreated() == null) {
			valid = false;
		}

		if (role.getDateModified() == null) {
			valid = false;
		}

		if (role.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (role.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (role.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (role.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (role.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (role.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (role.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (role.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (role.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (role.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("rolePermissions", additionalAssertFieldName)) {
				if (role.getRolePermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roleType", additionalAssertFieldName)) {
				if (role.getRoleType() == null) {
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

	protected void assertValid(Page<Role> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Role> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Role> roles = page.getItems();

		int size = roles.size();

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
					com.liferay.headless.admin.user.dto.v1_0.Role.class)) {

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

	protected boolean equals(Role role1, Role role2) {
		if (role1 == role2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals((Map)role1.getActions(), (Map)role2.getActions())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						role1.getAvailableLanguages(),
						role2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getCreator(), role2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getDateCreated(), role2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getDateModified(), role2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getDescription(), role2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)role1.getDescription_i18n(),
						(Map)role2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						role1.getExternalReferenceCode(),
						role2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(role1.getId(), role2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(role1.getName(), role2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)role1.getName_i18n(), (Map)role2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getPermissions(), role2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("rolePermissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getRolePermissions(),
						role2.getRolePermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roleType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						role1.getRoleType(), role2.getRoleType())) {

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

		if (!(_roleResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_roleResource;

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
		EntityField entityField, String operator, Role role) {

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

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = role.getDateCreated();

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

				sb.append(_format.format(role.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = role.getDateModified();

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

				sb.append(_format.format(role.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = role.getDescription();

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
			Object object = role.getExternalReferenceCode();

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
			Object object = role.getName();

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

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("rolePermissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("roleType")) {
			Object object = role.getRoleType();

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

	protected Role randomRole() throws Exception {
		return new Role() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				roleType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Role randomIrrelevantRole() throws Exception {
		Role randomIrrelevantRole = randomRole();

		return randomIrrelevantRole;
	}

	protected Role randomPatchRole() throws Exception {
		return randomRole();
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

	protected RoleResource roleResource;
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
		LogFactoryUtil.getLog(BaseRoleResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.RoleResource
		_roleResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}