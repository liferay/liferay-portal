/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class LayoutsExporterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, serviceContext.getUserId(),
					serviceContext.getScopeGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					serviceContext);

		_layoutPageTemplateCollectionId =
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId();

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_layoutPageTemplateCollectionMessage = StringBundler.concat(
			"User ", _user.getUserId(), " must have VIEW permission for ",
			LayoutPageTemplateCollection.class.getName(), " ",
			_layoutPageTemplateCollectionId);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		_layoutPageTemplateEntryId =
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId();

		_layoutPageTemplateEntryMessage = StringBundler.concat(
			"User ", _user.getUserId(), " must have VIEW permission for ",
			LayoutPageTemplateEntry.class.getName(), " ",
			_layoutPageTemplateEntryId);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(), 0, 0, false,
				RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				serviceContext);

		_layoutUtilityPageEntryId =
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId();

		_layoutUtilityPageEntryMessage = StringBundler.concat(
			"User ", _user.getUserId(), " must have VIEW permission for ",
			LayoutUtilityPageEntry.class.getName(), " ",
			_layoutUtilityPageEntryId);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_company != null) {
			_companyLocalService.deleteCompany(_company.getCompanyId());
		}

		if (_group != null) {
			_groupLocalService.deleteGroup(_group);
		}
	}

	@Test
	@TestInfo("LPD-82698")
	public void testExportLayoutPageTemplateCollections() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		try {
			_layoutsExporter.exportLayoutPageTemplateCollections(
				new long[] {_layoutPageTemplateCollectionId});

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(_layoutPageTemplateCollectionMessage));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	@TestInfo("LPD-82698")
	public void testExportLayoutPageTemplateEntries() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		try {
			_layoutsExporter.exportLayoutPageTemplateEntries(
				new long[] {_layoutPageTemplateEntryId},
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertEquals(
				_layoutPageTemplateEntryMessage,
				principalException.getMessage());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	@TestInfo("LPD-82698")
	public void testExportLayoutPageTemplateEntriesAndLayoutPageTemplateCollections()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		try {
			_layoutsExporter.
				exportLayoutPageTemplateEntriesAndLayoutPageTemplateCollections(
					new long[0], new long[] {_layoutPageTemplateCollectionId});

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(_layoutPageTemplateCollectionMessage));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		try {
			_layoutsExporter.
				exportLayoutPageTemplateEntriesAndLayoutPageTemplateCollections(
					new long[] {_layoutPageTemplateEntryId}, new long[0]);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertEquals(
				_layoutPageTemplateEntryMessage,
				principalException.getMessage());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	@TestInfo("LPD-116046")
	public void testExportLayoutUtilityPageEntries() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		try {
			_layoutsExporter.exportLayoutUtilityPageEntries(
				new long[] {_layoutUtilityPageEntryId});

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertEquals(
				_layoutUtilityPageEntryMessage,
				principalException.getMessage());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Group _group;

	@Inject
	private static GroupLocalService _groupLocalService;

	private static long _layoutPageTemplateCollectionId;

	@Inject
	private static LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	private static String _layoutPageTemplateCollectionMessage;
	private static long _layoutPageTemplateEntryId;

	@Inject
	private static LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private static String _layoutPageTemplateEntryMessage;
	private static long _layoutUtilityPageEntryId;

	@Inject
	private static LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	private static String _layoutUtilityPageEntryMessage;
	private static User _user;

	@Inject
	private LayoutsExporter _layoutsExporter;

}