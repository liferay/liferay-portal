import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleNotFound from 'lifecycle/components/LifecycleNotFound';
import LifecycleSettingsForm from 'lifecycle/components/LifecycleSettingsForm';
import LifecyclesPage from 'lifecycle/components/LifecyclesPage';
import Loading from 'shared/components/Loading';
import React from 'react';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {LifecycleAccountDataEmptyState} from 'lifecycle/components/LifecycleEmptyStates';
import {
	buildUpdateLifecyclePayload,
	stageConfigsFromLifecycle,
} from 'lifecycle/utils/lifecyclePayload';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLifecycleAccountData} from 'lifecycle/hooks/useLifecycleAccountData';
import {useLifecycleSettingsForm} from 'lifecycle/hooks/useLifecycleSettingsForm';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {AccountDataStatus} from 'lifecycle/utils/types';

interface IEditLifecycleFormProps {
	lifecycle: ILifecycleDetail;
}

const EditLifecycleForm: React.FC<IEditLifecycleFormProps> = ({lifecycle}) => {
	const {groupId} = useParams();

	const form = useLifecycleSettingsForm({
		description: lifecycle.description ?? '',
		name: lifecycle.name ?? '',
		stageConfigs: () => stageConfigsFromLifecycle(lifecycle.stages),
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

	const handleSave = () =>
		submit({
			errorMessage: Liferay.Language.get('an-unexpected-error-occurred'),
			run: () =>
				API.lifecycle.updateLifecycle(
					buildUpdateLifecyclePayload({
						description: lifecycleDescription,
						groupId: groupId!,
						lifecycleId: lifecycle.id,
						name: lifecycleName,
						stageConfigs,
					})
				),
			successMessage: () =>
				Liferay.Language.get('the-lifecycle-was-updated-successfully'),
		});

	return (
		<LifecycleSettingsForm
			authorName={lifecycle.authorName ?? ''}
			form={form}
			modifiedDate={lifecycle.modifiedDate}
			onSubmit={handleSave}
			submitLabel={Liferay.Language.get('save-lifecycle')}
			title={Liferay.Language.get('edit-lifecycle')}
		/>
	);
};

const EditLifecycle = () => {
	const {groupId, lifecycleId} = useParams();

	const currentUser = useCurrentUser();

	const {refetch: refetchAccountData, status: accountDataStatus} =
		useLifecycleAccountData();

	const {
		data: lifecycle,
		error: lifecycleError,
		loading: lifecycleLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycle,
		variables: {groupId: groupId!, lifecycleId: lifecycleId!},
	});

	if (accountDataStatus === AccountDataStatus.Loading || lifecycleLoading) {
		return (
			<LifecyclesPage showTitle={false}>
				<Loading />
			</LifecyclesPage>
		);
	}

	if (accountDataStatus !== AccountDataStatus.Ready) {
		return (
			<LifecyclesPage>
				<LifecycleAccountDataEmptyState
					authorized={currentUser.isAdmin()}
					groupId={groupId!}
					onReload={refetchAccountData}
					status={accountDataStatus}
				/>
			</LifecyclesPage>
		);
	}

	if (lifecycleError || !lifecycle) {
		return <LifecycleNotFound />;
	}

	return <EditLifecycleForm lifecycle={lifecycle} />;
};

export default EditLifecycle;
