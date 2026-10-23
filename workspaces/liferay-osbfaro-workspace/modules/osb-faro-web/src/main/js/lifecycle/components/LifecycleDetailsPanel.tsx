import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ClayPanel from '@clayui/panel';
import ClayTabs from '@clayui/tabs';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSticker from './LifecycleSticker';
import Loading from 'shared/components/Loading';
import React, {useState} from 'react';
import StageTrigger from './StageTrigger';
import {formatDateToTimeZone, NUMERIC_DATE_TIME_FORMAT} from 'shared/util/date';
import {
	getMaxTimeInStageLabel,
	getStageCountLabel,
} from 'lifecycle/utils/lifecycleLabels';
import {getStageTrigger} from 'lifecycle/utils/stageTrigger';
import {ILifecycleDetail, ILifecycleStage} from 'shared/api/lifecycle';
import {PageActions} from 'shared/components/base-page/Header';
import {SidePanel, Text} from '@clayui/core';
import {useLifecycleActions} from 'lifecycle/hooks/useLifecycleActions';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {useTimeZone} from 'shared/hooks/useTimeZone';
import {sortBy} from 'lodash';

interface IField {
	label: string;
	secondary?: boolean;
	value?: React.ReactNode;
}

const FieldList: React.FC<{fields: IField[]}> = ({fields}) => (
	<dl className="mb-0">
		{fields
			.filter(({value}) => !!value)
			.map(({label, secondary, value}) => (
				<div className="mb-3" key={label}>
					<dt>
						<Text size={3} weight="semi-bold">
							{label}
						</Text>
					</dt>

					<dd className="mb-0">
						<Text
							color={secondary ? 'secondary' : undefined}
							size={3}
						>
							{value}
						</Text>
					</dd>
				</div>
			))}
	</dl>
);

const Section: React.FC<{children: React.ReactNode; title: string}> = ({
	children,
	title,
}) => (
	<ClayPanel
		collapsable={false}
		displayTitle={
			<ClayPanel.Header className="border-bottom">
				<ClayPanel.Title className="panel-title text-secondary text-uppercase">
					{title}
				</ClayPanel.Title>
			</ClayPanel.Header>
		}
		displayType="unstyled"
	>
		<ClayPanel.Body>{children}</ClayPanel.Body>
	</ClayPanel>
);

const StageCard: React.FC<{stage: ILifecycleStage}> = ({stage}) => {
	const trigger = getStageTrigger(stage);

	return (
		<Card className="mb-3 p-3">
			<Section title={Liferay.Language.get('stage-info')}>
				<FieldList
					fields={[
						{
							label: Liferay.Language.get('name'),
							value: stage.stageName,
						},
						{
							label: Liferay.Language.get('description'),
							secondary: true,
							value: stage.description,
						},
						{
							label: Liferay.Language.get('max-time-in-stage'),
							value: getMaxTimeInStageLabel(stage.maxDuration),
						},
					]}
				/>
			</Section>

			{!!trigger.conditions.length && (
				<Section title={Liferay.Language.get('stage-trigger')}>
					<StageTrigger trigger={trigger} />
				</Section>
			)}
		</Card>
	);
};

const StagesTab: React.FC<{stages: ILifecycleStage[]}> = ({stages}) => (
	<>
		{sortBy(stages, 'priority').map((stage) => (
			<StageCard key={stage.id} stage={stage} />
		))}
	</>
);

interface ILifecycleDetailsPanelProps {
	lifecycle?: ILifecycleDetail;
	lifecycleId: string;
	onBack?: () => void;
	onDefaultChange?: (lifecycleId: string) => void;
	onView?: () => void;
}

const LifecycleDetailsPanel: React.FC<ILifecycleDetailsPanelProps> = ({
	lifecycle: openLifecycle,
	lifecycleId,
	onBack,
	onDefaultChange,
	onView,
}) => {
	const {groupId} = useParams();

	const [activeTab, setActiveTab] = useState(0);

	const {timeZoneId} = useTimeZone();

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycle,
		skipRequest: !!openLifecycle,
		variables: {groupId: groupId!, lifecycleId},
	});

	const lifecycle = openLifecycle ?? data;

	const actions = useLifecycleActions({
		lifecycle,
		onDefaultChange: () => {
			if (!openLifecycle) {
				refetch();
			}

			onDefaultChange?.(lifecycleId);
		},
		onView,
	});

	const renderBody = () => {
		if (!openLifecycle && loading) {
			return <Loading />;
		}

		if (error || !lifecycle) {
			return <ErrorDisplay onReload={refetch} spacer />;
		}

		return (
			<>
				<ClayTabs
					active={activeTab}
					className="px-4"
					onActiveChange={setActiveTab}
				>
					<ClayTabs.Item>
						{Liferay.Language.get('details')}
					</ClayTabs.Item>

					<ClayTabs.Item>
						{Liferay.Language.get('stages')}
					</ClayTabs.Item>
				</ClayTabs>

				<ClayTabs.Content activeIndex={activeTab} className="p-4">
					<ClayTabs.TabPane className="p-0">
						<FieldList
							fields={[
								{
									label: Liferay.Language.get('description'),
									value: lifecycle.description,
								},
								{
									label: Liferay.Language.get(
										'last-modified-by'
									),
									value: lifecycle.authorName,
								},
								{
									label: Liferay.Language.get(
										'modified-date'
									),
									value:
										lifecycle.modifiedDate != null &&
										formatDateToTimeZone(
											lifecycle.modifiedDate,
											NUMERIC_DATE_TIME_FORMAT,
											timeZoneId
										),
								},
								{
									label: Liferay.Language.get('stages'),
									value:
										lifecycle.stages &&
										getStageCountLabel(
											lifecycle.stages.length
										),
								},
							]}
						/>
					</ClayTabs.TabPane>

					<ClayTabs.TabPane className="p-0">
						<StagesTab stages={lifecycle.stages ?? []} />
					</ClayTabs.TabPane>
				</ClayTabs.Content>
			</>
		);
	};

	return (
		<>
			<SidePanel.Header
				className="px-4"
				messages={{
					backAriaLabel: Liferay.Language.get('back'),
					closeAriaLabel: Liferay.Language.get('close'),
				}}
				onBack={onBack}
			>
				<div className="align-items-center d-flex">
					<LifecycleSticker className="flex-shrink-0 mr-2" />

					<SidePanel.Title className="flex-grow-1 text-truncate">
						<Text size={6} weight="semi-bold">
							{lifecycle?.name}
						</Text>
					</SidePanel.Title>

					{!!actions.length && (
						<PageActions
							actions={actions}
							actionsDisplayLimit={0}
						/>
					)}
				</div>
			</SidePanel.Header>

			<SidePanel.Body className="p-0">{renderBody()}</SidePanel.Body>
		</>
	);
};

export default LifecycleDetailsPanel;
