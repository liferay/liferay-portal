/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Thiago Buarque
 */
public class ViewResourcesDesignLibraryMVCRenderCommandTest
	extends BaseDesignLibraryMVCRenderCommandTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Override
	protected MVCRenderCommand getMVCRenderCommand() {
		return new ViewResourcesDesignLibraryMVCRenderCommand();
	}

	@Override
	protected String getPath() {
		return "/view_resources.jsp";
	}

}