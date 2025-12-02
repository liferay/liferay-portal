/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.ml.embedding.text.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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

		_nonlocalizedContentSB = new StringBundler((_size * 2) - 1);
	}

	public void appendToAll(String value) {
		_append(_nonlocalizedContentSB, value);

		for (StringBundler localizedContentSB :
				_languageContentSBMap.values()) {

			_append(localizedContentSB, value);
		}
	}

	public void appendToLocale(String languageId, String value) {
		_append(_getLanguageContentStringBundler(languageId), value);
	}

	public void contribute(Document document) {
		if (!FeatureFlagManagerUtil.isEnabled(_companyId, "LPS-122920")) {
			return;
		}

		for (String languageId :
				_textEmbeddingDocumentContributor.getLanguageIds(_model)) {

			String localizedContent = getLocalizedContent(languageId);

			if (localizedContent == null) {
				localizedContent = getLocalizedContent(_defaultLanguageId);

				if (localizedContent == null) {
					continue;
				}
			}

			_textEmbeddingDocumentContributor.contribute(
				document, languageId, _model, localizedContent);
		}
	}

	public String getLocalizedContent(String languageId) {
		StringBundler languageContentSB = _languageContentSBMap.get(languageId);

		if ((languageContentSB != null) && (languageContentSB.length() != 0)) {
			return languageContentSB.toString();
		}

		if (!_defaultLanguageId.equals(languageId)) {
			StringBundler defaultLanguageContentSB = _languageContentSBMap.get(
				_defaultLanguageId);

			if ((defaultLanguageContentSB != null) &&
				(defaultLanguageContentSB.length() != 0)) {

				return defaultLanguageContentSB.toString();
			}
		}

		return null;
	}

	public Map<String, String> getLocalizedContentMap() {
		Map<String, String> languageContentMap = new TreeMap<>();

		if (_languageContentSBMap.isEmpty()) {
			return languageContentMap;
		}

		for (Map.Entry<String, StringBundler> entry :
				_languageContentSBMap.entrySet()) {

			StringBundler languageContentSB = entry.getValue();

			if ((languageContentSB != null) &&
				(languageContentSB.length() != 0)) {

				languageContentMap.put(
					entry.getKey(), languageContentSB.toString());
			}
		}

		return languageContentMap;
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

	private StringBundler _getLanguageContentStringBundler(String languageId) {
		return _languageContentSBMap.computeIfAbsent(
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
	private final Map<String, StringBundler> _languageContentSBMap =
		new TreeMap<>();
	private final T _model;
	private final StringBundler _nonlocalizedContentSB;
	private final int _size;
	private final TextEmbeddingDocumentContributor
		_textEmbeddingDocumentContributor;

}