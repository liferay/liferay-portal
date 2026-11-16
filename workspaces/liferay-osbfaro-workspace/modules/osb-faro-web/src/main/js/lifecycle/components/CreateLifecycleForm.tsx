import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSettingsForm from 'lifecycle/components/LifecycleSettingsForm';
import Loading from 'shared/components/Loading';
import React from 'react';
import {buildCreateLifecyclePayload} from 'lifecycle/utils/lifecyclePayload';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useParams} from 'react-router-dom';

interface ICreateLifecycleFormProps {
	description: string;
	name: string;
	segmentId?: string;
	stageConfigs: () => IStageConfig[];
}

const CreateLifecycleForm: React.FC<ICreateLifecycleFormProps> = ({
	description,
	name,
	segmentId,
	stageConfigs: initialStageConfigs,
}) => {
	const {channelId, groupId} = useParams();

	const currentUser = useCurrentUser();

	const form = useLifecycleSettingsForm({
		description,
		name,
		stageConfigs: initialStageConfigs,
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
		submit({
			errorMessage: Liferay.Language.get(
				'the-creation-process-failed-to-complete.-try-again-later'
			),
			redirect: ({externalReferenceCode}) => ({
				replace: true,
				url: toRoute(Routes.LIFECYCLE_DASHBOARD, {
					channelId,
					groupId,
					lifecycleId: externalReferenceCode,
				}),
			}),
			run: () =>
				API.lifecycle.createLifecycle(
					buildCreateLifecyclePayload({
						channelId: channelId!,
						description: lifecycleDescription,
						groupId: groupId!,
						name: lifecycleName,
						segmentId,
						stageConfigs,
					})
				),
			successMessage: (lifecycle) =>
				sub(Liferay.Language.get('x-was-created'), [
					lifecycle.name || lifecycleName.trim(),
				]) as string,
		});

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

export default CreateLifecycleForm;
