/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Javier Gamarra
 */
public class MBCategoryAssetRenderer extends BaseJSPAssetRenderer<MBCategory> {

	public MBCategoryAssetRenderer(
		Company company, String historyRouterBasePath, MBCategory mbCategory,
		ModelResourcePermission<MBCategory> mbCategoryModelResourcePermission) {

		_company = company;
		_historyRouterBasePath = historyRouterBasePath;
		_mbCategory = mbCategory;
		_mbCategoryModelResourcePermission = mbCategoryModelResourcePermission;
	}

	@Override
	public MBCategory getAssetObject() {
		return _mbCategory;
	}

	@Override
	public String getClassName() {
		return MBCategory.class.getName();
	}

	@Override
	public long getClassPK() {
		return _mbCategory.getCategoryId();
	}

	@Override
	public long getGroupId() {
		return _mbCategory.getGroupId();
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
		return _mbCategory.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _mbCategory.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _mbCategory.getName();
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return null;
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws PortalException {

		return StringBundler.concat(
			_company.getPortalURL(_mbCategory.getGroupId()),
			_historyRouterBasePath, "/questions/", _mbCategory.getCategoryId());
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		return null;
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		return null;
	}

	@Override
	public long getUserId() {
		return _mbCategory.getUserId();
	}

	@Override
	public String getUserName() {
		return _mbCategory.getUserName();
	}

	@Override
	public String getUuid() {
		return _mbCategory.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _mbCategoryModelResourcePermission.contains(
			permissionChecker, _mbCategory, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _mbCategoryModelResourcePermission.contains(
			permissionChecker, _mbCategory, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_CATEGORY, _mbCategory);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private final Company _company;
	private final String _historyRouterBasePath;
	private final MBCategory _mbCategory;
	private final ModelResourcePermission<MBCategory>
		_mbCategoryModelResourcePermission;

}