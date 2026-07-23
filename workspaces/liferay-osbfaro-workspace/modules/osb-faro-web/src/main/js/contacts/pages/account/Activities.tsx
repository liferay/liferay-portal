import ActivityStreamCard from './components/ActivityStreamCard';
import ActivityStreamCardHeader from './components/ActivityStreamCardHeader';
import BaseCard from 'shared/components/base-card';
import Loading from 'shared/components/Loading';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {Interval, RangeSelectors} from 'shared/types';
import {useParams} from 'react-router-dom';

interface IActivitiesProps {
	accountName?: string;
}

const Activities: React.FC<IActivitiesProps> = ({accountName}) => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId: routeChannelId, id} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();

	const channelId = routeChannelId ?? selectedChannel?.id;

	if (!channelId) {
		return <Loading />;
	}

	return (
		<>
			<BaseCard
				className="account-activity-stream-card"
				description={Liferay.Language.get(
					'chronological-timeline-of-the-accounts-activities-within-the-selected-timeframe-with-details-on-events-and-session-context'
				)}
				Header={ActivityStreamCardHeader}
				headerProps={{showRangeKey: true}}
				label={Liferay.Language.get('activity-stream').toUpperCase()}
				legacyDropdownRangeKey={false}
				minHeight={500}
				showInterval
			>
				{({
					interval,
					rangeSelectors,
				}: {
					interval: Interval;
					rangeSelectors: RangeSelectors;
				}) => (
					<ActivityStreamCard
						accountId={id}
						accountName={accountName}
						channelId={channelId}
						interval={interval}
						rangeSelectors={rangeSelectors}
					/>
				)}
			</BaseCard>
		</>
	);
};

export default Activities;
