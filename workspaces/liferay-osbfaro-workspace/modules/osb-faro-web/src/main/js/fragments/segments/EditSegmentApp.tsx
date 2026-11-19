import BundleRouter from 'route-middleware/BundleRouter';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import SegmentEdit from 'segment/pages/Edit';

interface IEditSegmentParams {
	[name: string]: string | undefined;
	category?: string;
	id?: string;
	type?: string;
}

const EditSegmentApp: React.FC<IPageAppProps<IEditSegmentParams>> = (props) => (
	<PageApp {...props}>
		<BundleRouter data={SegmentEdit} />
	</PageApp>
);

export default EditSegmentApp;
