import moment from 'moment';
import React from 'react';
import {Colors} from 'shared/util/charts';
import {CurrentUsage} from './CurrentUsage';
import {formatDateToTimeZone, getCustomDateFormat} from 'shared/util/date';
import {STATUS_DISPLAY_MAP} from 'shared/util/subscriptions';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {toLocale, toRounded, toThousands} from 'shared/util/numbers';
import {UsageMetric} from './UsageMetric';
import {UsageMetricBarChart} from './UsageMetricBarChart';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface IKnownIndividualsSessionProps {
	currentPlan: any;
}

export const KnownIndividualsSession = ({
	currentPlan,
}: IKnownIndividualsSessionProps) => {
	const {timeZoneId} = useTimeZone();
	const {count, limit, status} = currentPlan.metrics.get('individuals');
	const syncedIndividualsCount =
		currentPlan.metrics.get('syncedIndividualsCount') ?? 0;
	const available = limit - count;

	return (
		<div className="mt-4 mb-5">
			<UsageMetric
				description={
					sub(
						Liferay.Language.get(
							'active-users-logged-on-your-dxp-instance-have-been-tracked-by-analytics-cloud-since-x'
						),
						[
							formatDateToTimeZone(
								moment(currentPlan.startDate),
								getCustomDateFormat(),
								timeZoneId
							),
						]
					) as string
				}
				title={Liferay.Language.get('known-individuals')}
			>
				<CurrentUsage
					count={count}
					items={{
						itemA: {
							color: (Colors as {[key: string]: any})[
								(STATUS_DISPLAY_MAP as {[key: string]: string})[
									status
								]
							],
							label: Liferay.Language.get('known-individuals'),
							value: count,
						},
					}}
					legendText={sub(
						available === 1
							? Liferay.Language.get(
									'1-known-individual-is-available'
								)
							: Liferay.Language.get(
									'x-known-individuals-are-available'
								),
						[toLocale(available > 0 ? available : 0)]
					)}
					limit={limit}
					percentageText={(percentage: number) =>
						sub(
							Liferay.Language.get(
								'x-known-individuals-were-used'
							),
							[toRounded(percentage)]
						) as string
					}
				/>

				<div className="mt-4">
					<div className="mb-1">
						<Text color="secondary" size={3}>
							{Liferay.Language.get(
								'individuals-breakdown'
							).toUpperCase()}
						</Text>
					</div>

					<UsageMetricBarChart
						items={{
							itemA: {
								color: Colors.mormont,
								label: sub(
									Liferay.Language.get(
										'individuals-synced-to-analytics-cloud-x'
									),
									[toThousands(syncedIndividualsCount)]
								) as string,
								value: syncedIndividualsCount,
							},
							itemB: {
								color: Colors.primary,
								label: sub(
									Liferay.Language.get('known-individuals-x'),
									[toThousands(count)]
								) as string,
								value: count,
							},
						}}
						total={syncedIndividualsCount + count}
					/>
				</div>
			</UsageMetric>
		</div>
	);
};
