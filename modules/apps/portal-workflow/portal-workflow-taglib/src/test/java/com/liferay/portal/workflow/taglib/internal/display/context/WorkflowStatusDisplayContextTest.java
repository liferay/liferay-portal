/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.taglib.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.taglib.internal.constants.WorkflowStatusConstants;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

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
		BeanProperties beanProperties = Mockito.mock(BeanProperties.class);

		Mockito.when(
			beanProperties.getInteger(Mockito.any(), Mockito.eq("status"))
		).thenReturn(
			2
		);

		ReflectionTestUtil.setFieldValue(
			BeanPropertiesUtil.class, "_beanProperties", beanProperties);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_setAttribute(
			"bean", mockHttpServletRequest, Mockito.mock(Object.class));
		_setAttribute("status", mockHttpServletRequest, 1);

		Assert.assertEquals(
			Integer.valueOf(2),
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