/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import {SideNavigationItem} from './types/SideNavigation';

const SCOPE_ITEM_SELECTOR = '.side-navigation-scope-item';
const SCROLLER_SELECTOR = '.sidebar-body';

export function useSideNavigationStuck(
	ref: React.RefObject<HTMLElement | null>,
	items: Array<SideNavigationItem>
) {
	const [stuck, setStuck] = useState(false);

	useEffect(() => {
		const scroller =
			ref.current?.querySelector<HTMLElement>(SCROLLER_SELECTOR);

		if (!scroller) {
			return;
		}

		const scopeItem =
			scroller.querySelector<HTMLElement>(SCOPE_ITEM_SELECTOR);

		const sentinel = document.createElement(scopeItem ? 'li' : 'div');

		sentinel.setAttribute('role', 'none');

		if (scopeItem) {
			scopeItem.before(sentinel);
		}
		else {
			scroller.prepend(sentinel);
		}

		const observer = new IntersectionObserver(
			([entry]) => setStuck(!entry.isIntersecting),
			{root: scroller}
		);

		observer.observe(sentinel);

		return () => {
			observer.disconnect();

			sentinel.remove();
		};
	}, [items, ref]);

	return stuck;
}
