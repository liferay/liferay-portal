/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class AssetVocabularyLocalServiceTest {

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
	public void testGetOrAddEmptyVocabulary() throws Exception {

		// Lazy referencing disabled

		try {
			_assetVocabularyLocalService.getOrAddEmptyVocabulary(
				StringUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchVocabularyException noSuchVocabularyException) {
			Assert.assertNotNull(noSuchVocabularyException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AssetVocabulary vocabulary =
				_assetVocabularyLocalService.getOrAddEmptyVocabulary(
					StringUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, vocabulary.getStatus());
		}
	}

	@Test
	public void testUpdateAssetVocabularyWithLazyReferencingEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AssetVocabulary vocabulary =
				_assetVocabularyLocalService.getOrAddEmptyVocabulary(
					StringUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, vocabulary.getStatus());

			Locale locale = _portal.getSiteDefaultLocale(_group.getGroupId());
			String title = RandomTestUtil.randomString();

			vocabulary = _assetVocabularyLocalService.updateVocabulary(
				vocabulary.getVocabularyId(),
				HashMapBuilder.put(
					locale, title
				).build(),
				null, vocabulary.getSettings(),
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

			Assert.assertEquals(title, vocabulary.getTitle(locale));
			Assert.assertEquals(
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
				vocabulary.getVisibilityType());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, vocabulary.getStatus());
		}
	}

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}