/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.InitialRequestSyncUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
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
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@RunWith(Arquillian.class)
public class LayoutLocalServiceWrapperTest {

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
	@TestInfo({"LPD-99344", "LPD-103832"})
	public void testCopyLayoutContent() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		List<LayoutContentVersion> layoutContentVersions =
			_testCopyLayoutContent(1, layout);

		LayoutContentVersion layoutContentVersion = layoutContentVersions.get(
			0);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			layoutContentVersion.getStatus());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			draftLayout.getPlid(), layoutContentVersion.getPlid());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId());

		_testCopyLayoutContent(
			0,
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_testCopyLayoutContent(
			0, _layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()));

		Layout typeContentLayout = LayoutTestUtil.addTypeContentLayout(_group);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_testCopyLayoutContent(0, typeContentLayout);
		}

		try (SafeCloseable safeCloseable =
				_swapInitialRequestSyncUtilWithSafeCloseable()) {

			_testCopyLayoutContent(0, typeContentLayout);
		}
	}

	private SafeCloseable _swapInitialRequestSyncUtilWithSafeCloseable() {
		DefaultNoticeableFuture<Void>
			originalSyncCallableDefaultNoticeableFuture =
				ReflectionTestUtil.getAndSetFieldValue(
					InitialRequestSyncUtil.class,
					"_syncCallableDefaultNoticeableFuture",
					new DefaultNoticeableFuture<>());

		return () -> ReflectionTestUtil.setFieldValue(
			InitialRequestSyncUtil.class,
			"_syncCallableDefaultNoticeableFuture",
			originalSyncCallableDefaultNoticeableFuture);
	}

	private List<LayoutContentVersion> _testCopyLayoutContent(
			int count, Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		List<LayoutContentVersion> layoutContentVersions1 =
			_layoutContentVersionLocalService.getLayoutContentVersions(
				draftLayout.getPlid());

		_layoutLocalService.copyLayoutContent(draftLayout, layout);

		List<LayoutContentVersion> layoutContentVersions2 =
			_layoutContentVersionLocalService.getLayoutContentVersions(
				draftLayout.getPlid());

		Assert.assertEquals(
			layoutContentVersions2.toString(),
			layoutContentVersions1.size() + count,
			layoutContentVersions2.size());

		return layoutContentVersions2;
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

}