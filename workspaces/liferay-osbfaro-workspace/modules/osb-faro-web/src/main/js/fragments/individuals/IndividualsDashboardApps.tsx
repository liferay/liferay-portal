import BundleRouter from 'route-middleware/BundleRouter';
import IndividualsDashboard from 'individual/dashboard/pages';
import IndividualsDashboardCDP from 'individual/dashboard/pages/IndividualsDashboardCDP';
import PageApp, {IPageAppProps} from '../PageApp';
import React, {lazy} from 'react';
import {LDPElement} from 'shared/components/LDPElement';

const Distribution = lazy(
	() => import('individual/dashboard/pages/Distribution')
);
const InterestDetails = lazy(
	() => import('individual/dashboard/pages/InterestDetails')
);
const Interests = lazy(() => import('individual/dashboard/pages/Interests'));
const KnownIndividuals = lazy(
	() => import('individual/dashboard/pages/KnownIndividuals')
);
const Overview = lazy(() => import('individual/dashboard/pages/Overview'));

interface IIndividualsParams {
	[name: string]: string | undefined;
	campaignId?: string;
	campaignName?: string;
}

interface IIndividualsOverviewParams extends IIndividualsParams {
	downloadReport?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
}

interface IIndividualsDistributionParams extends IIndividualsParams {
	delta?: string;
	fieldMappingFieldName?: string;
	numberOfBins?: string;
	page?: string;
	query?: string;
}

interface IIndividualsListParams extends IIndividualsParams {
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	sortOrder?: string;
}

interface IIndividualsInterestParams extends IIndividualsListParams {
	downloadReport?: string;
	interestId: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
}

function createIndividualsDashboardApp<
	Params extends {[name: string]: string | undefined},
>(content: React.ReactNode): React.FC<IPageAppProps<Params>> {
	return (props) => (
		<PageApp {...props}>
			<LDPElement
				fallback={
					<IndividualsDashboard>{content}</IndividualsDashboard>
				}
			>
				<IndividualsDashboardCDP />
			</LDPElement>
		</PageApp>
	);
}

export const IndividualsApp =
	createIndividualsDashboardApp<IIndividualsOverviewParams>(
		<BundleRouter data={Overview} destructured={false} />
	);

export const IndividualsDistributionApp =
	createIndividualsDashboardApp<IIndividualsDistributionParams>(
		<BundleRouter data={Distribution} />
	);

export const IndividualsInterestApp =
	createIndividualsDashboardApp<IIndividualsInterestParams>(
		<BundleRouter data={InterestDetails} destructured={false} />
	);

export const IndividualsInterestsApp =
	createIndividualsDashboardApp<IIndividualsListParams>(
		<BundleRouter data={Interests} destructured={false} />
	);

export const KnownIndividualsApp =
	createIndividualsDashboardApp<IIndividualsListParams>(
		<BundleRouter data={KnownIndividuals} />
	);
