/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.ml.embedding.text.helper;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Rodrigo Guedes de Souza
 * @author Joshua Cords
 */
public class TextEmbeddingContentHelper<T extends BaseModel<T>> {

	public TextEmbeddingContentHelper(
		long companyId, String defaultLanguageId, String delimiter, T model,
		int size,
		TextEmbeddingDocumentContributor textEmbeddingDocumentContributor) {

		_companyId = companyId;
		_defaultLanguageId = defaultLanguageId;
		_delimiter = delimiter;
		_model = model;
		_size = size;
		_textEmbeddingDocumentContributor = textEmbeddingDocumentContributor;

		_nonlocalizedContentSB = new StringBundler((size * 2) - 1);
	}

	public void append(String value) {
		_append(_nonlocalizedContentSB, value);

		for (StringBundler localizedContentSB : _localizedContentSBs.values()) {
			_append(localizedContentSB, value);
		}
	}

	public void append(String languageId, String value) {
		_append(_getLocalizedContentSB(languageId), value);
	}

	public void contribute(Document document) {
		for (String languageId :
				_textEmbeddingDocumentContributor.getLanguageIds(_model)) {

			String localizedContent = getLocalizedContent(languageId);

			if (localizedContent == null) {
				continue;
			}

			_textEmbeddingDocumentContributor.contribute(
				document, languageId, _model, localizedContent);
		}
	}

	public String getLocalizedContent(String languageId) {
		StringBundler localizedContentSB = _localizedContentSBs.get(languageId);

		if ((localizedContentSB != null) &&
			(localizedContentSB.length() != 0)) {

			return localizedContentSB.toString();
		}

		if (!_defaultLanguageId.equals(languageId)) {
			localizedContentSB = _localizedContentSBs.get(_defaultLanguageId);

			if ((localizedContentSB != null) &&
				(localizedContentSB.length() != 0)) {

				return localizedContentSB.toString();
			}
		}

		return null;
	}

	public Map<String, String> getLocalizedContentMap() {
		Map<String, String> localizedContentMap = new TreeMap<>();

		for (Map.Entry<String, StringBundler> entry :
				_localizedContentSBs.entrySet()) {

			StringBundler localizedContentSB = entry.getValue();

			if ((localizedContentSB != null) &&
				(localizedContentSB.length() != 0)) {

				localizedContentMap.put(
					entry.getKey(), localizedContentSB.toString());
			}
		}

		return localizedContentMap;
	}

	public String getNonlocalizedContent() {
		return _nonlocalizedContentSB.toString();
	}

	private void _append(StringBundler sb, String value) {
		if (sb == null) {
			return;
		}

		if (sb.length() > 0) {
			sb.append(_delimiter);
		}

		sb.append(value);
	}

	private StringBundler _getLocalizedContentSB(String languageId) {
		return _localizedContentSBs.computeIfAbsent(
			languageId,
			key -> {
				StringBundler sb = new StringBundler(_size);

				if (_nonlocalizedContentSB.length() != 0) {
					sb.append(_nonlocalizedContentSB);
				}

				return sb;
			});
	}

	private final long _companyId;
	private final String _defaultLanguageId;
	private final String _delimiter;
	private final Map<String, StringBundler> _localizedContentSBs =
		new TreeMap<>();
	private final T _model;
	private final StringBundler _nonlocalizedContentSB;
	private final int _size;
	private final TextEmbeddingDocumentContributor
		_textEmbeddingDocumentContributor;

}