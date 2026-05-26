/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {UtilTab, getEditorDefaults} from '@pqina/pintura';
import {PinturaEditor} from '@pqina/react-pintura';
import {sub} from 'frontend-js-web';
import React, {useMemo, useRef, useState} from 'react';

import '../css/PinturaEditorModal.scss';

// Map Pintura util ids to Lexicon icon symbols available in
// clay-css/lib/images/icons/icons.svg. Entries without a Lexicon match are
// left out so Pintura keeps its own default icon for those tabs.

const LEXICON_ICON_BY_UTIL_ID: Record<string, string> = {
	annotate: 'pencil',
	crop: 'cut',
	decorate: 'star',
	filter: 'filter',
	finetune: 'adjust',
	frame: 'square',
	redact: 'hidden',
	resize: 'expand',
};

// Build an SVG string that references the Lexicon sprite Liferay already
// loads via the theme. Pintura's `UtilTab.icon` accepts raw SVG markup.

const getLexiconIconSvg = (symbol: string): string =>
	`<svg class="lexicon-icon lexicon-icon-${symbol}" focusable="false" role="presentation"><use href="${Liferay.Icons.spritemap}#${symbol}" /></svg>`;

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

	const editorProps = useMemo(() => {
		const defaults = getEditorDefaults();

		return {
			...defaults,
			enableButtonExport: false,
			enableNavigateHistory: true,
			enableZoomControls: true,
			layoutVerticalToolbarPreference: 'bottom' as const,
			locale: {
				...defaults.locale,

				// TODO Replace with Liferay.Language.get('adjust') once the
				// language key lands (it isn't in Language.properties yet).

				finetuneLabel: 'Adjust',
			},
			willRenderUtilTabs: (tabs: UtilTab[]) =>
				tabs.map((tab) => {
					const symbol = LEXICON_ICON_BY_UTIL_ID[tab.id];

					return symbol
						? {...tab, icon: getLexiconIconSvg(symbol)}
						: tab;
				}),
		};
	}, []);

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
			className="pintura-modal"
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
					{...editorProps}
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
