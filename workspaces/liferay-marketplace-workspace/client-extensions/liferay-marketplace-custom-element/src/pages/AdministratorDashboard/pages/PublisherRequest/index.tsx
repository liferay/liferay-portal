/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import {useModal} from '@clayui/modal';
import {format} from 'date-fns';
import {ComponentProps, useState} from 'react';

import ListView from '../../../../components/ListView';
import Page from '../../../../components/Page';
import i18n from '../../../../i18n';
import PublisherRequestModal, {
	PublisherStatusDisplayType,
} from './PublisherRequestModal';

const PublisherRequest = () => {
	const modal = useModal();

	const [selectedRequest, setSelectedRequest] =
		useState<PublisherRequestInfo>();

	return (
		<Page
			description={i18n.translate('users-requests-to-become-a-publisher')}
			title={i18n.translate('publisher-requests')}
		>
			<ListView<PublisherRequestInfo>
				id="request-publisher-account"
				resource="o/c/requestpublisheraccounts?sort=dateCreated:desc"
				tableProps={{
					columns: [
						{
							clickable: true,
							id: 'firstName',
							name: i18n.translate('name'),
							render: (name, {lastName}) => (
								<span className="text-capitalize text-nowrap">{`${name}  ${lastName}`}</span>
							),
						},
						{
							clickable: true,
							id: 'emailAddress',
							name: i18n.translate('email'),
						},
						{
							clickable: true,
							id: 'requestDescription',
							name: i18n.translate('description'),
							render: (requestDescription) => (
								<span
									className="text-truncate"
									title={requestDescription}
								>
									{requestDescription}
								</span>
							),
							truncate: true,
						},
						{
							clickable: true,
							id: 'creator',
							name: i18n.translate('requester'),
							render: (creator) => (
								<div>{creator?.name ?? 'Guest'}</div>
							),
						},
						{
							clickable: true,
							id: 'dateCreated',
							name: i18n.translate('created-at'),
							render: (dateCreated) => (
								<span className="ml-2 text-capitalize text-nowrap">
									{format(
										new Date(dateCreated),
										'MMM dd, yyyy'
									)}
								</span>
							),
						},
						{
							clickable: true,
							id: 'requestStatus',
							name: i18n.translate('status'),
							render: (
								requestStatus = {key: 'open', name: 'Open'}
							) => {
								const displayType =
									PublisherStatusDisplayType[
										requestStatus.key as keyof typeof PublisherStatusDisplayType
									];

								return (
									<ClayLabel
										className="text-nowrap"
										displayType={
											displayType as ComponentProps<
												typeof ClayLabel
											>['displayType']
										}
									>
										{requestStatus?.name}
									</ClayLabel>
								);
							},
						},
					],
					onClickRow: (item) => {
						setSelectedRequest(item);

						modal.onOpenChange(true);
					},
				}}
			>
				{(_, {mutate}) => (
					<PublisherRequestModal
						{...modal}
						mutate={mutate}
						selectedRequest={selectedRequest}
					/>
				)}
			</ListView>
		</Page>
	);
};

export default PublisherRequest;
