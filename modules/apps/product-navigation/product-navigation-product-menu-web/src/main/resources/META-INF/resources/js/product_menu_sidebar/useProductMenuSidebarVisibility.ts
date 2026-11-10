/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMediaQuery} from '@liferay/layout-js-components-web';
import {useUnifiedSidebarVisibility} from 'frontend-js-components-web';
import {useCallback, useEffect, useRef, useState} from 'react';

const OVERLAY_MEDIA_QUERY = '(max-width: 991.98px)';
const TRANSITION_TIMEOUT = 300;

export function useProductMenuSidebarVisibility(
	initialVisible: boolean,
	visibleSessionKey: string
) {
	const [closedAsOverlay] = useState(
		() => initialVisible && window.matchMedia(OVERLAY_MEDIA_QUERY).matches
	);

	const initiallyVisible = initialVisible && !closedAsOverlay;

	const overlay = useMediaQuery(OVERLAY_MEDIA_QUERY);

	const transitionControllerRef = useRef<AbortController | null>(null);

	const updateBody = useCallback((visible: boolean) => {
		transitionControllerRef.current?.abort();

		const transitionController = new AbortController();

		transitionControllerRef.current = transitionController;

		const {body} = document;

		const endTransition = () => {
			body.classList.remove('sidenav-transition');

			transitionController.abort();
		};

		body.addEventListener(
			'transitionend',
			(event) => {
				if (
					event.propertyName === 'padding-left' &&
					(event.target as HTMLElement).id === 'wrapper'
				) {
					endTransition();
				}
			},
			{signal: transitionController.signal}
		);

		const timeoutId = setTimeout(endTransition, TRANSITION_TIMEOUT);

		transitionController.signal.addEventListener('abort', () =>
			clearTimeout(timeoutId)
		);

		body.classList.add('sidenav-transition');
		body.classList.toggle('open', visible);
		body.classList.toggle('product-menu-open', visible);
	}, []);

	const {updateVisible, visible} = useUnifiedSidebarVisibility({
		hiddenSessionValue: 'closed',
		initialVisible: initiallyVisible,
		onVisibleChange: updateBody,
		sessionKey: visibleSessionKey,
		stateChangedEventName: 'productMenuSidebarStateChanged',
		stateRequestedEventName: 'productMenuSidebarStateRequested',
		visibleSessionValue: 'open',
	});

	useEffect(function cleanupTransition() {
		return () => {
			transitionControllerRef.current?.abort();

			document.body.classList.remove('sidenav-transition');
		};
	}, []);

	useEffect(
		function closeOverlayOnLoad() {
			if (!closedAsOverlay) {
				return;
			}

			document.body.classList.remove('open', 'product-menu-open');

			Liferay.fire('productMenuSidebarStateChanged', {visible: false});
		},
		[closedAsOverlay]
	);

	useEffect(
		function announceReady() {

			// Requests fired before the sidebar subscribed were lost, so let
			// their senders know they can fire them again.

			Liferay.fire('productMenuSidebarReady', {
				visible: initiallyVisible,
			});
		},
		[initiallyVisible]
	);

	return {overlay, updateVisible, visible};
}
