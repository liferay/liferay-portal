/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {forwardRef, useCallback, useEffect, useRef} from 'react';

import BaseEditor from './BaseEditor';

const ClassicEditor = forwardRef(
	(
		{
			ariaInvalid,
			ariaLabel,
			ariaRequired,
			className,
			contents,
			editorConfig,
			initialToolbarSet = 'simple',
			name,
			onReady = () => {},
			title,
			...otherProps
		},
		ref
	) => {
		const editableARIAAttributesRef = useRef({
			ariaInvalid,
			ariaLabel,
			ariaRequired,
		});
		const editorInstanceRef = useRef();

		/**
		 * The identity of this callback has to stay stable. CKEditor's `on`
		 * deduplicates listeners by function, so a stable identity is what
		 * keeps `contentDom` from accumulating one listener per call, and the
		 * current attributes are read from a ref so that the listener never
		 * applies the values a past render closed over.
		 */

		const setEditableARIAAttributes = useCallback(() => {
			const editable = editorInstanceRef.current?.editable();

			if (!editable) {
				return;
			}

			const {ariaInvalid, ariaLabel, ariaRequired} =
				editableARIAAttributesRef.current;

			if (ariaLabel) {
				editable.setAttribute('aria-label', ariaLabel);
			}

			if (ariaInvalid) {
				editable.setAttribute('aria-invalid', 'true');
			}
			else {
				editable.removeAttribute('aria-invalid');
			}

			if (ariaRequired) {
				editable.setAttribute('aria-required', 'true');
			}
			else {
				editable.removeAttribute('aria-required');
			}
		}, []);

		useEffect(() => {
			editableARIAAttributesRef.current = {
				ariaInvalid,
				ariaLabel,
				ariaRequired,
			};

			setEditableARIAAttributes();
		}, [ariaInvalid, ariaLabel, ariaRequired, setEditableARIAAttributes]);

		return (
			<div className={className} id={`${name}Container`}>
				{title && (
					<label className="control-label" htmlFor={name}>
						{title}
					</label>
				)}

				<BaseEditor
					className="lfr-editable"
					config={{
						toolbar: initialToolbarSet,
						...editorConfig,
					}}
					contents={contents}
					name={name}
					onBeforeLoad={(CKEDITOR) => {
						CKEDITOR.disableAutoInline = true;
						CKEDITOR.dtd.$removeEmpty.i = 0;
						CKEDITOR.dtd.$removeEmpty.span = 0;

						CKEDITOR.getNextZIndex = function () {
							return CKEDITOR.dialog._.currentZIndex
								? CKEDITOR.dialog._.currentZIndex + 10
								: Liferay.zIndex.WINDOW + 10;
						};
					}}
					onInstanceReady={({editor}) => {
						editorInstanceRef.current = editor;

						const loadData = () => {
							editor.setData(contents, {
								callback: () => {
									editor.resetUndo();

									onReady({editor});
								},
								noSnapshot: true,
							});
						};

						const isBBCodePluginEnabled =
							editor.config.extraPlugins?.indexOf('bbcode') !==
							-1;

						if (isBBCodePluginEnabled) {
							const hasProcessor =
								editor.dataProcessor &&
								editor.dataProcessor.constructor.name ===
									'BBCodeDataProcessor';

							if (hasProcessor) {
								loadData();
							}
							else {
								editor.once(
									'customDataProcessorLoaded',
									loadData
								);
							}
						}
						else {
							loadData();
						}

						setEditableARIAAttributes();

						editor.on('contentDom', setEditableARIAAttributes);
					}}
					ref={ref}
					{...otherProps}
				/>
			</div>
		);
	}
);

ClassicEditor.propTypes = {
	contents: PropTypes.string,
	editorConfig: PropTypes.object,
	initialToolbarSet: PropTypes.string,
	name: PropTypes.string,
	title: PropTypes.string,
};

export {ClassicEditor};
export default ClassicEditor;
