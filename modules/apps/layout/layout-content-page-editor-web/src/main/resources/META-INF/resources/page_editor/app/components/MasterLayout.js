/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useRef} from 'react';
import {useDrop} from 'react-dnd';

import {
	LayoutDataPropTypes,
	getLayoutDataItemPropTypes,
} from '../../prop_types/index';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';
import {useSelectItem} from '../contexts/ControlsContext';
import {useSelector, useSelectorRef} from '../contexts/StoreContext';
import Layout from './Layout';
import FragmentContent from './fragment_content/FragmentContent';
import FormRelationship from './layout_data_items/FormRelationship';
import {FormStep} from './layout_data_items/FormStep';
import {FormStepContainer} from './layout_data_items/FormStepContainer';
import hasDropZoneChild from './layout_data_items/hasDropZoneChild';
import {
	Collection,
	Column,
	Container,
	Form,
	Row,
} from './layout_data_items/index';

const LAYOUT_DATA_ITEMS = {
	[LAYOUT_DATA_ITEM_TYPES.collection]: Collection,
	[LAYOUT_DATA_ITEM_TYPES.collectionItem]: CollectionItem,
	[LAYOUT_DATA_ITEM_TYPES.column]: MasterColumn,
	[LAYOUT_DATA_ITEM_TYPES.container]: Container,
	[LAYOUT_DATA_ITEM_TYPES.form]: Form,
	[LAYOUT_DATA_ITEM_TYPES.formRelationship]: FormRelationship,
	[LAYOUT_DATA_ITEM_TYPES.formStep]: FormStep,
	[LAYOUT_DATA_ITEM_TYPES.formStepContainer]: FormStepContainer,
	[LAYOUT_DATA_ITEM_TYPES.dropZone]: DropZoneContainer,
	[LAYOUT_DATA_ITEM_TYPES.fragment]: Fragment,
	[LAYOUT_DATA_ITEM_TYPES.fragmentDropZone]: Root,
	[LAYOUT_DATA_ITEM_TYPES.root]: Root,
	[LAYOUT_DATA_ITEM_TYPES.row]: Row,
};

const MasterPage = React.memo(() => {
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const masterLayoutData = useSelector(
		(state) => state.masterLayout?.masterLayoutData
	);

	const mainItem = masterLayoutData.items[masterLayoutData.rootItems.main];

	const [, targetRef] = useDrop({
		accept: Object.values(LAYOUT_DATA_ITEM_TYPES),
		drop: (_, monitor) => {
			const {x, y} = monitor.getClientOffset();

			const element = document.elementFromPoint(x, y);

			if (element.closest('.page-editor')) {
				return;
			}

			openToast({
				message: Liferay.Language.get(
					'fragments-and-widgets-cannot-be-placed-inside-this-area'
				),
				type: 'danger',
			});
		},
	});

	return (
		<div className="master-page" ref={targetRef}>
			<MasterLayoutDataItem
				fragmentEntryLinks={fragmentEntryLinks}
				item={mainItem}
				layoutData={masterLayoutData}
			/>
		</div>
	);
});

MasterPage.displayName = 'MasterPage';

export default MasterPage;

function MasterLayoutDataItem({fragmentEntryLinks, item, layoutData}) {
	const Component = LAYOUT_DATA_ITEMS[item.type];

	if (!Component) {
		return null;
	}

	return (
		<Component
			fragmentEntryLinks={fragmentEntryLinks}
			item={item}
			layoutData={layoutData}
		>
			{item.children.map((childId) => {
				return (
					<MasterLayoutDataItem
						fragmentEntryLinks={fragmentEntryLinks}
						item={layoutData.items[childId]}
						key={childId}
						layoutData={layoutData}
					/>
				);
			})}
		</Component>
	);
}

MasterLayoutDataItem.propTypes = {
	fragmentEntryLinks: PropTypes.object.isRequired,
	item: getLayoutDataItemPropTypes().isRequired,
	layoutData: LayoutDataPropTypes.isRequired,
};

function DropZoneContainer() {
	const mainItemId = useSelector((state) => state.layoutData.rootItems.main);

	return <Layout mainItemId={mainItemId} withinMasterPage />;
}

function Root({children}) {
	return <div>{children}</div>;
}

function CollectionItem({children}) {
	return <div>{children}</div>;
}

function Fragment({item, layoutData}) {
	const ref = useRef(null);
	const selectItem = useSelectItem();

	const hasDropzoneChild = useMemo(
		() => hasDropZoneChild(item, layoutData),
		[item, layoutData]
	);

	useEffect(() => {
		const element = ref.current;

		if (!element) {
			return;
		}

		const handler = (event) => {
			const element = event.target;

			if (element.closest('[href]')) {
				event.preventDefault();
			}

			if (!event.target.closest('.page-editor')) {
				selectItem(null);
			}
		};

		element.addEventListener('click', handler);

		if (!hasDropzoneChild) {
			element.setAttribute('inert', '');
		}

		element.setAttribute('aria-hidden', 'true');

		return () => {
			element.removeEventListener('click', handler);

			if (!hasDropzoneChild) {
				element.removeAttribute('inert');
			}

			element.removeAttribute('aria-hidden');
		};
	});

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);
	const masterLayoutDataRef = useSelectorRef(
		(state) => state.masterLayout?.masterLayoutData
	);

	const getPortals = useCallback(
		(element) =>
			Array.from(element.querySelectorAll('lfr-drop-zone')).map(
				(dropZoneElement) => {
					const mainItemId =
						dropZoneElement.getAttribute('uuid') || '';

					const Component = () =>
						mainItemId ? (
							<MasterLayoutDataItem
								fragmentEntryLinks={
									fragmentEntryLinksRef.current
								}
								item={
									masterLayoutDataRef.current.items[
										mainItemId
									]
								}
								layoutData={masterLayoutDataRef.current}
							/>
						) : null;

					Component.displayName = `DropZone(${mainItemId})`;

					return {
						Component,
						element: dropZoneElement,
					};
				}
			),
		[fragmentEntryLinksRef, masterLayoutDataRef]
	);

	return (
		<FragmentContent
			className="page-editor__fragment-content--master"
			elementRef={ref}
			fragmentEntryLinkId={item.config.fragmentEntryLinkId}
			getPortals={getPortals}
			item={item}
		/>
	);
}

Fragment.propTypes = {
	fragmentEntryLinks: PropTypes.object.isRequired,
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}),
	}).isRequired,
};

function MasterColumn({children, className, ...otherProps}) {
	return (
		<Column
			{...otherProps}
			className={classNames(className, 'page-editor__col--master')}
		>
			{children}
		</Column>
	);
}
