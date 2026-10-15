/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.helper;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Samuel Trong Tran
 */
public class PublicationHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testEditorRoleModelResourceActions() {
		String[] modelResourceActions = _getModelResourceActions(
			PublicationRoleConstants.ROLE_EDITOR);

		Assert.assertFalse(
			ArrayUtil.contains(modelResourceActions, ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.UPDATE));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.VIEW));
		Assert.assertFalse(
			ArrayUtil.contains(
				modelResourceActions, CTActionKeys.INVITE_USERS));
		Assert.assertFalse(
			ArrayUtil.contains(modelResourceActions, CTActionKeys.PUBLISH));
	}

	@Test
	public void testInviterRoleModelResourceActions() {
		String[] modelResourceActions = _getModelResourceActions(
			PublicationRoleConstants.ROLE_ADMIN);

		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.UPDATE));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.VIEW));
		Assert.assertTrue(
			ArrayUtil.contains(
				modelResourceActions, CTActionKeys.INVITE_USERS));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, CTActionKeys.PUBLISH));
	}

	@Test
	public void testPublisherRoleModelResourceActions() {
		String[] modelResourceActions = _getModelResourceActions(
			PublicationRoleConstants.ROLE_PUBLISHER);

		Assert.assertFalse(
			ArrayUtil.contains(modelResourceActions, ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.UPDATE));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.VIEW));
		Assert.assertFalse(
			ArrayUtil.contains(
				modelResourceActions, CTActionKeys.INVITE_USERS));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, CTActionKeys.PUBLISH));
	}

	@Test
	public void testViewerRoleModelResourceActions() {
		String[] modelResourceActions = _getModelResourceActions(
			PublicationRoleConstants.ROLE_VIEWER);

		Assert.assertFalse(
			ArrayUtil.contains(modelResourceActions, ActionKeys.PERMISSIONS));
		Assert.assertFalse(
			ArrayUtil.contains(modelResourceActions, ActionKeys.UPDATE));
		Assert.assertTrue(
			ArrayUtil.contains(modelResourceActions, ActionKeys.VIEW));
		Assert.assertFalse(
			ArrayUtil.contains(
				modelResourceActions, CTActionKeys.INVITE_USERS));
		Assert.assertFalse(
			ArrayUtil.contains(modelResourceActions, CTActionKeys.PUBLISH));
	}

	private String[] _getModelResourceActions(int role) {
		return ReflectionTestUtil.invoke(
			new PublicationHelper(), "_getModelResourceActions",
			new Class<?>[] {int.class}, role);
	}

}