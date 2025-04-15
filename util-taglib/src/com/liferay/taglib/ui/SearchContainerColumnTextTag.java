/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.search.TextSearchEntry;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class SearchContainerColumnTextTag<R>
	extends SearchContainerColumnTag implements BodyTag {

	@Override
	public int doEndTag() {
		try {
			SearchContainerRowTag<R> searchContainerRowTag =
				(SearchContainerRowTag<R>)findAncestorWithClass(
					this, SearchContainerRowTag.class);

			ResultRow resultRow = searchContainerRowTag.getRow();

			if (Validator.isNotNull(_property)) {
				_value = String.valueOf(
					BeanPropertiesUtil.getObject(
						resultRow.getObject(), _property));
			}
			else if (Validator.isNotNull(_buffer)) {
				_value = _sb.toString();
			}
			else if (_value == null) {
				BodyContent bodyContent = getBodyContent();

				if (bodyContent != null) {
					_value = bodyContent.getString();
				}
				else {
					_value = String.valueOf(
						BeanPropertiesUtil.getObject(
							resultRow.getObject(), getName()));
				}
			}

			if (_translate) {
				_value = LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					_value);
			}

			if (index <= -1) {
				List<SearchEntry> searchEntries = resultRow.getEntries();

				index = searchEntries.size();
			}

			if (resultRow.isRestricted() || Validator.isBlank(_value)) {
				_href = null;
			}

			TextSearchEntry textSearchEntry = new TextSearchEntry();

			textSearchEntry.setAlign(getAlign());
			textSearchEntry.setColspan(getColspan());
			textSearchEntry.setCssClass(getCssClass());
			textSearchEntry.setHref(String.valueOf(getHref()));
			textSearchEntry.setName(getValue());
			textSearchEntry.setTarget(getTarget());
			textSearchEntry.setTitle(getTitle());
			textSearchEntry.setTruncate(getTruncate());
			textSearchEntry.setValign(getValign());

			resultRow.addSearchEntry(index, textSearchEntry);

			return EVAL_PAGE;
		}
		finally {
			index = -1;
			_value = null;

			align = SearchEntry.DEFAULT_ALIGN;
			_buffer = null;
			colspan = SearchEntry.DEFAULT_COLSPAN;
			cssClass = SearchEntry.DEFAULT_CSS_CLASS;
			_helpMessage = null;
			_href = null;
			name = null;
			_orderable = false;
			_orderableProperty = null;
			_property = null;
			_sb = null;
			_target = null;
			_title = null;
			_translate = false;
			valign = SearchEntry.DEFAULT_VALIGN;
		}
	}

	@Override
	public int doStartTag() throws JspException {
		if (_orderable && Validator.isNull(_orderableProperty)) {
			_orderableProperty = name;
		}

		SearchContainerRowTag<R> searchContainerRowTag =
			(SearchContainerRowTag<R>)findAncestorWithClass(
				this, SearchContainerRowTag.class);

		if (searchContainerRowTag == null) {
			throw new JspTagException(
				"Requires liferay-ui:search-container-row");
		}

		if (!searchContainerRowTag.isHeaderNamesAssigned()) {
			List<String> headerNames = searchContainerRowTag.getHeaderNames();

			String name = getName();

			if (Validator.isNull(name) && Validator.isNotNull(_property)) {
				name = _property;
			}

			headerNames.add(name);

			if (Validator.isNotNull(_helpMessage)) {
				Map<String, String> helpMessages =
					searchContainerRowTag.getHelpMessages();

				helpMessages.put(name, _helpMessage);
			}

			if (_orderable) {
				Map<String, String> orderableHeaders =
					searchContainerRowTag.getOrderableHeaders();

				if (Validator.isNotNull(_orderableProperty)) {
					orderableHeaders.put(name, _orderableProperty);
				}
				else if (Validator.isNotNull(_property)) {
					orderableHeaders.put(name, _property);
				}
				else if (Validator.isNotNull(name)) {
					orderableHeaders.put(name, name);
				}
			}
		}

		if (Validator.isNotNull(_property)) {
			return SKIP_BODY;
		}
		else if (Validator.isNotNull(_buffer)) {
			_sb = new StringBundler();

			pageContext.setAttribute(_buffer, _sb);

			return EVAL_BODY_INCLUDE;
		}
		else if (Validator.isNull(_value)) {
			return EVAL_BODY_BUFFERED;
		}

		return SKIP_BODY;
	}

	public String getBuffer() {
		return _buffer;
	}

	public Object getHref() {
		if (_href instanceof PortletURL) {
			_href = _href.toString();
		}

		return _href;
	}

	public String getOrderableProperty() {
		return _orderableProperty;
	}

	public String getProperty() {
		return _property;
	}

	public String getTarget() {
		return _target;
	}

	public String getTitle() {
		return _title;
	}

	public String getValue() {
		return _value;
	}

	public boolean isOrderable() {
		return _orderable;
	}

	public void setBuffer(String buffer) {
		_buffer = buffer;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setHref(Object href) {
		_href = href;
	}

	public void setOrderable(boolean orderable) {
		_orderable = orderable;
	}

	public void setOrderableProperty(String orderableProperty) {
		_orderableProperty = orderableProperty;
	}

	public void setProperty(String property) {
		_property = property;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTranslate(boolean translate) {
		_translate = translate;
	}

	public void setValue(String value) {
		_value = value;
	}

	private String _buffer;
	private String _helpMessage;
	private Object _href;
	private boolean _orderable;
	private String _orderableProperty;
	private String _property;
	private StringBundler _sb;
	private String _target;
	private String _title;
	private boolean _translate;
	private String _value;

}