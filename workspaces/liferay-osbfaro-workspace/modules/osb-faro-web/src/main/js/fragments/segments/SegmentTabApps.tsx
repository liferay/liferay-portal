import BundleRouter from 'route-middleware/BundleRouter';
import PageApp, {IPageAppProps} from '../PageApp';
import React, {lazy} from 'react';
import SegmentProfile from 'segment/pages/SegmentProfile';
import {Segment} from 'shared/util/records';

const Distribution = lazy(() => import('segment/pages/Distribution'));
const InterestDetails = lazy(() => import('segment/pages/InterestDetails'));
const Interests = lazy(() => import('segment/pages/Interests'));
const Membership = lazy(() => import('segment/pages/Membership'));
const Overview = lazy(() => import('segment/pages/Overview'));
const OverviewRealTime = lazy(() => import('segment/pages/OverviewRealTime'));

interface ISegmentTabContentArgs {
	isBatch: boolean;
	segment: Segment;
}

interface IRangeParams {
	[name: string]: string | undefined;
	downloadReport?: string;
	rangeEnd?: string;
	rangeKey?: string;
	rangeStart?: string;
}

interface ISegmentParams extends IRangeParams {
	id: string;
}

interface ISegmentDistributionParams {
	[name: string]: string | undefined;
	fieldMappingFieldName?: string;
	id: string;
	numberOfBins?: string;
}

interface ISegmentListParams extends ISegmentParams {
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	sortOrder?: string;
}

interface ISegmentInterestParams extends ISegmentListParams {
	active?: string;
	interestId: string;
	tabId?: string;
}

function createSegmentTabApp<
	Params extends {[name: string]: string | undefined},
>(
	renderContent: (args: ISegmentTabContentArgs) => React.ReactNode
): React.FC<IPageAppProps<Params>> {
	return (props) => (
		<PageApp {...props}>
			<SegmentProfile renderContent={renderContent} />
		</PageApp>
	);
}

export const SegmentApp = createSegmentTabApp<ISegmentParams>(
	({isBatch, segment}) => (
		<BundleRouter
			componentProps={{segment}}
			data={isBatch ? Overview : OverviewRealTime}
		/>
	)
);

export const SegmentDistributionApp =
	createSegmentTabApp<ISegmentDistributionParams>(({segment}) => (
		<BundleRouter componentProps={{segment}} data={Distribution} />
	));

export const SegmentInterestApp = createSegmentTabApp<ISegmentInterestParams>(
	({segment}) => (
		<BundleRouter componentProps={{segment}} data={InterestDetails} />
	)
);

export const SegmentInterestsApp = createSegmentTabApp<ISegmentListParams>(
	({segment}) => (
		<BundleRouter
			componentProps={{segment}}
			data={Interests}
			destructured={false}
		/>
	)
);

export const SegmentMembershipApp = createSegmentTabApp<ISegmentListParams>(
	({segment}) => <BundleRouter componentProps={{segment}} data={Membership} />
);
