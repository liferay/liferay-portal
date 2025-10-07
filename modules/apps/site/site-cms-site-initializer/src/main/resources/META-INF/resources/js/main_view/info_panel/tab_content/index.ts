/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import CategorizationTabContent from './CategorizationTabContent';
import CommentsTabContent from './CommentsTabContent';
import DetailsTabContent from './DetailsTabContent';
import PerformanceTabContent from './PerformanceTabContent';
import VersionsTabContent from './VersionsTabContent';

export const TABS = {
	CATEGORIZATION: {
		component: CategorizationTabContent,
		id: 'categorization',
		name: Liferay.Language.get('categorization'),
	},
	COMMENTS: {
		component: CommentsTabContent,
		id: 'comments',
		name: Liferay.Language.get('comments'),
	},
	DETAILS: {
		component: DetailsTabContent,
		id: 'details',
		name: Liferay.Language.get('details'),
	},
	PERFORMANCE: {
		component: PerformanceTabContent,
		id: 'performance',
		name: Liferay.Language.get('performance'),
	},
	VERSIONS: {
		component: VersionsTabContent,
		id: 'versions',
		name: Liferay.Language.get('versions'),
	},
};
