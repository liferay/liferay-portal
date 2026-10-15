import ClayLabel from '@clayui/label';
import React from 'react';
import {PropTypes} from 'prop-types';
import {UserStatuses} from 'shared/util/constants';

const STATUS_LANG_MAP = {
	[UserStatuses.Approved]: Liferay.Language.get('approved'),
	[UserStatuses.Pending]: Liferay.Language.get('pending')
};

class StatusRenderer extends React.Component {
	static defaultProps = {
		data: {}
	};

	static propTypes = {
		data: PropTypes.object
	};

	getDisplayType(status) {
		switch (status) {
			case UserStatuses.Approved:
				return 'success';
			case UserStatuses.Pending:
				return 'warning';
			default:
				return 'secondary';
		}
	}

	render() {
		const {
			className,
			data: {status}
		} = this.props;

		const displayType = this.getDisplayType(status);

		return (
			<td className={className}>
				<ClayLabel displayType={displayType} inverse>
					{STATUS_LANG_MAP[status]}
				</ClayLabel>
			</td>
		);
	}
}

export default StatusRenderer;
