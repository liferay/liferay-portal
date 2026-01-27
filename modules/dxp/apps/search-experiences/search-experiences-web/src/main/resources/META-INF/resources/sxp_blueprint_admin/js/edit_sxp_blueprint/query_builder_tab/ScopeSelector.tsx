/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Body, Cell, Head, Row, Table} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {openSelectionModal} from 'frontend-js-components-web';
import React, {useContext, useMemo, useState} from 'react';

import CustomPanel from '../../shared/CustomPanel';

// @ts-ignore

import ThemeContext from '../../shared/ThemeContext';
import {ACTIVE, INACTIVE, STATUS} from '../../utils/constants';
import {IScope, ISorting} from '../../utils/types';

export default function ScopeSelector({
	scope,
	setScope,
}: {
	scope: IScope[];
	setScope: (scope: IScope[]) => void;
}) {
	const [sort, setSort] = useState<ISorting | null>();

	const {
		namespace,
		selectScopeURL,
	}: {namespace: string; selectScopeURL: string} = useContext(ThemeContext);

	const getScopeItemTypeLabel = (type: string) => {
		if (type === '1') {
			return Liferay.Language.get('space');
		}

		if (type === '0') {
			return Liferay.Language.get('asset-library');
		}

		return Liferay.Language.get('site');
	};

	const filteredScope = useMemo(() => {
		if (!sort) {
			return scope;
		}

		return scope.slice().sort((a, b) => {
			let aValue = a[sort.column as keyof IScope];
			let bValue = b[sort.column as keyof IScope];

			// If sorting by type, compare the labels instead of the raw IDs

			if (sort.column === 'type') {
				aValue = getScopeItemTypeLabel(a.type);
				bValue = getScopeItemTypeLabel(b.type);
			}

			// Sort using Intl.Collator JS object to handle string and
			// numeric comparison, keeping in mind of current locale.

			let comparisonResult = new Intl.Collator(
				Liferay.ThemeDisplay.getBCP47LanguageId(),
				{numeric: true}
			).compare(aValue, bValue);

			// If the sorting direction is descending, invert the value

			if (sort.direction === 'descending') {
				comparisonResult *= -1;
			}

			return comparisonResult;
		});
	}, [sort, scope]);

	const _handleSelectScope = () => {
		openSelectionModal({
			id: `${namespace}selectScope`,
			onSelect: (selectedItem: {
				groupdepotentrytype: string;
				groupdescriptivename: string;
				groupexternalreferencecode: string;
				groupid: string;
				groupscopelabel: string;
			}) => {
				if (!selectedItem) {
					return;
				}

				if (
					scope.find(
						(item) =>
							item.externalReferenceCode ===
							selectedItem.groupexternalreferencecode
					)
				) {
					return;
				}

				setScope([
					...scope,
					{
						externalReferenceCode:
							selectedItem.groupexternalreferencecode,
						name: selectedItem.groupdescriptivename,
						status: STATUS.ACTIVE,
						type: selectedItem.groupdepotentrytype,
					},
				]);
			},
			selectEventName: `${namespace}selectScope`,
			title: Liferay.Language.get('select-scope'),
			url: selectScopeURL,
		});
	};

	const _handleRemoveScope = (externalReferenceCode: string) => {
		setScope(
			scope.filter(
				(item) => item.externalReferenceCode !== externalReferenceCode
			)
		);
	};

	return (
		<CustomPanel
			classNames="scope-selector"
			title={Liferay.Language.get('scope')}
		>
			<>
				<span className="text-4 text-secondary">
					{Liferay.Language.get('scope-selector-description')}
				</span>

				<div className="c-mt-4">
					<ClayButton
						displayType="secondary"
						onClick={_handleSelectScope}
					>
						<span className="inline-item inline-item-before">
							<ClayIcon symbol="plus" />
						</span>

						{Liferay.Language.get('select-scope')}
					</ClayButton>
				</div>

				{!!scope.length && (
					<Table
						columnsVisibility={false}
						onSortChange={setSort}
						sort={sort}
					>
						<Head
							items={[
								{
									id: 'name',
									name: Liferay.Language.get('name'),
									sortable: true,
								},
								{
									id: 'type',
									name: Liferay.Language.get('type'),
									sortable: true,
								},
								{
									id: 'status',
									name: Liferay.Language.get('status'),
									sortable: true,
									width: '20%',
								},
								{
									id: 'options',
									name: Liferay.Language.get('options'),
									sortable: false,
									width: '100px',
								},
							]}
						>
							{(column) => (
								<Cell
									key={column.id}
									sortable={column.sortable}
									width={column.width}
								>
									{column.name}
								</Cell>
							)}
						</Head>

						<Body>
							{filteredScope.map((scopeItem, index) => (
								<Row key={index}>
									<Cell>{scopeItem.name}</Cell>

									<Cell>
										{getScopeItemTypeLabel(scopeItem.type)}
									</Cell>

									<Cell>
										{scopeItem.status === STATUS.ACTIVE ? (
											<ClayLabel displayType="success">
												{ACTIVE}
											</ClayLabel>
										) : (
											<ClayLabel displayType="secondary">
												{INACTIVE}
											</ClayLabel>
										)}
									</Cell>

									<Cell align="center">
										<ClayButton
											aria-label={Liferay.Language.get(
												'remove'
											)}
											className="component-action"
											displayType="unstyled"
											onClick={() =>
												_handleRemoveScope(
													scopeItem.externalReferenceCode
												)
											}
										>
											<ClayIcon symbol="times-circle" />
										</ClayButton>
									</Cell>
								</Row>
							))}
						</Body>
					</Table>
				)}
			</>
		</CustomPanel>
	);
}
