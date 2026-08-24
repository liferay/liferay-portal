/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EConfigInURLBehavior,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';

import ReportEntryStatusRenderer from './cell_renderers/ReportEntryStatusRenderer';

export default function ReportEntriesFDSPropsTransformer({...otherProps}) {
	return {
		...otherProps,
		configInURLBehavior: EConfigInURLBehavior.OFF,
		customRenderers: {
			tableCell: [
				{
					component: ReportEntryStatusRenderer,
					name: 'reportEntryStatusRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
	};
}
