import BundleRouter from 'route-middleware/BundleRouter';
import React from 'react';
import SectionApp from '../SectionApp';
import SegmentsList from 'segment/pages/List';

interface ISegmentsAppProps {
	channelId: string;
	groupId: string;
}

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

export {SegmentsApp as Segments};
