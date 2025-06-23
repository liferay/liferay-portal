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
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.SiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.permission.Permission;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.SiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.SiteTestEntitySerDes;
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
public abstract class BaseSiteTestEntityResourceTestCase {

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

		_siteTestEntityResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		siteTestEntityResource = SiteTestEntityResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		permissionsSiteTestEntityResource = SiteTestEntityResource.builder(
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

		SiteTestEntity siteTestEntity1 = randomSiteTestEntity();

		String json = objectMapper.writeValueAsString(siteTestEntity1);

		SiteTestEntity siteTestEntity2 = SiteTestEntitySerDes.toDTO(json);

		Assert.assertTrue(equals(siteTestEntity1, siteTestEntity2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SiteTestEntity siteTestEntity = randomSiteTestEntity();

		String json1 = objectMapper.writeValueAsString(siteTestEntity);
		String json2 = SiteTestEntitySerDes.toJSON(siteTestEntity);

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

		SiteTestEntity siteTestEntity = randomSiteTestEntity();

		siteTestEntity.setDescription(regex);
		siteTestEntity.setExternalReferenceCode(regex);

		String json = SiteTestEntitySerDes.toJSON(siteTestEntity);

		Assert.assertFalse(json.contains(regex));

		siteTestEntity = SiteTestEntitySerDes.toDTO(json);

		Assert.assertEquals(regex, siteTestEntity.getDescription());
		Assert.assertEquals(regex, siteTestEntity.getExternalReferenceCode());
	}

	@Test
	public void testDeleteSiteSiteTestEntityByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SiteTestEntity siteTestEntity =
			testDeleteSiteSiteTestEntityByExternalReferenceCode_addSiteTestEntity();

		assertHttpResponseStatusCode(
			204,
			siteTestEntityResource.
				deleteSiteSiteTestEntityByExternalReferenceCodeHttpResponse(
					siteTestEntity.getExternalReferenceCode(),
					siteTestEntity.getSiteId()));

		assertHttpResponseStatusCode(
			404,
			siteTestEntityResource.
				getSiteSiteTestEntityByExternalReferenceCodeHttpResponse(
					siteTestEntity.getExternalReferenceCode(),
					siteTestEntity.getSiteId()));
		assertHttpResponseStatusCode(
			404,
			siteTestEntityResource.
				getSiteSiteTestEntityByExternalReferenceCodeHttpResponse(
					"-", siteTestEntity.getSiteId()));
	}

	protected SiteTestEntity
			testDeleteSiteSiteTestEntityByExternalReferenceCode_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testGetSiteSiteTestEntitiesPage() throws Exception {
		Long siteId = testGetSiteSiteTestEntitiesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteSiteTestEntitiesPage_getIrrelevantSiteId();

		Page<SiteTestEntity> page =
			siteTestEntityResource.getSiteSiteTestEntitiesPage(siteId);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			SiteTestEntity irrelevantSiteTestEntity =
				testGetSiteSiteTestEntitiesPage_addSiteTestEntity(
					irrelevantSiteId, randomIrrelevantSiteTestEntity());

			page = siteTestEntityResource.getSiteSiteTestEntitiesPage(
				irrelevantSiteId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantSiteTestEntity,
				(List<SiteTestEntity>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteTestEntitiesPage_getExpectedActions(
					irrelevantSiteId));
		}

		SiteTestEntity siteTestEntity1 =
			testGetSiteSiteTestEntitiesPage_addSiteTestEntity(
				siteId, randomSiteTestEntity());

		SiteTestEntity siteTestEntity2 =
			testGetSiteSiteTestEntitiesPage_addSiteTestEntity(
				siteId, randomSiteTestEntity());

		page = siteTestEntityResource.getSiteSiteTestEntitiesPage(siteId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(siteTestEntity1, (List<SiteTestEntity>)page.getItems());
		assertContains(siteTestEntity2, (List<SiteTestEntity>)page.getItems());
		assertValid(
			page, testGetSiteSiteTestEntitiesPage_getExpectedActions(siteId));

		for (SiteTestEntity siteTestEntity : page.getItems()) {
			Assert.assertNull(siteTestEntity.getPermissions());
		}

		page = permissionsSiteTestEntityResource.getSiteSiteTestEntitiesPage(
			siteId);

		for (SiteTestEntity siteTestEntity : page.getItems()) {
			Assert.assertNotNull(siteTestEntity.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteTestEntitiesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/test/v1.0/sites/{siteId}/site-test-entities/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected SiteTestEntity testGetSiteSiteTestEntitiesPage_addSiteTestEntity(
			Long siteId, SiteTestEntity siteTestEntity)
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			siteId, siteTestEntity);
	}

	protected Long testGetSiteSiteTestEntitiesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteSiteTestEntitiesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGetSiteSiteTestEntityByExternalReferenceCode()
		throws Exception {

		SiteTestEntity postSiteTestEntity =
			testGetSiteSiteTestEntityByExternalReferenceCode_addSiteTestEntity();

		SiteTestEntity getSiteTestEntity =
			siteTestEntityResource.getSiteSiteTestEntityByExternalReferenceCode(
				postSiteTestEntity.getExternalReferenceCode(),
				postSiteTestEntity.getSiteId());

		assertEquals(postSiteTestEntity, getSiteTestEntity);
		assertValid(getSiteTestEntity);

		Assert.assertNull(getSiteTestEntity.getPermissions());

		getSiteTestEntity =
			permissionsSiteTestEntityResource.
				getSiteSiteTestEntityByExternalReferenceCode(
					postSiteTestEntity.getExternalReferenceCode(),
					postSiteTestEntity.getSiteId());

		Assert.assertNotNull(getSiteTestEntity.getPermissions());
	}

	protected SiteTestEntity
			testGetSiteSiteTestEntityByExternalReferenceCode_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testGetSiteTestEntity() throws Exception {
		SiteTestEntity postSiteTestEntity =
			testGetSiteTestEntity_addSiteTestEntity();

		SiteTestEntity getSiteTestEntity =
			siteTestEntityResource.getSiteTestEntity(
				postSiteTestEntity.getId());

		assertEquals(postSiteTestEntity, getSiteTestEntity);
		assertValid(getSiteTestEntity);

		Assert.assertNull(getSiteTestEntity.getPermissions());

		getSiteTestEntity = permissionsSiteTestEntityResource.getSiteTestEntity(
			postSiteTestEntity.getId());

		Assert.assertNotNull(getSiteTestEntity.getPermissions());
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		SiteTestEntity postSiteTestEntity =
			testGetSiteTestEntity_addSiteTestEntity();

		SiteTestEntity getSiteTestEntity =
			siteTestEntityResource.getSiteTestEntity(
				postSiteTestEntity.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.SiteTestEntity"
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
			postSiteTestEntity.getId());

		assertEquals(
			getSiteTestEntity, SiteTestEntitySerDes.toDTO(item.toString()));
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

	protected SiteTestEntity testGetSiteTestEntity_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testGetSiteTestEntityPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SiteTestEntity postSiteTestEntity =
			testGetSiteTestEntityPermissionsPage_addSiteTestEntity();

		Page<Permission> page =
			siteTestEntityResource.getSiteTestEntityPermissionsPage(
				postSiteTestEntity.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected SiteTestEntity
			testGetSiteTestEntityPermissionsPage_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testPatchSiteTestEntity() throws Exception {
		SiteTestEntity postSiteTestEntity =
			testPatchSiteTestEntity_addSiteTestEntity();

		SiteTestEntity randomPatchSiteTestEntity = randomPatchSiteTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SiteTestEntity patchSiteTestEntity =
			siteTestEntityResource.patchSiteTestEntity(
				postSiteTestEntity.getId(), randomPatchSiteTestEntity);

		SiteTestEntity expectedPatchSiteTestEntity = postSiteTestEntity.clone();

		BeanTestUtil.copyProperties(
			randomPatchSiteTestEntity, expectedPatchSiteTestEntity);

		SiteTestEntity getSiteTestEntity =
			siteTestEntityResource.getSiteTestEntity(
				patchSiteTestEntity.getId());

		assertEquals(expectedPatchSiteTestEntity, getSiteTestEntity);
		assertValid(getSiteTestEntity);
	}

	protected SiteTestEntity testPatchSiteTestEntity_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testPostSiteSiteTestEntity() throws Exception {
		SiteTestEntity randomSiteTestEntity = randomSiteTestEntity();

		SiteTestEntity postSiteTestEntity =
			testPostSiteSiteTestEntity_addSiteTestEntity(randomSiteTestEntity);

		assertEquals(randomSiteTestEntity, postSiteTestEntity);
		assertValid(postSiteTestEntity);

		SiteTestEntity randomPermissionsSiteTestEntity1 =
			randomPermissionsSiteTestEntity();

		SiteTestEntity postPermissionsSiteTestEntity1 =
			testPostSiteSiteTestEntity_addSiteTestEntity(
				randomPermissionsSiteTestEntity1);

		Assert.assertNull(postPermissionsSiteTestEntity1.getPermissions());

		SiteTestEntity randomPermissionsSiteTestEntity2 =
			randomPermissionsSiteTestEntity();

		SiteTestEntity postPermissionsSiteTestEntity2 =
			testPostSiteSiteTestEntity_addPermissionsSiteTestEntity(
				randomPermissionsSiteTestEntity2);

		Assert.assertNotNull(postPermissionsSiteTestEntity2.getPermissions());
	}

	protected SiteTestEntity testPostSiteSiteTestEntity_addSiteTestEntity(
			SiteTestEntity siteTestEntity)
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGetSiteSiteTestEntitiesPage_getSiteId(), siteTestEntity);
	}

	protected SiteTestEntity
			testPostSiteSiteTestEntity_addPermissionsSiteTestEntity(
				SiteTestEntity siteTestEntity)
		throws Exception {

		return permissionsSiteTestEntityResource.postSiteSiteTestEntity(
			testGetSiteSiteTestEntitiesPage_getSiteId(), siteTestEntity);
	}

	@Test
	public void testPutSiteSiteTestEntityByExternalReferenceCode()
		throws Exception {

		SiteTestEntity postSiteTestEntity =
			testPutSiteSiteTestEntityByExternalReferenceCode_addSiteTestEntity();

		SiteTestEntity randomSiteTestEntity = randomSiteTestEntity();

		SiteTestEntity putSiteTestEntity =
			siteTestEntityResource.putSiteSiteTestEntityByExternalReferenceCode(
				postSiteTestEntity.getExternalReferenceCode(),
				postSiteTestEntity.getSiteId(), randomSiteTestEntity);

		assertEquals(randomSiteTestEntity, putSiteTestEntity);
		assertValid(putSiteTestEntity);

		Assert.assertNull(putSiteTestEntity.getPermissions());

		SiteTestEntity getSiteTestEntity =
			siteTestEntityResource.getSiteSiteTestEntityByExternalReferenceCode(
				putSiteTestEntity.getExternalReferenceCode(),
				putSiteTestEntity.getSiteId());

		assertEquals(randomSiteTestEntity, getSiteTestEntity);
		assertValid(getSiteTestEntity);

		SiteTestEntity randomPermissionsSiteTestEntity =
			randomPermissionsSiteTestEntity();

		putSiteTestEntity =
			siteTestEntityResource.putSiteSiteTestEntityByExternalReferenceCode(
				postSiteTestEntity.getExternalReferenceCode(),
				postSiteTestEntity.getSiteId(),
				randomPermissionsSiteTestEntity);

		assertEquals(randomPermissionsSiteTestEntity, putSiteTestEntity);
		assertValid(putSiteTestEntity);

		Assert.assertNull(putSiteTestEntity.getPermissions());

		putSiteTestEntity =
			permissionsSiteTestEntityResource.
				putSiteSiteTestEntityByExternalReferenceCode(
					postSiteTestEntity.getExternalReferenceCode(),
					postSiteTestEntity.getSiteId(),
					randomPermissionsSiteTestEntity);

		Assert.assertNotNull(putSiteTestEntity.getPermissions());

		SiteTestEntity newSiteTestEntity =
			testPutSiteSiteTestEntityByExternalReferenceCode_createSiteTestEntity();

		putSiteTestEntity =
			siteTestEntityResource.putSiteSiteTestEntityByExternalReferenceCode(
				newSiteTestEntity.getExternalReferenceCode(),
				newSiteTestEntity.getSiteId(), newSiteTestEntity);

		assertEquals(newSiteTestEntity, putSiteTestEntity);
		assertValid(putSiteTestEntity);

		getSiteTestEntity =
			siteTestEntityResource.getSiteSiteTestEntityByExternalReferenceCode(
				putSiteTestEntity.getExternalReferenceCode(),
				putSiteTestEntity.getSiteId());

		assertEquals(newSiteTestEntity, getSiteTestEntity);

		Assert.assertEquals(
			newSiteTestEntity.getExternalReferenceCode(),
			putSiteTestEntity.getExternalReferenceCode());
	}

	protected SiteTestEntity
			testPutSiteSiteTestEntityByExternalReferenceCode_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	protected SiteTestEntity
			testPutSiteSiteTestEntityByExternalReferenceCode_createSiteTestEntity()
		throws Exception {

		return randomSiteTestEntity();
	}

	@Test
	public void testPutSiteTestEntity() throws Exception {
		SiteTestEntity postSiteTestEntity =
			testPutSiteTestEntity_addSiteTestEntity();

		SiteTestEntity randomSiteTestEntity = randomSiteTestEntity();

		SiteTestEntity putSiteTestEntity =
			siteTestEntityResource.putSiteTestEntity(
				postSiteTestEntity.getId(), randomSiteTestEntity);

		assertEquals(randomSiteTestEntity, putSiteTestEntity);
		assertValid(putSiteTestEntity);

		Assert.assertNull(putSiteTestEntity.getPermissions());

		SiteTestEntity getSiteTestEntity =
			siteTestEntityResource.getSiteTestEntity(putSiteTestEntity.getId());

		assertEquals(randomSiteTestEntity, getSiteTestEntity);
		assertValid(getSiteTestEntity);

		SiteTestEntity randomPermissionsSiteTestEntity =
			randomPermissionsSiteTestEntity();

		putSiteTestEntity = siteTestEntityResource.putSiteTestEntity(
			postSiteTestEntity.getId(), randomPermissionsSiteTestEntity);

		assertEquals(randomPermissionsSiteTestEntity, putSiteTestEntity);
		assertValid(putSiteTestEntity);

		Assert.assertNull(putSiteTestEntity.getPermissions());

		putSiteTestEntity = permissionsSiteTestEntityResource.putSiteTestEntity(
			postSiteTestEntity.getId(), randomPermissionsSiteTestEntity);

		Assert.assertNotNull(putSiteTestEntity.getPermissions());
	}

	protected SiteTestEntity testPutSiteTestEntity_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testPutSiteTestEntityPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SiteTestEntity siteTestEntity =
			testPutSiteTestEntityPermissionsPage_addSiteTestEntity();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			siteTestEntityResource.putSiteTestEntityPermissionsPageHttpResponse(
				siteTestEntity.getId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"PERMISSIONS"});
							setRoleName(role.getName());
						}
					}
				}));

		assertHttpResponseStatusCode(
			404,
			siteTestEntityResource.putSiteTestEntityPermissionsPageHttpResponse(
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

	protected SiteTestEntity
			testPutSiteTestEntityPermissionsPage_addSiteTestEntity()
		throws Exception {

		return siteTestEntityResource.postSiteSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		SiteTestEntity siteTestEntity, List<SiteTestEntity> siteTestEntities) {

		boolean contains = false;

		for (SiteTestEntity item : siteTestEntities) {
			if (equals(siteTestEntity, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			siteTestEntities + " does not contain " + siteTestEntity, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SiteTestEntity siteTestEntity1, SiteTestEntity siteTestEntity2) {

		Assert.assertTrue(
			siteTestEntity1 + " does not equal " + siteTestEntity2,
			equals(siteTestEntity1, siteTestEntity2));
	}

	protected void assertEquals(
		List<SiteTestEntity> siteTestEntities1,
		List<SiteTestEntity> siteTestEntities2) {

		Assert.assertEquals(siteTestEntities1.size(), siteTestEntities2.size());

		for (int i = 0; i < siteTestEntities1.size(); i++) {
			SiteTestEntity siteTestEntity1 = siteTestEntities1.get(i);
			SiteTestEntity siteTestEntity2 = siteTestEntities2.get(i);

			assertEquals(siteTestEntity1, siteTestEntity2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SiteTestEntity> siteTestEntities1,
		List<SiteTestEntity> siteTestEntities2) {

		Assert.assertEquals(siteTestEntities1.size(), siteTestEntities2.size());

		for (SiteTestEntity siteTestEntity1 : siteTestEntities1) {
			boolean contains = false;

			for (SiteTestEntity siteTestEntity2 : siteTestEntities2) {
				if (equals(siteTestEntity1, siteTestEntity2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				siteTestEntities2 + " does not contain " + siteTestEntity1,
				contains);
		}
	}

	protected void assertValid(SiteTestEntity siteTestEntity) throws Exception {
		boolean valid = true;

		if (siteTestEntity.getDateCreated() == null) {
			valid = false;
		}

		if (siteTestEntity.getDateModified() == null) {
			valid = false;
		}

		if (siteTestEntity.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				siteTestEntity.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (siteTestEntity.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (siteTestEntity.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (siteTestEntity.getPermissions() == null) {
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

	protected void assertValid(Page<SiteTestEntity> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SiteTestEntity> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SiteTestEntity> siteTestEntities = page.getItems();

		int size = siteTestEntities.size();

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
					com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						SiteTestEntity.class)) {

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
		SiteTestEntity siteTestEntity1, SiteTestEntity siteTestEntity2) {

		if (siteTestEntity1 == siteTestEntity2) {
			return true;
		}

		if (!Objects.equals(
				siteTestEntity1.getSiteId(), siteTestEntity2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteTestEntity1.getDateCreated(),
						siteTestEntity2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteTestEntity1.getDateModified(),
						siteTestEntity2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteTestEntity1.getDescription(),
						siteTestEntity2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						siteTestEntity1.getExternalReferenceCode(),
						siteTestEntity2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteTestEntity1.getId(), siteTestEntity2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteTestEntity1.getPermissions(),
						siteTestEntity2.getPermissions())) {

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

		if (!(_siteTestEntityResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_siteTestEntityResource;

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
		SiteTestEntity siteTestEntity) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = siteTestEntity.getDateCreated();

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

				sb.append(_format.format(siteTestEntity.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = siteTestEntity.getDateModified();

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

				sb.append(_format.format(siteTestEntity.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = siteTestEntity.getDescription();

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
			Object object = siteTestEntity.getExternalReferenceCode();

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

	protected SiteTestEntity randomSiteTestEntity() throws Exception {
		return new SiteTestEntity() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected SiteTestEntity randomIrrelevantSiteTestEntity() throws Exception {
		SiteTestEntity randomIrrelevantSiteTestEntity = randomSiteTestEntity();

		randomIrrelevantSiteTestEntity.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantSiteTestEntity;
	}

	protected SiteTestEntity randomPatchSiteTestEntity() throws Exception {
		return randomSiteTestEntity();
	}

	protected SiteTestEntity randomPermissionsSiteTestEntity()
		throws Exception {

		SiteTestEntity siteTestEntity = randomSiteTestEntity();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		siteTestEntity.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return siteTestEntity;
	}

	protected SiteTestEntityResource siteTestEntityResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected SiteTestEntityResource permissionsSiteTestEntityResource;
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
		LogFactoryUtil.getLog(BaseSiteTestEntityResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		SiteTestEntityResource _siteTestEntityResource;

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