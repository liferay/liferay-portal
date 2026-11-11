/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.taglib.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.model.LayoutRevisionWrapper;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.taglib.internal.constants.WorkflowStatusConstants;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Feliphe Marinho
 */
public class WorkflowStatusDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetStatus() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_setAttribute(
			"bean", mockHttpServletRequest,
			new LayoutRevisionWrapper(null) {

				@Override
				public int getStatus() {
					return 2;
				}

			});
		_setAttribute("status", mockHttpServletRequest, 1);

		Assert.assertEquals(
			Integer.valueOf(2),
			_workflowStatusDisplayContext.getStatus(mockHttpServletRequest));

		_setAttribute(
			"bean", mockHttpServletRequest,
			ProxyUtil.newProxyInstance(
				WorkflowableComment.class.getClassLoader(),
				new Class<?>[] {WorkflowableComment.class},
				(proxy, method, arguments) -> 3));

		Assert.assertEquals(
			Integer.valueOf(3),
			_workflowStatusDisplayContext.getStatus(mockHttpServletRequest));

		_setAttribute("bean", mockHttpServletRequest, new Object());

		Assert.assertEquals(
			Integer.valueOf(1),
			_workflowStatusDisplayContext.getStatus(mockHttpServletRequest));

		_setAttribute("bean", mockHttpServletRequest, null);

		Assert.assertEquals(
			Integer.valueOf(1),
			_workflowStatusDisplayContext.getStatus(mockHttpServletRequest));
	}

	@Test
	public void testGetStatusMessage() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_setAttribute("status", mockHttpServletRequest, 2);
		_setAttribute(
			"statusMessage", mockHttpServletRequest, "Status Message");

		Assert.assertEquals(
			"Status Message",
			_workflowStatusDisplayContext.getStatusMessage(
				mockHttpServletRequest));

		_setAttribute(
			"statusMessage", mockHttpServletRequest, StringPool.BLANK);

		Assert.assertEquals(
			WorkflowConstants.LABEL_DRAFT,
			_workflowStatusDisplayContext.getStatusMessage(
				mockHttpServletRequest));
	}

	private void _setAttribute(
		String attribute, HttpServletRequest httpServletRequest, Object value) {

		httpServletRequest.setAttribute(
			WorkflowStatusConstants.ATTRIBUTE_NAMESPACE + attribute, value);
	}

	private final WorkflowStatusDisplayContext _workflowStatusDisplayContext =
		new WorkflowStatusDisplayContext();

}