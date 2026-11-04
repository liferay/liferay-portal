/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.notification.test.util.CommerceNotificationTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceNotificationQueueEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testDeleteCommerceNotificationQueueEntries() throws Exception {
		CommerceNotificationTemplate commerceNotificationTemplate =
			CommerceNotificationTestUtil.addNotificationTemplate(
				_user.getEmailAddress(),
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
				ServiceContextTestUtil.getServiceContext(
					_group.getCompanyId(), _group.getGroupId(),
					_user.getUserId()));

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			_commerceNotificationQueueEntryLocalService.
				addCommerceNotificationQueueEntry(
					_user.getUserId(), _group.getGroupId(),
					commerceNotificationTemplate.
						getCommerceNotificationTemplateId(),
					RandomTestUtil.randomString() + "@liferay.com",
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString() + "@liferay.com",
					RandomTestUtil.randomString(), StringPool.BLANK,
					StringPool.BLANK, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), 0);

		commerceNotificationQueueEntry =
			_commerceNotificationQueueEntryLocalService.updateSent(
				commerceNotificationQueueEntry.
					getCommerceNotificationQueueEntryId(),
				true);

		_commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueueEntries(
				new Date(System.currentTimeMillis() + Time.HOUR));

		Assert.assertNull(
			_commerceNotificationQueueEntryLocalService.
				fetchCommerceNotificationQueueEntry(
					commerceNotificationQueueEntry.
						getCommerceNotificationQueueEntryId()));

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				commerceNotificationQueueEntry.getGroupId(),
				_portal.getClassNameId(
					commerceNotificationQueueEntry.getModelClassName()),
				commerceNotificationQueueEntry.getPrimaryKey(),
				SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(systemEvents.toString(), 1, systemEvents.size());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	@Inject
	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	private User _user;

}