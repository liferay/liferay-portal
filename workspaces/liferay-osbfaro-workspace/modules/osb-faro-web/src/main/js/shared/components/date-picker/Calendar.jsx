import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import getCN from 'classnames';
import moment from 'moment';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {chunk, noop, range} from 'lodash';
import {isDateOrRange, isInRange, isRange} from './util';
import {PropTypes} from 'prop-types';
import {getShortWeekdayNames} from 'shared/util/date';

const FIVE_ROWS = 35;
const SIX_ROWS = 42;

/**
 * Determines if a date has been selected by the range as either the
 * start or the end.
 * @param {Moment|Range} dateOrRange - The range to check in.
 * @param {Moment} date - The date.
 * @returns {boolean}
 */
export function isSelected(dateOrRange, date) {
	if (moment.isMoment(dateOrRange)) {
		return dateOrRange.clone().startOf('day').isSame(date);
	}

	const {end, start} = dateOrRange;

	return (!!start && start.isSame(date)) || (!!end && end.isSame(date));
}

/**
 * Determines if the hovered date is before the range start date.
 * @param {Moment|Range} dateOrRange - The range to check against.
 * @param {Moment} hoveredDated - The currently hovered date.
 * @returns {boolean}
 */
export function isHoverDateBeforeStartDate(dateOrRange, hoveredDate) {
	if (moment.isMoment(dateOrRange)) {
		return false;
	}

	const {start} = dateOrRange;

	return !!start && start.isAfter(hoveredDate);
}

/**
 * Determines if the date is in between the range start and hoveredDate.
 * @param {Range} range - The range to check in.
 * @param {Moment} date - The date to check.
 * @param {Moment} hoveredDated - The currently hovered date.
 * @returns {boolean}
 */
export function isInHoverRange(range, date, hoveredDate) {
	if (!range.start) {
		return false;
	}

	const {start} = range;

	if (isHoverDateBeforeStartDate(range, hoveredDate)) {
		return date.isSameOrBefore(start) && date.isSameOrAfter(hoveredDate);
	}

	return date.isSameOrAfter(start) && date.isSameOrBefore(hoveredDate);
}

/**
 * Determines if a date is the start of the range.
 * @param {Moment|Range} dateOrRange - The range to check in.
 * @param {Moment} date - The date.
 * @returns {boolean}
 */
export function isStart(dateOrRange, date) {
	if (moment.isMoment(dateOrRange)) {
		return false;
	}

	const {start} = dateOrRange;

	return !!start && start.isSame(date);
}

/**
 * Determines if a date is the end of the range.
 * @param {Moment|Range} dateOrRange - The range to check in.
 * @param {Moment} date - The date.
 * @returns {boolean}
 */
export function isEnd(dateOrRange, date) {
	if (moment.isMoment(dateOrRange)) {
		return false;
	}

	const {end} = dateOrRange;

	return !!end && end.isSame(date);
}

class Day extends React.Component {
	static defaultProps = {
		isSelectingEndDate: false,
		onSelect: noop,
		outsideMonth: false,
		selected: false
	};

	static propTypes = {
		date: PropTypes.instanceOf(moment).isRequired,
		isSelectingEndDate: PropTypes.bool,
		onSelect: PropTypes.func,
		onSetHoveredDate: PropTypes.func,
		outsideMonth: PropTypes.bool,
		selected: PropTypes.bool
	};

	@autobind
	handleClick() {
		const {date, isSelectingEndDate, onSelect, selected} = this.props;

		if (!isSelectingEndDate || !selected) {
			onSelect(date);
		}
	}

	@autobind
	handleMouseOver() {
		const {date, onSetHoveredDate} = this.props;

		onSetHoveredDate(date);
	}

	render() {
		const {className, date, outsideMonth, selected, ...otherProps} =
			this.props;

		const classes = getCN('button-root day-root', className, {
			'outside-month': outsideMonth,
			selected
		});

		return (
			<ClayButton
				className={classes}
				displayType='unstyled'
				onClick={this.handleClick}
				onFocus={this.handleMouseOver}
				onMouseOver={this.handleMouseOver}
				{...omitDefinedProps(otherProps, Day.propTypes)}
			>
				{date.date()}
			</ClayButton>
		);
	}
}

export default class Calendar extends React.Component {
	static defaultProps = {
		onSelect: noop
	};

