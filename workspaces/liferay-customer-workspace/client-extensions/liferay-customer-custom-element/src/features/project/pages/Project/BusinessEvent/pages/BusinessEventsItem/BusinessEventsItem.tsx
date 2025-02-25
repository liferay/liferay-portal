/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';

import './BusinessEventsItem.css';

import {Nav} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import NavigationBar from '@clayui/navigation-bar';
import {ButtonDropDown, Skeleton} from '~/components';
import {getBusinessEventsById} from '~/services/liferay/api';
import i18n from '~/utils/I18n';
import {IProject} from '~/utils/types';

import {IBusinessEventTicket} from '../../BusinessEvents';
import BusinessEventsItemActivityHistory from './components/BusinessEventsItemActivityHistory';
import BusinessEventsItemDetails from './components/BusinessEventsItemDetails';
import BusinessEventsItemEdition from './components/BusinessEventsItemEdition';

const BusinessEventsItem = ({accountKey}: IProject) => {
	const {id} = useParams();
	const [eventTicket, setEventTicket] = useState<IBusinessEventTicket | null>(
		null
	);
	const [loading, setLoading] = useState(true);
	const [activeTab, setActiveTab] = useState('event-details');
	const [currentIndex, setCurrentIndex] = useState(0);
	const [isEditing, setIsEditing] = useState(false);

	useEffect(() => {
		if (id) {
			const fetchEvent = async () => {
				try {
					const eventData = await getBusinessEventsById(id);
					setEventTicket(eventData);
				}
				catch (error) {
					console.error('Error', error);
				}
				finally {
					setLoading(false);
				}
			};

			fetchEvent();
		}
	}, [id]);

	if (loading) {
		return <div>{i18n.translate('loading')}</div>;
	}

	if (!eventTicket) {
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
					className={`align-items-center font-weight-semi-bold be-status be-status-${eventTicket?.eventStatus?.name.toLowerCase()} mb-1 d-inline px-2 py-1`}
				>
					{eventTicket?.eventStatus?.name}
				</div>

				<div className="d-flex justify-content-between mb-4 mt-2">
					<div className="font-weight-bold text-neutral-10">
						<h3>{eventTicket.name}</h3>
					</div>

					<div>
						<ButtonDropDown items={userOptions} label="Actions" />
					</div>
				</div>
			</div>

			<div className="mb-4">
				<NavigationBar triggerLabel={i18n.translate(activeTab)}>
					{loading ? (
						<div>
							<Skeleton align="left" height={20} width={100} />
						</div>
					) : (
						getNavItems()
					)}
				</NavigationBar>
			</div>

			<div className="mt-4">
				{activeTab === 'event-details' &&
					(!isEditing ? (
						<BusinessEventsItemDetails eventTicket={eventTicket} />
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
