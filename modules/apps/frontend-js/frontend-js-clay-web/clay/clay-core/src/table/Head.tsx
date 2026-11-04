/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput, ClayToggle as Toggle} from '@clayui/form';
import Icon from '@clayui/icon';
import Layout from '@clayui/layout';
import {useId} from '@clayui/shared';
import React, {useEffect, useLayoutEffect, useRef, useState} from 'react';

import {ChildrenFunction, Collection, useCollection} from '../collection';
import {Item, Menu} from '../drop-down';
import {Cell} from './Cell';
import {Scope, ScopeContext} from './ScopeContext';
import {useTable} from './context';

type HeaderProps = {
	clearSearchLabel: string;
	description: string;
	descriptionId: string;
	inputRef: React.Ref<HTMLInputElement>;
	name: string;
	nameId: string;
	onSearchTermChange: (searchTerm: string) => void;
	searchLabel: string;
	searchTerm: string;
};

function Header({
	clearSearchLabel,
	description,
	descriptionId,
	inputRef,
	name,
	nameId,
	onSearchTermChange,
	searchLabel,
	searchTerm,
}: HeaderProps) {
	return (
		<>
			<div className="dropdown-subheader mb-0" id={nameId}>
				{name.toUpperCase()}
			</div>

			<div
				className="dropdown-section py-0 text-secondary"
				id={descriptionId}
			>
				{description}
			</div>

			<div className="dropdown-section">
				<ClayInput.Group small>
					<ClayInput.GroupItem>
						<ClayInput
							aria-label={searchLabel}
							insetAfter
							onChange={(event) =>
								onSearchTermChange(event.target.value)
							}
							placeholder={searchLabel}
							ref={inputRef}
							type="text"
							value={searchTerm}
						/>

						<ClayInput.GroupInsetItem after tag="span">
							{searchTerm ? (
								<ClayButtonWithIcon
									aria-label={clearSearchLabel}
									displayType="unstyled"
									monospaced
									onClick={() => onSearchTermChange('')}
									symbol="times"
									title={clearSearchLabel}
								/>
							) : (
								<div className="input-group-inset-icon">
									<Icon symbol="search" />
								</div>
							)}
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</div>

			<div className="dropdown-divider" />
		</>
	);
}

const useIsomorphicEffect =
	typeof window === 'undefined' ? useEffect : useLayoutEffect;

interface IProps<T>
	extends Omit<
		React.TableHTMLAttributes<HTMLTableSectionElement>,
		'children'
	> {

	/**
	 * Children content to render a dynamic or static content.
	 */
	children: React.ReactNode | ChildrenFunction<T, unknown>;

	/**
	 * Property to render content with dynamic data.
	 */
	items?: Array<T>;
}

