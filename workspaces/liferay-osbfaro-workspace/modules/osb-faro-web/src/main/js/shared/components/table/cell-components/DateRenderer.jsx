import moment from 'moment';
import React from 'react';
import {get} from 'lodash';
import {getCustomDateFormat} from 'shared/util/date';
import {PropTypes} from 'prop-types';

export default class DateRenderer extends React.Component {
	static defaultProps = {
		dateFormatter: date => moment(date).format(getCustomDateFormat()),
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

		const formattedDate = date ? dateFormatter(date) : '-';

		return <td className={className}>{formattedDate}</td>;
	}
}
