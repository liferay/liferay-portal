import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSettingsForm from 'lifecycle/components/LifecycleSettingsForm';
import Loading from 'shared/components/Loading';
import React from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {buildCreateLifecyclePayload} from 'lifecycle/utils/lifecyclePayload';
import {createDefaultStageConfigs} from 'lifecycle/utils/stageConfiguration';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const CreateLifecycle = () => {
	const {channelId, groupId} = useParams();

	const {data: lifecycles, loading} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycles,
		variables: {channelId, groupId: groupId!},
	});

	const {
		canSubmit,
		catalogError,
		catalogFields,
		catalogLoading,
		lifecycleName,
		lifecycleURL,
		refetchCatalog,
		setLifecycleName,
		stageConfigs,
		submit,
		updateStage,
	} = useLifecycleSettingsForm(createDefaultStageConfigs, '');

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
						groupId: groupId!,
						name: lifecycleName,
						stageConfigs,
					})
				),
			Liferay.Language.get('the-lifecycle-was-created-successfully')
		);

	return (
		<LifecycleSettingsForm
			backURL={lifecycleURL}
			catalogFields={catalogFields?.items}
			lifecycleName={lifecycleName}
			onLifecycleNameChange={setLifecycleName}
			onStageChange={updateStage}
			onSubmit={handleCreate}
			stageConfigs={stageConfigs}
			submitDisabled={!canSubmit}
			submitLabel={Liferay.Language.get('create')}
		/>
	);
};

export default CreateLifecycle;
