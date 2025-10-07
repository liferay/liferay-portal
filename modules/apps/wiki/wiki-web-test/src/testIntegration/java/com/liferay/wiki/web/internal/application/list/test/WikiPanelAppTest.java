/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.application.list.test;

import com.liferay.application.list.PanelApp;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class WikiPanelAppTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-35013")
	@Test
	public void testIsShow() throws Exception {
		User user = UserTestUtil.addUser();

		try {
			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			Assert.assertFalse(
				_wikiPanelApp.isShow(
					permissionChecker,
					_groupLocalService.getGroup(TestPropsValues.getGroupId())));
		}
		finally {
			_userLocalService.deleteUser(user);
		}

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		Assert.assertTrue(
			_wikiPanelApp.isShow(
				permissionChecker,
				_groupLocalService.getGroup(TestPropsValues.getGroupId())));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject(
		filter = "component.name=com.liferay.wiki.web.internal.application.list.WikiPanelApp"
	)
	private PanelApp _wikiPanelApp;

}