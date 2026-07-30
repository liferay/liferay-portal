/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v4_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	@TestInfo("LPD-100147")
	public void testUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"FragmentEntryLinkConfigurationUpgradeProcess");

		Class<?> clazz = upgradeProcess.getClass();

		Map<Long, String> expectedConfigurations = new HashMap<>();

		Map<String, String> rendererKeyResourceNames = HashMapBuilder.put(
			"com.liferay.fragment.internal.renderer." +
				"ContentFlagsFragmentRenderer",
			"dependencies/content_flags_configuration.json"
		).put(
			"com.liferay.fragment.internal.renderer." +
				"ContentObjectFragmentRenderer",
			"dependencies/content_object_configuration.json"
		).put(
			"com.liferay.fragment.internal.renderer." +
				"ContentRatingsFragmentRenderer",
			"dependencies/content_ratings_configuration.json"
		).put(
			"com.liferay.fragment.renderer.menu.display.internal." +
				"MenuDisplayFragmentRenderer",
			"dependencies/menu_display_configuration.json"
		).build();

		for (Map.Entry<String, String> entry :
				rendererKeyResourceNames.entrySet()) {

			FragmentRenderer fragmentRenderer =
				_fragmentRendererRegistry.getFragmentRenderer(entry.getKey());

			FragmentEntryLink backfilledFragmentEntryLink =
				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					StringPool.BLANK, fragmentRenderer, _draftLayout, null, 0,
					_segmentsExperienceId);
			FragmentEntryLink preservedFragmentEntryLink =
				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					StringPool.BLANK, fragmentRenderer, _draftLayout, null, 1,
					_segmentsExperienceId);

			_nullifyConfiguration(
				backfilledFragmentEntryLink.getFragmentEntryLinkId());

			expectedConfigurations.put(
				backfilledFragmentEntryLink.getFragmentEntryLinkId(),
				StringUtil.read(clazz.getResourceAsStream(entry.getValue())));
			expectedConfigurations.put(
				preservedFragmentEntryLink.getFragmentEntryLinkId(),
				preservedFragmentEntryLink.getConfiguration());
		}

		FragmentRenderer collectionFilterFragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(
				"com.liferay.fragment.renderer.collection.filter.internal." +
					"CollectionFilterFragmentRenderer");

		FragmentEntryLink nullifiedFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				StringPool.BLANK, collectionFilterFragmentRenderer,
				_draftLayout, null, 0, _segmentsExperienceId);
		FragmentEntryLink unmodifiedFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				StringPool.BLANK, collectionFilterFragmentRenderer,
				_draftLayout, null, 1, _segmentsExperienceId);

		_nullifyConfiguration(
			nullifiedFragmentEntryLink.getFragmentEntryLinkId());

		expectedConfigurations.put(
			nullifiedFragmentEntryLink.getFragmentEntryLinkId(),
			StringPool.BLANK);
		expectedConfigurations.put(
			unmodifiedFragmentEntryLink.getFragmentEntryLinkId(),
			unmodifiedFragmentEntryLink.getConfiguration());

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();

		for (Map.Entry<Long, String> entry :
				expectedConfigurations.entrySet()) {

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					entry.getKey());

			Assert.assertEquals(
				entry.getValue(), fragmentEntryLink.getConfiguration());
		}
	}

	private void _nullifyConfiguration(long fragmentEntryLinkId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update FragmentEntryLink set configuration = null where " +
					"fragmentEntryLinkId = ?")) {

			preparedStatement.setLong(1, fragmentEntryLinkId);

			preparedStatement.executeUpdate();
		}
	}

	private Layout _draftLayout;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}