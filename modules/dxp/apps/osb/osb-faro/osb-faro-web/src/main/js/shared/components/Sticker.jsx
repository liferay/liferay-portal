import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {EntityTypes} from '../util/constants';
import {isString} from 'lodash';
import {PropTypes} from 'prop-types';

const DISPLAYS = [
	'primary',
	'secondary',
	'success',
	'info',
	'warning',
	'danger',
	'light',
	'dark',
	'point',
	'boolean',
	'number',
	'date',
	'string'
];

const ID_DISPLAYS = [
	'chartBlue',
	'chartLimeGreen',
	'chartOrange',
	'chartRed',
	'chartSeaGreen'
];

const SIZES = ['xs', 'sm', 'lg', 'xl', 'xxl'];

class Sticker extends React.Component {
	static defaultProps = {
		circle: false
	};

	static propTypes = {
		circle: PropTypes.bool,
		className: PropTypes.string,
		display: PropTypes.string,
		size: PropTypes.oneOf(SIZES),
		symbol: PropTypes.string
	};

	render() {
		const {
			children,
			circle,
			className,
			display,
			size,
			symbol,
			...otherProps
		} = this.props;

		const classes = getCN(
			'sticker',
			'sticker-root',
			'sticker-static',
			className,
			{
				'sticker-circle': circle,
				'sticker-rounded': !circle,
				[`sticker-${display}`]: display,
				[`sticker-${size}`]: size
			}
		);

		return (
			<div
				{...omitDefinedProps(otherProps, Sticker.propTypes)}
				className={classes}
			>
				{symbol && <ClayIcon className='icon-root' symbol={symbol} />}

				{children}
			</div>
		);
	}
}

export function getSymbol(type) {
	switch (type) {
		case EntityTypes.Account:
		case EntityTypes.DataSource:
			return 'document';
		case EntityTypes.IndividualsSegment:
			return 'faro_contacts_segments';
		case EntityTypes.Individual:
		default:
			return 'faro_contacts_individuals';
	}
}

export function hashCode(s) {
	return s.split('').reduce((a, b) => a + b.charCodeAt(0), 0);
}

/**
 * Returns a string from an array of display name strings based
 * on the numericized id
 * @param {string|number} id - Id of the item.
 * @param {Array} displays - Array of display name strings.
 * @returns {string} - Display name from displays
 */
export function getDisplayForId(id = 0, displays = ID_DISPLAYS) {
	if (isString(id)) {
		id = hashCode(id);
	}

	return displays[id % displays.length];
}

Sticker.DISPLAYS = DISPLAYS;
Sticker.SIZES = SIZES;
export default Sticker;
