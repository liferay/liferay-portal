/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useProvider} from '@clayui/provider';
import classNames from 'classnames';
import React from 'react';
import {createPortal} from 'react-dom';

export const ClayPortalContext =
	React.createContext<React.RefObject<Element | null> | null>(null);

ClayPortalContext.displayName = 'ClayPortalContext';

function createDivElement(className?: string, id?: string) {
	const element = document.createElement('div');
	if (className) {
		element.setAttribute('class', className);
	}
	if (id) {
		element.setAttribute('id', id);
	}

	return element;
}

export interface IBaseProps {

	/**
	 * Class to add to the root element
	 */
	className?: string;

	/**
	 * Id fof the root element
	 */
	id?: string;
}

interface IProps extends IBaseProps {
	children: React.ReactNode;

	/**
	 * Ref of element to render portal into.
	 */
	containerRef?: React.RefObject<Element>;

	/**
	 * Ref of element to render nested portals into.
	 */
	subPortalRef?: React.RefObject<Element>;
}

export function ClayPortal({
	children,
	className,
	containerRef,
	id,
	subPortalRef,
}: IProps) {
	const {theme} = useProvider();
	const parentPortalRef = React.useContext(ClayPortalContext);
	const portalRef = React.useRef(
		typeof document !== 'undefined'
			? createDivElement(classNames(theme, className), id)
			: null
	);
	React.useEffect(() => {
		const closestParent =
			parentPortalRef && parentPortalRef.current
				? parentPortalRef.current
				: document.body;
		const elToMountTo =
			containerRef && containerRef.current
				? containerRef.current
				: closestParent;
		if (elToMountTo && portalRef.current) {
			elToMountTo.appendChild(portalRef.current);
		}

		return () => {
			if (portalRef.current) {
				if (typeof portalRef.current.remove === 'function') {
					portalRef.current.remove();
				}
				else if (elToMountTo) {
					elToMountTo.removeChild(portalRef.current);
				}
			}
		};
	}, [containerRef, parentPortalRef]);
	const content = (
		<ClayPortalContext.Provider
			value={subPortalRef ? subPortalRef : portalRef}
		>
			{children}
		</ClayPortalContext.Provider>
	);

	return portalRef.current
		? createPortal(content, portalRef.current)
		: content;
}
