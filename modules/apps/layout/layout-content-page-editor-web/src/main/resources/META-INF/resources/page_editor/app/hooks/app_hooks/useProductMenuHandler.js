/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {hideProductMenuIfPresent} from '@liferay/layout-js-components-web';
import {useEffect, useRef} from 'react';

import {switchSidebarPanel} from '../../actions/index';
import {useDispatch, useSelector} from '../../contexts/StoreContext';
import selectSidebarIsOpened from '../../selectors/selectSidebarIsOpened';

export default function useProductMenuHandler() {
	const dispatch = useDispatch();
	const lastSidebarStateRef = useRef(true);

	const sidebarOpen = useSelector(selectSidebarIsOpened);
	const sidebarHidden = useSelector((state) => state.sidebar.hidden);

	useEffect(() => {
		hideProductMenuIfPresent({
			onHide: () => {
				dispatch(switchSidebarPanel({sidebarOpen: true}));
			},
		});
	}, [dispatch]);

	useEffect(() => {
		const sideNavigation = Liferay.SideNavigation?.instance(
			document.querySelector('.product-menu-toggle')
		);

		const onProductMenuChange = ({open}) => {
			if (sidebarHidden) {
				return;
			}

			if (open) {
				lastSidebarStateRef.current = sidebarOpen;

				dispatch(
					switchSidebarPanel({
						itemConfigurationOpen: false,
						sidebarOpen: false,
					})
				);
			}
			else {
				dispatch(
					switchSidebarPanel({
						itemConfigurationOpen: true,
						sidebarOpen: lastSidebarStateRef.current,
					})
				);
			}
		};

		const closeSideNavigationListener = sideNavigation?.on(
			'closed.lexicon.sidenav',
			() => onProductMenuChange({open: false})
		);
		const openSideNavigationListener = sideNavigation?.on(
			'openStart.lexicon.sidenav',
			() => onProductMenuChange({open: true})
		);

		return () => {
			closeSideNavigationListener?.removeListener();
			openSideNavigationListener?.removeListener();
		};
	}, [dispatch, sidebarOpen, sidebarHidden]);
}
