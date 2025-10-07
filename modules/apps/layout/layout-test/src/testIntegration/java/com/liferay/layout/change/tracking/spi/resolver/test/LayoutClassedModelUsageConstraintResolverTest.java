/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.change.tracking.spi.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
public class LayoutClassedModelUsageConstraintResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testResolveConflict() throws Exception {
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long classPK = RandomTestUtil.randomLong();
		String containKey = RandomTestUtil.randomString();
		long containerType = RandomTestUtil.randomLong();
		long plid = RandomTestUtil.randomLong();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
				TestPropsValues.getGroupId(), classExternalReferenceCode,
				classNameId, classPK, containKey, containerType, plid,
				ServiceContextTestUtil.getServiceContext());
		}

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			TestPropsValues.getGroupId(), classExternalReferenceCode,
			classNameId, classPK, containKey, containerType, plid,
			ServiceContextTestUtil.getServiceContext());

		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection);

		for (Map.Entry<Long, List<ConflictInfo>> entry :
				conflictInfoMap.entrySet()) {

			for (ConflictInfo conflictInfo : entry.getValue()) {
				Assert.assertTrue(conflictInfo.isResolved());
			}
		}
	}

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

}