	static propTypes = {
		currentMonth: PropTypes.instanceOf(moment).isRequired,
		date: (props, propName, componentName) => {
			if (!isDateOrRange(props[propName])) {
				return new Error(
					`Invalid prop \`${propName}\` supplied to` +
						` \`${componentName}\`. Validation failed.`
				);
			}
		},
		maxDate: PropTypes.instanceOf(moment),
		minDate: PropTypes.instanceOf(moment),
		onSelect: PropTypes.func
	};

	state = {
		hoveredDate: null
	};

	getDateGrid() {
		const {currentMonth} = this.props;

		const month = currentMonth.clone().startOf('month');

		const daysInMonth = month.daysInMonth();
		const firstDay = month.day();

		const cells = firstDay + daysInMonth > 35 ? SIX_ROWS : FIVE_ROWS;

		const dates = range(1, cells + 1).map(i => {
			if (i < firstDay + 1) {
				return {
					date: month.clone().subtract(firstDay + 1 - i, 'days'),
					outsideMonth: true
				};
			} else if (i - firstDay > daysInMonth) {
				return {
					date: month
						.clone()
						.add(1, 'months')
						.add(i - firstDay - daysInMonth - 1, 'days'),
					outsideMonth: true
				};
			} else {
				return {
					date: month.clone().add(i - firstDay - 1, 'days'),
					outsideMonth: false
				};
			}
		});

		return chunk(dates, 7).map((row, i) => ({
			key: `${currentMonth.format()}_${i}`,
			row
		}));
	}

	@autobind
	handleSetHoveredDate(date) {
		const {date: dateOrRange} = this.props;

		if (dateOrRange && dateOrRange.start) {
			this.setState({hoveredDate: date});
		}
	}

	highlightEnd(date) {
		const {
			props: {date: dateOrRange},
			state: {hoveredDate}
		} = this;

		const rangeComplete = dateOrRange.end && dateOrRange.start;

		const hoverDateBeforeStartDate = isHoverDateBeforeStartDate(
			dateOrRange,
			hoveredDate
		);

		if (rangeComplete) {
			return isEnd(dateOrRange, date);
		} else if (hoverDateBeforeStartDate) {
			return isStart(dateOrRange, date);
		} else {
			return date.isSame(hoveredDate);
		}
	}

	highlightStart(date) {
		const {
			props: {date: dateOrRange},
			state: {hoveredDate}
		} = this;

		const rangeComplete = dateOrRange.end && dateOrRange.start;

		const hoverDateBeforeStartDate = isHoverDateBeforeStartDate(
			dateOrRange,
			hoveredDate
		);

		if (rangeComplete || !hoverDateBeforeStartDate) {
			return isStart(dateOrRange, date);
		} else {
			return date.isSame(hoveredDate);
		}
	}

	render() {
		const {
			props: {className, date: dateOrRange, maxDate, minDate, onSelect},
			state: {hoveredDate}
		} = this;

		const range = isRange(dateOrRange);
		const rangeComplete = range && dateOrRange.end && dateOrRange.start;

		return (
			<table className={getCN('calendar-root', {className})}>
				<thead>
					<tr>
						{getShortWeekdayNames().map(day => (
							<th key={day}>{day}</th>
						))}
					</tr>
				</thead>

				<tbody>
					{this.getDateGrid().map(({key, row}) => (
						<tr key={key}>
							{row.map(({date, outsideMonth}) => (
								<td
									className={getCN({
										'end-range':
											range && this.highlightEnd(date),
										hover:
											range &&
											!rangeComplete &&
											isInHoverRange(
												dateOrRange,
												date,
												hoveredDate
											),
										'in-range':
											range &&
											isInRange(dateOrRange, date),
										'start-range':
											range && this.highlightStart(date)
									})}
									key={date.format()}
								>
									<div className='day-container'>
										<Day
											date={date}
											disabled={
												(minDate &&
													date.isBefore(minDate)) ||
												(maxDate &&
													date.isAfter(maxDate))
											}
											isSelectingEndDate={
												range &&
												!!dateOrRange.start &&
												!dateOrRange.end
											}
											onSelect={onSelect}
											onSetHoveredDate={
												this.handleSetHoveredDate
											}
											outsideMonth={outsideMonth}
											selected={
												dateOrRange &&
												isSelected(dateOrRange, date)
											}
										/>
									</div>
								</td>
							))}
						</tr>
					))}
				</tbody>
			</table>
		);
	}
}
