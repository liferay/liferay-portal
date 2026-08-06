import moment from 'moment';
import React from 'react';
import {Colors} from 'shared/util/charts';
import {CurrentUsage} from './CurrentUsage';
import {CUSTOM_DATE_FORMAT, formatDateToTimeZone} from 'shared/util/date';
import {STATUS_DISPLAY_MAP} from 'shared/util/subscriptions';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {UsageMetric} from './UsageMetric';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface IPageViewsSessionProps {
	currentPlan: any;
}

export const PageViewsSession = ({currentPlan}: IPageViewsSessionProps) => {
	const {timeZoneId} = useTimeZone();
	const {count, limit, status} = currentPlan.metrics.get('pageViews');
	const available = limit - count;

	return (
		<UsageMetric
			description={
				sub(
					Liferay.Language.get(
						'total-page-views-have-been-tracked-by-analytics-cloud-since-x'
					),
					[
						formatDateToTimeZone(
							moment(currentPlan.startDate),
							CUSTOM_DATE_FORMAT,
							timeZoneId
						),
					]
				) as string
			}
			title={Liferay.Language.get('page-views')}
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
						label: Liferay.Language.get('page-views'),
						value: count,
					},
				}}
				legendText={sub(
					Liferay.Language.get('x-page-views-are-available'),
					[toLocale(available > 0 ? available : 0)]
				)}
				limit={limit}
				percentageText={(percentage: number) =>
					percentage === 1
						? Liferay.Language.get('1-page-view-was-used')
						: Liferay.Language.get('x-page-views-were-used')
				}
			/>
		</UsageMetric>
	);
};
