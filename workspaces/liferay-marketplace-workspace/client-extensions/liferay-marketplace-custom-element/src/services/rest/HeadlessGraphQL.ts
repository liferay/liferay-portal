/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetcher from '../fetcher';

type Metrics<T> = {[key: string]: APIResponse<T>};

export default class GraphQL {
	static async metrics<T>(
		query: {
			group: string;
			name: string;
			options?: Record<string, string>;
		},
		filters: Record<string, string>,
		options?: Record<string, any>
	) {
		const queries = Object.entries(filters)
			.map(([alias, filter]) => {
				const option = options?.[alias];

				const body = (option?.body || query.options?.body) ?? '';
				const sort = (options?.sort || query?.options?.sort) ?? '';
				const pageSize =
					(option?.pageSize || query.options?.pageSize) ?? 1;

				return `${alias}: ${query.name}(filter: "${filter}", pageSize: ${pageSize}, sort: "${sort}") {
					totalCount

					${body}
			  	}
			`;
			})
			.join('\n');

		try {
			const response = await fetcher.post<{
				data: {
					metrics: Metrics<T>;
				};
			}>(`/o/graphql`, {
				query: `
		  {
			metrics: ${query.group} {
			  ${queries}
			}
		  }
		`,
			});

			return response;
		}
		catch {
			const metrics: Metrics<T> = {};

			for (const filterKey in filters) {
				(metrics as any)[filterKey] = {
					items: [],
					totalCount: 0,
				} as unknown as Metrics<T>;
			}

			return {
				data: {
					metrics,
				},
			};
		}
	}
}
