/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jan Brychta
 */
@Component(
	property = "path=/cms/subscribe_folder", service = StrutsAction.class
)
public class SubscribeFolderStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long objectEntryFolderId = ParamUtil.getLong(
			httpServletRequest, "classPK");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_modelResourcePermission.check(
			GuestOrUserUtil.getPermissionChecker(), objectEntryFolderId,
			ActionKeys.SUBSCRIBE);

		String cmd = ParamUtil.getString(httpServletRequest, "cmd");

		if (cmd.equals(Constants.SUBSCRIBE)) {
			_objectEntryFolderLocalService.subscribeObjectEntryFolder(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
				objectEntryFolderId);
		}
		else {
			_objectEntryFolderLocalService.unsubscribeObjectEntryFolder(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
				objectEntryFolderId);
		}

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.toString(_jsonFactory.createJSONObject()));

		return null;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder> _modelResourcePermission;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}