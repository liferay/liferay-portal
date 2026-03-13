/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLabel from '@clayui/label';
import ClayPanel from '@clayui/panel';
import ClayPopover from '@clayui/popover';
import {openConfirmModal, useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import {COLLECTION_APPLIED_FILTERS_FRAGMENT_ENTRY_KEY} from '../../../../../../../app/config/constants/collectionAppliedFiltersFragmentKey';
import {COLLECTION_FILTER_FRAGMENT_ENTRY_KEY} from '../../../../../../../app/config/constants/collectionFilterFragmentEntryKey';
import {COMMON_STYLES_ROLES} from '../../../../../../../app/config/constants/commonStylesRoles';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../app/config/constants/freemarkerFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../../app/config/constants/viewportSizes';
import {config} from '../../../../../../../app/config/index';
import {useDisplayPagePreviewItem} from '../../../../../../../app/contexts/DisplayPagePreviewItemContext';
import {
	useDispatch,
	useGetState,
	useSelector,
} from '../../../../../../../app/contexts/StoreContext';
import selectSegmentsExperienceId from '../../../../../../../app/selectors/selectSegmentsExperienceId';
import CollectionService from '../../../../../../../app/services/CollectionService';
import InfoItemService from '../../../../../../../app/services/InfoItemService';
import updateCollectionDisplayCollection from '../../../../../../../app/thunks/updateCollectionDisplayCollection';
import updateItemConfig from '../../../../../../../app/thunks/updateItemConfig';
import {CACHE_KEYS} from '../../../../../../../app/utils/cache';
import {COLLECTION_LIST_STYLES} from '../../../../../../../app/utils/collectionListStyles';
import {getResponsiveConfig} from '../../../../../../../app/utils/getResponsiveConfig';
import {isLayoutDataItemDeleted} from '../../../../../../../app/utils/isLayoutDataItemDeleted';
import useCache from '../../../../../../../app/utils/useCache';
import CollectionSelector from '../../../../../../../common/components/CollectionSelector';
import {CommonStyles} from '../CommonStyles';
import {FlexOptions} from '../FlexOptions';
import {EmptyCollectionOptions} from './EmptyCollectionOptions';
import {LayoutSelector} from './LayoutSelector';
import {ListItemStyleSelector} from './ListItemStyleSelector';
import {NoPaginationOptions} from './NoPaginationOptions';
import {PaginationOptions} from './PaginationOptions';
import {PaginationSelector} from './PaginationSelector';
import {ShowGutterSelector} from './ShowGutterSelector';
import {StyleDisplaySelector} from './StyleDisplaySelector';
import {VerticalAlignmentSelector} from './VerticalAlignmentSelector';

export function CollectionGeneralPanel({item}) {
	const {
		collection,
		displayAllItems,
		displayAllPages,
		emptyCollectionOptions,
		listStyle,
		numberOfColumns,
		numberOfItems: initialNumberOfItems,
		numberOfItemsPerPage: initialNumberOfItemsPerPage,
		numberOfPages: initialNumberOfPages,
		paginationType,
	} = item.config;

	const collectionItemType = collection?.itemType || null;
	const flexEnabled =
		listStyle === COLLECTION_LIST_STYLES.flexColumn ||
		listStyle === COLLECTION_LIST_STYLES.flexRow;

	const restrictedItemIds = useSelector((state) => state.restrictedItemIds);
	const segmentsExperienceId = useSelector(selectSegmentsExperienceId);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const [availableListItemStyles, setAvailableListItemStyles] = useState([]);

	const collectionConfig = getResponsiveConfig(
		item.config,
		selectedViewportSize
	);

	const collectionEmptyCollectionMessageId = useId();
	const collectionLayoutId = useId();
	const collectionListItemStyleId = useId();
	const collectionPaginationTypeId = useId();
	const collectionVerticalAlignmentId = useId();

	const dispatch = useDispatch();
	const getState = useGetState();

	const editConfigurationURL = useCache({
		fetcher: () =>
			CollectionService.getCollectionEditConfigurationUrl({
				collectionKey: collection?.key,
				itemId: item.itemId,
				segmentsExperienceId,
			}).then(({url}) => url),
		key: [
			CACHE_KEYS.collectionConfigurationUrl,
			collection?.key,
			item.itemId,
			segmentsExperienceId,
		],
	});

	const warningMessage = useCache({
		fetcher: async () => {
			if (!collection) {
				return '';
			}

			try {
				const response =
					await CollectionService.getCollectionWarningMessage({
						layoutDataItemId: item.itemId,
						segmentsExperienceId,
					});

				return response.warningMessage;
			}
			catch (error) {
				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}

				return '';
			}
		},
		key: [
			CACHE_KEYS.collectionWarningMessage,
			segmentsExperienceId,
			item.itemId,
			JSON.stringify(item.config),
		],
	});

	const previewItem = useDisplayPagePreviewItem();

	const optionsMenuItems = useMemo(() => {
		if (!editConfigurationURL) {
			return [];
		}

		const url = new URL(editConfigurationURL);

		url.searchParams.set(`${config.portletNamespace}type`, collection.type);

		if (previewItem) {
			url.searchParams.set(
				`${config.portletNamespace}classNameId`,
				previewItem.data.classNameId
			);

			url.searchParams.set(
				`${config.portletNamespace}classPK`,
				previewItem.data.classPK
			);
		}

		return [
			{
				href: url.href,
				label: Liferay.Language.get('filter-collection'),
				symbolLeft: 'filter',
			},
		];
	}, [collection, editConfigurationURL, previewItem]);

	const handleCollectionSelect = (collection = {}) => {
		dispatch(
			updateCollectionDisplayCollection({
				collection: Object.keys(collection).length ? collection : null,
				itemId: item.itemId,
				listStyle: COLLECTION_LIST_STYLES.grid,
			})
		);
	};

	const handleConfigurationChanged = useCallback(
		(itemConfig) => {
			if (selectedViewportSize !== VIEWPORT_SIZES.desktop) {
				itemConfig = {[selectedViewportSize]: itemConfig};
			}

			dispatch(
				updateItemConfig({
					itemConfig,
					itemIds: [item.itemId],
				})
			);
		},
		[item.itemId, dispatch, selectedViewportSize]
	);

	const onBeforeCollectionSelect = useCallback(
		({preventDefault}) => {
			const state = getState();

			const isLinkedToFilter = Object.values(state.layoutData.items)
				.filter(
					({itemId}) =>
						!isLayoutDataItemDeleted(state.layoutData, itemId)
				)
				.some((layoutDataItem) => {
					if (
						layoutDataItem.type !== LAYOUT_DATA_ITEM_TYPES.fragment
					) {
						return false;
					}

					const fragmentEntryLink =
						state.fragmentEntryLinks[
							layoutDataItem.config.fragmentEntryLinkId
						];

					if (
						fragmentEntryLink.fragmentEntryKey !==
							COLLECTION_FILTER_FRAGMENT_ENTRY_KEY &&
						fragmentEntryLink.fragmentEntryKey !==
							COLLECTION_APPLIED_FILTERS_FRAGMENT_ENTRY_KEY
					) {
						return false;
					}

					return fragmentEntryLink.editableValues[
						FREEMARKER_FRAGMENT_ENTRY_PROCESSOR
					]?.targetCollections?.includes(item.itemId);
				});

			if (isLinkedToFilter) {
				openConfirmModal({
					message: `${Liferay.Language.get(
						'if-you-change-the-collection-you-unlink-the-collection-filter'
					)}\n\n${Liferay.Language.get('do-you-want-to-continue')}`,
					onConfirm: (isConfirmed) => {
						if (!isConfirmed) {
							preventDefault();
						}
					},
				});
			}
		},
		[getState, item.itemId]
	);

	useEffect(() => {
		if (
			collection &&
			listStyle &&
			!Object.values(COLLECTION_LIST_STYLES).includes(listStyle)
		) {
			InfoItemService.getAvailableListItemRenderers({
				itemSubtype: collection.itemSubtype,
				itemType: collection.itemType,
				listStyle,
			})
				.then((response) => {
					setAvailableListItemStyles(response);
				})
				.catch(() => {
					setAvailableListItemStyles([]);
				});
		}
	}, [collection, listStyle]);

	if (restrictedItemIds.has(item.itemId)) {
		return (
			<ClayAlert displayType="secondary" role={null}>
				{Liferay.Language.get(
					'this-content-cannot-be-displayed-due-to-permission-restrictions'
				)}
			</ClayAlert>
		);
	}

	return (
		<>
			<div className="mb-3 panel-group-sm">
				<ClayPanel
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get(
						'collection-display-options'
					)}
					displayType="unstyled"
					showCollapseIcon
				>
					<ClayPanel.Body>
						{selectedViewportSize === VIEWPORT_SIZES.desktop && (
							<>
								<CollectionSelector
									collectionItem={collection}
									itemSelectorURL={
										config.collectionSelectorURL
									}
									label={Liferay.Language.get('collection')}
									onBeforeCollectionSelect={
										onBeforeCollectionSelect
									}
									onCollectionSelect={handleCollectionSelect}
									optionsMenuItems={optionsMenuItems}
								/>

								{collection?.classPK && (
									<VariationsPopover
										classPK={collection.classPK}
									/>
								)}
							</>
						)}

						{collection && (
							<>
								{selectedViewportSize ===
									VIEWPORT_SIZES.desktop && (
									<StyleDisplaySelector
										collectionItemType={collectionItemType}
										handleConfigurationChanged={
											handleConfigurationChanged
										}
										key={collection.key}
										listStyle={listStyle}
									/>
								)}

								{flexEnabled && (
									<FlexOptions
										itemConfig={collectionConfig}
										onConfigChange={(name, value) => {
											handleConfigurationChanged({
												[name]: value,
											});
										}}
									/>
								)}

								{listStyle === COLLECTION_LIST_STYLES.grid && (
									<>
										<LayoutSelector
											collectionConfig={collectionConfig}
											collectionLayoutId={
												collectionLayoutId
											}
											handleConfigurationChanged={
												handleConfigurationChanged
											}
										/>

										{selectedViewportSize ===
											VIEWPORT_SIZES.desktop && (
											<>
												{numberOfColumns > 1 && (
													<ShowGutterSelector
														checked={
															item.config.gutters
														}
														handleConfigurationChanged={
															handleConfigurationChanged
														}
													/>
												)}

												<VerticalAlignmentSelector
													collectionVerticalAlignmentId={
														collectionVerticalAlignmentId
													}
													handleConfigurationChanged={
														handleConfigurationChanged
													}
													value={
														item.config
															.verticalAlignment
													}
												/>
											</>
										)}
									</>
								)}

								{selectedViewportSize ===
									VIEWPORT_SIZES.desktop && (
									<>
										{listStyle !==
											COLLECTION_LIST_STYLES.grid &&
											!!availableListItemStyles.length && (
												<ListItemStyleSelector
													availableListItemStyles={
														availableListItemStyles
													}
													collectionListItemStyleId={
														collectionListItemStyleId
													}
													handleConfigurationChanged={
														handleConfigurationChanged
													}
													item={item}
												/>
											)}

										<EmptyCollectionOptions
											collectionEmptyCollectionMessageId={
												collectionEmptyCollectionMessageId
											}
											emptyCollectionOptions={
												emptyCollectionOptions
											}
											handleConfigurationChanged={
												handleConfigurationChanged
											}
										/>

										<PaginationSelector
											collectionPaginationTypeId={
												collectionPaginationTypeId
											}
											handleConfigurationChanged={
												handleConfigurationChanged
											}
											value={paginationType || 'none'}
										/>

										{paginationType !== 'none' ? (
											<PaginationOptions
												displayAllPages={
													displayAllPages
												}
												handleConfigurationChanged={
													handleConfigurationChanged
												}
												initialNumberOfItemsPerPage={
													initialNumberOfItemsPerPage
												}
												initialNumberOfPages={
													initialNumberOfPages
												}
												warningMessage={warningMessage}
											/>
										) : (
											<NoPaginationOptions
												collection={collection}
												displayAllItems={
													displayAllItems
												}
												handleConfigurationChanged={
													handleConfigurationChanged
												}
												initialNumberOfItems={
													initialNumberOfItems
												}
												warningMessage={warningMessage}
											/>
										)}
									</>
								)}
							</>
						)}
					</ClayPanel.Body>
				</ClayPanel>
			</div>

			<CommonStyles
				commonStylesValues={collectionConfig.styles}
				item={item}
				role={COMMON_STYLES_ROLES.general}
			/>
		</>
	);
}

CollectionGeneralPanel.propTypes = {
	item: PropTypes.object.isRequired,
};

function VariationsPopover({classPK}) {
	const popoverId = useId();

	const variations = useCache({
		fetcher: () => CollectionService.getCollectionVariations(classPK),
		key: [CACHE_KEYS.collectionVariations, classPK],
	});

	if (!variations?.length) {
		return null;
	}

	return (
		<ClayPopover
			alignPosition="bottom"
			closeOnClickOutside
			header={
				<span className="font-weight-bold">
					{Liferay.Language.get('collection-variations')}
				</span>
			}
			id={popoverId}
			trigger={
				<ClayButton
					borderless
					className="mb-0 mt-2"
					displayType="secondary"
					size="xs"
				>
					{sub(
						Liferay.Language.get('x-variations'),
						variations.length
					)}
				</ClayButton>
			}
		>
			{variations.map((variation, index) => (
				<ClayLabel
					className="mb-2 mr-2"
					displayType="secondary"
					key={index}
				>
					{variation}
				</ClayLabel>
			))}
		</ClayPopover>
	);
}

VariationsPopover.propTypes = {
	classPK: PropTypes.string.isRequired,
};
