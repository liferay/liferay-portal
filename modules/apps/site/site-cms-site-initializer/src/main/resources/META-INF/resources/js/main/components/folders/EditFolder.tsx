/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface EditFolderProps {
	description?: string;
	name: string;
	space: string;
}

const EditFolder: React.FC<EditFolderProps> = ({description, name, space}) => {
	return (
		<h1>
			{' '}

			Edit Folder Page {name} {description} {space}{' '}
		</h1>
	);
};

export default EditFolder;
