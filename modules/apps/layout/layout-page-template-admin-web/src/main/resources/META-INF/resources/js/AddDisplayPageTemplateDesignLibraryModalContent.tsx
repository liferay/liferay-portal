/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AddDesignLibraryResourceModalContent from './components/AddDesignLibraryResourceModalContent';
import {MappingType} from './types/MappingTypes';

interface Props {
	addDisplayPageURL: string;
	closeModal: () => void;
	mappingTypes: MappingType[];
	namespace: string;
}

export default function AddDisplayPageTemplateDesignLibraryModalContent({
	addDisplayPageURL,
	closeModal,
	mappingTypes,
	namespace,
}: Props) {
	return (
		<AddDesignLibraryResourceModalContent
			closeModal={closeModal}
			formSubmitURL={addDisplayPageURL}
			mappingTypes={mappingTypes}
			namespace={namespace}
			title={Liferay.Language.get('new-display-page-template')}
		/>
	);
}
