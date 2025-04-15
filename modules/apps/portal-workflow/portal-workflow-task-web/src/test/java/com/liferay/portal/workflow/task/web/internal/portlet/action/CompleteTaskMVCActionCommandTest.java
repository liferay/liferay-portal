/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletResponse;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class CompleteTaskMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_completeTaskMVCActionCommand = new CompleteTaskMVCActionCommand();
	}

	@Test
	public void testGetHttpServletRequest() {
		ActionRequest mockActionRequest = new MockActionRequest();

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE, new MockPortletResponse());

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getHttpServletRequest(mockActionRequest)
		).thenReturn(
			mockHttpServletRequest
		);

		ReflectionTestUtil.setFieldValue(
			_completeTaskMVCActionCommand, "_portal", portal);

		HttpServletRequest httpServletRequest = ReflectionTestUtil.invoke(
			_completeTaskMVCActionCommand, "_getHttpServletRequest",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockActionRequest, new MockActionResponse());

		Assert.assertNotNull(httpServletRequest);
		Assert.assertNotNull(
			httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE));
	}

	@Test
	public void testGetHttpServletRequestWithNoPortletResponse() {
		ActionRequest mockActionRequest = new MockActionRequest();
		ActionResponse mockActionResponse = new MockActionResponse();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getHttpServletRequest(mockActionRequest)
		).thenReturn(
			new MockHttpServletRequest()
		);

		Mockito.when(
			portal.getLiferayPortletResponse(mockActionResponse)
		).thenReturn(
			new MockLiferayPortletActionResponse()
		);

		ReflectionTestUtil.setFieldValue(
			_completeTaskMVCActionCommand, "_portal", portal);

		HttpServletRequest httpServletRequest = ReflectionTestUtil.invoke(
			_completeTaskMVCActionCommand, "_getHttpServletRequest",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockActionRequest, mockActionResponse);

		Assert.assertNotNull(httpServletRequest);
		Assert.assertNotNull(
			httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE));
	}

	private CompleteTaskMVCActionCommand _completeTaskMVCActionCommand;

}