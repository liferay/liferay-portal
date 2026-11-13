/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.dynamic.data.mapping.exception.RequiredStructureException;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class DeleteDataDefinitionMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-113664")
	public void testProcessAction() throws Exception {
		DataDefinition dataDefinition = _addDataDefinition();

		_mvcActionCommand.processAction(
			_createMockLiferayPortletActionRequest(dataDefinition),
			new MockLiferayPortletActionResponse());

		Assert.assertNull(
			_ddmStructureLocalService.fetchStructure(dataDefinition.getId()));

		dataDefinition = _addDataDefinition();

		_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			null, _portal.getClassNameId(JournalArticle.class),
			dataDefinition.getDataDefinitionKey(),
			RandomTestUtil.randomString(),
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_createMockLiferayPortletActionRequest(dataDefinition);

		PortletException portletException = Assert.assertThrows(
			PortletException.class,
			() -> _mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));

		Assert.assertTrue(
			portletException.getCause() instanceof
				RequiredStructureException.
					MustNotDeleteStructureReferencedByStructureLinks);

		Assert.assertNotNull(
			_ddmStructureLocalService.fetchStructure(dataDefinition.getId()));
	}

	private DataDefinition _addDataDefinition() throws Exception {
		Class<?> clazz = getClass();

		return DataDefinitionTestUtil.addDataDefinition(
			"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
			StringUtil.read(
				clazz.getResourceAsStream(
					"dependencies/data_definition_with_valid_fields.json")),
			TestPropsValues.getUser());
	}

	private MockLiferayPortletActionRequest
			_createMockLiferayPortletActionRequest(
				DataDefinition dataDefinition)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"dataDefinitionId", String.valueOf(dataDefinition.getId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(filter = "mvc.command.name=/journal/delete_data_definition")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

}