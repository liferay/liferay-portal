import AddWorkspace from 'shared/pages/AddWorkspace';
import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import GuestPageApp, {IGuestPageAppProps} from '../GuestPageApp';
import React from 'react';
import {ENABLE_ADD_TRIAL_WORKSPACE} from 'shared/util/constants';
import {config} from 'shared/util/config';

interface IAddWorkspaceParams {
	[name: string]: string | undefined;
	corpProjectUuid?: string;
}

const AddWorkspacePage = () => {
	const {corpProjectUuid} = config.params;

	if (!corpProjectUuid && !ENABLE_ADD_TRIAL_WORKSPACE) {
		return <ErrorPage />;
	}

	return <BundleRouter data={AddWorkspace} />;
};

const AddWorkspaceApp: React.FC<IGuestPageAppProps<IAddWorkspaceParams>> = (
	props
) => (
	<GuestPageApp {...props}>
		<AddWorkspacePage />
	</GuestPageApp>
);

export default AddWorkspaceApp;
