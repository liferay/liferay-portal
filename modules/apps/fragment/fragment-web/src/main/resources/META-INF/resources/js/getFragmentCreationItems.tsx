/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentSetModalContent} from '@liferay/layout-js-components-web';
import {openModal, openToast} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';
import React from 'react';

const FRAGMENT_TYPE_COMPONENT = 1;
const FRAGMENT_TYPE_INPUT = 3;

type FragmentCollection = {fragmentCollectionId: number; name: string};

type Props = {
	addFragmentCollectionURL: string;
	addFragmentEntryURL: string;
	fragmentCollections?: Array<FragmentCollection>;
	namespace: string;
};

export default function getFragmentCreationItems({
	addFragmentCollectionURL,
	addFragmentEntryURL,
	fragmentCollections = [],
	namespace,
}: Props): Array<{label: string; onClick: () => void}> {
	const submitFragmentEntry = (
		fragmentCollectionId: number,
		fragmentName: string | undefined,
		fragmentType: number
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
			.then(({redirectURL}) => {
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

	const addFragmentItem = (label: string, fragmentType: number) => ({
		label,
		onClick: () =>
			openModal({
				contentComponent: ({closeModal}: {closeModal: () => void}) => (
					<FragmentSetModalContent
						addFragmentCollectionURL={addFragmentCollectionURL}
						allowCustomName
						closeModal={closeModal}
						fragmentCollections={fragmentCollections}
						onSubmitFragmentCollection={(
							fragmentCollectionId: number,
							fragmentName?: string
						) =>
							submitFragmentEntry(
								fragmentCollectionId,
								fragmentName,
								fragmentType
							)
						}
						portletNamespace={namespace}
					/>
				),
			}),
	});

	return [
		addFragmentItem(
			Liferay.Language.get('new-basic-fragment'),
			FRAGMENT_TYPE_COMPONENT
		),
		addFragmentItem(
			Liferay.Language.get('new-form-fragment'),
			FRAGMENT_TYPE_INPUT
		),
		{
			label: Liferay.Language.get('new-fragment-set'),
			onClick: () =>
				openModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<FragmentSetModalContent
							addFragmentCollectionURL={addFragmentCollectionURL}
							closeModal={closeModal}
							fragmentCollections={[]}
							onSubmitFragmentCollection={() =>
								navigate(location.href)
							}
							portletNamespace={namespace}
						/>
					),
				}),
		},
	];
}
