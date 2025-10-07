/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import FieldWrapper from './FieldWrapper';

export type TFieldFile = {
	errorMessage?: string;
	fieldId: string;
	label: string;
	onFileChange?: (file: File | null) => void;
	validExtensions: string;
};

const FieldFile = ({
	errorMessage,
	fieldId,
	label,
	onFileChange,
	validExtensions,
}: TFieldFile) => {
	const fileInputRef = useRef<HTMLInputElement | null>(null);
	const [file, setFile] = useState<File | null>(null);

	const resetFileInput = () => {
		if (fileInputRef && fileInputRef.current) {
			fileInputRef.current.value = '';
		}

		setFile(null);
	};

	const onChangeClick = () => {
		fileInputRef.current?.click();

		resetFileInput();
	};

	const handleFileInputChange = async ({
		target,
	}: React.ChangeEvent<HTMLInputElement>) => {
		if (
			!target.files ||
			target.files?.length === 0 ||
			!target.files[0].name.endsWith(validExtensions)
		) {
			return;
		}

		setFile(target.files[0]);
	};

	useEffect(() => {
		if (onFileChange) {
			onFileChange(file);
		}
	}, [file, onFileChange]);

	return (
		<FieldWrapper
			errorMessage={errorMessage}
			fieldId={fieldId}
			label={label}
		>
			<ClayInput.Group>
				<ClayInput.GroupItem>
					<ClayInput id={fieldId} value={file?.name || ''} />

					<input
						accept={validExtensions}
						className="d-none"
						onChange={handleFileInputChange}
						ref={fileInputRef}
						type="file"
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem shrink>
					{file ? (
						<>
							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('change-x'),
									Liferay.Language.get('file')
								)}
								className="lfr-portal-tooltip"
								displayType="secondary"
								onClick={onChangeClick}
								symbol="change"
								title={sub(
									Liferay.Language.get('change-x'),
									Liferay.Language.get('file')
								)}
								type="button"
							/>

							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('remove-x'),
									Liferay.Language.get('file')
								)}
								className="lfr-portal-tooltip"
								displayType="unstyled"
								onClick={resetFileInput}
								symbol="trash"
								title={sub(
									Liferay.Language.get('remove-x'),
									Liferay.Language.get('file')
								)}
								type="button"
							/>
						</>
					) : (
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('add')}
							className="lfr-portal-tooltip"
							displayType="secondary"
							onClick={() => fileInputRef.current?.click()}
							symbol="plus"
							title={Liferay.Language.get('add')}
							type="button"
						/>
					)}
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</FieldWrapper>
	);
};

export default FieldFile;
