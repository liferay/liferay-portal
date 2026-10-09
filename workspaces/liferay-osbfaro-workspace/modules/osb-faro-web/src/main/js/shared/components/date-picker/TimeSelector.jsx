import autobind from 'autobind-decorator';
import ClayIcon from 'shared/components/Icon';
import Input from 'shared/components/Input';
import moment from 'moment-timezone';
import PropTypes from 'prop-types';
import React from 'react';
import {applyTimeZone} from 'shared/util/date';

export default class TimeSelector extends React.Component {
	static defaultProps = {
		value: ''
	};

	static propTypes = {
		onChange: PropTypes.func,
		timeZoneId: PropTypes.string,
		value: PropTypes.any
	};

	@autobind
	handleChange(event) {
		const {value} = event.target;
		const {onChange} = this.props;

		onChange(value);
	}

	render() {
		const {timeZoneId, value} = this.props;

		const timezoneOffset = applyTimeZone(value, timeZoneId).format('Z');

		return (
			<div className='time-selector-root'>
				<ClayIcon className='icon-root' symbol='time' />

				<Input
					onChange={this.handleChange}
					type='time'
					value={
						moment.isMoment(value) ? value.format('HH:mm') : value
					}
				/>

				<div className='time-zone'>{`(GMT ${timezoneOffset})`}</div>
			</div>
		);
	}
}
