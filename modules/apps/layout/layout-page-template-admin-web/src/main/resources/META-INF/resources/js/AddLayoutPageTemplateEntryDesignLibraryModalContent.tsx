/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PageTemplateModalContent} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';
import React from 'react';

import type {PageTemplateSet} from '@liferay/layout-js-components-web';

export type AddLayoutPageTemplateEntryDesignLibraryModalContentProps = {
	addLayoutPageTemplateEntryURL: string;
	addPageTemplateSetURL: string;
	closeModal: () => void;
	mode: 'page-template' | 'set';
	namespace: string;
	pageTemplateSets: Array<PageTemplateSet>;
};

export default function AddLayoutPageTemplateEntryDesignLibraryModalContent({
	addLayoutPageTemplateEntryURL,
	addPageTemplateSetURL,
	closeModal,
	mode,
	namespace,
	pageTemplateSets,
}: AddLayoutPageTemplateEntryDesignLibraryModalContentProps) {
	const submitLayoutPageTemplateEntry = async (
		pageTemplateSetId: number,
		pageTemplateName?: string
	) => {
		const formData = new FormData();

		formData.append(
			`${namespace}layoutPageTemplateCollectionId`,
			String(pageTemplateSetId)
		);

		formData.append(`${namespace}name`, pageTemplateName ?? '');

		try {
			const response = await fetch(addLayoutPageTemplateEntryURL, {
				body: formData,
				method: 'POST',
			});

			const {redirectURL}: {redirectURL?: string} = await response.json();

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
		}
		catch {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	return (
		<PageTemplateModalContent
			addPageTemplateSetURL={addPageTemplateSetURL}
			allowCustomName={mode === 'page-template'}
			closeModal={closeModal}
			namespace={namespace}
			onSubmitPageTemplateSet={
				mode === 'page-template'
					? submitLayoutPageTemplateEntry
					: () => navigate(location.href)
			}
			pageTemplateSets={mode === 'page-template' ? pageTemplateSets : []}
		/>
	);
}
