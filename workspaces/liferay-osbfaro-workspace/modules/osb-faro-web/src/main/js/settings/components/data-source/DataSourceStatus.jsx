import ClayLabel from '@clayui/label';
import Panel from 'shared/components/Panel';
import React from 'react';
import {PropTypes} from 'prop-types';

export default class DataSourceStatus extends React.Component {
	static propTypes = {
		display: PropTypes.string.isRequired,
		label: PropTypes.string.isRequired,
		message: PropTypes.oneOfType([PropTypes.array, PropTypes.string])
			.isRequired
	};

	render() {
		const {display, label, message} = this.props;

		return (
			<Panel
				className={`data-source-status-root${
					this.props.className ? ` ${this.props.className}` : ''
				}`}
				label={
					<ClayLabel
						displayType={display}
						inverse
						size='lg'
					>
						{label}
					</ClayLabel>
				}
				title={
					<div>{Liferay.Language.get('current-status-colon')}</div>
				}
			>
				{message}
			</Panel>
		);
	}
}
