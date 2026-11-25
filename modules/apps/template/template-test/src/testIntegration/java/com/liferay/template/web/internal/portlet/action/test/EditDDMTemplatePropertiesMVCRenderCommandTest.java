/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.test.util.TemplateTestUtil;

import jakarta.portlet.PortletException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class EditDDMTemplatePropertiesMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-117734")
	public void testRender() throws Exception {
		_group = GroupTestUtil.addGroup();

		TemplateEntry templateEntry = TemplateTestUtil.addAnyTemplateEntry(
			_infoItemServiceRegistry,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.getDDMTemplate(
			templateEntry.getDDMTemplateId());

		_testRender(
			ActionKeys.UPDATE, ddmTemplate,
			_editDDMTemplatePropertiesMVCRenderCommand);
		_testRender(
			ActionKeys.VIEW, ddmTemplate,
			_viewWidgetTemplatesUsagesMVCRenderCommand);
	}

	private void _testRender(
			String actionId, DDMTemplate ddmTemplate,
			MVCRenderCommand mvcRenderCommand)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"ddmTemplateId", String.valueOf(ddmTemplate.getTemplateId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			_permissionCheckerFactory.create(UserTestUtil.addUser()));
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		try {
			mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse());

			Assert.fail();
		}
		catch (PortletException portletException) {
			PrincipalException.MustHavePermission mustHavePermission =
				(PrincipalException.MustHavePermission)
					portletException.getCause();

			Assert.assertArrayEquals(
				new String[] {actionId}, mustHavePermission.actionId);
			Assert.assertEquals(
				ddmTemplate.getTemplateId(), mustHavePermission.resourceId);
			Assert.assertEquals(
				_ddmPermissionSupport.getTemplateModelResourceName(
					ddmTemplate.getResourceClassName()),
				mustHavePermission.resourceName);
		}
	}

	@Inject
	private DDMPermissionSupport _ddmPermissionSupport;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject(filter = "mvc.command.name=/template/edit_ddm_template_properties")
	private MVCRenderCommand _editDDMTemplatePropertiesMVCRenderCommand;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject(filter = "mvc.command.name=/template/view_widget_templates_usages")
	private MVCRenderCommand _viewWidgetTemplatesUsagesMVCRenderCommand;

}