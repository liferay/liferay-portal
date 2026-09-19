/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search.suggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class NGramHolder {

	public void addNGram(int number, String gram) {
		String key = "gram" + number;

		List<String> grams = _nGrams.get(key);

		if (grams == null) {
			grams = new ArrayList<>();

			_nGrams.put(key, grams);
		}

		grams.add(gram);
	}

	public void addNGramEnd(int number, String gram) {
		_nGramEnds.put("end" + number, gram);
	}

	public void addNGramStart(int number, String gram) {
		_nGramStarts.put("start" + number, gram);
	}

	public Map<String, String> getNGramEnds() {
		return _nGramEnds;
	}

	public Map<String, String> getNGramStarts() {
		return _nGramStarts;
	}

	public Map<String, List<String>> getNGrams() {
		return _nGrams;
	}

	private final Map<String, String> _nGramEnds = new HashMap<>();
	private final Map<String, String> _nGramStarts = new HashMap<>();
	private final Map<String, List<String>> _nGrams = new HashMap<>();

}