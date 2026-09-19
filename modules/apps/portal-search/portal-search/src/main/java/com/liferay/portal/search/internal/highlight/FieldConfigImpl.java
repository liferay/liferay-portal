/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.highlight;

import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.FieldConfigBuilder;
import com.liferay.portal.search.query.Query;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class FieldConfigImpl implements FieldConfig {

	public FieldConfigImpl() {
	}

	public FieldConfigImpl(FieldConfigImpl fieldConfigImpl) {
		_boundaryChars = fieldConfigImpl._boundaryChars;
		_boundaryMaxScan = fieldConfigImpl._boundaryMaxScan;
		_boundaryScannerLocale = fieldConfigImpl._boundaryScannerLocale;
		_boundaryScannerType = fieldConfigImpl._boundaryScannerType;
		_fieldName = fieldConfigImpl._fieldName;
		_forceSource = fieldConfigImpl._forceSource;
		_fragmenter = fieldConfigImpl._fragmenter;
		_fragmentOffset = fieldConfigImpl._fragmentOffset;
		_fragmentSize = fieldConfigImpl._fragmentSize;
		_highlighterType = fieldConfigImpl._highlighterType;
		_highlightFilter = fieldConfigImpl._highlightFilter;
		_highlightQuery = fieldConfigImpl._highlightQuery;
		_matchedFields = fieldConfigImpl._matchedFields;
		_noMatchSize = fieldConfigImpl._noMatchSize;
		_numFragments = fieldConfigImpl._numFragments;
		_order = fieldConfigImpl._order;
		_phraseLimit = fieldConfigImpl._phraseLimit;
		_postTags = fieldConfigImpl._postTags;
		_preTags = fieldConfigImpl._preTags;
		_requireFieldMatch = fieldConfigImpl._requireFieldMatch;
	}

	@Override
	public char[] getBoundaryChars() {
		return _boundaryChars;
	}

	@Override
	public Integer getBoundaryMaxScan() {
		return _boundaryMaxScan;
	}

	@Override
	public String getBoundaryScannerLocale() {
		return _boundaryScannerLocale;
	}

	@Override
	public String getBoundaryScannerType() {
		return _boundaryScannerType;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getFieldName()}
	 */
	@Deprecated
	@Override
	public String getField() {
		return _fieldName;
	}

	@Override
	public String getFieldName() {
		return _fieldName;
	}

	@Override
	public Boolean getForceSource() {
		return _forceSource;
	}

	@Override
	public Integer getFragmentOffset() {
		return _fragmentOffset;
	}

	@Override
	public Integer getFragmentSize() {
		return _fragmentSize;
	}

	@Override
	public String getFragmenter() {
		return _fragmenter;
	}

	@Override
	public Boolean getHighlightFilter() {
		return _highlightFilter;
	}

	@Override
	public Query getHighlightQuery() {
		return _highlightQuery;
	}

	@Override
	public String getHighlighterType() {
		return _highlighterType;
	}

	@Override
	public String[] getMatchedFields() {
		return _matchedFields;
	}

	@Override
	public Integer getNoMatchSize() {
		return _noMatchSize;
	}

	@Override
	public Integer getNumFragments() {
		return _numFragments;
	}

	@Override
	public String getOrder() {
		return _order;
	}

	@Override
	public Integer getPhraseLimit() {
		return _phraseLimit;
	}

	@Override
	public String[] getPostTags() {
		return _postTags;
	}

	@Override
	public String[] getPreTags() {
		return _preTags;
	}

	@Override
	public Boolean getRequireFieldMatch() {
		return _requireFieldMatch;
	}

	public static final class FieldConfigBuilderImpl
		implements FieldConfigBuilder {

		public FieldConfigBuilderImpl(String fieldName) {
			_fieldConfigImpl._fieldName = fieldName;
		}

		@Override
		public FieldConfigBuilder boundaryChars(char... boundaryChars) {
			_fieldConfigImpl._boundaryChars = boundaryChars;

			return this;
		}

		@Override
		public FieldConfigBuilder boundaryMaxScan(Integer boundaryMaxScan) {
			_fieldConfigImpl._boundaryMaxScan = boundaryMaxScan;

			return this;
		}

		@Override
		public FieldConfigBuilder boundaryScannerLocale(
			String boundaryScannerLocale) {

			_fieldConfigImpl._boundaryScannerLocale = boundaryScannerLocale;

			return this;
		}

		@Override
		public FieldConfigBuilder boundaryScannerType(
			String boundaryScannerType) {

			_fieldConfigImpl._boundaryScannerType = boundaryScannerType;

			return this;
		}

		@Override
		public FieldConfig build() {
			return new FieldConfigImpl(_fieldConfigImpl);
		}

		/**
		 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
		 *             HighlightsImpl#fieldConfigBuilder(String)}
		 */
		@Deprecated
		@Override
		public FieldConfigBuilder field(String field) {
			_fieldConfigImpl._fieldName = field;

			return this;
		}

		@Override
		public FieldConfigBuilder forceSource(Boolean forceSource) {
			_fieldConfigImpl._forceSource = forceSource;

			return this;
		}

		@Override
		public FieldConfigBuilder fragmentOffset(Integer fragmentOffset) {
			_fieldConfigImpl._fragmentOffset = fragmentOffset;

			return this;
		}

		@Override
		public FieldConfigBuilder fragmentSize(Integer fragmentSize) {
			_fieldConfigImpl._fragmentSize = fragmentSize;

			return this;
		}

		@Override
		public FieldConfigBuilder fragmenter(String fragmenter) {
			_fieldConfigImpl._fragmenter = fragmenter;

			return this;
		}

		@Override
		public FieldConfigBuilder highlightFilter(Boolean highlightFilter) {
			_fieldConfigImpl._highlightFilter = highlightFilter;

			return this;
		}

		@Override
		public FieldConfigBuilder highlightQuery(Query highlightQuery) {
			_fieldConfigImpl._highlightQuery = highlightQuery;

			return this;
		}

		@Override
		public FieldConfigBuilder highlighterType(String highlighterType) {
			_fieldConfigImpl._highlighterType = highlighterType;

			return this;
		}

		@Override
		public FieldConfigBuilder matchedFields(String... matchedFields) {
			_fieldConfigImpl._matchedFields = matchedFields;

			return this;
		}

		@Override
		public FieldConfigBuilder noMatchSize(Integer noMatchSize) {
			_fieldConfigImpl._noMatchSize = noMatchSize;

			return this;
		}

		@Override
		public FieldConfigBuilder numFragments(Integer numFragments) {
			_fieldConfigImpl._numFragments = numFragments;

			return this;
		}

		@Override
		public FieldConfigBuilder order(String order) {
			_fieldConfigImpl._order = order;

			return this;
		}

		@Override
		public FieldConfigBuilder phraseLimit(Integer phraseLimit) {
			_fieldConfigImpl._phraseLimit = phraseLimit;

			return this;
		}

		@Override
		public FieldConfigBuilder postTags(String... postTags) {
			_fieldConfigImpl._postTags = postTags;

			return this;
		}

		@Override
		public FieldConfigBuilder preTags(String... preTags) {
			_fieldConfigImpl._preTags = preTags;

			return this;
		}

		@Override
		public FieldConfigBuilder requireFieldMatch(Boolean requireFieldMatch) {
			_fieldConfigImpl._requireFieldMatch = requireFieldMatch;

			return this;
		}

		private final FieldConfigImpl _fieldConfigImpl = new FieldConfigImpl();

	}

	private char[] _boundaryChars = {};
	private Integer _boundaryMaxScan;
	private String _boundaryScannerLocale;
	private String _boundaryScannerType;
	private String _fieldName;
	private Boolean _forceSource;
	private Integer _fragmentOffset;
	private Integer _fragmentSize;
	private String _fragmenter;
	private Boolean _highlightFilter;
	private Query _highlightQuery;
	private String _highlighterType;
	private String[] _matchedFields = {};
	private Integer _noMatchSize;
	private Integer _numFragments;
	private String _order;
	private Integer _phraseLimit;
	private String[] _postTags = {};
	private String[] _preTags = {};
	private Boolean _requireFieldMatch;

}