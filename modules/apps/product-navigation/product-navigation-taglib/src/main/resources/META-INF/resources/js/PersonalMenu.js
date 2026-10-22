/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClaySticker from '@clayui/sticker';
import {SideNavigationColorSchemeButton} from '@liferay/application-list-taglib';
import {fetch, loadModule, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useRef, useState} from 'react';

function mapItemsOnClick(items) {
	return items.map((item) => {
		const {
			items: nestedItems,
			jsOnClickConfig,
			onClickESModule,
			...otherKeys
		} = item;

		const newVal = {...otherKeys};

		if (nestedItems) {
			newVal.items = mapItemsOnClick(nestedItems);
		}

		if (onClickESModule) {
			newVal.onClick = async () => {
				const onClickFn = await loadModule(onClickESModule);

				onClickFn(jsOnClickConfig);
			};
		}

		return newVal;
	});
}

const defaultItems = [
	{
		label: (
			<div
				aria-label={Liferay.Language.get('loading')}
				className="d-flex justify-content-center"
				role="progressbar"
			>
				<ClayLoadingIndicator />
			</div>
		),
	},
];

function PersonalMenu({
	color,
	isImpersonated,
	itemsURL,
	label,
	showColorSchemeButton,
	size,
	userName,
	userPortraitURL,
}) {
	const [items, setItems] = useState(defaultItems);
	const keyboardInteractionRef = useRef(false);
	const pointerOpenRef = useRef(false);
	const preloadPromiseRef = useRef();
	const triggerRef = useRef(null);

	function preloadItems() {
		if (!preloadPromiseRef.current) {
			preloadPromiseRef.current = fetch(itemsURL)
				.then((response) => response.json())
				.then((responseItems) =>
					setItems(mapItemsOnClick(responseItems))
				);
		}
	}

	// Clay moves the focus into the menu when it opens. Keep that for
	// keyboard users, also when the items arrive after the menu is open, and
	// give the focus back to the trigger when the menu was opened with a
	// pointer, so the header button does not get focused and show its tooltip.

	useEffect(() => {
		if (preloadPromiseRef.current && keyboardInteractionRef.current) {
			const firstElement = document.querySelector(
				'.dropdown-menu-personal-menu button, .dropdown-menu-personal-menu [role=menuitem]'
			);

			firstElement?.focus();
		}
	}, [items]);

	const onActiveChange = (active) => {
		pointerOpenRef.current = active && !keyboardInteractionRef.current;
	};

	const onHeaderFocus = () => {
		if (pointerOpenRef.current) {
			triggerRef.current?.focus();

			pointerOpenRef.current = false;
		}
	};

	// ClayDropDownWithItems only forwards function refs to the trigger.

	const triggerProps = {
		onFocus: preloadItems,
		onKeyDown: () => {
			keyboardInteractionRef.current = true;
		},
		onMouseOver: preloadItems,
		onPointerDown: () => {
			keyboardInteractionRef.current = false;
		},
		ref: (node) => {
			triggerRef.current = node;
		},
	};

	let headerContent;
	let menuItems = items;

	if (showColorSchemeButton && items[0]?.type === 'group') {
		const [{label: userLabel, ...firstGroup}, ...otherItems] = items;

		headerContent = (
			<div
				className="align-items-center d-flex dropdown-subheader justify-content-between p-0"
				onFocus={onHeaderFocus}
			>
				{userLabel}

				<SideNavigationColorSchemeButton className="c-ml-2 c-mr-n1" />
			</div>
		);

		menuItems = [firstGroup, ...otherItems];
	}

	return (
		<ClayDropDownWithItems
			headerContent={headerContent}
			items={menuItems}
			menuElementAttrs={{className: 'dropdown-menu-personal-menu'}}
			onActiveChange={onActiveChange}
			trigger={
				label ? (
					<div
						{...triggerProps}
						dangerouslySetInnerHTML={{__html: label}}
					/>
				) : (
					<ClayButton
						{...triggerProps}
						aria-label={sub(
							Liferay.Language.get('x-user-profile'),
							userName
						)}
						className="rounded-circle"
						data-qa-id="userPersonalMenu"
						displayType="unstyled"
						title={Liferay.Language.get('user-profile-menu')}
					>
						<span
							className={`sticker sticker-user-icon sticker-${size}`}
						>
							<ClaySticker
								className={`user-icon-color-${color}`}
								shape="circle"
								size={size}
							>
								{userPortraitURL ? (
									<img
										alt=""
										className="sticker-img"
										src={userPortraitURL}
									/>
								) : (
									<ClayIcon symbol="user" />
								)}
							</ClaySticker>

							{isImpersonated && (
								<ClaySticker
									className="sticker-user-icon"
									id="impersonate-user-sticker"
									outside
									position="bottom-right"
									shape="circle"
									size={size ? 'sm' : ''}
								>
									<span id="impersonate-user-icon">
										<ClayIcon symbol="user" />
									</span>
								</ClaySticker>
							)}
						</span>
					</ClayButton>
				)
			}
		/>
	);
}

PersonalMenu.propTypes = {
	itemsURL: PropTypes.string,
	showColorSchemeButton: PropTypes.bool,
};

export default PersonalMenu;
