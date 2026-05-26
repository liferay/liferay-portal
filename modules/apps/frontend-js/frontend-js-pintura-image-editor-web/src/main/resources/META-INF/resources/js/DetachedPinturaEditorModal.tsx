/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useModal} from '@clayui/modal';
import React, {useEffect} from 'react';

import PinturaEditorModal from './PinturaEditorModal';

export interface DetachedPinturaEditorModalProps {
	imageName: string;
	imageUrl: string | Blob;
	onSave: (blob: Blob) => void | Promise<void>;
}

// The Liferay module loader generates a <link> tag for SCSS imports
// processed at build time, but the runtime that creates that tag never
// fires for this bundle, so the Clay theme overrides never reach the page.
// Injecting the stylesheet manually at module load is the spike-friendly
// workaround until the bundling pipeline is sorted out.

const THEME_CSS_HREF =
	'/o/frontend-js-pintura-image-editor-web/css/PinturaEditorModal.css';
const THEME_CSS_LINK_ID = 'lfr-pintura-theme';

if (
	typeof document !== 'undefined' &&
	!document.getElementById(THEME_CSS_LINK_ID)
) {
	const themeLink = document.createElement('link');

	themeLink.id = THEME_CSS_LINK_ID;
	themeLink.rel = 'stylesheet';
	themeLink.type = 'text/css';
	themeLink.href = THEME_CSS_HREF;

	document.head.appendChild(themeLink);
}

export default function DetachedPinturaEditorModal({
	imageName,
	imageUrl,
	onSave,
}: DetachedPinturaEditorModalProps) {
	const {observer, onOpenChange, open} = useModal();

	useEffect(() => {
		onOpenChange(true);
	}, [onOpenChange]);

	return (
		<PinturaEditorModal
			imageName={imageName}
			imageUrl={imageUrl}
			observer={observer}
			onOpenChange={onOpenChange}
			onSave={onSave}
			open={open}
		/>
	);
}
