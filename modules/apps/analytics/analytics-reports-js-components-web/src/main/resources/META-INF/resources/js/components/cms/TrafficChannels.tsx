/* eslint-disable @typescript-eslint/no-unused-vars */

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import {Context} from '../../Context';
import useFetch from '../../hooks/useFetch';
import {buildQueryString} from '../../utils/buildQueryString';
import EmptyState from '../EmptyState';
import Title from '../Title';

type TrafficChannelsData = {
	count: number;
	name: string;
	percentage: number;
};

type TrafficChannelsApiResponse = {
	items: {count: number; name: string; percentage: number}[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
};

const trafficChannelsNames: Record<string, string> = {
	'affiliates': Liferay.Language.get('affiliates'),
	'direct': Liferay.Language.get('direct'),
	'display': Liferay.Language.get('display'),
	'email': Liferay.Language.get('email'),
	'organic': Liferay.Language.get('organic'),
	'other': Liferay.Language.get('other'),
	'other-advertising': Liferay.Language.get('other-advertising'),
	'paid': Liferay.Language.get('paid'),
	'paid-search': Liferay.Language.get('paid-search'),
	'referral': Liferay.Language.get('referral'),
	'social': Liferay.Language.get('social'),
};

const TrafficChannelsContent = ({data}: {data: TrafficChannelsApiResponse}) => {
	const formattedItems = data.items.map((channel) => ({
		count: channel.count,
		name: channel.name,
		percentage: channel.percentage * 100,
	}));

	return (
		<main
			aria-label={Liferay.Language.get('traffic-channel')}
			className="tab-focus traffic-channels-table"
			role="table"
			tabIndex={0}
		>
			<header
				className="d-flex flex-row justify-content-between py-3"
				role="row"
			>
				<div
					aria-label={Liferay.Language.get('traffic-channel')}
					role="columnheader"
					style={{width: '35%'}}
				>
					<Text color="secondary" size={3} weight="semi-bold">
						{Liferay.Language.get('traffic-channel')}
					</Text>
				</div>

				<div
					aria-label={Liferay.Language.get('views')}
					role="columnheader"
					style={{width: '35%'}}
				>
					<Text color="secondary" size={3} weight="semi-bold">
						{Liferay.Language.get('views')}
					</Text>
				</div>

				<div
					aria-label={sub(Liferay.Language.get('x-of-x'), [
						'%',
						Liferay.Language.get('views'),
					])}
					className="d-flex justify-content-end"
					role="columnheader"
					style={{width: '30%'}}
				>
					<Text color="secondary" size={3} weight="semi-bold">
						{sub(Liferay.Language.get('x-of-x'), [
							'%',
							Liferay.Language.get('views'),
						])}
					</Text>
				</div>
			</header>

			{formattedItems.map(({count, name, percentage}, index) => (
				<div
					aria-label={`${Liferay.Language.get('traffic-channel')}: ${trafficChannelsNames[name]}`}
					className="d-flex flex-row py-3 tab-focus traffic-channel-item"
					key={name}
					role="row"
					tabIndex={0}
				>
					<div
						aria-label={trafficChannelsNames[name]}
						className="tab-focus traffic-channel-item__name"
						role="cell"
						style={{width: '35%'}}
					>
						<Text size={3} weight="semi-bold">
							{trafficChannelsNames[name]}
						</Text>
					</div>

					<div
						aria-label={`${Liferay.Language.get('volume')}: ${count}`}
						className="d-flex flex-row tab-focus traffic-channel-item__chart"
						role="cell"
						style={{width: '40%'}}
					>
						<div
							aria-hidden="true"
							className="traffic-channel-item__chart__bar"
							style={{width: `${percentage}%`}}
						/>

						<div className="traffic-channel-item__chart__value">
							<Text size={3} weight="semi-bold">
								{count}
							</Text>
						</div>
					</div>

					<div
						aria-label={`Percentage: ${percentage.toFixed(2)}%`}
						className="d-flex justify-content-end tab-focus traffic-channel-item__percentage"
						role="cell"
						style={{width: '25%'}}
					>
						<Text size={3} weight="semi-bold">
							{`${percentage.toFixed(2)}%`}
						</Text>
					</div>
				</div>
			))}
		</main>
	);
};

export function TrafficChannels() {
	const {externalReferenceCode, filters} = useContext(Context);
	const queryParams = buildQueryString({
		externalReferenceCode,
		groupId: filters.channel,
		rangeKey: filters.rangeSelector.rangeKey,
	});

	const {data, loading} = useFetch<TrafficChannelsApiResponse>(
		`/o/analytics-cms-rest/v1.0/object-entry-acquisition-channels${queryParams}`
	);

	return (
		<section
			aria-labelledby="traffic-channels-header"
			className="mt-3 tab-focus"
			tabIndex={0}
		>
			<Title
				section
				value={Liferay.Language.get('views-by-traffic-channels')}
			/>

			<Text
				aria-labelledby={Liferay.Language.get(
					'this-metric-calculates-the-top-five-traffic-channels-that-generated-the-highest-number-of-views-for-the-asset'
				)}
				color="secondary"
				size={3}
				weight="normal"
			>
				{Liferay.Language.get(
					'this-metric-calculates-the-top-five-traffic-channels-that-generated-the-highest-number-of-views-for-the-asset'
				)}
			</Text>

			{loading ? (
				<ClayLoadingIndicator className="my-6" data-testid="loading" />
			) : data?.totalCount ? (
				<TrafficChannelsContent data={data} />
			) : (
				<EmptyState
					className="pb-6"
					description={Liferay.Language.get(
						'there-is-no-data-available-for-the-applied-filters-or-from-the-data-source'
					)}
					maxWidth={320}
					title={Liferay.Language.get('no-data-available-yet')}
				>
					<ClayLink
						href="https://learn.liferay.com/w/content-management-system/analytics-and-monitoring"
						target="_blank"
					>
						<span className="mr-1">
							{Liferay.Language.get(
								'learn-more-about-asset-performance'
							)}
						</span>

						<ClayIcon fontSize={12} symbol="shortcut" />
					</ClayLink>
				</EmptyState>
			)}
		</section>
	);
}
