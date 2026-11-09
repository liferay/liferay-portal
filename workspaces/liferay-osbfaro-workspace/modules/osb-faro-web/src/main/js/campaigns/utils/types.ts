export type UtmParameter =
	| 'utmCampaign'
	| 'utmContent'
	| 'utmMedium'
	| 'utmSource'
	| 'utmTerm';

export type UtmParameters = Partial<Record<UtmParameter, string[]>>;

/**
 * A `utm_*` parameter beyond the five standard ones, which the card lists
 * after them under the name the URL carried.
 */
export interface ICustomUtmParameter {
	name: string;
	values: string[];
}

export interface IUtmParameterRow {
	name: string;
	values: string[];
}
