/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mateus Xavier
 */
public class RenderStructureFieldMVCResourceCommandTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	@Test
	public void testCreateDDMFormFieldRenderingContext() {
		Mockito.when(
			_httpServletRequest.getParameter("namespace")
		).thenReturn(
			_SCRIPT
		);

		Mockito.when(
			_httpServletRequest.getParameter("portletNamespace")
		).thenReturn(
			_SCRIPT
		);

		RenderStructureFieldMVCResourceCommand
			renderStructureFieldMVCResourceCommand =
				new RenderStructureFieldMVCResourceCommand();

		ReflectionTestUtil.setFieldValue(
			renderStructureFieldMVCResourceCommand, "_portal", _portal);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			renderStructureFieldMVCResourceCommand.
				createDDMFormFieldRenderingContext(
					_httpServletRequest,
					Mockito.mock(HttpServletResponse.class));

		Assert.assertEquals(
			HtmlUtil.escapeAttribute(_SCRIPT),
			ddmFormFieldRenderingContext.getNamespace());
		Assert.assertEquals(
			HtmlUtil.escapeAttribute(_SCRIPT),
			ddmFormFieldRenderingContext.getPortletNamespace());
	}

	@Test
	public void testGetDDMFormField() {
		Mockito.when(
			_httpServletRequest.getParameter("fieldName")
		).thenReturn(
			HtmlUtil.escapeAttribute(_SCRIPT)
		);

		DDMFormDeserializer ddmFormDeserializer = Mockito.mock(
			DDMFormDeserializer.class);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse = Mockito.mock(
				DDMFormDeserializerDeserializeResponse.class);

		Mockito.when(
			ddmFormDeserializer.deserialize(Mockito.any())
		).thenReturn(
			ddmFormDeserializerDeserializeResponse
		);

		DDMForm ddmForm = Mockito.mock(DDMForm.class);

		Mockito.when(
			ddmFormDeserializerDeserializeResponse.getDDMForm()
		).thenReturn(
			ddmForm
		);

		DDMFormField mockDDMFormField = Mockito.mock(DDMFormField.class);

		Mockito.when(
			mockDDMFormField.getName()
		).thenReturn(
			HtmlUtil.escapeAttribute(_SCRIPT)
		);

		Mockito.when(
			ddmForm.getDDMFormFieldsMap(true)
		).thenReturn(
			Collections.singletonMap(
				HtmlUtil.escapeAttribute(_SCRIPT), mockDDMFormField)
		);

		RenderStructureFieldMVCResourceCommand
			renderStructureFieldMVCResourceCommand =
				new RenderStructureFieldMVCResourceCommand();

		ReflectionTestUtil.setFieldValue(
			renderStructureFieldMVCResourceCommand, "_jsonDDMFormDeserializer",
			ddmFormDeserializer);

		DDMFormField ddmFormField =
			renderStructureFieldMVCResourceCommand.getDDMFormField(
				_httpServletRequest);

		Assert.assertEquals(
			HtmlUtil.escapeAttribute(_SCRIPT), ddmFormField.getName());
	}

	private static final String _SCRIPT =
		"'\"></option><img onerror=alert(123) src=x>";

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}