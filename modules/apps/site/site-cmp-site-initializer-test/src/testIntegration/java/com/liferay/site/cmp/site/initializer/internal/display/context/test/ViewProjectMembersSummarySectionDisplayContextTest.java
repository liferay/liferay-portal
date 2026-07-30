/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
@Sync
public class ViewProjectMembersSummarySectionDisplayContextTest
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		_group = _groupLocalService.getGroup(_objectEntry.getGroupId());

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		CreationMenu creationMenu = getCreationMenu(null);

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(dropdownItems.toString(), 1, dropdownItems.size());

		DropdownItem dropdownItem = dropdownItems.get(0);

		Assert.assertEquals("addMembers", getValue(dropdownItem, "action"));
		Assert.assertEquals(
			String.valueOf(TestPropsValues.getUserId()),
			getValue(dropdownItem, "assetLibraryCreatorUserId"));
		Assert.assertEquals(
			String.valueOf(_objectEntry.getObjectEntryId()),
			getValue(dropdownItem, "cmpProjectObjectEntryId"));
		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			getValue(dropdownItem, "externalReferenceCode"));
		Assert.assertEquals(
			"groupIds in (" + _depotEntry.getGroupId() + ")",
			getValue(dropdownItem, "filter"));
		Assert.assertEquals(
			"true", getValue(dropdownItem, "hasAssignMembersPermission"));
		Assert.assertEquals("Add Members", dropdownItem.get("label"));
		Assert.assertEquals("Members (1)", getValue(dropdownItem, "title"));
	}

	@Test
	public void testGetHeaderProps() throws Exception {
		_assertHeaderProps("groupIds in (" + _depotEntry.getGroupId() + ")");

		_userGroup = UserGroupTestUtil.addUserGroup();

		_groupLocalService.addUserGroupGroups(
			_userGroup.getUserGroupId(), new long[] {_depotEntry.getGroupId()});

		_assertHeaderProps(
			StringBundler.concat(
				"(groupIds in (", _depotEntry.getGroupId(),
				") or userGroupIds in ('", _userGroup.getUserGroupId(), "'))"));
	}

	@Override
	protected String getObjectDefinitionExternalReferenceCode() {
		return "L_CMP_PROJECT";
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, _objectEntry);

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectMembersSummarySectionDisplayContext");
	}

	private void _assertHeaderProps(String expectedFilter) throws Exception {
		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"apiURL",
				StringBundler.concat(
					"/o/headless-asset-library/v1.0/asset-libraries/",
					_group.getExternalReferenceCode(),
					"/user-accounts?page=1&pageSize=8&nestedFields=roles")
			).put(
				"label", "View All Members"
			).put(
				"permissions",
				HashMapBuilder.<String, Object>put(
					"hasAssignMembersPermission", true
				).build()
			).put(
				"spaceModalProps",
				HashMapBuilder.<String, Object>put(
					"action", "open-members-modal"
				).put(
					"assetLibraryCreatorUserId",
					String.valueOf(TestPropsValues.getUserId())
				).put(
					"cmpProjectObjectEntryId", _objectEntry.getObjectEntryId()
				).put(
					"externalReferenceCode", _group.getExternalReferenceCode()
				).put(
					"filter", expectedFilter
				).build()
			).put(
				"title", "Members (1)"
			).put(
				"url", StringPool.BLANK
			).build(),
			getHeaderProps(null));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectMembersSummaryJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private ObjectEntry _objectEntry;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

}