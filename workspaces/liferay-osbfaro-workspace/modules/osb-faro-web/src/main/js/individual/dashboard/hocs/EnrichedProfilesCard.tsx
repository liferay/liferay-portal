import * as API from 'shared/api';
import Card from 'shared/components/Card';
import getCN from 'classnames';
import InfoPopover from 'shared/components/InfoPopover';
import React from 'react';
import {DataSource} from 'shared/util/records';
import {isFinite} from 'lodash';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {useParams} from 'react-router-dom';
import {validContactsConfig} from 'shared/util/data-sources';
import {withRequest} from 'shared/hoc';

const EnrichedProfilesBody = ({count}: {count: number}) => (
	<Card.Body className="d-flex flex-column">
		<div className="total d-flex flex-grow-1 text-center justify-content-center align-items-center">
			{sub(Liferay.Language.get('x-profiles'), [
				isFinite(count) ? toLocale(count) : 0,
			])}
		</div>

		<div className="description text-center">
			{Liferay.Language.get(
				'enriched-with-attributes-or-behaviors-in-the-last-30-days'
			)}
		</div>
	</Card.Body>
);

const EnrichedProfilesBodyWithData = React.memo(
	withRequest(
		({channelId, groupId}: {channelId: string; groupId: string}) =>
			API.individuals.fetchEnrichedProfilesCount({channelId, groupId}),
		({total}: {total: number}) => ({count: total}),
		{page: false}
	)(EnrichedProfilesBody)
) as React.ComponentType<{channelId: string; groupId: string}>;

const renderInfoPopover = () => (
	<InfoPopover
		content={Liferay.Language.get(
			'total-count-of-individual-profiles-with-enrichment-from-data-source-updates-or-anonymous-profile-resolutions-in-the-last-30-days'
		)}
		title={Liferay.Language.get('enriched-profiles')}
	/>
);

interface IEnrichedProfilesCardProps extends React.HTMLAttributes<HTMLElement> {
	dataSources: DataSource[];
}

const EnrichedProfilesCard: React.FC<IEnrichedProfilesCardProps> = ({
	dataSources,
}) => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();
	const contactsConfigured =
		!dataSources || dataSources.some(validContactsConfig);

	return (
		<Card
			className={getCN('enriched-profiles-card-root', {
				inverted: !contactsConfigured,
				['text-secondary']: contactsConfigured,
			})}
			reportContainer={ReportContainer.EnrichedProfilesCard}
		>
			{contactsConfigured ? (
				<>
					<Card.Header className="d-flex justify-content-between">
						<Card.Title>
							{Liferay.Language.get('enriched-profiles')}
						</Card.Title>

						{renderInfoPopover()}
					</Card.Header>

					<EnrichedProfilesBodyWithData
						channelId={channelId}
						groupId={groupId}
					/>
				</>
			) : (
				<Card.Body>
					<div className="d-flex justify-content-between">
						<Card.Title>
							{Liferay.Language.get('know-your-audience-better')}
						</Card.Title>

						{renderInfoPopover()}
					</div>

					<p>
						{Liferay.Language.get(
							'know-your-audience-better-by-connecting-people-data-to-enrich-profiles.-get-started-by-syncing-contacts-from-dxp-or-by-adding-a-data-source-with-people-data'
						)}
					</p>
				</Card.Body>
			)}
		</Card>
	);
};

export default EnrichedProfilesCard;
