/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinderRegistryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.configuration.WikiGroupServiceOverriddenConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.web.internal.security.permission.resource.WikiPagePermission;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Julio Camarero
 * @author Sergio González
 */
public class WikiPageAssetRenderer
	extends BaseJSPAssetRenderer<WikiPage> implements TrashRenderer {

	public static final String TYPE = "wiki_page";

	public static long getClassPK(WikiPage page) {
		if (!page.isApproved() && !page.isDraft() && !page.isPending() &&
			!page.isInTrash() &&
			(page.getVersion() != WikiPageConstants.VERSION_DEFAULT)) {

			return page.getPageId();
		}

		return page.getResourcePrimKey();
	}

	public WikiPageAssetRenderer(
		HtmlParser htmlParser, TrashHelper trashHelper,
		WikiEngineRenderer wikiEngineRenderer, WikiPage page) {

		_htmlParser = htmlParser;
		_trashHelper = trashHelper;
		_wikiEngineRenderer = wikiEngineRenderer;
		_page = page;
	}

	@Override
	public WikiPage getAssetObject() {
		return _page;
	}

	@Override
	public String getClassName() {
		return WikiPage.class.getName();
	}

	@Override
	public long getClassPK() {
		return getClassPK(_page);
	}

	@Override
	public String getDiscussionPath() {
		if (_wikiGroupServiceOverriddenConfiguration == null) {
			try {
				_wikiGroupServiceOverriddenConfiguration =
					ConfigurationProviderUtil.getConfiguration(
						WikiGroupServiceOverriddenConfiguration.class,
						new GroupServiceSettingsLocator(
							_page.getGroupId(), WikiConstants.SERVICE_NAME));
			}
			catch (Exception exception) {
				_log.error(exception);

				return null;
			}
		}

		if (_wikiGroupServiceOverriddenConfiguration.pageCommentsEnabled()) {
			return "edit_page_discussion";
		}

		return null;
	}

	@Override
	public long getGroupId() {
		return _page.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/wiki/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		return WikiPortletKeys.WIKI;
	}

	@Override
	public int getStatus() {
		return _page.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			return _htmlParser.extractText(
				_wikiEngineRenderer.convert(_page, null, null, null));
		}
		catch (Exception exception) {
			_log.error(exception);

			return _page.getContent();
		}
	}

	@Override
	public String getTitle(Locale locale) {
		if (!_page.isInTrash() || (_trashHelper == null)) {
			return _page.getTitle();
		}

		return _trashHelper.getOriginalTitle(_page.getTitle());
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		Group group = GroupLocalServiceUtil.fetchGroup(_page.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, WikiPortletKeys.WIKI, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/wiki/edit_page"
		).setParameter(
			"nodeId", _page.getNodeId()
		).setParameter(
			"title", _page.getTitle()
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLExport(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, WikiPortletKeys.WIKI
		).setActionName(
			"/wiki/export_page"
		).setParameter(
			"nodeId", _page.getNodeId()
		).setParameter(
			"nodeName",
			() -> {
				WikiNode node = _page.getNode();

				return node.getName();
			}
		).setParameter(
			"title", _page.getTitle()
		).setParameter(
			"version", _page.getVersion()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<WikiPage> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCRenderCommandName(
			"/wiki/view"
		).setParameter(
			"nodeId", _page.getNodeId()
		).setParameter(
			"title", _page.getTitle()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public PortletURL getURLViewDiffs(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		WikiPage previousVersionPage =
			WikiPageLocalServiceUtil.getPreviousVersionPage(_page);

		if (previousVersionPage.getVersion() == _page.getVersion()) {
			return null;
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, WikiPortletKeys.WIKI,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/wiki/compare_versions"
		).setParameter(
			"groupId", _page.getGroupId()
		).setParameter(
			"nodeId", _page.getNodeId()
		).setParameter(
			"sourceVersion", previousVersionPage.getVersion()
		).setParameter(
			"targetVersion", _page.getVersion()
		).setParameter(
			"title", _page.getTitle()
		).buildPortletURL();
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

		if (!_hasViewInContextGroupLayout(_page.getGroupId(), themeDisplay)) {
			return null;
		}

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, "/wiki/find_page",
			"pageResourcePrimKey", _page.getResourcePrimKey());
	}

	@Override
	public long getUserId() {
		return _page.getUserId();
	}

	@Override
	public String getUserName() {
		return _page.getUserName();
	}

	@Override
	public String getUuid() {
		return _page.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return WikiPagePermission.contains(
			permissionChecker, _page, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return WikiPagePermission.contains(
			permissionChecker, _page, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(WikiWebKeys.WIKI_PAGE, _page);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isConvertible() {
		return true;
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
					getClassName());

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
		WikiPageAssetRenderer.class);

	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final HtmlParser _htmlParser;
	private final WikiPage _page;
	private final TrashHelper _trashHelper;
	private final WikiEngineRenderer _wikiEngineRenderer;
	private WikiGroupServiceOverriddenConfiguration
		_wikiGroupServiceOverriddenConfiguration;

}