/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsImpl;

import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adam Brandizzi
 */
public class DDMFormDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		PropsUtil.setProps(new PropsImpl());
	}

	@Before
	public void setUp() throws PortalException {
		_setUpDDMStructureLocalService();
		_setUpGroupLocalService();
		_setUpJSONFactoryUtil();
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpPrefsParamUtilMockedStatic();
		_setUpResourceBundleUtil();
	}

	@After
	public void tearDown() {
		_prefsParamUtilMockedStatic.close();
	}

	@Test
	public void testAutosaveWithGuestUser() throws Exception {
		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext(_mockRenderRequest(true));

		Assert.assertFalse(ddmFormDisplayContext.isAutosaveEnabled());
	}

	@Test
	public void testAutosaveWithNonguestUser1() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings =
			_mockDDMFormInstanceSettingsAutosaveWithNonguestUser();

		Mockito.when(
			ddmFormInstanceSettings.autosaveEnabled()
		).thenReturn(
			Boolean.FALSE
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertFalse(ddmFormDisplayContext.isAutosaveEnabled());
	}

	@Test
	public void testAutosaveWithNonguestUser2() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings =
			_mockDDMFormInstanceSettingsAutosaveWithNonguestUser();

		Mockito.when(
			ddmFormInstanceSettings.autosaveEnabled()
		).thenReturn(
			Boolean.TRUE
		);

		Mockito.when(
			_ddmFormWebConfiguration.autosaveInterval()
		).thenReturn(
			1
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertTrue(ddmFormDisplayContext.isAutosaveEnabled());
	}

	@Test
	public void testAutosaveWithNonguestUser3() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings =
			_mockDDMFormInstanceSettingsAutosaveWithNonguestUser();

		Mockito.when(
			ddmFormInstanceSettings.autosaveEnabled()
		).thenReturn(
			Boolean.TRUE
		);

		Mockito.when(
			_ddmFormWebConfiguration.autosaveInterval()
		).thenReturn(
			0
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertFalse(ddmFormDisplayContext.isAutosaveEnabled());
	}

	@Test
	public void testCreateDDMFormRenderingContext() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		_mockDDMFormInstance(ddmFormInstanceSettings);

		DDMFormDisplayContext ddmFormDisplayContext = Mockito.spy(
			_createDDMFormDisplayContext());

		Mockito.doReturn(
			true
		).when(
			ddmFormDisplayContext
		).hasAddFormInstanceRecordPermission();

		DDMFormInstance ddmFormInstance = new DDMFormInstanceImpl();

		Mockito.doReturn(
			true
		).when(
			ddmFormDisplayContext
		).hasValidStorageType(
			ddmFormInstance
		);

		DDMFormRenderingContext ddmFormRenderingContext =
			ddmFormDisplayContext.createDDMFormRenderingContext(
				new DDMForm(), ddmFormInstance, null);

		Assert.assertFalse(
			ddmFormRenderingContext.getProperty(
				"showPartialResultsToRespondents"));

		Mockito.when(
			ddmFormInstanceSettings.showPartialResultsToRespondents()
		).thenReturn(
			true
		);

		ddmFormRenderingContext =
			ddmFormDisplayContext.createDDMFormRenderingContext(
				new DDMForm(), ddmFormInstance, null);

		Assert.assertTrue(
			ddmFormRenderingContext.getProperty(
				"showPartialResultsToRespondents"));
	}

	@Test
	public void testDDMFormRenderingContextLocaleIsThemeDisplayLocale()
		throws Exception {

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Locale defaultLocale = LocaleUtil.BRAZIL;

		Set<Locale> availableLocales = new HashSet<>();

		availableLocales.add(defaultLocale);
		availableLocales.add(LocaleUtil.SPAIN);

		DDMForm ddmForm = _createDDMForm(availableLocales, defaultLocale);

		_mockHttpServletRequest2.addParameter(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.SPAIN));

		DDMFormRenderingContext ddmFormRenderingContext =
			ddmFormDisplayContext.createDDMFormRenderingContext(
				ddmForm, new DDMFormInstanceImpl(), null);

		Assert.assertEquals(
			LocaleUtil.SPAIN, ddmFormRenderingContext.getLocale());
	}

	@Test
	public void testGetCustomizedSubmitLabel() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		_mockDDMFormInstance(ddmFormInstanceSettings);

		String submitLabel = "Enviar Personalizado";

		Mockito.when(
			ddmFormInstanceSettings.submitLabel()
		).thenReturn(
			JSONUtil.put(
				_DEFAULT_LANGUAGE_ID, submitLabel
			).toString()
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertEquals(
			submitLabel, ddmFormDisplayContext.getSubmitLabel());
	}

	@Test
	public void testGetDDMFormContext() throws Exception {
		ThemeDisplay themeDisplay = _mockThemeDisplay(false);

		_mockHttpServletRequest2.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		DDMFormDisplayContext ddmFormDisplayContext = Mockito.spy(
			_createDDMFormDisplayContext());

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			"Select", RandomTestUtil.randomString(),
			DDMFormFieldTypeConstants.SELECT, FieldConstants.STRING, true,
			false, false);

		ddmFormField.setProperty("dataSourceType", "data-provider");

		ddmForm.addDDMFormField(ddmFormField);

		Mockito.doReturn(
			ddmForm
		).when(
			ddmFormDisplayContext
		).getDDMForm(
			Mockito.any(DDMFormInstance.class)
		);

		DDMFormLayout ddmFormLayout = Mockito.mock(DDMFormLayout.class);

		Mockito.doReturn(
			ddmFormLayout
		).when(
			ddmFormDisplayContext
		).getDDMFormLayout(
			Mockito.any(DDMFormInstance.class)
		);

		DDMFormInstance ddmFormInstance = _mockDDMFormInstance(
			Mockito.mock(DDMFormInstanceSettings.class));

		Mockito.doReturn(
			ddmFormInstance
		).when(
			ddmFormDisplayContext
		).getFormInstance();

		Mockito.doReturn(
			themeDisplay
		).when(
			ddmFormDisplayContext
		).getThemeDisplay();

		Mockito.doReturn(
			true
		).when(
			ddmFormDisplayContext
		).hasAddFormInstanceRecordPermission();

		Mockito.doReturn(
			true
		).when(
			ddmFormDisplayContext
		).hasValidStorageType(
			ddmFormInstance
		);

		DDMFormFieldOptions actualDDMFormFieldOptions =
			(DDMFormFieldOptions)ddmFormField.getProperty("options");

		Assert.assertTrue(
			SetUtil.isEmpty(actualDDMFormFieldOptions.getOptionsValues()));

		DDMFormFieldOptions expectedDDMFormFieldOptions =
			new DDMFormFieldOptions();

		expectedDDMFormFieldOptions.addOptionLabel(
			RandomTestUtil.randomString(), LocaleUtil.US,
			RandomTestUtil.randomString());
		expectedDDMFormFieldOptions.addOptionLabel(
			RandomTestUtil.randomString(), LocaleUtil.US,
			RandomTestUtil.randomString());

		Mockito.when(
			_ddmFormFieldOptionsFactory.create(
				Mockito.eq(ddmFormField),
				Mockito.any(DDMFormFieldRenderingContext.class))
		).thenReturn(
			expectedDDMFormFieldOptions
		);

		ddmFormDisplayContext.getDDMFormContext();

		actualDDMFormFieldOptions =
			(DDMFormFieldOptions)ddmFormField.getProperty("options");

		Assert.assertFalse(
			SetUtil.isEmpty(actualDDMFormFieldOptions.getOptionsValues()));

		Assert.assertEquals(
			expectedDDMFormFieldOptions, actualDDMFormFieldOptions);
	}

	@Test
	public void testGetFormInstanceId() throws Exception {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		long ddmFormInstanceId = RandomTestUtil.randomLong();

		Mockito.when(
			ddmFormInstance.getFormInstanceId()
		).thenReturn(
			ddmFormInstanceId
		);

		Mockito.when(
			_ddmFormInstanceLocalService.getFormInstanceByStructureId(
				Mockito.anyLong())
		).thenReturn(
			ddmFormInstance
		);

		_prefsParamUtilMockedStatic.when(
			() -> PrefsParamUtil.getString(
				_renderRequest.getPreferences(), _renderRequest,
				"ddmStructureExternalReferenceCode")
		).thenReturn(
			RandomTestUtil.randomString()
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext(_renderRequest);

		Assert.assertEquals(
			ddmFormInstanceId, ddmFormDisplayContext.getFormInstanceId());

		_prefsParamUtilMockedStatic.when(
			() -> PrefsParamUtil.getString(
				_renderRequest.getPreferences(), _renderRequest,
				"ddmStructureExternalReferenceCode")
		).thenReturn(
			StringPool.BLANK
		);

		ddmFormInstanceId = RandomTestUtil.randomLong();

		_prefsParamUtilMockedStatic.when(
			() -> PrefsParamUtil.getLong(
				_renderRequest.getPreferences(), _renderRequest,
				"formInstanceId")
		).thenReturn(
			ddmFormInstanceId
		);

		ddmFormDisplayContext = _createDDMFormDisplayContext(_renderRequest);

		Assert.assertEquals(
			ddmFormInstanceId, ddmFormDisplayContext.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWithMissingSettings() throws Exception {
		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		DDMFormInstance ddmFormInstance = Mockito.spy(
			new DDMFormInstanceImpl());

		String expectedSettings = StringUtil.randomString();

		ddmFormInstance.setSettings(expectedSettings);

		Mockito.when(
			_ddmFormInstanceLocalService.fetchFormInstance(Mockito.anyLong())
		).thenReturn(
			ddmFormInstance
		);

		Mockito.when(
			_ddmFormInstanceVersion.getSettings()
		).thenReturn(
			StringPool.BLANK
		);

		ddmFormInstance = ddmFormDisplayContext.getFormInstance();

		Assert.assertThat(
			ddmFormInstance.getSettings(), CoreMatchers.is(expectedSettings));
	}

	@Test
	public void testGetLimitToOneSubmissionPerUserMap() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		_mockDDMFormInstance(ddmFormInstanceSettings);

		String limitToOneSubmissionPerUserBody = RandomTestUtil.randomString();

		Mockito.when(
			ddmFormInstanceSettings.limitToOneSubmissionPerUserBody()
		).thenReturn(
			JSONUtil.put(
				_DEFAULT_LANGUAGE_ID, limitToOneSubmissionPerUserBody
			).toString()
		);

		String limitToOneSubmissionPerUserHeader =
			RandomTestUtil.randomString();

		Mockito.when(
			ddmFormInstanceSettings.limitToOneSubmissionPerUserHeader()
		).thenReturn(
			JSONUtil.put(
				_DEFAULT_LANGUAGE_ID, limitToOneSubmissionPerUserHeader
			).toString()
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Map<String, String> limitToOneSubmissionPerUserMap =
			ddmFormDisplayContext.getLimitToOneSubmissionPerUserMap();

		Assert.assertEquals(
			limitToOneSubmissionPerUserBody,
			limitToOneSubmissionPerUserMap.get(
				"limitToOneSubmissionPerUserBody"));
		Assert.assertEquals(
			limitToOneSubmissionPerUserHeader,
			limitToOneSubmissionPerUserMap.get(
				"limitToOneSubmissionPerUserHeader"));
	}

	@Test
	public void testGetLocale() throws PortalException {
		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getParameter(Mockito.eq("defaultLanguageId"))
		).thenReturn(
			"pt_BR"
		);

		Locale defaultLocale = LocaleUtil.US;
		Locale expectedLocale = LocaleUtil.BRAZIL;

		DDMForm ddmForm = _createDDMForm(
			new HashSet<>(Arrays.asList(defaultLocale, expectedLocale)),
			defaultLocale);

		Assert.assertEquals(
			expectedLocale,
			ddmFormDisplayContext.getLocale(httpServletRequest, ddmForm));
	}

	@Test
	public void testGetSubmitLabel() throws Exception {
		_mockDDMFormInstance(Mockito.mock(DDMFormInstanceSettings.class));

		String submitLabel = "Submit";

		_mockLanguageGet("submit-form", submitLabel);

		_mockWorkflowDefinitionLinkLocalService(false);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertEquals(
			submitLabel, ddmFormDisplayContext.getSubmitLabel());
	}

	@Test
	public void testGetSubmitLabelWithWorkflow() throws Exception {
		_mockDDMFormInstance(Mockito.mock(DDMFormInstanceSettings.class));

		String submitLabel = "Submit For Workflow";

		_mockLanguageGet("submit-for-workflow", submitLabel);

		_mockWorkflowDefinitionLinkLocalService(true);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertEquals(
			submitLabel, ddmFormDisplayContext.getSubmitLabel());
	}

	@Test
	public void testIsFormAvailableForGuest() throws Exception {
		DDMFormInstance ddmFormInstance = _mockDDMFormInstance();

		Mockito.when(
			_ddmFormInstanceLocalService.fetchFormInstance(Mockito.anyLong())
		).thenReturn(
			ddmFormInstance
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertFalse(ddmFormDisplayContext.isFormAvailable());
	}

	@Test
	public void testIsFormAvailableForLoggedUser() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		Mockito.when(
			ddmFormInstanceSettings.published()
		).thenReturn(
			true
		);

		DDMFormInstance ddmFormInstance = _mockDDMFormInstance(
			ddmFormInstanceSettings);

		Mockito.when(
			_ddmFormInstanceLocalService.fetchFormInstance(Mockito.anyLong())
		).thenReturn(
			ddmFormInstance
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertTrue(ddmFormDisplayContext.isFormAvailable());
	}

	@Test
	public void testIsSharedFormWithoutPortletSession() throws Exception {
		RenderRequest renderRequest = _mockRenderRequest();

		Assert.assertNull(renderRequest.getPortletSession(false));

		DDMFormDisplayContext createDDMFormDisplayContext =
			_createDDMFormDisplayContext(renderRequest);

		Assert.assertTrue(createDDMFormDisplayContext.isFormShared());
	}

	@Test
	public void testIsSharedFormWithPortletSession() throws Exception {
		RenderRequest renderRequest = _mockRenderRequest();

		PortletSession portletSession = renderRequest.getPortletSession(true);

		Assert.assertNotNull(portletSession);

		portletSession.setAttribute("shared", Boolean.TRUE);

		DDMFormDisplayContext createDDMFormDisplayContext =
			_createDDMFormDisplayContext(renderRequest);

		Assert.assertTrue(createDDMFormDisplayContext.isFormShared());
	}

	@Test
	public void testIsSharedURL() throws Exception {
		DDMFormDisplayContext ddmFormDisplayContext = Mockito.spy(
			_createDDMFormDisplayContext());

		Mockito.doReturn(
			123L
		).when(
			ddmFormDisplayContext
		).getFormInstanceId();

		Mockito.doReturn(
			_mockThemeDisplay(false)
		).when(
			ddmFormDisplayContext
		).getThemeDisplay();

		Assert.assertTrue(ddmFormDisplayContext.isSharedURL());
	}

	@Test
	public void testIsShowIconInEditMode() throws Exception {
		_mockHttpServletRequest1.addParameter("p_l_mode", Constants.EDIT);

		DDMFormDisplayContext ddmFormDisplayContext = _createSpy(
			false, false, false);

		Assert.assertFalse(ddmFormDisplayContext.isShowConfigurationIcon());
	}

	@Test
	public void testIsShowIconInPreview() throws Exception {
		DDMFormDisplayContext ddmFormDisplayContext = _createSpy(
			false, true, false);

		Assert.assertFalse(ddmFormDisplayContext.isShowConfigurationIcon());
	}

	@Test
	public void testIsShowIconWithPermission() throws Exception {
		_portletPermissionUtilMockedStatic = Mockito.mockStatic(
			PortletPermissionUtil.class);

		_portletPermissionUtilMockedStatic.when(
			() -> PortletPermissionUtil.contains(
				Mockito.any(PermissionChecker.class), Mockito.any(Layout.class),
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			true
		);

		DDMFormDisplayContext ddmFormDisplayContext = _createSpy(
			false, false, true);

		Assert.assertTrue(ddmFormDisplayContext.isShowConfigurationIcon());

		_portletPermissionUtilMockedStatic.close();
	}

	@Test
	public void testIsShowIconWithSharedForm() throws Exception {
		DDMFormDisplayContext ddmFormDisplayContext = _createSpy(
			true, false, true);

		Assert.assertFalse(ddmFormDisplayContext.isShowConfigurationIcon());
	}

	@Test
	public void testIsShowPartialResultsToRespondents() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		_mockDDMFormInstance(ddmFormInstanceSettings);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Assert.assertFalse(
			ddmFormDisplayContext.isShowPartialResultsToRespondents());

		Mockito.when(
			ddmFormInstanceSettings.showPartialResultsToRespondents()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			ddmFormDisplayContext.isShowPartialResultsToRespondents());
	}

	@Test
	public void testIsShowSuccessPage() throws Exception {
		_mockDDMFormInstance(Mockito.mock(DDMFormInstanceSettings.class));

		RenderRequest renderRequest = _mockRenderRequest();

		SessionMessages.add(renderRequest, "formInstanceRecordAdded");

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext(renderRequest);

		Assert.assertTrue(ddmFormDisplayContext.isShowSuccessPage());
	}

	@Test
	public void testIsShowSuccessPageWithRedirectURL() throws Exception {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		Mockito.when(
			ddmFormInstanceSettings.redirectURL()
		).thenReturn(
			"http://localhost:8080/web/forms/shared/-/form/123"
		);

		_mockDDMFormInstance(ddmFormInstanceSettings);

		RenderRequest renderRequest = _mockRenderRequest();

		SessionMessages.add(renderRequest, "formInstanceRecordAdded");

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext(renderRequest);

		Assert.assertFalse(ddmFormDisplayContext.isShowSuccessPage());
	}

	@Test
	public void testSubmissionLimitReachedDefaultMessage() throws Exception {
		String limitToOneSubmissionPerUserBody =
			"You can fill out this form only once. Contact the owner of the " +
				"form if you think this is a mistake.";
		String limitToOneSubmissionPerUserHeader =
			"You have already responded.";

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq(
					"you-can-fill-out-this-form-only-once.-contact-the-owner-" +
						"of-the-form-if-you-think-this-is-a-mistake"))
		).thenReturn(
			limitToOneSubmissionPerUserBody
		);

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("you-have-already-responded"))
		).thenReturn(
			limitToOneSubmissionPerUserHeader
		);

		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		_mockDDMFormInstance(ddmFormInstanceSettings);

		Mockito.when(
			ddmFormInstanceSettings.limitToOneSubmissionPerUserBody()
		).thenReturn(
			JSONUtil.put(
				_DEFAULT_LANGUAGE_ID, StringPool.BLANK
			).toString()
		);

		Mockito.when(
			ddmFormInstanceSettings.limitToOneSubmissionPerUserHeader()
		).thenReturn(
			JSONUtil.put(
				_DEFAULT_LANGUAGE_ID, StringPool.BLANK
			).toString()
		);

		DDMFormDisplayContext ddmFormDisplayContext =
			_createDDMFormDisplayContext();

		Map<String, String> limitToOneSubmissionPerUserMap =
			ddmFormDisplayContext.getLimitToOneSubmissionPerUserMap();

		Assert.assertEquals(
			limitToOneSubmissionPerUserBody,
			limitToOneSubmissionPerUserMap.get(
				"limitToOneSubmissionPerUserBody"));
		Assert.assertEquals(
			limitToOneSubmissionPerUserHeader,
			limitToOneSubmissionPerUserMap.get(
				"limitToOneSubmissionPerUserHeader"));
	}

	private DDMForm _createDDMForm(
		Set<Locale> availableLocales, Locale locale) {

		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(availableLocales);

		DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
			new DDMFormSuccessPageSettings();

		ddmFormSuccessPageSettings.setEnabled(true);

		ddmForm.setDDMFormSuccessPageSettings(ddmFormSuccessPageSettings);

		ddmForm.setDefaultLocale(locale);

		return ddmForm;
	}

	private DDMFormDisplayContext _createDDMFormDisplayContext()
		throws PortalException {

		return _createDDMFormDisplayContext(_mockRenderRequest());
	}

	private DDMFormDisplayContext _createDDMFormDisplayContext(
			RenderRequest renderRequest)
		throws PortalException {

		return new DDMFormDisplayContext(
			_ddmFormFieldOptionsFactory,
			Mockito.mock(DDMFormFieldTypeServicesRegistry.class),
			_ddmFormInstanceLocalService,
			Mockito.mock(DDMFormInstanceRecordService.class),
			Mockito.mock(DDMFormInstanceRecordVersionLocalService.class),
			_ddmFormInstanceService, _mockDDMFormInstanceVersionLocalService(),
			Mockito.mock(DDMFormRenderer.class),
			Mockito.mock(DDMFormValuesFactory.class),
			Mockito.mock(DDMFormValuesMerger.class), _ddmFormWebConfiguration,
			Mockito.mock(DDMStorageAdapterRegistry.class),
			_ddmStructureLocalService, _groupLocalService,
			new JSONFactoryImpl(), null, null, null, null, null,
			Mockito.mock(Portal.class), renderRequest, new MockRenderResponse(),
			Mockito.mock(RoleLocalService.class),
			Mockito.mock(UserLocalService.class),
			_workflowDefinitionLinkLocalService);
	}

	private DDMFormDisplayContext _createSpy(
			boolean formShared, boolean preview, boolean sharedURL)
		throws Exception {

		DDMFormDisplayContext ddmFormDisplayContext = Mockito.spy(
			_createDDMFormDisplayContext());

		Mockito.doReturn(
			formShared
		).when(
			ddmFormDisplayContext
		).isFormShared();

		Mockito.doReturn(
			preview
		).when(
			ddmFormDisplayContext
		).isPreview();

		Mockito.doReturn(
			sharedURL
		).when(
			ddmFormDisplayContext
		).isSharedURL();

		return ddmFormDisplayContext;
	}

	private DDMFormInstance _mockDDMFormInstance() throws Exception {
		DDMFormInstance formInstance = Mockito.mock(DDMFormInstance.class);

		DDMFormInstanceSettings formInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		Mockito.when(
			formInstance.getSettingsModel()
		).thenReturn(
			formInstanceSettings
		);

		return formInstance;
	}

	private DDMFormInstance _mockDDMFormInstance(
			DDMFormInstanceSettings ddmFormInstanceSettings)
		throws Exception {

		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		Mockito.when(
			ddmFormInstance.getSettingsModel()
		).thenReturn(
			ddmFormInstanceSettings
		);

		DDMStructure ddmStructure = _mockDDMStructure();

		Mockito.when(
			ddmFormInstance.getStructure()
		).thenReturn(
			ddmStructure
		);

		Mockito.when(
			_ddmFormInstanceLocalService.fetchFormInstance(Mockito.anyLong())
		).thenReturn(
			ddmFormInstance
		);

		return ddmFormInstance;
	}

	private DDMFormInstanceSettings
			_mockDDMFormInstanceSettingsAutosaveWithNonguestUser()
		throws Exception {

		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		Mockito.when(
			ddmFormInstance.getSettingsModel()
		).thenReturn(
			ddmFormInstanceSettings
		);

		Mockito.when(
			_ddmFormInstanceLocalService.fetchFormInstance(Mockito.anyLong())
		).thenReturn(
			ddmFormInstance
		);

		return ddmFormInstanceSettings;
	}

	private DDMFormInstanceVersionLocalService
			_mockDDMFormInstanceVersionLocalService()
		throws PortalException {

		Mockito.when(
			_ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
				Mockito.anyLong(), Mockito.anyInt())
		).thenReturn(
			_ddmFormInstanceVersion
		);

		return _ddmFormInstanceVersionLocalService;
	}

	private DDMStructure _mockDDMStructure() throws Exception {
		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		Locale defaultLocale = LocaleUtil.fromLanguageId(_DEFAULT_LANGUAGE_ID);

		DDMForm ddmForm = _createDDMForm(
			new HashSet<>(Arrays.asList(defaultLocale)), defaultLocale);

		Mockito.when(
			ddmStructure.getDDMForm()
		).thenReturn(
			ddmForm
		);

		return ddmStructure;
	}

	private void _mockLanguageGet(String key, String value) {
		Mockito.when(
			_language.get(Mockito.any(ResourceBundle.class), Mockito.eq(key))
		).thenReturn(
			value
		);
	}

	private RenderRequest _mockRenderRequest() {
		return _mockRenderRequest(false);
	}

	private RenderRequest _mockRenderRequest(boolean guestUser) {
		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		mockRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _mockThemeDisplay(guestUser));
		mockRenderRequest.setParameter("shared", Boolean.TRUE.toString());

		return mockRenderRequest;
	}

	private ThemeDisplay _mockThemeDisplay(boolean guestUser) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			Mockito.mock(Layout.class)
		);

		Mockito.when(
			themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			portletDisplay.getPortletResource()
		).thenReturn(
			null
		);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			portletDisplay
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			"http://localhost:8080/web/forms/shared?form=123"
		);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.isGuestUser()
		).thenReturn(
			guestUser
		);

		Mockito.when(
			themeDisplay.getUser()
		).thenReturn(
			user
		);

		Mockito.when(
			themeDisplay.getUserId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			!guestUser
		);

		return themeDisplay;
	}

	private void _mockWorkflowDefinitionLinkLocalService(
		boolean hasWorkflowDefinitionLink) {

		Mockito.when(
			_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyLong())
		).thenReturn(
			hasWorkflowDefinitionLink
		);
	}

	private void _setUpDDMStructureLocalService() throws PortalException {
		Mockito.when(
			_ddmStructureLocalService.getStructureByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			Mockito.mock(DDMStructure.class)
		);
	}

	private void _setUpGroupLocalService() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			group
		);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.getLanguageId(Mockito.any(Locale.class))
		).thenReturn(
			_DEFAULT_LANGUAGE_ID
		);

		Mockito.when(
			_language.getLanguageId(Mockito.eq(_mockHttpServletRequest2))
		).thenReturn(
			_DEFAULT_LANGUAGE_ID
		);

		_whenLanguageIsAvailableLocale(LocaleUtil.BRAZIL);
		_whenLanguageIsAvailableLocale(LocaleUtil.SPAIN);

		languageUtil.setLanguage(_language);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		portalUtil.setPortal(portal);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any(RenderRequest.class))
		).thenReturn(
			_mockHttpServletRequest2
		);

		Mockito.when(
			portal.getLiferayPortletRequest(Mockito.any(RenderRequest.class))
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
		);

		Mockito.when(
			portal.getOriginalServletRequest(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_mockHttpServletRequest1
		);
	}

	private void _setUpPrefsParamUtilMockedStatic() {
		_prefsParamUtilMockedStatic.when(
			() -> PrefsParamUtil.getString(
				_renderRequest.getPreferences(), _renderRequest,
				"groupExternalReferenceCode")
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpResourceBundleUtil() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

	private void _whenLanguageIsAvailableLocale(Locale locale) {
		Mockito.when(
			_language.isAvailableLocale(Mockito.eq(locale))
		).thenReturn(
			true
		);

		Mockito.when(
			_language.isAvailableLocale(
				Mockito.eq(LocaleUtil.toLanguageId(locale)))
		).thenReturn(
			true
		);
	}

	private static final String _DEFAULT_LANGUAGE_ID = "es_ES";

	private final DDMFormFieldOptionsFactory _ddmFormFieldOptionsFactory =
		Mockito.mock(DDMFormFieldOptionsFactory.class);
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService =
		Mockito.mock(DDMFormInstanceLocalService.class);
	private final DDMFormInstanceService _ddmFormInstanceService = Mockito.mock(
		DDMFormInstanceService.class);
	private final DDMFormInstanceVersion _ddmFormInstanceVersion = Mockito.mock(
		DDMFormInstanceVersion.class);
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService = Mockito.mock(
			DDMFormInstanceVersionLocalService.class);
	private final DDMFormWebConfiguration _ddmFormWebConfiguration =
		Mockito.mock(DDMFormWebConfiguration.class);
	private final DDMStructureLocalService _ddmStructureLocalService =
		Mockito.mock(DDMStructureLocalService.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final Language _language = Mockito.mock(Language.class);
	private final MockHttpServletRequest _mockHttpServletRequest1 =
		new MockHttpServletRequest();
	private final MockHttpServletRequest _mockHttpServletRequest2 =
		new MockHttpServletRequest();
	private MockedStatic<PortletPermissionUtil>
		_portletPermissionUtilMockedStatic;
	private final MockedStatic<PrefsParamUtil> _prefsParamUtilMockedStatic =
		Mockito.mockStatic(PrefsParamUtil.class);
	private final RenderRequest _renderRequest = _mockRenderRequest();
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService = Mockito.mock(
			WorkflowDefinitionLinkLocalService.class);

}