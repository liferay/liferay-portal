/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {openModal, openToast, sub} from 'frontend-js-web';
import React, {Dispatch, SetStateAction} from 'react';

import EditAPIPropertyModalContent from './modals/EditAPIPropertyModalContent';
import {
	BUSINESS_TYPES_TO_SYMBOLS,
	UNSUPPORTED_BUSINESS_TYPES,
} from './utils/constants';

interface PropertiesTreeViewProps {
	schemaUIData: APISchemaUIData;
	searchState: SearchState;
	setSchemaUIData: Dispatch<SetStateAction<APISchemaUIData>>;
}

interface SearchState {
	filteredSchemaProperties: TreeViewItemData[];
	searchKeyword: string;
}

export default function PropertiesTreeView({
	schemaUIData,
	searchState,
	setSchemaUIData,
}: PropertiesTreeViewProps) {
	const getIconName = (businessType: ObjectFieldBusinessType) => {
		if (businessType) {
			return BUSINESS_TYPES_TO_SYMBOLS[businessType];
		}

		return 'simple-circle';
	};

	const getItems = () => {
		if (
			!searchState.filteredSchemaProperties.length &&
			searchState.searchKeyword !== ''
		) {
			return [];
		}

		if (searchState.filteredSchemaProperties.length) {
			return searchState.filteredSchemaProperties;
		}

		return schemaUIData.schemaProperties;
	};

	const handleEditAPIProperty = ({
		businessType,
		description,
		name,
		objectFieldId,
		objectFieldName,
	}: Partial<TreeViewItemData>) => {
		openModal({
			center: true,
			contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
				EditAPIPropertyModalContent({
					businessType,
					closeModal,
					description,
					name,
					objectFieldId,
					objectFieldName,
					setSchemaUIData,
				}),
			id: 'editAPIPropertyModal',
			size: 'md',
		});
	};

	const handleRemoveProperty = (objectFieldId: number) => {
		setSchemaUIData((previous) => {
			return previous.schemaProperties
				? {
						...previous,
						schemaProperties: previous.schemaProperties.filter(
							(property) =>
								property.objectFieldId !== objectFieldId
						),
					}
				: previous;
		});
		openToast({
			message: Liferay.Language.get('schema-property-was-deleted'),
			type: 'success',
		});
	};

	return (
		<div className="d-flex treeview-container">
			{!schemaUIData.schemaProperties?.length ? (
				<div className="first-property-drop-area">
					<p>
						{Liferay.Language.get(
							'click-on-the-object-fields-in-the-sidebar-to-add-properties'
						)}
					</p>
				</div>
			) : (
				<TreeView
					dragAndDrop
					items={getItems()}
					onItemMove={(_, parentItem, __) => {
						return parentItem ? false : true;
					}}
					onItemsChange={(items) =>
						items &&
						setSchemaUIData((previous) => ({
							...previous,
							schemaProperties: items,
						}))
					}
				>
					{({
						businessType,
						description,
						name,
						objectDefinitionName,
						objectFieldId,
						objectFieldName,
					}) => (
						<TreeView.Item
							actions={
								<>
									<ClayButton
										aria-label={sub(
											Liferay.Language.get(
												'edit-x-property'
											),
											name
										)}
										monospaced
										onClick={() =>
											handleEditAPIProperty({
												businessType,
												description,
												name,
												objectFieldId,
												objectFieldName,
											})
										}
									>
										<ClayIcon symbol="pencil" />
									</ClayButton>

									<ClayButton
										aria-label={sub(
											Liferay.Language.get(
												'delete-x-property'
											),
											name
										)}
										monospaced
										onClick={() => {
											handleRemoveProperty(objectFieldId);
										}}
									>
										<ClayIcon symbol="trash" />
									</ClayButton>
								</>
							}
							key={objectFieldId}
						>
							<ClayIcon symbol={getIconName(businessType)} />

							<span className="treeview-item-label">{objectFieldName}</span>

							{UNSUPPORTED_BUSINESS_TYPES.includes(
								businessType
							) && (
								<ClayTooltipProvider>
									<span
										className="inline-item-after"
										title={Liferay.Language.get(
											'under-development'
										)}
									>
										<ClayIcon
											className="text-secondary"
											symbol="warning-full"
										/>
									</span>
								</ClayTooltipProvider>
							)}

							<span className="text-truncate treeview-item-path">
								&nbsp;
								{!UNSUPPORTED_BUSINESS_TYPES.includes(
									businessType
								) &&
									`(${objectDefinitionName}.${objectFieldName})`}
							</span>
						</TreeView.Item>
					)}
				</TreeView>
			)}
		</div>
	);
}
