/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';
import {PagesTree} from 'staging-taglib';

const PAGES_TREE_NAMESPACE = '_exportPageTreeModal_';

const PATH_MAIN = Liferay.ThemeDisplay.getPathMain();
const GET_LAYOUTS_TREE_URL = `${PATH_MAIN}/portal/get_layouts_tree`;
const SESSION_TREE_JS_CLICK_URL = `${PATH_MAIN}/portal/session_tree_js_click`;

export interface PageTreeModalConfiguration {
	liveGroupId: number;
	pageSize: number;
	privateLayoutsAvailable: boolean;
}

interface Props
	extends Omit<PageTreeModalConfiguration, 'privateLayoutsAvailable'> {
	initialAll?: boolean;
	initialSelectedIds: string[];
	onClose: () => void;
	onSubmit: (
		result: {layoutIds?: number[]; privateLayout: boolean} | null
	) => void;
	privateLayout: boolean;
}

export default function PageTreeModal({
	initialAll = false,
	initialSelectedIds,
	liveGroupId,
	onClose,
	onSubmit,
	pageSize,
	privateLayout,
}: Props) {
	const {observer} = useModal({onClose});

	const [items, setItems] = useState<unknown[] | null>(null);
	const [error, setError] = useState(false);
	const [selectedLayoutIds, setSelectedLayoutIds] =
		useState<string[]>(initialSelectedIds);
	const treeRef = useRef<HTMLDivElement>(null);
	const initialAllRef = useRef(initialAll);
	const initialSelectedIdsRef = useRef(initialSelectedIds);
	const treeId = `exportPageTreeModal_${privateLayout ? 'private' : 'public'}`;

	useEffect(() => {
		let cancelled = false;

		setItems(null);
		setError(false);

		const sessionTreeId = `${treeId}SelectedNode`;
		const initialIds = initialSelectedIdsRef.current;

		const sync = async () => {
			await fetch(SESSION_TREE_JS_CLICK_URL, {
				body: new URLSearchParams({
					cmd: 'collapse',
					treeId: sessionTreeId,
				}),
				method: 'post',
			});

			let nextSelectedLayoutIds: string[] | undefined;

			if (initialAllRef.current) {
				const selectAllResponse = await fetch(
					SESSION_TREE_JS_CLICK_URL,
					{
						body: new URLSearchParams({
							cmd: 'layoutCheck',
							groupId: String(liveGroupId),
							plid: '0',
							privateLayout: String(privateLayout),
							recursive: 'true',
							treeId: sessionTreeId,
						}),
						method: 'post',
					}
				);

				const payload = await selectAllResponse.json();

				nextSelectedLayoutIds = (payload ?? []).map(String);
			}
			else if (initialIds.length) {
				await fetch(SESSION_TREE_JS_CLICK_URL, {
					body: new URLSearchParams({
						cmd: 'expand',
						nodeIds: initialIds.join(','),
						treeId: sessionTreeId,
					}),
					method: 'post',
				});
			}

			const response = await fetch(
				`${GET_LAYOUTS_TREE_URL}?${new URLSearchParams({
					end: String(pageSize),
					groupId: String(liveGroupId),
					incomplete: 'true',
					parentLayoutId: '0',
					privateLayout: String(privateLayout),
					start: '0',
				})}`
			);

			const data = await response.json();

			if (cancelled) {
				return;
			}

			if (nextSelectedLayoutIds) {
				setSelectedLayoutIds(nextSelectedLayoutIds);
			}

			setItems([
				{
					children: data.items,
					hasChildren: true,
					hasGuestViewPermission: true,
					id: '0',
					layoutId: 0,
					name: privateLayout
						? Liferay.Language.get('private-pages')
						: Liferay.Language.get('public-pages'),
					paginated: data.hasMoreElements,
				},
			]);
		};

		sync().catch(() => !cancelled && setError(true));

		return () => {
			cancelled = true;
		};
	}, [liveGroupId, pageSize, privateLayout, treeId]);

	const handleSelect = () => {
		const input = treeRef.current?.querySelector<HTMLInputElement>(
			`input[name="${PAGES_TREE_NAMESPACE}layoutIds"]`
		);

		const layoutIds = input
			? (JSON.parse(input.value || '[]') as (number | string)[])
					.map(Number)
					.filter((id) => !Number.isNaN(id) && id !== 0)
			: [];

		if (!layoutIds.length) {
			onSubmit(null);

			return;
		}

		const allChecked =
			treeRef.current?.querySelectorAll(
				'input[type="checkbox"]:not(:checked)'
			).length === 0;

		onSubmit(allChecked ? {privateLayout} : {layoutIds, privateLayout});
	};

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('pages-to-export')}
			</ClayModal.Header>

			<ClayModal.Body>
				{error && (
					<ClayAlert displayType="danger">
						{Liferay.Language.get('an-unexpected-error-occurred')}
					</ClayAlert>
				)}

				{!items && !error && <ClayLoadingIndicator />}

				{items && (
					<div ref={treeRef}>
						<PagesTree
							config={{
								changeItemSelectionURL:
									SESSION_TREE_JS_CLICK_URL,
								loadMoreItemsURL: GET_LAYOUTS_TREE_URL,
								maxPageSize: pageSize,
								namespace: PAGES_TREE_NAMESPACE,
							}}
							groupId={String(liveGroupId)}
							items={items}
							key={treeId}
							portletNamespace={PAGES_TREE_NAMESPACE}
							privateLayout={privateLayout}
							selectedLayoutIds={selectedLayoutIds}
							treeId={treeId}
						/>
					</div>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={handleSelect}>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
