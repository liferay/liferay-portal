import DynamicSegment from './edit/Dynamic';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {get} from 'lodash';
import {optional} from 'shared/hoc';
import {PropTypes} from 'prop-types';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {withSegment} from 'shared/hoc/WithSegment';

export class Edit extends React.Component {
	static defaultProps = {
		type: SegmentTypes.Dynamic
	};

	static propTypes = {
		segment: PropTypes.instanceOf(Segment)
	};

	render() {
		const {segment, ...otherProps} = this.props;

		const segmentType = get(segment, 'segmentType') || SegmentTypes.Dynamic;

		if (segmentType) {
			return (
				<DynamicSegment
					{...omitDefinedProps(otherProps, Edit.propTypes)}
					segment={segment}
					type={segmentType}
				/>
			);
		}
	}
}

export default optional(withSegment(true))(Edit);
