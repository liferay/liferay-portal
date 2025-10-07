/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {getObjectValueFromPath} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import Actions from '../../actions/Actions';

function Email({
	borderBottom,
	item,
	items,
}: {
	borderBottom: boolean;
	item: any;
	items: any[];
}) {
	const {openSidePanel, selectedItemsKey} = useContext(
		FrontendDataSetContext
	);

	function handleClickOnSubject(event: any) {
		event.preventDefault();

		openSidePanel({
			slug: 'email',
			url: item.href,
		});
	}

	return (
		<li
			className={classNames(
				'bg-white d-flex p-4',
				borderBottom
					? 'border-top-0 border-left-0 border-right-0 border-bottom'
					: 'border-0'
			)}
		>
			<div className="row">
				<div className="col">
					<div className="row">
						<div className="col">
							<div className="row">
								{item.author?.avatarSrc && (
									<div className="col-auto">
										<ClaySticker
											className="sticker-user-icon"
											size="xl"
										>
											<div className="sticker-overlay">
												<img
													className="sticker-img"
													src={item.author?.avatarSrc}
												/>
											</div>
										</ClaySticker>
									</div>
								)}

								<div className="col d-flex flex-column justify-content-center">
									<small className="d-block text-body">
										<strong>{item.author.name}</strong>
									</small>

									<small className="d-block">
										{item.author.email}
									</small>
								</div>
							</div>
						</div>

						<div className="col-auto d-flex flex-column justify-content-center">
							<ClayLabel
								displayType={
									item.status?.displayStyle || 'success'
								}
							>
								{item.status?.label}
							</ClayLabel>
						</div>

						<div className="col-auto d-flex flex-column justify-content-center">
							<small>{item.date}</small>
						</div>

						<div className="col-12">
							<div className="h5 mt-3">
								<a href="#" onClick={handleClickOnSubject}>
									{item.subject}
								</a>
							</div>

							<div>{item.summary}</div>
						</div>
					</div>
				</div>

				{item.actionDropdownItems?.length ? (
					<div className="col-auto d-flex flex-column justify-content-center">
						<Actions
							actions={item.actionDropdownItems}
							itemData={undefined}
							itemId={getObjectValueFromPath({
								object: item,
								path: selectedItemsKey,
							})}
							items={items}
						/>
					</div>
				) : null}
			</div>
		</li>
	);
}

function EmailsList({
	dataLoading,
	items,
}: {
	dataLoading: boolean;
	items: any[];
}) {
	const {style} = useContext(FrontendDataSetContext);

	if (dataLoading) {
		return <ClayLoadingIndicator className="mt-7" />;
	}

	if (!items?.length) {
		return null;
	}

	return (
		<ClayList
			className={classNames(
				'mb-0',
				style === 'default' ? 'border-bottom' : 'border'
			)}
		>
			{items.map((item: any, i: number) => (
				<Email
					borderBottom={i !== items.length - 1}
					item={item}
					items={items}
					key={i}
				/>
			))}
		</ClayList>
	);
}

export default EmailsList;
