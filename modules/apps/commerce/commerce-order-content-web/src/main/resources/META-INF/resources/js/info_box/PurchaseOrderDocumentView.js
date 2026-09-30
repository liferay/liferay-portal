/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {CommerceServiceProvider} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import {isEditable} from '../util';

function getBase64(file) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = () => resolve(reader.result.split(',').pop());
		reader.onerror = (error) => reject(error);
	});
}

const PurchaseOrderDocumentView = ({
	additionalProps,
	buttonDisplayType,
	elementId,
	field,
	fieldValue,
	hasUpdatePermission,
	isOpen,
	label,
	namespace,
	orderId,
	readOnly,
	spritemap,
}) => {
	const [downloadURL, setDownloadURL] = useState(
		additionalProps?.downloadURL ? additionalProps?.downloadURL : null
	);
	const inputFileRef = useRef(null);
	const [inputValue, setInputValue] = useState(
		additionalProps?.value ? additionalProps?.value : null
	);
	const [isOwner, setIsOwner] = useState(additionalProps?.isOwner);
	const [value, setValue] = useState(fieldValue);

	const refreshAttachmentsTable = () => {
		if (additionalProps?.fdsId) {
			window.top.Liferay.fire('fds-update-display', {
				id: additionalProps.fdsId,
			});
		}
	};

	const addAttachment = async (file) => {
		const payload = {
			attachment: await getBase64(file),
			title: file.name,
			type: 'purchaseOrderDocument',
		};

		CommerceServiceProvider.DeliveryCartAPI('v1')
			.addAttachment(orderId, payload)
			.then((response) => {
				setDownloadURL(response.url);
				setInputValue(response.id);
				setIsOwner(true);
				setValue(response.title);

				refreshAttachmentsTable();
			})
			.catch((error) => {
				openToast({
					message:
						error.message ||
						Liferay.Language.get('an-unexpected-error-occurred'),
					type: 'danger',
				});
			});
	};

	const deleteAttachment = async (attachmentId) => {
		return CommerceServiceProvider.DeliveryCartAPI('v1')
			.deleteAttachment(orderId, attachmentId)
			.then(() => {
				setDownloadURL(null);
				setInputValue(null);
				setValue('');

				refreshAttachmentsTable();
			})
			.catch((error) => {
				openToast({
					message:
						error.message ||
						Liferay.Language.get('an-unexpected-error-occurred'),
					type: 'danger',
				});
			});
	};

	const handleChangeFile = async (event) => {
		event.preventDefault();
		event.stopPropagation();

		const file = event.target.files[0];

		if (inputValue) {
			deleteAttachment(inputValue).then(async () => {
				await addAttachment(file);
			});
		}
		else {
			await addAttachment(file);
		}
	};

	const handleDeleteFile = async (event) => {
		event.preventDefault();

		await deleteAttachment(inputValue);
	};

	const handleFileChooser = async (event) => {
		event.preventDefault();

		inputFileRef.current.click();
	};

	return (
		<div className={`${namespace}info-box my-3`} id={elementId}>
			{hasUpdatePermission && !readOnly && isEditable(field, isOpen) ? (
				<input
					className="d-none"
					id="file"
					onChange={handleChangeFile.bind(this)}
					ref={inputFileRef}
					type="file"
				/>
			) : null}

			<div className="align-items-center d-flex">
				{label ? (
					<div className="h5 info-box-label m-0">{label}</div>
				) : null}

				{hasUpdatePermission &&
				!readOnly &&
				!value &&
				isEditable(field, isOpen) ? (
					<ClayButton
						aria-expanded={isOpen}
						aria-haspopup="dialog"
						aria-label={sub(Liferay.Language.get('add-x'), label)}
						className="ml-2"
						data-qa-id={`${field}-infoBoxButton`}
						displayType={buttonDisplayType}
						onClick={handleFileChooser.bind(this)}
						size="xs"
					>
						{Liferay.Language.get('add')}
					</ClayButton>
				) : null}
			</div>

			<div>
				<p className="d-flex info-box-value mt-1">
					{value && (
						<ClayLink
							className="flex-grow-1 text-truncate"
							href={downloadURL}
							target="_blank"
						>
							<ClayIcon
								className="mr-2"
								spritemap={spritemap}
								symbol="document-default"
							/>

							{value}
						</ClayLink>
					)}

					{hasUpdatePermission &&
					!readOnly &&
					value &&
					isEditable(field, isOpen) &&
					isOwner ? (
						<ClayButton.Group className="flex-nowrap">
							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('edit-x'),
									label
								)}
								data-qa-id={`${field}-infoBoxEditButton`}
								displayType="link"
								onClick={handleFileChooser.bind(this)}
								size="xs"
								spritemap={spritemap}
								symbol="change"
								title={sub(
									Liferay.Language.get('edit-x'),
									label
								)}
							/>

							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('delete-x'),
									label
								)}
								data-qa-id={`${field}-infoBoxDeleteButton`}
								displayType="link"
								onClick={handleDeleteFile.bind(this)}
								size="xs"
								spritemap={spritemap}
								symbol="trash"
								title={sub(
									Liferay.Language.get('delete-x'),
									label
								)}
							/>
						</ClayButton.Group>
					) : null}
				</p>
			</div>
		</div>
	);
};

export default PurchaseOrderDocumentView;
