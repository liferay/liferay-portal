/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

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
 * @author Jonathan Lee
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = AssetRendererFactory.class
)
public class MBCategoryAssetRendererFactory
	extends BaseAssetRendererFactory<MBCategory> {

	public static final String TYPE = "category";

	public MBCategoryAssetRendererFactory() {
		setCategorizable(false);
		setPortletId(MBPortletKeys.MESSAGE_BOARDS);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<MBCategory> getAssetRenderer(long classPK, int type)
		throws PortalException {

		MBCategory category = _mbCategoryLocalService.getMBCategory(classPK);

		MBCategoryAssetRenderer mbCategoryAssetRenderer =
			new MBCategoryAssetRenderer(
				category, _categoryModelResourcePermission);

		mbCategoryAssetRenderer.setAssetRendererType(type);

		return mbCategoryAssetRenderer;
	}

	@Override
	public String getClassName() {
		return MBCategory.class.getName();
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

		MBCategory category = _mbCategoryLocalService.getMBCategory(classPK);

		return _categoryModelResourcePermission.contains(
			permissionChecker, category, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBCategoryAssetRendererFactory.class);

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

}