/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

const FieldFeedback = ({message}) => (
	<ClayForm.FeedbackGroup>
		<ClayForm.FeedbackItem>
			<ClayForm.FeedbackIndicator symbol="exclamation-full" />

			{message}
		</ClayForm.FeedbackItem>
	</ClayForm.FeedbackGroup>
);

const ParameterModal = ({onClose, onSave, parameter, parameterNames}) => {
	const [name, setName] = useState(parameter?.name ?? '');
	const [submitted, setSubmitted] = useState(false);
	const [value, setValue] = useState(parameter?.value ?? '');

	const {observer} = useModal({onClose});

	const trimmedName = name.trim();

	const duplicated =
		trimmedName !== parameter?.name && parameterNames.includes(trimmedName);

	let nameError = null;

	if (submitted && !trimmedName) {
		nameError = Liferay.Language.get('this-field-is-required');
	}
	else if (submitted && duplicated) {
		nameError = Liferay.Language.get('this-parameter-is-already-set');
	}

	const valueError =
		submitted && !value.trim()
			? Liferay.Language.get('this-field-is-required')
			: null;

	const save = () => {
		setSubmitted(true);

		if (!trimmedName || duplicated || !value.trim()) {
			return;
		}

		onSave({name: trimmedName, value: value.trim()});
	};

	return (
		<ClayModal center observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get(
					parameter ? 'edit-parameter' : 'add-parameter'
				)}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group className={nameError ? 'has-error' : ''}>
					<label htmlFor="parameterName">
						{Liferay.Language.get('parameter')}

						<span className="ml-1 text-warning">*</span>
					</label>

					<ClayInput
						id="parameterName"
						onChange={({target}) => setName(target.value)}
						type="text"
						value={name}
					/>

					{nameError && <FieldFeedback message={nameError} />}
				</ClayForm.Group>

				<ClayForm.Group className={valueError ? 'has-error' : ''}>
					<label htmlFor="parameterValue">
						{Liferay.Language.get('value')}

						<span className="ml-1 text-warning">*</span>
					</label>

					<ClayInput
						id="parameterValue"
						onChange={({target}) => setValue(target.value)}
						type="text"
						value={value}
					/>

					{valueError && <FieldFeedback message={valueError} />}
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={save}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

ParameterModal.propTypes = {
	onClose: PropTypes.func.isRequired,
	onSave: PropTypes.func.isRequired,
	parameter: PropTypes.shape({
		name: PropTypes.string,
		value: PropTypes.string,
	}),
	parameterNames: PropTypes.arrayOf(PropTypes.string).isRequired,
};

export default ParameterModal;
