/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {forwardRef} from 'react';

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
		return (
			<div
				{...(ariaInvalid ? {'aria-invalid': 'true'} : {})}
				aria-label={ariaLabel}
				className={className}
				id={`${name}Container`}
				role="textbox"
			>
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
						editor.setData(contents, {
							callback: () => {
								editor.resetUndo();

								onReady({editor});
							},
							noSnapshot: true,
						});

						const iframe = document.querySelector(
							'iframe.cke_wysiwyg_frame'
						);

						iframe.onload = function () {
							const iframeDocument = iframe.contentDocument;
							const iframeBody =
								iframeDocument.querySelector(
									'body.cke_editable'
								);

							if (iframeBody) {
								iframeBody.setAttribute(
									'aria-required',
									ariaRequired
								);
							}
						};
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
