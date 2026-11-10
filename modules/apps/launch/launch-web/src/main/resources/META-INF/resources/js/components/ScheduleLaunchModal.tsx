/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import {objectToFormData, sub} from 'frontend-js-web';
import React, {useId, useRef, useState} from 'react';

import LaunchService from '../services/LaunchService';
import {
	formatUTCOffset,
	getDateTimeConfig,
	getLocalTime,
	toTimestamp,
} from '../util/dateTime';

import type {FormEvent} from 'react';

import type {TimeZoneOption} from '../util/dateTime';

export interface ScheduleLaunchModalProps {
	closeModal: () => void;
	launchName: string;
	onScheduled: (redirect?: string) => void;
	portletNamespace: string;
	scheduleLaunchURL: string;
	timeZoneId: string;
	timeZones: TimeZoneOption[];
}

export default function ScheduleLaunchModal({
	closeModal,
	launchName,
	onScheduled,
	portletNamespace,
	scheduleLaunchURL,
	timeZoneId: defaultTimeZoneId,
	timeZones,
}: ScheduleLaunchModalProps) {
	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const [dateTime, setDateTime] = useState('');
	const [error, setError] = useState<string | null>(null);
	const [loading, setLoading] = useState(false);
	const [timeZoneId, setTimeZoneId] = useState(defaultTimeZoneId);

	const dateTimeConfig = getDateTimeConfig(locale, timeZoneId);

	const dateTimeId = useId();
	const timeZoneSelectId = useId();

	const dateTimeInputRef = useRef<HTMLInputElement>(null);

	const timestamp = toTimestamp(
		dateTime,
		dateTimeConfig.dateFormat,
		timeZoneId
	);

	const localDateTime =
		timestamp !== null
			? getLocalTime({
					locale,
					selectedTimeZoneId: timeZoneId,
					timeZoneId: defaultTimeZoneId,
					timestamp,
				})
			: null;

	async function handleSubmit(event: FormEvent<HTMLFormElement>) {
		event.preventDefault();

		setError(null);
		setLoading(true);

		const {data, error} = await LaunchService.scheduleLaunch(
			scheduleLaunchURL,
			objectToFormData({
				[`${portletNamespace}publishTime`]: String(timestamp),
			})
		);

		setLoading(false);

		if (error) {
			setError(error);

			dateTimeInputRef.current?.focus();

			return;
		}

		if (!data) {
			return;
		}

		closeModal();

		onScheduled(data.redirect);
	}

	return (
		<>
			<ClayModal.Header>
				{sub(Liferay.Language.get('schedule-x'), launchName)}
			</ClayModal.Header>

			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Body>
					{error && (
						<ClayAlert
							displayType="danger"
							title={`${Liferay.Language.get('error')}:`}
						>
							{error}
						</ClayAlert>
					)}

					<p className="text-3 text-secondary">
						{Liferay.Language.get(
							'all-entries-must-be-ready-to-publish-by-the-scheduled-date-and-time.-otherwise,-the-launch-will-not-be-published'
						)}
					</p>

					<div className="row">
						<div className="col-8">
							<FieldBase
								className="mb-0"
								helpMessage={
									localDateTime
										? sub(
												Liferay.Language.get(
													'your-local-time-x'
												),
												localDateTime
											)
										: undefined
								}
								id={dateTimeId}
								label={Liferay.Language.get('date-and-time')}
								required
							>
								<ClayDatePicker
									aria-describedby={
										localDateTime
											? `${dateTimeId}fieldFeedback`
											: undefined
									}
									aria-required
									{...dateTimeConfig}
									id={dateTimeId}
									onChange={setDateTime}
									ref={dateTimeInputRef}
									time
									timezone={formatUTCOffset(
										timestamp ?? Date.now(),
										timeZoneId
									)}
									value={dateTime}
								/>
							</FieldBase>
						</div>

						<div className="col-4">
							<FieldBase
								className="mb-0"
								hideFeedback
								id={timeZoneSelectId}
								label={Liferay.Language.get('time-zone')}
							>
								<ClaySelectWithOption
									id={timeZoneSelectId}
									onChange={(event) =>
										setTimeZoneId(event.target.value)
									}
									options={timeZones}
									value={timeZoneId}
								/>
							</FieldBase>
						</div>
					</div>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								disabled={loading}
								displayType="secondary"
								onClick={closeModal}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={loading || timestamp === null}
								type="submit"
							>
								{Liferay.Language.get('schedule')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</>
	);
}
