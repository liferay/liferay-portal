/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventInfo} from 'ckeditor5';
import {loadEditorClientExtensions} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import '../../css/ckeditor5/editor.scss';

import {CKEditor} from '@ckeditor/ckeditor5-react';
import ClayLoadingIndicator from '@clayui/loading-indicator';

import {LiferayEditorConfig, TEditor} from './utils/types';

const BaseEditor = ({
	className,
	config,
	data,
	disabled,
	editor,
	onChange,
	onReady,
}: {
	className?: string;
	config?: LiferayEditorConfig;
	data?: string;
	disabled?: boolean;
	editor: any;
	onChange?: (event: EventInfo, editor: TEditor) => void;
	onReady?: (editor: TEditor) => void;
}) => {
	const [loading, setLoading] = useState(true);
	const firstRenderRef = useRef(true);

	const [editorConfig, setEditorConfig] = useState(config);

	useEffect(() => {
		if (firstRenderRef.current) {
			firstRenderRef.current = false;
		}
		else {
			return;
		}

		if (!editorConfig || !editorConfig.editorTransformerURLs) {
			setLoading(false);

			return;
		}

		const {extraPlugins, licenseKey, plugins} = editorConfig;

		loadEditorClientExtensions({
			config: editorConfig,
			onLoad: ({transformedConfig}: any) => {
				setEditorConfig(() => ({
					...transformedConfig,
					extraPlugins,
					licenseKey,
					plugins,
				}));

				setLoading(false);
			},
		});
	}, [editorConfig]);

	return loading ? (
		<ClayLoadingIndicator />
	) : (
		<div className={`lfr-ck ${className ? className : ''}`}>
			<CKEditor
				config={editorConfig}
				data={data}
				disabled={disabled}
				editor={editor}
				onChange={onChange}
				onReady={onReady}
			/>
		</div>
	);
};

export default BaseEditor;
