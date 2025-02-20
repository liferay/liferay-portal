/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useState} from 'react';

import {HEADERS, TEMPLATE_CREATED_EVENT} from './constants';

async function saveTemplate(formDataQuerySelector, updateData, url) {
	const mainFormData = document.querySelector(formDataQuerySelector);
	const formData = new FormData(mainFormData);

	for (const [key, value] of Object.entries(updateData)) {
		formData.append(key, value);
	}

	const response = await fetch(url, {
		body: formData,
		headers: HEADERS,
		method: 'POST',
	});

	return await response.json();
}

const SaveTemplateModal = ({
	closeModal,
	formDataQuerySelector,
	formSubmitURL,
	namespace,
	observer,
}) => {
	const inputNameId = namespace + 'templateName';
	const isMounted = useIsMounted();
	const [errorMessage, setErrorMessage] = useState();
	const [loadingResponse, setLoadingResponse] = useState(false);
	const [inputValue, setInputValue] = useState('');

	const handleSubmit = async (event) => {
		event.preventDefault();

		try {
			const updateData = {[inputNameId]: inputValue};

			const saveTemplateResponse = await saveTemplate(
				formDataQuerySelector,
				updateData,
				formSubmitURL
			);

			if (isMounted()) {
				if (saveTemplateResponse.error) {
					setLoadingResponse(false);
					setErrorMessage(saveTemplateResponse.error);
				}
				else {
					Liferay.fire(TEMPLATE_CREATED_EVENT, {
						template: saveTemplateResponse,
					});

					openToast({
						message: Liferay.Language.get('template-was-created'),
						type: 'success',
					});

					closeModal();
				}
			}
		}
		catch (error) {
			setErrorMessage(Liferay.Language.get('unexpected-error'));
		}
		finally {
			setLoadingResponse(false);
		}
	};

	return (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header>
				{Liferay.Language.get('save-as-template')}
			</ClayModal.Header>

			<ClayForm id={`${namespace}form`} onSubmit={handleSubmit}>
				<ClayModal.Body>
					<ClayForm.Group className={errorMessage ? 'has-error' : ''}>
						<label htmlFor={inputNameId}>
							{Liferay.Language.get('name')}

							<span className="reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<ClayInput
							autoFocus
							disabled={loadingResponse}
							id={inputNameId}
							name={inputNameId}
							onChange={(event) =>
								setInputValue(event.target.value)
							}
							placeholder={Liferay.Language.get('template-name')}
							required
							type="text"
							value={inputValue}
						/>

						{errorMessage && (
							<ClayForm.FeedbackGroup>
								<ClayForm.FeedbackItem>
									<ClayForm.FeedbackIndicator symbol="exclamation-full" />

									{errorMessage}
								</ClayForm.FeedbackItem>
							</ClayForm.FeedbackGroup>
						)}
					</ClayForm.Group>
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
								disabled={loadingResponse || !inputValue.length}
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

								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
};

export default SaveTemplateModal;
