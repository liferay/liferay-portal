import CompositionCard from 'segment/components/CompositionCard';
import CriteriaCard from 'segment/components/criteria-card';

import DistributionCard from 'contacts/hoc/segment/DistributionCard';
import InterestsCard from 'contacts/hoc/segment/InterestsCard';
import React, {useCallback, useEffect, useRef} from 'react';
import SegmentActivationCard from 'segment/components/SegmentActivationCard';
import SegmentProfileCard from 'segment/components/ProfileCard';
import {debounce} from 'lodash';
import {ReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface IOverviewProps {
	channelId: string;
	groupId: string;
	segment: Segment;
}

const HEADER_MARGIN = 16;

const Overview: React.FC<IOverviewProps> = ({channelId, groupId, segment}) => {
	const {
		activation,
		activeIndividualCount,
		criteriaString,
		id,
		includeAnonymousUsers,
		individualCount,
		knownIndividualCount,
		sequential,
	} = segment;
	const {timeZoneId} = useTimeZone();

	const _sideColumnRef = useRef<any>();

	const updateHeaderVisible = useCallback(
		debounce(() => {
			const node = _sideColumnRef.current;
			if (node) {
				const {top} = node.parentElement.getBoundingClientRect();
				const headerSize = top > HEADER_MARGIN ? top : HEADER_MARGIN;
				node.style.maxHeight = `calc(100vh - ${headerSize}px)`;
			}
		}, 250),
		[]
	);

	useEffect(() => {
		updateHeaderVisible();
		window.addEventListener('scroll', updateHeaderVisible);
		return () => window.removeEventListener('scroll', updateHeaderVisible);
	}, []);

	return (
		<div className="overview-layout">
			<div className="overview-column-main">
				{activation && (
					<SegmentActivationCard segmentActivation={activation} />
				)}

				<SegmentProfileCard
					channelId={channelId}
					groupId={groupId}
					id={id}
					segment={segment}
				/>

				<InterestsCard
					channelId={channelId}
					groupId={groupId}
					id={id}
				/>

				<DistributionCard
					channelId={channelId}
					groupId={groupId}
					id={id}
				/>
			</div>

			<div className="overview-column-side" ref={_sideColumnRef}>
				<ReferencedObjectsProvider segment={segment}>
					<CriteriaCard
						channelId={channelId}
						criteriaString={criteriaString ?? ''}
						groupId={groupId}
						includeAnonymousUsers={includeAnonymousUsers}
						segmentType={SegmentTypes.Batch}
						sequential={sequential}
						timeZoneId={timeZoneId}
					/>
				</ReferencedObjectsProvider>

				<CompositionCard
					activeIndividualCount={activeIndividualCount}
					individualCount={individualCount}
					knownIndividualCount={knownIndividualCount}
				/>
			</div>
		</div>
	);
};

export default Overview;
