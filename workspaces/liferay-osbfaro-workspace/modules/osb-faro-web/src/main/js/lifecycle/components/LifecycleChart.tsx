import Card from 'shared/components/Card';
import classNames from 'classnames';
import Label from '@clayui/label';
import Loading from 'shared/components/Loading';
import React from 'react';
import {ButtonWithIcon, Icon, Text} from '@clayui/core';
import {ILifecycleStage} from 'lifecycle/utils/types';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {SectionHeader} from 'shared/components/SectionHeader';
import {sub} from 'shared/util/lang';
import {formatPercent, toRounded, toThousands} from 'shared/util/numbers';
import {useLifecycle} from 'lifecycle/context/LifecycleContext';

const EMPTY_STAGES: ILifecycleStage[] = [
	LifecycleStages.AWARE,
	LifecycleStages.ENGAGED,
	LifecycleStages.PIPELINE,
	LifecycleStages.ONBOARDING,
	LifecycleStages.ESTABLISHED,
].map((stageType) => ({
	accountsCount: 0,
	averageStageDuration: 0,
	conversionRateToNextStage: 0,
	description: '',
	percentage: 0,
	stageType,
}));

const REFERENCE_BAR_HEIGHT = 136;
const MIN_BAR_HEIGHT = 4;

const getBarHeight = (percentage: number, referencePercentage: number) => {
	if (referencePercentage <= 0) return MIN_BAR_HEIGHT;
	return (
		(percentage / referencePercentage) * REFERENCE_BAR_HEIGHT ||
		MIN_BAR_HEIGHT
	);
};

interface IStageMetricsProps {
	accountsCount: number;
	averageDaysInStage: number;
	description: string;
	onFilterClick: (stageType: LifecycleStages) => void;
	percentage: number;
	placeholder?: boolean;
	referencePercentage: number;
	stageType: LifecycleStages;
}

const StageMetrics = ({
	accountsCount,
	averageDaysInStage,
	description,
	onFilterClick,
	percentage,
	placeholder,
	referencePercentage,
	stageType,
}: IStageMetricsProps) => {
	const barHeight = getBarHeight(percentage, referencePercentage);

	const barClassName =
		percentage > 0 ? 'stage-metrics__bar' : 'stage-metrics__bar--empty';

	const titleId = `stage-metrics-title-${stageType}`;

	return (
		<div className="col-12 col-lg d-flex flex-column p-3 stage-metrics">
			<div className="align-items-center d-flex mb-2">
				<Text id={titleId} size={4} weight="semi-bold">
					{lifecycleStagesLabelMap[stageType].label}
				</Text>
				<ButtonWithIcon
					aria-labelledby={titleId}
					borderless
					className="ml-auto"
					data-tooltip-align="top"
					displayType="secondary"
					onClick={() => onFilterClick(stageType)}
					size="sm"
					symbol="filter"
					title={
						sub(Liferay.Language.get('filter-by-x'), [
							lifecycleStagesLabelMap[stageType].label,
						]) as string
					}
				/>
			</div>
			<Text as="p" color="secondary">
				{description}
			</Text>
			<div className="justify-self-center mt-auto">
				<div>
					<p className="mb-0">
						<Text size={7} weight="bold">
							{toThousands(accountsCount)}
						</Text>
					</p>
					<Text color="secondary" size={4}>
						{(
							sub(
								Liferay.Language.get(
									'x-percent-of-all-accounts'
								),
								[toRounded(percentage)]
							) as string
						).toLowerCase()}
					</Text>
				</div>
				<div className="mt-3 text-secondary">
					{averageDaysInStage != 0 ? (
						<>
							<span className="mr-4">
								{toRounded(averageDaysInStage, 2)}
							</span>
							<span>
								{Liferay.Language.get('avg.-day').toLowerCase()}
							</span>
						</>
					) : (
						<>
							<span className="mr-4">{'—'}</span>
							<span>
								{Liferay.Language.get(
									'no-activity'
								).toLowerCase()}
							</span>
						</>
					)}
				</div>
			</div>
			<div
				className="align-items-end d-none d-lg-flex mt-3 mx-n2"
				style={{height: REFERENCE_BAR_HEIGHT}}
			>
				{!placeholder && (
					<div
						className={`${barClassName} rounded-lg w-100`}
						style={{height: barHeight}}
					/>
				)}
			</div>
		</div>
	);
};

