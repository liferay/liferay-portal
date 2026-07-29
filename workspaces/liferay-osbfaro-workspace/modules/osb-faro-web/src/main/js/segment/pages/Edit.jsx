import DynamicSegment from './edit/Dynamic';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {get} from 'lodash';
import {optional} from 'shared/hoc';
import {PropTypes} from 'prop-types';
import {Segment} from 'shared/util/records';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {withSegment} from 'shared/hoc/WithSegment';

export class Edit extends React.Component {
	static defaultProps = {
		type: SegmentTypes.Batch
	};

	static propTypes = {
		category: PropTypes.oneOf([
			SegmentCategories.Account,
			SegmentCategories.Individual
		]),
		segment: PropTypes.instanceOf(Segment),
		type: PropTypes.oneOf([SegmentTypes.RealTime, SegmentTypes.Batch])
	};

	render() {
		const {category, segment, type, ...otherProps} = this.props;

		const segmentType = get(segment, 'segmentType') || type;

		const segmentCategory =
			get(segment, 'segmentCategory') ||
			category ||
			SegmentCategories.Individual;

		if (segmentType) {
			return (
				<DynamicSegment
					{...omitDefinedProps(otherProps, Edit.propTypes)}
					segment={segment}
					segmentCategory={segmentCategory}
					type={segmentType}
				/>
			);
		}
	}
}

export default optional(withSegment(true))(Edit);
