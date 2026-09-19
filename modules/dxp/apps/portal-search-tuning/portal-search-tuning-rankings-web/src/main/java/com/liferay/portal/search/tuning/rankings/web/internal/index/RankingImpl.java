/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.search.tuning.rankings.helper.RankingHelper;
import com.liferay.portal.search.tuning.rankings.index.Ranking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Bryan Engler
 */
public class RankingImpl implements Ranking {

	public RankingImpl(RankingImpl rankingImpl) {
		_aliases = new ArrayList<>(rankingImpl._aliases);
		_groupExternalReferenceCode = rankingImpl._groupExternalReferenceCode;
		_hiddenDocumentIds = new LinkedHashSet<>(
			rankingImpl._hiddenDocumentIds);
		_indexName = rankingImpl._indexName;
		_name = rankingImpl._name;
		_pinnedDocumentIds = new HashSet<>(rankingImpl._pinnedDocumentIds);
		_pins = new ArrayList<>(rankingImpl._pins);
		_queryString = rankingImpl._queryString;
		_rankingDocumentId = rankingImpl._rankingDocumentId;
		_rankingHelper = rankingImpl._rankingHelper;
		_status = rankingImpl._status;
		_sxpBlueprintExternalReferenceCode =
			rankingImpl._sxpBlueprintExternalReferenceCode;
	}

	@Override
	public List<String> getAliases() {
		return Collections.unmodifiableList(_aliases);
	}

	@Override
	public String getGroupExternalReferenceCode() {
		return _groupExternalReferenceCode;
	}

	@Override
	public List<String> getHiddenDocumentIds() {
		return new ArrayList<>(_hiddenDocumentIds);
	}

	@Override
	public String getIndexName() {
		return _indexName;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getNameForDisplay() {
		StringBundler sb = new StringBundler(4);

		sb.append(_name);

		if (!Objects.equals(_name, _queryString)) {
			sb.append(CharPool.OPEN_BRACKET);
			sb.append(_queryString);
			sb.append(CharPool.CLOSE_BRACKET);
		}

		return sb.toString();
	}

	@Override
	public List<Ranking.Pin> getPins() {
		return Collections.unmodifiableList(_pins);
	}

	@Override
	public String getQueryString() {
		return _queryString;
	}

	@Override
	public Collection<String> getQueryStrings() {
		return _rankingHelper.getQueryStrings(_queryString, _aliases);
	}

	@Override
	public String getRankingDocumentId() {
		return _rankingDocumentId;
	}

	@Override
	public String getSXPBlueprintExternalReferenceCode() {
		return _sxpBlueprintExternalReferenceCode;
	}

	@Override
	public String getStatus() {
		return _status;
	}

	@Override
	public boolean isPinned(String documentId) {
		if (_pinnedDocumentIds.contains(documentId)) {
			return true;
		}

		for (String pinnedDocumentId : _pinnedDocumentIds) {
			if (documentId.equals(
					_rankingHelper.getDocumentId(pinnedDocumentId))) {

				return true;
			}
		}

		return false;
	}

	protected static class BuilderImpl implements Ranking.Builder {

		public BuilderImpl(Ranking ranking, RankingHelper rankingHelper) {
			_rankingImpl = (RankingImpl)ranking;

			_rankingImpl._rankingHelper = rankingHelper;
		}

		public BuilderImpl(RankingHelper rankingHelper) {
			this(new RankingImpl(), rankingHelper);
		}

		public BuilderImpl aliases(List<String> aliases) {
			_rankingImpl._aliases = aliases;

			return this;
		}

		public Ranking build() {
			return new RankingImpl(_rankingImpl);
		}

		public BuilderImpl groupExternalReferenceCode(
			String groupExternalReferenceCode) {

			_rankingImpl._groupExternalReferenceCode =
				groupExternalReferenceCode;

			return this;
		}

		public BuilderImpl hiddenDocumentIds(List<String> hiddenDocumentIds) {
			_rankingImpl._hiddenDocumentIds = new LinkedHashSet<>(
				toList(hiddenDocumentIds));

			return this;
		}

		public BuilderImpl indexName(String indexName) {
			_rankingImpl._indexName = indexName;

			return this;
		}

		public BuilderImpl name(String name) {
			_rankingImpl._name = name;

			return this;
		}

		public BuilderImpl pins(List<Ranking.Pin> pins) {
			if (pins != null) {
				Set<String> documentIds = new LinkedHashSet<>();

				pins.forEach(pin -> documentIds.add(pin.getDocumentId()));

				_rankingImpl._pinnedDocumentIds = documentIds;

				_rankingImpl._pins = pins;
			}
			else {
				_rankingImpl._pinnedDocumentIds.clear();

				_rankingImpl._pins.clear();
			}

			return this;
		}

		public BuilderImpl queryString(String queryString) {
			_rankingImpl._queryString = queryString;

			return this;
		}

		public BuilderImpl rankingDocumentId(String rankingDocumentId) {
			_rankingImpl._rankingDocumentId = rankingDocumentId;

			return this;
		}

		public BuilderImpl status(String status) {
			_rankingImpl._status = status;

			return this;
		}

		public BuilderImpl sxpBlueprintExternalReferenceCode(
			String sxpBlueprintExternalReferenceCode) {

			_rankingImpl._sxpBlueprintExternalReferenceCode =
				sxpBlueprintExternalReferenceCode;

			return this;
		}

		protected <T, V extends T> List<T> toList(List<V> list) {
			if (list != null) {
				return new ArrayList<>(list);
			}

			return new ArrayList<>();
		}

		private final RankingImpl _rankingImpl;

	}

	protected static class PinImpl implements Ranking.Pin {

		public PinImpl(PinImpl pinImpl) {
			_position = pinImpl._position;
			_documentId = pinImpl._documentId;
		}

		public String getDocumentId() {
			return _documentId;
		}

		public int getPosition() {
			return _position;
		}

		protected static class BuilderImpl implements Ranking.Pin.Builder {

			@Override
			public Ranking.Pin build() {
				return new PinImpl(_pinImpl);
			}

			@Override
			public Builder documentId(String documentId) {
				_pinImpl._documentId = documentId;

				return this;
			}

			@Override
			public Builder position(int position) {
				_pinImpl._position = position;

				return this;
			}

			private final PinImpl _pinImpl = new PinImpl();

		}

		private PinImpl() {
		}

		private String _documentId;
		private int _position;

	}

	private RankingImpl() {
	}

	private List<String> _aliases = new ArrayList<>();
	private String _groupExternalReferenceCode;
	private Set<String> _hiddenDocumentIds = new LinkedHashSet<>();
	private String _indexName;
	private String _name;
	private Set<String> _pinnedDocumentIds = new LinkedHashSet<>();
	private List<Ranking.Pin> _pins = new ArrayList<>();
	private String _queryString;
	private String _rankingDocumentId;
	private RankingHelper _rankingHelper;
	private String _status;
	private String _sxpBlueprintExternalReferenceCode;

}