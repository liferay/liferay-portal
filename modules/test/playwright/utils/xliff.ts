/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function changeXliffModelId(xliff: string) {
	return xliff.replace(/JournalArticle:\d+/, (modelId) => `${modelId}1`);
}

export function translateXliff(
	xliff: string,
	translations: {[source: string]: string}
) {
	let translatedCount = 0;

	const translatedXliff = xliff.replace(
		/(<source[^>]*>\s*<!\[CDATA\[)([\s\S]*?)(\]\]>\s*<\/source>\s*<target[^>]*?)(?:\/>|>[\s\S]*?<\/target>)/g,
		(match, sourceStart, source, targetStart) => {
			if (!(source in translations)) {
				return match;
			}

			translatedCount++;

			return `${sourceStart}${source}${targetStart}><![CDATA[${translations[source]}]]></target>`;
		}
	);

	if (translatedCount !== Object.keys(translations).length) {
		throw new Error(
			`Translated ${translatedCount} of ${Object.keys(translations).length} sources in:\n${xliff}`
		);
	}

	return translatedXliff;
}
