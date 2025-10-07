/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useModal} from '@clayui/modal';
import {
	BuilderScreen,
	Card,
	stringUtils,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {ModalEditViewColumn} from '../ModalEditViewColumn/ModalEditViewColumn';
import {TYPES, useViewContext} from '../objectViewContext';

const ViewBuilderScreen: React.FC<
	{children?: React.ReactNode | undefined} & {}
> = () => {
	const [visibleEditModal, setVisibleEditModal] = useState(false);
	const [editingObjectFieldName, setEditingObjectFieldName] = useState('');

	const {observer, onClose} = useModal({
		onClose: () => setVisibleEditModal(false),
	});

	const [
		{
			creationLanguageId,
			objectFields,
			objectView: {objectViewColumns},
		},
		dispatch,
	] = useViewContext();

	const objectFieldsMap = new Map();

	objectFields.forEach((field) => {
		objectFieldsMap.set(field.name, field);
	});

	const selected = objectViewColumns.map((column) => {
		if (objectFieldsMap.has(column.objectFieldName)) {
			return objectFieldsMap.get(column.objectFieldName);
		}
	});

	const handleAddColumns = () => {
		const parentWindow = Liferay.Util.getOpener();

		parentWindow.Liferay.fire('openModalSelectObjectFields', {
			getName: ({label, name}: ObjectField) =>
				stringUtils.getLocalizableLabel({
					fallbackLabel: name,
					fallbackLanguageId: creationLanguageId,
					labels: label,
				}),
			header: Liferay.Language.get('add-columns'),
			items: objectFields.map((objectField) => {
				return {
					...objectField,
					disableCheckbox: false,
				};
			}),
			onSave: (selectedObjectFields: ObjectField[]) =>
				dispatch({
					payload: {
						creationLanguageId,
						selectedObjectFields,
					},
					type: TYPES.ADD_OBJECT_VIEW_COLUMN,
				}),
			selected,
			showModal: true,
			title: Liferay.Language.get('select-the-columns'),
		});
	};

	const handleChangeColumnOrder = (
		draggedIndex: number,
		targetIndex: number
	) => {
		dispatch({
			payload: {draggedIndex, targetIndex},
			type: TYPES.CHANGE_OBJECT_VIEW_COLUMN_ORDER,
		});
	};

	const handleDeleteColumn = (objectFieldName: string) => {
		dispatch({
			payload: {objectFieldName},
			type: TYPES.DELETE_OBJECT_VIEW_COLUMN,
		});

		dispatch({
			payload: {objectFieldName},
			type: TYPES.DELETE_OBJECT_VIEW_SORT_COLUMN,
		});
	};

	return (
		<>
			<Card title={Liferay.Language.get('columns')}>
				<BuilderScreen
					builderScreenItems={objectViewColumns ?? []}
					emptyState={{
						buttonText: Liferay.Language.get('add-column'),
						description: Liferay.Language.get(
							'add-columns-to-start-creating-a-view'
						),
						title: Liferay.Language.get('no-columns-added-yet'),
					}}
					firstColumnHeader={Liferay.Language.get('name')}
					hasDragAndDrop
					onChangeColumnOrder={handleChangeColumnOrder}
					onDeleteColumn={handleDeleteColumn}
					onEditingObjectFieldName={setEditingObjectFieldName}
					onVisibleEditModal={setVisibleEditModal}
					openModal={handleAddColumns}
					secondColumnHeader={Liferay.Language.get('column-label')}
				/>
			</Card>

			{visibleEditModal && (
				<ModalEditViewColumn
					editingObjectFieldName={editingObjectFieldName}
					observer={observer}
					onClose={onClose}
				/>
			)}
		</>
	);
};

export default ViewBuilderScreen;
