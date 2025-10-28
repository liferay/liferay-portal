/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React, {useEffect, useRef, useState} from 'react';

import LoadingButton from '../../LoadingButton/LoadingButton.es';
import ValidatedInput from '../../ValidatedInput/ValidatedInput.es';

export default function VariantForm({
	errorMessage,
	name = '',
	onClose,
	onSave,
	title,
	variantId,
}) {
	const [inputName, setInputName] = useState(name);
	const [error, setError] = useState(false);
	const [invalidForm, setInvalidForm] = useState(false);
	const [loading, setLoading] = useState(false);
	const mountedRef = useRef();

	const onSubmit = (event) => {
		event.preventDefault();

		if (!invalidForm) {
			setLoading(true);

			onSave({name: inputName, variantId})
				.then(() => {
					if (mountedRef.current) {
						setLoading(false);
						onClose();
					}
				})
				.catch(() => {
					if (mountedRef.current) {
						setLoading(false);
						setError(true);
					}
				});
		}
	};

	useEffect(() => {
		mountedRef.current = true;

		return () => {
			mountedRef.current = false;
		};
	});

	return (
		<form onSubmit={onSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				{error && errorMessage && (
					<ClayAlert
						displayType="danger"
						title={Liferay.Language.get('error')}
					>
						{errorMessage}
					</ClayAlert>
				)}

				<ValidatedInput
					autofocus
					errorMessage={Liferay.Language.get(
						'variant-name-is-required'
					)}
					label={Liferay.Language.get('name')}
					onChange={(event) => setInputName(event.target.value)}
					onValidationChange={setInvalidForm}
					required
					value={inputName}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<LoadingButton
							disabled={loading || invalidForm}
							displayType="primary"
							loading={loading}
							onClick={onSubmit}
							type="submit"
						>
							{Liferay.Language.get('save')}
						</LoadingButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
