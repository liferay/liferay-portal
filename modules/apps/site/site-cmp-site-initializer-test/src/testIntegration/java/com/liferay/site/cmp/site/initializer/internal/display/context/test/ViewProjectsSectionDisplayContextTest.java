/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
@Sync
public class ViewProjectsSectionDisplayContextTest
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetAPIURL() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&filter=",
				"objectDefinitionId eq ",
				objectDefinition.getObjectDefinitionId(),
				"&nestedFields=embedded,",
				"embedded.r_userToCMPProjectManager_user,",
				"embedded.r_userToCMPProjectSponsor_user",
				"&nestedFieldsDepth=2"),
			getAPIURL(null));
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		CreationMenu creationMenu = getCreationMenu(null);

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(dropdownItems.toString(), 1, dropdownItems.size());

		DropdownItem dropdownItem = dropdownItems.get(0);

		Assert.assertEquals("createProject", getValue(dropdownItem, "action"));
		Assert.assertEquals("New Project", dropdownItem.get("label"));
		Assert.assertEquals(
			String.valueOf(objectDefinition.getObjectDefinitionId()),
			getValue(dropdownItem, "objectDefinitionId"));
		Assert.assertEquals(
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_project?objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(), "&plid=",
				themeDisplay.getPlid(), "&redirect=",
				themeDisplay.getURLCurrent()),
			getValue(dropdownItem, "redirect"));
		Assert.assertEquals("Project", getValue(dropdownItem, "title"));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems(null);

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 6,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit", "Edit", "get", fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLink", "View", null, fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"bell-on", "subscribe", "Watch Project", "post",
			fdsActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"bell-off", "unsubscribe", "Stop Watching Project", "post",
			fdsActionDropdownItems.get(3));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"users", "view-members", "View Members", null,
			fdsActionDropdownItems.get(4));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null, fdsActionDropdownItems.get(5));
	}

	@Test
	public void testGetFDSFilters() throws Exception {
		List<FDSFilter> fdsFilters = getFDSFilters(null);

		Assert.assertEquals(fdsFilters.toString(), 5, fdsFilters.size());

		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "cmpDueDate", "due-date",
			fdsFilters.get(0));
		assertFDSFilter(
			FDSEntityFieldTypes.INTEGER, "cmpProjectManagerUserId", "manager",
			fdsFilters.get(1));
		assertFDSFilter(
			FDSEntityFieldTypes.INTEGER, "cmpProjectSponsorUserId", "sponsor",
			fdsFilters.get(2));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpState", "state", fdsFilters.get(3));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "keywords", "tag", fdsFilters.get(4));
	}

	@Override
	protected String getObjectDefinitionExternalReferenceCode() {
		return "L_CMP_PROJECT";
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectsSectionDisplayContext");
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}