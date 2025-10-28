/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import getCN from 'classnames';
import {fetch, navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {CUSTOM_JSON_SXP_ELEMENT} from '../utils/data';
import {DEFAULT_ERROR} from '../utils/errorMessages';
import {DEFAULT_HEADERS} from '../utils/fetch/fetch_data';
import isDefined from '../utils/functions/is_defined';
import {setInitialSuccessToast} from '../utils/toasts';

const ADD_EVENT = 'addSXPElement';

const AddSXPElementModal = ({
	defaultLocale,
	editSXPElementURL,
	portletNamespace,
}) => {
	const isMounted = useIsMounted();

	const [descriptionInputValue, setDescriptionInputValue] = useState('');
	const [errorMessage, setErrorMessage] = useState();
	const [loadingResponse, setLoadingResponse] = useState(false);
	const [titleInputValue, setTitleInputValue] = useState('');
	const [visibleModal, setVisibleModal] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => setVisibleModal(false),
	});

	useEffect(() => {
		Liferay.on(ADD_EVENT, () => setVisibleModal(true));

		return () => {
			Liferay.detach(ADD_EVENT);
		};
	}, []);

	const _handleFormError = (responseContent) => {
		setErrorMessage(responseContent.error || DEFAULT_ERROR);

		setLoadingResponse(false);
	};

	const _handleSubmit = (event) => {
		event.preventDefault();

		fetch('/o/search-experiences-rest/v1.0/sxp-elements', {
			body: JSON.stringify({
				description_i18n: {[defaultLocale]: descriptionInputValue},
				elementDefinition: CUSTOM_JSON_SXP_ELEMENT.elementDefinition,
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
				if (isMounted()) {
					if (responseContent.error) {
						_handleFormError(responseContent);
					}
					else {
						setVisibleModal(false);

						if (isDefined(responseContent.id)) {
							const url = new URL(editSXPElementURL);

							url.searchParams.set(
								`${portletNamespace}sxpElementId`,
								responseContent.id
							);

							setInitialSuccessToast(
								Liferay.Language.get(
									'the-element-was-created-successfully'
								)
							);

							navigate(url);
						}
						else {
							setInitialSuccessToast(
								Liferay.Language.get(
									'the-element-was-created-successfully'
								)
							);

							navigate(window.location.href);
						}
					}
				}
			})
			.catch((response) => {
				_handleFormError(response);
			});

		setLoadingResponse(true);
	};

	return (
		<ClayModalProvider>
			{visibleModal && (
				<ClayModal
					className="sxp-add-element-modal-root"
					observer={observer}
					size="md"
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('new-search-element')}
					</ClayModal.Header>

					<form
						id={`${portletNamespace}form`}
						onSubmit={_handleSubmit}
					>
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
										setDescriptionInputValue(
											event.target.value
										)
									}
									value={descriptionInputValue}
								/>
							</div>
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
			)}
		</ClayModalProvider>
	);
};

export default AddSXPElementModal;
