import { ApiHelpers } from "../../../helpers/ApiHelpers";

export default async function deleteSites(apiHelpers: ApiHelpers, ...siteIds: string[]) {
    for (const siteId of siteIds) {
        let response = await apiHelpers.headlessSite.deleteSite(siteId);
        if (!response.ok()) {
            response = await apiHelpers.headlessSite.deleteSite(siteId);
        }
        expect(response.ok()).toBe(true);
    }
}