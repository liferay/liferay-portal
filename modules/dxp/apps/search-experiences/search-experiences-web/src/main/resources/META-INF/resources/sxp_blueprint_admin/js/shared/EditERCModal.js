/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import getCN from 'classnames';
import {fetch} from 'frontend-js-web';
import React, {useContext, useRef, useState} from 'react';

import {DEFAULT_HEADERS} from '../utils/fetch/fetch_data';
import sub from '../utils/language/sub';
import ThemeContext from './ThemeContext';

const externalReferenceCodeRegex = new RegExp('^([\\w\\d-_]+)$');

export function ERCModal({
	initialExternalReferenceCode = '',
	observer,
	onClose,
	onSubmit,
}) {
	const {sxpType} = useContext(ThemeContext);

	const [externalReferenceCode, setExternalReferenceCode] = useState(
		initialExternalReferenceCode
	);
	const [error, setError] = useState('');

	const externalReferenceCodeInputRef = useRef();

	const _handleSubmit = (event) => {
		event.preventDefault();

		const validateUrl =
			sxpType === 'sxpElement'
				? '/o/search-experiences-rest/v1.0/sxp-elements/validate'
				: '/o/search-experiences-rest/v1.0/sxp-blueprints/validate';

		if (!externalReferenceCode.trim()) {
			setError(Liferay.Language.get('this-field-is-required'));

			externalReferenceCodeInputRef.current.focus();

			return;
		}

		if (!externalReferenceCodeRegex.test(externalReferenceCode.trim())) {
			setError(
				Liferay.Language.get(
					'please-enter-only-alphanumeric-characters-dashes-or-underscores'
				)
			);

			externalReferenceCodeInputRef.current.focus();

			return;
		}

		if (externalReferenceCode.trim() === initialExternalReferenceCode) {
			onClose();

			return;
		}

		// Verify that the external reference code is unique if it has changed.

		fetch(validateUrl, {
			body: JSON.stringify({
				externalReferenceCode,
			}),
			headers: DEFAULT_HEADERS,
			method: 'POST',
		})
			.then((response) => {
				return response.json().then((data) => ({
					ok: response.ok,
					responseContent: data,
				}));
			})
			.then(({ok, responseContent}) => {
				if (!ok) {
					if (
						responseContent.type ===
							'DuplicateSXPBlueprintExternalReferenceCodeException' ||
						responseContent.type ===
							'DuplicateSXPElementExternalReferenceCodeException'
					) {
						setError(
							sub(
								Liferay.Language.get('the-x-is-already-in-use'),
								[
									Liferay.Language.get(
										'external-reference-code'
									),
								]
							)
						);

						externalReferenceCodeInputRef.current.focus();

						return;
					}
					else {
						throw Error();
					}
				}

				onSubmit(externalReferenceCode.trim());
				onClose();
			})
			.catch(() => {
				setError(Liferay.Language.get('an-unexpected-error-occurred'));
			});
	};

	const _handleInputChange = (event) => {
		event.preventDefault();

		setExternalReferenceCode(event.target.value);
	};

	return (
		<ClayModal
			className="sxp-edit-external-reference-code-modal-root"
			observer={observer}
			size="md"
		>
			<ClayForm onSubmit={_handleSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{sub(Liferay.Language.get('edit-x'), [
						Liferay.Language.get('external-reference-code'),
					])}
				</ClayModal.Header>

				<ClayModal.Body>
					<ClayForm.Group className={getCN({'has-error': !!error})}>
						<label htmlFor="externalReferenceCode">
							{Liferay.Language.get('external-reference-code')}

							<span className="c-ml-1 reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<ClayInput
							id="externalReferenceCode"
							onChange={_handleInputChange}
							ref={externalReferenceCodeInputRef}
							type="text"
							value={externalReferenceCode}
						/>

						{error && (
							<div className="form-feedback-item">
								<span className="form-feedback-indicator inline-item-before">
									<ClayIcon symbol="exclamation-full" />
								</span>

								{error}
							</div>
						)}
					</ClayForm.Group>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton displayType="primary" type="submit">
								{Liferay.Language.get('done')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}

export default function EditERCModal({
	children,
	disabled = false,
	externalReferenceCode,
	onSubmit,
}) {
	const [visible, setVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => setVisible(false),
	});

	const _handleOpen = () => {
		setVisible(true);
	};

	return (
		<>
			{visible && (
				<ERCModal
					initialExternalReferenceCode={externalReferenceCode}
					observer={observer}
					onClose={onClose}
					onSubmit={onSubmit}
				/>
			)}

			{disabled ? (
				children
			) : (
				<ClayButton
					aria-label={Liferay.Language.get('edit')}
					className="shadow-none"
					displayType="unstyled"
					onClick={_handleOpen}
				>
					{children}
				</ClayButton>
			)}
		</>
	);
}
