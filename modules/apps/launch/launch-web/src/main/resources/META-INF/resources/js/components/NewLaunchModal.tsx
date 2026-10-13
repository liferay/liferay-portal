/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React, {useState} from 'react';

import {createLaunch} from '../api/launches';
import fillViewLaunchURL from '../util/fillViewLaunchURL';

interface Props {
	onClose: () => void;
	viewLaunchURL: string;
}

export default function NewLaunchModal({
	onClose: onCloseModal,
	viewLaunchURL,
}: Props) {
	const [description, setDescription] = useState('');
	const [name, setName] = useState('');

	const {observer, onClose} = useModal({
		onClose: onCloseModal,
	});

	function handleSubmit(event: React.FormEvent) {
		event.preventDefault();

		createLaunch({description, name})
			.then((launch) => {
				window.location.href = fillViewLaunchURL(viewLaunchURL, launch);
			})
			.catch((exception: Error) =>
				openToast({
					message: escapeHTML(exception.message),
					type: 'danger',
				})
			);
	}

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header>
				{Liferay.Language.get('new-launch')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm onSubmit={handleSubmit}>
					<ClayForm.Group>
						<label htmlFor="launchName">
							{Liferay.Language.get('name')}
						</label>

						<ClayInput
							id="launchName"
							onChange={(event) => setName(event.target.value)}
							required
							type="text"
							value={name}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor="launchDescription">
							{Liferay.Language.get('description')}
						</label>

						<ClayInput
							component="textarea"
							id="launchDescription"
							onChange={(event) =>
								setDescription(event.target.value)
							}
							value={description}
						/>
					</ClayForm.Group>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton disabled={!name} onClick={handleSubmit}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
