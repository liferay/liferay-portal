/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.security.permission.resource.BlogsEntryPermission;
import com.liferay.blogs.web.internal.util.BlogsEntryUtil;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinderRegistryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Jorge Ferrer
 * @author Juan Fernández
 * @author Sergio González
 * @author Zsolt Berentey
 */
public class BlogsEntryAssetRenderer
	extends BaseJSPAssetRenderer<BlogsEntry> implements TrashRenderer {

	public BlogsEntryAssetRenderer(
		BlogsEntry entry, ResourceBundleLoader resourceBundleLoader) {

		_entry = entry;
		_resourceBundleLoader = resourceBundleLoader;
	}

	@Override
	public BlogsEntry getAssetObject() {
		return _entry;
	}

	@Override
	public String getClassName() {
		return BlogsEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _entry.getEntryId();
	}

	@Override
	public String getDiscussionPath() {
		if (PropsValues.BLOGS_ENTRY_COMMENTS_ENABLED) {
			return "edit_entry_discussion";
		}

		return null;
	}

	@Override
	public long getGroupId() {
		return _entry.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/blogs/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<BlogsEntry> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _entry.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		int abstractLength = AssetHelper.ASSET_ENTRY_ABSTRACT_LENGTH;

		if (portletRequest != null) {
			abstractLength = GetterUtil.getInteger(
				portletRequest.getAttribute(
					WebKeys.ASSET_ENTRY_ABSTRACT_LENGTH),
				AssetHelper.ASSET_ENTRY_ABSTRACT_LENGTH);
		}

		String summary = HtmlUtil.escape(_entry.getDescription());

		if (Validator.isNull(summary)) {
			summary = StringUtil.shorten(
				HtmlUtil.stripHtml(_entry.getContent()), abstractLength);
		}

		return summary;
	}

	@Override
	public String getTitle(Locale locale) {
		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		return BlogsEntryUtil.getDisplayTitle(resourceBundle, _entry);
	}

	@Override
	public String getType() {
		return BlogsEntryAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(HttpServletRequest httpServletRequest) {
		Group group = GroupLocalServiceUtil.fetchGroup(_entry.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group, BlogsPortletKeys.BLOGS, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/blogs/edit_entry"
		).setParameter(
			"entryId", _entry.getEntryId()
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return getURLEdit(
			PortalUtil.getHttpServletRequest(liferayPortletRequest));
	}

	@Override
	public String getUrlTitle() {
		return _entry.getUrlTitle();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<BlogsEntry> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCRenderCommandName(
			"/blogs/view_entry"
		).setParameter(
			"entryId", _entry.getEntryId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws PortalException {

		if (_assetDisplayPageFriendlyURLProvider != null) {
			String friendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						getClassName(),
						new ClassPKInfoItemIdentifier(getClassPK())),
					themeDisplay);

			if (Validator.isNotNull(friendlyURL)) {
				return friendlyURL;
			}
		}

		if (!_hasViewInContextGroupLayout(_entry.getGroupId(), themeDisplay)) {
			return null;
		}

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, "/blogs/find_entry", "entryId",
			_entry.getEntryId());
	}

	@Override
	public long getUserId() {
		return _entry.getUserId();
	}

	@Override
	public String getUserName() {
		return _entry.getUserName();
	}

	@Override
	public String getUuid() {
		return _entry.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return BlogsEntryPermission.contains(
			permissionChecker, _entry, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return BlogsEntryPermission.contains(
			permissionChecker, _entry, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(WebKeys.BLOGS_ENTRY, _entry);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	public void setAssetDisplayPageFriendlyURLProvider(
		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
	}

	private boolean _hasViewInContextGroupLayout(
		long groupId, ThemeDisplay themeDisplay) {

		try {
			PortletLayoutFinder portletLayoutFinder =
				PortletLayoutFinderRegistryUtil.getPortletLayoutFinder(
					BlogsEntry.class.getName());

			PortletLayoutFinder.Result result = portletLayoutFinder.find(
				themeDisplay, groupId);

			if ((result == null) || Validator.isNull(result.getPortletId())) {
				return false;
			}

			return true;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryAssetRenderer.class);

	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final BlogsEntry _entry;
	private final ResourceBundleLoader _resourceBundleLoader;

}