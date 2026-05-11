/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import {ContentLabel} from '@clayui/label';
import ClaySticker from '@clayui/sticker';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {Option, OptionsList} from './OptionsList';

function DesignLibraryScopedStyleBookSubtitle({designLibraryName, id}) {
	return (
		<ContentLabel
			aria-label={sub(
				Liferay.Language.get('style-book-from-x-design-library'),
				designLibraryName
			)}
			className="mt-1"
			displayType="content-5"
			id={id}
			large
			withClose={false}
		>
			<ContentLabel.ItemBefore>
				<ClaySticker className="bg-white rounded" inline size="xs">
					<ClayIcon symbol="books" />
				</ClaySticker>
			</ContentLabel.ItemBefore>

			<ContentLabel.ItemExpand>
				{designLibraryName}
			</ContentLabel.ItemExpand>
		</ContentLabel>
	);
}

function StyleBookOption({onSelectStyleBook, selectedStyleBook, styleBook}) {
	const isActive =
		selectedStyleBook?.styleBookEntryERC === styleBook.styleBookEntryERC &&
		(selectedStyleBook?.styleBookEntryScopeERC || '') ===
			(styleBook.styleBookEntryScopeERC || '');
	const isDesignLibraryScopedStyleBook = !!styleBook.styleBookEntryScopeERC;

	const subtitleId = useId();

	const onClick = () =>
		onSelectStyleBook({
			styleBookEntryERC: styleBook.styleBookEntryERC,
			styleBookEntryScopeERC: styleBook.styleBookEntryScopeERC,
		});

	if (!isDesignLibraryScopedStyleBook) {
		return (
			<Option
				{...styleBook}
				icon="magic"
				isActive={isActive}
				onClick={onClick}
			/>
		);
	}

	return (
		<Option
			{...styleBook}
			ariaDescribedBy={subtitleId}
			icon="book"
			iconClassName="page-editor__sidebar__design-options__design-library-icon"
			isActive={isActive}
			onClick={onClick}
			subtitle={
				<DesignLibraryScopedStyleBookSubtitle
					designLibraryName={styleBook.subtitle}
					id={subtitleId}
				/>
			}
		/>
	);
}

export default function StyleBooksList({
	onSelectStyleBook,
	selectedStyleBook,
	styleBooks,
	themeName,
}) {
	if (!styleBooks.length) {
		return (
			<ClayAlert className="mt-3" displayType="info">
				{Liferay.Language.get(
					'the-current-theme-does-not-support-style-books'
				)}
			</ClayAlert>
		);
	}

	const banner = (
		<ClayAlert className="mt-3" displayType="info" title="Info">
			{sub(
				Liferay.Language.get(
					'only-style-books-based-on-the-frontend-token-definition-provided-by-x-are-visible'
				),
				themeName
			)}
		</ClayAlert>
	);

	return (
		<OptionsList banner={banner} options={styleBooks}>
			{(styleBook) => (
				<StyleBookOption
					onSelectStyleBook={onSelectStyleBook}
					selectedStyleBook={selectedStyleBook}
					styleBook={styleBook}
				/>
			)}
		</OptionsList>
	);
}
