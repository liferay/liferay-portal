/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutPrototypeLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddLayoutPrototype() throws PortalException {
		try {
			ExportImportThreadLocal.setLayoutImportInProcess(false);

			LayoutPrototype layoutPrototype = _testAddLayoutPrototype();

			Assert.assertNotNull(
				_layoutPageTemplateEntryLocalService.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId()));

			try (SafeCloseable safeCloseable =
					BatchEngineThreadLocal.
						setBatchImportInProcessWithSafeCloseable(true)) {

				layoutPrototype = _testAddLayoutPrototype();

				Assert.assertNotNull(
					_layoutPageTemplateEntryLocalService.
						fetchFirstLayoutPageTemplateEntry(
							layoutPrototype.getLayoutPrototypeId()));
			}

			ExportImportThreadLocal.setLayoutImportInProcess(true);

			layoutPrototype = _testAddLayoutPrototype();

			Assert.assertNull(
				_layoutPageTemplateEntryLocalService.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId()));
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}
	}

	@Test
	public void testUpdateLayoutPrototype() throws Exception {
		User user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, user.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, user.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, user.getUserId(), _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		_userLocalService.deleteUser(user);

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.fetchLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId());

		Assert.assertNotNull(
			_layoutPrototypeLocalService.updateLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId(),
				layoutPrototype.getNameMap(), null, false,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId())));
	}

	private LayoutPrototype _testAddLayoutPrototype() throws PortalException {
		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.addLayoutPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		Assert.assertNotNull(layoutPrototype);

		return layoutPrototype;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Inject
	private UserLocalService _userLocalService;

}