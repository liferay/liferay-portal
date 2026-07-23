/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class StyleBookEntryProviderUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	@TestInfo("LPD-88081")
	public void testGetStyleBookEntries() throws Exception {
		StyleBookEntry depotEntryStyleBookEntry =
			_addDepotEntryStyleBookEntry();
		StyleBookEntry otherThemeStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId(), _THEME_ID_OTHER);
		StyleBookEntry styleBookEntry = _addStyleBookEntry(_group.getGroupId());

		_testGetStyleBookEntries(
			false, depotEntryStyleBookEntry, otherThemeStyleBookEntry,
			styleBookEntry);
		_testGetStyleBookEntries(
			true, depotEntryStyleBookEntry, otherThemeStyleBookEntry,
			styleBookEntry);
	}

	@Test
	public void testGetStyleBookEntriesPaginated() throws Exception {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(
			_group.getGroupId(), "Alpha", themeId);
		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(
			_group.getGroupId(), "Beta", themeId);

		Assert.assertEquals(
			2,
			StyleBookEntryProviderUtil.getStyleBookEntriesCount(
				_group.getCompanyId(), _group.getGroupId(), null, themeId));

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				_group.getCompanyId(), _group.getGroupId(), null, themeId, 0, 1,
				StyleBookEntryNameComparator.getInstance(true));

		Assert.assertEquals(
			styleBookEntries.toString(), 1, styleBookEntries.size());
		Assert.assertEquals(styleBookEntry1, styleBookEntries.get(0));

		Assert.assertEquals(
			1,
			StyleBookEntryProviderUtil.getStyleBookEntriesCount(
				_group.getCompanyId(), _group.getGroupId(), "Beta", themeId));

		styleBookEntries = StyleBookEntryProviderUtil.getStyleBookEntries(
			_group.getCompanyId(), _group.getGroupId(), "Beta", themeId, 0, 10,
			StyleBookEntryNameComparator.getInstance(true));

		Assert.assertEquals(
			styleBookEntries.toString(), 1, styleBookEntries.size());
		Assert.assertEquals(styleBookEntry2, styleBookEntries.get(0));
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-98556")
	public void testGetStyleBookEntriesWhenChildSiteIsUsed() throws Exception {
		Group parentGroup = _addGroup();

		StyleBookEntry parentStyleBookEntry = _addStyleBookEntry(
			parentGroup.getGroupId());

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		_groups.add(childGroup);

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), childGroup.getGroupId());

		Assert.assertFalse(
			styleBookEntries.toString(),
			styleBookEntries.contains(parentStyleBookEntry));
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-57283"))
	@Test
	@TestInfo("LPD-88081")
	public void testGetStyleBookEntry() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		StyleBookEntry styleBookEntry = _addStyleBookEntry(_group.getGroupId());

		_testGetStyleBookEntry(
			styleBookEntry, styleBookEntry.getExternalReferenceCode(), null);

		Group connectedDepotGroup = _addConnectedDepotGroup();

		_testGetStyleBookEntry(
			null, RandomTestUtil.randomString(),
			connectedDepotGroup.getExternalReferenceCode());

		StyleBookEntry connectedDepotStyleBookEntry = _addStyleBookEntry(
			connectedDepotGroup.getGroupId());

		_testGetStyleBookEntry(
			connectedDepotStyleBookEntry,
			connectedDepotStyleBookEntry.getExternalReferenceCode(),
			connectedDepotGroup.getExternalReferenceCode());

		Group disconnectedGroup = _addGroup();

		StyleBookEntry disconnectedGroupStyleBookEntry = _addStyleBookEntry(
			disconnectedGroup.getGroupId());

		_testGetStyleBookEntry(
			null, disconnectedGroupStyleBookEntry.getExternalReferenceCode(),
			disconnectedGroup.getExternalReferenceCode());

		_testGetStyleBookEntry(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private Group _addConnectedDepotGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		_depotEntries.add(depotEntry);

		return depotEntry.getGroup();
	}

	private StyleBookEntry _addDepotEntryStyleBookEntry() throws Exception {
		Group depotGroup = _addConnectedDepotGroup();

		return _addStyleBookEntry(depotGroup.getGroupId());
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	private StyleBookEntry _addStyleBookEntry(long groupId) throws Exception {
		return _addStyleBookEntry(groupId, _THEME_ID_CLASSIC);
	}

	private StyleBookEntry _addStyleBookEntry(long groupId, String themeId)
		throws Exception {

		return _addStyleBookEntry(
			groupId, RandomTestUtil.randomString(), themeId);
	}

	private StyleBookEntry _addStyleBookEntry(
			long groupId, String name, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			false, null, name, null, themeId, null);
	}

	private void _testGetStyleBookEntries(
			boolean connectedDepotEntriesEnabled,
			StyleBookEntry depotEntryStyleBookEntry,
			StyleBookEntry otherThemeStyleBookEntry,
			StyleBookEntry styleBookEntry)
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					"feature.flag.LPD-57283",
					String.valueOf(connectedDepotEntriesEnabled))) {

			List<StyleBookEntry> styleBookEntries =
				StyleBookEntryProviderUtil.getStyleBookEntries(
					TestPropsValues.getCompanyId(), _group.getGroupId());

			Assert.assertEquals(
				styleBookEntries.toString(), connectedDepotEntriesEnabled,
				styleBookEntries.contains(depotEntryStyleBookEntry));
			Assert.assertTrue(
				styleBookEntries.toString(),
				styleBookEntries.contains(otherThemeStyleBookEntry));
			Assert.assertTrue(
				styleBookEntries.toString(),
				styleBookEntries.contains(styleBookEntry));

			styleBookEntries = StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				styleBookEntry.getThemeId());

			Assert.assertEquals(
				styleBookEntries.toString(), connectedDepotEntriesEnabled,
				styleBookEntries.contains(depotEntryStyleBookEntry));
			Assert.assertFalse(
				styleBookEntries.toString(),
				styleBookEntries.contains(otherThemeStyleBookEntry));
			Assert.assertTrue(
				styleBookEntries.toString(),
				styleBookEntries.contains(styleBookEntry));
		}
	}

	private void _testGetStyleBookEntry(
			StyleBookEntry expectedStyleBookEntry, String styleBookEntryERC,
			String styleBookEntryScopeERC)
		throws Exception {

		_layout.setStyleBookEntryERC(styleBookEntryERC);
		_layout.setStyleBookEntryScopeERC(styleBookEntryScopeERC);

		_layout = _layoutLocalService.updateLayout(_layout);

		StyleBookEntry actualStyleBookEntry =
			StyleBookEntryProviderUtil.getStyleBookEntry(_layout);

		if (expectedStyleBookEntry == null) {
			Assert.assertNull(actualStyleBookEntry);

			return;
		}

		Assert.assertEquals(
			expectedStyleBookEntry.getStyleBookEntryId(),
			actualStyleBookEntry.getStyleBookEntryId());
	}

	private static final String _THEME_ID_CLASSIC = "classic_WAR_classictheme";

	private static final String _THEME_ID_OTHER = "other_WAR_othertheme";

	@DeleteAfterTestRun
	private List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}