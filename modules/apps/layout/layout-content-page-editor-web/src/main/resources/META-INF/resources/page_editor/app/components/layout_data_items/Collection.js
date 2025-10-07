/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import CollectionSelector from '../../../common/components/CollectionSelector';
import {COLUMN_SIZE_MODULE_PER_ROW_SIZES} from '../../config/constants/columnSizes';
import {CONTENT_DISPLAY_OPTIONS} from '../../config/constants/contentDisplayOptions';
import {config} from '../../config/index';
import {
	CollectionItemContext,
	CollectionItemContextProvider,
	useCollectionItemId,
	useIsDisabledCollectionItem,
} from '../../contexts/CollectionItemContext';
import {useDisplayPagePreviewItem} from '../../contexts/DisplayPagePreviewItemContext';
import {useDispatch, useSelector} from '../../contexts/StoreContext';
import selectLanguageId from '../../selectors/selectLanguageId';
import selectSegmentsExperienceId from '../../selectors/selectSegmentsExperienceId';
import CollectionService from '../../services/CollectionService';
import updateCollectionDisplayCollection from '../../thunks/updateCollectionDisplayCollection';
import updateItemConfig from '../../thunks/updateItemConfig';
import {collectionIsMapped} from '../../utils/collectionIsMapped';
import {COLLECTION_LIST_STYLES} from '../../utils/collectionListStyles';
import getLayoutDataItemClassName from '../../utils/getLayoutDataItemClassName';
import getLayoutDataItemUniqueClassName from '../../utils/getLayoutDataItemUniqueClassName';
import {getResponsiveConfig} from '../../utils/getResponsiveConfig';
import {ITEM_SELECTOR_VARIANTS} from '../../utils/itemSelectorVariants';
import UnsafeHTML from '../UnsafeHTML';
import CollectionPagination from './CollectionPagination';

function paginationIsEnabled(collectionConfig) {
	return collectionConfig.paginationType !== 'none';
}

const NotCollectionSelected = ({collection, dispatch, item}) => {
	const handleCollectionSelect = (collection = {}) => {
		dispatch(
			updateCollectionDisplayCollection({
				collection: Object.keys(collection).length ? collection : null,
				itemId: item.itemId,
				listStyle: COLLECTION_LIST_STYLES.grid,
			})
		);
	};

	return (
		<div className="align-items-center bg-lighter d-flex flex-column page-editor__form-unmapped-state page-editor__no-fragments-state">
			<p className="page-editor__no-fragments-state__title">
				{Liferay.Language.get('map-your-collection')}
			</p>

			<p className="mb-3 page-editor__no-fragments-state__message">
				{Liferay.Language.get('select-a-collection-to-display')}
			</p>

			<CollectionSelector
				collectionItem={collection}
				itemSelectorURL={config.collectionSelectorURL}
				label={Liferay.Language.get('select-collection')}
				onCollectionSelect={handleCollectionSelect}
				variant={ITEM_SELECTOR_VARIANTS.button}
			/>
		</div>
	);
};

const EmptyCollectionMessage = () => (
	<div className="page-editor__collection__message">
		{Liferay.Language.get('there-are-no-items-to-display')}
	</div>
);

const EmptyCollectionGridMessage = () => (
	<div className="alert alert-info">
		{Liferay.Language.get(
			'the-collection-is-empty-to-display-your-items-add-them-to-the-collection-or-choose-a-different-collection'
		)}
	</div>
);

const EditModeMaxItemsAlert = () => (
	<div className="alert alert-fluid alert-info">
		<div className="container-fluid">
			{sub(
				Liferay.Language.get(
					'in-edit-mode,-the-number-of-elements-displayed-is-limited-to-x-due-to-performance'
				),
				config.maxNumberOfItemsInEditMode
			)}
		</div>
	</div>
);

