import React from 'react';
import {formatDateToTimeZone, getCustomDateFormat} from 'shared/util/date';
import {
	getBestVariant,
	getMetricName,
	toThousandsABTesting,
} from 'experiments/util/experiments';
import {IExperiment} from './types';
import {MetricName} from 'experiments/util/types';
import {sub} from 'shared/util/lang';
import {SummaryBaseCard} from './SummaryBaseCard';
import {SummaryParagraph} from './SummaryParagraph';
import {SummarySection} from './SummarySection';
import {formatPercent} from 'shared/util/numbers';

export const SummaryRunningCard: React.FC<{
	experiment: IExperiment & {
		description?: string;
		metrics: {
			completion: number;
			elapsedDays: number;
			estimatedDaysLeft?: number;
			variantMetrics: IExperiment['dxpVariants'];
		};
		sessions: number;
		startedDate?: string;
		type?: string;
	};
	timeZoneId: string;
}> = ({experiment, timeZoneId}) => {
	const {
		description,
		goal,
		metrics: {completion, elapsedDays, estimatedDaysLeft},
		sessions,
		startedDate,
		status,
		type,
	} = experiment;

	const bestVariant = getBestVariant({
		dxpVariants: experiment.dxpVariants,
		goal: goal as {metric: MetricName} | undefined,
		metrics: experiment.metrics,
	});

	return (
		<SummaryBaseCard status={status.toLowerCase()}>
			<SummaryBaseCard.Header
				Description={() =>
					sub(Liferay.Language.get('started-x'), [
						formatDateToTimeZone(
							startedDate,
							getCustomDateFormat(),
							timeZoneId
						),
					]) as any
				}
				title={Liferay.Language.get('test-is-running')}
			/>

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

						{type === 'AB' && (
							<SummarySection
								title={Liferay.Language.get('days-running')}
							>
								<SummarySection.Heading
									value={String(elapsedDays)}
								/>

								{!!estimatedDaysLeft && (
									<SummarySection.Description
										value={
											sub(
												estimatedDaysLeft > 1
													? Liferay.Language.get(
															'about-x-days-left'
														)
													: Liferay.Language.get(
															'about-x-day-left'
														),
												[estimatedDaysLeft]
											) as string
										}
									/>
								)}
							</SummarySection>
						)}

						<SummarySection
							title={Liferay.Language.get('total-test-sessions')}
						>
							<SummarySection.Heading
								value={toThousandsABTesting(sessions)}
							/>
						</SummarySection>

						<SummarySection
							title={Liferay.Language.get('test-metric')}
						>
							<SummarySection.MetricType
								value={
									goal &&
									getMetricName(goal.metric as MetricName)
								}
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
					</div>
				</div>
			</SummaryBaseCard.Body>
		</SummaryBaseCard>
	);
};
