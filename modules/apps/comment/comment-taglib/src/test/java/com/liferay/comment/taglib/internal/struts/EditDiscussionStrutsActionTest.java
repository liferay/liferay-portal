/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.struts;

import com.liferay.comment.configuration.CommentGroupServiceConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Anderson Luiz
 */
public class EditDiscussionStrutsActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecute() throws Exception {
		Comment comment = Mockito.mock(Comment.class);
		String externalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			comment.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		long commentId = RandomTestUtil.randomLong();

		Mockito.when(
			_commentManager.fetchComment(commentId)
		).thenReturn(
			comment
		);

		Mockito.when(
			_commentManager.updateComment(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(Function.class))
		).thenReturn(
			commentId
		);

		Mockito.when(
			_configurationProvider.getGroupConfiguration(
				Mockito.eq(CommentGroupServiceConfiguration.class),
				Mockito.anyLong())
		).thenReturn(
			Mockito.mock(CommentGroupServiceConfiguration.class)
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));
		mockHttpServletRequest.setParameter(Constants.CMD, Constants.ADD);
		mockHttpServletRequest.setParameter(
			"commentId", String.valueOf(commentId));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_editDiscussionStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			content.contains(
				"\"externalReferenceCode\":\"" + externalReferenceCode + "\""));

		Mockito.verify(
			_commentManager, Mockito.times(1)
		).fetchComment(
			Mockito.anyLong()
		);
	}

	@Mock
	private CommentManager _commentManager;

	@Mock
	private ConfigurationProvider _configurationProvider;

	@Mock
	private DiscussionPermission _discussionPermission;

	@InjectMocks
	private EditDiscussionStrutsAction _editDiscussionStrutsAction;

	@Mock
	private UserLocalService _userLocalService;

}