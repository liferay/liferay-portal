/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.nofications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AssetPublisherUserNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addOmniadminUser();

		_group = _groupLocalService.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			null, 0, GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Test Site"
			).put(
				LocaleUtil.SPAIN, "Sitio de Pruebas"
			).build(),
			null, GroupConstants.TYPE_SITE_OPEN, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, "/test-site", true,
			true, ServiceContextTestUtil.getServiceContext());

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_portletId = LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout,
			AssetPublisherPortletKeys.ASSET_PUBLISHER, "column-1",
			new HashMap<>());

		_subscribe();
		_updatePortletPreferences();

		MailServiceTestUtil.clearMessages();
	}

	@Test
	public void testUserNotification() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		_assertAssetPublisherNotifications(
			_getExpectedMailBody(
				journalArticle.getTitle(_user.getLanguageId()),
				_portal.getPortletTitle(
					AssetPublisherPortletKeys.ASSET_PUBLISHER,
					_user.getLanguageId()),
				_group.getDescriptiveName(_user.getLocale())));
	}

	@Test
	public void testUserNotificationWithDifferentUserLocale() throws Exception {
		_user = _userLocalService.updateLanguageId(
			_user.getUserId(), LanguageUtil.getLanguageId(LocaleUtil.SPAIN));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Title"
			).put(
				LocaleUtil.SPAIN, "Titulo"
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Content"
			).put(
				LocaleUtil.SPAIN, "Contenido"
			).build(),
			null, LocaleUtil.getDefault(), null, true, true,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		_assertAssetPublisherNotifications(
			_getExpectedMailBody(
				journalArticle.getTitle(_user.getLanguageId()),
				_portal.getPortletTitle(
					AssetPublisherPortletKeys.ASSET_PUBLISHER,
					_user.getLanguageId()),
				_group.getDescriptiveName(_user.getLocale())));
	}

	private void _assertAssetPublisherNotifications(String expectedMailBody) {
		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		Assert.assertEquals(expectedMailBody, mailMessage.getBody());
	}

	private String _getExpectedMailBody(
		String assetEntries, String portletTitle, String siteName) {

		return StringUtil.replace(
			_EMAIL_ASSET_ENTRY_ADDED_BODY,
			new String[] {
				"[$ASSET_ENTRIES$]", "[$PORTLET_TITLE$]", "[$SITE_NAME$]"
			},
			new String[] {
				HtmlUtil.escape(assetEntries), portletTitle, siteName
			});
	}

	private void _subscribe() throws Exception {
		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portletId);

		_subscriptionLocalService.addSubscription(
			_user.getUserId(), _group.getGroupId(),
			PortletPreferences.class.getName(),
			portletPreferences.getPortletPreferencesId());
	}

	private void _updatePortletPreferences() throws Exception {
		jakarta.portlet.PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		portletPreferences.setValue(
			_localization.getLocalizedName(
				"emailAssetEntryAddedBody",
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault())),
			_EMAIL_ASSET_ENTRY_ADDED_BODY);
		portletPreferences.setValue("selectionStyle", "dynamic");

		portletPreferences.store();
	}

	private static final String _EMAIL_ASSET_ENTRY_ADDED_BODY =
		"[$PORTLET_TITLE$]: [$SITE_NAME$]: [$ASSET_ENTRIES$]";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private Localization _localization;

	@Inject
	private Portal _portal;

	private String _portletId;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject(
		filter = "component.name=com.liferay.asset.publisher.web.internal.scheduler.CheckAssetEntrySchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}