/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import {createResourceURL, fetch} from 'frontend-js-web';
import React, {useState} from 'react';

export function EditClientOAuth2ModalContent({
	alertText,
	baseResourceURL,
	closeModal,
	handleSetInputValue,
	id,
	initialValue,
	isSecret = false,
	label,
	title,
	tooltip,
}: {
	alertText: string;
	baseResourceURL: string;
	closeModal: () => void;
	handleSetInputValue: (newInputValue: string) => void;
	id: string;
	initialValue: string;
	isSecret: boolean;
	label: string;
	title: string;
	tooltip: string;
}) {
	const [modalValue, setModalValue] = useState(initialValue);

	const modalId = `${id}modal`;
	const valueChanged = modalValue !== initialValue;

	const resourceURL = createResourceURL(baseResourceURL, {
		p_p_id: 'com_liferay_oauth2_provider_web_internal_portlet_OAuth2AdminPortlet',
		p_p_resource_id: '/oauth2_provider/generate_random_secret',
	}).href;

	const generateRandomSecret = async () => {
		const response = await fetch(resourceURL, {
			method: 'POST',
		});

		const responseText = (await response.text()) as string;

		setModalValue(responseText);
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<div className="modal-body">
				<ClayAlert
					displayType="warning"
					title={Liferay.Language.get('warning')}
				>
					{alertText}
				</ClayAlert>

				<FieldBase id={modalId} label={label} tooltip={tooltip}>
					<ClayInput.Group>
						<ClayInput.GroupItem prepend>
							<ClayInput
								id={modalId}
								name={modalId}
								onChange={({target: {value}}) =>
									setModalValue(value)
								}
								type="text"
								value={modalValue}
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem append shrink>
							<ClayButton
								disabled={!valueChanged}
								displayType="secondary"
								onClick={() => setModalValue(initialValue)}
							>
								{Liferay.Language.get('revert')}
							</ClayButton>

							{isSecret && (
								<ClayButton
									displayType="secondary"
									onClick={() => generateRandomSecret()}
								>
									{Liferay.Language.get(
										'generate-new-secret'
									)}
								</ClayButton>
							)}
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</FieldBase>
			</div>

			<ClayModal.Footer
				first={
					valueChanged ? (
						<span>
							<ClayIcon symbol="unlock" />

							{Liferay.Language.get('changed')}
						</span>
					) : (
						<span>
							<ClayIcon symbol="lock" />

							{Liferay.Language.get('unchanged')}
						</span>
					)
				}
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => {
								setModalValue(initialValue);
								closeModal();
							}}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							onClick={() => {
								handleSetInputValue(modalValue);
								closeModal();
							}}
						>
							{Liferay.Language.get('apply')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
