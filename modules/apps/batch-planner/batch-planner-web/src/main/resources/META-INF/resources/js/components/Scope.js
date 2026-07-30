/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelect} from '@clayui/form';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {HEADLESS_BATCH_PLANNER_URL, SCHEMA_SELECTED_EVENT} from '../constants';

function Scope({portletNamespace}) {
	const [scopes, setScopes] = useState([]);

	useEffect(() => {
		const handleSchemaUpdated = (event) => {
			if (event.schemaName) {
				const planURL = `${HEADLESS_BATCH_PLANNER_URL}/plans/${event.schemaName.replace(
					'#',
					encodeURIComponent('#')
				)}`;

				Promise.all(
					['asset-library-scopes', 'site-scopes'].map((scopesPath) =>
						fetch(
							`${planURL}/${scopesPath}?export=${event.isExport}`
						).then((response) => {
							if (!response.ok) {
								throw new Error();
							}

							return response.json();
						})
					)
				)
					.then(([assetLibraryScopesJSON, siteScopesJSON]) => {
						setScopes([
							...(assetLibraryScopesJSON.items || []),
							...(siteScopesJSON.items || []),
						]);
					})
					.catch(() => {
						setScopes([]);

						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});
					});
			}
			else {
				setScopes([]);
			}
		};

		Liferay.on(SCHEMA_SELECTED_EVENT, handleSchemaUpdated);

		return () => {
			Liferay.detach(SCHEMA_SELECTED_EVENT, handleSchemaUpdated);
		};
	}, []);

	const selectId = `${portletNamespace}siteId`;

	return (
		!!scopes.length && (
			<ClayForm.Group>
				<label htmlFor={selectId}>
					{Liferay.Language.get('scope')}
				</label>

				<ClaySelect id={selectId} name={selectId}>
					{scopes.map((scope) => (
						<ClaySelect.Option
							key={scope.value}
							label={scope.label}
							value={scope.value}
						/>
					))}
				</ClaySelect>
			</ClayForm.Group>
		)
	);
}

export default Scope;
