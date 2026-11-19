import BundleRouter from 'route-middleware/BundleRouter';
import IndividualProfileRoutes from 'individual/profile/pages/ProfileRoutes';
import IndividualProfileRoutesCDP from 'individual/profile/pages/ProfileRoutesCDP';
import IndividualsDashboard from 'individual/dashboard/pages';
import IndividualsDashboardCDP from 'individual/dashboard/pages/IndividualsDashboardCDP';
import React from 'react';
import SectionApp from '../SectionApp';
import {LDPElement} from 'shared/components/LDPLayout';
import {RouteObject} from 'react-router-dom';

interface IIndividualsAppProps {
	channelId: string;
	groupId: string;
}

interface IIndividualAppProps extends IIndividualsAppProps {
	params: {id: string};
}

interface IIndividualInterestAppProps extends IIndividualsAppProps {
	params: {id: string; interestId: string};
}

interface IIndividualsInterestAppProps extends IIndividualsAppProps {
	params: {interestId: string};
}

const INDIVIDUALS_ROUTES: RouteObject[] = [
	{
		element: (
			<LDPElement
				fallback={<BundleRouter data={IndividualProfileRoutes} />}
			>
				<BundleRouter data={IndividualProfileRoutesCDP} />
			</LDPElement>
		),
		path: 'contacts/individuals/known-individuals/:id/*',
	},
	{
		element: (
			<LDPElement
				fallback={
					<BundleRouter
						data={IndividualsDashboard}
						destructured={false}
					/>
				}
			>
				<BundleRouter
					data={IndividualsDashboardCDP}
					destructured={false}
				/>
			</LDPElement>
		),
		path: 'contacts/individuals/*',
	},
];

const IndividualApp: React.FC<IIndividualAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`contacts/individuals/known-individuals/${params.id}`}
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualDetailsApp: React.FC<IIndividualAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/individuals/known-individuals/${params.id}/details`}
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualInterestApp: React.FC<IIndividualInterestAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/individuals/known-individuals/${params.id}/interests/${encodeURIComponent(params.interestId)}`}
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualInterestsApp: React.FC<IIndividualAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/individuals/known-individuals/${params.id}/interests`}
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualSegmentsApp: React.FC<IIndividualAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/individuals/known-individuals/${params.id}/segments`}
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualsApp: React.FC<IIndividualsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/individuals"
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualsDistributionApp: React.FC<IIndividualsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/individuals/distribution"
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualsInterestApp: React.FC<IIndividualsInterestAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/individuals/interests/${encodeURIComponent(params.interestId)}`}
		routes={INDIVIDUALS_ROUTES}
	/>
);

const IndividualsInterestsApp: React.FC<IIndividualsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/individuals/interests"
		routes={INDIVIDUALS_ROUTES}
	/>
);

const KnownIndividualsApp: React.FC<IIndividualsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/individuals/known-individuals"
		routes={INDIVIDUALS_ROUTES}
	/>
);

export {
	IndividualApp as Individual,
	IndividualDetailsApp as IndividualDetails,
	IndividualInterestApp as IndividualInterest,
	IndividualInterestsApp as IndividualInterests,
	IndividualSegmentsApp as IndividualSegments,
	IndividualsApp as Individuals,
	IndividualsDistributionApp as IndividualsDistribution,
	IndividualsInterestApp as IndividualsInterest,
	IndividualsInterestsApp as IndividualsInterests,
	KnownIndividualsApp as KnownIndividuals,
};
