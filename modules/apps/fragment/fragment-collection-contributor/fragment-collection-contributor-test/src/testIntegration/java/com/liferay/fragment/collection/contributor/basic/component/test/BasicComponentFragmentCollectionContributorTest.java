/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.contributor.basic.component.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class BasicComponentFragmentCollectionContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-51802")
	public void testAccordionAccessibility() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-accordion");

		Document document = Jsoup.parseBodyFragment(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				_fragmentEntryLinkService.addFragmentEntryLink(
					null, _group.getGroupId(), 0,
					fragmentEntry.getFragmentEntryId(), 0, layout.getPlid(),
					fragmentEntry.getCss(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
					StringPool.BLANK, StringPool.BLANK, 0, null,
					fragmentEntry.getType(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId())),
				_getFragmentEntryProcessorContext(
					layout, LocaleUtil.getMostRelevantLocale())));

		Elements elements = document.select("[aria-controls]");

		Assert.assertEquals(elements.toString(), 1, elements.size());

		for (Element element : elements) {
			Assert.assertNotNull(
				document.getElementById(element.attr("aria-controls")));
		}
	}

	@Test
	@TestInfo("LPD-26242")
	public void testSliderAccessibility() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-slider");

		Document document = Jsoup.parseBodyFragment(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				_fragmentEntryLinkService.addFragmentEntryLink(
					null, _group.getGroupId(), 0,
					fragmentEntry.getFragmentEntryId(), 0, layout.getPlid(),
					fragmentEntry.getCss(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
					StringPool.BLANK, StringPool.BLANK, 0, null,
					fragmentEntry.getType(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId())),
				_getFragmentEntryProcessorContext(
					layout, LocaleUtil.getMostRelevantLocale())));

		Elements elements = document.select("[aria-controls]");

		Assert.assertFalse(elements.toString(), elements.isEmpty());

		for (Element element : elements) {
			Assert.assertNotNull(
				document.getElementById(element.attr("aria-controls")));
		}
	}

	private FragmentEntryProcessorContext _getFragmentEntryProcessorContext(
			Layout layout, Locale locale)
		throws Exception {

		return new DefaultFragmentEntryProcessorContext(
			_getHttpServletRequest(layout, locale),
			new MockHttpServletResponse(), FragmentEntryLinkConstants.EDIT,
			locale);
	}

	private HttpServletRequest _getHttpServletRequest(
			Layout layout, Locale locale)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLayout(layout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@DeleteAfterTestRun
	private Group _group;

}