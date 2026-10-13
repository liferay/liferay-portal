import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSettingsForm from 'lifecycle/components/LifecycleSettingsForm';
import Loading from 'shared/components/Loading';
import React from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {buildCreateLifecyclePayload} from 'lifecycle/utils/lifecyclePayload';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const CreateLifecycle = () => {
	const {channelId, groupId} = useParams();

	const currentUser = useCurrentUser();

	const {data: lifecycles, loading} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycles,
		variables: {channelId, groupId: groupId!},
	});

	const {
		addStage,
		canSubmit,
		catalogError,
		catalogFields,
		catalogLoading,
		lifecycleDescription,
		lifecycleName,
		lifecycleURL,
		moveStage,
		refetchCatalog,
		removeStage,
		selectedStage,
		setLifecycleDescription,
		setLifecycleName,
		setSelectedKey,
		stageConfigs,
		submit,
		updateStage,
	} = useLifecycleSettingsForm({
		description: '',
		name: '',
		stageConfigs: () => [],
	});

	if (loading || catalogLoading) {
		return <Loading />;
	}

	if (lifecycles?.length) {
		return <RouteNotFound />;
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
			backURL={lifecycleURL}
			catalogFields={catalogFields?.items}
			lifecycleDescription={lifecycleDescription}
			lifecycleName={lifecycleName}
			onAddStage={addStage}
			onLifecycleDescriptionChange={setLifecycleDescription}
			onLifecycleNameChange={setLifecycleName}
			onMoveStage={moveStage}
			onRemoveStage={removeStage}
			onSelect={setSelectedKey}
			onStageChange={updateStage}
			onSubmit={handleCreate}
			selectedStage={selectedStage}
			stageConfigs={stageConfigs}
			submitDisabled={!canSubmit}
			submitLabel={Liferay.Language.get('create-lifecycle')}
			title={Liferay.Language.get('new-lifecycle')}
		/>
	);
};

export default CreateLifecycle;
