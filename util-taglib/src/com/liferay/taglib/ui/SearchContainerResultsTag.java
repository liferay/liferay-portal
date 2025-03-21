/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raymond Augé
 * @author Roberto Díaz
 */
public class SearchContainerResultsTag<R> extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		try {
			SearchContainerTag<R> searchContainerTag =
				(SearchContainerTag<R>)findAncestorWithClass(
					this, SearchContainerTag.class);

			SearchContainer<R> searchContainer =
				searchContainerTag.getSearchContainer();

			String totalVar = searchContainer.getTotalVar();

			if (totalVar.equals(SearchContainer.DEFAULT_TOTAL_VAR)) {
				pageContext.removeAttribute(totalVar);
			}

			if (_results == null) {
				_results = searchContainer.getResults();

				if (_results.isEmpty()) {
					_results = (List<R>)pageContext.getAttribute(_resultsVar);
				}
			}

			if ((_results != null) && _calculateStartAndEnd) {
				_results = _results.subList(
					searchContainer.getStart(), searchContainer.getResultEnd());
			}

			searchContainer.setResultsAndTotal(
				() -> _results, searchContainer.getTotal());

			pageContext.setAttribute(_resultsVar, _results);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			_calculateStartAndEnd = false;
			_results = null;
			_resultsVar = SearchContainer.DEFAULT_RESULTS_VAR;
		}
	}

	@Override
	public int doStartTag() throws JspException {
		SearchContainerTag<R> searchContainerTag =
			(SearchContainerTag<R>)findAncestorWithClass(
				this, SearchContainerTag.class);

		if (searchContainerTag == null) {
			throw new JspTagException("Requires liferay-ui:search-container");
		}

		if (_results == null) {
			pageContext.setAttribute(_resultsVar, new ArrayList<R>());
		}

		return EVAL_BODY_INCLUDE;
	}

	public List<R> getResults() {
		return _results;
	}

	public String getResultsVar() {
		return _resultsVar;
	}

	public boolean isCalculateStartAndEnd() {
		return _calculateStartAndEnd;
	}

	public void setCalculateStartAndEnd(boolean calculateStartAndEnd) {
		_calculateStartAndEnd = calculateStartAndEnd;
	}

	public void setResults(List<R> results) {
		_results = results;
	}

	public void setResultsVar(String resultsVar) {
		_resultsVar = resultsVar;
	}

	private boolean _calculateStartAndEnd;
	private List<R> _results;
	private String _resultsVar = SearchContainer.DEFAULT_RESULTS_VAR;

}