interface IStageProgressionProps {
	nextBarHeight: number;
	percentage: number;
	placeholder?: boolean;
	previousBarHeight: number;
}

const StageProgression = ({
	nextBarHeight,
	percentage,
	placeholder,
	previousBarHeight,
}: IStageProgressionProps) => {
	const topLeft = REFERENCE_BAR_HEIGHT - previousBarHeight;
	const topRight = REFERENCE_BAR_HEIGHT - nextBarHeight;

	return (
		<div className="align-items-center align-items-lg-stretch border-light col-12 col-lg-auto d-flex flex-lg-column flex-row justify-content-center justify-content-lg-start px-2 py-3 stage-progression">
			<Label className="mt-lg-auto p-0" displayType="info">
				<span className="inline-item ml-1">
					{formatPercent(percentage, 0)}
					<span className="d-lg-none ml-1">
						{Liferay.Language.get(
							'conversion-to-next-stage'
						).toLowerCase()}
					</span>
					<Icon symbol="angle-right-small" />
				</span>
			</Label>
			<div
				className={classNames([
					'd-none',
					'd-lg-block',
					'mt-auto',
					'mx-n2',
					placeholder ? '' : 'bg-light stage-progression__fill',
				])}
				style={{
					clipPath: placeholder
						? undefined
						: `polygon(0 ${topLeft}px, 100% ${topRight}px, 100% 100%, 0 100%)`,
					height: REFERENCE_BAR_HEIGHT,
				}}
			/>
		</div>
	);
};

interface ILifecycleChartProps {
	error?: boolean;
	loading?: boolean;
	stages?: ILifecycleStage[];
}

const LifecycleChart = ({error, loading, stages}: ILifecycleChartProps) => {
	const {selectStage} = useLifecycle();

	const isEmpty = error || !stages?.length;

	const resolvedStages: ILifecycleStage[] = isEmpty
		? EMPTY_STAGES
		: stages.filter(
				(stage) => stage.stageType !== LifecycleStages.AT_RISK
			)!;

	const onFilterClick = (stageType: LifecycleStages) =>
		selectStage(stageType);

	const refPct = Math.max(
		...resolvedStages.map((stage) => stage.percentage),
		0
	);

	return (
		<>
			<SectionHeader
				icon="polls"
				title={Liferay.Language.get('lifecycle-stages')}
			/>
			<Card className="p-3">
				<Card.Title>
					{Liferay.Language.get('accounts-by-stage')}
				</Card.Title>
				<Card.Body noPadding>
					<div className="mt-1">
						<Text color="secondary" size={3}>
							{Liferay.Language.get(
								'the-distribution-of-accounts-across-the-lifecycle-stages-within-the-timeframe'
							)}
						</Text>
						{loading ? (
							<Loading className="mt-4" />
						) : (
							<div className="flex-lg-nowrap h-100 mt-4 no-gutters row">
								{resolvedStages.map((stage, index) => {
									const nextStage = resolvedStages[index + 1];
									return (
										<React.Fragment key={stage.stageType}>
											<StageMetrics
												accountsCount={
													stage.accountsCount
												}
												averageDaysInStage={
													stage.averageStageDuration
												}
												description={stage.description}
												onFilterClick={onFilterClick}
												percentage={stage.percentage}
												placeholder={isEmpty}
												referencePercentage={refPct}
												stageType={stage.stageType}
											/>
											{nextStage && (
												<StageProgression
													nextBarHeight={getBarHeight(
														nextStage.percentage,
														refPct
													)}
													percentage={
														stage.conversionRateToNextStage ??
														0
													}
													placeholder={isEmpty}
													previousBarHeight={getBarHeight(
														stage.percentage,
														refPct
													)}
												/>
											)}
										</React.Fragment>
									);
								})}
							</div>
						)}
					</div>
				</Card.Body>
			</Card>
		</>
	);
};

export default LifecycleChart;
