/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {
	ClayToaster,
	DisplayType,
	IClayAlertProps,
	createToastStore,
} from '@clayui/alert';
import {render} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {uuidv4} from 'frontend-js-web';
import React, {MouseEvent, ReactNode} from 'react';
import {flushSync} from 'react-dom';

import './notification.scss';

const DEFAULT_ALERT_CONTAINER_ID = 'ToastAlertContainer';

const DEFAULT_RENDER_DATA = {
	portletId: 'UNKNOWN_PORTLET_ID',
};

const DEFAULT_TOAST_TYPE_TITLES = {
	danger: Liferay.Language.get('error'),
	info: Liferay.Language.get('info'),
	secondary: '',
	success: Liferay.Language.get('success'),
	warning: Liferay.Language.get('warning'),
};

const TOAST_AUTO_CLOSE_INTERVAL = 5000;

const toastStore = createToastStore();

let toasterNode: HTMLElement | null = null;

const renderToasterIfMissing = () => {
	if (toasterNode?.isConnected) {
		return;
	}

	Liferay.destroyComponent(DEFAULT_ALERT_CONTAINER_ID);

	toasterNode = document.body.appendChild(document.createElement('div'));

	render(
		<ClayToaster
			className="cadmin"
			id={DEFAULT_ALERT_CONTAINER_ID}
			store={toastStore}
		/>,
		{
			...DEFAULT_RENDER_DATA,
			__reactDOMFlushSync: true,
			componentId: DEFAULT_ALERT_CONTAINER_ID,
		},
		toasterNode
	);
};

const rootsMap = new Map();

const openContainerToast = ({
	alertProps,
	containerElement,
	onClick,
	onClose,
	renderData,
	toastProps,
}: {
	alertProps: IClayAlertProps;
	containerElement: HTMLElement;
	onClick: NonNullable<OpenToastProps['onClick']>;
	onClose: NonNullable<OpenToastProps['onClose']>;
	renderData: NonNullable<OpenToastProps['renderData']>;
	toastProps: NonNullable<OpenToastProps['toastProps']>;
}) => {
	const id = uuidv4();

	const rootElement = containerElement.appendChild(
		document.createElement('div')
	);

	const cleanUp = () => {
		if (rootsMap.has(id)) {
			const root = rootsMap.get(id);

			root.unmount();

			rootsMap.delete(id);
		}
	};

	const onCloseFn = (event: CloseEvent) => {
		if (onClose) {
			onClose({event});
		}

		if (!event || !event.defaultPrevented) {
			rootElement.remove();

			cleanUp();
		}
	};

	const root = render(
		<ClayAlert
			{...alertProps}
			className={classNames('mb-3', toastProps.className)}
			onClick={(event) => onClick({event, onClose: onCloseFn})}
			onClose={onCloseFn as () => void}
		/>,
		{...renderData, __reactDOMFlushSync: true},
		rootElement
	);

	rootsMap.set(id, root);
};

const openGlobalToast = ({
	alertProps,
	onClick,
	onClose,
	renderData,
}: {
	alertProps: IClayAlertProps;
	onClick: NonNullable<OpenToastProps['onClick']>;
	onClose: NonNullable<OpenToastProps['onClose']>;
	renderData: NonNullable<OpenToastProps['renderData']>;
}) => {
	const key: string = flushSync(() =>
		toastStore.add({
			...alertProps,
			onClick: (event: MouseEvent) =>
				onClick({
					event,
					onClose: (event) =>
						toastStore.close(
							key,
							event as unknown as MouseEvent<HTMLButtonElement>
						),
				}),
			onClose: ((event: CloseEvent) => onClose({event})) as () => void,
		})
	);

	Liferay.component(
		renderData.componentId ||
			`__UNNAMED_COMPONENT__${renderData.portletId}__${uuidv4()}`,
		{destroy: () => toastStore.remove(key)},
		{
			destroyOnNavigate: !renderData.portletId,
			portletId: renderData.portletId,
		}
	);

	renderToasterIfMissing();
};

export interface OpenToastProps {
	autoClose?: number | boolean;
	autoFocus?: boolean;
	container?: HTMLElement;
	containerId?: string;
	message?: string;
	onClick?: ({
		event,
		onClose,
	}: {
		event: MouseEvent;
		onClose: (event: CloseEvent) => void;
	}) => void;
	onClose?: ({event}: {event: CloseEvent}) => void;
	renderData?: {componentId?: string; portletId?: string};
	title?: string;
	toastProps?: {
		actions?: ReactNode; // Element | React.JSX.Element;
		autoClose?: number | boolean;
		className?: string;
	};
	type?: DisplayType;
	variant?: 'feedback' | 'stripe' | 'inline';
}

/**
 * Function that implements the Toast pattern, which allows to present feedback
 * to user actions as a toast message in the lower left corner of the page
 *
 * @param {number|boolean} autoClose Flag to indicate alert should automatically call onClose.
 * It also accepts a duration (in ms) which indicates how long to wait. If true is passed in, the
 * timeout will be 10000ms. See https://clayui.com/docs/components/alert.html for more details.
 * @param {HTMLElement} container Target element where the toast React component should be mounted.
 * @param {string} containerId The id of the element where the toast React component should be mounted.
 * @param {string|HTML} message The message to show in the toast notification
 * @param {string|HTML} title The title associated with the message
 * @param {string} displayType The displayType of notification to show. It can be one of the
 * following: 'danger', 'info', 'success', 'warning'
 * @return {ClayToast} The Alert toast created
 * @review
 */

function openToast({
	autoClose = TOAST_AUTO_CLOSE_INTERVAL,
	autoFocus = false,
	container,
	containerId,
	message = '',
	onClick = () => {},
	onClose = () => {},
	renderData = DEFAULT_RENDER_DATA,
	title,
	toastProps = {},
	type = 'success',
	variant,
}: OpenToastProps) {
	let titleHTML =
		title === undefined ? DEFAULT_TOAST_TYPE_TITLES[type] : title;

	if (titleHTML) {
		titleHTML = titleHTML.replace(/:$/, '');
		titleHTML = `<strong class="lead">${titleHTML}:</strong>`;
	}
	else {
		titleHTML = '';
	}

	const content = (
		<div
			dangerouslySetInnerHTML={{
				__html: `${titleHTML}${message}`,
			}}
		/>
	);

	const alertProps: IClayAlertProps = {
		autoClose,
		autoFocus,
		children: content,
		closeButtonAriaLabel: Liferay.Language.get('close'),
		displayType: type,
		variant,
		...toastProps,
		role: 'alert',
	};

	const containerElement =
		container || (containerId && document.getElementById(containerId));

	if (containerElement) {
		openContainerToast({
			alertProps,
			containerElement,
			onClick,
			onClose,
			renderData,
			toastProps,
		});
	}
	else {
		openGlobalToast({alertProps, onClick, onClose, renderData});
	}
}

export default openToast;
