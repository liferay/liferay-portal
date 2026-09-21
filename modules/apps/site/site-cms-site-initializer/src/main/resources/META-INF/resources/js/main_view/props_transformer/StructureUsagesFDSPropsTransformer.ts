/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';

import StatusLabel from '../../common/components/StatusLabel';
import {getScopeExternalReferenceCode} from '../../common/utils/getScopeExternalReferenceCode';
import confirmAndDeleteEntryAction from './actions/confirmAndDeleteEntryAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SpaceRendererWithCache from './cell_renderers/SpaceRendererWithCache';
import TypeRenderer from './cell_renderers/TypeRenderer';

export default function StructureUsagesFDSPropsTransformer({
	...otherProps
}: {
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						SpaceRendererWithCache({
							scopeKey: itemData.embedded.scopeKey,
							spaceExternalReferenceCode:
								getScopeExternalReferenceCode(itemData),
						}),
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: TypeRenderer,
					name: 'typeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({value}) => StatusLabel(value),
					name: 'statusTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {data: {id: string}};
			event: Event;
			itemData: {
				embedded: {
					actions: {delete: {href: string; method: string}};
				};
				title: string;
			};
			loadData: () => void;
		}) {
			if (action?.data?.id === 'delete') {
				event?.preventDefault();

				confirmAndDeleteEntryAction({
					bodyHTML: Liferay.Language.get(
						'are-you-sure-you-want-to-delete-this-entry'
					),
					deleteAction: itemData.embedded.actions.delete,
					loadData,
					successMessage: sub(
						Liferay.Language.get('x-was-successfully-deleted'),
						`<strong>${Liferay.Util.escapeHTML(itemData.title)}</strong>`
					),
					title: Liferay.Language.get('delete-entry'),
				});
			}
		},
		searchAsYouType: true,
		searchSuggestionsEnabled: true,
	};
}
