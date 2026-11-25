import BundleRouter from 'route-middleware/BundleRouter';
import GuestPageApp, {IGuestPageAppProps} from '../GuestPageApp';
import React from 'react';
import WorkspaceNotFound from 'shared/pages/WorkspaceNotFound';

const WorkspaceNotFoundApp: React.FC<IGuestPageAppProps> = (props) => (
	<GuestPageApp {...props}>
		<BundleRouter data={WorkspaceNotFound} />
	</GuestPageApp>
);

export default WorkspaceNotFoundApp;
