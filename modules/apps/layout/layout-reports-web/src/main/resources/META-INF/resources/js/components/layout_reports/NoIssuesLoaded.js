/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React, {useContext} from 'react';

import {
	StoreDispatchContext,
	StoreStateContext,
} from '../../context/StoreContext';
import loadIssues from '../../utils/loadIssues';

export default function NoIssuesLoaded() {
	const {data, languageId} = useContext(StoreStateContext);
	const dispatch = useContext(StoreDispatchContext);

	const {imagesPath} = data;

	const defaultIllustration = `${imagesPath}/issues_default.svg`;

	const onLaunchButtonClick = () => {
		const url = data.pageURLs.find(
			(pageURL) =>
				pageURL.languageId === (languageId || data.defaultLanguageId)
		);

		loadIssues({
			dispatch,
			languageId,
			url,
		});
	};

	return (
		<div className="c-pb-3 c-px-3 text-center">
			<img
				alt={Liferay.Language.get(
					'default-page-audit-image-alt-description'
				)}
				className="my-4"
				src={defaultIllustration}
				width="120px"
			/>

			<p className="text-secondary">
				{Liferay.Language.get(
					"launch-a-page-audit-to-check-issues-that-impact-on-your-page's-accesibility-and-seo"
				)}
			</p>

			<ClayButton displayType="secondary" onClick={onLaunchButtonClick}>
				{Liferay.Language.get('launch[verb]')}
			</ClayButton>
		</div>
	);
}
