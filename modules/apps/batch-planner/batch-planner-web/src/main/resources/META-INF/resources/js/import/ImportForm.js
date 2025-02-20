/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTable from '@clayui/table';
import {openToast} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import SaveTemplate from '../SaveTemplate';
import {
	CSV_FORMAT,
	DISALLOWED_CSV_ENTITY_TYPES,
	FILE_EXTENSION_EVENT,
	FILE_SCHEMA_EVENT,
	SCHEMA_SELECTED_EVENT,
	TEMPLATE_SELECTED_EVENT,
	TEMPLATE_SOILED_EVENT,
} from '../constants';
import getFieldsFromSchema from '../getFieldsFromSchema';
import {getAvailableMappings} from '../utilities/mappings';
import ImportMappingItem from './ImportMappingItem';
import ImportSubmit from './ImportSubmit';

const TableFieldsHeader = () => (
	<ClayTable.Head>
		<ClayTable.Row>
			<ClayTable.Cell headingCell headingTitle>
				{Liferay.Language.get('destination-field')}
			</ClayTable.Cell>

			<ClayTable.Cell headingCell headingTitle>
				{Liferay.Language.get('source-file-field')}
			</ClayTable.Cell>

			<ClayTable.Cell headingCell headingTitle>
				{Liferay.Language.get('preview')}
			</ClayTable.Cell>
		</ClayTable.Row>
	</ClayTable.Head>
);

const anyOfGroupNotFilled = (fieldsSelections, relationshipGroups) => {
	return Object.keys(relationshipGroups).some(
		(group) =>
			!relationshipGroups[group].some((value) =>
				Object.keys(fieldsSelections).some((field) => field === value)
			)
	);
};

const isAnyOfValid = (dbField, fieldsSelections, relationshipGroups) => {
	return Object.keys(fieldsSelections).some(
		(selectedField) =>
			relationshipGroups[dbField.anyOfGroup] &&
			relationshipGroups[dbField.anyOfGroup].includes(selectedField)
	);
};

