import DataValue from 'shared/components/DataValue';
import moment from 'moment';
import React from 'react';
import {get} from 'lodash';
import {formatDate, getCustomDateFormat} from 'shared/util/date';
import {PropTypes} from 'prop-types';

export default class DateRenderer extends React.Component {
	static defaultProps = {
		dateFormatter: date => formatDate(moment(date), getCustomDateFormat()),
		datePath: 'dateCreated'
	};

	static propTypes = {
		data: PropTypes.object.isRequired,
		dateFormatter: PropTypes.func,
		datePath: PropTypes.oneOfType([PropTypes.array, PropTypes.string])
	};

	render() {
		const {className, data, dateFormatter, datePath} = this.props;

		const date = get(data, datePath);

		return (
			<td className={className}>
				<DataValue format={dateFormatter} value={date} />
			</td>
		);
	}
}
