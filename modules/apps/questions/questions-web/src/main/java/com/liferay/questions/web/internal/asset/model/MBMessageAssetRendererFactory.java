/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

/**
 * @author Javier Gamarra
 */
public class MBMessageAssetRendererFactory
	extends BaseAssetRendererFactory<MBMessage> {

	public static final String TYPE = "message";

	public MBMessageAssetRendererFactory(
		CompanyLocalService companyLocalService,
		DiscussionPermission discussionPermission, String historyRouterPath,
		MBMessageLocalService mbMessageLocalService,
		ModelResourcePermission<MBMessage> mbMessageModelResourcePermission) {

		_companyLocalService = companyLocalService;
		_discussionPermission = discussionPermission;
		_historyRouterPath = historyRouterPath;
		_mbMessageLocalService = mbMessageLocalService;
		_mbMessageModelResourcePermission = mbMessageModelResourcePermission;

		setLinkable(true);
		setPortletId(MBPortletKeys.MESSAGE_BOARDS);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<MBMessage> getAssetRenderer(long classPK, int type)
		throws PortalException {

		MBMessage mbMessage = _mbMessageLocalService.getMessage(classPK);

		MBMessageAssetRenderer mbMessageAssetRenderer =
			new MBMessageAssetRenderer(
				_companyLocalService.getCompany(mbMessage.getCompanyId()),
				_discussionPermission, _historyRouterPath, mbMessage,
				_mbMessageModelResourcePermission);

		mbMessageAssetRenderer.setAssetRendererType(type);

		return mbMessageAssetRenderer;
	}

	@Override
	public String getClassName() {
		return MBMessage.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "comments";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		return null;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _mbMessageModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private final CompanyLocalService _companyLocalService;
	private final DiscussionPermission _discussionPermission;
	private final String _historyRouterPath;
	private final MBMessageLocalService _mbMessageLocalService;
	private final ModelResourcePermission<MBMessage>
		_mbMessageModelResourcePermission;

}