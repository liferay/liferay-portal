/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Form from '../../../../../../components/MarketplaceForm';
import {Tooltip} from '../../../../../../components/Tooltip/Tooltip';
import {
	SolutionTypes,
	useSolutionContext,
} from '../../../../../../context/SolutionContext';
import i18n from '../../../../../../i18n';

const tooltipInfo = {
	companyProfileInfo: 'Company Information',
	descriptionInfo: 'description',
	emailInfo: 'Email',
	phoneInfo: 'Phone',
	websiteInfo: 'Website',
};

const CompanyProfile = () => {
	const [
		{
			company: {description, email, phone, website},
		},
		dispatch,
	] = useSolutionContext();

	const onChange = (event: any) =>
		dispatch({
			payload: {[event.target.name]: event.target.value},
			type: SolutionTypes.SET_COMPANY,
		});

	return (
		<div className="mb-4 solutions-form-company-profile">
			<div className="align-items-center d-flex jus">
				<h3 className="mb-0 mr-2">
					{i18n.translate('company-profile')}
				</h3>

				<Tooltip
					tooltip={tooltipInfo.companyProfileInfo}
					tooltipText={i18n.translate('more-info')}
				/>
			</div>

			<hr />

			<Form.FormControl>
				<Form.Label
					className="mt-3"
					htmlFor="description"
					info={tooltipInfo.descriptionInfo}
					required
				>
					{i18n.translate('description')}
				</Form.Label>

				<div className="rich-text-editor">
					<Form.RichTextEditor
						maxLength={700}
						onChange={(value) =>
							dispatch({
								payload: {description: value},
								type: SolutionTypes.SET_COMPANY,
							})
						}
						placeholder="Insert text here"
						value={description}
					/>
				</div>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="website"
					info={tooltipInfo.websiteInfo}
					required
				>
					{i18n.translate('website')}
				</Form.Label>

				<Form.Input
					name="website"
					onChange={onChange}
					placeholder="http://www.yourdomain.com"
					type="text"
					value={website}
				/>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="email"
					info={tooltipInfo.emailInfo}
					required
				>
					{i18n.translate('email')}
				</Form.Label>

				<Form.Input
					name="email"
					onChange={onChange}
					placeholder="name@yourdomain.com"
					type="email"
					value={email}
				/>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="phone"
					info={tooltipInfo.phoneInfo}
					required
				>
					{i18n.translate('phone')}
				</Form.Label>

				<Form.Input
					name="phone"
					onChange={onChange}
					placeholder="+1 (123) 456-7890"
					value={phone}
				/>
			</Form.FormControl>
		</div>
	);
};

export default CompanyProfile;
