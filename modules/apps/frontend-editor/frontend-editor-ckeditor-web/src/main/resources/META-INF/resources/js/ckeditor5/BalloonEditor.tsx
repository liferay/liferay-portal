/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {BalloonEditor as BaseBalloonEditor, EventInfo} from 'ckeditor5';
import React from 'react';

import BaseEditor from './BaseEditor';
import getDefaultEditorConfig from './utils/getDefaultEditorConfig';
import {
	EEditorConfigPreset,
	EEditorVariant,
	LiferayEditorConfig,
	TEditor,
} from './utils/types';

const BalloonEditor = ({
	className,
	config,
	data,
	onChange,
	onReady,
}: {
	className?: string;
	config?: LiferayEditorConfig;
	data?: string;
	onChange?: (event: EventInfo, editor: TEditor) => void;
	onReady?: (editor: TEditor) => void;
}) => {
	return (
		<BaseEditor
			className={className}
			config={{
				...getDefaultEditorConfig({
					editorVariant: EEditorVariant.BALLOON,
					preset: config?.preset || EEditorConfigPreset.ADVANCED,
				}),
				...config,
			}}
			data={data}
			editor={BaseBalloonEditor}
			onChange={onChange}
			onReady={onReady}
		/>
	);
};

export default BalloonEditor;
