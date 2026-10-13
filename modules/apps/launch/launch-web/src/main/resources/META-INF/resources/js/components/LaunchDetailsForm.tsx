/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {getLaunch, updateLaunch} from '../api/launches';

interface Props {
	launchSetId: number;
	portletNamespace: string;
}

export default function LaunchDetailsForm({
	launchSetId,
	portletNamespace,
}: Props) {
	const [description, setDescription] = useState('');
	const [loading, setLoading] = useState(true);
	const [name, setName] = useState('');

	useEffect(() => {
		getLaunch(launchSetId)
			.then((launch) => {
				setDescription(launch.description ?? '');
				setName(launch.name);
			})
			.catch((exception: Error) =>
				openToast({
					message: escapeHTML(exception.message),
					type: 'danger',
				})
			)
			.finally(() => setLoading(false));
	}, [launchSetId]);

	function handleSubmit(event: React.FormEvent) {
		event.preventDefault();

		updateLaunch({description, id: launchSetId, name})
			.then(() => {
				openToast({
					message: Liferay.Language.get(
						'the-launch-was-saved-successfully'
					),
					type: 'success',
				});

				const url = new URL(window.location.href);

				url.searchParams.set(`${portletNamespace}launchSetName`, name);

				window.location.href = url.toString();
			})
			.catch((exception: Error) =>
				openToast({
					message: escapeHTML(exception.message),
					type: 'danger',
				})
			);
	}

	if (loading) {
		return <div>{Liferay.Language.get('loading')}</div>;
	}

	return (
		<ClayForm className="sheet" onSubmit={handleSubmit}>
			<ClayForm.Group>
				<label htmlFor="launchName">
					{Liferay.Language.get('name')}

					<span className="reference-mark text-warning">*</span>
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
					onChange={(event) => setDescription(event.target.value)}
					value={description}
				/>
			</ClayForm.Group>

			<ClayButton disabled={!name} type="submit">
				{Liferay.Language.get('save')}
			</ClayButton>
		</ClayForm>
	);
}
