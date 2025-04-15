/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;
import java.util.Objects;

/**
 * @author Juan Fernández
 * @author Shuyang Zhou
 */
public class AssetLinksTag extends IncludeTag {

	public long getAssetEntryId() {
		return _assetEntryId;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public boolean getViewInContext() {
		return _viewInContext;
	}

	public boolean isViewInContext() {
		return _viewInContext;
	}

	public void setAssetEntryId(long assetEntryId) {
		_assetEntryId = assetEntryId;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setViewInContext(boolean viewInContext) {
		_viewInContext = viewInContext;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_assetEntryId = 0;
		_className = StringPool.BLANK;
		_classPK = 0;
		_page = _PAGE;
		_portletURL = null;
		_viewInContext = true;
	}

	@Override
	protected String getPage() {
		return _page;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (_page == null) {
			return;
		}

		if ((_assetEntryId <= 0) && (_classPK > 0)) {
			try {
				AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
					_className, _classPK);

				if (assetEntry != null) {
					_assetEntryId = assetEntry.getEntryId();
				}
			}
			catch (SystemException systemException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(systemException);
				}
			}
		}

		if (_assetEntryId <= 0) {
			_page = null;

			return;
		}

		List<Tuple> assetLinkEntries = null;

		try {
			assetLinkEntries = _getAssetLinkEntries();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (ListUtil.isEmpty(assetLinkEntries)) {
			_page = null;

			return;
		}

		httpServletRequest.setAttribute(
			"liferay-asset:asset-links:assetLinkEntries", assetLinkEntries);
	}

	private List<Tuple> _getAssetLinkEntries() throws Exception {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(portletRequest);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(portletResponse);

		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getDirectLinks(
			_assetEntryId, false);

		return TransformUtil.transform(
			assetLinks,
			assetLink -> {
				AssetEntry assetLinkEntry = null;

				if (assetLink.getEntryId1() == _assetEntryId) {
					assetLinkEntry = AssetEntryLocalServiceUtil.getEntry(
						assetLink.getEntryId2());
				}
				else {
					assetLinkEntry = AssetEntryLocalServiceUtil.getEntry(
						assetLink.getEntryId1());
				}

				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassName(
							assetLinkEntry.getClassName());

				if (assetRendererFactory == null) {
					if (_log.isWarnEnabled()) {
						String className = PortalUtil.getClassName(
							assetLinkEntry.getClassNameId());

						_log.warn(
							"No asset renderer factory found for class " +
								className);
					}

					return null;
				}

				if (!assetRendererFactory.isActive(
						themeDisplay.getCompanyId())) {

					return null;
				}

				AssetRenderer<?> assetRenderer =
					assetRendererFactory.getAssetRenderer(
						assetLinkEntry.getClassPK());

				if (assetRenderer == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"No asset renderer found for class PK " +
								assetLinkEntry.getClassPK());
					}

					return null;
				}

				if (!assetRenderer.hasViewPermission(
						themeDisplay.getPermissionChecker()) ||
					!(assetLinkEntry.isVisible() ||
					  (assetRenderer.getStatus() ==
						  WorkflowConstants.STATUS_SCHEDULED))) {

					return null;
				}

				Group group = GroupLocalServiceUtil.getGroup(
					assetLinkEntry.getGroupId());

				Group scopeGroup = themeDisplay.getScopeGroup();

				if (group.isStaged() &&
					(group.isStagingGroup() ^ scopeGroup.isStagingGroup())) {

					return null;
				}

				String viewURL = _getViewURL(
					assetLinkEntry, assetRenderer,
					assetRendererFactory.getType(), liferayPortletRequest,
					liferayPortletResponse, themeDisplay);

				return new Tuple(assetLinkEntry, viewURL);
			});
	}

	private String _getViewURL(
			AssetEntry assetLinkEntry, AssetRenderer<?> assetRenderer,
			String type, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			ThemeDisplay themeDisplay)
		throws Exception {

		String viewURL = null;

		PortletURL viewAssetURL = null;

		if (_portletURL != null) {
			viewAssetURL = PortletURLUtil.clone(
				_portletURL, liferayPortletResponse);
		}
		else {
			viewAssetURL = PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					getRequest(), assetRenderer.getClassName(),
					PortletProvider.Action.VIEW)
			).setRedirect(
				themeDisplay.getURLCurrent()
			).setWindowState(
				WindowState.MAXIMIZED
			).buildPortletURL();
		}

		viewAssetURL.setParameter(
			"assetEntryId", String.valueOf(assetLinkEntry.getEntryId()));
		viewAssetURL.setParameter("showRelatedAssets", Boolean.TRUE.toString());
		viewAssetURL.setParameter("type", type);

		String urlTitle = assetRenderer.getUrlTitle(themeDisplay.getLocale());

		if (Validator.isNotNull(urlTitle)) {
			if (assetRenderer.getGroupId() != themeDisplay.getSiteGroupId()) {
				viewAssetURL.setParameter(
					"groupId", String.valueOf(assetRenderer.getGroupId()));
			}

			viewAssetURL.setParameter("urlTitle", urlTitle);
		}

		if (_viewInContext) {
			String noSuchEntryRedirect = viewAssetURL.toString();

			String viewInContextURL = assetRenderer.getURLViewInContext(
				liferayPortletRequest, liferayPortletResponse,
				noSuchEntryRedirect);

			if (Validator.isNotNull(viewInContextURL) &&
				!Objects.equals(viewInContextURL, noSuchEntryRedirect)) {

				viewInContextURL = HttpComponentsUtil.setParameter(
					viewInContextURL, "inheritRedirect", Boolean.TRUE);
				viewInContextURL = HttpComponentsUtil.setParameter(
					viewInContextURL, "redirect", themeDisplay.getURLCurrent());
			}

			viewURL = viewInContextURL;
		}

		if (Validator.isNull(viewURL)) {
			viewURL = viewAssetURL.toString();
		}

		return viewURL;
	}

	private static final String _PAGE = "/asset_links/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(AssetLinksTag.class);

	private long _assetEntryId;
	private String _className = StringPool.BLANK;
	private long _classPK;
	private String _page = _PAGE;
	private PortletURL _portletURL;
	private boolean _viewInContext = true;

}