/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {CodeEditor} from '@liferay/object-js-components-web';
import {openModal} from 'frontend-js-components-web';
import React, {useState} from 'react';

import './openCodeEditorModal.scss';

interface Props {
	initialValue: string;
	mode: 'text/html' | 'text/javascript';
	onSave: (value: string) => void;
	title: string;
}

export default function openCodeEditorModal({
	initialValue,
	mode,
	onSave,
	title,
}: Props) {
	openModal({
		className: 'element-variations__code-editor-modal',
		containerProps: {},
		contentComponent: ({closeModal}: {closeModal: () => void}) => (
			<CodeEditorModalContent
				closeModal={closeModal}
				initialValue={initialValue}
				mode={mode}
				onSave={onSave}
				title={title}
			/>
		),
		size: 'full-screen',
	});
}

function CodeEditorModalContent({
	closeModal,
	initialValue,
	mode,
	onSave,
	title,
}: Props & {closeModal: () => void}) {
	const [content, setContent] = useState(initialValue);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body className="d-flex flex-column">
				<CodeEditor
					className="flex-grow-1"
					mode={mode}
					onChange={(value) => setContent(value ?? '')}
					value={initialValue}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={() => {
								onSave(content);

								closeModal();
							}}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
