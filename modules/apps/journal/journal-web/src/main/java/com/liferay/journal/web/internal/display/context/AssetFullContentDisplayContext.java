/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class AssetFullContentDisplayContext {

	public AssetFullContentDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public JournalArticleDisplay getJournalArticleDisplay() {
		if (_journalArticleDisplay != null) {
			return _journalArticleDisplay;
		}

		_journalArticleDisplay =
			(JournalArticleDisplay)_httpServletRequest.getAttribute(
				WebKeys.JOURNAL_ARTICLE_DISPLAY);

		return _journalArticleDisplay;
	}

	public PortletURL getPaginationURL() {
		String pageRedirect = ParamUtil.getString(
			_httpServletRequest, "redirect");

		PortletURL currentURLObj = _getCurrentURLObj();

		if (Validator.isNull(pageRedirect) && (currentURLObj != null)) {
			pageRedirect = _currentURLObj.toString();
		}

		PortletURL portletURL = _getPortletURL();

		if (portletURL == null) {
			return null;
		}

		JournalArticleDisplay journalArticleDisplay =
			getJournalArticleDisplay();

		return PortletURLBuilder.create(
			portletURL
		).setMVCPath(
			"/view_content.jsp"
		).setRedirect(
			pageRedirect
		).setParameter(
			"cur", ParamUtil.getInteger(_httpServletRequest, "cur")
		).setParameter(
			"groupId", journalArticleDisplay.getGroupId()
		).setParameter(
			"page", (String)null
		).setParameter(
			"type",
			() -> {
				AssetRendererFactory<?> assetRendererFactory =
					_getAssetRendererFactory();

				return assetRendererFactory.getType();
			}
		).setParameter(
			"urlTitle", journalArticleDisplay.getUrlTitle()
		).buildPortletURL();
	}

	private AssetRendererFactory<?> _getAssetRendererFactory() {
		if (_assetRendererFactory != null) {
			return _assetRendererFactory;
		}

		_assetRendererFactory =
			(AssetRendererFactory<?>)_httpServletRequest.getAttribute(
				WebKeys.ASSET_RENDERER_FACTORY);

		return _assetRendererFactory;
	}

	private PortletURL _getCurrentURLObj() {
		if ((_liferayPortletRequest == null) ||
			(_liferayPortletResponse == null)) {

			return null;
		}

		Portlet portlet = _liferayPortletResponse.getPortlet();

		if (Objects.equals(
				portlet.getPortletId(),
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET)) {

			return null;
		}

		if (_currentURLObj != null) {
			return _currentURLObj;
		}

		_currentURLObj = PortletURLUtil.getCurrent(
			_liferayPortletRequest, _liferayPortletResponse);

		return _currentURLObj;
	}

	private PortletURL _getPortletURL() {
		PortletURL portletURL = _getCurrentURLObj();

		if (portletURL == null) {
			return null;
		}

		try {
			return PortletURLUtil.clone(
				_currentURLObj, _liferayPortletResponse);
		}
		catch (PortletException portletException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portletException);
			}

			return PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setParameters(
				_currentURLObj.getParameterMap()
			).buildPortletURL();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetFullContentDisplayContext.class);

	private AssetRendererFactory<?> _assetRendererFactory;
	private PortletURL _currentURLObj;
	private final HttpServletRequest _httpServletRequest;
	private JournalArticleDisplay _journalArticleDisplay;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}