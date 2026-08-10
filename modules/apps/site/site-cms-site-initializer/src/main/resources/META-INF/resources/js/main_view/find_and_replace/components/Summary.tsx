/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayModal from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {
	EConfigInURLBehavior,
	FrontendDataSet,
} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';

import AsyncButton from '../../../structure_builder/components/AsyncButton';
import {
	FindAndReplaceContext,
	ReplaceItem,
	useOpenDiscard,
} from '../contexts/FindAndReplaceContext';
import FindAndReplaceService from '../services/FindAndReplaceService';
import {filterItemsBySearch} from '../utils/filterItemBySearch';
import {filterItemsByLocale} from '../utils/filterItemsByLocale';
import {getItemChanges} from '../utils/getItemChanges';

export function Summary() {
	const {
		closeModal,
		dataSetId,
		items: allItems,
		localeId,
		replacement,
		search,
		setHistory,
		setView,
	} = useContext(FindAndReplaceContext);

	const openDiscard = useOpenDiscard();

	const items = useMemo(() => {
		if (!allItems) {
			return [];
		}

		if (localeId === 'all') {
			return allItems;
		}

		return filterItemsByLocale(allItems, localeId);
	}, [allItems, localeId]);

	function applyChanges() {
		const filteredItems = filterItemsBySearch(items, search);

		if (!filteredItems.length) {
			return;
		}

		FindAndReplaceService.performBulkReplace({
			dataSetId,
			items: filteredItems,
			localeId,
			replacement,
			search,
		});

		setHistory({hasAppliedAll: true});

		closeModal();
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
						onClick={() => setView('setup')}
						size="sm"
						symbol="angle-left"
					/>

					{Liferay.Language.get('review-changes')}
				</div>
			</ClayModal.Header>

			<ClayModal.Body>
				<FrontendDataSet
					configInURLBehavior={EConfigInURLBehavior.OFF}
					id="findAndReplaceFds"
					items={items}
					onItemsPropSearch={(item, query) =>
						item.title.includes(query)
					}
					pagination={{
						deltas: [{label: 10}, {label: 20}],
						initialDelta: 10,
					}}
					searchAsYouType={true}
					showPagination
					style="fluid"
					views={[
						{
							component: ({items}: {items: ReplaceItem[]}) => (
								<ClayList>
									{items.map((item) => (
										<ListItem item={item} key={item.id} />
									))}
								</ClayList>
							),
						},
					]}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={openDiscard}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={applyChanges}>
							{Liferay.Language.get(
								'apply-changes-to-all-assets'
							)}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

function ListItem({item}: {item: ReplaceItem}) {
	const {
		apply,
		discard,
		localeId,
		replacement,
		search,
		setPreviewId,
		setView,
	} = useContext(FindAndReplaceContext);

	const [status, setStatus] = useState<'applied' | 'applying' | 'idle'>(
		'idle'
	);

	const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

	const changes = getItemChanges(item, search, localeId);

	useEffect(() => {
		return () => {
			if (timeoutRef.current) {
				clearTimeout(timeoutRef.current);
			}
		};
	}, []);

	async function applyChanges() {
		if (status !== 'idle') {
			return;
		}

		setStatus('applying');

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

		setStatus('applied');

		openToast({
			message: sub(
				Liferay.Language.get('changes-applied-to-x'),
				Liferay.Util.escapeHTML(item.title)
			),
			type: 'success',
		});

		timeoutRef.current = setTimeout(() => {
			apply(item.id);
		}, 5000);
	}

	return (
		<ClayList.Item
			className={classNames('align-items-center', {
				'list-item__success': status === 'applied',
			})}
			flex
		>
			<ClayList.ItemField>
				<ClaySticker
					className={classNames('inline-item', item.stickerClassName)}
				>
					<ClayIcon symbol={item.stickerSymbol} />
				</ClaySticker>
			</ClayList.ItemField>

			<ClayList.ItemField expand>
				<ClayList.ItemTitle>
					<ClayButton
						className="font-weight-semi-bold text-underline"
						disabled={status !== 'idle'}
						displayType="unstyled"
						onClick={() => {
							setPreviewId(item.id);

							setView('preview');
						}}
						type="button"
					>
						{item.title}
					</ClayButton>
				</ClayList.ItemTitle>

				<ClayList.ItemText>
					{sub(Liferay.Language.get('x-changes'), changes)}
				</ClayList.ItemText>
			</ClayList.ItemField>

			{changes === 0 ? (
				<ClayList.ItemField>
					<p className="m-0 text-info">
						<ClayIcon className="mr-2" symbol="info-panel-open" />

						<span>
							{Liferay.Language.get(
								'no-exact-matches-were-found'
							)}
						</span>
					</p>
				</ClayList.ItemField>
			) : (
				<>
					<ClayList.ItemField>
						<AsyncButton
							disabled={status !== 'idle'}
							displayType="secondary"
							label={Liferay.Language.get('apply-changes')}
							onClick={applyChanges}
							status={status === 'applying' ? 'loading' : 'idle'}
						/>
					</ClayList.ItemField>

					<ClayList.ItemField>
						<ClayButtonWithIcon
							aria-label={sub(
								Liferay.Language.get('discard-changes-to-x'),
								item.title
							)}
							borderless
							disabled={status !== 'idle'}
							displayType="secondary"
							monospaced
							onClick={() => discard(item.id)}
							size="sm"
							symbol="times-circle"
						/>
					</ClayList.ItemField>
				</>
			)}
		</ClayList.Item>
	);
}
