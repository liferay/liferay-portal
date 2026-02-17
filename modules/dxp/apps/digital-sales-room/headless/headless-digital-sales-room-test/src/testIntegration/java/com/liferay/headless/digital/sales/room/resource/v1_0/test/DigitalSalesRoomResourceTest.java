/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.digital.sales.room.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.digital.sales.room.test.util.DigitalSalesRoomTestUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.DigitalSalesRoom;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.DigitalSalesRoomTemplate;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.UserAccountBrief;
import com.liferay.headless.digital.sales.room.client.pagination.Page;
import com.liferay.headless.digital.sales.room.client.pagination.Pagination;
import com.liferay.headless.digital.sales.room.client.resource.v1_0.DigitalSalesRoomResource;
import com.liferay.headless.digital.sales.room.client.resource.v1_0.DigitalSalesRoomTemplateResource;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-66359")
@Ignore
@RunWith(Arquillian.class)
public class DigitalSalesRoomResourceTest
	extends BaseDigitalSalesRoomResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId()));
		_objectDefinition = DigitalSalesRoomTestUtil.getObjectDefinition(
			DigitalSalesRoomResourceTest.class);
	}

	@Override
	@Test
	public void testDeleteDigitalSalesRoom() throws Exception {
		super.testDeleteDigitalSalesRoom();

		_testDeleteDigitalSalesRoomWithPermission();
	}

	@Override
	@Test
	public void testGetDigitalSalesRoom() throws Exception {
		super.testGetDigitalSalesRoom();

		_testGetDigitalSalesRoomWithPermission();
	}

	@Override
	@Test
	public void testGetDigitalSalesRoomsPage() throws Exception {
		super.testGetDigitalSalesRoomsPage();

		_testGetDigitalSalesRoomsPageWithPermission();
	}

	@Ignore
	@Override
	@Test
	public void testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage()
		throws Exception {

		super.testGetDigitalSalesRoomTemplateDigitalSalesRoomsPage();
	}

	@Override
	@Test
	public void testPatchDigitalSalesRoom() throws Exception {
		super.testPatchDigitalSalesRoom();

		_testPatchDigitalSalesRoomWithPermission();
		_testPatchDigitalSalesRoomWithStyleBookEntry();
		_testPatchDigitalSalesRoomWithUserAccount();
	}

	@Override
	@Test
	public void testPostDigitalSalesRoom() throws Exception {
		super.testPostDigitalSalesRoom();

		_testPostDigitalSalesRoomWithPermission();
		_testPostDigitalSalesRoomWithSiteInitializer();
		_testPostDigitalSalesRoomWithUserAccount();
	}

	@Override
	@Test
	public void testPostDigitalSalesRoomTemplateDigitalSalesRoom()
		throws Exception {

		super.testPostDigitalSalesRoomTemplateDigitalSalesRoom();

		_testPostDigitalSalesRoomTemplateDigitalSalesRoom();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"accountId", "channelId", "channelName", "clientName",
			"description", "friendlyUrlPath", "name", "primaryColor",
			"secondaryColor"
		};
	}

	@Override
	protected DigitalSalesRoom randomDigitalSalesRoom() throws Exception {
		return new DigitalSalesRoom() {
			{
				accountId = _accountEntry.getAccountEntryId();
				channelId = RandomTestUtil.nextLong();
				channelName = RandomTestUtil.randomString();
				clientName = RandomTestUtil.randomString();
				description = RandomTestUtil.randomString();
				externalReferenceCode = RandomTestUtil.randomString();
				friendlyUrlPath = StringUtil.toLowerCase(
					StringPool.SLASH + RandomTestUtil.randomString());
				name = RandomTestUtil.randomString();
				primaryColor = RandomTestUtil.randomString();
				secondaryColor = RandomTestUtil.randomString();
			}
		};
	}

	@Override
	protected DigitalSalesRoom testDeleteDigitalSalesRoom_addDigitalSalesRoom()
		throws Exception {

		return digitalSalesRoomResource.postDigitalSalesRoom(
			randomDigitalSalesRoom());
	}

	@Override
	protected DigitalSalesRoom testGetDigitalSalesRoom_addDigitalSalesRoom()
		throws Exception {

		return digitalSalesRoomResource.postDigitalSalesRoom(
			randomDigitalSalesRoom());
	}

	@Override
	protected DigitalSalesRoom testGetDigitalSalesRoomsPage_addDigitalSalesRoom(
			DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		return digitalSalesRoomResource.postDigitalSalesRoom(digitalSalesRoom);
	}

	@Override
	protected DigitalSalesRoom testPatchDigitalSalesRoom_addDigitalSalesRoom()
		throws Exception {

		return digitalSalesRoomResource.postDigitalSalesRoom(
			randomDigitalSalesRoom());
	}

	@Override
	protected DigitalSalesRoom testPostDigitalSalesRoom_addDigitalSalesRoom(
			DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		return digitalSalesRoomResource.postDigitalSalesRoom(digitalSalesRoom);
	}

	@Override
	protected DigitalSalesRoom
			testPostDigitalSalesRoomTemplateDigitalSalesRoom_addDigitalSalesRoom(
				DigitalSalesRoom digitalSalesRoom)
		throws Exception {

		return digitalSalesRoomResource.postDigitalSalesRoom(digitalSalesRoom);
	}

	private void _addUserRole(String[] actionIds, long userId)
		throws Exception {

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, new ServiceContext());

		for (String actionId : actionIds) {
			String name = _objectDefinition.getClassName();

			if (Objects.equals(actionId, ObjectActionKeys.ADD_OBJECT_ENTRY)) {
				name = _objectDefinition.getResourceName();
			}

			_resourcePermissionLocalService.addResourcePermission(
				TestPropsValues.getCompanyId(), name,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role.getRoleId(), actionId);
		}

		_roleLocalService.addUserRole(userId, role.getRoleId());
	}

	private void _assertEqualsStyleBookEntry(
			DigitalSalesRoom digitalSalesRoom, long groupId)
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				groupId, "dsr-classic");

		JSONObject jsonObject1 = _jsonFactory.createJSONObject(
			styleBookEntry.getFrontendTokensValues());

		JSONObject jsonObject2 = jsonObject1.getJSONObject("brandColor1");

		Assert.assertEquals("primaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getPrimaryColor(), jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("brandColor2");

		Assert.assertEquals("secondaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getSecondaryColor(),
			jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("btnPrimaryBackgroundColor");

		Assert.assertEquals("primaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getPrimaryColor(), jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("btnPrimaryBorderColor");

		Assert.assertEquals("primaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getPrimaryColor(), jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject(
			"btnPrimaryHoverBackgroundColor");

		Assert.assertEquals("primaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getPrimaryColor(), jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("btnSecondaryBackgroundColor");

		Assert.assertEquals("secondaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getSecondaryColor(),
			jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("btnSecondaryBorderColor");

		Assert.assertEquals("secondaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getSecondaryColor(),
			jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject(
			"btnSecondaryHoverBackgroundColor");

		Assert.assertEquals("secondaryColor", jsonObject2.getString("name"));
		Assert.assertEquals(
			digitalSalesRoom.getSecondaryColor(),
			jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("primaryColor");

		Assert.assertEquals(
			digitalSalesRoom.getPrimaryColor(), jsonObject2.getString("value"));

		jsonObject2 = jsonObject1.getJSONObject("secondaryColor");

		Assert.assertEquals(
			digitalSalesRoom.getSecondaryColor(),
			jsonObject2.getString("value"));
	}

	private void _assertLayouts(long groupId, String[] names) throws Exception {
		List<Layout> layouts = _layoutLocalService.getLayouts(groupId, false);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				TransformUtil.transformToArray(
					layouts,
					layout -> layout.getName(LocaleUtil.getSiteDefault()),
					String.class),
				names));

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), "Guest");

		for (Layout layout : layouts) {
			boolean hasResourcePermission =
				_resourcePermissionLocalService.hasResourcePermission(
					layout.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()), role.getRoleId(),
					ActionKeys.VIEW);

			if (Objects.equals(
					layout.getName(LocaleUtil.getSiteDefault()), "Documents") ||
				Objects.equals(
					layout.getName(LocaleUtil.getSiteDefault()),
					"Onboarding")) {

				Assert.assertFalse(hasResourcePermission);
			}
			else {
				Assert.assertTrue(hasResourcePermission);
			}
		}
	}

	private DigitalSalesRoom _randomDigitalSalesRoom(
			Consumer<DigitalSalesRoom> digitalSalesRoomConsumer)
		throws Exception {

		DigitalSalesRoom randomDigitalSalesRoom = randomDigitalSalesRoom();

		digitalSalesRoomConsumer.accept(randomDigitalSalesRoom);

		return randomDigitalSalesRoom;
	}

	private void _testDeleteDigitalSalesRoomWithPermission() throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), "test",
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> digitalSalesRoom.setUserAccountBriefs(
				new UserAccountBrief[] {
					new UserAccountBrief() {
						{
							setEmailAddress(user.getEmailAddress());
						}
					}
				}));

		DigitalSalesRoom digitalSalesRoom =
			digitalSalesRoomResource.postDigitalSalesRoom(
				randomDigitalSalesRoom);

		DigitalSalesRoomResource digitalSalesRoomResource =
			DigitalSalesRoomResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).build();

		assertHttpResponseStatusCode(
			403,
			digitalSalesRoomResource.deleteDigitalSalesRoomHttpResponse(
				digitalSalesRoom.getId()));

		_addUserRole(new String[] {ActionKeys.DELETE}, user.getUserId());

		assertHttpResponseStatusCode(
			204,
			digitalSalesRoomResource.deleteDigitalSalesRoomHttpResponse(
				digitalSalesRoom.getId()));
	}

	private void _testGetDigitalSalesRoomsPageWithPermission()
		throws Exception {

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), "test",
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		DigitalSalesRoomResource digitalSalesRoomResource =
			DigitalSalesRoomResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).build();

		Page<DigitalSalesRoom> digitalSalesRoomsPage =
			digitalSalesRoomResource.getDigitalSalesRoomsPage(
				null, Pagination.of(1, 10));

		Map<String, Map<String, String>> actions =
			digitalSalesRoomsPage.getActions();

		Assert.assertTrue(actions.isEmpty());

		_addUserRole(
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY}, user.getUserId());

		digitalSalesRoomsPage =
			digitalSalesRoomResource.getDigitalSalesRoomsPage(
				null, Pagination.of(1, 10));

		actions = digitalSalesRoomsPage.getActions();

		Assert.assertNotNull(actions.get("create"));
	}

	private void _testGetDigitalSalesRoomWithPermission() throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), "test",
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> digitalSalesRoom.setUserAccountBriefs(
				new UserAccountBrief[] {
					new UserAccountBrief() {
						{
							setEmailAddress(user.getEmailAddress());
						}
					}
				}));

		DigitalSalesRoom digitalSalesRoom =
			digitalSalesRoomResource.postDigitalSalesRoom(
				randomDigitalSalesRoom);

		DigitalSalesRoomResource digitalSalesRoomResource =
			DigitalSalesRoomResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).build();

		digitalSalesRoom = digitalSalesRoomResource.getDigitalSalesRoom(
			digitalSalesRoom.getId());

		Map<String, Map<String, String>> actions =
			digitalSalesRoom.getActions();

		Assert.assertTrue(actions.isEmpty());

		_addUserRole(new String[] {ActionKeys.DELETE}, user.getUserId());

		digitalSalesRoom = digitalSalesRoomResource.getDigitalSalesRoom(
			digitalSalesRoom.getId());

		actions = digitalSalesRoom.getActions();

		Assert.assertNotNull(actions.get("delete"));
		Assert.assertNull(actions.get("update"));

		_addUserRole(new String[] {ActionKeys.UPDATE}, user.getUserId());

		digitalSalesRoom = digitalSalesRoomResource.getDigitalSalesRoom(
			digitalSalesRoom.getId());

		actions = digitalSalesRoom.getActions();

		Assert.assertNotNull(actions.get("delete"));
		Assert.assertNotNull(actions.get("update"));
	}

	private void _testPatchDigitalSalesRoomWithPermission() throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), "test",
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> {
				digitalSalesRoom.setAccountId(0L);
				digitalSalesRoom.setUserAccountBriefs(
					new UserAccountBrief[] {
						new UserAccountBrief() {
							{
								setEmailAddress(user.getEmailAddress());
							}
						}
					});
			});

		DigitalSalesRoom digitalSalesRoom =
			digitalSalesRoomResource.postDigitalSalesRoom(
				randomDigitalSalesRoom);

		DigitalSalesRoomResource digitalSalesRoomResource =
			DigitalSalesRoomResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).build();

		assertHttpResponseStatusCode(
			403,
			digitalSalesRoomResource.patchDigitalSalesRoomHttpResponse(
				digitalSalesRoom.getId(), digitalSalesRoom));

		_addUserRole(
			new String[] {
				ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW
			},
			user.getUserId());

		assertHttpResponseStatusCode(
			200,
			digitalSalesRoomResource.patchDigitalSalesRoomHttpResponse(
				digitalSalesRoom.getId(), digitalSalesRoom));
	}

	private void _testPatchDigitalSalesRoomWithStyleBookEntry()
		throws Exception {

		DigitalSalesRoom postDigitalSalesRoom =
			testPatchDigitalSalesRoom_addDigitalSalesRoom();

		DigitalSalesRoom randomPatchDigitalSalesRoom =
			randomPatchDigitalSalesRoom();

		digitalSalesRoomResource.patchDigitalSalesRoom(
			postDigitalSalesRoom.getId(), randomPatchDigitalSalesRoom);

		_assertEqualsStyleBookEntry(
			randomPatchDigitalSalesRoom, postDigitalSalesRoom.getId());
	}

	private void _testPatchDigitalSalesRoomWithUserAccount() throws Exception {
		User user1 = UserTestUtil.addUser();
		User user2 = UserTestUtil.addUser();

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> digitalSalesRoom.setUserAccountBriefs(
				new UserAccountBrief[] {
					new UserAccountBrief() {
						{
							setEmailAddress(user1.getEmailAddress());
						}
					},
					new UserAccountBrief() {
						{
							setEmailAddress(user2.getEmailAddress());
						}
					}
				}));

		DigitalSalesRoom postDigitalSalesRoom =
			testPostDigitalSalesRoom_addDigitalSalesRoom(
				randomDigitalSalesRoom);

		UserAccountBrief[] userAccountBriefs =
			postDigitalSalesRoom.getUserAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(userAccountBriefs), 3, userAccountBriefs.length);

		long[] userAccountBriefIds = TransformUtil.transformToLongArray(
			userAccountBriefs, UserAccountBrief::getId);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				userAccountBriefIds,
				new long[] {
					TestPropsValues.getUserId(), user1.getUserId(),
					user2.getUserId()
				}));

		User user3 = UserTestUtil.addUser();

		randomDigitalSalesRoom.setUserAccountBriefs(
			new UserAccountBrief[] {
				new UserAccountBrief() {
					{
						setEmailAddress(user3.getEmailAddress());
					}
				}
			});

		DigitalSalesRoom patchDigitalSalesRoom =
			digitalSalesRoomResource.patchDigitalSalesRoom(
				postDigitalSalesRoom.getId(), randomDigitalSalesRoom);

		userAccountBriefs = patchDigitalSalesRoom.getUserAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(userAccountBriefs), 4, userAccountBriefs.length);

		userAccountBriefIds = TransformUtil.transformToLongArray(
			userAccountBriefs, UserAccountBrief::getId);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				userAccountBriefIds,
				new long[] {
					TestPropsValues.getUserId(), user1.getUserId(),
					user2.getUserId(), user3.getUserId()
				}));

		randomDigitalSalesRoom.setUserAccountBriefs((UserAccountBrief[])null);

		patchDigitalSalesRoom = digitalSalesRoomResource.patchDigitalSalesRoom(
			postDigitalSalesRoom.getId(), randomDigitalSalesRoom);

		userAccountBriefs = patchDigitalSalesRoom.getUserAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(userAccountBriefs), 4, userAccountBriefs.length);

		userAccountBriefIds = TransformUtil.transformToLongArray(
			userAccountBriefs, UserAccountBrief::getId);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				userAccountBriefIds,
				new long[] {
					TestPropsValues.getUserId(), user1.getUserId(),
					user2.getUserId(), user3.getUserId()
				}));
	}

	private void _testPostDigitalSalesRoomTemplateDigitalSalesRoom()
		throws Exception {

		User user1 = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		DigitalSalesRoomTemplateResource digitalSalesRoomTemplateResource =
			DigitalSalesRoomTemplateResource.builder(
			).authentication(
				user1.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).build();

		DigitalSalesRoomTemplate digitalSalesRoomTemplate =
			digitalSalesRoomTemplateResource.postDigitalSalesRoomTemplate(
				new DigitalSalesRoomTemplate() {
					{
						description = RandomTestUtil.randomString();
						name = RandomTestUtil.randomString();
						primaryColor = RandomTestUtil.randomString();
						secondaryColor = RandomTestUtil.randomString();
					}
				});

		Layout layout1 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), digitalSalesRoomTemplate.getId(),
			false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			"T" + RandomTestUtil.randomString(), StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_NODE, false,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));
		Layout layout2 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), digitalSalesRoomTemplate.getId(),
			false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			"Z" + RandomTestUtil.randomString(), StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_NODE, false,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		User user2 = UserTestUtil.addUser();
		User user3 = UserTestUtil.addUser();

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> digitalSalesRoom.setUserAccountBriefs(
				new UserAccountBrief[] {
					new UserAccountBrief() {
						{
							setEmailAddress(user2.getEmailAddress());
						}
					},
					new UserAccountBrief() {
						{
							setEmailAddress(user3.getEmailAddress());
						}
					}
				}));

		DigitalSalesRoom digitalSalesRoom =
			digitalSalesRoomResource.
				postDigitalSalesRoomTemplateDigitalSalesRoom(
					digitalSalesRoomTemplate.getId(), randomDigitalSalesRoom);

		assertEquals(randomDigitalSalesRoom, digitalSalesRoom);
		assertValid(digitalSalesRoom);

		UserAccountBrief[] userAccountBriefs =
			digitalSalesRoom.getUserAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(userAccountBriefs), 3, userAccountBriefs.length);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				digitalSalesRoom.getId(), "digital-sales-room");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				TransformUtil.transformToArray(
					_fragmentEntryLocalService.getFragmentEntries(
						digitalSalesRoom.getId(),
						fragmentCollection.getFragmentCollectionId(), 0),
					FragmentEntryModel::getName, String.class),
				new String[] {
					"Gallery Block", "Header Main", "Header User",
					"Our Team Block", "PDF Preview Block",
					"Question and Answer Block", "Text Block", "Timeline Block",
					"Video Block", "Welcome Block"
				}));

		_assertLayouts(
			digitalSalesRoomTemplate.getId(),
			new String[] {
				"Documents", "Login", "Onboarding",
				layout1.getName(LocaleUtil.getSiteDefault()),
				layout2.getName(LocaleUtil.getSiteDefault())
			});
	}

	private void _testPostDigitalSalesRoomWithPermission() throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), "test",
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> {
				digitalSalesRoom.setAccountId(0L);
				digitalSalesRoom.setUserAccountBriefs(
					new UserAccountBrief[] {
						new UserAccountBrief() {
							{
								setEmailAddress(user.getEmailAddress());
							}
						}
					});
			});

		DigitalSalesRoomResource digitalSalesRoomResource =
			DigitalSalesRoomResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).build();

		assertHttpResponseStatusCode(
			403,
			digitalSalesRoomResource.postDigitalSalesRoomHttpResponse(
				randomDigitalSalesRoom));

		_addUserRole(
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY}, user.getUserId());

		assertHttpResponseStatusCode(
			200,
			digitalSalesRoomResource.postDigitalSalesRoomHttpResponse(
				randomDigitalSalesRoom));
	}

	private void _testPostDigitalSalesRoomWithSiteInitializer()
		throws Exception {

		DigitalSalesRoom randomDigitalSalesRoom = randomDigitalSalesRoom();

		DigitalSalesRoom postDigitalSalesRoom =
			testPostDigitalSalesRoom_addDigitalSalesRoom(
				randomDigitalSalesRoom);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				postDigitalSalesRoom.getId(), "dsr");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				TransformUtil.transformToArray(
					_fragmentEntryLocalService.getFragmentEntries(
						postDigitalSalesRoom.getId(),
						fragmentCollection.getFragmentCollectionId(), 0),
					FragmentEntryModel::getName, String.class),
				new String[] {
					"Gallery Block", "Header Main", "Header User",
					"Our Team Block", "PDF Preview Block",
					"Question and Answer Block", "Text Block", "Timeline Block",
					"Video Block", "Welcome Block"
				}));

		_assertEqualsStyleBookEntry(
			randomDigitalSalesRoom, postDigitalSalesRoom.getId());
		_assertLayouts(
			postDigitalSalesRoom.getId(),
			new String[] {"Documents", "Login", "Onboarding"});
	}

	private void _testPostDigitalSalesRoomWithUserAccount() throws Exception {
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);
		User user1 = UserTestUtil.addUser();
		User user2 = UserTestUtil.addUser();

		DigitalSalesRoom randomDigitalSalesRoom = _randomDigitalSalesRoom(
			digitalSalesRoom -> digitalSalesRoom.setUserAccountBriefs(
				new UserAccountBrief[] {
					new UserAccountBrief() {
						{
							setEmailAddress(user1.getEmailAddress());
						}
					},
					new UserAccountBrief() {
						{
							setEmailAddress(user2.getEmailAddress());
							setRoleKey(role.getName());
						}
					}
				}));

		DigitalSalesRoom postDigitalSalesRoom =
			testPostDigitalSalesRoom_addDigitalSalesRoom(
				randomDigitalSalesRoom);

		UserAccountBrief[] userAccountBriefs =
			postDigitalSalesRoom.getUserAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(userAccountBriefs), 3, userAccountBriefs.length);

		for (UserAccountBrief userAccountBrief :
				postDigitalSalesRoom.getUserAccountBriefs()) {

			if (userAccountBrief.getId() == TestPropsValues.getUserId()) {
				Assert.assertEquals(
					userAccountBrief,
					_toUserAccountBrief(
						_roleLocalService.fetchRole(
							TestPropsValues.getCompanyId(),
							RoleConstants.SITE_OWNER),
						TestPropsValues.getUser()));
			}
			else if (userAccountBrief.getId() == user1.getUserId()) {
				Assert.assertEquals(
					userAccountBrief, _toUserAccountBrief(null, user1));
			}
			else if (userAccountBrief.getId() == user2.getUserId()) {
				Assert.assertEquals(
					userAccountBrief, _toUserAccountBrief(role, user2));
			}
			else {
				Assert.assertTrue(false);
			}
		}
	}

	private UserAccountBrief _toUserAccountBrief(Role role, User user) {
		return new UserAccountBrief() {
			{
				setAlternateName(user::getScreenName);
				setEmailAddress(user::getEmailAddress);
				setExternalReferenceCode(user::getExternalReferenceCode);
				setId(user::getUserId);
				setName(user::getFullName);
				setRoleKey(
					() -> {
						if (role == null) {
							return null;
						}

						return role.getName();
					});
			}
		};
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}