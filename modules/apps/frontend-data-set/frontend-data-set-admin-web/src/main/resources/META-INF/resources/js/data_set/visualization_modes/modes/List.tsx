/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import ClayTable from '@clayui/table';
import classNames from 'classnames';
import {openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import '../../../../css/ListVisualizationMode.scss';
import FieldSelectModalContent from '../../../components/AddDataSourceFieldsModalContent';
import {
	DEFAULT_FETCH_HEADERS,
	OBJECT_RELATIONSHIP,
} from '../../../utils/constants';
import getDataSetResourceURL from '../../../utils/getDataSetResourceURL';
import openDefaultFailureToast from '../../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../../utils/openDefaultSuccessToast';
import {IField, IFieldTreeItem} from '../../../utils/types';
import {IDataSetSectionProps} from '../../DataSet';
import AddCustomFieldModalContent from '../components/AddCustomFieldModalContent';
import FieldAssignmentControls from '../components/FieldAssignmentControls';

interface IFDSListSection {
	externalReferenceCode: string;
	fieldName: string;
	id: string;
	name: string;
	rendererName?: string;
}
interface IListSection {
	externalReferenceCode?: IFDSListSection['externalReferenceCode'];
	field?: IField;
	fieldTreeItems: Array<IFieldTreeItem>;
	id?: IFDSListSection['id'];
	label: string;
	name: IFDSListSection['name'];
}

export default function List(props: IDataSetSectionProps) {
	const {dataSet, fieldTreeItems} = props;

	const [listSections, setListSections] = useState<Array<IListSection>>([
		{fieldTreeItems, label: Liferay.Language.get('title'), name: 'title'},
		{
			fieldTreeItems,
			label: Liferay.Language.get('description'),
			name: 'description',
		},
		{fieldTreeItems, label: Liferay.Language.get('image'), name: 'image'},
		{fieldTreeItems, label: Liferay.Language.get('symbol'), name: 'symbol'},
	]);
	const [saveButtonDisabled, setSaveButtonDisabled] = useState(false);

	const getFDSListSections = async () => {
		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS,
		});

		const response = await fetch(url, {
			headers: DEFAULT_FETCH_HEADERS,
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return null;
		}

		const responseJSON = await response.json();

		const fdsListSections = responseJSON?.items;

		if (!fdsListSections) {
			openDefaultFailureToast();

			return null;
		}

		setListSections(
			listSections.map((listSection) => {
				const fdsListSection = fdsListSections.find(
					(fdsListSection: IFDSListSection) =>
						fdsListSection.name === listSection.name
				);

				if (!fdsListSection) {
					return {
						fieldTreeItems,
						label: listSection.label,
						name: listSection.name,
					};
				}

				return {
					...listSection,
					externalReferenceCode: fdsListSection.externalReferenceCode,
					field: {
						name: fdsListSection.fieldName,
					},
				};
			})
		);
	};

	const clearFDSListSection = async ({
		closeModal,
		listSection,
	}: {
		closeModal?: Function;
		listSection: IListSection;
	}) => {
		if (!listSection.externalReferenceCode) {
			if (closeModal) {
				closeModal();
			}

			return;
		}

		setSaveButtonDisabled(true);

		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
			relatedResourceERC: String(listSection.externalReferenceCode),
			relationship: OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS,
		});

		const response = await fetch(url, {method: 'DELETE'});

		setSaveButtonDisabled(false);

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		if (closeModal) {
			closeModal();
		}

		setListSections(
			listSections.map((section) => {
				if (section.name !== listSection.name) {
					return section;
				}

				const nextListSection = {...listSection};

				delete nextListSection.externalReferenceCode;
				delete nextListSection.field;

				return nextListSection;
			})
		);

		openDefaultSuccessToast();
	};

	const saveFDSListSection = async ({
		closeModal,
		field,
		listSection,
	}: {
		closeModal: Function;
		field: IField;
		listSection: IListSection;
	}) => {
		setSaveButtonDisabled(true);

		let method;
		let url;

		if (listSection.externalReferenceCode) {
			method = 'PATCH';

			url = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				relatedResourceERC: listSection.externalReferenceCode,
				relationship: OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS,
			});
		}
		else {
			method = 'POST';

			url = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				relationship: OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS,
			});
		}

		const response = await fetch(url, {
			body: JSON.stringify({
				fieldName: field.name,
				name: listSection.name,
			}),
			headers: DEFAULT_FETCH_HEADERS,
			method,
		});

		setSaveButtonDisabled(false);

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const fdsListSection: IFDSListSection = await response.json();

		closeModal();

		setListSections(
			listSections.map((listSection) => {
				if (listSection.name !== fdsListSection.name) {
					return listSection;
				}

				return {
					...listSection,
					externalReferenceCode: fdsListSection.externalReferenceCode,
					field: {
						name: fdsListSection.fieldName,
					},
					id: fdsListSection.id,
				};
			})
		);

		openDefaultSuccessToast();
	};

	useEffect(() => {
		getFDSListSections();

		// eslint-disable-next-line react-compiler/react-compiler
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<ClayLayout.ContentCol className="c-gap-4 list-visualization-mode">
			<ClayAlert
				displayType="info"
				title={`${Liferay.Language.get('info')}:`}
				variant="stripe"
			>
				{Liferay.Language.get(
					'this-visualization-mode-will-not-be-shown-until-you-assign-at-least-one-field-to-a-list-element'
				)}
			</ClayAlert>

			<ClayTable className="mb-0">
				<ClayTable.Head>
					<ClayTable.Row>
						<ClayTable.Cell
							className="list-section-label"
							headingCell
						>
							{Liferay.Language.get('list-element')}
						</ClayTable.Cell>

						<ClayTable.Cell className="field-name" headingCell>
							{Liferay.Language.get('field')}
						</ClayTable.Cell>
					</ClayTable.Row>
				</ClayTable.Head>

				<ClayTable.Body>
					{listSections.map((listSection) => (
						<ListSection
							key={listSection.name}
							listSection={listSection}
							modalProps={props}
							onClearSelection={() => {
								clearFDSListSection({listSection});
							}}
							onSelect={({closeModal, selectedField}) => {
								selectedField
									? saveFDSListSection({
											closeModal,
											field: selectedField,
											listSection,
										})
									: clearFDSListSection({
											closeModal,
											listSection,
										});
							}}
							saveButtonDisabled={saveButtonDisabled}
						/>
					))}
				</ClayTable.Body>
			</ClayTable>
		</ClayLayout.ContentCol>
	);
}

