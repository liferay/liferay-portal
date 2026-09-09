/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Stage.scss';

import React from 'react';

import {useEditorId} from '../chrome/instance';
import {t} from '../i18n';
import {imageTransform} from '../imaging/geometry';
import {LoadedImage} from '../imaging/loadImage';
import {EditState, rotatedSize} from '../state/types';

interface Props {
	image: LoadedImage;
	onWorkspacePointerLeave?: () => void;
	onWorkspacePointerMove?: (event: React.PointerEvent) => void;
	onZoom: (direction: -1 | 1) => void;
	onZoomActual: () => void;
	onZoomFit: () => void;
	state: EditState;
	workspaceRef?: React.Ref<HTMLDivElement>;
	zoom: number;
}

export function Workspace({
	image,
	onWorkspacePointerLeave,
	onWorkspacePointerMove,
	onZoom,
	onZoomActual,
	onZoomFit,
	state,
	workspaceRef,
	zoom,
}: Props) {
	const eid = useEditorId();

	const bounds = rotatedSize(state);

	const handleKeyDown = (event: React.KeyboardEvent) => {
		if (event.key === '+' || event.key === '=') {
			event.preventDefault();
			onZoom(1);
		}
		else if (event.key === '-' || event.key === '_') {
			event.preventDefault();
			onZoom(-1);
		}
		else if (event.key === '0') {
			event.preventDefault();
			onZoomFit();
		}
		else if (event.key === '1') {
			event.preventDefault();
			onZoomActual();
		}
	};

	return (
		<div
			aria-describedby={eid('workspace-description')}
			aria-label={t('image-workspace')}
			className="editor-workspace"
			onKeyDown={handleKeyDown}
			onPointerLeave={onWorkspacePointerLeave}
			onPointerMove={onWorkspacePointerMove}
			ref={workspaceRef}
			role="region"
			tabIndex={0}
		>
			<span className="sr-only" id={eid('workspace-description')}>
				{t('workspace-description')}
			</span>

			<svg
				className="editor-stage"
				height={bounds.height * zoom}
				viewBox={`0 0 ${bounds.width} ${bounds.height}`}
				width={bounds.width * zoom}
			>
				<g transform={imageTransform(state)}>
					<image
						height={state.sourceHeight}
						href={image.previewUrl}
						preserveAspectRatio="none"
						width={state.sourceWidth}
					/>
				</g>
			</svg>
		</div>
	);
}
