/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ClayVerticalNav} from '@clayui/nav';
import classNames from 'classnames';
import React, {useCallback, useEffect, useRef, useState} from 'react';

type VerticalNavItem = React.ComponentProps<
	typeof ClayVerticalNav
>['items'][0] & {
	textValue: string;
};

interface ApplicationsMenuItem {
	href?: string;
	id: string;
	items?: Array<ApplicationsMenuItem>;
	label: string;
	leadingIcon?: string;
}

interface Props {
	items: Array<ApplicationsMenuItem>;
	label: string;
	portletId: string;
	visibility: {
		hidden: string;
		initialState: boolean;
		stateKey: string;
		visible: string;
	};
}

function ApplicationsMenu({items, label, portletId, visibility}: Props) {
	const [visible, setVisible] = useState(visibility.initialState);
	const visibilityRef = useRef(visibility);

	function buildNavigationItem(item: ApplicationsMenuItem): VerticalNavItem {
		return {
			...item,
			...(item.items && {
				initialExpanded: itemContainsPortletId(item),
				items: item.items.map((child) => buildNavigationItem(child)),
			}),
			...(item.leadingIcon && {
				label: (
					<div className="align-items-center d-flex flex-row">
						<ClayIcon symbol={item.leadingIcon} />

						<span className="c-px-2">{item.label}</span>
					</div>
				),
			}),
			textValue: item.label,
		};
	}

	function itemContainsPortletId(item: ApplicationsMenuItem): boolean {
		if (!item.items) {
			return item.id === portletId;
		}

		return item.items.some((child) => itemContainsPortletId(child));
	}

	const updateVisibleState = useCallback(async (visible: boolean) => {
		await Liferay.Util.Session.set(
			visibilityRef.current.stateKey,
			visible
				? visibilityRef.current.visible
				: visibilityRef.current.hidden
		);

		setVisible(visible);

		Liferay.fire('applicationsMenuStateChanged', {visible});
	}, []);

	useEffect(() => {
		async function handleStateRequest({visible}: {visible: boolean}) {
			await updateVisibleState(visible);
		}

		Liferay.on('applicationsMenuStateRequested', handleStateRequest);

		return () =>
			Liferay.detach(
				'applicationsMenuStateRequested',
				handleStateRequest
			);
	}, [updateVisibleState]);

	return (
		<div
			className={classNames('applications-menu-sidebar-wrapper', {
				visible,
			})}
		>
			<div className="applications-menu-sidebar">
				<div className="align-items-center c-px-3 c-py-4 d-flex flex-row">
					<div>

						{/* TODO: replace icon below with actual panel icon */}

						<ClayIcon symbol="grid" />
					</div>

					<div className="c-px-2 flex-grow-1 text-4 text-weight-bold">
						{label}
					</div>

					<button
						className="close lfr-portal-tooltip rounded-lg"
						onClick={() => updateVisibleState(false)}
						title={Liferay.Language.get('close-product-menu')}
						type="button"
					>
						<ClayIcon symbol="times" />
					</button>
				</div>

				<div className="applications-menu-sidebar-body">
					<ClayVerticalNav
						active={portletId}
						itemAriaCurrent={true}
						items={items.map(buildNavigationItem)}
					/>
				</div>
			</div>
		</div>
	);
}

export default ApplicationsMenu;
