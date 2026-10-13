import AddConditionButton from 'shared/components/conditions-panel/AddConditionButton';
import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ConditionChip from 'shared/components/condition-chip/ConditionChip';
import ConditionsPanel from 'shared/components/conditions-panel/ConditionsPanel';
import ConditionsSection from 'shared/components/conditions-panel/ConditionsSection';
import React, {useRef} from 'react';
import SortableConditionChip from 'shared/components/condition-chip/SortableConditionChip';
import useKeyboardReorder from 'shared/components/condition-chip/useKeyboardReorder';
import {
	isConditionComplete,
	isStageComplete,
	isStageConfigured,
} from 'lifecycle/utils/lifecycleOperators';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';
import {noop} from 'lodash';
import {sub} from 'shared/util/lang';
import {useAnnounce} from 'shared/components/condition-chip/AnnounceContext';

const STAGE_DRAG_TYPE = 'lifecycle-stage-chip';

const DRAG_TYPES = [STAGE_DRAG_TYPE];

type Movement = {from: number; to: number};

const ErrorIcon = () => (
	<span className="text-danger">
		<ClayIcon symbol="exclamation-full" />

		<span className="sr-only">{Liferay.Language.get('error')}</span>
	</span>
);

const getStageName = (stage: IStageConfig, number: number) =>
	stage.name || (sub(Liferay.Language.get('stage-x'), [number]) as string);

const getStageOverline = (stage: IStageConfig, number: number) => {
	if (!isStageConfigured(stage)) {
		return sub(Liferay.Language.get('stage-x-no-trigger'), [number]);
	}

	const count = stage.conditions.filter(isConditionComplete).length;

	if (count === 1) {
		return sub(Liferay.Language.get('stage-x-1-condition'), [number]);
	}

	return sub(Liferay.Language.get('stage-x-x-conditions'), [number, count]);
};

interface ILifecycleStructureProps {
	onAddStage: () => void;
	onMoveStage: (movement: Movement) => void;
	onRemoveStage: (key: string) => void;
	onSelect: (key: string | null) => void;
	selectedKey: string | null;
	stageConfigs: IStageConfig[];
	stageErrorKeys: string[];
}

const LifecycleStructure: React.FC<ILifecycleStructureProps> = ({
	onAddStage,
	onMoveStage,
	onRemoveStage,
	onSelect,
	selectedKey,
	stageConfigs,
	stageErrorKeys,
}) => {
	const announce = useAnnounce();

	const sectionRef = useRef<HTMLElement>(null);

	const getKeyboardProps = useKeyboardReorder({
		count: stageConfigs.length,
		getName: (index) => getStageName(stageConfigs[index], index + 1),
		onMove: onMoveStage,
	});

	const addStage = () => {
		onAddStage();

		announce(
			sub(Liferay.Language.get('added-x'), [
				sub(Liferay.Language.get('stage-x'), [
					stageConfigs.length + 1,
				]) as string,
			]) as string
		);
	};

	const removeStage = (stage: IStageConfig, number: number) => {
		onRemoveStage(stage.key);

		announce(
			sub(Liferay.Language.get('removed-x'), [
				getStageName(stage, number),
			]) as string
		);

		setTimeout(() => sectionRef.current?.focus());
	};

	return (
		<ConditionsSection
			action={
				<AddConditionButton
					label={Liferay.Language.get('add-stage')}
					onClick={addStage}
				/>
			}
			className="lifecycle-structure-section"
			ref={sectionRef}
			title={Liferay.Language.get('structure')}
		>
			{stageConfigs.length ? (
				<div className="c-gap-2 d-flex flex-column mt-3">
					{stageConfigs.map((stage, index) => {
						const number = index + 1;

						return (
							<SortableConditionChip
								dragType={STAGE_DRAG_TYPE}
								index={index}
								key={stage.key}
								keyboard={getKeyboardProps(index)}
								label={
									stageErrorKeys.includes(stage.key) ? (
										<ErrorIcon />
									) : (
										stage.name
									)
								}
								name={getStageName(stage, number)}
								onMove={onMoveStage}
								onRemove={() => removeStage(stage, number)}
								onSelect={() => onSelect(stage.key)}
								overline={getStageOverline(stage, number)}
								selected={selectedKey === stage.key}
								sticker={{
									displayType: isStageComplete(stage)
										? 'success'
										: 'warning',
								}}
							/>
						);
					})}
				</div>
			) : (
				<ClayEmptyState
					className="text-center"
					description={Liferay.Language.get(
						'to-create-a-new-lifecycle-add-stages-to-define-its-structure'
					)}
					small
					title={Liferay.Language.get('no-stages-yet')}
				>
					<ClayButton
						displayType="secondary"
						onClick={addStage}
						size="sm"
					>
						{Liferay.Language.get('new')}
					</ClayButton>
				</ClayEmptyState>
			)}
		</ConditionsSection>
	);
};

interface ILifecycleConditionsPanelProps {
	lifecycleName: string;
	onAddStage?: () => void;
	onMoveStage?: (movement: Movement) => void;
	onRemoveStage?: (key: string) => void;
	onSelect: (key: string | null) => void;
	selectedKey: string | null;
	stageConfigs: IStageConfig[];
	stageErrorKeys?: string[];
	summaryError?: boolean;
}

const LifecycleConditionsPanel: React.FC<ILifecycleConditionsPanelProps> = ({
	lifecycleName,
	onAddStage = noop,
	onMoveStage = noop,
	onRemoveStage = noop,
	onSelect,
	selectedKey,
	stageConfigs,
	stageErrorKeys = [],
	summaryError = false,
}) => (
	<ConditionsPanel
		dragTypes={DRAG_TYPES}
		title={Liferay.Language.get('lifecycle-conditions')}
	>
		<div className="border-bottom px-4 py-3">
			<ConditionChip
				label={summaryError ? <ErrorIcon /> : lifecycleName}
				name={Liferay.Language.get('configuration-summary')}
				onSelect={() => onSelect(null)}
				overline={Liferay.Language.get('configuration-summary')}
				selected={selectedKey === null}
				staticSticker
				sticker={{symbol: 'plant'}}
			/>
		</div>

		<LifecycleStructure
			onAddStage={onAddStage}
			onMoveStage={onMoveStage}
			onRemoveStage={onRemoveStage}
			onSelect={onSelect}
			selectedKey={selectedKey}
			stageConfigs={stageConfigs}
			stageErrorKeys={stageErrorKeys}
		/>
	</ConditionsPanel>
);

export default LifecycleConditionsPanel;
