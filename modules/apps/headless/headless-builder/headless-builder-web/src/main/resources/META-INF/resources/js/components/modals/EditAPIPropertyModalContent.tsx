/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import React, {Dispatch, SetStateAction, useEffect, useState} from 'react';

import {Select} from '../fieldComponents/Select';

type DataError = {
	dataType: boolean;
	mappedProperty: boolean;
	name: boolean;
};

interface PropertyUIData {
	dataType: ObjectFieldBusinessType;
	description: string;
	mappedProperty: string;
	name: string;
}

interface EditAPIPropertyModalContentProps extends Partial<TreeViewItemData> {
	closeModal: voidReturn;
	setSchemaUIData: Dispatch<SetStateAction<APISchemaUIData>>;
}

export default function EditAPIPropertyModalContent({
	businessType,
	closeModal,
	description,
	name,
	objectFieldId,
	objectFieldName,
	setSchemaUIData,
}: EditAPIPropertyModalContentProps) {
	const [localUIData, setLocalUIData] = useState<Partial<PropertyUIData>>({
		dataType: businessType,
		description,
		mappedProperty: objectFieldName,
		name,
	});

	const [displayError, setDisplayError] = useState<DataError>({
		dataType: false,
		mappedProperty: false,
		name: false,
	});

	useEffect(() => {
		for (const key in localUIData) {
			if (localUIData[key as keyof PropertyUIData] !== '') {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}
	}, [localUIData]);

	function validateData() {
		let isDataValid = true;
		const mandatoryFields = ['dataType', 'mappedProperty', 'name'];

		if (!Object.keys(localUIData).length) {
			const errors = mandatoryFields.reduce(
				(errors, field) => ({...errors, [field]: true}),
				{}
			);
			setDisplayError(errors as DataError);

			isDataValid = false;
		}
		else {
			mandatoryFields.forEach((field) => {
				if (localUIData[field as keyof PropertyUIData]) {
					setDisplayError((previousErrors) => ({
						...previousErrors,
						[field]: false,
					}));
				}
				else {
					setDisplayError((previousErrors) => ({
						...previousErrors,
						[field]: true,
					}));
					isDataValid = false;
				}
			});
		}

		return isDataValid;
	}

	const handleUpdate = () => {
		const isDataValid = validateData();

		if (isDataValid) {
			setSchemaUIData((previous) =>
				previous.schemaProperties
					? {
							...previous,
							schemaProperties: previous.schemaProperties.map(
								(property) =>
									property.objectFieldId !== objectFieldId
										? property
										: {
												...property,
												...localUIData,
											}
							),
						}
					: previous
			);
			closeModal();
			openToast({
				message: Liferay.Language.get('schema-property-was-updated'),
				type: 'success',
			});
		}
		else {
			return;
		}
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('edit-property')}
			</ClayModal.Header>

			<div className="modal-body">
				<ClayForm>
					<ClayForm.Group
						className={classNames({
							'has-error': displayError.name,
						})}
					>
						<label htmlFor="PropertyName">
							{Liferay.Language.get('name')}

							<span className="ml-1 reference-mark text-warning">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<ClayInput
							aria-invalid={displayError.name}
							aria-required="true"
							autoComplete="off"
							id="PropertyName"
							onChange={({target: {value}}) =>
								setLocalUIData((previous) => ({
									...previous,
									name: value,
								}))
							}
							onKeyPress={(event) =>
								event.key === 'Enter' && event.preventDefault()
							}
							placeholder={Liferay.Language.get('enter-name')}
							value={localUIData.name}
						/>

						<div className="feedback-container">
							<ClayForm.FeedbackGroup>
								{displayError.name && (
									<ClayForm.FeedbackItem className="mt-2">
										<ClayForm.FeedbackIndicator symbol="exclamation-full" />

										<span id="inputNameErrorMessage">
											{Liferay.Language.get(
												'please-enter-a-property-name'
											)}
										</span>
									</ClayForm.FeedbackItem>
								)}
							</ClayForm.FeedbackGroup>
						</div>
					</ClayForm.Group>

					<ClayForm.Group>
						<label
							htmlFor="properyDescriptionField"
							id="properyDescriptionField"
						>
							{Liferay.Language.get('description')}
						</label>

						<textarea
							aria-labelledby="properyDescriptionField"
							autoComplete="off"
							className="form-control"
							id="properyDescriptionField"
							onChange={({target: {value}}) =>
								setLocalUIData((previous) => ({
									...previous,
									description: value,
								}))
							}
							placeholder={Liferay.Language.get(
								'add-a-description-to-this-property'
							)}
							value={localUIData.description}
						/>
					</ClayForm.Group>

					<ClayForm.Group
						className={classNames({
							'has-error': displayError.dataType,
						})}
					>
						<label htmlFor="selectTrigger">
							{Liferay.Language.get('data-type')}

							<span className="ml-1 reference-mark text-warning">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<Select
							disabled
							onClick={() => {}}
							options={[
								{label: businessType!, value: businessType!},
							]}
							placeholder={Liferay.Language.get(
								'select-a-data-type'
							)}
							required
							selectedOption={{
								label: localUIData.dataType!,
								value: localUIData.dataType!,
							}}
						/>

						<div className="feedback-container">
							<ClayForm.FeedbackGroup>
								{displayError.dataType && (
									<ClayForm.FeedbackItem className="mt-2">
										<ClayForm.FeedbackIndicator symbol="exclamation-full" />

										<span id="selectObjectErrorMessage">
											{Liferay.Language.get(
												'please-select-a-data-type'
											)}
										</span>
									</ClayForm.FeedbackItem>
								)}
							</ClayForm.FeedbackGroup>
						</div>
					</ClayForm.Group>

					<ClayForm.Group
						className={classNames({
							'has-error': displayError.mappedProperty,
						})}
					>
						<label htmlFor="selectTrigger">
							{Liferay.Language.get('mapped-property')}

							<span className="ml-1 reference-mark text-warning">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<Select
							disabled
							onClick={() => {}}
							options={[
								{
									label: objectFieldName!,
									value: objectFieldName!,
								},
							]}
							placeholder={Liferay.Language.get(
								'select-a-mapped-property'
							)}
							required
							selectedOption={{
								label: localUIData.mappedProperty!,
								value: localUIData.mappedProperty!,
							}}
						/>

						<div className="feedback-container">
							<ClayForm.FeedbackGroup>
								{displayError.mappedProperty && (
									<ClayForm.FeedbackItem className="mt-2">
										<ClayForm.FeedbackIndicator symbol="exclamation-full" />

										<span id="selectObjectErrorMessage">
											{Liferay.Language.get(
												'please-select-a-property'
											)}
										</span>
									</ClayForm.FeedbackItem>
								)}
							</ClayForm.FeedbackGroup>
						</div>
					</ClayForm.Group>

					<div aria-live="assertive" className="sr-only">
						{(displayError.name || displayError.dataType) && (
							<span>
								{Liferay.Language.get(
									'there-are-errors-on-the-form-please-check-if-any-mandatory-fields-have-not-been-completed'
								)}
							</span>
						)}
					</div>
				</ClayForm>
			</div>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							id="modalCancelButton"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							id="modalCreateButton"
							onClick={handleUpdate}
							type="button"
						>
							{Liferay.Language.get('update')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
