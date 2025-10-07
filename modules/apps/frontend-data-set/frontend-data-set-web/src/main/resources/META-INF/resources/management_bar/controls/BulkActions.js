/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import classNames from 'classnames';
import {postForm, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {OPEN_SIDE_PANEL} from '../../utils/eventsDefinitions';
import {getOpenedSidePanel} from '../../utils/sidePanels';
import ViewsContext from '../../views/ViewsContext';
import InfoPanelToggleButton from './InfoPanelToggleButton';
import SelectionCheckbox from './SelectionCheckbox';

function getQueryString(key, values = []) {
	return `?${key}=${values.join(',')}`;
}

function getRichPayload(payload, key, values = []) {
	const richPayload = {
		...payload,
		url: payload.baseURL + getQueryString(key, values),
	};

	return richPayload;
}

function BulkActions({
	bulkActions,
	fluid,
	handleCheckboxClick,
	handleSelectAll,
	items,
	onClear,
	pageSelectedItemsValue,
	selectedItems,
	selectedItemsKey,
	selectedItemsValue,
	showSelectAll,
	total,
}) {
	const {
		actionParameterName,
		allItemsSelectedActive,
		apiURL,
		onBulkActionItemClick,
		searchParam,
		showBulkActionsManagementBar,
		showBulkActionsManagementBarActions,
		showInfoPanel,
	} = useContext(FrontendDataSetContext);

	const [{filters}] = useContext(ViewsContext);

	const [currentSidePanelActionPayload, setCurrentSidePanelActionPayload] =
		useState(null);

	function getAdditionalData(filters, searchParam) {
		return {
			filters: filters
				.filter((item) => item.active)
				.map((item) => {
					return {
						id: item.id,
						multiple: item.multiple,
						odataFilterString: item.odataFilterString,
						selectedItemsLabel: item.selectedItemsLabel,
					};
				}),
			searchQuery: searchParam,
		};
	}

	function handleActionClick(
		actionDefinition,
		formId,
		formName,
		loadData,
		namespace,
		sidePanelId
	) {
		const {data, href, slug, target} = actionDefinition;
		if (target === 'sidePanel') {
			const sidePanelActionPayload = {
				baseURL: href,
				id: sidePanelId,
				onAfterSubmit: () => loadData(),
				slug: slug ?? null,
			};

			Liferay.fire(
				OPEN_SIDE_PANEL,
				getRichPayload(
					sidePanelActionPayload,
					selectedItemsKey,
					selectedItemsValue
				)
			);

			setCurrentSidePanelActionPayload(sidePanelActionPayload);
		}
		else if (onBulkActionItemClick) {
			onBulkActionItemClick({
				action: actionDefinition,
				formId,
				formName,
				loadData,
				namespace,
				selectedData: {
					apiURL,
					items: allItemsSelectedActive ? [] : selectedItems,
					keyValues: allItemsSelectedActive ? [] : selectedItemsValue,
					selectAll: allItemsSelectedActive,
					...(allItemsSelectedActive &&
						getAdditionalData(filters, searchParam)),
				},
			});
		}
		else if (formId || (formName && namespace)) {
			const namespacedId = formId || `${namespace}${formName}`;

			const form = document.getElementById(namespacedId);

			if (form) {
				postForm(form, {
					data: {
						...data,
						[`${actionParameterName || selectedItemsKey}`]:
							allItemsSelectedActive
								? []
								: selectedItemsValue.join(','),
						selectAll: allItemsSelectedActive,
						...(allItemsSelectedActive &&
							getAdditionalData(filters, searchParam)),
					},
					url: href || form.action,
				});
			}
		}
	}

	useEffect(
		() => {
			if (!currentSidePanelActionPayload) {
				return;
			}

			const currentOpenedSidePanel = getOpenedSidePanel();

			if (
				currentOpenedSidePanel?.id ===
					currentSidePanelActionPayload.id &&
				currentOpenedSidePanel.url.indexOf(
					currentSidePanelActionPayload.baseURL
				) > -1
			) {
				Liferay.fire(
					OPEN_SIDE_PANEL,
					getRichPayload(
						currentSidePanelActionPayload,
						selectedItemsValue
					)
				);
			}
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[selectedItemsValue]
	);

	return showBulkActionsManagementBar && selectedItemsValue.length ? (
		<FrontendDataSetContext.Consumer>
			{({
				formId,
				formName,
				loadData,
				namespace,
				selectable,
				sidePanelId,
			}) => (
				<nav
					className="management-bar management-bar-primary navbar navbar-expand-md"
					data-qa-id="selectionToolbar"
				>
					<div
						className={classNames(
							'container-fluid',
							!fluid && 'px-0'
						)}
					>
						<ul className="navbar-nav">
							{!!total && selectable && (
								<li className="nav-item">
									<SelectionCheckbox
										handleCheckboxClick={
											handleCheckboxClick
										}
										items={items}
										selectedItemsValue={
											pageSelectedItemsValue
										}
									/>
								</li>
							)}

							<li className="nav-item">
								<span className="text-truncate">
									{selectedItemsValue.length === total ||
									allItemsSelectedActive
										? sub(
												Liferay.Language.get(
													'all-selected-x-of-x-items'
												),
												total,
												total
											)
										: sub(
												Liferay.Language.get(
													'x-of-x-items-selected'
												),
												selectedItemsValue.length,
												total
											)}
								</span>

								<ClayLink
									className="c-ml-3"
									href="#"
									onClick={(event) => {
										event.preventDefault();
										onClear();
									}}
								>
									{Liferay.Language.get('clear')}
								</ClayLink>

								{pageSelectedItemsValue.length ===
									items.length &&
									showSelectAll &&
									!allItemsSelectedActive && (
										<ClayLink
											className="c-ml-3"
											href="#"
											onClick={(event) => {
												event.preventDefault();
												handleSelectAll(true);
											}}
										>
											{Liferay.Language.get('select-all')}
										</ClayLink>
									)}
							</li>
						</ul>

						{showBulkActionsManagementBarActions && (
							<ul className="bulk-actions navbar-nav">
								{!!bulkActions.length &&
									bulkActions
										.filter(
											(bulkAction) =>
												bulkAction.data?.highlighted
										)
										.map((highlightedBulkAction) => {
											return (
												<li
													className="nav-item"
													key={
														highlightedBulkAction
															.data?.id
													}
												>
													<ClayButton
														className="bulk-action-btn nav-link"
														displayType="unstyled"
														onClick={() =>
															handleActionClick(
																highlightedBulkAction,
																formId,
																formName,
																loadData,
																namespace,
																sidePanelId
															)
														}
													>
														<span className="bulk-action-btn-icon inline-item inline-item-before">
															<ClayIcon
																symbol={
																	highlightedBulkAction.icon
																}
															/>
														</span>

														<span className="bulk-action-btn-text">
															{
																highlightedBulkAction.label
															}
														</span>
													</ClayButton>
												</li>
											);
										})}

								{!!bulkActions.length && (
									<li className="nav-item">
										<DropDown
											closeOnClick
											hasLeftSymbols
											trigger={
												<ClayButtonWithIcon
													aria-label={Liferay.Language.get(
														'actions'
													)}
													className="nav-link nav-link-monospaced"
													displayType="unstyled"
													symbol="ellipsis-v"
													title={Liferay.Language.get(
														'actions'
													)}
												/>
											}
										>
											<DropDown.ItemList>
												{bulkActions.map(
													(actionDefinition) => (
														<DropDown.Item
															key={
																actionDefinition.label
															}
															onClick={() =>
																handleActionClick(
																	actionDefinition,
																	formId,
																	formName,
																	loadData,
																	namespace,
																	sidePanelId
																)
															}
															symbolLeft={
																actionDefinition.icon
															}
														>
															{
																actionDefinition.label
															}
														</DropDown.Item>
													)
												)}
											</DropDown.ItemList>
										</DropDown>
									</li>
								)}

								{showInfoPanel && (
									<li className="nav-item">
										<InfoPanelToggleButton symbol="info-circle" />
									</li>
								)}
							</ul>
						)}
					</div>
				</nav>
			)}
		</FrontendDataSetContext.Consumer>
	) : null;
}

BulkActions.propTypes = {
	allItemsSelectedActive: PropTypes.bool,
	bulkActions: PropTypes.arrayOf(
		PropTypes.shape({
			href: PropTypes.string.isRequired,
			icon: PropTypes.string.isRequired,
			label: PropTypes.string.isRequired,
			method: PropTypes.string,
			target: PropTypes.oneOf(['sidePanel', 'modal']),
		})
	),
	deselectItems: PropTypes.func.isRequired,
	fluid: PropTypes.bool.isRequired,
	handleCheckboxClick: PropTypes.func.isRequired,
	handleSelectAll: PropTypes.func.isRequired,
	items: PropTypes.array.isRequired,
	onClear: PropTypes.func.isRequired,
	pageSelectedItemsValue: PropTypes.array.isRequired,
	selectedItemsKey: PropTypes.string.isRequired,
	selectedItemsValue: PropTypes.array.isRequired,
	showSelectAll: PropTypes.bool.isRequired,
	total: PropTypes.number,
};

export default BulkActions;
