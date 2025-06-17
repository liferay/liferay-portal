import React from 'react';
import Sticker from 'shared/components/Sticker';
import {SegmentStates, SegmentTypes} from 'shared/util/constants';

export default ({segmentType, state}) => {
	const disabled = state === SegmentStates.Disabled;

	const getSymbol = () => {
		if (disabled) {
			return 'warning';
		} else if (segmentType === SegmentTypes.Static) {
			return 'individual_static_segment';
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
