/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Page from '../../../components/Page';
import SearchBuilder from '../../../core/SearchBuilder';
import {ProductTypeVocabulary} from '../../../enums/Product';
import {AdministratorAppsListView} from './Apps';

export default function Solutions() {
	return (
		<Page title="Solutions">
			<AdministratorAppsListView
				filter={SearchBuilder.lambda(
					'categoryNames',
					ProductTypeVocabulary.SOLUTION
				)}
				listViewProps={{id: 'administrator-solutions'}}
			/>
		</Page>
	);
}
