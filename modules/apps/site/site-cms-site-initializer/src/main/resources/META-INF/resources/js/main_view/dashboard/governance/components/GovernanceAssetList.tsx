/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayLink from '@clayui/link';
import {
	FrontendDataSet,
	IItemActionsData,
} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo} from 'react';

import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {FDS_EVENT_UPDATE_DISPLAY} from '../../../../common/utils/constants';
import getDashboardAssetListFDSProps from '../../../props_transformer/getDashboardAssetListFDSProps';
import {GovernanceContext} from '../GovernanceContext';
import {GOVERNANCE_ASSET_LIST_PAGE_SIZE} from '../GovernanceService';
import {GovernanceAdditionalProps} from '../types';

const EMPTY_STATE_IMAGE = '/states/cms_empty_state.svg';

const VIEWS = [
	{
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					actionId: 'actionLink',
					contentRenderer: 'assetRenderer',
					fieldName: 'embedded.title',
					label: Liferay.Language.get('title'),
				},
			],
		},
		thumbnail: 'table',
	},
];

export function getGovernanceAssetListPanelId(id: string) {
	return `${id}-panel`;
}

export function GovernanceAssetList({
	additionalProps,
	apiURL,
	count,
	description,
	emptyStateDescription = '',
	emptyStateTitle,
	id,
	itemActionIds,
	renderSubtitle,
	title,
	viewAllHref,
}: {
	additionalProps: GovernanceAdditionalProps;
	apiURL: string;
	count: number;
	description: string;
	emptyStateDescription?: string;
	emptyStateTitle: string;
	id: string;
	itemActionIds: IItemActionsData['id'][];
	renderSubtitle: (item: ISearchAssetObjectEntry) => React.ReactNode;
	title: string;
	viewAllHref: string;
}) {
	const {reloadStatistics} = useContext(GovernanceContext);

	useEffect(() => {
		const handleUpdateDisplay = (event?: {id?: string}) => {
			if (event?.id === id) {
				reloadStatistics();
			}
		};

		Liferay.on(FDS_EVENT_UPDATE_DISPLAY, handleUpdateDisplay);

		return () => {
			Liferay.detach(FDS_EVENT_UPDATE_DISPLAY, handleUpdateDisplay);
		};
	}, [id, reloadStatistics]);

	const fdsProps = useMemo(
		() =>
			getDashboardAssetListFDSProps({
				additionalProps,
				apiURL,
				id,
				itemsActions: itemActionIds.flatMap((actionId) =>
					additionalProps.fdsActionDropdownItems.filter(
						(action) => action.data?.id === actionId
					)
				),
				onDataChange: reloadStatistics,
				renderSubtitle,
			}),
		[
			additionalProps,
			apiURL,
			id,
			itemActionIds,
			reloadStatistics,
			renderSubtitle,
		]
	);

	const titleId = `${id}-title`;

	return (
		<section
			aria-labelledby={titleId}
			className="border rounded-lg"
			id={getGovernanceAssetListPanelId(id)}
		>
			<div className="pt-3 px-3">
				<div className="align-items-center d-flex justify-content-between mb-2 pb-1">
					<span className="font-weight-semi-bold text-4" id={titleId}>
						{title}
					</span>

					<ClayLink
						aria-label={sub(
							Liferay.Language.get('view-all-x'),
							title
						)}
						borderless
						className="font-weight-semi-bold text-3"
						href={viewAllHref}
						small
					>
						{Liferay.Language.get('view-all')}
					</ClayLink>
				</div>

				<span className="text-3 text-secondary">{description}</span>
			</div>

			<div className="cms-fds-fluid cms-governance-asset-list custom-empty-state">
				<FrontendDataSet
					{...fdsProps}
					emptyState={{
						description: emptyStateDescription,
						image: EMPTY_STATE_IMAGE,
						title: emptyStateTitle,
					}}
					id={id}
					showManagementBar={false}
					showPagination={false}
					showSearch={false}
					style="fluid"
					views={VIEWS}
				/>
			</div>

			{count ? (
				<div className="p-3">
					<Text color="secondary" size={3}>
						{sub(
							Liferay.Language.get('showing-x-of-x-items'),
							Math.min(count, GOVERNANCE_ASSET_LIST_PAGE_SIZE),
							count
						)}
					</Text>
				</div>
			) : null}
		</section>
	);
}
