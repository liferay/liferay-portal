/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelect, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import getCN from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import InputSets, {useInputSets} from '../shared/input_sets/index';

const ORDERS = {
	ASC: {
		label: Liferay.Language.get('ascending'),
		value: 'ascending',
	},
	DESC: {
		label: Liferay.Language.get('descending'),
		value: 'descending',
	},
};

const VIEWS = {
	CLASSIC: {
		label: Liferay.Language.get('classic'),
		value: 'classic',
	},
	NEW: {
		label: Liferay.Language.get('new'),
		value: 'new',
	},
};

/**
 * Adds the possible order symbol (+/-) at the end of the indexed field name.
 * @param {Array} fields Field array of objects with fieldName, label, and order.
 * @return {Array} Field array of objects with fieldName (has the order symbol)
 * and label.
 */
const addOrdersToFieldNames = (fields) =>
	fields.map(({field: fieldName, label, order}) => ({
		field: `${fieldName}${order === ORDERS.DESC.value ? '-' : '+'}`,
		label,
	}));

/**
 * Uses the indexed field name to determine if order is ascending (+) or
 * descending (-).
 * @param {string} fieldName The indexed field name with possible order symbol.
 * @return {string} Order value (ascending or descending).
 */
const getOrderFromFieldName = (fieldName) => {
	return fieldName.slice(-1) === '-' ? ORDERS.DESC.value : ORDERS.ASC.value;
};

/**
 * Gets the relevance field object.
 * @param {Array} fieldsJSONArray
 * @returns {object}
 */
const getRelevanceFieldObject = (fieldsJSONArray) => {
	let relevanceField = null;

	fieldsJSONArray.some((item) => {
		if (item.field === '') {
			relevanceField = item;

			return true;
		}

		return false;
	});

	return relevanceField;
};

/**
 * Cleans up the fields array by removing those that have empty indexed
 * field names.
 * @param {Array} fields The list of fields.
 * @return {Array} The cleaned up list of fields.
 */
const removeEmptyFields = (fields) =>
	fields.filter(({field: fieldName}) => fieldName);

/**
 * Removes the possible order symbol (+/-) at the end of the indexed field
 * name.
 * @param {string} fieldName The indexed field name with possible order symbol.
 * @return {string} The indexed field name without order symbol.
 */
const removeOrderFromFieldName = (fieldName) => {
	return fieldName.slice(-1) === '+' || fieldName.slice(-1) === '-'
		? fieldName.slice(0, -1)
		: fieldName;
};

/**
 * Transforms the preference key fields format into a format easier to work with
 * in the component. Adds `order` property.
 * @param {Array} fieldsJSONArray
 * @returns {Array}
 */
const transformFieldsJSONArrayToFieldsArray = (fieldsJSONArray) => {
	const fieldsJSONArrayWithRemovedRelevance = fieldsJSONArray.filter(
		({field}) => field !== ''
	);

	return fieldsJSONArrayWithRemovedRelevance.map(
		({field: fieldName, label}) => ({
			field: removeOrderFromFieldName(fieldName),
			label,
			order: getOrderFromFieldName(fieldName),
		})
	);
};

function Inputs({index, onInputSetItemChange, value}) {
	const _handleChangeValue = (property) => (event) => {
		onInputSetItemChange(index, {[property]: event.target.value});
	};

	return (
		<>
			<ClayInput.GroupItem>
				<label htmlFor={`indexedField${index}`}>
					{Liferay.Language.get('indexed-field')}

					<ClayTooltipProvider>
						<span
							className="c-ml-2"
							tabIndex={0}
							title={Liferay.Language.get('indexed-field-help')}
						>
							<ClayIcon symbol="question-circle-full" />
						</span>
					</ClayTooltipProvider>
				</label>

				<ClayInput
					id={`indexedField${index}`}
					onChange={_handleChangeValue('field')}
					type="text"
					value={value.field || ''}
				/>
			</ClayInput.GroupItem>

			<ClayInput.GroupItem>
				<label htmlFor={`displayLabel${index}`}>
					{Liferay.Language.get('display-label')}
				</label>

				<ClayInput
					id={`displayLabel${index}`}
					onChange={_handleChangeValue('label')}
					type="text"
					value={value.label || ''}
				/>
			</ClayInput.GroupItem>

			<ClayInput.GroupItem>
				<label htmlFor={`order${index}`}>
					{Liferay.Language.get('order[sort]')}
				</label>

				<ClaySelect
					aria-label={Liferay.Language.get('select-order')}
					id={`order${index}`}
					onChange={_handleChangeValue('order')}
					value={value.order}
				>
					{Object.keys(ORDERS).map((key) => (
						<ClaySelect.Option
							key={ORDERS[key].value}
							label={ORDERS[key].label}
							value={ORDERS[key].value}
						/>
					))}
				</ClaySelect>
			</ClayInput.GroupItem>
		</>
	);
}

