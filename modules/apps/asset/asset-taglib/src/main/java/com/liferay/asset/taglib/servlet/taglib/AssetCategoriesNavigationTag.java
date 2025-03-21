/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alvaro del Castillo
 * @author Eduardo Lundgren
 * @author Jorge Ferrer
 */
public class AssetCategoriesNavigationTag extends IncludeTag {

	public long[] getVocabularyIds() {
		return _vocabularyIds;
	}

	public boolean isHidePortletWhenEmpty() {
		return _hidePortletWhenEmpty;
	}

	public void setHidePortletWhenEmpty(boolean hidePortletWhenEmpty) {
		_hidePortletWhenEmpty = hidePortletWhenEmpty;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setVocabularyIds(long[] vocabularyIds) {
		_vocabularyIds = vocabularyIds;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_hidePortletWhenEmpty = false;
		_vocabularyIds = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-asset:asset-tags-navigation:hidePortletWhenEmpty",
			String.valueOf(_hidePortletWhenEmpty));
		httpServletRequest.setAttribute(
			"liferay-asset:asset-tags-navigation:vocabularyIds",
			_vocabularyIds);
	}

	private static final String _PAGE = "/asset_categories_navigation/page.jsp";

	private boolean _hidePortletWhenEmpty;
	private long[] _vocabularyIds;

}