/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsImpl;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class DDMFormViewFormInstanceRecordsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());
	}

	@Before
	public void setUp() throws PortalException {
		_setUpPortalUtil();

		_setUpDDMFormViewFormInstanceRecordsDisplayContext();
	}

	@Test
	public void testGetAvailableLocalesCount() throws Exception {
		Assert.assertEquals(
			2,
			_ddmFormViewFormInstanceRecordsDisplayContext.
				getAvailableLocalesCount());
	}

	@Test
	public void testGetColumnValue() {
		Assert.assertEquals(
			"mockedRenderResponse, mockedRenderResponse",
			_ddmFormViewFormInstanceRecordsDisplayContext.getColumnValue(
				_mockDDMFormField(DDMFormFieldTypeConstants.SEARCH_LOCATION),
				"field",
				Arrays.asList(
					DDMFormValuesTestUtil.createDDMFormFieldValue(
						"field",
						DDMFormValuesTestUtil.createLocalizedValue(
							StringUtil.randomString(), LocaleUtil.US)),
					DDMFormValuesTestUtil.createDDMFormFieldValue(
						"field",
						DDMFormValuesTestUtil.createLocalizedValue(
							StringUtil.randomString(), LocaleUtil.US)))));
	}

	@Test
	public void testGetDefaultLocale() throws Exception {
		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		DDMFormValues ddmFormValues = Mockito.mock(DDMFormValues.class);

		Mockito.when(
			ddmFormValues.getDefaultLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			ddmFormInstanceRecord.getDDMFormValues()
		).thenReturn(
			ddmFormValues
		);

		Assert.assertEquals(
			LocaleUtil.US,
			_ddmFormViewFormInstanceRecordsDisplayContext.getDefaultLocale(
				ddmFormInstanceRecord));
	}

	@Test
	public void testGetVisibleFields() {
		Assert.assertEquals(
			ListUtil.fromArray("city", "country"),
			_ddmFormViewFormInstanceRecordsDisplayContext.getVisibleFields(
				DDMFormTestUtil.createSearchLocationDDMFormField(
					DDMFormValuesTestUtil.createLocalizedValue(
						StringPool.BLANK, LocaleUtil.US),
					"field",
					DDMFormValuesTestUtil.createLocalizedValue(
						Arrays.toString(new String[] {"city", "country"}),
						LocaleUtil.US))));
	}

	private DDMForm _mockDDMForm() {
		DDMForm ddmForm = Mockito.mock(DDMForm.class);

		Mockito.when(
			ddmForm.getAvailableLocales()
		).thenReturn(
			new HashSet<Locale>(Arrays.asList(LocaleUtil.US, LocaleUtil.BRAZIL))
		);

		return ddmForm;
	}

	private DDMFormField _mockDDMFormField(String type) {
		DDMFormField ddmFormField = Mockito.mock(DDMFormField.class);

		Mockito.when(
			ddmFormField.getType()
		).thenReturn(
			type
		);

		return ddmFormField;
	}

	private DDMFormFieldTypeServicesRegistry
		_mockDDMFormFieldTypeServicesRegistry() {

		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			Mockito.mock(DDMFormFieldTypeServicesRegistry.class);

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			_mockDDMFormFieldValueRenderer();

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueRenderer(
				Mockito.anyString())
		).thenReturn(
			ddmFormFieldValueRenderer
		);

		return ddmFormFieldTypeServicesRegistry;
	}

	private DDMFormFieldValueRenderer _mockDDMFormFieldValueRenderer() {
		DDMFormFieldValueRenderer ddmFormFieldValueRenderer = Mockito.mock(
			DDMFormFieldValueRenderer.class);

		Mockito.when(
			ddmFormFieldValueRenderer.render(
				Mockito.anyString(), Mockito.any(DDMFormFieldValue.class),
				Mockito.any(Locale.class))
		).thenReturn(
			"mockedRenderResponse"
		);

		return ddmFormFieldValueRenderer;
	}

	private DDMFormInstance _mockDDMFormInstance() throws PortalException {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		DDMForm ddmForm = _mockDDMForm();

		Mockito.when(
			ddmFormInstance.getDDMForm()
		).thenReturn(
			ddmForm
		);

		DDMStructure ddmStructure = _mockDDMStructure(ddmForm);

		Mockito.when(
			ddmFormInstance.getStructure()
		).thenReturn(
			ddmStructure
		);

		return ddmFormInstance;
	}

	private DDMStructure _mockDDMStructure(DDMForm ddmForm) {
		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		Mockito.when(
			ddmStructure.getDDMForm()
		).thenReturn(
			ddmForm
		);

		return ddmStructure;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			new PortletDisplay()
		);

		return themeDisplay;
	}

	private void _setUpDDMFormViewFormInstanceRecordsDisplayContext()
		throws PortalException {

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			renderRequest.getParameter(Mockito.eq("redirect"))
		).thenReturn(
			"test"
		);

		_ddmFormViewFormInstanceRecordsDisplayContext =
			new DDMFormViewFormInstanceRecordsDisplayContext(
				renderRequest, Mockito.mock(RenderResponse.class),
				_mockDDMFormInstance(),
				Mockito.mock(DDMFormInstanceRecordLocalService.class),
				_mockDDMFormFieldTypeServicesRegistry());
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			httpServletRequest
		);

		portalUtil.setPortal(portal);
	}

	private DDMFormViewFormInstanceRecordsDisplayContext
		_ddmFormViewFormInstanceRecordsDisplayContext;

}