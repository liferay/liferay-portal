/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Body, Cell, Row, Table, Text} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import formatActionURL from '../../../common/utils/formatActionURL';
import {ViewDashboardContext} from '../ViewDashboardContext';
import {
	AssetType,

	// TODO When available, implement asset type to show the correct icon

	AssetTypeIcons as _AssetTypeIcons,
} from '../utils/assetTypes';
import usePagination from '../utils/usePagination';
import {BaseCard} from './BaseCard';
import {Item} from './FilterDropdown';

type ExpiredAsset = {

	// TODO: When available, implement asset type to show the correct icon

	_assetType?: AssetType;
	href: string;
	title: string;
	usages: number;
};

type ExpiredAssetsApiResponse = {
	items: ExpiredAsset[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
};

async function fetchExpiredAssetsData({
	language,
	page,
	pageSize,
	space,
}: {
	language: Item;
	page: number;
	pageSize: number;
	space: Item;
}) {
	const queryParams = buildQueryString(
		{
			depotEntryId: space?.value,
			languageId: language?.value,
			page: page.toString(),
			pageSize: pageSize.toString(),
		},
		{
			shouldIgnoreParam: (value) => value === 'all',
		}
	);

	const endpoint = `/o/analytics-cms-rest/v1.0/expired-assets${queryParams}`;

	const {data, error} =
		await ApiHelper.get<ExpiredAssetsApiResponse>(endpoint);

	if (error) {
		console.error(error);
	}

	if (data) {
		return data;
	}
}

function ExpiredAssetItem({

	// TODO: When available, implement asset type to show the correct icon

	// assetType,

	href,
	title,
	usage,
}: {
	assetType?: AssetType;
	href: string;
	title: string;
	usage: number;
}) {
	return (
		<div className="align-items-center cms-dashboard__expired-assets__item d-flex">

			{/* TODO: When available, implement asset type to show the correct icon */}

			{/* <div className="bg-white mb-1 mr-2 p-2 rounded-lg">
				<Icon
					className="mx-1"
					color={
						AssetTypeIcons[assetType]?.color
					}
					symbol={
						AssetTypeIcons[assetType]?.symbol
					}
				/>
			</div> */}

			<div className="d-flex flex-column">
				<span className="mb-0 text-dark text-weight-semi-bold">
					{title}
				</span>

				<Text color="secondary" size={3}>
					{sub(
						usage === 1
							? Liferay.Language.get('x-usage')
							: Liferay.Language.get('x-usages'),
						[usage]
					).toLowerCase()}
				</Text>
			</div>

			<div className="ml-auto">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('view-asset')}
					className="border-0"
					data-testid="view-asset-button"
					data-tooltip-align="top"
					displayType="secondary"
					onClick={() => {
						openModal({
							size: 'full-screen',
							title,
							url: formatActionURL(title, href),
						});
					}}
					symbol="view"
					title={Liferay.Language.get('view-asset')}
				/>
			</div>
		</div>
	);
}

const EmptyState = () => {
	const {
		changeLanguage,
		changeSpace,
		filters: {language, space},
	} = useContext(ViewDashboardContext);

	const hasFilters: boolean =
		(language && language.value !== 'all') ||
		(space && space.value !== 'all');

	if (hasFilters) {
		return (
			<ClayEmptyState
				description={Liferay.Language.get(
					'sorry,-no-results-were-found'
				)}
				imgSrc={
					Liferay.ThemeDisplay.getPathThemeImages() +
					'/states/search_state.svg'
				}
				title={Liferay.Language.get('no-results-found')}
			>
				<ClayButton
					displayType="secondary"
					onClick={() => {
						changeLanguage({
							label: Liferay.Language.get('all-languages'),
							value: 'all',
						});
						changeSpace({
							label: Liferay.Language.get('all-spaces'),
							value: 'all',
						});
					}}
				>
					{Liferay.Language.get('clear-filters')}
				</ClayButton>
			</ClayEmptyState>
		);
	}

	return (
		<ClayEmptyState
			className="cms-dashboard__empty-state"
			description={Liferay.Language.get(
				'there-are-no-assets-expired-in-the-spaces'
			)}
			imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
			imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
			title={Liferay.Language.get('no-assets-expired-yet')}
		/>
	);
};

function ExpiredAssetsCard() {
	const {
		filters: {language, space},
	} = useContext(ViewDashboardContext);

	const [expiredAssetsList, setExpiredAssetsList] =
		useState<ExpiredAssetsApiResponse>();

	const [loading, setLoading] = useState(true);

	const {handleDeltaChange, handlePageChange, pagination} = usePagination();

	useEffect(() => {
		async function fetchData() {
			setLoading(true);

			const data = await fetchExpiredAssetsData({
				language,
				page: pagination.page,
				pageSize: pagination.pageSize,
				space,
			});

			if (data) {
				setExpiredAssetsList(data);
			}

			setLoading(false);
		}

		fetchData();
	}, [language, pagination, space]);

	if (loading) {
		return (
			<ClayLoadingIndicator
				data-testid="loading"
				displayType="primary"
				shape="squares"
				size="md"
			/>
		);
	}

	return (
		<BaseCard
			contentClassName="mx-n3"
			description={Liferay.Language.get(
				'this-report-provides-a-list-of-assets-that-have-reached-their-expiration-date'
			)}
			title={Liferay.Language.get('expired-assets')}
		>
			<Table borderless striped={true}>
				<Body items={expiredAssetsList?.items || []}>
					{(row) => (
						<Row>
							<Cell className="border-0">
								<ExpiredAssetItem

									// TODO: When available, implement asset type to show the correct icon
									// assetType={row['assetType']}

									href={row['href']}
									title={row['title']}
									usage={row['usages']}
								/>
							</Cell>
						</Row>
					)}
				</Body>
			</Table>

			{!expiredAssetsList || expiredAssetsList.totalCount === 0 ? (
				<EmptyState />
			) : (
				<ClayPaginationBarWithBasicItems
					active={pagination.page}
					activeDelta={pagination.pageSize}
					className="mx-3"
					deltas={pagination.deltas}
					ellipsisBuffer={3}
					ellipsisProps={{
						'aria-label': Liferay.Language.get('more'),
						'title': Liferay.Language.get('more'),
					}}
					onActiveChange={handlePageChange}
					onDeltaChange={handleDeltaChange}
					totalItems={expiredAssetsList?.totalCount || 0}
				/>
			)}
		</BaseCard>
	);
}

export {ExpiredAssetsCard};
