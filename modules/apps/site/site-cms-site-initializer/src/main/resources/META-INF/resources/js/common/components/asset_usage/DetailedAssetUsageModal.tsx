/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import formatActionURL from '../../utils/formatActionURL';
import {BulkActionItem} from './types';

import '../../../../css/components/AssetUsageModals.scss';

interface IDetailedAssetUsageModalProps {
	item: BulkActionItem;
}

const ViewButton = ({
	item,
	itemData,
}: {
	item: BulkActionItem;
	itemData: {type: string; url?: string};
}) => {
	if (!itemData.url) {
		return null;
	}

	if (itemData.url.includes('objectEntryId')) {
		return (
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('view-asset')}
				borderless
				data-testid="view-asset-button"
				displayType="secondary"
				onClick={() => {
					openModal({
						size: 'full-screen',
						title: item.name,
						url: formatActionURL(item.name, itemData.url as string),
					});
				}}
				symbol="view"
				title={Liferay.Language.get('view-asset')}
			/>
		);
	}

	return (
		<ClayLink
			aria-label={Liferay.Language.get('view-page')}
			borderless
			button
			data-testid="view-page-link"
			displayType="secondary"
			href={itemData.url}
			target="_blank"
			title={Liferay.Language.get('view-page-in-a-new-tab')}
		>
			<ClayIcon symbol="shortcut" />
		</ClayLink>
	);
};

const DetailedAssetUsageModal: React.FC<IDetailedAssetUsageModalProps> = ({
	item,
}) => {
	const [showUsageAlertWithNoPermission, setShowUsageAlertWithNoPermission] =
		useState(true);
	const modalWrapperRef = useRef<HTMLDivElement>(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		setTimeout(() => {
			const paginationResultsElements =
				modalWrapperRef.current?.querySelector('.pagination-results');

			const matchPaginationResults =
				paginationResultsElements?.textContent?.match(
					/of\s+(\d+)\s+entries/
				);

			if (matchPaginationResults) {
				const totalCount = parseInt(matchPaginationResults[1], 10);

				setShowUsageAlertWithNoPermission(
					totalCount < item.attributes.usages
				);
			}

			setLoading(false);
		}, 1000);
	}, [item.attributes.usages]);

	return (
		<ClayTooltipProvider>
			<div
				className="cms-detailed-asset-usage-modal"
				ref={modalWrapperRef}
			>
				<ClayModal.Header>
					{sub(Liferay.Language.get('usages-of-x'), `"${item.name}"`)}
				</ClayModal.Header>

				{loading && (
					<ClayLoadingIndicator
						data-testid="loading"
						style={{
							bottom: 0,
							left: 0,
							margin: 'auto',
							position: 'absolute',
							right: 0,
							top: 0,
						}}
					/>
				)}

				<ClayModal.Body style={loading ? {visibility: 'hidden'} : {}}>
					{showUsageAlertWithNoPermission && (
						<ClayAlert
							className="mb-4"
							displayType="warning"
							title={Liferay.Language.get('warning')}
						>
							<Text>
								{Liferay.Language.get(
									'some-references-are-hidden-because-you-do-not-have-permission-to-view-them.-please-contact-the-asset-owner-for-assistance'
								)}
							</Text>
						</ClayAlert>
					)}

					<FrontendDataSet
						apiURL={`/o/headless-cms/v1.0/asset-usages/${item.classPK}`}
						customRenderers={{
							tableCell: [
								{
									component: ({itemData}) => (
										<ViewButton
											item={item}
											itemData={itemData}
										/>
									),
									name: 'viewButton',
									type: 'internal',
								},
							],
						}}
						emptyState={
							showUsageAlertWithNoPermission
								? {
										description: Liferay.Language.get(
											'there-are-no-references-linked-to-this-asset'
										),
										image: '/states/empty_state.svg',
										imageReducedMotion:
											'/states/empty_state_reduced_motion.svg',
										title: Liferay.Language.get(
											'no-references'
										),
									}
								: undefined
						}
						id="asset-usages-table"
						pagination={{
							deltas: [{label: 20}, {label: 40}, {label: 60}],
							initialDelta: 20,
						}}
						showManagementBar
						showPagination
						showSearch
						sorts={[
							{
								active: true,
								direction: 'desc',
								key: 'name',
								label: Liferay.Language.get('name'),
							},
						]}
						views={[
							{
								contentRenderer: 'table',
								default: true,
								label: Liferay.Language.get('table'),
								name: 'table',
								schema: {
									fields: [
										{
											fieldName: 'name',
											label: Liferay.Language.get('name'),
											sortable: true,
										},
										{
											fieldName: 'type',
											label: Liferay.Language.get('type'),
											sortable: false,
										},
										{
											contentRenderer: 'viewButton',
											fieldName: 'view',
											label: '',
										},
									],
								},
								thumbnail: 'table',
							},
						]}
					/>
				</ClayModal.Body>
			</div>
		</ClayTooltipProvider>
	);
};

export {DetailedAssetUsageModal};
