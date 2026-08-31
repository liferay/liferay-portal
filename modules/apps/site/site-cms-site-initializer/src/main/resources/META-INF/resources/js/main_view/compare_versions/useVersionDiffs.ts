/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import ApiHelper from '../../common/services/ApiHelper';

export type DiffType = 'additions' | 'removals';

export type Diffs = Record<string, string>;

type VersionDiffs = {source: Diffs; target: Diffs};

const COMPARE_VERSIONS_URL = '/o/cms/compare-versions';

export function useVersionDiffs({
	languageId,
	objectEntryId,
	sourceVersion,
	targetVersion,
}: {
	languageId: string;
	objectEntryId: number;
	sourceVersion: number | null;
	targetVersion: number | null;
}) {
	const [diffs, setDiffs] = useState<VersionDiffs | null>(null);

	useEffect(() => {
		setDiffs(null);

		if (sourceVersion === null || targetVersion === null) {
			return;
		}

		let stale = false;

		const getDiffs = async () => {
			const {data, error} = await ApiHelper.post<{
				diffs: VersionDiffs;
			}>(COMPARE_VERSIONS_URL, {
				languageId,
				objectEntryId,
				sourceVersion,
				targetVersion,
			});

			if (stale) {
				return;
			}

			setDiffs(error === null ? data.diffs : null);
		};

		getDiffs();

		return () => {
			stale = true;
		};
	}, [languageId, objectEntryId, sourceVersion, targetVersion]);

	return diffs;
}

export function injectContentDiffs(
	diffs: Diffs | null,
	diffType: DiffType,
	iframe: HTMLIFrameElement
) {
	const iframeDocument = iframe.contentDocument;

	if (!iframeDocument) {
		return;
	}

	removePreviousContentDiffs(iframeDocument);

	if (!diffs) {
		return;
	}

	iframeDocument.body.classList.add(`cms-compare-versions-${diffType}`);

	applyFieldDiffs(diffs, diffType, iframeDocument);

	hideEmptyElements(iframeDocument);
}

function applyFieldDiffs(
	diffs: Diffs,
	diffType: DiffType,
	iframeDocument: Document
) {
	const borderColorCssClass =
		diffType === 'additions' ? 'border-success' : 'border-danger';

	Object.entries(diffs).forEach(([fieldName, diffHTML]) => {
		const field =
			iframeDocument.querySelector(
				`[data-field-name="ObjectField_${fieldName}"]`
			) ??
			iframeDocument.querySelector(
				`[data-field-name="ObjectEntry_${fieldName}"]`
			);

		if (!field) {
			return;
		}

		const control = field.querySelector('.form-control');

		const container = iframeDocument.createElement('div');

		container.className = `${
			control?.className ?? 'form-control'
		} cms-compare-versions-diff`;

		// XSS: diffHTML is escaped by
		// ObjectEntryVersionFieldValueResolver.toDisplayValue, except rich
		// text, which is HTML by design

		container.innerHTML = diffHTML;

		const formGroup = field.querySelector('.form-group') ?? field;

		formGroup.appendChild(container);

		container
			.querySelectorAll('.cms-compare-versions-attachment')
			.forEach((image) => {
				image.classList.add(borderColorCssClass);

				formGroup.insertBefore(
					image.closest('[class*="diff-html"]') ?? image,
					container
				);
			});
	});
}

function hideEmptyElements(iframeDocument: Document) {
	iframeDocument
		.querySelectorAll<HTMLElement>(
			'.cms-compare-versions-diff :is(blockquote, figure, li, ol, table, ul)'
		)
		.forEach((element) => {
			if (
				!element.innerText.trim() &&
				!element.querySelector(
					'audio, embed, iframe, img, oembed, picture, svg, video'
				)
			) {
				element.style.display = 'none';
			}
		});
}

function removePreviousContentDiffs(iframeDocument: Document) {
	iframeDocument
		.querySelectorAll('.cms-compare-versions-diff')
		.forEach((element) => element.remove());
	iframeDocument
		.querySelectorAll('.cms-compare-versions-attachment')
		.forEach((element) =>
			(element.closest('[class*="diff-html"]') ?? element).remove()
		);

	iframeDocument.body.classList.remove(
		'cms-compare-versions-additions',
		'cms-compare-versions-removals'
	);
}
