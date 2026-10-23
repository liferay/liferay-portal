import ConditionChip from 'shared/components/condition-chip/ConditionChip';
import ConditionsPanel from 'shared/components/conditions-panel/ConditionsPanel';
import LifecycleStructure, {
	ErrorIcon,
	STAGE_DRAG_TYPE,
} from 'lifecycle/components/LifecycleStructure';
import React from 'react';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';

const DRAG_TYPES = [STAGE_DRAG_TYPE];

interface ILifecycleConditionsPanelProps {
	lifecycleName: string;
	onAddStage: () => void;
	onMoveStage: (movement: {from: number; to: number}) => void;
	onRemoveStage: (key: string) => void;
	onSelect: (key: string | null) => void;
	selectedKey: string | null;
	stageConfigs: IStageConfig[];
	stageErrorKeys: string[];
	summaryError: boolean;
}

const LifecycleConditionsPanel: React.FC<ILifecycleConditionsPanelProps> = ({
	lifecycleName,
	onAddStage,
	onMoveStage,
	onRemoveStage,
	onSelect,
	selectedKey,
	stageConfigs,
	stageErrorKeys,
	summaryError,
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
