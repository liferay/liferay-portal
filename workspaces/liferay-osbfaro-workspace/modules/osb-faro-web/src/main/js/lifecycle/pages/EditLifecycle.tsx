import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSettingsForm from 'lifecycle/components/LifecycleSettingsForm';
import Loading from 'shared/components/Loading';
import React from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {
	buildUpdateLifecyclePayload,
	stageConfigsFromLifecycle,
} from 'lifecycle/utils/lifecyclePayload';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

interface IEditLifecycleFormProps {
	lifecycle: ILifecycleDetail;
}

const EditLifecycleForm: React.FC<IEditLifecycleFormProps> = ({lifecycle}) => {
	const {groupId, lifecycleId} = useParams();

	const {
		canSubmit,
		catalogError,
		catalogFields,
		catalogLoading,
		goToDashboard,
		lifecycleName,
		lifecycleURL,
		refetchCatalog,
		setLifecycleName,
		stageConfigs,
		submit,
		updateStage,
	} = useLifecycleSettingsForm(
		() => stageConfigsFromLifecycle(lifecycle.stages),
		lifecycle.name ?? ''
	);

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

	const handleSave = () =>
		submit(
			() =>
				API.lifecycle.updateLifecycle(
					buildUpdateLifecyclePayload({
						groupId: groupId!,
						lifecycleId: lifecycleId!,
						name: lifecycleName,
						stageConfigs,
					})
				),
			Liferay.Language.get('the-lifecycle-was-updated-successfully')
		);

	return (
		<LifecycleSettingsForm
			backURL={lifecycleURL}
			catalogFields={catalogFields?.items}
			lifecycleName={lifecycleName}
			onCancel={goToDashboard}
			onLifecycleNameChange={setLifecycleName}
			onStageChange={updateStage}
			onSubmit={handleSave}
			stageConfigs={stageConfigs}
			submitDisabled={!canSubmit}
			submitLabel={Liferay.Language.get('save')}
		/>
	);
};

const EditLifecycle = () => {
	const {groupId, lifecycleId} = useParams();

	const {
		data: lifecycle,
		error: lifecycleError,
		loading: lifecycleLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycle,
		variables: {groupId: groupId!, lifecycleId: lifecycleId!},
	});

	if (lifecycleLoading) {
		return <Loading />;
	}

	if (lifecycleError || !lifecycle) {
		return <RouteNotFound />;
	}

	return <EditLifecycleForm lifecycle={lifecycle} />;
};

export default EditLifecycle;
