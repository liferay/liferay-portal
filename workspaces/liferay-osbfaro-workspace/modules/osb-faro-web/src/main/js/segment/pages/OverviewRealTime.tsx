import CriteriaCard from 'segment/components/criteria-card';
import React from 'react';
import {ReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface IOverviewProps {
	channelId: string;
	groupId: string;
	segment: Segment;
}

const RealTimeSegmentOverview: React.FC<IOverviewProps> = ({
	channelId,
	groupId,
	segment,
}) => {
	const {timeZoneId} = useTimeZone();

	return (
		<ReferencedObjectsProvider segment={segment}>
			<CriteriaCard
				channelId={channelId}
				criteriaString={segment.criteriaString ?? ''}
				groupId={groupId}
				includeAnonymousUsers={segment.includeAnonymousUsers}
				segmentType={SegmentTypes.RealTime}
				sequential={segment.sequential}
				timeZoneId={timeZoneId}
			/>
		</ReferencedObjectsProvider>
	);
};

export default RealTimeSegmentOverview;
