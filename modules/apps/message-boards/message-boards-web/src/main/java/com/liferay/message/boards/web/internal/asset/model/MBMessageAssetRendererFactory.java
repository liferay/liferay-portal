/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HtmlParser;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = AssetRendererFactory.class
)
public class MBMessageAssetRendererFactory
	extends BaseAssetRendererFactory<MBMessage> {

	public static final String TYPE = "message";

	public MBMessageAssetRendererFactory() {
		setLinkable(true);
		setPortletId(MBPortletKeys.MESSAGE_BOARDS);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<MBMessage> getAssetRenderer(long classPK, int type)
		throws PortalException {

		MBMessageAssetRenderer mbMessageAssetRenderer =
			new MBMessageAssetRenderer(
				_discussionPermission, _htmlParser,
				_mbMessageLocalService.getMessage(classPK),
				_messageModelResourcePermission);

		mbMessageAssetRenderer.setAssetDisplayPageFriendlyURLProvider(
			_assetDisplayPageFriendlyURLProvider);
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

		LiferayPortletURL liferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(
				MBPortletKeys.MESSAGE_BOARDS, PortletRequest.RENDER_PHASE);

		try {
			liferayPortletURL.setWindowState(windowState);
		}
		catch (WindowStateException windowStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(windowStateException);
			}
		}

		return liferayPortletURL;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _messageModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBMessageAssetRendererFactory.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBMessage)"
	)
	private ModelResourcePermission<MBMessage> _messageModelResourcePermission;

}