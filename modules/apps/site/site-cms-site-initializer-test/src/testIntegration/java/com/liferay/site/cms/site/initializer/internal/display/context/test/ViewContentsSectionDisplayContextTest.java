/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
@Sync
public class ViewContentsSectionDisplayContextTest
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	public Map<String, Object> getBaseAdditionalProps() throws Exception {
		return new HashMapBuilder<>().putAll(
			super.getBaseAdditionalProps()
		).put(
			"breadcrumbProps",
			HashMapBuilder.<String, Object>put(
				"breadcrumbItems",
				JSONUtil.putAll(
					JSONUtil.put(
						"active", false
					).put(
						"href", (String)null
					).put(
						"label", "test"
					))
			).put(
				"hideSpace", true
			).build()
		).build();
	}

	@Test
	@TestInfo("LPD-87118")
	public void testGetBulkActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> bulkActionDropdownItems =
			getBulkActionDropdownItems();

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 13,
			bulkActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null, bulkActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"move-folder", "move-to", "Move To", null,
			bulkActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"copy", "copy-to", "Copy To", null, bulkActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"copy", "duplicate", "Duplicate", null,
			bulkActionDropdownItems.get(3));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"time", "expire", "Expire", null, bulkActionDropdownItems.get(4));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"upload", "export-for-translation", "Export for Translation", null,
			bulkActionDropdownItems.get(5));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit-categories", "Edit Categories", "post",
			bulkActionDropdownItems.get(6));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit-tags", "Edit Tags", "post",
			bulkActionDropdownItems.get(7));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "permissions", "Permissions", null,
			bulkActionDropdownItems.get(8));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "default-permissions", "Default Permissions",
			null, bulkActionDropdownItems.get(9));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "edit-default-permissions-by-role",
			"Edit Default Permissions by Role", null,
			bulkActionDropdownItems.get(10));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "edit-permissions-by-role",
			"Edit Permissions by Role", null, bulkActionDropdownItems.get(11));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "reset-to-default-permissions",
			"Reset to Default Permissions", null,
			bulkActionDropdownItems.get(12));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 16,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLinkFolder", "View Folder", "get",
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "editFolder", "Edit", "get",
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build(),
			fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "actionLink", "Edit", "get",
			fdsActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "view-content", "View", null,
			fdsActionDropdownItems.get(3));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "view-file", "View", null, fdsActionDropdownItems.get(4));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"automatic-translate", "translate", "Translate", "get",
			fdsActionDropdownItems.get(5));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"share", "share", "Share", "get", fdsActionDropdownItems.get(6));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"info-circle-open", "show-details", "Show Details", null,
			fdsActionDropdownItems.get(7));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"time", "expire", "Expire", "post", fdsActionDropdownItems.get(8));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "version-history", "View History", "get",
			fdsActionDropdownItems.get(9));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"move-folder", "move", "Move", null,
			fdsActionDropdownItems.get(10));

		FDSActionDropdownItem copyFDSActionDropdownItem =
			fdsActionDropdownItems.get(11);

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
			fdsActionDropdownItems.get(12));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"download", "import-translation", "Import Translation", null,
			fdsActionDropdownItems.get(13));

		FDSActionDropdownItem permissionsFDSActionDropdownItem =
			fdsActionDropdownItems.get(14);

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
			"trash", "delete", "Delete", null, fdsActionDropdownItems.get(15));
	}

	@Override
	protected Map<String, String> getExpectedCreationMenuItems()
		throws PortalException {

		return LinkedHashMapBuilder.put(
			"folder", StringPool.BLANK
		).put(
			"basic-web-content", getRedirect("L_CMS_BASIC_WEB_CONTENT")
		).put(
			"blog", getRedirect("L_CMS_BLOG")
		).build();
	}

	@Override
	protected String getFilterString() {
		return "cmsRoot eq true and cmsSection eq 'contents' and " +
			"rootDescendantNode eq false";
	}

	@Override
	protected String getObjectFolderExternalReferenceCode() {
		return ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES;
	}

	@Override
	protected String getRootObjectEntryFolderExternalReferenceCode() {
		return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS;
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object contentsSectionDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewContentsSectionDisplayContext");

		Assert.assertNotNull(contentsSectionDisplayContext);

		return contentsSectionDisplayContext;
	}

	@Override
	protected boolean isFolderSearchEnabled() {
		return true;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewContentsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}