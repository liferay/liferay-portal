/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

/**
 * @author Javier Gamarra
 */
public class MBCategoryAssetRendererFactory
	extends BaseAssetRendererFactory<MBCategory> {

	public static final String TYPE = "category";

	public MBCategoryAssetRendererFactory(
		CompanyLocalService companyLocalService, String historyRouterBasePath,
		MBCategoryLocalService mbCategoryLocalService,
		ModelResourcePermission<MBCategory> mbCategoryModelResourcePermission) {

		_companyLocalService = companyLocalService;
		_historyRouterBasePath = historyRouterBasePath;
		_mbCategoryLocalService = mbCategoryLocalService;
		_mbCategoryModelResourcePermission = mbCategoryModelResourcePermission;

		setCategorizable(false);
		setPortletId(MBPortletKeys.MESSAGE_BOARDS);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<MBCategory> getAssetRenderer(long classPK, int type)
		throws PortalException {

		MBCategory mbCategory = _mbCategoryLocalService.getMBCategory(classPK);

		Company company = _companyLocalService.getCompany(
			mbCategory.getCompanyId());

		MBCategoryAssetRenderer mbCategoryAssetRenderer =
			new MBCategoryAssetRenderer(
				company, _historyRouterBasePath, mbCategory,
				_mbCategoryModelResourcePermission);

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

		return null;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _mbCategoryModelResourcePermission.contains(
			permissionChecker, _mbCategoryLocalService.getMBCategory(classPK),
			actionId);
	}

	private final CompanyLocalService _companyLocalService;
	private final String _historyRouterBasePath;
	private final MBCategoryLocalService _mbCategoryLocalService;
	private final ModelResourcePermission<MBCategory>
		_mbCategoryModelResourcePermission;

}