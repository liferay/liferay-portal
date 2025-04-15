/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class AssetCategoriesSummaryTag<R> extends IncludeTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public String getMessage() {
		return _message;
	}

	public String getParamName() {
		return _paramName;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public int[] getVisibleTypes() {
		return _visibleTypes;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setMessage(String message) {
		_message = message;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setParamName(String paramName) {
		_paramName = paramName;
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setVisibleTypes(int[] visibleTypes) {
		_visibleTypes = visibleTypes;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_classPK = 0;
		_displayStyle = null;
		_message = null;
		_paramName = null;
		_portletURL = null;
		_visibleTypes = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		List<AssetCategory> assetCategories = new ArrayList<>();

		AssetCategoriesAvailableTag<R> assetCategoriesAvailableTag =
			(AssetCategoriesAvailableTag<R>)findAncestorWithClass(
				this, AssetCategoriesAvailableTag.class);

		if (assetCategoriesAvailableTag != null) {
			assetCategories = assetCategoriesAvailableTag.getAssetCategories();
		}

		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:assetCategories",
			assetCategories);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:className", _className);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:classPK",
			String.valueOf(_classPK));
		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:displayStyle",
			_displayStyle);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:message", _message);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:paramName", _paramName);
		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:portletURL", _portletURL);

		if (_visibleTypes == null) {
			_visibleTypes = new int[] {
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC
			};
		}

		httpServletRequest.setAttribute(
			"liferay-asset:asset-categories-summary:visibleTypes",
			_visibleTypes);
	}

	private static final String _PAGE = "/asset_categories_summary/page.jsp";

	private String _className;
	private long _classPK;
	private String _displayStyle;
	private String _message;
	private String _paramName;
	private PortletURL _portletURL;
	private int[] _visibleTypes;

}