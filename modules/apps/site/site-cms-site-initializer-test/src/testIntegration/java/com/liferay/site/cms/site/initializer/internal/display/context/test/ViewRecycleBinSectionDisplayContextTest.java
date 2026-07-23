/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
public class ViewRecycleBinSectionDisplayContextTest
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	public Map<String, Object> getBaseAdditionalProps() throws Exception {
		Map<String, Object> baseAdditionalProps =
			super.getBaseAdditionalProps();

		baseAdditionalProps.remove("additionalAPIURLParameters");

		return baseAdditionalProps;
	}

	@Test
	public void testGetBreadcrumbProps() throws Exception {
		HttpServletRequest httpServletRequest = getMockHttpServletRequest();

		Object displayContext = getSectionDisplayContext(httpServletRequest);

		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"breadcrumbItems",
				JSONUtil.putAll(
					JSONUtil.put(
						"active", false
					).put(
						"href", (String)null
					).put(
						"label",
						language.get(LocaleUtil.getDefault(), "recycle-bin")
					))
			).put(
				"hideSpace", true
			).put(
				"showEmptyRecycleBinAction", false
			).build(),
			_getBreadcrumbProps(displayContext));
	}

	@Test
	@TestInfo("LPD-87118")
	public void testGetBulkActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> bulkActionDropdownItems =
			getBulkActionDropdownItems();

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 2,
			bulkActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null, bulkActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"restore", "restore", "Restore", null,
			bulkActionDropdownItems.get(1));
	}

	@Override
	@Test
	public void testGetCMSSectionFilterString() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			false, TestPropsValues.getUserId());

		_assertTrashEnabled(depotEntry1, false);

		DepotEntry depotEntry2 = _addDepotEntry(
			true, TestPropsValues.getUserId());

		_assertTrashEnabled(depotEntry2, true);

		DepotEntry depotEntry3 = _addDepotEntry(
			true, TestPropsValues.getUserId());

		_assertTrashEnabled(depotEntry3, true);

		User cmsAdministratorUser = UserTestUtil.addCompanyUser(
			companyLocalService.getCompany(TestPropsValues.getCompanyId()),
			RoleConstants.CMS_ADMINISTRATOR);

		setUser(cmsAdministratorUser);

		String filterString = getCMSSectionFilterString(
			getSectionDisplayContext(
				getMockHttpServletRequest(cmsAdministratorUser)));

		Assert.assertTrue(
			filterString,
			filterString.contains("groupIds/any") &&
			filterString.contains(String.valueOf(depotEntry2.getGroupId())) &&
			filterString.contains(String.valueOf(depotEntry3.getGroupId())) &&
			filterString.contains(
				"status eq " + WorkflowConstants.STATUS_IN_TRASH));

		User regularUser = UserTestUtil.addUser();

		setUser(regularUser);

		Object displayContext = getSectionDisplayContext(
			getMockHttpServletRequest(regularUser));

		filterString = getCMSSectionFilterString(displayContext);

		Assert.assertTrue(
			filterString,
			filterString.contains("status eq " + WorkflowConstants.STATUS_ANY));

		groupLocalService.addUserGroup(
			regularUser.getUserId(), depotEntry1.getGroupId());
		groupLocalService.addUserGroup(
			regularUser.getUserId(), depotEntry2.getGroupId());

		setUser(regularUser);

		filterString = getCMSSectionFilterString(
			getSectionDisplayContext(getMockHttpServletRequest(regularUser)));

		Assert.assertTrue(
			filterString,
			filterString.contains(
				_getExpectedFilterString(depotEntry2.getGroupId())));
		Assert.assertFalse(
			filterString,
			filterString.contains(
				_getExpectedFilterString(depotEntry3.getGroupId())));

		_depotEntryLocalService.deleteDepotEntry(depotEntry1);
		_depotEntryLocalService.deleteDepotEntry(depotEntry2);
		_depotEntryLocalService.deleteDepotEntry(depotEntry3);

		_userLocalService.deleteUser(cmsAdministratorUser);
		_userLocalService.deleteUser(regularUser);
	}

	@Override
	@Test
	public void testGetCreationMenu() {
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 3,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLinkFolder", "View Folder", "get",
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", "delete",
			fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"restore", "restore", "Restore", "restore",
			fdsActionDropdownItems.get(2));
	}

	@Override
	protected String appendStatus(String filterString) {
		return filterString + " and status eq 8";
	}

	@Override
	protected CreationMenu getCreationMenu(
		ObjectEntryFolder objectEntryFolder) {

		return null;
	}

	@Override
	protected Map<String, String> getExpectedCreationMenuItems() {
		return Collections.emptyMap();
	}

	@Override
	protected String getFilterString() {
		return "cmsRoot eq true and (cmsSection eq 'contents' or cmsSection " +
			"eq 'files') and rootDescendantNode eq false";
	}

	@Override
	protected String getObjectFolderExternalReferenceCode() {
		if (RandomTestUtil.randomBoolean()) {
			return ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES;
		}

		return ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES;
	}

	@Override
	protected String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		};
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object viewRecycleBinSectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewRecycleBinSectionDisplayContext");

		Assert.assertNotNull(viewRecycleBinSectionDisplayContext);

		return viewRecycleBinSectionDisplayContext;
	}

	private DepotEntry _addDepotEntry(boolean trashEnabled, long userId)
		throws Exception {

		DepotEntry depotEntry = addDepotEntry(
			RandomTestUtil.randomString(), userId);

		_setTrashEnabledGroupProperty(
			depotEntry.getGroup(), String.valueOf(trashEnabled));

		return depotEntry;
	}

	private void _assertTrashEnabled(
			DepotEntry depotEntry, boolean expectedTrashEnabled)
		throws Exception {

		Group depotGroup = depotEntry.getGroup();

		Assert.assertEquals(
			expectedTrashEnabled,
			GetterUtil.getBoolean(
				depotGroup.getTypeSettingsProperty("trashEnabled")));
	}

	private Map<String, Object> _getBreadcrumbProps(Object displayContext) {
		return ReflectionTestUtil.invoke(
			displayContext, "getBreadcrumbProps", new Class<?>[0]);
	}

	private String _getExpectedFilterString(long... groupIds) {
		return StringBundler.concat(
			"groupIds/any(g:g in (",
			StringUtil.merge(groupIds, StringPool.COMMA), ")) and status eq ",
			WorkflowConstants.STATUS_IN_TRASH);
	}

	private void _setTrashEnabledGroupProperty(Group group, String value)
		throws Exception {

		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		if (value == null) {
			unicodeProperties.remove("trashEnabled");
		}
		else {
			unicodeProperties.setProperty("trashEnabled", value);
		}

		_groupLocalService.updateGroup(
			group.getGroupId(), unicodeProperties.toString());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewRecycleBinJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}