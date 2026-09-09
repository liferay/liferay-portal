/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import {
	ImageEditor,
	ImageEditorLoadError,
	disposeLoadedImage,
	liferayMessages,
	loadImage,
	setMessages,
} from '@liferay/frontend-js-image-editor-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

setMessages(liferayMessages);

const SAMPLE_URL = '/o/frontend-js-image-editor-sample-web/images/sample.jpg';

function demoSave({blob, fileName}) {
	const url = URL.createObjectURL(blob);

	const anchor = document.createElement('a');

	anchor.download = fileName;
	anchor.href = url;

	anchor.click();

	setTimeout(() => URL.revokeObjectURL(url), 10000);
}

function EditorModal({image, onClose}) {
	const {observer, onClose: closeModal} = useModal({onClose});

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
				withTitle
			>
				{Liferay.Language.get('editing-image')}
			</ClayModal.Header>

			<ClayModal.Body className="overflow-hidden p-0">
				<ImageEditor
					image={image}
					onClose={closeModal}
					onSave={demoSave}
					spritemap={Liferay.Icons.spritemap}
				/>
			</ClayModal.Body>
		</ClayModal>
	);
}

export function App() {
	const [error, setError] = useState(null);
	const [image, setImage] = useState(null);
	const [loading, setLoading] = useState(false);

	const abortControllerRef = useRef(null);
	const imageRef = useRef(null);

	useEffect(() => {
		imageRef.current = image;
	});

	useEffect(() => {
		return () => {
			abortControllerRef.current?.abort();

			if (imageRef.current) {
				disposeLoadedImage(imageRef.current);
			}
		};
	}, []);

	const close = (current) => {
		disposeLoadedImage(current);

		setImage(null);
	};

	const open = async () => {
		if (loading) {
			return;
		}

		setError(null);
		setLoading(true);

		const abortController = new AbortController();

		abortControllerRef.current = abortController;

		try {
			const response = await fetch(SAMPLE_URL, {
				signal: abortController.signal,
			});

			const loadedImage = await loadImage(
				await response.blob(),
				'sample.jpg'
			);

			// A load that finishes after the portlet unmounted has no
			// state to land in; release it instead of leaking its URL.

			if (abortController.signal.aborted) {
				disposeLoadedImage(loadedImage);

				return;
			}

			setImage(loadedImage);
		}
		catch (loadError) {
			if (abortController.signal.aborted) {
				return;
			}

			setError(
				loadError instanceof ImageEditorLoadError
					? loadError.message
					: 'The sample image did not load'
			);
		}
		finally {
			if (!abortController.signal.aborted) {
				setLoading(false);
			}
		}
	};

	return (
		<div className="p-4">

			{/*
				aria-disabled instead of disabled: disabling the focused
				button drops focus to the body, and the editor's focus trap
				would then return focus there instead of here on close.
			*/}

			<button
				aria-disabled={loading}
				className="btn btn-primary"
				onClick={open}
				type="button"
			>
				Edit sample image
			</button>

			{error && (
				<div className="alert alert-danger mt-3" role="alert">
					{error}
				</div>
			)}

			{image && (
				<EditorModal image={image} onClose={() => close(image)} />
			)}
		</div>
	);
}
