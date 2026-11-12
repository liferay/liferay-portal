import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import React from 'react';
import {columns, FrontendDataSet} from 'shared/components/FrontendDataSet';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {pickBy} from 'lodash';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {Text} from '@clayui/core';
import {useParams} from 'react-router-dom';
import {useRangeQueryValues} from 'shared/hooks/useRangeQueryValues';
import {useRangeSelectorChange} from 'shared/hooks/useRangeSelectorChange';

export interface IAccountCampaign {
	campaignName: string;
	id: string;
	individualsTouched: number;
	lastTouchDate: string;
}

interface ITopCampaignsProps {
	accountName?: string;
}

const TopCampaigns: React.FC<ITopCampaignsProps> = ({accountName}) => {
	const {channelId = '', groupId = '', id = ''} = useParams();

	const {apiRangeParams, rangeSelectors, routeRangeParams} =
		useRangeQueryValues();

	const linkQueryValues = {
		...routeRangeParams,
		...pickBy({accountId: id, accountName}),
	};

	const {handleRangeSelectorChange} = useRangeSelectorChange();

	return (
		<Card minHeight={260} testId="top-campaigns">
			<Card.Title className="align-items-center d-flex justify-content-between p-3">
				<Text size={5} weight="semi-bold">
					{Liferay.Language.get('top-campaigns').toUpperCase()}
				</Text>

				<DropdownRangeKey
					legacy={false}
					onRangeSelectorChange={handleRangeSelectorChange}
					rangeSelectors={rangeSelectors}
				/>
			</Card.Title>

			<Card.Body className="p-0">
				{id && (
					<FrontendDataSet
						apiURL={setUriQueryValues(
							{
								channelId,
								...apiRangeParams,
							},
							`/o/faro/contacts/${groupId}/account/${id}/campaigns`
						)}
						customDataRenderers={{
							campaignNameRenderer: ({
								itemData,
								value,
							}: {
								itemData: IAccountCampaign;
								value: string;
							}) =>
								columns.nameAndLinkRenderer({
									channelId,
									groupId,
									itemData,
									queryValues: linkQueryValues,
									route: Routes.CAMPAIGNS_DETAIL,
									value,
								}),
							countRenderer: columns.countRenderer,
							dateRenderer: columns.dateRenderer,
						}}
						emptyState={{
							description: Liferay.Language.get(
								'this-account-has-not-been-touched-by-any-campaign-yet'
							),
							title: Liferay.Language.get('no-campaigns-found'),
						}}
						id="top-campaigns-dataset"
						showManagementBar={false}
						showPagination={false}
						showSearch={false}
						sorts={[
							{
								active: true,
								default: true,
								direction: 'desc',
								key: 'individualsTouched',
								label: Liferay.Language.get(
									'individuals-touched'
								),
							},
						]}
						views={[
							{
								contentRenderer: 'table',
								default: true,
								initialPaginationDelta: 5,
								label: Liferay.Language.get('default-view'),
								name: 'table',
								schema: {
									fields: [
										{
											contentRenderer:
												'campaignNameRenderer',
											fieldName: 'campaignName',
											label: Liferay.Language.get(
												'campaign-name'
											),
											truncate: true,
										},
										{
											contentRenderer: 'countRenderer',
											fieldName: 'individualsTouched',
											label: Liferay.Language.get(
												'individuals-touched'
											),
										},
										{
											contentRenderer: 'dateRenderer',
											fieldName: 'lastTouchDate',
											label: Liferay.Language.get(
												'last-touch'
											),
										},
									],
								},
								thumbnail: 'table',
							},
						]}
					/>
				)}
			</Card.Body>

			{id && (
				<Card.Footer className="d-flex justify-content-end">
					<ClayLink
						borderless
						button
						className="button-root rounded-lg"
						displayType="primary"
						href={setUriQueryValues(
							linkQueryValues,
							toRoute(Routes.CAMPAIGNS, {channelId, groupId})
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

export default TopCampaigns;
