/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useContext, useMemo, useState} from 'react';

import {AppContext} from './AppContext';
import {ButtonList} from './ButtonList';
import {CollapsableButtonList} from './CollapsableButtonList';

const SEARCH_INPUT_ID = 'ddm_template_editor_Sidebar-SearchInputId';

export function ElementsSidebarPanel({className}) {
	const {
		inputChannel,
		templateVariableGroups: initialTemplateVariableGroups,
	} = useContext(AppContext);

	const templateVariableGroups = useMemo(
		() =>
			initialTemplateVariableGroups.map((group) => ({
				...group,
				items: group.items.map((item) =>
					item.repeatable ? {...item, label: `${item.label}*`} : item
				),
			})),
		[initialTemplateVariableGroups]
	);

	const onButtonClick = (item) => inputChannel.sendData(item.content);

	const [searchValue, setSearchValue] = useState('');

	const filteredItems = useMemo(() => {
		const slugify = (str) => str.toLowerCase().replace(/\s/g, '');
		const query = slugify(searchValue);

		if (query) {
			return templateVariableGroups
				.map(({items}) =>
					items.filter((item) => slugify(item.label).includes(query))
				)
				.reduce((a, b) => a.concat(b), []);
		}
		else {
			return null;
		}
	}, [searchValue, templateVariableGroups]);

	return (
		<div className={classNames(className, 'px-3 panel-group-sm')}>
			<h1 className="ddm_template_editor__App-sidebar-title my-3">
				{Liferay.Language.get('elements')}
			</h1>

			<ClayForm.Group className="mb-4" small>
				<label className="sr-only" htmlFor={SEARCH_INPUT_ID}>
					{Liferay.Language.get('search')}
				</label>

				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							id={SEARCH_INPUT_ID}
							insetAfter
							onChange={(event) =>
								setSearchValue(event.target.value)
							}
							placeholder={`${Liferay.Language.get('search')}...`}
							value={searchValue}
						/>

						<ClayInput.GroupInsetItem after tag="span">
							{searchValue ? (
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get('clear')}
									borderless
									displayType="secondary"
									monospaced={false}
									onClick={() => setSearchValue('')}
									symbol={searchValue ? 'times' : 'search'}
									title={Liferay.Language.get('clear')}
								/>
							) : (
								<ClayIcon
									className="search-icon"
									symbol="search"
								/>
							)}
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>

			{filteredItems ? (
				filteredItems.length !== 0 ? (
					<ButtonList
						items={filteredItems}
						onButtonClick={onButtonClick}
					/>
				) : (
					<ClayEmptyState
						description={Liferay.Language.get(
							'try-again-with-a-different-search'
						)}
						image={
							<ClayIcon
								className="c-empty-state-icon c-empty-state-icon-search-state"
								illustration
								symbol="search-state"
							/>
						}
						small
						title={Liferay.Language.get('no-results-found')}
					/>
				)
			) : (
				templateVariableGroups.map(({items, label}) => (
					<CollapsableButtonList
						items={items}
						key={label}
						label={label}
						onButtonClick={onButtonClick}
					/>
				))
			)}
		</div>
	);
}

ElementsSidebarPanel.propTypes = {
	className: PropTypes.string,
};
