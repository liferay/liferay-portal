/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import {NetworkStatus} from '@clayui/data-provider';
import ClayLabel from '@clayui/label';
import {openToast, stringUtils} from '@liferay/object-js-components-web';
import {FieldBase} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import './SelectObjectDefinition.scss';

const SEARCH_DELAY = 300;

const getObjectDefinitionLabel = ({
	defaultLanguageId,
	label,
	name,
}: Partial<ObjectDefinition>) =>
	stringUtils.getLocalizableLabel({
		fallbackLabel: name,
		fallbackLanguageId: defaultLanguageId as Liferay.Language.Locale,
		labels: label,
	});

interface SelectObjectDefinitionProps {
	disabled?: boolean;
	error?: string;
	initialValue?: string;
	label?: string;
	objectDefinition1?: Partial<ObjectDefinition>;
	reverseOrder: boolean;
	setValues: (values: Partial<ObjectRelationship>) => void;
}

export default function SelectObjectDefinition({
	disabled,
	error,
	initialValue,
	label,
	objectDefinition1,
	reverseOrder,
	setValues,
}: SelectObjectDefinitionProps) {
	const [loadedObjectDefinitions, setLoadedObjectDefinitions] = useState<
		Partial<ObjectDefinition>[]
	>([]);
	const [networkStatus, setNetworkStatus] = useState(NetworkStatus.Unused);
	const [totalCount, setTotalCount] = useState(0);
	const [value, setValue] = useState(initialValue ?? '');
	const [search, setSearch] = useState('');

	const lastPageRef = useRef(0);
	const loadedPageRef = useRef(0);
	const requestRef = useRef(0);

	const getPageURL = useCallback(
		(page: number) => {
			const url = new URL(
				`${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/object-admin/v1.0/object-definitions`
			);

			url.searchParams.set('page', String(page));
			url.searchParams.set('search', search);
			url.searchParams.set('sort', 'label:asc');

			return url.toString();
		},
		[search]
	);

	const fetchPage = useCallback(
		async (page: number) => {
			const request = ++requestRef.current;

			setNetworkStatus(NetworkStatus.Loading);

			try {
				const response = await fetch(getPageURL(page), {
					credentials: 'include',
					headers: new Headers({'x-csrf-token': Liferay.authToken}),
					method: 'GET',
				});

				const {items, lastPage, totalCount} = await response.json();

				if (request !== requestRef.current) {
					return;
				}

				lastPageRef.current = lastPage;
				loadedPageRef.current = page;

				setTotalCount(totalCount);
				setLoadedObjectDefinitions((previousObjectDefinitions) =>
					page === 1
						? items
						: [...previousObjectDefinitions, ...items]
				);
			}
			catch (error) {
				if (request === requestRef.current) {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				}
			}
			finally {
				if (request === requestRef.current) {
					setNetworkStatus(NetworkStatus.Unused);
				}
			}
		},
		[getPageURL]
	);

	useEffect(
		() => () => {
			requestRef.current++;
		},
		[]
	);

	const loadMore = useCallback(async () => {
		if (
			networkStatus === NetworkStatus.Loading ||
			loadedPageRef.current >= lastPageRef.current
		) {
			return;
		}

		await fetchPage(loadedPageRef.current + 1);
	}, [fetchPage, networkStatus]);

	useEffect(() => {
		const timeout = setTimeout(() => fetchPage(1), SEARCH_DELAY);

		return () => clearTimeout(timeout);
	}, [fetchPage]);

	const objectDefinitions = useMemo(
		() =>
			loadedObjectDefinitions.filter(
				({modifiable, parameterRequired, storageType}) =>
					(objectDefinition1?.modifiable || modifiable) &&
					(!Liferay.FeatureFlags['LPS-135430'] ||
						storageType === 'default') &&
					!parameterRequired
			),
		[loadedObjectDefinitions, objectDefinition1]
	);

	const loadedCount = objectDefinitions.length;

	const selectableTotalCount =
		totalCount - (loadedObjectDefinitions.length - loadedCount);

	useEffect(() => {
		setValue(initialValue ?? '');
	}, [initialValue]);

	return (
		<FieldBase
			disabled={disabled}
			errorMessage={error}
			id="objectRelationshipSelectObjectDefinition"
			label={label}
			required
		>
			<Autocomplete
				aria-label={label}
				caption={
					selectableTotalCount ? (
						<span aria-hidden="true">
							{sub(Liferay.Language.get('showing-x-of-x-items'), [
								loadedCount,
								selectableTotalCount,
							])}
						</span>
					) : undefined
				}
				disabled={disabled}
				filterKey={getObjectDefinitionLabel}
				items={objectDefinitions}
				loadingState={networkStatus}
				menuTrigger="focus"
				messages={{
					infiniteScrollInitialLoad: Liferay.Language.get(
						'x-item-loaded-reach-the-last-item-to-load-more'
					),
					infiniteScrollInitialLoadPlural: Liferay.Language.get(
						'x-items-loaded-reach-the-last-item-to-load-more'
					),
					infiniteScrollOnLoad:
						Liferay.Language.get('loading-more-items'),
					infiniteScrollOnLoaded: Liferay.Language.get(
						'showing-x-of-x-items'
					).replace('{1}', String(selectableTotalCount)),
					infiniteScrollOnLoadedPlural: Liferay.Language.get(
						'showing-x-of-x-items'
					).replace('{1}', String(selectableTotalCount)),
					loading: Liferay.Language.get('loading...'),
					notFound: Liferay.Language.get('no-results-found'),
				}}
				onChange={(newValue) => {
					setValue(newValue);
					setSearch(newValue);
				}}
				onItemsChange={() => {}}
				onLoadMore={loadMore}
				placeholder={Liferay.Language.get(
					'search-for-an-object-definition'
				)}
				value={value}
			>
				{(item) => {
					const label = getObjectDefinitionLabel(item);

					return (
						<Autocomplete.Item
							key={item.externalReferenceCode}
							onClick={() => {
								if (!reverseOrder) {
									setValues({
										objectDefinitionExternalReferenceCode2:
											item.externalReferenceCode,
										objectDefinitionId2: item.id,
										objectDefinitionName2: item.name,
									});
								}
								else {
									setValues({
										objectDefinitionExternalReferenceCode1:
											item.externalReferenceCode,
										objectDefinitionId1: item.id,
									});
								}

								setValue(label);
								setSearch('');
							}}
							textValue={label}
						>
							<div className="lfr-objects__select-object-definition-option">
								<div>{label}</div>

								<ClayLabel
									displayType={
										item.system ? 'info' : 'warning'
									}
								>
									{item.system
										? Liferay.Language.get('system')
										: Liferay.Language.get('custom')}
								</ClayLabel>
							</div>
						</Autocomplete.Item>
					);
				}}
			</Autocomplete>
		</FieldBase>
	);
}
