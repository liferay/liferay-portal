import React from 'react';
import {formatDateToTimeZone} from 'shared/util/date';
import {
	getBestVariant,
	getMetricName,
	mergedVariants,
	toThousandsABTesting,
} from 'experiments/util/experiments';
import {IExperiment} from './types';
import {MetricName} from 'experiments/util/types';
import {sub} from 'shared/util/lang';
import {SummaryAlert} from './SummaryAlert';
import {SummaryBaseCard} from './SummaryBaseCard';
import {SummaryParagraph} from './SummaryParagraph';
import {SummarySection} from './SummarySection';
import {SummaryTitle} from './SummaryTitle';
import {formatPercent, toRounded} from 'shared/util/numbers';

export const SummaryTerminatedCard: React.FC<{
	experiment: IExperiment & {
		description?: string;
		finishedDate?: string;
		metrics: {
			completion: number;
			elapsedDays: number;
			estimatedDaysLeft?: number;
			variantMetrics: IExperiment['dxpVariants'];
		};
		sessions: number;
		startedDate?: string;
	};
	timeZoneId: string;
}> = ({experiment, timeZoneId}) => {
	const {
		description,
		dxpVariants,
		finishedDate,
		goal,
		metrics: {completion, elapsedDays, estimatedDaysLeft, variantMetrics},
		sessions,
		startedDate,
		status,
		winnerDXPVariantId,
	} = experiment;

	const variants = mergedVariants(dxpVariants, variantMetrics);

	const bestVariant = getBestVariant({
		dxpVariants: experiment.dxpVariants,
		goal: goal as {metric: MetricName} | undefined,
		metrics: experiment.metrics,
	});

	const secondPlaceVariant = variants.find(
		({dxpVariantId}) => dxpVariantId !== winnerDXPVariantId
	);

	const secondPlaceVariantMetrics = variantMetrics.find(
		({dxpVariantId}) => dxpVariantId !== winnerDXPVariantId
	);

	const winnerVariant = variants.find(
		({dxpVariantId}) => dxpVariantId === winnerDXPVariantId
	);

	const winnerVariantMetrics = variantMetrics.find(
		({dxpVariantId}) => dxpVariantId === winnerDXPVariantId
	);

	return (
		<SummaryBaseCard status={status.toLowerCase()}>
			<SummaryBaseCard.Header
				Description={() => (
					<div className="date">
						<div>
							{sub(Liferay.Language.get('started-x'), [
								formatDateToTimeZone(
									startedDate,
									'll',
									timeZoneId
								),
							])}
						</div>

						{finishedDate && (
							<div>
								{sub(Liferay.Language.get('stopped-x'), [
									formatDateToTimeZone(
										finishedDate,
										'll',
										timeZoneId
									),
								])}
							</div>
						)}
					</div>
				)}
				title={Liferay.Language.get('test-was-terminated')}
			/>

			{winnerDXPVariantId ? (
				winnerVariant?.dxpVariantId !== 'DEFAULT' ? (
					<SummaryAlert symbol="exclamation-circle">
						<SummaryTitle
							className="font-weight-bold mb-1"
							label={
								sub(
									Liferay.Language.get(
										'x-has-outperformed-x-by-at-least-x-percent'
									),
									[
										winnerVariant?.dxpVariantName,
										secondPlaceVariant?.dxpVariantName,
										toRounded(
											Math.abs(
												winnerVariantMetrics?.improvement ??
													0
											),
											2
										),
									]
								) as string
							}
						/>

						<strong>
							{Liferay.Language.get(
								'while-some-improvement-was-observed-the-current-test-has-not-gathered-sufficient-data-to-confidently-determine-a-winner'
							)}
						</strong>
					</SummaryAlert>
				) : (
					<SummaryAlert symbol="exclamation-circle">
						<SummaryTitle
							className="font-weight-bold mb-1"
							label={
								sub(
									Liferay.Language.get(
										'x-has-outperformed-x-by-at-least-x-percent'
									),
									[
										winnerVariant?.dxpVariantName,
										secondPlaceVariant?.dxpVariantName,
										toRounded(
											Math.abs(
												secondPlaceVariantMetrics?.improvement ??
													0
											),
											2
										),
									]
								) as string
							}
						/>

						<strong>
							{Liferay.Language.get(
								'while-some-improvement-was-observed-the-current-test-has-not-gathered-sufficient-data-to-confidently-determine-a-winner'
							)}
						</strong>
					</SummaryAlert>
				)
			) : (
				<SummaryAlert symbol="exclamation-circle">
					<SummaryTitle
						className="font-weight-bold mb-1"
						label={Liferay.Language.get('there-is-no-clear-winner')}
					/>

					<strong>
						{Liferay.Language.get(
							'while-some-improvement-was-observed-the-current-test-has-not-gathered-sufficient-data-to-confidently-determine-a-winner'
						)}
					</strong>
				</SummaryAlert>
			)}

			<SummaryBaseCard.Body>
				<div className="w-100 mt-4">
					<SummaryParagraph
						description={description}
						title={Liferay.Language.get('summary')}
					/>

					<div className="analytics-summary-card-sections">
						<SummarySection
							title={Liferay.Language.get('test-completion')}
						>
							<SummarySection.Heading
								value={formatPercent(completion)}
							/>

							<SummarySection.ProgressBar
								value={Math.floor(completion)}
							/>
						</SummarySection>

						<SummarySection
							title={Liferay.Language.get('days-running')}
						>
							<SummarySection.Heading
								value={String(elapsedDays)}
							/>

							{!!estimatedDaysLeft && (
								<SummarySection.Description
									value={String(
										sub(
											Liferay.Language.get(
												'about-x-days-left'
											),
											[estimatedDaysLeft]
										)
									)}
								/>
							)}
						</SummarySection>

						<SummarySection
							title={Liferay.Language.get('total-test-sessions')}
						>
							<SummarySection.Heading
								value={toThousandsABTesting(sessions)}
							/>
						</SummarySection>

						{goal && (
							<SummarySection
								title={Liferay.Language.get('test-metric')}
							>
								<SummarySection.MetricType
									value={getMetricName(
										goal.metric as MetricName
									)}
								/>
								{bestVariant?.improvement !== undefined &&
									bestVariant.improvement > 0 && (
										<SummarySection.Variant
											lift={formatPercent(
												bestVariant.improvement
											)}
											status="up"
										/>
									)}
							</SummarySection>
						)}
					</div>
				</div>
			</SummaryBaseCard.Body>
		</SummaryBaseCard>
	);
};
