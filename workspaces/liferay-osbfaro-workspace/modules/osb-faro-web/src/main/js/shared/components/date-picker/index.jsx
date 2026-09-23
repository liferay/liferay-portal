import autobind from 'autobind-decorator';
import Calendar from './Calendar';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import DatePickerSelect from './DatePickerSelect';
import getCN from 'classnames';
import moment from 'moment';
import React from 'react';
import TimeSelector from './TimeSelector';
import {isAboveMaxRange, isDateOrRange, isRange, updateRange} from './util';
import {noop, range} from 'lodash';
import {PropTypes} from 'prop-types';
import {sub} from 'shared/util/lang';
import {
	formatDate,
	getMonthNames,
	TIME_FORMAT
} from 'shared/util/date';

export default class DatePicker extends React.Component {
	static defaultProps = {
		disabled: false,
		displayLabel: true,
		minDate: moment().subtract(100, 'years'),
		onSelect: noop,
		showTimeSelector: false
	};

	static propTypes = {
		date: (props, propName, componentName) => {
			if (!isDateOrRange(props[propName])) {
				return new Error(
					`Invalid prop \`${propName}\` supplied to` +
						` \`${componentName}\`. Validation failed.`
				);
			}
		},
		disabled: PropTypes.bool,
		displayLabel: PropTypes.bool,
		header: PropTypes.node,
		maxDate: PropTypes.instanceOf(moment),
		maxRange: PropTypes.number,
		minDate: PropTypes.instanceOf(moment),
		onSelect: PropTypes.func,
		showTimeSelector: PropTypes.bool,
		timeZoneId: PropTypes.string
	};

	state = {
		currentMonth: moment(),
		currentTime: formatDate(moment(), TIME_FORMAT),
		maxRangeError: false
	};

	constructor(props) {
		super(props);

		const {date, maxRange} = this.props;

		if (moment.isMoment(date)) {
			this.state = {
				...this.state,
				currentMonth: date.clone().startOf('month')
			};
		} else {
			this.state = {
				...this.state,
				maxRangeError: isAboveMaxRange(date, maxRange)
			};
		}
	}

	componentDidUpdate(prevProps) {
		const {date} = this.props;

		if (moment.isMoment(date) && !date.isSame(prevProps.date)) {
			this.setState({
				currentMonth: this.props.date.clone().startOf('month')
			});
		}
	}

	@autobind
	handleCurrentMonth() {
		this.setState({
			currentMonth: moment().startOf('month')
		});
	}

	@autobind
	handleMonthSelect(event) {
		const {value} = event.target;
		const {currentMonth} = this.state;

		this.setState({
			currentMonth: currentMonth
				.clone()
				.month(Number(value))
		});
	}

	@autobind
	handleNextMonth() {
		this.updateCurrentMonth(1);
	}

	@autobind
	handlePrevMonth() {
		this.updateCurrentMonth(-1);
	}

	@autobind
	handleSelect(newDate) {
		const {date, maxRange, onSelect} = this.props;

		const newValue = isRange(date) ? updateRange(date, newDate) : newDate;

		const maxRangeError = isAboveMaxRange(newValue, maxRange);

		this.setState({maxRangeError});

		if (!isRange(date) || !maxRange || !maxRangeError) {
			onSelect(newValue);
		}
	}

	@autobind
	handleTimeChange(newTime) {
		const {date} = this.props;

		const [hour, minute] = newTime.split(':');

		const newDate = date || moment();

		this.handleSelect(newDate.clone().set({hour, minute}));
	}

	@autobind
	handleYearSelect(event) {
		const {value} = event.target;
		const {currentMonth} = this.state;

		this.setState({
			currentMonth: currentMonth.clone().year(value)
		});
	}

	@autobind
	isNextDisabled() {
		const {
			props: {maxDate},
			state: {currentMonth}
		} = this;

		return !!maxDate && maxDate.isSame(currentMonth, 'month');
	}

	@autobind
	isPrevDisabled() {
		const {
			props: {minDate},
			state: {currentMonth}
		} = this;

		return !!minDate && !minDate.isBefore(currentMonth);
	}

	isCurrentDisabled() {
		const {currentMonth} = this.state;

		return currentMonth.isSame(moment().startOf('month'));
	}

	updateCurrentMonth(diff) {
		this.setState({
			currentMonth: this.state.currentMonth.clone().add(diff, 'months')
		});
	}

	render() {
		const {
			props: {
				className,
				date,
				disabled,
				displayLabel,
				header,
				maxDate,
				maxRange,
				minDate,
				showTimeSelector,
				timeZoneId
			},
			state: {currentMonth, maxRangeError}
		} = this;

		const currentYear = moment().year();

		const endYear = maxDate && maxDate.year();
		const startYear = minDate.year();

		const classes = getCN('date-picker-root', {
			disabled
		});

		return (
			<div aria-disabled={disabled} className={getCN(classes, className)}>
				{header && (
					<div className='picker-header picker-header--border'>
						{header}
					</div>
				)}

				{displayLabel && isRange(date) && (
					<div className='picker-header'>
						<label>
							{date.start && !date.end
								? Liferay.Language.get('end-date')
								: Liferay.Language.get('start-date')}
						</label>
					</div>
				)}

				<div className='controls'>
					<div className='month-toggle-wrapper'>
						<DatePickerSelect
							onChange={this.handleMonthSelect}
							options={getMonthNames().map((month, index) => ({
								label: month,
								value: index
							}))}
							selected={String(currentMonth.month())}
						/>

						<DatePickerSelect
							onChange={this.handleYearSelect}
							options={range(
								endYear || currentYear + 5,
								Math.min(startYear - 1, currentYear - 1),
								-1
							).map(year => ({
								label: year,
								value: year
							}))}
							selected={currentMonth.format('YYYY')}
						/>
					</div>

					<ClayButton
						aria-label={Liferay.Language.get('previous-month')}
						className='button-root'
						data-testid='previous-month'
						disabled={this.isPrevDisabled()}
						displayType='secondary'
						monospaced
						onClick={this.handlePrevMonth}
						size='sm'
					>
						<ClayIcon
							className='icon-root'
							symbol='angle-left-small'
						/>
					</ClayButton>

					<ClayButton
						className='button-root current-day-btn'
						disabled={this.isCurrentDisabled()}
						displayType='secondary'
						monospaced
						onClick={this.handleCurrentMonth}
						size='sm'
					>
						{'•'}
					</ClayButton>

					<ClayButton
						aria-label={Liferay.Language.get('next-month')}
						className='button-root'
						data-testid='next-month'
						disabled={this.isNextDisabled()}
						displayType='secondary'
						monospaced
						onClick={this.handleNextMonth}
						size='sm'
					>
						<ClayIcon
							className='icon-root'
							symbol='angle-right-small'
						/>
					</ClayButton>
				</div>

				<div className='picker-body'>
					<Calendar
						currentMonth={currentMonth}
						date={date}
						maxDate={maxDate}
						minDate={minDate}
						onSelect={this.handleSelect}
					/>
				</div>

				{!isRange(date) && showTimeSelector && (
					<div className='picker-footer'>
						<TimeSelector
							onChange={this.handleTimeChange}
							timeZoneId={timeZoneId}
							value={date}
						/>
					</div>
				)}

				{maxRangeError && (
					<div className='range-warning'>
						<ClayIcon className='icon-root' symbol='warning' />

						{sub(
							Liferay.Language.get(
								'this-exceeds-the-maximum-range-of-x-days'
							),
							[maxRange]
						)}
					</div>
				)}
			</div>
		);
	}
}
