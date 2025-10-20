/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.assignee;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.test.portlet.MockResourceURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.internal.PortalContextImpl;

import jakarta.portlet.PortalContext;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
public class AssigneeDDMFormFieldTemplateContextContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			Mockito.mock(RequestBackedPortletURLFactory.class);

		Mockito.doReturn(
			new TestMockResourceURL(new PortalContextImpl(), "urlType")
		).when(
			requestBackedPortletURLFactory
		).createResourceURL(
			_PORTLET_ID
		);

		Mockito.when(
			RequestBackedPortletURLFactoryUtil.create(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			requestBackedPortletURLFactory
		);
	}

	@After
	public void tearDown() {
		_requestBackedPortletURLFactoryUtilMockedStatic.close();
	}

	@Test
	public void testGetParameters() throws Exception {
		DDMFormField ddmFormField = new DDMFormField(
			"assignee", ObjectDDMFormFieldTypeConstants.ASSIGNEE);

		ddmFormField.setProperty("portletId", _PORTLET_ID);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			new MockHttpServletRequest());

		Assert.assertEquals(
			"http://localhost:8080/object_entries/autocomplete_assignee",
			MapUtil.getString(
				_assigneeDDMFormFieldTemplateContextContributor.getParameters(
					ddmFormField, ddmFormFieldRenderingContext),
				"searchURL"));
	}

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

	private final AssigneeDDMFormFieldTemplateContextContributor
		_assigneeDDMFormFieldTemplateContextContributor =
			new AssigneeDDMFormFieldTemplateContextContributor();
	private final MockedStatic<RequestBackedPortletURLFactoryUtil>
		_requestBackedPortletURLFactoryUtilMockedStatic = Mockito.mockStatic(
			RequestBackedPortletURLFactoryUtil.class);

	private class TestMockResourceURL extends MockResourceURL {

		public TestMockResourceURL(
			PortalContext portalContext, String urlType) {

			super(portalContext, urlType);
		}

		@Override
		public String toString() {
			return "http://localhost:8080" + getResourceID();
		}

	}

}