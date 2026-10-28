/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class LayoutPreviewRendererTest {

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
	public void testRender() throws Exception {
		Layout layout = _addLayout();

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(layout);

		Layout siblingLayout = _addLayout();

		FragmentEntryLink siblingFragmentEntryLink = _addFragmentEntryLink(
			siblingLayout);

		String html = _render(layout, LocaleUtil.US);

		Theme theme = _themeLocalService.fetchTheme(
			TestPropsValues.getCompanyId(),
			PropsValues.DEFAULT_REGULAR_THEME_ID);

		Assert.assertThat(
			html,
			CoreMatchers.containsString(
				"/o/" + theme.getServletContextName() + "/css/main."));

		Assert.assertThat(
			html, CoreMatchers.containsString(fragmentEntryLink.getCss()));
		Assert.assertThat(
			html, CoreMatchers.containsString(fragmentEntryLink.getHtml()));
		Assert.assertThat(
			html, CoreMatchers.containsString(fragmentEntryLink.getJs()));
		Assert.assertThat(
			html,
			CoreMatchers.not(
				CoreMatchers.containsString(
					siblingFragmentEntryLink.getHtml())));
		Assert.assertThat(html, CoreMatchers.containsString("signed-out"));
		Assert.assertThat(
			html, CoreMatchers.not(CoreMatchers.containsString("signed-in")));
		Assert.assertThat(
			html, CoreMatchers.containsString("/image/company_logo"));

		String siblingHTML = _render(siblingLayout, LocaleUtil.US);

		Assert.assertThat(
			siblingHTML,
			CoreMatchers.containsString(siblingFragmentEntryLink.getHtml()));
		Assert.assertThat(
			siblingHTML,
			CoreMatchers.not(
				CoreMatchers.containsString(fragmentEntryLink.getHtml())));
	}

	@Test
	public void testRenderLayoutOfOtherSite() throws Exception {
		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN);

		Layout layout = _addLayout(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build());

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			"<div class=\"site-default-locale\">" +
				"${themeDisplay.getSiteDefaultLocale()}</div>",
			StringPool.BLANK, draftLayout, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			FragmentConstants.TYPE_COMPONENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					LayoutTestUtil.addTypeContentLayout(
						GroupTestUtil.addGroup()))) {

			Assert.assertThat(
				_render(
					layout, LocaleUtil.SPAIN,
					ServiceContextThreadLocal.getServiceContext()),
				CoreMatchers.containsString(
					"<div class=\"site-default-locale\">es_ES</div>"));
			Assert.assertEquals(
				LocaleUtil.US, LocaleThreadLocal.getSiteDefaultLocale());
		}
	}

	@Test(expected = NoSuchLayoutException.class)
	public void testRenderUnpublishedLayout() throws Exception {
		Layout layout = _addLayout(
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			false);

		_render(layout, LocaleUtil.US);
	}

	@Test
	public void testRenderWithLocale() throws Exception {
		String englishName = RandomTestUtil.randomString();
		String spanishName = RandomTestUtil.randomString();

		Layout layout = _addLayout(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, spanishName
			).put(
				LocaleUtil.US, englishName
			).build());

		String englishHTML = _render(layout, LocaleUtil.US);

		Assert.assertThat(
			englishHTML, CoreMatchers.containsString(englishName));
		Assert.assertThat(
			englishHTML,
			CoreMatchers.not(CoreMatchers.containsString(spanishName)));

		String spanishHTML = _render(layout, LocaleUtil.SPAIN);

		Assert.assertThat(
			spanishHTML, CoreMatchers.containsString(spanishName));
		Assert.assertThat(
			spanishHTML,
			CoreMatchers.not(CoreMatchers.containsString(englishName)));
	}

	private FragmentEntryLink _addFragmentEntryLink(Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, draftLayout,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		return fragmentEntryLink;
	}

	private Layout _addLayout() throws Exception {
		return _addLayout(
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build());
	}

	private Layout _addLayout(Map<Locale, String> nameMap) throws Exception {
		return _addLayout(nameMap, true);
	}

	private Layout _addLayout(Map<Locale, String> nameMap, boolean publish)
		throws Exception {

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0, nameMap,
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, false,
			Collections.emptyMap(), null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		if (publish) {
			ContentLayoutTestUtil.publishLayout(
				layout.fetchDraftLayout(), layout);
		}

		return layout;
	}

	private String _render(Layout layout, Locale locale) throws Exception {
		layout = _layoutLocalService.getLayout(layout.getPlid());

		return _layoutPreviewRenderer.render(
			layout, locale,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private String _render(
			Layout layout, Locale locale, ServiceContext serviceContext)
		throws Exception {

		layout = _layoutLocalService.getLayout(layout.getPlid());

		return _layoutPreviewRenderer.render(
			layout, locale,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			serviceContext);
	}

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPreviewRenderer _layoutPreviewRenderer;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private ThemeLocalService _themeLocalService;

}