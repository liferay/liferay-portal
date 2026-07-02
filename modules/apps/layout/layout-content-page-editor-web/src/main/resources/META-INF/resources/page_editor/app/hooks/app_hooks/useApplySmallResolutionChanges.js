/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMediaQuery} from '@liferay/layout-js-components-web';
import {debounce} from 'frontend-js-web';
import {useEffect} from 'react';

import {useDispatch, useSelectorRef} from '../../contexts/StoreContext';
import switchSidebarPanel from '../../thunks/switchSidebarPanel';

const ELEMENTS_SELECTORS = [
	'.page-editor__sidebar__content',
	'.page-editor__item-configuration-sidebar',
	'.page-editor__wrapper',
];

export default function useApplySmallResolutionChanges() {
	const dispatch = useDispatch();

	const sidebarRef = useSelectorRef((state) => state.sidebar);

	const isSmallResolution = useMediaQuery('(max-width: 768px)');

	useEffect(() => {
		document.body.classList.toggle(
			'page-editor__small-resolution',
			isSmallResolution
		);

		if (sidebarRef.current?.hidden) {
			return;
		}

		dispatch(
			switchSidebarPanel({
				itemConfigurationOpen: !isSmallResolution,
				sidebarOpen: !isSmallResolution,
			})
		);
	}, [dispatch, isSmallResolution, sidebarRef]);

	useEffect(() => {
		if (!isSmallResolution) {
			return;
		}

		const elements = ELEMENTS_SELECTORS.map((selector) =>
			document.querySelector(selector)
		).filter(Boolean);

		const onResize = debounce(() => {
			document.body.style.setProperty('--editor-height', '0px');

			requestAnimationFrame(() => {
				let maxHeight = 0;

				for (const element of elements) {
					const rect = element.getBoundingClientRect();

					if (rect.height > maxHeight) {
						maxHeight = rect.height;
					}
				}

				document.body.style.setProperty(
					'--editor-height',
					maxHeight + 'px'
				);
			});
		}, 500);

		const resizeObserver = new ResizeObserver(onResize);

		elements.forEach((element) => {
			resizeObserver.observe(element);
		});

		return () => {
			resizeObserver.disconnect();
		};
	}, [dispatch, isSmallResolution]);
}
