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
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jan Brychta
 */
@FeatureFlag("LPD-103654")
@RunWith(Arquillian.class)
public class SubscribeFolderStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		_testExecuteSubscribe();
		_testExecuteUnsubscribe();
		_testExecuteWithoutSubscribePermission();
	}

	private Group _addGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		return _groupLocalService.getGroup(depotEntry.getGroupId());
	}

	private ObjectEntryFolder _addObjectEntryFolder(long groupId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, groupId, TestPropsValues.getUserId(),
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
			Group group, Layout layout, ObjectEntryFolder objectEntryFolder,
			String cmd)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				group, layout);

		mockHttpServletRequest.setParameter("cmd", cmd);
		mockHttpServletRequest.setParameter(
			"objectEntryFolderId",
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()));

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
		Group group1 = _addGroup();

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			group1.getGroupId());

		Assert.assertFalse(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));

		Group group2 = GroupTestUtil.addGroup();

		_execute(
			_getMockHttpServletRequest(
				group2, LayoutTestUtil.addTypeContentLayout(group2),
				objectEntryFolder, Constants.SUBSCRIBE),
			TestPropsValues.getUser());

		Assert.assertTrue(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));

		Subscription subscription = _subscriptionLocalService.fetchSubscription(
			objectEntryFolder.getCompanyId(), TestPropsValues.getUserId(),
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId());

		Assert.assertEquals(group1.getGroupId(), subscription.getGroupId());
	}

	private void _testExecuteUnsubscribe() throws Exception {
		Group group = _addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			group.getGroupId());

		_execute(
			_getMockHttpServletRequest(
				group, layout, objectEntryFolder, Constants.SUBSCRIBE),
			TestPropsValues.getUser());

		Assert.assertTrue(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));

		_execute(
			_getMockHttpServletRequest(
				group, layout, objectEntryFolder, Constants.UNSUBSCRIBE),
			TestPropsValues.getUser());

		Assert.assertFalse(
			_isSubscribed(objectEntryFolder, TestPropsValues.getUserId()));
	}

	private void _testExecuteWithoutSubscribePermission() throws Exception {
		Group group = _addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			group.getGroupId());

		User guestUser = _userLocalService.getGuestUser(
			objectEntryFolder.getCompanyId());

		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _execute(
					_getMockHttpServletRequest(
						group, layout, objectEntryFolder, Constants.SUBSCRIBE),
					guestUser));

		Assert.assertArrayEquals(
			new String[] {ActionKeys.SUBSCRIBE}, mustHavePermission.actionId);
		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			mustHavePermission.resourceId);
		Assert.assertEquals(
			ObjectEntryFolder.class.getName(), mustHavePermission.resourceName);

		Assert.assertFalse(
			_isSubscribed(objectEntryFolder, guestUser.getUserId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject(filter = "path=/cms/subscribe_folder")
	private StrutsAction _subscribeFolderStrutsAction;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}