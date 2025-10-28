/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate, setSessionValue} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

function PageTypeSelector({
	addLayoutURL,
	configureLayoutSetURL,
	namespace,
	pageTypeOptions,
	pageTypeSelectedOption,
	pagesTreeURL,
	showAddIcon,
}) {
	const [addPageDropdownActive, setAddPageDropdownActive] = useState(false);

	const handleSelect = (type) => {
		setSessionValue(`${namespace}PAGE_TYPE_SELECTED_OPTION`, type).then(
			() => {
				Liferay.Portlet.destroy(`#p_p_id${namespace}`);

				fetch(pagesTreeURL)
					.then((response) => {
						if (!response.ok) {
							throw new Error();
						}

						return response.text();
					})
					.then((productMenuContent) => {
						const sidebar = document.querySelector(
							'.lfr-product-menu-sidebar .sidebar-body .pages-tree'
						);

						sidebar.innerHTML = '';

						const range = document.createRange();
						range.selectNode(sidebar);

						sidebar.appendChild(
							range.createContextualFragment(productMenuContent)
						);
					})
					.catch(() => {
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							title: Liferay.Language.get('error'),
							type: 'danger',
						});
					});
			}
		);
	};

	const handleOnAddPageClick = useCallback(() => {
		setAddPageDropdownActive(false);
		navigate(addLayoutURL);
	}, [addLayoutURL]);

	return (
		<div className="align-items-center d-flex page-type-selector">
			<Picker
				UNSAFE_menuClassName="cadmin"
				aria-label={Liferay.Language.get('pages-type')}
				className="form-control-sm pr-5 w-auto"
				items={pageTypeOptions.filter((option) => option.items.length)}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onSelectionChange={handleSelect}
				selectedKey={pageTypeSelectedOption}
			>
				{(group) => (
					<ClayDropDown.Group
						header={group.label}
						items={group.items}
						key={group.label}
					>
						{(item) => (
							<Option
								className="page-type-selector-option"
								id={item.value}
								key={item.value}
							>
								{item.label}
							</Option>
						)}
					</ClayDropDown.Group>
				)}
			</Picker>

			<div className="flex-fill flex-grow-1 text-right">
				{showAddIcon && (
					<ClayDropDown
						active={addPageDropdownActive}
						menuElementAttrs={{
							containerProps: {
								className: 'cadmin',
							},
						}}
						onActiveChange={setAddPageDropdownActive}
						trigger={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('add-page')}
								className="add-page-button"
								displayType="unstyled"
								size="sm"
								symbol="plus"
								title={Liferay.Language.get('add-page')}
							/>
						}
					>
						<ClayDropDown.ItemList>
							{addLayoutURL && (
								<ClayDropDown.Item
									data-value={Liferay.Language.get(
										'add-page'
									)}
									key={Liferay.Language.get('add-page')}
									onClick={handleOnAddPageClick}
									title={Liferay.Language.get('add-page')}
								>
									{Liferay.Language.get('add-page')}
								</ClayDropDown.Item>
							)}
						</ClayDropDown.ItemList>
					</ClayDropDown>
				)}
			</div>

			<div className="autofit-col ml-2">
				{configureLayoutSetURL && (
					<ClayLink
						aria-label={Liferay.Language.get('configure-pages')}
						borderless
						className="configure-link"
						displayType="unstyled"
						href={configureLayoutSetURL}
						monospaced
						outline
						title={Liferay.Language.get('configure-pages')}
					>
						<ClayIcon symbol="cog" />
					</ClayLink>
				)}
			</div>
		</div>
	);
}

PageTypeSelector.propTypes = {
	addLayoutURL: PropTypes.string,
	configureLayoutSetURL: PropTypes.string,
	namespace: PropTypes.string,
	pageTypeOptions: PropTypes.arrayOf(
		PropTypes.shape({
			items: PropTypes.arrayOf(
				PropTypes.shape({
					name: PropTypes.string,
					value: PropTypes.value,
				})
			),
			name: PropTypes.string,
			value: PropTypes.string,
		})
	),
	pageTypeSelectedOption: PropTypes.string,
	pageTypeSelectedOptionLabel: PropTypes.string,
	showAddIcon: PropTypes.bool,
};

export default PageTypeSelector;
