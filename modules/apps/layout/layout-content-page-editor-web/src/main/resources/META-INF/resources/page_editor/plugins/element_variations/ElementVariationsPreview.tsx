/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {
	forwardRef,
	useCallback,
	useEffect,
	useImperativeHandle,
	useRef,
	useState,
} from 'react';

import {Action, ElementVariation} from './elementVariationsReducer';
import getEditableElementOptions from './getEditableElementOptions';
import getElementVariationScript from './getElementVariationScript';

const HIGHLIGHT_STYLE_ELEMENT_ID = 'lfr-element-variation-highlight';

export interface ElementVariationsPreviewRef {
	reload: () => void;
}

interface Props {
	defaultLanguageId: string;
	dispatch: React.Dispatch<Action>;
	draftElementVariation: ElementVariation | null;
	highlightedTargetElement: string | null;
	itemNames: Record<string, string>;
	languageId: string;
	previewURL: string;
}

const ElementVariationsPreview = forwardRef<ElementVariationsPreviewRef, Props>(
	function ElementVariationsPreview(
		{
			defaultLanguageId,
			dispatch,
			draftElementVariation,
			highlightedTargetElement,
			itemNames,
			languageId,
			previewURL: initialPreviewURL,
		},
		ref
	) {
		const iframeRef = useRef<HTMLIFrameElement>(null);
		const appliedElementVariationRef = useRef<{
			element: Element;
			originalHTML: string | null;
			styleElement: HTMLStyleElement | null;
		} | null>(null);

		const [previewReady, setPreviewReady] = useState(false);
		const [previewURL, setPreviewURL] = useState(
			() => `${initialPreviewURL}&languageId=${languageId}`
		);

		useImperativeHandle(
			ref,
			() => ({
				reload: () => {
					setPreviewReady(false);

					iframeRef.current?.contentWindow?.location.reload();
				},
			}),
			[]
		);

		const applyDraftElementVariation = useCallback(() => {
			const iframeDocument = iframeRef.current?.contentDocument;

			if (!iframeDocument?.body) {
				return;
			}

			const appliedElementVariation = appliedElementVariationRef.current;

			if (appliedElementVariation) {
				if (
					appliedElementVariation.originalHTML !== null &&
					appliedElementVariation.element.isConnected
				) {
					appliedElementVariation.element.innerHTML =
						appliedElementVariation.originalHTML;
				}

				appliedElementVariation.styleElement?.remove();

				appliedElementVariationRef.current = null;
			}

			if (!draftElementVariation?.targetElement) {
				return;
			}

			const {hide, html, js, targetElement} = draftElementVariation;

			const element = iframeDocument.querySelector(targetElement);

			if (!element) {
				return;
			}

			const htmlValue = html[languageId] ?? html[defaultLanguageId] ?? '';
			const jsValue = js[languageId] ?? js[defaultLanguageId] ?? '';

			let originalHTML: string | null = null;
			let styleElement: HTMLStyleElement | null = null;

			if (htmlValue) {
				originalHTML = element.innerHTML;

				element.innerHTML = htmlValue;
			}

			if (jsValue) {
				const scriptElement = iframeDocument.createElement('script');

				scriptElement.textContent = getElementVariationScript({
					js: jsValue,
					targetElement,
				});

				iframeDocument.body.appendChild(scriptElement);

				iframeDocument.body.removeChild(scriptElement);
			}

			if (hide) {
				styleElement = iframeDocument.createElement('style');

				styleElement.textContent = `${targetElement} { display: none !important; }`;

				iframeDocument.head.appendChild(styleElement);
			}

			appliedElementVariationRef.current = {
				element,
				originalHTML,
				styleElement,
			};
		}, [defaultLanguageId, draftElementVariation, languageId]);

		const highlightTargetElements = useCallback(() => {
			const iframeDocument = iframeRef.current?.contentDocument;

			if (!iframeDocument?.head) {
				return;
			}

			let styleElement = iframeDocument.getElementById(
				HIGHLIGHT_STYLE_ELEMENT_ID
			) as HTMLStyleElement | null;

			const rules = [];

			if (highlightedTargetElement) {
				rules.push(
					`${highlightedTargetElement} { box-shadow: inset 0 0 0 2px var(--primary-l1, #4b93ff) !important; }`
				);
			}

			if (draftElementVariation?.targetElement) {
				rules.push(
					`${draftElementVariation.targetElement} { box-shadow: inset 0 0 0 2px var(--primary, #0b5fff) !important; }`
				);
			}

			if (!rules.length) {
				styleElement?.remove();

				return;
			}

			if (!styleElement) {
				styleElement = iframeDocument.createElement('style');

				styleElement.id = HIGHLIGHT_STYLE_ELEMENT_ID;

				iframeDocument.head.appendChild(styleElement);
			}

			styleElement.textContent = rules.join(' ');
		}, [draftElementVariation, highlightedTargetElement]);

		useEffect(() => {
			applyDraftElementVariation();
		}, [applyDraftElementVariation]);

		useEffect(() => {
			highlightTargetElements();
		}, [highlightTargetElements, previewReady]);

		useEffect(() => {
			setPreviewReady(false);

			setPreviewURL(`${initialPreviewURL}&languageId=${languageId}`);
		}, [initialPreviewURL, languageId]);

		return (
			<div className="d-flex flex-column flex-grow-1 position-relative">
				{previewReady ? null : (
					<ClayLoadingIndicator className="mt-3" />
				)}

				<iframe
					className="border-0 flex-grow-1 w-100"
					onLoad={() => {
						const iframeDocument =
							iframeRef.current?.contentDocument;

						if (iframeDocument) {
							dispatch({
								editableElementOptions:
									getEditableElementOptions(
										iframeDocument,
										itemNames
									),
								type: 'SET_EDITABLE_ELEMENT_OPTIONS',
							});
						}

						applyDraftElementVariation();

						highlightTargetElements();

						setPreviewReady(true);
					}}
					ref={iframeRef}
					src={previewURL}
					style={{visibility: previewReady ? 'visible' : 'hidden'}}
					title={Liferay.Language.get('element-variations')}
				/>
			</div>
		);
	}
);

export default ElementVariationsPreview;
