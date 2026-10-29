/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AddDesignLibraryResourceModalContent from './components/AddDesignLibraryResourceModalContent';

interface Props {
	addMasterLayoutURL: string;
	closeModal: () => void;
	namespace: string;
}

export default function AddMasterPageDesignLibraryModalContent({
	addMasterLayoutURL,
	closeModal,
	namespace,
}: Props) {
	return (
		<AddDesignLibraryResourceModalContent
			closeModal={closeModal}
			formSubmitURL={addMasterLayoutURL}
			mappingTypes={[]}
			namespace={namespace}
			title={Liferay.Language.get('new-master-page')}
		/>
	);
}
