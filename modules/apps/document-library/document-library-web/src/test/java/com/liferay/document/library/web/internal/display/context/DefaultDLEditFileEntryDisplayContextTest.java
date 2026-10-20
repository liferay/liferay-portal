/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.document.library.web.internal.display.context.helper.FileEntryDisplayContextHelper;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.portal.bean.BeanPropertiesImpl;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
public class DefaultDLEditFileEntryDisplayContextTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(new BeanPropertiesImpl());
	}

	@Test
	public void testGetPublishButtonLabel() throws Exception {
		DefaultDLEditFileEntryDisplayContext
			defaultDLEditFileEntryDisplayContext =
				_getDefaultDLEditFileEntryDisplayContext();

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			Mockito.mock(FileEntryDisplayContextHelper.class);

		ReflectionTestUtil.setFieldValue(
			defaultDLEditFileEntryDisplayContext,
			"_fileEntryDisplayContextHelper", fileEntryDisplayContextHelper);

		try (MockedStatic<DLUtil> dlUtilMockedStatic = Mockito.mockStatic(
				DLUtil.class)) {

			dlUtilMockedStatic.when(
				() -> DLUtil.hasWorkflowDefinitionLink(
					Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
					Mockito.anyLong())
			).thenReturn(
				true
			);

			Assert.assertEquals(
				"submit-for-workflow",
				defaultDLEditFileEntryDisplayContext.getPublishButtonLabel());

			Mockito.when(
				fileEntryDisplayContextHelper.isCheckedOut()
			).thenReturn(
				true
			);

			Assert.assertEquals(
				"save",
				defaultDLEditFileEntryDisplayContext.getPublishButtonLabel());
		}
	}

	@Test
	public void testIsPublishButtonDisabled() throws Exception {
		DefaultDLEditFileEntryDisplayContext
			defaultDLEditFileEntryDisplayContext =
				_getDefaultDLEditFileEntryDisplayContext();

		FileEntryDisplayContextHelper fileEntryDisplayContextHelper =
			Mockito.mock(FileEntryDisplayContextHelper.class);

		Mockito.when(
			fileEntryDisplayContextHelper.isCheckedOutByOther()
		).thenReturn(
			false
		);

		ReflectionTestUtil.setFieldValue(
			defaultDLEditFileEntryDisplayContext,
			"_fileEntryDisplayContextHelper", fileEntryDisplayContextHelper);

		Assert.assertFalse(
			defaultDLEditFileEntryDisplayContext.isPublishButtonDisabled());

		Mockito.when(
			fileEntryDisplayContextHelper.isCheckedOutByOther()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			defaultDLEditFileEntryDisplayContext.isPublishButtonDisabled());
	}

	private DefaultDLEditFileEntryDisplayContext
		_getDefaultDLEditFileEntryDisplayContext() {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return new DefaultDLEditFileEntryDisplayContext(
			_configurationProvider, _ddmFormValuesFactory,
			_ddmStorageEngineManager, _dlFileEntryType, _dlValidator,
			mockHttpServletRequest);
	}

	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private final DDMFormValuesFactory _ddmFormValuesFactory = Mockito.mock(
		DDMFormValuesFactory.class);
	private final DDMStorageEngineManager _ddmStorageEngineManager =
		Mockito.mock(DDMStorageEngineManager.class);
	private final DLFileEntryType _dlFileEntryType = Mockito.mock(
		DLFileEntryType.class);
	private final DLValidator _dlValidator = Mockito.mock(DLValidator.class);

}