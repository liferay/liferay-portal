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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
	service = AssetRendererFactory.class
)
public class LayoutAssetRendererFactory
	extends BaseAssetRendererFactory<Layout> {

	public static final String TYPE = "layout";

	public LayoutAssetRendererFactory() {
		setClassName(Layout.class.getName());
		setSelectable(false);
		setPortletId(LayoutAdminPortletKeys.GROUP_PAGES);
	}

	@Override
	public AssetEntry getAssetEntry(Layout layout) throws PortalException {
		AssetEntry assetEntry = _assetEntryLocalService.createAssetEntry(
			layout.getPlid());

		assetEntry.setGroupId(layout.getGroupId());
		assetEntry.setCompanyId(layout.getCompanyId());
		assetEntry.setUserId(layout.getUserId());
		assetEntry.setUserName(layout.getUserName());
		assetEntry.setCreateDate(layout.getCreateDate());
		assetEntry.setClassNameId(
			_portal.getClassNameId(Layout.class.getName()));
		assetEntry.setClassPK(layout.getPlid());
		assetEntry.setTitle(layout.getHTMLTitle(LocaleUtil.getSiteDefault()));

		return assetEntry;
	}

	@Override
	public AssetEntry getAssetEntry(long assetEntryId) throws PortalException {
		return getAssetEntry(getClassName(), assetEntryId);
	}

	@Override
	public AssetEntry getAssetEntry(String className, long classPK)
		throws PortalException {

		return getAssetEntry(_layoutLocalService.getLayout(classPK));
	}

	@Override
	public AssetRenderer<Layout> getAssetRenderer(long plid, int type)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return null;
		}

		LayoutAssetRenderer layoutAssetRenderer = new LayoutAssetRenderer(
			layout);

		layoutAssetRenderer.setAssetRendererType(type);
		layoutAssetRenderer.setServletContext(_servletContext);

		return layoutAssetRenderer;
	}

	@Override
	public String getClassName() {
		return Layout.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "page";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isSearchable() {
		return true;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

}