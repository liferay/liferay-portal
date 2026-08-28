/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch, navigate} from 'frontend-js-web';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import OmniSearchResultHeader from './OmniSearchResultHeader';
import OmniSearchResultRow from './OmniSearchResultRow';
import useKeyboardNavigation, {Section} from './useKeyboardNavigation';

import '../css/OmniSearch.scss';

type OmniSearchResultItem = {
	description: string;
	icon: string;
	title: string;
	type: 'ENTRY';
	url: string;
};

type OmniSearchSection = {
	icon: string;
	omniSearchResults: OmniSearchResultItem[];
	title: string;
	type: 'SECTION';
};

export default function OmniSearch({resultsURL}: {resultsURL: string}) {
	const [loading, setLoading] = useState<boolean>(false);
	const [omniSearchSections, setOmniSearchSections] = useState<
		OmniSearchSection[] | null
	>(null);
	const [query, setQuery] = useState<string>('');
	const [visible, setVisible] = useState<boolean>(false);

	const inputRef = useRef<HTMLInputElement>(null);

	const {observer} = useModal({
		onClose: () => setVisible(false),
	});

	const sections: Section[] = useMemo(
		() =>
			(omniSearchSections ?? []).map((section) => ({
				icon: section.icon,
				items: section.omniSearchResults.map((result, index) => ({
					description: result.description,
					icon: result.icon,
					key: `${section.title}-${index}-${result.title}`,
					onClick: () => {
						if (result.url) {
							navigate(result.url);
						}
					},
					title: result.title,
				})),
				key: section.title,
				label: section.title,
			})),
		[omniSearchSections]
	);

	const {activeIndex, onInputKeyDown, sectionOffsets} = useKeyboardNavigation(
		sections,
		() => setVisible(true)
	);

	useEffect(() => {
		if (!visible) {
			return;
		}

		setQuery('');
		setOmniSearchSections(null);

		inputRef.current?.focus();

		const redirectFocusToInput = (event: FocusEvent) => {
			const input = inputRef.current;
			const target = event.target as HTMLElement;

			if (input && target !== input && target.matches('.modal-title')) {
				input.focus();
			}
		};

		document.addEventListener('focusin', redirectFocusToInput);

		return () => {
			document.removeEventListener('focusin', redirectFocusToInput);
		};
	}, [visible]);

	useEffect(() => {
		if (!visible) {
			return;
		}

		const trimmedQuery = query.trim();

		if (!trimmedQuery) {
			setLoading(false);
			setOmniSearchSections(null);

			return;
		}

		setLoading(true);

		const controller = new AbortController();

		const timeoutId = setTimeout(async () => {
			try {
				const response = await fetch(
					`${resultsURL}&keywords=${encodeURIComponent(
						trimmedQuery
					)}&redirect=${encodeURIComponent(window.location.href)}`,
					{cache: 'no-store', signal: controller.signal}
				);

				const data = await response.json();

				setOmniSearchSections(data ?? []);
			}
			catch (error) {
				if ((error as Error).name !== 'AbortError') {
					setOmniSearchSections([]);
				}
			}
			finally {
				setLoading(false);
			}
		}, 300);

		return () => {
			clearTimeout(timeoutId);
			controller.abort();
		};
	}, [resultsURL, query, visible]);

	return (
		<>
			<ClayButtonWithIcon
				aria-haspopup="dialog"
				aria-label={`${Liferay.Language.get('omni-search')} (Ctrl+K)`}
				className="control-menu-nav-link lfr-portal-tooltip"
				data-qa-id="omniSearch"
				displayType="unstyled"
				onClick={() => setVisible(true)}
				size="sm"
				symbol="search"
				title={`${Liferay.Language.get('omni-search')} (Ctrl+K)`}
			/>

			{visible && (
				<ClayModal
					className="cadmin omni-search-modal"
					observer={observer}
				>
					<ClayModal.Header>
						<ClayInput.Group>
							<ClayInput.GroupItem className="input-group-item-focusable">
								<ClayInput
									aria-activedescendant={
										activeIndex >= 0
											? `omniSearchOption${activeIndex}`
											: undefined
									}
									aria-autocomplete="list"
									aria-controls={
										sections.length
											? sections
													.map(
														(section) =>
															`omniSearchListbox${section.key}`
													)
													.join(' ')
											: undefined
									}
									aria-expanded={!!sections.length}
									className="form-control-lg input-group-inset input-group-inset-after input-group-inset-before omni-search-input"
									onChange={(event) =>
										setQuery(event.target.value)
									}
									onKeyDown={onInputKeyDown}
									placeholder={Liferay.Language.get('search')}
									ref={inputRef}
									role="combobox"
									type="text"
									value={query}
								/>

								<ClayInput.GroupInsetItem before tag="span">
									<ClayIcon
										className="text-secondary"
										symbol="search"
									/>
								</ClayInput.GroupInsetItem>

								<ClayInput.GroupInsetItem after tag="span">
									{loading && (
										<ClayLoadingIndicator size="sm" />
									)}
								</ClayInput.GroupInsetItem>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayModal.Header>

					<ClayModal.Body>
						{sections.map((section, sectionIndex) => {
							const offset = sectionOffsets[sectionIndex];

							return (
								<React.Fragment key={section.key}>
									<OmniSearchResultHeader
										icon={section.icon}
										label={section.label}
									/>

									<ul
										className="list-unstyled omni-search-list"
										id={`omniSearchListbox${section.key}`}
										role="listbox"
									>
										{section.items.map((item, index) => {
											const itemIndex = offset + index;

											return (
												<OmniSearchResultRow
													active={
														itemIndex ===
														activeIndex
													}
													id={`omniSearchOption${itemIndex}`}
													item={item}
													key={item.key}
													onClick={item.onClick}
												/>
											);
										})}
									</ul>
								</React.Fragment>
							);
						})}

						{!loading &&
							omniSearchSections !== null &&
							!sections.length && (
								<div className="c-my-4 text-center text-secondary">
									{Liferay.Language.get(
										'there-are-no-results'
									)}
								</div>
							)}
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
}
