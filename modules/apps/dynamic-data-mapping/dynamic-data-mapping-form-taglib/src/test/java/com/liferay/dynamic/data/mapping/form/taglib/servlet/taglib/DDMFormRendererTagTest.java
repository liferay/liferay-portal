/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceVersionImpl;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pedro Queiroz
 */
public class DDMFormRendererTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_ddmFormInstanceLocalServiceUtilMockedStatic.close();

		_ddmFormInstanceRecordLocalServiceUtilMockedStatic.close();

		_ddmFormInstanceRecordVersionLocalServiceUtilMockedStatic.close();

		_ddmFormInstanceVersionLocalServiceUtilMockedStatic.close();

		_ddmFormValuesFactoryServiceRegistration.unregister();

		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		setUpDDMFormInstanceLocalService();
		setUpDDMFormInstancePermission();
		setUpDDMFormInstanceRecordLocalService();
		setUpDDMFormInstanceRecordVersionLocalService();
		setUpDDMFormInstanceVersionLocalService();
		setUpDDMFormValuesFactory();
		setUpHttpServletRequest();
		setUpLanguageUtil();
		setUpPortalUtil();
	}

	@Test
	public void testCreateDDMFormRenderingContext() {
		setDDMFormRendererTagInputs(1L, null, null, null, false, false);

		DDMForm ddmForm = new DDMForm();

		ddmForm.setDefaultLocale(LocaleUtil.US);

		DDMFormRenderingContext ddmFormRenderingContext =
			_ddmFormRendererTag.createDDMFormRenderingContext(ddmForm);

		Assert.assertNotNull(ddmFormRenderingContext.getContainerId());
		Assert.assertEquals(
			_ddmFormInstance.getGroupId(),
			ddmFormRenderingContext.getGroupId());
		Assert.assertNotNull(ddmFormRenderingContext.getHttpServletRequest());
		Assert.assertNotNull(ddmFormRenderingContext.getHttpServletResponse());
		Assert.assertEquals(LocaleUtil.US, ddmFormRenderingContext.getLocale());
		Assert.assertTrue(ddmFormRenderingContext.isViewMode());
		Assert.assertNotNull(ddmFormRenderingContext.getDDMFormValues());
		Assert.assertNotNull(ddmFormRenderingContext.getPortletNamespace());
		Assert.assertFalse(ddmFormRenderingContext.isReadOnly());
		Assert.assertFalse(ddmFormRenderingContext.isShowSubmitButton());
	}

	@Test
	public void testGetDDMFormDefaultLocaleWhenLocaleIsNotAvailable() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setDefaultLocale(LocaleUtil.BRAZIL);

		Assert.assertEquals(
			LocaleUtil.BRAZIL,
			_ddmFormRendererTag.getLocale(_httpServletRequest, ddmForm));
	}

	@Test
	public void testGetFormInstanceWhenFormInstanceRecordIdHasHigherPriority() {
		setDDMFormRendererTagInputs(1L, 2L, null, 4L);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(2L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWhenFormInstanceRecordVersionIdHasHigherPriority() {
		setDDMFormRendererTagInputs(1L, 2L, 3L, 4L);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(3L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWhenFormInstanceVersionIdHasHigherPriority() {
		setDDMFormRendererTagInputs(1L, null, null, 4L);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(4L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWithFormInstanceId() {
		setDDMFormRendererTagInputs(1L, null, null, null);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(1L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWithFormInstanceRecordId() {
		setDDMFormRendererTagInputs(null, 2L, null, null);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(2L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWithFormInstanceRecordVersionId() {
		setDDMFormRendererTagInputs(null, null, 3L, null);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(3L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetFormInstanceWithFormInstanceVersionId() {
		setDDMFormRendererTagInputs(null, null, null, 4L);

		DDMFormInstance ddmFormInstance =
			_ddmFormRendererTag.getDDMFormInstance();

		Assert.assertEquals(4L, ddmFormInstance.getFormInstanceId());
	}

	@Test
	public void testGetLocaleFromRequestWhenDDMFormIsNull() {
		Assert.assertEquals(
			LocaleUtil.US,
			_ddmFormRendererTag.getLocale(_httpServletRequest, null));
	}

	@Test
	public void testGetLocaleWhenLocaleIsAvailable() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setDefaultLocale(LocaleUtil.BRAZIL);
		ddmForm.setAvailableLocales(
			createAvailableLocales(LocaleUtil.BRAZIL, LocaleUtil.US));

		Assert.assertEquals(
			LocaleUtil.US,
			_ddmFormRendererTag.getLocale(_httpServletRequest, ddmForm));
	}

	@Test
	public void testGetRedirectURLWhenFormInstanceIsNull() {
		setDDMFormRendererTagInputs(null, null, null, null);

		Assert.assertEquals(
			StringPool.BLANK, _ddmFormRendererTag.getRedirectURL());
	}

	protected Set<Locale> createAvailableLocales(Locale... locales) {
		Set<Locale> availableLocales = new LinkedHashSet<>();

		for (Locale locale : locales) {
			availableLocales.add(locale);
		}

		return availableLocales;
	}

	protected void mockDDMFormInstance(long ddmFormInstanceId) {
		DDMFormInstanceImpl ddmFormInstanceImpl = new DDMFormInstanceImpl();

		ddmFormInstanceImpl.setFormInstanceId(ddmFormInstanceId);

		Mockito.when(
			DDMFormInstanceLocalServiceUtil.fetchFormInstance(
				Mockito.eq(ddmFormInstanceId))
		).thenReturn(
			ddmFormInstanceImpl
		);
	}

	protected void setDDMFormRendererTagInputs(
		Long ddmFormInstanceId, Long ddmFormInstanceRecordId,
		Long ddmFormInstanceRecordVersionId, Long ddmFormInstanceVersionId) {

		_ddmFormRendererTag.setDdmFormInstanceId(ddmFormInstanceId);
		_ddmFormRendererTag.setDdmFormInstanceRecordId(ddmFormInstanceRecordId);
		_ddmFormRendererTag.setDdmFormInstanceRecordVersionId(
			ddmFormInstanceRecordVersionId);
		_ddmFormRendererTag.setDdmFormInstanceVersionId(
			ddmFormInstanceVersionId);
	}

	protected void setDDMFormRendererTagInputs(
		Long ddmFormInstanceId, Long ddmFormInstanceRecordId,
		Long ddmFormInstanceRecordVersionId, Long ddmFormInstanceVersionId,
		Boolean showFormBasicInfo, Boolean showSubmitButton) {

		setDDMFormRendererTagInputs(
			ddmFormInstanceId, ddmFormInstanceRecordId,
			ddmFormInstanceRecordVersionId, ddmFormInstanceVersionId);

		_ddmFormRendererTag.setShowFormBasicInfo(showFormBasicInfo);
		_ddmFormRendererTag.setShowSubmitButton(showSubmitButton);
	}

	protected void setUpDDMFormInstanceLocalService() throws Exception {
		_ddmFormInstance = new DDMFormInstanceImpl();

		Mockito.when(
			DDMFormInstanceLocalServiceUtil.getService()
		).thenReturn(
			Mockito.mock(DDMFormInstanceLocalService.class)
		);

		mockDDMFormInstance(1L);
		mockDDMFormInstance(2L);
		mockDDMFormInstance(3L);
		mockDDMFormInstance(4L);
	}

	protected void setUpDDMFormInstancePermission() throws PortalException {
		ModelResourcePermission<DDMFormInstance>
			ddmFormInstanceModelResourcePermission = Mockito.mock(
				ModelResourcePermission.class);

		Mockito.when(
			ddmFormInstanceModelResourcePermission.contains(
				Mockito.nullable(PermissionChecker.class),
				Mockito.nullable(DDMFormInstance.class),
				Mockito.nullable(String.class))
		).thenReturn(
			true
		);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		bundleContext.registerService(
			ModelResourcePermission.class,
			ddmFormInstanceModelResourcePermission,
			MapUtil.singletonDictionary(
				"model.class.name",
				"com.liferay.dynamic.data.mapping.model.DDMFormInstance"));
	}

	protected void setUpDDMFormInstanceRecordLocalService() throws Exception {
		_ddmFormInstanceRecord = new DDMFormInstanceRecordImpl();

		_ddmFormInstanceRecord.setFormInstanceId(2L);

		Mockito.when(
			DDMFormInstanceRecordLocalServiceUtil.getService()
		).thenReturn(
			Mockito.mock(DDMFormInstanceRecordLocalService.class)
		);

		Mockito.when(
			DDMFormInstanceRecordLocalServiceUtil.fetchDDMFormInstanceRecord(
				Mockito.anyLong())
		).thenReturn(
			_ddmFormInstanceRecord
		);
	}

	protected void setUpDDMFormInstanceRecordVersionLocalService()
		throws Exception {

		_ddmFormInstanceRecordVersion = new DDMFormInstanceRecordVersionImpl();

		_ddmFormInstanceRecordVersion.setFormInstanceId(3L);

		Mockito.when(
			DDMFormInstanceRecordVersionLocalServiceUtil.getService()
		).thenReturn(
			Mockito.mock(DDMFormInstanceRecordVersionLocalService.class)
		);

		Mockito.when(
			DDMFormInstanceRecordVersionLocalServiceUtil.
				fetchDDMFormInstanceRecordVersion(Mockito.anyLong())
		).thenReturn(
			_ddmFormInstanceRecordVersion
		);
	}

	protected void setUpDDMFormInstanceVersionLocalService() throws Exception {
		_ddmFormInstanceVersion = new DDMFormInstanceVersionImpl();

		_ddmFormInstanceVersion.setFormInstanceId(4L);

		Mockito.when(
			DDMFormInstanceVersionLocalServiceUtil.getService()
		).thenReturn(
			Mockito.mock(DDMFormInstanceVersionLocalService.class)
		);

		Mockito.when(
			DDMFormInstanceVersionLocalServiceUtil.fetchDDMFormInstanceVersion(
				Mockito.anyLong())
		).thenReturn(
			_ddmFormInstanceVersion
		);
	}

	protected void setUpDDMFormValuesFactory() throws Exception {
		_ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			new DDMForm());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_ddmFormValuesFactoryServiceRegistration =
			bundleContext.registerService(
				DDMFormValuesFactory.class, _ddmFormValuesFactory, null);

		Mockito.when(
			_ddmFormValuesFactory.create(
				Mockito.any(HttpServletRequest.class),
				Mockito.any(DDMForm.class))
		).thenReturn(
			_ddmFormValues
		);
	}

	protected void setUpHttpServletRequest() throws IllegalAccessException {
		_httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE, new MockRenderResponse());
		_httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		ReflectionTestUtil.setFieldValue(
			_ddmFormRendererTag, "_httpServletRequest", _httpServletRequest);
	}

	protected void setUpLanguageUtil() {
		Mockito.when(
			_language.getLanguageId(Mockito.eq(_httpServletRequest))
		).thenReturn(
			"en_US"
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	protected void setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		portalUtil.setPortal(portal);

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any(RenderRequest.class))
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			portal.getHttpServletResponse(Mockito.any(RenderResponse.class))
		).thenReturn(
			new MockHttpServletResponse()
		);
	}

	private static final MockedStatic<DDMFormInstanceLocalServiceUtil>
		_ddmFormInstanceLocalServiceUtilMockedStatic = Mockito.mockStatic(
			DDMFormInstanceLocalServiceUtil.class);
	private static final MockedStatic<DDMFormInstanceRecordLocalServiceUtil>
		_ddmFormInstanceRecordLocalServiceUtilMockedStatic = Mockito.mockStatic(
			DDMFormInstanceRecordLocalServiceUtil.class);
	private static final MockedStatic
		<DDMFormInstanceRecordVersionLocalServiceUtil>
			_ddmFormInstanceRecordVersionLocalServiceUtilMockedStatic =
				Mockito.mockStatic(
					DDMFormInstanceRecordVersionLocalServiceUtil.class);
	private static final MockedStatic<DDMFormInstanceVersionLocalServiceUtil>
		_ddmFormInstanceVersionLocalServiceUtilMockedStatic =
			Mockito.mockStatic(DDMFormInstanceVersionLocalServiceUtil.class);
	private static ServiceRegistration<DDMFormValuesFactory>
		_ddmFormValuesFactoryServiceRegistration;
	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private DDMFormInstance _ddmFormInstance;
	private DDMFormInstanceRecord _ddmFormInstanceRecord;
	private DDMFormInstanceRecordVersion _ddmFormInstanceRecordVersion =
		Mockito.mock(DDMFormInstanceRecordVersion.class);
	private DDMFormInstanceVersion _ddmFormInstanceVersion;
	private final DDMFormRendererTag _ddmFormRendererTag =
		new DDMFormRendererTag();
	private DDMFormValues _ddmFormValues;
	private final DDMFormValuesFactory _ddmFormValuesFactory = Mockito.mock(
		DDMFormValuesFactory.class);
	private final HttpServletRequest _httpServletRequest =
		new MockHttpServletRequest();
	private final Language _language = Mockito.mock(Language.class);

}