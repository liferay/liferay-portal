/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.Renderer;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Julio Camarero
 */
public class AssetDisplayTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			callSetAttributes();

			doInclude(null, false);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			clearDynamicAttributes();
			clearParams();
			clearProperties();

			cleanUpSetAttributes();

			setPage(null);
			setUseCustomPage(true);

			cleanUp();
		}
	}

	public int getAbstractLength() {
		return _abstractLength;
	}

	public AssetEntry getAssetEntry() {
		return _assetEntry;
	}

	public AssetRenderer<?> getAssetRenderer() {
		if (_renderer instanceof AssetRenderer<?>) {
			return (AssetRenderer<?>)_renderer;
		}

		return null;
	}

	public AssetRendererFactory<?> getAssetRendererFactory() {
		return _assetRendererFactory;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public Renderer getRenderer() {
		return _renderer;
	}

	public String getTemplate() {
		return _template;
	}

	public String getViewURL() {
		return _viewURL;
	}

	public boolean isShowComments() {
		return _showComments;
	}

	public boolean isShowExtraInfo() {
		return _showExtraInfo;
	}

	public boolean isShowHeader() {
		return _showHeader;
	}

	public void setAbstractLength(int abstractLength) {
		_abstractLength = abstractLength;
	}

	public void setAssetEntry(AssetEntry assetEntry) {
		_assetEntry = assetEntry;
	}

	public void setAssetRenderer(AssetRenderer<?> assetRenderer) {
		_renderer = assetRenderer;
	}

	public void setAssetRendererFactory(
		AssetRendererFactory<?> assetRendererFactory) {

		_assetRendererFactory = assetRendererFactory;
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

	public void setRenderer(Renderer renderer) {
		_renderer = renderer;
	}

	public void setShowComments(boolean showComments) {
		_showComments = showComments;
	}

	public void setShowExtraInfo(boolean showExtraInfo) {
		_showExtraInfo = showExtraInfo;
	}

	public void setShowHeader(boolean showHeader) {
		_showHeader = showHeader;
	}

	public void setTemplate(String template) {
		_template = template;
	}

	public void setViewURL(String viewURL) {
		_viewURL = viewURL;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_abstractLength = 200;
		_assetEntry = null;
		_assetRendererFactory = null;
		_className = null;
		_classPK = 0;
		_page = null;
		_renderer = null;
		_showComments = false;
		_showExtraInfo = false;
		_showHeader = false;
		_template = AssetRenderer.TEMPLATE_FULL_CONTENT;
		_viewURL = null;
	}

	@Override
	protected String getPage() {
		return _page;
	}

	@Override
	protected void includePage(
			String page, HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			boolean include = _renderer.include(
				getRequest(), httpServletResponse, _template);

			if (include) {
				return;
			}
		}
		catch (Exception exception) {
			_log.error("Unable to include asset renderer template", exception);
		}

		super.includePage(
			"/asset_display/" + _template + ".jsp", httpServletResponse);
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			WebKeys.ASSET_ENTRY_ABSTRACT_LENGTH, _abstractLength);

		AssetEntry assetEntry = _assetEntry;

		if (assetEntry == null) {
			if (Validator.isNotNull(_className) && (_classPK > 0)) {
				assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
					_className, _classPK);
			}
			else if (_renderer != null) {
				assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
					_renderer.getClassName(), _renderer.getClassPK());
			}
		}

		httpServletRequest.setAttribute(
			"liferay-asset:asset-display:assetEntry", assetEntry);

		if ((_renderer == null) && (assetEntry != null)) {
			_renderer = assetEntry.getAssetRenderer();
		}

		if (_renderer instanceof AssetRenderer) {
			AssetRenderer<?> assetRenderer = (AssetRenderer<?>)_renderer;

			httpServletRequest.setAttribute(
				WebKeys.ASSET_RENDERER, assetRenderer);
		}
		else {
			httpServletRequest.setAttribute(
				"liferay-asset:asset-display:renderer", _renderer);
		}

		AssetRendererFactory<?> assetRendererFactory = _assetRendererFactory;

		if ((assetRendererFactory == null) && (assetEntry != null)) {
			assetRendererFactory = assetEntry.getAssetRendererFactory();
		}

		if (assetRendererFactory != null) {
			httpServletRequest.setAttribute(
				WebKeys.ASSET_RENDERER_FACTORY, assetRendererFactory);
		}

		httpServletRequest.setAttribute(WebKeys.ASSET_ENTRY_VIEW_URL, _viewURL);

		addParam("showComments", String.valueOf(_showComments));
		addParam("showExtraInfo", String.valueOf(_showExtraInfo));
		addParam("showHeader", String.valueOf(_showHeader));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetDisplayTag.class);

	private int _abstractLength = 200;
	private AssetEntry _assetEntry;
	private AssetRendererFactory<?> _assetRendererFactory;
	private String _className;
	private long _classPK;
	private String _page;
	private Renderer _renderer;
	private boolean _showComments;
	private boolean _showExtraInfo;
	private boolean _showHeader;
	private String _template = AssetRenderer.TEMPLATE_FULL_CONTENT;
	private String _viewURL;

}