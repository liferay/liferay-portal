/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.internal.change.tracking.spi.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
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
import java.util.Collections;
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
public class FriendlyURLEntryLocalizationConstraintResolverTest {

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
		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		FriendlyURLEntry friendlyURLEntry = _addConflictingFriendlyURLEntry(
			_classNameLocalService.getClassNameId(User.class), _ctCollection,
			languageId);

		String urlTitle = _getURLTitle(
			_ctCollection, friendlyURLEntry, languageId);

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

			Assert.assertEquals(
				urlTitle,
				_getURLTitle(_ctCollection, friendlyURLEntry, languageId));
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

			Assert.assertNotEquals(
				urlTitle,
				_getURLTitle(_ctCollection, friendlyURLEntry, languageId));
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

			_addConflictingFriendlyURLEntry(
				_classNameLocalService.getClassNameId(User.class),
				_ctCollection,
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

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

	private FriendlyURLEntry _addConflictingFriendlyURLEntry(
			long classNameId, CTCollection ctCollection, String languageId)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry = null;

		String urlTitle = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			friendlyURLEntry =
				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					_group.getGroupId(), classNameId,
					RandomTestUtil.randomLong(), languageId,
					Collections.singletonMap(languageId, urlTitle),
					serviceContext);
		}

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, RandomTestUtil.randomLong(),
			languageId, Collections.singletonMap(languageId, urlTitle),
			serviceContext);

		return friendlyURLEntry;
	}

	private ConflictInfo _getConflictInfo(CTCollection ctCollection)
		throws Exception {

		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(ctCollection);

		Assert.assertEquals(
			conflictInfoMap.toString(), 1, conflictInfoMap.size());

		List<ConflictInfo> conflictInfos = conflictInfoMap.get(
			_classNameLocalService.getClassNameId(
				FriendlyURLEntryLocalization.class));

		Assert.assertEquals(conflictInfos.toString(), 1, conflictInfos.size());

		return conflictInfos.get(0);
	}

	private String _getURLTitle(
		CTCollection ctCollection, FriendlyURLEntry friendlyURLEntry,
		String languageId) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
					friendlyURLEntry.getFriendlyURLEntryId(), languageId);

			return friendlyURLEntryLocalization.getUrlTitle();
		}
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}