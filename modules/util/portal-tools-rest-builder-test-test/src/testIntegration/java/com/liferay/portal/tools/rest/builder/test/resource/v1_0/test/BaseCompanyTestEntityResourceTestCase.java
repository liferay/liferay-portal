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

import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.permission.Permission;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.CompanyTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.CompanyTestEntitySerDes;
import com.liferay.portal.util.PropsValues;
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
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public abstract class BaseCompanyTestEntityResourceTestCase {

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

		_companyTestEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		companyTestEntityResource = CompanyTestEntityResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		permissionsCompanyTestEntityResource =
			CompanyTestEntityResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).parameter(
				"nestedFields", "permissions"
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

		CompanyTestEntity companyTestEntity1 = randomCompanyTestEntity();

		String json = objectMapper.writeValueAsString(companyTestEntity1);

		CompanyTestEntity companyTestEntity2 = CompanyTestEntitySerDes.toDTO(
			json);

		Assert.assertTrue(equals(companyTestEntity1, companyTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		CompanyTestEntity companyTestEntity = randomCompanyTestEntity();

		String json1 = objectMapper.writeValueAsString(companyTestEntity);
		String json2 = CompanyTestEntitySerDes.toJSON(companyTestEntity);

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

		CompanyTestEntity companyTestEntity = randomCompanyTestEntity();

		companyTestEntity.setDescription(regex);
		companyTestEntity.setExternalReferenceCode(regex);

		String json = CompanyTestEntitySerDes.toJSON(companyTestEntity);

		Assert.assertFalse(json.contains(regex));

		companyTestEntity = CompanyTestEntitySerDes.toDTO(json);

		Assert.assertEquals(regex, companyTestEntity.getDescription());
		Assert.assertEquals(
			regex, companyTestEntity.getExternalReferenceCode());
	}

	@Test
	public void testGetCompanyTestEntitiesPage() throws Exception {
		Page<CompanyTestEntity> page =
			companyTestEntityResource.getCompanyTestEntitiesPage();

		long totalCount = page.getTotalCount();

		CompanyTestEntity companyTestEntity1 =
			testGetCompanyTestEntitiesPage_addCompanyTestEntity(
				randomCompanyTestEntity());

		CompanyTestEntity companyTestEntity2 =
			testGetCompanyTestEntitiesPage_addCompanyTestEntity(
				randomCompanyTestEntity());

		page = companyTestEntityResource.getCompanyTestEntitiesPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			companyTestEntity1, (List<CompanyTestEntity>)page.getItems());
		assertContains(
			companyTestEntity2, (List<CompanyTestEntity>)page.getItems());
		assertValid(page, testGetCompanyTestEntitiesPage_getExpectedActions());

		for (CompanyTestEntity companyTestEntity : page.getItems()) {
			Assert.assertNull(companyTestEntity.getPermissions());
		}

		page =
			permissionsCompanyTestEntityResource.getCompanyTestEntitiesPage();

		for (CompanyTestEntity companyTestEntity : page.getItems()) {
			Assert.assertNotNull(companyTestEntity.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetCompanyTestEntitiesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected CompanyTestEntity
			testGetCompanyTestEntitiesPage_addCompanyTestEntity(
				CompanyTestEntity companyTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetCompanyTestEntity() throws Exception {
		CompanyTestEntity postCompanyTestEntity =
			testGetCompanyTestEntity_addCompanyTestEntity();

		CompanyTestEntity getCompanyTestEntity =
			companyTestEntityResource.getCompanyTestEntity(
				postCompanyTestEntity.getId());

		assertEquals(postCompanyTestEntity, getCompanyTestEntity);
		assertValid(getCompanyTestEntity);

		Assert.assertNull(getCompanyTestEntity.getPermissions());

		getCompanyTestEntity =
			permissionsCompanyTestEntityResource.getCompanyTestEntity(
				postCompanyTestEntity.getId());

		Assert.assertNotNull(getCompanyTestEntity.getPermissions());
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		CompanyTestEntity postCompanyTestEntity =
			testGetCompanyTestEntity_addCompanyTestEntity();

		CompanyTestEntity getCompanyTestEntity =
			companyTestEntityResource.getCompanyTestEntity(
				postCompanyTestEntity.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.CompanyTestEntity"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postCompanyTestEntity.getId());

		assertEquals(
			getCompanyTestEntity,
			CompanyTestEntitySerDes.toDTO(item.toString()));
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

	protected CompanyTestEntity testGetCompanyTestEntity_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetCompanyTestEntityByExternalReferenceCode()
		throws Exception {

		CompanyTestEntity postCompanyTestEntity =
			testGetCompanyTestEntityByExternalReferenceCode_addCompanyTestEntity();

		CompanyTestEntity getCompanyTestEntity =
			companyTestEntityResource.
				getCompanyTestEntityByExternalReferenceCode(
					postCompanyTestEntity.getExternalReferenceCode());

		assertEquals(postCompanyTestEntity, getCompanyTestEntity);
		assertValid(getCompanyTestEntity);

		Assert.assertNull(getCompanyTestEntity.getPermissions());

		getCompanyTestEntity =
			permissionsCompanyTestEntityResource.
				getCompanyTestEntityByExternalReferenceCode(
					postCompanyTestEntity.getExternalReferenceCode());

		Assert.assertNotNull(getCompanyTestEntity.getPermissions());
	}

	protected CompanyTestEntity
			testGetCompanyTestEntityByExternalReferenceCode_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetCompanyTestEntityPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CompanyTestEntity postCompanyTestEntity =
			testGetCompanyTestEntityPermissionsPage_addCompanyTestEntity();

		Page<Permission> page =
			companyTestEntityResource.getCompanyTestEntityPermissionsPage(
				postCompanyTestEntity.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected CompanyTestEntity
			testGetCompanyTestEntityPermissionsPage_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchCompanyTestEntity() throws Exception {
		CompanyTestEntity postCompanyTestEntity =
			testPatchCompanyTestEntity_addCompanyTestEntity();

		CompanyTestEntity randomPatchCompanyTestEntity =
			randomPatchCompanyTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CompanyTestEntity patchCompanyTestEntity =
			companyTestEntityResource.patchCompanyTestEntity(
				postCompanyTestEntity.getId(), randomPatchCompanyTestEntity);

		CompanyTestEntity expectedPatchCompanyTestEntity =
			postCompanyTestEntity.clone();

		BeanTestUtil.copyProperties(
			randomPatchCompanyTestEntity, expectedPatchCompanyTestEntity);

		CompanyTestEntity getCompanyTestEntity =
			companyTestEntityResource.getCompanyTestEntity(
				patchCompanyTestEntity.getId());

		assertEquals(expectedPatchCompanyTestEntity, getCompanyTestEntity);
		assertValid(getCompanyTestEntity);
	}

	protected CompanyTestEntity
			testPatchCompanyTestEntity_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCompanyTestEntity() throws Exception {
		CompanyTestEntity randomCompanyTestEntity = randomCompanyTestEntity();

		CompanyTestEntity postCompanyTestEntity =
			testPostCompanyTestEntity_addCompanyTestEntity(
				randomCompanyTestEntity);

		assertEquals(randomCompanyTestEntity, postCompanyTestEntity);
		assertValid(postCompanyTestEntity);

		CompanyTestEntity randomPermissionsCompanyTestEntity1 =
			randomPermissionsCompanyTestEntity();

		CompanyTestEntity postPermissionsCompanyTestEntity1 =
			testPostCompanyTestEntity_addCompanyTestEntity(
				randomPermissionsCompanyTestEntity1);

		Assert.assertNull(postPermissionsCompanyTestEntity1.getPermissions());

		CompanyTestEntity randomPermissionsCompanyTestEntity2 =
			randomPermissionsCompanyTestEntity();

		CompanyTestEntity postPermissionsCompanyTestEntity2 =
			testPostCompanyTestEntity_addPermissionsCompanyTestEntity(
				randomPermissionsCompanyTestEntity2);

		Assert.assertNotNull(
			postPermissionsCompanyTestEntity2.getPermissions());
	}

	protected CompanyTestEntity testPostCompanyTestEntity_addCompanyTestEntity(
			CompanyTestEntity companyTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected CompanyTestEntity
			testPostCompanyTestEntity_addPermissionsCompanyTestEntity(
				CompanyTestEntity companyTestEntity)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutCompanyTestEntity() throws Exception {
		CompanyTestEntity postCompanyTestEntity =
			testPutCompanyTestEntity_addCompanyTestEntity();

		CompanyTestEntity randomCompanyTestEntity = randomCompanyTestEntity();

		CompanyTestEntity putCompanyTestEntity =
			companyTestEntityResource.putCompanyTestEntity(
				postCompanyTestEntity.getId(), randomCompanyTestEntity);

		assertEquals(randomCompanyTestEntity, putCompanyTestEntity);
		assertValid(putCompanyTestEntity);

		Assert.assertNull(putCompanyTestEntity.getPermissions());

		CompanyTestEntity getCompanyTestEntity =
			companyTestEntityResource.getCompanyTestEntity(
				putCompanyTestEntity.getId());

		assertEquals(randomCompanyTestEntity, getCompanyTestEntity);
		assertValid(getCompanyTestEntity);

		CompanyTestEntity randomPermissionsCompanyTestEntity =
			randomPermissionsCompanyTestEntity();

		putCompanyTestEntity = companyTestEntityResource.putCompanyTestEntity(
			postCompanyTestEntity.getId(), randomPermissionsCompanyTestEntity);

		assertEquals(randomPermissionsCompanyTestEntity, putCompanyTestEntity);
		assertValid(putCompanyTestEntity);

		Assert.assertNull(putCompanyTestEntity.getPermissions());

		putCompanyTestEntity =
			permissionsCompanyTestEntityResource.putCompanyTestEntity(
				postCompanyTestEntity.getId(),
				randomPermissionsCompanyTestEntity);

		Assert.assertNotNull(putCompanyTestEntity.getPermissions());
	}

	protected CompanyTestEntity testPutCompanyTestEntity_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutCompanyTestEntityByExternalReferenceCode()
		throws Exception {

		CompanyTestEntity postCompanyTestEntity =
			testPutCompanyTestEntityByExternalReferenceCode_addCompanyTestEntity();

		CompanyTestEntity randomCompanyTestEntity = randomCompanyTestEntity();

		CompanyTestEntity putCompanyTestEntity =
			companyTestEntityResource.
				putCompanyTestEntityByExternalReferenceCode(
					postCompanyTestEntity.getExternalReferenceCode(),
					randomCompanyTestEntity);

		assertEquals(randomCompanyTestEntity, putCompanyTestEntity);
		assertValid(putCompanyTestEntity);

		Assert.assertNull(putCompanyTestEntity.getPermissions());

		CompanyTestEntity getCompanyTestEntity =
			companyTestEntityResource.
				getCompanyTestEntityByExternalReferenceCode(
					putCompanyTestEntity.getExternalReferenceCode());

		assertEquals(randomCompanyTestEntity, getCompanyTestEntity);
		assertValid(getCompanyTestEntity);

		CompanyTestEntity randomPermissionsCompanyTestEntity =
			randomPermissionsCompanyTestEntity();

		putCompanyTestEntity =
			companyTestEntityResource.
				putCompanyTestEntityByExternalReferenceCode(
					postCompanyTestEntity.getExternalReferenceCode(),
					randomPermissionsCompanyTestEntity);

		assertEquals(randomPermissionsCompanyTestEntity, putCompanyTestEntity);
		assertValid(putCompanyTestEntity);

		Assert.assertNull(putCompanyTestEntity.getPermissions());

		putCompanyTestEntity =
			permissionsCompanyTestEntityResource.
				putCompanyTestEntityByExternalReferenceCode(
					postCompanyTestEntity.getExternalReferenceCode(),
					randomPermissionsCompanyTestEntity);

		Assert.assertNotNull(putCompanyTestEntity.getPermissions());

		CompanyTestEntity newCompanyTestEntity =
			testPutCompanyTestEntityByExternalReferenceCode_createCompanyTestEntity();

		putCompanyTestEntity =
			companyTestEntityResource.
				putCompanyTestEntityByExternalReferenceCode(
					newCompanyTestEntity.getExternalReferenceCode(),
					newCompanyTestEntity);

		assertEquals(newCompanyTestEntity, putCompanyTestEntity);
		assertValid(putCompanyTestEntity);

		getCompanyTestEntity =
			companyTestEntityResource.
				getCompanyTestEntityByExternalReferenceCode(
					putCompanyTestEntity.getExternalReferenceCode());

		assertEquals(newCompanyTestEntity, getCompanyTestEntity);

		Assert.assertEquals(
			newCompanyTestEntity.getExternalReferenceCode(),
			putCompanyTestEntity.getExternalReferenceCode());
	}

	protected CompanyTestEntity
			testPutCompanyTestEntityByExternalReferenceCode_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected CompanyTestEntity
			testPutCompanyTestEntityByExternalReferenceCode_createCompanyTestEntity()
		throws Exception {

		return randomCompanyTestEntity();
	}

	@Test
	public void testPutCompanyTestEntityPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CompanyTestEntity companyTestEntity =
			testPutCompanyTestEntityPermissionsPage_addCompanyTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			companyTestEntityResource.
				putCompanyTestEntityPermissionsPageHttpResponse(
					companyTestEntity.getId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			companyTestEntityResource.
				putCompanyTestEntityPermissionsPageHttpResponse(
					0L,
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected CompanyTestEntity
			testPutCompanyTestEntityPermissionsPage_addCompanyTestEntity()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		CompanyTestEntity companyTestEntity,
		List<CompanyTestEntity> companyTestEntities) {

		boolean contains = false;

		for (CompanyTestEntity item : companyTestEntities) {
			if (equals(companyTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			companyTestEntities + " does not contain " + companyTestEntity,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		CompanyTestEntity companyTestEntity1,
		CompanyTestEntity companyTestEntity2) {

		Assert.assertTrue(
			companyTestEntity1 + " does not equal " + companyTestEntity2,
			equals(companyTestEntity1, companyTestEntity2));
	}

	protected void assertEquals(
		List<CompanyTestEntity> companyTestEntities1,
		List<CompanyTestEntity> companyTestEntities2) {

		Assert.assertEquals(
			companyTestEntities1.size(), companyTestEntities2.size());

		for (int i = 0; i < companyTestEntities1.size(); i++) {
			CompanyTestEntity companyTestEntity1 = companyTestEntities1.get(i);
			CompanyTestEntity companyTestEntity2 = companyTestEntities2.get(i);

			assertEquals(companyTestEntity1, companyTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<CompanyTestEntity> companyTestEntities1,
		List<CompanyTestEntity> companyTestEntities2) {

		Assert.assertEquals(
			companyTestEntities1.size(), companyTestEntities2.size());

		for (CompanyTestEntity companyTestEntity1 : companyTestEntities1) {
			boolean contains = false;

			for (CompanyTestEntity companyTestEntity2 : companyTestEntities2) {
				if (equals(companyTestEntity1, companyTestEntity2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				companyTestEntities2 + " does not contain " +
					companyTestEntity1,
				contains);
		}
	}

	protected void assertValid(CompanyTestEntity companyTestEntity)
		throws Exception {

		boolean valid = true;

		if (companyTestEntity.getDateCreated() == null) {
			valid = false;
		}

		if (companyTestEntity.getDateModified() == null) {
			valid = false;
		}

		if (companyTestEntity.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (companyTestEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (companyTestEntity.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (companyTestEntity.getPermissions() == null) {
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

	protected void assertValid(Page<CompanyTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<CompanyTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<CompanyTestEntity> companyTestEntities =
			page.getItems();

		int size = companyTestEntities.size();

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
						CompanyTestEntity.class)) {

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
		CompanyTestEntity companyTestEntity1,
		CompanyTestEntity companyTestEntity2) {

		if (companyTestEntity1 == companyTestEntity2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						companyTestEntity1.getDateCreated(),
						companyTestEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						companyTestEntity1.getDateModified(),
						companyTestEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						companyTestEntity1.getDescription(),
						companyTestEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						companyTestEntity1.getExternalReferenceCode(),
						companyTestEntity2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						companyTestEntity1.getId(),
						companyTestEntity2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						companyTestEntity1.getPermissions(),
						companyTestEntity2.getPermissions())) {

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

		if (!(_companyTestEntityResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_companyTestEntityResource;

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
		CompanyTestEntity companyTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = companyTestEntity.getDateCreated();

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

				sb.append(_format.format(companyTestEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = companyTestEntity.getDateModified();

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

				sb.append(_format.format(companyTestEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = companyTestEntity.getDescription();

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
			Object object = companyTestEntity.getExternalReferenceCode();

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

	protected CompanyTestEntity randomCompanyTestEntity() throws Exception {
		return new CompanyTestEntity() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
			}
		};
	}

	protected CompanyTestEntity randomIrrelevantCompanyTestEntity()
		throws Exception {

		CompanyTestEntity randomIrrelevantCompanyTestEntity =
			randomCompanyTestEntity();

		return randomIrrelevantCompanyTestEntity;
	}

	protected CompanyTestEntity randomPatchCompanyTestEntity()
		throws Exception {

		return randomCompanyTestEntity();
	}

	protected CompanyTestEntity randomPermissionsCompanyTestEntity()
		throws Exception {

		CompanyTestEntity companyTestEntity = randomCompanyTestEntity();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		companyTestEntity.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return companyTestEntity;
	}

	protected CompanyTestEntityResource companyTestEntityResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected CompanyTestEntityResource permissionsCompanyTestEntityResource;
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
		LogFactoryUtil.getLog(BaseCompanyTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		CompanyTestEntityResource _companyTestEntityResource;

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