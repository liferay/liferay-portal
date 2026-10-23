import AddConditionButton from 'shared/components/conditions-panel/AddConditionButton';
import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
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
import {getPluralMessage, sub} from 'shared/util/lang';
import {useAnnounce} from 'shared/components/condition-chip/AnnounceContext';

export const STAGE_DRAG_TYPE = 'lifecycle-stage-chip';

type Movement = {from: number; to: number};

export const ErrorIcon = () => (
	<span className="text-danger">
		<ClayIcon symbol="exclamation-full" />

		<span className="sr-only">{Liferay.Language.get('error')}</span>
	</span>
);

const getStageName = (stage: IStageConfig, number: number) =>
	stage.name || (sub(Liferay.Language.get('stage-x'), [number]) as string);

const getStageTriggerLabel = (stage: IStageConfig) => {
	if (!isStageConfigured(stage)) {
		return Liferay.Language.get('no-trigger');
	}

	return getPluralMessage(
		Liferay.Language.get('1-condition'),
		Liferay.Language.get('x-conditions'),
		stage.conditions.filter(isConditionComplete).length,
		true
	) as string;
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
								overline={
									<>
										{sub(Liferay.Language.get('stage-x'), [
											number,
										])}

										{' | '}

										{getStageTriggerLabel(stage)}
									</>
								}
								selected={selectedKey === stage.key}
								sticker={
									isStageComplete(stage)
										? {
												displayType: 'success',
												symbol: 'check-circle',
											}
										: {
												displayType: 'warning',
												symbol: 'exclamation-circle',
											}
								}
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

export default LifecycleStructure;
