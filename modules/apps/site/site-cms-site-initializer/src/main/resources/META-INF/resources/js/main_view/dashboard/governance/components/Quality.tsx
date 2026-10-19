/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {
	IFrontendDataSetProps,
	IInternalRenderer,
	IItemsActions,
} from '@liferay/frontend-data-set-web';
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
import {
	IMissingRequiredFieldsAsset,
	getMissingRequiredFieldsFDSProps,
	renderMissingRequiredFields,
} from '../../../props_transformer/MissingRequiredFieldsFDSPropsTransformer';
import AssetRenderer from '../../../props_transformer/cell_renderers/AssetRenderer';
import {AssetListFDSProps} from '../../../props_transformer/utils/createAssetListFDSPropsBuilder';
import {SectionHeader} from '../../common/SectionHeader';
import InteractiveCard, {
	MetricColor,
} from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {AssetStatistics} from '../GovernanceService';
import getCMSSectionURL from '../getCMSSectionURL';
import {GovernanceAdditionalProps} from '../types';
import {
	GovernanceListPanel,
	getGovernanceAssetListPanelId,
	getGovernanceAssetListViews,
} from './GovernanceAssetList';
import {PercentageOfTotal} from './PercentageOfTotal';

interface IQualityAsset {
	id?: number;
	title?: string;
}

interface QualityCard<T extends IQualityAsset> {
	color: MetricColor;
	countKey: keyof Pick<
		AssetStatistics,
		'brokenLinksCount' | 'missingRequiredFieldsCount'
	>;
	description: string;
	emptyStateDescription: string;
	emptyStateTitle: string;
	getAPIURL: (assetLibraryId?: string) => string;
	getFDSProps: (props: AssetListFDSProps) => Partial<IFrontendDataSetProps>;
	icon: string;
	id: string;
	itemsActions: IItemsActions[];
	renderSubtitle: (itemData: T) => React.ReactNode;
	sectionPath: string;
	title: string;
}

type AnyQualityCard =
	| QualityCard<IBrokenLinkAsset>
	| QualityCard<IMissingRequiredFieldsAsset>;

function getQualityCards(
	additionalProps: GovernanceAdditionalProps
): AnyQualityCard[] {
	return [
		{
			color: 'red',
			countKey: 'brokenLinksCount',
			description: Liferay.Language.get(
				'these-are-assets-containing-links-to-content-that-has-been-deleted,-expired,-or-moved-to-the-recycle-bin'
			),
			emptyStateDescription: Liferay.Language.get(
				'there-are-no-broken-links'
			),
			emptyStateTitle: Liferay.Language.get('no-broken-links'),
			getAPIURL: GovernanceService.getBrokenLinkAssetsURL,
			getFDSProps: getBrokenLinksFDSProps,
			icon: 'chain-broken',
			id: 'brokenLinks',
			itemsActions: additionalProps.brokenLinksFDSActionDropdownItems,
			renderSubtitle: renderBrokenLinks,
			sectionPath: 'broken-links',
			title: Liferay.Language.get('broken-links'),
		},
		{
			color: 'orange',
			countKey: 'missingRequiredFieldsCount',
			description: Liferay.Language.get(
				'these-are-assets-missing-a-field-their-content-type-requires'
			),
			emptyStateDescription: Liferay.Language.get(
				'there-is-no-missing-required-data'
			),
			emptyStateTitle: Liferay.Language.get('no-missing-required-data'),
			getAPIURL: GovernanceService.getMissingRequiredFieldsAssetsURL,
			getFDSProps: getMissingRequiredFieldsFDSProps,
			icon: 'custom-field',
			id: 'missingRequiredFields',
			itemsActions:
				additionalProps.missingRequiredFieldsFDSActionDropdownItems,
			renderSubtitle: renderMissingRequiredFields,
			sectionPath: 'missing-required-data',
			title: Liferay.Language.get('missing-required-data'),
		},
	];
}

async function openViewModal(
	additionalProps: GovernanceAdditionalProps,
	itemData: IQualityAsset,
	items: IQualityAsset[]
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
			itemData: IQualityAsset;
			items: IQualityAsset[];
		}) => openViewModal(additionalProps, itemData, items),
	};
}

function QualityAssetList<T extends IQualityAsset>({
	additionalProps,
	card,
}: {
	additionalProps: GovernanceAdditionalProps;
	card: QualityCard<T>;
}) {
	const {space, statistics} = useContext(GovernanceContext);

	const fdsProps = useMemo(
		() => ({
			...card.getFDSProps({
				additionalProps,
				apiURL: card.getAPIURL(
					space.value === 'all' ? undefined : space.value
				),
				id: card.id,
				itemsActions: [
					...card.itemsActions,
					getViewItemAction(additionalProps),
				],
				searchAsYouType: false,
				searchSuggestionsEnabled: false,
			}),
			customRenderers: {
				tableCell: [
					{
						component: ({actions, itemData, options, value}) => (
							<AssetRenderer<T>
								actions={actions}
								additionalProps={additionalProps}
								itemData={itemData}
								onViewClick={(item) =>
									openViewModal(additionalProps, item, [item])
								}
								options={options}
								renderSubtitle={card.renderSubtitle}
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
		[additionalProps, card, space]
	);

	return (
		<GovernanceListPanel
			count={statistics?.[card.countKey] ?? 0}
			description={card.description}
			emptyStateDescription={card.emptyStateDescription}
			emptyStateTitle={card.emptyStateTitle}
			fdsProps={fdsProps}
			id={card.id}
			title={card.title}
			viewAllHref={getCMSSectionURL(card.sectionPath, space.siteId)}
		/>
	);
}

export function Quality({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);
	const [expandedCardId, setExpandedCardId] = useState<string | null>(null);

	const cards = useMemo(
		() => getQualityCards(additionalProps),
		[additionalProps]
	);

	const expandedCard = cards.find(({id}) => id === expandedCardId);

	const title = Liferay.Language.get('quality');

	return (
		<div aria-label={title} className="pt-4" role="group">
			<SectionHeader icon="speed" title={title} />

			<ClayLayout.Row className="mt-4">
				{cards.map((card) => {
					const count = statistics?.[card.countKey] ?? 0;
					const expanded = card.id === expandedCardId;

					return (
						<ClayLayout.Col className="mb-4" key={card.id} md={4}>
							<InteractiveCard
								active={expanded}
								aria-controls={getGovernanceAssetListPanelId(
									card.id
								)}
								aria-expanded={expanded}
								color={card.color}
								description={card.description}
								icon={card.icon}
								loading={loadingStatistics}
								onClick={() =>
									setExpandedCardId(expanded ? null : card.id)
								}
								title={card.title}
								value={count}
							>
								<PercentageOfTotal
									count={count}
									template={Liferay.Language.get(
										'x-of-x-assets'
									)}
									total={statistics?.totalCount ?? 0}
								/>
							</InteractiveCard>
						</ClayLayout.Col>
					);
				})}

				{expandedCard ? (
					<ClayLayout.Col className="mb-4" size={12}>
						<QualityAssetList
							additionalProps={additionalProps}
							card={expandedCard}
							key={expandedCard.id}
						/>
					</ClayLayout.Col>
				) : null}
			</ClayLayout.Row>
		</div>
	);
}
