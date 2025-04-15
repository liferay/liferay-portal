/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
	service = AssetRendererFactory.class
)
public class BlogsEntryAssetRendererFactory
	extends BaseAssetRendererFactory<BlogsEntry> {

	public static final String TYPE = "blog";

	public BlogsEntryAssetRendererFactory() {
		setClassName(BlogsEntry.class.getName());
		setLinkable(true);
		setPortletId(BlogsPortletKeys.BLOGS);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<BlogsEntry> getAssetRenderer(long classPK, int type)
		throws PortalException {

		BlogsEntryAssetRenderer blogsEntryAssetRenderer =
			new BlogsEntryAssetRenderer(
				_blogsEntryLocalService.getEntry(classPK),
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader());

		blogsEntryAssetRenderer.setAssetDisplayPageFriendlyURLProvider(
			_assetDisplayPageFriendlyURLProvider);
		blogsEntryAssetRenderer.setAssetRendererType(type);
		blogsEntryAssetRenderer.setServletContext(_servletContext);

		return blogsEntryAssetRenderer;
	}

	@Override
	public AssetRenderer<BlogsEntry> getAssetRenderer(
			long groupId, String urlTitle)
		throws PortalException {

		BlogsEntryAssetRenderer blogsEntryAssetRenderer =
			new BlogsEntryAssetRenderer(
				_blogsEntryLocalService.getEntry(groupId, urlTitle),
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader());

		blogsEntryAssetRenderer.setServletContext(_servletContext);

		return blogsEntryAssetRenderer;
	}

	@Override
	public String getClassName() {
		return BlogsEntry.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "blogs";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLAdd(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, getGroup(liferayPortletRequest),
				BlogsPortletKeys.BLOGS, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/blogs/edit_entry"
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		LiferayPortletURL liferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(
				BlogsPortletKeys.BLOGS, PortletRequest.RENDER_PHASE);

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
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, long groupId, long classTypeId) {

		return _portletResourcePermission.contains(
			permissionChecker, groupId, ActionKeys.ADD_ENTRY);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _blogsEntryModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryAssetRendererFactory.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference(target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)")
	private ModelResourcePermission<BlogsEntry>
		_blogsEntryModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(target = "(resource.name=" + BlogsConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.blogs.web)")
	private ServletContext _servletContext;

}