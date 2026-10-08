/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useEffect, useState} from 'react';

import isClickOutside from '../../hooks/useOnClickOutside';

const SidebarContext = createContext({});

const SidebarContextProvider = ({
	children,
	dataEngineModule,
	formReportRecordsFieldValuesURL,
	portletNamespace,
}) => {
	const [sidebarState, setSidebarState] = useState({
		field: null,
		isOpen: false,
		totalEntries: 0,
		type: null,
	});

	useEffect(() => {
		const keysPressed = {};
		const eventHandlerClickOutside = ({target}) => {
			if (
				sidebarState.isOpen &&
				isClickOutside(
					target,
					'#' + portletNamespace + 'sidebar-reports',
					'#' +
						portletNamespace +
						sidebarState.field.name +
						'-see-more'
				)
			) {
				setSidebarState(() => ({
					isOpen: false,
				}));
			}
		};

		const focusOnLiElement = (index) => {
			const firstLi = document.querySelector(
				`#${portletNamespace}_entry_slidebar_${index}`
			);
			if (firstLi) {
				firstLi.focus();
			}
		};

		const eventHandlerKeyUp = ({keyCode, target}) => {
			const isTargetOutside = isClickOutside(
				target,
				'#' + portletNamespace + 'sidebar-reports'
			);
			const index = Number(target.getAttribute('data-index'));
			const indexLastElement = sidebarState.totalEntries - 1;
			const closeButtonId = `${portletNamespace}close-sidebar`;
			const isCloseButton = target.id === closeButtonId;
			const isTabPressed = keyCode === 9 || keysPressed[9];
			const isShiftPressed = keyCode === 16 || keysPressed[16];
			const isForwardNavigation = isTabPressed && !isShiftPressed;
			const isBackwardNavigation = isTabPressed && isShiftPressed;
			const isEscapePressed = keyCode === 27 || keysPressed[27];

			if (sidebarState.isOpen) {
				if (isTargetOutside && isTabPressed) {
					focusOnLiElement(0);
				}
				else if (isEscapePressed) {
					setSidebarState(() => ({
						isOpen: false,
					}));
				}
				else if (!isTargetOutside) {
					if (isForwardNavigation && index === indexLastElement) {
						const closeButton = document.querySelector(
							`#${closeButtonId}`
						);
						if (closeButton) {
							closeButton.focus();
						}
					}
					if (isBackwardNavigation && isCloseButton) {
						focusOnLiElement(indexLastElement);
					}
				}
			}

			delete keysPressed[keyCode];
		};

		const eventHandlerKeyDown = ({keyCode}) => {
			keysPressed[keyCode] = true;
		};

		window.addEventListener('click', eventHandlerClickOutside);
		window.addEventListener('keydown', eventHandlerKeyDown);
		window.addEventListener('keyup', eventHandlerKeyUp);

		return () => {
			window.removeEventListener('click', eventHandlerClickOutside);
			window.removeEventListener('keydown', eventHandlerKeyDown);
			window.removeEventListener('keyup', eventHandlerKeyUp);
		};
	});

	const toggleSidebar = (field, summary, totalEntries, type) => {
		const isOpen = field !== undefined;

		if (isOpen) {
			setSidebarState(() => ({
				field,
				isOpen,
				summary,
				totalEntries,
				type,
			}));
		}
		else {
			setSidebarState(() => ({
				...sidebarState,
				isOpen,
			}));
		}
	};

	return (
		<SidebarContext.Provider
			value={{
				...sidebarState,
				dataEngineModule,
				formReportRecordsFieldValuesURL,
				portletNamespace,
				toggleSidebar,
			}}
		>
			{children}
		</SidebarContext.Provider>
	);
};

export {SidebarContext, SidebarContextProvider};
