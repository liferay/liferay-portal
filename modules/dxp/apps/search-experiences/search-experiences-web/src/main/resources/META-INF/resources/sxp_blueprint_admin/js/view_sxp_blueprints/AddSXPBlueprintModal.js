/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import getCN from 'classnames';
import {fetch, navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {
	DEFAULT_ADVANCED_CONFIGURATION,
	DEFAULT_HIGHLIGHT_CONFIGURATION,
	DEFAULT_PARAMETER_CONFIGURATION,
	DEFAULT_SORT_CONFIGURATION,
} from '../utils/data';
import {DEFAULT_ERROR} from '../utils/errorMessages';
import {DEFAULT_HEADERS} from '../utils/fetch/fetch_data';
import {setInitialSuccessToast} from '../utils/toasts';

const ADD_EVENT = 'addSXPBlueprint';

const AddModal = ({
	defaultLocale,
	editSXPBlueprintURL,
	observer,
	onClose,
	portletNamespace,
}) => {
	const isMounted = useIsMounted();

	const [errorMessage, setErrorMessage] = useState();
	const [loadingResponse, setLoadingResponse] = useState(false);

	const [descriptionInputValue, setDescriptionInputValue] = useState('');
	const [titleInputValue, setTitleInputValue] = useState('');
	const [collectionProviderToggleValue, setCollectionProviderToggleValue] =
		useState(false);

	const _handleFormError = (responseContent) => {
		setErrorMessage(responseContent.error || DEFAULT_ERROR);

		setLoadingResponse(false);
	};

	const _handleSubmit = (event) => {
		event.preventDefault();

		fetch('/o/search-experiences-rest/v1.0/sxp-blueprints', {
			body: JSON.stringify({
				configuration: {
					advancedConfiguration: DEFAULT_ADVANCED_CONFIGURATION,
					aggregationConfiguration: {},
					generalConfiguration: Liferay.FeatureFlags['LPS-129412']
						? {
								clauseContributorsExcludes: [],
								clauseContributorsIncludes: ['*'],
								collectionProvider:
									collectionProviderToggleValue,
								searchableAssetTypes: [],
							}
						: {
								clauseContributorsExcludes: [],
								clauseContributorsIncludes: ['*'],
								searchableAssetTypes: [],
							},
					highlightConfiguration: DEFAULT_HIGHLIGHT_CONFIGURATION,
					parameterConfiguration: DEFAULT_PARAMETER_CONFIGURATION,
					queryConfiguration: {
						applyIndexerClauses: true,
					},
					sortConfiguration: DEFAULT_SORT_CONFIGURATION,
				},
				description_i18n: {[defaultLocale]: descriptionInputValue},
				elementInstances: [],
				title_i18n: {[defaultLocale]: titleInputValue},
			}),
			headers: DEFAULT_HEADERS,
			method: 'POST',
		})
			.then((response) => {
				if (!response.ok) {
					_handleFormError();
				}

				return response.json();
			})
			.then((responseContent) => {
				if (!isMounted()) {
					return;
				}

				if (responseContent.error) {
					_handleFormError(responseContent);
				}
				else {
					onClose();

					if (responseContent.id) {
						const url = new URL(editSXPBlueprintURL);

						url.searchParams.set(
							`${portletNamespace}sxpBlueprintId`,
							responseContent.id
						);

						setInitialSuccessToast(
							Liferay.Language.get(
								'the-blueprint-was-created-successfully'
							)
						);

						navigate(url);
					}
					else {
						setInitialSuccessToast(
							Liferay.Language.get(
								'the-blueprint-was-created-successfully'
							)
						);

						navigate(window.location.href);
					}
				}
			})
			.catch((response) => {
				_handleFormError(response);
			});

		setLoadingResponse(true);
	};

	return (
		<ClayModal
			className="sxp-add-blueprint-modal-root"
			observer={observer}
			size="md"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('new-search-blueprint')}
			</ClayModal.Header>

			<form id={`${portletNamespace}form`} onSubmit={_handleSubmit}>
				<ClayModal.Body>
					<div
						className={getCN('form-group', {
							'has-error': errorMessage,
						})}
					>
						<label
							className="control-label"
							htmlFor={`${portletNamespace}title`}
						>
							{Liferay.Language.get('title')}

							<span className="reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<input
							autoFocus
							className="form-control"
							disabled={loadingResponse}
							id={`${portletNamespace}title`}
							name={`${portletNamespace}title`}
							onChange={(event) =>
								setTitleInputValue(event.target.value)
							}
							required
							type="text"
							value={titleInputValue}
						/>

						<input
							id={`${portletNamespace}title_${defaultLocale}`}
							name={`${portletNamespace}title_${defaultLocale}`}
							type="hidden"
							value={titleInputValue}
						/>

						{errorMessage && (
							<div className="form-feedback-item">
								<ClayIcon
									className="inline-item inline-item-before"
									symbol="exclamation-full"
								/>

								{errorMessage}
							</div>
						)}
					</div>

					<div className="form-group">
						<label
							className="control-label"
							htmlFor={`${portletNamespace}description`}
						>
							{Liferay.Language.get('description')}
						</label>

						<textarea
							className="form-control"
							disabled={loadingResponse}
							id={`${portletNamespace}description`}
							name={`${portletNamespace}description`}
							onChange={(event) =>
								setDescriptionInputValue(event.target.value)
							}
							value={descriptionInputValue}
						/>

						<input
							id={`${portletNamespace}description_${defaultLocale}`}
							name={`${portletNamespace}description_${defaultLocale}`}
							type="hidden"
							value={descriptionInputValue}
						/>
					</div>

					{Liferay.FeatureFlags['LPS-129412'] && (
						<div className="form-group">
							<ClayToggle
								aria-label={Liferay.Language.get(
									'enable-as-a-collection-provider'
								)}
								checked={collectionProviderToggleValue}
								label={
									<>
										{Liferay.Language.get(
											'enable-as-a-collection-provider'
										)}

										<ClayTooltipProvider>
											<span
												title={Liferay.Language.get(
													'enable-as-a-collection-provider-help'
												)}
											>
												<ClayIcon
													className="c-ml-2 text-3 text-secondary"
													symbol="question-circle-full"
												/>
											</span>
										</ClayTooltipProvider>
									</>
								}
								onChange={() =>
									setCollectionProviderToggleValue(
										!collectionProviderToggleValue
									)
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

								{Liferay.Language.get('create')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</form>
		</ClayModal>
	);
};

export function AddSXPBlueprintModal({
	defaultLocale,
	editSXPBlueprintURL,
	portletNamespace,
}) {
	const {observer, onClose} = useModal({
		onClose: () => setVisibleModal(false),
	});

	const [visibleModal, setVisibleModal] = useState(false);

	useEffect(() => {
		Liferay.on(ADD_EVENT, () => setVisibleModal(true));

		return () => {
			Liferay.detach(ADD_EVENT);
		};
	}, []);

	return (
		<ClayModalProvider>
			{visibleModal && (
				<AddModal
					defaultLocale={defaultLocale}
					editSXPBlueprintURL={editSXPBlueprintURL}
					observer={observer}
					onClose={onClose}
					portletNamespace={portletNamespace}
				/>
			)}
		</ClayModalProvider>
	);
}

export default AddSXPBlueprintModal;
