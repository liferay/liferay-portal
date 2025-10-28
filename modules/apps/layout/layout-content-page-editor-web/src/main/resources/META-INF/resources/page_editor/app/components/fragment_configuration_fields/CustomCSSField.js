/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {useControlledState} from '@liferay/layout-js-components-web';
import {useId} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {PopoverTooltip} from '../../../common/components/PopoverTooltip';
import {FRAGMENT_CLASS_PLACEHOLDER} from '../../config/constants/fragmentClassPlaceholder';
import CodeMirrorEditor from '../CodeMirrorEditor';

const defaultValue = `.${FRAGMENT_CLASS_PLACEHOLDER} {\n\n}`;

export default function CustomCSSField({field, onValueSelect, value}) {
	const id = useId();
	const tooltipId = useId();

	const [customCSS, setCustomCSS] = useControlledState(value || defaultValue);
	const [editorModalOpen, setEditorModalOpen] = useState(false);

	const onSelect = (content) => {
		if (defaultValue.trim() === content?.trim()) {
			if (value) {
				onValueSelect(field.name, '');
			}

			return;
		}

		if (value !== content) {
			onValueSelect(field.name, content);
		}
	};

	return (
		<>
			<ClayForm.Group className="page-editor__custom-css-field" small>
				<div className="align-items-end d-flex justify-content-between">
					<label htmlFor={id}>
						{Liferay.Language.get('custom-css')}

						<PopoverTooltip
							content={Liferay.Language.get(
								'you-can-add-your-own-css-and-include-variables-to-use-existing-tokens'
							)}
							header={Liferay.Language.get('custom-css')}
							id={tooltipId}
							trigger={
								<ClayIcon
									aria-label={Liferay.Language.get(
										'show-more'
									)}
									className="ml-2"
									symbol="question-circle-full"
								/>
							}
						/>
					</label>

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('expand')}
						className="mb-2 p-0 page-editor__custom-css-field__expand-button text-secondary"
						displayType="unstyled"
						monospaced
						onClick={() => setEditorModalOpen(true)}
						size="sm"
						symbol="expand"
						title={Liferay.Language.get('expand')}
					/>
				</div>

				<textarea
					className="form-control text-3"
					id={id}
					onBlur={() => {
						onSelect(customCSS);
					}}
					onChange={(event) => setCustomCSS(event.target.value)}
					value={customCSS}
				/>
			</ClayForm.Group>

			<CustomCSSEditorModal
				customCSS={customCSS}
				onClose={() => {
					setEditorModalOpen(false);
				}}
				onSave={(content) => {
					setCustomCSS(content);
					onSelect(content);
				}}
				visible={editorModalOpen}
			/>
		</>
	);
}

function CustomCSSEditorModal({
	customCSS,
	onClose: onCloseCallback,
	onSave,
	visible,
}) {
	const [content, setContent] = useControlledState(customCSS);

	const {observer, onClose} = useModal({
		onClose: () => {
			onCloseCallback();
		},
	});

	return (
		visible && (
			<ClayModal
				containerProps={{className: 'cadmin'}}
				observer={observer}
				size="full-screen"
			>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('custom-css')}
				</ClayModal.Header>

				<ClayModal.Body>
					<CodeMirrorEditor
						className="page-editor__custom-css-field__editor-modal"
						initialContent={content}
						mode="css"
						onChange={setContent}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => {
									onClose();
								}}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								onClick={() => {
									onSave(content);
									onClose();
								}}
							>
								{Liferay.Language.get('add')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayModal>
		)
	);
}
