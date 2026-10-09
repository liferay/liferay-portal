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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
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
public class
	GroupIdPrivateLayoutFriendlyURLAndLanguageIdLayoutFriendlyURLConstraintResolverTest {

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
		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

		_addConflictingLayoutFriendlyURLs(
			_ctCollection, LocaleUtil.toLanguageId(LocaleUtil.SPAIN), layout1,
			layout2);

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

			Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);
			Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

			_addConflictingLayoutFriendlyURLs(
				_ctCollection, LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
				layout1, layout2);

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

	private void _addConflictingLayoutFriendlyURLs(
			CTCollection ctCollection, String languageId, Layout layout1,
			Layout layout2)
		throws Exception {

		String friendlyURL = StringPool.SLASH + RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_layoutFriendlyURLLocalService.addLayoutFriendlyURL(
				TestPropsValues.getUserId(), layout1.getCompanyId(),
				layout1.getGroupId(), layout1.getPlid(),
				layout1.isPrivateLayout(), friendlyURL, languageId,
				serviceContext);
		}

		_layoutFriendlyURLLocalService.addLayoutFriendlyURL(
			TestPropsValues.getUserId(), layout2.getCompanyId(),
			layout2.getGroupId(), layout2.getPlid(), layout2.isPrivateLayout(),
			friendlyURL, languageId, serviceContext);
	}

	private ConflictInfo _getConflictInfo(CTCollection ctCollection)
		throws Exception {

		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(ctCollection);

		List<ConflictInfo> conflictInfos = conflictInfoMap.get(
			_classNameLocalService.getClassNameId(LayoutFriendlyURL.class));

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

}