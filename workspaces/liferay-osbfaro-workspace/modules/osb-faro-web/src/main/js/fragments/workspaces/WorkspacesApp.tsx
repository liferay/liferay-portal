import BundleRouter from 'route-middleware/BundleRouter';
import GuestPageApp, {IGuestPageAppProps} from '../GuestPageApp';
import React from 'react';
import Workspaces from 'shared/pages/Workspaces';

interface IWorkspacesParams {
	[name: string]: string | undefined;
	allBasicConfigured?: string;
}

const WorkspacesApp: React.FC<IGuestPageAppProps<IWorkspacesParams>> = (
	props
) => (
	<GuestPageApp {...props}>
		<BundleRouter data={Workspaces} />
	</GuestPageApp>
);

export {WorkspacesApp as Workspaces};
