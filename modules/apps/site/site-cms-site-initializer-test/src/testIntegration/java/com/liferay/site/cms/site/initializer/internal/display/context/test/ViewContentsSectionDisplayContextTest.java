/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
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
@FeatureFlag("LPD-17564")
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

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 15,
			fdsActionDropdownItems.size());

		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "view", "actionLinkFolder",
			"view-folder", "get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "info-circle-open", "show-details",
			"show-details", null, "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "pencil", "editFolder", "edit",
			"get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), "pencil", "actionLink", "edit",
			"get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(4), "share", "share", "share", "get",
			"item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(5), "automatic-translate", "translate",
			"translate", "get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(6), "time", "expire", "expire", "post",
			"item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(7), "view", "view-content", "view", null,
			"item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(8), "view", "view-file", "view", null,
			"item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(9), "date-time", "version-history",
			"view-history", "get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(10), "upload", "export-for-translation",
			"export-for-translation", null, "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(11), "download", "import-translation",
			"import-translation", null, "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(12), "password-policies", "permissions",
			"permissions", "get", "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(13), "password-policies",
			"default-permissions", "default-permissions", null, "item");
		assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(14), "trash", "delete", "delete", null,
			"item");
	}

	@Override
	protected Map<String, String> getExpectedCreationMenuItems()
		throws PortalException {

		return LinkedHashMapBuilder.put(
			"folder", StringPool.BLANK
		).put(
			"basic-content", getRedirect("L_BASIC_WEB_CONTENT")
		).put(
			"blog", getRedirect("L_BLOG")
		).build();
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

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewContentsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}