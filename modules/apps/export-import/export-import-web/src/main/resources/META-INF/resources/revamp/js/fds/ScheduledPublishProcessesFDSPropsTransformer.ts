/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import ProcessAuthorRenderer from './cell_renderers/ProcessAuthorRenderer';
import ProcessEndDateRenderer from './cell_renderers/ProcessEndDateRenderer';
import ProcessTitleRenderer from './cell_renderers/ProcessTitleRenderer';

export default function ScheduledPublishProcessesFDSPropsTransformer({
	...otherProps
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: ProcessTitleRenderer,
					name: 'processTitleRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ProcessAuthorRenderer,
					name: 'processAuthorRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ProcessEndDateRenderer,
					name: 'processEndDateRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
	};
}
