/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.web.internal.security.permission.resource.WikiPagePermission;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Jorge Ferrer
 * @author Raymond Augé
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + WikiPortletKeys.WIKI,
	service = AssetRendererFactory.class
)
public class WikiPageAssetRendererFactory
	extends BaseAssetRendererFactory<WikiPage> {

	public static final String TYPE = "wiki";

	public WikiPageAssetRendererFactory() {
		setClassName(WikiPage.class.getName());
		setLinkable(true);
		setPortletId(WikiPortletKeys.WIKI);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<WikiPage> getAssetRenderer(long classPK, int type)
		throws PortalException {

		WikiPage page = _wikiPageLocalService.fetchWikiPage(classPK);

		if (page == null) {
			if (type == TYPE_LATEST_APPROVED) {
				try {
					page = _wikiPageLocalService.getPage(classPK, Boolean.TRUE);
				}
				catch (NoSuchPageException noSuchPageException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchPageException);
					}

					page = _wikiPageLocalService.getPage(
						classPK, (Boolean)null);
				}
			}
			else if (type == TYPE_LATEST) {
				page = _wikiPageLocalService.getPage(classPK, (Boolean)null);
			}
			else {
				throw new IllegalArgumentException(
					"Unknown asset renderer type " + type);
			}
		}

		WikiPageAssetRenderer wikiPageAssetRenderer = new WikiPageAssetRenderer(
			_htmlParser, _trashHelper, _wikiEngineRenderer, page);

		wikiPageAssetRenderer.setAssetDisplayPageFriendlyURLProvider(
			_assetDisplayPageFriendlyURLProvider);
		wikiPageAssetRenderer.setAssetRendererType(type);
		wikiPageAssetRenderer.setServletContext(_servletContext);

		return wikiPageAssetRenderer;
	}

	@Override
	public String getClassName() {
		return WikiPage.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "wiki";
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
				WikiPortletKeys.WIKI, PortletRequest.RENDER_PHASE);

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

		return WikiPagePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Override
	public boolean isActive(long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-35013")) {
			return false;
		}

		return super.isActive(companyId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageAssetRendererFactory.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private HtmlParser _htmlParser;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.wiki.web)")
	private ServletContext _servletContext;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}