/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {usePrevious} from '@liferay/frontend-js-react-web';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import React, {useCallback, useContext, useEffect} from 'react';

import batchRenderFragmentEntryContentRequest from '../../common/batchRenderFragmentEntryContentRequest';
import {updateFragmentEntryLinkContent} from '../actions/index';
import {FRAGMENT_ENTRY_TYPES} from '../config/constants/fragmentEntryTypes';
import InfoItemService from '../services/InfoItemService';
import LayoutService from '../services/LayoutService';
import isMappedToInfoItem from '../utils/editable_value/isMappedToInfoItem';
import isMappedToLayout from '../utils/editable_value/isMappedToLayout';
import isMappedToStructure from '../utils/editable_value/isMappedToStructure';
import getPortletId from '../utils/getPortletId';
import {useDisplayPagePreviewItem} from './DisplayPagePreviewItemContext';
import {useAddPendingItem} from './PortletContentContext';
import {useDispatch} from './StoreContext';

export const INITIAL_STATE = {
	collectionConfig: null,
	collectionId: null,
	collectionItem: null,
	collectionItemId: null,
	collectionItemIndex: null,
	customCollectionSelectorURL: null,
	isDisabled: false,
	setCollectionItemContent: () => null,
};

const CollectionItemContext = React.createContext(INITIAL_STATE);

const CollectionItemContextProvider = CollectionItemContext.Provider;

const useCollectionItemId = () => {
	const context = useContext(CollectionItemContext);

	return context.collectionItemId;
};

const useCollectionItemIndex = () => {
	const context = useContext(CollectionItemContext);

	return context.collectionItemIndex;
};

const useCustomCollectionSelectorURL = () => {
	const context = useContext(CollectionItemContext);

	return context.customCollectionSelectorURL;
};

const useCollectionConfig = () => {
	const context = useContext(CollectionItemContext);

	return context.collectionConfig;
};

const useIsDisabledCollectionItem = () => {
	const context = useContext(CollectionItemContext);

	return context.isDisabled;
};

const useGetContent = (
	fragmentEntryLink = {},
	languageId,
	segmentsExperienceId
) => {
	const {
		collectionContent = {},
		content,
		editableValues,
		fragmentEntryLinkId,
	} = fragmentEntryLink;

	const collectionItemContext = useContext(CollectionItemContext);
	const dispatch = useDispatch();
	const fieldSets = fragmentEntryLink.configuration?.fieldSets;

	const addPendingItem = useAddPendingItem();

	const {
		className: collectionItemClassName,
		classPK: collectionItemClassPK,
		externalReferenceCode: collectionItemExternalReferenceCode,
	} = collectionItemContext.collectionItem || {};

	const {collectionItemId} = collectionItemContext;

	const {
		className: displayPagePreviewItemClassName,
		classPK: displayPagePreviewItemClassPK,
		externalReferenceCode: displayPagePreviewItemExternalReferenceCode,
	} = useDisplayPagePreviewItem()?.data || {};

	const withinCollection = !isNullOrUndefined(
		collectionItemContext.collectionItem
	);

	const [itemClassName, itemClassPK, itemExternalReferenceCode] =
		withinCollection
			? [
					collectionItemClassName,
					collectionItemClassPK,
					collectionItemExternalReferenceCode,
				]
			: [
					displayPagePreviewItemClassName,
					displayPagePreviewItemClassPK,
					displayPagePreviewItemExternalReferenceCode,
				];

	const previousEditableValues = usePrevious(editableValues);
	const previousLanguageId = usePrevious(languageId);
	const previousItemClassName = usePrevious(itemClassName);
	const previousItemClassPK = usePrevious(itemClassPK);
	const previousItemExternalReferenceCode = usePrevious(
		itemExternalReferenceCode
	);

	useEffect(() => {
		const hasLocalizable =
			!!fieldSets?.some((fieldSet) =>
				fieldSet.fields.some((field) => field.localizable)
			) ||
			fragmentEntryLink.fragmentEntryType === FRAGMENT_ENTRY_TYPES.input;

		if (
			shouldRenderFragmentEntryLink({
				editableValues,
				hasLocalizable,
				itemClassName,
				itemClassPK,
				itemExternalReferenceCode,
				languageId,
				previousEditableValues,
				previousItemClassName,
				previousItemClassPK,
				previousItemExternalReferenceCode,
				previousLanguageId,
				withinCollection,
			})
		) {
			batchRenderFragmentEntryContentRequest(
				languageId,
				segmentsExperienceId,

				{
					fragmentEntryLinkId,
					itemClassName,
					itemClassPK,
					itemExternalReferenceCode,
				},

				(content) => {
					dispatch(
						updateFragmentEntryLinkContent({
							collectionItemId,
							content,
							fragmentEntryLinkId,
						})
					);
				}
			);
		}
	}, [
		collectionItemId,
		dispatch,
		editableValues,
		fieldSets,
		fragmentEntryLinkId,
		fragmentEntryLink.fragmentEntryType,
		itemClassName,
		itemClassPK,
		itemExternalReferenceCode,
		languageId,
		previousEditableValues,
		previousItemClassName,
		previousItemClassPK,
		previousItemExternalReferenceCode,
		previousLanguageId,
		segmentsExperienceId,
		withinCollection,
	]);

	useEffect(() => {
		const onRefreshPortlet = ({portletId}) => {
			if (getPortletId(editableValues) !== portletId) {
				return;
			}

			addPendingItem(fragmentEntryLinkId);
		};

		Liferay.on('refreshPortlet', onRefreshPortlet);

		return () => Liferay.detach('refreshPortlet', onRefreshPortlet);
	}, [addPendingItem, editableValues, fragmentEntryLinkId]);

	return (
		(!isNullOrUndefined(collectionItemId)
			? collectionContent[collectionItemId]
			: null) || content
	);
};

