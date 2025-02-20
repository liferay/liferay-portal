/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView as ClayTreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

import '../css/PagesTree.scss';

const ROOT_ITEM_ID = '0';
const SPACE_KEYCODE = 32;

export function PagesTree({
	config,
	groupId,
	items,
	portletNamespace: namespace,
	privateLayout,
	selectedLayoutIds: initialSelectedLayoutIds,
	treeId,
}) {
	const {changeItemSelectionURL, loadMoreItemsURL, maxPageSize} = config;

	const [selectedLayoutIds, setSelectedLayoutIds] = useState(
		initialSelectedLayoutIds
	);

	const onLoadMore = useCallback(
		(item, initialCursor = 1) => {
			if (!item.hasChildren) {
				return Promise.resolve({
					cursor: null,
					items: null,
				});
			}

			const cursor = item.children ? initialCursor : 0;

			return fetch(loadMoreItemsURL, {
				body: Liferay.Util.objectToURLSearchParams({
					groupId,
					parentLayoutId: item.layoutId,
					privateLayout,
					selPlid: item.plid,
					start: cursor * maxPageSize,
				}),
				method: 'post',
			})
				.then((response) => response.json())
				.then(({hasMoreElements, items: nextItems}) => ({
					cursor: hasMoreElements ? cursor + 1 : null,
					items: nextItems,
				}))
				.catch(() => openErrorToast());
		},
		[groupId, loadMoreItemsURL, maxPageSize, privateLayout]
	);

	const onSelectedChange = useCallback(
		(selected, item) => {
			fetch(changeItemSelectionURL, {
				body: Liferay.Util.objectToFormData({
					cmd: selected ? 'layoutCheck' : 'layoutUncheck',
					doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
					groupId,
					layoutId: item.layoutId,
					plid: item.plid,
					privateLayout,
					recursive: true,
					treeId: `${treeId}SelectedNode`,
				}),
				method: 'post',
			})
				.then((response) => response.json())
				.then((nextSelectedLayoutIds) =>
					setSelectedLayoutIds(nextSelectedLayoutIds)
				)
				.catch(() => openErrorToast());
		},
		[changeItemSelectionURL, groupId, privateLayout, treeId]
	);

	return (
		<>
			<ClayTreeView
				defaultExpandedKeys={new Set([ROOT_ITEM_ID])}
				defaultItems={items}
				onLoadMore={onLoadMore}
				onSelectionChange={() => {}}
				selectedKeys={new Set(selectedLayoutIds)}
				selectionMode="multiple"
				showExpanderOnHover={false}
			>
				{(item, selection, expand, load) => (
					<TreeItem
						expand={expand}
						item={item}
						load={load}
						namespace={namespace}
						onSelectedChange={onSelectedChange}
						selection={selection}
					/>
				)}
			</ClayTreeView>

			<input
				name={`${namespace}layoutIds`}
				readOnly
				type="hidden"
				value={JSON.stringify(selectedLayoutIds)}
			/>
		</>
	);
}

PagesTree.propTypes = {
	config: PropTypes.object.isRequired,
	groupId: PropTypes.string.isRequired,
	items: PropTypes.array.isRequired,
	portletNamespace: PropTypes.string.isRequired,
	privateLayout: PropTypes.bool.isRequired,
	selectedLayoutIds: PropTypes.array.isRequired,
	treeId: PropTypes.string.isRequired,
};

