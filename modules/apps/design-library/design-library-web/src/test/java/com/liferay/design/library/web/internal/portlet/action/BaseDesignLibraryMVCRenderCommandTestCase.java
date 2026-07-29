/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.design.library.web.internal.constants.DesignLibraryWebKeys;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Thiago Buarque
 */
public abstract class BaseDesignLibraryMVCRenderCommandTestCase {

	@Before
	public void setUp() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getHttpServletRequest(
				Mockito.any(PortletRequest.class))
		).thenReturn(
			mockHttpServletRequest
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getLiferayPortletRequest(
				Mockito.any(PortletRequest.class))
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getOriginalServletRequest(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			mockHttpServletRequest
		);

		mvcRenderCommand = getMVCRenderCommand();

		ReflectionTestUtil.setFieldValue(
			mvcRenderCommand, "depotEntryService", depotEntryService);
	}

	@After
	public void tearDown() {
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testRender() throws Exception {
		DepotEntry depotEntry = mockDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		MockRenderRequest mockRenderRequest = createMockRenderRequest(
			depotEntry.getDepotEntryId());

		Assert.assertEquals(
			getPath(),
			mvcRenderCommand.render(
				mockRenderRequest, new MockRenderResponse()));

		Assert.assertSame(
			depotEntry,
			mockRenderRequest.getAttribute(
				DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY));
	}

	@Test
	public void testRenderWithAssetLibrary() throws Exception {
		DepotEntry depotEntry = mockDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		assertError(depotEntry.getDepotEntryId());
	}

	@Test
	public void testRenderWithNonexistentDesignLibrary() throws Exception {
		assertError(RandomTestUtil.randomLong());
	}

	@Test
	public void testRenderWithoutViewPermission() throws Exception {
		DepotEntry depotEntry = mockDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		long depotEntryId = depotEntry.getDepotEntryId();

		Mockito.doThrow(
			new PrincipalException.MustHavePermission(
				permissionChecker, DepotEntry.class.getName(), depotEntryId,
				ActionKeys.VIEW)
		).when(
			depotEntryService
		).getDepotEntry(
			depotEntryId
		);

		assertError(depotEntryId);
	}

	protected void assertError(long depotEntryId) throws Exception {
		MockRenderRequest mockRenderRequest = createMockRenderRequest(
			depotEntryId);

		Assert.assertEquals(
			"/error.jsp",
			mvcRenderCommand.render(
				mockRenderRequest, new MockRenderResponse()));

		Assert.assertNull(
			mockRenderRequest.getAttribute(
				DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY));
		Assert.assertTrue(
			SessionErrors.contains(
				mockRenderRequest,
				PrincipalException.MustHavePermission.class));
	}

	protected MockRenderRequest createMockRenderRequest(long depotEntryId) {
		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPermissionChecker()
		).thenReturn(
			permissionChecker
		);

		mockRenderRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		mockRenderRequest.setParameter(
			"designLibraryEntryId", String.valueOf(depotEntryId));

		return mockRenderRequest;
	}

	protected abstract MVCRenderCommand getMVCRenderCommand();

	protected abstract String getPath();

	protected DepotEntry mockDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		long depotEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			depotEntry.getDepotEntryId()
		).thenReturn(
			depotEntryId
		);

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			type
		);

		Mockito.when(
			depotEntryService.getDepotEntry(depotEntryId)
		).thenReturn(
			depotEntry
		);

		return depotEntry;
	}

	protected final DepotEntryService depotEntryService = Mockito.mock(
		DepotEntryService.class);
	protected MVCRenderCommand mvcRenderCommand;
	protected final PermissionChecker permissionChecker = Mockito.mock(
		PermissionChecker.class);

	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}