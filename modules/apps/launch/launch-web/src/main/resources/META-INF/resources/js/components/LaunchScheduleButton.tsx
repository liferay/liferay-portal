/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML, fetch} from 'frontend-js-web';
import React, {useState} from 'react';

interface Props {
	portletNamespace: string;
	published: boolean;
	scheduleLaunchURL: string;
	scheduledDate: string | null;
}

export default function LaunchScheduleButton({
	portletNamespace,
	published,
	scheduleLaunchURL,
	scheduledDate,
}: Props) {
	const [publishDate, setPublishDate] = useState('');
	const [scheduleActive, setScheduleActive] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => setScheduleActive(false),
	});

	async function handleSchedule() {
		const body = new FormData();

		body.append(
			`${portletNamespace}publishTime`,
			String(new Date(publishDate).getTime())
		);

		try {
			const response = await fetch(scheduleLaunchURL, {
				body,
				method: 'POST',
			});

			const {error, redirect, validationError} = await response.json();

			if (error || validationError) {
				openToast({
					message: escapeHTML(error || validationError),
					type: 'danger',
				});

				return;
			}

			window.location.href = redirect;
		}
		catch (exception) {
			openToast({
				message: escapeHTML((exception as Error).message),
				type: 'danger',
			});
		}
	}

	if (scheduledDate) {
		return (
			<ClayLabel displayType="info">
				{`${Liferay.Language.get('scheduled')}: ${new Date(
					scheduledDate
				).toLocaleString()}`}
			</ClayLabel>
		);
	}

	if (published) {
		return null;
	}

	return (
		<>
			{scheduleActive && (
				<ClayModal observer={observer} size="sm">
					<ClayModal.Header>
						{Liferay.Language.get('schedule')}
					</ClayModal.Header>

					<ClayModal.Body>
						<ClayInput
							onChange={(event) =>
								setPublishDate(event.target.value)
							}
							type="datetime-local"
							value={publishDate}
						/>
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

								<ClayButton
									disabled={!publishDate}
									onClick={handleSchedule}
								>
									{Liferay.Language.get('schedule')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}

			<ClayButton
				className="text-nowrap"
				displayType="secondary"
				onClick={() => setScheduleActive(true)}
				small
				type="button"
			>
				<ClayIcon
					className="inline-item inline-item-before"
					symbol="calendar"
				/>

				{Liferay.Language.get('schedule')}
			</ClayButton>
		</>
	);
}
