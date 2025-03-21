/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';
import {LocalesDropdown} from 'dynamic-data-mapping-form-field-type';
import {
	FieldChangeEventHandler,
	LocalizedValue,
} from 'dynamic-data-mapping-form-field-type/src/main/resources/META-INF/resources/types';
import {isEmptyObject} from 'dynamic-data-mapping-form-field-type/src/main/resources/META-INF/resources/util/basicJsUtils';
import {AvailableLocale} from 'dynamic-data-mapping-form-field-type/src/main/resources/META-INF/resources/util/localizable/LocalesDropdown';
import React, {useEffect, useState} from 'react';

import AttachmentBase, {
	AttachmentBaseProps,
	AttachmentFile,
} from './AttachmentBase';

export interface AttachmentLocalizedObjectFieldProps
	extends AttachmentBaseProps<string | LocalizedValue<string>> {
	availableLocales: AvailableLocale[];
	fieldName: string;
	fileEntryProperties: LocalizedValue<AttachmentFile>;
	onChange: FieldChangeEventHandler<LocalizedValue<string>>;
	value: LocalizedValue<string>;
}

export default function AttachmentLocalizedObjectField({
	availableLocales,
	fieldName,
	fileEntryProperties,
	onChange,
	value,
	...otherProps
}: AttachmentLocalizedObjectFieldProps) {
	const [attachment, setAttachment] =
		useState<LocalizedValue<AttachmentFile>>(fileEntryProperties);

	const {
		defaultLanguageId,
		editingLanguageId,
	}: {
		defaultLanguageId: Liferay.Language.Locale;
		editingLanguageId: Liferay.Language.Locale;
	} = useFormState();

	const getAttachment = () => {
		if (!attachment[editingLanguageId]) {
			return null;
		}

		return attachment[editingLanguageId] as AttachmentFile;
	};

	const handleAttachmentChange = (
		attachmentValue: AttachmentFile,
		fileId: string
	) => {
		const newValue = {
			...value,
			[editingLanguageId]: fileId,
		};

		onChange({target: {value: newValue}});

		setAttachment((previous) => {
			return {
				...previous,
				[editingLanguageId]: attachmentValue,
			};
		});
	};

	const handleDelete = () => {
		if (Object.hasOwn(attachment, editingLanguageId)) {
			const newAttachment = {...attachment};
			delete newAttachment[editingLanguageId];
			setAttachment(newAttachment);
		}

		if (Object.hasOwn(value, editingLanguageId)) {
			const newValue = {...value};
			delete newValue[editingLanguageId];
			onChange({target: {value: newValue}});
		}
	};

	useEffect(() => {
		if (
			!Object.hasOwn(attachment, editingLanguageId) &&
			!isEmptyObject(attachment)
		) {
			setAttachment((previous) => {
				return {
					...previous,
					...(attachment[defaultLanguageId] && {
						[editingLanguageId]: attachment[defaultLanguageId],
					}),
				};
			});
		}
	}, [attachment, defaultLanguageId, editingLanguageId]);

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem className="ddm-object-field-attachment-localized">
				<AttachmentBase
					{...otherProps}
					attachment={getAttachment()}
					handleDelete={handleDelete}
					onAttachmentChange={handleAttachmentChange}
					value={value}
				/>
			</ClayInput.GroupItem>

			<ClayInput.GroupItem shrink>
				<LocalesDropdown
					availableLocales={availableLocales}
					fieldName={fieldName}
					value={attachment}
				/>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}
