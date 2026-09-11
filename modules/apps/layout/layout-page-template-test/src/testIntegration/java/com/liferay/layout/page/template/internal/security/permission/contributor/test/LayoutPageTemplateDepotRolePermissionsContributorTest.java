/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.security.permission.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateDepotRolePermissionsContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-57283"))
	@Test
	@TestInfo("LPD-104558")
	public void testManageLayoutPageTemplatesWithDesignLibraryMember()
		throws Exception {

		Group group = _addDesignLibraryGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				group.getGroupId(), LayoutPageTemplateEntryTypeConstants.BASIC,
				WorkflowConstants.STATUS_APPROVED);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				UserTestUtil.addGroupUser(
					group, DepotRolesConstants.DESIGN_LIBRARY_MEMBER))) {

			long layoutPageTemplateCollectionId =
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId();

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_layoutPageTemplateStructureService.
						updateLayoutPageTemplateStructureData(
							group.getGroupId(),
							layoutPageTemplateEntry.getPlid(),
							SegmentsExperienceConstants.ID_DEFAULT, _DATA));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_layoutPageTemplateCollectionService.
						updateLayoutPageTemplateCollection(
							layoutPageTemplateCollectionId,
							RandomTestUtil.randomString()));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_layoutPageTemplateEntryService.
						updateLayoutPageTemplateEntry(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							RandomTestUtil.randomString()));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_layoutPageTemplateEntryService.
						deleteLayoutPageTemplateEntry(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId()));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() ->
					_layoutPageTemplateCollectionService.
						deleteLayoutPageTemplateCollection(
							layoutPageTemplateCollectionId));
		}
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-57283"))
	@Test
	@TestInfo("LPD-104558")
	public void testManageLayoutPageTemplatesWithDesignLibraryRoles()
		throws Exception {

		Group group = _addDesignLibraryGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_assertManageLayoutPageTemplates(
				group, serviceContext,
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR);
			_assertManageLayoutPageTemplates(
				group, serviceContext,
				DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER);
			_assertManageLayoutPageTemplates(
				group, serviceContext,
				DepotRolesConstants.DESIGN_LIBRARY_OWNER);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private Group _addDesignLibraryGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		return depotEntry.getGroup();
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(
			Group group, User ownerUser, ServiceContext serviceContext)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				ownerUser)) {

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					addLayoutPageTemplateCollection(
						null, group.getGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, RandomTestUtil.randomString(), StringPool.BLANK,
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}
	}

	private void _assertManageLayoutPageTemplates(
			Group group, ServiceContext serviceContext, String roleName)
		throws Exception {

		User ownerUser = UserTestUtil.addGroupUser(group, roleName);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(group, ownerUser, serviceContext);

		User nonownerUser = UserTestUtil.addGroupUser(group, roleName);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				nonownerUser)) {

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureService.
					updateLayoutPageTemplateStructureData(
						group.getGroupId(), layoutPageTemplateEntry.getPlid(),
						SegmentsExperienceConstants.ID_DEFAULT, _DATA);

			Assert.assertEquals(
				_DATA,
				layoutPageTemplateStructure.getData(
					SegmentsExperienceConstants.ID_DEFAULT));

			long layoutPageTemplateCollectionId =
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId();
			String layoutPageTemplateCollectionName =
				RandomTestUtil.randomString();

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					updateLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId,
						layoutPageTemplateCollectionName);

			Assert.assertEquals(
				layoutPageTemplateCollectionName,
				layoutPageTemplateCollection.getName());

			String layoutPageTemplateEntryName = RandomTestUtil.randomString();

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					layoutPageTemplateEntryName);

			Assert.assertEquals(
				layoutPageTemplateEntryName, layoutPageTemplateEntry.getName());

			_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			_layoutPageTemplateCollectionService.
				deleteLayoutPageTemplateCollection(
					layoutPageTemplateCollectionId);
		}
	}

	private static final String _DATA = "{}";

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPageTemplateStructureService
		_layoutPageTemplateStructureService;

}