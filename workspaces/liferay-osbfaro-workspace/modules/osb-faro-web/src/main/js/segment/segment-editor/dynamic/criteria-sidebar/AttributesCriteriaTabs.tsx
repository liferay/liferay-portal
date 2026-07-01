import ClayTabs from '@clayui/tabs';
import React, {useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {filterPropertiesByLabel, renderProperties} from './criteriaProperties';
import {List} from 'immutable';
import {Property} from 'shared/util/records';

const DEFAULT_TAB = 0;

interface IAttributesCriteriaTabsProps {
	customProperties: List<Property>;
	defaultProperties: List<Property>;
	searchValue: string;
}

const AttributesCriteriaTabs: React.FC<IAttributesCriteriaTabsProps> = ({
	customProperties,
	defaultProperties,
	searchValue,
}) => {
	const [activeTab, setActiveTab] = useState<number>(DEFAULT_TAB);

	const properties =
		activeTab === DEFAULT_TAB ? defaultProperties : customProperties;

	const filteredProperties = useMemo(
		() => filterPropertiesByLabel(properties, searchValue),
		[properties, searchValue]
	);

	return (
		<div className="events-criteria-tabs">
			<ClayTabs active={activeTab} onActiveChange={setActiveTab}>
				<ClayTabs.Item>{Liferay.Language.get('default')}</ClayTabs.Item>

				<ClayTabs.Item>{Liferay.Language.get('custom')}</ClayTabs.Item>
			</ClayTabs>

			<div className="events-criteria-tabs-content mt-3">
				{renderProperties(
					filteredProperties,
					searchValue,
					activeTab === DEFAULT_TAB
						? undefined
						: {
								description: Liferay.Language.get(
									'create-a-custom-field-to-get-started'
								),
								link: {
									href: URLConstants.CustomFieldsDocumentation,
									label: Liferay.Language.get(
										'learn-more-about-fields'
									),
								},
								title: Liferay.Language.get(
									'no-custom-fields-yet'
								),
							}
				)}
			</div>
		</div>
	);
};

export default AttributesCriteriaTabs;
