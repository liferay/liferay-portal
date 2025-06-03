/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClassicEditor as BaseClassicEditor, EventInfo} from 'ckeditor5';
import React from 'react';

import BaseEditor from './BaseEditor';
import getDefaultEditorConfig from './utils/getDefaultEditorConfig';
import {
	EEditorConfigPreset,
	EEditorVariant,
	LiferayEditorConfig,
	TEditor,
} from './utils/types';

const ClassicEditor = ({
	className,
	config,
	data,
	disabled,
	onChange,
	onReady,
}: {
	className?: string;
	config?: LiferayEditorConfig;
	data?: string;
	disabled?: boolean;
	onChange?: (event: EventInfo, editor: TEditor) => void;
	onReady?: (editor: TEditor) => void;
}) => {
	return (
		<BaseEditor
			className={className}
			config={{
				...getDefaultEditorConfig({
					editorVariant: EEditorVariant.CLASSIC,
					preset: config?.preset || EEditorConfigPreset.ADVANCED,
				}),
				...config,
			}}
			data={data}
			disabled={disabled}
			editor={BaseClassicEditor}
			onChange={onChange}
			onReady={(editor) => {
				Liferay.fire('ckeditor:ready', {editor});

				if ('toolbar' in editor.ui.view) {
					editor.ui.view.toolbar.items.map((item: any) => {
						if (item.buttonView) {
							item.buttonView.tooltipPosition = 'n';
						}

						item.tooltipPosition = 'n';
					});
				}

				const hasControlMenu = document.querySelector(
					'.control-menu-container'
				);

				if (!hasControlMenu) {
					editor.ui.viewportOffset = {
						top: 0,
					};
				}

				onReady?.(editor);
			}}
		/>
	);
};

export default ClassicEditor;
