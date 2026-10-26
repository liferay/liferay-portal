import * as API from 'shared/api';
import {Alert} from 'shared/types';
import {addAlert} from 'shared/actions/alerts';
import {getLifecycleSaveErrorMessage} from 'lifecycle/utils/lifecycleErrors';
import {CATALOG_FIELDS_MAX_PAGE_SIZE} from 'shared/api/catalog';
import {
	createStageConfig,
	IStageConfig,
} from 'lifecycle/utils/stageConfiguration';
import {
	getDuplicateStageKeys,
	getLifecycleErrorMessages,
	getStageErrorMessages,
	hasLifecycleName,
	hasStageChipError,
} from 'lifecycle/utils/lifecycleValidation';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {Routes, toRoute} from 'shared/util/router';
import {useDispatch} from 'react-redux';
import {moveItem} from 'shared/util/array';
import {useEffect, useMemo, useRef, useState} from 'react';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useLocation, useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const LIFECYCLE_NAME_LOOKUP_PAGE_SIZE = 100;

const NAME_IS_ALREADY_USED = 'name-is-already-used';

interface IRedirect {
	replace?: boolean;
	url: string;
}

interface ISubmitOptions {
	errorMessage: string;
	redirect?: (lifecycle: ILifecycleDetail) => IRedirect;
	run: () => Promise<ILifecycleDetail>;
	successMessage: (lifecycle: ILifecycleDetail) => string;
}

