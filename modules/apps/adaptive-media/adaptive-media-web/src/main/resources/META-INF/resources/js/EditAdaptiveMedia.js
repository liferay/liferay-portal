/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {
	ClayCheckbox,
	ClayRadio,
	ClayRadioGroup,
	ClayToggle,
} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {useFormik} from 'formik';
import {FeatureIndicator, openToast} from 'frontend-js-components-web';
import {
	fetch,
	navigate,
	normalizeFriendlyURL,
	objectToFormData,
} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

import {HelpMessage, Input, RequiredMark} from './form/Components';
import {alphanumeric, required, validate} from './form/validations';

const getRandomUuid = () => Math.floor(Math.random() * 10000);
const scrollToTop = () => window.scrollTo({behavior: 'smooth', top: 0});

const VALID_INPUT_KEYS = new Set([
	'0',
	'1',
	'2',
	'3',
	'4',
	'5',
	'6',
	'7',
	'8',
	'9',
	'ArrowUp',
	'ArrowRight',
	'ArrowDown',
	'ArrowLeft',
	'Backspace',
	'Up',
	'Right',
	'Down',
	'Left',
	'Enter',
	'Tab',
]);

const EditAdaptiveMedia = ({
	actionUrl,
	amImageConfigurationEntry,
	automaticUuid,
	configurationEntryEditable,
	configurationEntryUuid,
	namespace,
	redirect,
}) => {
	const [automaticId, setAutomaticId] = useState(automaticUuid);
	const [errorMessage, setErrorMessage] = useState(null);

	const adaptationURLParametersId = `${namespace}adaptationURLParameters`;
	const automaticRadioId = `${namespace}automaticUuid`;
	const delegateAdaptationId = `${namespace}delegateAdaptation`;
	const descriptionId = `${namespace}description`;
	const highResolutionId = `${namespace}addHighResolution`;
	const maxHeightId = `${namespace}maxHeight`;
	const maxWidthId = `${namespace}maxWidth`;
	const nameId = `${namespace}name`;
	const newUuidId = `${namespace}newUuid`;

	let adaptationURLParameters = '';
	let delegateAdaptation = false;
	let maxWidth = '';
	let maxHeight = '';

	if (amImageConfigurationEntry) {
		const properties = amImageConfigurationEntry.properties;

		const currentMaxWidth = properties['max-width'];
		const currentMaxHeight = properties['max-height'];

		if (Number(currentMaxWidth) !== 0) {
			maxWidth = currentMaxWidth;
		}
		if (Number(currentMaxHeight) !== 0) {
			maxHeight = currentMaxHeight;
		}

		adaptationURLParameters = properties['adaptation-url-parameters'] || '';
		delegateAdaptation = properties['delegate-adaptation'] === 'true';
	}

	const formik = useFormik({
		initialValues: {
			[adaptationURLParametersId]: adaptationURLParameters,
			[delegateAdaptationId]: delegateAdaptation,
			[descriptionId]: amImageConfigurationEntry
				? amImageConfigurationEntry.description
				: '',
			[highResolutionId]: false,
			[maxHeightId]: maxHeight,
			[maxWidthId]: maxWidth,
			[nameId]: amImageConfigurationEntry
				? amImageConfigurationEntry.name
				: '',
			[newUuidId]: configurationEntryUuid,
			[`${namespace}uuid`]: configurationEntryUuid,
		},
		onSubmit: (values) => {
			fetch(actionUrl, {
				body: objectToFormData(values),
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({message, success}) => {
					if (success) {
						openToast({
							message,
							title: Liferay.Language.get('success'),
							type: 'success',
						});

						navigate(redirect);
					}
					else {
						setErrorMessage(message);
						scrollToTop();
					}
				})
				.catch(() => {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						title: Liferay.Language.get('error'),
						type: 'danger',
					});
				});
		},
		validate: (values) => {
			const errorsList = validate(
				{
					[nameId]: [required],
					[newUuidId]: [alphanumeric],
				},
				values
			);

			if (values[maxWidthId] === 0 && values[maxHeightId] === 0) {
				errorsList[maxWidthId] = Liferay.Language.get(
					'please-enter-a-max-width-or-max-height-value-larger-than-0'
				);
			}
			else if (!values[maxWidthId] && !values[maxHeightId]) {
				errorsList[maxWidthId] = Liferay.Language.get(
					'at-least-one-value-is-required'
				);
			}

			return errorsList;
		},
	});

	const {errors, handleBlur, handleChange, setFieldValue, touched, values} =
		formik;

	const onCancel = useCallback(() => {
		if (redirect) {
			navigate(redirect);
		}
	}, [redirect]);

	const handleChangeUuid = (event) => {
		const nameValue = event.target.value;

		if (automaticId) {
			setFieldValue(
				newUuidId,
				normalizeFriendlyURL(nameValue) || getRandomUuid(),
				false
			);
		}

		setFieldValue(nameId, nameValue);
	};

	const handleKeydownNumbersOnly = (event) => {
		if (!VALID_INPUT_KEYS.has(event.key)) {
			event.preventDefault();
		}
	};

	return (
		<ClayForm onSubmit={formik.handleSubmit}>
			{errorMessage && (
				<ClayAlert displayType="danger">{errorMessage}</ClayAlert>
			)}

			{!configurationEntryEditable && (
				<ClayAlert>
					{Liferay.Language.get(
						'the-images-for-this-resolution-are-already-adapted'
					)}
				</ClayAlert>
			)}

			<Input
				error={touched[nameId] && errors[nameId]}
				label={Liferay.Language.get('name')}
				name={nameId}
				onBlur={handleBlur}
				onChange={handleChangeUuid}
				required
				value={values[nameId]}
			/>

			<Input
				label={Liferay.Language.get('description')}
				name={descriptionId}
				onBlur={handleBlur}
				onChange={handleChange}
				type="textarea"
				value={values[descriptionId]}
			/>

			<div className="sheet-section">
				<h3 className="sheet-subtitle">
					{Liferay.Language.get('size')}

					<RequiredMark />
				</h3>

				<label className="control-label form-group">
					{Liferay.Language.get(
						'please-enter-at-least-one-of-the-following-fields'
					)}

					<HelpMessage
						message={Liferay.Language.get(
							'leave-a-size-field-empty-to-get-images-scaled-proportionally'
						)}
					/>
				</label>

				<ClayLayout.Row>
					<ClayLayout.Col md="3">
						<Input
							disabled={!configurationEntryEditable}
							error={
								touched[maxWidthId] &&
								touched[maxHeightId] &&
								errors[maxWidthId]
							}
							label={Liferay.Language.get('max-width-px')}
							min="0"
							name={maxWidthId}
							onBlur={handleBlur}
							onChange={handleChange}
							onKeyDown={handleKeydownNumbersOnly}
							type="number"
							value={values[maxWidthId]}
						/>
					</ClayLayout.Col>

					<ClayLayout.Col md="3">
						<Input
							disabled={!configurationEntryEditable}
							error={
								touched[maxWidthId] &&
								touched[maxHeightId] &&
								Boolean(errors[maxWidthId])
							}
							label={Liferay.Language.get('max-height-px')}
							min="0"
							name={maxHeightId}
							onBlur={handleBlur}
							onChange={handleChange}
							onKeyDown={handleKeydownNumbersOnly}
							type="number"
							value={values[maxHeightId]}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>

				{!amImageConfigurationEntry && (
					<ClayCheckbox
						checked={values[highResolutionId]}
						id={highResolutionId}
						label={Liferay.Language.get(
							'add-a-resolution-for-high-density-displays'
						)}
						name={highResolutionId}
						onChange={(event) => {
							setFieldValue(
								highResolutionId,
								event.target.checked
							);
						}}
					/>
				)}
			</div>

			{Liferay.FeatureFlags['LPD-94784'] && (
				<div className="sheet-section">
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('adaptation')}
					</h3>

					<ClayForm.Group>
						<ClayToggle
							id={delegateAdaptationId}
							label={Liferay.Language.get('delegate-adaptation')}
							name={delegateAdaptationId}
							onToggle={(value) => {
								setFieldValue(delegateAdaptationId, value);
							}}
							toggled={values[delegateAdaptationId]}
						/>

						<FeatureIndicator type="beta" />

						<HelpMessage
							message={Liferay.Language.get(
								'delegate-adaptation-help'
							)}
						/>
					</ClayForm.Group>

					<Input
						disabled={!values[delegateAdaptationId]}
						helpMessage={Liferay.Language.get(
							'adaptation-url-parameters-help'
						)}
						label={Liferay.Language.get(
							'adaptation-url-parameters'
						)}
						name={adaptationURLParametersId}
						onBlur={handleBlur}
						onChange={handleChange}
						value={values[adaptationURLParametersId]}
					/>
				</div>
			)}

			<div className="sheet-section">
				<h3 className="sheet-subtitle">
					{Liferay.Language.get('identifier')}
				</h3>

				<ClayRadioGroup
					name={automaticRadioId}
					onChange={setAutomaticId}
					value={automaticId}
				>
					<ClayRadio
						disabled={!configurationEntryEditable}
						label={Liferay.Language.get('automatic')}
						value={true}
					>
						<HelpMessage
							message={Liferay.Language.get(
								'the-id-is-based-on-the-name-field'
							)}
						/>
					</ClayRadio>

					<ClayRadio
						disabled={!configurationEntryEditable}
						label={Liferay.Language.get('custom')}
						value={false}
					/>
				</ClayRadioGroup>

				<Input
					disabled={automaticId || !configurationEntryEditable}
					error={
						!automaticId && touched[newUuidId] && errors[newUuidId]
					}
					label={Liferay.Language.get('id')}
					name={newUuidId}
					onBlur={handleBlur}
					onChange={handleChange}
					value={values[newUuidId]}
				/>
			</div>

			<div className="sheet-footer">
				<ClayButton.Group spaced>
					<ClayButton type="submit">
						{Liferay.Language.get('save')}
					</ClayButton>

					<ClayButton
						displayType="secondary"
						onClick={onCancel}
						type="cancel"
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>
				</ClayButton.Group>
			</div>
		</ClayForm>
	);
};

EditAdaptiveMedia.propTypes = {
	actionUrl: PropTypes.string.isRequired,
	amImageConfigurationEntry: PropTypes.shape({
		description: PropTypes.string,
		name: PropTypes.string,
		properties: PropTypes.shape({
			['adaptation-url-parameters']: PropTypes.string,
			['delegate-adaptation']: PropTypes.string,
			['max-height']: PropTypes.string,
			['max-width']: PropTypes.string,
		}),
	}),
	automaticUuid: PropTypes.bool,
	configurationEntryEditable: PropTypes.bool.isRequired,
	configurationEntryUuid: PropTypes.string,
	namespace: PropTypes.string.isRequired,
	redirect: PropTypes.string.isRequired,
};

export default EditAdaptiveMedia;
