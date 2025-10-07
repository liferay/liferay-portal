/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addDays} from 'date-fns';
import useSWR from 'swr';

import SearchBuilder from '../../../core/SearchBuilder';
import GraphQL from '../../../services/rest/HeadlessGraphQL';
import {getLastDayOfMonth} from '../../../utils/date';

export const METRIC_PARAMETER = {
	month: 30,
	q1: 1,
	q2: 2,
	q3: 3,
	q4: 4,
	week: 7,
};

type FilterType = 'month' | 'q1' | 'q2' | 'q3' | 'q4' | 'week';

const currentTime = new Date();

const useOrderMetrics = (param: FilterType) => {
	return useSWR('metrics/order', async () => {
		const beforeLastPeriod = addDays(
			currentTime,
			-METRIC_PARAMETER[param as keyof typeof METRIC_PARAMETER] * 2
		);

		const lastPeriod = addDays(
			currentTime,
			-METRIC_PARAMETER[param as keyof typeof METRIC_PARAMETER]
		);

		beforeLastPeriod.setHours(0, 0, 0);
		lastPeriod.setHours(23, 59, 59);

		const {
			orders,
			ordersCreateBetweenLastPeriod,
			ordersCreatedLastPeriod,
			...metrics
		} = await GraphQL.metrics(
			{
				group: 'headlessCommerceAdminOrder_v1_0',
				name: 'orders',
			},
			{
				orders: '',
				ordersCreateBetweenLastPeriod: new SearchBuilder()
					.lt('createDate', lastPeriod.toISOString())
					.and()
					.gt('createDate', beforeLastPeriod.toISOString())
					.build(),
				ordersCreatedLastPeriod: SearchBuilder.gt(
					'createDate',
					lastPeriod.toISOString()
				),
				ordersThisMonth: new SearchBuilder()
					.gt(
						'createDate',
						new Date(
							currentTime.getFullYear(),
							currentTime.getMonth(),
							1,
							0,
							0,
							0
						).toISOString()
					)
					.and()
					.lt(
						'createDate',
						new Date(
							currentTime.getFullYear(),
							currentTime.getMonth(),
							getLastDayOfMonth(
								currentTime.getMonth(),
								currentTime.getFullYear()
							),
							23,
							59,
							59
						).toISOString()
					)
					.build(),
				ordersThisYear: SearchBuilder.gt(
					'createDate',
					new Date(
						currentTime.getFullYear(),
						0,
						1,
						0,
						0,
						0
					).toISOString()
				),
			}
		).then(({data: {metrics}}) => ({
			orders: metrics.orders.totalCount,
			ordersCreateBetweenLastPeriod:
				metrics.ordersCreateBetweenLastPeriod.totalCount,
			ordersCreatedLastPeriod: metrics.ordersCreatedLastPeriod.totalCount,
			ordersThisMonth: metrics.ordersThisMonth.totalCount,
			ordersThisYear: metrics.ordersThisYear.totalCount,
		}));

		const newOrders =
			ordersCreatedLastPeriod - ordersCreateBetweenLastPeriod;

		let growth = Number(
			((newOrders / ordersCreatedLastPeriod) * 100).toFixed(2)
		);

		if (Number.isNaN(growth)) {
			growth = 0;
		}

		return {
			beforeLastPeriod: ordersCreateBetweenLastPeriod,
			growth,
			lastPeriod: ordersCreatedLastPeriod,
			totalCount: orders,
			...metrics,
		};
	});
};

export default useOrderMetrics;
