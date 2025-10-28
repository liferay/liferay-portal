/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useState} from 'react';

import {COPY_BUTTON_CSS_CLASS} from '../utils/constants';
import {openSuccessToast} from '../utils/toasts';
import CodeMirrorEditor from './CodeMirrorEditor';

const PreviewModal = ({body, children, size = 'md', title}) => {
	const [visible, setVisible] = useState(false);
	const {observer} = useModal({
		onClose: () => setVisible(false),
	});

	return (
		<>
			{visible && (
				<ClayModal
					className="sxp-preview-modal-root"
					observer={observer}
					size={size}
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{title}
					</ClayModal.Header>

					<ClayModal.Body>{body}</ClayModal.Body>
				</ClayModal>
			)}

			<div onClick={() => setVisible(!visible)}>{children}</div>
		</>
	);
};

export function PreviewModalWithCopyDownload({
	children,
	fileName,
	foldInitializationDelay,
	folded = false,
	lineWrapping,
	readOnly = true,
	size,
	text,
	title,
	type = 'application/json',
}) {
	return (
		<PreviewModal
			body={
				<>
					<ClayButton.Group spaced>
						<ClayButton
							className={COPY_BUTTON_CSS_CLASS}
							data-clipboard-text={text}
							displayType="secondary"
							small
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="copy" />
							</span>

							{Liferay.Language.get('copy-to-clipboard')}
						</ClayButton>

						<ClayLink
							displayType="secondary"
							download={fileName}
							href={URL.createObjectURL(
								new Blob([text], {
									type,
								})
							)}
							onClick={() => {
								openSuccessToast({
									message: Liferay.Language.get('downloaded'),
								});
							}}
							outline
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="download" />
							</span>

							{Liferay.Language.get('download')}
						</ClayLink>
					</ClayButton.Group>

					<CodeMirrorEditor
						foldInitializationDelay={foldInitializationDelay}
						folded={folded}
						lineWrapping={lineWrapping}
						readOnly={readOnly}
						value={text}
					/>
				</>
			}
			size={size}
			title={title}
		>
			{children}
		</PreviewModal>
	);
}

export default PreviewModal;