const FlexContainer = ({
	child,
	collection,
	collectionConfig,
	collectionId,
	collectionLength,
}) => {
	const {align, flexWrap, justify, listStyle} = collectionConfig;

	const maxNumberOfItems =
		Math.min(
			collectionLength,
			getNumberOfItems(collection, collectionConfig)
		) || 1;

	const numberOfItemsToDisplay = Math.min(
		maxNumberOfItems,
		config.maxNumberOfItemsInEditMode
	);

	return (
		<div
			className={classNames({
				[align]: !!align,
				'd-flex flex-column':
					listStyle === CONTENT_DISPLAY_OPTIONS.flexColumn,
				'd-flex flex-row':
					listStyle === CONTENT_DISPLAY_OPTIONS.flexRow,
				[flexWrap]: Boolean(flexWrap),
				[justify]: Boolean(justify),
			})}
		>
			{Array.from({length: numberOfItemsToDisplay}).map((_, index) => (
				<ItemContext
					collectionConfig={collectionConfig}
					collectionId={collectionId}
					collectionItem={collection.items[index] ?? {}}
					customCollectionSelectorURL={
						collection.customCollectionSelectorURL
					}
					index={index}
					key={index}
				>
					{child}
				</ItemContext>
			))}
		</div>
	);
};

const Grid = ({
	child,
	collection,
	collectionConfig,
	collectionId,
	collectionLength,
	customCollectionSelectorURL,
}) => {
	const maxNumberOfItems =
		Math.min(
			collectionLength,
			getNumberOfItems(collection, collectionConfig)
		) || 1;

	const numberOfItemsToDisplay = Math.min(
		maxNumberOfItems,
		config.maxNumberOfItemsInEditMode
	);

	return (
		<>
			<ClayLayout.Row
				className={classNames(
					`align-items-${collectionConfig.verticalAlignment}`,
					{
						'no-gutters': !collectionConfig.gutters,
					}
				)}
			>
				{Array.from({length: numberOfItemsToDisplay}).map(
					(_, index) => {
						const key = `col-${index}`;

						return (
							<ClayLayout.Col
								key={key}
								size={
									COLUMN_SIZE_MODULE_PER_ROW_SIZES[
										collectionConfig.numberOfColumns
									][collectionConfig.numberOfColumns][
										index % collectionConfig.numberOfColumns
									]
								}
							>
								{index < numberOfItemsToDisplay && (
									<ItemContext
										collectionConfig={collectionConfig}
										collectionId={collectionId}
										collectionItem={
											collection.items[index] ?? {}
										}
										customCollectionSelectorURL={
											customCollectionSelectorURL
										}
										index={index}
									>
										{child}
									</ItemContext>
								)}
							</ClayLayout.Col>
						);
					}
				)}
			</ClayLayout.Row>

			{maxNumberOfItems > config.maxNumberOfItemsInEditMode && (
				<EditModeMaxItemsAlert />
			)}
		</>
	);
};

const ItemContext = ({
	children,
	collectionConfig,
	collectionId,
	collectionItem,
	customCollectionSelectorURL,
	index,
}) => {
	const isDisabled = useIsDisabledCollectionItem();

	const ancestorId = useCollectionItemId();

	const contextValue = useMemo(
		() => ({
			collectionConfig,
			collectionId,
			collectionItem,
			collectionItemId: `${ancestorId}_${index}`,
			collectionItemIndex: index,
			customCollectionSelectorURL,
			isDisabled: isDisabled || index > 0,
		}),
		[
			ancestorId,
			collectionConfig,
			collectionId,
			collectionItem,
			index,
			customCollectionSelectorURL,
			isDisabled,
		]
	);

	return (
		<CollectionItemContextProvider value={contextValue}>
			{children}
		</CollectionItemContextProvider>
	);
};

