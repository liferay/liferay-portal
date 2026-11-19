import BundleRouter from 'route-middleware/BundleRouter';
import EditLayout from 'shared/components/EditLayout';
import React from 'react';
import SectionApp from '../SectionApp';
import SegmentEdit from 'segment/pages/Edit';
import SegmentLinkRedirect from 'shared/components/SegmentLinkRedirect';
import SegmentProfileRoutes from 'segment/pages/ProfileRoutes';
import SegmentsList from 'segment/pages/List';
import {RouteObject} from 'react-router-dom';

interface ISegmentsAppProps {
	channelId: string;
	groupId: string;
}

interface ISegmentAppProps extends ISegmentsAppProps {
	params: {id: string};
}

interface ISegmentInterestAppProps extends ISegmentsAppProps {
	params: {id: string; interestId: string; tabId?: string};
}

interface IEditSegmentAppProps extends ISegmentsAppProps {
	params: {id?: string};
}

const SEGMENT_ROUTES: RouteObject[] = [
	{
		element: (
			<SegmentLinkRedirect>
				<BundleRouter data={SegmentProfileRoutes} />
			</SegmentLinkRedirect>
		),
		path: 'contacts/segments/:id/*',
	},
];

const SegmentsApp: React.FC<ISegmentsAppProps> = (props) => (
	<SectionApp
		{...props}
		path="contacts/segments"
		routes={[
			{
				element: <BundleRouter data={SegmentsList} />,
				path: 'contacts/segments',
			},
		]}
	/>
);

const SegmentApp: React.FC<ISegmentAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={`contacts/segments/${params.id}`}
		routes={SEGMENT_ROUTES}
	/>
);

const SegmentDistributionApp: React.FC<ISegmentAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/segments/${params.id}/distribution`}
		routes={SEGMENT_ROUTES}
	/>
);

const SegmentInterestApp: React.FC<ISegmentInterestAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={
			params.tabId
				? `contacts/segments/${params.id}/interests/${encodeURIComponent(params.interestId)}/${params.tabId}`
				: `contacts/segments/${params.id}/interests/${encodeURIComponent(params.interestId)}`
		}
		routes={SEGMENT_ROUTES}
	/>
);

const SegmentInterestsApp: React.FC<ISegmentAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/segments/${params.id}/interests`}
		routes={SEGMENT_ROUTES}
	/>
);

const SegmentMembershipApp: React.FC<ISegmentAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		path={`contacts/segments/${params.id}/membership`}
		routes={SEGMENT_ROUTES}
	/>
);

const EditSegmentApp: React.FC<IEditSegmentAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		path={
			params.id
				? `contacts/segments/${params.id}/edit`
				: 'contacts/segments/create'
		}
		routes={[
			{
				children: [
					{
						element: <BundleRouter data={SegmentEdit} />,
						path: 'contacts/segments/:id/edit',
					},
					{
						element: <BundleRouter data={SegmentEdit} />,
						path: 'contacts/segments/create',
					},
				],
				element: <EditLayout />,
			},
		]}
	/>
);

export {
	EditSegmentApp as EditSegment,
	SegmentApp as Segment,
	SegmentDistributionApp as SegmentDistribution,
	SegmentInterestApp as SegmentInterest,
	SegmentInterestsApp as SegmentInterests,
	SegmentMembershipApp as SegmentMembership,
	SegmentsApp as Segments,
};
