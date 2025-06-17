import ClayIcon from '@clayui/icon';
import Item from '../components/Item';
import React from 'react';
import Row from '../components/Row';

const SIZES = ['sm', 'md', 'lg', 'xl', 'xxl', 'xxxl'];

const IconKit = () => (
	<div>
		<Row>
			{SIZES.map((size, index) => (
				<Item key={index}>
					<ClayIcon
						className={`icon-root icon-size-${size}`}
						symbol='ac_star'
					/>
				</Item>
			))}
		</Row>

		<Row>
			{SIZES.map((size, index) => (
				<Item key={index}>
					<ClayIcon
						className={`icon-root icon-size-${size}`}
						symbol='ac_star'
					/>
				</Item>
			))}
		</Row>
	</div>
);

export default IconKit;
