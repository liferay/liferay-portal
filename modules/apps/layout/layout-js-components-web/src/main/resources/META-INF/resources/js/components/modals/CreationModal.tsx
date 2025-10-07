/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

interface Props {
	buttonLabel: string;
	descriptionInputValue: string;
	formSubmitURL: string;
	heading: string;
	nameInputValue: string;
	onCloseModal: () => void;
	portletNamespace: string;
}

function CreationModal({
	buttonLabel = Liferay.Language.get('save'),
	descriptionInputValue: initialDescriptionInputValue,
	formSubmitURL,
	heading,
	nameInputValue: initialNameInputValue,
	onCloseModal,
	portletNamespace,
}: Props) {
	const [descriptionInputValue, setDescriptionInputValue] = useState<string>(
		initialDescriptionInputValue || ''
	);
	const [error, setError] = useState<string>('');
	const isMounted = useIsMounted();
	const [loading, setLoading] = useState<boolean>(false);
	const [nameInputError, setNameInputError] = useState<string>('');
	const [nameInputValue, setNameInputValue] = useState<string>(
		initialNameInputValue || ''
	);

	const {observer, onClose} = useModal({
		onClose: () => {
			onCloseModal();
		},
	});

	const formRef = useRef();
	const handleSubmit = (event: any) => {
		event.preventDefault();

		if (!nameInputValue) {
			setError(
				sub(
					Liferay.Language.get('the-x-field-is-empty'),
					Liferay.Language.get('name')
				)
			);
			setNameInputError(Liferay.Language.get('this-field-is-required'));

			return;
		}

		if (!formRef.current) {
			return;
		}

		const formData = new FormData(formRef.current);

		fetch(formSubmitURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then((responseContent) => {
				if (isMounted()) {
					if (responseContent.error) {
						setLoading(false);

						setNameInputError(responseContent.error);
					}
					else {
						onClose();

						if (responseContent.redirectURL) {
							navigate(responseContent.redirectURL);
						}
					}
				}
			})
			.catch((error) => {
				setNameInputError(error.message);
			});
	};

	return (
		<ClayModal className="m-0" observer={observer}>
			<ClayModal.Header>{heading}</ClayModal.Header>

			<ClayModal.Body className="m-0">
				<ClayForm
					id={`${portletNamespace}form`}

					// @ts-ignore

					noValidate
					onSubmit={handleSubmit}

					// @ts-ignore

					ref={formRef}
				>
					{error && (
						<ClayAlert displayType="danger" title="Error:">
							{error}
						</ClayAlert>
					)}

					<ClayForm.Group
						className={nameInputError ? 'has-error' : ''}
					>
						<label
							className="control-label"
							htmlFor={`${portletNamespace}name`}
						>
							{Liferay.Language.get('name')}

							<span className="reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<ClayInput
							disabled={loading}
							id={`${portletNamespace}name`}
							name={`${portletNamespace}name`}
							onChange={(event) => {
								if (event.target.value) {
									setError('');
									setNameInputError('');
								}
								else {
									setNameInputError(
										Liferay.Language.get(
											'this-field-is-required'
										)
									);
								}

								setNameInputValue(event.target.value);
							}}
							required
							value={nameInputValue}
						/>

						{nameInputError && (
							<ClayAlert displayType="danger" variant="feedback">
								{nameInputError}
							</ClayAlert>
						)}
					</ClayForm.Group>

					<label
						className="c-mt-4 control-label"
						htmlFor={`${portletNamespace}description`}
					>
						{Liferay.Language.get('description')}
					</label>

					<ClayInput
						component="textarea"
						disabled={loading}
						id={`${portletNamespace}description`}
						name={`${portletNamespace}description`}
						onChange={(event) =>
							setDescriptionInputValue(event.target.value)
						}
						value={descriptionInputValue}
					/>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={loading}
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading}
							displayType="primary"
							form={`${portletNamespace}form`}
							type="submit"
						>
							{loading && (
								<ClayLoadingIndicator
									displayType="secondary"
									size="sm"
								/>
							)}

							{buttonLabel}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

export default CreationModal;
