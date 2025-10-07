import React from 'react';
import Sticker from 'shared/components/Sticker';
import {SegmentStates} from 'shared/util/constants';

export default ({state}) => {
	const disabled = state === SegmentStates.Disabled;

	const getSymbol = () => {
		if (disabled) {
			return 'warning';
		} else {
			return 'individual_dynamic_segment';
		}
	};

	return (
		<Sticker
			className='segment-sticker-root'
			display={disabled ? 'warning' : 'dark'}
			symbol={getSymbol()}
		/>
	);
};
