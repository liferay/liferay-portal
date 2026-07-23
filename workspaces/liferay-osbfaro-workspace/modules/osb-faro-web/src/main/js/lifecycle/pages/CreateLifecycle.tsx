import * as API from 'shared/api';
import Card from 'shared/components/Card';
import DocumentTitle from 'shared/components/DocumentTitle';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSettingsToolbar from 'lifecycle/components/LifecycleSettingsToolbar';
import Loading from 'shared/components/Loading';
import React, {useEffect, useState} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {Alert} from 'shared/types';
import {addAlert} from 'shared/actions/alerts';
import {CATALOG_FIELDS_MAX_PAGE_SIZE} from 'shared/api/catalog';
import {buildCreateLifecyclePayload} from 'lifecycle/utils/lifecyclePayload';
import {isStageConfigured} from 'lifecycle/utils/lifecycleOperators';
import {
	createDefaultStageConfigs,
	IStageConfig,
	LIFECYCLE_STAGE_ORDER,
} from 'lifecycle/utils/stageConfiguration';
import {Routes, toRoute} from 'shared/util/router';
import {useDispatch} from 'react-redux';
import {useHistory, useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const CreateLifecycle = () => {
	const {channelId, groupId} = useParams();

	const history = useHistory();

	const dispatch = useDispatch();

	const [stageConfigs, setStageConfigs] = useState<IStageConfig[]>(
		createDefaultStageConfigs
	);

	const [lifecycleName, setLifecycleName] = useState('');

	const {data: lifecycles, loading} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycles,
		variables: {groupId: groupId!},
	});

	const {
		data: catalogFields,
		error: catalogError,
		loading: catalogLoading,
		refetch: refetchCatalog,
	} = useRequest({
		dataSourceFn: API.catalog.fetchCatalogFields,
		variables: {
			entity: 'account',
			groupId: groupId!,
			pageSize: CATALOG_FIELDS_MAX_PAGE_SIZE,
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

	const canCreate =
		!!lifecycleName.trim() && stageConfigs.every(isStageConfigured);

	const lifecycleURL = toRoute(Routes.LIFECYCLE, {channelId, groupId});

	const goToDashboard = () => history.push(lifecycleURL);

	const handleCreate = () => {
		API.lifecycle
			.createLifecycle(
				buildCreateLifecyclePayload({
					channelId: channelId!,
					groupId: groupId!,
					name: lifecycleName,
					stageConfigs,
				})
			)
			.then(() => {
				dispatch(
					addAlert({
						alertType: Alert.Types.Success,
						message: Liferay.Language.get(
							'the-lifecycle-was-created-successfully'
						),
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
	};

	const updateStage = (index: number, value: IStageConfig) =>
		setStageConfigs((previous) =>
			previous.map((config, current) =>
				current === index ? value : config
			)
		);

	return (
		<div className="d-flex flex-column">
			<DocumentTitle title={Liferay.Language.get('lifecycle-settings')} />

			<LifecycleSettingsToolbar
				backURL={lifecycleURL}
				createDisabled={!canCreate}
				onCancel={goToDashboard}
				onCreate={handleCreate}
			/>

			<div className="justify-self-center d-inline-block mt-5 mx-auto">
				<Card>
					<Card.Body>
						<Card.Title>
							{Liferay.Language.get('stage-configuration')}
						</Card.Title>

						<p className="mt-3 text-secondary">
							{Liferay.Language.get(
								'define-entry-conditions-for-each-lifecycle-stage-an-account-moves-to-a-stage-when-it-meets-the-selected-conditions'
							)}
						</p>

						<ClayForm.Group className="mb-4">
							<label
								className="font-weight-semi-bold"
								htmlFor="lifecycleName"
							>
								{Liferay.Language.get('lifecycle-name')}

								<span className="reference-mark">
									<ClayIcon symbol="asterisk" />
								</span>
							</label>

							<ClayInput
								id="lifecycleName"
								onChange={(event) =>
									setLifecycleName(event.target.value)
								}
								sizing="sm"
								value={lifecycleName}
							/>
						</ClayForm.Group>

						{LIFECYCLE_STAGE_ORDER.map((stageType, index) => (
							<StageConfigurationPanel
								defaultExpanded={index === 0}
								fields={catalogFields?.items}
								index={index + 1}
								key={stageType}
								onChange={(value) => updateStage(index, value)}
								stageType={stageType}
								value={stageConfigs[index]}
							/>
						))}
					</Card.Body>
				</Card>
			</div>
		</div>
	);
};

export default CreateLifecycle;
