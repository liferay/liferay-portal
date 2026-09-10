/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;
import com.liferay.site.staticexport.StaticSiteExporter;

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

		StaticSiteExport staticSiteExport = _staticSiteExporter.export(
			_group.getGroupId(), Set.of(LocaleUtil.US));

		List<StaticSiteExportLayout> staticSiteExportLayouts =
			staticSiteExport.getStaticSiteExportLayouts();

		Assert.assertEquals(
			staticSiteExportLayouts.toString(), 1,
			staticSiteExportLayouts.size());

		StaticSiteExportLayout staticSiteExportLayout =
			staticSiteExportLayouts.get(0);

		Assert.assertEquals(layout.getPlid(), staticSiteExportLayout.getPlid());
		Assert.assertEquals(LocaleUtil.US, staticSiteExportLayout.getLocale());
		Assert.assertEquals(
			StringUtil.removeFirst(layout.getFriendlyURL(), StringPool.SLASH) +
				".html",
			staticSiteExportLayout.getPath());
		Assert.assertThat(
			staticSiteExportLayout.getHTML(),
			CoreMatchers.containsString(layout.getName(LocaleUtil.US)));
	}

	@Test
	public void testExportWithUnpublishedLayout() throws Exception {
		LayoutTestUtil.addTypeContentLayout(_group);

		StaticSiteExport staticSiteExport = _staticSiteExporter.export(
			_group.getGroupId(), Set.of(LocaleUtil.US));

		List<StaticSiteExportLayout> staticSiteExportLayouts =
			staticSiteExport.getStaticSiteExportLayouts();

		Assert.assertTrue(
			staticSiteExportLayouts.toString(),
			staticSiteExportLayouts.isEmpty());
	}

	private Group _group;

	@Inject
	private StaticSiteExporter _staticSiteExporter;

}