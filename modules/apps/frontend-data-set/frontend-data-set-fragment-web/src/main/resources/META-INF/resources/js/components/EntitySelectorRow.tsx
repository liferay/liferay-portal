/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {openSelectionModal} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React from 'react';

import {EMappingMode, IContentMappedTokenValue} from '../tokenMapping';

const EDITOR_PORTLET_ID =
	'com_liferay_layout_content_page_editor_web_internal_portlet_ContentPageEditorPortlet';

const ITEM_SELECTOR_URL_RESOURCE_COMMAND =
	'/frontend_data_set_fragment/get_info_item_selector_url';

function buildResourceURL(resourceCommand: string): string {
	const url = new URL(window.location.href);

	const params = new URLSearchParams();

	url.searchParams.forEach((paramValue, paramKey) => {
		if (!paramKey.startsWith('p_p_') && !paramKey.startsWith('_')) {
			params.append(paramKey, paramValue);
		}
	});

	params.set('p_p_id', EDITOR_PORTLET_ID);
	params.set('p_p_lifecycle', '2');
	params.set('p_p_resource_id', resourceCommand);
	params.set('p_p_state', 'normal');

	if (Liferay.authToken) {
		params.set('p_auth', Liferay.authToken);
	}

	url.search = params.toString();

	return url.toString();
}

function getSelectedValue(selection: any) {
	if (!selection) {
		return null;
	}

	if (typeof selection.value === 'string') {
		try {
			return JSON.parse(selection.value);
		}
		catch (error) {
			return null;
		}
	}

	if (selection.value && typeof selection.value === 'object') {
		return selection.value;
	}

	const [selectedValue] = Object.values(selection);

	return selectedValue ?? null;
}

async function openInfoItemSelector(
	handleSelectedEntity: (selected: any) => void
) {
	const response = await fetch(
		buildResourceURL(ITEM_SELECTOR_URL_RESOURCE_COMMAND)
	);

	if (!response.ok) {
		return;
	}

	const {eventName, url} = (await response.json()) as {
		eventName: string;
		url: string;
	};

	if (!url) {
		return;
	}

	const relayedEventName = `${eventName}NotEmpty`;

	const eventHandler = Liferay.on(eventName, (event: any) => {
		const selection = event.data || event;

		if (!selection || !Object.keys(selection).length) {
			return;
		}

		Liferay.fire(relayedEventName, {data: selection});
	});

	openSelectionModal({
		onClose: () => eventHandler.detach(),
		onSelect: (selection: any) => {
			const selectedValue = getSelectedValue(selection);

			if (!selectedValue) {
				return;
			}

			handleSelectedEntity(selectedValue);
		},
		selectEventName: relayedEventName,
		title: Liferay.Language.get('select-an-entity'),
		url,
	});
}

interface IProps {
	entity: IContentMappedTokenValue;
	onEntityChange: (entity: IContentMappedTokenValue) => void;
	onEntityRemove: () => void;
}

export default function EntitySelectorRow({
	entity,
	onEntityChange,
	onEntityRemove,
}: IProps) {
	const entityLabel = Liferay.Language.get('entity');
	const isEntitySelected = !!entity.className;

	const selectButtonIcon = isEntitySelected ? 'change' : 'plus';

	const selectButtonLabel = sub(
		isEntitySelected
			? Liferay.Language.get('change-x')
			: Liferay.Language.get('select-x'),
		entityLabel
	);

	const removeButtonLabel = sub(
		Liferay.Language.get('remove-x'),
		entityLabel
	);

	const displayValue = isEntitySelected
		? entity.title ||
			(entity.fieldId === 'externalReferenceCode'
				? entity.externalReferenceCode
				: entity.classPK) ||
			''
		: '';

	const handleSelectButton = () => {
		openInfoItemSelector((selected) => {
			onEntityChange({
				className: selected.className,
				classPK: String(selected.classPK ?? ''),
				externalReferenceCode: selected.externalReferenceCode ?? '',
				fieldId: entity.fieldId,
				mappingMode: EMappingMode.CONTENT,
				title: selected.title,
			});
		});
	};

	return (
		<ClayForm.Group>
			<label>{entityLabel}</label>

			<ClayInput.Group small>
				<ClayInput.GroupItem>
					<ClayInput
						placeholder={sub(
							Liferay.Language.get('no-x-selected'),
							entityLabel
						)}
						readOnly
						sizing="sm"
						type="text"
						value={displayValue}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem shrink>
					<ClayButtonWithIcon
						aria-label={selectButtonLabel}
						displayType="secondary"
						onClick={handleSelectButton}
						size="sm"
						symbol={selectButtonIcon}
						title={selectButtonLabel}
					/>
				</ClayInput.GroupItem>

				{isEntitySelected && (
					<ClayInput.GroupItem shrink>
						<ClayButtonWithIcon
							aria-label={removeButtonLabel}
							displayType="secondary"
							onClick={onEntityRemove}
							size="sm"
							symbol="trash"
							title={removeButtonLabel}
						/>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</ClayForm.Group>
	);
}
