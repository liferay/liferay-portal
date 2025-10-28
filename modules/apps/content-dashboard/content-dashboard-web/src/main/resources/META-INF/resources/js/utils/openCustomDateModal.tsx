/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {render} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {dateUtils, navigate} from 'frontend-js-web';
import React, {useState} from 'react';

const NOT_SELECTED_OPTION = {
	label: `-- ${Liferay.Language.get('select-type')} --`,
	value: 'not-selected',
};

function getDateRange(start: string | undefined, end: string | undefined) {
	if (start && end) {
		return `${start} - ${end}`;
	}

	return undefined;
}

function isValidDate(date: string) {
	return !isNaN(new Date(date).getDate());
}

type Props = {
	dateTypes: Array<{label: string; value: string}>;
	filterUrl: string;
	namespace: string;
	selectedDateType: string | undefined;
	selectedEndDate: string | undefined;
	selectedStartDate: string | undefined;
};

export default function openCustomDateModal(props: Props) {
	render(CustomDateModal as any, {...props}, document.createElement('div'));
}

export function CustomDateModal({
	dateTypes,
	filterUrl,
	namespace,
	selectedDateType,
	selectedEndDate,
	selectedStartDate,
}: Props) {
	const {observer, onOpenChange, open} = useModal({
		defaultOpen: true,
		onClose: () => onOpenChange(false),
	});

	const dateTypeId = useId();
	const rangeId = useId();

	const [dateType, setDateType] = useState(selectedDateType);
	const [endDate, setEndDate] = useState(selectedEndDate);
	const [startDate, setStartDate] = useState(selectedStartDate);

	const [hasError, setHasError] = useState(false);

	const onSave = () => {
		if (!isValidDate(startDate!) || !isValidDate(endDate!)) {
			setHasError(true);

			return;
		}

		const url = new URL(filterUrl);

		url.searchParams.set(`${namespace}dateType`, dateType!);
		url.searchParams.set(`${namespace}startDate`, startDate!);
		url.searchParams.set(`${namespace}endDate`, endDate!);

		navigate(url.toString());
	};

	if (!open) {
		return null;
	}

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('filter-by-date')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group>
					<label htmlFor={dateTypeId}>
						{Liferay.Language.get('date-type')}
					</label>

					<ClaySelectWithOption
						id={dateTypeId}
						onChange={(event) => setDateType(event.target.value)}
						options={[NOT_SELECTED_OPTION, ...dateTypes]}
						value={dateType || NOT_SELECTED_OPTION.value}
					/>
				</ClayForm.Group>

				{dateType ? (
					<ClayForm.Group
						className={classNames({'has-error': hasError})}
					>
						<label htmlFor={rangeId}>
							{Liferay.Language.get('date-range')}
						</label>

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
							onChange={(range: string) => {
								const [start, end] = range!.split(' - ');

								setStartDate(start);
								setEndDate(end);

								setHasError(false);
							}}
							placeholder="YYYY-MM-DD - YYYY-MM-DD"
							range
							value={getDateRange(startDate, endDate)}
							weekdaysShort={dateUtils.getWeekdaysShort()}
							years={{
								end: new Date().getFullYear() + 10,
								start: new Date().getFullYear() - 10,
							}}
						/>

						{hasError && (
							<ClayForm.FeedbackGroup role="alert">
								<ClayForm.FeedbackItem>
									{Liferay.Language.get(
										'date-range-is-invalid'
									)}
								</ClayForm.FeedbackItem>
							</ClayForm.FeedbackGroup>
						)}
					</ClayForm.Group>
				) : null}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onOpenChange(false)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={
								!dateType || !getDateRange(startDate, endDate)
							}
							onClick={onSave}
						>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
