import BundleRouter from 'route-middleware/BundleRouter';
import React from 'react';
import SectionApp from '../SectionApp';
import SitesDashboard from 'sites/pages';
import TouchpointRoutes from 'sites/touchpoints/pages/TouchpointRoutes';
import {RouteObject} from 'react-router-dom';

interface ISitesAppProps {
	channelId: string;
	groupId: string;
}

interface IPageParams {
	title: string;
	touchpoint: string;
}

interface IPageAppProps extends ISitesAppProps {
	params: IPageParams;
}

interface ISiteInterestAppProps extends ISitesAppProps {
	params: {interestId: string};
}

const SITES_ROUTES: RouteObject[] = [
	{
		element: (
			<BundleRouter
				data={TouchpointRoutes as unknown as React.ComponentType}
				destructured={false}
			/>
		),
		path: 'sites/pages/:touchpointType/:touchpoint/:title?',
	},
	{
		element: <BundleRouter data={SitesDashboard} destructured={false} />,
		path: 'sites/*',
	},
];

const getPagePath = (
	touchpointType: string,
	{title, touchpoint}: IPageParams
) => {
	const segments = [
		'sites',
		'pages',
		touchpointType,
		touchpoint,
		title,
	].filter(Boolean);

	const encodedSegments = segments.map(encodeURIComponent);

	return encodedSegments.join('/');
};

const PageApp: React.FC<IPageAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={getPagePath('overview', params)}
		routes={SITES_ROUTES}
	/>
);

const PageAccountsApp: React.FC<IPageAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={getPagePath('accounts', params)}
		routes={SITES_ROUTES}
	/>
);

const PageKnownIndividualsApp: React.FC<IPageAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={getPagePath('known-individuals', params)}
		routes={SITES_ROUTES}
	/>
);

const PagePathApp: React.FC<IPageAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={getPagePath('path', params)}
		routes={SITES_ROUTES}
	/>
);

const PagesApp: React.FC<ISitesAppProps> = (props) => (
	<SectionApp {...props} path="sites/pages" routes={SITES_ROUTES} />
);

const SearchTermsApp: React.FC<ISitesAppProps> = (props) => (
	<SectionApp {...props} path="sites/search-terms" routes={SITES_ROUTES} />
);

const SiteInterestApp: React.FC<ISiteInterestAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`sites/interests/${encodeURIComponent(params.interestId)}`}
		routes={SITES_ROUTES}
	/>
);

const SiteInterestsApp: React.FC<ISitesAppProps> = (props) => (
	<SectionApp {...props} path="sites/interests" routes={SITES_ROUTES} />
);

const SitesApp: React.FC<ISitesAppProps> = (props) => (
	<SectionApp {...props} path="sites" routes={SITES_ROUTES} />
);

export {
	PageApp as Page,
	PageAccountsApp as PageAccounts,
	PageKnownIndividualsApp as PageKnownIndividuals,
	PagePathApp as PagePath,
	PagesApp as Pages,
	SearchTermsApp as SearchTerms,
	SiteInterestApp as SiteInterest,
	SiteInterestsApp as SiteInterests,
	SitesApp as Sites,
};
