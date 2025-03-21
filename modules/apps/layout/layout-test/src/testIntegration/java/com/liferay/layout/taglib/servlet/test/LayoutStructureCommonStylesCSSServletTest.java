/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class LayoutStructureCommonStylesCSSServletTest {

	public static final String COMMON_CSS_STYLE =
		".lfr-layout-structure-item-container {padding: 0;} " +
			".lfr-layout-structure-item-row {overflow: hidden;} " +
				".portlet-borderless .portlet-content {padding: 0;}";

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testDoesNotRender() throws Exception {
		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _layout.getPlid(),
				_read("layout_structure_container_fixed.json"));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(_getHttpServletRequest(), mockHttpServletResponse);

		Assert.assertEquals(
			_normalize(mockHttpServletResponse.getContentAsString()),
			_normalize(
				COMMON_CSS_STYLE +
					_read("expected_style_container_fixed.css")));
	}

	@Test
	public void testRenderCommonStyles() throws Exception {
		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _layout.getPlid(),
				_read("layout_structure.json"));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(_getHttpServletRequest(), mockHttpServletResponse);

		Assert.assertEquals(
			_normalize(mockHttpServletResponse.getContentAsString()),
			_normalize(COMMON_CSS_STYLE + _read("expected_style.css")));
	}

	@Test
	public void testRenderCommonStylesWithCustomCSS() throws Exception {
		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _layout.getPlid(),
				_read("layout_structure_with_custom_css.json"));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(_getHttpServletRequest(), mockHttpServletResponse);

		Assert.assertEquals(
			_normalize(mockHttpServletResponse.getContentAsString()),
			_normalize(
				COMMON_CSS_STYLE +
					_read("expected_style_with_custom_css.css")));
	}

	@Test
	public void testRenderCommonStylesWithResponsive() throws Exception {
		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _layout.getPlid(),
				_read("layout_structure_with_responsive_styles.json"));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(_getHttpServletRequest(), mockHttpServletResponse);

		Assert.assertEquals(
			_normalize(mockHttpServletResponse.getContentAsString()),
			_normalize(
				COMMON_CSS_STYLE +
					_read("expected_style_with_responsive_styles.css")));
	}

	@Test
	public void testRenderEmptyTagWhenItDoesNotHaveStyles() throws Exception {
		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), _layout.getPlid(),
				_read("layout_structure_without_styles.json"));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(_getHttpServletRequest(), mockHttpServletResponse);

		Assert.assertEquals(
			_normalize(mockHttpServletResponse.getContentAsString()),
			_normalize(COMMON_CSS_STYLE));
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		mockHttpServletRequest.setParameter(
			"plid", String.valueOf(_layout.getPlid()));
		mockHttpServletRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));

		ThemeDisplay themeDisplay = _getThemeDisplay();

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		themeDisplay.setLayout(_layout);
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private String _normalize(String value) {
		return value.replaceAll("[\n\t ]", StringPool.BLANK);
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/layout/taglib/servlet/taglib/test/dependencies/" +
				fileName);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.layout.taglib.internal.servlet.LayoutStructureCommonStylesCSSServlet"
	)
	private Servlet _servlet;

}