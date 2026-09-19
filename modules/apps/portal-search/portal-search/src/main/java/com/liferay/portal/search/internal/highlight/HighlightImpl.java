/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.highlight;

import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.highlight.HighlightBuilder;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class HighlightImpl implements Highlight {

	public HighlightImpl() {
	}

	public HighlightImpl(HighlightImpl highlightImpl) {
		_fieldConfigs.addAll(highlightImpl._fieldConfigs);
		_boundaryChars = highlightImpl._boundaryChars;
		_boundaryMaxScan = highlightImpl._boundaryMaxScan;
		_boundaryScannerLocale = highlightImpl._boundaryScannerLocale;
		_boundaryScannerType = highlightImpl._boundaryScannerType;
		_encoder = highlightImpl._encoder;
		_forceSource = highlightImpl._forceSource;
		_fragmenter = highlightImpl._fragmenter;
		_fragmentSize = highlightImpl._fragmentSize;
		_highlighterType = highlightImpl._highlighterType;
		_highlightFilter = highlightImpl._highlightFilter;
		_highlightQuery = highlightImpl._highlightQuery;
		_noMatchSize = highlightImpl._noMatchSize;
		_numOfFragments = highlightImpl._numOfFragments;
		_order = highlightImpl._order;
		_phraseLimit = highlightImpl._phraseLimit;
		_postTags = highlightImpl._postTags;
		_preTags = highlightImpl._preTags;
		_requireFieldMatch = highlightImpl._requireFieldMatch;
		_tagsSchema = highlightImpl._tagsSchema;
		_useExplicitFieldOrder = highlightImpl._useExplicitFieldOrder;
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

	@Override
	public String getEncoder() {
		return _encoder;
	}

	@Override
	public List<FieldConfig> getFieldConfigs() {
		return Collections.unmodifiableList(_fieldConfigs);
	}

	@Override
	public Boolean getForceSource() {
		return _forceSource;
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
	public Integer getNoMatchSize() {
		return _noMatchSize;
	}

	@Override
	public Integer getNumOfFragments() {
		return _numOfFragments;
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

	@Override
	public String getTagsSchema() {
		return _tagsSchema;
	}

	@Override
	public Boolean getUseExplicitFieldOrder() {
		return _useExplicitFieldOrder;
	}

	public static final class HighlightBuilderImpl implements HighlightBuilder {

		@Override
		public HighlightBuilder addFieldConfig(FieldConfig fieldConfig) {
			_highlightImpl._fieldConfigs.add(fieldConfig);

			return this;
		}

		@Override
		public HighlightBuilder boundaryChars(char... boundaryChars) {
			_highlightImpl._boundaryChars = boundaryChars;

			return this;
		}

		@Override
		public HighlightBuilder boundaryMaxScan(Integer boundaryMaxScan) {
			_highlightImpl._boundaryMaxScan = boundaryMaxScan;

			return this;
		}

		@Override
		public HighlightBuilder boundaryScannerLocale(
			String boundaryScannerLocale) {

			_highlightImpl._boundaryScannerLocale = boundaryScannerLocale;

			return this;
		}

		@Override
		public HighlightBuilder boundaryScannerType(
			String boundaryScannerType) {

			_highlightImpl._boundaryScannerType = boundaryScannerType;

			return this;
		}

		@Override
		public Highlight build() {
			return new HighlightImpl(_highlightImpl);
		}

		@Override
		public HighlightBuilder encoder(String encoder) {
			_highlightImpl._encoder = encoder;

			return this;
		}

		@Override
		public HighlightBuilder fieldConfigs(
			Collection<FieldConfig> fieldConfigs) {

			_highlightImpl._fieldConfigs.clear();

			_highlightImpl._fieldConfigs.addAll(fieldConfigs);

			return this;
		}

		@Override
		public HighlightBuilder forceSource(Boolean forceSource) {
			_highlightImpl._forceSource = forceSource;

			return this;
		}

		@Override
		public HighlightBuilder fragmentSize(Integer fragmentSize) {
			_highlightImpl._fragmentSize = fragmentSize;

			return this;
		}

		@Override
		public HighlightBuilder fragmenter(String fragmenter) {
			_highlightImpl._fragmenter = fragmenter;

			return this;
		}

		@Override
		public HighlightBuilder highlightFilter(Boolean highlightFilter) {
			_highlightImpl._highlightFilter = highlightFilter;

			return this;
		}

		@Override
		public HighlightBuilder highlightQuery(Query highlightQuery) {
			_highlightImpl._highlightQuery = highlightQuery;

			return this;
		}

		@Override
		public HighlightBuilder highlighterType(String highlighterType) {
			_highlightImpl._highlighterType = highlighterType;

			return this;
		}

		@Override
		public HighlightBuilder noMatchSize(Integer noMatchSize) {
			_highlightImpl._noMatchSize = noMatchSize;

			return this;
		}

		@Override
		public HighlightBuilder numOfFragments(Integer numOfFragments) {
			_highlightImpl._numOfFragments = numOfFragments;

			return this;
		}

		@Override
		public HighlightBuilder order(String order) {
			_highlightImpl._order = order;

			return this;
		}

		@Override
		public HighlightBuilder phraseLimit(Integer phraseLimit) {
			_highlightImpl._phraseLimit = phraseLimit;

			return this;
		}

		@Override
		public HighlightBuilder postTags(String... postTags) {
			_highlightImpl._postTags = postTags;

			return this;
		}

		@Override
		public HighlightBuilder preTags(String... preTags) {
			_highlightImpl._preTags = preTags;

			return this;
		}

		@Override
		public HighlightBuilder requireFieldMatch(Boolean requireFieldMatch) {
			_highlightImpl._requireFieldMatch = requireFieldMatch;

			return this;
		}

		@Override
		public HighlightBuilder tagsSchema(String tagsSchema) {
			_highlightImpl._tagsSchema = tagsSchema;

			return this;
		}

		@Override
		public HighlightBuilder useExplicitFieldOrder(
			Boolean useExplicitFieldOrder) {

			_highlightImpl._useExplicitFieldOrder = useExplicitFieldOrder;

			return this;
		}

		private final HighlightImpl _highlightImpl = new HighlightImpl();

	}

	private char[] _boundaryChars = {};
	private Integer _boundaryMaxScan;
	private String _boundaryScannerLocale;
	private String _boundaryScannerType;
	private String _encoder;
	private final List<FieldConfig> _fieldConfigs = new ArrayList<>();
	private Boolean _forceSource;
	private Integer _fragmentSize;
	private String _fragmenter;
	private Boolean _highlightFilter;
	private Query _highlightQuery;
	private String _highlighterType;
	private Integer _noMatchSize;
	private Integer _numOfFragments;
	private String _order;
	private Integer _phraseLimit;
	private String[] _postTags = {};
	private String[] _preTags = {};
	private Boolean _requireFieldMatch;
	private String _tagsSchema;
	private Boolean _useExplicitFieldOrder;

}