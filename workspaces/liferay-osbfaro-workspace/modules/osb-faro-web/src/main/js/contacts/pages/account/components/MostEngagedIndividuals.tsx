import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import IndividualsDataSet from './IndividualsDataSet';
import React from 'react';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {
	removeUriQueryParam,
	Routes,
	setUriQueryValues,
	toRoute,
} from 'shared/util/router';
import {Text} from '@clayui/core';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useParams} from 'react-router-dom';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const MostEngagedIndividuals: React.FC = () => {
	const history = useHistoryAdapter();

	const {channelId, groupId, id} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();

	const rangeSelectors = useQueryRangeSelectors();

	const handleRangeSelectorChange = ({
		rangeEnd,
		rangeKey,
		rangeStart,
	}: RangeSelectors) => {

		// The bounds belong to a custom range only, so drop the previous ones
		// before writing rather than leaving them behind to narrow a preset.

		history.push(
			setUriQueryValues(
				pickBy({rangeEnd, rangeKey, rangeStart}),
				removeUriQueryParam(
					window.location.href,
					'rangeEnd',
					'rangeStart'
				)
			)
		);
	};

	return (
		<Card minHeight={260} testId="most-engaged-individuals">
			<Card.Title className="align-items-center d-flex justify-content-between p-3">
				<Text size={5} weight="semi-bold">
					{Liferay.Language.get(
						'most-engaged-individuals'
					).toUpperCase()}
				</Text>

				<DropdownRangeKey
					legacy={false}
					onRangeSelectorChange={handleRangeSelectorChange}
					rangeSelectors={rangeSelectors}
				/>
			</Card.Title>

			<Card.Body className="p-0">
				<IndividualsDataSet preview />
			</Card.Body>

			{id && (
				<Card.Footer className="d-flex justify-content-end">
					<ClayLink
						borderless
						button
						className="button-root rounded-lg"
						displayType="primary"
						href={setUriQueryValues(
							pickBy(rangeSelectors),
							toRoute(Routes.CONTACTS_ACCOUNT_PROFILE, {
								channelId,
								groupId,
								id,
							})
						)}
						small
					>
						{Liferay.Language.get('view-all')}
					</ClayLink>
				</Card.Footer>
			)}
		</Card>
	);
};

export default MostEngagedIndividuals;
