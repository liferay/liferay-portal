/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {DatePicker} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import {callWindowGlobalFunction} from '../../js/utils/callWindowGlobalFunction';
import {convertToUTC} from '../../js/utils/convertToUTC';

interface ModalSchedulePublicationProps {
	hiddenScheduleValues: {[key: string]: string | null};
	portletNamespace: string;
	submitRef: string;
	value: string;
}

export default function ModalSchedulePublication({
	hiddenScheduleValues,
	portletNamespace,
	submitRef,
	value,
}: ModalSchedulePublicationProps) {
	const [dateError, setDateError] = useState('');
	const [displayDate, setDisplayDate] = useState(value ?? '');
	const [visible, setVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			setDateError('');
			setDisplayDate('');
			setVisible(false);
		},
	});

	const handleError = (value: string): boolean => {
		if (!value) {
			setDateError(Liferay.Language.get('this-field-is-required'));

			return false;
		}

		setDateError('');

		return true;
	};

	const handleSubmit = () => {
		const isValid = handleError(displayDate);

		if (!isValid) {
			return;
		}

		const hiddenInput = document.getElementById(
			`${portletNamespace}scheduleContainer`
		) as HTMLInputElement;

		hiddenInput.value = JSON.stringify({
			...hiddenScheduleValues,
			displayDate: convertToUTC(displayDate),
		});

		callWindowGlobalFunction(submitRef);

		onClose();

		Liferay.fire('submitObjectEntry');
	};

	useEffect(() => {
		const openModal = () => setVisible(true);

		Liferay.on('openModalSchedulePublication', openModal);

		return () => {
			Liferay.detach(
				'openModalSchedulePublication',
				openModal as () => void
			);
		};
	}, []);

	return (
		<>
			{visible && (
				<ClayModal center observer={observer}>
					<ClayModal.Header>
						{Liferay.Language.get('schedule-publication')}
					</ClayModal.Header>

					<ClayModal.Body>
						<p className="text-secondary">
							{Liferay.Language.get(
								'set-the-date-and-time-for-publishing-the-object-entry'
							)}
						</p>

						<DatePicker
							error={dateError}
							id={portletNamespace + 'displayDate'}
							label={Liferay.Language.get('publish-date')}
							onBlur={() => {
								handleError(displayDate);
							}}
							onChange={(value) => {
								setDisplayDate(value);
								handleError(value);
							}}
							required
							type="DateTime"
							value={displayDate}
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
									displayType="primary"
									onClick={() => {
										handleSubmit();
									}}
								>
									{Liferay.Language.get('schedule')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
