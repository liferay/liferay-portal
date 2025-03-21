/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class AssetMetadataTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		if (_hasMetadata) {
			return super.doEndTag();
		}

		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		if (ArrayUtil.isEmpty(_metadataFields)) {
			return SKIP_BODY;
		}

		_hasMetadata = true;

		return super.doStartTag();
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String[] getMetadataFields() {
		return _metadataFields;
	}

	public boolean isFilterByMetadata() {
		return _filterByMetadata;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setFilterByMetadata(boolean filterByMetadata) {
		_filterByMetadata = filterByMetadata;
	}

	public void setMetadataFields(String[] metadataFields) {
		_metadataFields = metadataFields;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = StringPool.BLANK;
		_classPK = 0;
		_filterByMetadata = false;
		_hasMetadata = false;
		_metadataFields = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_className);

		try {
			httpServletRequest.setAttribute(
				"liferay-asset:asset-metadata:assetEntry",
				assetRendererFactory.getAssetEntry(_className, _classPK));
			httpServletRequest.setAttribute(
				"liferay-asset:asset-metadata:assetRenderer",
				assetRendererFactory.getAssetRenderer(_classPK));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		httpServletRequest.setAttribute(
			"liferay-asset:asset-metadata:className", _className);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-metadata:classPK", _classPK);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-metadata:filterByMetadata", _filterByMetadata);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-metadata:metadataFields", _metadataFields);
	}

	private static final String _PAGE = "/asset_metadata/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetMetadataTag.class);

	private String _className = StringPool.BLANK;
	private long _classPK;
	private boolean _filterByMetadata;
	private boolean _hasMetadata;
	private String[] _metadataFields;

}