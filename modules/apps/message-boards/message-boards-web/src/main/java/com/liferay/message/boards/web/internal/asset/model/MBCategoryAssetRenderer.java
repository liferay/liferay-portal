/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Sergio González
 * @author Jonathan Lee
 */
public class MBCategoryAssetRenderer extends BaseJSPAssetRenderer<MBCategory> {

	public MBCategoryAssetRenderer(
		MBCategory category,
		ModelResourcePermission<MBCategory> categoryModelResourcePermission) {

		_category = category;
		_categoryModelResourcePermission = categoryModelResourcePermission;
	}

	@Override
	public MBCategory getAssetObject() {
		return _category;
	}

	@Override
	public String getClassName() {
		return MBCategory.class.getName();
	}

	@Override
	public long getClassPK() {
		return _category.getCategoryId();
	}

	@Override
	public long getGroupId() {
		return _category.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/message_boards/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public int getStatus() {
		return _category.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _category.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _category.getName();
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(_category.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, MBPortletKeys.MESSAGE_BOARDS, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/message_boards/edit_category"
		).setParameter(
			"mbCategoryId", _category.getCategoryId()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<MBCategory> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCRenderCommandName(
			"/message_boards/view_category"
		).setParameter(
			"mbCategoryId", _category.getCategoryId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		return getURLViewInContext(
			liferayPortletRequest, noSuchEntryRedirect,
			"/message_boards/find_category", "mbCategoryId",
			_category.getCategoryId());
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, "/message_boards/find_category",
			"mbCategoryId", _category.getCategoryId());
	}

	@Override
	public long getUserId() {
		return _category.getUserId();
	}

	@Override
	public String getUserName() {
		return _category.getUserName();
	}

	@Override
	public String getUuid() {
		return _category.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _categoryModelResourcePermission.contains(
			permissionChecker, _category, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _categoryModelResourcePermission.contains(
			permissionChecker, _category, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_CATEGORY, _category);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private final MBCategory _category;
	private final ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

}