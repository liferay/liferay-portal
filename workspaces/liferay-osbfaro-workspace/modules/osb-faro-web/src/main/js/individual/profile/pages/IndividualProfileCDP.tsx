import * as API from 'shared/api';
import AccountMembership from '../components/AccountMembership';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import IndividualAttributesCDP from '../components/IndividualAttributesCDP';
import IndividualDetailsCDP from '../components/IndividualAllAttributesCDP';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {Individual} from 'shared/util/records';
import {isNil} from 'lodash';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useRequest} from 'shared/hooks/useRequest';

enum IndividualProfileCDPCards {
	AccountMembership = 'ACCOUNT_MEMBERSHIP',
	AllAttributes = 'ALL_ATTRIBUTES',
	IndividualsAttributes = 'INDIVIDUAL_ATTRIBUTES',
}

interface IIndividualProfileCDPProps {
	channelId: string;
	groupId: string;
	individual: Individual;
}
interface IProfileCDPEmptyStateProps {
	authorized: boolean;
	dataSourceData?: any;
	groupId: string;
	pageDisplay?: boolean;
	type?: IndividualProfileCDPCards;
}

const ProfileCDPEmptyState: React.FC<IProfileCDPEmptyStateProps> = ({
	authorized,
	dataSourceData,
	groupId,
	pageDisplay = true,
	type = IndividualProfileCDPCards.IndividualsAttributes,
}) => {
	if (isNil(dataSourceData?.total) || dataSourceData?.total === 0) {
		const isAccount = type === IndividualProfileCDPCards.AccountMembership;

		const getLabels = () => {
			if (isAccount) {
				return {
					description: authorized
						? Liferay.Language.get(
								'connect-a-data-source-with-account-data-to-get-started'
							)
						: Liferay.Language.get(
								'contact-an-administrator-to-connect-a-data-source-containing-account-data'
							),
					title: Liferay.Language.get('no-account-data-synced'),
				};
			}

			return {
				description: authorized
					? Liferay.Language.get(
							'connect-a-data-source-with-individuals-data-to-get-started'
						)
					: Liferay.Language.get(
							'contact-an-administrator-to-connect-a-data-source-containing-individuals-data'
						),
				title: Liferay.Language.get('no-individuals-data-synced'),
			};
		};

		const {description, title} = getLabels();

		return (
			<Card pageDisplay={pageDisplay}>
				<Card.Body>
					<NoResultsDisplay
						description={
							<>
								<p className="mb-2">{description}</p>
								<ClayLink
									className="d-block mb-3"
									decoration="underline"
									href={URLConstants.DataSourceConnection}
									target="_blank"
								>
									{Liferay.Language.get(
										'learn-more-about-data-sources'
									)}
									<span className="inline-item inline-item-after">
										<ClayIcon
											fontSize={8}
											symbol="shortcut"
										/>
									</span>
								</ClayLink>
							</>
						}
						primary
						title={title}
					>
						{authorized ? (
							<ClayLink
								button
								className="button-root mt-1"
								displayType="primary"
								href={toRoute(
									Routes.SETTINGS_DATA_SOURCE_LIST,
									{groupId}
								)}
							>
								{Liferay.Language.get('connect-data-source')}
							</ClayLink>
						) : undefined}
					</NoResultsDisplay>
				</Card.Body>
			</Card>
		);
	}

	return null;
};

const IndividualProfileCDP: React.FC<IIndividualProfileCDPProps> = ({
	channelId,
	groupId,
	individual,
}) => {
	const {data: dataSourceData, loading: dataSourceLoading} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta: 1,
			groupId,
		},
	});

	const currentUser = useCurrentUser();

	const authorized = currentUser.isAdmin();

	const showEmptyState =
		!dataSourceLoading &&
		(isNil(dataSourceData?.total) || dataSourceData?.total === 0);

	return (
		<>
			<IndividualAttributesCDP
				contactId={individual.get('id')}
				loading={dataSourceLoading}
				propertiesData={individual.get('properties')}
				showEmptyState={showEmptyState}
			>
				<ProfileCDPEmptyState
					authorized={authorized}
					dataSourceData={dataSourceData}
					groupId={groupId}
					pageDisplay={false}
				/>
			</IndividualAttributesCDP>

			<AccountMembership
				accountData={individual.getIn(['accounts', 0])}
				channelId={channelId}
				groupId={groupId}
				loading={dataSourceLoading}
				showEmptyState={showEmptyState}
			>
				<ProfileCDPEmptyState
					authorized={authorized}
					dataSourceData={dataSourceData}
					groupId={groupId}
					pageDisplay={false}
				/>
			</AccountMembership>

			<IndividualDetailsCDP
				groupId={groupId}
				individualId={individual.get('id')}
				showEmptyState={showEmptyState}
			>
				<ProfileCDPEmptyState
					authorized={authorized}
					dataSourceData={dataSourceData}
					groupId={groupId}
					pageDisplay={false}
				/>
			</IndividualDetailsCDP>
		</>
	);
};

export default IndividualProfileCDP;
