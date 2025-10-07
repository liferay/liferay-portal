/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useRef} from 'react';

import {CONTAINER_WIDTH_TYPES} from '../config/constants/containerWidthTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../config/constants/viewportSizes';
import {config} from '../config/index';
import {useGlobalContext} from '../contexts/GlobalContext';
import {useSelector, useSelectorRef} from '../contexts/StoreContext';
import {deepEqual} from '../utils/checkDeepEqual';
import generateStyleSheet from '../utils/generateStyleSheet';
import {getResponsiveConfig} from '../utils/getResponsiveConfig';
import hasInnerCommonStyles from '../utils/hasInnerCustomStyles';

const LAYOUT_DATA_ITEMS_WITH_COMMON_STYLES = [
	LAYOUT_DATA_ITEM_TYPES.collection,
	LAYOUT_DATA_ITEM_TYPES.container,
	LAYOUT_DATA_ITEM_TYPES.form,
	LAYOUT_DATA_ITEM_TYPES.formRelationship,
	LAYOUT_DATA_ITEM_TYPES.formStepContainer,
	LAYOUT_DATA_ITEM_TYPES.row,
	LAYOUT_DATA_ITEM_TYPES.fragment,
];

export default function CommonStylesManager() {
	const stylesPerViewportRef = useRef({});
	const masterStylesPerViewportRef = useRef({});

	const layoutData = useSelector((state) => state.layoutData);
	const masterLayoutData = useSelector(
		(state) => state.masterLayout?.masterLayoutData
	);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const globalContext = useGlobalContext();

	useEffect(() => {
		const {styleSheet: previousStyleSheet, styles: previousStyles} =
			stylesPerViewportRef.current[selectedViewportSize] || {};

		const {styleSheet, styles} = calculateStyles({
			fragmentEntryLinks: fragmentEntryLinksRef.current,
			isMaster: true,
			items: Object.values(layoutData.items),
			previousStyleSheet,
			previousStyles,
			selectedViewportSize,
		});

		stylesPerViewportRef.current[selectedViewportSize] = {
			styleSheet,
			styles,
		};

		createOrUpdateStyleTag({
			globalContext,
			id: 'layout-common-styles',
			styleSheet,
		});
	}, [
		layoutData.items,
		selectedViewportSize,
		globalContext,
		fragmentEntryLinksRef,
	]);

	useEffect(() => {
		if (!masterLayoutData) {
			return;
		}

		const {styleSheet: previousStyleSheet, styles: previousStyles} =
			masterStylesPerViewportRef.current[selectedViewportSize] || {};

		const {styleSheet, styles} = calculateStyles({
			fragmentEntryLinks: fragmentEntryLinksRef.current,
			isMaster: false,
			items: Object.values(masterLayoutData.items),
			previousStyleSheet,
			previousStyles,
			selectedViewportSize,
		});

		masterStylesPerViewportRef.current[selectedViewportSize] = {
			styleSheet,
			styles,
		};

		createOrUpdateStyleTag({
			globalContext,
			id: 'layout-master-common-styles',
			styleSheet,
		});
	}, [
		masterLayoutData,
		selectedViewportSize,
		globalContext,
		fragmentEntryLinksRef,
	]);

	return null;
}

function createOrUpdateStyleTag({globalContext, id, styleSheet}) {
	let styleTag = globalContext.document.getElementById(id);

	if (!styleTag) {
		styleTag = globalContext.document.createElement('style');
		styleTag.id = id;
		styleTag.type = 'text/css';
		styleTag.dataset.sennaTrack = 'temporary';

		globalContext.document.head.appendChild(styleTag);
	}

	styleTag.innerHTML = styleSheet;

	return styleTag;
}

function hasCommonStyles(item) {
	return LAYOUT_DATA_ITEMS_WITH_COMMON_STYLES.includes(item.type);
}

/**
 * Filter the styles that we don't need to include in the CSS.
 *
 * The values that are rejected are:
 *  - Empty style values
 *  - Values equals to the default value (in desktop).
 *  - Margin left and margin right in a fixed Container (fixed container already have defined margins).
 */
function filterStyles({item, selectedViewportSize, styles}) {
	const filteredStyles = {};

	Object.entries(styles).forEach(([styleName, styleValue]) => {
		const {defaultValue} = config.commonStylesFields[styleName];

		const isContainerFixed =
			item.config?.widthType === CONTAINER_WIDTH_TYPES.fixed;

		if (
			styleValue &&
			(defaultValue !== styleValue ||
				selectedViewportSize !== VIEWPORT_SIZES.desktop) &&
			(!isContainerFixed ||
				(styleName !== 'marginRight' && styleName !== 'marginLeft'))
		) {
			filteredStyles[styleName] = styleValue;
		}
	});

	return filteredStyles;
}

function calculateStyles({
	fragmentEntryLinks,
	isMaster,
	items,
	previousStyleSheet,
	previousStyles,
	selectedViewportSize,
}) {
	const itemsWithTopper = new Set();
	const nextStyles = {};

	items.forEach((item) => {
		if (hasCommonStyles(item)) {
			const customCSS = getResponsiveConfig(
				item.config,
				selectedViewportSize
			)?.customCSS;

			const styles = getResponsiveConfig(
				item.config,
				selectedViewportSize
			)?.styles;

			const filteredStyles = filterStyles({
				item,
				selectedViewportSize,
				styles,
			});

			nextStyles[item.itemId] = {
				customCSS,
				styles: filteredStyles,
			};

			if (!isMaster) {
				return;
			}

			const fragmentEntryLink =
				fragmentEntryLinks[item.config.fragmentEntryLinkId];

			if (
				item.type !== LAYOUT_DATA_ITEM_TYPES.fragment ||
				!hasInnerCommonStyles(fragmentEntryLink)
			) {
				itemsWithTopper.add(item.itemId);
			}
		}
	});

	if (
		!previousStyles ||
		!previousStyleSheet ||
		!deepEqual(previousStyles, nextStyles)
	) {
		const styleSheet = generateStyleSheet(nextStyles, {
			itemsWithTopper,
		});

		return {styleSheet, styles: nextStyles};
	}

	return {styleSheet: previousStyleSheet, styles: nextStyles};
}
