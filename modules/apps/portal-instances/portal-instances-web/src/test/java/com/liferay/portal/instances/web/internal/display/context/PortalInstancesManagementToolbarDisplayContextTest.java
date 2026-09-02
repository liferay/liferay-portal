/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class PortalInstancesManagementToolbarDisplayContextTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_liferayPortletResponse.createRenderURL()
		).thenReturn(
			_portletURL
		);
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
		_portletURLUtilMockedStatic.close();
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DATABASE_PARTITION_ENABLED", true)) {

			List<DropdownItem> dropdownItems = _getDropdownItems();

			Assert.assertEquals(
				dropdownItems.toString(), 2, dropdownItems.size());
			Assert.assertTrue(_containsImportDropdownItem(dropdownItems));

			Mockito.verify(
				_portletURL
			).setParameter(
				"mvcPath", "/add_db_partition_company.jsp"
			);

			Mockito.verify(
				_portletURL, Mockito.times(2)
			).setWindowState(
				LiferayWindowState.POP_UP
			);
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DATABASE_PARTITION_ENABLED", false)) {

			List<DropdownItem> dropdownItems = _getDropdownItems();

			Assert.assertEquals(
				dropdownItems.toString(), 1, dropdownItems.size());
			Assert.assertFalse(_containsImportDropdownItem(dropdownItems));
		}
	}

	private boolean _containsImportDropdownItem(
		List<DropdownItem> dropdownItems) {

		for (DropdownItem dropdownItem : dropdownItems) {
			Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
				"data");

			if ((data != null) && data.containsKey("importURL")) {
				return true;
			}
		}

		return false;
	}

	private List<DropdownItem> _getDropdownItems() {
		PortalInstancesManagementToolbarDisplayContext
			portalInstancesManagementToolbarDisplayContext =
				new PortalInstancesManagementToolbarDisplayContext(
					_httpServletRequest, _liferayPortletRequest,
					_liferayPortletResponse);

		CreationMenu creationMenu =
			portalInstancesManagementToolbarDisplayContext.getCreationMenu();

		return (List<DropdownItem>)creationMenu.get("primaryItems");
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletURL _portletURL = Mockito.mock(PortletURL.class);
	private final MockedStatic<PortletURLUtil> _portletURLUtilMockedStatic =
		Mockito.mockStatic(PortletURLUtil.class);

}