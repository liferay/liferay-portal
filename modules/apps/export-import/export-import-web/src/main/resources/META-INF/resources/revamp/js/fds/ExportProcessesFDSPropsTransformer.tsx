/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import React from 'react';

import ProcessAuthorRenderer from './cell_renderers/ProcessAuthorRenderer';
import ProcessCompletionDateRenderer from './cell_renderers/ProcessCompletionDateRenderer';
import ProcessStatusRenderer from './cell_renderers/ProcessStatusRenderer';
import ProcessTitleRenderer from './cell_renderers/ProcessTitleRenderer';
import {ItemAction, toLiveVisibilityAction} from './liveVisibilityAction';

export default function ExportProcessesFDSPropsTransformer({
	additionalProps,
	itemsActions,
	...otherProps
}: {
	additionalProps: {staticSiteExportBackgroundTaskIds: number[]};
	itemsActions?: ItemAction[];
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
					component: ProcessCompletionDateRenderer,
					name: 'processCompletionDateRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: (props: object) => (
						<ProcessStatusRenderer
							{...props}
							progressEndpoint="/o/export-import/v1.0/export-processes"
						/>
					),
					name: 'exportProcessStatusRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions?.map((itemAction) => {
			if (itemAction.data?.id === 'relaunch') {
				return {
					...itemAction,
					isVisible: (itemData: {id: number}) =>
						!additionalProps.staticSiteExportBackgroundTaskIds.includes(
							itemData.id
						),
				};
			}

			return toLiveVisibilityAction(itemAction);
		}),
	};
}