function SortConfigurationOptions({
	fieldsInputName = '',
	fieldsJSONArray = [
		{field: '', label: 'relevance'},
		{field: 'modified-', label: 'modified'},
		{field: 'modified+', label: 'modified-oldest-first'},
		{field: 'created-', label: 'created'},
		{field: 'created+', label: 'created-oldest-first'},
		{field: 'userName', label: 'user'},
	],
	namespace = '',
}) {
	const relevanceField = getRelevanceFieldObject(fieldsJSONArray);

	const [classicFields, setClassicFields] = useState(fieldsJSONArray);

	const {
		getInputSetItemProps,
		onInputSetItemChange,
		onInputSetsAdd,
		onInputSetsChange,
		value: fields,
	} = useInputSets(transformFieldsJSONArrayToFieldsArray(fieldsJSONArray));

	const [relevanceLabel, setRelevanceLabel] = useState(
		relevanceField ? relevanceField.label : 'relevance'
	);
	const [relevanceOn, setRelevanceOn] = useState(!!relevanceField);
	const [showAlert, setShowAlert] = useState(true);
	const [view, setView] = useState(VIEWS.NEW);

	useEffect(() => {
		const formElement = document.getElementById(`${namespace}fm`);

		const handleFormSubmit = () => {

			// This directly manipulates the input element to avoid
			// re-rendering the component. This doesn't update the
			// `classicFields` state since it will re-render the component
			// and `submitForm` might be called before the input value is
			// updated since `setState` is asynchronous.
			//
			// This also checks for the element id `${namespace}fieldsId` to
			// determine if the classic view is being used instead of the state
			// `view`. Otherwise, switching between views will add and remove
			// the event listener and cause an unresolved issue where the form
			// is submitted twice after submitting the classic view more than
			// once.

			if (document.getElementById(`${namespace}fieldsId`)) {
				const fieldsInputElement = document.getElementsByName(
					`${namespace}${fieldsInputName}`
				)[0];

				fieldsInputElement.value = JSON.stringify(
					_getInputElementsFieldsArray().filter(
						({field, label}) => label || field
					)
				);
			}
		};

		formElement?.addEventListener('submit', handleFormSubmit);

		return () => {
			formElement?.removeEventListener('submit', handleFormSubmit);
		};
	}, [fieldsInputName, namespace]);

	useEffect(() => {
		if (view.value !== VIEWS.CLASSIC.value) {
			return;
		}

		AUI().use('liferay-auto-fields', () => {
			new Liferay.AutoFields({
				contentBox: `#${namespace}fieldsId`,
				namespace: `${namespace}`,
			}).render();
		});
	}, [namespace, view]);

	/**
	 * Since the classic view is controlled by the Liferay.AutoFields component,
	 * the `classicFields` state isn't updated when items are added or removed.
	 * This is needed to grab the current values when switching to the new view
	 * or submitting the form.
	 * @returns {Array}
	 */
	const _getInputElementsFieldsArray = () => {
		const newFields = [];

		const autoFieldFormRows = [
			...document.getElementsByClassName('field-form-row'),
		].filter((element) => {
			return !element.hidden;
		});

		autoFieldFormRows.forEach((item) => {
			const field =
				item.getElementsByClassName('sort-field-input')[0].value;
			const label = item.getElementsByClassName('label-input')[0].value;

			newFields.push({
				field,
				label,
			});
		});

		return newFields;
	};

	const _handleChangeClassicValue = (index, property) => (event) => {
		const newClassicFields = [...classicFields];

		newClassicFields[index][property] = event.target.value;

		setClassicFields(newClassicFields);
	};

	const _handleCloseAlert = () => {
		setShowAlert(false);
	};

	const _handleSwitchView = () => {
		if (view.value === VIEWS.NEW.value) {
			setClassicFields(
				relevanceOn
					? [
							{field: '', label: relevanceLabel},
							...addOrdersToFieldNames(removeEmptyFields(fields)),
						]
					: addOrdersToFieldNames(removeEmptyFields(fields))
			);

			setView(VIEWS.CLASSIC);
		}
		else {
			const newFields = _getInputElementsFieldsArray();

			onInputSetsChange(transformFieldsJSONArrayToFieldsArray(newFields));

			const classicRelevanceField = getRelevanceFieldObject(newFields);

			setRelevanceLabel(
				classicRelevanceField
					? classicRelevanceField.label
					: 'relevance'
			);
			setRelevanceOn(!!classicRelevanceField);

			setView(VIEWS.NEW);
		}
	};

	return (
		<div className="sort-configurations-options">
			<div className="c-mb-2 flex-row-reverse form-inline view-switcher">
				<ClayButton
					borderless
					displayType="secondary"
					onClick={_handleSwitchView}
					small
				>
					{sub(Liferay.Language.get('switch-to-x-view'), [
						view.value === VIEWS.NEW.value
							? VIEWS.CLASSIC.label
							: VIEWS.NEW.label,
					])}
				</ClayButton>
			</div>

			{view.value === VIEWS.NEW.value && (
				<>
					<InputSets>
						<ClayForm.Group className="c-mb-0 c-pl-5 c-pr-2 input-sets-item-form-group list-group-item rounded">
							<ClayInput.Group>
								<ClayInput.GroupItem>
									<label htmlFor="relevance">
										{Liferay.Language.get('display-label')}
									</label>

									<ClayInput
										id="relevance"
										onChange={(event) =>
											setRelevanceLabel(
												event.target.value
											)
										}
										type="text"
										value={relevanceLabel}
									/>

									<ClayForm.FeedbackGroup>
										<ClayForm.Text>
											{Liferay.Language.get(
												'relevance-can-be-turned-on-or-off-but-not-removed'
											)}
										</ClayForm.Text>
									</ClayForm.FeedbackGroup>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem
									className="c-pl-2 c-pr-2"
									shrink
								>
									<ClayToggle
										label={
											relevanceOn
												? Liferay.Language.get('on')
												: Liferay.Language.get('off')
										}
										onToggle={() =>
											setRelevanceOn(!relevanceOn)
										}
										toggled={relevanceOn}
									/>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayForm.Group>

						{fields.map((valueItem, valueIndex) => (

							// eslint-disable-next-line react/jsx-key
							<InputSets.Item
								{...getInputSetItemProps(valueItem, valueIndex)}
							>
								<Inputs
									index={valueIndex}
									onInputSetItemChange={onInputSetItemChange}
									value={valueItem}
								/>
							</InputSets.Item>
						))}

						<ClayButton
							aria-label={Liferay.Language.get('add-option')}
							className={getCN({
								'c-mt-4': !fields.length,
							})}
							displayType="secondary"
							onClick={onInputSetsAdd}
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="plus" />
							</span>

							{Liferay.Language.get('add-option')}
						</ClayButton>
					</InputSets>

					<input
						name={`${namespace}${fieldsInputName}`}
						type="hidden"
						value={JSON.stringify(
							relevanceOn
								? [
										{field: '', label: relevanceLabel},
										...addOrdersToFieldNames(
											removeEmptyFields(fields)
										),
									]
								: addOrdersToFieldNames(
										removeEmptyFields(fields)
									)
						)}
					/>
				</>
			)}

			{view.value === VIEWS.CLASSIC.value && showAlert && (
				<ClayAlert
					displayType="info"
					onClose={_handleCloseAlert}
					title={Liferay.Language.get('info')}
				>
					{Liferay.Language.get(
						'the-classic-view-will-be-removed-in-a-future-version'
					)}
				</ClayAlert>
			)}

			{view.value === VIEWS.CLASSIC.value && (
				<span id={`${namespace}fieldsId`}>
					{classicFields.map(({field, label}, index) => (
						<div
							className="field-form-row lfr-form-row lfr-form-row-inline"
							key={index}
						>
							<div className="row-fields">
								<div className="c-mb-2 form-group">
									<label htmlFor={`label_${index}`}>
										{Liferay.Language.get('label')}
									</label>

									<ClayInput
										className="label-input"
										id={`label_${index}`}
										onChange={_handleChangeClassicValue(
											index,
											'label'
										)}
										type="text"
										value={label}
									/>
								</div>

								<div className="c-mb-3 form-group">
									<label htmlFor={`field_${index}`}>
										{Liferay.Language.get('field')}
									</label>

									<ClayInput
										className="sort-field-input"
										id={`field_${index}`}
										onChange={_handleChangeClassicValue(
											index,
											'field'
										)}
										type="text"
										value={field}
									/>
								</div>
							</div>
						</div>
					))}

					<input
						name={`${namespace}${fieldsInputName}`}
						type="hidden"
						value={JSON.stringify(classicFields)}
					/>
				</span>
			)}
		</div>
	);
}

export default SortConfigurationOptions;
