/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {fetch, runScriptsInElement} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

export default function FormRelationshipAddButton({
	contentId,
	itemId,
	label,
	renderURL,
}: {
	contentId: string;
	itemId: string;
	label: Record<Liferay.Language.Locale, string>;
	renderURL: string;
}) {
	const value =
		label?.[Liferay.ThemeDisplay.getLanguageId()] ??
		label?.[Liferay.ThemeDisplay.getDefaultLanguageId()] ??
		Liferay.Language.get('add-new');

	const [disabled, setDisabled] = useState(false);

	const formRelationshipElement = useMemo(
		() =>
			document.querySelector(
				`[data-form-relationship-structure-item-content-id="${contentId}"]`
			)!.parentElement!,
		[contentId]
	);

	useEffect(() => {
		updateRemoveButtonVisibility(formRelationshipElement);
	}, [formRelationshipElement]);

	const onClick = () => {
		setDisabled(true);

		const numberOfChildren = getNumberOfChildren(formRelationshipElement);

		const url = new URL(renderURL);

		url.searchParams.set(
			'relatedItemExternalReferenceCode',
			'LAYOUT_DEFAULT_EXTERNAL_REFERENCE_CODE' +
				String(numberOfChildren).padStart(4, '0')
		);

		fetch(url.toString())
			.then((response) => response.text())
			.then((html) => {
				setDisabled(false);

				const div = document.createElement('div');

				div.innerHTML = html;

				const content = div.querySelector(
					`[data-layout-structure-item-id="${itemId}"] > [data-form-relationship-structure-item-content-id]`
				)!;

				if (formRelationshipElement) {
					const lastChild = formRelationshipElement.lastElementChild;

					formRelationshipElement.insertBefore(content, lastChild);

					runScriptsInElement(content as HTMLElement);

					updateRemoveButtonVisibility(formRelationshipElement);
				}
			});
	};

	return (
		<ClayButton
			aria-label={value ? '' : Liferay.Language.get('add-new')}
			borderless
			disabled={disabled}
			displayType="primary"
			onClick={onClick}
			size="sm"
		>
			<ClayIcon
				className={classNames('text-primary', {
					'mr-2': value,
				})}
				style={{transform: 'rotate(45deg)'}}
				symbol="times-circle-full"
			/>

			{value}
		</ClayButton>
	);
}

function getNumberOfChildren(formRelationshipElement: HTMLElement) {
	return formRelationshipElement.querySelectorAll(
		':scope > [data-form-relationship-structure-item-content-id]'
	).length;
}

const onRemoveButtonClick = (event: Event) => {
	const removeButton = event.currentTarget as HTMLElement;

	const formRelationshipContentElement = removeButton.closest(
		'[data-form-relationship-structure-item-content-id]'
	) as HTMLElement;

	const formRelationshipElement =
		formRelationshipContentElement.parentElement as HTMLElement;
	if (
		removeButton.dataset.classname &&
		removeButton.dataset.externalReferenceCode
	) {
		updateDeletedExternalReferenceCodes(
			removeButton.dataset.classname,
			removeButton.dataset.externalReferenceCode,
			formRelationshipElement
		);
	}

	formRelationshipContentElement.remove();

	updateRemoveButtonVisibility(formRelationshipElement);
};

function updateDeletedExternalReferenceCodes(
	className: string,
	externalReferenceCode: string,
	formRelationshipElement: HTMLElement
) {
	const form = formRelationshipElement.closest('form');

	if (!form) {
		return;
	}

	const input = getOrCreateDeletedExternalReferenceCodeInput(
		form
	) as HTMLInputElement;

	let currentValue: string[] = [];

	if (input.value) {
		currentValue = input.value.split(',');
	}

	currentValue.push(`${className}_${externalReferenceCode}`);

	input.value = currentValue.join(',');
}

function getOrCreateDeletedExternalReferenceCodeInput(form: HTMLFormElement) {
	const deletedExternalReferenceCodeInput = form.querySelector(
		'[name="deletedItemIdentifiers"]'
	);

	if (deletedExternalReferenceCodeInput) {
		return deletedExternalReferenceCodeInput;
	}

	const input = document.createElement('input');

	input.type = 'hidden';
	input.name = 'deletedItemIdentifiers';

	form.appendChild(input);

	return input;
}

function updateRemoveButtonVisibility(formRelationshipElement: HTMLElement) {
	const removeButtons = formRelationshipElement.querySelectorAll(
		':scope > [data-form-relationship-structure-item-content-id] > .lfr-form-relationship-remove-button button'
	);

	if (getNumberOfChildren(formRelationshipElement) > 1) {
		removeButtons.forEach((button) => {
			button.classList.remove('d-none');

			button.addEventListener('click', onRemoveButtonClick);
		});
	}
	else {
		removeButtons.forEach((button) => {
			button.classList.add('d-none');
			button.removeEventListener('click', onRemoveButtonClick);
		});
	}
}
