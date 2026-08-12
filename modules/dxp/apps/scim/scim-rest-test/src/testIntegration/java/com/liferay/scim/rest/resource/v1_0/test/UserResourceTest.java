/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.resource.v1_0.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.scim.rest.client.dto.v1_0.Address;
import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
import com.liferay.scim.rest.client.dto.v1_0.Name;
import com.liferay.scim.rest.client.dto.v1_0.Operation;
import com.liferay.scim.rest.client.dto.v1_0.PatchOp;
import com.liferay.scim.rest.client.dto.v1_0.User;
import com.liferay.scim.rest.client.dto.v1_0.UserSchemaExtension;
import com.liferay.scim.rest.client.http.HttpInvoker;
import com.liferay.scim.rest.resource.v1_0.test.util.ScimTestUtil;
import com.liferay.scim.rest.util.ScimClientUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Olivér Kecskeméty
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserResourceTest extends BaseUserResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseUserResourceTestCase.setUpClass();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.scim.rest.internal.configuration." +
				"ScimClientOAuth2ApplicationConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"matcherField", "email"
			).put(
				"oAuth2ApplicationName", "scim-client-test"
			).put(
				"userId", TestPropsValues.getUserId()
			).build());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_pid);
	}

	@Override
	@Test
	public void testDeleteV2User() throws Exception {
		User user = testDeleteV2User_addUser();

		assertHttpResponseStatusCode(
			204, userResource.deleteV2UserHttpResponse(user.getId()));

		assertHttpResponseStatusCode(
			404, userResource.getV2UserByIdHttpResponse(user.getId()));

		com.liferay.portal.kernel.model.User portalUser =
			_userLocalService.getUserByExternalReferenceCode(
				user.getExternalId(), TestPropsValues.getCompanyId());

		Assert.assertFalse(portalUser.isActive());

		_userLocalService.deleteUser(portalUser);

		assertHttpResponseStatusCode(
			404, userResource.getV2UserByIdHttpResponse(user.getId()));

		// Delete an existing user with no SCIM client ID

		portalUser = UserTestUtil.addUser();

		assertHttpResponseStatusCode(
			404,
			userResource.deleteV2UserHttpResponse(
				String.valueOf(portalUser.getUserId())));

		// Delete an existing user provided by another SCIM client

		ScimTestUtil.saveSCIMClientId(
			com.liferay.portal.kernel.model.User.class.getName(),
			portalUser.getUserId(), portalUser.getCompanyId());

		assertHttpResponseStatusCode(
			409,
			userResource.deleteV2UserHttpResponse(
				String.valueOf(portalUser.getUserId())));

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, userResource.deleteV2UserHttpResponse("12345"));
	}

	@Override
	@Test
	public void testGetV2UserById() throws Exception {
		assertHttpResponseStatusCode(
			404, userResource.getV2UserByIdHttpResponse("12345"));

		User user = testDeleteV2User_addUser();

		HttpInvoker.HttpResponse httpResponse =
			userResource.getV2UserByIdHttpResponse(user.getId());

		assertHttpResponseStatusCode(200, httpResponse);
		assertValid(User.toDTO(httpResponse.getContent()));

		com.liferay.portal.kernel.model.User portalUser =
			_userLocalService.getUser(GetterUtil.getLong(user.getId()));

		_emailAddressLocalService.deleteEmailAddresses(
			portalUser.getCompanyId(), Contact.class.getName(),
			portalUser.getContactId());

		user = _getUser(user.getId());

		MultiValuedAttribute[] emails = user.getEmails();

		Assert.assertEquals(portalUser.getEmailAddress(), emails[0].getValue());

		_userLocalService.updateStatus(
			GetterUtil.getLong(user.getId()), WorkflowConstants.STATUS_INACTIVE,
			new ServiceContext());

		user = _getUser(user.getId());

		Assert.assertFalse(user.getActive());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, userResource.getV2UserByIdHttpResponse(user.getId()));
	}

	@Override
	@Test
	public void testGetV2Users() throws Exception {
		UserTestUtil.addUser();

		_assertListResponse(userResource.getV2Users(5, 0, null), 0, 0);

		User user1 = testDeleteV2User_addUser();
		User user2 = testDeleteV2User_addUser();

		_assertListResponse(
			userResource.getV2Users(5, 0, null), 2, 2, user1, user2);

		User user3 = testDeleteV2User_addUser();

		_assertListResponse(
			userResource.getV2Users(5, 3, null), 3, 1, user1, user2, user3);

		long userId = GetterUtil.getLong(user3.getId());

		_userLocalService.updateStatus(
			userId, WorkflowConstants.STATUS_INACTIVE, new ServiceContext());

		_assertListResponse(
			userResource.getV2Users(5, 0, null), 3, 3, user1, user2, user3);

		ScimTestUtil.saveSCIMClientId(
			com.liferay.portal.kernel.model.User.class.getName(), userId,
			TestPropsValues.getCompanyId(),
			ScimClientUtil.generateScimClientId(
				"scim-client-test" + RandomTestUtil.randomString()));

		_assertListResponse(userResource.getV2Users(-1, 1, null), 2, 0);
		_assertListResponse(userResource.getV2Users(1, 2, null), 2, 1, user2);
		_assertListResponse(
			userResource.getV2Users(1, null, null), 2, 1, user1);
		_assertListResponse(
			userResource.getV2Users(5, 0, null), 2, 2, user1, user2);
		_assertListResponse(
			userResource.getV2Users(10000, null, null), 2, 2, user1, user2);
		_assertListResponse(
			userResource.getV2Users(null, 2, null), 2, 1, user2);
		_assertListResponse(userResource.getV2Users(null, 10000, null), 2, 0);
		_assertListResponse(
			userResource.getV2Users(
				5, 0,
				"externalId eq \"" + RandomTestUtil.randomString() + "\""),
			0, 0);
		_assertListResponse(
			userResource.getV2Users(5, 0, "externalId eq \"" + _PREFIX + "\""),
			0, 0);
		_assertListResponse(
			userResource.getV2Users(
				5, 0, "externalId eq \"" + user1.getExternalId() + "\""),
			1, 1, user1);
		_assertListResponse(
			userResource.getV2Users(
				5, 0, "userName eq \"" + RandomTestUtil.randomString() + "\""),
			0, 0);
		_assertListResponse(
			userResource.getV2Users(5, 0, "userName eq \"" + _PREFIX + "\""), 0,
			0);
		_assertListResponse(
			userResource.getV2Users(
				5, 0, "userName eq \"" + user1.getUserName() + "\""),
			1, 1, user1);

		assertHttpResponseStatusCode(
			400,
			userResource.getV2UsersHttpResponse(
				5, 0,
				RandomTestUtil.randomString() + "eq \"" +
					RandomTestUtil.randomString() + "\""));

		assertHttpResponseStatusCode(
			204,
			userResource.deleteV2UserHttpResponse(
				String.valueOf(user2.getId())));

		_assertListResponse(userResource.getV2Users(5, 0, null), 1, 1, user1);

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, userResource.getV2UsersHttpResponse(5, 0, null));
	}

	@Override
	@Test
	@TestInfo("LPD-48895")
	public void testPatchV2User() throws Exception {
		_testPatchV2User(
			"active", "false", user -> Assert.assertFalse(user.getActive()));

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		_testPatchV2User(
			"emails[type eq \"work\" and primary eq \"true\"].value",
			emailAddress,
			user -> {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					String.valueOf(user.getEmails()[0]));

				Assert.assertEquals(
					emailAddress, jsonObject.getString("value"));
			});

		String title = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_testPatchV2User(
			"title", title,
			user -> Assert.assertEquals(title, user.getTitle()));

		String userName = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_testPatchV2User(
			"userName", userName,
			user -> Assert.assertEquals(userName, user.getUserName()));
	}

	@Override
	@Test
	public void testPostV2User() throws Exception {
		User postUser1 = randomUser();

		userResource.postV2User(postUser1);

		com.liferay.portal.kernel.model.User portalUser1 =
			_userLocalService.getUserByExternalReferenceCode(
				postUser1.getExternalId(), TestPropsValues.getCompanyId());

		assertEquals(
			postUser1, _getUser(String.valueOf(portalUser1.getUserId())));

		// Provision an existing inactive user with SCIM client ID set

		_userLocalService.updateStatus(
			portalUser1, WorkflowConstants.STATUS_INACTIVE,
			new ServiceContext());

		Assert.assertFalse(portalUser1.isActive());

		postUser1.setActive(true);

		userResource.postV2User(postUser1);

		com.liferay.portal.kernel.model.User updatedPortalUser1 =
			_userLocalService.getUserByExternalReferenceCode(
				postUser1.getExternalId(), TestPropsValues.getCompanyId());

		Assert.assertTrue(updatedPortalUser1.isActive());

		// Provision an existing inactive user with no SCIM client ID set

		com.liferay.portal.kernel.model.User portalUser2 =
			UserTestUtil.addUser();

		_userLocalService.updateStatus(
			portalUser2, WorkflowConstants.STATUS_INACTIVE,
			new ServiceContext());

		Assert.assertFalse(portalUser2.isActive());

		User postUser2 = _createUser(portalUser2);

		HttpInvoker.HttpResponse httpResponse =
			userResource.postV2UserHttpResponse(postUser2);

		portalUser2 = _userLocalService.getUserByExternalReferenceCode(
			postUser2.getExternalId(), TestPropsValues.getCompanyId());

		Assert.assertTrue(portalUser2.isActive());

		postUser2 = User.toDTO(httpResponse.getContent());

		Assert.assertEquals(
			portalUser2.getUserId(), GetterUtil.getInteger(postUser2.getId()));

		com.liferay.portal.kernel.model.User updatedPortalUser2 =
			_userLocalService.getUser(portalUser2.getUserId());

		Assert.assertEquals(
			postUser2.getExternalId(),
			updatedPortalUser2.getExternalReferenceCode());

		// Provision an existing user provided by another SCIM client

		com.liferay.portal.kernel.model.User portalUser3 =
			UserTestUtil.addUser();

		ScimTestUtil.saveSCIMClientId(
			com.liferay.portal.kernel.model.User.class.getName(),
			portalUser3.getUserId(), portalUser3.getCompanyId());

		User postUser3 = _createUser(portalUser3);

		assertHttpResponseStatusCode(
			409, userResource.postV2UserHttpResponse(postUser3));

		User postUser4 = randomUser();

		String expectedFormattedAddress = RandomTestUtil.randomString();
		String expectedStreetAddress = RandomTestUtil.randomString();

		postUser4.setAddresses(
			new Address[] {
				new Address() {
					{
						country = RandomTestUtil.randomString();
						locality = RandomTestUtil.randomString();
						primary = true;
						streetAddress = expectedStreetAddress;
						type = RandomTestUtil.randomString();
					}
				},
				new Address() {
					{
						formatted = expectedFormattedAddress;
						locality = RandomTestUtil.randomString();
					}
				},
				new Address() {
					{
						country = RandomTestUtil.randomString();
						type = RandomTestUtil.randomString();
					}
				}
			});

		postUser4.setActive((Boolean)null);

		assertHttpResponseStatusCode(
			201, userResource.postV2UserHttpResponse(postUser4));

		com.liferay.portal.kernel.model.User portalUser4 =
			_userLocalService.getUserByExternalReferenceCode(
				postUser4.getExternalId(), TestPropsValues.getCompanyId());

		Assert.assertTrue(portalUser4.isActive());

		List<com.liferay.portal.kernel.model.Address> addresses =
			_addressLocalService.getAddresses(
				portalUser4.getCompanyId(), Contact.class.getName(),
				portalUser4.getContactId());

		Assert.assertEquals(addresses.toString(), 2, addresses.size());

		ListType listType = _listTypeLocalService.fetchListType(
			portalUser4.getCompanyId(), "other",
			Contact.class.getName() + ".address");

		List<String> streets = new ArrayList<>();

		for (com.liferay.portal.kernel.model.Address address : addresses) {
			Assert.assertEquals(0, address.getCountryId());
			Assert.assertEquals(
				listType.getListTypeId(), address.getListTypeId());

			streets.add(address.getStreet1());
		}

		Assert.assertTrue(streets.contains(expectedFormattedAddress));
		Assert.assertTrue(streets.contains(expectedStreetAddress));

		assertHttpResponseStatusCode(
			204,
			userResource.deleteV2UserHttpResponse(
				String.valueOf(portalUser4.getUserId())));

		assertHttpResponseStatusCode(
			404,
			userResource.getV2UserByIdHttpResponse(
				String.valueOf(portalUser4.getUserId())));

		postUser4.setActive(true);

		assertHttpResponseStatusCode(
			201, userResource.postV2UserHttpResponse(postUser4));

		assertHttpResponseStatusCode(
			200,
			userResource.getV2UserByIdHttpResponse(
				String.valueOf(portalUser4.getUserId())));

		portalUser4 = _userLocalService.getUserByExternalReferenceCode(
			postUser4.getExternalId(), TestPropsValues.getCompanyId());

		Assert.assertTrue(portalUser4.isActive());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, userResource.postV2UserHttpResponse(randomUser()));
	}

	@Ignore
	@Override
	@Test
	public void testPostV2UserSearch() throws Exception {
	}

	@Override
	@Test
	public void testPutV2User() throws Exception {
		assertHttpResponseStatusCode(
			404, userResource.putV2UserHttpResponse("12345", randomUser()));

		com.liferay.portal.kernel.model.User portalUser =
			UserTestUtil.addUser();

		User user1 = _createUser(portalUser);

		assertHttpResponseStatusCode(
			404, userResource.putV2UserHttpResponse(user1.getId(), user1));

		User user2 = testDeleteV2User_addUser();

		String newTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		user2.setTitle(newTitle);

		HttpInvoker.HttpResponse httpResponse =
			userResource.putV2UserHttpResponse(user2.getId(), user2);

		assertEquals(user2, User.toDTO(httpResponse.getContent()));

		ScimTestUtil.saveSCIMClientId(
			com.liferay.portal.kernel.model.User.class.getName(),
			GetterUtil.getLong(user2.getId()), TestPropsValues.getCompanyId(),
			ScimClientUtil.generateScimClientId(
				"scim-client-test" + RandomTestUtil.randomString()));

		assertHttpResponseStatusCode(
			409, userResource.putV2UserHttpResponse(user2.getId(), user2));

		User user3 = testDeleteV2User_addUser();

		assertHttpResponseStatusCode(
			204, userResource.deleteV2UserHttpResponse(user3.getId()));

		assertHttpResponseStatusCode(
			404, userResource.putV2UserHttpResponse(user3.getId(), user2));

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, userResource.putV2UserHttpResponse(user2.getId(), user2));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"emails", "externalId", "name", "title",
			"urn_ietf_params_scim_schemas_extension_liferay_2_0_User",
			"userName"
		};
	}

	@Override
	protected ObjectMapper getClientSerDesObjectMapper() {
		ObjectMapper objectMapper = super.getClientSerDesObjectMapper();

		objectMapper.setPropertyNamingStrategy(
			new PropertyNamingStrategy() {

				@Override
				public String nameForField(
					MapperConfig<?> config, AnnotatedField field,
					String defaultName) {

					if (!StringUtil.startsWith(defaultName, "urn")) {
						return super.nameForField(config, field, defaultName);
					}

					return "urn:ietf:params:scim:schemas:extension:liferay:" +
						"2.0:User";
				}

			});

		return objectMapper;
	}

	@Override
	protected User randomUser() throws Exception {
		User user = super.randomUser();

		user.setActive(true);
		user.setEmails(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						primary = true;
						type = "work";
						value = user.getUserName() + "@liferay.com";
					}
				}
			});
		user.setExternalId(_PREFIX + user.getExternalId());
		user.setId((String)null);
		user.setName(
			new Name() {
				{
					familyName = RandomTestUtil.randomString();
					givenName = RandomTestUtil.randomString();
					middleName = RandomTestUtil.randomString();
				}
			});
		user.setSchemas(
			new String[] {
				"urn:ietf:params:scim:schemas:core:2.0:User",
				"urn:ietf:params:scim:schemas:extension:liferay:2.0:User"
			});
		user.setUrn_ietf_params_scim_schemas_extension_liferay_2_0_User(
			new UserSchemaExtension() {
				{
					birthday = DateUtils.truncate(new Date(), Calendar.DATE);
					male = true;
				}
			});
		user.setUserName(_PREFIX + user.getUserName());

		return user;
	}

	@Override
	protected User testDeleteV2User_addUser() throws Exception {
		User user = randomUser();

		HttpInvoker.HttpResponse httpResponse =
			userResource.postV2UserHttpResponse(user);

		Assert.assertEquals(2, httpResponse.getStatusCode() / 100);

		JSONObject userJSONObject = _jsonFactory.createJSONObject(
			httpResponse.getContent());

		user.setId(userJSONObject.getString("id"));

		return user;
	}

	private void _assertListResponse(
			Object response, long expectedTotalResults,
			long expectedItemsPerPage, User... expectedUsers)
		throws Exception {

		JSONObject listResponseJSONObject = _jsonFactory.createJSONObject(
			response.toString());

		JSONArray schemasJSONArray = listResponseJSONObject.getJSONArray(
			"schemas");

		Assert.assertEquals(
			"urn:ietf:params:scim:api:messages:2.0:ListResponse",
			schemasJSONArray.get(0));

		Assert.assertEquals(
			expectedTotalResults,
			listResponseJSONObject.getLong("totalResults"));
		Assert.assertEquals(
			expectedItemsPerPage,
			listResponseJSONObject.getLong("itemsPerPage"));

		if (ArrayUtil.isEmpty(expectedUsers)) {
			Assert.assertFalse(listResponseJSONObject.has("Resources"));

			return;
		}

		JSONArray resourcesJSONArray = listResponseJSONObject.getJSONArray(
			"Resources");

		Assert.assertEquals(expectedItemsPerPage, resourcesJSONArray.length());

		for (int i = 0; i < resourcesJSONArray.length(); i++) {
			JSONObject userJSONObject = resourcesJSONArray.getJSONObject(i);

			assertContains(
				User.toDTO(userJSONObject.toString()),
				Arrays.asList(expectedUsers));
		}
	}

	private User _createUser(com.liferay.portal.kernel.model.User portalUser)
		throws Exception {

		User user = randomUser();

		user.setEmails(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						primary = true;
						type = "default";
						value = portalUser.getEmailAddress();
					}
				}
			});

		return user;
	}

	private User _getUser(String userId) throws Exception {
		Object userObject = userResource.getV2UserById(userId);

		return User.toDTO(userObject.toString());
	}

	private void _testPatchV2User(
			String fieldPath, String fieldValue,
			UnsafeConsumer<User, Exception> unsafeConsumer)
		throws Exception {

		User user = testDeleteV2User_addUser();

		com.liferay.portal.kernel.model.User portalUser =
			_userLocalService.getUserByExternalReferenceCode(
				user.getExternalId(), TestPropsValues.getCompanyId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {portalUser.getUserId()});

		portalUser = _userLocalService.updatePortrait(
			portalUser.getUserId(),
			FileUtil.getBytes(
				UserResourceTest.class, "dependencies/liferay.png"));

		long portraitId = portalUser.getPortraitId();

		PatchOp patchOp = new PatchOp();

		patchOp.setOperations(
			new Operation[] {
				new Operation() {
					{
						setOp("replace");
						setPath(fieldPath);
						setValue(fieldValue);
					}
				}
			});

		patchOp.setSchemas(
			new String[] {"\"urn:ietf:params:scim:api:messages:2.0:PatchOp\""});

		HttpInvoker.HttpResponse httpResponse =
			userResource.patchV2UserHttpResponse(user.getId(), patchOp);

		assertHttpResponseStatusCode(200, httpResponse);

		HttpInvoker.HttpResponse httpResponse1 =
			userResource.getV2UserByIdHttpResponse(user.getId());

		User patchUser = User.toDTO(httpResponse1.getContent());

		assertValid(patchUser);
		unsafeConsumer.accept(patchUser);

		portalUser = _userLocalService.getUser(portalUser.getUserId());

		Assert.assertEquals(portraitId, portalUser.getPortraitId());
		Assert.assertTrue(
			ArrayUtil.contains(portalUser.getRoleIds(), role.getRoleId()));
	}

	private static final String _PREFIX = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private EmailAddressLocalService _emailAddressLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	private String _pid;

	@Inject
	private UserLocalService _userLocalService;

}