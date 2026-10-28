/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.exception.DepotEntryGroupRelToGroupException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class ConnectDepotEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessAction() throws Exception {
		_testProcessActionWithStagingDisabled();
		_testProcessActionWithStagingEnabled();
	}

	private MockLiferayPortletActionRequest
		_getMockLiferayPortletActionRequest() {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"depotEntryId", String.valueOf(_depotEntry.getDepotEntryId()));
		mockLiferayPortletActionRequest.setParameter(
			"redirect", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"toGroupId", String.valueOf(_group.getGroupId()));

		return mockLiferayPortletActionRequest;
	}

	private void _testProcessActionWithStagingDisabled() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));

		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_depotEntry.getDepotEntryId(), _group.getGroupId()));
	}

	private void _testProcessActionWithStagingEnabled() throws Exception {
		DepotTestUtil.withLocalStagingEnabled(
			_group,
			stagingGroup -> {
				int depotEntryGroupRelsCount =
					_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
						_depotEntry);

				MockLiferayPortletActionRequest
					mockLiferayPortletActionRequest =
						_getMockLiferayPortletActionRequest();

				_mvcActionCommand.processAction(
					mockLiferayPortletActionRequest,
					new MockLiferayPortletActionResponse());

				Assert.assertTrue(
					SessionErrors.contains(
						mockLiferayPortletActionRequest,
						DepotEntryGroupRelToGroupException.MustNotBeStaged.
							class));

				Assert.assertEquals(
					depotEntryGroupRelsCount,
					_depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
						_depotEntry));
			});
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "mvc.command.name=/depot/connect_depot_entry")
	private MVCActionCommand _mvcActionCommand;

}