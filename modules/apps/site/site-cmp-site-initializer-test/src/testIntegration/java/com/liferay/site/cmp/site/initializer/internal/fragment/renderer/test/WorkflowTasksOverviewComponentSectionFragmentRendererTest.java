/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class WorkflowTasksOverviewComponentSectionFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = CMPTestUtil.getOrAddGroup(
			WorkflowTasksOverviewComponentSectionFragmentRendererTest.class);

		_themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()));
				setLocale(LocaleUtil.getDefault());
				setScopeGroupId(group.getGroupId());
				setUser(TestPropsValues.getUser());
			}
		};
	}

	@Test
	public void testGetProps() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		Map<String, Object> props = ReflectionTestUtil.invoke(
			_fragmentRenderer, "getProps",
			new Class<?>[] {
				FragmentRendererContext.class, HttpServletRequest.class
			},
			null, httpServletRequest);

		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
				"com.liferay.portal.workflow.kaleo.model.",
				"KaleoTaskInstanceToken&filter=cmpTaskObjectEntryIds/any(x:x ",
				"gt 0)"),
			props.get("filterURL"));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.WorkflowTasksOverviewComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private ThemeDisplay _themeDisplay;

}