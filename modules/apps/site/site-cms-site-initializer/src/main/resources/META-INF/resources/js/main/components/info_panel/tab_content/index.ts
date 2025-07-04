/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import CategorizationTabContent from './CategorizationTabContent';
import DetailsTabContent from './DetailsTabContent';
import PerformanceTabContent from './PerformanceTabContent';
import VersionsTabContent from './VersionsTabContent';

type Tab = {
	className?: string;
	component: React.ComponentType;
	id: string;
	name: string;
};

export const TABS: Record<string, Tab> = {
	CATEGORIZATION: {
		component: CategorizationTabContent,
		id: 'categorization',
		name: Liferay.Language.get('categorization'),
	},
	DETAILS: {
		component: DetailsTabContent,
		id: 'details',
		name: Liferay.Language.get('details'),
	},
	PERFORMANCE: {
		className: 'p-4',
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