function TreeItem({
	expand,
	item,
	load,
	namespace,
	onSelectedChange,
	selection,
}) {
	const handleKeyDown = useCallback(
		(event, item) => {
			if (event.keyCode === SPACE_KEYCODE) {
				event.stopPropagation();

				onSelectedChange(!selection.has(item.id), item);
			}
		},
		[onSelectedChange, selection]
	);

	return (
		<ClayTreeView.Item onKeyDown={(event) => handleKeyDown(event, item)}>
			<ClayTreeView.ItemStack>
				<ClayCheckbox
					containerProps={{className: 'my-0'}}
					onChange={(event) => {
						onSelectedChange(event.target.checked, item);
					}}
					tabIndex={-1}
				/>

				{item.icon && <ClayIcon symbol={item.icon} />}

				<div className={classNames('align-items-center c-ml-1 d-flex')}>
					<span
						className={classNames('flex-grow-0', {
							'layout-incomplete': item.incomplete,
						})}
						title={getItemTitle(item)}
					>
						{getItemName(item)}
					</span>

					{item.id !== '0' && !item.hasGuestViewPermission ? (
						<span
							aria-label={Liferay.Language.get('restricted-page')}
							className="c-ml-2 lfr-portal-tooltip"
							title={Liferay.Language.get('restricted-page')}
						>
							<ClayIcon
								className="c-mt-0 lfr-portal-tooltip text-4"
								symbol="password-policies"
							/>
						</span>
					) : null}
				</div>
			</ClayTreeView.ItemStack>

			<ClayTreeView.Group items={item.children}>
				{(childItem) => (
					<ClayTreeView.Item
						expandable={childItem.hasChildren}
						onKeyDown={(event) => handleKeyDown(event, childItem)}
					>
						<ClayCheckbox
							aria-labelledby={getId(namespace, childItem.id)}
							containerProps={{className: 'my-0'}}
							onChange={(event) =>
								onSelectedChange(
									event.target.checked,
									childItem
								)
							}
							tabIndex={-1}
						/>

						{childItem.icon && <ClayIcon symbol={childItem.icon} />}

						<div
							className={classNames(
								'align-items-center c-ml-1 d-flex'
							)}
						>
							<span
								className={classNames({
									'layout-incomplete': childItem.incomplete,
								})}
								id={getId(namespace, childItem.id)}
								title={getItemTitle(childItem)}
							>
								{getItemName(childItem)}
							</span>

							{!childItem.hasGuestViewPermission ? (
								<span
									aria-label={Liferay.Language.get(
										'restricted-page'
									)}
									className="c-ml-2 lfr-portal-tooltip"
									title={Liferay.Language.get(
										'restricted-page'
									)}
								>
									<ClayIcon
										className="c-mt-0 lfr-portal-tooltip text-4"
										symbol="password-policies"
									/>
								</span>
							) : null}
						</div>
					</ClayTreeView.Item>
				)}
			</ClayTreeView.Group>

			{load.get(item.id) !== null &&
				expand.has(item.id) &&
				item.paginated && (
					<ClayButton
						borderless
						className="ml-3"
						displayType="secondary"
						onClick={() => load.loadMore(item.id, item)}
					>
						{Liferay.Language.get('load-more-results')}
					</ClayButton>
				)}
		</ClayTreeView.Item>
	);
}

TreeItem.propTypes = {
	expand: PropTypes.object.isRequired,
	item: PropTypes.object.isRequired,
	load: PropTypes.object.isRequired,
	namespace: PropTypes.string.isRequired,
	onSelectedChange: PropTypes.func.isRequired,
	selection: PropTypes.object.isRequired,
};

function getId(namespace, key) {
	return `${namespace}pages-tree-${key}`;
}

function getItemName(item) {
	if (item.layoutBranchName) {
		return `${item.name} [${item.layoutBranchName}]`;
	}

	return item.name;
}

function getItemTitle(item) {
	if (!item.layoutRevisionId) {
		return null;
	}

	if (!item.layoutRevisionHead) {
		return Liferay.Language.get(
			'there-is-no-version-of-this-page-marked-as-ready-for-publication'
		);
	}

	if (item.layoutBranchName) {
		return Liferay.Language.get(
			'this-is-the-page-variation-that-is-marked-as-ready-for-publication'
		);
	}

	if (item.incomplete) {
		return Liferay.Language.get(
			'this-page-is-not-enabled-in-this-site-pages-variation,-but-is-available-in-other-variations'
		);
	}
}

function openErrorToast() {
	openToast({
		message: Liferay.Language.get('an-unexpected-error-occurred'),
		title: Liferay.Language.get('error'),
		type: 'danger',
	});
}
