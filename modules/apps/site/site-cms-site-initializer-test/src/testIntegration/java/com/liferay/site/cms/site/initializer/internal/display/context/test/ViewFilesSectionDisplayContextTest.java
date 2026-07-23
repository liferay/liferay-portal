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
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
@Sync
public class ViewFilesSectionDisplayContextTest
	extends BaseFilesSectionDisplayContextTestCase {

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
		).put(
			"galleryViewEnabled", true
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
			"download", "download", "Download", null,
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

	@Override
	protected Map<String, String> getExpectedCreationMenuItems()
		throws PortalException {

		return LinkedHashMapBuilder.put(
			"single-file", getRedirect("L_CMS_BASIC_DOCUMENT")
		).put(
			"multiple-files", StringPool.BLANK
		).put(
			"folder", StringPool.BLANK
		).put(
			"external-video", getRedirect("L_CMS_EXTERNAL_VIDEO")
		).build();
	}

	@Override
	protected String getFilterString() {
		return "cmsRoot eq true and cmsSection eq 'files' and " +
			"rootDescendantNode eq false";
	}

	@Override
	protected String getObjectFolderExternalReferenceCode() {
		return ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES;
	}

	@Override
	protected String getRootObjectEntryFolderExternalReferenceCode() {
		return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES;
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object filesSectionDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewFilesSectionDisplayContext");

		Assert.assertNotNull(filesSectionDisplayContext);

		return filesSectionDisplayContext;
	}

	@Override
	protected boolean isFolderSearchEnabled() {
		return true;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewFilesJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}