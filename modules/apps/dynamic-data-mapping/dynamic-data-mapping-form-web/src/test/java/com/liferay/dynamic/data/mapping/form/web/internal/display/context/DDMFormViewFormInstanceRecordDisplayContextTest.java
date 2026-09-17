/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Alberto Sousa
 */
public class DDMFormViewFormInstanceRecordDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDDMForm();
		_setUpDDMFormInstanceRecordService();
		_setUpDDMFormInstanceVersionLocalService();
		_setUpDDMFormRenderer();
		_setUpDDMFormValuesMerger();
		_setUpHttpServletRequest();
		_setUpLanguageUtil();
		_setUpPortalUtil();
	}

	@Test
	public void testGetDDMFormContext() throws Exception {
		DDMFormViewFormInstanceRecordDisplayContext
			ddmFormViewFormInstanceRecordDisplayContext =
				new DDMFormViewFormInstanceRecordDisplayContext(
					_httpServletRequest,
					Mockito.mock(HttpServletResponse.class),
					_ddmFormInstanceRecordService,
					_ddmFormInstanceVersionLocalService, _ddmFormRenderer,
					Mockito.mock(DDMFormValuesFactory.class),
					_ddmFormValuesMerger);

		ddmFormViewFormInstanceRecordDisplayContext.getDDMFormContext(
			_renderRequest);

		Mockito.verify(
			_ddmFormInstanceRecordService
		).getFormInstanceRecord(
			_FORM_INSTANCE_RECORD_ID
		);
	}

	@Test
	public void testGetDDMFormContextWithRedirectURL() throws Exception {
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

		Mockito.when(
			_httpServletRequest.getParameter("redirect")
		).thenReturn(
			_REDIRECT_URL
		);

		DDMFormViewFormInstanceRecordDisplayContext
			ddmFormViewFormInstanceRecordDisplayContext =
				new DDMFormViewFormInstanceRecordDisplayContext(
					_httpServletRequest,
					Mockito.mock(HttpServletResponse.class),
					_ddmFormInstanceRecordService,
					_ddmFormInstanceVersionLocalService, _ddmFormRenderer,
					Mockito.mock(DDMFormValuesFactory.class),
					_ddmFormValuesMerger);

		ddmFormViewFormInstanceRecordDisplayContext.getDDMFormContext(
			_renderRequest);

		ArgumentCaptor<DDMFormRenderingContext>
			ddmFormRenderingContextArgumentCaptor = ArgumentCaptor.forClass(
				DDMFormRenderingContext.class);

		Mockito.verify(
			_ddmFormRenderer
		).getDDMFormTemplateContext(
			Mockito.any(), Mockito.any(),
			ddmFormRenderingContextArgumentCaptor.capture()
		);

		DDMFormRenderingContext ddmFormRenderingContext =
			ddmFormRenderingContextArgumentCaptor.getValue();

		Assert.assertEquals(
			_ESCAPED_REDIRECT_URL, ddmFormRenderingContext.getRedirectURL());
	}

	private DDMFormInstance _mockDDMFormInstance() throws Exception {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		Mockito.when(
			ddmFormInstance.getFormInstanceVersion(Mockito.anyString())
		).thenReturn(
			_ddmFormInstanceVersion
		);

		return ddmFormInstance;
	}

	private DDMFormInstanceRecord _mockDDMFormInstanceRecord()
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		DDMFormValues ddmFormValues = _mockDDMFormValues();

		Mockito.when(
			ddmFormInstanceRecord.getDDMFormValues()
		).thenReturn(
			ddmFormValues
		);

		DDMFormInstance ddmFormInstance = _mockDDMFormInstance();

		Mockito.when(
			ddmFormInstanceRecord.getFormInstance()
		).thenReturn(
			ddmFormInstance
		);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceVersion()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return ddmFormInstanceRecord;
	}

	private DDMFormValues _mockDDMFormValues() {
		DDMFormValues ddmFormValues = Mockito.mock(DDMFormValues.class);

		Mockito.when(
			ddmFormValues.getAvailableLocales()
		).thenReturn(
			Collections.singleton(LocaleUtil.US)
		);

		Mockito.when(
			ddmFormValues.getDefaultLocale()
		).thenReturn(
			LocaleUtil.US
		);

		return ddmFormValues;
	}

	private void _setUpDDMForm() {
		Mockito.when(
			_ddmForm.getAvailableLocales()
		).thenReturn(
			SetUtil.fromArray(LocaleUtil.US)
		);
	}

	private void _setUpDDMFormInstanceRecordService() throws Exception {
		DDMFormInstanceRecord ddmFormInstanceRecord =
			_mockDDMFormInstanceRecord();

		Mockito.when(
			_ddmFormInstanceRecordService.getFormInstanceRecord(
				_FORM_INSTANCE_RECORD_ID)
		).thenReturn(
			ddmFormInstanceRecord
		);
	}

	private void _setUpDDMFormInstanceVersionLocalService() throws Exception {
		DDMStructureVersion ddmStructureVersion = Mockito.mock(
			DDMStructureVersion.class);

		Mockito.when(
			ddmStructureVersion.getDDMForm()
		).thenReturn(
			_ddmForm
		);

		Mockito.when(
			ddmStructureVersion.getDDMFormLayout()
		).thenReturn(
			Mockito.mock(DDMFormLayout.class)
		);

		Mockito.when(
			_ddmFormInstanceVersion.getStructureVersion()
		).thenReturn(
			ddmStructureVersion
		);

		Mockito.when(
			_ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
				Mockito.anyLong(), Mockito.anyInt())
		).thenReturn(
			_ddmFormInstanceVersion
		);
	}

	private void _setUpDDMFormRenderer() throws Exception {
		Mockito.when(
			_ddmFormRenderer.getDDMFormTemplateContext(
				Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			Collections.emptyMap()
		);
	}

	private void _setUpDDMFormValuesMerger() {
		DDMFormValues mergedDDMFormValues = Mockito.mock(DDMFormValues.class);

		Mockito.when(
			mergedDDMFormValues.getDefaultLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_ddmFormValuesMerger.merge(Mockito.any(), Mockito.any())
		).thenReturn(
			mergedDDMFormValues
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_httpServletRequest.getParameter("formInstanceRecordId")
		).thenReturn(
			String.valueOf(_FORM_INSTANCE_RECORD_ID)
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.escapeRedirect(_REDIRECT_URL)
		).thenReturn(
			_ESCAPED_REDIRECT_URL
		);

		portalUtil.setPortal(portal);
	}

	private static final String _ESCAPED_REDIRECT_URL =
		RandomTestUtil.randomString();

	private static final long _FORM_INSTANCE_RECORD_ID =
		RandomTestUtil.randomLong();

	private static final String _REDIRECT_URL = RandomTestUtil.randomString();

	private final DDMForm _ddmForm = Mockito.mock(DDMForm.class);
	private final DDMFormInstanceRecordService _ddmFormInstanceRecordService =
		Mockito.mock(DDMFormInstanceRecordService.class);
	private final DDMFormInstanceVersion _ddmFormInstanceVersion = Mockito.mock(
		DDMFormInstanceVersion.class);
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService = Mockito.mock(
			DDMFormInstanceVersionLocalService.class);
	private final DDMFormRenderer _ddmFormRenderer = Mockito.mock(
		DDMFormRenderer.class);
	private final DDMFormValuesMerger _ddmFormValuesMerger = Mockito.mock(
		DDMFormValuesMerger.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);

}