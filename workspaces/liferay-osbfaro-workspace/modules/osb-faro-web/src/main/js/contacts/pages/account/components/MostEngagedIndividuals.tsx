import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import IndividualsDataSet from './IndividualsDataSet';
import React from 'react';
import {Routes, toRoute} from 'shared/util/router';
import {Text} from '@clayui/core';
import {useParams} from 'react-router-dom';

const MostEngagedIndividuals: React.FC = () => {
	const {channelId, groupId, id} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();

	return (
		<Card minHeight={260} testId="most-engaged-individuals">
			<Card.Title className="p-3">
				<Text weight="semi-bold">
					{Liferay.Language.get(
						'most-engaged-individuals'
					).toUpperCase()}
				</Text>
			</Card.Title>

			<Card.Body className="p-0">
				<IndividualsDataSet preview />
			</Card.Body>

			{id && (
				<Card.Footer className="d-flex justify-content-end">
					<ClayLink
						borderless
						button
						className="button-root"
						displayType="primary"
						href={toRoute(Routes.CONTACTS_ACCOUNT_PROFILE, {
							channelId,
							groupId,
							id,
						})}
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
