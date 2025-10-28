/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import {fetch, navigate, objectToFormData} from 'frontend-js-web';
import React, {useState} from 'react';

type FrontendTokenDefinitionProvider = {
	name: string;
	themeId: string;
};

interface AddStyleBookModalProps {
	addStyleBookEntryURL: string;
	closeModal: () => void;
	frontendTokenDefinitionProviders?: Array<FrontendTokenDefinitionProvider>;
	namespace: string;
}

const AddStyleBookModalContent = ({
	addStyleBookEntryURL,
	closeModal,
	frontendTokenDefinitionProviders = [],
	namespace,
}: AddStyleBookModalProps) => {
	const [errorMessage, setErrorMessage] = useState<string>('');
	const [loading, setLoading] = useState(false);
	const [name, setName] = useState<string>('');
	const [themeId, setThemeId] = useState<React.Key>(
		frontendTokenDefinitionProviders[0].themeId
	);

	const validateName = (name: string) => {
		const errorMessage = !name.trim()
			? Liferay.Language.get('this-field-is-required')
			: '';

		setErrorMessage(errorMessage);

		return errorMessage;
	};

	const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const errorMessage = validateName(name);

		if (errorMessage) {
			return;
		}

		setLoading(true);

		const body = Liferay.Util.ns(namespace, {
			name,
			themeId,
		});

		fetch(addStyleBookEntryURL, {
			body: objectToFormData(body),
			method: 'POST',
		})
			.then((response) => response.json())
			.then(({error, redirectURL}) => {
				if (error) {
					setErrorMessage(error);
					setLoading(false);
				}
				else if (redirectURL) {
					navigate(redirectURL, {
						beforeScreenFlip: closeModal,
					});
				}
			})
			.catch(({error}) => {
				setErrorMessage(error || '');
			});
	};

	const formId = `${namespace}form`;
	const frontendTokenDefinitionProviderId = `${namespace}frontendTokenDefinitionProvider`;
	const nameId = `${namespace}name`;

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-style-book')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm id={formId} onSubmit={handleSubmit}>
					<FieldBase
						helpMessage={Liferay.Language.get(
							'the-style-book-will-be-created-based-on-the-provided-frontend-token-definition'
						)}
						id={frontendTokenDefinitionProviderId}
						label={Liferay.Language.get('create-style-book-for')}
					>
						<Picker
							defaultSelectedKey={themeId}
							id={frontendTokenDefinitionProviderId}
							items={frontendTokenDefinitionProviders}
							messages={{
								itemDescribedby: Liferay.Language.get(
									'you-are-currently-on-a-text-element,-inside-of-a-list-box'
								),
								itemSelected:
									Liferay.Language.get('x-selected'),
								scrollToBottomAriaLabel:
									Liferay.Language.get('scroll-to-bottom'),
								scrollToTopAriaLabel:
									Liferay.Language.get('scroll-to-top'),
							}}
							onSelectionChange={setThemeId}
							selectedKey={themeId}
						>
							{(item) => (
								<Option
									key={item.themeId}
									textValue={item.name}
								>
									{item.name}
								</Option>
							)}
						</Picker>
					</FieldBase>

					<FieldBase
						className="mb-0"
						errorMessage={errorMessage}
						id={nameId}
						label={Liferay.Language.get('name')}
						required
					>
						<ClayInput
							id={nameId}
							onChange={(event) => {
								const name = event.target.value;

								setName(name);

								validateName(name);
							}}
							value={name}
						/>
					</FieldBase>
				</ClayForm>
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
							disabled={Boolean(errorMessage)}
							displayType="primary"
							form={formId}
							type="submit"
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
};

export default AddStyleBookModalContent;
