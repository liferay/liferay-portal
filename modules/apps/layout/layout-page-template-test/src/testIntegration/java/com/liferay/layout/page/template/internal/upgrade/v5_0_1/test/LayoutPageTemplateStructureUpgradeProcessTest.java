/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v5_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.sql.Connection;

import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateStructureUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		_addClassPKColumn();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_dropClassPKColumn();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgradeDisplaPageWithFragments() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, new ServiceContext());

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		_deleteLayoutPageTemplateStructure(layout);
		_deleteLayoutPageTemplateStructure(layout.fetchDraftLayout());

		_addFragmentEntryLink(layoutPageTemplateEntry.getPlid());

		_runUpgrade();

		_updatePlidColumn(layoutPageTemplateEntry.getPlid());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(
							layoutPageTemplateEntry.getPlid()));

		Assert.assertNotNull(layoutPageTemplateStructureRel);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructureRel.getData());

		Assert.assertTrue(
			MapUtil.isNotEmpty(
				layoutStructure.getFragmentLayoutStructureItems()));
	}

	@Test
	public void testUpgradeDisplaPageWithoutFragments() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				0, 0, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, new ServiceContext());

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		_deleteLayoutPageTemplateStructure(layout);
		_deleteLayoutPageTemplateStructure(layout.fetchDraftLayout());

		_runUpgrade();

		_updatePlidColumn(layoutPageTemplateEntry.getPlid());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(
							layoutPageTemplateEntry.getPlid()));

		Assert.assertNotNull(layoutPageTemplateStructureRel);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructureRel.getData());

		Assert.assertTrue(
			MapUtil.isEmpty(layoutStructure.getFragmentLayoutStructureItems()));
	}

	@Test
	public void testUpgradeLayoutWithFragments() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_deleteLayoutPageTemplateStructure(layout);
		_deleteLayoutPageTemplateStructure(layout.fetchDraftLayout());

		_addFragmentEntryLink(layout.getPlid());

		_runUpgrade();

		_updatePlidColumn(layout.getPlid());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid()));

		Assert.assertNotNull(layoutPageTemplateStructureRel);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructureRel.getData());

		Assert.assertTrue(
			MapUtil.isNotEmpty(
				layoutStructure.getFragmentLayoutStructureItems()));
	}

	@Test
	public void testUpgradeLayoutWithoutFragments() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_deleteLayoutPageTemplateStructure(layout);
		_deleteLayoutPageTemplateStructure(layout.fetchDraftLayout());

		_runUpgrade();

		_updatePlidColumn(layout.getPlid());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid()));

		Assert.assertNotNull(layoutPageTemplateStructureRel);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructureRel.getData());

		Assert.assertTrue(
			MapUtil.isEmpty(layoutStructure.getFragmentLayoutStructureItems()));
	}

	private static void _addClassPKColumn() throws Exception {
		_classPKColumnsAdded = false;
		_indexMetadataList = Collections.emptyList();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (!dbInspector.hasColumn(
					"LayoutPageTemplateStructure", "classNameId") &&
				!dbInspector.hasColumn(
					"LayoutPageTemplateStructure", "classPK")) {

				_db.runSQLTemplate(
					"alter table LayoutPageTemplateStructure add classNameId " +
						"LONG;",
					true);
				_db.runSQLTemplate(
					"alter table LayoutPageTemplateStructure add classPK LONG;",
					true);
				_db.runSQLTemplate(
					"update LayoutPageTemplateStructure set classPK = plid;",
					true);

				_classPKColumnsAdded = true;

				_indexMetadataList = _db.dropIndexes(
					connection, "LayoutPageTemplateStructure", "plid");
			}
		}
	}

	private static void _dropClassPKColumn() throws Exception {
		if (!_classPKColumnsAdded) {
			return;
		}

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (dbInspector.hasColumn(
					"LayoutPageTemplateStructure", "classNameId") &&
				dbInspector.hasColumn(
					"LayoutPageTemplateStructure", "classPK")) {

				_db.runSQLTemplate(
					"alter table LayoutPageTemplateStructure drop column " +
						"classNameId;",
					true);
				_db.runSQLTemplate(
					"alter table LayoutPageTemplateStructure drop column " +
						"classPK;",
					true);
			}

			if (ListUtil.isNotEmpty(_indexMetadataList)) {
				_db.addIndexes(connection, _indexMetadataList);
			}
		}
	}

	private void _addFragmentEntryLink(long plid) throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				StringUtil.randomString(), StringUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, "{fieldSets: []}", null,
				0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), null,
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getScopeERC(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid),
			plid, fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
			StringPool.BLANK, StringPool.BLANK, 0, StringPool.BLANK,
			fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _deleteLayoutPageTemplateStructure(Layout layout)
		throws Exception {

		_layoutPageTemplateStructureLocalService.
			deleteLayoutPageTemplateStructure(
				_group.getGroupId(), layout.getPlid());
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();
		}
	}

	private void _updatePlidColumn(long plid) throws Exception {
		_db.runSQL(
			StringBundler.concat(
				"update LayoutPageTemplateStructure set plid = ", plid,
				" where classPK = ", plid));

		_multiVMPool.clear();
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.page.template.internal.upgrade.v5_0_1." +
			"LayoutPageTemplateStructureUpgradeProcess";

	private static boolean _classPKColumnsAdded;
	private static DB _db;
	private static List<IndexMetadata> _indexMetadataList;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}