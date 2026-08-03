/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import React from 'react';
import {ExportReportEntriesModal} from 'staging-taglib';

import ProcessAuthorRenderer from './cell_renderers/ProcessAuthorRenderer';
import ProcessCompletionDateRenderer from './cell_renderers/ProcessCompletionDateRenderer';
import ProcessStatusRenderer from './cell_renderers/ProcessStatusRenderer';
import ProcessTitleRenderer from './cell_renderers/ProcessTitleRenderer';
import {ItemAction, toLiveVisibilityAction} from './liveVisibilityAction';

export default function ImportProcessesFDSPropsTransformer({
	itemsActions,
	...otherProps
}: {
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
							progressEndpoint="/o/export-import/v1.0/import-processes"
						/>
					),
					name: 'importProcessStatusRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions?.map(toLiveVisibilityAction),
		onActionDropdownItemClick({
			action,
			event,
			itemData,
		}: {
			action: {data: {id: string}};
			event: Event;
			itemData: {id: number; name?: string};
		}) {
			if (action.data.id === 'exportReportEntries') {
				event?.preventDefault();

				const filename = `${(itemData.name ?? '').replace(
					/\.lar$/,
					''
				)}_report_entries.zip`;

				openModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<ExportReportEntriesModal
							closeModal={closeModal}
							filename={filename}
							importProcessId={String(itemData.id)}
						/>
					),
					disableAutoClose: true,
				});
			}
		},
	};
}
