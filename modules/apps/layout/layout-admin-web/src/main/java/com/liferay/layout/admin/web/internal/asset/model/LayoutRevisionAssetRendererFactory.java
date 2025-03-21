/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	property = "jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
	service = AssetRendererFactory.class
)
public class LayoutRevisionAssetRendererFactory
	extends BaseAssetRendererFactory<LayoutRevision> {

	public static final String TYPE = "layout_revision";

	public LayoutRevisionAssetRendererFactory() {
		setClassName(LayoutRevision.class.getName());
		setCategorizable(false);
		setPortletId(LayoutAdminPortletKeys.GROUP_PAGES);
		setSelectable(false);
	}

	@Override
	public AssetEntry getAssetEntry(LayoutRevision layoutRevision)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchLocalService.getLayoutSetBranch(
				layoutRevision.getLayoutSetBranchId());

		User user = _userLocalService.getUserById(layoutRevision.getUserId());

		AssetEntry assetEntry = _assetEntryLocalService.createAssetEntry(
			layoutRevision.getLayoutRevisionId());

		assetEntry.setGroupId(layoutRevision.getGroupId());
		assetEntry.setCompanyId(user.getCompanyId());
		assetEntry.setUserId(user.getUserId());
		assetEntry.setUserName(user.getFullName());
		assetEntry.setCreateDate(layoutRevision.getCreateDate());
		assetEntry.setClassNameId(
			_portal.getClassNameId(LayoutRevision.class.getName()));
		assetEntry.setClassPK(layoutRevision.getLayoutRevisionId());
		assetEntry.setTitle(
			StringBundler.concat(
				layoutRevision.getHTMLTitle(LocaleUtil.getSiteDefault()), " [",
				layoutSetBranch.getName(), "]"));

		return assetEntry;
	}

	@Override
	public AssetEntry getAssetEntry(long assetEntryId) throws PortalException {
		return getAssetEntry(getClassName(), assetEntryId);
	}

	@Override
	public AssetEntry getAssetEntry(String className, long classPK)
		throws PortalException {

		return getAssetEntry(
			_layoutRevisionLocalService.getLayoutRevision(classPK));
	}

	@Override
	public AssetRenderer<LayoutRevision> getAssetRenderer(
			long layoutRevisionId, int type)
		throws PortalException {

		LayoutRevisionAssetRenderer layoutRevisionAssetRenderer =
			new LayoutRevisionAssetRenderer(
				_layoutRevisionLocalService.getLayoutRevision(
					layoutRevisionId));

		layoutRevisionAssetRenderer.setAssetRendererType(type);
		layoutRevisionAssetRenderer.setServletContext(_servletContext);

		return layoutRevisionAssetRenderer;
	}

	@Override
	public String getClassName() {
		return LayoutRevision.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "edit-layout";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}