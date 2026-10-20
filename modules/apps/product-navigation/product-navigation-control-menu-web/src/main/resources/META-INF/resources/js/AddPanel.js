/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DragPreview} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useEffect, useMemo, useState} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import DragAndDrop from './DragAndDrop';
import TabsPanel from './TabsPanel';
import {LAYOUT_DATA_ITEM_TYPES} from './constants/layoutDataItemTypes';

const INITIAL_STATE = {
	addContentsURLs: null,
	contents: null,
	displayGrid: false,
	getContentsURL: null,
	hasAddContentPermission: false,
	namespace: null,
	plid: null,
	portletNamespace: null,
	setDisplayGrid: () => null,
	setWidgets: () => null,
	widgets: null,
};

export const AddPanelContext = React.createContext(INITIAL_STATE);
const AddPanelContextProvider = AddPanelContext.Provider;

const updateUsedPortlet = ({item, portlet, used}) => {
	if (portlet.portletId === item.itemId && !portlet.instanceable) {
		portlet.used = used;

		if (portlet.portletItems?.length) {
			portlet.portletItems.map((portletItem) => {
				portletItem.used = used;
			});
		}
	}
};

const updateUsedCategoryPortlet = ({category, item, used}) => {
	if (category.portlets?.length) {
		category.portlets.map((portlet) => {
			updateUsedPortlet({item, portlet, used});
		});
	}

	return category.categories?.length
		? {
				...category,
				categories: category.categories.map((category) =>
					updateUsedCategoryPortlet({category, item, used})
				),
			}
		: category;
};

export function updateUsedWidget({item, used = true, widgets}) {
	return widgets.map((collection) => {
		updateUsedCategoryPortlet({category: collection, item, used});

		return {
			...collection,
			portlets: collection.portlets.map((portlet) => {
				updateUsedPortlet({item, portlet, used});

				return {...portlet};
			}),
		};
	});
}

const normalizeWidget = (widget) => {
	return {
		data: {
			deprecated: widget.deprecated,
			instanceable: widget.instanceable,
			portletId: widget.portletId,
			portletItemId: widget.portletItemId || null,
			used: widget.used,
		},
		disabled: !widget.instanceable && widget.used,
		icon: widget.instanceable ? 'square-hole-multi' : 'square-hole',
		itemId: widget.portletId,
		label: widget.title,
		portletItems: widget.portletItems?.length
			? widget.portletItems.map(normalizeWidget)
			: null,
		type: LAYOUT_DATA_ITEM_TYPES.widget,
	};
};

const normalizeCollections = (collection) => {
	const normalizedElement = {
		children: collection.portlets.map(normalizeWidget),
		collectionId: collection.path,
		label: collection.title,
	};

	if (collection.categories?.length) {
		normalizedElement.collections =
			collection.categories.map(normalizeCollections);
	}

	return normalizedElement;
};

export function normalizeContent(content) {
	return {
		category: content.type,
		data: {
			className: content.className,
			classPK: content.classPK,
			draggable: content.draggable,
			instanceable: content.instanceable,
			portletId: content.portletId,
		},
		disabled: !content.draggable,
		icon: content.icon,
		itemId: `${content.portletId}_${content.classPK}`,
		label: content.title,
		type: LAYOUT_DATA_ITEM_TYPES.content,
	};
}

const AddPanel = ({
	addContentsURLs,
	contents,
	getContentsURL,
	hasAddContentPermission,
	languageDirection,
	languageId,
	namespace,
	plid,
	portletNamespace,
	widgets: widgetsItems,
}) => {
	const [displayGrid, setDisplayGrid] = useState(false);
	const [widgets, setWidgets] = useState(widgetsItems);

	const rtl = languageDirection[languageId] === 'rtl';

	useEffect(() => {
		const removePortlet = (item) => {
			const portlet = {
				...item,
				itemId: item.portletId.replace(/_USER_(.*)/, ''),
			};
			const updatedWidgets = updateUsedWidget({
				item: portlet,
				used: false,
				widgets,
			});

			setWidgets(updatedWidgets);
		};

		Liferay.on('closePortlet', removePortlet);

		return () => {
			Liferay.detach('closePortlet', removePortlet);
		};
	}, [widgets]);

	const tabs = useMemo(
		() => [
			{
				collections: widgets.map((collection) =>
					normalizeCollections(collection)
				),
				id: 'widgets',
				label: Liferay.Language.get('widgets'),
			},
			...(hasAddContentPermission
				? [
						{
							collections: [
								{
									children: contents.map(normalizeContent),
									collectionId: 'recent-content',
									label: Liferay.Language.get(
										'recent[items]'
									),
								},
							],
							id: 'content',
							label: Liferay.Language.get('content'),
						},
					]
				: []),
		],
		[contents, hasAddContentPermission, widgets]
	);

	return (
		<div className={classNames('sidebar-body__add-panel p-0', {rtl})}>
			<AddPanelContextProvider
				value={{
					addContentsURLs,
					contents,
					displayGrid,
					getContentsURL,
					namespace,
					plid,
					portletNamespace,
					setDisplayGrid,
					setWidgets,
					widgets,
				}}
			>
				<DndProvider backend={HTML5Backend}>
					<DragPreview />

					<DragAndDrop />

					<TabsPanel tabs={tabs} />
				</DndProvider>
			</AddPanelContextProvider>
		</div>
	);
};

AddPanel.propTypes = {
	addContentsURLs: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
	contents: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
	getContentsURL: PropTypes.string.isRequired,
	hasAddContentPermission: PropTypes.bool.isRequired,
	languageDirection: PropTypes.shape({}),
	languageId: PropTypes.string.isRequired,
	namespace: PropTypes.string.isRequired,
	plid: PropTypes.string.isRequired,
	portletNamespace: PropTypes.string.isRequired,
	widgets: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
};

export default AddPanel;
