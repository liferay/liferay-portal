import React from 'react';
import {formatDateToTimeZone, getCustomDateFormat} from 'shared/util/date';
import {
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
import {formatPercent} from 'shared/util/numbers';

export const SummaryCompletedCard: React.FC<{
	experiment: IExperiment & {
		description?: string;
		finishedDate?: string;
		metrics: {
			completion: number;
			elapsedDays: number;
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
		dxpVariants,
		finishedDate,
		goal,
		metrics: {completion, elapsedDays, variantMetrics},
		publishedDXPVariantId,
		sessions,
		startedDate,
		status,
		type,
	} = experiment;

	const publishedVariant = mergedVariants(dxpVariants, variantMetrics).find(
		({dxpVariantId}) => dxpVariantId === publishedDXPVariantId
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
									getCustomDateFormat(),
									timeZoneId
								),
							])}
						</div>

						{finishedDate && (
							<div>
								{sub(Liferay.Language.get('ended-x'), [
									formatDateToTimeZone(
										finishedDate,
										getCustomDateFormat(),
										timeZoneId
									),
								])}
							</div>
						)}
					</div>
				)}
				title={Liferay.Language.get('test-complete')}
			/>

			<SummaryAlert symbol="check-circle">
				<SummaryTitle
					className="font-weight-bold mb-1"
					label={
						sub(Liferay.Language.get('x-has-been-published'), [
							publishedVariant?.dxpVariantName,
						]) as string
					}
				/>

				<strong>
					{Liferay.Language.get(
						'no-more-data-will-be-collected-for-this-test'
					)}
				</strong>
			</SummaryAlert>

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
								title={Liferay.Language.get('days-ran')}
							>
								<SummarySection.Heading
									value={String(elapsedDays)}
								/>
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

							{publishedVariant?.improvement !== undefined &&
								publishedVariant.improvement > 0 && (
									<SummarySection.Variant
										lift={formatPercent(
											publishedVariant.improvement
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
