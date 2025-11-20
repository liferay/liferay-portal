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
import InfoPanelToggleButton from './InfoPanelToggleButton';
import MainSearch from './MainSearch';
import SnapshotsControls from './SnapshotsControls';
import SortDropdown from './SortDropdown';
import FiltersDropdown from './filters/FiltersDropdown';

function NavBar({creationMenu, showSearch}) {
	const {showInfoPanel} = useContext(FrontendDataSetContext);

	const [{filters, snapshotsEnabled, sorts, views}] =
		useContext(ViewsContext);

	const [showMobile, setShowMobile] = useState(false);

	return (
		<ManagementToolbar.ItemList
			className="container-fluid ml-2 navbar navbar-expand-md"
			data-qa-id="managementToolbar"
		>
			<ManagementToolbar.ItemList>
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

				{snapshotsEnabled && <SnapshotsControls />}

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
						<InfoPanelToggleButton symbol="info-circle-open" />
					</ManagementToolbar.Item>
				)}
			</ManagementToolbar.ItemList>
		</ManagementToolbar.ItemList>
	);
}

NavBar.propTypes = {
	creationMenu: PropTypes.shape({
		primaryItems: PropTypes.array,
		secondaryItems: PropTypes.array,
	}),
	showSearch: PropTypes.bool,
};

NavBar.defaultProps = {
	creationMenu: {
		primaryItems: [],
	},
	showSearch: true,
};

export default NavBar;
