/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {FormField, validateForm} from './components/ContentTypeModalForm';
import {ValidationError} from './types/ValidationError';

interface Props {
	addMasterLayoutURL: string;
	closeModal: () => void;
	namespace: string;
}

export default function AddMasterPageDesignLibraryModalContent({
	addMasterLayoutURL,
	closeModal,
	namespace,
}: Props) {
	const [error, setError] = useState<ValidationError>({});
	const [loading, setLoading] = useState(false);

	const formRef = useRef<HTMLFormElement>(null);
	const nameInputRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		if (nameInputRef.current) {
			nameInputRef.current.focus();
		}
	}, []);

	const handleSubmit = useCallback(
		(event: any) => {
			event.preventDefault();

			const form = formRef.current;

			const error = validateForm(form, namespace);

			if (Object.keys(error).length !== 0) {
				setError(error);

				return;
			}

			setLoading(true);

			fetch(addMasterLayoutURL, {
				body: new FormData(form!),
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({error, redirectURL}) => {
					if (error) {
						setError({name: error});
						setLoading(false);
					}
					else if (redirectURL) {
						navigate(redirectURL, {beforeScreenFlip: closeModal});
					}
				})
				.catch(() => {
					setLoading(false);

					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				});
		},
		[addMasterLayoutURL, closeModal, namespace]
	);

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('new-master-page')}
			</ClayModal.Header>

			<ClayModal.Body>
				<form onSubmit={handleSubmit} ref={formRef}>
					<FormField
						error={error.name}
						id={`${namespace}name`}
						name={Liferay.Language.get('name')}
					>
						<input
							className="form-control"
							disabled={loading}
							id={`${namespace}name`}
							name={`${namespace}name`}
							onChange={() => setError({})}
							ref={nameInputRef}
						/>
					</FormField>
				</form>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading}
							displayType="primary"
							onClick={handleSubmit}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
