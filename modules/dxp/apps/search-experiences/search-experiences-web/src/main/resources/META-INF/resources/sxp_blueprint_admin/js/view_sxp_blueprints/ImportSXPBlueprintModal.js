/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {DEFAULT_HEADERS} from '../utils/fetch/fetch_data';

const VALID_EXTENSIONS = '.json';

const ImportSXPBlueprintModal = ({portletNamespace, redirectURL}) => {
	const [errorMessage, setErrorMessage] = useState();
	const [loadingResponse, setLoadingResponse] = useState(false);
	const [importFile, setImportFile] = useState();
	const [visible, setVisible] = useState(false);

	// Define componentId upon mount to prevent browser console error
	// about `Component with id is being registered twice`.

	const componentId = `${portletNamespace}importModal`;

	const {observer, onClose} = useModal({
		onClose: () => {
			setVisible(false);
		},
	});

	const _handleClose = (redirect) => {
		setErrorMessage('');
		setImportFile(null);

		onClose(false);

		if (redirect) {
			navigate(redirect);
		}
	};

	const _handleFormError = (error) => {
		setErrorMessage(
			error ||
				Liferay.Language.get(
					'an-unexpected-error-occurred-while-importing-your-file'
				)
		);

		setLoadingResponse(false);
	};

	const _handleInputChange = (event) => {
		setImportFile(event.target.files[0]);
	};

	const _handleSubmit = async () => {
		setLoadingResponse(true);

		const importText = await new Response(importFile).text();

		try {
			const isElement = !!JSON.parse(importText).elementDefinition;

			const fetchURL = isElement
				? '/o/search-experiences-rest/v1.0/sxp-elements'
				: '/o/search-experiences-rest/v1.0/sxp-blueprints';

			fetch(fetchURL, {
				body: importText,
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
							responseContent.type.includes(
								'DuplicateSXPBlueprintExternalReferenceCodeException'
							) ||
							responseContent.type.includes(
								'DuplicateSXPElementExternalReferenceCodeException'
							)
						) {
							_handleFormError(
								isElement
									? Liferay.Language.get(
											'unable-to-import-element-with-the-same-external-reference-code-as-an-existing-element'
										)
									: Liferay.Language.get(
											'unable-to-import-blueprint-with-the-same-external-reference-code-as-an-existing-blueprint'
										)
							);
						}
						else if (
							responseContent.type.includes(
								'SXPElementTitleException'
							)
						) {
							_handleFormError(
								sub(
									Liferay.Language.get(
										'error.default-locale-x-title-blank'
									),
									Liferay.ThemeDisplay.getDefaultLanguageId()
								)
							);
						}
						else {
							_handleFormError(
								isElement
									? Liferay.Language.get(
											'unable-to-import-because-the-element-configuration-is-invalid'
										)
									: Liferay.Language.get(
											'unable-to-import-because-the-blueprint-configuration-is-invalid'
										)
							);
						}

						if (process.env.NODE_ENV === 'development') {
							console.error(responseContent.title);
						}
					}

					setLoadingResponse(false);

					if (ok) {
						_handleClose(redirectURL);
					}
				})
				.catch(() => {
					_handleFormError();
				});
		}
		catch {
			_handleFormError();
		}
	};

	useEffect(() => {
		Liferay.component(
			componentId,
			{
				open: () => {
					setVisible(true);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () => Liferay.destroyComponent(componentId);
	}, [componentId, setVisible]);

	return visible ? (
		<ClayModal
			className="sxp-import-modal-root"
			observer={observer}
			size="full-screen"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import')}
			</ClayModal.Header>

			<ClayModal.Body>
				{errorMessage && (
					<ClayAlert
						displayType="danger"
						onClose={() => setErrorMessage('')}
						title={Liferay.Language.get('error')}
					>
						{errorMessage}
					</ClayAlert>
				)}

				<p className="text-secondary">
					{Liferay.Language.get(
						'select-a-blueprint-or-element-json-file-to-import'
					)}
				</p>

				<div className="form-group">
					<label className="control-label" htmlFor="file">
						{Liferay.Language.get('select-file')}

						<span className="reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<ClayInput
						accept={VALID_EXTENSIONS}
						name="file"
						onChange={_handleInputChange}
						required
						type="file"
					/>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={loadingResponse}
							displayType="secondary"
							onClick={_handleClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!importFile || loadingResponse}
							displayType="primary"
							onClick={_handleSubmit}
						>
							{loadingResponse && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									/>
								</span>
							)}

							{Liferay.Language.get('import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
};

export default ImportSXPBlueprintModal;
