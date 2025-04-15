/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class PageIteratorTag extends IncludeTag {

	public int getCur() {
		return _cur;
	}

	public String getCurParam() {
		return _curParam;
	}

	public int getDelta() {
		return _delta;
	}

	public String getDeltaParam() {
		return _deltaParam;
	}

	public String getFormName() {
		return _formName;
	}

	public String getId() {
		return _id;
	}

	public String getJsCall() {
		return _jsCall;
	}

	public String getMarkupView() {
		return _markupView;
	}

	public int getMaxPages() {
		return _maxPages;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public String getTarget() {
		return _target;
	}

	public int getTotal() {
		return _total;
	}

	public String getType() {
		return _type;
	}

	public boolean isDeltaConfigurable() {
		return _deltaConfigurable;
	}

	public boolean isForcePost() {
		return _forcePost;
	}

	public void setCur(int cur) {
		_cur = cur;
	}

	public void setCurParam(String curParam) {
		_curParam = curParam;
	}

	public void setDelta(int delta) {
		_delta = delta;
	}

	public void setDeltaConfigurable(boolean deltaConfigurable) {
		_deltaConfigurable = deltaConfigurable;
	}

	public void setDeltaParam(String deltaParam) {
		_deltaParam = deltaParam;
	}

	public void setForcePost(boolean forcePost) {
		_forcePost = forcePost;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setJsCall(String jsCall) {
		_jsCall = jsCall;
	}

	public void setMarkupView(String markupView) {
		_markupView = markupView;
	}

	public void setMaxPages(int maxPages) {
		_maxPages = maxPages;
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setTotal(int total) {
		_total = total;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cur = 0;
		_curParam = null;
		_delta = SearchContainer.DEFAULT_DELTA;
		_deltaConfigurable = SearchContainer.DEFAULT_DELTA_CONFIGURABLE;
		_deltaParam = SearchContainer.DEFAULT_DELTA_PARAM;
		_forcePost = SearchContainer.DEFAULT_FORCE_POST;
		_formName = "fm";
		_id = null;
		_jsCall = null;
		_markupView = null;
		_maxPages = 10;
		_pages = 0;
		_portletURL = null;
		_target = "_self";
		_total = 0;
		_type = "regular";
	}

	@Override
	protected String getEndPage() {
		if (_pages > 1) {
			return "/html/taglib/ui/page_iterator/end.jsp";
		}

		return null;
	}

	@Override
	protected String getStartPage() {
		if (Validator.isNull(_markupView)) {
			return "/html/taglib/ui/page_iterator/deprecated/start.jsp";
		}

		return "/html/taglib/ui/page_iterator/start.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		_pages = (int)Math.ceil((double)_total / _delta);

		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:cur", String.valueOf(_cur));
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:curParam", _curParam);
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:delta", String.valueOf(_delta));
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:deltaConfigurable",
			String.valueOf(_deltaConfigurable));
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:deltaParam", _deltaParam);
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:forcePost", String.valueOf(_forcePost));
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:formName", _formName);
		httpServletRequest.setAttribute("liferay-ui:page-iterator:id", _id);
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:jsCall", _jsCall);
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:maxPages", String.valueOf(_maxPages));
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:pages", String.valueOf(_pages));
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:portletURL", _portletURL);
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:target", _target);
		httpServletRequest.setAttribute(
			"liferay-ui:page-iterator:total", String.valueOf(_total));
		httpServletRequest.setAttribute("liferay-ui:page-iterator:type", _type);
	}

	private int _cur;
	private String _curParam;
	private int _delta = SearchContainer.DEFAULT_DELTA;
	private boolean _deltaConfigurable =
		SearchContainer.DEFAULT_DELTA_CONFIGURABLE;
	private String _deltaParam = SearchContainer.DEFAULT_DELTA_PARAM;
	private boolean _forcePost = SearchContainer.DEFAULT_FORCE_POST;
	private String _formName = "fm";
	private String _id;
	private String _jsCall;
	private String _markupView;
	private int _maxPages = 10;
	private int _pages;
	private PortletURL _portletURL;
	private String _target = "_self";
	private int _total;
	private String _type = "regular";

}