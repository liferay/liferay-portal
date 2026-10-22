import DataValue from 'shared/components/DataValue';
import getCN from 'classnames';
import React from 'react';
import {isNil} from 'lodash';
import {Link} from 'react-router-dom';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';

export default class SourceCell extends React.Component {
	static propTypes = {
		data: PropTypes.shape({
			dataSourceId: PropTypes.string,
			dataSourceName: PropTypes.string
		}),
		groupId: PropTypes.string
	};

	render() {
		const {
			className,
			data: {dataSourceId, dataSourceName},
			groupId
		} = this.props;

		return (
			<td className={getCN('table-cell-expand', className)}>
				{!isNil(dataSourceId) ? (
					<Link
						className='text-truncate'
						to={toRoute(Routes.SETTINGS_DATA_SOURCE, {
							groupId,
							id: dataSourceId
						})}
					>
						<DataValue value={dataSourceName} />
					</Link>
				) : (
					<DataValue value={dataSourceName} />
				)}
			</td>
		);
	}
}
