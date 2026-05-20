/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {getEditorDefaults} from '@pqina/pintura';
import {PinturaEditor} from '@pqina/react-pintura';
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

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
	const [saving, setSaving] = useState(false);

	const handleDone = () => {
		setSaving(true);

		editorRef.current?.editor.processImage();
	};

	const handleProcess = async (result: {dest: Blob}) => {
		try {
			await onSave(result.dest);

			onOpenChange(false);
		}
		finally {
			setSaving(false);
		}
	};

	if (!open) {
		return null;
	}

	const title = sub(Liferay.Language.get('edit-x'), imageName);

	return (
		<ClayModal
			disableAutoClose={saving}
			observer={observer}
			size="full-screen"
		>
			{saving ? (
				<ClayModal.Header withTitle={false}>
					<ClayModal.ItemGroup>
						<ClayModal.Item>
							<ClayModal.Title>{title}</ClayModal.Title>
						</ClayModal.Item>
					</ClayModal.ItemGroup>
				</ClayModal.Header>
			) : (
				<ClayModal.Header>{title}</ClayModal.Header>
			)}

			<ClayModal.Body scrollable={false}>
				<PinturaEditor
					{...getEditorDefaults()}
					onProcess={handleProcess}
					ref={editorRef}
					src={imageUrl}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<>
						<ClayButton
							className="mr-2"
							disabled={saving}
							displayType="secondary"
							onClick={() => onOpenChange(false)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={saving}
							loading={saving}
							onClick={handleDone}
						>
							{saving
								? Liferay.Language.get('saving')
								: Liferay.Language.get('done')}
						</ClayButton>
					</>
				}
			/>
		</ClayModal>
	);
}
