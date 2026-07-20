import BaseCard from 'shared/components/base-card';
import ProfileCardWithDataCDP from '../components/ProfileCardWithDataCDP';
import React from 'react';
import {Individual} from 'shared/util/records';
import {pickBy} from 'lodash';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useHistory} from 'react-router-dom';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

const DEFAULT_SESSIONS_DELTA = 50;

interface IProfileCardCDP extends React.HTMLAttributes<HTMLElement> {
	channelId: string;
	entity: Individual;
	groupId: string;
	tabId: string;
	timeZoneId: string;
}

const ProfileCardCDP: React.FC<IProfileCardCDP> = ({tabId, ...props}) => {
	const history = useHistory();

	const {
		delta,
		onDeltaChange,
		onPageChange,
		onQueryChange,
		page,
		query,
		resetPage,
	} = useStatefulPagination(undefined, {
		initialDelta: DEFAULT_SESSIONS_DELTA,
	});

	return (
		<>
			<SectionHeader
				icon="analytics"
				title={Liferay.Language.get('interaction-history')}
			/>

			<BaseCard
				className="individual-profile-card-root page-display"
				description={Liferay.Language.get(
					'displays-a-chronological-timeline-of-events-within-the-selected-timeframe-including-session-context'
				)}
				headerProps={{
					showRangeKey: true,
					tabId,
				}}
				label={Liferay.Language.get('individual-events').toUpperCase()}
				legacyDropdownRangeKey={false}
				showInterval
			>
				{({
					interval,
					onChangeInterval,
					onRangeSelectorsChange,
					rangeSelectors,
				}) => (
					<ProfileCardWithDataCDP
						{...props}
						delta={delta}
						interval={interval}
						onChangeInterval={onChangeInterval}
						onDeltaChange={onDeltaChange}
						onPageChange={onPageChange}
						onQueryChange={(query) => {
							history.push(
								setUriQueryValues(
									pickBy({query}),
									removeUriQueryParam(
										window.location.href,
										'query'
									)
								)
							);

							onQueryChange(query);
						}}
						onRangeSelectorsChange={(rangeSelectors) => {
							history.push(
								setUriQueryValues(
									pickBy(rangeSelectors),
									removeUriQueryParam(
										window.location.href,
										'rangeEnd',
										'rangeStart'
									)
								)
							);

							onRangeSelectorsChange(rangeSelectors);
						}}
						page={page}
						query={query}
						rangeSelectors={rangeSelectors}
						resetPage={resetPage}
						tabId={tabId}
					/>
				)}
			</BaseCard>
		</>
	);
};

export default ProfileCardCDP;
