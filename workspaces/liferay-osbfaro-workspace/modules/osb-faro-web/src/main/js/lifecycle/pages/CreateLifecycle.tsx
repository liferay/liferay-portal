import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSettingsForm from 'lifecycle/components/LifecycleSettingsForm';
import Loading from 'shared/components/Loading';
import React from 'react';
import {buildCreateLifecyclePayload} from 'lifecycle/utils/lifecyclePayload';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useParams} from 'react-router-dom';

const CreateLifecycle = () => {
	const {channelId, groupId} = useParams();

	const currentUser = useCurrentUser();

	const form = useLifecycleSettingsForm({
		description: '',
		name: '',
		stageConfigs: () => [],
	});

	const {
		catalogError,
		catalogLoading,
		lifecycleDescription,
		lifecycleName,
		refetchCatalog,
		stageConfigs,
		submit,
	} = form;

	if (catalogLoading) {
		return <Loading />;
	}

	if (catalogError) {
		return (
			<div className="align-items-center d-flex justify-content-center py-8">
				<ErrorDisplay onReload={refetchCatalog} spacer />
			</div>
		);
	}

	const handleCreate = () =>
		submit(
			() =>
				API.lifecycle.createLifecycle(
					buildCreateLifecyclePayload({
						channelId: channelId!,
						description: lifecycleDescription,
						groupId: groupId!,
						name: lifecycleName,
						stageConfigs,
					})
				),
			Liferay.Language.get('the-lifecycle-was-created-successfully')
		);

	return (
		<LifecycleSettingsForm
			authorName={currentUser.name}
			form={form}
			onSubmit={handleCreate}
			submitLabel={Liferay.Language.get('create-lifecycle')}
			title={Liferay.Language.get('new-lifecycle')}
		/>
	);
};

export default CreateLifecycle;
