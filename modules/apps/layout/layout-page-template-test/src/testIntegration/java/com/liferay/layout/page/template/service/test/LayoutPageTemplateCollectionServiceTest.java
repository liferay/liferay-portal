/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionException;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateCollectionServiceTest {

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

	@Test(expected = DuplicateLayoutPageTemplateCollectionException.class)
	public void testAddDuplicateBasicLayoutPageTemplateCollections()
		throws Exception {

		String name = StringUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, name, null, LayoutPageTemplateCollectionTypeConstants.BASIC,
			serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, name, null, LayoutPageTemplateCollectionTypeConstants.BASIC,
			serviceContext);
	}

	@Test(expected = DuplicateLayoutPageTemplateCollectionException.class)
	public void testAddDuplicateDisplayPageLayoutPageTemplateCollections()
		throws Exception {

		String name = StringUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, name, null,
			LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
			serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, name, null,
			LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
			serviceContext);
	}

	@Test
	public void testAddLayoutPageTemplateCollection() throws PortalException {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			"Layout Page Template Collection",
			layoutPageTemplateCollection.getName());
	}

	@Test(expected = PrincipalException.class)
	public void testAddLayoutPageTemplateCollectionByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			String externalReferenceCode = StringUtil.randomString();

			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					externalReferenceCode, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test(expected = LayoutPageTemplateCollectionNameException.class)
	public void testAddLayoutPageTemplateCollectionWithEmptyName()
		throws Exception {

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, StringPool.BLANK, null,
			LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test(
		expected = DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException.class
	)
	public void testAddLayoutPageTemplateCollectionWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			externalReferenceCode, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, RandomTestUtil.randomString(), null,
			LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			externalReferenceCode, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, RandomTestUtil.randomString(), null,
			LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = LayoutPageTemplateCollectionNameException.class)
	public void testAddLayoutPageTemplateCollectionWithNullName()
		throws Exception {

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, null, null, LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testAddMultipleLayoutPageTemplateCollections()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		int originalLayoutPageTemplateCollectionsCount =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollectionsCount(
					_group.getGroupId(),
					LayoutPageTemplateEntryTypeConstants.BASIC);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "Layout Page Template Collection 1", StringPool.BLANK,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "Layout Page Template Collection 2", StringPool.BLANK,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		int actualLayoutPageTemplateCollectionsCount =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollectionsCount(
					_group.getGroupId(),
					LayoutPageTemplateEntryTypeConstants.BASIC);

		Assert.assertEquals(
			originalLayoutPageTemplateCollectionsCount + 2,
			actualLayoutPageTemplateCollectionsCount);
	}

	@Test
	public void testDeleteLayoutPageTemplateCollection() throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId());

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()));
	}

	@Test
	public void testDeleteLayoutPageTemplateCollectionByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			externalReferenceCode, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, RandomTestUtil.randomString(), null,
			LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			externalReferenceCode, _group.getGroupId());

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					externalReferenceCode, _group.getGroupId()));
	}

	@Test(expected = PrincipalException.class)
	public void testDeleteLayoutPageTemplateCollectionByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			externalReferenceCode, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, RandomTestUtil.randomString(), null,
			LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			_layoutPageTemplateCollectionService.
				deleteLayoutPageTemplateCollection(
					externalReferenceCode, _group.getGroupId());

			Assert.assertNull(
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						externalReferenceCode, _group.getGroupId()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testDeleteLayoutPageTemplateCollections() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection1 =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection 1", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		LayoutPageTemplateCollection layoutPageTemplateCollection2 =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection 2", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		_layoutPageTemplateCollectionService.
			deleteLayoutPageTemplateCollections(
				new long[] {
					layoutPageTemplateCollection1.
						getLayoutPageTemplateCollectionId(),
					layoutPageTemplateCollection2.
						getLayoutPageTemplateCollectionId()
				});

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollection1.
						getLayoutPageTemplateCollectionId()));

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollection2.
						getLayoutPageTemplateCollectionId()));
	}

	@Test
	public void testFetchLayoutPageTemplateCollectionByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			externalReferenceCode, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, RandomTestUtil.randomString(), null,
			LayoutPageTemplateCollectionTypeConstants.BASIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					externalReferenceCode, _group.getGroupId()));
	}

	@Test(expected = PrincipalException.class)
	public void testFetchLayoutPageTemplateCollectionByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					externalReferenceCode, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

		Role guestRole = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			TestPropsValues.getCompanyId(),
			LayoutPageTemplateCollection.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					externalReferenceCode, _group.getGroupId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testGetLayoutPageTemplateCollections() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection1 =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection 1", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		LayoutPageTemplateCollection layoutPageTemplateCollection2 =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection 2", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		List<LayoutPageTemplateCollection> actualLayoutPageTemplateCollections =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollections(
					_group.getGroupId(),
					LayoutPageTemplateEntryTypeConstants.BASIC);

		Assert.assertTrue(
			actualLayoutPageTemplateCollections.contains(
				layoutPageTemplateCollection1));
		Assert.assertTrue(
			actualLayoutPageTemplateCollections.contains(
				layoutPageTemplateCollection2));
	}

	@Test
	public void testGetLayoutPageTemplateCollectionsByComparator()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "AA Page Template Collection", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "AB Page Template Collection", null,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "AC Page Template Collection", null,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		OrderByComparator<LayoutPageTemplateCollection> orderByComparator =
			LayoutPageTemplateCollectionNameComparator.getInstance(true);

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollections(
					_group.getGroupId(),
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator);

		LayoutPageTemplateCollection firstLayoutPageTemplateCollection =
			layoutPageTemplateCollections.get(0);

		Assert.assertEquals(
			firstLayoutPageTemplateCollection, layoutPageTemplateCollection);

		orderByComparator =
			LayoutPageTemplateCollectionNameComparator.getInstance(false);

		layoutPageTemplateCollections =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollections(
					_group.getGroupId(),
					LayoutPageTemplateEntryTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator);

		LayoutPageTemplateCollection lastLayoutPageTemplateCollection =
			layoutPageTemplateCollections.get(
				layoutPageTemplateCollections.size() - 1);

		Assert.assertEquals(
			lastLayoutPageTemplateCollection, layoutPageTemplateCollection);
	}

	@Test
	public void testGetLayoutPageTemplateCollectionsByKeywords()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		int originalLayoutPageTemplateCollectionsCount =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollectionsCount(
					_group.getGroupId(), "Theme",
					LayoutPageTemplateEntryTypeConstants.BASIC);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "Fjord Theme collection", null,
			LayoutPageTemplateEntryTypeConstants.BASIC, serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "Theme Westeros collection", null,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		int actualLayoutPageTemplateCollectionsCount =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollectionsCount(
					_group.getGroupId(), "Theme",
					LayoutPageTemplateEntryTypeConstants.BASIC);

		Assert.assertEquals(
			originalLayoutPageTemplateCollectionsCount + 2,
			actualLayoutPageTemplateCollectionsCount);
	}

	@Test
	public void testGetLayoutPageTemplateCollectionsByKeywordsAndComparator()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "AA Fjord Collection", null,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "AB Theme Collection", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		_layoutPageTemplateCollectionService.addLayoutPageTemplateCollection(
			null, _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, "AC Theme Collection", null,
			LayoutPageTemplateCollectionTypeConstants.BASIC, serviceContext);

		OrderByComparator<LayoutPageTemplateCollection> orderByComparator =
			LayoutPageTemplateCollectionNameComparator.getInstance(true);

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollections(
					_group.getGroupId(), "Theme",
					LayoutPageTemplateEntryTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator);

		LayoutPageTemplateCollection firstLayoutPageTemplateCollection =
			layoutPageTemplateCollections.get(0);

		Assert.assertEquals(
			firstLayoutPageTemplateCollection, layoutPageTemplateCollection);

		orderByComparator =
			LayoutPageTemplateCollectionNameComparator.getInstance(false);

		layoutPageTemplateCollections =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollections(
					_group.getGroupId(), "Theme",
					LayoutPageTemplateEntryTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator);

		LayoutPageTemplateCollection lastLayoutPageTemplateCollection =
			layoutPageTemplateCollections.get(
				layoutPageTemplateCollections.size() - 1);

		Assert.assertEquals(
			lastLayoutPageTemplateCollection, layoutPageTemplateCollection);
	}

	@Test
	public void testGetLayoutPageTemplateCollectionsByRange() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection1 =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection 1", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		LayoutPageTemplateCollection layoutPageTemplateCollection2 =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection 2", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		List<LayoutPageTemplateCollection> actualLayoutPageTemplateCollections =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollections(
					_group.getGroupId(),
					LayoutPageTemplateEntryTypeConstants.BASIC, 0, 2);

		Assert.assertTrue(
			actualLayoutPageTemplateCollections.contains(
				layoutPageTemplateCollection1));
		Assert.assertTrue(
			actualLayoutPageTemplateCollections.contains(
				layoutPageTemplateCollection2));
	}

	@Test
	public void testLayoutPageTemplateCollectionsWithPageTemplates()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					serviceContext);

		_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			null, _group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			null, "Layout Page Template Entry",
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId());

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()));
	}

	@Test
	public void testUpdateLayoutPageTemplateCollection()
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), TestPropsValues.getUserId()));

		layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					"Layout Page Template Collection New", "Description New");

		Assert.assertEquals(
			"layout-page-template-collection-new",
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionKey());
		Assert.assertEquals(
			"Layout Page Template Collection New",
			layoutPageTemplateCollection.getName());
		Assert.assertEquals(
			"Description New", layoutPageTemplateCollection.getDescription());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

}