/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.digital.sales.room.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.digital.sales.room.client.dto.v1_0.DigitalSalesRoom;
import com.liferay.headless.digital.sales.room.client.http.HttpInvoker;
import com.liferay.headless.digital.sales.room.client.pagination.Page;
import com.liferay.headless.digital.sales.room.client.pagination.Pagination;
import com.liferay.headless.digital.sales.room.client.resource.v1_0.DigitalSalesRoomResource;
import com.liferay.headless.digital.sales.room.client.serdes.v1_0.DigitalSalesRoomSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
 * @author Stefano Motta
 * @generated
 */
@Generated("")
public abstract class BaseDigitalSalesRoomResourceTestCase {

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

		_digitalSalesRoomResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		digitalSalesRoomResource = DigitalSalesRoomResource.builder(
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

		DigitalSalesRoom digitalSalesRoom1 = randomDigitalSalesRoom();

		String json = objectMapper.writeValueAsString(digitalSalesRoom1);

		DigitalSalesRoom digitalSalesRoom2 = DigitalSalesRoomSerDes.toDTO(json);

		Assert.assertTrue(equals(digitalSalesRoom1, digitalSalesRoom2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DigitalSalesRoom digitalSalesRoom = randomDigitalSalesRoom();

		String json1 = objectMapper.writeValueAsString(digitalSalesRoom);
		String json2 = DigitalSalesRoomSerDes.toJSON(digitalSalesRoom);

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

		DigitalSalesRoom digitalSalesRoom = randomDigitalSalesRoom();

		digitalSalesRoom.setAccountName(regex);
		digitalSalesRoom.setChannelName(regex);
		digitalSalesRoom.setClientName(regex);
		digitalSalesRoom.setDescription(regex);
		digitalSalesRoom.setExternalReferenceCode(regex);
		digitalSalesRoom.setFriendlyUrlPath(regex);
		digitalSalesRoom.setName(regex);
		digitalSalesRoom.setOwnerName(regex);
		digitalSalesRoom.setPrimaryColor(regex);
		digitalSalesRoom.setSecondaryColor(regex);

		String json = DigitalSalesRoomSerDes.toJSON(digitalSalesRoom);

		Assert.assertFalse(json.contains(regex));

		digitalSalesRoom = DigitalSalesRoomSerDes.toDTO(json);

		Assert.assertEquals(regex, digitalSalesRoom.getAccountName());
		Assert.assertEquals(regex, digitalSalesRoom.getChannelName());
		Assert.assertEquals(regex, digitalSalesRoom.getClientName());
		Assert.assertEquals(regex, digitalSalesRoom.getDescription());
		Assert.assertEquals(regex, digitalSalesRoom.getExternalReferenceCode());
		Assert.assertEquals(regex, digitalSalesRoom.getFriendlyUrlPath());
		Assert.assertEquals(regex, digitalSalesRoom.getName());
		Assert.assertEquals(regex, digitalSalesRoom.getOwnerName());
		Assert.assertEquals(regex, digitalSalesRoom.getPrimaryColor());
		Assert.assertEquals(regex, digitalSalesRoom.getSecondaryColor());
	}

	@Test
	public void testDeleteDigitalSalesRoom() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DigitalSalesRoom digitalSalesRoom =
			testDeleteDigitalSalesRoom_addDigitalSalesRoom();

		assertHttpResponseStatusCode(
			204,
			digitalSalesRoomResource.deleteDigitalSalesRoomHttpResponse(
				digitalSalesRoom.getId()));

		assertHttpResponseStatusCode(
			404,
			digitalSalesRoomResource.getDigitalSalesRoomHttpResponse(
				digitalSalesRoom.getId()));
		assertHttpResponseStatusCode(
			404, digitalSalesRoomResource.getDigitalSalesRoomHttpResponse(0L));
	}

	protected DigitalSalesRoom testDeleteDigitalSalesRoom_addDigitalSalesRoom()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetDigitalSalesRoom() throws Exception {
		DigitalSalesRoom postDigitalSalesRoom =
			testGetDigitalSalesRoom_addDigitalSalesRoom();

		DigitalSalesRoom getDigitalSalesRoom =
			digitalSalesRoomResource.getDigitalSalesRoom(
				postDigitalSalesRoom.getId());

		assertEquals(postDigitalSalesRoom, getDigitalSalesRoom);
		assertValid(getDigitalSalesRoom);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DigitalSalesRoom postDigitalSalesRoom =
			testGetDigitalSalesRoom_addDigitalSalesRoom();

		DigitalSalesRoom getDigitalSalesRoom =
			digitalSalesRoomResource.getDigitalSalesRoom(
				postDigitalSalesRoom.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.digital.sales.room.dto.v1_0.DigitalSalesRoom"
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
			postDigitalSalesRoom.getId());

		assertEquals(
			getDigitalSalesRoom, DigitalSalesRoomSerDes.toDTO(item.toString()));
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

	protected DigitalSalesRoom testGetDigitalSalesRoom_addDigitalSalesRoom()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage()
		throws Exception {

		Long digitalSalesRoomTemplateId =
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getDigitalSalesRoomTemplateId();
		Long irrelevantDigitalSalesRoomTemplateId =
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getIrrelevantDigitalSalesRoomTemplateId();

		Page<DigitalSalesRoom> page =
			digitalSalesRoomResource.
				getDigitalSalesRoomTemplateDigitalSalesRoomsPage(
					digitalSalesRoomTemplateId);

		long totalCount = page.getTotalCount();

		if (irrelevantDigitalSalesRoomTemplateId != null) {
			DigitalSalesRoom irrelevantDigitalSalesRoom =
				testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_addDigitalSalesRoom(
					irrelevantDigitalSalesRoomTemplateId,
					randomIrrelevantDigitalSalesRoom());

			page =
				digitalSalesRoomResource.
					getDigitalSalesRoomTemplateDigitalSalesRoomsPage(
						irrelevantDigitalSalesRoomTemplateId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDigitalSalesRoom,
				(List<DigitalSalesRoom>)page.getItems());
			assertValid(
				page,
				testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getExpectedActions(
					irrelevantDigitalSalesRoomTemplateId));
		}

		DigitalSalesRoom digitalSalesRoom1 =
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_addDigitalSalesRoom(
				digitalSalesRoomTemplateId, randomDigitalSalesRoom());

		DigitalSalesRoom digitalSalesRoom2 =
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_addDigitalSalesRoom(
				digitalSalesRoomTemplateId, randomDigitalSalesRoom());

		page =
			digitalSalesRoomResource.
				getDigitalSalesRoomTemplateDigitalSalesRoomsPage(
					digitalSalesRoomTemplateId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			digitalSalesRoom1, (List<DigitalSalesRoom>)page.getItems());
		assertContains(
			digitalSalesRoom2, (List<DigitalSalesRoom>)page.getItems());
		assertValid(
			page,
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getExpectedActions(
				digitalSalesRoomTemplateId));

		digitalSalesRoomResource.deleteDigitalSalesRoom(
			digitalSalesRoom1.getId());

		digitalSalesRoomResource.deleteDigitalSalesRoom(
			digitalSalesRoom2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getExpectedActions(
				Long digitalSalesRoomTemplateId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected DigitalSalesRoom
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_addDigitalSalesRoom(
				Long digitalSalesRoomTemplateId,
				DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		return digitalSalesRoomResource.
			postDigitalSalesRoomTemplateDigitalSalesRoom(
				digitalSalesRoomTemplateId, digitalSalesRoom);
	}

	protected Long
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getDigitalSalesRoomTemplateId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getIrrelevantDigitalSalesRoomTemplateId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDigitalSalesRoomsPage() throws Exception {
		Page<DigitalSalesRoom> page =
			digitalSalesRoomResource.getDigitalSalesRoomsPage(
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		DigitalSalesRoom digitalSalesRoom1 =
			testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
				randomDigitalSalesRoom());

		DigitalSalesRoom digitalSalesRoom2 =
			testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
				randomDigitalSalesRoom());

		page = digitalSalesRoomResource.getDigitalSalesRoomsPage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			digitalSalesRoom1, (List<DigitalSalesRoom>)page.getItems());
		assertContains(
			digitalSalesRoom2, (List<DigitalSalesRoom>)page.getItems());
		assertValid(page, testGetDigitalSalesRoomsPage_getExpectedActions());

		digitalSalesRoomResource.deleteDigitalSalesRoom(
			digitalSalesRoom1.getId());

		digitalSalesRoomResource.deleteDigitalSalesRoom(
			digitalSalesRoom2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDigitalSalesRoomsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDigitalSalesRoomsPageWithPagination() throws Exception {
		Page<DigitalSalesRoom> digitalSalesRoomsPage =
			digitalSalesRoomResource.getDigitalSalesRoomsPage(null, null);

		int totalCount = GetterUtil.getInteger(
			digitalSalesRoomsPage.getTotalCount());

		DigitalSalesRoom digitalSalesRoom1 =
			testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
				randomDigitalSalesRoom());

		DigitalSalesRoom digitalSalesRoom2 =
			testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
				randomDigitalSalesRoom());

		DigitalSalesRoom digitalSalesRoom3 =
			testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
				randomDigitalSalesRoom());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DigitalSalesRoom> page1 =
				digitalSalesRoomResource.getDigitalSalesRoomsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				digitalSalesRoom1, (List<DigitalSalesRoom>)page1.getItems());

			Page<DigitalSalesRoom> page2 =
				digitalSalesRoomResource.getDigitalSalesRoomsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				digitalSalesRoom2, (List<DigitalSalesRoom>)page2.getItems());

			Page<DigitalSalesRoom> page3 =
				digitalSalesRoomResource.getDigitalSalesRoomsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				digitalSalesRoom3, (List<DigitalSalesRoom>)page3.getItems());
		}
		else {
			Page<DigitalSalesRoom> page1 =
				digitalSalesRoomResource.getDigitalSalesRoomsPage(
					null, Pagination.of(1, totalCount + 2));

			List<DigitalSalesRoom> digitalSalesRooms1 =
				(List<DigitalSalesRoom>)page1.getItems();

			Assert.assertEquals(
				digitalSalesRooms1.toString(), totalCount + 2,
				digitalSalesRooms1.size());

			Page<DigitalSalesRoom> page2 =
				digitalSalesRoomResource.getDigitalSalesRoomsPage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DigitalSalesRoom> digitalSalesRooms2 =
				(List<DigitalSalesRoom>)page2.getItems();

			Assert.assertEquals(
				digitalSalesRooms2.toString(), 1, digitalSalesRooms2.size());

			Page<DigitalSalesRoom> page3 =
				digitalSalesRoomResource.getDigitalSalesRoomsPage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				digitalSalesRoom1, (List<DigitalSalesRoom>)page3.getItems());
			assertContains(
				digitalSalesRoom2, (List<DigitalSalesRoom>)page3.getItems());
			assertContains(
				digitalSalesRoom3, (List<DigitalSalesRoom>)page3.getItems());
		}
	}

	protected DigitalSalesRoom testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
			DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchDigitalSalesRoom() throws Exception {
		DigitalSalesRoom postDigitalSalesRoom =
			testPatchDigitalSalesRoom_addDigitalSalesRoom();

		DigitalSalesRoom randomPatchDigitalSalesRoom =
			randomPatchDigitalSalesRoom();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DigitalSalesRoom patchDigitalSalesRoom =
			digitalSalesRoomResource.patchDigitalSalesRoom(
				postDigitalSalesRoom.getId(), randomPatchDigitalSalesRoom);

		DigitalSalesRoom expectedPatchDigitalSalesRoom =
			postDigitalSalesRoom.clone();

		BeanTestUtil.copyProperties(
			randomPatchDigitalSalesRoom, expectedPatchDigitalSalesRoom);

		DigitalSalesRoom getDigitalSalesRoom =
			digitalSalesRoomResource.getDigitalSalesRoom(
				patchDigitalSalesRoom.getId());

		assertEquals(expectedPatchDigitalSalesRoom, getDigitalSalesRoom);
		assertValid(getDigitalSalesRoom);
	}

	protected DigitalSalesRoom testPatchDigitalSalesRoom_addDigitalSalesRoom()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDigitalSalesRoom() throws Exception {
		DigitalSalesRoom randomDigitalSalesRoom = randomDigitalSalesRoom();

		DigitalSalesRoom postDigitalSalesRoom =
			testPostDigitalSalesRoom_addDigitalSalesRoom(
				randomDigitalSalesRoom);

		assertEquals(randomDigitalSalesRoom, postDigitalSalesRoom);
		assertValid(postDigitalSalesRoom);
	}

	protected DigitalSalesRoom testPostDigitalSalesRoom_addDigitalSalesRoom(
			DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDigitalSalesRoomTemplateDigitalSalesRoom()
		throws Exception {

		DigitalSalesRoom randomDigitalSalesRoom = randomDigitalSalesRoom();

		DigitalSalesRoom postDigitalSalesRoom =
			testPostDigitalSalesRoomTemplateDigitalSalesRoom_addDigitalSalesRoom(
				randomDigitalSalesRoom);

		assertEquals(randomDigitalSalesRoom, postDigitalSalesRoom);
		assertValid(postDigitalSalesRoom);
	}

	protected DigitalSalesRoom
			testPostDigitalSalesRoomTemplateDigitalSalesRoom_addDigitalSalesRoom(
				DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		return digitalSalesRoomResource.
			postDigitalSalesRoomTemplateDigitalSalesRoom(
				testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage_getDigitalSalesRoomTemplateId(),
				digitalSalesRoom);
	}

	protected void assertContains(
		DigitalSalesRoom digitalSalesRoom,
		List<DigitalSalesRoom> digitalSalesRooms) {

		boolean contains = false;

		for (DigitalSalesRoom item : digitalSalesRooms) {
			if (equals(digitalSalesRoom, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			digitalSalesRooms + " does not contain " + digitalSalesRoom,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DigitalSalesRoom digitalSalesRoom1,
		DigitalSalesRoom digitalSalesRoom2) {

		Assert.assertTrue(
			digitalSalesRoom1 + " does not equal " + digitalSalesRoom2,
			equals(digitalSalesRoom1, digitalSalesRoom2));
	}

	protected void assertEquals(
		List<DigitalSalesRoom> digitalSalesRooms1,
		List<DigitalSalesRoom> digitalSalesRooms2) {

		Assert.assertEquals(
			digitalSalesRooms1.size(), digitalSalesRooms2.size());

		for (int i = 0; i < digitalSalesRooms1.size(); i++) {
			DigitalSalesRoom digitalSalesRoom1 = digitalSalesRooms1.get(i);
			DigitalSalesRoom digitalSalesRoom2 = digitalSalesRooms2.get(i);

			assertEquals(digitalSalesRoom1, digitalSalesRoom2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DigitalSalesRoom> digitalSalesRooms1,
		List<DigitalSalesRoom> digitalSalesRooms2) {

		Assert.assertEquals(
			digitalSalesRooms1.size(), digitalSalesRooms2.size());

		for (DigitalSalesRoom digitalSalesRoom1 : digitalSalesRooms1) {
			boolean contains = false;

			for (DigitalSalesRoom digitalSalesRoom2 : digitalSalesRooms2) {
				if (equals(digitalSalesRoom1, digitalSalesRoom2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				digitalSalesRooms2 + " does not contain " + digitalSalesRoom1,
				contains);
		}
	}

	protected void assertValid(DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		boolean valid = true;

		if (digitalSalesRoom.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (digitalSalesRoom.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountName", additionalAssertFieldName)) {
				if (digitalSalesRoom.getAccountName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (digitalSalesRoom.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("banner", additionalAssertFieldName)) {
				if (digitalSalesRoom.getBanner() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (digitalSalesRoom.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelName", additionalAssertFieldName)) {
				if (digitalSalesRoom.getChannelName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("clientLogo", additionalAssertFieldName)) {
				if (digitalSalesRoom.getClientLogo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("clientName", additionalAssertFieldName)) {
				if (digitalSalesRoom.getClientName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (digitalSalesRoom.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (digitalSalesRoom.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (digitalSalesRoom.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (digitalSalesRoom.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (digitalSalesRoom.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (digitalSalesRoom.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ownerId", additionalAssertFieldName)) {
				if (digitalSalesRoom.getOwnerId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ownerName", additionalAssertFieldName)) {
				if (digitalSalesRoom.getOwnerName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("primaryColor", additionalAssertFieldName)) {
				if (digitalSalesRoom.getPrimaryColor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("secondaryColor", additionalAssertFieldName)) {
				if (digitalSalesRoom.getSecondaryColor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"userAccountBriefs", additionalAssertFieldName)) {

				if (digitalSalesRoom.getUserAccountBriefs() == null) {
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

	protected void assertValid(Page<DigitalSalesRoom> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DigitalSalesRoom> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DigitalSalesRoom> digitalSalesRooms =
			page.getItems();

		int size = digitalSalesRooms.size();

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
					com.liferay.headless.digital.sales.room.dto.v1_0.
						DigitalSalesRoom.class)) {

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
		DigitalSalesRoom digitalSalesRoom1,
		DigitalSalesRoom digitalSalesRoom2) {

		if (digitalSalesRoom1 == digitalSalesRoom2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getAccountId(),
						digitalSalesRoom2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getAccountName(),
						digitalSalesRoom2.getAccountName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)digitalSalesRoom1.getActions(),
						(Map)digitalSalesRoom2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("banner", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getBanner(),
						digitalSalesRoom2.getBanner())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getChannelId(),
						digitalSalesRoom2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getChannelName(),
						digitalSalesRoom2.getChannelName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clientLogo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getClientLogo(),
						digitalSalesRoom2.getClientLogo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clientName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getClientName(),
						digitalSalesRoom2.getClientName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getCreateDate(),
						digitalSalesRoom2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getDescription(),
						digitalSalesRoom2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						digitalSalesRoom1.getExternalReferenceCode(),
						digitalSalesRoom2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getFriendlyUrlPath(),
						digitalSalesRoom2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getId(), digitalSalesRoom2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getModifiedDate(),
						digitalSalesRoom2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getName(),
						digitalSalesRoom2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("ownerId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getOwnerId(),
						digitalSalesRoom2.getOwnerId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("ownerName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getOwnerName(),
						digitalSalesRoom2.getOwnerName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("primaryColor", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getPrimaryColor(),
						digitalSalesRoom2.getPrimaryColor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("secondaryColor", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						digitalSalesRoom1.getSecondaryColor(),
						digitalSalesRoom2.getSecondaryColor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"userAccountBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						digitalSalesRoom1.getUserAccountBriefs(),
						digitalSalesRoom2.getUserAccountBriefs())) {

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

		if (!(_digitalSalesRoomResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_digitalSalesRoomResource;

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
		DigitalSalesRoom digitalSalesRoom) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountName")) {
			Object object = digitalSalesRoom.getAccountName();

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

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("banner")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelName")) {
			Object object = digitalSalesRoom.getChannelName();

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

		if (entityFieldName.equals("clientLogo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("clientName")) {
			Object object = digitalSalesRoom.getClientName();

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

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = digitalSalesRoom.getCreateDate();

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

				sb.append(_format.format(digitalSalesRoom.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = digitalSalesRoom.getDescription();

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
			Object object = digitalSalesRoom.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyUrlPath")) {
			Object object = digitalSalesRoom.getFriendlyUrlPath();

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

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = digitalSalesRoom.getModifiedDate();

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

				sb.append(_format.format(digitalSalesRoom.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = digitalSalesRoom.getName();

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

		if (entityFieldName.equals("ownerId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("ownerName")) {
			Object object = digitalSalesRoom.getOwnerName();

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

		if (entityFieldName.equals("primaryColor")) {
			Object object = digitalSalesRoom.getPrimaryColor();

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

		if (entityFieldName.equals("secondaryColor")) {
			Object object = digitalSalesRoom.getSecondaryColor();

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

		if (entityFieldName.equals("userAccountBriefs")) {
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

	protected DigitalSalesRoom randomDigitalSalesRoom() throws Exception {
		return new DigitalSalesRoom() {
			{
				accountId = RandomTestUtil.randomLong();
				accountName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				channelName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				clientName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				ownerId = RandomTestUtil.randomLong();
				ownerName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				primaryColor = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				secondaryColor = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected DigitalSalesRoom randomIrrelevantDigitalSalesRoom()
		throws Exception {

		DigitalSalesRoom randomIrrelevantDigitalSalesRoom =
			randomDigitalSalesRoom();

		return randomIrrelevantDigitalSalesRoom;
	}

	protected DigitalSalesRoom randomPatchDigitalSalesRoom() throws Exception {
		return randomDigitalSalesRoom();
	}

	protected DigitalSalesRoomResource digitalSalesRoomResource;
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
		LogFactoryUtil.getLog(BaseDigitalSalesRoomResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.digital.sales.room.resource.v1_0.
		DigitalSalesRoomResource _digitalSalesRoomResource;

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