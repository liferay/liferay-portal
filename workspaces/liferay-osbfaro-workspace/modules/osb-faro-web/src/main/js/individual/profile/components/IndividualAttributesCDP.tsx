import React from 'react';
import {
	DataDrivenConfig,
	GeneralInfoSection,
} from 'shared/components/GeneralInfoSection';
import {formatUTCDate, getCustomDateFormat} from 'shared/util/date';
import {SectionHeader} from 'shared/components/SectionHeader';

interface IIndividualAttributesProps {
	children?: React.ReactNode;
	contactId?: string;
	loading?: boolean;
	propertiesData: Map<string, any>;
	showEmptyState?: boolean;
}

const INFO_LANGUAGE_MAP: Record<string, string> = {
	birthDate: Liferay.Language.get('birthday'),
	city: Liferay.Language.get('city'),
	contactId: 'contactId',
	country: Liferay.Language.get('country'),
	email: Liferay.Language.get('email'),
	familyName: Liferay.Language.get('last-name'),
	givenName: Liferay.Language.get('first-name'),
	jobTitle: Liferay.Language.get('job-title'),
	languageId: Liferay.Language.get('language'),
	middleName: Liferay.Language.get('middle-name'),
	prefix: Liferay.Language.get('prefix'),
	screenName: Liferay.Language.get('screen-name'),
	suffix: Liferay.Language.get('suffix'),
	timezoneOffset: Liferay.Language.get('time-zone'),
	userId: 'User ID',
	uuid: 'UUID',
};

const contextualInfoConfig: DataDrivenConfig = [
	{
		columnClass: 'col-12',
		items: [
			{
				className: 'col-12 col-md-4 col-sm-6',
				icon: 'web-content',
				key: 'screenName',
			},
			{className: 'col-12 col-md-4 col-sm-6', key: 'prefix'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'userId'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'email'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'givenName'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'uuid'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'jobTitle'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'middleName'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'languageId'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'familyName'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'birthDate'},
			{className: 'col-12 col-md-4 col-sm-6', key: 'suffix'},
		],
		title: Liferay.Language.get('personal-information'),
	},
];

const IndividualAttributesCDP: React.FC<IIndividualAttributesProps> = ({
	children: emptyState,
	contactId,
	loading = false,
	propertiesData,
	showEmptyState,
}) => {
	const getValue = (key: string): string | undefined => {
		if (key === 'birthDate') {
			const birthDate = propertiesData?.get('birthDate');

			return birthDate
				? formatUTCDate(birthDate, getCustomDateFormat())
				: undefined;
		}

		if (key === 'contactId') return contactId;

		return propertiesData?.get(key) || undefined;
	};

	return (
		<>
			<SectionHeader
				icon="user"
				title={Liferay.Language.get('individual-attributes')}
			/>

			{showEmptyState && !loading ? (
				emptyState
			) : (
				<GeneralInfoSection
					config={contextualInfoConfig}
					getValue={getValue}
					languageMap={INFO_LANGUAGE_MAP}
					loading={loading}
				/>
			)}
		</>
	);
};

export default IndividualAttributesCDP;
