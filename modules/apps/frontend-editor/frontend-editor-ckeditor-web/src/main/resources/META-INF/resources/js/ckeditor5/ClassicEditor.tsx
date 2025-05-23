/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClassicEditor as BaseClassicEditor, EventInfo} from 'ckeditor5';
import React from 'react';

import '../../css/ckeditor5/editor.scss';

import {CKEditor} from '@ckeditor/ckeditor5-react';

import getDefaultEditorConfig from './utils/getDefaultEditorConfig';
import {
	EEditorConfigPreset,
	EEditorType,
	LiferayEditorConfig,
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
	onChange?: (event: EventInfo, editor: BaseClassicEditor) => void;
	onReady?: (editor: BaseClassicEditor) => void;
}) => {
	return (
		<div className={`lfr-ck ${className ? className : ''}`}>
			<CKEditor
				config={{
					...getDefaultEditorConfig({
						editorType: EEditorType.CLASSIC,
						preset: config?.preset || EEditorConfigPreset.ADVANCED,
					}),
					...config,
				}}
				data={data}
				disabled={disabled}
				editor={BaseClassicEditor}
				onChange={onChange}
				onReady={(editor: BaseClassicEditor) => {
					Liferay.fire('ckeditor:ready', {editor});

					editor.ui.view.toolbar.items.map((item: any) => {
						if (item.buttonView) {
							item.buttonView.tooltipPosition = 'n';
						}

						item.tooltipPosition = 'n';
					});

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
		</div>
	);
};

export default ClassicEditor;
