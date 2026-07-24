/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentSetModalContent} from '@liferay/layout-js-components-web';
import {openModal, openToast} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';
import React from 'react';
import {AddStyleBookModalContent} from 'style-book-web';

const FRAGMENT_TYPE_COMPONENT = 1;
const FRAGMENT_TYPE_INPUT = 3;

type FragmentCollection = {fragmentCollectionId: number; name: string};

type FrontendTokenDefinitionProvider = {
	name: string;
	themeId: string;
};

export interface DesignAssetCreationProps {
	addFragmentCollectionURL?: string;
	addFragmentEntryURL?: string;
	addStyleBookEntryURL?: string;
	canAddStyleBook?: boolean;
	canManageFragments?: boolean;
	fragmentCollections?: Array<FragmentCollection>;
	fragmentNamespace?: string;
	frontendTokenDefinitionProviders?: Array<FrontendTokenDefinitionProvider>;
	styleBookNamespace?: string;
}

export default function getDesignAssetCreationItems({
	addFragmentCollectionURL,
	addFragmentEntryURL,
	addStyleBookEntryURL,
	canAddStyleBook = false,
	canManageFragments = false,
	fragmentCollections = [],
	fragmentNamespace = '',
	frontendTokenDefinitionProviders = [],
	styleBookNamespace = '',
}: DesignAssetCreationProps): Array<{label: string; onClick: () => void}> {
	const primaryItems: Array<{label: string; onClick: () => void}> = [];

	if (canAddStyleBook && addStyleBookEntryURL) {
		primaryItems.push({
			label: Liferay.Language.get('new-style-book'),
			onClick: () =>
				openModal({
					contentComponent: ({closeModal}) =>
						AddStyleBookModalContent({
							addStyleBookEntryURL,
							closeModal,
							frontendTokenDefinitionProviders,
							namespace: styleBookNamespace,
						}),
				}),
		});
	}

	if (canManageFragments && addFragmentEntryURL && addFragmentCollectionURL) {
		const pushAddFragmentItem = (label: string, fragmentType: number) => {
			primaryItems.push({
				label,
				onClick: () =>
					openModal({
						contentComponent: ({closeModal}) => (
							<FragmentSetModalContent
								addFragmentCollectionURL={
									addFragmentCollectionURL
								}
								allowCustomName
								closeModal={closeModal}
								fragmentCollections={fragmentCollections}
								onSubmitFragmentCollection={(
									fragmentCollectionId: number,
									fragmentName?: string
								) => {
									const formData = new FormData();

									formData.append(
										`${fragmentNamespace}fragmentCollectionId`,
										String(fragmentCollectionId)
									);

									formData.append(
										`${fragmentNamespace}name`,
										fragmentName ?? ''
									);

									formData.append(
										`${fragmentNamespace}type`,
										String(fragmentType)
									);

									fetch(addFragmentEntryURL, {
										body: formData,
										method: 'POST',
									})
										.then((response) => response.json())
										.then(({redirectURL}) => {
											if (!redirectURL) {
												navigate(location.href);

												return;
											}

											navigate(
												addParams(
													{
														[`${fragmentNamespace}redirect`]:
															location.href,
													},
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
								}}
								portletNamespace={fragmentNamespace}
							/>
						),
					}),
			});
		};

		pushAddFragmentItem(
			Liferay.Language.get('new-basic-fragment'),
			FRAGMENT_TYPE_COMPONENT
		);

		pushAddFragmentItem(
			Liferay.Language.get('new-form-fragment'),
			FRAGMENT_TYPE_INPUT
		);
	}

	if (canManageFragments && addFragmentCollectionURL) {
		primaryItems.push({
			label: Liferay.Language.get('new-fragment-set'),
			onClick: () =>
				openModal({
					contentComponent: ({closeModal}) => (
						<FragmentSetModalContent
							addFragmentCollectionURL={addFragmentCollectionURL}
							closeModal={closeModal}
							fragmentCollections={[]}
							onSubmitFragmentCollection={() =>
								navigate(location.href)
							}
							portletNamespace={fragmentNamespace}
						/>
					),
				}),
		});
	}

	return primaryItems;
}
