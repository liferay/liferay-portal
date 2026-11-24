/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class SubscribeFolderStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = _groupLocalService.getGroup(_depotEntry.getGroupId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testExecute() throws Exception {
		_testExecuteSubscribe();
		_testExecuteUnsubscribe();
		_testExecuteWithoutSubscribePermission();
	}

	private ObjectEntryFolder _addObjectEntryFolder() throws Exception {
		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, _depotEntry.getGroupId(), TestPropsValues.getUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private MockHttpServletResponse _execute(
			MockHttpServletRequest mockHttpServletRequest, User user)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_subscribeFolderStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		return mockHttpServletResponse;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			ObjectEntryFolder objectEntryFolder, String cmd)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group, _layout);

		mockHttpServletRequest.setParameter(
			"classPK",
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()));
		mockHttpServletRequest.setParameter("cmd", cmd);

		return mockHttpServletRequest;
	}

	private boolean _isSubscribed(
		ObjectEntryFolder objectEntryFolder, long userId) {

		return _subscriptionLocalService.isSubscribed(
			objectEntryFolder.getCompanyId(), userId,
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId());
	}

	private void _testExecuteSubscribe() throws Exception {
		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder();

		Assert.assertFalse(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));

		_execute(
			_getMockHttpServletRequest(objectEntryFolder, Constants.SUBSCRIBE),
			TestPropsValues.getUser());

		Assert.assertTrue(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));
	}

	private void _testExecuteUnsubscribe() throws Exception {
		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder();

		_execute(
			_getMockHttpServletRequest(objectEntryFolder, Constants.SUBSCRIBE),
			TestPropsValues.getUser());

		_execute(
			_getMockHttpServletRequest(
				objectEntryFolder, Constants.UNSUBSCRIBE),
			TestPropsValues.getUser());

		Assert.assertFalse(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));
	}

	private void _testExecuteWithoutSubscribePermission() throws Exception {
		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder();

		User user = _userLocalService.getDefaultUser(
			objectEntryFolder.getCompanyId());

		try {
			_execute(
				_getMockHttpServletRequest(
					objectEntryFolder, Constants.SUBSCRIBE),
				user);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception instanceof PrincipalException.MustHavePermission);
		}

		Assert.assertFalse(_isSubscribed(objectEntryFolder, user.getUserId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Layout _layout;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject(filter = "path=/cms/subscribe_folder")
	private StrutsAction _subscribeFolderStrutsAction;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}