/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import React, {useState} from 'react';

import RequiredMark from '../../../components/RequiredMark';
import ValidationFeedback from '../../../components/ValidationFeedback';
import {IField} from '../../../utils/types';

const AddCustomFieldModalContent = ({
	closeModal,
	namespace,
	onSaveButtonClick,
}: {
	closeModal: Function;
	namespace: string;
	onSaveButtonClick: (selectedField: IField) => void;
}) => {
	const [fieldName, setFieldName] = useState<string>();
	const [
		requiredFieldNameValidationError,
		setRequiredFieldNameValidationError,
	] = useState<boolean>(false);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-field-manually')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group
					className={classNames({
						'has-error': requiredFieldNameValidationError,
					})}
				>
					<label htmlFor={`${namespace}FieldNameInput`}>
						{Liferay.Language.get('field-name')}

						<RequiredMark />

						<span
							className="label-icon lfr-portal-tooltip ml-2"
							title={Liferay.Language.get(
								'you-can-add-a-field-that-is-in-the-API-response-but-not-declared-in-the-schema'
							)}
						>
							<ClayIcon symbol="question-circle-full" />
						</span>
					</label>

					<ClayInput
						id={`${namespace}FieldNameInput`}
						onChange={(event) => {
							setRequiredFieldNameValidationError(false);
							setFieldName(event.target.value);
						}}
						placeholder={Liferay.Language.get('type-field-here')}
						type="text"
					/>

					{requiredFieldNameValidationError && (
						<ValidationFeedback
							message={Liferay.Language.get(
								'alert-you-must-enter-a-field-name'
							)}
						/>
					)}
				</ClayForm.Group>
			</ClayModal.Body>
			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => closeModal()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							onClick={() => {
								if (!fieldName) {
									setRequiredFieldNameValidationError(true);
								}
								else {
									onSaveButtonClick({name: fieldName});
								}
							}}
						>
							{Liferay.Language.get('add')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

export default AddCustomFieldModalContent;
