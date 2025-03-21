/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.repository.model.RepositoryModel;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.ParamAndPropertyAncestorTagImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class SearchContainerRowTag<R>
	extends ParamAndPropertyAncestorTagImpl implements BodyTag {

	public static final String DEFAULT_INDEX_VAR = "index";

	public static final String DEFAULT_MODEL_VAR = "model";

	public static final String DEFAULT_ROW_VAR = "row";

	@Override
	public void addParam(String name, String value) {
		if (name.equals("className")) {
			_resultRow.setClassName(value);
		}
		else if (name.equals("classHoverName")) {
			_resultRow.setClassHoverName(value);
		}
		else if (name.equals("restricted")) {
			_resultRow.setRestricted(GetterUtil.getBoolean(value));
		}
		else {
			Object object = pageContext.getAttribute(value);

			if (object == null) {
				object = value;
			}

			_resultRow.setParameter(name, object);
		}
	}

	@Override
	public int doAfterBody() {
		if (!_headerNamesAssigned && (_headerNames != null) &&
			!_headerNames.isEmpty()) {

			_searchContainer.setHeaderNames(_headerNames);
			_searchContainer.setHelpMessages(_helpMessages);
			_searchContainer.setOrderableHeaders(_orderableHeaders);

			_headerNamesAssigned = true;
		}

		if (!_resultRow.isSkip()) {
			_resultRows.add(_resultRow);
		}

		_rowIndex++;

		if (_rowIndex < _results.size()) {
			processRow();

			return EVAL_BODY_AGAIN;
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		_headerNames = null;
		_headerNamesAssigned = false;
		_resultRows = null;
		_rowIndex = 0;
		_resultRow = null;

		_ariaLabel = StringPool.BLANK;
		_bold = false;
		_className = null;
		_cssClass = StringPool.BLANK;
		_escapedModel = false;
		_helpMessages = null;
		_indexVar = DEFAULT_INDEX_VAR;
		_keyProperty = null;
		_modelVar = DEFAULT_MODEL_VAR;
		_orderableHeaders = null;
		_rowIdProperty = null;
		_rowVar = DEFAULT_ROW_VAR;
		_stringKey = false;
		_state = StringPool.BLANK;
		_tabIndex = StringPool.BLANK;

		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		SearchContainerTag<R> searchContainerTag =
			(SearchContainerTag<R>)findAncestorWithClass(
				this, SearchContainerTag.class);

		if (searchContainerTag == null) {
			throw new JspException("Requires liferay-ui:search-container");
		}

		_searchContainer = searchContainerTag.getSearchContainer();

		_searchContainer.setClassName(_className);

		_resultRows = _searchContainer.getResultRows();

		_results = _searchContainer.getResults();

		HttpServletRequest httpServletRequest = getRequest();

		httpServletRequest.setAttribute(
			"liferay-ui:search-container-row:ariaLabel", _ariaLabel);
		httpServletRequest.setAttribute(
			"liferay-ui:search-container-row:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:search-container-row:tabIndex", _tabIndex);

		if ((_results != null) && !_results.isEmpty()) {
			processRow();

			return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}

	public String getAriaLabel() {
		return _ariaLabel;
	}

	public String getClassName() {
		return _className;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public List<String> getHeaderNames() {
		if (_headerNames == null) {
			_headerNames = new ArrayList<>();
		}

		return _headerNames;
	}

	public Map<String, String> getHelpMessages() {
		if (_helpMessages == null) {
			_helpMessages = new LinkedHashMap<>();
		}

		return _helpMessages;
	}

	public String getIndexVar() {
		return _indexVar;
	}

	public String getKeyProperty() {
		return _keyProperty;
	}

	public String getModelVar() {
		return _modelVar;
	}

	public Map<String, String> getOrderableHeaders() {
		if (_orderableHeaders == null) {
			_orderableHeaders = new LinkedHashMap<>();
		}

		return _orderableHeaders;
	}

	public ResultRow getRow() {
		return _resultRow;
	}

	public String getRowVar() {
		return _rowVar;
	}

	public String getState() {
		return _state;
	}

	public String getTabIndex() {
		return _tabIndex;
	}

	public boolean isBold() {
		return _bold;
	}

	public boolean isEscapedModel() {
		return _escapedModel;
	}

	public boolean isHeaderNamesAssigned() {
		return _headerNamesAssigned;
	}

	public boolean isStringKey() {
		return _stringKey;
	}

	public void setAriaLabel(String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setBold(boolean bold) {
		_bold = bold;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setEscapedModel(boolean escapedModel) {
		_escapedModel = escapedModel;
	}

	public void setHeaderNames(List<String> headerNames) {
		_headerNames = headerNames;
	}

	public void setHeaderNamesAssigned(boolean headerNamesAssigned) {
		_headerNamesAssigned = headerNamesAssigned;
	}

	public void setHelpMessages(Map<String, String> helpMessages) {
		_helpMessages = helpMessages;
	}

	public void setIndexVar(String indexVar) {
		_indexVar = indexVar;
	}

	public void setKeyProperty(String keyProperty) {
		_keyProperty = keyProperty;
	}

	public void setModelVar(String var) {
		_modelVar = var;
	}

	public void setOrderableHeaders(Map<String, String> orderableHeaders) {
		_orderableHeaders = orderableHeaders;
	}

	public void setRow(ResultRow row) {
		_resultRow = row;
	}

	public void setRowIdProperty(String rowIdProperty) {
		_rowIdProperty = rowIdProperty;
	}

	public void setRowVar(String rowVar) {
		_rowVar = rowVar;
	}

	public void setState(String state) {
		_state = state;
	}

	public void setStringKey(boolean stringKey) {
		_stringKey = stringKey;
	}

	public void setTabIndex(String tabIndex) {
		_tabIndex = tabIndex;
	}

	protected void processRow() {
		Object model = _results.get(_rowIndex);

		if (isEscapedModel()) {
			if (model instanceof BaseModel) {
				BaseModel<?> baseModel = (BaseModel<?>)model;

				model = baseModel.toEscapedModel();
			}
			else if (model instanceof RepositoryModel) {
				RepositoryModel<?> repositoryModel = (RepositoryModel<?>)model;

				model = repositoryModel.toEscapedModel();
			}
		}

		String primaryKey = null;

		if (Validator.isNull(_keyProperty)) {
			primaryKey = String.valueOf(model);
		}
		else if (isStringKey()) {
			primaryKey = BeanPropertiesUtil.getStringSilent(
				model, _keyProperty);
		}
		else {
			Object primaryKeyObject = BeanPropertiesUtil.getObjectSilent(
				model, _keyProperty);

			primaryKey = String.valueOf(primaryKeyObject);
		}

		String rowId = null;

		if (Validator.isNull(_rowIdProperty)) {
			rowId = String.valueOf(_rowIndex + 1);
		}
		else {
			Object rowIdObject = BeanPropertiesUtil.getObjectSilent(
				model, _rowIdProperty);

			if (Validator.isNull(rowIdObject)) {
				rowId = String.valueOf(_rowIndex + 1);
			}
			else {
				rowId =
					FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
						String.valueOf(rowIdObject));
			}
		}

		_resultRow = new com.liferay.taglib.search.ResultRow(
			rowId, model, primaryKey, _rowIndex, _bold, _ariaLabel, _cssClass,
			_state, _tabIndex);

		pageContext.setAttribute(_indexVar, _rowIndex);
		pageContext.setAttribute(_modelVar, model);
		pageContext.setAttribute(_rowVar, _resultRow);
	}

	private String _ariaLabel = StringPool.BLANK;
	private boolean _bold;
	private String _className;
	private String _cssClass = StringPool.BLANK;
	private boolean _escapedModel;
	private List<String> _headerNames;
	private boolean _headerNamesAssigned;
	private Map<String, String> _helpMessages;
	private String _indexVar = DEFAULT_INDEX_VAR;
	private String _keyProperty;
	private String _modelVar = DEFAULT_MODEL_VAR;
	private Map<String, String> _orderableHeaders;
	private ResultRow _resultRow;
	private List<ResultRow> _resultRows;
	private List<R> _results;
	private String _rowIdProperty;
	private int _rowIndex;
	private String _rowVar = DEFAULT_ROW_VAR;
	private SearchContainer<R> _searchContainer;
	private String _state = StringPool.BLANK;
	private boolean _stringKey;
	private String _tabIndex;

}