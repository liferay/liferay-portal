/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
@Sync
public class ViewHomeRecentAssetsFilesSectionDisplayContextTest
	extends BaseFilesSectionDisplayContextTestCase {

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

		baseAdditionalProps.put("breadcrumbProps", _getBreadcrumbProps());
		baseAdditionalProps.remove("additionalAPIURLParameters");

		return baseAdditionalProps;
	}

	@Override
	@Test
	public void testGetBreadcrumbProps() throws Exception {
		AssertUtils.assertEquals(
			_getBreadcrumbProps(),
			ReflectionTestUtil.invoke(
				getSectionDisplayContext(getMockHttpServletRequest()),
				"getBreadcrumbProps", new Class<?>[0]));
	}

	@Override
	@Test
	public void testGetCreationMenu() throws Exception {
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 19,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLinkFolder", "View Folder", "get",
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"download", "download", "Download", "get",
			fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "editFolder", "Edit", "get",
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			fdsActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "actionLink", "Edit", "get",
			fdsActionDropdownItems.get(3));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "view-content", "View", null,
			fdsActionDropdownItems.get(4));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "view-file", "View", null, fdsActionDropdownItems.get(5));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"automatic-translate", "translate", "Translate", "get",
			fdsActionDropdownItems.get(6));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"share", "share", "Share", "get", fdsActionDropdownItems.get(7));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"info-circle-open", "show-details", "Show Details", null,
			fdsActionDropdownItems.get(8));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"time", "expire", "Expire", "post", fdsActionDropdownItems.get(9));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "version-history", "View History", "get",
			fdsActionDropdownItems.get(10));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"move-folder", "move", "Move", null,
			fdsActionDropdownItems.get(11));

		FDSActionDropdownItem copyFDSActionDropdownItem =
			fdsActionDropdownItems.get(12);

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"copy", "copy-menu", "Copy", null, "contextual", null,
			copyFDSActionDropdownItem);

		List<FDSActionDropdownItem> copyFDSActionDropdownItems =
			(List<FDSActionDropdownItem>)copyFDSActionDropdownItem.get("items");

		Assert.assertEquals(
			copyFDSActionDropdownItems.toString(), 2,
			copyFDSActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"copy", "copy", "Copy To", null, copyFDSActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"copy", "duplicate", "Duplicate", null,
			copyFDSActionDropdownItems.get(1));

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"upload", "export-for-translation", "Export for Translation", null,
			fdsActionDropdownItems.get(13));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"download", "import-translation", "Import Translation", null,
			fdsActionDropdownItems.get(14));

		FDSActionDropdownItem permissionsFDSActionDropdownItem =
			fdsActionDropdownItems.get(15);

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "permissions-menu", "Permissions", null,
			"contextual", null, permissionsFDSActionDropdownItem);

		List<FDSActionDropdownItem> permissionsFDSActionDropdownItems =
			(List<FDSActionDropdownItem>)permissionsFDSActionDropdownItem.get(
				"items");

		Assert.assertEquals(
			permissionsFDSActionDropdownItems.toString(), 4,
			permissionsFDSActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "permissions", "Permissions", "get",
			permissionsFDSActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "default-permissions", "Default Permissions",
			null,
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			permissionsFDSActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "edit-and-propagate-default-permissions",
			"Edit and Propagate Default Permissions", null,
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			permissionsFDSActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "reset-to-default-permissions",
			"Reset to Default Permissions", null,
			permissionsFDSActionDropdownItems.get(3));

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null, fdsActionDropdownItems.get(16));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-expiration-date", "Update Expiration Date",
			null, fdsActionDropdownItems.get(17));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-review-date", "Update Review Date", null,
			fdsActionDropdownItems.get(18));
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

	@Override
	protected String getFilterString() {
		return StringBundler.concat(
			"(cmsSection eq 'contents' or cmsSection eq 'files') and ",
			"objectDefinitionExternalReferenceCode ne '",
			ObjectEntryFolderConstants.
				EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER,
			"'");
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
			fragmentRendererContext, httpServletRequest,
			new MockHttpServletResponse());

		Object viewHomeRecentAssetsSectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewHomeRecentAssetsSectionDisplayContext");

		Assert.assertNotNull(viewHomeRecentAssetsSectionDisplayContext);

		return viewHomeRecentAssetsSectionDisplayContext;
	}

	private Map<String, Object> _getBreadcrumbProps() {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"active", false
				).put(
					"href", (String)null
				).put(
					"label", "All"
				))
		).put(
			"hideSpace", true
		).build();
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewHomeRecentAssetsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}