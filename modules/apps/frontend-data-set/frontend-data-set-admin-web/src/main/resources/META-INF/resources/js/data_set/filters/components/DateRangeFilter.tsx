/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import classNames from 'classnames';
import {dateUtils} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {IDateFilter, IField, IFilter} from '../../../utils/types';
import Configuration from './Configuration';
import Footer from './Footer';

function Header() {
	return <>{Liferay.Language.get('new-date-range-filter')}</>;
}

interface IBodyProps {
	fieldNames?: string[];
	fields: IField[];
	filter?: IFilter;
	namespace: string;
	onCancel: Function;
	onSave: Function;
}

const intl = new Intl.DateTimeFormat(
	Liferay.ThemeDisplay.getBCP47LanguageId(),
	{
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
	}
);

function Body({
	fieldNames: usedFieldNames,
	fields,
	filter,
	namespace,
	onCancel,
	onSave,
}: IBodyProps) {
	const [fieldInUseValidationError, setFieldInUseValidationError] =
		useState<boolean>(false);
	const [fieldValidationError, setFieldValidationError] =
		useState<boolean>(false);
	const [labelValidationError, setLabelValidationError] = useState(false);

	const [saveButtonDisabled, setSaveButtonDisabled] =
		useState<boolean>(false);
	const fdsFilterLabelTranslations = filter?.label_i18n ?? {};
	const [i18nFilterLabels, setI18nFilterLabels] = useState(
		fdsFilterLabelTranslations
	);

	const [selectedField, setSelectedField] = useState<IField | undefined>(
		filter ? {label: filter.fieldName, name: filter.fieldName} : undefined
	);
	const [from, setFrom] = useState<string>(
		filter && (filter as IDateFilter)?.from
			? intl.format(new Date((filter as IDateFilter)?.from))
			: ''
	);
	const [to, setTo] = useState<string>(
		filter && (filter as IDateFilter)?.to
			? intl.format(new Date((filter as IDateFilter)?.to))
			: ''
	);
	const [isValidDateRange, setIsValidDateRange] = useState<boolean>(true);

	const fromFormElementId = `${namespace}From`;
	const toFormElementId = `${namespace}To`;

	useEffect(() => {
		let isValid = true;

		const dateTo = new Date(to);

		const dateFrom = new Date(from);

		if (to && from) {
			isValid =
				dateFrom < dateTo || dateFrom.getTime() === dateTo.getTime();
		}

		setIsValidDateRange(isValid);
	}, [from, to]);

	const isi18nFilterLabelsValid = (
		i18nFilterLabels: Partial<Liferay.Language.FullyLocalizedValue<string>>
	) => {
		let isValid = true;

		if (!i18nFilterLabels || !Object.values(i18nFilterLabels).length) {
			isValid = false;
		}

		Object.values(i18nFilterLabels).forEach((value) => {
			if (!value) {
				isValid = false;
			}
		});

		return isValid;
	};

	const validate = () => {
		let isValid = true;

		const isLabelValid = isi18nFilterLabelsValid(i18nFilterLabels);
		setLabelValidationError(!isLabelValid);

		isValid = isLabelValid;

		if (!selectedField) {
			setFieldValidationError(true);

			isValid = false;
		}

		if (selectedField && !filter) {
			if (usedFieldNames?.includes(selectedField?.name)) {
				setFieldInUseValidationError(true);

				isValid = false;
			}
		}

		const dateTo = new Date(to);
		const dateFrom = new Date(from);

		if (to && from) {
			const isValidRange =
				dateFrom < dateTo || dateFrom.getTime() === dateTo.getTime();

			setIsValidDateRange(isValidRange);

			if (!isValidRange) {
				isValid = false;
			}
		}

		return isValid;
	};

	const saveDateRangeFilter = () => {
		setSaveButtonDisabled(true);

		const success = validate();

		if (success) {
			const formData = {
				fieldName: selectedField?.name,
				from,
				label_i18n: i18nFilterLabels,
				to,
				type: selectedField?.format,
			};

			onSave(formData);
		}
		else {
			setSaveButtonDisabled(false);
		}
	};

	return (
		<>
			<ClayLayout.SheetSection>
				<Configuration
					fieldInUseValidationError={fieldInUseValidationError}
					fieldValidationError={fieldValidationError}
					fields={fields}
					filter={filter}
					labelValidationError={labelValidationError}
					namespace={namespace}
					onBlur={() => {
						setLabelValidationError(
							!isi18nFilterLabelsValid(i18nFilterLabels)
						);
					}}
					onChangeField={(newValue) => {
						setSelectedField(newValue);
						setFieldValidationError(!newValue);
						setFieldInUseValidationError(
							newValue
								? !!usedFieldNames?.includes(newValue.name)
								: false
						);
					}}
					onChangeLabel={(newValue) => {
						setI18nFilterLabels(newValue);
					}}
					selectedField={selectedField}
				/>

				{!fieldInUseValidationError && (
					<ClayForm.Group className="form-group-autofit">
						<div
							className={classNames('form-group-item', {
								'has-error': !isValidDateRange,
							})}
						>
							<label htmlFor={fromFormElementId}>
								{Liferay.Language.get('from')}
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
								dateFormat="yyyy-MM-dd"
								firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
								inputName={fromFormElementId}
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
								onChange={(value: any) => {
									setFrom(value);
								}}
								placeholder="YYYY-MM-DD"
								value={from}
								weekdaysShort={dateUtils.getWeekdaysShort()}
								years={{
									end: new Date().getFullYear() + 25,
									start: new Date().getFullYear() - 50,
								}}
							/>

							{!isValidDateRange && (
								<ClayForm.FeedbackGroup>
									<ClayForm.FeedbackItem>
										<ClayForm.FeedbackIndicator symbol="exclamation-full" />

										{Liferay.Language.get(
											'date-range-is-invalid.-from-must-be-before-to'
										)}
									</ClayForm.FeedbackItem>
								</ClayForm.FeedbackGroup>
							)}
						</div>

						<div className="form-group-item">
							<label htmlFor={toFormElementId}>
								{Liferay.Language.get('to[date-time]')}
							</label>

							<ClayDatePicker
								dateFormat="yyyy-MM-dd"
								inputName={toFormElementId}
								onChange={(value: any) => {
									setTo(value);
								}}
								placeholder="YYYY-MM-DD"
								value={to}
								years={{
									end: new Date().getFullYear() + 25,
									start: new Date().getFullYear() - 50,
								}}
							/>
						</div>
					</ClayForm.Group>
				)}
			</ClayLayout.SheetSection>

			<Footer
				onCancel={onCancel}
				onSave={saveDateRangeFilter}
				saveButtonDisabled={saveButtonDisabled}
			/>
		</>
	);
}

export default {
	Body,
	Header,
};
