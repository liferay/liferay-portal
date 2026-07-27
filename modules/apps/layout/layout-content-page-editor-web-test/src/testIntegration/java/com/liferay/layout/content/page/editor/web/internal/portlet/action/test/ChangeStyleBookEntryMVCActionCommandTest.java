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
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
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
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

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
public class ChangeStyleBookEntryMVCActionCommandTest {

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
	@TestInfo("LPD-89205")
	public void testChangeStyleBookEntry() throws Exception {
		Group depotGroup = _addConnectedDepotGroup();

		StyleBookEntry depotStyleBookEntry = _addStyleBookEntry(
			depotGroup.getGroupId());

		_testChangeStyleBookEntry(
			depotStyleBookEntry.getExternalReferenceCode(),
			depotGroup.getExternalReferenceCode());

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		_testChangeStyleBookEntry(
			siteStyleBookEntry.getExternalReferenceCode(), "");
	}

	@Test
	public void testIsLayoutLockRequired() {
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "isLayoutLockRequired", new Class<?>[0]));
	}

	private Group _addConnectedDepotGroup() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		return depotEntry.getGroup();
	}

	private StyleBookEntry _addStyleBookEntry(long groupId) throws Exception {
		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			false, null, RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), null);
	}

	private void _testChangeStyleBookEntry(
			String styleBookEntryERC, String styleBookEntryScopeERC)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _layout);

		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryERC", styleBookEntryERC);
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryScopeERC", styleBookEntryScopeERC);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout updatedLayout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertEquals(
			styleBookEntryERC, updatedLayout.getStyleBookEntryERC());
		Assert.assertEquals(
			styleBookEntryScopeERC, updatedLayout.getStyleBookEntryScopeERC());
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
		filter = "mvc.command.name=/layout_content_page_editor/change_style_book_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}