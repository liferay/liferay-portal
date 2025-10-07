/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {checkCookieConsentForTypes} from '@liferay/cookies-banner-web';
import classnames from 'classnames';
import {COOKIE_TYPES, checkConsent} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import './mini_compare.scss';
import CommerceCookie from '../../utilities/cookies';
import {
	ITEM_REMOVED_FROM_COMPARE,
	PRODUCT_COMPARISON_TOGGLED,
	TOGGLE_ITEM_IN_PRODUCT_COMPARISON,
} from '../../utilities/eventsDefinitions';

const compareCookie = new CommerceCookie(
	'COMMERCE_COMPARE_cpDefinitionIds_',
	COOKIE_TYPES.FUNCTIONAL
);

function toggleStatus(commerceChannelGroupId, id, toggle) {
	const value = compareCookie.getValue(commerceChannelGroupId);

	const cpDefinitionIds = value ? value.split(':') : [];

	if (toggle) {
		if (!cpDefinitionIds.includes(id)) {
			cpDefinitionIds.push(id);
		}
	}
	else {
		const index = cpDefinitionIds.indexOf(id);

		if (index !== -1) {
			cpDefinitionIds.splice(index, 1);
		}
	}

	compareCookie.setValue(commerceChannelGroupId, cpDefinitionIds.join(':'));
}

function alertCookies(alertType, alertTitle, alertMessage) {
	Liferay.Util.openToast({
		message: alertMessage,
		title: alertTitle,
		toastProps: {
			autoClose: 5000,
		},
		type: alertType,
	});
}

function Item(props) {
	return (
		<div className={classnames('mini-compare-item', props.id && 'active')}>
			<ClaySticker className="mini-compare-thumbnail-container" size="lg">
				<div
					className="mini-compare-thumbnail"
					style={
						props.thumbnail
							? {backgroundImage: `url('${props.thumbnail}')`}
							: {}
					}
				/>
			</ClaySticker>

			<button className="mini-compare-delete" onClick={props.onDelete}>
				<ClayIcon symbol="times" />
			</button>
		</div>
	);
}

function MiniCompare(props) {
	const [items, setItems] = useState(() => {
		const value = compareCookie.getValue(props.commerceChannelGroupId);
		const ids = value ? value.split(':') : [];

		return ids.map(
			(id) => props.items?.find((item) => item.id === id) || {id}
		);
	});
	const [functionalCookiesConsent, setFunctionalCookiesConsent] = useState(
		checkConsent(COOKIE_TYPES.FUNCTIONAL)
	);

	const triggerCheckCookieConsent = useCallback(() => {
		return !functionalCookiesConsent && items?.length > 0;
	}, [functionalCookiesConsent, items?.length]);

	useEffect(() => {
		if (triggerCheckCookieConsent()) {
			checkCookieConsentForTypes(COOKIE_TYPES.FUNCTIONAL, {
				alertMessage: Liferay.Language.get(
					'product-comparison-cookies-alert'
				),
				customTitle: Liferay.Language.get(
					'product-comparison-cookies-title'
				),
			})
				.then(() => {
					compareCookie.setValue(
						props.commerceChannelGroupId,
						items.map((item) => item.id).join(':')
					);
					setFunctionalCookiesConsent(true);
					alertCookies(
						'success',
						Liferay.Language.get('cookies-allowed'),
						Liferay.Language.get(
							'product-comparison-cookies-success'
						)
					);
				})
				.catch(() => {
					alertCookies(
						'warning',
						Liferay.Language.get('cookies-not-allowed'),
						Liferay.Language.get(
							'product-comparison-cookies-warning'
						)
					);
				});
		}
	}, [
		functionalCookiesConsent,
		items,
		props.commerceChannelGroupId,
		triggerCheckCookieConsent,
	]);

	useEffect(() => {
		function toggleItem({id, thumbnail}) {
			const newItem = {
				id,
				thumbnail,
			};

			setItems((items) => {
				const included = items.find((element) => element.id === id);

				toggleStatus(props.commerceChannelGroupId, id, !included);

				return included
					? items.filter((i) => i.id !== id)
					: items.concat(newItem);
			});
		}

		Liferay.on(TOGGLE_ITEM_IN_PRODUCT_COMPARISON, toggleItem);

		return () => {
			Liferay.detach(TOGGLE_ITEM_IN_PRODUCT_COMPARISON, toggleItem);
		};
	}, [
		props.commerceChannelGroupId,
		props.itemsLimit,
		props.portletNamespace,
	]);

	useEffect(() => {
		Liferay.fire(PRODUCT_COMPARISON_TOGGLED, {
			disabled: items.length >= props.itemsLimit,
		});
	}, [items, props.itemsLimit]);

	return triggerCheckCookieConsent() ? null : (
		<div className={classnames('mini-compare', !!items.length && 'active')}>
			{Array(props.itemsLimit)
				.fill(null)
				.map((_el, i) => {
					const currentItem = items[i] || {};

					return (
						<Item
							{...currentItem}
							key={i}
							onDelete={(event) => {
								event.preventDefault();
								setItems(
									items.filter((v) => v.id !== currentItem.id)
								);
								toggleStatus(
									props.commerceChannelGroupId,
									currentItem.id,
									false
								);
								Liferay.fire(
									ITEM_REMOVED_FROM_COMPARE,
									currentItem
								);
							}}
						/>
					);
				})}

			<a className="btn btn-primary" href={props.compareProductsURL}>
				{Liferay.Language.get('compare')}
			</a>
		</div>
	);
}

MiniCompare.propTypes = {
	commerceChannelGroupId: PropTypes.number,
	compareProductsURL: PropTypes.string.isRequired,
	items: PropTypes.arrayOf(
		PropTypes.shape({
			id: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
				.isRequired,
			thumbnail: PropTypes.string,
		})
	),
	itemsLimit: PropTypes.number,
	portletNamespace: PropTypes.string.isRequired,
};

MiniCompare.defaultProps = {
	items: [],
	itemsLimit: 5,
};

export default MiniCompare;
