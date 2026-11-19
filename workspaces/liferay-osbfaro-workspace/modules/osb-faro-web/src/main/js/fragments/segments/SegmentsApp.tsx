import BundleRouter from 'route-middleware/BundleRouter';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import SegmentsList from 'segment/pages/List';

interface ISegmentsParams {
	[name: string]: string | undefined;
	delta?: string;
	field?: string;
	page?: string;
	query?: string;
	segmentCategory?: string;
	segmentType?: string;
	sortOrder?: string;
}

const SegmentsApp: React.FC<IPageAppProps<ISegmentsParams>> = (props) => (
	<PageApp {...props}>
		<BundleRouter data={SegmentsList} />
	</PageApp>
);

export {SegmentsApp as Segments};
