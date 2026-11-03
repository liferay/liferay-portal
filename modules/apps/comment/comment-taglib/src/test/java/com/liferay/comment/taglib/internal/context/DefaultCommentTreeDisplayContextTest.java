/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.context;

import com.liferay.comment.taglib.internal.context.helper.DiscussionRequestHelper;
import com.liferay.comment.taglib.internal.context.helper.DiscussionTaglibHelper;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brian Wulbern
 */
public class DefaultCommentTreeDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_themeDisplay.getSiteGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			_themeDisplay.isSignedIn()
		).thenReturn(
			true
		);

		Mockito.when(
			_discussionRequestHelper.getThemeDisplay()
		).thenReturn(
			_themeDisplay
		);

		_defaultCommentTreeDisplayContext =
			new DefaultCommentTreeDisplayContext(
				_discussionRequestHelper,
				Mockito.mock(DiscussionTaglibHelper.class),
				Mockito.mock(DiscussionPermission.class),
				Mockito.mock(DiscussionComment.class));
	}

	@Test
	@TestInfo({"LPD-113633", "LPS-74621"})
	public void testIsActionControlsVisible() throws Exception {
		Assert.assertTrue(
			_defaultCommentTreeDisplayContext.isActionControlsVisible());

		Mockito.when(
			_group.isStagingGroup()
		).thenReturn(
			true
		);

		Assert.assertFalse(
			_defaultCommentTreeDisplayContext.isActionControlsVisible());
	}

	@Test
	@TestInfo({"LPD-113633", "LPS-74621"})
	public void testIsReplyButtonVisible() {
		Assert.assertTrue(
			_defaultCommentTreeDisplayContext.isReplyButtonVisible());

		Mockito.when(
			_group.isStagingGroup()
		).thenReturn(
			true
		);

		Assert.assertFalse(
			_defaultCommentTreeDisplayContext.isReplyButtonVisible());
	}

	private DefaultCommentTreeDisplayContext _defaultCommentTreeDisplayContext;
	private final DiscussionRequestHelper _discussionRequestHelper =
		Mockito.mock(DiscussionRequestHelper.class);
	private final Group _group = Mockito.mock(Group.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}