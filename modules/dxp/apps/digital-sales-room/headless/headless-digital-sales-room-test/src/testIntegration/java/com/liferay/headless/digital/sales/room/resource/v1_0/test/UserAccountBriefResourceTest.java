/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.digital.sales.room.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.sales.room.constants.DigitalSalesRoomTicketConstants;
import com.liferay.digital.sales.room.test.util.DigitalSalesRoomTestUtil;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.DigitalSalesRoom;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.UserAccountBrief;
import com.liferay.headless.digital.sales.room.client.resource.v1_0.DigitalSalesRoomResource;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class UserAccountBriefResourceTest
	extends BaseUserAccountBriefResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ObjectDefinition objectDefinition =
			DigitalSalesRoomTestUtil.getObjectDefinition(
				DigitalSalesRoomResourceTest.class);

		User user = UserTestUtil.getAdminUser(objectDefinition.getCompanyId());

		DigitalSalesRoomResource digitalSalesRoomResource =
			DigitalSalesRoomResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).build();

		_digitalSalesRoom = digitalSalesRoomResource.postDigitalSalesRoom(
			new DigitalSalesRoom() {
				{
					accountId = 0L;
					channelId = 0L;
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
			});
	}

	@Override
	@Test
	public void testPatchDigitalSalesRoomUserAccountBrief() throws Exception {
		UserAccountBrief postUserAccountBrief =
			testPostDigitalSalesRoomUserAccountBrief_addUserAccountBrief(
				randomUserAccountBrief());

		UserAccountBrief patchUserAccountBrief =
			userAccountBriefResource.patchDigitalSalesRoomUserAccountBrief(
				_digitalSalesRoom.getId(), postUserAccountBrief.getId(),
				new UserAccountBrief() {
					{
						roleKey = "Site Administrator";
					}
				});

		Assert.assertEquals(
			postUserAccountBrief.getId(), patchUserAccountBrief.getId());
		Assert.assertEquals(
			"Site Administrator", patchUserAccountBrief.getRoleKey());
	}

	@Override
	@Test
	public void testPostDigitalSalesRoomUserAccountBrief() throws Exception {
		super.testPostDigitalSalesRoomUserAccountBrief();

		_testPostDigitalSalesRoomUserAccountBrief();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"emailAddress"};
	}

	@Override
	protected UserAccountBrief randomUserAccountBrief() throws Exception {
		User user = UserTestUtil.addUser();

		return new UserAccountBrief() {
			{
				emailAddress = user.getEmailAddress();
			}
		};
	}

	@Override
	protected Long
			testDeleteDigitalSalesRoomUserAccountBrief_getDigitalSalesRoomId()
		throws Exception {

		return _digitalSalesRoom.getId();
	}

	@Override
	protected Long
			testGetDigitalSalesRoomUserAccountBriefsPage_getDigitalSalesRoomId()
		throws Exception {

		return _digitalSalesRoom.getId();
	}

	private void _testPostDigitalSalesRoomUserAccountBrief() throws Exception {
		UserAccountBrief randomUserAccountBrief1 = randomUserAccountBrief();

		UserAccountBrief postUserAccountBrief =
			testPostDigitalSalesRoomUserAccountBrief_addUserAccountBrief(
				randomUserAccountBrief1);

		assertEquals(randomUserAccountBrief1, postUserAccountBrief);
		assertValid(postUserAccountBrief);

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			_digitalSalesRoom.getId(),
			DigitalSalesRoomTicketConstants.TYPE_INVITE_MEMBER);

		Assert.assertEquals(tickets.toString(), 2, tickets.size());

		Assert.assertTrue(
			ListUtil.exists(
				tickets,
				ticket -> {
					try {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							ticket.getExtraInfo());

						return Objects.equals(
							randomUserAccountBrief1.getEmailAddress(),
							jsonObject.get("emailAddress"));
					}
					catch (Exception exception) {
						return false;
					}
				}));

		List<NotificationQueueEntry> notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertTrue(
			ListUtil.exists(
				notificationQueueEntries,
				notificationQueueEntry -> {
					Map<String, Object> notificationRecipientSettingsMap =
						NotificationRecipientSettingUtil.
							getNotificationRecipientSettingsMap(
								notificationQueueEntry);

					return Objects.equals(
						randomUserAccountBrief1.getEmailAddress(),
						String.valueOf(
							notificationRecipientSettingsMap.get(
								NotificationRecipientSettingConstants.
									NAME_TO)));
				}));

		UserAccountBrief randomUserAccountBrief2 = randomUserAccountBrief();

		randomUserAccountBrief2.setEmailAddress(
			RandomTestUtil.randomString() + "@liferay.com");

		postUserAccountBrief =
			testPostDigitalSalesRoomUserAccountBrief_addUserAccountBrief(
				randomUserAccountBrief2);

		assertEquals(randomUserAccountBrief2, postUserAccountBrief);
		assertValid(postUserAccountBrief);

		tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			_digitalSalesRoom.getId(),
			DigitalSalesRoomTicketConstants.TYPE_INVITE_MEMBER);

		Assert.assertEquals(tickets.toString(), 3, tickets.size());
		Assert.assertTrue(
			ListUtil.exists(
				tickets,
				ticket -> {
					try {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							ticket.getExtraInfo());

						return Objects.equals(
							randomUserAccountBrief2.getEmailAddress(),
							jsonObject.get("emailAddress"));
					}
					catch (Exception exception) {
						return false;
					}
				}));

		notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertTrue(
			ListUtil.exists(
				notificationQueueEntries,
				notificationQueueEntry -> {
					Map<String, Object> notificationRecipientSettingsMap =
						NotificationRecipientSettingUtil.
							getNotificationRecipientSettingsMap(
								notificationQueueEntry);

					return Objects.equals(
						randomUserAccountBrief2.getEmailAddress(),
						String.valueOf(
							notificationRecipientSettingsMap.get(
								NotificationRecipientSettingConstants.
									NAME_TO)));
				}));
	}

	private DigitalSalesRoom _digitalSalesRoom;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

}