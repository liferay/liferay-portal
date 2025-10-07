/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ReactPortal, useIsMounted} from '@liferay/frontend-js-react-web';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import RawDOM from '../../common/components/RawDOM';
import {config} from '../config/index';

type GlobalContextValueType = {
	document: Document;
	iframe?: HTMLIFrameElement;
	window: Window;
};

type GlobalContextType = [
	GlobalContextValueType,
	(value: GlobalContextValueType) => void,
];

const DEFAULT_GLOBAL_CONTEXT: GlobalContextValueType = {document, window};

const GlobalContext = React.createContext<GlobalContextType>([
	DEFAULT_GLOBAL_CONTEXT,
	() => {},
]);

export function GlobalContextFrame({
	children,
	useIframe,
}: {
	children: React.ReactNode;
	useIframe: boolean;
}) {
	const [baseElement, setBaseElement] = useState<HTMLElement | null>(null);
	const [iframeContext, setIframeContext] =
		useState<GlobalContextValueType | null>(null);
	const [iframeElement, setIframeElement] =
		useState<HTMLIFrameElement | null>(null);
	const isMounted = useIsMounted();
	const [loadIframe, setLoadIframe] = useState(false);
	const localContext = useMemo(() => ({document, window}), []);
	const [, setGlobalContext] = useContext<GlobalContextType>(GlobalContext);

	useEffect(() => {
		if (useIframe && !loadIframe) {
			setLoadIframe(true);
		}
	}, [useIframe, loadIframe]);

	useEffect(() => {
		let timeoutId: NodeJS.Timeout | null = null;

		const handleIframeLoaded = () => {
			if (
				!isMounted() ||
				!iframeElement ||
				!iframeElement.contentDocument
			) {
				return;
			}

			const pageEditorStylesLinkId = `${config.portletNamespace}pageEditorStylesLink`;

			if (
				!iframeElement.contentDocument?.getElementById(
					pageEditorStylesLinkId
				)
			) {
				const pageEditorStylesLink =
					iframeElement.contentDocument.createElement('link');

				pageEditorStylesLink.id = pageEditorStylesLinkId;
				pageEditorStylesLink.rel = 'stylesheet';
				pageEditorStylesLink.href = config.getIframeContentCssURL;

				iframeElement.contentDocument.head.appendChild(
					pageEditorStylesLink
				);
			}

			const element =
				iframeElement.contentDocument &&
				(iframeElement.contentDocument.getElementById('main-content') ||
					iframeElement.contentDocument.getElementById(
						'master-layout-wrapper'
					));

			if (element) {

				// False positive - react-compiler/react-compiler
				// eslint-disable-next-line react-compiler/react-compiler
				element.innerHTML = '';

				iframeElement.contentWindow?.requestAnimationFrame(() => {
					setBaseElement(element);

					setIframeContext({
						document: iframeElement.contentDocument!,
						iframe: iframeElement,
						window: iframeElement.contentWindow!,
					});
				});
			}
			else {
				timeoutId = setTimeout(handleIframeLoaded, 100);
			}
		};

		if (iframeElement) {
			iframeElement.addEventListener('load', handleIframeLoaded);
			iframeElement.src = config.getIframeContentURL;
		}

		return () => {
			if (timeoutId) {
				clearTimeout(timeoutId);
			}

			if (iframeElement) {
				iframeElement.removeEventListener('load', handleIframeLoaded);
			}

			if (isMounted()) {
				setBaseElement(null);
				setIframeContext(DEFAULT_GLOBAL_CONTEXT);
			}
		};
	}, [iframeElement, isMounted]);

	let context: GlobalContextValueType | null = null;
	let content;

	if (useIframe && baseElement && iframeContext) {
		content = <ReactPortal container={baseElement}>{children}</ReactPortal>;
		context = iframeContext;

		iframeElement?.classList.remove(
			'page-editor__global-context-iframe--hidden',
			'page-editor__global-context-iframe--loading'
		);
	}
	else if (!useIframe) {
		content = <>{children}</>;
		context = localContext;

		if (iframeElement) {
			iframeElement.classList.add(
				'page-editor__global-context-iframe--hidden'
			);
		}
	}
	else {
		content = <ClayLoadingIndicator />;
		context = localContext;
	}

	useEffect(() => {
		setGlobalContext(context!);
	}, [context, setGlobalContext]);

	return (
		<>
			{content}

			{loadIframe ? (
				<RawDOM
					TagName="iframe"
					elementRef={(element) => {
						if (element) {
							element.classList.add(
								'page-editor__global-context-iframe',
								'page-editor__global-context-iframe--loading'
							);
						}

						setIframeElement(element as HTMLIFrameElement);
					}}
				/>
			) : null}
		</>
	);
}

GlobalContextFrame.propTypes = {
	useIframe: PropTypes.bool,
};

export function GlobalContextProvider({children}: {children: React.ReactNode}) {
	return (
		<GlobalContext.Provider value={useState(DEFAULT_GLOBAL_CONTEXT)}>
			{children}
		</GlobalContext.Provider>
	);
}

export function useGlobalContext() {
	const [globalContext] = useContext(GlobalContext);

	return globalContext;
}
