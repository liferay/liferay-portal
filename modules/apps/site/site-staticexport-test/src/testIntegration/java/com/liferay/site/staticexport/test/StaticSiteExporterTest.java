/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;
import com.liferay.site.staticexport.StaticSiteExportReport;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;

import java.io.File;

import java.util.List;
import java.util.Set;

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
public class StaticSiteExporterTest {

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
	public void testExport() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			_assertStaticSiteExport(layout, staticSiteExport);
		}
	}

	@Test
	public void testExportWithSiteInitializer() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.welcome");

			siteInitializer.initialize(_group.getGroupId());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			List<StaticSiteExportLayout> staticSiteExportLayouts =
				staticSiteExport.getStaticSiteExportLayouts();

			Assert.assertEquals(
				staticSiteExportLayouts.toString(),
				_layoutLocalService.getLayoutsCount(_group.getGroupId(), false),
				staticSiteExportLayouts.size());

			for (StaticSiteExportLayout staticSiteExportLayout :
					staticSiteExportLayouts) {

				String html = staticSiteExportLayout.getHTML();

				Assert.assertThat(html, CoreMatchers.containsString("</html>"));
				Assert.assertThat(
					html,
					CoreMatchers.containsString(
						"/o/layout-common-styles/main."));
			}

			boolean image = false;

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExport.getStaticSiteExportResources()) {

				String url = staticSiteExportResource.getURL();

				if (url.startsWith("/documents/") ||
					url.startsWith("/o/adaptive-media/")) {

					image = true;
				}

				File file = staticSiteExportResource.getFile();

				Assert.assertTrue(url, file.length() > 0);
			}

			Assert.assertTrue(
				String.valueOf(staticSiteExport.getStaticSiteExportResources()),
				image);

			StaticSiteExportReport staticSiteExportReport =
				staticSiteExport.getStaticSiteExportReport();

			List<StaticSiteExportReport.Failure> layoutFailures =
				staticSiteExportReport.getLayoutFailures();

			Assert.assertTrue(
				layoutFailures.toString(), layoutFailures.isEmpty());

			List<StaticSiteExportReport.Failure> resourceFailures =
				staticSiteExportReport.getResourceFailures();

			Assert.assertTrue(
				resourceFailures.toString(), resourceFailures.isEmpty());
		}
	}

	@Test
	public void testExportWithUnpublishedLayout() throws Exception {
		LayoutTestUtil.addTypeContentLayout(_group);

		try (StaticSiteExport staticSiteExport = _staticSiteExporter.export(
				_group.getGroupId(), Set.of(LocaleUtil.US))) {

			List<StaticSiteExportLayout> staticSiteExportLayouts =
				staticSiteExport.getStaticSiteExportLayouts();

			Assert.assertTrue(
				staticSiteExportLayouts.toString(),
				staticSiteExportLayouts.isEmpty());
		}
	}

	private void _assertStaticSiteExport(
		Layout layout, StaticSiteExport staticSiteExport) {

		List<StaticSiteExportLayout> staticSiteExportLayouts =
			staticSiteExport.getStaticSiteExportLayouts();

		Assert.assertEquals(
			staticSiteExportLayouts.toString(), 1,
			staticSiteExportLayouts.size());

		StaticSiteExportLayout staticSiteExportLayout =
			staticSiteExportLayouts.get(0);

		Assert.assertEquals(LocaleUtil.US, staticSiteExportLayout.getLocale());
		Assert.assertEquals(
			StringUtil.removeFirst(layout.getFriendlyURL(), StringPool.SLASH) +
				".html",
			staticSiteExportLayout.getPath());
		Assert.assertEquals(layout.getPlid(), staticSiteExportLayout.getPlid());
		Assert.assertThat(
			staticSiteExportLayout.getHTML(),
			CoreMatchers.containsString(layout.getName(LocaleUtil.US)));
		Assert.assertThat(
			staticSiteExportLayout.getHTML(),
			CoreMatchers.containsString(
				StringBundler.concat(
					"href=\"/", staticSiteExportLayout.getPath(),
					"\" rel=\"canonical\"")));

		List<StaticSiteExportResource> staticSiteExportResources =
			staticSiteExport.getStaticSiteExportResources();

		Assert.assertFalse(staticSiteExportResources.isEmpty());

		boolean bundleResource = false;
		boolean generatedResource = false;
		boolean stylesheet = false;

		for (StaticSiteExportResource staticSiteExportResource :
				staticSiteExportResources) {

			String path = staticSiteExportResource.getPath();

			Assert.assertFalse(path, path.startsWith(StringPool.SLASH));
			Assert.assertFalse(path, path.contains(StringPool.QUESTION));

			String url = staticSiteExportResource.getURL();

			Assert.assertTrue(url, url.startsWith(StringPool.SLASH));

			File file = staticSiteExportResource.getFile();

			Assert.assertTrue(url, file.length() > 0);

			if (url.contains("/__liferay__/")) {
				bundleResource = true;
			}

			if (url.contains("/layout-common-styles/")) {
				generatedResource = true;
			}

			if (url.contains(".css")) {
				stylesheet = true;

				Assert.assertThat(
					staticSiteExportLayout.getHTML(),
					CoreMatchers.containsString(StringPool.SLASH + path));
			}
		}

		Assert.assertTrue(staticSiteExportResources.toString(), bundleResource);
		Assert.assertTrue(
			staticSiteExportResources.toString(), generatedResource);
		Assert.assertTrue(staticSiteExportResources.toString(), stylesheet);

		StaticSiteExportReport staticSiteExportReport =
			staticSiteExport.getStaticSiteExportReport();

		List<StaticSiteExportReport.Failure> layoutFailures =
			staticSiteExportReport.getLayoutFailures();

		Assert.assertTrue(layoutFailures.toString(), layoutFailures.isEmpty());

		List<StaticSiteExportReport.Failure> resourceFailures =
			staticSiteExportReport.getResourceFailures();

		Assert.assertTrue(
			resourceFailures.toString(), resourceFailures.isEmpty());
	}

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private StaticSiteExporter _staticSiteExporter;

}