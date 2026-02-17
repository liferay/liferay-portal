/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.digital.sales.room.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.sales.room.test.util.DigitalSalesRoomTestUtil;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.DigitalSalesRoom;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.InvitedMemberBrief;
import com.liferay.headless.digital.sales.room.client.dto.v1_0.UserAccountBrief;
import com.liferay.headless.digital.sales.room.client.resource.v1_0.DigitalSalesRoomResource;
import com.liferay.headless.digital.sales.room.client.resource.v1_0.UserAccountBriefResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-66359")
@Ignore
@RunWith(Arquillian.class)
public class InvitedMemberBriefResourceTest
	extends BaseInvitedMemberBriefResourceTestCase {

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

		_userAccountBriefResource = UserAccountBriefResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).build();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"emailAddress"};
	}

	@Override
	protected InvitedMemberBrief randomInvitedMemberBrief() throws Exception {
		return new InvitedMemberBrief() {
			{
				emailAddress = RandomTestUtil.randomString() + "@liferay.com";
			}
		};
	}

	@Override
	protected InvitedMemberBrief
			testDeleteDigitalSalesRoomInvitedMemberBrief_addInvitedMemberBrief()
		throws Exception {

		return testGetDigitalSalesRoomInvitedMemberBriefsPage_addInvitedMemberBrief(
			_digitalSalesRoom.getId(), randomInvitedMemberBrief());
	}

	@Override
	protected Long
			testDeleteDigitalSalesRoomInvitedMemberBrief_getDigitalSalesRoomId()
		throws Exception {

		return _digitalSalesRoom.getId();
	}

	@Override
	protected InvitedMemberBrief
			testGetDigitalSalesRoomInvitedMemberBriefsPage_addInvitedMemberBrief(
				Long digitalSalesRoomId, InvitedMemberBrief invitedMemberBrief)
		throws Exception {

		UserAccountBrief userAccountBrief =
			_userAccountBriefResource.postDigitalSalesRoomUserAccountBrief(
				_digitalSalesRoom.getId(),
				new UserAccountBrief() {
					{
						setEmailAddress(invitedMemberBrief::getEmailAddress);
					}
				});

		invitedMemberBrief.setId(userAccountBrief.getId());

		return invitedMemberBrief;
	}

	@Override
	protected Long
			testGetDigitalSalesRoomInvitedMemberBriefsPage_getDigitalSalesRoomId()
		throws Exception {

		return _digitalSalesRoom.getId();
	}

	private DigitalSalesRoom _digitalSalesRoom;
	private UserAccountBriefResource _userAccountBriefResource;

}