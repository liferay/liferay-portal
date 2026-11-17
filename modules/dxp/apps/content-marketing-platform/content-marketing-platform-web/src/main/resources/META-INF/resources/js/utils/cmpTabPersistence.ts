/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_EVENT, getConfigParamName} from '@liferay/frontend-data-set-web';
import {COOKIE_TYPES, sessionStorage} from 'frontend-js-web';

export const STORAGE_KEY = 'cmpGlobalTasksActiveTab';

let installed = false;

const tabIndexToFDSId = new Map<number, string>();

function findTabsContainer(): HTMLElement | null {
	const tabsContainers = Array.from(
		document.querySelectorAll<HTMLElement>('.component-tabs')
	);

	const container = tabsContainers.find((tabs) => {
		const panels = getTabPanelItems(tabs);

		return (
			panels.length === 3 &&
			panels.every((panel) => panel.querySelector('.cms-section'))
		);
	});

	return container || null;
}

function getTabNavItems(container: HTMLElement): HTMLElement[] {
	return Array.from(
		container.querySelectorAll<HTMLElement>('.nav-link')
	).filter((navLink) => !navLink.closest('.tab-panel-item'));
}

function getTabPanelItems(container: HTMLElement): HTMLElement[] {
	return Array.from(
		container.querySelectorAll<HTMLElement>('.tab-panel-item')
	).filter((panel) => !panel.parentElement?.closest('.tab-panel-item'));
}

function getTabIndexToRestore() {
	const searchParams = new URLSearchParams(window.location.search);

	for (const [tabIndex, fdsId] of tabIndexToFDSId) {
		if (searchParams.has(getConfigParamName(fdsId))) {
			return tabIndex;
		}
	}

	const stored = sessionStorage.getItem(STORAGE_KEY, COOKIE_TYPES.NECESSARY);

	if (stored === null) {
		return null;
	}

	return Number(stored);
}

function restoreActiveTab() {
	const index = getTabIndexToRestore();

	if (index === null) {
		return;
	}

	if (!Number.isInteger(index) || index <= 0) {
		return;
	}

	const container = findTabsContainer();

	if (!container) {
		return;
	}

	const tabs = getTabNavItems(container);

	if (tabs[index] && !tabs[index].classList.contains('active')) {
		tabs[index].click();
	}
}

function handleActivePanel(event: {panel: HTMLElement}) {
	const container = findTabsContainer();

	if (!container || !container.contains(event.panel)) {
		return;
	}

	const index = getTabPanelItems(container).indexOf(event.panel);

	if (index < 0) {
		return;
	}

	sessionStorage.setItem(STORAGE_KEY, String(index), COOKIE_TYPES.NECESSARY);

	const fdsId = tabIndexToFDSId.get(index);

	if (fdsId) {
		Liferay.fire(FDS_EVENT.UPDATE_DISPLAY, {id: fdsId});
	}
}

export function registerTabFDS(fdsId: string, tabIndex: number) {
	tabIndexToFDSId.set(tabIndex, fdsId);
}

export function installCMPTabPersistence() {
	if (!installed) {
		installed = true;

		Liferay.on('tabsFragment:activePanel', handleActivePanel);
	}

	restoreActiveTab();
}
