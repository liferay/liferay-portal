/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringBundler;

import java.io.IOException;

/**
 * @author Alan Huang
 */
public class PropertiesLanguageContractionsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/content/Language.properties")) {
			return content;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;
			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				int x = line.indexOf("'");

				if (x == -1) {
					continue;
				}

				String[] array = line.split("=", 2);

				if (array.length != 2) {
					continue;
				}

				String lowerCaseDescription = array[1].toLowerCase();

				for (String contraction : _CONTRACTIONS) {
					int i = lowerCaseDescription.indexOf(contraction);

					if ((i == -1) ||
						Character.isLetterOrDigit(
							lowerCaseDescription.charAt(i - 1)) ||
						Character.isLetterOrDigit(
							lowerCaseDescription.charAt(
								i + contraction.length()))) {

						continue;
					}

					addMessage(
						fileName,
						StringBundler.concat(
							"Do not use contraction \"", contraction,
							"\" in the description of key \"", array[0], "\""),
						lineNumber);
				}
			}
		}

		return content;
	}

	private static final String[] _CONTRACTIONS = {
		"aren't", "can't", "could've", "couldn't", "didn't", "doesn't", "don't",
		"hadn't", "hasn't", "haven't", "how's", "I'd", "I'll", "I've", "isn't",
		"it's", "let's", "shouldn't", "that's", "there's", "wasn't", "we'd",
		"we'll", "we're", "we've", "weren't", "what's", "where's", "won't",
		"would've", "wouldn't", "you'd", "you'll", "you're", "you've"
	};

}