const Collection = React.memo(
	React.forwardRef(({children, item}, ref) => {
		const child = React.Children.toArray(children)[0];
		const collectionConfig = item.config;
		const emptyCollection = useMemo(
			() => ({
				fakeCollection: true,
				items: {length: collectionConfig.numberOfItems || 1},
				length: collectionConfig.numberOfItems || 1,
				totalNumberOfItems: collectionConfig.numberOfItems || 1,
			}),
			[collectionConfig.numberOfItems]
		);

		const dispatch = useDispatch();
		const languageId = useSelector(selectLanguageId);

		const [activePage, setActivePage] = useState(1);
		const [collection, setCollection] = useState(emptyCollection);
		const [loading, setLoading] = useState(!!collectionConfig.collection);

		const numberOfItems = getNumberOfItems(collection, collectionConfig);

		const isMounted = useIsMounted();

		useEffect(() => {
			if (
				activePage > collectionConfig.numberOfPages &&
				!collectionConfig.displayAllPages
			) {
				setActivePage(1);
			}
		}, [
			collectionConfig.displayAllPages,
			collectionConfig.numberOfItems,
			collectionConfig.numberOfItemsPerPage,
			collectionConfig.numberOfPages,
			activePage,
		]);

		const context = useContext(CollectionItemContext);
		const {classNameId, classPK, externalReferenceCode} =
			context.collectionItem || {};

		const displayPagePreviewItemData =
			useDisplayPagePreviewItem()?.data ?? {};

		const itemClassNameId =
			classNameId || displayPagePreviewItemData.classNameId;
		const itemClassPK = classPK || displayPagePreviewItemData.classPK;
		const itemExternalReferenceCode =
			externalReferenceCode ||
			displayPagePreviewItemData.externalReferenceCode;
		const segmentsExperienceId = useSelector(selectSegmentsExperienceId);

		useEffect(() => {
			if (
				collectionConfig.collection &&
				(activePage <= collectionConfig.numberOfPages ||
					collectionConfig.displayAllPages)
			) {
				setLoading(true);

				CollectionService.getCollectionField({
					activePage,
					classNameId: itemClassNameId,
					classPK: itemClassPK,
					collection: collectionConfig.collection,
					displayAllItems: collectionConfig.displayAllItems,
					displayAllPages: collectionConfig.displayAllPages,
					externalReferenceCode: itemExternalReferenceCode,
					languageId,
					listItemStyle: collectionConfig.listItemStyle || null,
					listStyle: collectionConfig.listStyle,
					numberOfItems: collectionConfig.numberOfItems,
					numberOfItemsPerPage: collectionConfig.numberOfItemsPerPage,
					numberOfPages: collectionConfig.numberOfPages,

					paginationType: collectionConfig.paginationType,
					segmentsExperienceId,
					templateKey: collectionConfig.templateKey || null,
				})
					.then((response) => {
						const {itemSubtype, itemType, ...collection} = response;

						if (isMounted()) {
							setCollection(
								!!collection.length &&
									collection.items?.length > 0
									? collection
									: {...collection, ...emptyCollection}
							);
						}

						// LPS-133832
						// Update itemType/itemSubtype if the user changes the type of the collection

						const {
							itemSubtype: previousItemSubtype,
							itemType: previousItemType,
						} = collectionConfig?.collection ?? {};

						if (
							(!isNullOrUndefined(itemType) &&
								itemType !== previousItemType) ||
							(!isNullOrUndefined(itemSubtype) &&
								itemSubtype !== previousItemSubtype)
						) {
							const nextItemType = isNullOrUndefined(itemType)
								? previousItemType
								: itemType;

							const nextItemSubtype = isNullOrUndefined(
								itemSubtype
							)
								? previousItemSubtype
								: itemSubtype;

							dispatch(
								updateItemConfig({
									itemConfig: {
										...collectionConfig,
										collection: {
											...collectionConfig.collection,
											itemSubtype: nextItemSubtype,
											itemType: nextItemType,
										},
									},
									itemIds: [item.itemId],
								})
							);
						}
					})
					.catch((error) => {
						if (process.env.NODE_ENV === 'development') {
							console.error(error);
						}
					})
					.finally(() => {
						if (isMounted()) {
							setLoading(false);
						}
					});
			}
		}, [
			activePage,
			collectionConfig,
			dispatch,
			emptyCollection,
			item.itemId,
			itemClassNameId,
			itemClassPK,
			itemExternalReferenceCode,
			isMounted,
			languageId,
			segmentsExperienceId,
		]);

		const selectedViewportSize = useSelector(
			(state) => state.selectedViewportSize
		);

		const responsiveConfig = getResponsiveConfig(
			item.config,
			selectedViewportSize
		);

		const flexEnabled =
			collectionConfig.listStyle === CONTENT_DISPLAY_OPTIONS.flexColumn ||
			collectionConfig.listStyle === CONTENT_DISPLAY_OPTIONS.flexRow;

		const showEmptyMessage =
			collection.fakeCollection &&
			collectionConfig.listStyle !== '' &&
			!flexEnabled;

		let CollectionContent = null;

		if (collection.isRestricted) {
			CollectionContent = (
				<ClayAlert displayType="secondary" role={null}>
					{Liferay.Language.get(
						'this-content-cannot-be-displayed-due-to-permission-restrictions'
					)}
				</ClayAlert>
			);
		}
		else if (loading) {
			CollectionContent = <ClayLoadingIndicator />;
		}
		else if (!collectionIsMapped(collectionConfig)) {
			CollectionContent = (
				<NotCollectionSelected
					collection={collection}
					dispatch={dispatch}
					item={item}
				/>
			);
		}
		else if (showEmptyMessage) {
			CollectionContent = <EmptyCollectionMessage />;
		}
		else if (collection.content) {
			CollectionContent = <UnsafeHTML markup={collection.content} />;
		}
		else {
			CollectionContent = (
				<>
					{collection.fakeCollection && (
						<EmptyCollectionGridMessage />
					)}
					{flexEnabled ? (
						<FlexContainer
							child={child}
							collection={collection}
							collectionConfig={responsiveConfig}
							collectionId={item.itemId}
							collectionLength={collection.items.length}
						/>
					) : (
						<Grid
							child={child}
							collection={collection}
							collectionConfig={responsiveConfig}
							collectionId={item.itemId}
							collectionLength={collection.items.length}
							customCollectionSelectorURL={
								collection.customCollectionSelectorURL
							}
						/>
					)}
				</>
			);
		}

		return (
			<div
				className={classNames(
					'page-editor__collection',
					getLayoutDataItemUniqueClassName(item.itemId),
					getLayoutDataItemClassName(item.type)
				)}
				ref={ref}
			>
				{CollectionContent}

				{!collection.isRestricted &&
					collectionIsMapped(collectionConfig) &&
					paginationIsEnabled(collectionConfig) && (
						<CollectionPagination
							activePage={activePage}
							collectionConfig={collectionConfig}
							collectionId={item.itemId}
							onPageChange={setActivePage}
							totalNumberOfItems={
								collection.fakeCollection ? 0 : numberOfItems
							}
							totalPages={getNumberOfPages(
								collection,
								collectionConfig
							)}
						/>
					)}
			</div>
		);
	})
);

Collection.displayName = 'Collection';

function getNumberOfItems(collection, collectionConfig) {
	if (paginationIsEnabled(collectionConfig)) {
		const itemsPerPage = Math.min(
			collectionConfig.numberOfItemsPerPage,
			config.searchContainerPageMaxDelta
		);

		return collectionConfig.displayAllPages
			? collection.totalNumberOfItems
			: Math.min(
					collectionConfig.numberOfPages * itemsPerPage,
					collection.totalNumberOfItems
				);
	}

	return collectionConfig.displayAllItems
		? collection.totalNumberOfItems
		: Math.min(
				collectionConfig.numberOfItems,
				collection.totalNumberOfItems
			);
}

function getNumberOfPages(collection, collectionConfig) {
	const itemsPerPage = Math.min(
		collectionConfig.numberOfItemsPerPage,
		config.searchContainerPageMaxDelta
	);

	return collectionConfig.displayAllPages
		? Math.ceil(collection.totalNumberOfItems / itemsPerPage)
		: Math.min(
				Math.ceil(collection.totalNumberOfItems / itemsPerPage),
				collectionConfig.numberOfPages
			);
}

export default Collection;
