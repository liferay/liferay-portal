import * as API from 'shared/api';
import {Alert} from 'shared/types';
import {addAlert} from 'shared/actions/alerts';
import {CATALOG_FIELDS_MAX_PAGE_SIZE} from 'shared/api/catalog';
import {isStageConfigured} from 'lifecycle/utils/lifecycleOperators';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';
import {Routes, toRoute} from 'shared/util/router';
import {useDispatch} from 'react-redux';
import {useEffect, useState} from 'react';
import {useHistory, useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

/**
 * Owns the orchestration shared by the create and edit lifecycle pages: the
 * fields-catalog request, the editable stage/name state, the submit scaffold,
 * and dashboard navigation. Each page supplies only its initial values and the
 * API call it runs on submit.
 */
export const useLifecycleSettingsForm = (
	initialStageConfigs: () => IStageConfig[],
	initialName: string
) => {
	const dispatch = useDispatch();

	const history = useHistory();

	const {channelId, groupId} = useParams();

	const [stageConfigs, setStageConfigs] =
		useState<IStageConfig[]>(initialStageConfigs);

	const [lifecycleName, setLifecycleName] = useState(initialName);

	const {
		data: catalogFields,
		error: catalogError,
		loading: catalogLoading,
		refetch: refetchCatalog,
	} = useRequest({
		dataSourceFn: API.catalog.fetchCatalogFields,
		variables: {
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

	const lifecycleURL = toRoute(Routes.LIFECYCLE, {channelId, groupId});

	const goToDashboard = () => history.push(lifecycleURL);

	const updateStage = (index: number, value: IStageConfig) =>
		setStageConfigs((previous) =>
			previous.map((config, current) =>
				current === index ? value : config
			)
		);

	const canSubmit =
		!!lifecycleName.trim() && stageConfigs.every(isStageConfigured);

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
	};
};
