/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
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

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-103654")) {

			throw new UnsupportedOperationException();
		}

		String cmd = ParamUtil.getString(httpServletRequest, "cmd");

		long objectEntryFolderId = ParamUtil.getLong(
			httpServletRequest, "objectEntryFolderId");

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.getObjectEntryFolder(
				objectEntryFolderId);

		if (cmd.equals(Constants.SUBSCRIBE)) {
			_objectEntryFolderService.subscribeObjectEntryFolder(
				objectEntryFolder.getGroupId(), objectEntryFolderId);
		}
		else {
			_objectEntryFolderService.unsubscribeObjectEntryFolder(
				objectEntryFolder.getGroupId(), objectEntryFolderId);
		}

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.toString(_jsonFactory.createJSONObject()));

		return null;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

}