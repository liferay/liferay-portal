/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {LanguagePicker} from '@clayui/core';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import {Locale, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {Key, useContext, useMemo, useState} from 'react';

import AsyncButton from '../../../structure_builder/components/AsyncButton';
import {
	FindAndReplaceContext,
	useOpenDiscard,
} from '../contexts/FindAndReplaceContext';
import FindAndReplaceService from '../services/FindAndReplaceService';
import {filterItemsByLocale} from '../utils/filterItemsByLocale';
import {flatFields} from '../utils/flatFields';

export function Preview() {
	const {
		apply,
		discard,
		items: allItems,
		localeId,
		locales,
		previewId,
		replacement,
		search,
		setPreviewId,
		setView,
	} = useContext(FindAndReplaceContext);

	const openDiscard = useOpenDiscard();

	const [previewLocaleId, setPreviewLocaleId] = useState<Locale['id']>(
		locales[0]?.id
	);

	const [status, setStatus] = useState<'idle' | 'loading'>('idle');

	const items = useMemo(() => {
		if (!allItems) {
			return [];
		}

		if (localeId === 'all') {
			return allItems;
		}

		return filterItemsByLocale(allItems, localeId);
	}, [allItems, localeId]);

	const {index, item} = useMemo(() => {
		const index = items.findIndex(({id}) => id === previewId);

		return {index, item: items[index]};
	}, [items, previewId]);

	const fields = useMemo(
		() =>
			item
				? flatFields(
						{
							...item,
							fields: item.fields.filter(
								(field) => field.name !== 'title'
							),
						},
						localeId === 'all' ? previewLocaleId : localeId
					)
				: [],
		[item, localeId, previewLocaleId]
	);

	if (!item) {
		return null;
	}

	const hasPrevious = index > 0;
	const hasNext = !!items[index + 1];

	function goPrevious() {
		if (!hasPrevious) {
			return;
		}

		setPreviewId(items[index - 1].id);
	}

	function goNext() {
		if (!hasNext) {
			return;
		}

		setPreviewId(items[index + 1].id);
	}

	function discardChanges() {
		if (hasNext) {
			goNext();
		}
		else if (hasPrevious) {
			goPrevious();
		}

		discard(item.id);
	}

	async function applyChanges() {
		setStatus('loading');

		const {error} = await FindAndReplaceService.performSingleReplace({
			item,
			localeId,
			replacement,
			search,
		});

		if (error) {
			setStatus('idle');

			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			return;
		}

		openToast({
			message: sub(
				Liferay.Language.get('changes-applied-to-x'),
				Liferay.Util.escapeHTML(item.title)
			),
			type: 'success',
		});

		setStatus('idle');

		if (hasNext) {
			goNext();
		}
		else if (hasPrevious) {
			goPrevious();
		}

		apply(item.id);
	}

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				<div className="align-items-center c-gap-3 d-flex">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('back')}
						borderless
						className="text-secondary"
						displayType="unstyled"
						monospaced
						onClick={() => setView('summary')}
						size="sm"
						symbol="angle-left"
					/>

					{item.title}
				</div>
			</ClayModal.Header>

			<ClayModal.Body className="align-items-center cms-find-and-replace-preview d-flex flex-row">
				<div className="align-items-center d-flex justify-content-center">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('previous')}
						disabled={!hasPrevious}
						displayType="secondary"
						monospaced
						onClick={goPrevious}
						rounded
						symbol="angle-left"
					/>
				</div>

				<div className="d-flex flex-column flex-grow-1 h-100">
					{localeId === 'all' ? (
						<LanguagePicker
							classNamesTrigger="mb-5 flex-shrink-0"
							locales={locales}
							onSelectedLocaleChange={(id: Key) => {
								setPreviewLocaleId(id as Locale['id']);
							}}
							selectedLocaleId={previewLocaleId}
							small
						/>
					) : null}

					<p
						className={classNames('h2 mb-5 text-9', {
							'mt-4': localeId === 'all',
						})}
						dangerouslySetInnerHTML={{
							__html: formatContent(
								Liferay.Util.escapeHTML(item.title),
								search,
								replacement
							),
						}}
					/>

					<div>
						{fields.map(({label, value}, index) => (
							<div key={index}>
								<span className="font-weight-semi-bold text-3">
									{label}
								</span>

								<p
									className="mb-4"
									dangerouslySetInnerHTML={{
										__html: formatContent(
											Liferay.Util.escapeHTML(value),
											search,
											replacement
										),
									}}
								/>
							</div>
						))}
					</div>
				</div>

				<div className="align-items-center d-flex justify-content-center">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('next')}
						disabled={!hasNext}
						displayType="secondary"
						monospaced
						onClick={goNext}
						rounded
						symbol="angle-right"
					/>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				className="bg-primary-l3 cms-find-and-replace-preview__footer"
				first={
					<span className="text-secondary">
						{sub(
							Liferay.Language.get('x-of-x-assets'),
							index + 1,
							items.length
						)}
					</span>
				}
				last={
					<ClayButton.Group spaced>
						<ClayButton
							borderless
							displayType="secondary"
							onClick={openDiscard}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={discardChanges}
						>
							{Liferay.Language.get('discard-changes')}
						</ClayButton>

						<AsyncButton
							label={Liferay.Language.get('apply-changes')}
							onClick={applyChanges}
							size="regular"
							status={status}
						/>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

function formatContent(value: string, search: string, replacement: string) {
	return value.replaceAll(
		search,
		(match) =>
			`<span class="cms-find-and-replace-preview__search text-danger">
				${match}
			</span>
			<span class="cms-find-and-replace-preview__replacement font-weight-bold text-success">
				${replacement}
			</span>`
	);
}
