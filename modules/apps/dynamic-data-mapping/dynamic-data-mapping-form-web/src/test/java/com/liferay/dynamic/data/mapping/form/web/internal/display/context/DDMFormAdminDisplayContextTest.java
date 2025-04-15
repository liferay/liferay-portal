/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextFactory;
import com.liferay.dynamic.data.mapping.form.builder.internal.context.DDMFormContextToDDMFormValues;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializer;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRegistry;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsImpl;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 */
public class DDMFormAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());
	}

	@Before
	public void setUp() throws Exception {
		_setUpPortalUtil();

		_setUpLanguageUtil();
		_setUpResourceBundleLoaderUtil();

		_setUpDDMFormDisplayContext();
	}

	@Test
	public void testGetDDMFormRenderingContextDDMFormValues() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.US);

		_setRenderRequestParamenter(
			"serializedSettingsContext",
			_read("ddm-form-settings-values.json"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			DDMFormTestUtil.createDDMForm("workflowDefinition"));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"eBvF8zup", "workflowDefinition",
				new UnlocalizedValue("[\"Single Approver\"]")));

		Assert.assertEquals(
			ddmFormValues,
			_ddmFormAdminDisplayContext.
				getDDMFormRenderingContextDDMFormValues());
	}

	@Test
	public void testGetPublishedFormURL() throws Exception {
		Assert.assertEquals(
			getSharedFormURL() + _SHARED_FORM_INSTANCE_ID,
			_ddmFormAdminDisplayContext.getPublishedFormURL(
				_mockDDMFormInstance(_SHARED_FORM_INSTANCE_ID, true, true)));
		Assert.assertEquals(
			getSharedFormURL() + _SHARED_FORM_INSTANCE_ID,
			_ddmFormAdminDisplayContext.getPublishedFormURL(
				_mockDDMFormInstance(_SHARED_FORM_INSTANCE_ID, true, false)));
		Assert.assertEquals(
			StringPool.BLANK,
			_ddmFormAdminDisplayContext.getPublishedFormURL(
				_mockDDMFormInstance(_SHARED_FORM_INSTANCE_ID, false, false)));
	}

	@Test
	public void testGetSharedFormURL() {
		Assert.assertEquals(
			getSharedFormURL(), _ddmFormAdminDisplayContext.getSharedFormURL());
	}

	@Test
	public void testGetSharedFormURLForSharedFormInstance() {
		_setRenderRequestParamenter(
			"formInstanceId", String.valueOf(_SHARED_FORM_INSTANCE_ID));

		Assert.assertEquals(
			getSharedFormURL(), _ddmFormAdminDisplayContext.getSharedFormURL());
	}

	@Test
	public void testIsShowPartialResultsToRespondents() throws Exception {
		Assert.assertFalse(
			_ddmFormAdminDisplayContext.isShowPartialResultsToRespondents(
				null));

		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		DDMFormInstance ddmFormInstance = _mockDDMFormInstance(
			ddmFormInstanceSettings);

		Assert.assertFalse(
			_ddmFormAdminDisplayContext.isShowPartialResultsToRespondents(
				ddmFormInstance));

		Mockito.when(
			ddmFormInstanceSettings.showPartialResultsToRespondents()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_ddmFormAdminDisplayContext.isShowPartialResultsToRespondents(
				ddmFormInstance));
	}

	protected String getSharedFormURL() {
		return StringBundler.concat(
			_PORTAL_URL, _PUBLIC_FRIENDLY_URL_PATH, _GROUP_FRIENDLY_URL_PATH,
			_PUBLIC_PAGE_FRIENDLY_URL_PATH, _FORM_APPLICATION_PATH);
	}

	private DDMFormContextToDDMFormValues _getDDMFormContextToDDMFormValues() {
		DDMFormContextToDDMFormValues ddmFormContextToDDMFormValues =
			new DDMFormContextToDDMFormValues();

		ReflectionTestUtil.setFieldValue(
			ddmFormContextToDDMFormValues, "jsonFactory",
			new JSONFactoryImpl());

		return ddmFormContextToDDMFormValues;
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

		return ddmFormInstance;
	}

	private DDMFormInstance _mockDDMFormInstance(
			long formInstanceId, boolean requireAuthentication)
		throws Exception {

		DDMFormInstance formInstance = Mockito.mock(DDMFormInstance.class);

		Mockito.when(
			formInstance.getFormInstanceId()
		).thenReturn(
			formInstanceId
		);

		DDMFormInstanceSettings settings = _mockDDMFormInstanceSettings(
			requireAuthentication);

		Mockito.when(
			formInstance.getSettingsModel()
		).thenReturn(
			settings
		);

		return formInstance;
	}

	private DDMFormInstance _mockDDMFormInstance(
			long formInstanceId, boolean published,
			boolean requireAuthentication)
		throws Exception {

		DDMFormInstance ddmFormInstance = _mockDDMFormInstance(
			formInstanceId, requireAuthentication);

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		Mockito.when(
			ddmFormInstanceSettings.published()
		).thenReturn(
			published
		);

		return ddmFormInstance;
	}

	private DDMFormInstanceService _mockDDMFormInstanceService()
		throws Exception {

		DDMFormInstanceService ddmFormInstanceService = Mockito.mock(
			DDMFormInstanceService.class);

		DDMFormInstance sharedDDMFormInstance = _mockDDMFormInstance(
			_SHARED_FORM_INSTANCE_ID, false);

		Mockito.when(
			ddmFormInstanceService.fetchFormInstance(
				Mockito.eq(_SHARED_FORM_INSTANCE_ID))
		).thenReturn(
			sharedDDMFormInstance
		);

		return ddmFormInstanceService;
	}

	private DDMFormInstanceSettings _mockDDMFormInstanceSettings(
		boolean requireAuthentication) {

		DDMFormInstanceSettings settings = Mockito.mock(
			DDMFormInstanceSettings.class);

		Mockito.when(
			settings.requireAuthentication()
		).thenReturn(
			requireAuthentication
		);

		return settings;
	}

	private HttpServletRequest _mockHttpServletRequest() {
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		return httpServletRequest;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPathFriendlyURLPublic()
		).thenReturn(
			_PUBLIC_FRIENDLY_URL_PATH
		);

		Mockito.when(
			themeDisplay.getPortalURL()
		).thenReturn(
			_PORTAL_URL
		);

		return themeDisplay;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getResourceAsStream("dependencies/" + fileName));
	}

	private void _setRenderRequestParamenter(String parameter, String value) {
		Mockito.when(
			_renderRequest.getParameter(Mockito.eq(parameter))
		).thenReturn(
			value
		);
	}

	private void _setUpDDMFormDisplayContext() throws Exception {
		_renderRequest = Mockito.mock(RenderRequest.class);

		_ddmFormAdminDisplayContext = new DDMFormAdminDisplayContext(
			_renderRequest, Mockito.mock(RenderResponse.class),
			Mockito.mock(DDMFormBuilderContextFactory.class),
			Mockito.mock(DDMFormBuilderSettingsRetriever.class),
			_getDDMFormContextToDDMFormValues(),
			Mockito.mock(DDMFormFieldTypeServicesRegistry.class),
			Mockito.mock(DDMFormFieldTypesSerializer.class),
			Mockito.mock(DDMFormInstanceLocalService.class),
			Mockito.mock(DDMFormInstanceRecordLocalService.class),
			Mockito.mock(DDMFormInstanceRecordWriterRegistry.class),
			_mockDDMFormInstanceService(),
			Mockito.mock(DDMFormInstanceVersionLocalService.class),
			Mockito.mock(DDMFormRenderer.class),
			Mockito.mock(DDMFormTemplateContextFactory.class),
			Mockito.mock(DDMFormValuesFactory.class),
			Mockito.mock(DDMFormValuesMerger.class),
			Mockito.mock(DDMFormWebConfiguration.class),
			Mockito.mock(DDMStorageAdapterRegistry.class),
			Mockito.mock(DDMStructureLocalService.class),
			Mockito.mock(DDMStructureService.class),
			Mockito.mock(JSONFactory.class), Mockito.mock(NPMResolver.class),
			null, Mockito.mock(Portal.class));
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		HttpServletRequest httpServletRequest = _mockHttpServletRequest();

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			httpServletRequest
		);

		portalUtil.setPortal(portal);
	}

	private void _setUpResourceBundleLoaderUtil() {
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

	private static final String _FORM_APPLICATION_PATH =
		Portal.FRIENDLY_URL_SEPARATOR + "form/";

	private static final String _GROUP_FRIENDLY_URL_PATH = "/forms";

	private static final String _PORTAL_URL = "http://localhost:9999";

	private static final String _PUBLIC_FRIENDLY_URL_PATH = "/web";

	private static final String _PUBLIC_PAGE_FRIENDLY_URL_PATH = "/shared";

	private static final long _SHARED_FORM_INSTANCE_ID =
		RandomTestUtil.randomLong();

	private DDMFormAdminDisplayContext _ddmFormAdminDisplayContext;
	private RenderRequest _renderRequest;

}