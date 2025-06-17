/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IItemsActions} from '..';
import React, {useContext, useState} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../FrontendDataSetContext';
import filterItemActions from '../utils/actionItems/filterItemActions';
import handleActionClick from '../utils/actionItems/handleActionClick';
import ViewsContext from '../views/ViewsContext';
import ActionsDropdown from './ActionsDropdown';
import QuickActions from './QuickActions';

const QUICK_ACTIONS_MAX_NUMBER = 3;

function Actions({
	actions,
	itemData,
	itemId,
}: {
	actions: Array<IItemsActions>;
	itemData: any;
	itemId: string | number;
}) {
	const {
		allItemsSelectedActive,
		executeAsyncItemAction,
		highlightItems,
		inlineEditingSettings,
		loadData,
		onActionDropdownItemClick,
		onInfoPanelToggleButtonClick,
		openModal,
		openSidePanel,
		selectedItemsValue,
		toggleItemInlineEdit,
	}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

	const [
		{
			activeView: {quickActionsEnabled},
		},
	]: any = useContext(ViewsContext);

	const [loading, setLoading] = useState(false);
	const [menuActive, setMenuActive] = useState(false);

	const isRowSelected =
		allItemsSelectedActive ||
		selectedItemsValue?.some(
			(selectedItemValue) => String(selectedItemValue) === String(itemId)
		);

	const inlineEditingAvailable =
		inlineEditingSettings && itemData.actions?.update;

	const inlineEditingAlwaysOn =
		inlineEditingAvailable && inlineEditingSettings.alwaysOn;

	const formattedActions = filterItemActions(actions, itemData);

	if (inlineEditingAvailable && !inlineEditingAlwaysOn) {
		formattedActions.unshift({
			icon: 'fieldset',
			label: Liferay.Language.get('inline-edit'),
			target: 'inlineEdit',
		});
	}

	const handleClick = ({
		action,
		closeMenu,
		event,
	}: {
		action: IItemsActions;
		closeMenu: any;
		event: any;
	}) => {
		handleActionClick({
			action,
			closeMenu,
			event,
			executeAsyncItemAction,
			highlightItems,
			itemData,
			itemId,
			loadData,
			onActionDropdownItemClick,
			onInfoPanelToggleButtonClick,
			openModal,
			openSidePanel,
			setLoading,
			toggleItemInlineEdit,
		});
	};

	return (
		<>
			{quickActionsEnabled &&
				formattedActions.length > 1 &&
				!isRowSelected && (
					<QuickActions
						actions={formattedActions.slice(
							0,
							QUICK_ACTIONS_MAX_NUMBER
						)}
						itemData={itemData}
						itemId={itemId}
						onClick={handleClick}
					/>
				)}

			<ActionsDropdown
				actions={formattedActions}
				itemData={itemData}
				itemId={itemId}
				loading={loading}
				menuActive={menuActive}
				onClick={handleClick}
				onMenuActiveChange={setMenuActive}
				setLoading={setLoading}
			/>
		</>
	);
}

export default Actions;
