/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.web.internal.struts;

import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "path=/portal/get_page_version_preview",
	service = StrutsAction.class
)
public class GetPageVersionPreviewStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.
				fetchLayoutContentVersionByExternalReferenceCode(
					ParamUtil.getString(
						httpServletRequest, "externalReferenceCode"),
					ParamUtil.getLong(httpServletRequest, "groupId"));

		if (layoutContentVersion == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return null;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutContentVersion.getPlid());

		if ((layout == null) || !layout.isDraftLayout() ||
			!layout.isTypeContent()) {

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-10622") ||
			!_layoutPermission.contains(
				PermissionCheckerFactoryUtil.create(themeDisplay.getRealUser()),
				layout, ActionKeys.UPDATE)) {

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return null;
		}

		LayoutContentVersionPreview layoutContentVersionPreview =
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					layoutContentVersion.getLayoutContentVersionId(),
					ParamUtil.getString(httpServletRequest, "languageId"),
					ParamUtil.getString(
						httpServletRequest, "segmentsExperienceERC"));

		if (layoutContentVersionPreview == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return null;
		}

		ServletResponseUtil.write(
			httpServletResponse, layoutContentVersionPreview.getHtml());

		return null;
	}

	@Reference
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Reference
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

}