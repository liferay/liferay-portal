/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

jest.mock('@liferay/frontend-data-set-web', () => ({
	FDS_EVENT: {
		UPDATE_DISPLAY: 'fds-update-display',
	},
	getConfigParamName: (id: string) => `${id}_fdsConfig`,
}));

const mockFire = jest.fn();
const mockOn = jest.fn();
const tabClickSpies: jest.Mock[] = [];

function buildTabsContainer(activeIndex = 0) {
	const container = document.createElement('div');

	container.className = 'component-tabs';

	tabClickSpies.length = 0;

	['All Tasks', 'Project Tasks', 'Workflow'].forEach((label, index) => {
		const tab = document.createElement('a');

		tab.className = 'nav-link';
		tab.textContent = label;

		if (index === activeIndex) {
			tab.classList.add('active');
		}

		const clickSpy = jest.fn();

		tab.addEventListener('click', clickSpy);
		tabClickSpies.push(clickSpy);

		container.appendChild(tab);
	});

	['allTasksPanel', 'projectTasksPanel', 'workflowTasksPanel'].forEach(
		(id) => {
			const panel = document.createElement('div');

			panel.className = 'tab-panel-item';
			panel.id = id;

			const section = document.createElement('div');

			section.className = 'cms-section';

			panel.appendChild(section);

			container.appendChild(panel);
		}
	);

	document.body.appendChild(container);

	return container;
}

function loadModule() {
	let exports: any;

	jest.isolateModules(() => {
		exports = require('../../js/utils/cmpTabPersistence');
	});

	return exports;
}

beforeEach(() => {
	mockFire.mockReset();
	mockOn.mockReset();
	document.body.innerHTML = '';
	window.history.replaceState(null, '', '/');
	window.sessionStorage.clear();

	(globalThis as any).Liferay = {
		...(globalThis as any).Liferay,
		fire: mockFire,
		on: mockOn,
	};
});

describe('cmpTabPersistence', () => {
	describe('refresh FDS on tab switch', () => {
		it('ignores activePanel events for panels outside the CMP tabs container', () => {
			buildTabsContainer(0);

			const {STORAGE_KEY, installCMPTabPersistence, registerTabFDS} =
				loadModule();

			registerTabFDS('allTasksFDS', 0);

			installCMPTabPersistence();

			const handleActivePanel = mockOn.mock.calls[0][1];
			const otherPanel = document.createElement('div');

			document.body.appendChild(otherPanel);

			handleActivePanel({panel: otherPanel});

			expect(mockFire).not.toHaveBeenCalled();
			expect(window.sessionStorage.getItem(STORAGE_KEY)).toBeNull();
		});

		it('skips FDS refresh when the activated tab has no registered FDS id', () => {
			const container = buildTabsContainer(0);

			const {STORAGE_KEY, installCMPTabPersistence} = loadModule();

			installCMPTabPersistence();

			const handleActivePanel = mockOn.mock.calls[0][1];
			const projectTasksPanel = container.querySelectorAll(
				'.tab-panel-item'
			)[1] as HTMLElement;

			handleActivePanel({panel: projectTasksPanel});

			expect(window.sessionStorage.getItem(STORAGE_KEY)).toBe('1');

			expect(mockFire).not.toHaveBeenCalled();
		});

		it('writes the new tab index to sessionStorage and fires FDS refresh', () => {
			const container = buildTabsContainer(0);

			const {STORAGE_KEY, installCMPTabPersistence, registerTabFDS} =
				loadModule();

			registerTabFDS('allTasksFDS', 0);
			registerTabFDS('projectTasksFDS', 1);
			registerTabFDS('workflowTasksFDS', 2);

			installCMPTabPersistence();

			const handleActivePanel = mockOn.mock.calls[0][1];
			const projectTasksPanel = container.querySelectorAll(
				'.tab-panel-item'
			)[1] as HTMLElement;

			handleActivePanel({panel: projectTasksPanel});

			expect(window.sessionStorage.getItem(STORAGE_KEY)).toBe('1');

			expect(mockFire).toHaveBeenCalledWith('fds-update-display', {
				id: 'projectTasksFDS',
			});
		});
	});

	describe('restore tab on page load', () => {
		it('clicks the persisted tab when sessionStorage has a non-zero index', () => {
			buildTabsContainer(0);

			const {STORAGE_KEY, installCMPTabPersistence} = loadModule();

			window.sessionStorage.setItem(STORAGE_KEY, '1');

			installCMPTabPersistence();

			expect(tabClickSpies[1]).toHaveBeenCalledTimes(1);
			expect(tabClickSpies[0]).not.toHaveBeenCalled();
			expect(tabClickSpies[2]).not.toHaveBeenCalled();
		});

		it('clicks the tab whose FDS receives a configuration in the URL', () => {
			buildTabsContainer(0);

			const {STORAGE_KEY, installCMPTabPersistence, registerTabFDS} =
				loadModule();

			window.history.replaceState(
				null,
				'',
				'/tasks?workflowTasksFDS_fdsConfig=%7B%7D'
			);
			window.sessionStorage.setItem(STORAGE_KEY, '1');

			registerTabFDS('allTasksFDS', 0);
			registerTabFDS('workflowTasksFDS', 2);

			installCMPTabPersistence();

			expect(tabClickSpies[2]).toHaveBeenCalledTimes(1);
			expect(tabClickSpies[0]).not.toHaveBeenCalled();
			expect(tabClickSpies[1]).not.toHaveBeenCalled();
		});

		it('registers the activePanel listener only once', () => {
			buildTabsContainer(0);

			const {installCMPTabPersistence} = loadModule();

			installCMPTabPersistence();
			installCMPTabPersistence();
			installCMPTabPersistence();

			expect(mockOn).toHaveBeenCalledTimes(1);
			expect(mockOn).toHaveBeenCalledWith(
				'tabsFragment:activePanel',
				expect.any(Function)
			);
		});

		it('restores the tab on each install so SPA re-entry re-applies it', () => {
			const {STORAGE_KEY, installCMPTabPersistence} = loadModule();

			window.sessionStorage.setItem(STORAGE_KEY, '1');

			buildTabsContainer(0);

			installCMPTabPersistence();

			expect(tabClickSpies[1]).toHaveBeenCalledTimes(1);

			document.body.innerHTML = '';

			buildTabsContainer(0);

			installCMPTabPersistence();

			expect(tabClickSpies[1]).toHaveBeenCalledTimes(1);
			expect(mockOn).toHaveBeenCalledTimes(1);
		});

		it('skips restore when sessionStorage is empty', () => {
			buildTabsContainer(0);

			const {installCMPTabPersistence} = loadModule();

			installCMPTabPersistence();

			tabClickSpies.forEach((spy) => expect(spy).not.toHaveBeenCalled());
		});

		it('skips restore when the persisted tab is already active', () => {
			buildTabsContainer(2);

			const {STORAGE_KEY, installCMPTabPersistence} = loadModule();

			window.sessionStorage.setItem(STORAGE_KEY, '2');

			installCMPTabPersistence();

			tabClickSpies.forEach((spy) => expect(spy).not.toHaveBeenCalled());
		});
	});
});
