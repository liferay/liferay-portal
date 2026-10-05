/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class ChangeMasterLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-81022")
	public void testChangeMasterLayout() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			_group.getCompanyId(), true, "LPD-57283");

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		Group depotGroup = depotEntry.getGroup();

		LayoutPageTemplateEntry depotMasterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				depotGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		_testChangeMasterLayout(
			depotMasterLayoutPageTemplateEntry,
			depotGroup.getExternalReferenceCode());

		LayoutPageTemplateEntry siteMasterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		_testChangeMasterLayout(
			siteMasterLayoutPageTemplateEntry, StringPool.BLANK);
	}

	@Test
	public void testIsLayoutLockRequired() {
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "isLayoutLockRequired", new Class<?>[0]));
	}

	private void _testChangeMasterLayout(
			LayoutPageTemplateEntry masterLayoutPageTemplateEntry,
			String masterLayoutPageTemplateEntryScopeERC)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _layout);

		mockLiferayPortletActionRequest.addParameter(
			"masterLayoutPageTemplateEntryERC",
			masterLayoutPageTemplateEntry.getExternalReferenceCode());
		mockLiferayPortletActionRequest.addParameter(
			"masterLayoutPageTemplateEntryScopeERC",
			masterLayoutPageTemplateEntryScopeERC);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		LayoutStructure masterLayoutStructure = LayoutStructure.of(
			jsonObject.getJSONObject("masterLayoutData"));

		Assert.assertNotNull(
			masterLayoutStructure.getDropZoneLayoutStructureItem());

		Layout layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getExternalReferenceCode(),
			layout.getMasterLayoutPageTemplateEntryERC());
		Assert.assertEquals(
			masterLayoutPageTemplateEntryScopeERC,
			layout.getMasterLayoutPageTemplateEntryScopeERC());
		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/change_master_layout"
	)
	private MVCActionCommand _mvcActionCommand;

}