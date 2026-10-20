/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;

import jakarta.servlet.http.HttpServletRequest;

import java.io.StringWriter;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class LocalizationSelectFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-109773")
	public void testRender() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<Translator> serviceRegistration =
			bundleContext.registerService(
				Translator.class, new TestTranslator(),
				MapUtil.singletonDictionary(
					"service.ranking", Integer.MAX_VALUE));

		try {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					null, null, null,
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(_layout.getPlid()),
					_layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, 0, "localization-select",
					FragmentConstants.TYPE_INPUT, _serviceContext);

			String content = _render(fragmentEntryLink, true);

			Assert.assertTrue(
				content, content.contains("\"autoTranslationEnabled\":true"));

			content = _render(fragmentEntryLink, false);

			Assert.assertTrue(
				content, content.contains("\"autoTranslationEnabled\":false"));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private String _render(
			FragmentEntryLink fragmentEntryLink, boolean signedIn)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setMode(FragmentEntryLinkConstants.VIEW);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			TilesUtil.DEFINITION,
			new Definition(StringPool.BLANK, new HashMap<>()));

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _layout);

		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setSignedIn(signedIn);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_serviceContext.setRequest(httpServletRequest);

		_fragmentRenderer.render(
			defaultFragmentRendererContext, httpServletRequest,
			new MockHttpServletResponse());

		ScriptData scriptData = (ScriptData)httpServletRequest.getAttribute(
			WebKeys.AUI_SCRIPT_DATA);

		StringWriter stringWriter = new StringWriter();

		scriptData.writeTo(stringWriter);

		return String.valueOf(stringWriter.getBuffer());
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.internal.renderer.LocalizationSelectFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	private static class TestTranslator implements Translator {

		@Override
		public boolean isAIAssisted() {
			return false;
		}

		@Override
		public boolean isEnabled(long companyId) {
			return true;
		}

		@Override
		public TranslatorPacket translate(TranslatorPacket translatorPacket) {
			return translatorPacket;
		}

	}

}