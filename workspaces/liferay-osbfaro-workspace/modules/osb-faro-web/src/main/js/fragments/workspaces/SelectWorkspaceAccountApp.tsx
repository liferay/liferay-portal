import BundleRouter from 'route-middleware/BundleRouter';
import GuestPageApp, {IGuestPageAppProps} from '../GuestPageApp';
import React from 'react';
import SelectWorkspaceAccount from 'shared/pages/SelectWorkspaceAccount';

const SelectWorkspaceAccountApp: React.FC<IGuestPageAppProps> = (props) => (
	<GuestPageApp {...props}>
		<BundleRouter data={SelectWorkspaceAccount} />
	</GuestPageApp>
);

export default SelectWorkspaceAccountApp;
