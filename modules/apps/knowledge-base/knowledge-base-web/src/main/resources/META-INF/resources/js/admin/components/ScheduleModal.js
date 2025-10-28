/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayModal from '@clayui/modal';
import classnames from 'classnames';
import {dateUtils} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

const DIFFERENCE_IN_YEARS = 1;

export function isValidDate(dateString, userTimeZone) {

	// Regular expression for the 'yyyy-MM-dd HH:mm' format

	const dateRegex =
		/^(\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/;

	if (!dateRegex.test(dateString.trim())) {
		return false;
	}

	const [, year, month, day, hour, minute] = dateString
		.trim()
		.match(dateRegex);
	const date = new Date(year, month - 1, day, hour, minute);

	const currenDate = new Date(
		new Date().toLocaleString('en-US', {timeZone: userTimeZone})
	);

	if (date <= currenDate) {
		return false;
	}

	const dateYear = date.getFullYear();
	const currentYear = currenDate.getFullYear();

	if (dateYear > currentYear + DIFFERENCE_IN_YEARS) {
		return false;
	}

	return true;
}

const noop = () => {};

export default function ScheduleModal({
	callback = noop,
	displayDate: initialDisplayDate,
	scheduled,
	timeZone,
	observer,
	onModalClose = noop,
}) {
	const [displayDate, setDisplayDate] = useState(
		scheduled ? initialDisplayDate : ''
	);
	const [invalidDate, setInvalidDate] = useState(false);

	const handleScheduleButtonOnClick = () => {
		onModalClose();
		callback(displayDate.trim());
	};

	const publisNowButtonOnClick = () => {
		onModalClose();
		callback('');
	};

	const currentYear = new Date().getFullYear();

	useEffect(() => {
		setInvalidDate(
			displayDate !== '' && !isValidDate(displayDate, timeZone)
		);
	}, [displayDate, timeZone]);

	return (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{scheduled
					? Liferay.Language.get('edit-scheduled-publication')
					: Liferay.Language.get('schedule-publication')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="text-secondary">
					{scheduled
						? Liferay.Language.get(
								'this-article-is-set-to-publish-later'
							)
						: Liferay.Language.get(
								'set-date-and-time-for-publication'
							)}
				</p>

				<div className={classnames({'has-error': invalidDate})}>
					<label>{Liferay.Language.get('date-and-time')}</label>

					<ClayDatePicker
						ariaLabels={{
							buttonChooseDate: `${Liferay.Language.get(
								'select-date'
							)}`,
							buttonDot: `${Liferay.Language.get(
								'select-current-date'
							)}`,
							buttonNextMonth: `${Liferay.Language.get(
								'select-next-month'
							)}`,
							buttonPreviousMonth: `${Liferay.Language.get(
								'select-previous-month'
							)}`,
							dialog: `${Liferay.Language.get('select-date')}`,
							selectMonth: `${Liferay.Language.get('select-a-month')}`,
							selectYear: `${Liferay.Language.get('select-a-year')}`,
						}}
						dateFormat="yyyy-MM-dd"
						firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
						months={[
							`${Liferay.Language.get('january')}`,
							`${Liferay.Language.get('february')}`,
							`${Liferay.Language.get('march')}`,
							`${Liferay.Language.get('april')}`,
							`${Liferay.Language.get('may')}`,
							`${Liferay.Language.get('june')}`,
							`${Liferay.Language.get('july')}`,
							`${Liferay.Language.get('august')}`,
							`${Liferay.Language.get('september')}`,
							`${Liferay.Language.get('october')}`,
							`${Liferay.Language.get('november')}`,
							`${Liferay.Language.get('december')}`,
						]}
						onChange={setDisplayDate}
						placeholder="yyyy-MM-dd HH:mm"
						time
						timezone={timeZone}
						value={displayDate}
						weekdaysShort={dateUtils.getWeekdaysShort()}
						years={{
							end: currentYear + DIFFERENCE_IN_YEARS,
							start: currentYear,
						}}
					/>
				</div>

				{invalidDate && (
					<div className="error-container mt-1">
						<ClayAlert
							displayType="danger"
							title={Liferay.Language.get('error-colon') + ' '}
							variant="feedback"
						>
							{Liferay.Language.get('please-enter-a-valid-date')}
						</ClayAlert>
					</div>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<>
						<ClayButton
							borderless="<%= true %>"
							className="mr-3"
							displayType="secondary"
							onClick={onModalClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						{scheduled && (
							<ClayButton
								className="mr-3"
								displayType="secondary"
								onClick={publisNowButtonOnClick}
							>
								{Liferay.Language.get('publish-now')}
							</ClayButton>
						)}

						<ClayButton
							disabled={invalidDate || !displayDate}
							displayType="primary"
							onClick={handleScheduleButtonOnClick}
						>
							{Liferay.Language.get('schedule')}
						</ClayButton>
					</>
				}
			/>
		</ClayModal>
	);
}

ScheduleModal.propTypes = {
	callback: PropTypes.func,
	displayDate: PropTypes.string,
	observer: PropTypes.object.isRequired,
	onModalClose: PropTypes.func,
	scheduled: PropTypes.bool,
	timeZone: PropTypes.string,
};
