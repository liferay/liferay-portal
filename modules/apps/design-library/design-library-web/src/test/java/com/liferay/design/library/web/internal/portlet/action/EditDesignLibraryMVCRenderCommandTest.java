/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class EditDesignLibraryMVCRenderCommandTest
	extends BaseDesignLibraryMVCRenderCommandTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRenderWithoutUpdatePermission() throws Exception {
		DepotEntry depotEntry = mockDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		Mockito.doThrow(
			new PrincipalException.MustHavePermission(
				permissionChecker, DepotEntry.class.getName(),
				depotEntry.getDepotEntryId(), ActionKeys.UPDATE)
		).when(
			_depotEntryModelResourcePermission
		).check(
			permissionChecker, depotEntry, ActionKeys.UPDATE
		);

		assertError(depotEntry.getDepotEntryId());
	}

	@Override
	protected MVCRenderCommand getMVCRenderCommand() {
		EditDesignLibraryMVCRenderCommand editDesignLibraryMVCRenderCommand =
			new EditDesignLibraryMVCRenderCommand();

		ReflectionTestUtil.setFieldValue(
			editDesignLibraryMVCRenderCommand,
			"_depotEntryModelResourcePermission",
			_depotEntryModelResourcePermission);

		return editDesignLibraryMVCRenderCommand;
	}

	@Override
	protected String getPath() {
		return "/edit_design_library.jsp";
	}

	private final ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission = Mockito.mock(
			ModelResourcePermission.class);

}