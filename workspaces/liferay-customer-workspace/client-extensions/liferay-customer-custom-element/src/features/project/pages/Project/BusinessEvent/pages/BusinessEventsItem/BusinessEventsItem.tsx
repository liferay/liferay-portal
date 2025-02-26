/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Nav} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import NavigationBar from '@clayui/navigation-bar';
import {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {ButtonDropDown} from '~/components';
import i18n from '~/utils/I18n';
import {IBusinessEvent} from '~/utils/types';

import BusinessEventsItemActivityHistory from './components/BusinessEventsItemActivityHistory';
import BusinessEventsItemDetails from './components/BusinessEventsItemDetails';
import BusinessEventsItemEdition from './components/BusinessEventsItemEdition';

import './BusinessEventsItem.css';

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {getBusinessEventById} from '~/services/liferay/api';

const BusinessEventsItem = () => {
	const {accountKey, id} = useParams<{accountKey: string; id: string}>();
	const [businessEvent, setBusinessEvent] = useState<IBusinessEvent | null>(
		null
	);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		if (id) {
			const fetchEvent = async () => {
				try {
					setLoading(true);

					const eventData = await getBusinessEventById(id);

					setBusinessEvent(eventData);
				}
				catch (error) {
					console.error('Error', error);

					setBusinessEvent(null);
				}
				finally {
					setLoading(false);
				}
			};

			fetchEvent();
		}
	}, [id]);

	const [activeTab, setActiveTab] = useState('event-details');
	const [currentIndex, setCurrentIndex] = useState(0);
	const [isEditing, setIsEditing] = useState(false);

	if (loading) {
		return (
			<div className="mx-auto">
				<ClayLoadingIndicator size="sm" />
			</div>
		);
	}

	if (!businessEvent) {
		return <div>{i18n.translate('no-data-found')}</div>;
	}

	const userOptions = [
		{
			customOptionStyle: 'pr-5',
			icon: <ClayIcon symbol="pencil" />,
			label: i18n.translate('edit-event-details'),
			onClick: () => {
				setIsEditing(true);
			},
		},
		{
			customOptionStyle: 'pr-5',
			icon: <ClayIcon symbol="check-circle" />,
			label: i18n.translate('record-actual-go-live'),
			onClick: () => {},
		},
		{
			customOptionStyle: 'pr-5',
			icon: <ClayIcon symbol="trash" />,
			label: i18n.translate('cancel-event'),
			onClick: () => {},
		},
	];

	const handleOnClick = (index: number) => {
		setCurrentIndex(index);

		if (index === 0) {
			setActiveTab('event-details');
		}
		else if (index === 1) {
			setActiveTab('activity-history');
		}
	};

	const getNavItems = () => {
		const items = [
			{key: 'event-details', label: 'event-details'},
			{key: 'activity-history', label: 'activity-history'},
		];

		return items.map((item, index) => (
			<Nav.Item
				key={`${item.key}-${index}`}
				onClick={() => handleOnClick(index)}
			>
				<Nav.Link
					active={index === currentIndex}
					aria-label={`Switch to ${item.label}`}
					className="be-nav-link text-neutral-10"
				>
					{i18n.translate(item.label)}
				</Nav.Link>
			</Nav.Item>
		));
	};

	return (
		<div>
			<div className="be-breadcrumbs font-weight-semi-bold mb-4">
				<span className="mx-2">
					<Link to={`/${accountKey}/business-events/`}>
						<ClayIcon className="mr-1" symbol="order-arrow-left" />

						{i18n.translate('back-to-business-events')}
					</Link>
				</span>
			</div>

			<div>
				<div
					className={`align-items-center font-weight-semi-bold be-status be-status-${businessEvent?.eventStatus?.name.toLowerCase()} mb-1 d-inline px-2 py-1`}
				>
					{businessEvent?.eventStatus?.name}
				</div>

				<div className="d-flex justify-content-between mb-4 mt-2">
					<div className="font-weight-bold text-neutral-10">
						<h3>{businessEvent.name}</h3>
					</div>

					<div>
						<ButtonDropDown items={userOptions} label="Actions" />
					</div>
				</div>
			</div>

			<div className="mb-4">
				<NavigationBar triggerLabel={i18n.translate(activeTab)}>
					{getNavItems()}
				</NavigationBar>
			</div>

			<div className="mt-4">
				{activeTab === 'event-details' &&
					(!isEditing ? (
						<BusinessEventsItemDetails
							businessEvent={businessEvent}
						/>
					) : (
						<BusinessEventsItemEdition />
					))}
				{activeTab === 'activity-history' && (
					<div>
						<BusinessEventsItemActivityHistory />
					</div>
				)}
			</div>
		</div>
	);
};

export default BusinessEventsItem;
