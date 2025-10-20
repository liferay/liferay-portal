/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import {useModal} from '@clayui/modal';

import businessIcon from '../../../assets/icons/business.svg';
import userIcon from '../../../assets/icons/user.svg';
import {AccountTypes} from '../../../enums/Account';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import CreateAccountModalForm from './CreateAccountModalForm';

import './CreateNewAccount.scss';

type DisplayCardProps = {
	description: string;
	svg: string;
	title: string;
};

const DisplayCard = ({description, svg, title}: DisplayCardProps) => (
	<div className="align-items-center border create-new-account-card d-flex justify-content-center">
		<div className="d-flex justify-content-center mr-3">
			<img alt={title} draggable="false" src={svg} />
		</div>

		<div className="create-new-account-card-description">
			<b>{title}</b>

			<div className="mr-1">{description}</div>
		</div>
	</div>
);

const Divider = () => (
	<div className="align-items-center d-flex">
		<hr className="w-50" />

		<b className="divider mx-2">OR</b>

		<hr className="w-50" />
	</div>
);

const CreateNewAccount = ({accounts}: {accounts: Account[]}) => {
	const modal = useModal();

	const hasAccounts = !!accounts.length;
	const isBusinessAccount = accounts.some(
		(account) => account.type === AccountTypes.BUSINESS
	);

	return (
		<div className="create-new-account-container d-flex flex-column mt-5">
			{!hasAccounts && (
				<>
					<div className="mr-1">
						There are no Liferay Marketplace accounts available for
						<b className="ml-1">
							{Liferay.ThemeDisplay.getUserEmailAddress()}
						</b>
						. You need to create a new personal or business account
						or join an already existing Marketplace business account
						to proceed.
					</div>

					<DisplayCard
						description={i18n.translate(
							'for-businesses-with-a-vat-tax-number-this-account-type-support-multiple-users-it-also-possible-to-join-an-already-existing-business-account'
						)}
						svg={businessIcon}
						title={i18n.translate('business-account')}
					/>

					<DisplayCard
						description={i18n.translate(
							'for-individuals-without-a-vat-tax-number-this-account-support-single-user-only'
						)}
						svg={userIcon}
						title={i18n.translate('personal-account')}
					/>
				</>
			)}

			{hasAccounts && !isBusinessAccount && (
				<>
					<Divider />

					<DisplayCard
						description={i18n.translate(
							'designed-for-companies-with-a-tax-vat-number-this-account-type-also-lets-you-manage-multiple-users-under-one-profile-click-the-button-below-to-create-your-first-business-account-today'
						)}
						svg={businessIcon}
						title={i18n.translate(
							'unlock-exclusive-benefits-with-a-business-account'
						)}
					/>
				</>
			)}

			{hasAccounts && isBusinessAccount && <Divider />}

			<Button
				className="create-new-account-button"
				onClick={() => modal.onOpenChange(true)}
			>
				{i18n.translate('new-account')}
			</Button>

			<CreateAccountModalForm modal={modal} />
		</div>
	);
};

export default CreateNewAccount;
