/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactPortal} from '@liferay/frontend-js-react-web';
import React, {useEffect, useState} from 'react';

import OmniSearch from './OmniSearch';

const CLAIMED_ATTRIBUTE = 'data-omni-search-claimed';

const ITEM_CLASS_NAME = 'omni-search-cms-toolbar-item';

export default function CmsToolbarOmniSearch({
	applicationsMenuPortletId,
	resultsURL,
}: {
	applicationsMenuPortletId: string;
	resultsURL: string;
}) {
	const [target, setTarget] = useState<HTMLElement | null>(null);

	useEffect(() => {
		let createdItem: HTMLElement | null = null;
		let currentTarget: HTMLElement | null = null;

		const claimTarget = (): HTMLElement | null => {
			const nav = document.querySelector('nav.cms-control-menu');

			if (!nav) {
				return null;
			}

			const existingItem = nav.querySelector<HTMLElement>(
				`.${ITEM_CLASS_NAME}`
			);

			if (existingItem) {
				if (existingItem.hasAttribute(CLAIMED_ATTRIBUTE)) {
					return null;
				}

				existingItem.setAttribute(CLAIMED_ATTRIBUTE, 'true');

				return existingItem;
			}

			const item = document.createElement('li');

			item.className = `tbar-item ${ITEM_CLASS_NAME}`;

			item.setAttribute(CLAIMED_ATTRIBUTE, 'true');

			const applicationsMenuItem = nav
				.querySelector(
					`.portlet-boundary_${applicationsMenuPortletId}_`
				)
				?.closest('.tbar-item');

			if (applicationsMenuItem?.parentNode) {
				applicationsMenuItem.parentNode.insertBefore(
					item,
					applicationsMenuItem
				);
			}
			else {
				const list = nav.querySelector('ul') ?? nav;

				list.insertBefore(item, list.lastElementChild);
			}

			createdItem = item;

			return item;
		};

		const ensureTarget = () => {
			if (currentTarget?.isConnected) {
				return;
			}

			if (currentTarget) {
				currentTarget.removeAttribute(CLAIMED_ATTRIBUTE);

				currentTarget = null;
			}

			currentTarget = claimTarget();

			setTarget(currentTarget);
		};

		ensureTarget();

		const mutationObserver = new MutationObserver(ensureTarget);

		mutationObserver.observe(document.body, {
			childList: true,
			subtree: true,
		});

		return () => {
			mutationObserver.disconnect();

			if (createdItem) {
				createdItem.remove();
			}
			else if (currentTarget) {
				currentTarget.removeAttribute(CLAIMED_ATTRIBUTE);
			}
		};
	}, [applicationsMenuPortletId]);

	if (!target) {
		return null;
	}

	return (
		<ReactPortal container={target}>
			<OmniSearch resultsURL={resultsURL} />
		</ReactPortal>
	);
}