export function Head<T extends Record<string, any>>(
	{children, items, ...otherProps}: IProps<T>,
	ref: React.Ref<HTMLTableSectionElement>
) {
	const {
		alwaysVisibleColumns,
		columnsVisibility,
		messages,
		onHeadCellsChange,
		onVisibleColumnsChange,
		visibleColumns,
	} = useTable();

	const collection = useCollection<T>({
		children,
		items,
		suppressTextValueWarning: false,
		visibleKeys: new Set(visibleColumns.keys()),
	});

	const columnsVisibilityDescriptionId = useId();
	const columnsVisibilityHeaderId = useId();

	const searchInputRef = useRef<HTMLInputElement>(null);

	const [menuHeight, setMenuHeight] = useState<number>();
	const [searchTerm, setSearchTerm] = useState('');

	const onSearchTermChange = (searchTerm: string) => {
		if (menuHeight === undefined) {
			setMenuHeight(
				searchInputRef.current?.closest<HTMLElement>('.dropdown-menu')
					?.offsetHeight
			);
		}

		setSearchTerm(searchTerm);
	};

	const columnsVisibilityItems = collection
		.getItems()
		.filter(
			(item) =>
				!alwaysVisibleColumns.has(item) &&
				String(collection.getItem(item).value)
					.toLowerCase()
					.includes(searchTerm.trim().toLowerCase())
		);

	useIsomorphicEffect(() => {
		if (visibleColumns.size === 0) {
			onVisibleColumnsChange(collection.getItems(), 0);
		}
		onHeadCellsChange(collection.getSize());
	}, [collection.getSize()]);

	return (
		<thead {...otherProps} ref={ref}>
			<ScopeContext.Provider value={Scope.Head}>
				<tr>
					<Collection<T> collection={collection} />

					{columnsVisibility && (
						<Cell keyValue="visibility" width="64px">
							<span className="sr-only">
								{messages['columnsVisibilityCell'] ||
									messages['columnsVisibility']}
							</span>

							<Menu
								UNSAFE_focusableElements={[
									'input[role="switch"]',
								]}
								alwaysClose={false}
								aria-describedby={
									columnsVisibilityDescriptionId
								}
								aria-labelledby={columnsVisibilityHeaderId}
								header={
									<>
										<Header
											clearSearchLabel={
												messages[
													'columnsVisibilityClearSearch'
												] ?? 'Clear Search'
											}
											description={
												messages[
													'columnsVisibilityDescription'
												]!
											}
											descriptionId={
												columnsVisibilityDescriptionId
											}
											inputRef={searchInputRef}
											name={
												messages[
													'columnsVisibilityHeader'
												]!
											}
											nameId={columnsVisibilityHeaderId}
											onSearchTermChange={
												onSearchTermChange
											}
											searchLabel={
												messages[
													'columnsVisibilitySearch'
												] ?? 'Search'
											}
											searchTerm={searchTerm}
										/>

										{!columnsVisibilityItems.length && (
											<div className="dropdown-section text-secondary">
												{messages[
													'columnsVisibilityNoResults'
												] ?? 'No Results Found'}
											</div>
										)}
									</>
								}
								items={columnsVisibilityItems}
								onActiveChange={(active: boolean) => {
									if (!active) {
										setMenuHeight(undefined);
										setSearchTerm('');
									}
								}}
								style={{
									maxWidth: '210px',
									minHeight: menuHeight,
									minWidth: '210px',
								}}
								trigger={
									<Button
										aria-label={
											messages['columnsVisibility']
										}
										borderless
										displayType="secondary"
										monospaced
										size="sm"
										title={messages['columnsVisibility']}
									>
										<Icon symbol="caret-bottom" />
									</Button>
								}
							>
								{(item) => (
									<Item
										key={item}
										onClick={() => {
											if (
												visibleColumns.has(item) &&
												visibleColumns.size === 1
											) {
												return;
											}

											onVisibleColumnsChange(
												item,
												collection.getItem(item).index
											);
										}}
										tabIndex={-1}
										textValue={
											collection.getItem(item).value
										}
									>
										<Layout.ContentRow verticalAlign="center">
											<Layout.ContentCol expand>
												{collection.getItem(item).value}
											</Layout.ContentCol>

											<Layout.ContentCol float="end">
												<Toggle
													aria-label={
														collection.getItem(item)
															.value
													}
													containerProps={{
														style: {
															marginBottom: 0,
														},
													}}
													disabled={
														visibleColumns.has(
															item
														) &&
														visibleColumns.size -
															alwaysVisibleColumns.size ===
															1
													}
													onChange={(event) => {
														event.stopPropagation();

														onVisibleColumnsChange(
															item,
															collection.getItem(
																item
															).index
														);
													}}
													onKeyDown={(event) => {
														switch (event.key) {
															case 'Enter':
																onVisibleColumnsChange(
																	item,
																	collection.getItem(
																		item
																	).index
																);
																break;
															default:
																break;
														}
													}}
													sizing="sm"
													toggled={visibleColumns.has(
														item
													)}
												/>
											</Layout.ContentCol>
										</Layout.ContentRow>
									</Item>
								)}
							</Menu>
						</Cell>
					)}
				</tr>
			</ScopeContext.Provider>
		</thead>
	);
}

type ForwardRef = {
	<T>(
		props: IProps<T> & {ref?: React.Ref<HTMLTableSectionElement>}
	): JSX.Element;
	displayName: string;
};

export const ForwardHead = React.forwardRef(Head) as ForwardRef;

ForwardHead.displayName = 'TableHead';
