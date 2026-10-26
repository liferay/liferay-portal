import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import processingStateReducedMotionURL from '../../../images/states/lifecycle_processing_state_reduced_motion.svg?url';
import processingStateURL from '../../../images/states/lifecycle_processing_state.svg?url';
import AccountsDataSet from 'shared/components/accounts-data-set/AccountsDataSet';
import BasePage from 'shared/components/base-page';
import ClayEmptyState from '@clayui/empty-state';
import FilterPicker from '../components/FilterPicker';
import getCN from 'classnames';
import LifecycleSwitchPanel, {
	LifecyclePanelView,
} from 'lifecycle/components/LifecycleSwitchPanel';
import LifecycleChart from 'lifecycle/components/LifecycleChart';
import LifecycleNotFound from 'lifecycle/components/LifecycleNotFound';
import LifecycleSticker from 'lifecycle/components/LifecycleSticker';
import LifecyclesPage from 'lifecycle/components/LifecyclesPage';
import TrailingNinetyDayRange from 'shared/components/TrailingNinetyDayRange';
import Loading from 'shared/components/Loading';
import OverviewSection from '../components/OverviewSection';
import React, {useContext, useRef, useState} from 'react';
import SegmentDropdown from 'shared/components/SegmentDropdown';
import {ChannelContext} from 'shared/context/channel';
import {ClayButtonWithIcon} from '@clayui/button';
import {
	LifecycleContextProvider,
	useLifecycle,
} from '../context/LifecycleContext';
import {LifecycleAccountDataEmptyState} from 'lifecycle/components/LifecycleEmptyStates';
import {useParams} from 'react-router-dom';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {Routes, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLifecycleAccountData} from 'lifecycle/hooks/useLifecycleAccountData';
import {useLifecycleActions} from 'lifecycle/hooks/useLifecycleActions';
import {useRequest} from 'shared/hooks/useRequest';
import {useSegmentFilter} from 'shared/hooks/useSegmentFilter';
import {noop} from 'lodash';
import {AccountDataStatus} from 'lifecycle/utils/types';

const ProcessingLifecycleEmptyState = () => (
	<div className="card mt-4 py-8">
		<ClayEmptyState
			description={Liferay.Language.get(
				'your-configuration-is-complete.-metrics-will-appear-in-the-dashboard-once-processing-is-complete'
			)}
			imgSrc={processingStateURL}
			imgSrcReducedMotion={processingStateReducedMotionURL}
			title={Liferay.Language.get('your-dashboard-is-almost-ready')}
		/>
	</div>
);

const LifecycleOverview = () => {
	const {filters, lifecycleId} = useLifecycle();

	const {groupId} = useParams();

	const {data: overviewData, loading: overviewLoading} = useRequest({
		dataSourceFn: API.lifecycle.fetchOverviewMetrics,
		variables: {
			country: filters.countryFilter,
			groupId: groupId!,
			industry: filters.industryFilter,
			lifecycleId,
		},
	});

	return <OverviewSection loading={overviewLoading} metrics={overviewData} />;
};

interface ILifecycleStagesSectionProps {
	description?: string;
	name?: string;
}

const LifecycleStagesSection = ({
	description,
	name,
}: ILifecycleStagesSectionProps) => {
	const {filters, lifecycleId} = useLifecycle();

	const {groupId} = useParams();

	const {segmentId} = useSegmentFilter();

	const {
		data: stagesData,
		error: stagesError,
		loading: stagesLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycleStages,
		variables: {
			country: filters.countryFilter,
			groupId,
			industry: filters.industryFilter,
			lifecycleId,
			segmentId,
		},
	});

	return (
		<LifecycleChart
			description={description}
			error={!!stagesError}
			loading={stagesLoading}
			name={name}
			stages={stagesData}
		/>
	);
};

const LifecycleAccounts = () => {
	const {filters, lifecycleId, stageSelectionNonce} = useLifecycle();

	const {channelId, groupId} = useParams();

	const {segmentId, segmentName} = useSegmentFilter();

	return (
		<section>
			<SectionHeader
				icon="box-container"
				title={Liferay.Language.get('accounts')}
			/>

			<AccountsDataSet
				accountLifecycleId={lifecycleId}
				apiURL={`/o/faro/contacts/${groupId!}/account-lifecycle/${lifecycleId}/accounts`}
				channelId={channelId!}
				countryFilter={filters.countryFilter}
				groupId={groupId!}
				industryFilter={filters.industryFilter}
				lifecycleStageFilter={filters.lifecycleStageFilter}
				rangeKeyFilter={RangeKeyTimeRanges.Last30Days}
				segmentFilter={segmentId}
				segmentName={segmentName}
				stageSelectionNonce={stageSelectionNonce}
			/>
		</section>
	);
};

