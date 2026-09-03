/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.test.util.AssetDisplayPageEntryTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.util.LayoutPageTemplateEntryUtil;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class BaseAssetDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_group = _addGroup();
	}

	@Test
	@TestInfo("LPD-104242")
	public void testGetLayoutFriendlyURLComposite() throws Exception {
		_testGetLayoutFriendlyURLComposite();

		_testGetLayoutFriendlyURLCompositeWhenDisconnected();
		_testGetLayoutFriendlyURLCompositeWhenNoDisplayPage();
	}

	private Group _addConnectedDesignLibraryGroup(Group group)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), group.getGroupId());

		return depotEntry.getGroup();
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate(
			long groupId, JournalArticle journalArticle)
		throws Exception {

		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			groupId, _getClassNameId(),
			LayoutPageTemplateEntryUtil.getClassTypeKey(
				_getClassNameId(), journalArticle.getDDMStructureId(),
				_group.getGroupId()),
			true, WorkflowConstants.STATUS_APPROVED);
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	private JournalArticle _addJournalArticle() throws Exception {
		return JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	private void _assertLayoutFriendlyURLComposite(
			boolean designLibrariesEnabled,
			LayoutPageTemplateEntry expectedLayoutPageTemplateEntry,
			JournalArticle journalArticle)
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					String.valueOf(designLibrariesEnabled))) {

			LayoutFriendlyURLComposite layoutFriendlyURLComposite =
				_friendlyURLResolver.getLayoutFriendlyURLComposite(
					TestPropsValues.getCompanyId(), _group.getGroupId(), false,
					_friendlyURLResolver.getURLSeparator() +
						journalArticle.getUrlTitle(),
					Collections.emptyMap(),
					HashMapBuilder.<String, Object>put(
						WebKeys.LOCALE, LocaleUtil.getDefault()
					).build());

			Layout layout = layoutFriendlyURLComposite.getLayout();

			if (expectedLayoutPageTemplateEntry == null) {
				Assert.assertNull(layout);

				return;
			}

			Assert.assertEquals(
				expectedLayoutPageTemplateEntry.getPlid(), layout.getPlid());
		}
	}

	private long _getClassNameId() {
		return _portal.getClassNameId(JournalArticle.class.getName());
	}

	private void _testGetLayoutFriendlyURLComposite() throws Exception {
		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			_addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), journalArticle);

		_assertLayoutFriendlyURLComposite(false, null, journalArticle);
		_assertLayoutFriendlyURLComposite(
			true, designLibraryLayoutPageTemplateEntry, journalArticle);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(_group.getGroupId(), journalArticle);

		_assertLayoutFriendlyURLComposite(
			true, layoutPageTemplateEntry, journalArticle);
	}

	private void _testGetLayoutFriendlyURLCompositeWhenDisconnected()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_addGroup());

		_addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), journalArticle);

		_assertLayoutFriendlyURLComposite(true, null, journalArticle);
	}

	private void _testGetLayoutFriendlyURLCompositeWhenNoDisplayPage()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		_addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), journalArticle);

		AssetDisplayPageEntryTestUtil.addAssetDisplayPageEntry(
			_group.getGroupId(), _getClassNameId(),
			journalArticle.getResourcePrimKey(), 0,
			AssetDisplayPageConstants.TYPE_NONE);

		_assertLayoutFriendlyURLComposite(true, null, journalArticle);
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.asset.display.page.portlet.JournalArticleAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private Portal _portal;

}