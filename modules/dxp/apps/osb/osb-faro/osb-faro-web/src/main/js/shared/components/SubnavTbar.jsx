import getCN from 'classnames';
import React from 'react';
import {PropTypes} from 'prop-types';

const DISPLAYS = ['primary', ''];

class TbarItem extends React.Component {
	static defaultProps = {
		expand: false
	};

	static propTypes = {
		expand: PropTypes.bool
	};

	render() {
		const {children, className, expand} = this.props;

		const classes = getCN('tbar-item', className, {
			'tbar-item-expand': expand
		});

		return (
			<div className={classes}>
				<TbarSection>{children}</TbarSection>
			</div>
		);
	}
}

class TbarSection extends React.Component {
	render() {
		const {children, className} = this.props;
		return (
			<div className={getCN('tbar-section', 'text-truncate', className)}>
				{children}
			</div>
		);
	}
}

class SubnavTbar extends React.Component {
	static defaultProps = {
		display: ''
	};

	static propTypes = {
		className: PropTypes.string,
		display: PropTypes.oneOf(DISPLAYS)
	};

	render() {
		const {children, className, display} = this.props;

		const classes = getCN(
			className,
			'mb-3',
			'subnav-tbar-root',
			'subnav-tbar',
			'tbar',
			{
				'subnav-tbar-primary': display === 'primary'
			}
		);

		return (
			<nav className={classes}>
				<div className='container-fluid'>
					<div className='tbar-nav'>{children}</div>
				</div>
			</nav>
		);
	}
}

SubnavTbar.Item = TbarItem;
SubnavTbar.Section = TbarSection;

export default SubnavTbar;
