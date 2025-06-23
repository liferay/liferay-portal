/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {ManagementToolbar} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import ViewsContext from '../../views/ViewsContext';
import ActiveViewSelector from './ActiveViewSelector';
import CreationMenu from './CreationMenu';
import CustomViewsControls from './CustomViewsControls';
import InfoPanelToggleButton from './InfoPanelToggleButton';
import MainSearch from './MainSearch';
import SelectionCheckbox from './SelectionCheckbox';
import SortDropdown from './SortDropdown';
import FiltersDropdown from './filters/FiltersDropdown';

function NavBar({creationMenu, handleCheckboxClick, items, showSearch}) {
	const {selectable, selectionType, showInfoPanel} = useContext(
		FrontendDataSetContext
	);

	const [{customViewsEnabled, filters, sorts, views}] =
		useContext(ViewsContext);

	const [showMobile, setShowMobile] = useState(false);

	return (
		<ManagementToolbar.Container
			className="justify-content-space-between"
			data-qa-id="managementToolbar"
		>
			<ManagementToolbar.ItemList>
				{!!items.length &&
					selectable &&
					selectionType === 'multiple' && (
						<ManagementToolbar.Item>
							<SelectionCheckbox
								handleCheckboxClick={handleCheckboxClick}
								items={items}
								selectedItemsValue={[]}
							/>
						</ManagementToolbar.Item>
					)}

				{!!filters.length && (
					<ManagementToolbar.Item>
						<FiltersDropdown />
					</ManagementToolbar.Item>
				)}

				{!!sorts.length && sorts.some((sort) => !!sort.label) && (
					<ManagementToolbar.Item>
						<SortDropdown />
					</ManagementToolbar.Item>
				)}
			</ManagementToolbar.ItemList>

			{showSearch && (
				<>
					<ManagementToolbar.Search
						onSubmit={(event) => {
							event.preventDefault();
						}}
						showMobile={showMobile}
					>
						<MainSearch
							onClear={() => {
								setShowMobile(false);
							}}
						/>
					</ManagementToolbar.Search>
				</>
			)}

			<ManagementToolbar.ItemList>
				{showSearch && (
					<ManagementToolbar.Item className="navbar-breakpoint-d-none">
						<ClayButton
							aria-label={Liferay.Language.get('search')}
							className="nav-link nav-link-monospaced"
							displayType="unstyled"
							onClick={() => setShowMobile(true)}
						>
							<ClayIcon symbol="search" />
						</ClayButton>
					</ManagementToolbar.Item>
				)}

				{customViewsEnabled && <CustomViewsControls />}

				{views?.length > 1 && (
					<ManagementToolbar.Item>
						<ActiveViewSelector views={views} />
					</ManagementToolbar.Item>
				)}

				{creationMenu && (
					<ManagementToolbar.Item>
						<CreationMenu {...creationMenu} />
					</ManagementToolbar.Item>
				)}

				{showInfoPanel && (
					<ManagementToolbar.Item>
						<InfoPanelToggleButton symbol="info-panel-closed" />
					</ManagementToolbar.Item>
				)}
			</ManagementToolbar.ItemList>
		</ManagementToolbar.Container>
	);
}

NavBar.propTypes = {
	creationMenu: PropTypes.shape({
		primaryItems: PropTypes.array,
		secondaryItems: PropTypes.array,
	}),
	handleCheckboxClick: PropTypes.func.isRequired,
	items: PropTypes.array.isRequired,
	showSearch: PropTypes.bool,
};

NavBar.defaultProps = {
	creationMenu: {
		primaryItems: [],
	},
	showSearch: true,
};

export default NavBar;
