/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class ViewRolesManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_renderRequest.getAttribute(RolesAdminWebKeys.CURRENT_ROLE_TYPE)
		).thenReturn(
			_roleTypeContributor
		);

		Mockito.when(
			_renderResponse.createRenderURL()
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);

		Mockito.when(
			_roleTypeContributor.getType()
		).thenReturn(
			RoleConstants.TYPE_DEPOT
		);
	}

	@Test
	public void testGetFilterItemsDropdownItems() {
		try (MockedStatic<LanguageUtil> languageUtilMockedStatic =
				Mockito.mockStatic(LanguageUtil.class)) {

			languageUtilMockedStatic.when(
				() -> LanguageUtil.get(
					Mockito.any(HttpServletRequest.class), Mockito.anyString())
			).thenAnswer(
				invocation -> invocation.getArgument(1)
			);

			Mockito.when(
				_roleTypeContributor.getSubtypes()
			).thenReturn(
				new String[0]
			);

			ViewRolesManagementToolbarDisplayContext
				viewRolesManagementToolbarDisplayContext =
					_createViewRolesManagementToolbarDisplayContext();

			Assert.assertNull(
				viewRolesManagementToolbarDisplayContext.
					getFilterItemsDropdownItems());

			Mockito.when(
				_roleTypeContributor.getSubtypes()
			).thenReturn(
				new String[] {"space", "project"}
			);

			viewRolesManagementToolbarDisplayContext =
				_createViewRolesManagementToolbarDisplayContext();

			List<DropdownItem> dropdownItems =
				viewRolesManagementToolbarDisplayContext.
					getFilterItemsDropdownItems();

			Assert.assertEquals(
				dropdownItems.toString(), 1, dropdownItems.size());

			DropdownItem dropdownItem = dropdownItems.get(0);

			Assert.assertEquals("filter-by-subtype", dropdownItem.get("label"));

			List<DropdownItem> subtypeDropdownItems = _getSubtypeDropdownItems(
				dropdownItem);

			Assert.assertEquals(
				subtypeDropdownItems.toString(), 3,
				subtypeDropdownItems.size());

			_assertDropdownItem(true, subtypeDropdownItems.get(0), "all");
			_assertDropdownItem(false, subtypeDropdownItems.get(1), "space");
			_assertDropdownItem(false, subtypeDropdownItems.get(2), "project");

			Mockito.when(
				_httpServletRequest.getParameter("subtype")
			).thenReturn(
				"space"
			);

			viewRolesManagementToolbarDisplayContext =
				_createViewRolesManagementToolbarDisplayContext();

			dropdownItems =
				viewRolesManagementToolbarDisplayContext.
					getFilterItemsDropdownItems();

			subtypeDropdownItems = _getSubtypeDropdownItems(
				dropdownItems.get(0));

			_assertDropdownItem(false, subtypeDropdownItems.get(0), "all");
			_assertDropdownItem(true, subtypeDropdownItems.get(1), "space");
			_assertDropdownItem(false, subtypeDropdownItems.get(2), "project");
		}
	}

	@Test
	public void testGetFilterLabelItems() {
		try (MockedStatic<LanguageUtil> languageUtilMockedStatic =
				Mockito.mockStatic(LanguageUtil.class)) {

			languageUtilMockedStatic.when(
				() -> LanguageUtil.get(
					Mockito.any(HttpServletRequest.class), Mockito.anyString())
			).thenAnswer(
				invocation -> invocation.getArgument(1)
			);

			ViewRolesManagementToolbarDisplayContext
				viewRolesManagementToolbarDisplayContext =
					_createViewRolesManagementToolbarDisplayContext();

			List<LabelItem> labelItems =
				viewRolesManagementToolbarDisplayContext.getFilterLabelItems();

			Assert.assertTrue(labelItems.toString(), labelItems.isEmpty());

			Mockito.when(
				_httpServletRequest.getParameter("subtype")
			).thenReturn(
				"space"
			);

			viewRolesManagementToolbarDisplayContext =
				_createViewRolesManagementToolbarDisplayContext();

			labelItems =
				viewRolesManagementToolbarDisplayContext.getFilterLabelItems();

			Assert.assertEquals(labelItems.toString(), 1, labelItems.size());

			LabelItem labelItem = labelItems.get(0);

			Assert.assertEquals(Boolean.TRUE, labelItem.get("closeable"));
			Assert.assertEquals("subtype: space", labelItem.get("label"));

			Map<String, Object> data = (Map<String, Object>)labelItem.get(
				"data");

			Assert.assertTrue(
				data.toString(), data.containsKey("removeLabelURL"));
		}
	}

	private void _assertDropdownItem(
		boolean active, DropdownItem dropdownItem, String label) {

		Assert.assertEquals(active, dropdownItem.get("active"));
		Assert.assertEquals(label, dropdownItem.get("label"));
	}

	private ViewRolesManagementToolbarDisplayContext
		_createViewRolesManagementToolbarDisplayContext() {

		ViewRolesManagementToolbarDisplayContext
			viewRolesManagementToolbarDisplayContext =
				new ViewRolesManagementToolbarDisplayContext(
					_httpServletRequest, _renderRequest, _renderResponse,
					"descriptive");

		ReflectionTestUtil.setFieldValue(
			viewRolesManagementToolbarDisplayContext, "_orderByCol", "title");
		ReflectionTestUtil.setFieldValue(
			viewRolesManagementToolbarDisplayContext, "_orderByType", "asc");

		return viewRolesManagementToolbarDisplayContext;
	}

	private List<DropdownItem> _getSubtypeDropdownItems(
		DropdownItem dropdownItem) {

		return (List<DropdownItem>)dropdownItem.get("items");
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final RoleTypeContributor _roleTypeContributor = Mockito.mock(
		RoleTypeContributor.class);

}