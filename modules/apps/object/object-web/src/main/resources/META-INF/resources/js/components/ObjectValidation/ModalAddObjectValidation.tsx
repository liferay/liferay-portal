/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {
	API,
	FormError,
	Input,
	SingleSelect,
	constantsUtils,
	invalidateRequired,
	useForm,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';

interface ModalAddObjectValidationProps {
	allowScriptContentToBeExecutedOrIncluded: boolean;
	apiURL: string;
	objectValidationRuleEngines: LabelValueObject[];
	setShowAddObjectRelationshipModal: (value: boolean) => void;
}

const initialValues: Partial<ObjectValidation> = {
	active: false,
	engine: 'ddm',
	name: {en_US: ''},
	script: 'script_placeholder',
};

export function ModalAddObjectValidation({
	allowScriptContentToBeExecutedOrIncluded,
	apiURL,
	objectValidationRuleEngines,
	setShowAddObjectRelationshipModal,
}: ModalAddObjectValidationProps) {
	const [error, setError] = useState<string>('');
	const {observer, onClose} = useModal({
		onClose: () => setShowAddObjectRelationshipModal(false),
	});

	const getObjectValidationRuleEngines = () => {
		let newObjectValidationRuleEngines = [...objectValidationRuleEngines];

		if (!allowScriptContentToBeExecutedOrIncluded) {
			newObjectValidationRuleEngines =
				newObjectValidationRuleEngines.filter(
					(objectValidationRuleEngine) =>
						objectValidationRuleEngine.value !== 'groovy'
				);
		}

		return newObjectValidationRuleEngines;
	};

	const onSubmit = async (objectValidation: Partial<ObjectValidation>) => {
		try {
			await API.save({
				item: {
					...objectValidation,
					errorLabel: {
						[defaultLanguageId]:
							objectValidation.engine === 'compositeKey'
								? Liferay.Language.get(
										'the-field-values-are-already-in-use'
									)
								: '',
					},
				} as Partial<ObjectValidation>,
				method: 'POST',
				url: apiURL,
			});

			onClose();

			window.location.reload();
		}
		catch (error) {
			setError((error as Error).message);
		}
	};

	const validate = (validation: Partial<ObjectValidation>) => {
		const errors: FormError<Partial<ObjectValidation>> = {};
		const label = validation.name?.[defaultLanguageId];

		if (invalidateRequired(label)) {
			errors.name = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const {errors, handleSubmit, setValues, values} = useForm<ObjectValidation>(
		{
			initialValues,
			onSubmit,
			validate,
		}
	);

	return (
		<ClayModalProvider>
			<ClayModal center observer={observer}>
				<ClayForm onSubmit={handleSubmit}>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('new-validation')}
					</ClayModal.Header>

					<ClayModal.Body>
						{error && (
							<ClayAlert displayType="danger">{error}</ClayAlert>
						)}

						<Input
							autoComplete="off"
							error={errors.name}
							label={Liferay.Language.get('label')}
							name="label"
							onChange={({target: {value}}) => {
								setValues({
									...values,
									name: {[defaultLanguageId]: value},
								});
							}}
							required
							value={
								(values as ObjectValidation).name[
									defaultLanguageId
								] ?? ''
							}
						/>

						<SingleSelect<LabelValueObject>
							error={errors.engine}
							id="objectValidationType"
							items={getObjectValidationRuleEngines()}
							label={Liferay.Language.get('type')}
							onSelectionChange={(value) => {
								setValues({
									...values,
									engine: value as string,
								});
							}}
							required
							selectedKey={values.engine}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onClose()}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton type="submit">
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayForm>
			</ClayModal>
		</ClayModalProvider>
	);
}
