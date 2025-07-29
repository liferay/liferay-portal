/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayDatePicker from '@clayui/date-picker';
import {dateUtils, navigate} from 'frontend-js-web';
import React from 'react';

import ChangeTrackingBaseScheduleView from './ChangeTrackingBaseScheduleView';

class ChangeTrackingRescheduleView extends ChangeTrackingBaseScheduleView {
	constructor(props) {
		super(props);

		const {
			redirect,
			rescheduleURL,
			scheduledDate,
			scheduledTime,
			spritemap,
			timeZone,
			unscheduleURL,
		} = props;

		this.redirect = redirect;
		this.rescheduleURL = rescheduleURL;
		this.spritemap = spritemap;
		this.timeZone = timeZone;
		this.unscheduleURL = unscheduleURL;

		this.state = {
			date: scheduledDate,
			dateError: '',
			formError: null,
			time: scheduledTime,
			timeError: '',
			validationError: null,
		};
	}

	render() {
		return (
			<div className="sheet sheet-lg">
				<div className="sheet-header">
					<h2 className="sheet-title">
						{Liferay.Language.get('reschedule-publication')}
					</h2>
				</div>

				<div className="sheet-section">
					<label>{Liferay.Language.get('date-and-time')}</label>

					<div className="input-group">
						<div className={this.getDateClassName()}>
							<div>
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
									onValueChange={this.handleDateChange}
									placeholder="YYYY-MM-DD"
									spritemap={this.spritemap}
									timezone={this.timeZone}
									value={
										typeof this.state.date === 'string'
											? this.state.date
											: this.state.date.getFullYear() +
												'-' +
												this.pad(
													this.state.date.getMonth() +
														1
												) +
												'-' +
												this.pad(
													this.state.date.getDate()
												)
									}
									weekdaysShort={dateUtils.getWeekdaysShort()}
									years={{
										end: new Date().getFullYear() + 1,
										start: new Date().getFullYear() - 1,
									}}
								/>

								{this.getDateHelpText()}
							</div>
						</div>

						<div className={this.getTimeClassName()}>
							<div>
								<input
									className="form-control"
									onChange={(event) =>
										this.handleTimeChange(
											event.target.value
										)
									}
									type="time"
									value={this.state.time}
								/>

								{this.getTimeHelpText()}
							</div>

							<div className="input-group-item input-group-item-shrink">
								<span className="input-group-text">
									({this.timeZone})
								</span>
							</div>
						</div>
					</div>
				</div>

				{this.state.formError && (
					<ClayAlert
						displayType="danger"
						spritemap={this.spritemap}
						title={this.state.formError}
					/>
				)}

				<div className="sheet-footer sheet-footer-btn-block-sm-down">
					<div className="btn-group">
						<div className="btn-group-item">
							<button
								className="btn btn-primary"
								onClick={() =>
									this.doSchedule(this.rescheduleURL)
								}
								type="button"
							>
								{Liferay.Language.get('reschedule')}
							</button>
						</div>

						<div className="btn-group-item">
							<button
								className="btn btn-secondary"
								onClick={() =>
									submitForm(
										document.hrefFm,
										this.unscheduleURL
									)
								}
								type="button"
							>
								{Liferay.Language.get('unschedule')}
							</button>
						</div>

						<div className="btn-group-item">
							<button
								className="btn btn-outline-borderless btn-secondary"
								onClick={() => navigate(this.redirect)}
								type="button"
							>
								{Liferay.Language.get('cancel')}
							</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default function (props) {
	return <ChangeTrackingRescheduleView {...props} />;
}
