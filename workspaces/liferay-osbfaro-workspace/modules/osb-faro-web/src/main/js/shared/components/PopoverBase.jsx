import getCN from 'classnames';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {PropTypes} from 'prop-types';

/**
 * Header
 * @param {object} param0
 * @memberof PopoverBase component
 */
const Header = ({children}) => <div className='popover-header'>{children}</div>;

/**
 * Body
 * @param {object} param0
 * @memberof PopoverBase component
 */
const Body = ({children}) => <div className='popover-body'>{children}</div>;

/**
 * Footer
 * @param {object} param0
 * @memberof PopoverBase component
 */
const Footer = ({children}) => <div className='popover-footer'>{children}</div>;

/**
 * Popover Base
 * @class
 */
class PopoverBase extends React.Component {
	static defaultProps = {
		placement: 'none',
		visible: false
	};

	static propTypes = {
		/**
		 * @type {string}
		 * @default undefined
		 */
		placement: PropTypes.oneOf(['bottom', 'left', 'none', 'right', 'top']),

		/**
		 * @type {boolean}
		 * @default false
		 */
		visible: PropTypes.bool
	};

	constructor(props) {
		super(props);

		this._elementRef = React.createRef();
	}

	/**
	 * Lifecycle Render - ReactJS
	 */
	render() {
		const {children, className, placement, visible, ...otherProps} =
			this.props;
		const classes = getCN('popover', className, {
			[`clay-popover-${placement}`]: placement,
			['hide']: !visible
		});

		return (
			<div
				{...omitDefinedProps(otherProps, PopoverBase.propTypes)}
				className={classes}
				hidden={!visible}
				ref={this._elementRef}
			>
				{placement !== 'none' && <div className='arrow' />}
				{children}
			</div>
		);
	}
}

PopoverBase.Header = Header;
PopoverBase.Body = Body;
PopoverBase.Footer = Footer;
export {PopoverBase};
export default PopoverBase;