function ImportForm({
	formDataQuerySelector,
	formImportURL,
	formSaveAsTemplateURL,
	mappedFields,
	portletNamespace,
}) {
	const [dbFields, setDbFields] = useState({
		optional: [],
		required: [],
	});
	const [formEvaluated, setFormEvaluated] = useState(false);
	const [fileFields, setFileFields] = useState();
	const [fileContent, setFileContent] = useState();
	const [fieldsSelections, setFieldsSelections] = useState({});
	const [mappingsToBeEvaluated, setMappingsToBeEvaluated] =
		useState(mappedFields);
	const relationshipGroups = useMemo(() => {
		const groups = {};

		dbFields.required.forEach((dbField) => {
			const {anyOfGroup, name} = dbField;

			if (anyOfGroup) {
				if (!groups[anyOfGroup]) {
					groups[anyOfGroup] = [];
				}

				groups[anyOfGroup].push(name);
			}
		});

		return groups;
	}, [dbFields]);
	const useTemplateMappingRef = useRef();

	const formIsValid = useMemo(() => {
		if (
			!Object.keys(fieldsSelections).length ||
			!(dbFields.optional.length + dbFields.required.length)
		) {
			return false;
		}

		const requiredFieldNotFilled = dbFields.required.some(
			(dbField) => !fieldsSelections[dbField.name] && !dbField.anyOfGroup
		);

		return (
			!requiredFieldNotFilled &&
			!anyOfGroupNotFilled(fieldsSelections, relationshipGroups)
		);
	}, [dbFields, fieldsSelections, relationshipGroups]);

	const updateFieldMapping = (fileField, dbFieldName) => {
		setFieldsSelections((prevSelections) => {
			if (fileField) {
				return {
					...prevSelections,
					[dbFieldName]: fileField,
				};
			}

			const updatedDbFields = {...prevSelections};

			delete updatedDbFields[dbFieldName];

			return updatedDbFields;
		});

		Liferay.fire(TEMPLATE_SOILED_EVENT);
	};

	useEffect(() => {
		const dbFieldsUnordered = [...dbFields.optional, ...dbFields.required];

		if (
			dbFields.optional.length + dbFields.required.length &&
			fileFields &&
			!useTemplateMappingRef.current
		) {
			const availableMappings = getAvailableMappings(
				mappingsToBeEvaluated,
				fileFields,
				dbFieldsUnordered
			);

			setFieldsSelections(availableMappings);
		}
	}, [dbFields, fileFields, mappingsToBeEvaluated]);

	useEffect(() => {
		function handleSchemaUpdated({schema}) {
			const newDBFields = getFieldsFromSchema(schema);

			setDbFields(newDBFields);

			toggleDownloadTemplateAlert(schema);
		}

		function handleFileExtensionUpdate({entityType, fileExtension}) {
			if (
				fileExtension === CSV_FORMAT &&
				DISALLOWED_CSV_ENTITY_TYPES.includes(entityType)
			) {
				setDbFields({
					optional: [],
					required: [],
				});

				const downloadTemplateAlert = document.getElementById(
					`${portletNamespace}downloadTemplateAlert`
				);

				if (downloadTemplateAlert) {
					downloadTemplateAlert.classList.add('hide');
				}
			}
		}

		function handleFileSchemaUpdate({fileContent, schema}) {
			setFileContent(fileContent);

			setFileFields(getFirstLevelFieldNames(schema));
		}

		function handleTemplateSelect({template}) {
			if (template) {
				setMappingsToBeEvaluated(template.mappings);
			}
		}

		function getFirstLevelFieldNames(schema) {
			return [
				...new Set(
					schema.map((fileField) => {
						return fileField.split('.')[0];
					})
				),
			];
		}

		function toggleDownloadTemplateAlert(schema) {
			const downloadTemplateAlert = document.getElementById(
				`${portletNamespace}downloadTemplateAlert`
			);

			if (schema) {
				downloadTemplateAlert.classList.remove('hide');
			}
			else {
				downloadTemplateAlert.classList.add('hide');
			}
		}

		Liferay.on(FILE_EXTENSION_EVENT, handleFileExtensionUpdate);
		Liferay.on(FILE_SCHEMA_EVENT, handleFileSchemaUpdate);
		Liferay.on(SCHEMA_SELECTED_EVENT, handleSchemaUpdated);
		Liferay.on(TEMPLATE_SELECTED_EVENT, handleTemplateSelect);

		return () => {
			Liferay.detach(FILE_EXTENSION_EVENT, handleFileExtensionUpdate);
			Liferay.detach(FILE_SCHEMA_EVENT, handleFileSchemaUpdate);
			Liferay.detach(SCHEMA_SELECTED_EVENT, handleSchemaUpdated);
			Liferay.detach(TEMPLATE_SELECTED_EVENT, handleTemplateSelect);
		};
	}, [portletNamespace]);

	const formIsVisible = !!(
		dbFields.optional.length + dbFields.required.length
	);

	const handleEvaluateForm = useCallback(() => {
		setFormEvaluated(true);

		if (!formIsVisible) {
			openToast({
				message: Liferay.Language.get(
					'please-upload-a-file-and-select-the-required-columns-before-continuing'
				),
				type: 'danger',
			});

			return;
		}

		if (!fileFields) {
			openToast({
				message: Liferay.Language.get('please-upload-a-file'),
				type: 'danger',
			});

			return;
		}

		if (!formIsValid) {
			openToast({
				message: Liferay.Language.get(
					'you-must-map-at-least-one-field-and-all-required-fields-before-continuing'
				),
				title: Liferay.Language.get('error'),
				type: 'danger',
			});
		}
	}, [formIsValid, formIsVisible, fileFields]);

	return (
		<>
			{formIsVisible && (
				<div className="card import-mapping-table">
					<div className="card-header h4">
						{Liferay.Language.get('import-mappings')}
					</div>

					<div className="card-body p-0">
						<ClayTable borderless hover={false}>
							<TableFieldsHeader />

							<ClayTable.Body>
								{!!dbFields.required.length && (
									<>
										<ClayTable.Row divider>
											<ClayTable.Cell
												className="text-uppercase"
												colSpan={3}
											>
												{Liferay.Language.get(
													'required-fields'
												)}
											</ClayTable.Cell>
										</ClayTable.Row>

										{dbFields.required.map((dbField) => (
											<ImportMappingItem
												dbField={dbField}
												fileFields={fileFields}
												formEvaluated={formEvaluated}
												isAnyOfValid={isAnyOfValid(
													dbField,
													fieldsSelections,
													relationshipGroups
												)}
												key={dbField.name}
												portletNamespace={
													portletNamespace
												}
												previewValue={
													fieldsSelections[
														dbField.name
													] &&
													fileContent?.length &&
													fileContent[0][
														fieldsSelections[
															dbField.name
														]
													]
												}
												required={true}
												selectedFileField={
													fieldsSelections[
														dbField.name
													] ||
													('' &&
														!anyOfGroupNotFilled(
															fieldsSelections,
															relationshipGroups
														))
												}
												updateFieldMapping={(
													selectedFileField
												) =>
													updateFieldMapping(
														selectedFileField,
														dbField.name
													)
												}
											/>
										))}
									</>
								)}

								{!!dbFields.optional.length && (
									<>
										<ClayTable.Row divider>
											<ClayTable.Cell
												className="text-uppercase"
												colSpan={3}
											>
												{Liferay.Language.get(
													'optional-fields'
												)}
											</ClayTable.Cell>
										</ClayTable.Row>

										{dbFields.optional.map((dbField) => (
											<ImportMappingItem
												dbField={dbField}
												fileFields={fileFields}
												formEvaluated={formEvaluated}
												key={dbField.name}
												portletNamespace={
													portletNamespace
												}
												previewValue={
													fieldsSelections[
														dbField.name
													] &&
													fileContent?.length &&
													fileContent[0][
														fieldsSelections[
															dbField.name
														]
													]
												}
												required={false}
												selectedFileField={
													fieldsSelections[
														dbField.name
													] || ''
												}
												updateFieldMapping={(
													selectedFileField
												) =>
													updateFieldMapping(
														selectedFileField,
														dbField.name
													)
												}
											/>
										))}
									</>
								)}
							</ClayTable.Body>
						</ClayTable>
					</div>
				</div>
			)}

			<div className="mt-4 sheet-footer">
				<SaveTemplate
					evaluateForm={handleEvaluateForm}
					formIsValid={formIsValid}
					formIsVisible={formIsVisible}
					formSaveAsTemplateDataQuerySelector={formDataQuerySelector}
					formSaveAsTemplateURL={formSaveAsTemplateURL}
					portletNamespace={portletNamespace}
					type="import"
				/>

				<ImportSubmit
					evaluateForm={handleEvaluateForm}
					fieldsSelections={fieldsSelections}
					fileContent={fileContent}
					formDataQuerySelector={formDataQuerySelector}
					formImportURL={formImportURL}
					formIsValid={formIsValid}
					formIsVisible={formIsVisible}
				/>
			</div>
		</>
	);
}

ImportForm.defaultProps = {
	mappedFields: {},
};

ImportForm.propTypes = {
	formDataQuerySelector: PropTypes.string.isRequired,
	formImportURL: PropTypes.string.isRequired,
	formSaveAsTemplateURL: PropTypes.string.isRequired,
	portletNamespace: PropTypes.string.isRequired,
};

export default ImportForm;
