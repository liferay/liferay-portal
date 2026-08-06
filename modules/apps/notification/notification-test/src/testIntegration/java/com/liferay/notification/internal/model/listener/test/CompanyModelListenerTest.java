/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.NotificationRecipientLocalService;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.test.util.NotificationTemplateUtil;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Paulo Albuquerque
 */
@RunWith(Arquillian.class)
public class CompanyModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		_companyId = company.getCompanyId();

		_user = UserTestUtil.getAdminUser(_companyId);
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		NotificationRecipientSetting notificationRecipientSetting =
			NotificationRecipientSettingUtil.createNotificationRecipientSetting(
				"userScreenName", "[%CURRENT_USER_EMAIL_ADDRESS%]");

		Assert.assertEquals(
			TestPropsValues.getCompanyId(),
			notificationRecipientSetting.getCompanyId());

		NotificationContext notificationContext =
			NotificationTemplateUtil.createNotificationContext(
				Collections.singletonList(notificationRecipientSetting),
				NotificationConstants.TYPE_USER_NOTIFICATION);

		NotificationQueueEntry notificationQueueEntry =
			_notificationQueueEntryLocalService.addNotificationQueueEntry(
				notificationContext);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(),
			notificationQueueEntry.getCompanyId());

		Assert.assertEquals(
			1,
			_notificationQueueEntryLocalService.
				getNotificationQueueEntriesCount());

		NotificationRecipient notificationRecipient = null;
		NotificationTemplate notificationTemplate = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(_companyId)) {

			notificationRecipientSetting =
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"userScreenName", _user.getScreenName());

			Assert.assertEquals(
				_companyId, notificationRecipientSetting.getCompanyId());

			notificationContext =
				NotificationTemplateUtil.createNotificationContext(
					Collections.singletonList(notificationRecipientSetting),
					NotificationConstants.TYPE_USER_NOTIFICATION);

			notificationQueueEntry =
				_notificationQueueEntryLocalService.addNotificationQueueEntry(
					notificationContext);

			Assert.assertEquals(
				_companyId, notificationQueueEntry.getCompanyId());

			Assert.assertEquals(
				2,
				_notificationQueueEntryLocalService.
					getNotificationQueueEntriesCount());

			notificationTemplate =
				_notificationTemplateLocalService.addNotificationTemplate(
					notificationContext);

			Assert.assertEquals(
				_companyId, notificationTemplate.getCompanyId());

			notificationRecipient =
				notificationTemplate.getNotificationRecipient();

			Assert.assertEquals(
				_companyId, notificationRecipient.getCompanyId());
		}

		Assert.assertNotNull(
			_notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					"COMMERCE_ORDER_TEMPLATE", TestPropsValues.getCompanyId()));

		_companyLocalService.deleteCompany(_companyId);

		Assert.assertEquals(
			1,
			_notificationQueueEntryLocalService.
				getNotificationQueueEntriesCount());

		Assert.assertNotNull(
			_notificationTemplateLocalService.
				fetchNotificationTemplateByExternalReferenceCode(
					"COMMERCE_ORDER_TEMPLATE", TestPropsValues.getCompanyId()));

		Assert.assertNull(
			_notificationQueueEntryLocalService.fetchNotificationQueueEntry(
				notificationQueueEntry.getNotificationQueueEntryId()));
		Assert.assertNull(
			_notificationRecipientLocalService.fetchNotificationRecipient(
				notificationRecipient.getNotificationRecipientId()));
		Assert.assertNull(
			_notificationRecipientSettingLocalService.
				fetchNotificationRecipientSetting(
					notificationRecipientSetting.
						getNotificationRecipientSettingId()));
		Assert.assertNull(
			_notificationTemplateLocalService.fetchNotificationTemplate(
				notificationTemplate.getNotificationTemplateId()));
	}

	private long _companyId;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject
	private NotificationRecipientLocalService
		_notificationRecipientLocalService;

	@Inject
	private NotificationRecipientSettingLocalService
		_notificationRecipientSettingLocalService;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	private User _user;

}