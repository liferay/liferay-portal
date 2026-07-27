/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Provider} from '@clayui/core';
import {ClayIconSpriteContext} from '@clayui/icon';
import {
	CONSTANTS,
	accessibilityMenuAtom,
} from '@liferay/accessibility-settings-state-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import React, {useMemo} from 'react';
import * as ReactDOM from 'react-dom';
import {createRoot} from 'react-dom/client';

let counter = 0;

const CLASSNAME_TOOLTIP_SCOPE = 'lfr-tooltip-scope';

/**
 * This flag is a temporary workaround for unit tests that still use React 16.
 * It will be removed once the tests are converted to React 18
 */
const USE_REACT_16 = process.env.USE_REACT_16 === 'true';

/**
 * Wrapper for ReactDOM render that automatically:
 *
 * - Provides commonly-needed context (for example, the Clay spritemap).
 * - Unmounts when portlets are destroyed based on the received
 *   `portletId` value inside `renderData`. If none is passed, the
 *   component will be automatically unmounted before the next navigation.
 *
 * @param {Function|React.Element} renderable Component, or function that returns an Element, to be rendered.
 * @param {object} renderData Data to be passed to the component as props.
 * @param {HTMLElement} container DOM node where the component is to be mounted.
 *
 * The React docs advise not to rely on the render return value, so we
 * don't propagate it.
 *
 * @see https://reactjs.org/docs/react-dom.html#render
 */
export default function render(
	renderable:
		| NonNullable<React.ReactNode>
		| NonNullable<React.ForwardRefExoticComponent<any>>
		| React.ComponentType<any>
		| ((props: any) => NonNullable<React.ReactNode>),
	renderData: {
		__reactDOMFlushSync?: boolean;
		componentId?: string;
		portletId?: string;
		[key: string]: unknown;
	},
	container: Element
) {
	if (!container) {
		return;
	}

	if (!Liferay.SPA || Liferay.SPA.app) {
		const {
			__reactDOMFlushSync,
			hasBodyContent,
			portletId,
			...componentProps
		} = renderData;

		let {componentId} = renderData;

		if (!componentId) {
			componentId = `__UNNAMED_COMPONENT__${portletId}__${counter++}`;
		}

		if (hasBodyContent) {
			const tagBodyContent = container.querySelector('.tag-body-content');

			if (tagBodyContent?.children.length) {
				componentProps.children = tagBodyContent.children;
			}
		}

		container.classList.add(CLASSNAME_TOOLTIP_SCOPE);

		let root: any;

		if (!USE_REACT_16) {
			root = createRoot(container);
		}

		Liferay.component(
			componentId,
			{
				destroy: () => {
					container.classList.remove(CLASSNAME_TOOLTIP_SCOPE);

					try {
						if (USE_REACT_16) {
							ReactDOM.unmountComponentAtNode(container);
						}
						else {
							root.unmount();
						}
					}
					catch (error) {
						if (process.env.NODE_ENV === 'development') {
							console.error(error);
						}
					}
				},
			},
			{destroyOnNavigate: !portletId, portletId}
		);

		const Component: React.ElementType =
			typeof renderable === 'function' ||
			(renderable as any).$$typeof === Symbol.for('react.forward_ref')
				? (renderable as any)
				: null;

		const App = (
			<LiferayProvider spritemap={Liferay.Icons.spritemap}>
				{
					(Component ? (
						<Component {...componentProps} portletId={portletId} />
					) : (
						renderable
					)) as React.ReactNode
				}
			</LiferayProvider>
		);

		if (USE_REACT_16) {

			// eslint-disable-next-line @liferay/portal/no-react-dom-render
			ReactDOM.render(App, container);
		}
		else {
			const renderApp = () => {
				root.render(App);
			};

			// `__reactDOMFlushSync` is an escape hatch to avoid async rendering in React 18
			// This is only intended to be used for incremental upgrading.

			if (__reactDOMFlushSync) {
				ReactDOM.flushSync(renderApp);
			}
			else {
				renderApp();
			}
		}

		return root;
	}
	else {
		Liferay.once('SPAReady', () => {
			render(renderable, renderData, container);
		});
	}
}

type Props = {
	children: React.ReactNode;
	spritemap: string;
};

function LiferayProvider({children, spritemap}: Props) {
	const [accessibilityMenu] = useLiferayState(accessibilityMenuAtom);

	const reducedMotion = useMemo(() => {
		const reducedMotion =
			accessibilityMenu[CONSTANTS.ACCESSIBILITY_SETTING_REDUCED_MOTION];

		if (reducedMotion?.value) {
			return 'always';
		}
		else {
			return 'user';
		}
	}, [accessibilityMenu]);

	return (
		<Provider reducedMotion={reducedMotion} spritemap={spritemap}>
			<ClayIconSpriteContext.Provider value={spritemap}>
				{children}
			</ClayIconSpriteContext.Provider>
		</Provider>
	);
}
