/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import {navigate} from 'frontend-js-web';
import {ImageEditor} from 'item-selector-taglib';
import React, {useEffect, useRef, useState} from 'react';

export default function EditImageWithImageEditor({
	editImageURL,
	portletNamespace,
	redirectURL,
}) {
	const fileEntryIdRef = useRef();

	const [imageURL, setImageURL] = useState();
	const [mimeType, setMimeType] = useState('');
	const [showModal, setShowModal] = useState();

	const handleOnClose = () => {
		setShowModal(false);
	};

	const handleSave = (response) => {
		if (response?.success) {
			navigate(redirectURL);
		}
	};

	const {observer, onClose} = useModal({
		onClose: handleOnClose,
	});

	useEffect(() => {
		window[`${portletNamespace}editWithImageEditor`] = ({
			fileEntryId,
			imageURL,
			mimeType,
		}) => {
			fileEntryIdRef.current = fileEntryId;

			setImageURL(imageURL);
			setMimeType(mimeType);
			setShowModal(true);
		};

		return () => {
			window[`${portletNamespace}editWithImageEditor`] = () => {};
		};
	}, [portletNamespace]);

	return (
		<>
			{showModal && (
				<ClayModal
					className="image-editor-modal"
					observer={observer}
					size="full-screen"
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('editing-image')}
					</ClayModal.Header>

					<ClayModal.Body>
						{imageURL && (
							<ImageEditor
								imageId={fileEntryIdRef.current}
								imageSrc={imageURL}
								mimeType={mimeType}
								onCancel={onClose}
								onSave={handleSave}
								saveURL={editImageURL}
							/>
						)}
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
}
