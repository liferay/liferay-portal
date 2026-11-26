/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.query;

/**
 * @author Michael C. Han
 */
public class CommonTermsQuery extends Query {

	public CommonTermsQuery(String field, String text) {
		_field = field;
		_text = text;
	}

	@Override
	public <T> T accept(QueryVisitor<T> queryVisitor) {
		return queryVisitor.visit(this);
	}

	public String getAnalyzer() {
		return _analyzer;
	}

	public Float getCutoffFrequency() {
		return _cutoffFrequency;
	}

	public String getField() {
		return _field;
	}

	public Operator getHighFreqOperator() {
		return _highFreqOperator;
	}

	public Operator getLowFreqOperator() {
		return _lowFreqOperator;
	}

	public String getText() {
		return _text;
	}

	public void setAnalyzer(String analyzer) {
		_analyzer = analyzer;
	}

	public void setCutoffFrequency(Float cutoffFrequency) {
		_cutoffFrequency = cutoffFrequency;
	}

	public void setHighFreqOperator(Operator highFreqOperator) {
		_highFreqOperator = highFreqOperator;
	}

	public void setLowFreqOperator(Operator lowFreqOperator) {
		_lowFreqOperator = lowFreqOperator;
	}

	private static final long serialVersionUID = 1L;

	private String _analyzer;
	private Float _cutoffFrequency;
	private final String _field;
	private Operator _highFreqOperator = Operator.OR;
	private Operator _lowFreqOperator = Operator.OR;
	private final String _text;

}