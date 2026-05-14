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

export default function DetachedPinturaEditorModal({
	imageName,
	imageUrl,
	onSave,
}: DetachedPinturaEditorModalProps) {
	const {observer, onOpenChange, open} = useModal();

	useEffect(() => {
		const cssId = 'pintura-editor-css';

		if (!document.getElementById(cssId)) {
			const link = document.createElement('link');
			link.href =
				'/o/frontend-js-pintura-image-editor-web/css/pintura.css';
			link.id = cssId;
			link.rel = 'stylesheet';
			document.head.appendChild(link);
		}

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
