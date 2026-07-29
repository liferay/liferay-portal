/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class EditDDMTemplateMVCRenderCommandTest
	extends BaseDDMTemplateMVCRenderCommandTestCase {

	@Test
	public void testRender() throws Exception {
		_testRenderWithAddDDMTemplatePermission();
		_testRenderWithMVCPath();
		_testRenderWithNonexistentDDMTemplate();
		_testRenderWithoutAddDDMTemplatePermission();
		_testRenderWithoutUpdatePermission();
		_testRenderWithUpdatePermission();
	}

	private void _testRenderWithAddDDMTemplatePermission() throws Exception {
		String path = "/edit_ddm_template.jsp";

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			getMockLiferayPortletRenderRequest(
				path, addUserWithAddDDMTemplatePermission());

		mockLiferayPortletRenderRequest.setParameter(
			"mvcRenderCommandName", _MVC_RENDER_COMMAND_NAME);

		Assert.assertEquals(path, render(mockLiferayPortletRenderRequest));

		assertDDMTemplateDisplayContext(mockLiferayPortletRenderRequest);
	}

	private void _testRenderWithMVCPath() throws Exception {
		String path = "/edit_ddm_template.jsp";

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			getMockLiferayPortletRenderRequest(path, addUser());

		mockLiferayPortletRenderRequest.setParameter(
			"ddmTemplateId", String.valueOf(ddmTemplate.getTemplateId()));
		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		Assert.assertNull(render(mockLiferayPortletRenderRequest));

		assertNoDDMTemplateDisplayContext(mockLiferayPortletRenderRequest);
	}

	private void _testRenderWithNonexistentDDMTemplate() throws Exception {
		String path = "/error.jsp";

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			getMockLiferayPortletRenderRequest(path, addUser());

		mockLiferayPortletRenderRequest.setParameter(
			"ddmTemplateId", String.valueOf(RandomTestUtil.randomLong()));
		mockLiferayPortletRenderRequest.setParameter(
			"mvcRenderCommandName", _MVC_RENDER_COMMAND_NAME);

		Assert.assertEquals(path, render(mockLiferayPortletRenderRequest));

		assertSessionError(
			mockLiferayPortletRenderRequest, NoSuchTemplateException.class);

		assertNoDDMTemplateDisplayContext(mockLiferayPortletRenderRequest);
	}

	private void _testRenderWithoutAddDDMTemplatePermission() throws Exception {
		String path = "/error.jsp";

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			getMockLiferayPortletRenderRequest(path, addUser());

		mockLiferayPortletRenderRequest.setParameter(
			"mvcRenderCommandName", _MVC_RENDER_COMMAND_NAME);

		Assert.assertEquals(path, render(mockLiferayPortletRenderRequest));

		assertSessionError(
			mockLiferayPortletRenderRequest,
			PrincipalException.MustHavePermission.class);

		assertNoDDMTemplateDisplayContext(mockLiferayPortletRenderRequest);
	}

	private void _testRenderWithoutUpdatePermission() throws Exception {
		String path = "/error.jsp";

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			getMockLiferayPortletRenderRequest(path, addUser());

		mockLiferayPortletRenderRequest.setParameter(
			"ddmTemplateId", String.valueOf(ddmTemplate.getTemplateId()));
		mockLiferayPortletRenderRequest.setParameter(
			"mvcRenderCommandName", _MVC_RENDER_COMMAND_NAME);

		Assert.assertEquals(path, render(mockLiferayPortletRenderRequest));

		assertSessionError(
			mockLiferayPortletRenderRequest,
			PrincipalException.MustHavePermission.class);

		assertNoDDMTemplateDisplayContext(mockLiferayPortletRenderRequest);
	}

	private void _testRenderWithUpdatePermission() throws Exception {
		String path = "/edit_ddm_template.jsp";

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			getMockLiferayPortletRenderRequest(
				path, addUserWithUpdatePermission());

		mockLiferayPortletRenderRequest.setParameter(
			"ddmTemplateId", String.valueOf(ddmTemplate.getTemplateId()));
		mockLiferayPortletRenderRequest.setParameter(
			"mvcRenderCommandName", _MVC_RENDER_COMMAND_NAME);

		Assert.assertEquals(path, render(mockLiferayPortletRenderRequest));

		assertDDMTemplateDisplayContext(mockLiferayPortletRenderRequest);
	}

	private static final String _MVC_RENDER_COMMAND_NAME =
		"/journal/edit_ddm_template";

}