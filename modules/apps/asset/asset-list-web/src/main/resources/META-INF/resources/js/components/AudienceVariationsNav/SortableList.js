/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayList from '@clayui/list';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {saveVariationsListPriorityService} from '../../actions/index';
import SortableListItem from './SortableListItem';
import {buildItemsPriorityURL} from './utils/index';

const savePriority = async ({itemIndex, itemName, url}) => {
	try {
		const {ok, status} = await saveVariationsListPriorityService({url});

		if (!ok || status !== 200) {
			throw new Error();
		}

		openToast({
			message: sub(
				Liferay.Language.get('variation-x-moved-to-position-x'),
				itemName,
				itemIndex + 1
			),
			type: 'success',
		});
	}
	catch (error) {
		openToast({
			message: Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'danger',
		});
	}
};

const SortableList = ({defaultItem, items, namespace, savePriorityURL}) => {
	const [listItems, setListItems] = useState(items);

	const handleItemMove = ({
		direction = 0,
		hoverIndex = null,
		index,
		saveAfterMove = false,
	}) => {
		const nextIndex = hoverIndex ?? index + direction;
		const tempList = [...listItems];

		tempList.splice(index, 1);

		tempList.splice(nextIndex, 0, listItems[index]);

		setListItems(tempList);

		if (saveAfterMove) {
			handleSavePriority({
				itemIndex: nextIndex,
				itemName: listItems[index].name,
				items: tempList,
			});
		}
	};

	const handleSavePriority = ({itemIndex, itemName, items = listItems}) => {
		savePriority({
			itemIndex,
			itemName,
			url: buildItemsPriorityURL({
				items,
				namespace,
				url: savePriorityURL,
			}),
		});
	};

	const handleItemDelete = ({deleteURL}) => {
		if (!deleteURL) {
			return;
		}

		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					submitForm(document.hrefFm, deleteURL);
				}
			},
		});
	};

	return (
		<DndProvider backend={HTML5Backend}>
			<nav role="navigation">
				<ClayList className="mt-4" role="list">
					{listItems.map((item, index) => (
						<SortableListItem
							handleItemDelete={handleItemDelete}
							handleItemMove={handleItemMove}
							handleSavePriority={handleSavePriority}
							id={`sortableListItem-id-${item.id}`}
							index={index}
							key={item.id}
							role="listitem"
							sortableListItem={item}
							totalItems={listItems.length}
						/>
					))}

					{defaultItem && (
						<SortableListItem
							handleItemDelete={handleItemDelete}
							handleItemMove={handleItemMove}
							handleSavePriority={handleSavePriority}
							id={`sortableListItem-id-${defaultItem.id}`}
							index={listItems.length}
							role="listitem"
							sortable={false}
							sortableListItem={defaultItem}
							totalItems={listItems.length}
						/>
					)}
				</ClayList>
			</nav>
		</DndProvider>
	);
};

SortableList.propTypes = {
	defaultItem: PropTypes.object,
	items: PropTypes.array.isRequired,
	namespace: PropTypes.string.isRequired,
	savePriorityURL: PropTypes.string.isRequired,
};

export default SortableList;
