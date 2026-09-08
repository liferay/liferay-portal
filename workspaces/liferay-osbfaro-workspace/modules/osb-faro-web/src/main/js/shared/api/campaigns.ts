/**
 * Mirrors `CampaignDisplay` on the Faro BFF, whose de-underscored field names
 * are the contract. The data set fetches the list itself through `apiURL`, so
 * this module carries the model without a request function; the metrics and
 * touched accounts endpoints get theirs with their own tasks.
 */
export interface ICampaign {
	accountsTouched: number;
	campaignName: string;
	campaignType: string;
	endDate: string | number;
	id: string;
	individualsTouched: number;
	origin?: string;
	startDate: string | number;
	status: string;
}
