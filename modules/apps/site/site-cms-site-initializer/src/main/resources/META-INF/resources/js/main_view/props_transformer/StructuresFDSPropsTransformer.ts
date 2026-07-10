/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionItem,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';

import StatusLabel from '../../common/components/StatusLabel';
import {IBulkActionFDSData} from '../../common/types/BulkActionTask';
import {ObjectDefinition} from '../../common/types/ObjectDefinition';
import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {StructureWorkflowItem} from '../modal/AssignDefaultWorkflowModalContent';
import assignStructureDefaultWorkflowBulkAction from './actions/AssignStructureDefaultWorkflowBulkSelectionAction';
import defaultWorkflowStructureAction from './actions/defaultWorkflowStructureAction';
import deleteStructureAction from './actions/deleteStructureAction';
import importStructureAction from './actions/importStructureAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import StructureScopeRenderer from './cell_renderers/StructureScopeRenderer';
import TypeRenderer from './cell_renderers/TypeRenderer';
import transformFDSBulkActions from './utils/transformFDSBulkActions';

export default function StructuresFDSPropsTransformer({
	additionalProps,
	bulkActions = [],
	...otherProps
}: {
	additionalProps?: any;
	apiURL: string;
	bulkActions: Array<IBulkActionItem>;
	otherProps: any;
}) {
	return {
		...otherProps,
		additionalProps,
		bulkActions: transformFDSBulkActions(bulkActions),
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: (props: any) =>
						SimpleActionLinkRenderer({
							...props,
							systemIconLabel: Liferay.Language.get(
								'system-default-structure'
							),
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: StructureScopeRenderer,
					name: 'structureScopeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: (props: any) =>
						TypeRenderer({
							...props,
							objectFolderTypeLabels:
								additionalProps?.objectFolderTypeLabels,
						}),
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
		async onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {
				data: {id: string; structureId?: string; workflow?: string};
				href?: string;
			};
			event: Event;
			itemData: {
				actions: {
					delete: {href: string; method: string};
				};
				id: number;
				label: Partial<Liferay.Language.FullyLocalizedValue<string>>;
				objectFolderExternalReferenceCode: string;
				objectRelationships: ObjectDefinition['objectRelationships'];
				status: {code: number};
				workflowDefinitionLinks: ObjectDefinition['workflowDefinitionLinks'];
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
					getObjectDefinitionDeleteInfoURL: target.href,
					loadData,
					name:
						getLocalizedValue(
							itemData.label,
							Liferay.ThemeDisplay.getLanguageId()
						) || getLocalizedValue(itemData.label),
					relationships: itemData.objectRelationships,
					status: itemData.status.code,
					structureId: itemData.id,
				});
			}
			else if (action.data.id === 'assign-default-workflow') {
				const item = {
					id: String(itemData.id),
					name: getLocalizedValue(itemData.label),
					workflow: itemData.workflowDefinitionLinks?.[0]
						? itemData.workflowDefinitionLinks[0]
								.workflowDefinitionName
						: '',
				} as StructureWorkflowItem;

				defaultWorkflowStructureAction([item]);
			}
		},
		onBulkActionItemClick: ({
			action,
			selectedData,
		}: {
			action: {data?: {id?: string}};
			selectedData: Required<IBulkActionFDSData>;
		}) => {
			if (action?.data?.id === 'assign-default-workflow') {
				const structureWorkflows = selectedData.items.map(
					(itemData: any): StructureWorkflowItem => {
						const defaultWorkflowLink =
							itemData.workflowDefinitionLinks.find(
								(workflowDefinitionLink: {
									groupExternalReferenceCode: string;
									workflowDefinitionName: string;
								}) =>
									workflowDefinitionLink.groupExternalReferenceCode ===
									''
							);

						return {
							id: String(itemData.id),
							name: getLocalizedValue(itemData.label),
							workflow:
								defaultWorkflowLink?.workflowDefinitionName ||
								'',
						};
					}
				);

				assignStructureDefaultWorkflowBulkAction({
					apiURL: otherProps.apiURL,
					selectedData,
					structureWorkflows,
				});
			}
		},
	};
}
