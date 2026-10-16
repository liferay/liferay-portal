/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React, {useContext, useMemo, useState} from 'react';

import ApiHelper from '../../../../common/services/ApiHelper';
import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import {openCMSModal} from '../../../../common/utils/openCMSModal';
import AssetNavigationModalContent from '../../../modal/asset_navigation_view/AssetNavigationModalContent';
import {
	IBrokenLinkAsset,
	getBrokenLinksFDSProps,
	renderBrokenLinks,
} from '../../../props_transformer/BrokenLinksFDSPropsTransformer';
import AssetRenderer from '../../../props_transformer/cell_renderers/AssetRenderer';
import {SectionHeader} from '../../common/SectionHeader';
import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService from '../GovernanceService';
import getCMSSectionURL from '../getCMSSectionURL';
import {GovernanceAdditionalProps} from '../types';
import {
	GovernanceListPanel,
	getGovernanceAssetListPanelId,
	getGovernanceAssetListViews,
} from './GovernanceAssetList';
import {PercentageOfTotal} from './PercentageOfTotal';

const BROKEN_LINKS_LIST_ID = 'brokenLinks';

function getBrokenLinksDescription() {
	return Liferay.Language.get(
		'these-are-assets-containing-links-to-content-that-has-been-deleted,-expired,-or-moved-to-the-recycle-bin'
	);
}

async function openViewModal(
	additionalProps: GovernanceAdditionalProps,
	itemData: IBrokenLinkAsset,
	items: IBrokenLinkAsset[]
) {
	const {data, error} = await ApiHelper.get<{
		items: ISearchAssetObjectEntry[];
	}>(GovernanceService.getAssetsByIdsURL(items.flatMap(({id}) => id ?? [])));

	const searchAssetObjectEntries = items.flatMap(
		({id}) => data?.items.find(({embedded}) => embedded?.id === id) ?? []
	);

	if (!searchAssetObjectEntries.length) {
		openToast({
			message: error
				? escapeHTML(error)
				: Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'danger',
		});

		return;
	}

	openCMSModal({
		contentComponent: () =>
			AssetNavigationModalContent({
				additionalProps,
				contentViewURL: additionalProps.contentViewURL,
				currentIndex: Math.max(
					searchAssetObjectEntries.findIndex(
						({embedded}) => embedded?.id === itemData.id
					),
					0
				),
				items: searchAssetObjectEntries,
			}),
		size: 'full-screen',
	});
}

function getViewItemAction(
	additionalProps: GovernanceAdditionalProps
): IItemsActions {
	return {
		data: {id: 'view'},
		icon: 'view',
		label: Liferay.Language.get('view'),
		onClick: ({
			itemData,
			items,
		}: {
			itemData: IBrokenLinkAsset;
			items: IBrokenLinkAsset[];
		}) => openViewModal(additionalProps, itemData, items),
	};
}

function BrokenLinksList({
	additionalProps,
	description,
}: {
	additionalProps: GovernanceAdditionalProps;
	description: string;
}) {
	const {space, statistics} = useContext(GovernanceContext);

	const fdsProps = useMemo(
		() => ({
			...getBrokenLinksFDSProps({
				additionalProps,
				apiURL: GovernanceService.getBrokenLinkAssetsURL(
					space.value === 'all' ? undefined : space.value
				),
				id: BROKEN_LINKS_LIST_ID,
				itemsActions: [
					...additionalProps.brokenLinksFDSActionDropdownItems,
					getViewItemAction(additionalProps),
				],
				searchAsYouType: false,
				searchSuggestionsEnabled: false,
			}),
			customRenderers: {
				tableCell: [
					{
						component: ({actions, itemData, options, value}) => (
							<AssetRenderer<IBrokenLinkAsset>
								actions={actions}
								additionalProps={additionalProps}
								itemData={itemData}
								onViewClick={(item) =>
									openViewModal(additionalProps, item, [item])
								}
								options={options}
								renderSubtitle={renderBrokenLinks}
								value={value}
							/>
						),
						name: 'assetRenderer',
						type: 'internal',
					} as IInternalRenderer,
				],
			},
			views: getGovernanceAssetListViews({
				actionId: 'edit',
				fieldName: 'title',
			}),
		}),
		[additionalProps, space]
	);

	return (
		<GovernanceListPanel
			count={statistics?.brokenLinksCount ?? 0}
			description={description}
			emptyStateDescription={Liferay.Language.get(
				'there-are-no-broken-links'
			)}
			emptyStateTitle={Liferay.Language.get('no-broken-links')}
			fdsProps={fdsProps}
			id={BROKEN_LINKS_LIST_ID}
			title={Liferay.Language.get('broken-links')}
			viewAllHref={getCMSSectionURL('broken-links', space.siteId)}
		/>
	);
}

export function Quality({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);
	const [brokenLinksExpanded, setBrokenLinksExpanded] = useState(false);

	const count = statistics?.brokenLinksCount ?? 0;

	const description = getBrokenLinksDescription();

	const title = Liferay.Language.get('quality');

	return (
		<div aria-label={title} className="pt-4" role="group">
			<SectionHeader icon="speed" title={title} />

			<ClayLayout.Row className="mt-4">
				<ClayLayout.Col className="mb-4" md={4}>
					<InteractiveCard
						active={brokenLinksExpanded}
						aria-controls={getGovernanceAssetListPanelId(
							BROKEN_LINKS_LIST_ID
						)}
						aria-expanded={brokenLinksExpanded}
						color="red"
						description={description}
						icon="chain-broken"
						loading={loadingStatistics}
						onClick={() =>
							setBrokenLinksExpanded((expanded) => !expanded)
						}
						title={Liferay.Language.get('broken-links')}
						value={count}
					>
						<PercentageOfTotal
							count={count}
							template={Liferay.Language.get('x-of-x-assets')}
							total={statistics?.totalCount ?? 0}
						/>
					</InteractiveCard>
				</ClayLayout.Col>

				{brokenLinksExpanded ? (
					<ClayLayout.Col className="mb-4" size={12}>
						<BrokenLinksList
							additionalProps={additionalProps}
							description={description}
						/>
					</ClayLayout.Col>
				) : null}
			</ClayLayout.Row>
		</div>
	);
}
