import Card from 'shared/components/Card';
import ClayEmptyState from '@clayui/empty-state';
import ClayPopover from '@clayui/popover';
import Label from '@clayui/label';
import Loading from 'shared/components/Loading';
import React from 'react';
import {ButtonWithIcon, Icon, Text} from '@clayui/core';
import {ClayButtonWithIcon} from '@clayui/button';
import {ILifecycleStage} from 'lifecycle/utils/types';
import {SectionHeader} from 'shared/components/SectionHeader';
import {getPluralMessage, sub} from 'shared/util/lang';
import {formatPercent, toRounded, toThousands} from 'shared/util/numbers';
import {useHorizontalScroll} from 'shared/hooks/useHorizontalScroll';
import {useLifecycle} from 'lifecycle/context/LifecycleContext';
import {sortBy} from 'lodash';

const LOADING_CARD_MIN_HEIGHT = 432;
const REFERENCE_BAR_HEIGHT = 136;
const MIN_BAR_HEIGHT = 4;

const getBarHeight = (percentage: number, referencePercentage: number) => {
	if (referencePercentage <= 0) return MIN_BAR_HEIGHT;
	return (
		(percentage / referencePercentage) * REFERENCE_BAR_HEIGHT ||
		MIN_BAR_HEIGHT
	);
};

const getAverageDaysLabel = (averageStageDuration: number) =>
	(
		getPluralMessage(
			Liferay.Language.get('1-avg.-day'),
			Liferay.Language.get('x-avg.-days'),
			Math.round(averageStageDuration * 10) / 10,
			true,
			[toRounded(averageStageDuration)]
		) as string
	).toLowerCase();

interface IStageDescriptionProps {
	description: string;
	name: string;
}

const StageDescription = ({description, name}: IStageDescriptionProps) => (
	<ClayPopover
		alignPosition="top"
		closeOnClickOutside
		header={name}
		trigger={
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('more-information')}
				className="ml-1 text-secondary"
				displayType="unstyled"
				size="xs"
				symbol="question-circle-full"
			/>
		}
	>
		{description}
	</ClayPopover>
);

interface IStageMetricsProps {
	onFilterClick: (stageId: string) => void;
	referencePercentage: number;
	stage: ILifecycleStage;
}

const StageMetrics = ({
	onFilterClick,
	referencePercentage,
	stage: {
		accountsCount,
		averageStageDuration,
		conversionRateToNextStage,
		description,
		id,
		percentage,
		stageName: name,
	},
}: IStageMetricsProps) => {
	const barHeight = getBarHeight(percentage, referencePercentage);

	const barClassName =
		percentage > 0 ? 'stage-metrics__bar' : 'stage-metrics__bar--empty';

	const titleId = `stage-metrics-title-${id}`;

	return (
		<div className="border d-flex flex-column p-3 rounded-lg stage-metrics">
			<div className="align-items-center d-flex mb-3">
				<Text id={titleId} size={4} weight="semi-bold">
					{name}
				</Text>

				{description && (
					<StageDescription description={description} name={name} />
				)}

				<ButtonWithIcon
					aria-labelledby={titleId}
					borderless
					className="ml-auto"
					data-tooltip-align="top"
					displayType="secondary"
					onClick={() => onFilterClick(id)}
					size="sm"
					symbol="filter"
					title={
						sub(Liferay.Language.get('filter-by-x'), [
							name,
						]) as string
					}
				/>
			</div>

			<div className="mt-auto">
				<p className="mb-0">
					<Text size={7} weight="bold">
						{toThousands(accountsCount)}
					</Text>
				</p>

				<Text color="secondary" size={4}>
					{(
						sub(Liferay.Language.get('x-percent-of-all-accounts'), [
							toRounded(percentage),
						]) as string
					).toLowerCase()}
				</Text>
			</div>

			<div className="mt-1">
				<Text color="secondary" size={4}>
					{averageStageDuration != 0 ? (
						getAverageDaysLabel(averageStageDuration)
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
				</Text>
			</div>

			<div
				className="align-items-end d-flex mt-3 position-relative"
				style={{height: REFERENCE_BAR_HEIGHT}}
			>
				<div
					className={`${barClassName} rounded-lg w-100`}
					style={{height: barHeight}}
				/>

				{conversionRateToNextStage != null && (
					<Label
						className="m-0 stage-metrics__conversion"
						displayType="info"
						inverse
					>
						<span className="inline-item ml-1">
							{formatPercent(conversionRateToNextStage, 0)}

							<span className="sr-only">
								{Liferay.Language.get(
									'conversion-to-next-stage'
								)}
							</span>

							<Icon symbol="angle-double-right-small" />
						</span>
					</Label>
				)}
			</div>
		</div>
	);
};

interface ILifecycleChartProps {
	description?: string;
	error?: boolean;
	loading?: boolean;
	name?: string;
	stages?: ILifecycleStage[];
}

interface IStageCardsProps {
	stages: ILifecycleStage[];
}

const StageCards = ({stages}: IStageCardsProps) => {
	const {selectStage} = useLifecycle();

	const {
		canScrollNext,
		canScrollPrevious,
		scrollNext,
		scrollPrevious,
		trackRef,
		updateArrows,
	} = useHorizontalScroll(stages.length);

	const referencePercentage = Math.max(
		...stages.map((stage) => stage.percentage),
		0
	);

	return (
		<div className="mt-4">
			<div
				className="c-gap-3 d-flex overflow-auto stage-metrics-track"
				onScroll={updateArrows}
				ref={trackRef}
			>
				{stages.map((stage) => (
					<StageMetrics
						key={stage.id}
						onFilterClick={selectStage}
						referencePercentage={referencePercentage}
						stage={stage}
					/>
				))}
			</div>

			{(canScrollPrevious || canScrollNext) && (
				<div className="align-items-center c-gap-3 d-flex justify-content-center mt-3">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('previous')}
						borderless
						disabled={!canScrollPrevious}
						displayType="secondary"
						onClick={scrollPrevious}
						size="sm"
						symbol="angle-left"
					/>

					<Text color="secondary" size={3}>
						{Liferay.Language.get('scroll-to-view-all-stages')}
					</Text>

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('next')}
						borderless
						disabled={!canScrollNext}
						displayType="secondary"
						onClick={scrollNext}
						size="sm"
						symbol="angle-right"
					/>
				</div>
			)}
		</div>
	);
};

const LifecycleChart = ({
	description,
	error,
	loading,
	name,
	stages,
}: ILifecycleChartProps) => {
	if (!loading && !error && !stages?.length) {
		return null;
	}

	const renderStages = () => {
		if (loading) {
			return <Loading />;
		}

		if (error) {
			return (
				<ClayEmptyState
					className="text-center"
					description={Liferay.Language.get(
						'lifecycle-data-will-appear-here-when-synced'
					)}
					small
					title={Liferay.Language.get('no-lifecycle-data-available')}
				/>
			);
		}

		return <StageCards stages={sortBy(stages, 'priority')} />;
	};

	return (
		<>
			<SectionHeader
				icon="polls"
				title={Liferay.Language.get('lifecycle-stages')}
			/>
			<Card
				className="p-3"
				minHeight={loading ? LOADING_CARD_MIN_HEIGHT : undefined}
			>
				<Card.Title>{name}</Card.Title>
				<Card.Body noPadding>
					<div className="mt-1">
						<Text color="secondary" size={3}>
							{description}
						</Text>

						{renderStages()}
					</div>
				</Card.Body>
			</Card>
		</>
	);
};

export default LifecycleChart;