const useWithinCollection = () => {
	const context = useContext(CollectionItemContext);

	return !isNullOrUndefined(context.collectionItem);
};

const shouldRenderFragmentEntryLink = ({
	editableValues,
	hasLocalizable,
	itemClassName,
	itemClassPK,
	itemExternalReferenceCode,
	languageId,
	previousEditableValues,
	previousItemClassName,
	previousItemClassPK,
	previousItemExternalReferenceCode,
	previousLanguageId,
	withinCollection,
}) => {

	// For normal fragments we need to render again if the change the locale
	// and the fragment have some localizable configuration fields

	if (hasLocalizable && previousLanguageId !== languageId) {
		return true;
	}

	// For fragments inside a collection, we need to render when previousItemClassName, previousItemClassPK or previousExternalReferenceCode
	// is undefined. This happens when the collection is render at the first time or when changing the "preview with"
	// to none. When setting the item to none the component is unmounted, which means that we cannot rely on
	// the usePrevious hook values. Also we need to render when editable values change

	if (
		withinCollection &&
		(isNullOrUndefined(previousItemClassName) ||
			(itemClassPK && isNullOrUndefined(previousItemClassPK)) ||
			(itemExternalReferenceCode &&
				isNullOrUndefined(previousItemExternalReferenceCode)) ||
			(!isNullOrUndefined(editableValues) &&
				previousEditableValues !== editableValues))
	) {
		return true;
	}

	// For any other case, we need to render when the className, classPK or
	// externalReferenceCode changes

	if (
		previousItemClassName !== itemClassName ||
		previousItemClassPK !== itemClassPK ||
		previousItemExternalReferenceCode !== itemExternalReferenceCode
	) {
		return true;
	}

	return false;
};

const useGetFieldValue = () => {
	const {collectionItem} = useContext(CollectionItemContext);
	const displayPagePreviewItem = useDisplayPagePreviewItem();

	const getFromServer = useCallback(
		(editable) => {
			if (isMappedToInfoItem(editable)) {
				return InfoItemService.getInfoItemFieldValue({
					...editable,
				}).then((response) => {
					if (!response || !Object.keys(response).length) {
						throw new Error('Field value does not exist');
					}

					const {fieldValue = ''} = response;

					return fieldValue;
				});
			}

			if (isMappedToLayout(editable)) {
				return LayoutService.getLayoutFriendlyURL(editable.layout).then(
					(response) => response.friendlyURL || ''
				);
			}

			if (isMappedToStructure(editable) && displayPagePreviewItem) {
				return InfoItemService.getInfoItemFieldValue({
					...editable,
					...displayPagePreviewItem.data,
					fieldId: editable.mappedField,
					languageId: editable.languageId,
				}).then((response) => {
					if (!response || !Object.keys(response).length) {
						throw new Error('Field value does not exist');
					}

					const {fieldValue = ''} = response;

					return fieldValue;
				});
			}

			return Promise.resolve(editable?.defaultValue || editable);
		},
		[displayPagePreviewItem]
	);

	const getFromCollectionItem = useCallback(
		({collectionFieldId}) =>
			!isNullOrUndefined(collectionItem[collectionFieldId])
				? Promise.resolve(collectionItem[collectionFieldId])
				: Promise.reject(),
		[collectionItem]
	);

	if (collectionItem) {
		return getFromCollectionItem;
	}
	else {
		return getFromServer;
	}
};

export {
	CollectionItemContext,
	CollectionItemContextProvider,
	useCollectionConfig,
	useCollectionItemId,
	useCollectionItemIndex,
	useCustomCollectionSelectorURL,
	useGetContent,
	useGetFieldValue,
	useIsDisabledCollectionItem,
	useWithinCollection,
};