const BaseLifecycle = () => {
	const currentUser = useCurrentUser();
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, lifecycleId} = useParams();

	const {refetch: refetchAccountData, status: accountDataStatus} =
		useLifecycleAccountData();

	const {segmentId, segmentName, setSegment} = useSegmentFilter();

	const {
		data: lifecycle,
		error: lifecycleError,
		loading: lifecycleLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycle,
		variables: {groupId: groupId!, lifecycleId: lifecycleId!},
	});

	const switchButtonRef = useRef<HTMLButtonElement>(null);

	const [panelView, setPanelView] = useState<LifecyclePanelView | null>(null);

	const [defaultChange, setDefaultChange] = useState<{
		count: number;
		lifecycleId: string | null;
	}>({count: 0, lifecycleId: null});

	const defaultLifecycleId = defaultChange.lifecycleId;

	const changeDefault = (nextDefaultLifecycleId: string) =>
		setDefaultChange(({count}) => ({
			count: count + 1,
			lifecycleId: nextDefaultLifecycleId,
		}));

	const defaultAccountLifecycle = defaultLifecycleId
		? defaultLifecycleId === lifecycle?.id
		: lifecycle?.defaultAccountLifecycle;

	const openLifecycle = lifecycle && {...lifecycle, defaultAccountLifecycle};

	const refreshDefault = () =>
		API.lifecycle
			.fetchLifecycles({
				channelId,
				defaultAccountLifecycle: true,
				groupId: groupId!,
			})
			.then((lifecycles) => {
				const nextDefault = lifecycles.find(
					({defaultAccountLifecycle}) => defaultAccountLifecycle
				);

				if (nextDefault) {
					changeDefault(nextDefault.id);
				}
			})
			.catch(noop);

	const {actions, manageNotificationsModal} = useLifecycleActions({
		lifecycle: openLifecycle,
		onDefaultChange: () => changeDefault(lifecycle!.id),
		onShowDetails: () =>
			setPanelView({lifecycleId: lifecycle!.id, type: 'details'}),
	});

	const processing = lifecycle?.processedDate == null;

	const authorized = currentUser.isAdmin();

	const newLifecycleActions = [
		{
			button: true,
			className: 'rounded-lg',
			displayType: 'primary',
			href: toRoute(Routes.LIFECYCLE_CREATE, {channelId, groupId}),
			label: Liferay.Language.get('new-lifecycle'),
			state: {lifecycleId},
		},
	];

	const title = lifecycle?.name ?? '';

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
					authorized={authorized}
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

	return (
		<BasePage documentTitle={title || Liferay.Language.get('lifecycles')}>
			<LifecycleContextProvider
				key={lifecycleId}
				lifecycleId={lifecycle?.id}
			>
				<BasePage.Header
					breadcrumbs={[
						breadcrumbs.getHome({
							channelId: channelId!,
							groupId: groupId!,
							label: selectedChannel?.name,
						}),
						breadcrumbs.getLifecycles({
							channelId: channelId!,
							groupId: groupId!,
						}),
						...(lifecycle?.name
							? [
									breadcrumbs.getEntityName({
										label: lifecycle.name,
									}),
								]
							: []),
					]}
					fluid
					groupId={groupId!}
				>
					<BasePage.Row>
						<BasePage.Header.TitleSection
							className="mb-3"
							sticker={
								defaultAccountLifecycle && (
									<LifecycleSticker className="mr-2" pinned />
								)
							}
							title={title}
						>
							{!!actions.length && (
								<BasePage.Header.PageActionsToolbar
									actions={actions}
								/>
							)}

							<ClayButtonWithIcon
								aria-expanded={!!panelView}
								aria-label={Liferay.Language.get(
									'switch-dashboard'
								)}
								borderless
								className={getCN(
									'ml-2 rounded text-secondary',
									{active: !!panelView}
								)}
								data-tooltip-align="top"
								displayType="secondary"
								onClick={() =>
									setPanelView(
										panelView ? null : {type: 'switch'}
									)
								}
								ref={switchButtonRef}
								size="sm"
								symbol="change"
								title={Liferay.Language.get('switch-dashboard')}
							/>
						</BasePage.Header.TitleSection>

						{authorized && (
							<BasePage.Header.Section className="mb-3">
								<BasePage.Header.PageActions
									actions={newLifecycleActions}
								/>
							</BasePage.Header.Section>
						)}
					</BasePage.Row>
				</BasePage.Header>
				{!processing && (
					<BasePage.SubHeader fluid>
						<div className="d-flex justify-content-between w-100">
							<div className="d-flex">
								<FilterPicker
									className="mr-3"
									entityLabel={Liferay.Language.get(
										'industries'
									)}
									fieldMappingFieldName="industry"
									filterKey="industryFilter"
								/>

								<FilterPicker
									className="mr-3"
									entityLabel={Liferay.Language.get(
										'countries'
									)}
									fieldMappingFieldName="country"
									filterKey="countryFilter"
								/>

								<SegmentDropdown
									initialSegmentId={segmentId}
									initialSegmentName={segmentName}
									onFilterChange={setSegment}
								/>
							</div>

							<TrailingNinetyDayRange />
						</div>
					</BasePage.SubHeader>
				)}
				<BasePage.Body fluid>
					{processing ? (
						<ProcessingLifecycleEmptyState />
					) : (
						<>
							<LifecycleOverview />

							<LifecycleStagesSection
								description={lifecycle.description}
								name={lifecycle.name}
							/>

							<LifecycleAccounts />
						</>
					)}
				</BasePage.Body>
			</LifecycleContextProvider>

			<LifecycleSwitchPanel
				defaultChangeCount={defaultChange.count}
				onDefaultChange={changeDefault}
				onDefaultDelete={refreshDefault}
				onViewChange={setPanelView}
				openLifecycle={openLifecycle}
				triggerRef={switchButtonRef}
				view={panelView}
			/>

			{manageNotificationsModal}
		</BasePage>
	);
};

export default BaseLifecycle;
