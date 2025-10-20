import CompositionCard from 'segment/components/CompositionCard';
import CriteriaCard from 'segment/components/criteria-card';
import DistributionCard from 'contacts/hoc/segment/DistributionCard';
import InterestsCard from 'contacts/hoc/segment/InterestsCard';
import React, {useCallback, useEffect, useRef} from 'react';
import SegmentProfileCard from 'segment/components/ProfileCard';
import {connect, ConnectedProps} from 'react-redux';
import {debounce} from 'lodash';
import {RootState} from 'shared/store';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';

const HEADER_MARGIN = 16;
const connector = connect((store: RootState, {groupId}: {groupId: string}) => ({
	timeZoneId: store.getIn([
		'projects',
		groupId,
		'data',
		'timeZone',
		'timeZoneId'
	])
}));

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IOverviewProps extends PropsFromRedux {
	channelId: string;
	groupId: string;
	id: string;
	segment: Segment;
	tabId?: string;
}

const Overview: React.FC<IOverviewProps> = ({
	channelId,
	groupId,
	id,
	segment,
	timeZoneId
}) => {
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

	const {
		activeIndividualCount,
		criteriaString,
		includeAnonymousUsers,
		individualCount,
		knownIndividualCount,
		segmentType
	} = segment;

	return (
		<div className='overview-layout'>
			<div className='overview-column-main'>
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

			<div className='overview-column-side' ref={_sideColumnRef}>
				<CompositionCard
					activeIndividualCount={activeIndividualCount}
					individualCount={individualCount}
					knownIndividualCount={knownIndividualCount}
				/>

				{segmentType === SegmentTypes.Batch && (
					<CriteriaCard
						criteriaString={criteriaString}
						includeAnonymousUsers={includeAnonymousUsers}
						segment={segment}
						timeZoneId={timeZoneId}
					/>
				)}
			</div>
		</div>
	);
};

export default connector(Overview);