const serializeLifecycle = (
	name: string,
	description: string,
	stageConfigs: IStageConfig[]
) =>
	JSON.stringify({
		description,
		name,
		stages: stageConfigs.map((stage) => ({
			conditions: stage.conditions.map(
				({conditionValue, field, operator}) => ({
					conditionValue,
					field,
					operator,
				})
			),
			description: stage.description,
			id: stage.id,
			matchLogic: stage.matchLogic,
			maxTimeDays: stage.maxTimeDays,
			maxTimeEnabled: stage.maxTimeEnabled,
			name: stage.name,
		})),
	});

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

	const {state} = useLocation();

	const [stageConfigs, setStageConfigs] =
		useState<IStageConfig[]>(initialStageConfigs);

	const [lifecycleName, setLifecycleName] = useState(initialName);

	const [lifecycleDescription, setLifecycleDescription] =
		useState(initialDescription);

	const [selectedKey, setSelectedKey] = useState<string | null>(null);

	const [blurredFields, setBlurredFields] = useState<string[]>([]);

	const [submitAttempted, setSubmitAttempted] = useState(false);

	const [alertCount, setAlertCount] = useState(0);

	const [alertVisible, setAlertVisible] = useState(false);

	const [saving, setSaving] = useState(false);

	const [nameTaken, setNameTaken] = useState(false);

	const [redirect, setRedirect] = useState<IRedirect | null>(null);

	const submitted = !!redirect;

	const initialSnapshotRef = useRef<string | null>(null);

	if (initialSnapshotRef.current === null) {
		initialSnapshotRef.current = serializeLifecycle(
			lifecycleName,
			lifecycleDescription,
			stageConfigs
		);
	}

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

	const backLifecycleId = lifecycleId ?? state?.lifecycleId;

	const backURL = toRoute(
		backLifecycleId ? Routes.LIFECYCLE_DASHBOARD : Routes.LIFECYCLE,
		{channelId, groupId, lifecycleId: backLifecycleId}
	);

	useEffect(() => {
		if (redirect?.replace) {
			history.replace(redirect.url);
		}
		else if (redirect) {
			history.push(redirect.url);
		}
	}, [history, redirect]);

	const changeLifecycleName = (name: string) => {
		setLifecycleName(name);

		setNameTaken(false);
	};

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
		setStageConfigs((previous) => moveItem(previous, from, to));

	const selectedStage =
		stageConfigs.find((stage) => stage.key === selectedKey) ?? null;

	const duplicateStageKeys = getDuplicateStageKeys(stageConfigs);

	const lifecycleErrorMessages = getLifecycleErrorMessages({
		description: lifecycleDescription,
		duplicateStageKeys,
		name: lifecycleName,
		stageConfigs,
	});

	const selectedStageErrorMessages = selectedStage
		? getStageErrorMessages(
				selectedStage,
				duplicateStageKeys.includes(selectedStage.key)
			)
		: [];

	let alertMessages: string[] = [];

	if (alertVisible) {
		alertMessages = selectedStageErrorMessages.length
			? selectedStageErrorMessages
			: [
					...lifecycleErrorMessages,
					...(nameTaken
						? [
								Liferay.Language.get(
									'this-lifecycle-name-is-currently-in-use.-please-try-a-different-one'
								),
							]
						: []),
				];
	}

	const snapshot = useMemo(
		() =>
			serializeLifecycle(
				lifecycleName,
				lifecycleDescription,
				stageConfigs
			),
		[lifecycleDescription, lifecycleName, stageConfigs]
	);

	const changed = snapshot !== initialSnapshotRef.current;

	const blurField = (id: string) =>
		setBlurredFields((previous) =>
			previous.includes(id) ? previous : [...previous, id]
		);

	const isFieldErrorVisible = (id: string) =>
		submitAttempted || blurredFields.includes(id);

	const showNameTaken = () => {
		setNameTaken(true);

		setAlertVisible(true);

		setAlertCount((count) => count + 1);

		setSelectedKey(null);
	};

	const isNameTaken = () =>
		API.lifecycle
			.fetchLifecyclesPage({
				channelId,
				groupId: groupId!,
				page: 1,
				pageSize: LIFECYCLE_NAME_LOOKUP_PAGE_SIZE,
				search: lifecycleName,
			})
			.then(({items}) =>
				hasLifecycleName(items, lifecycleName, lifecycleId)
			);

	const submit = ({
		errorMessage,
		redirect: getRedirect = () => ({url: backURL}),
		run,
		successMessage,
	}: ISubmitOptions) => {
		if (saving) {
			return Promise.resolve();
		}

		setSubmitAttempted(true);

		if (lifecycleErrorMessages.length) {
			setAlertVisible(true);

			setAlertCount((count) => count + 1);

			return Promise.resolve();
		}

		setAlertVisible(false);

		setSaving(true);

		return isNameTaken()
			.then((taken) => {
				if (taken) {
					showNameTaken();

					return;
				}

				return run().then((lifecycle) => {
					dispatch(
						addAlert({
							alertType: Alert.Types.Success,
							message: successMessage(lifecycle),
						})
					);

					setRedirect(getRedirect(lifecycle));
				});
			})
			.catch((error) => {
				if (error?.message === NAME_IS_ALREADY_USED) {
					showNameTaken();

					return;
				}

				dispatch(
					addAlert({
						alertType: Alert.Types.Error,
						message: getLifecycleSaveErrorMessage(
							error,
							errorMessage
						),
					})
				);
			})
			.finally(() => setSaving(false));
	};

	return {
		addStage,
		alertCount,
		alertMessages,
		backURL,
		blurField,
		catalogError,
		catalogFields,
		catalogLoading,
		changed: changed && !submitted,
		dismissAlert: () => setAlertVisible(false),
		duplicateStageKeys,
		isFieldErrorVisible,
		lifecycleDescription,
		lifecycleName,
		moveStage,
		nameTaken,
		refetchCatalog,
		removeStage,
		saving,
		selectedStage,
		setLifecycleDescription,
		setLifecycleName: changeLifecycleName,
		setSelectedKey,
		stageConfigs,
		stageErrorKeys: stageConfigs
			.filter(
				(stage) =>
					isFieldErrorVisible(`stageName-${stage.key}`) &&
					(hasStageChipError(stage) ||
						duplicateStageKeys.includes(stage.key))
			)
			.map((stage) => stage.key),
		submit,
		summaryError: !lifecycleName.trim() || !lifecycleDescription.trim(),
		updateStage,
	};
};
