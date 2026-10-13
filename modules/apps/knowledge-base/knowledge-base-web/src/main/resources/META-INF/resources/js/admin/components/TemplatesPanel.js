/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TreeView as ClayTreeView} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import React, {useMemo} from 'react';

import normalizeItems from '../utils/normalizeItems';
import ActionsDropdown from './ActionsDropdown';

const ITEM_TYPES_SYMBOL = {
	KBFolder: 'folder',
	template: 'document-text',
};

export default function TemplatesPanel({items: initialItems, selectedItemId}) {
	const items = useMemo(() => normalizeItems(initialItems), [initialItems]);

	return items?.length ? (
		<ClayTreeView
			defaultItems={items}
			defaultSelectedKeys={new Set([selectedItemId])}
			nestedKey="children"
			showExpanderOnHover={false}
		>
			{(item) => {
				return (
					<ClayTreeView.Item
						actions={ActionsDropdown({actions: item.actions})}
					>
						<ClayTreeView.ItemStack
							className={classnames({
								'knowledge-base-navigation-item-active':
									item.id === selectedItemId,
							})}
						>
							<ClayLink displayType="secondary" href={item.href}>
								<ClayIcon
									symbol={ITEM_TYPES_SYMBOL[item.type]}
								/>

								<span className="component-text">
									{item.name}
								</span>
							</ClayLink>
						</ClayTreeView.ItemStack>

						<ClayTreeView.Group items={item.children}>
							{(item) => {
								return (
									<ClayTreeView.Item
										actions={ActionsDropdown({
											actions: item.actions,
										})}
										className={classnames({
											'knowledge-base-navigation-item-active':
												item.id === selectedItemId,
										})}
									>
										<ClayLink
											displayType="secondary"
											href={item.href}
										>
											<ClayIcon
												symbol={
													ITEM_TYPES_SYMBOL[item.type]
												}
											/>

											<span className="component-text">
												{item.name}
											</span>
										</ClayLink>
									</ClayTreeView.Item>
								);
							}}
						</ClayTreeView.Group>
					</ClayTreeView.Item>
				);
			}}
		</ClayTreeView>
	) : (
		<ClayEmptyState
			description=""
			image={
				<ClayIcon
					className="c-empty-state-icon c-empty-state-icon-empty-state"
					illustration
					symbol="empty-state"
				/>
			}
			small
			title={Liferay.Language.get('there-are-no-article-templates')}
		/>
	);
}

const itemShape = {
	href: PropTypes.string.isRequired,
	id: PropTypes.string.isRequired,
	name: PropTypes.string.isRequired,
	type: PropTypes.oneOf(Object.keys(ITEM_TYPES_SYMBOL)).isRequired,
};

itemShape.children = PropTypes.arrayOf(PropTypes.shape(itemShape));

TemplatesPanel.propTypes = {
	items: PropTypes.arrayOf(PropTypes.shape(itemShape)),
	selectedItemId: PropTypes.string,
};
