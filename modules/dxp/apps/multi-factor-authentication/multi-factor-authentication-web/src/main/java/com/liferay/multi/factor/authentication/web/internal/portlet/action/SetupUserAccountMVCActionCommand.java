/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.spi.checker.setup.SetupMFAChecker;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/my_account/setup_user_account"
	},
	service = MVCActionCommand.class
)
public class SetupUserAccountMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SetupMFAChecker.class, "(service.id=*)",
			new PropertyServiceReferenceMapper<>("service.id"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long setupMFACheckerServiceId = ParamUtil.getLong(
			actionRequest, "setupMFACheckerServiceId");

		SetupMFAChecker setupMFAChecker = _serviceTrackerMap.getService(
			setupMFACheckerServiceId);

		if (setupMFAChecker == null) {
			_log.error(
				"Unable to get setup MFA checker " + setupMFACheckerServiceId);

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			throw new PrincipalException();
		}

		User selectedUser = _portal.getSelectedUser(actionRequest);

		if ((selectedUser.getUserId() != themeDisplay.getUserId()) &&
			!ModelResourcePermissionUtil.contains(
				_userModelResourcePermission,
				themeDisplay.getPermissionChecker(), 0,
				selectedUser.getUserId(), ActionKeys.IMPERSONATE)) {

			return;
		}

		if (ParamUtil.getBoolean(actionRequest, "removeExistingSetup")) {
			setupMFAChecker.removeExistingSetup(selectedUser.getUserId());
		}
		else if (!setupMFAChecker.setUp(
					_portal.getHttpServletRequest(actionRequest),
					selectedUser.getUserId())) {

			SessionErrors.add(actionRequest, "setupUserAccountFailed");
		}

		String redirect = _portal.escapeRedirect(
			ParamUtil.getString(actionRequest, "redirect"));

		if (Validator.isBlank(redirect)) {
			redirect = themeDisplay.getPortalURL();
		}

		actionResponse.sendRedirect(redirect);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SetupUserAccountMVCActionCommand.class);

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<Long, SetupMFAChecker> _serviceTrackerMap;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ModelResourcePermission<User> _userModelResourcePermission;

}