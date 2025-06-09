/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import {EDITABLE_TYPES} from '../../config/constants/editableTypes';
import {TEXT_EDITABLE_TYPES} from '../../config/constants/textEditableTypes';
import {
	useGetContent,
	useGetFieldValue,
	useWithinCollection,
} from '../../contexts/CollectionItemContext';
import {useIsProcessorEnabled} from '../../contexts/EditableProcessorContext';
import {useGlobalContext} from '../../contexts/GlobalContext';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
} from '../../contexts/StoreContext';
import selectCanConfigureWidgets from '../../selectors/selectCanConfigureWidgets';
import selectLanguageId from '../../selectors/selectLanguageId';
import selectSegmentsExperienceId from '../../selectors/selectSegmentsExperienceId';
import resolveEditableConfig from '../../utils/editable_value/resolveEditableConfig';
import resolveEditableValue from '../../utils/editable_value/resolveEditableValue';
import getLayoutDataItemCssClasses from '../../utils/getLayoutDataItemCssClasses';
import getLayoutDataItemUniqueClassName from '../../utils/getLayoutDataItemUniqueClassName';
import getPortletCustomActions from '../../utils/getPortletCustomActions';
import {getResponsiveConfig} from '../../utils/getResponsiveConfig';
import hasInnerCommonStyles from '../../utils/hasInnerCustomStyles';
import useBackgroundImageValue from '../../utils/useBackgroundImageValue';
import UnsafeHTML from '../UnsafeHTML';
import FragmentContentInteractionsFilter from './FragmentContentInteractionsFilter';
import FragmentContentProcessor from './FragmentContentProcessor';
import getAllEditables from './getAllEditables';

