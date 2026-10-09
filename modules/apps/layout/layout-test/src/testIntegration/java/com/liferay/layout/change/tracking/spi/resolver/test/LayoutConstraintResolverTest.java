/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.change.tracking.spi.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class LayoutConstraintResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testResolveConflict() throws Exception {
		Layout layout = _addConflictingLayouts(_ctCollection);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"automaticFriendlyURLConflictResolutionEnabled",
							false
						).build())) {

			ConflictInfo conflictInfo = _getConflictInfo(_ctCollection);

			Assert.assertFalse(conflictInfo.isResolved());
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"automaticFriendlyURLConflictResolutionEnabled",
							true
						).build())) {

			ConflictInfo conflictInfo = _getConflictInfo(_ctCollection);

			Assert.assertTrue(conflictInfo.isResolved());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection.getCtCollectionId())) {

				Layout resolvedLayout = _layoutLocalService.getLayout(
					layout.getPlid());

				LayoutFriendlyURL layoutFriendlyURL =
					_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
						layout.getPlid(),
						LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

				Assert.assertEquals(
					resolvedLayout.getFriendlyURL(),
					layoutFriendlyURL.getFriendlyURL());
			}
		}
	}

	@Test
	public void testResolveConflictResolvesAllConflicts() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"automaticFriendlyURLConflictResolutionEnabled",
							true
						).build())) {

			_addConflictingLayouts(_ctCollection);

			Map<Long, List<ConflictInfo>> conflictInfoMap =
				_ctCollectionLocalService.checkConflicts(_ctCollection);

			List<String> unresolvedClassNames = new ArrayList<>();

			for (Map.Entry<Long, List<ConflictInfo>> entry :
					conflictInfoMap.entrySet()) {

				for (ConflictInfo conflictInfo : entry.getValue()) {
					if (conflictInfo.isResolved()) {
						continue;
					}

					ClassName className = _classNameLocalService.getClassName(
						entry.getKey());

					unresolvedClassNames.add(className.getValue());
				}
			}

			Assert.assertEquals(
				unresolvedClassNames.toString(), 0,
				unresolvedClassNames.size());
		}
	}

	private Layout _addConflictingLayouts(CTCollection ctCollection)
		throws Exception {

		Layout layout = null;

		Map<Locale, String> friendlyURLMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(),
			StringPool.SLASH + RandomTestUtil.randomString()
		).build();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			layout = LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), false,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), StringPool.BLANK,
				friendlyURLMap, false);
		}

		LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), StringPool.BLANK,
			friendlyURLMap, false);

		return layout;
	}

	private ConflictInfo _getConflictInfo(CTCollection ctCollection)
		throws Exception {

		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(ctCollection);

		List<ConflictInfo> conflictInfos = conflictInfoMap.get(
			_classNameLocalService.getClassNameId(Layout.class));

		Assert.assertEquals(conflictInfos.toString(), 1, conflictInfos.size());

		return conflictInfos.get(0);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

}