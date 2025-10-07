/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

export const DEFAULT_SXP_BLUEPRINT_CONFIGURATION = {
	advancedConfiguration: {},
	aggregationConfiguration: {},
	generalConfiguration: {
		clauseContributorsExcludes: [],
		clauseContributorsIncludes: ['*'],
		searchableAssetTypes: [],
	},
	highlightConfiguration: {},
	parameterConfiguration: {},
	queryConfiguration: {
		applyIndexerClauses: true,
	},
	sortConfiguration: {},
};

const DEFAULT_SXP_ELEMENT_DEFINITION = {
	category: 'custom',
	configuration: {},
	icon: 'custom-field',
};

export class SearchExperiencesApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'search-experiences-rest/v1.0';
	}

	/**
	 * It allows creating an SXPBlueprint.
	 *
	 * @param configuration Technical configurations that include sort,
	 * highlight, aggregation, and query configurations
	 * @param defaultLanguageId the language id to assign for title/description
	 * @param description the description of the blueprint
	 * @param elementInstances the elements to be included in the blueprint
	 * @param title the title of the blueprint
	 */

	async createSXPBlueprint({
		configuration = DEFAULT_SXP_BLUEPRINT_CONFIGURATION,
		defaultLanguageId = 'en_US',
		description = '',
		elementInstances = [],
		title = `Blueprint${getRandomInt()}`,
	}: {
		configuration?: Object;
		defaultLanguageId?: string;
		description?: string;
		elementInstances?: Object[];
		title?: string;
	} = {}): Promise<SXPBlueprint> {
		const sxpBlueprint = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sxp-blueprints`,
			{
				data: {
					configuration,
					description_i18n: {
						[defaultLanguageId]: description,
					},
					elementInstances,
					title_i18n: {[defaultLanguageId]: title},
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: sxpBlueprint.id,
				type: 'sxpBlueprint',
			});
		}

		return sxpBlueprint;
	}

	/**
	 * It allows creating an SXPElement.
	 *
	 * @param defaultLanguageId the language id to assign for title/description
	 * @param description the description of the blueprint
	 * @param elementDefinition Contains element properties like category,
	 * icon, and configuration
	 * @param title the title of the blueprint
	 */

	async createSXPElement({
		defaultLanguageId = 'en_US',
		description = '',
		elementDefinition = DEFAULT_SXP_ELEMENT_DEFINITION,
		title = `Element${getRandomInt()}`,
	}: {
		defaultLanguageId?: string;
		description?: string;
		elementDefinition?: Object;
		title?: string;
	} = {}): Promise<SXPElement> {
		const sxpElement = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sxp-elements`,
			{
				data: {
					description_i18n: {
						[defaultLanguageId]: description,
					},
					elementDefinition,
					title_i18n: {[defaultLanguageId]: title},
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: sxpElement.id,
				type: 'sxpElement',
			});
		}

		return sxpElement;
	}

	/**
	 * It allows deleting an sxpBlueprint.
	 *
	 * @param id the id of the blueprint
	 */

	async deleteSXPBlueprint(id: number | string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/sxp-blueprints/${id}`
		);
	}

	/**
	 * It allows deleting an sxpElement.
	 *
	 * @param id the id of the element
	 */

	async deleteSXPElement(id: number | string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/sxp-elements/${id}`
		);
	}
}
