/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pedro Leite
 */
@FeatureFlag("LPD-17564")
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
						"label", "recycle-bin"
					))
			).put(
				"hideSpace", true
			).build(),
			_getBreadcrumbProps(displayContext));
	}

	@Test
	public void testGetCMSSectionFilterString() throws Exception {
		Object displayContext = getSectionDisplayContext(
			getMockHttpServletRequest());

		Group defaultGroup = groupLocalService.getGroup(
			group.getCompanyId(), "Default");

		Assert.assertNull(defaultGroup.getTypeSettingsProperty("trashEnabled"));

		String filterString = _getCMSSectionFilterString(displayContext);

		Assert.assertTrue(
			filterString.contains(
				StringBundler.concat(
					"status eq ", WorkflowConstants.STATUS_IN_TRASH,
					" and groupIds/any(g:g in (", defaultGroup.getGroupId(),
					"))")));

		_setTrashEnabledGroupProperty(defaultGroup, Boolean.FALSE.toString());

		Assert.assertFalse(
			GetterUtil.getBoolean(
				defaultGroup.getTypeSettingsProperty("trashEnabled")));

		filterString = _getCMSSectionFilterString(displayContext);

		Assert.assertTrue(
			filterString.contains("status eq " + WorkflowConstants.STATUS_ANY));

		_setTrashEnabledGroupProperty(defaultGroup, Boolean.TRUE.toString());

		Assert.assertTrue(
			GetterUtil.getBoolean(
				defaultGroup.getTypeSettingsProperty("trashEnabled")));

		filterString = _getCMSSectionFilterString(displayContext);

		Assert.assertTrue(
			filterString.contains(
				StringBundler.concat(
					"status eq ", WorkflowConstants.STATUS_IN_TRASH,
					" and groupIds/any(g:g in (", defaultGroup.getGroupId(),
					"))")));

		DepotEntry depotEntry = addDepotEntry(
			RandomTestUtil.randomString(), DepotConstants.TYPE_SPACE);

		try {
			Group depotGroup = depotEntry.getGroup();

			_setTrashEnabledGroupProperty(depotGroup, Boolean.TRUE.toString());

			Assert.assertTrue(
				GetterUtil.getBoolean(
					depotGroup.getTypeSettingsProperty("trashEnabled")));

			filterString = _getCMSSectionFilterString(displayContext);

			Assert.assertTrue(
				filterString.contains(
					StringBundler.concat(
						"status eq ", WorkflowConstants.STATUS_IN_TRASH,
						" and groupIds/any(g:g in (", defaultGroup.getGroupId(),
						",", depotGroup.getGroupId(), "))")));

			_setTrashEnabledGroupProperty(defaultGroup, null);
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 3,
			fdsActionDropdownItems.size());

		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "view", "actionLinkFolder",
			"view-folder", "get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "trash", "delete", "delete",
			"delete", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "restore", "restore", "restore",
			"restore", "item");
	}

	@Override
	protected CreationMenu getCreationMenu(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return null;
	}

	@Override
	protected Map<String, String> getExpectedCreationMenuItems()
		throws PortalException {

		return Collections.emptyMap();
	}

	protected MockHttpServletRequest getMockHttpServletRequest()
		throws Exception {

		return getMockHttpServletRequest(null);
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

	private HashMap<String, Object> _getBreadcrumbProps(Object displayContext) {
		return ReflectionTestUtil.invoke(
			displayContext, "getBreadcrumbProps", new Class<?>[0]);
	}

	private String _getCMSSectionFilterString(Object displayContext) {
		return ReflectionTestUtil.invoke(
			displayContext, "getCMSSectionFilterString", new Class<?>[0],
			new Object[0]);
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

}