/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchClassNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class DisplayPageTemplateServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddLayoutPageTemplateEntry() throws Exception {
		_testAddLayoutPageTemplateEntry();
		_testAddLayoutPageTemplateEntryWithInvalidClassNameId();
	}

	@Test
	public void testDeleteLayoutPageTemplateEntry() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group1.getGroupId());

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertNull(
			_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	@Test
	public void testGetLayoutPageTemplateEntries() throws Exception {
		_testGetLayoutPageTemplateEntriesAcrossGroups();
		_testGetLayoutPageTemplateEntriesAcrossGroupsWithName();
		_testGetLayoutPageTemplateEntriesAcrossGroupsWithPagination();
		_testGetLayoutPageTemplateEntriesWithoutGroups();
	}

	@Test
	public void testGetLayoutPageTemplateEntriesCount() throws Exception {
		_testGetLayoutPageTemplateEntriesCountAcrossGroups();
		_testGetLayoutPageTemplateEntriesCountAcrossGroupsWithName();
		_testGetLayoutPageTemplateEntriesCountWithoutGroups();
	}

	private LayoutPageTemplateEntry _createDisplayPageEntry(
			long classNameId, String classTypeKey)
		throws PortalException {

		return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			null, _group1.getGroupId(), 0, null, classNameId, classTypeKey,
			RandomTestUtil.randomString(), 0, WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _testAddLayoutPageTemplateEntry() throws Exception {
		String name = RandomTestUtil.randomString();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group1.getGroupId(), 0, null, name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext(
					_group1.getGroupId(), TestPropsValues.getUserId()));

		LayoutPageTemplateEntry persistedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(name, persistedLayoutPageTemplateEntry.getName());
	}

	private void _testAddLayoutPageTemplateEntryWithInvalidClassNameId()
		throws Exception {

		Assert.assertThrows(
			NoSuchClassNameException.class,
			() -> _createDisplayPageEntry(0, RandomTestUtil.randomString()));
	}

	private void _testGetLayoutPageTemplateEntriesAcrossGroups()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group1.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey());
		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group2.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey());
		LayoutPageTemplateEntry layoutPageTemplateEntry3 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group2.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), false,
				WorkflowConstants.STATUS_DRAFT);

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 2,
			layoutPageTemplateEntries.size());
		Assert.assertTrue(
			layoutPageTemplateEntries.contains(layoutPageTemplateEntry1));
		Assert.assertTrue(
			layoutPageTemplateEntries.contains(layoutPageTemplateEntry2));

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 3,
			layoutPageTemplateEntries.size());
		Assert.assertTrue(
			layoutPageTemplateEntries.contains(layoutPageTemplateEntry3));

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 1,
			layoutPageTemplateEntries.size());
		Assert.assertEquals(
			layoutPageTemplateEntry1, layoutPageTemplateEntries.get(0));
	}

	private void _testGetLayoutPageTemplateEntriesAcrossGroupsWithName()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		String name = RandomTestUtil.randomString();

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group1.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), false, null, name,
				WorkflowConstants.STATUS_APPROVED);

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group2.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), false, null, name,
				WorkflowConstants.STATUS_APPROVED);

		LayoutPageTemplateEntry layoutPageTemplateEntry3 =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group1.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), false, null,
				name + RandomTestUtil.randomString(),
				WorkflowConstants.STATUS_DRAFT);

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false, null,
			RandomTestUtil.randomString(), WorkflowConstants.STATUS_APPROVED);

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 2,
			layoutPageTemplateEntries.size());
		Assert.assertTrue(
			layoutPageTemplateEntries.contains(layoutPageTemplateEntry1));
		Assert.assertTrue(
			layoutPageTemplateEntries.contains(layoutPageTemplateEntry2));

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 3,
			layoutPageTemplateEntries.size());
		Assert.assertTrue(
			layoutPageTemplateEntries.contains(layoutPageTemplateEntry3));

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 1,
			layoutPageTemplateEntries.size());
		Assert.assertEquals(
			layoutPageTemplateEntry1, layoutPageTemplateEntries.get(0));
	}

	private void _testGetLayoutPageTemplateEntriesAcrossGroupsWithPagination()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey());
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey());
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false,
			WorkflowConstants.STATUS_DRAFT);

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 2,
			layoutPageTemplateEntries.size());

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 3,
			layoutPageTemplateEntries.size());

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED, 0, 1, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 1,
			layoutPageTemplateEntries.size());
	}

	private void _testGetLayoutPageTemplateEntriesCountAcrossGroups()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey());
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey());
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false,
			WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			2,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED));
		Assert.assertEquals(
			3,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			1,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[] {_group1.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED));
	}

	private void _testGetLayoutPageTemplateEntriesCountAcrossGroupsWithName()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());
		String name = RandomTestUtil.randomString();

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false, null, name,
			WorkflowConstants.STATUS_APPROVED);
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false, null, name,
			WorkflowConstants.STATUS_APPROVED);
		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false, null,
			name + RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_DRAFT);

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group2.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey(), false, null,
			RandomTestUtil.randomString(), WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			2,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED));
		Assert.assertEquals(
			3,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[] {_group1.getGroupId(), _group2.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			1,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[] {_group1.getGroupId()},
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED));
	}

	private void _testGetLayoutPageTemplateEntriesCountWithoutGroups()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey());

		Assert.assertEquals(
			0,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[0],
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED));
		Assert.assertEquals(
			0,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				new long[0],
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED));
	}

	private void _testGetLayoutPageTemplateEntriesWithoutGroups()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group1.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructure.getStructureKey());

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[0],
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 0,
			layoutPageTemplateEntries.size());

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[0],
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 0,
			layoutPageTemplateEntries.size());

		layoutPageTemplateEntries =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				new long[0],
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureKey(), RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			layoutPageTemplateEntries.toString(), 0,
			layoutPageTemplateEntries.size());
	}

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private Portal _portal;

}