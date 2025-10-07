/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import deleteStructureAction from './actions/deleteStructureAction';
import importStructureAction from './actions/importStructureAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import NameRenderer from './cell_renderers/NameRenderer';
import StructureScopeRenderer from './cell_renderers/StructureScopeRenderer';
import TypeRenderer from './cell_renderers/TypeRenderer';

export default function StructuresFDSPropsTransformer({
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
					component: NameRenderer,
					name: 'nameTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: StructureScopeRenderer,
					name: 'structureScopeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: TypeRenderer,
					name: 'typeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		async onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {data: {id: string}; href?: string};
			event: Event;
			itemData: {
				actions: {
					delete: {href: string; method: string};
				};
				label: Partial<Liferay.Language.FullyLocalizedValue<string>>;
				objectFolderExternalReferenceCode: string;
				status: {code: number};
			};
			loadData: () => {};
		}) {
			if (action.data.id === 'import') {
				event.preventDefault();
				const target = event.target as HTMLAnchorElement;

				importStructureAction(
					target.href,
					itemData.objectFolderExternalReferenceCode,
					loadData
				);
			}
			else if (action.data.id === 'delete') {
				event.preventDefault();
				const target = event.target as HTMLAnchorElement;

				await deleteStructureAction({
					deleteAction: itemData.actions.delete,
					getObjectDefinitionDeleteInfoURL: target.href,
					loadData,
					name:
						getLocalizedValue(
							itemData.label,
							Liferay.ThemeDisplay.getLanguageId()
						) || getLocalizedValue(itemData.label),
					status: itemData.status.code,
				});
			}
		},
	};
}
