/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import React from 'react';

import ApiHelper from '../../services/ApiHelper';
import {displayErrorToast} from '../../utils/toastUtil';
import {AssetUsageListModal} from './AssetUsageListModal';
import {DetailedAssetUsageModal} from './DetailedAssetUsageModal';
import {BulkActionItem, BulkActionItemResponse} from './types';

const fetchUsageAssetData = async ({
	apiURL = '',
	bulkActionItems,
	fetchChildren = false,
	page,
	pageSize,
	search,
	selectAll,
}: {
	apiURL?: string;
	bulkActionItems: Omit<BulkActionItem, 'attributes'>[];
	fetchChildren?: boolean;
	page: number;
	pageSize: number;
	search: string;
	selectAll: boolean;
}): Promise<{data: BulkActionItemResponse | null; error: Error | null}> => {
	const queryString = buildQueryString({
		fetchChildren: String(fetchChildren),
		filter: getFilterFromApiURL(apiURL),
		page: String(page),
		pageSize: String(pageSize),
		search,
	});

	const {data, error} = await ApiHelper.post<BulkActionItemResponse>(
		`/o/headless-cms/v1.0/bulk-action-item/preview${queryString}`,
		{
			bulkActionItems,
			selectAll,
			type: 'DeleteBulkAction',
		}
	);

	if (error) {
		console.error(error);
	}

	if (data) {
		return {data, error};
	}

	return {data: null, error: null};
};

const openAssetUsageListModal = async ({
	apiURL,
	itemsData,
	onDelete,
	onSkip,
	selectAll,
}: {
	apiURL?: string;
	folderId?: number;
	itemsData: ItemData[];
	onDelete: () => void;
	onSkip?: () => void;
	selectAll: boolean;
}) => {
	const {data, error} = await fetchUsageAssetData({
		apiURL,
		bulkActionItems: selectAll
			? []
			: itemsData.map(({embedded, entryClassName}) => ({
					classExternalReferenceCode: embedded.externalReferenceCode,
					className: entryClassName,
					classPK: embedded.id,
					name: embedded.title,
				})),
		page: 1,
		pageSize: 20,
		search: '',
		selectAll,
	});

	if (error) {
		displayErrorToast();

		return {data: null};
	}

	if (!data) {
		return {data: null};
	}

	const firstItem = data?.items[0].attributes;

	if (
		!!firstItem?.usages ||
		(firstItem?.type === 'FOLDER' && !!firstItem?.itemsCount)
	) {
		openModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<AssetUsageListModal
					apiParams={{
						apiURL,
						selectAll,
					}}
					closeModal={closeModal}
					initialData={data}
					onDelete={onDelete}
				/>
			),
			size: 'lg',
			status: 'danger',
		});
	}
	else {
		onSkip ? onSkip() : onDelete();
	}
};

const openDetailedAssetUsageModal = ({
	item,
	onClose,
}: {
	item: BulkActionItem;
	onClose: () => void;
}) => {
	openModal({
		contentComponent: () => <DetailedAssetUsageModal item={item} />,
		onClose,
		size: 'lg',
	});
};

function buildQueryString(params: Record<string, string | string[]>) {
	const queryParams = Object.keys(params).sort();

	const queryString = queryParams
		.map((key) => {
			return `${encodeURIComponent(key)}=${encodeURIComponent(params[key] as string)}`;
		})
		.filter(Boolean)
		.join('&');

	return `?${queryString}`;
}

function getFilterFromApiURL(apiURL: string) {
	const queryIndex = apiURL.indexOf('?');

	if (queryIndex === -1) {
		return '';
	}

	const queryString = apiURL.slice(queryIndex + 1);
	const searchParams = new URLSearchParams(queryString);

	return searchParams.get('filter') ?? '';
}

export {
	fetchUsageAssetData,
	openAssetUsageListModal,
	openDetailedAssetUsageModal,
};
