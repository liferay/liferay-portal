/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import Avatar from './Avatar';

export default function Option({image, name}: {image?: string; name: string}) {
	return (
		<div className="object-field__assignee-option">
			<Avatar image={image} name={name} />

			<span>{name}</span>
		</div>
	);
}
