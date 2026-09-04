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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
@Sync
public class ViewAllSectionDisplayContextTest
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
		).build();
	}

	@Test
	public void testGetAdditionalAPIURLParametersWithSearchQuery()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest();

		mockHttpServletRequest.setParameter("q", "test-query");

		String additionalAPIURLParameters = ReflectionTestUtil.invoke(
			getSectionDisplayContext(mockHttpServletRequest),
			"getAdditionalAPIURLParameters", new Class<?>[0]);

		Assert.assertFalse(
			additionalAPIURLParameters,
			additionalAPIURLParameters.contains(StringPool.QUESTION));
		Assert.assertTrue(
			additionalAPIURLParameters,
			additionalAPIURLParameters.contains("&search=test-query"));
	}

	@Test
	@TestInfo("LPD-87118")
	public void testGetBulkActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> bulkActionDropdownItems =
			getBulkActionDropdownItems();

		if (LicenseManagerUtil.isAppEnabled(App.CMP)) {
			FrontendDataSetTestUtil.assertFDSActionDropdownItem(
				"archive", "add-assets-to-project", "Add Assets to Project",
				"post", bulkActionDropdownItems.remove(9));
		}

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 17,
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
			"download", "download", "Download", null,
			bulkActionDropdownItems.get(6));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit-categories", "Edit Categories", "post",
			bulkActionDropdownItems.get(7));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit-tags", "Edit Tags", "post",
			bulkActionDropdownItems.get(8));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"semantic-search", "find-and-replace", "Find and Replace", null,
			bulkActionDropdownItems.get(9));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-expiration-date", "Update Expiration Date",
			null, bulkActionDropdownItems.get(10));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-review-date", "Update Review Date", null,
			bulkActionDropdownItems.get(11));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "permissions", "Permissions", null,
			bulkActionDropdownItems.get(12));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "default-permissions", "Default Permissions",
			null, bulkActionDropdownItems.get(13));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "edit-default-permissions-by-role",
			"Edit Default Permissions by Role", null,
			bulkActionDropdownItems.get(14));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "edit-permissions-by-role",
			"Edit Permissions by Role", null, bulkActionDropdownItems.get(15));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"password-policies", "reset-to-default-permissions",
			"Reset to Default Permissions", null,
			bulkActionDropdownItems.get(16));
	}

	@Override
	protected String getCMSSectionFilterString(Object displayContext) {
		return ReflectionTestUtil.invoke(
			displayContext, "getAdditionalAPIURLParameters", new Class<?>[0],
			new Object[0]);
	}

	@Override
	protected Map<String, String> getExpectedCreationMenuItems()
		throws PortalException {

		return LinkedHashMapBuilder.put(
			"basic-web-content",
			getRedirect(
				"L_CMS_BASIC_WEB_CONTENT",
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)
		).put(
			"single-file",
			getRedirect(
				"L_CMS_BASIC_DOCUMENT",
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)
		).put(
			"multiple-files", StringPool.BLANK
		).put(
			"blog",
			getRedirect(
				"L_CMS_BLOG",
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)
		).put(
			"external-video",
			getRedirect(
				"L_CMS_EXTERNAL_VIDEO",
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)
		).build();
	}

	@Override
	protected String getFilterString() {
		return StringBundler.concat(
			"(cmsSection eq 'contents' or cmsSection eq 'files') and ",
			"objectDefinitionExternalReferenceCode ne '",
			ObjectEntryFolderConstants.
				EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER,
			"' and rootDescendantNode eq false");
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

		Object allSectionDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewAllSectionDisplayContext");

		Assert.assertNotNull(allSectionDisplayContext);

		return allSectionDisplayContext;
	}

	@Override
	protected boolean isFolderSearchEnabled() {
		return true;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewAllJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}