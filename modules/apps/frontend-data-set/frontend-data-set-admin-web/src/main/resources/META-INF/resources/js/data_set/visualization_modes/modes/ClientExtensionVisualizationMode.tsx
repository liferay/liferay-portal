/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayInput} from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayTable from '@clayui/table';
import classNames from 'classnames';
import {openModal} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import AddDataSourceFieldsModalContent from '../../../components/AddDataSourceFieldsModalContent';
import {DEFAULT_FETCH_HEADERS} from '../../../utils/constants';
import getDataSetResourceURL from '../../../utils/getDataSetResourceURL';
import {
	ISection,
	getClientExtensionSections,
	getSectionFields,
	getStaleSections,
	hasMissingRequiredSections,
	serializeSectionFields,
} from '../../../utils/getVisualizationModeSections';
import openDefaultFailureToast from '../../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../../utils/openDefaultSuccessToast';
import {IField, IFieldTreeItem} from '../../../utils/types';
import {IDataSetSectionProps} from '../../DataSet';
import AddCustomFieldModalContent from '../components/AddCustomFieldModalContent';
import FieldAssignmentControls from '../components/FieldAssignmentControls';

interface IClientExtensionVisualizationModeProps extends IDataSetSectionProps {
	visualizationModeName: string;
}

function flattenFields(items: IFieldTreeItem[]): IField[] {
	return items.flatMap((item) => [
		item,
		...flattenFields(item.children ?? []),
	]);
}

export default function ClientExtensionVisualizationMode(
	props: IClientExtensionVisualizationModeProps
) {
	const {
		dataSet,
		fieldTreeItems,
		onDataSetUpdate,
		visualizationModeClientExtensions,
		visualizationModeName,
	} = props;

	const [saving, setSaving] = useState(false);
	const savingRef = useRef(false);

	const sections = getClientExtensionSections(
		visualizationModeClientExtensions,
		visualizationModeName
	);
	const sectionFields = getSectionFields(dataSet, visualizationModeName);

	if (!sections.length) {
		return (
			<ClayAlert
				displayType="info"
				title={`${Liferay.Language.get('info')}:`}
				variant="stripe"
			>
				{Liferay.Language.get(
					'this-visualization-mode-is-provided-by-a-client-extension-and-receives-all-the-fields-of-each-item'
				)}
			</ClayAlert>
		);
	}

	const fields = flattenFields(fieldTreeItems);
	const staleSectionNames = getStaleSections(sectionFields, sections);

	const save = async (sectionName: string, field?: IField) => {
		if (savingRef.current) {
			return false;
		}

		const updatedSectionFields = {...sectionFields};

		if (field) {
			updatedSectionFields[sectionName] = field.name;
		}
		else {
			delete updatedSectionFields[sectionName];
		}

		savingRef.current = true;

		setSaving(true);

		let responseJSON = null;

		try {
			const response = await fetch(
				getDataSetResourceURL({
					dataSetERC: dataSet.externalReferenceCode,
				}),
				{
					body: JSON.stringify({
						visualizationModes: serializeSectionFields({
							dataSet,
							sectionFields: updatedSectionFields,
							visualizationModeName,
						}),
					}),
					headers: DEFAULT_FETCH_HEADERS,
					method: 'PATCH',
				}
			);

			responseJSON = response.ok ? await response.json() : null;
		}
		catch (error) {
			responseJSON = null;
		}

		savingRef.current = false;

		setSaving(false);

		if (!responseJSON?.id) {
			openDefaultFailureToast();

			return false;
		}

		openDefaultSuccessToast();

		onDataSetUpdate(responseJSON);

		return true;
	};

	return (
		<ClayLayout.ContentCol className="c-gap-4">
			{!!staleSectionNames.length && (
				<ClayAlert
					displayType="warning"
					title={`${Liferay.Language.get('warning')}:`}
					variant="stripe"
				>
					{sub(
						Liferay.Language.get(
							'the-client-extension-no-longer-declares-these-item-elements-x'
						),
						staleSectionNames.join(', ')
					)}
				</ClayAlert>
			)}

			{hasMissingRequiredSections(sectionFields, sections) && (
				<ClayAlert
					displayType="warning"
					title={`${Liferay.Language.get('warning')}:`}
					variant="stripe"
				>
					{Liferay.Language.get(
						'this-visualization-mode-will-not-be-shown-until-you-assign-a-field-to-each-required-item-element'
					)}
				</ClayAlert>
			)}

			<ClayTable className="mb-0">
				<ClayTable.Head>
					<ClayTable.Row>
						<ClayTable.Cell headingCell>
							{Liferay.Language.get('item-element')}
						</ClayTable.Cell>

						<ClayTable.Cell headingCell>
							{Liferay.Language.get('field')}
						</ClayTable.Cell>
					</ClayTable.Row>
				</ClayTable.Head>

				<ClayTable.Body>
					{sections.map((section) => (
						<SectionRow
							field={
								sectionFields[section.name]
									? fields.find(
											({name}) =>
												name ===
												sectionFields[section.name]
										) ?? {name: sectionFields[section.name]}
									: undefined
							}
							fieldTreeItems={fieldTreeItems}
							key={section.name}
							modalProps={props}
							onSave={async (field, closeModal) => {
								if (await save(section.name, field)) {
									closeModal?.();
								}
							}}
							saving={saving}
							section={section}
						/>
					))}
				</ClayTable.Body>
			</ClayTable>
		</ClayLayout.ContentCol>
	);
}

interface ISectionRowProps {
	field?: IField;
	fieldTreeItems: IFieldTreeItem[];
	modalProps: IDataSetSectionProps;
	onSave: (field?: IField, closeModal?: Function) => void;
	saving: boolean;
	section: ISection;
}

function SectionRow({
	field,
	fieldTreeItems,
	modalProps,
	onSave,
	saving,
	section: {label, required},
}: ISectionRowProps) {
	const openAddCustomFieldModal = () => {
		openModal({
			contentComponent: ({closeModal}: {closeModal: Function}) => (
				<AddCustomFieldModalContent
					{...modalProps}
					closeModal={closeModal}
					onSaveButtonClick={(selectedField: IField) =>
						onSave(selectedField, closeModal)
					}
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
					}) => onSave(selectedFields[0], closeModal)}
					saveButtonDisabled={saving}
					selectedFields={field ? [field] : []}
				/>
			),
			size: 'lg',
		});
	};

	return (
		<ClayTable.Row>
			<ClayTable.Cell>
				<strong>{label}</strong>

				{required && (
					<ClayLabel className="ml-2" displayType="info">
						{Liferay.Language.get('required')}
					</ClayLabel>
				)}
			</ClayTable.Cell>

			<ClayTable.Cell>
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
							onClearSelection={() => onSave()}
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
