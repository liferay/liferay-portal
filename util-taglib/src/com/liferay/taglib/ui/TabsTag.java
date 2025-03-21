/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.util.JS;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.TabsTag}
 */
@Deprecated
public class TabsTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			httpServletRequest.setAttribute(
				"liferay-ui:tabs:namesJS", _namesJS);
			httpServletRequest.setAttribute("liferay-ui:tabs:param", _param);
			httpServletRequest.setAttribute("liferay-ui:tabs:value", _value);

			include(getEndPage(), false);

			httpServletRequest.removeAttribute("liferay-ui:tabs:backLabel");
			httpServletRequest.removeAttribute("liferay-ui:tabs:backURL");
			httpServletRequest.removeAttribute("liferay-ui:tabs:cssClass");
			httpServletRequest.removeAttribute("liferay-ui:tabs:formName");
			httpServletRequest.removeAttribute("liferay-ui:tabs:names");
			httpServletRequest.removeAttribute("liferay-ui:tabs:namesJS");
			httpServletRequest.removeAttribute("liferay-ui:tabs:onClick");
			httpServletRequest.removeAttribute("liferay-ui:tabs:param");
			httpServletRequest.removeAttribute("liferay-ui:tabs:portletURL");
			httpServletRequest.removeAttribute("liferay-ui:tabs:refresh");
			httpServletRequest.removeAttribute("liferay-ui:tabs:type");
			httpServletRequest.removeAttribute("liferay-ui:tabs:url");
			httpServletRequest.removeAttribute("liferay-ui:tabs:urls");
			httpServletRequest.removeAttribute("liferay-ui:tabs:value");
			httpServletRequest.removeAttribute("liferay-ui:tabs:values");

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			_backLabel = null;
			_backURL = null;
			_cssClass = StringPool.BLANK;
			_endPage = null;
			_formName = StringPool.BLANK;
			_names = null;
			_namesJS = null;
			_namesPos = 0;
			_onClick = null;
			_param = "tabs1";
			_portletURL = null;
			_refresh = true;
			_startPage = null;
			_tabsValues = null;
			_type = null;
			_url = null;
			_urls = null;
			_value = null;
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			httpServletRequest.setAttribute(
				"liferay-ui:tabs:backLabel", _backLabel);
			httpServletRequest.setAttribute(
				"liferay-ui:tabs:backURL", _backURL);
			httpServletRequest.setAttribute(
				"liferay-ui:tabs:cssClass", _cssClass);
			httpServletRequest.setAttribute(
				"liferay-ui:tabs:formName", _formName);
			httpServletRequest.setAttribute("liferay-ui:tabs:names", _names);

			_namesJS = JS.toScript(_names);

			httpServletRequest.setAttribute(
				"liferay-ui:tabs:namesJS", _namesJS);

			httpServletRequest.setAttribute(
				"liferay-ui:tabs:onClick", String.valueOf(_onClick));
			httpServletRequest.setAttribute("liferay-ui:tabs:param", _param);
			httpServletRequest.setAttribute(
				"liferay-ui:tabs:portletURL", _portletURL);
			httpServletRequest.setAttribute(
				"liferay-ui:tabs:refresh", String.valueOf(_refresh));

			if ((_tabsValues == null) || (_tabsValues.length < _names.length)) {
				_tabsValues = _names;
			}

			httpServletRequest.setAttribute(
				"liferay-ui:tabs:values", _tabsValues);

			httpServletRequest.setAttribute("liferay-ui:tabs:type", _type);
			httpServletRequest.setAttribute("liferay-ui:tabs:url", _url);
			httpServletRequest.setAttribute("liferay-ui:tabs:urls", _urls);

			if ((_value == null) && (_tabsValues.length > 0)) {
				_value = ParamUtil.getString(
					httpServletRequest, _param, _tabsValues[0]);
			}

			if (Validator.isNull(_value)) {
				if (_tabsValues.length > 0) {
					_value = _tabsValues[0];
				}
				else {
					_value = StringPool.BLANK;
				}
			}

			if (!ArrayUtil.contains(_tabsValues, _value)) {
				if (_tabsValues.length > 0) {
					_value = _tabsValues[0];
				}
				else {
					_value = StringPool.BLANK;
				}
			}

			if (_value == null) {
				_value = ParamUtil.getString(
					httpServletRequest, _param, _tabsValues[0]);
			}

			httpServletRequest.setAttribute("liferay-ui:tabs:value", _value);

			include(getStartPage(), true);

			return EVAL_BODY_INCLUDE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public String getBackLabel() {
		return _backLabel;
	}

	public String getBackURL() {
		return _backURL;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getFormName() {
		return _formName;
	}

	public String getNames() {
		return StringUtil.merge(_names);
	}

	public String getOnClick() {
		return _onClick;
	}

	public String getParam() {
		return _param;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public String getSectionName() {
		if (_names.length > _namesPos) {
			return _names[_namesPos];
		}

		return StringPool.BLANK;
	}

	public boolean getSectionSelected() {
		if ((_names.length == 0) ||
			((_names.length > _namesPos) && _names[_namesPos].equals(_value))) {

			return true;
		}

		return false;
	}

	public String getTabsValues() {
		return StringUtil.merge(_tabsValues);
	}

	public String getType() {
		return _type;
	}

	public String getUrl() {
		return _url;
	}

	public String[] getUrls() {
		return _urls;
	}

	public String getValue() {
		return _value;
	}

	public void incrementSection() {
		_namesPos++;
	}

	public boolean isRefresh() {
		return _refresh;
	}

	public void setBackLabel(String backLabel) {
		_backLabel = backLabel;
	}

	public void setBackURL(String backURL) {
		_backURL = backURL;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setEndPage(String endPage) {
		_endPage = endPage;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setNames(String names) {
		_names = StringUtil.split(names);
	}

	public void setOnClick(String onClick) {
		_onClick = onClick;
	}

	public void setParam(String param) {
		_param = param;
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setRefresh(boolean refresh) {
		_refresh = refresh;
	}

	public void setStartPage(String startPage) {
		_startPage = startPage;
	}

	public void setTabsValues(String tabsValues) {
		_tabsValues = StringUtil.split(tabsValues);
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUrl(String url) {
		_url = url;
	}

	public void setUrls(String[] urls) {
		_urls = urls;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	protected String getEndPage() {
		if (Validator.isNull(_endPage)) {
			return _END_PAGE;
		}

		return _endPage;
	}

	@Override
	protected String getStartPage() {
		if (Validator.isNull(_startPage)) {
			return _START_PAGE;
		}

		return _startPage;
	}

	private static final String _END_PAGE = "/html/taglib/ui/tabs/end.jsp";

	private static final String _START_PAGE = "/html/taglib/ui/tabs/start.jsp";

	private String _backLabel;
	private String _backURL;
	private String _cssClass;
	private String _endPage;
	private String _formName;
	private String[] _names;
	private String _namesJS;
	private int _namesPos;
	private String _onClick;
	private String _param = "tabs1";
	private PortletURL _portletURL;
	private boolean _refresh = true;
	private String _startPage;
	private String[] _tabsValues;
	private String _type;
	private String _url;
	private String[] _urls;
	private String _value;

}