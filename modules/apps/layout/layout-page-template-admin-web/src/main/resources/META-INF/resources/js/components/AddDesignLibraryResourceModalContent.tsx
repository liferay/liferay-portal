/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import {MODAL_TYPES} from '../constants/modalTypes';
import {MappingType} from '../types/MappingTypes';
import {ValidationError} from '../types/ValidationError';
import ContentTypeModalForm, {validateForm} from './ContentTypeModalForm';

interface Props {
	closeModal: () => void;
	formSubmitURL: string;
	mappingTypes: MappingType[];
	namespace: string;
	title: string;
}

export default function AddDesignLibraryResourceModalContent({
	closeModal,
	formSubmitURL,
	mappingTypes,
	namespace,
	title,
}: Props) {
	const [error, setError] = useState<ValidationError>({});
	const [loading, setLoading] = useState(false);

	const formRef = useRef<HTMLFormElement>(null);

	const handleSubmit = async (event: React.SyntheticEvent) => {
		event.preventDefault();

		const form = formRef.current!;

		const validationError = validateForm(form, namespace);

		if (Object.keys(validationError).length) {
			setError(validationError);

			return;
		}

		setLoading(true);

		try {
			const response = await fetch(formSubmitURL, {
				body: new FormData(form),
				method: 'POST',
			});

			const {error, redirectURL} = await response.json();

			if (error) {
				setError(typeof error === 'string' ? {name: error} : error);
				setLoading(false);

				return;
			}

			if (redirectURL) {
				navigate(redirectURL, {beforeScreenFlip: closeModal});
			}
		}
		catch {
			setLoading(false);

			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	return (
		<>
			<ClayModal.Header>{title}</ClayModal.Header>

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
							disabled={loading}
							displayType="primary"
							onClick={handleSubmit}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<ClayLoadingIndicator
										displayType="secondary"
										size="sm"
									/>
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
