/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {debounce} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import '@liferay/document-library-preview-css';

import '../css/main.scss';

const KEY_CODE_ENTER = 13;

const KEY_CODE_ESC = 27;

/**
 * Valid list of keycodes
 * Includes backspace, tab, arrows, delete and numbers
 */
const VALID_KEY_CODES = [
	8, 9, 37, 38, 39, 40, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
];

/**
 * Milisecons between goToPage calls
 */
const WAIT_BETWEEN_GO_TO_PAGE = 250;

type DocumentPreviewerProps = {
	alt?: string;
	baseImageURL: string;
	initialPage?: number;
	totalPages: number;
};

type LoadedPage = {
	loaded: boolean;
	pagePromise: Promise<void>;
};

const DocumentPreviewer = ({
	alt = '',
	baseImageURL,
	initialPage = 1,
	totalPages,
}: DocumentPreviewerProps) => {
	const [currentPage, setCurrentPage] = useState(initialPage);
	const [currentPageLoading, setCurrentPageLoading] = useState(false);
	const [expanded, setExpanded] = useState(false);
	const [loadedPages, setLoadedPages] = useState<Record<number, LoadedPage>>({
		[currentPage]: {
			loaded: true,
			pagePromise: Promise.resolve(),
		},
	});
	const [showPageInput, setShowPageInput] = useState<boolean>(false);

	const imageContainerRef = useRef<HTMLDivElement>(null);
	const pageInputRef = useRef<HTMLInputElement>(null);
	const showPageInputButtonRef = useRef<HTMLButtonElement>(null);

	const nextPageDisabled = currentPage === totalPages;
	const previousPageDisabled = currentPage === 1;

	const isMounted = useIsMounted();

	useEffect(() => {
		if (showPageInput) {
			const timer = setTimeout(() => {
				if (isMounted() && pageInputRef.current) {
					pageInputRef.current.focus();
				}
			}, 100);

			return () => clearTimeout(timer);
		}
	}, [showPageInput, isMounted]);

	const createImageURL = useCallback(
		(page: number) => {
			const imageURL = new URL(baseImageURL);
			imageURL.searchParams.set('previewFileIndex', String(page));

			return imageURL.toString();
		},
		[baseImageURL]
	);

	const loadPage = useCallback(
		(page: number): Promise<void> => {
			let pagePromise = loadedPages[page]?.pagePromise;

			if (!pagePromise) {
				const image = new Image();
				image.src = createImageURL(page);

				pagePromise = image.decode().then(() => {
					setLoadedPages((prev) => ({
						...prev,
						[page]: {...prev[page], loaded: true},
					}));
				});

				setLoadedPages((prev) => ({
					...prev,
					[page]: {loaded: false, pagePromise},
				}));
			}

			return pagePromise;
		},
		[createImageURL, loadedPages]
	);

	const loadAdjacentPages = useCallback(
		(page: number, adjacentPageCount = 2) => {
			for (let i = 1; i <= adjacentPageCount; i++) {
				if (page + i <= totalPages) {
					loadPage(page + i);
				}
				if (page - i > 1) {
					loadPage(page - i);
				}
			}
		},
		[totalPages, loadPage]
	);

	const loadCurrentPage = debounce((page: number) => {
		loadPage(page)
			.then(() => {
				loadAdjacentPages(page);
				setCurrentPageLoading(false);
			})
			.catch(() => {
				setCurrentPageLoading(false);
			});
	}, WAIT_BETWEEN_GO_TO_PAGE);

	const goToPage = (page: number) => {
		if (!loadedPages[page] || !loadedPages[page].loaded) {
			setCurrentPageLoading(true);
			loadCurrentPage(page);
		}

		if (imageContainerRef.current) {
			imageContainerRef.current.scrollTop = 0;
		}

		setCurrentPage(page);
	};

	const processPageInput = (value: string) => {
		let pageNumber = Number.parseInt(value, 10);
		pageNumber = pageNumber
			? Math.min(Math.max(1, pageNumber), totalPages)
			: currentPage;

		goToPage(pageNumber);
	};

	const hidePageInput = (returnFocus = true) => {
		setShowPageInput(false);

		if (returnFocus) {
			setTimeout(() => {
				if (isMounted() && showPageInputButtonRef.current) {
					showPageInputButtonRef.current.focus();
				}
			}, 100);
		}
	};

	const handleBlurPageInput = (event: React.FocusEvent<HTMLInputElement>) => {
		processPageInput(event.currentTarget.value);
		hidePageInput(false);
	};

	const handleKeyDownPageInput = (
		event: React.KeyboardEvent<HTMLInputElement>
	) => {
		const code = event.keyCode || event.charCode;

		if (code === KEY_CODE_ENTER) {
			processPageInput(event.currentTarget.value);
			hidePageInput();
		}
		else if (code === KEY_CODE_ESC) {
			hidePageInput();
		}
		else if (VALID_KEY_CODES.indexOf(code) === -1) {
			event.preventDefault();
		}
	};

	useEffect(() => {
		loadAdjacentPages(initialPage);
	}, [initialPage, loadAdjacentPages]);

	useEffect(() => {
		setCurrentPage(initialPage);
	}, [initialPage]);

	return (
		<div className="preview-file">
			<div
				className="preview-file-container preview-file-max-height"
				ref={imageContainerRef}
			>
				{currentPageLoading ? (
					<ClayLoadingIndicator />
				) : (
					<img
						alt={alt}
						className={`preview-file-document ${
							!expanded ? 'preview-file-document-fit' : ''
						}`}
						src={createImageURL(currentPage)}
					/>
				)}
			</div>

			<div className="preview-toolbar-container">
				<ClayButton.Group className="floating-bar">
					<ClayButton.Group>
						<ClayButton
							aria-label={
								totalPages > 1
									? Liferay.Language.get(
											'click-to-jump-to-a-page'
										)
									: undefined
							}
							className="btn-floating-bar btn-floating-bar-text"
							disabled={totalPages === 1}
							onClick={() => {
								setShowPageInput(true);
							}}
							ref={showPageInputButtonRef}
							title={
								totalPages > 1
									? Liferay.Language.get(
											'click-to-jump-to-a-page'
										)
									: undefined
							}
						>
							{`${Liferay.Language.get(
								'page'
							)} ${currentPage} / ${totalPages}`}
						</ClayButton>

						{showPageInput && (
							<div className="floating-bar-input-wrapper">
								<input
									className="floating-bar-input form-control form-control-sm"
									max={totalPages}
									min="1"
									onBlur={handleBlurPageInput}
									onKeyDown={handleKeyDownPageInput}
									placeholder={Liferay.Language.get(
										'page-...'
									)}
									ref={pageInputRef}
									type="number"
								/>
							</div>
						)}
					</ClayButton.Group>

					<ClayButton
						aria-label={Liferay.Language.get('page-above')}
						className="btn-floating-bar"
						disabled={previousPageDisabled}
						monospaced
						onClick={() => {
							goToPage(currentPage - 1);
						}}
						title={Liferay.Language.get('page-above')}
					>
						<ClayIcon symbol="caret-top" />
					</ClayButton>

					<ClayButton
						aria-label={Liferay.Language.get('page-below')}
						className="btn-floating-bar"
						disabled={nextPageDisabled}
						monospaced
						onClick={() => {
							goToPage(currentPage + 1);
						}}
						title={Liferay.Language.get('page-below')}
					>
						<ClayIcon symbol="caret-bottom" />
					</ClayButton>

					<div className="separator-floating-bar"></div>

					<ClayButton
						aria-label={
							expanded
								? Liferay.Language.get('zoom-to-fit')
								: Liferay.Language.get('expand')
						}
						className="btn-floating-bar"
						monospaced
						onClick={() => {
							setExpanded((expanded) => !expanded);
						}}
						title={
							expanded
								? Liferay.Language.get('zoom-to-fit')
								: Liferay.Language.get('expand')
						}
					>
						<ClayIcon
							symbol={expanded ? 'autosize' : 'full-size'}
						/>
					</ClayButton>
				</ClayButton.Group>
			</div>
		</div>
	);
};

export {DocumentPreviewer};
