/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {SingleSelect} from '@liferay/object-js-components-web';
import React, {FormEvent, useEffect, useMemo, useState} from 'react';

import {TYPES, useViewContext} from '../objectViewContext';
import {TObjectViewColumn} from '../types';

interface ModalAddDefaultSortColumnProps {
	editingObjectFieldName?: string;
	header: string;
	isEditingSort: boolean;
	observer: Observer;
	onClose: () => void;
}

const SORT_OPTIONS: LabelValueObject[] = [
	{
		label: Liferay.Language.get('ascending'),
		value: 'asc',
	},
	{
		label: Liferay.Language.get('descending'),
		value: 'desc',
	},
];

export function ModalAddDefaultSortColumn({
	editingObjectFieldName,
	header,
	isEditingSort,
	observer,
	onClose,
}: ModalAddDefaultSortColumnProps) {
	const [
		{
			creationLanguageId,
			objectFields,
			objectView: {objectViewColumns, objectViewSortColumns},
		},
		dispatch,
	] = useViewContext();

	const [availableViewColumns, setAvailableViewColumns] =
		useState<TObjectViewColumn[]>(objectViewColumns);

	const [selectedObjectSortColumnName, setSelectedObjectSortColumnValueName] =
		useState<string>();
	const [selectedObjetSortValue, setSelectedObjetSortValue] = useState('asc');

	const objectSortColumnItems = useMemo(() => {
		return availableViewColumns.map(({fieldLabel, objectFieldName}) => ({
			label: fieldLabel,
			value: objectFieldName,
		}));
	}, [availableViewColumns]);

	const onSubmit = (event: FormEvent) => {
		event.preventDefault();

		let objectFieldName = selectedObjectSortColumnName;

		if (!objectFieldName && !!objectSortColumnItems.length) {
			objectFieldName = objectSortColumnItems[0].value;
		}

		if (isEditingSort && editingObjectFieldName) {
			dispatch({
				payload: {
					editingObjectFieldName,
					selectedObjectSort: selectedObjetSortValue,
				},
				type: TYPES.EDIT_OBJECT_VIEW_SORT_COLUMN_SORT_ORDER,
			});
		}
		else {
			dispatch({
				payload: {
					creationLanguageId,
					objectFieldName: objectFieldName!,
					objectFields,
					objectViewSortColumns,
					selectedObjetSortValue,
				},
				type: TYPES.ADD_OBJECT_VIEW_SORT_COLUMN,
			});
		}

		onClose();
	};

	useEffect(() => {
		const newAvailableViewColumns = objectViewColumns.filter(
			({defaultSort, objectFieldBusinessType}) =>
				!defaultSort &&
				objectFieldBusinessType !== 'Aggregation' &&
				objectFieldBusinessType !== 'Attachment' &&
				objectFieldBusinessType !== 'Encrypted' &&
				objectFieldBusinessType !== 'Formula' &&
				objectFieldBusinessType !== 'Relationship' &&
				objectFieldBusinessType !== 'RichText'
		);

		if (editingObjectFieldName) {
			const newSelectedObjectSortColumn = objectViewSortColumns.find(
				(objectViewSortColumn) =>
					objectViewSortColumn.objectFieldName ===
					editingObjectFieldName
			);

			setSelectedObjectSortColumnValueName(
				newSelectedObjectSortColumn?.objectFieldName
			);
			setSelectedObjetSortValue(
				newSelectedObjectSortColumn?.sortOrder as string
			);
		}
		setAvailableViewColumns(newAvailableViewColumns);
	}, [editingObjectFieldName, objectViewColumns, objectViewSortColumns]);

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={onSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{header}
				</ClayModal.Header>

				<ClayModal.Body>
					<SingleSelect
						disabled={isEditingSort}
						id="objectViewModalAddDefaultSortColumn"
						items={objectSortColumnItems}
						label={Liferay.Language.get('columns')}
						onSelectionChange={(value) => {
							setSelectedObjectSortColumnValueName(
								value as string
							);
						}}
						required
						selectedKey={selectedObjectSortColumnName}
					/>

					<SingleSelect<LabelValueObject>
						items={SORT_OPTIONS}
						label={Liferay.Language.get('sorting')}
						onSelectionChange={(value) => {
							setSelectedObjetSortValue(value as string);
						}}
						selectedKey={selectedObjetSortValue}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group key={1} spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => onClose()}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={
									isEditingSort
										? false
										: !selectedObjectSortColumnName
								}
								displayType="primary"
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}
