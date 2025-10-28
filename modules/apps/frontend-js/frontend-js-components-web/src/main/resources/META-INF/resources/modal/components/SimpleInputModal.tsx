/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {FormEventHandler, useState} from 'react';

export interface SimpleInputModalAlert {
	message: string;
	style: DisplayType;
	title: string;
}

export interface SimpleInputModalCloseModal {
	(): void;
}

export type SimpleInputModalMethod = 'GET' | 'POST';

export interface SimpleInputModalOnFormSuccess {
	({redirectURL}: {redirectURL: string}): void;
}

export type SimpleInputModalSize = 'full-screen' | 'lg' | 'sm';

export interface SimpleInputModalProps {
	alert: SimpleInputModalAlert;
	buttonSubmitLabel: string;
	center: boolean;
	checkboxFieldLabel: string;
	checkboxFieldName: string;
	checkboxFieldValue: boolean;
	closeModal: SimpleInputModalCloseModal;
	dialogTitle: string;
	formSubmitURL: string;
	idFieldName: string;
	idFieldValue: never;
	initialVisible: boolean;
	mainFieldComponent: never;
	mainFieldLabel: string;
	mainFieldName: string;
	mainFieldPlaceholder: string;
	mainFieldValue: string;
	method: SimpleInputModalMethod;
	namespace: string;
	onFormSuccess: SimpleInputModalOnFormSuccess;
	required: boolean;
	size: SimpleInputModalSize;
}

/**
 * Manipulates small amounts of data with a form shown inside a modal.
 */
export default function SimpleInputModal({
	alert,
	buttonSubmitLabel = Liferay.Language.get('save'),
	center,
	checkboxFieldLabel,
	checkboxFieldName,
	checkboxFieldValue,
	closeModal,
	dialogTitle,
	formSubmitURL,
	idFieldName,
	idFieldValue,
	initialVisible,
	mainFieldComponent,
	mainFieldLabel,
	mainFieldName,
	mainFieldPlaceholder,
	mainFieldValue = '',
	method = 'POST',
	namespace,
	onFormSuccess,
	required = true,

	// @ts-ignore

	size = 'md',
}: SimpleInputModalProps) {
	const isMounted = useIsMounted();
	const [errorMessage, setErrorMessage] = useState('');
	const [highlighted, setHighlighted] = useState(false);
	const [loadingResponse, setLoadingResponse] = useState(false);
	const [visible, setVisible] = useState(initialVisible);
	const [inputValue, setInputValue] = useState(mainFieldValue);
	const [isChecked, setChecked] = useState(checkboxFieldValue);

	const handleFormError = (responseContent: {error: string}) => {
		setErrorMessage(responseContent.error || '');
	};

	const handleMainFieldRef = (mainFieldElement: HTMLInputElement) => {
		if (mainFieldElement && mainFieldValue && !highlighted) {
			mainFieldElement.setSelectionRange(0, mainFieldValue.length);
			setHighlighted(true);
		}
	};

	const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
		event.preventDefault();

		const error = inputValue
			? ''
			: Liferay.Language.get('this-field-is-required');

		if (error && required) {
			setErrorMessage(error);

			return;
		}

		setLoadingResponse(true);

		const form = (document.querySelector(`#${namespace}form`) ||
			undefined) as HTMLFormElement | undefined;

		const formData = new FormData(form);

		fetch(formSubmitURL, {
			body: formData,
			method,
		})
			.then((response) => response.json())
			.then((responseContent) => {
				if (isMounted()) {
					if (responseContent.error) {
						setLoadingResponse(false);

						handleFormError(responseContent);
					}
					else {
						setVisible(false);

						closeModal();

						if (responseContent.redirectURL) {
							navigate(responseContent.redirectURL);
						}
						else {
							if (onFormSuccess) {
								onFormSuccess({
									...responseContent,
									redirectURL:
										responseContent.redirectURL || '',
								});
							}
						}
					}
				}
			})
			.catch((response) => {
				handleFormError(response);
			});
	};

	const {observer, onClose} = useModal({
		onClose: () => {
			setVisible(false);

			closeModal();
		},
	});

	return visible ? (
		<ClayModal center={center} observer={observer} size={size}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{dialogTitle}
			</ClayModal.Header>

			<ClayForm
				id={`${namespace}form`}

				// @ts-ignore

				noValidate
				onSubmit={handleSubmit}
			>
				<ClayModal.Body>
					{alert && alert.message && alert.title && (
						<ClayAlert
							displayType={alert.style}
							title={alert.title}
						>
							{alert.message}
						</ClayAlert>
					)}

					<input
						name={`${namespace}${idFieldName}`}
						type="hidden"
						value={idFieldValue}
					/>

					<div
						className={`form-group ${
							errorMessage ? 'has-error' : ''
						}`}
					>
						<label
							className="control-label"
							htmlFor={`${namespace}${mainFieldName}`}
						>
							{mainFieldLabel}

							{required ? (
								<span className="reference-mark">
									<ClayIcon symbol="asterisk" />
								</span>
							) : null}
						</label>

						<ClayInput
							autoFocus
							className="form-control"
							component={mainFieldComponent}
							disabled={loadingResponse}
							id={`${namespace}${mainFieldName}`}
							name={`${namespace}${mainFieldName}`}
							onChange={(event) => {
								if (required) {
									setErrorMessage(
										event.target.value
											? ''
											: Liferay.Language.get(
													'this-field-is-required'
												)
									);
								}

								setInputValue(event.target.value);
							}}
							placeholder={mainFieldPlaceholder}
							ref={handleMainFieldRef}
							required={required}
							type="text"
							value={inputValue}
						/>

						{errorMessage && (
							<div className="form-feedback-item" role="alert">
								<ClayIcon
									className="inline-item inline-item-before"
									symbol="exclamation-full"
								/>

								{errorMessage}
							</div>
						)}
					</div>

					{checkboxFieldName && checkboxFieldLabel && (
						<div className="form-check">
							<ClayCheckbox
								checked={isChecked}
								disabled={loadingResponse}
								label={checkboxFieldLabel}
								name={`${namespace}${checkboxFieldName}`}
								onChange={() =>
									setChecked((isChecked) => !isChecked)
								}
							/>
						</div>
					)}
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								disabled={loadingResponse}
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={loadingResponse}
								displayType="primary"
								type="submit"
							>
								{loadingResponse && (
									<span className="inline-item inline-item-before">
										<span
											aria-hidden="true"
											className="loading-animation"
										></span>
									</span>
								)}

								{buttonSubmitLabel}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	) : (
		<></>
	);
}
