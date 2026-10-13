import AddConditionButton from 'shared/components/conditions-panel/AddConditionButton';
import ClayIcon from '@clayui/icon';
import ConditionChip from 'shared/components/condition-chip/ConditionChip';
import ConditionsPanel from 'shared/components/conditions-panel/ConditionsPanel';
import ConditionsSection from 'shared/components/conditions-panel/ConditionsSection';
import React from 'react';
import SortableConditionChip from 'shared/components/condition-chip/SortableConditionChip';
import {
	isConditionComplete,
	isStageComplete,
	isStageConfigured,
} from 'lifecycle/utils/lifecycleOperators';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';
import {noop} from 'lodash';
import {sub} from 'shared/util/lang';

const STAGE_DRAG_TYPE = 'lifecycle-stage-chip';

const DRAG_TYPES = [STAGE_DRAG_TYPE];

const ErrorIcon = () => (
	<span className="text-danger">
		<ClayIcon symbol="exclamation-full" />

		<span className="sr-only">{Liferay.Language.get('error')}</span>
	</span>
);

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

interface ILifecycleConditionsPanelProps {
	lifecycleName: string;
	onAddStage?: () => void;
	onMoveStage?: (movement: {from: number; to: number}) => void;
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

		<ConditionsSection
			action={
				<AddConditionButton
					label={Liferay.Language.get('add-stage')}
					onClick={onAddStage}
				/>
			}
			className="lifecycle-structure-section"
			title={Liferay.Language.get('structure')}
		>
			{!!stageConfigs.length && (
				<div className="c-gap-2 d-flex flex-column mt-3">
					{stageConfigs.map((stage, index) => {
						const number = index + 1;

						return (
							<SortableConditionChip
								dragType={STAGE_DRAG_TYPE}
								index={index}
								key={stage.key}
								label={
									stageErrorKeys.includes(stage.key) ? (
										<ErrorIcon />
									) : (
										stage.name
									)
								}
								name={
									stage.name ||
									(sub(Liferay.Language.get('stage-x'), [
										number,
									]) as string)
								}
								onMove={onMoveStage}
								onRemove={() => onRemoveStage(stage.key)}
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
			)}
		</ConditionsSection>
	</ConditionsPanel>
);

export default LifecycleConditionsPanel;
