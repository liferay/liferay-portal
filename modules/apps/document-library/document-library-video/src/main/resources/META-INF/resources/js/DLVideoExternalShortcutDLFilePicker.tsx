/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import DLVideoExternalShortcutInput from './components/DLVideoExternalShortcutInput';
import DLVideoExternalShortcutPreview from './components/DLVideoExternalShortcutPreview';
import {useDLVideoExternalShortcutFields} from './utils/useDLVideoExternalShortcutFields';

type DLVideoExternalShortcutDLFilePickerProps = {
	dlVideoExternalShortcutHTML?: string;
	dlVideoExternalShortcutURL?: string;
	getDLVideoExternalShortcutFieldsURL: string;
	namespace: string;
	onFilePickCallback: string;
};

const DLVideoExternalShortcutDLFilePicker: React.FC<
	DLVideoExternalShortcutDLFilePickerProps
> = ({
	dlVideoExternalShortcutHTML = '',
	dlVideoExternalShortcutURL = '',
	getDLVideoExternalShortcutFieldsURL,
	namespace,
	onFilePickCallback,
}) => {
	const [url, setUrl] = useState(dlVideoExternalShortcutURL);

	const {error, fields, loading} = useDLVideoExternalShortcutFields({
		getDLVideoExternalShortcutFieldsURL,
		namespace,
		url,
	});

	useEffect(() => {
		if (fields) {
			(window as any)[onFilePickCallback](fields);
		}
	}, [fields, onFilePickCallback]);

	return (
		<>
			<DLVideoExternalShortcutInput onChange={setUrl} url={url} />
			<DLVideoExternalShortcutPreview
				error={error}
				framed
				loading={loading}
				small
				videoHTML={fields ? fields.HTML : dlVideoExternalShortcutHTML}
			/>
		</>
	);
};

export default DLVideoExternalShortcutDLFilePicker;
