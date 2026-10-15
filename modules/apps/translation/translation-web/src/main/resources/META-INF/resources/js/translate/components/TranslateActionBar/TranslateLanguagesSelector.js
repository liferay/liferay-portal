/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import LanguageSelector from './LanguageSelector';

const TranslateLanguagesSelector = ({
	confirmChangesBeforeReload,
	sourceAvailableLanguages,
	sourceLanguageId,
	targetAvailableLanguages,
	targetLanguageId,
}) => {
	const changePage = (sourceId, targetId) => {
		confirmChangesBeforeReload({
			sourceLanguageId: sourceId,
			targetLanguageId: targetId,
		});
	};

	return (
		<ClayLayout.ContentRow
			className="languages-selector"
			verticalAlign="center"
		>
			<ClayLayout.ContentCol>
				{Liferay.Language.get('translate-from')}
			</ClayLayout.ContentCol>

			<ClayLayout.ContentCol>
				<LanguageSelector
					languageIds={sourceAvailableLanguages}
					onChange={(value) => {
						changePage(value, targetLanguageId);
					}}
					selectedLanguageId={sourceLanguageId}
				/>
			</ClayLayout.ContentCol>

			<ClayLayout.ContentCol>
				{Liferay.Language.get('to[direction]').toLowerCase()}
			</ClayLayout.ContentCol>

			<ClayLayout.ContentCol>
				<LanguageSelector
					languageIds={targetAvailableLanguages}
					onChange={(value) => {
						changePage(sourceLanguageId, value);
					}}
					selectedLanguageId={targetLanguageId}
				/>
			</ClayLayout.ContentCol>
		</ClayLayout.ContentRow>
	);
};

export default TranslateLanguagesSelector;
