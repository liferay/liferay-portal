/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentSetModalContent} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';
import React from 'react';

type FragmentCollection = {fragmentCollectionId: number; name: string};

export type AddFragmentDesignLibraryModalContentProps = {
	addFragmentCollectionURL: string;
	addFragmentEntryURL: string;
	closeModal: () => void;
	fragmentCollections: Array<FragmentCollection>;
	fragmentType: number;
	mode: 'fragment' | 'set';
	namespace: string;
};

export default function AddFragmentDesignLibraryModalContent({
	addFragmentCollectionURL,
	addFragmentEntryURL,
	closeModal,
	fragmentCollections,
	fragmentType,
	mode,
	namespace,
}: AddFragmentDesignLibraryModalContentProps) {
	const submitFragmentEntry = (
		fragmentCollectionId: number,
		fragmentName?: string
	) => {
		const formData = new FormData();

		formData.append(
			`${namespace}fragmentCollectionId`,
			String(fragmentCollectionId)
		);

		formData.append(`${namespace}name`, fragmentName ?? '');

		formData.append(`${namespace}type`, String(fragmentType));

		fetch(addFragmentEntryURL, {body: formData, method: 'POST'})
			.then((response) => response.json())
			.then(({redirectURL}: {redirectURL?: string}) => {
				if (!redirectURL) {
					navigate(location.href);

					return;
				}

				navigate(
					addParams(
						{[`${namespace}redirect`]: location.href},
						redirectURL
					)
				);
			})
			.catch(() =>
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				})
			);
	};

	return (
		<FragmentSetModalContent
			addFragmentCollectionURL={addFragmentCollectionURL}
			allowCustomName={mode === 'fragment'}
			closeModal={closeModal}
			fragmentCollections={mode === 'fragment' ? fragmentCollections : []}
			onSubmitFragmentCollection={
				mode === 'fragment'
					? submitFragmentEntry
					: () => navigate(location.href)
			}
			portletNamespace={namespace}
		/>
	);
}
