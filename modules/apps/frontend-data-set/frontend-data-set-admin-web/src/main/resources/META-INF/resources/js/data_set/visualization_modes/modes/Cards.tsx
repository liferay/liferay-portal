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

import '../../../../css/CardsVisualizationMode.scss';
import AddDataSourceFieldsModalContent from '../../../components/AddDataSourceFieldsModalContent';
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

interface IFDSCardsSection {
	externalReferenceCode: string;
	fieldName: string;
	id: string;
	name: string;
	rendererName?: string;
}
interface ICardsSection {
	externalReferenceCode?: IFDSCardsSection['externalReferenceCode'];
	field?: IField;
	fieldTreeItems: Array<IFieldTreeItem>;
	id?: IFDSCardsSection['id'];
	label: string;
	name: IFDSCardsSection['name'];
}

export default function Cards(props: IDataSetSectionProps) {
	const {dataSet, fieldTreeItems} = props;

	const [cardsSections, setCardsSections] = useState<Array<ICardsSection>>([
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

	const getFDSCardsSections = async () => {
		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS,
		});

		const response = await fetch(url, {headers: DEFAULT_FETCH_HEADERS});

		if (!response.ok) {
			openDefaultFailureToast();

			return null;
		}

		const responseJSON = await response.json();

		const fdsCardsSections = responseJSON?.items;

		if (!fdsCardsSections) {
			openDefaultFailureToast();

			return null;
		}

		setCardsSections(
			cardsSections.map((cardsSection) => {
				const fdsCardsSection = fdsCardsSections.find(
					(fdsCardsSection: IFDSCardsSection) =>
						fdsCardsSection.name === cardsSection.name
				);

				if (!fdsCardsSection) {
					return {
						fieldTreeItems,
						label: cardsSection.label,
						name: cardsSection.name,
					};
				}

				return {
					...cardsSection,
					externalReferenceCode:
						fdsCardsSection.externalReferenceCode,
					field: {
						name: fdsCardsSection.fieldName,
					},
					id: fdsCardsSection.id,
				};
			})
		);
	};

	const clearFDSCardSection = async ({
		cardsSection,
		closeModal,
	}: {
		cardsSection: ICardsSection;
		closeModal?: Function;
	}) => {
		if (!cardsSection.externalReferenceCode) {
			if (closeModal) {
				closeModal();
			}

			return;
		}

		setSaveButtonDisabled(true);

		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
			relatedResourceERC: String(cardsSection.externalReferenceCode),
			relationship: OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS,
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

		setCardsSections(
			cardsSections.map((section) => {
				if (section.name !== cardsSection.name) {
					return section;
				}

				const nextCardSection = {...cardsSection};

				delete nextCardSection.externalReferenceCode;
				delete nextCardSection.field;

				return nextCardSection;
			})
		);

		openDefaultSuccessToast();
	};

	const saveFDSCardsSection = async ({
		cardsSection,
		closeModal,
		field,
	}: {
		cardsSection: ICardsSection;
		closeModal: Function;
		field: IField;
	}) => {
		setSaveButtonDisabled(true);

		let method;
		let url;

		if (cardsSection.externalReferenceCode) {
			method = 'PATCH';

			url = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				relatedResourceERC: cardsSection.externalReferenceCode,
				relationship: OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS,
			});
		}
		else {
			method = 'POST';

			url = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				relationship: OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS,
			});
		}

		const response = await fetch(url, {
			body: JSON.stringify({
				fieldName: field.name,
				name: cardsSection.name,
			}),
			headers: DEFAULT_FETCH_HEADERS,
			method,
		});

		setSaveButtonDisabled(false);

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const fdsCardSection: IFDSCardsSection = await response.json();

		closeModal();

		setCardsSections(
			cardsSections.map((cardSection) => {
				if (cardSection.name !== fdsCardSection.name) {
					return cardSection;
				}

				return {
					...cardSection,
					externalReferenceCode: fdsCardSection.externalReferenceCode,
					field: {
						name: fdsCardSection.fieldName,
					},
				};
			})
		);

		openDefaultSuccessToast();
	};

	useEffect(() => {
		getFDSCardsSections();

		// eslint-disable-next-line react-compiler/react-compiler
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<ClayLayout.ContentCol className="c-gap-4 cards-visualization-mode">
			<ClayAlert
				displayType="info"
				title={`${Liferay.Language.get('info')}:`}
				variant="stripe"
			>
				{Liferay.Language.get(
					'this-visualization-mode-will-not-be-shown-until-you-assign-at-least-one-field-to-a-card-element'
				)}
			</ClayAlert>

			<ClayTable className="mb-0">
				<ClayTable.Head>
					<ClayTable.Row>
						<ClayTable.Cell
							className="cards-section-label"
							headingCell
						>
							{Liferay.Language.get('card-element')}
						</ClayTable.Cell>

						<ClayTable.Cell className="field-name" headingCell>
							{Liferay.Language.get('field')}
						</ClayTable.Cell>
					</ClayTable.Row>
				</ClayTable.Head>

				<ClayTable.Body>
					{cardsSections.map((cardsSection) => (
						<CardsSection
							cardsSection={cardsSection}
							key={cardsSection.name}
							modalProps={props}
							onClearSelection={() => {
								clearFDSCardSection({cardsSection});
							}}
							onSelect={({closeModal, selectedField}) => {
								selectedField
									? saveFDSCardsSection({
											cardsSection,
											closeModal,
											field: selectedField,
										})
									: clearFDSCardSection({
											cardsSection,
											closeModal,
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

interface ICardsSectionProps {
	cardsSection: ICardsSection;
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

function CardsSection({
	cardsSection,
	modalProps,
	onClearSelection,
	onSelect,
	saveButtonDisabled,
}: ICardsSectionProps) {
	const {field, fieldTreeItems, label} = cardsSection;

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
				<AddDataSourceFieldsModalContent
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
			<ClayTable.Cell className="cards-section-label">
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
