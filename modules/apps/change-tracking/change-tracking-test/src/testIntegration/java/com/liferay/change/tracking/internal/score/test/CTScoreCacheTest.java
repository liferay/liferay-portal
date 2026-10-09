/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.score.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
@Sync(destinationNames = CTDestinationNames.CT_SCORE)
public class CTScoreCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetScoreComputesOnDemandFromCTEntryCounts()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_ctCollection1 = _addCTCollectionWithDLFileEntries(1, group);
		_ctCollection2 = _addCTCollectionWithDLFileEntries(5, group);

		int score1 = _ctCollection1.getScore();
		int score2 = _ctCollection2.getScore();

		Assert.assertTrue(score1 > 0);
		Assert.assertTrue(score2 > score1);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testGetScoreIgnoresAmbientCTContext() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_ctCollection1 = _addCTCollectionWithDLFileEntries(3, group);
		_ctCollection2 = _addCTCollectionWithDLFileEntries(3, group);

		int score1 = _ctCollection1.getScore();

		int score2;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			score2 = _ctCollection2.getScore();
		}

		Assert.assertEquals(score1, score2);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testRemoveOnCTCollectionDeletion() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CTCollection ctCollection = _addCTCollectionWithDLFileEntries(1, group);

		long ctCollectionId = ctCollection.getCtCollectionId();

		PortalCache<Long, Integer> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_CACHE);

		Assert.assertEquals(
			Integer.valueOf(ctCollection.getScore()),
			portalCache.get(ctCollectionId));

		_ctCollectionLocalService.deleteCTCollection(ctCollection);

		Assert.assertNull(portalCache.get(ctCollectionId));

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	public void testRemoveOnRemoteScoreSizeClassificationChange()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		CTCollection ctCollection = _addCTCollectionWithDLFileEntries(1, group);

		long ctCollectionId = ctCollection.getCtCollectionId();

		PortalCache<Long, Integer> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				_PORTAL_CACHE_NAME_CT_SCORE_CACHE);

		Assert.assertEquals(
			Integer.valueOf(ctCollection.getScore()),
			portalCache.get(ctCollectionId));

		PortalCacheHelperUtil.removeWithoutReplicator(
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				"com.liferay.change.tracking.internal.score." +
					"CTScoreSizeClassificationCache"),
			ctCollectionId);

		Assert.assertNull(portalCache.get(ctCollectionId));

		_ctCollectionLocalService.deleteCTCollection(ctCollection);

		GroupTestUtil.deleteGroup(group);
	}

	private CTCollection _addCTCollectionWithDLFileEntries(
			int count, Group group)
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			DLFolder dlFolder = DLTestUtil.addDLFolder(group.getGroupId());

			for (int i = 0; i < count; i++) {
				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
			}
		}

		return ctCollection;
	}

	private static final String _PORTAL_CACHE_NAME_CT_SCORE_CACHE =
		"com.liferay.change.tracking.internal.score.CTScoreCache";

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

}