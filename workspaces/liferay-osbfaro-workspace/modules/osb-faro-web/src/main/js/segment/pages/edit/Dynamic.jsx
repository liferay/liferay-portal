import React from 'react';
import SegmentEditor from 'segment/segment-editor/dynamic';
import withBaseEdit from 'contacts/hoc/segment/WithBaseEdit';
import withPropertyGroups from 'segment/segment-editor/dynamic/hoc/WithPropertyGroups';
import {compose} from 'shared/hoc';
import {get} from 'lodash';
import {SegmentTypes} from 'shared/util/constants';
import {useQueryParams} from 'shared/hooks/useQueryParams';

const DynamicSegmentEdit = props => {
	const params = useQueryParams();
	const {id, propertyGroupsIList, segment} = props;

	// Default to a batch segment when neither an existing segment nor the URL
	// declares a type; only an explicit type=REAL_TIME yields a real-time
	// segment.

	const segmentType =
		get(segment, 'segmentType') || params.type || SegmentTypes.Batch;

	return (
		<SegmentEditor
			{...props}
			id={id}
			propertyGroupsIList={propertyGroupsIList}
			segment={segment}
			type={segmentType}
		/>
	);
};

export default compose(withPropertyGroups, withBaseEdit)(DynamicSegmentEdit);
