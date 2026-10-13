import * as API from 'shared/api';
import {Alert} from 'shared/types';
import {addAlert} from 'shared/actions/alerts';
import {CATALOG_FIELDS_MAX_PAGE_SIZE} from 'shared/api/catalog';
import {
	isConditionComplete,
	isStageConfigured,
} from 'lifecycle/utils/lifecycleOperators';
import {
	createStageConfig,
	IStageConfig,
} from 'lifecycle/utils/stageConfiguration';
import {Routes, toRoute} from 'shared/util/router';
import {useDispatch} from 'react-redux';
import {useEffect, useState} from 'react';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const isStageReady = (stage: IStageConfig) =>
	!!stage.name.trim() &&
	!!stage.description.trim() &&
	isStageConfigured(stage) &&
	stage.conditions.every(
		(condition) => !condition.field || isConditionComplete(condition)
	);

/**
 * Owns the orchestration shared by the create and edit lifecycle pages: the
 * fields-catalog request, the editable lifecycle and stage state, the selected
 * panel item, the submit scaffold, and dashboard navigation. Each page supplies
 * only its initial values and the API call it runs on submit.
 */
export const useLifecycleSettingsForm = ({
	description: initialDescription,
	name: initialName,
	stageConfigs: initialStageConfigs,
}: {
	description: string;
	name: string;
	stageConfigs: () => IStageConfig[];
}) => {
	const dispatch = useDispatch();

	const history = useHistoryAdapter();

	const {channelId, groupId, lifecycleId} = useParams();

	const [stageConfigs, setStageConfigs] =
		useState<IStageConfig[]>(initialStageConfigs);

	const [lifecycleName, setLifecycleName] = useState(initialName);

	const [lifecycleDescription, setLifecycleDescription] =
		useState(initialDescription);

	const [selectedKey, setSelectedKey] = useState<string | null>(null);

	const {
		data: catalogFields,
		error: catalogError,
		loading: catalogLoading,
		refetch: refetchCatalog,
	} = useRequest({
		dataSourceFn: API.catalog.fetchCatalogFields,
		variables: {
			capability: 'FILTER',
			groupId: groupId!,
			pageSize: CATALOG_FIELDS_MAX_PAGE_SIZE,
			tableName: 'account',
		},
	});

	useEffect(() => {
		if (catalogError) {
			dispatch(
				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
				})
			);
		}
	}, [catalogError, dispatch]);

	const lifecycleURL = toRoute(
		lifecycleId ? Routes.LIFECYCLE_DASHBOARD : Routes.LIFECYCLE,
		{channelId, groupId, lifecycleId}
	);

	const goToDashboard = () => history.push(lifecycleURL);

	const updateStage = (key: string, value: IStageConfig) =>
		setStageConfigs((previous) =>
			previous.map((config) => (config.key === key ? value : config))
		);

	const addStage = () => {
		const stage = createStageConfig();

		setStageConfigs((previous) => [...previous, stage]);

		setSelectedKey(stage.key);
	};

	const removeStage = (key: string) => {
		const index = stageConfigs.findIndex((stage) => stage.key === key);

		if (index === -1) {
			return;
		}

		const remaining = stageConfigs.filter((stage) => stage.key !== key);

		setStageConfigs(remaining);

		if (selectedKey === key) {
			setSelectedKey(
				(remaining[index] ?? remaining[index - 1])?.key ?? null
			);
		}
	};

	const moveStage = ({from, to}: {from: number; to: number}) =>
		setStageConfigs((previous) => {
			const next = [...previous];

			const [stage] = next.splice(from, 1);

			next.splice(to, 0, stage);

			return next;
		});

	const selectedStage =
		stageConfigs.find((stage) => stage.key === selectedKey) ?? null;

	const canSubmit =
		!!lifecycleName.trim() &&
		!!lifecycleDescription.trim() &&
		!!stageConfigs.length &&
		stageConfigs.every(isStageReady);

	const submit = (run: () => Promise<unknown>, successMessage: string) =>
		run()
			.then(() => {
				dispatch(
					addAlert({
						alertType: Alert.Types.Success,
						message: successMessage,
					})
				);

				goToDashboard();
			})
			.catch(() => {
				dispatch(
					addAlert({
						alertType: Alert.Types.Error,
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
					})
				);
			});

	return {
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
	};
};