const FragmentContent = ({
	className,
	computeEditables,
	elementRef,
	fragmentEntryLinkId,
	getPortals,
	item,
}) => {
	const dispatch = useDispatch();
	const isMounted = useIsMounted();
	const isProcessorEnabled = useIsProcessorEnabled();
	const globalContext = useGlobalContext();
	const getFieldValue = useGetFieldValue();

	const canConfigureWidgets = useSelector(selectCanConfigureWidgets);

	const [editables, setEditables] = useState([]);

	/**
	 * Updates editables array for the rendered fragment.
	 * @param {HTMLElement} [nextFragmentElement] Fragment element
	 *  If not specified, fragmentElement state is used instead.
	 * @return {Array} Updated editables array
	 */
	const onRender = useCallback(
		(fragmentElement) => {
			if (!computeEditables) {
				return;
			}

			let nextEditables = [];

			if (isMounted()) {
				nextEditables = getAllEditables(fragmentElement).map(
					(editable) => ({
						...editable,
						fragmentEntryLinkId,
						itemId: `${fragmentEntryLinkId}-${editable.editableId}`,
						parentId: item.itemId,
					})
				);
			}

			setEditables(nextEditables);

			return nextEditables;
		},
		[isMounted, fragmentEntryLinkId, item.itemId, computeEditables]
	);

	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[fragmentEntryLinkId],
		[fragmentEntryLinkId]
	);

	const languageId = useSelector(selectLanguageId);
	const segmentsExperienceId = useSelector(selectSegmentsExperienceId);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const defaultContent = useGetContent(
		fragmentEntryLink,
		languageId,
		segmentsExperienceId
	);

	const withinCollection = useWithinCollection();

	const [content, setContent] = useState('');

	/* eslint-disable-next-line react-hooks/exhaustive-deps */
	const editableValues = fragmentEntryLink
		? fragmentEntryLink.editableValues
		: {};

	const fragmentEntryLinkError = fragmentEntryLink?.error;

	const cssClasses = getLayoutDataItemCssClasses(item);

	const showPortletTopper = useMemo(
		() =>
			getPortletCustomActions(fragmentEntryLink).length ||
			fragmentEntryLink.fragmentEntryType !== 'widget',
		[fragmentEntryLink]
	);

	useEffect(() => {
		if (fragmentEntryLinkError) {
			throw new Error(fragmentEntryLinkError);
		}
	}, [fragmentEntryLinkError]);

	const isBeingEdited = editables.some((editable) =>
		isProcessorEnabled(editable.itemId)
	);

	/**
	 * fragmentElement keeps a copy of the fragment real HTML,
	 * we perform editableValues replacements over this copy
	 * to avoid multiple re-renders, when every replacement has
	 * finished, this function must be called.
	 *
	 * Synchronizes fragmentElement's content to the real fragment
	 * content. When this happens, the real re-render is performed.
	 */
	useEffect(() => {
		let fragmentElement = document.createElement('div');

		if (!isBeingEdited) {
			fragmentElement.innerHTML = defaultContent;

			if (hasInnerCommonStyles(fragmentEntryLink)) {
				const stylesElement =
					fragmentElement.querySelector('[data-lfr-styles]');

				if (stylesElement) {
					stylesElement.className = `${stylesElement.className} ${cssClasses}`;
				}
			}

			Promise.all(
				getAllEditables(fragmentElement).map((editable) => {
					const editableValue =
						editableValues[editable.editableValueNamespace][
							editable.editableId
						];

					return Promise.all([
						resolveEditableValue(
							editableValue,
							languageId,
							getFieldValue
						),
						resolveEditableConfig(
							editableValue?.config || {},
							languageId,
							getFieldValue
						),
					]).then(([value, editableConfig]) => {
						editable.processor.render(
							editable.element,
							value?.url ?? value,
							editableConfig,
							languageId,
							withinCollection
						);

						editable.element.classList.add('page-editor__editable');

						if (editable.type === EDITABLE_TYPES['rich-text']) {
							editable.element.classList.add('ck-content');
						}

						if (TEXT_EDITABLE_TYPES.has(editable.type)) {
							editable.element.setAttribute(
								'data-tooltip-floating',
								true
							);
						}
					});
				})
			).then(() => {
				if (isMounted() && fragmentElement) {
					setContent(fragmentElement.innerHTML);
				}
			});
		}

		return () => {
			fragmentElement = null;
		};
	}, [
		cssClasses,
		defaultContent,
		dispatch,
		editableValues,
		fragmentEntryLink,
		fragmentEntryLinkId,
		getFieldValue,
		isBeingEdited,
		isMounted,
		isProcessorEnabled,
		languageId,
		segmentsExperienceId,
		withinCollection,
	]);

	const responsiveConfig = getResponsiveConfig(
		item.config,
		selectedViewportSize
	);

	const {backgroundImage} = responsiveConfig.styles;

	const elementId = useId();
	const backgroundImageValue = useBackgroundImageValue(
		elementId,
		backgroundImage,
		getFieldValue
	);

	const style = useMemo(() => {
		const style = {};

		if (backgroundImageValue.url) {
			style[`--lfr-background-image-${item.itemId}`] =
				`url(${backgroundImageValue.url})`;

			if (backgroundImage?.fileEntryId) {
				style['--background-image-file-entry-id'] =
					backgroundImage.fileEntryId;
			}
		}

		return style;
	}, [backgroundImageValue?.url, item.itemId, backgroundImage]);

	const data = useMemo(() => {
		return {
			fragmentEntryLinkId,
		};
	}, [fragmentEntryLinkId]);

	return (
		<>
			<FragmentContentInteractionsFilter
				editables={editables}
				fragmentEntryLinkId={fragmentEntryLinkId}
				itemId={item.itemId}
			>
				<UnsafeHTML
					className={classNames(
						className,
						`page-editor__fragment-content`,
						{
							[`${fragmentEntryLink?.cssClass}`]:
								!hasInnerCommonStyles(fragmentEntryLink),
							[getLayoutDataItemCssClasses(item)]:
								!hasInnerCommonStyles(fragmentEntryLink),
							[getLayoutDataItemUniqueClassName(item.itemId)]:
								!hasInnerCommonStyles(fragmentEntryLink),
							'custom-height': item.config.styles?.height,
							'page-editor__fragment-content--portlet-topper-hidden':
								!canConfigureWidgets || !showPortletTopper,
						}
					)}
					contentRef={elementRef}
					data={data}
					getPortals={getPortals}
					globalContext={globalContext}
					id={elementId}
					markup={content}
					onRender={onRender}
					style={style}
				/>

				{backgroundImageValue.mediaQueries ? (
					<style>{backgroundImageValue.mediaQueries}</style>
				) : null}
			</FragmentContentInteractionsFilter>

			<FragmentContentProcessor
				editables={editables}
				fragmentEntryLinkId={fragmentEntryLinkId}
			/>
		</>
	);
};

FragmentContent.propTypes = {
	className: PropTypes.string,
	fragmentEntryLinkId: PropTypes.string.isRequired,
	getPortals: PropTypes.func.isRequired,
	item: PropTypes.object.isRequired,
};

export default React.memo(FragmentContent);
