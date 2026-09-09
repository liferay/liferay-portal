/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import {useEffect, useRef, useState} from 'react';

import {exportEditedImage} from '../imaging/exportImage';
import {LoadedImage} from '../imaging/loadImage';
import {EditState} from '../state/types';

export function useSaveController(
	image: LoadedImage,
	state: EditState,
	onSave: (
		result: {blob: Blob; fileName: string; state: EditState},
		signal: AbortSignal
	) => Promise<void> | void,
	announce: (message: string) => void,
	onClose: () => void
) {
	const [saving, setSaving] = useState(false);
	const [saveError, setSaveError] = useState(false);

	const saveControllerRef = useRef<AbortController | null>(null);

	useEffect(() => {
		return () => saveControllerRef.current?.abort();
	}, []);

	const handleSave = async () => {
		if (saveControllerRef.current) {
			return;
		}

		const controller = new AbortController();

		saveControllerRef.current = controller;

		setSaveError(false);
		setSaving(true);

		announce(Liferay.Language.get('saving'));

		try {
			const result = await exportEditedImage(image, state);

			if (controller.signal.aborted) {
				return;
			}

			await onSave({...result, state}, controller.signal);

			if (controller.signal.aborted) {
				return;
			}

			announce(
				sub(
					Liferay.Language.get('the-image-was-saved-as-x'),
					result.fileName
				)
			);

			onClose();
		}
		catch {
			if (!controller.signal.aborted) {
				setSaveError(true);

				announce(
					Liferay.Language.get(
						'unable-to-save-the-image-please-try-again'
					)
				);
			}
		}
		finally {
			saveControllerRef.current = null;

			if (!controller.signal.aborted) {
				setSaving(false);
			}
		}
	};

	return {handleSave, saveError, saving};
}
