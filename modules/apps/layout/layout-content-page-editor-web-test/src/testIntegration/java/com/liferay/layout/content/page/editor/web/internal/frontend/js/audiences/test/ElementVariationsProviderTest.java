/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.frontend.js.audiences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.js.audiences.ElementVariationsProvider;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@FeatureFlag("LPD-85746")
@RunWith(Arquillian.class)
public class ElementVariationsProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetElementVariations() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				group.getGroupId(), layout.getPlid());

		User guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(guestUser));

		Assert.assertNotNull(
			_elementVariationsProvider.getElementVariations(
				layout.getPlid(),
				segmentsExperience.getSegmentsExperienceId()));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), ActionKeys.VIEW);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(guestUser));

		Assert.assertNull(
			_elementVariationsProvider.getElementVariations(
				layout.getPlid(),
				segmentsExperience.getSegmentsExperienceId()));
	}

	@Inject
	private ElementVariationsProvider _elementVariationsProvider;

	@Inject
	private UserLocalService _userLocalService;

}