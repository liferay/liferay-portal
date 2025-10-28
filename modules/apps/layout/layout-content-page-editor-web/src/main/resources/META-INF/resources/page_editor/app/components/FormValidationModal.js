/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {default as ClayButton} from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import ClayPanel from '@clayui/panel';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React from 'react';

import {config} from '../config/index';
import {useFormValidations} from '../contexts/FormValidationContext';
import {getFormErrorDescription} from '../utils/getFormErrorDescription';

export function FormValidationModal({onCloseModal, onPublish}) {
	const {observer, onClose} = useModal({
		onClose: onCloseModal,
	});

	const formValidations = useFormValidations();

	if (
		formValidations.length === 1 &&
		formValidations[0].errors.length === 1
	) {
		const [formValidation] = formValidations;

		return (
			<SingleErrorModal
				formValidation={formValidation}
				observer={observer}
				onClose={onClose}
				onPublish={onPublish}
			/>
		);
	}

	return (
		<ClayModal
			center
			className="page-editor__form-validation-modal"
			observer={observer}
			status="warning"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('form-errors')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="mb-4">
					{Liferay.Language.get(
						'the-following-errors-have-been-found-with-the-forms-on-the-page'
					)}
				</p>

				{formValidations.map((formValidation, index) => {
					const typeLabel = config.formTypes.find(
						({value}) => value === formValidation.classNameId
					).label;

					return (
						<ClayPanel
							className="mb-0"
							collapsable
							defaultExpanded
							displayTitle={sub(
								Liferay.Language.get('x-form'),
								typeLabel
							)}
							displayType="link"
							key={index}
							showCollapseIcon
						>
							<ClayPanel.Body>
								{formValidation.errors.map((error) => {
									const {summary} = getFormErrorDescription({
										name: typeLabel,
										steps: error.steps,
										type: error.type,
									});

									return (
										<ClayAlert
											displayType="warning"
											key={error}
											variant="feedback"
										>
											{summary}
										</ClayAlert>
									);
								})}
							</ClayPanel.Body>
						</ClayPanel>
					);
				})}
			</ClayModal.Body>

			<ModalFooter onClose={onClose} onPublish={onPublish} />
		</ClayModal>
	);
}

FormValidationModal.propTypes = {
	onCloseModal: PropTypes.func.isRequired,
	onPublish: PropTypes.func.isRequired,
};

function SingleErrorModal({formValidation, observer, onClose, onPublish}) {
	const [error] = formValidation.errors;

	const typeLabel = config.formTypes.find(
		({value}) => value === formValidation.classNameId
	).label;

	const {message, title} = getFormErrorDescription({
		name: typeLabel,
		steps: error.steps,
		type: error.type,
	});

	return (
		<ClayModal
			center
			className="page-editor__form-validation-modal"
			observer={observer}
			status="warning"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="mb-0">{message}</p>
			</ClayModal.Body>

			<ModalFooter onClose={onClose} onPublish={onPublish} />
		</ClayModal>
	);
}

SingleErrorModal.propTypes = {
	formValidation: PropTypes.object.isRequired,
	observer: PropTypes.object.isRequired,
	onClose: PropTypes.func.isRequired,
	onPublish: PropTypes.func.isRequired,
};

function ModalFooter({onClose, onPublish}) {
	return (
		<ClayModal.Footer
			last={
				<ClayButton.Group spaced>
					<ClayButton displayType="secondary" onClick={onClose}>
						{Liferay.Language.get('cancel')}
					</ClayButton>

					<ClayButton displayType="warning" onClick={onPublish}>
						{Liferay.Language.get('publish')}
					</ClayButton>
				</ClayButton.Group>
			}
		/>
	);
}

ModalFooter.propTypes = {
	onClose: PropTypes.func.isRequired,
	onPublish: PropTypes.func.isRequired,
};
