import BundleRouter from 'route-middleware/BundleRouter';
import PageApp, {IPageAppProps} from '../PageApp';
import React, {lazy} from 'react';
import TouchpointProfile from 'sites/touchpoints/pages/TouchpointProfile';
import {RangeSelectors} from 'shared/types';

const Accounts = lazy(() => import('sites/touchpoints/pages/Accounts'));
const KnownIndividuals = lazy(
	() => import('sites/touchpoints/pages/KnownIndividuals')
);
const PagePath = lazy(() => import('sites/touchpoints/pages/PagePath'));
const TouchpointOverviewPage = lazy(
	() => import('sites/touchpoints/pages/TouchpointOverviewPage')
);

interface ITouchpointContentArgs {
	pathRangeSelectors: RangeSelectors;
}

interface ITouchpointParams {
	[name: string]: string | undefined;
	accountId?: string;
	accountName?: string;
	downloadReport?: string;
	experienceId?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
	segmentId?: string;
	segmentName?: string;
	title?: string;
	touchpoint: string;
}

interface ITouchpointListParams extends ITouchpointParams {
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	sortOrder?: string;
	useDB?: string;
}

function createTouchpointApp<
	Params extends {[name: string]: string | undefined},
>(
	renderContent: (args: ITouchpointContentArgs) => React.ReactNode
): React.FC<IPageAppProps<Params>> {
	return (props) => (
		<PageApp {...props}>
			<BundleRouter
				componentProps={{renderContent}}
				data={TouchpointProfile}
				destructured={false}
			/>
		</PageApp>
	);
}

export const PageAccountsApp = createTouchpointApp<ITouchpointListParams>(
	() => <BundleRouter data={Accounts} destructured={false} />
);

export const PageKnownIndividualsApp =
	createTouchpointApp<ITouchpointListParams>(() => (
		<BundleRouter data={KnownIndividuals} destructured={false} />
	));

export const PageOverviewApp = createTouchpointApp<ITouchpointParams>(() => (
	<BundleRouter data={TouchpointOverviewPage} destructured={false} />
));

export const PagePathApp = createTouchpointApp<ITouchpointParams>(
	({pathRangeSelectors}) => (
		<BundleRouter
			componentProps={{rangeSelectors: pathRangeSelectors}}
			data={PagePath}
			destructured={false}
		/>
	)
);