interface IListSectionProps {
	listSection: IListSection;
	modalProps: IDataSetSectionProps;
	onClearSelection: () => void;
	onSelect: ({
		closeModal,
		selectedField,
	}: {
		closeModal: Function;
		selectedField: IField;
	}) => void;
	saveButtonDisabled: boolean;
}

function ListSection({
	listSection,
	modalProps,
	onClearSelection,
	onSelect,
	saveButtonDisabled,
}: IListSectionProps) {
	const {field, fieldTreeItems, label} = listSection;

	const openAddCustomFieldModal = () => {
		openModal({
			contentComponent: ({closeModal}: {closeModal: Function}) => (
				<AddCustomFieldModalContent
					{...modalProps}
					closeModal={closeModal}
					onSaveButtonClick={(selectedField: IField) => {
						onSelect({
							closeModal,
							selectedField,
						});
					}}
				/>
			),
		});
	};

	const openAddDataSourceFieldsModal = () => {
		openModal({
			className: 'modal-height-full',
			contentComponent: ({closeModal}: {closeModal: Function}) => (
				<FieldSelectModalContent
					{...modalProps}
					closeModal={closeModal}
					fieldTreeItems={fieldTreeItems}
					onSaveButtonClick={({
						selectedFields,
					}: {
						selectedFields: Array<IField>;
					}) => {
						onSelect({
							closeModal,
							selectedField: selectedFields[0],
						});
					}}
					saveButtonDisabled={saveButtonDisabled}
					selectedFields={field ? [field] : []}
				/>
			),
			size: 'lg',
		});
	};

	return (
		<ClayTable.Row>
			<ClayTable.Cell className="list-section-label">
				<strong>{label}</strong>
			</ClayTable.Cell>

			<ClayTable.Cell className="field-name">
				<ClayInput.Group small>
					<ClayInput.GroupItem>
						<p
							className={classNames(
								'align-items-center d-flex mb-0',
								{'text-secondary': !field}
							)}
						>
							{field
								? field.label || field.name
								: Liferay.Language.get('not-assigned')}
						</p>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem shrink>
						<FieldAssignmentControls
							field={field}
							label={label}
							onClearSelection={onClearSelection}
							openAddCustomFieldModal={openAddCustomFieldModal}
							openAddDataSourceFieldsModal={
								openAddDataSourceFieldsModal
							}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayTable.Cell>
		</ClayTable.Row>
	);
}
