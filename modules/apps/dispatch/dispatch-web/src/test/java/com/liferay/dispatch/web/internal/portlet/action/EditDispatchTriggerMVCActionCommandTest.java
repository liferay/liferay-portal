/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.web.internal.security.permisison.resource.DispatchTriggerPermission;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Magdalena Jedraszak
 */
public class EditDispatchTriggerMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_originalDispatchTriggerModelResourcePermissionSnapshot =
			ReflectionTestUtil.getFieldValue(
				DispatchTriggerPermission.class,
				"_dispatchTriggerModelResourcePermissionSnapshot");

		ReflectionTestUtil.setFieldValue(
			DispatchTriggerPermission.class,
			"_dispatchTriggerModelResourcePermissionSnapshot",
			new Snapshot<ModelResourcePermission<DispatchTrigger>>(
				DispatchTriggerPermission.class,
				Snapshot.cast(ModelResourcePermission.class)) {

				@Override
				public ModelResourcePermission<DispatchTrigger> get() {
					return _dispatchTriggerModelResourcePermission;
				}

			});

		ReflectionTestUtil.setFieldValue(
			_editDispatchTriggerMVCActionCommand,
			"_dispatchTriggerLocalService", _dispatchTriggerLocalService);
		ReflectionTestUtil.setFieldValue(
			_editDispatchTriggerMVCActionCommand, "_messageBus", _messageBus);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(_permissionChecker);

		_mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			DispatchTriggerPermission.class,
			"_dispatchTriggerModelResourcePermissionSnapshot",
			_originalDispatchTriggerModelResourcePermissionSnapshot);
	}

	@Test
	public void testCheckPermission() throws Exception {
		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTriggerModelResourcePermission.contains(
				_permissionChecker, dispatchTriggerId, ActionKeys.UPDATE)
		).thenReturn(
			true
		);

		ReflectionTestUtil.invoke(
			_editDispatchTriggerMVCActionCommand, "_checkPermission",
			new Class<?>[] {ActionRequest.class, long.class},
			_mockLiferayPortletActionRequest, dispatchTriggerId);
	}

	@Test
	public void testRunProcessWithoutUpdatePermission() throws Exception {
		long dispatchTriggerId = RandomTestUtil.randomLong();

		_mockLiferayPortletActionRequest.addParameter(
			"dispatchTriggerId", String.valueOf(dispatchTriggerId));

		PrincipalException.MustHavePermission principalException =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> ReflectionTestUtil.invoke(
					_editDispatchTriggerMVCActionCommand, "_runProcess",
					new Class<?>[] {ActionRequest.class},
					_mockLiferayPortletActionRequest));

		Assert.assertEquals(dispatchTriggerId, principalException.resourceId);
		Assert.assertEquals(
			DispatchTrigger.class.getName(), principalException.resourceName);

		Mockito.verify(
			_messageBus, Mockito.never()
		).sendMessage(
			Mockito.anyString(), Mockito.any(Message.class)
		);
	}

	@Test
	public void testSendMessagePropagatesCompanyId() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTrigger.getCompanyId()
		).thenReturn(
			companyId
		);

		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTriggerLocalService.getDispatchTrigger(dispatchTriggerId)
		).thenReturn(
			_dispatchTrigger
		);

		ReflectionTestUtil.invoke(
			_editDispatchTriggerMVCActionCommand, "_sendMessage",
			new Class<?>[] {long.class}, dispatchTriggerId);

		ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(
			Message.class);

		Mockito.verify(
			_messageBus
		).sendMessage(
			Mockito.eq(DispatchConstants.EXECUTOR_DESTINATION_NAME),
			argumentCaptor.capture()
		);

		Message message = argumentCaptor.getValue();

		Assert.assertEquals(companyId, message.getLong("companyId"));
	}

	private final DispatchTrigger _dispatchTrigger = Mockito.mock(
		DispatchTrigger.class);
	private final DispatchTriggerLocalService _dispatchTriggerLocalService =
		Mockito.mock(DispatchTriggerLocalService.class);
	private final ModelResourcePermission<DispatchTrigger>
		_dispatchTriggerModelResourcePermission = Mockito.mock(
			ModelResourcePermission.class);
	private final EditDispatchTriggerMVCActionCommand
		_editDispatchTriggerMVCActionCommand =
			new EditDispatchTriggerMVCActionCommand();
	private final MessageBus _messageBus = Mockito.mock(MessageBus.class);
	private final MockLiferayPortletActionRequest
		_mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();
	private Snapshot<ModelResourcePermission<DispatchTrigger>>
		_originalDispatchTriggerModelResourcePermissionSnapshot;
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}