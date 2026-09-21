/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import BulkActionTaskResultRenderer from './cell_renderers/BulkActionTaskResultRenderer';

export default function BulkActionTaskReportFDSPropsTransformer({
	...otherProps
}: {
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: BulkActionTaskResultRenderer,
					name: 'result',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		searchAsYouType: true,
		searchSuggestionsEnabled: true,
	};
}
