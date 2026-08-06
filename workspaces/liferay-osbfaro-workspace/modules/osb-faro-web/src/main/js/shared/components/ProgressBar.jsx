import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React from 'react';
import {formatPercent} from 'shared/util/numbers';
import {PropTypes} from 'prop-types';

class ProgressBar extends React.Component {
	static defaultProps = {
		complete: false,
		error: false,
		value: 0
	};

	static propTypes = {
		complete: PropTypes.bool,
		error: PropTypes.bool,
		value: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
	};

	renderProgressInfo() {
		const {complete, error, value} = this.props;

		if (complete && !error) {
			return (
				<div className='progress-group-feedback'>
					<ClayIcon className='icon-root' symbol='check-circle' />
				</div>
			);
		} else if (error) {
			return (
				<div className='progress-group-feedback'>
					<ClayIcon className='icon-root' symbol='times-circle' />
				</div>
			);
		} else {
			return (
				((complete && error) || (!complete && !error)) &&
				formatPercent(Math.ceil(value), 0)
			);
		}
	}

	render() {
		const {className, complete, error, value} = this.props;

		const classes = getCN('progress-bar-root', 'progress-group', {
			'progress-danger': error,
			'progress-success': complete
		});

		const width = complete ? '100%' : `${value}%`;

		return (
			<div className={getCN(classes, className)}>
				<div className='progress'>
					<div
						className='progress-bar'
						role='progressbar'
						style={{width}}
					/>
				</div>

				<div className='progress-group-addon'>
					{this.renderProgressInfo()}
				</div>
			</div>
		);
	}
}

export default ProgressBar;
