/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import {useUnifiedSidebarAnnouncement} from './useUnifiedSidebarAnnouncement';
import {useUnifiedSidebarFocusReturn} from './useUnifiedSidebarFocusReturn';
import {useUnifiedSidebarShortcut} from './useUnifiedSidebarShortcut';

interface Props {
	navigationId: string;
	stateChangedEventName: string;
	stateRequestedEventName: string;
	visible: boolean;
}

function UnifiedSidebarToggler({
	navigationId,
	stateChangedEventName,
	stateRequestedEventName,
	visible: initialVisible,
}: Props) {
	const [visible, setVisible] = useState(initialVisible);

	async function toggle() {
		Liferay.fire(stateRequestedEventName, {visible: !visible});
	}

	const buttonRef = useRef<HTMLButtonElement>(null);

	const trackFocus = useUnifiedSidebarFocusReturn({
		buttonRef,
		navigationId,
		visible,
	});

	useEffect(() => {
		function handleStateChanged({visible}: {visible: boolean}) {
			trackFocus(visible);

			setVisible(visible);
		}

		Liferay.on(stateChangedEventName, handleStateChanged);

		// The navigation can change its state on load, for example when it
		// starts closed on a narrow screen or a page editor closes it, before
		// this toggler subscribes, so read its current state as well.

		const navigation = document.getElementById(navigationId);

		if (navigation) {
			setVisible(navigation.classList.contains('c-slideout-show'));
		}

		return () => Liferay.detach(stateChangedEventName, handleStateChanged);
	}, [navigationId, stateChangedEventName, trackFocus]);

	const {announceNextChange, announcement} =
		useUnifiedSidebarAnnouncement(visible);

	useUnifiedSidebarShortcut(navigationId, () => {
		announceNextChange();

		toggle();
	});

	const title = useMemo(
		() =>
			visible
				? Liferay.Language.get('close-product-menu')
				: Liferay.Language.get('open-product-menu'),
		[visible]
	);

	return (
		<>
			<ClayButtonWithIcon
				aria-controls={navigationId}
				aria-expanded={visible}
				aria-keyshortcuts="Control+Alt+M"
				aria-label={title}
				className="control-menu-nav-link"
				data-qa-id="sideNavigationToggler"
				displayType="unstyled"
				monospaced
				onClick={toggle}
				ref={buttonRef}
				role="tab"
				size="sm"
				symbol={visible ? 'product-menu-open' : 'product-menu-closed'}
				title={title}
			/>

			<span aria-live="polite" className="sr-only">
				{announcement}
			</span>
		</>
	);
}

export default UnifiedSidebarToggler;
