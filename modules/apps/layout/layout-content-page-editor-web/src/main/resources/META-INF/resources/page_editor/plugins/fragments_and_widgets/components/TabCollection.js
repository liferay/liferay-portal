/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {FeatureIndicator, useSessionState} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useRef, useState} from 'react';

import {FRAGMENTS_DISPLAY_STYLES} from '../../../app/config/constants/fragmentsDisplayStyles';
import {HIGHLIGHTED_COLLECTION_ID} from '../../../app/config/constants/highlightedCollectionId';
import {LIST_ITEM_TYPES} from '../../../app/config/constants/listItemTypes';
import {config} from '../../../app/config/index';
import useKeyboardNavigation from '../hooks/useKeyboardNavigation';
import {ScopeIndicator} from './ScopeIndicator';
import TabItem from './TabItem';

export default function TabCollection({
	collection,
	displayStyle,
	initialOpen,
	isSearchResult,
}) {
	const [open, setOpen] = useSessionState(
		`${config.portletNamespace}_fragment-collection_${collection.collectionId}_open`,
		initialOpen
	);

	const onRemoveHighlighted = (itemIndex) => {
		if (collection.collectionId !== HIGHLIGHTED_COLLECTION_ID) {
			return;
		}

		const items = collapseRef.current?.querySelectorAll('li');

		if (items.length === 1) {
			const nextCollapse = collapseRef.current.nextSibling;
			const nextHeader = nextCollapse.querySelector('button');

			nextHeader.focus();
		}

		const nextIndex = itemIndex === 0 ? 1 : itemIndex - 1;

		items[nextIndex]?.focus();
	};

	const collapseRef = useRef();

	return (
		<TabCollectionCollapse
			collapseRef={collapseRef}
			deprecated={collection.deprecated}
			isSearchResult={isSearchResult}
			open={open}
			scope={collection.scope}
			setOpen={setOpen}
			title={collection.label}
		>
			{collection.collections &&
				collection.collections.map((collection, index) => (
					<TabCollection
						collection={collection}
						displayStyle={displayStyle}
						initialOpen={false}
						isSearchResult={isSearchResult}
						key={index}
					/>
				))}

			<ul
				aria-orientation="vertical"
				className={`list-unstyled page-editor__fragments-widgets__tab-collection-${displayStyle} pb-2 w-100`}
				role="menu"
			>
				{collection.children.map((item, index) => (
					<React.Fragment key={item.itemId}>
						<TabItem
							displayStyle={displayStyle}
							item={item}
							key={item.itemId}
							onRemoveHighlighted={() =>
								onRemoveHighlighted(index)
							}
						/>

						{item.portletItems?.length && (
							<TabPortletItems item={item} />
						)}
					</React.Fragment>
				))}
			</ul>
		</TabCollectionCollapse>
	);
}

TabCollection.proptypes = {
	collection: PropTypes.shape({
		children: PropTypes.array,
		collectionId: PropTypes.string,
		label: PropTypes.string,
	}).isRequired,
	displayStyle: PropTypes.oneOf(Object.values(FRAGMENTS_DISPLAY_STYLES)),
	isSearchResult: PropTypes.bool,
	open: PropTypes.bool,
};

const TabPortletItems = ({item}) => (
	<ul className="d-flex flex-wrap list-unstyled w-100">
		{item.portletItems.map((portlet, index) => (
			<TabItem item={portlet} key={index} />
		))}
	</ul>
);

TabPortletItems.proptypes = {
	data: PropTypes.shape({
		instanceable: PropTypes.bool,
		portletId: PropTypes.string,
		portletItemId: PropTypes.string,
		used: PropTypes.bool,
	}).isRequired,
	disabled: PropTypes.bool,
	icon: PropTypes.string,
	itemId: PropTypes.string,
	label: PropTypes.string,
	portletItems: PropTypes.array,
	preview: PropTypes.string,
	type: PropTypes.string,
};

function TabCollectionCollapse({
	children,
	collapseRef,
	deprecated,
	isSearchResult,
	open,
	scope,
	setOpen,
	title,
}) {
	const [searchOpen, setSearchOpen] = useState(true);

	const isOpen = isSearchResult ? searchOpen : open;
	const setIsOpen = isSearchResult ? setSearchOpen : setOpen;

	const {isTarget, setElement} = useKeyboardNavigation({
		handleOpen: setIsOpen,
		type: LIST_ITEM_TYPES.header,
	});

	return (
		<li
			className="page-editor__collapse panel panel-unstyled"
			ref={collapseRef}
			role="none"
		>
			<button
				aria-expanded={isOpen ? 'true' : 'false'}
				aria-haspopup="menu"
				className={classNames(
					'mb-3 panel-header panel-header-link collapse-icon collapse-icon-middle show btn btn-unstyled',
					{
						collapsed: !isOpen,
					}
				)}
				onClick={() => setIsOpen(!isOpen)}
				ref={setElement}
				role="menuitem"
				tabIndex={isTarget ? 0 : -1}
				type="button"
			>
				<span className="panel-title">
					<span className="c-mr-2">{title}</span>

					<ScopeIndicator scope={scope} />

					{deprecated ? <FeatureIndicator type="deprecated" /> : null}

					<span
						className={`text-secondary collapse-icon-${
							isOpen ? 'open' : 'closed'
						}`}
					>
						<ClayIcon
							symbol={isOpen ? 'angle-down' : 'angle-right'}
						/>
					</span>
				</span>
			</button>

			{isOpen && children}
		</li>
	);
}

TabCollectionCollapse.proptypes = {
	open: PropTypes.bool.isRequired,
	setOpen: PropTypes.func.isRequired,
	title: PropTypes.string.isRequired,
};
