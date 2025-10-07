/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.scim.rest.client.dto.v1_0.Group;
import com.liferay.scim.rest.client.dto.v1_0.Meta;
import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
import com.liferay.scim.rest.client.dto.v1_0.Name;
import com.liferay.scim.rest.client.dto.v1_0.Operation;
import com.liferay.scim.rest.client.dto.v1_0.PatchOp;
import com.liferay.scim.rest.client.dto.v1_0.User;
import com.liferay.scim.rest.client.http.HttpInvoker;
import com.liferay.scim.rest.client.resource.v1_0.UserResource;
import com.liferay.scim.rest.resource.v1_0.test.util.ScimTestUtil;

import java.util.Arrays;

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
public class GroupResourceTest extends BaseGroupResourceTestCase {

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

		UserResource.Builder builder = UserResource.builder();

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		com.liferay.portal.kernel.model.User user = _userLocalService.getUser(
			TestPropsValues.getUserId());

		_userResource = builder.authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			company.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_pid);
	}

	@Override
	@Test
	public void testDeleteV2Group() throws Exception {
		Group group = testDeleteV2Group_addGroup();

		assertHttpResponseStatusCode(
			204, groupResource.deleteV2GroupHttpResponse(group.getId()));

		assertHttpResponseStatusCode(
			404, groupResource.getV2GroupByIdHttpResponse(group.getId(), null));

		Assert.assertNull(
			_userGroupLocalService.fetchUserGroupByExternalReferenceCode(
				group.getExternalId(), TestPropsValues.getCompanyId()));

		// Delete an existing group with no SCIM client ID

		UserGroup userGroup = _userGroupLocalService.addUserGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(), null,
			new ServiceContext());

		assertHttpResponseStatusCode(
			404,
			groupResource.deleteV2GroupHttpResponse(
				String.valueOf(userGroup.getUserGroupId())));

		// Delete an existing group provided by another SCIM client

		ScimTestUtil.saveSCIMClientId(
			UserGroup.class.getName(), userGroup.getUserGroupId(),
			userGroup.getCompanyId());

		assertHttpResponseStatusCode(
			409,
			groupResource.deleteV2GroupHttpResponse(
				String.valueOf(userGroup.getUserGroupId())));

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, groupResource.deleteV2GroupHttpResponse("12345"));
	}

	@Override
	@Test
	public void testGetV2GroupById() throws Exception {
		assertHttpResponseStatusCode(
			404, groupResource.getV2GroupByIdHttpResponse("12345", null));

		Group group1 = testDeleteV2Group_addGroup();

		HttpInvoker.HttpResponse httpResponse =
			groupResource.getV2GroupByIdHttpResponse(group1.getId(), null);

		assertHttpResponseStatusCode(200, httpResponse);
		assertValid(Group.toDTO(httpResponse.getContent()));

		Group group2 = _addGroupWithMember();

		httpResponse = groupResource.getV2GroupByIdHttpResponse(
			group2.getId(), null);

		assertHttpResponseStatusCode(200, httpResponse);

		Group getGroup = Group.toDTO(httpResponse.getContent());

		assertValid(getGroup);
		Assert.assertEquals(1, ArrayUtil.getLength(getGroup.getMembers()));

		httpResponse = groupResource.getV2GroupByIdHttpResponse(
			group2.getId(), "members");

		assertHttpResponseStatusCode(200, httpResponse);

		getGroup = Group.toDTO(httpResponse.getContent());

		assertValid(getGroup);
		Assert.assertNull(getGroup.getMembers());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404,
			groupResource.getV2GroupByIdHttpResponse(group1.getId(), null));
	}

	@Override
	@Test
	public void testGetV2Groups() throws Exception {
		_userGroupLocalService.addUserGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(), null,
			new ServiceContext());

		_assertListResponse(groupResource.getV2Groups(5, null, 0, null), 0, 0);

		Group group1 = testDeleteV2Group_addGroup();
		Group group2 = testDeleteV2Group_addGroup();

		_assertListResponse(
			groupResource.getV2Groups(5, null, 0, null), 2, 2, group1, group2);

		_assertListResponse(groupResource.getV2Groups(-1, null, 1, null), 3, 0);
		_assertListResponse(
			groupResource.getV2Groups(1, null, 2, null), 3, 1, group2);
		_assertListResponse(
			groupResource.getV2Groups(1, null, null, null), 3, 1, group1);

		Group group3 = testDeleteV2Group_addGroup();

		_assertListResponse(
			groupResource.getV2Groups(5, null, 3, null), 3, 1, group3);

		_assertListResponse(
			groupResource.getV2Groups(10000, null, null, null), 3, 3, group1,
			group2, group3);

		_assertListResponse(
			groupResource.getV2Groups(null, null, 10000, null), 3, 0);
		_assertListResponse(
			groupResource.getV2Groups(null, null, 2, null), 3, 2, group2,
			group3);

		_assertListResponse(
			groupResource.getV2Groups(
				5, null, 0,
				"displayName eq \"" + RandomTestUtil.randomString() + "\""),
			0, 0);
		_assertListResponse(
			groupResource.getV2Groups(
				5, null, 0, "displayName eq \"" + _PREFIX + "\""),
			0, 0);
		_assertListResponse(
			groupResource.getV2Groups(
				5, null, 0,
				"displayName eq \"" + group1.getDisplayName() + "\""),
			1, 1, group1);

		assertHttpResponseStatusCode(
			400,
			groupResource.getV2GroupsHttpResponse(
				5, null, 0,
				RandomTestUtil.randomString() + " eq +\"" +
					RandomTestUtil.randomString() + "\""));

		Group group4 = _addGroupWithMember();

		Group getGroup = _getGroupByListResponse(
			groupResource.getV2Groups(
				5, null, 0,
				"displayName eq \"" + group4.getDisplayName() + "\""));

		assertValid(getGroup);
		Assert.assertEquals(1, ArrayUtil.getLength(getGroup.getMembers()));

		getGroup = _getGroupByListResponse(
			groupResource.getV2Groups(
				5, "members", 0,
				"displayName eq \"" + group4.getDisplayName() + "\""
			).toString());

		assertValid(getGroup);
		Assert.assertNull(getGroup.getMembers());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, groupResource.getV2GroupsHttpResponse(5, null, 0, null));
	}

	@Override
	@Test
	public void testPatchV2Group() throws Exception {
		Group postGroup = randomGroup();

		User user1 = _addUser();

		postGroup.setMembers(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						Meta meta = user1.getMeta();

						$ref = meta.getLocation();

						value = user1.getId();
					}
				}
			});

		groupResource.postV2Group(postGroup);

		UserGroup userGroup =
			_userGroupLocalService.getUserGroupByExternalReferenceCode(
				postGroup.getExternalId(), TestPropsValues.getCompanyId());

		PatchOp patchOp = new PatchOp();

		String displayName1 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		patchOp.setOperations(
			new Operation[] {
				new Operation() {
					{
						setOp("replace");
						setPath("displayName");
						setValue(displayName1);
					}
				}
			});

		patchOp.setSchemas(
			new String[] {"\"urn:ietf:params:scim:api:messages:2.0:PatchOp\""});

		Group patchGroup = _patchGroup(patchOp, userGroup.getUserGroupId());

		Assert.assertEquals(displayName1, patchGroup.getDisplayName());

		String displayName2 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		patchOp.setOperations(
			new Operation[] {
				new Operation() {
					{
						setOp("replace");
						setValue(
							JSONFactoryUtil.createJSONObject(
								LinkedHashMapBuilder.put(
									"id",
									String.valueOf(userGroup.getUserGroupId())
								).put(
									"displayName", displayName2
								).build()));
					}
				}
			});

		patchGroup = _patchGroup(patchOp, userGroup.getUserGroupId());

		Assert.assertEquals(displayName2, patchGroup.getDisplayName());

		User user2 = _addUser();

		patchOp.setOperations(
			new Operation[] {
				new Operation() {
					{
						setOp("Add");
						setPath("members");
						setValue(
							JSONFactoryUtil.createJSONArray(
							).put(
								JSONUtil.put("value", user2.getId())
							));
					}
				}
			});

		patchGroup = _patchGroup(patchOp, userGroup.getUserGroupId());

		Assert.assertEquals(2, ArrayUtil.getLength(patchGroup.getMembers()));

		User user3 = _addUser();

		patchOp.setOperations(
			new Operation[] {
				new Operation() {
					{
						setOp("Remove");
						setPath("members");
						setValue(
							JSONFactoryUtil.createJSONArray(
							).put(
								JSONUtil.put("value", user1.getId())
							));
					}
				},
				new Operation() {
					{
						setOp("Add");
						setPath("members");
						setValue(
							JSONFactoryUtil.createJSONArray(
							).put(
								JSONUtil.put("value", user3.getId())
							));
					}
				}
			});

		patchGroup = _patchGroup(patchOp, userGroup.getUserGroupId());

		Assert.assertEquals(2, ArrayUtil.getLength(patchGroup.getMembers()));

		patchOp.setOperations(
			new Operation[] {
				new Operation() {
					{
						setOp("Remove");
						setPath("members");
						setValue(JSONUtil.put("value", user2.getId()));
					}
				},
				new Operation() {
					{
						setOp("Remove");
						setPath("members[value eq \"" + user3.getId() + "\"]");
					}
				}
			});

		patchGroup = _patchGroup(patchOp, userGroup.getUserGroupId());

		Assert.assertNull(patchGroup.getMembers());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404,
			groupResource.patchV2GroupHttpResponse(
				randomGroup().getId(), patchOp));
	}

	@Override
	@Test
	public void testPostV2Group() throws Exception {
		Group postGroup1 = randomGroup();

		User user = _addUser();

		postGroup1.setMembers(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						Meta meta = user.getMeta();

						$ref = meta.getLocation();

						value = user.getId();
					}
				}
			});

		groupResource.postV2Group(postGroup1);

		UserGroup userGroup1 =
			_userGroupLocalService.getUserGroupByExternalReferenceCode(
				postGroup1.getExternalId(), TestPropsValues.getCompanyId());

		assertEquals(postGroup1, _getGroup(userGroup1.getUserGroupId()));

		// Provision an existing group with no SCIM client ID set

		Group postGroup2 = randomGroup();

		UserGroup userGroup2 = _userGroupLocalService.addUserGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), postGroup2.getDisplayName(), null,
			new ServiceContext());

		postGroup2.setExternalId(userGroup2.getExternalReferenceCode());

		postGroup2.setMembers(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						Meta meta = user.getMeta();

						$ref = meta.getLocation();

						value = user.getId();
					}
				}
			});

		HttpInvoker.HttpResponse httpResponse =
			groupResource.postV2GroupHttpResponse(postGroup2);

		postGroup2 = Group.toDTO(httpResponse.getContent());

		Assert.assertEquals(
			userGroup2.getUserGroupId(),
			GetterUtil.getInteger(postGroup2.getId()));

		Assert.assertEquals(
			postGroup2.getExternalId(), userGroup2.getExternalReferenceCode());

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, groupResource.postV2GroupHttpResponse(randomGroup()));
	}

	@Ignore
	@Override
	@Test
	public void testPostV2GroupSearch() throws Exception {
	}

	@Override
	@Test
	public void testPutV2Group() throws Exception {
		Group group = testDeleteV2Group_addGroup();

		User user1 = _addUser();
		User user2 = _addUser();

		group.setMembers(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						Meta meta = user1.getMeta();

						$ref = meta.getLocation();

						value = user1.getId();
					}
				},
				new MultiValuedAttribute() {
					{
						Meta meta = user2.getMeta();

						$ref = meta.getLocation();

						value = user2.getId();
					}
				}
			});

		HttpInvoker.HttpResponse httpResponse =
			groupResource.putV2GroupHttpResponse(group.getId(), group);

		assertEquals(group, Group.toDTO(httpResponse.getContent()));

		group.setMembers(new MultiValuedAttribute[0]);

		httpResponse = groupResource.putV2GroupHttpResponse(
			group.getId(), group);

		assertEquals(group, Group.toDTO(httpResponse.getContent()));

		ConfigurationTestUtil.deleteConfiguration(_pid);

		assertHttpResponseStatusCode(
			404, groupResource.putV2GroupHttpResponse("12345", randomGroup()));
	}

	@Override
	protected void assertEquals(Group group1, Group group2) {
		super.assertEquals(group1, group2);

		Assert.assertEquals(
			ArrayUtil.getLength(group1.getMembers()),
			ArrayUtil.getLength(group2.getMembers()));

		if (ArrayUtil.isEmpty(group1.getMembers())) {
			return;
		}

		String[] group1MemberValues = TransformUtil.transform(
			group1.getMembers(), member -> member.getValue(), String.class);

		for (MultiValuedAttribute memberMultiValuedAttribute2 :
				group2.getMembers()) {

			Assert.assertTrue(
				ArrayUtil.contains(
					group1MemberValues, memberMultiValuedAttribute2.getValue(),
					false));
		}
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"displayName", "externalId"};
	}

	@Override
	protected Group randomGroup() throws Exception {
		Group group = super.randomGroup();

		group.setDisplayName(_PREFIX + group.getDisplayName());

		return group;
	}

	@Override
	protected Group testDeleteV2Group_addGroup() throws Exception {
		Group group = randomGroup();

		HttpInvoker.HttpResponse httpResponse =
			groupResource.postV2GroupHttpResponse(group);

		Assert.assertEquals(2, httpResponse.getStatusCode() / 100);

		JSONObject groupJSONObject = _jsonFactory.createJSONObject(
			httpResponse.getContent());

		group.setId(groupJSONObject.getString("id"));

		return group;
	}

	private Group _addGroupWithMember() throws Exception {
		Group group = randomGroup();

		User user = _addUser();

		group.setMembers(
			new MultiValuedAttribute[] {
				new MultiValuedAttribute() {
					{
						Meta meta = user.getMeta();

						$ref = meta.getLocation();

						value = user.getId();
					}
				}
			});

		HttpInvoker.HttpResponse httpResponse =
			groupResource.postV2GroupHttpResponse(group);

		Assert.assertEquals(2, httpResponse.getStatusCode() / 100);

		JSONObject groupJSONObject = _jsonFactory.createJSONObject(
			httpResponse.getContent());

		group.setId(groupJSONObject.getString("id"));

		return group;
	}

	private User _addUser() throws Exception {
		String emailPrefix = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		StringBundler profileUrlSB = new StringBundler(3);

		profileUrlSB.append("http://");
		profileUrlSB.append(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		profileUrlSB.append(".com");

		HttpInvoker.HttpResponse httpResponse =
			_userResource.postV2UserHttpResponse(
				new User() {
					{
						active = true;
						displayName = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						emails = new MultiValuedAttribute[] {
							new MultiValuedAttribute() {
								{
									primary = true;
									type = "default";
									value = emailPrefix + "@liferay.com";
								}
							}
						};
						externalId = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						locale = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						name = new Name() {
							{
								familyName = RandomTestUtil.randomString();
								givenName = RandomTestUtil.randomString();
								middleName = RandomTestUtil.randomString();
							}
						};
						password = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						preferredLanguage = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						profileUrl = profileUrlSB.toString();
						schemas = new String[] {
							"urn:ietf:params:scim:schemas:core:2.0:User"
						};
						timezone = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						title = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						userName = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						userType = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				});

		return User.toDTO(httpResponse.getContent());
	}

	private void _assertListResponse(
			Object response, long expectedTotalResults,
			long expectedItemsPerPage, Group... expectedGroups)
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

		if (ArrayUtil.isEmpty(expectedGroups)) {
			Assert.assertFalse(listResponseJSONObject.has("Resources"));

			return;
		}

		JSONArray resourcesJSONArray = listResponseJSONObject.getJSONArray(
			"Resources");

		Assert.assertEquals(expectedGroups.length, resourcesJSONArray.length());

		for (int i = 0; i < resourcesJSONArray.length(); i++) {
			JSONObject groupJSONObject = resourcesJSONArray.getJSONObject(i);

			assertContains(
				Group.toDTO(groupJSONObject.toString()),
				Arrays.asList(expectedGroups));
		}
	}

	private Group _getGroup(long userGroupId) throws Exception {
		Object groupObject = groupResource.getV2GroupById(
			String.valueOf(userGroupId), null);

		return Group.toDTO(groupObject.toString());
	}

	private Group _getGroupByListResponse(Object response) throws Exception {
		JSONObject responseJSONObject = _jsonFactory.createJSONObject(
			response.toString());

		JSONArray resourcesJSONArray = responseJSONObject.getJSONArray(
			"Resources");

		return Group.toDTO(
			resourcesJSONArray.getJSONObject(
				0
			).toString());
	}

	private Group _patchGroup(PatchOp patchOp, long userGroupId)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			groupResource.patchV2GroupHttpResponse(
				String.valueOf(userGroupId), patchOp);

		assertHttpResponseStatusCode(204, httpResponse);

		return _getGroup(userGroupId);
	}

	private static final String _PREFIX = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	private String _pid;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	private UserResource _userResource;

}