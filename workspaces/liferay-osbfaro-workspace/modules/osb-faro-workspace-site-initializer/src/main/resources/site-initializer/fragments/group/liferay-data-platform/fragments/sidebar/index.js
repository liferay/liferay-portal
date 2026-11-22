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

const mobileMediaQueryList = window.matchMedia('(max-width: 991.98px)');

let mobileSidebarOpen = false;
let sidebarOpen = sidebarContainer.classList.contains('open');

const content = sidebarContainer.querySelector('.content');

content.addEventListener('click', () => {
	if (mobileMediaQueryList.matches && mobileSidebarOpen) {
		handleSidebarToggleClick();
	}
});

mobileMediaQueryList.addEventListener('change', () => updateSidebar());

updateSidebar();

function addSidebarToggleClickEventListener(node) {
	node.addEventListener('click', () => handleSidebarToggleClick());

	node.removeAttribute('disabled');
}

function handleSidebarToggleClick() {
	if (mobileMediaQueryList.matches) {
		mobileSidebarOpen = !mobileSidebarOpen;

		sidebarContainer.classList.toggle('open-mobile', mobileSidebarOpen);
	}
	else {
		sidebarOpen = !sidebarOpen;

		sidebarContainer.classList.toggle('open', sidebarOpen);

		Liferay.Util.Session.set(
			'com.liferay.osb.faro.workspace.site.initializer_sidebarOpen',
			String(sidebarOpen)
		).catch(() => {});
	}

	updateSidebar();

	if (sidebarTrigger) {
		sidebarTrigger.focus();
	}
}

function updateSidebar() {
	let open = sidebarOpen;

	if (mobileMediaQueryList.matches) {
		open = mobileSidebarOpen;
	}

	sidebar.toggleAttribute('inert', !open);

	if (!sidebarTrigger) {
		return;
	}

	const [openIcon, closedIcon] =
		sidebarTrigger.querySelectorAll('.inline-item');

	openIcon.classList.toggle('hide', !open);
	closedIcon.classList.toggle('hide', open);

	sidebarTrigger.setAttribute('aria-expanded', String(open));
	sidebarTrigger.setAttribute('aria-pressed', String(open));

	if (open) {
		sidebarTrigger.setAttribute('title', sidebarTrigger.dataset.titleclose);
	}
	else {
		sidebarTrigger.setAttribute('title', sidebarTrigger.dataset.titleopen);
	}
}
