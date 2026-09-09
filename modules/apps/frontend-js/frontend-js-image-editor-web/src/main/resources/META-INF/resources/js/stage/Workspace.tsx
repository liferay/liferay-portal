/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Stage.scss';

import React from 'react';

import {useEditorId} from '../chrome/instance';
import {t} from '../i18n';
import {LoadedImage} from '../imaging/loadImage';

interface Props {
	image: LoadedImage;
	workspaceRef?: React.Ref<HTMLDivElement>;
	zoom: number;
}

export function Workspace({image, workspaceRef, zoom}: Props) {
	const eid = useEditorId();

	return (
		<div
			aria-describedby={eid('workspace-description')}
			aria-label={t('image-workspace')}
			className="editor-workspace"
			ref={workspaceRef}
			role="region"
			tabIndex={0}
		>
			<span className="sr-only" id={eid('workspace-description')}>
				{t('workspace-description')}
			</span>

			<svg
				className="editor-stage"
				height={image.height * zoom}
				viewBox={`0 0 ${image.width} ${image.height}`}
				width={image.width * zoom}
			>
				<image
					height={image.height}
					href={image.previewUrl}
					preserveAspectRatio="none"
					width={image.width}
				/>
			</svg>
		</div>
	);
}
