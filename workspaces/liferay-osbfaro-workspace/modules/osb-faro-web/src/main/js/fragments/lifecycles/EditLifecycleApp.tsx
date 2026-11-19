import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import LifecycleCreate from 'lifecycle/pages/CreateLifecycle';
import LifecycleDuplicate from 'lifecycle/pages/DuplicateLifecycle';
import LifecycleEdit from 'lifecycle/pages/EditLifecycle';
import PageApp, {IPageAppProps} from '../PageApp';
import React from 'react';
import {LDPElement} from 'shared/components/LDPElement';
import {config} from 'shared/util/config';

const LifecycleEditor = () => {
	const {lifecycleId, sourceLifecycleId} = config.params;

	let lifecycleEditor: React.ComponentType = LifecycleCreate;

	if (lifecycleId) {
		lifecycleEditor = LifecycleEdit;
	}
	else if (sourceLifecycleId) {
		lifecycleEditor = LifecycleDuplicate;
	}

	return <BundleRouter data={lifecycleEditor} destructured={false} />;
};

interface IEditLifecycleParams {
	[name: string]: string | undefined;
	backLifecycleId?: string;
	lifecycleId?: string;
	sourceLifecycleId?: string;
}

const EditLifecycleApp: React.FC<IPageAppProps<IEditLifecycleParams>> = (
	props
) => (
	<PageApp {...props}>
		<LDPElement fallback={<ErrorPage />}>
			<LifecycleEditor />
		</LDPElement>
	</PageApp>
);

export default EditLifecycleApp;
