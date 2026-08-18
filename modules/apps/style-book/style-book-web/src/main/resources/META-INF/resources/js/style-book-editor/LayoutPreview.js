/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {config} from './config';
import {LAYOUT_TYPES} from './constants/layoutTypes';
import {
	useLoading,
	usePreviewLayout,
	usePreviewLayoutType,
	useSetLoading,
} from './contexts/LayoutContext';
import {useFrontendTokensValues} from './contexts/StyleBookEditorContext';

export default React.memo(function LayoutPreview() {
	const frontendTokensValues = useFrontendTokensValues();
	const loading = useLoading();
	const previewLayout = usePreviewLayout();
	const previewLayoutType = usePreviewLayoutType();
	const setLoading = useSetLoading();

	const iframeRef = useRef();
	const [iframeLoaded, setIframeLoaded] = useState(false);

	const loadFrontendTokenValues = useCallback(() => {
		if (iframeLoaded) {
			const root = iframeRef.current.contentDocument.documentElement;

			if (root) {
				root.removeAttribute('style');

				for (const {
					cssVariableMapping,
					value,
				} of config.sortFrontendTokenValues(frontendTokensValues)) {
					root.style.setProperty(`--${cssVariableMapping}`, value);
				}

				setLoading(false);
			}
		}
	}, [frontendTokensValues, iframeLoaded, setLoading]);

	useEffect(() => {
		loadFrontendTokenValues();
	}, [loadFrontendTokenValues, frontendTokensValues]);

	useEffect(() => {
		if (
			iframeRef.current &&
			previewLayoutType !== LAYOUT_TYPES.fragmentCollection
		) {
			iframeRef.current.style['pointer-events'] = 'none';
		}
	}, [previewLayout, previewLayoutType]);

	return (
		<>
			<div className="style-book-editor__page-preview">
				{loading && previewLayout?.url && (
					<div className="align-items-center d-flex h-100 justify-content-center">
						<ClayLoadingIndicator />
					</div>
				)}

				{previewLayout?.url ? (
					<>
						<iframe
							className={classNames(
								'style-book-editor__page-preview-frame',
								{'d-none': loading}
							)}
							onLoad={() => {
								loadOverlay(iframeRef, previewLayoutType);
								setIframeLoaded(true);
								loadFrontendTokenValues();
							}}
							ref={iframeRef}
							src={previewLayout?.url}
							title={Liferay.Language.get('page-preview')}
						/>
					</>
				) : (
					<ClayEmptyState
						className="h-100 justify-content-center mt-0 style-book-editor__page-preview-empty-site-message"
						description={Liferay.Language.get(
							'you-cannot-preview-the-style-book-because-your-site-is-empty'
						)}
						imgSrc={`${themeDisplay.getPathThemeImages()}/states/empty_state.svg`}
						title={Liferay.Language.get('no-results-found')}
					/>
				)}
			</div>
		</>
	);
});

function loadOverlay(iframeRef, previewLayoutType) {
	if (previewLayoutType === LAYOUT_TYPES.fragmentCollection) {
		iframeRef.current.contentDocument.body.addEventListener(
			'click',
			(event) => {
				event.preventDefault();
			},
			{
				capture: true,
			}
		);
	}
	else {
		const style = {
			cursor: 'not-allowed',
			height: '100%',
			left: 0,
			position: 'fixed',
			top: 0,
			width: '100%',
			zIndex: 100000,
		};

		if (iframeRef.current) {
			const overlay = document.createElement('div');

			overlay.setAttribute('data-qa-id', 'styleBookPreviewOverlay');

			Object.keys(style).forEach((key) => {
				overlay.style[key] = style[key];
			});

			iframeRef.current.removeAttribute('style');
			iframeRef.current.contentDocument.body.append(overlay);
		}
	}
}
