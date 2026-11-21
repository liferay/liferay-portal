/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const sidebarId = `${fragmentEntryLinkNamespace}sidebar`;

const sidebar = fragmentElement.querySelector('.sidebar-container');
const sidebarCloseButton = fragmentElement.querySelector(
	'.sidebar-close-button'
);
const sidebarContainer = fragmentElement.querySelector('.sidebar-layout');
const sidebarTrigger = fragmentElement.querySelector('.sidebar-toggle');

if (sidebarTrigger) {
	sidebar.setAttribute('id', sidebarId);
	sidebarTrigger.setAttribute('aria-controls', sidebarId);

	addSidebarToggleClickEventListener(sidebarTrigger);
}

if (sidebarCloseButton) {
	addSidebarToggleClickEventListener(sidebarCloseButton);
}

let sidebarOpen = sidebarContainer.classList.contains('open');

function addSidebarToggleClickEventListener(node) {
	node.addEventListener('click', () => handleSidebarToggleClick());

	node.removeAttribute('disabled');
}

function handleSidebarToggleClick() {
	sidebarOpen = !sidebarOpen;

	toggleSidebar();

	Liferay.Util.Session.set(
		'com.liferay.osb.faro.workspace.site.initializer_sidebarOpen',
		String(sidebarOpen)
	).catch(() => {});
}

function toggleSidebar() {
	sidebarContainer.classList.toggle('open');

	sidebar.toggleAttribute('inert');

	if (sidebarTrigger) {
		sidebarTrigger
			.querySelectorAll('.inline-item')
			.forEach((node) => node.classList.toggle('hide'));

		sidebarTrigger.setAttribute('aria-expanded', String(sidebarOpen));
		sidebarTrigger.setAttribute('aria-pressed', String(sidebarOpen));

		if (sidebarOpen) {
			sidebarTrigger.setAttribute(
				'title',
				sidebarTrigger.dataset.titleclose
			);
		}
		else {
			sidebarTrigger.setAttribute(
				'title',
				sidebarTrigger.dataset.titleopen
			);
		}
	}

	if (sidebarTrigger) {
		sidebarTrigger.focus();
	}
}
