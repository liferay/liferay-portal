/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useCallback, useRef, useState} from 'react';

import ContentTypeModalForm, {
	validateForm,
} from './components/ContentTypeModalForm';
import {MODAL_TYPES} from './constants/modalTypes';
import {MappingType} from './types/MappingTypes';
import {ValidationError} from './types/ValidationError';

interface Props {
	closeModal: () => void;
	formSubmitURL: string;
	mappingTypes: MappingType[];
	namespace: string;
}

export default function AddDisplayPageTemplateDesignLibraryModalContent({
	closeModal,
	formSubmitURL,
	mappingTypes,
	namespace,
}: Props) {
	const [error, setError] = useState<ValidationError>({});
	const [loading, setLoading] = useState(false);

	const formRef = useRef<HTMLFormElement>(null);

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

			fetch(formSubmitURL, {
				body: new FormData(form!),
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({error, redirectURL}) => {
					if (error) {
						setError(error);
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
		[closeModal, formSubmitURL, namespace]
	);

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('new-display-page-template')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ContentTypeModalForm
					displayPageName=""
					error={error}
					formRef={formRef}
					mappingTypes={mappingTypes}
					namespace={namespace}
					onSubmit={handleSubmit}
					type={MODAL_TYPES.create}
				/>
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
