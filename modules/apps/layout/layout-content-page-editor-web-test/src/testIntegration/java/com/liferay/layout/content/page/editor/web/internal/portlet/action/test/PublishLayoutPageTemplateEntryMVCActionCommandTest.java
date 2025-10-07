/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.content.page.editor.web.internal.portlet.constants.LayoutContentPageEditorWebPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.util.structure.DeletedLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class PublishLayoutPageTemplateEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_DRAFT);

		_layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		_draftLayout = _layout.fetchDraftLayout();
	}

	@Test
	public void testDeletedItemsAreRemovedWhenLayoutPageTemplateEntryIsPublished()
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure(_draftLayout);

		LayoutStructureItem layoutStructureItem1 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		LayoutStructureItem layoutStructureItem2 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 1, 3);

		LayoutStructureItem layoutStructureItem3 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 1, 3);

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(layoutStructureItem1.getItemId()),
			Collections.emptyList());

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(layoutStructureItem2.getItemId()),
			Collections.emptyList());

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_draftLayout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_draftLayout.getPlid()),
				layoutStructure.toString());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_publishLayoutPageTemplateEntry",
			new Class<?>[] {Layout.class, Layout.class, long.class},
			_draftLayout, _layout, TestPropsValues.getUserId());

		layoutStructure = _getLayoutStructure(_draftLayout);

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		Assert.assertEquals(
			deletedLayoutStructureItems.toString(), 0,
			deletedLayoutStructureItems.size());

		Assert.assertNull(
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem1.getItemId()));
		Assert.assertNull(
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem2.getItemId()));
		Assert.assertNotNull(
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem3.getItemId()));
	}

	@Test
	public void testPortletWithGuestViewPermission() throws Exception {
		Assert.assertNotNull(_draftLayout);

		String portletId = _addTestPortletToLayout(_draftLayout);

		_addTestPortletGuestViewPermission(_draftLayout, portletId);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_publishLayoutPageTemplateEntry",
			new Class<?>[] {Layout.class, Layout.class, long.class},
			_draftLayout, _layout, TestPropsValues.getUserId());

		_assertGuestViewPermission(_layout, portletId, true);
	}

	@Test
	public void testPortletWithoutGuestViewPermission() throws Exception {
		Assert.assertNotNull(_draftLayout);

		String portletId = _addTestPortletToLayout(_draftLayout);

		_assertGuestViewPermission(_draftLayout, portletId, false);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_publishLayoutPageTemplateEntry",
			new Class<?>[] {Layout.class, Layout.class, long.class},
			_draftLayout, _layout, TestPropsValues.getUserId());

		_assertGuestViewPermission(_layout, portletId, false);
	}

	private void _addTestPortletGuestViewPermission(
			Layout layout, String portletId)
		throws Exception {

		_assertGuestViewPermission(layout, portletId, false);

		Role guestRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			layout.getCompanyId(),
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
			ResourceConstants.SCOPE_INDIVIDUAL,
			PortletPermissionUtil.getPrimaryKey(layout.getPlid(), portletId),
			guestRole.getRoleId(), new String[] {ActionKeys.VIEW});

		_assertGuestViewPermission(layout, portletId, true);
	}

	private String _addTestPortletToLayout(Layout layout) throws Exception {
		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				layout,
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private void _assertGuestViewPermission(
			Layout layout, String portletId, boolean guestViewPermission)
		throws Exception {

		boolean guestRoleFound = false;

		List<Role> roles = _resourcePermissionLocalService.getRoles(
			layout.getCompanyId(),
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
			ResourceConstants.SCOPE_INDIVIDUAL,
			PortletPermissionUtil.getPrimaryKey(layout.getPlid(), portletId),
			ActionKeys.VIEW);

		for (Role role : roles) {
			if (Objects.equals(role.getName(), RoleConstants.GUEST)) {
				guestRoleFound = true;

				break;
			}
		}

		Assert.assertEquals(guestViewPermission, guestRoleFound);
	}

	private LayoutStructure _getLayoutStructure(Layout layout)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private Layout _draftLayout;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/publish_layout_page_template_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}