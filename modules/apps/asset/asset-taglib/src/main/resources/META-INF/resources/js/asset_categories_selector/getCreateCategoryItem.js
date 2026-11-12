/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export default function getCreateCategoryItem({
	showAddCategoryButton,
	vocabularyIds = [],
}) {
	if (!showAddCategoryButton || vocabularyIds.length !== 1) {
		return undefined;
	}

	return {
		label: Liferay.Language.get('add-category'),
		onCreate: async (name) => {
			const response = await fetch(
				`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabularyIds[0]}/taxonomy-categories`,
				{
					body: JSON.stringify({name}),
					headers: {
						'Accept': 'application/json',
						'Accept-Language':
							Liferay.ThemeDisplay.getBCP47LanguageId(),
						'Content-Type': 'application/json',
					},
					method: 'POST',
				}
			);

			const json = await response.json();

			if (!response.ok) {
				throw new Error(
					json.title ??
						Liferay.Language.get('an-unexpected-error-occurred')
				);
			}
		},
	};
}
