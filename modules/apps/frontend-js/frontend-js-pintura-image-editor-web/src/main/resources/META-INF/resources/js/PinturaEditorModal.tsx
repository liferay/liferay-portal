/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {getEditorDefaults} from '@pqina/pintura';
import {PinturaEditor} from '@pqina/react-pintura';
import React, {useRef} from 'react';

export interface PinturaEditorModalProps {
	imageName: string;
	imageUrl: string | Blob;
	observer: ReturnType<typeof useModal>['observer'];
	onOpenChange: (open: boolean) => void;
	onSave: (blob: Blob) => void | Promise<void>;
	open: boolean;
}

export default function PinturaEditorModal({
	imageName,
	imageUrl,
	observer,
	onOpenChange,
	onSave,
	open,
}: PinturaEditorModalProps) {
	const editorRef = useRef<PinturaEditor>(null);

	const handleDone = () => {
		editorRef.current?.editor.processImage();
	};

	const handleProcess = async (result: {dest: Blob}) => {
		await onSave(result.dest);

		onOpenChange(false);
	};

	if (!open) {
		return null;
	}

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header>
				{Liferay.Language.get('edit-x').replace('{0}', imageName)}
			</ClayModal.Header>

			<ClayModal.Body scrollable={false}>
				<div style={{height: '100%'}}>
					<PinturaEditor
						{...getEditorDefaults()}
						onProcess={handleProcess}
						ref={editorRef}
						src={imageUrl}
					/>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<>
						<ClayButton
							className="mr-2"
							displayType="secondary"
							onClick={() => onOpenChange(false)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={handleDone}>
							{Liferay.Language.get('done')}
						</ClayButton>
					</>
				}
			/>
		</ClayModal>
	);
}
