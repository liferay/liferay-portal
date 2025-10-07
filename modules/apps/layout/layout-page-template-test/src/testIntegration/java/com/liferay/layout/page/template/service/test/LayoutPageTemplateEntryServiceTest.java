/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateEntryPersistence;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

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
public class LayoutPageTemplateEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				LayoutPersistenceConstants.BUNDLE_SYMBOLIC_NAME));

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				_group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test(
		expected = LayoutPageTemplateEntryNameException.MustNotBeDuplicate.class
	)
	public void testAddDuplicateBasicLayoutPageTemplateEntries()
		throws Exception {

		String name = RandomTestUtil.randomString();

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			name);

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			name);
	}

	@Test
	public void testAddDuplicateDisplayPageLayoutPageTemplateEntries()
		throws Exception {

		String name = RandomTestUtil.randomString();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			name, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
			WorkflowConstants.STATUS_DRAFT);

		LayoutPageTemplateCollection targetLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			targetLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId(),
			name, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
			WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			2,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				_group.getGroupId(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));

		try {
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				targetLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				name, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_DRAFT);

			Assert.fail();
		}
		catch (LayoutPageTemplateEntryNameException.MustNotBeDuplicate
					layoutPageTemplateEntryNameException) {

			if (_log.isDebugEnabled()) {
				_log.debug(layoutPageTemplateEntryNameException);
			}
		}
	}

	@Test(expected = LayoutPageTemplateEntryNameException.class)
	public void testAddLayoutPageEntryWithNullName() throws Exception {
		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			null);
	}

	@Test
	public void testAddLayoutPageTemplateEntriesOfDifferentTypesWithTheSameName()
		throws Exception {

		String name = RandomTestUtil.randomString();

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			name, LayoutPageTemplateEntryTypeConstants.BASIC,
			WorkflowConstants.STATUS_APPROVED);
		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			name, LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddLayoutPageTemplateEntry() throws PortalException {
		String name = RandomTestUtil.randomString();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				name);

		LayoutPageTemplateEntry persistedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(name, persistedLayoutPageTemplateEntry.getName());
	}

	@Test
	public void testAddLayoutPageTemplateEntryByTypeAndStatus()
		throws PortalException {

		String name = RandomTestUtil.randomString();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				name, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(name, layoutPageTemplateEntry.getName());
		Assert.assertEquals(
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
			layoutPageTemplateEntry.getType());
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			layoutPageTemplateEntry.getStatus());
	}

	@Test(expected = LayoutPageTemplateEntryNameException.class)
	public void testAddLayoutPageTemplateEntryWithEmptyName() throws Exception {
		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			StringPool.BLANK);
	}

	@Test
	public void testAddLayoutPageTemplateEntryWithLayoutPrototype()
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		LayoutPageTemplateEntry persistedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			persistedLayoutPageTemplateEntry.getName());

		Assert.assertNotEquals(
			0, layoutPageTemplateEntry.getLayoutPrototypeId());

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.fetchLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId());

		Assert.assertNotNull(layoutPrototype);
		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			layoutPrototype.getName(LocaleUtil.getMostRelevantLocale()));
	}

	@Test
	public void testAddLayoutPageTemplateEntryWithoutAddPermission()
		throws Exception {

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test(expected = LayoutPageTemplateEntryNameException.class)
	public void testAddLayoutPageTemplateEntryWithSymbolInName()
		throws Exception {

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			"Test %&# Name");
	}

	@Test
	public void testAddLayoutPageTemplateEntryWithUTF8CharsInName()
		throws Exception {

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			"你好andこんにちは");
	}

	@Test
	public void testAddMultipleLayoutPageTemplateEntries()
		throws PortalException {

		List<LayoutPageTemplateEntry> originalLayoutPageTemplateEntries =
			_layoutPageTemplateEntryPersistence.findByG_L(
				_layoutPageTemplateCollection.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId());

		LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId());

		List<LayoutPageTemplateEntry> actualLayoutPageTemplateEntries =
			_layoutPageTemplateEntryPersistence.findByG_L(
				_layoutPageTemplateCollection.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		Assert.assertEquals(
			actualLayoutPageTemplateEntries.toString(),
			originalLayoutPageTemplateEntries.size() + 2,
			actualLayoutPageTemplateEntries.size());
	}

	@Test
	public void testCopyLayoutPageTemplateEntry() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		LayoutPageTemplateEntry copiedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryService.copyLayoutPageTemplateEntry(
				_group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			layoutPageTemplateEntry.getGroupId(),
			copiedLayoutPageTemplateEntry.getGroupId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getCompanyId(),
			copiedLayoutPageTemplateEntry.getCompanyId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getLayoutPageTemplateCollectionId(),
			copiedLayoutPageTemplateEntry.getLayoutPageTemplateCollectionId());
		Assert.assertNotEquals(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryKey(),
			copiedLayoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		Assert.assertEquals(
			layoutPageTemplateEntry.getClassNameId(),
			copiedLayoutPageTemplateEntry.getClassNameId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getClassTypeId(),
			copiedLayoutPageTemplateEntry.getClassTypeId());
		Assert.assertTrue(
			StringUtil.startsWith(
				copiedLayoutPageTemplateEntry.getName(),
				StringBundler.concat(
					layoutPageTemplateEntry.getName(), " (",
					_language.get(LocaleUtil.getSiteDefault(), "copy"), ")")));
		Assert.assertEquals(
			layoutPageTemplateEntry.getType(),
			copiedLayoutPageTemplateEntry.getType());
		Assert.assertEquals(
			0, copiedLayoutPageTemplateEntry.getPreviewFileEntryId());
		Assert.assertNotEquals(
			layoutPageTemplateEntry.getPlid(),
			copiedLayoutPageTemplateEntry.getPlid());
	}

	@Test
	public void testCopyLayoutPageTemplateEntryWithPermissions()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		LayoutPageTemplateEntry copiedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryService.copyLayoutPageTemplateEntry(
				_group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			layoutPageTemplateEntry.getGroupId(),
			copiedLayoutPageTemplateEntry.getGroupId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getCompanyId(),
			copiedLayoutPageTemplateEntry.getCompanyId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getLayoutPageTemplateCollectionId(),
			copiedLayoutPageTemplateEntry.getLayoutPageTemplateCollectionId());
		Assert.assertNotEquals(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryKey(),
			copiedLayoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		Assert.assertEquals(
			layoutPageTemplateEntry.getClassNameId(),
			copiedLayoutPageTemplateEntry.getClassNameId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getClassTypeId(),
			copiedLayoutPageTemplateEntry.getClassTypeId());
		Assert.assertTrue(
			StringUtil.startsWith(
				copiedLayoutPageTemplateEntry.getName(),
				StringBundler.concat(
					layoutPageTemplateEntry.getName(), " (",
					_language.get(LocaleUtil.getDefault(), "copy"), ")")));
		Assert.assertEquals(
			layoutPageTemplateEntry.getType(),
			copiedLayoutPageTemplateEntry.getType());
		Assert.assertEquals(
			0, copiedLayoutPageTemplateEntry.getPreviewFileEntryId());
		Assert.assertNotEquals(
			layoutPageTemplateEntry.getPlid(),
			copiedLayoutPageTemplateEntry.getPlid());

		List<ResourcePermission> expectedResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				layoutPageTemplateEntry.getCompanyId(),
				LayoutPageTemplateEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));

		int actualResourcePermissionsCount =
			_resourcePermissionLocalService.getResourcePermissionsCount(
				copiedLayoutPageTemplateEntry.getCompanyId(),
				LayoutPageTemplateEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					copiedLayoutPageTemplateEntry.
						getLayoutPageTemplateEntryId()));

		Assert.assertEquals(
			expectedResourcePermissions.toString(),
			expectedResourcePermissions.size(), actualResourcePermissionsCount);
	}

	@Test
	public void testCopyLayoutPageTemplateEntryWithPreviewFileEntry()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		Repository repository = _portletFileRepository.addPortletRepository(
			_group.getGroupId(), LayoutAdminPortletKeys.GROUP_PAGES,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			FragmentCollection.class.getName(),
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			LayoutAdminPortletKeys.GROUP_PAGES, repository.getDlFolderId(),
			new byte[0], "test.png", ContentTypes.IMAGE_PNG, false);

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				fileEntry.getFileEntryId());

		LayoutPageTemplateEntry copiedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryService.copyLayoutPageTemplateEntry(
				_group.getGroupId(),
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotEquals(
			layoutPageTemplateEntry.getPreviewFileEntryId(),
			copiedLayoutPageTemplateEntry.getPreviewFileEntryId());

		FileEntry copiedFileEntry = _portletFileRepository.getPortletFileEntry(
			copiedLayoutPageTemplateEntry.getPreviewFileEntryId());

		Assert.assertEquals(
			copiedLayoutPageTemplateEntry.getLayoutPageTemplateEntryId() +
				"_preview.png",
			copiedFileEntry.getFileName());
		Assert.assertEquals(
			fileEntry.getExtension(), copiedFileEntry.getExtension());

		FileVersion copiedFileVersion = copiedFileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, copiedFileVersion.getStatus());
	}

	@Test
	public void testDeleteLayoutPageTemplateEntries() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntries(
			new long[] {
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId()
			});

		Assert.assertNull(
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId()));

		Assert.assertNull(
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry2.getLayoutPageTemplateEntryId()));
	}

	@Test
	public void testDeleteLayoutPageTemplateEntry() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertNull(
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	@Test
	public void testDeleteLayoutPageTemplateEntryByExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getExternalReferenceCode(),
			layoutPageTemplateEntry.getGroupId());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	@Test
	public void testDeleteLayoutPageTemplateEntryByExternalReferenceCodeWithoutDeletePermission()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				layoutPageTemplateEntry.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testDeleteLayoutPageTemplateEntryWithLayoutPrototype()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertNull(
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
		Assert.assertNull(
			_layoutPrototypeLocalService.fetchLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId()));
	}

	@Test
	public void testGetApprovedLayoutPageCollectionsAndLayoutPageTemplateEntriesWithKeywords()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, "Parent Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentParentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, "Parent Parent Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, "Layout Page Template Entry One",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, "Layout Page Template Entry Two",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		List<Object> layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					0, 0, "Entry",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentParentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry1));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry2));

		Assert.assertEquals(
			1,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					0, 0, "Entry",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testGetLayoutPageCollectionsAndLayoutPageTemplateEntries()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, RandomTestUtil.randomString(), null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		List<Object> layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry));

		Assert.assertEquals(
			1,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));

		layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry));

		Assert.assertEquals(
			1,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));

		layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry));

		Assert.assertEquals(
			1,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));
	}

	@Test
	public void testGetLayoutPageCollectionsAndLayoutPageTemplateEntriesWithKeywordsAndParentLayoutPageCollection()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, "Parent Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentParentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, "Parent Parent Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, "Layout Page Template Entry One",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, "Layout Page Template Entry Two",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		List<Object> layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					0, 0, "Layout",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentParentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry1));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry2));

		Assert.assertEquals(
			3,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					0, 0, "Layout",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY));

		layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					0, 0, "Entry",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentParentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry1));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry2));

		Assert.assertEquals(
			2,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					0, 0, "Entry",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testGetLayoutPageCollectionsAndLayoutPageTemplateEntriesWithKeywordsAndParentLayoutPageCollectionDefault()
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, "Parent Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateCollection parentParentLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					parentLayoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					null, "Parent Parent Layout Page Template Collection", null,
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, "Layout Page Template Entry One",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, "Layout Page Template Entry Two",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		List<Object> layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(), -1, 0, 0, "Layout",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentParentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry1));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry2));

		Assert.assertEquals(
			5,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(), -1, 0, 0, "Layout",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY));

		layoutPageCollectionsAndLayoutPageTemplateEntries =
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntries(
					_group.getGroupId(), -1, 0, 0, "Entry",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentLayoutPageTemplateCollection));
		Assert.assertFalse(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				parentParentLayoutPageTemplateCollection));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry1));
		Assert.assertTrue(
			layoutPageCollectionsAndLayoutPageTemplateEntries.contains(
				layoutPageTemplateEntry2));

		Assert.assertEquals(
			2,
			_layoutPageTemplateEntryService.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_group.getGroupId(), -1, 0, 0, "Entry",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testGetLayoutPageTemplateEntryByExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		LayoutPageTemplateEntry curLayoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					layoutPageTemplateEntry.getExternalReferenceCode(),
					layoutPageTemplateEntry.getGroupId());

		Assert.assertEquals(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			curLayoutPageTemplateEntry.getLayoutPageTemplateEntryId());
	}

	@Test
	public void testGetLayoutPageTemplateEntryByExternalReferenceCodeWithoutViewPermission()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, LayoutPageTemplateEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, LayoutPageTemplateEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			ActionKeys.VIEW);

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					layoutPageTemplateEntry.getExternalReferenceCode(),
					layoutPageTemplateEntry.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testUpdateLayoutPageTemplateEntryDefaultTemplate()
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), true);

		LayoutPageTemplateEntry persistedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertTrue(persistedLayoutPageTemplateEntry.isDefaultTemplate());
	}

	@Test
	public void testUpdateLayoutPageTemplateEntryName() throws PortalException {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				"tiger");

		LayoutPageTemplateEntry persistedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			"tiger",
			persistedLayoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		Assert.assertEquals(
			"tiger", persistedLayoutPageTemplateEntry.getName());

		_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), "leopard");

		Assert.assertEquals(
			"leopard",
			persistedLayoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
		Assert.assertEquals(
			"leopard", persistedLayoutPageTemplateEntry.getName());
	}

	@Test
	public void testUpdateLayoutPageTemplateEntryStatus()
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		layoutPageTemplateEntry = _layoutPageTemplateEntryService.updateStatus(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			WorkflowConstants.STATUS_PENDING);

		LayoutPageTemplateEntry persistedLayoutPageTemplateEntry =
			_layoutPageTemplateEntryPersistence.fetchByPrimaryKey(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING,
			persistedLayoutPageTemplateEntry.getStatus());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryServiceTest.class);

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	private LayoutPageTemplateCollection _layoutPageTemplateCollection;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateEntryPersistence
		_layoutPageTemplateEntryPersistence;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private ServiceContext _serviceContext;

}