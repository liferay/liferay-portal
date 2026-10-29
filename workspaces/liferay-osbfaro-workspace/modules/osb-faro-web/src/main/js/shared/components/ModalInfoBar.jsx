import getCN from 'classnames';
import React from 'react';

class ModalInfoBar extends React.Component {
	render() {
		return (
			<div
				className={getCN(
					'bg-light d-flex font-weight-semi-bold justify-content-end px-4 text-capitalize text-secondary',
					this.props.className
				)}
				style={{fontSize: 12}}
			>
				{this.props.children}
			</div>
		);
	}
}

export default ModalInfoBar;
